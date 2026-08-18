package godweer.mixins.client;

import godweer.modules.impl.misc.AntiCheatDetector;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class MixinClientCommonPacketListener {

    @Inject(method = "handleCustomPayload", at = @At("HEAD"))
    private void godweer$recordCustomPayload(ClientboundCustomPayloadPacket packet, CallbackInfo callbackInfo) {
        AntiCheatDetector.onCustomPayloadPacket(packet.payload().type().id().toString());
    }

    @Inject(method = "handleDisconnect", at = @At("HEAD"))
    private void godweer$recordDisconnect(ClientboundDisconnectPacket packet, CallbackInfo callbackInfo) {
        AntiCheatDetector.onDisconnectPacket(packet.reason().getString());
    }
}
