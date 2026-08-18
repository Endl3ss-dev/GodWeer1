package godweer.mixins.client;

import godweer.modules.impl.misc.AntiCheatDetector;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {

    @Inject(method = "handleMovePlayer", at = @At("HEAD"))
    private void godweer$recordPositionCorrection(ClientboundPlayerPositionPacket packet, CallbackInfo callbackInfo) {
        AntiCheatDetector.onPositionCorrectionPacket();
    }

    @Inject(method = "handleSystemChat", at = @At("HEAD"))
    private void godweer$recordSystemMessage(ClientboundSystemChatPacket packet, CallbackInfo callbackInfo) {
        AntiCheatDetector.onSystemMessagePacket(packet.content().getString());
    }
}
