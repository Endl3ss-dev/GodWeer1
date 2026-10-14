package swerve.utils.render.spatial;

import swerve.modules.impl.render.SpatialGui;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Состояние объёмного экрана на кадр: перехваченный Screen, режим камеры, флаги для миксинов.
 * Всё статическое и живёт только на потоке рендера.
 */
public final class SpatialState {

    public static Screen hooked;
    public static boolean effectiveFirstPerson;
    public static boolean firstPersonDueToBlock;
    public static boolean wasThirdPersonCamera;
    public static boolean wasClosing;
    public static float cameraYawOffset;
    public static double cameraX;
    public static double cameraY;
    public static double cameraZ;
    public static boolean hasCameraPos;
    /** true — Window отдаёт ванильный масштаб GUI (мир/HUD), false — масштаб панели (съёмка экрана, ввод). */
    public static boolean skipWindowOverride;
    /** Идёт отрисовка перехваченного экрана в текстуру. */
    public static boolean extracting;
    public static boolean headLockInitialized;

    private static boolean captureActive;
    private static boolean hasCameraStart;
    private static double cameraStartX;
    private static double cameraStartY;
    private static double cameraStartZ;
    private static float cameraStartYRot;

    private static float fovProgress;
    private static long lastFovNanos;

    private static boolean pendingSnap;
    private static float pendingSnapYaw;
    private static float pendingSnapPitch;

    private SpatialState() {
    }

    public static boolean active() {
        return SpatialGui.active();
    }

    public static boolean hookable(Screen screen) {
        SpatialGui module = SpatialGui.get();
        return module != null && module.shouldHookScreen(screen);
    }

    /** Перехваченный экран сейчас открыт и должен сниматься в текстуру. */
    public static boolean shouldCapture() {
        Minecraft mc = Minecraft.getInstance();
        Screen screen = hooked;
        if (screen == null || screen != mc.screen || !hookable(screen)) {
            captureActive = false;
            hasCameraStart = false;
            applyPendingSnap();
            return false;
        }
        if (!captureActive) {
            Camera camera = mc.gameRenderer.getMainCamera();
            Vec3 position = camera.getPosition();
            cameraStartX = position.x;
            cameraStartY = position.y;
            cameraStartZ = position.z;
            cameraStartYRot = camera.getYRot();
            hasCameraStart = true;
            captureActive = true;
        }
        return true;
    }

    public static boolean capturing() {
        return active() && shouldCapture();
    }

    public static boolean isCrosshairMode() {
        SpatialGui module = SpatialGui.get();
        return module != null && module.isEnable() && module.crosshairMode() && effectiveFirstPerson && hooked != null;
    }

    public static void hookScreen(Screen screen) {
        if (hooked == screen || !active()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (screen != mc.screen) {
            return;
        }
        if (hooked != null) {
            unhookScreen(false);
        }
        if (ScreenSurface.isClosing()) {
            finishClosing();
        }
        pendingSnap = false;
        skipWindowOverride = false;
        hooked = screen;
        ScreenSurface.onScreenOpened(System.currentTimeMillis());

        LocalPlayer player = mc.player;
        if (player != null && mc.level != null) {
            SpatialCamera.checkBlockCollision(player);
        }
        effectiveFirstPerson = SpatialGui.get().useFirstPerson();
        SpatialMouse.resetState();
        if (!effectiveFirstPerson) {
            SpatialTarget.prepare();
        }
    }

    public static void unhookScreen(boolean animate) {
        if (hooked == null) {
            return;
        }
        skipWindowOverride = false;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        SpatialGui module = SpatialGui.get();
        boolean wasFirstPerson = effectiveFirstPerson;
        SpatialMouse.releaseFromFirstPerson();
        boolean startClosing = animate && module != null && module.closeAnimation()
                && mc.level != null && ScreenSurface.beginClosing();
        if (!startClosing) {
            ScreenSurface.resetRecipeBook();
            ScreenSurface.resetPerspective();
        }
        hooked = null;
        headLockInitialized = false;
        firstPersonDueToBlock = false;

        if (wasFirstPerson && module != null && module.keepCameraAngle()) {
            Camera camera = mc.gameRenderer.getMainCamera();
            float yaw = camera.getYRot();
            float pitch = camera.getXRot();
            if (SpatialGui.INHERIT_SCREEN_ORIGIN) {
                pendingSnap = true;
                pendingSnapYaw = yaw;
                pendingSnapPitch = pitch;
            } else if (player != null) {
                applySnap(player, yaw, pitch);
            }
        } else {
            pendingSnap = false;
        }
    }

    // Угол, под которым игрок смотрел «сквозь» экран, становится его реальным поворотом.
    private static void applySnap(LocalPlayer player, float yaw, float pitch) {
        player.setYRot(yaw);
        player.setXRot(pitch);
        player.yRotO = yaw;
        player.xRotO = pitch;
        player.yBob = yaw;
        player.xBob = pitch;
        player.yBobO = yaw;
        player.xBobO = pitch;
    }

    private static void applyPendingSnap() {
        if (!pendingSnap) {
            return;
        }
        pendingSnap = false;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            applySnap(player, pendingSnapYaw, pendingSnapPitch);
        }
    }

