package swerve.utils.render.spatial;

import swerve.mixins.accessor.MouseHandlerAccessor;
import swerve.modules.impl.render.SpatialGui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.util.Mth;

/**
 * Модель игрока при открытой панели: тело довёрнуто к экрану, голова следит за курсором;
 * голова скрывается, когда камера третьего лица подъезжает вплотную.
 */
public final class SpatialAvatar {

    private static final float SMOOTHING = 0.15F;
    private static final double HIDE_DISTANCE_SQ = 0.6 * 0.6;

    private static float smoothedYaw;
    private static float smoothedPitch;
    private static float shownBody;
    private static float shownYaw;
    private static float shownPitch;
    private static long openStartNanos;

    private static float poseBody;
    private static float poseYaw;
    private static float posePitch;

    private SpatialAvatar() {
    }

    /** PlayerRenderer.extractRenderState TAIL: подменяет углы в состоянии рендера локального игрока. */
    public static void applyPose(AbstractClientPlayer player, PlayerRenderState state) {
        if (player != Minecraft.getInstance().player || SpatialState.extracting) {
            return;
        }
        if (!computePose(state.bodyRot, state.yRot, state.xRot)) {
            return;
        }
        state.bodyRot = poseBody;
        state.yRot = poseYaw;
        state.xRot = posePitch;
    }

    private static boolean computePose(float vanillaBody, float vanillaYaw, float vanillaPitch) {
        boolean closing = ScreenSurface.isClosing() && !ScreenSurface.isClosingFirstPerson();
        if (!(SpatialState.shouldCapture() || closing) || !SpatialState.active()) {
            SpatialState.headLockInitialized = false;
            return false;
        }
        if (closing) {
            float blend = SpatialState.closeBlend();
            poseBody = Mth.rotLerp(blend, shownBody, vanillaBody);
            poseYaw = Mth.rotLerp(blend, shownYaw, vanillaYaw);
            posePitch = Mth.lerp(blend, shownPitch, vanillaPitch);
            return true;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        SpatialGui module = SpatialGui.get();
        if (player == null || module == null) {
            return false;
        }
        MouseHandlerAccessor mouse = (MouseHandlerAccessor) mc.mouseHandler;
        float normX = Mth.clamp((float) (mouse.swerve$getXpos() / mc.getWindow().getScreenWidth()) * 2.0F - 1.0F, -1.0F, 1.0F);
        float normY = Mth.clamp((float) (mouse.swerve$getYpos() / mc.getWindow().getScreenHeight()) * 2.0F - 1.0F, -1.0F, 1.0F);
        if (SpatialGui.MIRROR_HEAD) {
            normX = -normX;
        }
        float side = module.mirrorThirdPerson() ? -1.0F : 1.0F;
        float targetBody = player.getYRot() + side * SpatialGui.AVATAR_BODY_OFFSET;
        float targetYaw = normX * SpatialGui.AVATAR_MAX_YAW + side * SpatialGui.AVATAR_BASE_YAW;
        float targetPitch = Mth.clamp(normY * SpatialGui.AVATAR_MAX_PITCH, -60.0F, 60.0F);

        long now = System.nanoTime();
        if (!SpatialState.headLockInitialized) {
            smoothedYaw = targetYaw;
            smoothedPitch = targetPitch;
            openStartNanos = now;
            SpatialState.headLockInitialized = true;
        } else {
            smoothedYaw = Mth.rotLerp(SMOOTHING, smoothedYaw, targetYaw);
            smoothedPitch = Mth.lerp(SMOOTHING, smoothedPitch, targetPitch);
        }

        float t = Mth.clamp((now - openStartNanos) / 1_000_000.0F / SpatialGui.OPEN_MS, 0.0F, 1.0F);
        float open = 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t);
        shownBody = Mth.rotLerp(open, vanillaBody, targetBody);
        shownYaw = Mth.rotLerp(open, vanillaYaw, smoothedYaw);
        shownPitch = Mth.lerp(open, vanillaPitch, smoothedPitch);
        poseBody = shownBody;
        poseYaw = shownYaw;
        posePitch = shownPitch;
        return true;
    }

    /** PlayerModel.setupAnim TAIL: голова локального игрока скрывается, когда камера панели ближе 0.6 блока. */
    public static boolean shouldHideHead(PlayerRenderState state) {
        if (!SpatialState.hasCameraPos || !SpatialState.active() || SpatialState.extracting) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || state.id != player.getId() || mc.options.getCameraType().isFirstPerson()) {
            return false;
        }
        if (!SpatialState.shouldCapture() && !ScreenSurface.isClosing() && !SpatialState.wasClosing) {
            return false;
        }
        double dx = SpatialState.cameraX - state.x;
        double dy = SpatialState.cameraY - (state.y + state.eyeHeight);
        double dz = SpatialState.cameraZ - state.z;
        return dx * dx + dy * dy + dz * dz < HIDE_DISTANCE_SQ;
    }

    /** Покачивание рук от первого лица следует за камерой, а не за поворотом игрока. */
    public static boolean swayActive() {
        SpatialGui module = SpatialGui.get();
        return module != null && module.isEnable() && SpatialState.effectiveFirstPerson
                && SpatialState.hooked != null && !module.hideHands();
    }
}
