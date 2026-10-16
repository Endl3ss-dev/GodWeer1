package swerve.modules.impl.render;

import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.ui.menu.MenuScreen;
import swerve.utils.animation.Easing;
import swerve.utils.animation.Easings;
import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

/**
 * Экраны (инвентарь, контейнеры, пауза) рисуются не поверх мира, а как объёмная панель в мире:
 * камера отъезжает от игрока (или остаётся от первого лица), а сам экран снимается в текстуру.
 * Вся логика — в swerve.utils.render.spatial.*, модуль хранит настройки и правила выбора экранов.
 */
public final class SpatialGui extends Module {

    public static final String CAMERA_THIRD = "Третье лицо";
    public static final String CAMERA_FIRST = "Первое лицо";
    public static final String CAMERA_GAME = "Как в игре";

    private static final String EASE_CUBIC = "Кубическая";
    private static final String EASE_ELASTIC = "Эластичная";
    private static final String EASE_BOUNCE = "Отскок";
    private static final String EASE_BACK = "Отдача";
    private static final String EASE_EXPO = "Экспонента";
    private static final String EASE_QUAD = "Квадратичная";
    private static final String EASE_QUART = "Квартичная";
    private static final Easing[] EASINGS = {
            Easings.CUBIC_OUT, Easings.ELASTIC_OUT, Easings.BOUNCE_OUT, Easings.BACK_OUT,
            Easings.EXPO_OUT, Easings.QUAD_OUT, Easings.QUART_OUT
    };

    // Значения, не вынесенные в ClickGui: равны умолчаниям оригинального поведения.
    public static final float SCREEN_SIDE_OFFSET = -0.6F;
    public static final float SCREEN_HEIGHT_OFFSET = -0.4F;
    public static final float SCREEN_YAW_OFFSET = 160.0F;
    public static final float SCREEN_PITCH_OFFSET = 0.0F;
    public static final float FP_SCREEN_SIDE_OFFSET = 0.0F;
    public static final float FP_SCREEN_HEIGHT_OFFSET = 0.0F;
    public static final float FP_SCREEN_YAW_OFFSET = 180.0F;
    public static final float FP_SCREEN_PITCH_OFFSET = 0.0F;
    public static final float CAMERA_TARGET_PITCH = 10.0F;
    public static final boolean LERP_CAMERA_PITCH = true;
    public static final int TRANSITION_MS = 300;
    public static final float TRANSITION_SKIP = 0.0F;
    public static final float TP_PARALLAX_YAW = 0.1F;
    public static final float TP_PARALLAX_PITCH = 0.03F;
    public static final float FP_PARALLAX_YAW = 0.4F;
    public static final float FP_PARALLAX_PITCH = 0.12F;
    public static final boolean TP_PARALLAX = true;
    public static final boolean FP_PARALLAX = true;
    public static final float FP_PITCH_CLAMP = 40.0F;
    public static final boolean LOCK_FP_YAW = false;
    public static final boolean INHERIT_SCREEN_ORIGIN = true;
    public static final int FADE_MS = 150;
    public static final int OPEN_MS = 300;
    public static final float OPEN_START_SCALE = 0.75F;
    public static final float RECIPE_BOOK_SHRINK = 1.5F;
    public static final int RENDER_SCALE_PERCENT = 100;
    public static final int SCREEN_ALPHA = 255;
    public static final boolean LINEAR_FILTER = true;
    public static final boolean HIDE_SHIELD = true;
    public static final boolean SWING_ON_CLICK = false;
    public static final float FOV_LERP_SPEED = 10.0F;
    public static final boolean AUTO_SCALE_BY_FOV = true;
    public static final float AUTO_SCALE_BASE_FOV = 70.0F;
    public static final float AUTO_SCALE_SCREEN_POWER = 1.2F;
    public static final float AUTO_SCALE_SIDE_MULT = -0.3F;
    public static final float AUTO_SCALE_DISTANCE_MULT = 0.8F;
    public static final float AUTO_SCALE_TP_SCREEN_MULT = 0.82F;
    public static final float AVATAR_BASE_YAW = 30.0F;
    public static final float AVATAR_BODY_OFFSET = 20.0F;
    public static final float AVATAR_MAX_YAW = 40.0F;
    public static final float AVATAR_MAX_PITCH = 25.0F;
    public static final boolean MIRROR_HEAD = false;

