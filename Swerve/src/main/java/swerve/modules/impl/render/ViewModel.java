package swerve.modules.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swerve.Swerve;
import swerve.managers.ThemeManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.ui.newgui.GuiRender;
import swerve.utils.animation.AnimationUtils;
import swerve.utils.animation.Easings;
import swerve.utils.color.ColorUtils;
import swerve.utils.render.HandViewport;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// Положение, масштаб и анимация рук от первого лица.
//
// Главное: при ударе рука НЕ уходит вниз. Ванильный swingArm делает три сдвига
// (-0.4*sin, 0.2*sin, -0.2*sin) и доворот на -80° вокруг X — именно они «роняют» руку.
// Здесь сдвиги и наклон умножаются на свои коэффициенты (по умолчанию 0), поэтому
// остаётся только лёгкий поворот на месте, а точка руки не двигается.
//
// Смещения применяются сразу после pushPose в renderArmWithItem, то есть ДО всех
// веток анимаций: пустая рука, предмет, карта в одной/двух руках, арбалет, блок,
// лук, еда/питьё, brush, riptide — позиция и масштаб держатся везде одинаково.
//
// Двойной клик по руке в чате — перетаскивание, колесо — масштаб (референс Kimiko).
// Пока рука схвачена, вокруг неё рисуется обводка по спроецированной точке руки.
public final class ViewModel extends Module {

    private static final String MODE_IN_PLACE = "На месте";
    private static final String MODE_STATIC = "Статично";
    private static final String MODE_VANILLA = "Ванилла";

    // Точка крепления руки из applyItemArmTransform / renderPlayerArm.
    private static final float ARM_X = 0.56F;
    private static final float ARM_Y = -0.52F;
    private static final float ARM_Z = -0.72F;
    private static final float ARM_HEIGHT = -0.6F;

    // Амплитуды ванильного взмаха (предмет в руке).
    private static final float ITEM_SWING_X = -0.4F;
    private static final float ITEM_SWING_Y = 0.2F;
    private static final float ITEM_SWING_Z = -0.2F;

    // Амплитуды ванильного взмаха пустой руки (renderPlayerArm).
    private static final float ARM_SWING_X = -0.3F;
    private static final float ARM_SWING_Y = 0.4F;
    private static final float ARM_SWING_Z = -0.4F;

    private static final float ARM_ROT_Y = 45.0F;
    private static final float ARM_ROT_SWING_Y = -20.0F;
    private static final float ARM_ROT_SWING_Z = -20.0F;
    private static final float ARM_ROT_SWING_X = -80.0F;

    private static final float SCALE_MIN = 0.2F;
    private static final float SCALE_MAX = 5.0F;
    private static final float SCALE_STEP = 0.1F;
    private static final float GRAB_RADIUS_UNITS = 0.14F;
    private static final float OUTLINE_THICKNESS = 1.4F;
    private static final long DOUBLE_CLICK_MS = 300L;
    private static final float FRAME_SMOOTHING = 12.0F;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/viewmodel.json");

    private static boolean eventsRegistered;
    private static ViewModel instance;

    private final ModeSetting swing = new ModeSetting("Взмах", MODE_IN_PLACE, MODE_IN_PLACE, MODE_STATIC, MODE_VANILLA);
    private final FloatSetting dip = new FloatSetting("Опускание", 0.0F, 0.0F, 1.0F, 0.05F)
            .visible(() -> !swing.is(MODE_VANILLA));
    private final FloatSetting tilt = new FloatSetting("Наклон удара", 0.0F, 0.0F, 1.0F, 0.05F)
            .visible(() -> swing.is(MODE_IN_PLACE));
    private final FloatSetting strength = new FloatSetting("Сила взмаха", 1.0F, 0.0F, 2.0F, 0.05F)
            .visible(() -> swing.is(MODE_IN_PLACE));
    private final BooleanSetting noEquipDrop = new BooleanSetting("Без опускания при смене", false)
            .visible(() -> !swing.is(MODE_VANILLA));

    private final FloatSetting mainX = new FloatSetting("Основная X", 0.0F, -1.5F, 1.5F, 0.01F);
    private final FloatSetting mainY = new FloatSetting("Основная Y", 0.0F, -1.5F, 1.5F, 0.01F);
    private final FloatSetting mainZ = new FloatSetting("Основная Z", 0.0F, -1.5F, 1.5F, 0.01F);
    private final FloatSetting offX = new FloatSetting("Вторая X", 0.0F, -1.5F, 1.5F, 0.01F);
    private final FloatSetting offY = new FloatSetting("Вторая Y", 0.0F, -1.5F, 1.5F, 0.01F);
    private final FloatSetting offZ = new FloatSetting("Вторая Z", 0.0F, -1.5F, 1.5F, 0.01F);
    private final FloatSetting scale = new FloatSetting("Масштаб", 1.0F, SCALE_MIN, SCALE_MAX, 0.05F);

