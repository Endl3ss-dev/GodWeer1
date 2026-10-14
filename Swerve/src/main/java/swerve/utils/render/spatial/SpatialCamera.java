package swerve.utils.render.spatial;

import com.mojang.blaze3d.platform.Window;

import swerve.mixins.accessor.MouseHandlerAccessor;
import swerve.modules.impl.render.SpatialGui;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Камера при открытой панели: отъезд за спину с плавным переходом и параллаксом от мыши (третье лицо)
 * либо свободный взгляд/параллакс от первого лица; обратный переход при закрытии.
 */
public final class SpatialCamera {

    private static final float MAX_YAW_OFFSET = 90.0F;
    private static final float PARALLAX_TAU_MS = 50.0F;
    private static final double COLLISION_CLEARANCE = 0.2;

    private static double startX;
    private static double startY;
    private static double startZ;
    private static float startYRot;
    private static float startXRot;
    private static long transitionStartTime;
    private static boolean wasCapturing;
    private static boolean transitioning;

    private static float smoothedYaw;
    private static float smoothedPitch;
    private static float baseXRot;
    private static float parallaxYaw;
    private static float parallaxPitch;
    private static long lastParallaxNanos;

    private static float freeLookYaw;
    private static float freeLookPitch;
    private static boolean wasCrosshairMode;

    private static boolean hasLastOffset;
    private static double lastOffsetX;
    private static double lastOffsetY;
    private static double lastOffsetZ;
    private static float lastYRot;
    private static float lastXRot;
    private static boolean closingCameraActive;

    private static double targetX;
    private static double targetY;
    private static double targetZ;
    private static float targetYaw;
    private static float targetPitch;

    private SpatialCamera() {
    }

    /** Camera.setup TAIL. */
    public static void onSetup(Camera camera, Entity entity, float partialTicks) {
        boolean capturing = SpatialState.shouldCapture();
        boolean active = SpatialState.active();
        SpatialGui module = SpatialGui.get();

        if (capturing && entity != null && active) {
            boolean resumedFromClosing = closingCameraActive;
            closingCameraActive = false;
            boolean firstPerson = module.useFirstPerson();
            SpatialState.effectiveFirstPerson = firstPerson;
            SpatialMouse.updateGrabForFirstPerson(firstPerson);

            if (!wasCapturing) {
                baseXRot = Mth.clamp(camera.getXRot(), -SpatialGui.FP_PITCH_CLAMP, SpatialGui.FP_PITCH_CLAMP);
            }
            boolean crosshair = SpatialState.isCrosshairMode();
            if (crosshair && !wasCrosshairMode) {
                freeLookYaw = 0.0F;
                freeLookPitch = 0.0F;
            }
            wasCrosshairMode = crosshair;

            if (!firstPerson && !SpatialState.firstPersonDueToBlock) {
                checkBlockCollision(entity);
            }

            computeTransform(module, entity, firstPerson, partialTicks);
            if (!wasCapturing || !transitioning || resumedFromClosing) {
                startTransition(camera, entity, firstPerson);
            }
            SpatialState.cameraYawOffset = smoothedYaw;
            applyUpdate(camera, firstPerson);

            Vec3 position = camera.getPosition();
            lastOffsetX = position.x - Mth.lerp(partialTicks, entity.xOld, entity.getX());
            lastOffsetY = position.y - Mth.lerp(partialTicks, entity.yOld, entity.getY());
            lastOffsetZ = position.z - Mth.lerp(partialTicks, entity.zOld, entity.getZ());
            hasLastOffset = true;
            lastYRot = camera.getYRot();
            lastXRot = camera.getXRot();
            wasCapturing = true;
        } else if (ScreenSurface.isClosing() && !ScreenSurface.isClosingFirstPerson() && hasLastOffset
                && entity != null && active) {
            closingCameraActive = true;
            SpatialState.wasClosing = true;
            float eased = SpatialState.closeBlend();

            double entityX = Mth.lerp(partialTicks, entity.xOld, entity.getX());
            double entityY = Mth.lerp(partialTicks, entity.yOld, entity.getY());
            double entityZ = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
            double screenX = entityX + lastOffsetX;
            double screenY = entityY + lastOffsetY;
            double screenZ = entityZ + lastOffsetZ;

            boolean toFirstPerson = SpatialState.isClosingThirdToFirstPerson();
            Vec3 current = camera.getPosition();
            double normalX = toFirstPerson ? entityX : current.x;
            double normalY = toFirstPerson ? entityY + entity.getEyeHeight() : current.y;
            double normalZ = toFirstPerson ? entityZ : current.z;
            float normalYRot = toFirstPerson ? entity.getYRot() : camera.getYRot();
            float normalXRot = toFirstPerson ? entity.getXRot() : camera.getXRot();

            double x = SpatialMath.lerp(screenX, normalX, eased);
            double y = SpatialMath.lerp(screenY, normalY, eased);
            double z = SpatialMath.lerp(screenZ, normalZ, eased);
            float yaw = SpatialMath.rotLerp(lastYRot, normalYRot, eased);
            float pitch = toFirstPerson || (SpatialGui.LERP_CAMERA_PITCH && SpatialState.wasThirdPersonCamera)
                    ? SpatialMath.lerp(lastXRot, normalXRot, eased)
                    : normalXRot;
            ((CameraHandle) camera).swerve$applyCamera(x, y, z, yaw, pitch);
            SpatialState.setCameraPos(x, y, z);
        } else {
            reset();
        }
    }

