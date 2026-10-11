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
import net.minecraft.world.entity.LivingEntity;

import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// Положение, масштаб и анимации рук от первого лица.
//
// Опускание руки убрано полностью и без настроек: ванильный swingArm делает сдвиг
// (-0.4/0.2/-0.2 * sin) и доворот -80° по X, а applyItemArmTransform роняет руку на
// equippedProgress * -0.6 при смене предмета. Здесь вместо swingArm всегда считается
// своя трансформация (базовая точка руки + пресет), а equippedProgress во всех ветках
// приводится к нулю — рука не уходит вниз ни при ударе, ни при смене предмета.
//
// Пресеты удара — полный набор из референса (Kimiko SwingAnimation), портированный на
// mojmap 1.21.4. Постоянные повороты пресетов умножены на огибающую sin(f*PI): без неё
// рука на первом кадре взмаха «выстреливала» в позу и щёлкала обратно на последнем.
//
// Смещения и масштаб применяются сразу после единственного pushPose в renderArmWithItem,
// то есть до ветвления: предмет, пустая рука, карта в одной/двух руках, арбалет, блок,
// лук, еда/питьё, brush и riptide получают одну и ту же позицию.
//
// Наведение и двойной клик по руке в чате: захват, перетаскивание, колесо — масштаб,
// ПКМ — сброс руки. Обводка рисуется по спроецированной точке руки (см. HandViewport).
public final class ViewModel extends Module {

    private static final String ANIM_IN_PLACE = "На месте";
    private static final String ANIM_SWING = "Взмах";
    private static final String ANIM_SWING_2 = "Взмах 2";
    private static final String ANIM_SHIFT = "Сдвиг";
    private static final String ANIM_BREAK = "Слом";
    private static final String ANIM_LUNGE = "Выпад";
    private static final String ANIM_SPEAR = "Копьё";
    private static final String ANIM_DOWN = "Вниз";
    private static final String ANIM_JELLY = "Желе";
    private static final String ANIM_SLAP = "Шлепок";
    private static final String ANIM_BONK = "Бонк";
    private static final String ANIM_CIRCLE = "Круг";
    private static final String ANIM_REALISM = "Реализм";
    private static final String ANIM_STATIC = "Статично";

    // Точка крепления руки из applyItemArmTransform / renderPlayerArm.
    private static final float ARM_X = 0.56F;
    private static final float ARM_Y = -0.52F;
    private static final float ARM_Z = -0.72F;

    // Амплитуды ванильного сдвига пустой руки (renderPlayerArm) — гасятся обратным translate.
    private static final float ARM_SWING_X = -0.3F;
    private static final float ARM_SWING_Y = 0.4F;
    private static final float ARM_SWING_Z = -0.4F;

    private static final float SCALE_MIN = 0.2F;
    private static final float SCALE_MAX = 5.0F;
    private static final float SCALE_STEP = 0.1F;
    private static final float GRAB_RADIUS_UNITS = 0.14F;
    private static final float OUTLINE_THICKNESS = 1.4F;
    private static final float OUTLINE_FILL = 0.10F;
    private static final long DOUBLE_CLICK_MS = 300L;
    private static final float OUTLINE_SPEED = 10.0F;

    private static final double C1 = 1.70158D;
    private static final double C3 = 2.70158D;
    private static final double C4 = 2.0943951023931953D;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/viewmodel.json");

    private static boolean eventsRegistered;
    private static ViewModel instance;

    private final ModeSetting anim = new ModeSetting("Анимация", ANIM_IN_PLACE,
            ANIM_IN_PLACE, ANIM_SWING, ANIM_SWING_2, ANIM_SHIFT, ANIM_BREAK, ANIM_LUNGE, ANIM_SPEAR,
            ANIM_DOWN, ANIM_JELLY, ANIM_SLAP, ANIM_BONK, ANIM_CIRCLE, ANIM_REALISM, ANIM_STATIC);
    private final FloatSetting strength = new FloatSetting("Сила", 1.0F, 0.5F, 3.0F, 0.05F)
            .visible(() -> !anim.is(ANIM_STATIC));
    private final BooleanSetting onlyOnTarget = new BooleanSetting("Только при наводке", false);

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

