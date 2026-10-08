package never.lose.cmd;

import never.lose.utils.client.ClientChat;

import java.util.Arrays;
import java.util.Locale;

public final class CommandManager {

    private CommandManager() {
    }

    private static String lastMessage;
    private static long lastStamp;

    public static boolean dispatch(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }
        String prefix = ClientPrefixManager.getPrefix();
        if (prefix == null || prefix.isEmpty() || !message.startsWith(prefix)) {
            return false;
        }

        long now = System.currentTimeMillis();
        if (message.equals(lastMessage) && now - lastStamp < 75L) {
            return true;
        }
        lastMessage = message;
        lastStamp = now;

        String body = message.substring(prefix.length()).trim();
        if (body.isEmpty()) {
            help();
            return true;
        }

        String[] split = body.split("\\s+");
        String name = split[0].toLowerCase(Locale.ROOT);
        String[] args = split.length == 1 ? new String[0] : Arrays.copyOfRange(split, 1, split.length);

        switch (name) {
            case "bind", "binds" -> BindManager.execute(args);
            case "cfg", "config", "configs" -> CfgManager.execute(args);
            case "friend", "friends" -> FriendManager.execute(args);
            case "prefix" -> ClientPrefixManager.execute(args);
            case "creator", "creators", "credits" -> creator();
            case "help", "?" -> help();
            default -> ClientChat.message("Неизвестная команда. Доступны: bind, cfg, friend, prefix, creator");
        }
        return true;
    }

    public static void help() {
        String prefix = ClientPrefixManager.getPrefix();
        ClientChat.message("Команды клиента:");
        ClientChat.raw(prefix + "bind add/del/clear/list", ClientChat.MUTED);
        ClientChat.raw(prefix + "cfg add/del/load/folder/auto-save/list", ClientChat.MUTED);
        ClientChat.raw(prefix + "friend add/del/list", ClientChat.MUTED);
        ClientChat.raw(prefix + "prefix [новая кнопка]", ClientChat.MUTED);
        ClientChat.raw(prefix + "creator", ClientChat.MUTED);
    }

    public static void creator() {
        ClientChat.raw("Создатели:", ClientChat.ACCENT);
        ClientChat.raw("SWeltek <3", ClientChat.TEXT);
        ClientChat.raw("SWearweLL", ClientChat.TEXT);
        ClientChat.raw("gostler", ClientChat.TEXT);
        ClientChat.raw("~ Идея свелтика", ClientChat.MUTED);
    }
}