    private static void computeTransform(SpatialGui module, Entity entity, boolean firstPerson, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        float distance = autoFovDistance(module, module.cameraDistance(), firstPerson);
        float sideOffset = autoFovSideOffset(module, module.cameraSideOffset(), firstPerson);
        float heightOffset = firstPerson ? entity.getEyeHeight() : Mth.clamp(module.cameraHeightOffset(), -4.0F, 4.0F);
        if (!firstPerson && module.mirrorThirdPerson()) {
            sideOffset = -sideOffset;
        }

        MouseHandlerAccessor mouse = (MouseHandlerAccessor) mc.mouseHandler;
        double rawMouseX = SpatialMouse.physicalX(mouse.swerve$getXpos());
        double rawMouseY = SpatialMouse.physicalY(mouse.swerve$getYpos());
        float normX = Mth.clamp((float) rawMouseX / window.getScreenWidth() * 2.0F - 1.0F, -1.0F, 1.0F);
        float normY = Mth.clamp((float) rawMouseY / window.getScreenHeight() * 2.0F - 1.0F, -1.0F, 1.0F);

        float yaw;
        float pitch;
        float positionYaw;
        if (firstPerson) {
            float entityXRot = entity.getXRot();
            if (SpatialState.isCrosshairMode()) {
                SpatialMouse.consumeFreeLook();
                double sensitivity = mouseSensitivity(mc);
                double xOffset = SpatialMouse.freeLookX * sensitivity;
                double yOffset = SpatialMouse.freeLookY * sensitivity * (mc.options.invertYMouse().get() ? -1.0 : 1.0);
                freeLookYaw += (float) xOffset;
                if (SpatialGui.LOCK_FP_YAW) {
                    freeLookYaw = Mth.clamp(freeLookYaw, -MAX_YAW_OFFSET, MAX_YAW_OFFSET);
                }
                freeLookPitch = Mth.clamp(freeLookPitch + (float) yOffset, -90.0F - entityXRot, 90.0F - entityXRot);
                smoothedYaw = freeLookYaw;
                smoothedPitch = entityXRot + freeLookPitch;
            } else if (!SpatialGui.FP_PARALLAX) {
                smoothParallax(0.0F, 0.0F);
                smoothedYaw = parallaxYaw;
                smoothedPitch = baseXRot + parallaxPitch;
            } else {
                smoothParallax(normX * MAX_YAW_OFFSET * SpatialGui.FP_PARALLAX_YAW, normY * 180.0F * SpatialGui.FP_PARALLAX_PITCH);
                smoothedYaw = parallaxYaw;
                smoothedPitch = baseXRot + parallaxPitch;
            }
            smoothedPitch = Mth.clamp(smoothedPitch, -90.0F, 90.0F);
            yaw = entity.getYRot() + smoothedYaw;
            pitch = smoothedPitch;
            positionYaw = yaw;
        } else {
            if (!SpatialGui.TP_PARALLAX) {
                smoothParallax(0.0F, 0.0F);
            } else {
                smoothParallax(normX * MAX_YAW_OFFSET * SpatialGui.TP_PARALLAX_YAW, normY * 180.0F * SpatialGui.TP_PARALLAX_PITCH);
            }
            smoothedYaw = parallaxYaw;
            smoothedPitch = parallaxPitch;
            yaw = entity.getYRot() + smoothedYaw;
            pitch = SpatialGui.CAMERA_TARGET_PITCH + smoothedPitch;
            positionYaw = entity.getYRot();
        }

        float yawRad = SpatialMath.rad(positionYaw);
        double entityX = Mth.lerp(partialTicks, entity.xOld, entity.getX());
        double entityY = Mth.lerp(partialTicks, entity.yOld, entity.getY());
        double entityZ = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
        double camX = entityX + Math.sin(yawRad) * distance + Math.cos(yawRad) * sideOffset;
        double camY = entityY + heightOffset;
        double camZ = entityZ - Math.cos(yawRad) * distance + Math.sin(yawRad) * sideOffset;

        targetX = camX;
        targetY = camY;
        targetZ = camZ;
        if (!firstPerson) {
            adjustForCollision(entity, entityX, entityY + entity.getEyeHeight(), entityZ);
        }
        targetYaw = yaw;
        targetPitch = pitch;
    }