    private static SpatialGui instance;

    private final BooleanSetting inventory = new BooleanSetting("Инвентарь", true);
    private final BooleanSetting containers = new BooleanSetting("Контейнеры", true);
    private final BooleanSetting pauseScreen = new BooleanSetting("Меню паузы", false);
    private final BooleanSetting allScreens = new BooleanSetting("Все экраны", false);

    private final ModeSetting cameraMode = new ModeSetting("Камера", CAMERA_THIRD, CAMERA_THIRD, CAMERA_FIRST, CAMERA_GAME);
    private final BooleanSetting fallbackFirstPerson = new BooleanSetting("Первое лицо у стены", false).visible(() -> !cameraMode.is(CAMERA_FIRST));
    private final FloatSetting cameraDistance = new FloatSetting("Дистанция камеры", 1.7F, 0.5F, 4.0F, 0.1F).visible(() -> !cameraMode.is(CAMERA_FIRST));
    private final FloatSetting cameraSideOffset = new FloatSetting("Смещение камеры", -1.2F, -4.0F, 4.0F, 0.1F).visible(() -> !cameraMode.is(CAMERA_FIRST));
    private final FloatSetting cameraHeightOffset = new FloatSetting("Высота камеры", 1.5F, -4.0F, 4.0F, 0.1F).visible(() -> !cameraMode.is(CAMERA_FIRST));
    private final BooleanSetting mirrorThirdPerson = new BooleanSetting("Зеркально", false).visible(() -> !cameraMode.is(CAMERA_FIRST));
    private final BooleanSetting crosshairMode = new BooleanSetting("Прицел вместо курсора", true).visible(() -> !cameraMode.is(CAMERA_THIRD));
    private final BooleanSetting keepCameraAngle = new BooleanSetting("Сохранять угол камеры", true).visible(() -> !cameraMode.is(CAMERA_THIRD));

    private final FloatSetting screenDistance = new FloatSetting("Дистанция экрана", 2.5F, 0.5F, 6.0F, 0.1F).visible(() -> !cameraMode.is(CAMERA_FIRST));
    private final FloatSetting screenScale = new FloatSetting("Размер экрана", 3.5F, 0.5F, 8.0F, 0.1F).visible(() -> !cameraMode.is(CAMERA_FIRST));
    private final FloatSetting fpScreenDistance = new FloatSetting("Дистанция экрана (1л)", 1.5F, 0.5F, 6.0F, 0.1F).visible(() -> !cameraMode.is(CAMERA_THIRD));
    private final FloatSetting fpScreenScale = new FloatSetting("Размер экрана (1л)", 1.8F, 0.5F, 8.0F, 0.1F).visible(() -> !cameraMode.is(CAMERA_THIRD));
    private final BooleanSetting curvedScreen = new BooleanSetting("Изогнутый экран", false).visible(() -> !cameraMode.is(CAMERA_THIRD));
    private final FloatSetting curvedArc = new FloatSetting("Изгиб, °", 30.0F, 5.0F, 150.0F, 5.0F).visible(() -> !cameraMode.is(CAMERA_THIRD) && curvedScreen.get());

    private final BooleanSetting openAnimation = new BooleanSetting("Анимация открытия", true);
    private final BooleanSetting closeAnimation = new BooleanSetting("Анимация закрытия", true);
    private final ModeSetting easing = new ModeSetting("Плавность", EASE_BACK, EASE_CUBIC, EASE_ELASTIC, EASE_BOUNCE, EASE_BACK, EASE_EXPO, EASE_QUAD, EASE_QUART).visible(openAnimation::get);

