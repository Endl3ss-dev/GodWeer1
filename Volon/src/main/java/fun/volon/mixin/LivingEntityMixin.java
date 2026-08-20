package fun.volon.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fun.volon.api.events.EventInvoker;
import fun.volon.api.events.implement.EventOnTravelPost;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.client.modules.impl.render.SwingAnimations;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "getHandSwingDuration", at = @At("HEAD"), cancellable = true)
    private void onGetHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return;
        }

        if (ModuleClass.INSTANCE == null) {
            return;
        }

        SwingAnimations tweaks = ModuleClass.swingAnimations;
        if (tweaks != null && tweaks.isEnable() && tweaks.smoothEnabled.isState()) {
            cir.setReturnValue((int) tweaks.slowAnimationSpeed.get());
        }
    }

    @Inject(method = "calcGlidingVelocity", at = @At("RETURN"), cancellable = true)
    private void onsetvelocity(Vec3d oldVelocity, CallbackInfoReturnable<Vec3d> cir) throws Exception {
        if (!EventInvoker.hasListeners(EventOnTravelPost.class)) {
            return;
        }

        EventOnTravelPost event = new EventOnTravelPost(
                oldVelocity.multiply(0.9900000095367432, 0.9800000190734863, 0.9900000095367432)
        );
        EventInvoker.invoke(event);
        cir.setReturnValue(event.getOldVelocity());
    }
}