    public static void finishClosing() {
        if (!ScreenSurface.isClosing()) {
            return;
        }
        ScreenSurface.endClosing();
        ScreenSurface.resetRecipeBook();
        ScreenSurface.resetPerspective();
        SpatialTarget.clear();
    }

    public static boolean isClosingThirdToFirstPerson() {
        SpatialGui module = SpatialGui.get();
        return ScreenSurface.isClosing() && !ScreenSurface.isClosingFirstPerson()
                && module != null && !module.autoCamera() && !wasThirdPersonCamera;
    }

    public static float closeBlend() {
        float duration = Math.max(1, SpatialGui.TRANSITION_MS);
        float openProgress = Math.max(SpatialGui.TRANSITION_SKIP, 1.0F - ScreenSurface.closeElapsedMs() / duration);
        return 1.0F - SpatialMath.easeInOutSine(openProgress);
    }

    /** Начало кадра (GameRenderer.render HEAD). */
    public static void beginFrame() {
        Minecraft mc = Minecraft.getInstance();
        Screen screen = mc.screen;
        if (hooked != null && screen != hooked) {
            unhookScreen(true);
        } else if (hooked == null && screen != null && !ScreenSurface.isClosing() && hookable(screen)) {
            // Модуль включили при уже открытом экране: перехватываем и перекладываем под масштаб панели.
            hookScreen(screen);
            if (hooked == screen) {
                screen.resize(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            }
        }
        if (ScreenSurface.isClosing() && (mc.level == null || ScreenSurface.isCloseFinished())) {
            finishClosing();
        }
        SpatialMouse.syncCursorGrab();
        updateFovProgress();

        if (capturing()) {
            SpatialTarget.prepare();
            ScreenSurface.onFrameStart();
        } else if (ScreenSurface.isClosing()) {
            ScreenSurface.onFrameStart();
        }
        skipWindowOverride = true;
    }

    private static void updateFovProgress() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            fovProgress = 0.0F;
            lastFovNanos = 0L;
            return;
        }
        SpatialGui module = SpatialGui.get();
        Screen current = mc.screen;
        boolean activeScreen = current != null && current == hooked && hookable(current);
        float target = activeScreen && module != null && module.overrideFov() ? 1.0F : 0.0F;

        long now = System.nanoTime();
        float dt = lastFovNanos == 0L ? 0.0F : Math.min((now - lastFovNanos) / 1_000_000_000.0F, 0.1F);
        lastFovNanos = now;
        float alpha = 1.0F - (float) Math.exp(-dt * SpatialGui.FOV_LERP_SPEED);
        fovProgress += (target - fovProgress) * alpha;
        if (Math.abs(target - fovProgress) < 0.001F) {
            fovProgress = target;
        }
    }

    public static float fovProgress() {
        return fovProgress;
    }

    public static boolean hasCameraStart() {
        return hasCameraStart;
    }

    public static double cameraStartX() {
        return cameraStartX;
    }

    public static double cameraStartY() {
        return cameraStartY;
    }

    public static double cameraStartZ() {
        return cameraStartZ;
    }

    public static float cameraStartYRot() {
        return cameraStartYRot;
    }

    public static void setCameraPos(double x, double y, double z) {
        cameraX = x;
        cameraY = y;
        cameraZ = z;
        hasCameraPos = true;
    }

    public static void clearCameraPos() {
        hasCameraPos = false;
    }

    /** Minecraft.setScreen: экран меняется до начала следующего кадра. */
    public static void onScreenChange(Screen next) {
        if (hooked != null && next != hooked) {
            unhookScreen(true);
        }
    }

    /** Выключение модуля: снять перехват без анимации, вернуть курсор и ванильную раскладку экрана. */
    public static void shutdown() {
        Minecraft mc = Minecraft.getInstance();
        Screen screen = hooked;
        if (screen != null) {
            unhookScreen(false);
        }
        if (ScreenSurface.isClosing()) {
            finishClosing();
        }
        pendingSnap = false;
        captureActive = false;
        hasCameraStart = false;
        skipWindowOverride = false;
        extracting = false;
        fovProgress = 0.0F;
        lastFovNanos = 0L;
        SpatialCamera.reset();
        SpatialMouse.releaseFromFirstPerson();
        SpatialTarget.destroy();
        if (screen != null && screen == mc.screen) {
            screen.resize(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        }
    }
}
