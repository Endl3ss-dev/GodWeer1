package godweer.mixins.client;

import godweer.utils.movement.MotionHooks;

import net.minecraft.client.player.LocalPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer {

    static {
        MotionHooks.markHooksLoaded();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void godweer$onPreMotion(CallbackInfo ci) {
        MotionHooks.preMotion((LocalPlayer) (Object) this);
    }
}
