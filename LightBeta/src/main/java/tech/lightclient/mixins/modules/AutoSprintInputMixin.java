package tech.lightclient.mixins.modules;


import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import tech.lightclient.client.events.impl.player.SprintEvent;
import tech.lightclient.modules.impl.movement.AutoSprint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(value = KeyboardInput.class, priority = 999)
public class AutoSprintInputMixin {

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "NEW", target = "(ZZZZZZZ)Lnet/minecraft/util/PlayerInput;")
    )
    private PlayerInput modifyInput(PlayerInput original) {
        SprintEvent eventS = new SprintEvent(original.sprint());
        AutoSprint autoSprint = AutoSprint.getInstance();
        if (autoSprint != null && autoSprint.isState()) {
            autoSprint.onSprint(eventS);
        }
        return new PlayerInput(
                original.forward(),
                original.backward(),
                original.left(),
                original.right(),
                original.jump(),
                original.sneak(),
                eventS.isSprinting()
        );
    }
}