    private final AnimationUtils outlineAnim = new AnimationUtils(0.0F, OUTLINE_SPEED, Easings.CUBIC_OUT);

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

    private int realismSide = -1;
    private boolean realismReady = true;

    public ViewModel() {
        super("ViewModel", "Позиция, масштаб и анимации рук. Двойной клик по руке в чате — перетащить, колесо — размер.",
                ModuleCategory.RENDER);
        instance = this;
        addSettings(anim, strength, onlyOnTarget,
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
        realismSide = -1;
        realismReady = true;
        HandViewport.reset();
        super.onDisable();
    }

    // ------------------------------------------------------------------
    // Точки входа из MixinItemInHandRenderer — статические, без аллокаций.
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

    // Полная замена ванильного swingArm. false — только когда модуль выключен.
    public static boolean applySwing(PoseStack poseStack, HumanoidArm arm, float swingProgress) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || poseStack == null || arm == null) {
            return false;
        }
        module.load();

        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        // equippedProgress сюда намеренно не передаётся: опускания руки больше нет.
        poseStack.translate((float) side * ARM_X, ARM_Y, ARM_Z);

        float progress = Mth.clamp(swingProgress, 0.0F, 1.0F);
        if (progress <= 0.0F || module.anim.is(ANIM_STATIC) || !module.targetAllows()) {
            return true;
        }

