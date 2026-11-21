package tech.lightclient.mixins.modules;


import net.minecraft.client.network.ClientPlayerEntity;
import tech.lightclient.client.events.impl.player.SprintEvent;
import tech.lightclient.client.utils.baritone.BaritoneRotationHook;
import tech.lightclient.modules.impl.movement.AutoSprint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(value = ClientPlayerEntity.class, priority = 999)
public class AutoSprintMovementMixin {

    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PlayerInput;sprint()Z"))
    private boolean hookSprintStart(boolean original) {
        SprintEvent event = new SprintEvent(original);
        AutoSprint autoSprint = AutoSprint.getInstance();
        if (autoSprint != null && autoSprint.isState()) {
            autoSprint.onSprint(event);
        }
        if (BaritoneRotationHook.isActive()) {
            return false;
        }
        return event.isSprinting();
    }

    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldStopSprinting()Z"))
    private boolean hookSprintStop(boolean original) {
        SprintEvent event = new SprintEvent(!original);
        AutoSprint autoSprint = AutoSprint.getInstance();
        if (autoSprint != null && autoSprint.isState()) {
            autoSprint.onSprint(event);
        }
        if (BaritoneRotationHook.isActive()) {
            return true;
        }
        return !event.isSprinting();
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPackets(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        SprintEvent event = new SprintEvent(player.isSprinting());
        AutoSprint autoSprint = AutoSprint.getInstance();
        if (autoSprint != null && autoSprint.isState()) {
            autoSprint.onSprint(event);
        }
    }
}
