package tech.lightclient.mixins.client;


import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.EventChat;
import tech.lightclient.commands.commands.CommandDispatcher;
import tech.lightclient.modules.impl.player.PlayerUtilsFunction;
import tech.lightclient.modules.impl.utils.ClientHide;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.EntityPosition;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Redirect(
            method = "onPlayerPositionLook",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/s2c/play/PlayerPositionLookS2CPacket;change()Lnet/minecraft/entity/EntityPosition;"
            )
    )
    private EntityPosition handleNoServerRotation(PlayerPositionLookS2CPacket packet) {
        EntityPosition originalChange = packet.change();
        var player = mc.player;
        if (player != null && PlayerUtilsFunction.NoServerRotation.get() && LightClient.getFunctionManager().getPlayerUtilsFunction().isState()) {
            float newYaw = packet.relatives().contains(PositionFlag.Y_ROT)
                    ? 0
                    : player.getYaw();
            float newPitch = packet.relatives().contains(PositionFlag.X_ROT)
                    ? 0
                    : player.getPitch();
            return new EntityPosition(
                    originalChange.position(),
                    originalChange.deltaMovement(),
                    newYaw,
                    newPitch
            );
        }
        return originalChange;
    }

    @Inject(method = "onPlayerList", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), cancellable = true)
    public void antiIngoringPlayerInfoSelfcode(PlayerListS2CPacket packet, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void light$sendChatMessage(String message, CallbackInfo ci) {
        EventChat eventChat = new EventChat(message);
        LightClient.getEventBus().post(eventChat);

        if (eventChat.isCancelled()) {
            ci.cancel();
            return;
        }

        boolean codeMatch = message.equals(ClientHide.getCode());

        if (codeMatch && (LightClient.isHide() || ClientHide.unhooked)) {
            ci.cancel();

            ClientHide clientHide = LightClient.getFunctionManager().getClientHide();
            if (clientHide != null) {
                clientHide.requestRestoreOnDisable();
                clientHide.restoreHide();
            } else {
                ClientHide.unhooked = false;
                LightClient.setHide(false);
            }

            return;
        }

        if (message.startsWith(CommandDispatcher.prefix) && !LightClient.isHide()) {
            ci.cancel();
        }
    }
}
