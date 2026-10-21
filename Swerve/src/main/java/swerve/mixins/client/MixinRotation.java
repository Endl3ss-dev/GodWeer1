package swerve.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import swerve.managers.ModuleManager;
import swerve.modules.impl.combat.Aura;
import swerve.modules.impl.combat.rotation.AuraRotation;

/**
 * Миксин, применяющий выбранную ротацию от модуля Aura к взгляду игрока.
 * Вызывается в конце LocalPlayer.tick(), после всей ванильной логики,
 * поэтому наша ротация перезаписывает ванильные углы.
 * <p>
 * В Mojang маппингах 1.21.4 в LocalPlayer нет公開ых полей yRot/xRot —
 * используются методы getYRot()/setYRot() и getXRot()/setXRot() из LivingEntity.
 */
@Mixin(LocalPlayer.class)
public class MixinRotation {

    @Shadow
    public float getYRot();

    @Shadow
    public void setYRot(float yaw);

    @Shadow
    public float getXRot();

    @Shadow
    public void setXRot(float pitch);

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

        float currentYaw = getYRot();
        float currentPitch = getXRot();

        float[] out = new float[2];
        rotation.calculateRotation(aura.getTarget(), mc, currentYaw, currentPitch, out);

        setYRot(out[0]);
        setXRot(out[1]);
    }
}
