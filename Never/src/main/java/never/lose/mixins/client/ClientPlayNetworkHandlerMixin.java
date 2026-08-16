package never.lose.mixins.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import never.lose.cmd.CommandManager;
import never.lose.modules.impl.Render.Ambience;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void neverlose$clientCommands(String content, CallbackInfo ci) {
        if (CommandManager.dispatch(content)) {
            ci.cancel();
        }
    }

    @Inject(method = "onWorldTimeUpdate", at = @At("TAIL"))
    private void endless$overrideWorldTime(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        Ambience.applyTimeOfDay();
    }
}