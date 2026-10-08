package never.lose.cmd;

import never.lose.utils.client.ClientChat;

import java.util.List;
import java.util.Locale;

public final class CfgManager {

    private CfgManager() {
    }

    public static void execute(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add", "save", "create" -> add(join(args, 1));
            case "del", "delete", "remove" -> delete(join(args, 1));
            case "load", "select" -> load(join(args, 1));
            case "folder", "dir", "open" -> never.lose.managers.CfgManager.openFolder();
            case "auto-save", "autosave", "auto_save" -> autoSave(shift(args));
            case "list", "ls" -> list();
            default -> usage();
        }
    }

    private static void add(String name) {
        if (name.isBlank()) {
            ClientChat.message("Использование: cfg add [название]");
            return;
        }
        if (never.lose.managers.CfgManager.save(name)) {
            ClientChat.message("Кфг с названием [" + name.trim() + "], был успешно добавлен!");
            return;
        }
        ClientChat.message("Не удалось сохранить кфг [" + name.trim() + "](");
    }

    private static void delete(String name) {
        if (name.isBlank()) {
            ClientChat.message("Использование: cfg del [название]");
            return;
        }
        if (never.lose.managers.CfgManager.delete(name)) {
            ClientChat.message("Кфг был удален!(");
            return;
        }
        ClientChat.message("Кфг с названием [" + name.trim() + "] не найден(");
    }

    private static void load(String name) {
        if (name.isBlank()) {
            ClientChat.message("Использование: cfg load [название]");
            return;
        }
        if (never.lose.managers.CfgManager.load(name)) {
            ClientChat.message("Кфг с названием [" + name.trim() + "], был успешно загружен!");
            return;
        }
        ClientChat.message("Кфг с названием [" + name.trim() + "] не найден(");
    }

    private static void autoSave(String[] args) {
        Boolean value = args.length == 0 ? !never.lose.managers.CfgManager.isAutoSave() : parseToggle(args[0]);
        if (value == null) {
            ClientChat.message("Использование: cfg auto-save on/off");
            return;
        }
        never.lose.managers.CfgManager.setAutoSave(value);
        ClientChat.message("AutoSaveCfg " + (value ? "Включен" : "Выключен"));
    }

    private static void list() {
        ClientChat.message("Вот полный список кфг:");
        List<never.lose.managers.CfgManager.ConfigEntry> configs = never.lose.managers.CfgManager.list();
        if (configs.isEmpty()) {
            ClientChat.raw("Список пуст", ClientChat.MUTED);
            return;
        }
        for (never.lose.managers.CfgManager.ConfigEntry entry : configs) {
            ClientChat.item(entry.date(), entry.name());
        }
    }

    private static Boolean parseToggle(String raw) {
        String value = raw.toLowerCase(Locale.ROOT);
        return switch (value) {
            case "on", "true", "1", "enable", "enabled", "вкл", "включить" -> true;
            case "off", "false", "0", "disable", "disabled", "выкл", "выключить" -> false;
            default -> null;
        };
    }

    private static void usage() {
        ClientChat.message("Использование: cfg add/del/load/folder/auto-save/list");
    }

    private static String[] shift(String[] args) {
        if (args.length <= 1) {
            return new String[0];
        }
        String[] next = new String[args.length - 1];
        System.arraycopy(args, 1, next, 0, next.length);
        return next;
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
