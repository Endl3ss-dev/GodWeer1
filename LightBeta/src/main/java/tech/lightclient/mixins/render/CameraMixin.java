package tech.lightclient.mixins.render;
import tech.lightclient.LightClient;import tech.lightclient.client.events.impl.render.EventCamera;
import tech.lightclient.client.events.impl.render.CameraRotationEvent;import tech.lightclient.modules.impl.player.FreeCamera;import tech.lightclient.modules.impl.render.FreeLook;import tech.lightclient.modules.impl.utils.Tweaks;import net.minecraft.client.network.ClientPlayerEntity;import net.minecraft.client.render.Camera;import net.minecraft.entity.Entity;import net.minecraft.util.math.MathHelper;import net.minecraft.world.World;import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.Shadow;import org.spongepowered.asm.mixin.Unique;import org.spongepowered.asm.mixin.injection.At;import org.spongepowered.asm.mixin.injection.Inject;import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;import tech.lightclient.client.utils.baritone.BaritoneRotationHook;import static tech.lightclient.client.utils.client.IMinecraft.mc;@Mixin(Camera.class)public abstract class CameraMixin {
    @Unique
    private CameraRotationEvent light$cameraRotationEvent;

    @Inject(method = "update", at = @At("HEAD"))
    private void light$cameraRotationHead(World area, Entity focusedEntity, boolean thirdPerson,
                                          boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (focusedEntity == null || LightClient.getEventBus() == null) {
            light$cameraRotationEvent = null;
            return;
        }

        light$cameraRotationEvent = new CameraRotationEvent(
                focusedEntity,
                focusedEntity.getYaw(tickDelta),
                focusedEntity.getPitch(tickDelta),
                tickDelta
        );
        LightClient.getEventBus().post(light$cameraRotationEvent);
    }

    @org.spongepowered.asm.mixin.injection.Redirect(
            method = "update",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V")
    )
    private void light$redirectCameraRotation(Camera camera, float yaw, float pitch) {
        CameraRotationEvent event = light$cameraRotationEvent;
        if (event != null && event.isChanged()) {
            setRotation(event.getYaw(), event.getPitch());
        } else {
            setRotation(yaw, pitch);
        }
    }

    @Inject(method = "update", at = @At("RETURN"))
    private void light$cameraRotationReturn(World area, Entity focusedEntity, boolean thirdPerson,
                                            boolean inverseView, float tickDelta, CallbackInfo ci) {
        light$cameraRotationEvent = null;
    }    @Shadow    private boolean thirdPerson;    @Shadow    protected abstract void setRotation(float yaw, float pitch);    @Shadow    protected abstract void moveBy(float f, float g, float h);    @Shadow    protected abstract void setPos(double x, double y, double z);    @Shadow    protected abstract float clipToSpace(float f);    @Unique    private float light$perspectiveDistance = 0.3f;    @Unique    private long light$lastPerspectiveNs = System.nanoTime();    @Unique    private float light$lastTargetDistance = 0f;    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.AFTER), cancellable = true)    public void onUpdate(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {        EventCamera event = new EventCamera();        LightClient.getEventBus().post(event);        FreeCamera freeCamera = LightClient.getFunctionManager().getFreeCamera();        if (freeCamera != null && freeCamera.isState()) {            setRotation(freeCamera.getFakeYaw(), freeCamera.getFakePitch());            setPos(                    freeCamera.getFakeX(tickDelta),                    freeCamera.getFakeY(tickDelta),                    freeCamera.getFakeZ(tickDelta)            );            this.thirdPerson = true;            ci.cancel();            return;        }        FreeLook freeLook = LightClient.getFunctionManager().getFreeLook();        if (freeLook != null && freeLook.isActive() && focusedEntity instanceof ClientPlayerEntity player && !player.isSleeping()) {            float pitch = inverseView ? -freeLook.getCameraPitch() : freeLook.getCameraPitch();            float yaw = freeLook.getCameraYaw() - (inverseView ? 180f : 0f);            float distance = thirdPerson ? event.getDistance() : 0f;            setRotation(yaw, pitch);            moveBy(event.isCameraClip() ? -distance : -clipToSpace(distance), 0.0F, 0.0F);            ci.cancel();            return;        }        if (BaritoneRotationHook.isActive()                && focusedEntity instanceof ClientPlayerEntity player && !player.isSleeping()) {            float camYaw = BaritoneRotationHook.getCameraYaw();            float camPitch = BaritoneRotationHook.getCameraPitch();            float pitch = inverseView ? -camPitch : camPitch;            float yaw = camYaw - (inverseView ? 180f : 0f);            float distance = thirdPerson ? event.getDistance() : 0f;            setRotation(yaw, pitch);            moveBy(event.isCameraClip() ? -distance : -clipToSpace(distance), 0.0F, 0.0F);            ci.cancel();            return;        }        float targetDistance = thirdPerson ? event.getDistance() : 0f;        boolean enteringThirdPerson = targetDistance > 0f && light$lastTargetDistance <= 0f;        light$lastTargetDistance = targetDistance;        long now = System.nanoTime();        float dt = (now - light$lastPerspectiveNs) / 1_000_000_000.0f;        light$lastPerspectiveNs = now;        boolean check = LightClient.getFunctionManager().getTweaks().isState() && Tweaks.f5animation.get();        if (LightClient.isHide() || !check) {            light$perspectiveDistance = targetDistance;        } else {            if (enteringThirdPerson) {                light$perspectiveDistance = 0.3f;            }            float speed = 14;            float t = MathHelper.clamp(dt * speed, 0f, 1f);            light$perspectiveDistance = MathHelper.lerp(t, light$perspectiveDistance, targetDistance);        }        if ((thirdPerson || light$perspectiveDistance > 0.01f) && focusedEntity instanceof ClientPlayerEntity player && !player.isSleeping()) {            float pitch = inverseView ? -mc.player.getPitch() : mc.player.getPitch();            float yaw = mc.player.getYaw() - (inverseView ? 180f : 0f);            float distance = light$perspectiveDistance;            setRotation(yaw, pitch);            moveBy(event.isCameraClip() ? -distance : -clipToSpace(distance), 0.0F, 0.0F);            ci.cancel();        }    }    @Inject(method = "update", at = @At("HEAD"))    private void onUpdateHead(CallbackInfo ci) {        if (LightClient.getEventBus() == null) return;        EventCamera event = new EventCamera();        LightClient.getEventBus().post(event);    }}