    private static void smoothParallax(float targetYawOffset, float targetPitchOffset) {
        long now = System.nanoTime();
        float dt = lastParallaxNanos == 0L ? 0.016F : Math.min((now - lastParallaxNanos) / 1_000_000_000.0F, 0.1F);
        lastParallaxNanos = now;
        float alpha = 1.0F - (float) Math.exp(-dt / (PARALLAX_TAU_MS / 1000.0F));
        parallaxYaw += (targetYawOffset - parallaxYaw) * alpha;
        parallaxPitch += (targetPitchOffset - parallaxPitch) * alpha;
    }

    private static void startTransition(Camera camera, Entity entity, boolean firstPerson) {
        if (SpatialState.hasCameraStart()) {
            startX = SpatialState.cameraStartX();
            startY = SpatialState.cameraStartY();
            startZ = SpatialState.cameraStartZ();
            startYRot = SpatialState.cameraStartYRot();
        } else {
            Vec3 position = camera.getPosition();
            startX = position.x;
            startY = position.y;
            startZ = position.z;
            startYRot = camera.getYRot();
        }
        startXRot = camera.getXRot();
        transitionStartTime = System.currentTimeMillis();
        transitioning = true;
        if (firstPerson) {
            smoothedYaw = targetYaw - entity.getYRot();
            smoothedPitch = targetPitch;
        } else {
            smoothedYaw = 0.0F;
            smoothedPitch = 0.0F;
        }
    }

    private static void applyUpdate(Camera camera, boolean firstPerson) {
        CameraHandle handle = (CameraHandle) camera;
        if (firstPerson) {
            handle.swerve$applyCamera(targetX, targetY, targetZ, targetYaw, targetPitch);
        } else {
            long elapsed = System.currentTimeMillis() - transitionStartTime;
            float adjusted = elapsed + SpatialGui.TRANSITION_SKIP * SpatialGui.TRANSITION_MS;
            float progress = Math.min(adjusted / SpatialGui.TRANSITION_MS, 1.0F);
            float eased = SpatialMath.easeInOutSine(progress);
            double x = SpatialMath.lerp(startX, targetX, eased);
            double y = SpatialMath.lerp(startY, targetY, eased);
            double z = SpatialMath.lerp(startZ, targetZ, eased);
            float yaw = progress >= 1.0F ? targetYaw : SpatialMath.rotLerp(startYRot, targetYaw, eased);
            float pitch = SpatialGui.LERP_CAMERA_PITCH && SpatialState.wasThirdPersonCamera && progress < 1.0F
                    ? SpatialMath.lerp(startXRot, targetPitch, eased)
                    : targetPitch;
            handle.swerve$applyCamera(x, y, z, yaw, pitch);
        }
        Vec3 position = camera.getPosition();
        SpatialState.setCameraPos(position.x, position.y, position.z);
    }

    public static void reset() {
        hasLastOffset = false;
        closingCameraActive = false;
        wasCapturing = false;
        transitioning = false;
        smoothedYaw = 0.0F;
        smoothedPitch = 0.0F;
        freeLookYaw = 0.0F;
        freeLookPitch = 0.0F;
        parallaxYaw = 0.0F;
        parallaxPitch = 0.0F;
        lastParallaxNanos = 0L;
        baseXRot = 0.0F;
        wasCrosshairMode = false;
        SpatialState.cameraYawOffset = 0.0F;
        SpatialState.wasThirdPersonCamera = false;
        SpatialState.firstPersonDueToBlock = false;
        SpatialState.effectiveFirstPerson = false;
        SpatialState.clearCameraPos();
        SpatialState.wasClosing = false;
        SpatialMouse.updateGrabForFirstPerson(false);
    }

