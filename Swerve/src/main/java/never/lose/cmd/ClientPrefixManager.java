package never.lose.cmd;

import never.lose.utils.client.ClientChat;

public final class ClientPrefixManager {

    private ClientPrefixManager() {
    }

    public static String getPrefix() {
        return never.lose.managers.ClientPrefixManager.getPrefix();
    }

    public static boolean setPrefix(String value) {
        return never.lose.managers.ClientPrefixManager.setPrefix(value);
    }

    public static void execute(String[] args) {
        if (args.length == 0) {
            ClientChat.message("Текущий префикс клиента: [" + getPrefix() + "]");
            ClientChat.message("Использование: prefix [новая кнопка]");
            return;
        }

        String next = String.join("", args).trim();
        if (next.length() > 3) {
            ClientChat.message("Префикс слишком длинный, максимум 3 символа(");
            return;
        }
        if (!setPrefix(next)) {
            ClientChat.message("Не удалось изменить префикс(");
            return;
        }
        ClientChat.message("Префикс клиента изменен на [" + getPrefix() + "], не забудьте)");
    }
}