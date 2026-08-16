package never.lose.events.client;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import never.lose.cmd.CommandManager;
import never.lose.managers.ClientPrefixManager;
import never.lose.managers.ModuleManager;
import never.lose.modules.impl.Misc.ChatPlus;

public final class ClientChatListener {

    private ClientChatListener() {}

    public static void register() {
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> !CommandManager.dispatch(message));
        ClientSendMessageEvents.ALLOW_COMMAND.register(command -> {
            String prefix = ClientPrefixManager.getPrefix();
            return !"/".equals(prefix) || !CommandManager.dispatch("/" + command);
        });
        ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, timestamp) -> {
            if (message != null) {
                ModuleManager.get().dispatchChat(message.getString());
            }
        });
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (message != null) {
                ModuleManager.get().dispatchChat(message.getString());
            }
        });
        ClientSendMessageEvents.MODIFY_CHAT.register(message -> {
            ChatPlus chatPlus = ModuleManager.get().get(ChatPlus.class);
            if (chatPlus == null || !chatPlus.isEnabled() || message == null) {
                return message;
            }
            return chatPlus.processOutgoing(message);
        });
    }
}
