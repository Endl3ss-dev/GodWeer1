package tech.lightclient.mixins.modules;


import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.PlayerInput;
import tech.lightclient.client.events.impl.player.SprintEvent;
import tech.lightclient.client.utils.baritone.BaritoneRotationHook;
import tech.lightclient.modules.impl.combat.AimBot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(value = ClientPlayerEntity.class, priority = 999)
public class AimBotSprintMixin {

    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PlayerInput;sprint()Z"))
    private boolean hookSprintStart(boolean original) {
        SprintEvent event = new SprintEvent(original);
        AimBot aimBot = AimBot.getInstance();
        if (aimBot != null && aimBot.isState()) {
            aimBot.onSprint(event);
        }
        if (BaritoneRotationHook.isActive()) {
            return false;
        }
        return event.isSprinting();
    }

    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldStopSprinting()Z"))
    private boolean hookSprintStop(boolean original) {
        SprintEvent event = new SprintEvent(!original);
        AimBot aimBot = AimBot.getInstance();
        if (aimBot != null && aimBot.isState()) {
            aimBot.onSprint(event);
        }
        if (BaritoneRotationHook.isActive()) {
            return true;
        }
        return !event.isSprinting();
    }
}
