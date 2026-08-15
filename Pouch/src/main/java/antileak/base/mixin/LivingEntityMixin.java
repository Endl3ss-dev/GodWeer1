package antileak.base.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import antileak.base.client.modules.impl.render.SwingAnimations;
import net.minecraft.entity.Entity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentEffectContext;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import antileak.base.api.events.implement.EventThorns;
import antileak.base.Punch;
import antileak.base.client.modules.impl.render.Removals;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public int hurtTime;

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

    @Inject(method = "getDamageTiltYaw", at = @At("HEAD"), cancellable = true)
    private void onGetDamageTiltYaw(CallbackInfoReturnable<Float> cir) {
        if ((Object) this != MinecraftClient.getInstance().player) return;
        if (ModuleClass.INSTANCE == null) return;

        Removals removals = ModuleClass.removals;
        if (removals != null && removals.isScreenShakeDisabled()) {
            cir.setReturnValue(0.0f);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if ((Object) this != MinecraftClient.getInstance().player) return;
        if (ModuleClass.INSTANCE == null) return;

        Removals removals = ModuleClass.removals;
        if (removals != null && removals.isScreenShakeDisabled()) {
            ((LivingEntity) (Object) this).hurtTime = 0;
        }
    }
}
