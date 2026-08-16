package never.lose.cmd;

import never.lose.managers.FriendManager.FriendEntry;
import never.lose.utils.client.ClientChat;

import java.util.List;
import java.util.Locale;

public final class FriendManager {

    private FriendManager() {
    }

    public static void execute(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add" -> add(join(args, 1));
            case "del", "delete", "remove" -> delete(join(args, 1));
            case "list", "ls" -> list();
            default -> usage();
        }
    }

    private static void add(String name) {
        if (name.isBlank()) {
            ClientChat.message("Использование: friend add [ник игрока]");
            return;
        }
        String nick = name.trim();
        if (never.lose.managers.FriendManager.isFriend(nick)) {
            ClientChat.message("Игрок с ником [" + nick + "] уже в списке друзей");
            return;
        }
        if (never.lose.managers.FriendManager.addFriend(nick)) {
            ClientChat.message("Игрок с ником [" + nick + "] был добавлен в друзья клиента!");
            return;
        }
        ClientChat.message("Не удалось добавить игрока [" + nick + "](");
    }

    private static void delete(String name) {
        if (name.isBlank()) {
            ClientChat.message("Использование: friend del [ник игрока]");
            return;
        }
        String nick = name.trim();
        if (never.lose.managers.FriendManager.removeFriend(nick)) {
            ClientChat.message("Игрок с ником [" + nick + "] был удален из списка друзей клиента(");
            return;
        }
        ClientChat.message("Игрок с ником [" + nick + "] не найден в списке друзей(");
    }

    private static void list() {
        ClientChat.message("Список ваших друзей:");
        List<FriendEntry> friends = never.lose.managers.FriendManager.getFriends();
        if (friends.isEmpty()) {
            ClientChat.raw("Список пуст", ClientChat.MUTED);
            return;
        }
        for (FriendEntry entry : friends) {
            ClientChat.item(entry.date(), entry.name());
        }
    }

    private static void usage() {
        ClientChat.message("Использование: friend add/del/list");
    }

    private static String join(String[] args, int from) {
        if (from >= args.length) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = from; i < args.length; i++) {
            if (i > from) {
                builder.append(' ');
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }
}
