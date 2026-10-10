package swerve.mixins.client;

import swerve.modules.impl.render.SwingAnimation;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "getCurrentSwingDuration", at = @At("RETURN"), cancellable = true)
    private void swerve$swingDuration(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this != Minecraft.getInstance().player) {
            return;
        }
        int vanilla = cir.getReturnValueI();
        int modified = SwingAnimation.modifySwingDuration(vanilla);
        if (modified != vanilla) {
            cir.setReturnValue(modified);
        }
    }
}
