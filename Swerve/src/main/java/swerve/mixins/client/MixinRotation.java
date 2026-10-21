package swerve.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import swerve.managers.ModuleManager;
import swerve.modules.impl.combat.Aura;
import swerve.modules.impl.combat.rotation.AuraRotation;
import swerve.mixins.accessor.RotationAccessor;

/**
 * Миксин, применяющий выбранную ротацию от модуля Aura к взгляду игрока.
 * Вызывается в конце LocalPlayer.tick(), после всей ванильной логики,
 * поэтому наша ротация перезаписывает ванильные углы.
 */
@Mixin(LocalPlayer.class)
public class MixinRotation {

    @Inject(method = "tick", at = @At("TAIL"))
    private void swerve$applyAuraRotation(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        Minecraft mc = Minecraft.getInstance();

        if (player != mc.player) {
            return;
        }

        Aura aura = ModuleManager.get(Aura.class);
        if (aura == null || !aura.isEnable()) {
            return;
        }

        AuraRotation rotation = aura.getRotation();
        if (rotation == null || aura.getTarget() == null) {
            return;
        }

        RotationAccessor accessor = (RotationAccessor) (Object) player;
        float currentYaw = accessor.swerve$getYaw();
        float currentPitch = accessor.swerve$getPitch();

        float[] out = new float[2];
        rotation.calculateRotation(aura.getTarget(), mc, currentYaw, currentPitch, out);

        accessor.swerve$setYaw(out[0]);
        accessor.swerve$setPitch(out[1]);
    }
}
