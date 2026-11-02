package tech.lightclient.mixins.client;


import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.GaussianGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicLong;

@Mixin(CheckedRandom.class)
public abstract class CheckedRandomMixin {

    @Shadow
    @Final
    private AtomicLong seed;

    @Shadow
    @Final
    private GaussianGenerator gaussianGenerator;

    @Inject(method = "next(I)I", at = @At("HEAD"), cancellable = true)
    private void light$atomicNext(int bits, CallbackInfoReturnable<Integer> cir) {
        AtomicLong s = this.seed;
        long prev;
        long next;
        do {
            prev = s.get();
            next = (prev * 25214903917L + 11L) & 281474976710655L;
        } while (!s.compareAndSet(prev, next));
        cir.setReturnValue((int) (next >>> (48 - bits)));
    }

    @Inject(method = "setSeed(J)V", at = @At("HEAD"), cancellable = true)
    private void light$atomicSetSeed(long seedValue, CallbackInfo ci) {
        this.seed.set((seedValue ^ 25214903917L) & 281474976710655L);
        this.gaussianGenerator.reset();
        ci.cancel();
    }
}
