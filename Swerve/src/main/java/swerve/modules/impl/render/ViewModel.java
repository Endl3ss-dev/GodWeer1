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
import swerve.ui.newgui.ClickGuiScreen;
import swerve.ui.newgui.GuiRender;
import swerve.utils.animation.AnimationUtils;
import swerve.utils.animation.Easings;
import swerve.utils.color.ColorUtils;
import swerve.utils.render.HandOutline;
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

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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

    private static final float ARM_X = 0.56F;
    private static final float ARM_Y = -0.52F;
    private static final float ARM_Z = -0.72F;

    private static final float ARM_SWING_X = -0.3F;
    private static final float ARM_SWING_Y = 0.4F;
    private static final float ARM_SWING_Z = -0.4F;

    private static final float SCALE_MIN = 0.2F;
    private static final float SCALE_MAX = 5.0F;
    private static final float SCALE_STEP = 0.22F;
    private static final float SCALE_ANIM_SPEED = 10.0F;
    private static final float DRAG_UNITS_PER_PIXEL = 3.2F;

    private static final float GRAB_PULSE = 0.06F;
    private static final float PULSE_IN_SPEED = 16.0F;
    private static final float PULSE_OUT_SPEED = 8.0F;

    private static final float FOLLOW_RATE = 18.0F;
    private static final float MAX_FRAME_DELTA = 0.05F;

    private static final float OUTLINE_IN_SPEED = 9.1F;
    private static final float OUTLINE_OUT_SPEED = 6.7F;
    private static final float OUTLINE_RECT_FILL = 0.10F;
    private static final float OUTLINE_RECT_THICKNESS = 1.4F;

    private static final double C1 = 1.70158D;
    private static final double C3 = 2.70158D;
    private static final double C4 = 2.0943951023931953D;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/viewmodel.json");

    private static final Matrix4f INVERTED = new Matrix4f();
    private static final Vector4f UNPROJECT = new Vector4f();

    private static boolean eventsRegistered;
    private static ViewModel instance;

    private final ModeSetting anim = new ModeSetting("Анимация", ANIM_IN_PLACE,
            ANIM_IN_PLACE, ANIM_SWING, ANIM_SWING_2, ANIM_SHIFT, ANIM_BREAK, ANIM_LUNGE, ANIM_SPEAR,
            ANIM_DOWN, ANIM_JELLY, ANIM_SLAP, ANIM_BONK, ANIM_CIRCLE, ANIM_REALISM, ANIM_STATIC);
    private final FloatSetting strength = new FloatSetting("Сила", 1.0F, 0.5F, 3.0F, 0.05F)
            .visible(() -> !anim.is(ANIM_STATIC));
    private final FloatSetting speed = new FloatSetting("Скорость", 1.0F, 0.1F, 3.0F, 0.05F)
            .visible(() -> !anim.is(ANIM_STATIC));
    private final BooleanSetting onlyOnTarget = new BooleanSetting("Только при наводке", false);

    private final BooleanSetting dragInChat = new BooleanSetting("Перетаскивание в чате", true);
    private final BooleanSetting outline = new BooleanSetting("Обводка при перетаскивании", true);
    private final FloatSetting outlineSize = new FloatSetting("Размер обводки", 3.0F, 1.0F, 8.0F, 0.5F)
            .visible(outline::get);

    private final AnimationUtils mainScaleAnim = new AnimationUtils(1.0F, SCALE_ANIM_SPEED, Easings.CUBIC_OUT);
    private final AnimationUtils offScaleAnim = new AnimationUtils(1.0F, SCALE_ANIM_SPEED, Easings.CUBIC_OUT);
    private final AnimationUtils outlineAnim = new AnimationUtils(0.0F, OUTLINE_IN_SPEED, Easings.CUBIC_OUT);
    private final AnimationUtils pulse = new AnimationUtils(0.0F, PULSE_IN_SPEED, Easings.CUBIC_OUT);

    private float mainTargetX;
    private float mainTargetY;
    private float offTargetX;
    private float offTargetY;
    private float mainOffsetX;
    private float mainOffsetY;
    private float offOffsetX;
    private float offOffsetY;
    private float mainScaleTarget = 1.0F;
    private float offScaleTarget = 1.0F;

    private boolean loaded;
    private boolean wasRightDown;
    private long lastFrameNanos;

    private boolean holding;
    private InteractionHand grabbed;
    private float grabCursorX;
    private float grabCursorY;
    private float grabOffsetX;
    private float grabOffsetY;

    private float mainPivotX;
    private float mainPivotY;
    private float mainPivotZ;
    private boolean mainPivotValid;
    private float offPivotX;
    private float offPivotY;
    private float offPivotZ;
    private boolean offPivotValid;

    private InteractionHand pulseHand;

    private InteractionHand outlineHand;
    private HumanoidArm lastMainArm;

    private int realismSide = -1;
    private boolean realismReady = true;

    public ViewModel() {
        super("ViewModel", "Позиция и анимации рук. Во вкладке Hands или в чате: клик по руке — перетащить, колесо — размер, ПКМ — сброс.",
                ModuleCategory.RENDER);
        instance = this;
        addSettings(anim, strength, speed, onlyOnTarget, dragInChat, outline, outlineSize);
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
        pulse.setValue(0.0F);
        pulseHand = null;
        realismSide = -1;
        realismReady = true;
        HandViewport.setWanted(false);
        HandViewport.reset();
        HandOutline.setWanted(false);
        HandOutline.setOutline(0.0F, outlineSize.get());
        super.onDisable();
    }

    public static void beginFrame() {
        ViewModel module = instance;
        Minecraft minecraft = Minecraft.getInstance();
        if (module == null || !module.isEnable() || minecraft.player == null) {
            HandViewport.setWanted(false);
            HandOutline.setWanted(false);
            return;
        }

        module.load();
        boolean active = module.chatDragContext() || module.editorContext();
        HandViewport.setWanted(active);
        HandOutline.setWanted(active);
    }

    public static void applyTransform(PoseStack poseStack, AbstractClientPlayer player, InteractionHand hand) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || poseStack == null || player == null || hand == null) {
            return;
        }
        module.load();

        boolean main = hand == InteractionHand.MAIN_HAND;
        HumanoidArm arm = main ? player.getMainArm() : player.getMainArm().getOpposite();
        float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float anchorX = ARM_X * side;

        if (HandViewport.isWanted()) {
            HandViewport.captureBase(hand, poseStack, anchorX, ARM_Y, ARM_Z);
        }

        float offsetX = main ? module.mainOffsetX : module.offOffsetX;
        float offsetY = main ? module.mainOffsetY : module.offOffsetY;
        if (offsetX != 0.0F || offsetY != 0.0F) {
            poseStack.translate(offsetX, offsetY, 0.0F);
        }

        boolean pivotValid = main ? module.mainPivotValid : module.offPivotValid;
        float pivotX = pivotValid ? (main ? module.mainPivotX : module.offPivotX) : anchorX;
        float pivotY = pivotValid ? (main ? module.mainPivotY : module.offPivotY) : ARM_Y;
        float pivotZ = pivotValid ? (main ? module.mainPivotZ : module.offPivotZ) : ARM_Z;

        float scaleValue = main ? module.mainScaleAnim.getValue() : module.offScaleAnim.getValue();
        if (hand == module.pulseHand) {
            scaleValue *= 1.0F + GRAB_PULSE * module.pulse.getValue();
        }
        if (scaleValue != 1.0F) {
            poseStack.translate(pivotX, pivotY, pivotZ);
            poseStack.scale(scaleValue, scaleValue, scaleValue);
            poseStack.translate(-pivotX, -pivotY, -pivotZ);
        }

        if (HandViewport.isWanted()) {
            HandViewport.capture(hand, poseStack, anchorX, ARM_Y, ARM_Z);
        }
    }

    public static boolean applySwing(PoseStack poseStack, HumanoidArm arm, float swingProgress) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || poseStack == null || arm == null) {
            return false;
        }
        module.load();

        float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        poseStack.translate(side * ARM_X, ARM_Y, ARM_Z);

        float progress = Mth.clamp(swingProgress * module.speed.get(), 0.0F, 1.0F);
        if (progress <= 0.0F || module.anim.is(ANIM_STATIC) || !module.targetAllows()) {
            return true;
        }

        module.applyPreset(poseStack, (int) side, progress, module.strength.get());
        return true;
    }

    public static boolean cancelArmDip(PoseStack poseStack, HumanoidArm arm, float swingProgress) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || poseStack == null || arm == null || swingProgress <= 0.0F) {
            return false;
        }

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

    public static float equipProgress(float equippedProgress) {
        ViewModel module = instance;
        return module != null && module.isEnable() ? 0.0F : equippedProgress;
    }

    private void applyPreset(PoseStack poseStack, int side, float f, float power) {
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
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * (45.0F + f2 * -20.0F * power)));
                poseStack.mulPose(Axis.ZP.rotationDegrees(armSide * f3 * -20.0F * power));
                poseStack.mulPose(Axis.YP.rotationDegrees(armSide * -45.0F));
            }
        }
    }

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

    public boolean isHandHovered(float mouseX, float mouseY) {
        if (HandOutline.isMaskReady() && HandOutline.isHandCoveredAt(mouseX, mouseY)) {
            return true;
        }
        if (HandViewport.isReady(InteractionHand.MAIN_HAND)) {
            float ax = HandViewport.anchorX(InteractionHand.MAIN_HAND);
            float ay = HandViewport.anchorY(InteractionHand.MAIN_HAND);
            float radius = Math.max(35.0F, HandViewport.pixelsPerUnitY(InteractionHand.MAIN_HAND) * 0.45F);
            if (distance(mouseX, mouseY, ax, ay) <= radius * radius) {
                return true;
            }
        }
        if (HandViewport.isReady(InteractionHand.OFF_HAND)) {
            float ax = HandViewport.anchorX(InteractionHand.OFF_HAND);
            float ay = HandViewport.anchorY(InteractionHand.OFF_HAND);
            float radius = Math.max(35.0F, HandViewport.pixelsPerUnitY(InteractionHand.OFF_HAND) * 0.45F);
            if (distance(mouseX, mouseY, ax, ay) <= radius * radius) {
                return true;
            }
        }
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        if (width > 0 && height > 0) {
            float relX = mouseX / (float) width;
            float relY = mouseY / (float) height;
            return relY >= 0.45F && (relX <= 0.45F || relX >= 0.55F);
        }
        return false;
    }

    public boolean onMousePress(int button, int action) {
        if (!isEnable() || action != GLFW.GLFW_PRESS) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.screen instanceof ChatScreen) || !dragInChat.get()) {
            return false;
        }
        load();

        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (holding && grabbed != null) {
                resetHand(grabbed);
                release(true);
            } else {
                resetAll();
            }
            return true;
        }

        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return false;
        }

        float mouseX = HandViewport.mouseGuiX(minecraft);
        float mouseY = HandViewport.mouseGuiY(minecraft);
        if (!isHandHovered(mouseX, mouseY)) {
            return false;
        }

        InteractionHand hand = pickHand(mouseX, mouseY);
        if (hand == null) {
            return false;
        }

        grab(hand, mouseX, mouseY);
        return true;
    }

    public boolean onScroll(double yOffset) {
        if (!isEnable() || yOffset == 0.0D) {
            return false;
        }
        if (!chatDragContext() && !editorContext()) {
            return false;
        }

        InteractionHand targetHand = grabbed;
        if (targetHand == null) {
            Minecraft mc = Minecraft.getInstance();
            float mx = HandViewport.mouseGuiX(mc);
            float my = HandViewport.mouseGuiY(mc);
            if (isHandHovered(mx, my)) {
                targetHand = pickHand(mx, my);
            }
        }
        if (targetHand == null) {
            return false;
        }

        load();
        float step = yOffset > 0.0D ? SCALE_STEP : -SCALE_STEP;
        if (targetHand == InteractionHand.MAIN_HAND) {
            mainScaleTarget = Mth.clamp(mainScaleTarget + step, SCALE_MIN, SCALE_MAX);
            mainScaleAnim.setSpeed(SCALE_ANIM_SPEED);
            mainScaleAnim.update(mainScaleTarget);
        } else {
            offScaleTarget = Mth.clamp(offScaleTarget + step, SCALE_MIN, SCALE_MAX);
            offScaleAnim.setSpeed(SCALE_ANIM_SPEED);
            offScaleAnim.update(offScaleTarget);
        }

        save();
        return true;
    }

    private void grab(InteractionHand hand, float mouseX, float mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int guiWidth = minecraft.getWindow().getGuiScaledWidth();
        int guiHeight = minecraft.getWindow().getGuiScaledHeight();

        grabbed = hand;
        outlineHand = hand;
        holding = true;
        grabCursorX = mouseX;
        grabCursorY = mouseY;

        boolean main = hand == InteractionHand.MAIN_HAND;
        grabOffsetX = main ? mainTargetX : offTargetX;
        grabOffsetY = main ? mainTargetY : offTargetY;

        setPivot(hand, mouseX, mouseY, guiWidth, guiHeight);

        pulseHand = hand;
        pulse.setSpeed(PULSE_IN_SPEED);
        pulse.update(1.0F);

        outlineAnim.setSpeed(OUTLINE_IN_SPEED);
        outlineAnim.update(1.0F);
        HandOutline.setOutline(outline.get() ? outlineAnim.getValue() : 0.0F, outlineSize.get());
    }

    private void setPivot(InteractionHand hand, float mouseX, float mouseY, int guiWidth, int guiHeight) {
        boolean main = hand == InteractionHand.MAIN_HAND;
        float pivotX = 0.0F;
        float pivotY = 0.0F;
        float pivotZ = 0.0F;
        boolean valid = false;

        Matrix4fc matrix = HandViewport.matrix(hand);
        float windowDepth = HandOutline.depthAt(mouseX, mouseY);
        if (matrix != null && HandViewport.isBaseReady(hand) && windowDepth > 0.0F
                && guiWidth > 0 && guiHeight > 0) {
            INVERTED.set(matrix).invert();
            UNPROJECT.set(mouseX / (float) guiWidth * 2.0F - 1.0F,
                    1.0F - mouseY / (float) guiHeight * 2.0F,
                    windowDepth * 2.0F - 1.0F,
                    1.0F);
            INVERTED.transform(UNPROJECT);

            float w = UNPROJECT.w();
            if (Math.abs(w) > 1.0E-6F) {
                float x = UNPROJECT.x() / w;
                float y = UNPROJECT.y() / w;
                float z = UNPROJECT.z() / w;
                if (Float.isFinite(x) && Float.isFinite(y) && Float.isFinite(z)) {
                    pivotX = x;
                    pivotY = y;
                    pivotZ = z;
                    valid = true;
                }
            }
        }

        if (!valid) {
            Minecraft minecraft = Minecraft.getInstance();
            HumanoidArm arm = minecraft.player != null ? minecraft.player.getMainArm() : HumanoidArm.RIGHT;
            if (!main) {
                arm = arm.getOpposite();
            }
            pivotX = ARM_X * (arm == HumanoidArm.RIGHT ? 1.0F : -1.0F);
            pivotY = ARM_Y;
            pivotZ = ARM_Z;
        }

        if (main) {
            mainPivotX = pivotX;
            mainPivotY = pivotY;
            mainPivotZ = pivotZ;
            mainPivotValid = true;
        } else {
            offPivotX = pivotX;
            offPivotY = pivotY;
            offPivotZ = pivotZ;
            offPivotValid = true;
        }
    }

    private void drag(float mouseX, float mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int guiWidth = minecraft.getWindow().getGuiScaledWidth();
        int guiHeight = minecraft.getWindow().getGuiScaledHeight();
        if (guiWidth <= 0 || guiHeight <= 0) {
            return;
        }

        boolean main = grabbed == InteractionHand.MAIN_HAND;
        boolean pivotValid = main ? mainPivotValid : offPivotValid;
        Matrix4fc matrix = HandViewport.matrix(grabbed);
        if (!pivotValid || matrix == null || !HandViewport.isBaseReady(grabbed)) {
            dragFallback(mouseX, mouseY, guiHeight);
            return;
        }

        float pivotX = main ? mainPivotX : offPivotX;
        float pivotY = main ? mainPivotY : offPivotY;
        float pivotZ = main ? mainPivotZ : offPivotZ;

        float m00 = matrix.m00();
        float m10 = matrix.m10();
        float m01 = matrix.m01();
        float m11 = matrix.m11();
        float m03 = matrix.m03();
        float m13 = matrix.m13();

        float clipX = m00 * pivotX + m10 * pivotY + matrix.m20() * pivotZ + matrix.m30();
        float clipY = m01 * pivotX + m11 * pivotY + matrix.m21() * pivotZ + matrix.m31();
        float clipW = m03 * pivotX + m13 * pivotY + matrix.m23() * pivotZ + matrix.m33();

        float ndcX = mouseX / (float) guiWidth * 2.0F - 1.0F;
        float ndcY = 1.0F - mouseY / (float) guiHeight * 2.0F;

        float e00 = m00 - ndcX * m03;
        float e01 = m10 - ndcX * m13;
        float e10 = m01 - ndcY * m03;
        float e11 = m11 - ndcY * m13;
        float determinant = e00 * e11 - e01 * e10;
        if (Math.abs(determinant) < 1.0E-6F) {
            dragFallback(mouseX, mouseY, guiHeight);
            return;
        }

        float r0 = ndcX * clipW - clipX;
        float r1 = ndcY * clipW - clipY;
        float targetX = (r0 * e11 - e01 * r1) / determinant;
        float targetY = (e00 * r1 - e10 * r0) / determinant;
        if (!Float.isFinite(targetX) || !Float.isFinite(targetY)) {
            dragFallback(mouseX, mouseY, guiHeight);
            return;
        }

        setTarget(grabbed, targetX, targetY);
    }

    private void dragFallback(float mouseX, float mouseY, int guiHeight) {
        float unitsPerPixel = DRAG_UNITS_PER_PIXEL / (float) guiHeight;
        setTarget(grabbed,
                grabOffsetX + (mouseX - grabCursorX) * unitsPerPixel,
                grabOffsetY - (mouseY - grabCursorY) * unitsPerPixel);
    }

    private InteractionHand pickHand(float mouseX, float mouseY) {
        boolean mainReady = HandViewport.isReady(InteractionHand.MAIN_HAND);
        boolean offReady = HandViewport.isReady(InteractionHand.OFF_HAND);
        if (mainReady && offReady) {
            return distance(mouseX, mouseY, HandViewport.anchorX(InteractionHand.MAIN_HAND),
                    HandViewport.anchorY(InteractionHand.MAIN_HAND))
                    <= distance(mouseX, mouseY, HandViewport.anchorX(InteractionHand.OFF_HAND),
                    HandViewport.anchorY(InteractionHand.OFF_HAND))
                    ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        }
        if (mainReady) {
            return InteractionHand.MAIN_HAND;
        }
        if (offReady) {
            return InteractionHand.OFF_HAND;
        }

        Minecraft minecraft = Minecraft.getInstance();
        HumanoidArm arm = minecraft.player != null ? minecraft.player.getMainArm() : HumanoidArm.RIGHT;
        boolean rightHalf = mouseX >= (float) minecraft.getWindow().getGuiScaledWidth() * 0.5F;
        return rightHalf == (arm == HumanoidArm.RIGHT) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    private static float distance(float mouseX, float mouseY, float x, float y) {
        float deltaX = mouseX - x;
        float deltaY = mouseY - y;
        return deltaX * deltaX + deltaY * deltaY;
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
            mainTargetX = 0.0F;
            mainTargetY = 0.0F;
            mainScaleTarget = 1.0F;
            mainScaleAnim.setSpeed(SCALE_ANIM_SPEED);
            mainScaleAnim.update(mainScaleTarget);
        } else {
            offTargetX = 0.0F;
            offTargetY = 0.0F;
            offScaleTarget = 1.0F;
            offScaleAnim.setSpeed(SCALE_ANIM_SPEED);
            offScaleAnim.update(offScaleTarget);
        }
        save();
    }

    private void resetAll() {
        holding = false;
        grabbed = null;
        mainTargetX = 0.0F;
        mainTargetY = 0.0F;
        offTargetX = 0.0F;
        offTargetY = 0.0F;
        mainScaleTarget = 1.0F;
        offScaleTarget = 1.0F;
        mainScaleAnim.setSpeed(SCALE_ANIM_SPEED);
        mainScaleAnim.update(mainScaleTarget);
        offScaleAnim.setSpeed(SCALE_ANIM_SPEED);
        offScaleAnim.update(offScaleTarget);
        save();
    }

    private void setTarget(InteractionHand hand, float x, float y) {
        if (hand == InteractionHand.MAIN_HAND) {
            mainTargetX = x;
            mainTargetY = y;
        } else {
            offTargetX = x;
            offTargetY = y;
        }
    }

    private void handleMainHandSwap() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        HumanoidArm arm = minecraft.player.getMainArm();
        if (lastMainArm == null) {
            lastMainArm = arm;
            return;
        }
        if (arm == lastMainArm) {
            return;
        }

        lastMainArm = arm;
        mainTargetX = -mainTargetX;
        mainOffsetX = -mainOffsetX;
        offTargetX = -offTargetX;
        offOffsetX = -offOffsetX;
        save();
    }

    private boolean chatDragContext() {
        return Minecraft.getInstance().screen instanceof ChatScreen && dragInChat.get();
    }

    private boolean editorContext() {
        return Minecraft.getInstance().screen instanceof ClickGuiScreen
                && ((ClickGuiScreen) Minecraft.getInstance().screen).isHandsTab();
    }

    public boolean editorReset(int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_RIGHT || !isEnable()) {
            return false;
        }
        if (holding && grabbed != null) {
            resetHand(grabbed);
            release(true);
        } else {
            resetAll();
        }
        return true;
    }

    public boolean editorGrab(float mouseX, float mouseY) {
        if (!isEnable() || Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return false;
        }
        if (!isHandHovered(mouseX, mouseY)) {
            return false;
        }
        InteractionHand hand = pickHand(mouseX, mouseY);
        if (hand == null) {
            return false;
        }
        grab(hand, mouseX, mouseY);
        return true;
    }

    public FloatSetting strengthSetting() {
        return strength;
    }

    public FloatSetting speedSetting() {
        return speed;
    }

    private void onHudRender(GuiGraphics graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        float delta = frameDelta();

        if (minecraft.player == null || minecraft.level == null || !isEnable()) {
            release(false);
            outlineHand = null;
            outlineAnim.update(0.0F);
            HandOutline.setWanted(false);
            HandOutline.setOutline(0.0F, outlineSize.get());
            return;
        }

        load();
        handleMainHandSwap();

        boolean canDrag = chatDragContext() || editorContext();
        if (!canDrag) {
            release(false);
        }

        long window = minecraft.getWindow().getWindow();
        boolean leftDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rightDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (rightDown && !wasRightDown && holding && grabbed != null) {
            resetHand(grabbed);
            release(true);
        }
        wasRightDown = rightDown;

        if (holding && grabbed != null && canDrag && leftDown) {
            drag(HandViewport.mouseGuiX(minecraft), HandViewport.mouseGuiY(minecraft));
        }
        if (!leftDown && holding) {
            release(true);
        }

        smooth(delta);
        mainScaleAnim.update(mainScaleTarget);
        offScaleAnim.update(offScaleTarget);
        if (pulse.getTarget() != (holding ? 1.0F : 0.0F)) {
            pulse.setSpeed(holding ? PULSE_IN_SPEED : PULSE_OUT_SPEED);
        }
        pulse.update(holding ? 1.0F : 0.0F);

        boolean dragging = holding && grabbed != null && outline.get();
        if (dragging) {
            outlineHand = grabbed;
        }
        float target = dragging ? 1.0F : 0.0F;
        if (outlineAnim.getTarget() != target) {
            outlineAnim.setSpeed(dragging ? OUTLINE_IN_SPEED : OUTLINE_OUT_SPEED);
        }
        outlineAnim.update(target);

        float alpha = outlineAnim.getValue();
        if (alpha <= 0.004F) {
            outlineHand = null;
            alpha = 0.0F;
        }

        HandOutline.setWanted(canDrag);
        HandOutline.setOutline(alpha, outlineSize.get());
        HandOutline.composite();

        if (!HandOutline.isShaderReady() && outlineHand != null) {
            drawRectOutline(graphics.pose());
        }

        boolean noMask = HandOutline.wantedFor() > 2_000_000_000L && !HandOutline.isMaskReady();
        if (canDrag && (noMask || !HandOutline.isHooked())) {
            HandOutline.reportStale();
        }
        if (canDrag && !HandViewport.isAnyReady()) {
            HandViewport.reportStale();
        }
    }

    private void smooth(float delta) {
        float factor = 1.0F - (float) Math.exp(-FOLLOW_RATE * delta);
        mainOffsetX = follow(mainOffsetX, mainTargetX, factor);
        mainOffsetY = follow(mainOffsetY, mainTargetY, factor);
        offOffsetX = follow(offOffsetX, offTargetX, factor);
        offOffsetY = follow(offOffsetY, offTargetY, factor);
    }

    private static float follow(float current, float target, float factor) {
        float delta = target - current;
        if (Math.abs(delta) < 1.0E-4F) {
            return target;
        }
        return current + delta * factor;
    }

    private float frameDelta() {
        long now = System.nanoTime();
        float delta = lastFrameNanos == 0L ? 1.0F / 60.0F : (float) (now - lastFrameNanos) * 1.0E-9F;
        lastFrameNanos = now;
        return Mth.clamp(delta, 0.0F, MAX_FRAME_DELTA);
    }

    private void drawRectOutline(PoseStack stack) {
        InteractionHand hand = outlineHand;
        if (hand == null || !HandViewport.isReady(hand)) {
            return;
        }

        float alpha = outlineAnim.getValue();
        float centerX = HandViewport.anchorX(hand);
        float centerY = HandViewport.anchorY(hand);
        float half = Math.max(10.0F, HandViewport.pixelsPerUnitY(hand) * outlineSize.get() * 0.04F);
        float radius = half * 0.35F;
        float size = half * 2.0F;

        int theme = ThemeManager.getColor();
        GuiRender.roundedRect(stack, centerX - half, centerY - half, size, size, radius,
                ColorUtils.multAlpha(theme, alpha * OUTLINE_RECT_FILL));
        GuiRender.roundedOutline(stack, centerX - half, centerY - half, size, size,
                OUTLINE_RECT_THICKNESS, ColorUtils.multAlpha(theme, alpha), radius);
    }

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
            mainTargetX = mainOffsetX = readFloat(json, "mainX", 0.0F);
            mainTargetY = mainOffsetY = readFloat(json, "mainY", 0.0F);
            offTargetX = offOffsetX = readFloat(json, "offX", 0.0F);
            offTargetY = offOffsetY = readFloat(json, "offY", 0.0F);
            mainScaleTarget = Mth.clamp(readFloat(json, "mainScale", 1.0F), SCALE_MIN, SCALE_MAX);
            offScaleTarget = Mth.clamp(readFloat(json, "offScale", 1.0F), SCALE_MIN, SCALE_MAX);
            mainScaleAnim.setValue(mainScaleTarget);
            offScaleAnim.setValue(offScaleTarget);
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to load viewmodel layout", exception);
        }
    }

    private void save() {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("mainX", mainTargetX);
            json.addProperty("mainY", mainTargetY);
            json.addProperty("offX", offTargetX);
            json.addProperty("offY", offTargetY);
            json.addProperty("mainScale", mainScaleTarget);
            json.addProperty("offScale", offScaleTarget);

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
