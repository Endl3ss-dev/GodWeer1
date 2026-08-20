package minced.core;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.text.Text;
import minced.enums.HEADLESS;

import java.util.List;

/**
 * Client-side command for managing the independent bot sessions.
 */
public final class CommandBot extends ClientCommand {
    public CommandBot() {
        super("bot", "bots");
    }

    @Override
    public void run(LiteralArgumentBuilder root) {
        root.executes(context -> showHelp());
        root.then(literal("help").executes(context -> showHelp()));
        root.then(literal("list").executes(context -> listBots()));
        root.then(literal("main").executes(context -> returnToMainView()));
        root.then(literal("disconnect-all").executes(context -> disconnectAll()));

        root.then(literal("connect")
                .then(argument("name")
                        .then(argument("address")
                                .executes(context -> connect(
                                        StringArgumentType.getString(context, "name"),
                                        StringArgumentType.getString(context, "address"),
                                        HEADLESS.RENDERABLE))
                                .then(argument("mode")
                                        .executes(context -> connect(
                                                StringArgumentType.getString(context, "name"),
                                                StringArgumentType.getString(context, "address"),
                                                HEADLESS.method174(StringArgumentType.getString(context, "mode"))))))));

        root.then(literal("disconnect")
                .then(argument("name").executes(context ->
                        disconnect(StringArgumentType.getString(context, "name")))));

        root.then(literal("reconnect")
                .then(argument("name").executes(context ->
                        reconnect(StringArgumentType.getString(context, "name")))));

        root.then(literal("view")
                .then(argument("name").executes(context ->
                        view(StringArgumentType.getString(context, "name")))));

        root.then(literal("status")
                .then(argument("name").executes(context ->
                        status(StringArgumentType.getString(context, "name")))));

        root.then(literal("send")
                .then(argument("name")
                        .then(com.mojang.brigadier.builder.RequiredArgumentBuilder
                                .argument("command", StringArgumentType.greedyString())
                                .executes(context -> send(
                                        StringArgumentType.getString(context, "name"),
                                        StringArgumentType.getString(context, "command"))))));
    }

    private static LiteralArgumentBuilder literal(String name) {
        return ClientCommand.method420(name);
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder argument(String name) {
        return ClientCommand.method417(name, StringArgumentType.word());
    }

    private static BotManager manager() {
        return ServiceRegistry.method2116();
    }

    private static int showHelp() {
        message("§d.bot connect <ник> <ip> [renderable|headless] §7— подключить бота");
        message("§d.bot list §7— список ботов");
        message("§d.bot status <ник> §7— состояние бота");
        message("§d.bot view <ник> §7— переключиться на бота");
        message("§d.bot main §7— вернуться к основному игроку");
        message("§d.bot send <ник> </команда> §7— выполнить команду от бота");
        message("§d.bot reconnect <ник> §7— переподключить бота");
        message("§d.bot disconnect <ник> §7— отключить бота");
        message("§d.bot disconnect-all §7— отключить всех ботов");
        return 1;
    }

    private static int listBots() {
        BotManager manager = manager();
        if (manager == null) {
            return error("Сервис ботов не запущен");
        }
        List<BotSession> bots = manager.method2145();
        if (bots.isEmpty()) {
            message("§7Подключённых ботов нет");
            return 1;
        }
        message("§dБоты (" + bots.size() + "):");
        for (BotSession bot : bots) {
            message(" §f" + bot.method62() + " §7— " + bot.method67());
        }
        return 1;
    }

    private static int connect(String name, String address, HEADLESS mode) {
        if (mode == null) {
            return error("Режим должен быть renderable или headless");
        }
        BotManager manager = manager();
        if (manager == null) {
            return error("Сервис ботов не запущен");
        }
        String failure = manager.method2181(name, address, mode);
        if (failure != null) {
            return error(failure);
        }
        message("§aПодключаю §f" + name + " §aк §f" + address + " §7(" + mode.method173() + ")");
        return 1;
    }

    private static int disconnect(String name) {
        BotManager manager = manager();
        if (manager == null || !manager.method2143(name, "Disconnected by .bot command")) {
            return error("Бот " + name + " не найден");
        }
        message("§aБот §f" + name + " §aотключён");
        return 1;
    }

    private static int disconnectAll() {
        BotManager manager = manager();
        if (manager == null) {
            return error("Сервис ботов не запущен");
        }
        int count = manager.method2145().size();
        manager.method2129("Disconnected by .bot command");
        message("§aОтключено ботов: §f" + count);
        return 1;
    }

    private static int reconnect(String name) {
        BotManager manager = manager();
        if (manager == null) {
            return error("Сервис ботов не запущен");
        }
        String failure = manager.method2132(name);
        if (failure != null) {
            return error(failure);
        }
        message("§aПереподключаю бота §f" + name);
        return 1;
    }

    private static int view(String name) {
        BotManager manager = manager();
        if (manager == null) {
            return error("Сервис ботов не запущен");
        }
        String failure = manager.method2151(name);
        if (failure != null) {
            return error(failure);
        }
        message("§aПереключение на бота §f" + name);
        return 1;
    }

    private static int returnToMainView() {
        BotManager manager = manager();
        if (manager == null || !manager.method2144()) {
            return error("Сейчас просмотр бота не активен");
        }
        message("§aВозврат к основному игроку");
        return 1;
    }

    private static int status(String name) {
        BotManager manager = manager();
        BotSession bot = manager == null ? null : manager.method2126(name);
        if (bot == null) {
            return error("Бот " + name + " не найден");
        }
        message("§d" + bot.method62() + "§7: " + bot.method67()
                + (bot.method66() == null ? "" : " §8| §7" + bot.method66()));
        return 1;
    }

    private static int send(String name, String command) {
        BotManager manager = manager();
        if (manager == null || !manager.method2170(name, command)) {
            return error("Бот " + name + " не найден");
        }
        message("§aОтправлено от §f" + name + "§a: §7" + command);
        return 1;
    }

    private static int error(String text) {
        message("§c" + text);
        return 0;
    }

    private static void message(String text) {
        ChatUtil.method3308(Text.literal(text), new Object[0]);
    }
}
