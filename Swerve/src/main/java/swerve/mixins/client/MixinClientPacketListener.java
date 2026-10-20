package swerve.mixins.client;

import swerve.cosmetics.Wearables;
import swerve.cosmetics.cape.CapeState;
import swerve.cosmetics.melee.MeleeState;
import swerve.utils.network.ServerTPS;

import java.util.UUID;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;

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

    // KillAura/TPS: пакеты времени — опорные точки трекера TPS сервера.
    // Adapted from Private/src/java/org/privates/client/utils/server/ServerTPS.java.
    @Inject(method = "handleSetTime", at = @At("HEAD"))
    private void swerve$serverTps(ClientboundSetTimePacket packet, CallbackInfo ci) {
        ServerTPS.onTimePacket();
    }

    @Inject(method = "handlePlayerChat(Lnet/minecraft/network/protocol/game/ClientboundPlayerChatPacket;)V",
            at = @At("HEAD"))
    private void swerve$cosmetics(ClientboundPlayerChatPacket packet, CallbackInfo ci) {
        try {
            String text = packet.unsignedContent().getString();
            int[] v = new int[Wearables.SYNC_VALUES];
            if (Wearables.parse(text, v)) {
                UUID sender = packet.sender();
                Wearables.applyRemote(sender, v[0], v[1], v[2], v[3]);
                MeleeState.applyRemote(sender, v[4]);
                CapeState.applyRemote(sender, v[5]);
            }
        } catch (RuntimeException ignored) {
        }
    }

    // Игрок вышел — его состояние больше не нужно.
    @Inject(method = "handlePlayerInfoRemove", at = @At("HEAD"))
    private void swerve$forget(ClientboundPlayerInfoRemovePacket packet, CallbackInfo ci) {
        try {
            for (UUID id : packet.profileIds()) {
                Wearables.forget(id);
                MeleeState.forget(id);
                CapeState.forget(id);
            }
        } catch (RuntimeException ignored) {
        }
    }
}
