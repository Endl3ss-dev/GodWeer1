package swerve.mixins.client;

import swerve.cosmetics.Wearables;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Гардероб: служебная строка синхронизации не показывается в чате.
 */
@Mixin(ChatComponent.class)
public class MixinChatComponent {

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;"
            + "Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            at = @At("HEAD"), cancellable = true)
    private void swerve$hideSync(Component message, MessageSignature signature, GuiMessageTag tag, CallbackInfo ci) {
        if (message == null) {
            return;
        }
        try {
            if (message.getString().contains(Wearables.MARKER)) {
                ci.cancel();
            }
        } catch (RuntimeException ignored) {
        }
    }
}
