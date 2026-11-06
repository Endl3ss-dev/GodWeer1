package tech.lightclient.mixins.render;


import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.lightclient.client.utils.player.TimerController;

@Mixin(RenderTickCounter.Dynamic.class)
public class RenderTickCounterMixin {

    @Shadow
    private float dynamicDeltaTicks;

    @Inject(
            method = "beginRenderTick(J)I",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;lastTimeMillis:J",
                    shift = At.Shift.AFTER
            )
    )
    private void light$multiplyDeltaTicks(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        this.dynamicDeltaTicks *= TimerController.getTimerSpeed();
    }
}
