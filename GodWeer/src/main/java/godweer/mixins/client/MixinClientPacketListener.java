package godweer.mixins.client;

import godweer.utils.movement.MotionHooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

    @Inject(method = {"handleTeleport", "handleSetPlayerPosition", "handleSetPlayerPositionAndRotation"},
            at = @At("TAIL"))
    private void godweer$onServerPosition(CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        MotionHooks.positionCorrection();
    }
}
