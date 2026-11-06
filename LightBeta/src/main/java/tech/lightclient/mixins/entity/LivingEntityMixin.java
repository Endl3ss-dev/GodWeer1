package tech.lightclient.mixins.entity;


import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.RotationFixEvent;
import tech.lightclient.client.events.impl.entity.EventJump;
import tech.lightclient.client.events.impl.player.JumpEvent;
import tech.lightclient.client.events.impl.player.TravelRotationEvent;
import tech.lightclient.client.events.impl.player.ElytraVelocityEvent;
import tech.lightclient.client.utils.baritone.BaritoneRotationHook;
import tech.lightclient.client.utils.math.MathUtil;
import tech.lightclient.client.utils.player.rotation.RotationTask;
import tech.lightclient.mixins.accessors.LEntityAccessor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LEntityAccessor {
    @Shadow
    public float headYaw;
    @Shadow public float lastHeadYaw;

    @Redirect(
            method = "calcGlidingVelocity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"
            )
    )
    private Vec3d redirectGetRotationVector(LivingEntity instance) {
        if (mc.player == null) return instance.getRotationVector();
        if (((Object) this) instanceof ClientPlayerEntity) {
            TravelRotationEvent event = new TravelRotationEvent(
                    (LivingEntity)(Object)this,
                    instance.getPitch(),
                    instance.getYaw()
            );
            LightClient.getEventBus().post(event);

            if (event.isCancelled()) {
                return MathUtil.getRotationVector(event.getPitch(), event.getYaw());
            }
        }
        return instance.getRotationVector();
    }

    @Inject(method = "calcGlidingVelocity", at = @At("HEAD"), cancellable = true)
    private void light$onCalcGlidingVelocity(Vec3d velocity, CallbackInfoReturnable<Vec3d> cir) {
        if ((Object) this instanceof ClientPlayerEntity player) {
            ElytraVelocityEvent event = new ElytraVelocityEvent(player, velocity);
            LightClient.getEventBus().post(event);

            if (event.isModified()) {
                cir.setReturnValue(event.getVelocity());
            }
        }
    }

    @Redirect(
            method = "calcGlidingVelocity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"
            )
    )
    private float redirectGetPitch(LivingEntity instance) {
        if (mc.player == null) return instance.getPitch();
        if (((Object) this) instanceof ClientPlayerEntity) {
            TravelRotationEvent event = TravelRotationEvent.getLastRotationEvent();
            return event != null && event.isCancelled() ? event.getPitch() : instance.getPitch();
        }
        return instance.getPitch();
    }

    @Unique
    private static float fixedYaw = 0;

    @Inject(at = @At("HEAD"), method = "getYaw", cancellable = true)
    public void yawFix(float tickProgress, CallbackInfoReturnable<Float> cir) {
        fixedYaw = tickProgress == 1.0F ? this.headYaw : MathHelper.lerpAngleDegrees(tickProgress, this.lastHeadYaw, this.headYaw);

        if (((Object) this) instanceof ClientPlayerEntity) {
            fixedYaw = RotationTask.antiAimModulo360(this.lastHeadYaw, fixedYaw);
            fixedYaw = RotationTask.fixNegativeZero(fixedYaw);

            cir.setReturnValue(fixedYaw);
        }
    }

    @Inject(at = @At("HEAD"), method = "jump", cancellable = true)
    public void onJump(CallbackInfo ci) {
        if ((Object) this == mc.player) {
            if (BaritoneRotationHook.isActive()) {
                return;
            }
            ci.cancel();

            JumpEvent jumpEvent = new JumpEvent();
            LightClient.getEventBus().post(jumpEvent);
            LightClient.getEventBus().post(new EventJump());

            RotationFixEvent RotationFixEvent = new RotationFixEvent();
            LightClient.getEventBus().post(RotationFixEvent);

            float f = mc.player.getJumpVelocity();
            if (!(f <= 1.0E-5F)) {
                Vec3d vec3d = mc.player.getVelocity();
                mc.player.setVelocity(vec3d.x, Math.max(f, vec3d.y), vec3d.z);
                if (mc.player.isSprinting()) {
                    float yawToUse = RotationTask.isRotating() ? RotationTask.visualHeadYaw : mc.player.getYaw();
                    float g = yawToUse * ((float)Math.PI / 180F);
                    mc.player.addVelocityInternal(new Vec3d((double)(-MathHelper.sin(g)) * 0.2, (double)0.0F, (double)MathHelper.cos(g) * 0.2));
                }

                mc.player.velocityDirty = true;
            }
        }
    }

}
