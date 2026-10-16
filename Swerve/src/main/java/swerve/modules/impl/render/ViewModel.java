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
import swerve.ui.newgui.GuiFonts;
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
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class ViewModel extends Module {

    private static final String ANIM_IN_PLACE = "На месте";
    private static final String ANIM_OLD = "1.8";
    private static final String ANIM_BLOCK = "Блок";
    private static final String ANIM_SWIPE = "Свайп";
    private static final String ANIM_CHOP = "Рубка";
    private static final String ANIM_SMOOTH = "Плавная";
    private static final String ANIM_PUSH = "Толкание";
    private static final String ANIM_TREMBLE = "Дрожь";
    private static final String ANIM_SELF = "На себя";
    private static final String ANIM_GLIDE = "Скольжение";
    private static final String ANIM_VERTICAL = "Вертикальная";
    private static final String ANIM_HORIZONTAL = "Горизонтальная";
    private static final String ANIM_REALISM = "Реализм";
    private static final String ANIM_STATIC = "Статично";

    private static final float ARM_X = 0.56F;
    private static final float ARM_Y = -0.52F;
    private static final float ARM_Z = -0.72F;

    // Ванильный applyItemArmTransform: translate(side*0.56, -0.52 + equip*-0.6, -0.72),
    // у пустой руки в renderPlayerArm тот же член equip*-0.6. equipProgress() отдаёт финал
    // экипировки (1.0), поэтому настоящий якорь руки лежит на ARM_EQUIP_DROP ниже ARM_Y.
    private static final float ARM_EQUIP_DROP = -0.6F;
    private static final float ARM_ANCHOR_Y = ARM_Y + ARM_EQUIP_DROP;

    private static final float ARM_SWING_X = -0.3F;
    private static final float ARM_SWING_Y = 0.4F;
    private static final float ARM_SWING_Z = -0.4F;

    private static final float SCALE_MIN = 0.2F;
    private static final float SCALE_MAX = 5.0F;
    private static final float SCALE_STEP = 0.22F;
    private static final float SCALE_ANIM_SPEED = 10.0F;

    private static final float GRAB_PULSE = 0.06F;
    private static final float PULSE_IN_SPEED = 16.0F;
    private static final float PULSE_OUT_SPEED = 8.0F;

    private static final float FOLLOW_RATE = 18.0F;
    private static final float MAX_FRAME_DELTA = 0.05F;

    private static final float OUTLINE_IN_SPEED = 4.5F;
    private static final float OUTLINE_OUT_SPEED = 6.7F;
    private static final float OUTLINE_RECT_FILL = 0.10F;
    private static final float OUTLINE_RECT_THICKNESS = 1.4F;

    private static final double C1 = 1.70158D;
    // [!] Было 2.70158 (= C1 + 1) при мёртвом C1: для easeInOutBack нужен c2 = c1 * 1.525,
    // из-за опечатки пресет «Плавная» давал не тот выброс.
    private static final double C3 = C1 * 1.525D;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/viewmodel.json");

    // Конфиг уходит на диск из отдельного демона: на игровом потоке остаётся только сборка
    // JSON, а createDirectories / writeString / move — это блокирующие вызовы (правило: не
    // блокировать render/tick). Дебаунс склеивает серию правок от колеса и драга.
    private static final long WRITE_DEBOUNCE_MS = 400L;
    private static final Object WRITE_LOCK = new Object();
    private static volatile String pendingJson;
    private static volatile boolean writePending;
    private static Thread configWriter;

    private static boolean eventsRegistered;
    private static ViewModel instance;

    public final FloatSetting strength = new FloatSetting("Сила", 1.0F, 0.1F, 2.0F, 0.05F);
    public final FloatSetting speed = new FloatSetting("Скорость", 1.0F, 0.5F, 2.0F, 0.05F);
    private final ModeSetting anim = new ModeSetting("Анимация", ANIM_IN_PLACE,
            ANIM_IN_PLACE, ANIM_OLD, ANIM_BLOCK, ANIM_SWIPE, ANIM_CHOP, ANIM_SMOOTH,
            ANIM_PUSH, ANIM_TREMBLE, ANIM_SELF, ANIM_GLIDE, ANIM_VERTICAL,
            ANIM_HORIZONTAL, ANIM_REALISM, ANIM_STATIC);
    private final BooleanSetting onlyAura = new BooleanSetting("Только при наводке", false);
    private final BooleanSetting outline = new BooleanSetting("Обводка силуэта", true);
    private final FloatSetting outlineSize = new FloatSetting("Размер обводки", 2.0F, 0.5F, 6.0F, 0.5F);

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
    private long lastFrameNanos;

    // Снимок настроек: их меняют и островок, и панель настроек, и меню анимаций, поэтому
    // сравниваем раз в кадр без аллокаций и помечаем конфиг грязным только на реальном изменении.
    private boolean settingsSnapshotted;
    private float snapStrength;
    private float snapSpeed;
    private float snapOutlineSize;
    private boolean snapOnlyAura;
    private boolean snapOutline;
    private int snapAnim;

    private boolean holding;
    private InteractionHand grabbed;
    private float grabCursorX;
    private float grabCursorY;
    private float grabOffsetX;
    private float grabOffsetY;

    private InteractionHand outlineHand;
    private InteractionHand pulseHand;
    private HumanoidArm lastMainArm;

    private boolean animMenuOpen;
    private float animMenuX;
    private float animMenuY;

    public ViewModel() {
        super("ViewModel", "Положение, масштаб и анимации рук от первого лица", ModuleCategory.RENDER);
        instance = this;
        addSettings(strength, speed, anim, onlyAura, outline, outlineSize);
        load();
        registerEvents();
    }

    public static ViewModel get() {
        return instance;
    }

    public FloatSetting strengthSetting() {
        return strength;
    }

    public FloatSetting speedSetting() {
        return speed;
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;
        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
            ViewModel module = instance;
            if (module != null) {
                module.onHudRender(guiGraphics);
            }
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        load();
        lastFrameNanos = System.nanoTime();
    }

    @Override
    public void onDisable() {
        holding = false;
        grabbed = null;
        outlineHand = null;
        pulseHand = null;
        animMenuOpen = false;
        HandOutline.setWanted(false);
        HandViewport.setWanted(false);
        super.onDisable();
        // [!] Выключение — последняя точка, где конфиг ещё можно дописать синхронно
        // (дальше клиент может быть закрыт): сбрасываем накопившийся дебаунс.
        if (writePending) {
            saveNow();
        }
    }

    // Единое условие для обоих хелперов рук: раньше beginFrame() проверял игрока/мир/вид
    // от первого лица, а onHudRender() — только вкладку Hands, из-за чего флаг wanted
    // флапал каждый кадр и HandOutline.release() уничтожал FBO по кругу.
    private boolean handsVisible(Minecraft minecraft) {
        return isEnable()
                && minecraft.player != null
                && minecraft.level != null
                && minecraft.options.getCameraType().isFirstPerson();
    }

    public static void beginFrame() {
        ViewModel module = instance;
        Minecraft minecraft = Minecraft.getInstance();
        boolean visible = module != null && module.handsVisible(minecraft);
        HandViewport.setWanted(visible);
        HandOutline.setWanted(visible && module.editorContext());
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
            HandViewport.captureBase(hand, poseStack, anchorX, ARM_ANCHOR_Y, ARM_Z);
        }

        float offsetX = main ? module.mainOffsetX : module.offOffsetX;
        float offsetY = main ? module.mainOffsetY : module.offOffsetY;
        if (offsetX != 0.0F || offsetY != 0.0F) {
            poseStack.translate(offsetX, offsetY, 0.0F);
        }

        float scaleValue = main ? module.mainScaleAnim.getValue() : module.offScaleAnim.getValue();
        if (module.holding && module.grabbed == hand) {
            scaleValue *= 1.0F + GRAB_PULSE * module.pulse.getValue();
        }
        if (scaleValue != 1.0F) {
            // [!] Масштаб вокруг якоря руки, а не вокруг начала координат камеры.
            // Для перспективной проекции scale() в центре камеры — no-op по картинке:
            // NDC не меняется (clip.xyz и clip.w масштабируются одинаково), рука лишь
            // уезжает вдоль своего луча по глубине. Поэтому настройка размера не делала
            // ничего, а уменьшение уводило руку за ближнюю плоскость отсечения — руки
            // пропадали совсем. С масштабом вокруг якоря вершина руки получает
            // s*v + anchor + offset: размер меняется, место и глубина руки остаются прежними.
            poseStack.translate(anchorX, ARM_ANCHOR_Y, ARM_Z);
            poseStack.scale(scaleValue, scaleValue, scaleValue);
            poseStack.translate(-anchorX, -ARM_ANCHOR_Y, -ARM_Z);
        }

        if (HandViewport.isWanted()) {
            HandViewport.capture(hand, poseStack, anchorX, ARM_ANCHOR_Y, ARM_Z);
        }

        if (main && module.lastMainArm != player.getMainArm()) {
            module.handleMainHandSwap(player.getMainArm());
        }
    }

    public static boolean applySwing(PoseStack poseStack, HumanoidArm arm, float swingProgress) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || poseStack == null || arm == null) {
            return false;
        }
        module.load();

        // Поворот пресета вокруг якоря руки — штатное размещение предмета, откатано к
        // исходному виду: без этой пары translate предмет в руке уходит с привычного места.
        float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        poseStack.translate(side * ARM_X, ARM_Y, ARM_Z);

        float progress = Mth.clamp(swingProgress * module.speed.get(), 0.0F, 1.0F);
        if (progress > 0.0F) {
            module.renderPreset(poseStack, progress, arm);
        }

        poseStack.translate(-side * ARM_X, -ARM_Y, -ARM_Z);
        return true;
    }

    public static boolean cancelArmDip(PoseStack poseStack, HumanoidArm arm, float swingProgress) {
        ViewModel module = instance;
        if (module == null || !module.isEnable() || poseStack == null || arm == null || swingProgress <= 0.0F) {
            return false;
        }

        float side = arm != HumanoidArm.LEFT ? 1.0F : -1.0F;
        float sqrt = Mth.sqrt(swingProgress);
        poseStack.translate(
                -side * ARM_SWING_X * Mth.sin(sqrt * (float) Math.PI),
                -ARM_SWING_Y * Mth.sin(sqrt * ((float) Math.PI * 2F)),
                -ARM_SWING_Z * Mth.sin(swingProgress * (float) Math.PI)
        );
        return true;
    }

    public static boolean isStatic() {
        ViewModel module = instance;
        return module != null && module.isEnable() && module.anim.is(ANIM_STATIC);
    }

    public static boolean staticSwing() {
        return isStatic();
    }

    // [!] Финал экипировки — это 1.0, а не 0.0: 0 — старт анимации смены предмета.
    // С нулём рука (и предмет в ней) постоянно стояла на 0.6 выше ванильного места —
    // -0.52 вместо -0.52 + 1*(-0.6) = -1.12, из-за чего руки казались крупнее и вылезали
    // из своего угла. Единица так же гасит анимацию смены предмета, но ставит руку на место.
    public static float equipProgress(float equippedProgress) {
        ViewModel module = instance;
        return module != null && module.isEnable() ? 1.0F : equippedProgress;
    }

    // [+] Текущий сдвиг руки для HandAnimations: якорь поворотов должен совпадать с реальным местом руки.
    public static float offsetX(InteractionHand hand) {
        ViewModel module = instance;
        if (module == null || !module.isEnable()) {
            return 0.0F;
        }
        return hand == InteractionHand.MAIN_HAND ? module.mainOffsetX : module.offOffsetX;
    }

    public static float offsetY(InteractionHand hand) {
        ViewModel module = instance;
        if (module == null || !module.isEnable()) {
            return 0.0F;
        }
        return hand == InteractionHand.MAIN_HAND ? module.mainOffsetY : module.offOffsetY;
    }

    private void renderPreset(PoseStack poseStack, float swingProgress, HumanoidArm arm) {
        Minecraft minecraft = Minecraft.getInstance();
        if (onlyAura.get() && (minecraft.crosshairPickEntity == null || !isLiving(minecraft.crosshairPickEntity))) {
            return;
        }

        float strength = this.strength.get();
        float side = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float sqrt = Mth.sqrt(swingProgress);
        float sinPi = Mth.sin(swingProgress * (float) Math.PI);
        float sinPiSqrt = Mth.sin(sqrt * (float) Math.PI);

        switch (anim.getCurrent()) {
            case ANIM_OLD -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (45.0F + sinPiSqrt * -20.0F) * strength));
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * (sinPi * -20.0F) * strength));
                poseStack.mulPose(Axis.XP.rotationDegrees((sinPi * -80.0F) * strength));
                poseStack.mulPose(Axis.YP.rotationDegrees(side * -45.0F * strength));
            }
            case ANIM_BLOCK -> {
                float f = Mth.sin(swingProgress * swingProgress * (float) Math.PI);
                poseStack.mulPose(Axis.XP.rotationDegrees(side * (f * -20.0F) * strength));
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * (sinPiSqrt * -20.0F) * strength));
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (sinPiSqrt * -40.0F) * strength));
            }
            case ANIM_SWIPE -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(side * (sinPi * -20.0F) * strength));
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * (sinPiSqrt * -40.0F) * strength));
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (sinPiSqrt * -60.0F) * strength));
            }
            case ANIM_CHOP -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (45.0F + sinPi * -20.0F) * strength));
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * (sinPi * -40.0F) * strength));
                poseStack.mulPose(Axis.XP.rotationDegrees((sinPiSqrt * -60.0F) * strength));
                poseStack.mulPose(Axis.YP.rotationDegrees(side * -45.0F * strength));
            }
            case ANIM_SMOOTH -> {
                float ease = (float) easeInOutBack(swingProgress);
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (ease * 40.0F) * strength));
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * (sinPi * -30.0F) * strength));
                poseStack.mulPose(Axis.XP.rotationDegrees((ease * -60.0F) * strength));
            }
            case ANIM_PUSH -> {
                poseStack.translate(0.0F, 0.0F, (-sinPi * 0.25F) * strength);
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (sinPi * 20.0F) * strength));
                poseStack.mulPose(Axis.XP.rotationDegrees((sinPi * -30.0F) * strength));
            }
            case ANIM_TREMBLE -> {
                float tremble = Mth.sin(swingProgress * (float) Math.PI * 6.0F) * 0.08F * (1.0F - swingProgress);
                poseStack.translate(side * tremble * strength, tremble * strength, 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (sinPiSqrt * -25.0F) * strength));
                poseStack.mulPose(Axis.XP.rotationDegrees((sinPi * -40.0F) * strength));
            }
            case ANIM_SELF -> {
                poseStack.translate(0.0F, 0.0F, (sinPi * 0.2F) * strength);
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (sinPiSqrt * 35.0F) * strength));
                poseStack.mulPose(Axis.XP.rotationDegrees((sinPi * -45.0F) * strength));
            }
            case ANIM_GLIDE -> {
                float glide = (float) Math.sin(swingProgress * Math.PI * 0.5D);
                poseStack.translate(side * glide * 0.15F * strength, -glide * 0.1F * strength, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * (glide * -20.0F) * strength));
                poseStack.mulPose(Axis.XP.rotationDegrees((sinPi * -35.0F) * strength));
            }
            case ANIM_VERTICAL -> {
                poseStack.translate(0.0F, (-sinPi * 0.25F) * strength, 0.0F);
                poseStack.mulPose(Axis.XP.rotationDegrees((sinPiSqrt * -70.0F) * strength));
            }
            case ANIM_HORIZONTAL -> {
                poseStack.translate(side * (sinPi * 0.25F) * strength, 0.0F, 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (sinPiSqrt * -80.0F) * strength));
            }
            case ANIM_REALISM -> {
                poseStack.translate(side * (sinPi * 0.08F) * strength, -sinPi * 0.05F * strength, -sinPi * 0.12F * strength);
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (sinPiSqrt * -18.0F) * strength));
                poseStack.mulPose(Axis.ZP.rotationDegrees(side * (sinPi * -12.0F) * strength));
                poseStack.mulPose(Axis.XP.rotationDegrees((sinPi * -42.0F) * strength));
            }
            case ANIM_STATIC -> {
            }
            default -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(side * (sinPiSqrt * 45.0F) * strength));
                poseStack.mulPose(Axis.XP.rotationDegrees((sinPiSqrt * -45.0F) * strength));
            }
        }
    }

    private static boolean isLiving(Object entity) {
        return entity instanceof LivingEntity;
    }

    private static double easeInOutBack(double x) {
        // Умножение вместо Math.pow(v, 2): метод в горячем пути (каждый кадр взмаха).
        double doubled = 2.0D * x;
        return x < 0.5D
                ? (doubled * doubled * ((C3 + 1.0D) * doubled - C3)) / 2.0D
                : ((doubled - 2.0D) * (doubled - 2.0D) * ((C3 + 1.0D) * (doubled - 2.0D) + C3) + 2.0D) / 2.0D;
    }

    public boolean isHandHovered(float mouseX, float mouseY) {
        if (HandOutline.isMaskReady()) {
            return HandOutline.isHandCoveredAt(mouseX, mouseY);
        }
        if (HandViewport.isReady(InteractionHand.MAIN_HAND)) {
            float ax = HandViewport.anchorX(InteractionHand.MAIN_HAND);
            float ay = HandViewport.anchorY(InteractionHand.MAIN_HAND);
            if (distance(mouseX, mouseY, ax, ay) <= 40.0F * 40.0F) {
                return true;
            }
        }
        if (HandViewport.isReady(InteractionHand.OFF_HAND)) {
            float ax = HandViewport.anchorX(InteractionHand.OFF_HAND);
            float ay = HandViewport.anchorY(InteractionHand.OFF_HAND);
            if (distance(mouseX, mouseY, ax, ay) <= 40.0F * 40.0F) {
                return true;
            }
        }
        return false;
    }

    public boolean onScroll(float mouseX, float mouseY, double vertical) {
        if (!isEnable() || vertical == 0.0) {
            return false;
        }
        InteractionHand hand = holding && grabbed != null ? grabbed : null;
        if (hand == null && isHandHovered(mouseX, mouseY)) {
            hand = pickHand(mouseX, mouseY);
        }
        if (hand == null) {
            return false;
        }

        float step = vertical > 0.0 ? SCALE_STEP : -SCALE_STEP;
        if (hand == InteractionHand.MAIN_HAND) {
            mainScaleTarget = Mth.clamp(mainScaleTarget + step, SCALE_MIN, SCALE_MAX);
            mainScaleAnim.setSpeed(SCALE_ANIM_SPEED);
            mainScaleAnim.update(mainScaleTarget);
        } else {
            offScaleTarget = Mth.clamp(offScaleTarget + step, SCALE_MIN, SCALE_MAX);
            offScaleAnim.setSpeed(SCALE_ANIM_SPEED);
            offScaleAnim.update(offScaleTarget);
        }
        markDirty();
        return true;
    }

    private void grab(InteractionHand hand, float mouseX, float mouseY) {
        grabbed = hand;
        outlineHand = hand;
        holding = true;
        grabCursorX = mouseX;
        grabCursorY = mouseY;
        boolean main = hand == InteractionHand.MAIN_HAND;
        grabOffsetX = main ? mainTargetX : offTargetX;
        grabOffsetY = main ? mainTargetY : offTargetY;

        pulseHand = hand;
        pulse.setSpeed(PULSE_IN_SPEED);
        pulse.update(1.0F);

        outlineAnim.setSpeed(OUTLINE_IN_SPEED);
        outlineAnim.update(1.0F);
        HandOutline.setOutline(outline.get() ? outlineAnim.getValue() : 0.0F, outlineSize.get());
    }

    public void drag(float mouseX, float mouseY) {
        if (!holding || grabbed == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        int guiHeight = minecraft.getWindow().getGuiScaledHeight();
        if (guiHeight <= 0) {
            return;
        }

        // [!] Смещение кладётся в poseStack ДО scale, поэтому делить дельту курсора нужно на
        // «базовый» ppu (снятый до нашего масштаба). Деление на отмасштабированный ppu
        // рассинхронизировало руку и курсор: при масштабе 2 рука шла вдвое медленнее мыши.
        float ppuX = HandViewport.basePixelsPerUnitX(grabbed);
        float ppuY = HandViewport.basePixelsPerUnitY(grabbed);
        if (ppuX <= 1.0F) {
            ppuX = HandViewport.pixelsPerUnitX(grabbed);
        }
        if (ppuY <= 1.0F) {
            ppuY = HandViewport.pixelsPerUnitY(grabbed);
        }
        if (ppuX <= 1.0F) {
            ppuX = (float) guiHeight * 0.95F;
        }
        if (ppuY <= 1.0F) {
            ppuY = (float) guiHeight * 0.95F;
        }

        float deltaX = (mouseX - grabCursorX) / ppuX;
        float deltaY = -(mouseY - grabCursorY) / ppuY;

        setTarget(grabbed, grabOffsetX + deltaX, grabOffsetY + deltaY);
    }

    public void editorDrag(float mouseX, float mouseY) {
        drag(mouseX, mouseY);
    }

    public void editorRelease() {
        release(true);
    }

    public boolean isHolding() {
        return holding;
    }

    public InteractionHand pickHand(float mouseX, float mouseY) {
        boolean mainReady = HandViewport.isReady(InteractionHand.MAIN_HAND);
        boolean offReady = HandViewport.isReady(InteractionHand.OFF_HAND);
        if (mainReady && offReady) {
            float distMain = distance(mouseX, mouseY,
                    HandViewport.anchorX(InteractionHand.MAIN_HAND),
                    HandViewport.anchorY(InteractionHand.MAIN_HAND));
            float distOff = distance(mouseX, mouseY,
                    HandViewport.anchorX(InteractionHand.OFF_HAND),
                    HandViewport.anchorY(InteractionHand.OFF_HAND));
            return distMain <= distOff ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
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
        boolean isRightMain = arm == HumanoidArm.RIGHT;
        return rightHalf == isRightMain ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
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

    private void reset(InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            mainTargetX = 0.0F;
            mainTargetY = 0.0F;
            mainOffsetX = 0.0F;
            mainOffsetY = 0.0F;
            mainScaleTarget = 1.0F;
            mainScaleAnim.setSpeed(SCALE_ANIM_SPEED);
            mainScaleAnim.update(mainScaleTarget);
        } else {
            offTargetX = 0.0F;
            offTargetY = 0.0F;
            offOffsetX = 0.0F;
            offOffsetY = 0.0F;
            offScaleTarget = 1.0F;
            offScaleAnim.setSpeed(SCALE_ANIM_SPEED);
            offScaleAnim.update(offScaleTarget);
        }
        markDirty();
    }

    private void resetAll() {
        holding = false;
        grabbed = null;
        mainTargetX = 0.0F;
        mainTargetY = 0.0F;
        offTargetX = 0.0F;
        offTargetY = 0.0F;
        mainOffsetX = 0.0F;
        mainOffsetY = 0.0F;
        offOffsetX = 0.0F;
        offOffsetY = 0.0F;
        mainScaleTarget = 1.0F;
        offScaleTarget = 1.0F;
        mainScaleAnim.setSpeed(SCALE_ANIM_SPEED);
        offScaleAnim.setSpeed(SCALE_ANIM_SPEED);
        mainScaleAnim.update(mainScaleTarget);
        offScaleAnim.update(offScaleTarget);
        markDirty();
    }

    private void release(boolean keepOutlineHand) {
        holding = false;
        grabbed = null;
        pulse.setSpeed(PULSE_OUT_SPEED);
        pulse.update(0.0F);
        outlineAnim.setSpeed(OUTLINE_OUT_SPEED);
        outlineAnim.update(0.0F);
        if (!keepOutlineHand) {
            outlineHand = null;
        }
        markDirty();
    }

    // [!] Вызывается из applyTransform, то есть из отрисовки рук: файловых вызовов здесь
    // быть не должно (только markDirty), а руку берём из аргумента, а не повторным
    // обращением к Minecraft.getInstance().player.
    private void handleMainHandSwap(HumanoidArm arm) {
        if (lastMainArm == null || lastMainArm == arm) {
            lastMainArm = arm;
            return;
        }
        lastMainArm = arm;

        float tmpX = mainTargetX;
        float tmpY = mainTargetY;
        mainTargetX = offTargetX;
        mainTargetY = offTargetY;
        offTargetX = tmpX;
        offTargetY = tmpY;

        float tmpScale = mainScaleTarget;
        mainScaleTarget = offScaleTarget;
        offScaleTarget = tmpScale;

        mainScaleAnim.update(mainScaleTarget);
        offScaleAnim.update(offScaleTarget);
        markDirty();
    }

    public boolean editorContext() {
        return Minecraft.getInstance().screen instanceof ClickGuiScreen
                && ((ClickGuiScreen) Minecraft.getInstance().screen).isHandsTab();
    }

    public boolean editorReset(int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_RIGHT || !isEnable()) {
            return false;
        }
        if (holding && grabbed != null) {
            reset(grabbed);
            release(false);
            return true;
        }
        resetAll();
        return true;
    }

    public boolean editorGrab(float mouseX, float mouseY) {
        if (!isEnable() && editorContext()) {
            setEnabled(true);
        }
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

    public boolean isAnimMenuOpen() {
        return animMenuOpen;
    }

    // [!] Выход из редактора: смена вкладки островка или закрытие GUI. Без этого захват
    // переживал контекст — рука оставалась с пульсом (масштаб ×1.06), оффсеты переставали
    // сглаживаться, а следующий вход в GUI двигал руку без клика и съедал релиз островка.
    public void cancelEditor() {
        animMenuOpen = false;
        if (holding) {
            release(true);
        }
    }

    public boolean editorMiddleClick(float mouseX, float mouseY) {
        if (!isEnable() && editorContext()) {
            setEnabled(true);
        }
        if (!isEnable() || Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return false;
        }
        if (!isHandHovered(mouseX, mouseY)) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        float screenW = (float) minecraft.getWindow().getGuiScaledWidth();
        float screenH = (float) minecraft.getWindow().getGuiScaledHeight();
        float menuW = 124.0F;
        float menuH = 22.0F + anim.getMods().size() * 15.0F + 6.0F;

        animMenuX = Mth.clamp(mouseX - 10.0F, 10.0F, Math.max(10.0F, screenW - menuW - 10.0F));
        animMenuY = Mth.clamp(mouseY - 10.0F, 10.0F, Math.max(10.0F, screenH - menuH - 10.0F));
        animMenuOpen = true;
        return true;
    }

    public boolean animMenuClick(float mouseX, float mouseY, int button) {
        if (!animMenuOpen) {
            return false;
        }
        List<String> modes = anim.getMods();
        float menuW = 124.0F;
        float itemH = 15.0F;
        float headerH = 22.0F;
        float menuH = headerH + modes.size() * itemH + 6.0F;

        if (mouseX >= animMenuX && mouseX <= animMenuX + menuW && mouseY >= animMenuY && mouseY <= animMenuY + menuH) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                float relY = mouseY - (animMenuY + headerH);
                if (relY >= 0.0F) {
                    int index = (int) (relY / itemH);
                    if (index >= 0 && index < modes.size()) {
                        anim.set(modes.get(index));
                        markDirty();
                        animMenuOpen = false;
                        return true;
                    }
                }
            }
            return true;
        }

        animMenuOpen = false;
        return true;
    }

    public void renderAnimMenu(PoseStack stack, float mouseX, float mouseY) {
        if (!animMenuOpen) {
            return;
        }
        List<String> modes = anim.getMods();
        float menuW = 124.0F;
        float itemH = 15.0F;
        float headerH = 22.0F;
        float menuH = headerH + modes.size() * itemH + 6.0F;
        float radius = 7.0F;

        GuiRender.drawGlass(stack, animMenuX, animMenuY, menuW, menuH, radius, true, 0.0F);
        GuiRender.roundedOutline(stack, animMenuX, animMenuY, menuW, menuH,
                0.5F, ColorUtils.getColor(255, 255, 255, 30), radius);

        GuiFonts.SF_BOLD.draw(stack, "Анимация атаки", animMenuX + 8.0F, animMenuY + 7.0F,
                ColorUtils.getColor(255, 255, 255, 220), 7.5F);

        float curY = animMenuY + headerH;
        int themeColor = ThemeManager.getColor();

        for (int i = 0; i < modes.size(); i++) {
            String mode = modes.get(i);
            boolean selected = anim.is(mode);
            boolean hovered = mouseX >= animMenuX + 4.0F && mouseX <= animMenuX + menuW - 4.0F
                    && mouseY >= curY && mouseY < curY + itemH;

            if (selected) {
                GuiRender.roundedRect(stack, animMenuX + 4.0F, curY + 1.0F, menuW - 8.0F, itemH - 2.0F, 3.5F,
                        ColorUtils.multAlpha(themeColor, 0.35F));
                GuiRender.roundedOutline(stack, animMenuX + 4.0F, curY + 1.0F, menuW - 8.0F, itemH - 2.0F,
                        0.5F, ColorUtils.multAlpha(themeColor, 0.8F), 3.5F);
            } else if (hovered) {
                GuiRender.roundedRect(stack, animMenuX + 4.0F, curY + 1.0F, menuW - 8.0F, itemH - 2.0F, 3.5F,
                        ColorUtils.getColor(255, 255, 255, 22));
            }

            int textColor = selected
                    ? ColorUtils.getColor(255, 255, 255, 255)
                    : (hovered ? ColorUtils.getColor(240, 240, 240, 230) : ColorUtils.getColor(170, 175, 185, 200));

            GuiFonts.SF_REGULAR.draw(stack, mode, animMenuX + 8.0F, curY + 3.5F, textColor, 7.0F);

            if (selected) {
                GuiRender.roundedRect(stack, animMenuX + menuW - 11.0F, curY + 5.0F, 4.0F, 4.0F, 2.0F,
                        ColorUtils.multAlpha(themeColor, 0.9F));
            }

            curY += itemH;
        }
    }

    private void onHudRender(GuiGraphics guiGraphics) {
        if (!isEnable()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        load();
        syncSettings();
        float delta = computeDeltaSeconds();

        // [!] Условие то же, что в beginFrame(): иначе вкладка Hands в третьем лице держала
        // HandOutline в wanted=true и флапала release() каждый кадр.
        boolean inHandsTab = editorContext() && handsVisible(minecraft);

        // [!] Захват умирает вместе с контекстом редактора (страховка к cancelEditor()).
        if (holding && !inHandsTab) {
            release(true);
        }

        if (holding) {
            mainOffsetX = mainTargetX;
            mainOffsetY = mainTargetY;
            offOffsetX = offTargetX;
            offOffsetY = offTargetY;
        } else {
            smooth(delta);
        }

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
        if (outlineAnim.getTarget() != (dragging ? 1.0F : 0.0F)) {
            outlineAnim.setSpeed(dragging ? OUTLINE_IN_SPEED : OUTLINE_OUT_SPEED);
        }
        outlineAnim.update(dragging ? 1.0F : 0.0F);

        float alpha = outlineAnim.getValue();
        if (!dragging && alpha <= 0.001F) {
            outlineHand = null;
        }

        HandOutline.setWanted(inHandsTab);
        HandOutline.setOutline(alpha, outlineSize.get());
        HandOutline.composite();

        if (!HandOutline.isShaderReady() && outlineHand != null) {
            drawRectOutline(guiGraphics.pose());
        }

        if (inHandsTab && outline.get() && dragging && !HandOutline.isMaskReady()) {
            HandOutline.reportStale();
        }
        if (inHandsTab && !HandViewport.hasAny()) {
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
        float diff = target - current;
        if (Math.abs(diff) < 0.0001F) {
            return target;
        }
        return current + diff * factor;
    }

    private float computeDeltaSeconds() {
        long now = System.nanoTime();
        if (lastFrameNanos == 0L) {
            lastFrameNanos = now;
            return 0.016F;
        }
        float delta = (float) (now - lastFrameNanos) / 1_000_000_000.0F;
        lastFrameNanos = now;
        return Mth.clamp(delta, 0.0F, MAX_FRAME_DELTA);
    }

    private void drawRectOutline(PoseStack stack) {
        InteractionHand hand = outlineHand;
        if (hand == null || !HandViewport.isReady(hand)) {
            return;
        }
        float alpha = outlineAnim.getValue();
        if (alpha <= 0.01F) {
            return;
        }

        float ax = HandViewport.anchorX(hand);
        float ay = HandViewport.anchorY(hand);
        float radius = Math.max(30.0F, HandViewport.pixelsPerUnitY(hand) * 0.35F);
        float x = ax - radius;
        float y = ay - radius;
        float size = radius * 2.0F;

        int theme = ThemeManager.getColor();
        GuiRender.roundedRect(stack, x, y, size, size,
                radius, ColorUtils.multAlpha(theme, alpha * OUTLINE_RECT_FILL));
        GuiRender.roundedOutline(stack, x, y, size, size,
                OUTLINE_RECT_THICKNESS, ColorUtils.multAlpha(theme, alpha), radius);
    }

    private void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        if (Files.exists(FILE)) {
            try {
                String json = Files.readString(FILE, StandardCharsets.UTF_8);
                JsonElement parsed = JsonParser.parseString(json);
                if (parsed.isJsonObject()) {
                    applyConfig(parsed.getAsJsonObject());
                }
            } catch (Throwable t) {
                Swerve.LOGGER.error("Failed to load ViewModel config", t);
            }
        }

        syncSettings();
    }

    private void applyConfig(JsonObject object) {
        mainTargetX = readFloat(object, "mainX", 0.0F);
        mainTargetY = readFloat(object, "mainY", 0.0F);
        offTargetX = readFloat(object, "offX", 0.0F);
        offTargetY = readFloat(object, "offY", 0.0F);

        mainOffsetX = mainTargetX;
        mainOffsetY = mainTargetY;
        offOffsetX = offTargetX;
        offOffsetY = offTargetY;

        mainScaleTarget = Mth.clamp(readFloat(object, "mainScale", 1.0F), SCALE_MIN, SCALE_MAX);
        offScaleTarget = Mth.clamp(readFloat(object, "offScale", 1.0F), SCALE_MIN, SCALE_MAX);
        // [!] setValue, а не update: update() запускал анимацию от 1.0 к сохранённому
        // значению, и руки на глазах росли при каждом включении модуля и входе в мир.
        mainScaleAnim.setValue(mainScaleTarget);
        offScaleAnim.setValue(offScaleTarget);

        // [!] Настройки раньше не сохранялись вовсе, хотя animMenuClick звал save():
        // выбранная в Hands анимация и слайдеры островка сбрасывались после перезапуска.
        // ModeSetting.set() сам игнорирует неизвестное значение — старый конфиг не ломается.
        strength.setValue(readFloat(object, "strength", strength.get()));
        speed.setValue(readFloat(object, "speed", speed.get()));
        outlineSize.setValue(readFloat(object, "outlineSize", outlineSize.get()));
        onlyAura.setState(readBoolean(object, "onlyAura", onlyAura.get()));
        outline.setState(readBoolean(object, "outline", outline.get()));
        anim.set(readString(object, "anim", anim.getCurrent()));
    }

    // Снимок настроек без аллокаций: 6 сравнений раз в кадр, markDirty только на изменении.
    private void syncSettings() {
        float strengthNow = strength.get();
        float speedNow = speed.get();
        float outlineSizeNow = outlineSize.get();
        boolean onlyAuraNow = onlyAura.get();
        boolean outlineNow = outline.get();
        int animNow = anim.getIndex();

        boolean changed = snapStrength != strengthNow
                || snapSpeed != speedNow
                || snapOutlineSize != outlineSizeNow
                || snapOnlyAura != onlyAuraNow
                || snapOutline != outlineNow
                || snapAnim != animNow;

        snapStrength = strengthNow;
        snapSpeed = speedNow;
        snapOutlineSize = outlineSizeNow;
        snapOnlyAura = onlyAuraNow;
        snapOutline = outlineNow;
        snapAnim = animNow;

        if (changed && settingsSnapshotted) {
            markDirty();
        }
        settingsSnapshotted = true;
    }

    private void markDirty() {
        pendingJson = buildConfig();
        synchronized (WRITE_LOCK) {
            writePending = true;
            if (configWriter == null) {
                configWriter = createWriter();
                configWriter.start();
            }
            WRITE_LOCK.notifyAll();
        }
    }

    // JSON собирается на игровом потоке (значения несутся сюда целиком, гонки нет),
    // а файловые вызовы делает демон.
    private String buildConfig() {
        JsonObject object = new JsonObject();
        object.addProperty("mainX", mainTargetX);
        object.addProperty("mainY", mainTargetY);
        object.addProperty("offX", offTargetX);
        object.addProperty("offY", offTargetY);
        object.addProperty("mainScale", mainScaleTarget);
        object.addProperty("offScale", offScaleTarget);
        object.addProperty("strength", strength.get());
        object.addProperty("speed", speed.get());
        object.addProperty("outlineSize", outlineSize.get());
        object.addProperty("onlyAura", onlyAura.get());
        object.addProperty("outline", outline.get());
        object.addProperty("anim", anim.getCurrent());
        return GSON.toJson(object);
    }

    private static Thread createWriter() {
        Thread thread = new Thread(ViewModel::writeLoop, "Swerve-ViewModel-Config");
        thread.setDaemon(true);
        return thread;
    }

    private static void writeLoop() {
        while (true) {
            String json;
            synchronized (WRITE_LOCK) {
                while (!writePending) {
                    try {
                        WRITE_LOCK.wait();
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                try {
                    WRITE_LOCK.wait(WRITE_DEBOUNCE_MS);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    return;
                }
                json = pendingJson;
                pendingJson = null;
                writePending = false;
            }

            if (json != null) {
                writeConfig(json);
            }
        }
    }

    // Синхронный сброс накопленного: только на выключении модуля.
    private void saveNow() {
        synchronized (WRITE_LOCK) {
            pendingJson = null;
            writePending = false;
            WRITE_LOCK.notifyAll();
        }
        writeConfig(buildConfig());
    }

    private static void writeConfig(String json) {
        try {
            Path parent = FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // Запись во временный файл и move: краш/выход посреди записи не рвёт конфиг.
            Path temp = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            Files.writeString(temp, json, StandardCharsets.UTF_8);
            try {
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (Throwable atomicUnsupported) {
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Throwable t) {
            Swerve.LOGGER.error("Failed to save ViewModel config", t);
        }
    }

    private static float readFloat(JsonObject object, String member, float fallback) {
        if (object == null || !object.has(member)) {
            return fallback;
        }
        try {
            return object.get(member).getAsFloat();
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static boolean readBoolean(JsonObject object, String member, boolean fallback) {
        if (object == null || !object.has(member)) {
            return fallback;
        }
        try {
            return object.get(member).getAsBoolean();
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static String readString(JsonObject object, String member, String fallback) {
        if (object == null || !object.has(member)) {
            return fallback;
        }
        try {
            return object.get(member).getAsString();
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return dx * dx + dy * dy;
    }
}
