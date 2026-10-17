package swerve.mixins.client;

import swerve.cosmetics.Wearables;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Гардероб: служебная строка чата с моделями применяется к игроку-отправителю.
 *
 * <p>Само сообщение не отменяется — иначе рвётся цепочка подписей чата.
 */
@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

    @Inject(method = "handlePlayerChat(Lnet/minecraft/network/protocol/game/ClientboundPlayerChatPacket;)V",
            at = @At("HEAD"))
    private void swerve$cosmetics(ClientboundPlayerChatPacket packet, CallbackInfo ci) {
        try {
            String text = packet.unsignedContent().getString();
            int[] v = new int[4];
            if (Wearables.parse(text, v)) {
                Wearables.applyRemote(packet.sender(), v[0], v[1], v[2], v[3]);
            }
        } catch (RuntimeException ignored) {
        }
    }
}
