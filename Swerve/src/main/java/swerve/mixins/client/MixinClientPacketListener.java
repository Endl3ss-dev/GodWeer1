package swerve.mixins.client;

import swerve.acd.AcdCollector;
import swerve.cosmetics.Wearables;
import swerve.cosmetics.cape.CapeState;
import swerve.cosmetics.melee.MeleeState;
import swerve.utils.network.ServerTPS;

import java.util.UUID;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;

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

    // ---- AntiCheatDetector: что сервер показал клиенту.
    //
    // Только чтение полученных пакетов на HEAD, до ванильной обработки: отмена,
    // задержка или подмена здесь означали бы вмешательство в игровой процесс. Каждый
    // обработчик закрыт в try/catch — диагностика не имеет права ронять игру, и при
    // поломке сбор выключается целиком (AcdCollector.disableCollection), а ванильный
    // путь пакета не трогается. Проверка «включён ли сбор» живёт внутри AcdCollector,
    // поэтому в выключенном состоянии цена обработчика — одно чтение volatile-флага.

    @Inject(method = "handleLogin(Lnet/minecraft/network/protocol/game/ClientboundLoginPacket;)V",
            at = @At("HEAD"))
    private void swerve$acdLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
        try {
            AcdCollector.onLogin(packet);
        } catch (Exception exception) {
            AcdCollector.disableCollection("handleLogin", exception);
        }
    }

    @Inject(method = "handleSystemChat(Lnet/minecraft/network/protocol/game/ClientboundSystemChatPacket;)V",
            at = @At("HEAD"))
    private void swerve$acdSystemChat(ClientboundSystemChatPacket packet, CallbackInfo ci) {
        try {
            AcdCollector.onSystemChat(packet);
        } catch (Exception exception) {
            AcdCollector.disableCollection("handleSystemChat", exception);
        }
    }

    @Inject(method = "handleDisguisedChat(Lnet/minecraft/network/protocol/game/ClientboundDisguisedChatPacket;)V",
            at = @At("HEAD"))
    private void swerve$acdDisguisedChat(ClientboundDisguisedChatPacket packet, CallbackInfo ci) {
        try {
            AcdCollector.onDisguisedChat(packet);
        } catch (Exception exception) {
            AcdCollector.disableCollection("handleDisguisedChat", exception);
        }
    }

    // Подписанный чат игроков: ничего не пишется, пока текст
    // не совпал с правилом, обычная переписка в журнал не попадает.
    @Inject(method = "handlePlayerChat(Lnet/minecraft/network/protocol/game/ClientboundPlayerChatPacket;)V",
            at = @At("HEAD"))
    private void swerve$acdPlayerChat(ClientboundPlayerChatPacket packet, CallbackInfo ci) {
        try {
            AcdCollector.onPlayerChat(packet);
        } catch (Exception exception) {
            AcdCollector.disableCollection("handlePlayerChat", exception);
        }
    }

    // Коррекция позиции нашим клиентом: это и есть «откат», если сервер его делал.
    @Inject(method = "handleMovePlayer(Lnet/minecraft/network/protocol/game/ClientboundPlayerPositionPacket;)V",
            at = @At("HEAD"))
    private void swerve$acdPositionCorrection(ClientboundPlayerPositionPacket packet, CallbackInfo ci) {
        try {
            AcdCollector.onPositionCorrection(packet);
        } catch (Exception exception) {
            AcdCollector.disableCollection("handleMovePlayer", exception);
        }
    }

    @Inject(method = "handleSetEntityMotion(Lnet/minecraft/network/protocol/game/ClientboundSetEntityMotionPacket;)V",
            at = @At("HEAD"))
    private void swerve$acdMotion(ClientboundSetEntityMotionPacket packet, CallbackInfo ci) {
        try {
            AcdCollector.onMotion(packet);
        } catch (Exception exception) {
            AcdCollector.disableCollection("handleSetEntityMotion", exception);
        }
    }
}