    private final BooleanSetting hideHud = new BooleanSetting("Скрыть HUD", false);
    private final BooleanSetting hideHotbar = new BooleanSetting("Скрыть хотбар", false);
    private final BooleanSetting hideHands = new BooleanSetting("Скрыть руки (1л)", false).visible(() -> !cameraMode.is(CAMERA_THIRD));
    private final BooleanSetting overrideFov = new BooleanSetting("Свой FOV", false);
    private final FloatSetting targetFov = new FloatSetting("FOV", 70.0F, 30.0F, 110.0F, 1.0F).visible(overrideFov::get);

    public SpatialGui() {
        super("SpatialGui", "Инвентарь и контейнеры как объёмная панель в мире", ModuleCategory.RENDER);
        addSettings(inventory, containers, pauseScreen, allScreens,
                cameraMode, fallbackFirstPerson, cameraDistance, cameraSideOffset, cameraHeightOffset, mirrorThirdPerson,
                crosshairMode, keepCameraAngle,
                screenDistance, screenScale, fpScreenDistance, fpScreenScale, curvedScreen, curvedArc,
                openAnimation, closeAnimation, easing,
                hideHud, hideHotbar, hideHands, overrideFov, targetFov);
        instance = this;
    }

    public static SpatialGui get() {
        return instance;
    }

    public static boolean active() {
        return instance != null && instance.isEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        SpatialState.shutdown();
    }

    // Правила выбора экрана. Собственный ClickGui и экраны без мира никогда не перехватываются.
    public boolean shouldHookScreen(Screen screen) {
        if (screen == null || !isEnable()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return false;
        }
        if (screen instanceof MenuScreen || screen instanceof TitleScreen || screen instanceof ChatScreen
                || screen instanceof ReceivingLevelScreen || screen instanceof LevelLoadingScreen) {
            return false;
        }
        if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            return inventory.get();
        }
        if (screen instanceof AbstractContainerScreen<?>) {
            return containers.get();
        }
        if (screen instanceof PauseScreen) {
            return pauseScreen.get();
        }
        return allScreens.get();
    }

    public boolean autoCamera() {
        return cameraMode.is(CAMERA_GAME);
    }

    public boolean useFirstPerson() {
        if (cameraMode.is(CAMERA_GAME)) {
            return Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
        }
        return cameraMode.is(CAMERA_FIRST) || SpatialState.firstPersonDueToBlock;
    }

    public boolean fallbackFirstPerson() {
        return fallbackFirstPerson.get();
    }

    public float cameraDistance() {
        return cameraDistance.get();
    }

    public float cameraSideOffset() {
        return cameraSideOffset.get();
    }

    public float cameraHeightOffset() {
        return cameraHeightOffset.get();
    }

    public boolean mirrorThirdPerson() {
        return mirrorThirdPerson.get();
    }

    public boolean crosshairMode() {
        return crosshairMode.get();
    }

    public boolean keepCameraAngle() {
        return keepCameraAngle.get();
    }

    public float screenDistance() {
        return screenDistance.get();
    }

    public float screenScale() {
        return screenScale.get();
    }

    public float fpScreenDistance() {
        return fpScreenDistance.get();
    }

    public float fpScreenScale() {
        return fpScreenScale.get();
    }

    public boolean curvedScreen() {
        return curvedScreen.get();
    }

    public float curvedArcDegrees() {
        return curvedArc.get();
    }

    public boolean openAnimation() {
        return openAnimation.get();
    }

    public boolean closeAnimation() {
        return closeAnimation.get();
    }

    public Easing easing() {
        int index = easing.getIndex();
        return index >= 0 && index < EASINGS.length ? EASINGS[index] : Easings.BACK_OUT;
    }

    public boolean hideHands() {
        return hideHands.get();
    }

    public boolean overrideFov() {
        return overrideFov.get();
    }

    public float targetFov() {
        return targetFov.get();
    }

    public static boolean shouldHideHud() {
        return active() && instance.hideHud.get() && SpatialState.hooked != null;
    }

    public static boolean shouldHideHotbar() {
        return active() && instance.hideHotbar.get() && SpatialState.hooked != null;
    }

    public static boolean shouldHideCrosshair() {
        return active() && SpatialState.effectiveFirstPerson && !instance.crosshairMode.get() && SpatialState.hooked != null;
    }
}
