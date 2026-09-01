package godweer.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import godweer.utils.movement.TimerManager;

import net.minecraft.client.DeltaTracker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DeltaTracker.Timer.class)
public abstract class MixinDeltaTracker {

    // Делим целевое время тика на множитель: при speed > 1 за кадр проходит больше тиков.
    @ModifyExpressionValue(
            method = "advanceGameTime",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/floats/FloatUnaryOperator;apply(F)F")
    )
    private float godweer$applyTimerSpeed(float targetMspt) {
        float timerSpeed = TimerManager.get();
        return timerSpeed <= 1.0F ? targetMspt : targetMspt / timerSpeed;
    }
}