    private final BooleanSetting dragInChat = new BooleanSetting("Перетаскивание в чате", true);
    private final BooleanSetting outline = new BooleanSetting("Обводка", true);
    private final FloatSetting outlineSize = new FloatSetting("Размер обводки", 0.11F, 0.02F, 0.30F, 0.01F)
            .visible(outline::get);

    private final AnimationUtils outlineAnim = new AnimationUtils(0.0F, FRAME_SMOOTHING, Easings.CUBIC_OUT);

    private boolean loaded;
    private boolean wasMouseDown;
    private boolean wasRightDown;
    private long lastClickMs;
    private boolean holding;
    private InteractionHand grabbed;
    private InteractionHand outlineHand;
    private float grabCursorX;
    private float grabCursorY;
    private float grabOffsetX;
    private float grabOffsetY;

    public ViewModel() {
        super("ViewModel", "Позиция, масштаб и анимации рук. Двойной клик по руке в чате — перетащить, колесо — размер.",
                ModuleCategory.RENDER);
        instance = this;
        addSettings(swing, dip, tilt, strength, noEquipDrop,
                mainX, mainY, mainZ, offX, offY, offZ, scale,
                dragInChat, outline, outlineSize);
        load();
        registerEvents();
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;
        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
            ViewModel module = instance;
            try {
                if (module != null) {
                    module.onHudRender(guiGraphics);
                }
            } finally {
                HandViewport.consume();
            }
        });
    }

    public static ViewModel get() {
        return instance;
    }

    @Override
    public void onEnable() {
        load();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        holding = false;
        grabbed = null;
        outlineHand = null;
        outlineAnim.setValue(0.0F);
        HandViewport.reset();
        super.onDisable();
    }

    // ------------------------------------------------------------------
    // Точки входа из MixinItemInHandRenderer — всё статическое и без аллокаций.
    // ------------------------------------------------------------------

    public static void applyTransform(PoseStack poseStack, AbstractClientPlayer player, InteractionHand hand) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || poseStack == null || player == null || hand == null) {
            return;
        }
        module.load();

        boolean main = hand == InteractionHand.MAIN_HAND;
        HumanoidArm arm = main ? player.getMainArm() : player.getMainArm().getOpposite();
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float anchorX = ARM_X * (float) side;

        float scaleValue = Mth.clamp(module.scale.get(), SCALE_MIN, SCALE_MAX);
        if (scaleValue != 1.0F) {
            // Масштаб вокруг точки крепления: рука растёт на месте, а не уезжает к камере.
            poseStack.translate(anchorX, ARM_Y, ARM_Z);
            poseStack.scale(scaleValue, scaleValue, scaleValue);
            poseStack.translate(-anchorX, -ARM_Y, -ARM_Z);
        }

        float offsetX = main ? module.mainX.get() : module.offX.get();
        float offsetY = main ? module.mainY.get() : module.offY.get();
        float offsetZ = main ? module.mainZ.get() : module.offZ.get();
        if (offsetX != 0.0F || offsetY != 0.0F || offsetZ != 0.0F) {
            poseStack.translate(offsetX, offsetY, offsetZ);
        }

        if (module.dragInChat.get() || module.outline.get()) {
            HandViewport.capture(hand, poseStack, anchorX, ARM_Y, ARM_Z);
        }
    }

    // Замена ванильного swingArm. Возвращает false, когда взмах должен остаться ванильным.
    public static boolean applySwing(PoseStack poseStack, HumanoidArm arm, int armX, float swingProgress,
                                     float equippedProgress) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || poseStack == null || arm == null
                || module.swing.is(MODE_VANILLA)) {
            return false;
        }
        module.load();

        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float progress = Mth.clamp(swingProgress, 0.0F, 1.0F);
        float dipValue = Mth.clamp(module.dip.get(), 0.0F, 1.0F);

        if (dipValue > 0.0F && progress > 0.0F) {
            float sqrt = Mth.sqrt(progress);
            float shiftX = ITEM_SWING_X * Mth.sin(sqrt * (float) Math.PI);
            float shiftY = ITEM_SWING_Y * Mth.sin(sqrt * (float) (Math.PI * 2.0D));
            float shiftZ = ITEM_SWING_Z * Mth.sin(progress * (float) Math.PI);
            poseStack.translate((float) armX * shiftX * dipValue, shiftY * dipValue, shiftZ * dipValue);
        }

        poseStack.translate((float) side * ARM_X, ARM_Y + equipProgress(equippedProgress) * ARM_HEIGHT, ARM_Z);

        if (module.swing.is(MODE_STATIC) || progress <= 0.0F) {
            return true;
        }

        float power = module.strength.get();
        float tiltValue = Mth.clamp(module.tilt.get(), 0.0F, 1.0F);
        float fast = Mth.sin(progress * progress * (float) Math.PI);
        float slow = Mth.sin(Mth.sqrt(progress) * (float) Math.PI);

        poseStack.mulPose(Axis.YP.rotationDegrees((float) side * (ARM_ROT_Y + fast * ARM_ROT_SWING_Y * power)));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) side * slow * ARM_ROT_SWING_Z * power));
        poseStack.mulPose(Axis.XP.rotationDegrees(slow * ARM_ROT_SWING_X * tiltValue));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) side * -ARM_ROT_Y));
        return true;
    }

    // Гасит positional-сдвиг пустой руки: renderPlayerArm сначала делает translate,
    // поэтому обратный сдвиг в родительском пространстве вычитается точно.
    public static boolean cancelArmDip(PoseStack poseStack, HumanoidArm arm, float swingProgress) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || poseStack == null || arm == null
                || module.swing.is(MODE_VANILLA) || swingProgress <= 0.0F) {
            return false;
        }
        module.load();

        float dipValue = Mth.clamp(module.dip.get(), 0.0F, 1.0F);
        if (dipValue >= 1.0F) {
            return false;
        }

        float side = arm != HumanoidArm.LEFT ? 1.0F : -1.0F;
        float sqrt = Mth.sqrt(swingProgress);
        float shiftX = ARM_SWING_X * Mth.sin(sqrt * (float) Math.PI);
        float shiftY = ARM_SWING_Y * Mth.sin(sqrt * (float) (Math.PI * 2.0D));
        float shiftZ = ARM_SWING_Z * Mth.sin(swingProgress * (float) Math.PI);
        float cancel = 1.0F - dipValue;

        poseStack.translate(-side * shiftX * cancel, -shiftY * cancel, -shiftZ * cancel);
        return true;
    }

    public static boolean staticSwing() {
        ViewModel module = instance;
        return module != null && module.isEnable() && module.swing.is(MODE_STATIC);
    }

    public static float equipProgress(float equippedProgress) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || !module.noEquipDrop.get()) {
            return equippedProgress;
        }
        return 0.0F;
    }

    // ------------------------------------------------------------------
    // Перетаскивание и обводка.
    // ------------------------------------------------------------------

    private void onHudRender(GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            release(false);
            outlineAnim.update(0.0F);
            return;
        }

        if (!isEnable()) {
            release(false);
            outlineAnim.update(0.0F);
            return;
        }
        load();

        boolean inChat = minecraft.screen instanceof ChatScreen;
        boolean canDrag = inChat && dragInChat.get();
        if (!canDrag) {
            release(false);
        }

        long window = minecraft.getWindow().getWindow();
        double guiScale = Math.max(1.0D, minecraft.getWindow().getGuiScale());
        float mouseX = (float) minecraft.mouseHandler.xpos();
        float mouseY = (float) minecraft.mouseHandler.ypos();

        boolean mouseDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (mouseDown && !wasMouseDown) {
            long now = System.currentTimeMillis();
            if (canDrag && now - lastClickMs <= DOUBLE_CLICK_MS) {
                grab(mouseX, mouseY);
            }
            lastClickMs = now;
        }
        wasMouseDown = mouseDown;

        boolean rightDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (rightDown && !wasRightDown && holding && grabbed != null) {
            resetHand(grabbed);
            release(true);
        }
        wasRightDown = rightDown;

        if (!mouseDown && holding) {
            release(true);
        }
        if (holding && grabbed != null) {
            drag(mouseX, mouseY);
        }

        outlineAnim.update(holding && grabbed != null ? 1.0F : 0.0F);
        drawOutline(graphics.pose(), guiScale);
    }

    private void grab(float mouseX, float mouseY) {
        InteractionHand hand = HandViewport.nearest(mouseX, mouseY, GRAB_RADIUS_UNITS);
        if (hand == null) {
            return;
        }

        grabbed = hand;
        outlineHand = hand;
        holding = true;
        grabCursorX = mouseX;
        grabCursorY = mouseY;

        boolean main = hand == InteractionHand.MAIN_HAND;
        grabOffsetX = main ? mainX.get() : offX.get();
        grabOffsetY = main ? mainY.get() : offY.get();
    }

    private void drag(float mouseX, float mouseY) {
        InteractionHand hand = grabbed;
        if (!HandViewport.isReady(hand)) {
            return;
        }

        float pixelsX = HandViewport.pixelsPerUnitX(hand);
        float pixelsY = HandViewport.pixelsPerUnitY(hand);
        if (pixelsX <= 1.0E-4F || pixelsY <= 1.0E-4F) {
            return;
        }

        float deltaX = (mouseX - grabCursorX) / pixelsX;
        float deltaY = -(mouseY - grabCursorY) / pixelsY;

        if (hand == InteractionHand.MAIN_HAND) {
            mainX.setValue(grabOffsetX + deltaX);
            mainY.setValue(grabOffsetY + deltaY);
        } else {
            offX.setValue(grabOffsetX + deltaX);
            offY.setValue(grabOffsetY + deltaY);
        }
    }

    private void release(boolean saveLayout) {
        if (holding && saveLayout) {
            save();
        }
        holding = false;
        grabbed = null;
    }

    private void resetHand(InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            mainX.setValue(0.0F);
            mainY.setValue(0.0F);
            mainZ.setValue(0.0F);
        } else {
            offX.setValue(0.0F);
            offY.setValue(0.0F);
            offZ.setValue(0.0F);
        }
        scale.setValue(1.0F);
    }

    // Колесо мыши, пока рука схвачена, — масштаб. Возвращает true, если событие съедено.
    public boolean onScroll(double yOffset) {
        if (!isEnable() || !holding || grabbed == null || yOffset == 0.0D) {
            return false;
        }
        if (!(Minecraft.getInstance().screen instanceof ChatScreen)) {
            return false;
        }

        load();
        scale.setValue(scale.get() + (yOffset > 0.0D ? SCALE_STEP : -SCALE_STEP));
        save();
        return true;
    }

    private void drawOutline(PoseStack stack, double guiScale) {
        float alpha = outlineAnim.getValue();
        InteractionHand hand = outlineHand;

        if (alpha <= 0.004F) {
            outlineHand = null;
            return;
        }
        if (!outline.get() || hand == null || !HandViewport.isReady(hand)) {
            return;
        }

        float centerX = (float) (HandViewport.anchorX(hand) / guiScale);
        float centerY = (float) (HandViewport.anchorY(hand) / guiScale);
        float pixelsY = (float) (HandViewport.pixelsPerUnitY(hand) / guiScale);
        float half = Math.max(6.0F, outlineSize.get() * pixelsY);

        int color = ColorUtils.multAlpha(ThemeManager.getColor(), alpha);
        int accent = ColorUtils.multAlpha(ThemeManager.getColor(1), alpha * 0.55F);
        float radius = half * 0.4F;

        GuiRender.roundedOutline(stack, centerX - half, centerY - half, half * 2.0F, half * 2.0F,
                OUTLINE_THICKNESS, color, radius);
        GuiRender.roundedOutline(stack, centerX - half - 2.0F, centerY - half - 2.0F,
                half * 2.0F + 4.0F, half * 2.0F + 4.0F, 0.7F, accent, radius + 2.0F);
    }

    // ------------------------------------------------------------------
    // Сохранение раскладки.
    // ------------------------------------------------------------------

    private void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        if (!Files.isRegularFile(FILE)) {
            return;
        }

        try {
            JsonElement root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8));
            if (!root.isJsonObject()) {
                return;
            }

            JsonObject json = root.getAsJsonObject();
            mainX.setValue(readFloat(json, "mainX", 0.0F));
            mainY.setValue(readFloat(json, "mainY", 0.0F));
            mainZ.setValue(readFloat(json, "mainZ", 0.0F));
            offX.setValue(readFloat(json, "offX", 0.0F));
            offY.setValue(readFloat(json, "offY", 0.0F));
            offZ.setValue(readFloat(json, "offZ", 0.0F));
            scale.setValue(readFloat(json, "scale", 1.0F));
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to load viewmodel layout", exception);
        }
    }

    private void save() {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("mainX", mainX.get());
            json.addProperty("mainY", mainY.get());
            json.addProperty("mainZ", mainZ.get());
            json.addProperty("offX", offX.get());
            json.addProperty("offY", offY.get());
            json.addProperty("offZ", offZ.get());
            json.addProperty("scale", scale.get());

            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to save viewmodel layout", exception);
        }
    }

    private static float readFloat(JsonObject json, String key, float fallback) {
        JsonElement element = json.get(key);
        if (element == null || !element.isJsonPrimitive()) {
            return fallback;
        }
        try {
            return element.getAsFloat();
        } catch (RuntimeException exception) {
            return fallback;
        }
    }
}