        module.applyPreset(poseStack, side, progress, module.strength.get());
        return true;
    }

    // Гасит сдвиг пустой руки: renderPlayerArm первым делом делает translate,
    // поэтому обратный сдвиг в родительском пространстве вычитается точно.
    public static boolean cancelArmDip(PoseStack poseStack, HumanoidArm arm, float swingProgress) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || poseStack == null || arm == null || swingProgress <= 0.0F) {
            return false;
        }
        // «Только при наводке» влияет на пресеты, но не на сам баг: сдвиг гасится всегда.

        float side = arm != HumanoidArm.LEFT ? 1.0F : -1.0F;
        float sqrt = Mth.sqrt(swingProgress);
        float shiftX = ARM_SWING_X * Mth.sin(sqrt * (float) Math.PI);
        float shiftY = ARM_SWING_Y * Mth.sin(sqrt * (float) (Math.PI * 2.0D));
        float shiftZ = ARM_SWING_Z * Mth.sin(swingProgress * (float) Math.PI);

        poseStack.translate(-side * shiftX, -shiftY, -shiftZ);
        return true;
    }

    public static boolean staticSwing() {
        ViewModel module = instance;
        return module != null && module.isEnable() && module.anim.is(ANIM_STATIC);
    }

    // Опускание при смене предмета убрано вместе с настройкой: пока модуль включён,
    // рука всегда стоит в финальной позиции.
    public static float equipProgress(float equippedProgress) {
        ViewModel module = instance;
        return module != null && module.isEnable() ? 0.0F : equippedProgress;
    }

    // ------------------------------------------------------------------
    // Пресеты удара. Отрицательные оси (XN/YN) заменены на XP/YP с обратным углом:
    // результат тот же, а набор констант Axis гарантированно совпадает с 1.21.4.
    // ------------------------------------------------------------------

    private void applyPreset(PoseStack poseStack, int side, float f, float power) {
        // f2/f3/f4 — те же величины, что в референсе; env == f4 гасит постоянные повороты.
        float env = Mth.sin(f * (float) Math.PI);
        float f2 = Mth.sin(f * f * (float) Math.PI);
        float f3 = Mth.sin(Mth.sqrt(f) * (float) Math.PI);
        float armSide = (float) side;

        switch (anim.getCurrent()) {
            case ANIM_SWING -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * (45.0F + f2 * -20.0F)));
                poseStack.mulPose(Axis.ZP.rotationDegrees(armSide * f3 * -20.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(f3 * -power * 22.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * -45.0F));
            }
            case ANIM_SWING_2 -> {
                poseStack.scale(1.0F, 1.0F, env + 1.0F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F * env * (power * 0.3F)));
            }
            case ANIM_SHIFT -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * 90.0F * env));
                poseStack.mulPose(Axis.ZP.rotationDegrees(armSide * -60.0F * env));
                poseStack.mulPose(Axis.XP.rotationDegrees((-90.0F - 20.0F * env * power) * env));
            }
            case ANIM_BREAK -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * 90.0F * env));
                poseStack.mulPose(Axis.ZP.rotationDegrees(armSide * -30.0F * env));
                poseStack.mulPose(Axis.XP.rotationDegrees((-90.0F - 20.0F * env * power) * env));
            }
            case ANIM_LUNGE -> {
                poseStack.translate(0.0F, 0.0F, -env * 0.2F);
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * 45.0F * env));
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * f2 * -20.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(armSide * f3 * -20.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(f3 * -Mth.clamp(power * 55.0F, 55.0F, 80.0F)));
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * 10.0F * env));
                poseStack.mulPose(Axis.XP.rotationDegrees(-80.0F * env));
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * 60.0F * env));
            }
            case ANIM_SPEAR -> {
                poseStack.translate(0.0F, 0.0F, -env * 0.2F);
                poseStack.scale(1.0F, 1.0F, env * (power * 0.35F) + 1.0F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F * env));
            }
            case ANIM_DOWN -> {
                poseStack.translate(-env * 0.5F, env * 0.25F, 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * 65.0F * env));
                poseStack.mulPose(Axis.XP.rotationDegrees((-110.0F - 80.0F * env) * env));
            }
            case ANIM_JELLY -> {
                poseStack.translate(0.0F, 0.0F, -env * 0.2F);
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * 35.0F * env));
                poseStack.scale(1.0F, 1.0F, env * (f * 0.65F * power * 0.2F) + 1.0F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F * env));
            }
            case ANIM_SLAP -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(f3 * -55.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(f3 * f2 * -120.0F * power));
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * (f3 * 60.0F)));
                poseStack.mulPose(Axis.XP.rotationDegrees(f3 * -55.0F));
            }
            case ANIM_BONK -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(15.0F * armSide * env));
                poseStack.mulPose(Axis.XP.rotationDegrees(-95.0F * easeOutElastic(f * f) * env));
            }
            case ANIM_CIRCLE -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(15.0F * armSide * env));
                float circle = Mth.lerp(1.0F - f, f, f * 0.5F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-(360.0F * easeOutBack(circle) + 45.0F) * env));
            }
            case ANIM_REALISM -> applyRealism(poseStack, side, f, env, power);
            default -> {
                // ANIM_IN_PLACE: ванильный поворот без уводящего руку вниз доворота по X.
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * (45.0F + f2 * -20.0F * power)));
                poseStack.mulPose(Axis.ZP.rotationDegrees(armSide * f3 * -20.0F * power));
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * -45.0F));
            }
        }
    }

    // Сторона slashes меняется раз в взмах, поэтому состояние живёт в модуле.
    private void applyRealism(PoseStack poseStack, int side, float f, float env, float power) {
        if (f < 0.12F && realismReady) {
            realismSide *= -1;
            realismReady = false;
        } else if (f > 0.78F) {
            realismReady = true;
        }

        float slash = (float) side * (float) realismSide;
        poseStack.scale(1.0F, 1.0F, env + 1.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(slash * 22.0F * env * Mth.clamp(power, 0.5F, 2.0F)));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F * env * (power * 0.3F)));
        poseStack.mulPose(Axis.XP.rotationDegrees(-45.0F * easeOutElastic(f * f) * env));
    }

    private boolean targetAllows() {
        if (!onlyOnTarget.get()) {
            return true;
        }
        return Minecraft.getInstance().crosshairPickEntity instanceof LivingEntity;
    }

    private static float easeOutBack(float value) {
        float shifted = value - 1.0F;
        return 1.0F + (float) C3 * shifted * shifted * shifted + (float) C1 * shifted * shifted;
    }

    private static float easeOutElastic(float value) {
        if (value <= 0.0F) {
            return 0.0F;
        }
        if (value >= 1.0F) {
            return 1.0F;
        }
        return (float) (Math.pow(2.0D, -10.0D * value) * Math.sin((value * 10.0D - 0.75D) * C4) + 1.0D);
    }

    // ------------------------------------------------------------------
    // Наведение, двойной клик, перетаскивание, обводка.
    // ------------------------------------------------------------------

    private void onHudRender(GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || !isEnable()) {
            release(false);
            outlineHand = null;
            outlineAnim.update(0.0F);
            return;
        }
        load();

        boolean inChat = minecraft.screen instanceof ChatScreen;
        boolean canDrag = inChat && dragInChat.get();
        if (!canDrag) {
            release(false);
        }

        float mouseX = HandViewport.mouseGuiX(minecraft);
        float mouseY = HandViewport.mouseGuiY(minecraft);
        long window = minecraft.getWindow().getWindow();

        // Наведение считается до обработки клика: второй клик двойного клика уже видит руку.
        InteractionHand hovered = canDrag ? HandViewport.nearest(mouseX, mouseY, GRAB_RADIUS_UNITS) : null;

        boolean mouseDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (mouseDown && !wasMouseDown) {
            long now = System.currentTimeMillis();
            if (canDrag && hovered != null && now - lastClickMs <= DOUBLE_CLICK_MS) {
                grab(hovered, mouseX, mouseY);
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

        InteractionHand focus = holding && grabbed != null ? grabbed : hovered;
        if (focus != null) {
            outlineHand = focus;
        }
        outlineAnim.update(focus != null ? 1.0F : 0.0F);
        drawOutline(graphics.pose());

        if (canDrag && !HandViewport.isAnyReady()) {
            HandViewport.reportStale();
        }
    }

    private void grab(InteractionHand hand, float mouseX, float mouseY) {
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
        if (pixelsX <= 1.0E-3F || pixelsY <= 1.0E-3F) {
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

    // Колесо мыши, пока рука схвачена, — масштаб. true — событие съедено.
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

    private void drawOutline(PoseStack stack) {
        float alpha = outlineAnim.getValue();
        InteractionHand hand = outlineHand;

        if (alpha <= 0.004F) {
            outlineHand = null;
            return;
        }
        if (!outline.get() || hand == null || !HandViewport.isReady(hand)) {
            return;
        }

        float centerX = HandViewport.anchorX(hand);
        float centerY = HandViewport.anchorY(hand);
        float half = Math.max(8.0F, outlineSize.get() * HandViewport.pixelsPerUnitY(hand));
        float radius = half * 0.35F;
        float size = half * 2.0F;

        int theme = ThemeManager.getColor();
        GuiRender.roundedRect(stack, centerX - half, centerY - half, size, size, radius,
                ColorUtils.multAlpha(theme, alpha * OUTLINE_FILL));
        GuiRender.roundedOutline(stack, centerX - half, centerY - half, size, size,
                OUTLINE_THICKNESS, ColorUtils.multAlpha(theme, alpha), radius);
        GuiRender.roundedOutline(stack, centerX - half - 2.0F, centerY - half - 2.0F, size + 4.0F, size + 4.0F,
                0.7F, ColorUtils.multAlpha(ThemeManager.getColor(1), alpha * 0.5F), radius + 2.0F);
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
