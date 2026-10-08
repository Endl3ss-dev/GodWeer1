package never.lose.cmd;

import never.lose.managers.ModuleManager;
import never.lose.modules.api.Module;
import never.lose.utils.client.ClientChat;

import java.util.List;
import java.util.Locale;

public final class BindManager {

    private BindManager() {
    }

    public static void execute(String[] args) {
        if (args.length == 0) {
            usage();
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add" -> add(shift(args));
            case "del", "delete", "remove" -> delete(shift(args));
            case "clear" -> clear();
            case "list", "ls" -> list();
            default -> usage();
        }
    }

    private static void add(String[] args) {
        if (args.length < 2) {
            ClientChat.message("Использование: bind add [кнопка] [модуль]");
            return;
        }

        int key = never.lose.managers.BindManager.parseKey(args[0]);
        Module module = findModule(join(args, 1));

        if (module == null && args.length >= 2) {
            Module swapped = findModule(args[0]);
            int swappedKey = never.lose.managers.BindManager.parseKey(join(args, 1));
            if (swapped != null && swappedKey != -1) {
                module = swapped;
                key = swappedKey;
            }
        }

        if (key == -1) {
            ClientChat.message("Клавиша [" + args[0] + "] не найдена(");
            return;
        }
        if (module == null) {
            ClientChat.message("Модуль [" + join(args, 1) + "] не найден(");
            return;
        }

        never.lose.managers.BindManager.setBind(module, key);
        ClientChat.message("Бинд был успешно добавлен!");
    }

    private static void delete(String[] args) {
        if (args.length == 0) {
            ClientChat.message("Использование: bind del [модуль]");
            return;
        }
        String name = join(args, 0);
        Module module = findModule(name);
        if (module == null) {
            ClientChat.message("Модуль [" + name + "] не найден(");
            return;
        }
        never.lose.managers.BindManager.removeBind(module);
        ClientChat.message("Бинд у модуля [" + module.getName() + "] был успешно удален!");
    }

    private static void clear() {
        never.lose.managers.BindManager.clearAll();
        ClientChat.message("Список биндов был очищен");
    }

    private static void list() {
        ClientChat.message("Вот полный список биндов:");
        List<never.lose.managers.BindManager.BindEntry> binds = never.lose.managers.BindManager.list();
        if (binds.isEmpty()) {
            ClientChat.raw("Список пуст", ClientChat.MUTED);
            return;
        }
        for (never.lose.managers.BindManager.BindEntry entry : binds) {
            ClientChat.item(entry.module(), entry.key());
        }
    }

    private static void usage() {
        ClientChat.message("Использование: bind add/del/clear/list");
    }

    private static Module findModule(String name) {
        return ModuleManager.get().get(name);
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
