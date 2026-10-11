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
// Перемещение — только перетаскиванием, ползунков координат нет (как в референсе):
// двойной клик по руке в чате, движение мыши, колесо — масштаб руки, ПКМ — сброс.
// Пиксели курсора переводятся в мировые юниты через якобиан проекции руки, поэтому
// рука идёт ровно под курсором на любом fov, масштабе и позиции. Сама рука при этом
// догоняет курсор экспоненциальным сглаживанием — скользит с небольшим отставанием.
//
// Обводка рисуется по силуэту руки (HandOutline: разница глубины до и после рук) и
// видна только пока рука в перетаскивании.
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
    private static final float SCALE_STEP = 0.22F;
    private static final float SCALE_ANIM_SPEED = 10.0F;
    // Предел смещения: при 0.8 юнитах рука никогда не уходит за экран целиком — её
    // всегда можно схватить снова, а ПКМ без ползунков координат остаётся запасным выходом.
    private static final float OFFSET_LIMIT = 0.8F;
    private static final float FALLBACK_DEPTH = 0.72F;
    private static final float DRAG_UNITS_PER_PIXEL = 3.2F;

    // Скольжение за курсором: 1/с. Меньше — сильнее отставание, больше — резче следует.
    private static final float FOLLOW_RATE = 18.0F;
    private static final float MAX_FRAME_DELTA = 0.05F;

    private static final float OUTLINE_IN_SPEED = 9.1F;
    private static final float OUTLINE_OUT_SPEED = 6.7F;
    private static final float OUTLINE_RECT_FILL = 0.10F;
    private static final float OUTLINE_RECT_THICKNESS = 1.4F;
    private static final long DOUBLE_CLICK_MS = 300L;

    private static final double C1 = 1.70158D;
    private static final double C3 = 2.70158D;
    private static final double C4 = 2.0943951023931953D;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/viewmodel.json");

    // Переиспользуемый результат якобиана: перетаскивание идёт каждый кадр.
    private static final float[] JACOBIAN = new float[4];

    private static boolean eventsRegistered;
    private static ViewModel instance;

    private final ModeSetting anim = new ModeSetting("Анимация", ANIM_IN_PLACE,
            ANIM_IN_PLACE, ANIM_SWING, ANIM_SWING_2, ANIM_SHIFT, ANIM_BREAK, ANIM_LUNGE, ANIM_SPEAR,
            ANIM_DOWN, ANIM_JELLY, ANIM_SLAP, ANIM_BONK, ANIM_CIRCLE, ANIM_REALISM, ANIM_STATIC);
    private final FloatSetting strength = new FloatSetting("Сила", 1.0F, 0.5F, 3.0F, 0.05F)
            .visible(() -> !anim.is(ANIM_STATIC));
    private final BooleanSetting onlyOnTarget = new BooleanSetting("Только при наводке", false);

    private final BooleanSetting dragInChat = new BooleanSetting("Перетаскивание в чате", true);
    private final BooleanSetting outline = new BooleanSetting("Обводка при перетаскивании", true);
    private final FloatSetting outlineSize = new FloatSetting("Размер обводки", 3.0F, 1.0F, 8.0F, 0.5F)
            .visible(outline::get);

    private final AnimationUtils mainScaleAnim = new AnimationUtils(1.0F, SCALE_ANIM_SPEED, Easings.CUBIC_OUT);
    private final AnimationUtils offScaleAnim = new AnimationUtils(1.0F, SCALE_ANIM_SPEED, Easings.CUBIC_OUT);
    private final AnimationUtils outlineAnim = new AnimationUtils(0.0F, OUTLINE_IN_SPEED, Easings.CUBIC_OUT);

    // Целевые значения — то, что даёт перетаскивание и что сохраняется;
    // offset* — сглаженные, именно они попадают в матрицу руки.
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
    private long lastClickMs;
    private long lastFrameNanos;

    private boolean holding;
    private InteractionHand grabbed;
    private final Matrix4f grabMatrix = new Matrix4f();
    private boolean grabMatrixValid;
    private float grabCursorX;
    private float grabCursorY;
    private float grabNdcX;
    private float grabNdcY;
    private float grabOffsetX;
    private float grabOffsetY;
    private float grabScale = 1.0F;
    private float grabPivotX;
    private float grabPivotY;
    private boolean grabPivotValid;
    private float grabDepth = FALLBACK_DEPTH;

    private InteractionHand outlineHand;
    private HumanoidArm lastMainArm;

    private int realismSide = -1;
    private boolean realismReady = true;

    public ViewModel() {
        super("ViewModel", "Позиция и анимации рук. Двойной клик по руке в чате — перетащить, колесо — размер, ПКМ — сброс.",
                ModuleCategory.RENDER);
        instance = this;
        addSettings(anim, strength, onlyOnTarget, dragInChat, outline, outlineSize);
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
        HandViewport.setWanted(false);
        HandViewport.reset();
        HandOutline.setWanted(false);
        HandOutline.setOutline(0.0F, outlineSize.get());
        super.onDisable();
    }

    // ------------------------------------------------------------------
    // Точки входа из MixinItemInHandRenderer — статические, без аллокаций.
    // ------------------------------------------------------------------

    // HEAD renderHandsWithItems: решает, нужны ли в этом кадре снимки рук и маска.
    public static void beginFrame() {
        ViewModel module = instance;
        Minecraft minecraft = Minecraft.getInstance();
        if (module == null || !module.isEnable() || minecraft.player == null) {
            HandViewport.setWanted(false);
            HandOutline.setWanted(false);
            return;
        }

        module.load();
        boolean inChat = minecraft.screen instanceof ChatScreen;
        boolean wanted = inChat && (module.dragInChat.get() || module.outline.get());
        HandViewport.setWanted(wanted);
        HandOutline.setWanted(inChat && module.dragInChat.get());
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

        float scaleValue = main ? module.mainScaleAnim.getValue() : module.offScaleAnim.getValue();
        if (scaleValue != 1.0F) {
            // Масштаб вокруг точки крепления: рука растёт на месте, а не уезжает к камере.
            poseStack.translate(anchorX, ARM_Y, ARM_Z);
            poseStack.scale(scaleValue, scaleValue, scaleValue);
            poseStack.translate(-anchorX, -ARM_Y, -ARM_Z);
        }

        float offsetX = main ? module.mainOffsetX : module.offOffsetX;
        float offsetY = main ? module.mainOffsetY : module.offOffsetY;
        if (offsetX != 0.0F || offsetY != 0.0F) {
            poseStack.translate(offsetX, offsetY, 0.0F);
        }

        if (HandViewport.isWanted()) {
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

        float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        // equippedProgress сюда намеренно не передаётся: опускания руки больше нет.
        poseStack.translate(side * ARM_X, ARM_Y, ARM_Z);

        float progress = Mth.clamp(swingProgress, 0.0F, 1.0F);
        if (progress <= 0.0F || module.anim.is(ANIM_STATIC) || !module.targetAllows()) {
            return true;
        }

        module.applyPreset(poseStack, (int) side, progress, module.strength.get());
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
        // f2/f3 — те же величины, что в референсе; env гасит постоянные повороты.
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
    // Ввод: двойной клик по руке, перетаскивание, колесо, ПКМ.
    // ------------------------------------------------------------------

    // Вызывается из MixinMouseHandler.onPress. true — событие съедено, чат его не получит.
    public boolean onMousePress(int button, int action) {
        if (!isEnable() || action != GLFW.GLFW_PRESS) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.screen instanceof ChatScreen) || !dragInChat.get()) {
            return false;
        }
        load();

        // ПКМ сбрасывает руку, которая сейчас в перетаскивании. Чужие клики не съедаются.
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (!holding || grabbed == null) {
                return false;
            }
            resetHand(grabbed);
            release(true);
            return true;
        }

        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return false;
        }

        long now = System.currentTimeMillis();
        boolean doubleClick = now - lastClickMs <= DOUBLE_CLICK_MS;
        lastClickMs = now;
        if (!doubleClick) {
            return false;
        }

        float mouseX = HandViewport.mouseGuiX(minecraft);
        float mouseY = HandViewport.mouseGuiY(minecraft);
        // Хватать можно только пиксели руки: маска силуэта, а не квадрат вокруг точки.
        if (!HandOutline.isHandCoveredAt(mouseX, mouseY)) {
            return false;
        }

        InteractionHand hand = pickHand(mouseX, mouseY);
        if (hand == null) {
            return false;
        }

        grab(hand, mouseX, mouseY);
        return true;
    }

    // Колесо мыши, пока рука схвачена, — масштаб этой руки. true — событие съедено.
    public boolean onScroll(double yOffset) {
        if (!isEnable() || yOffset == 0.0D || !holding || grabbed == null) {
            return false;
        }
        if (!(Minecraft.getInstance().screen instanceof ChatScreen)) {
            return false;
        }

        load();
        float step = yOffset > 0.0D ? SCALE_STEP : -SCALE_STEP;
        if (grabbed == InteractionHand.MAIN_HAND) {
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
        grabNdcX = guiWidth > 0 ? mouseX / (float) guiWidth * 2.0F - 1.0F : 0.0F;
        grabNdcY = guiHeight > 0 ? 1.0F - mouseY / (float) guiHeight * 2.0F : 0.0F;

        boolean main = hand == InteractionHand.MAIN_HAND;
        grabOffsetX = main ? mainTargetX : offTargetX;
        grabOffsetY = main ? mainTargetY : offTargetY;
        grabScale = main ? mainScaleTarget : offScaleTarget;
        grabPivotValid = HandViewport.isReady(hand);
        grabPivotX = HandViewport.anchorX(hand);
        grabPivotY = HandViewport.anchorY(hand);

        Matrix4fc matrix = HandViewport.matrix(hand);
        grabMatrixValid = matrix != null && HandViewport.isBaseReady(hand);
        if (grabMatrixValid) {
            grabMatrix.set(matrix);
            grabDepth = HandViewport.depth(hand);
        } else {
            grabDepth = FALLBACK_DEPTH;
        }

        outlineAnim.setSpeed(OUTLINE_IN_SPEED);
        outlineAnim.update(1.0F);
        HandOutline.setOutline(outline.get() ? outlineAnim.getValue() : 0.0F, outlineSize.get());
    }

    private void drag(float mouseX, float mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        int guiWidth = minecraft.getWindow().getGuiScaledWidth();
        int guiHeight = minecraft.getWindow().getGuiScaledHeight();
        if (guiWidth <= 0 || guiHeight <= 0) {
            return;
        }

        boolean main = grabbed == InteractionHand.MAIN_HAND;
        float scaleNow = main ? mainScaleAnim.getValue() : offScaleAnim.getValue();
        float targetX = 0.0F;
        float targetY = 0.0F;
        boolean solved = false;

        // Якобиан проекции руки: сколько экранных пикселей стоит один мировой юнит
        // в точке захвата. Без него рука тянулась бы с разной скоростью по осям.
        if (grabMatrixValid && grabDepth > 1.0E-4F) {
            jacobian(grabMatrix, grabDepth, grabNdcX, grabNdcY, guiWidth, guiHeight);
            float determinant = JACOBIAN[0] * JACOBIAN[3] - JACOBIAN[2] * JACOBIAN[1];
            if (Math.abs(determinant) > 1.0E-4F) {
                float ratio = grabScale > 1.0E-4F ? scaleNow / grabScale : 1.0F;
                float pivotX = grabPivotValid ? grabPivotX : grabCursorX;
                float pivotY = grabPivotValid ? grabPivotY : grabCursorY;
                // Масштаб меняется вокруг точки крепления: пиксель под курсором должен
                // оставаться тем же пикселем руки, иначе рука уезжает при колёсике.
                float deltaX = mouseX - pivotX - ratio * (grabCursorX - pivotX);
                float deltaY = mouseY - pivotY - ratio * (grabCursorY - pivotY);
                targetX = grabOffsetX + (JACOBIAN[3] * deltaX - JACOBIAN[2] * deltaY) / determinant;
                targetY = grabOffsetY + (-JACOBIAN[1] * deltaX + JACOBIAN[0] * deltaY) / determinant;
                solved = true;
            }
        }

        if (!solved) {
            // Запасной вариант: цена пикселя от высоты экрана, как в референсе.
            float unitsPerPixel = DRAG_UNITS_PER_PIXEL / (float) guiHeight;
            targetX = grabOffsetX + (mouseX - grabCursorX) * unitsPerPixel;
            targetY = grabOffsetY - (mouseY - grabCursorY) * unitsPerPixel;
        }

        setTarget(grabbed, Mth.clamp(targetX, -OFFSET_LIMIT, OFFSET_LIMIT),
                Mth.clamp(targetY, -OFFSET_LIMIT, OFFSET_LIMIT));
    }

    // d(экран)/d(юнит) в точке захвата; знак по Y перевёрнут — экранный Y смотрит вниз.
    private static void jacobian(Matrix4fc matrix, float depth, float ndcX, float ndcY, int width, int height) {
        float m00 = matrix.m00();
        float m01 = matrix.m01();
        float m03 = matrix.m03();
        float m10 = matrix.m10();
        float m11 = matrix.m11();
        float m13 = matrix.m13();
        float halfWidth = (float) width * 0.5F;
        float halfHeight = (float) height * 0.5F;

        JACOBIAN[0] = (m00 - ndcX * m03) / depth * halfWidth;
        JACOBIAN[1] = -(m01 - ndcY * m03) / depth * halfHeight;
        JACOBIAN[2] = (m10 - ndcX * m13) / depth * halfWidth;
        JACOBIAN[3] = -(m11 - ndcY * m13) / depth * halfHeight;
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

        // Точки рук не снялись: половина экрана и сторона основной руки.
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

    private void setTarget(InteractionHand hand, float x, float y) {
        if (hand == InteractionHand.MAIN_HAND) {
            mainTargetX = x;
            mainTargetY = y;
        } else {
            offTargetX = x;
            offTargetY = y;
        }
    }

    // Смена основной руки зеркалит раскладку, иначе руки меняются местами.
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

    // ------------------------------------------------------------------
    // Кадр HUD: сглаживание, обводка, состояние для HandOutline.
    // ------------------------------------------------------------------

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

        boolean canDrag = minecraft.screen instanceof ChatScreen && dragInChat.get();
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
            // Отпустили кнопку вне чата или чат закрылся: рука доезжает до цели сама.
            release(true);
        }

        smooth(delta);
        mainScaleAnim.update(mainScaleTarget);
        offScaleAnim.update(offScaleTarget);

        // Обводка — только пока рука в перетаскивании.
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

    // Экспоненциальное сглаживание, не зависящее от частоты кадров: рука скользит за
    // курсором с небольшим отставанием и так же плавно доезжает после отпускания.
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

    // Запасной вариант на случай, если шейдеры обводки не собрались: рамка по точке руки.
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