    /** Третье лицо упёрлось бы в блок — переключаемся на первое лицо (если разрешено). */
    public static void checkBlockCollision(Entity entity) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        SpatialGui module = SpatialGui.get();
        if (level == null || module == null || !module.fallbackFirstPerson()) {
            SpatialState.firstPersonDueToBlock = false;
            return;
        }
        float yawRad = SpatialMath.rad(entity.getYRot());
        double distance = module.cameraDistance();
        double sideOffset = module.cameraSideOffset();
        if (module.mirrorThirdPerson()) {
            sideOffset = -sideOffset;
        }
        double camX = entity.getX() + Math.sin(yawRad) * distance + Math.cos(yawRad) * sideOffset;
        double camY = entity.getY() + module.cameraHeightOffset();
        double camZ = entity.getZ() - Math.cos(yawRad) * distance + Math.sin(yawRad) * sideOffset;

        Vec3 eye = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
        Vec3 target = new Vec3(camX, camY, camZ);
        BlockPos blockPos = BlockPos.containing(camX, camY, camZ);
        BlockState state = level.getBlockState(blockPos);
        VoxelShape shape = state.getCollisionShape(level, blockPos);
        boolean insideBlock = !state.isAir() && !shape.isEmpty()
                && shape.bounds().move(blockPos).inflate(0.001).contains(target);
        boolean pathBlocked = level.clip(new ClipContext(eye, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity))
                .getType() != HitResult.Type.MISS;
        SpatialState.firstPersonDueToBlock = insideBlock || pathBlocked;
    }

    // Как ванильный зум третьего лица: 8 лучей со смещением, камера подтягивается к ближайшему препятствию.
    private static void adjustForCollision(Entity entity, double eyeX, double eyeY, double eyeZ) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        double dx = targetX - eyeX;
        double dy = targetY - eyeY;
        double dz = targetZ - eyeZ;
        double maxDistance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (maxDistance < 0.001) {
            return;
        }
        double cameraDistance = maxDistance;
        for (int i = 0; i < 8; i++) {
            double offsetX = ((i & 1) * 2 - 1) * COLLISION_CLEARANCE;
            double offsetY = ((i >> 1 & 1) * 2 - 1) * COLLISION_CLEARANCE;
            double offsetZ = ((i >> 2 & 1) * 2 - 1) * COLLISION_CLEARANCE;
            Vec3 from = new Vec3(eyeX + offsetX, eyeY + offsetY, eyeZ + offsetZ);
            Vec3 to = new Vec3(targetX + offsetX, targetY + offsetY, targetZ + offsetZ);
            HitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
            if (hit.getType() != HitResult.Type.MISS) {
                cameraDistance = Math.min(cameraDistance, hit.getLocation().distanceTo(from));
            }
        }
        double ratio = cameraDistance / maxDistance;
        targetX = eyeX + dx * ratio;
        targetY = eyeY + dy * ratio;
        targetZ = eyeZ + dz * ratio;
    }

    private static float currentFov(SpatialGui module) {
        return module.overrideFov() ? module.targetFov() : (float) (int) Minecraft.getInstance().options.fov().get();
    }

    private static float fovMultiplier(SpatialGui module) {
        return SpatialGui.AUTO_SCALE_BY_FOV ? currentFov(module) / SpatialGui.AUTO_SCALE_BASE_FOV : 1.0F;
    }

    private static float autoFovDistance(SpatialGui module, float baseDistance, boolean firstPerson) {
        if (firstPerson) {
            return 0.0F;
        }
        float adjustment = (1.0F / fovMultiplier(module) - 1.0F) * SpatialGui.AUTO_SCALE_DISTANCE_MULT;
        return Mth.clamp(baseDistance * (1.0F + adjustment), -4.0F, 4.0F);
    }

    private static float autoFovSideOffset(SpatialGui module, float baseSideOffset, boolean firstPerson) {
        if (firstPerson) {
            return 0.0F;
        }
        float adjustment = (1.0F / fovMultiplier(module) - 1.0F) * SpatialGui.AUTO_SCALE_SIDE_MULT;
        return Mth.clamp(baseSideOffset * (1.0F + adjustment), -4.0F, 4.0F);
    }

    private static double mouseSensitivity(Minecraft mc) {
        double value = mc.options.sensitivity().get() * 0.6 + 0.2;
        return value * value * value;
    }
}
