package tech.lightclient.commands.commands.defaults;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.config.FriendStorage;
import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.datatypes.FriendDataType;
import tech.lightclient.commands.datatypes.TabPlayerDataType;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.commands.helpers.Paginator;
import tech.lightclient.commands.helpers.TabCompleteHelper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static tech.lightclient.commands.IChatControl.FORCE_COMMAND_PREFIX;

public class FriendCommand extends Command {
    final FriendStorage friendRepository;

    protected FriendCommand() {
        super("friend");
        friendRepository = LightClient.getFriendStorage();
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String arg = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        switch (arg) {
            case "add" -> handleAddFriend(args);
            case "remove" -> handleRemove(args);
            case "clear" -> handleClear(args);
            case "list" -> handleList(label, args);
        }
    }

    void handleAddFriend(IArgConsumer args) throws CommandException {
        args.requireMin(1);
        String name = args.getString();

        if (friendRepository.exists(name)) {
            logDirect("Этот ник уже есть РІ списке!", Formatting.RED);
            return;
        }

        logDirect("Добавлен ник " + name, Formatting.GRAY);
        friendRepository.add(name);
    }

    void handleRemove(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        String name = args.getString();
        if (friendRepository.exists(name)) {
            logDirect(Formatting.GREEN + "Ник " + Formatting.RED + name + Formatting.GREEN + " был убран из списка!");
            friendRepository.remove(name);
        } else {
            logDirect("РРіСЂРѕРє СЃ никнеймом '" + name + "' не найден!");
        }
    }

    void handleList(String label, IArgConsumer args) throws CommandException {
        args.requireMax(1);

        Paginator.paginate(
                args, new Paginator<>(friendRepository.getFriends()),
                () -> logDirect("Список никнеймов друзей:"),
                friend -> {
                    String names = friend.getName();
                    MutableText namesComponent = Text.literal(names);
                    namesComponent.setStyle(namesComponent.getStyle().withColor(Formatting.WHITE));
                    return namesComponent;
                },
                FORCE_COMMAND_PREFIX + label
        );
    }

    void handleClear(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        friendRepository.clear();
        logDirect(Formatting.GREEN + "Все ники друзей были удалены.");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            String arg = args.getString();
            if (args.hasExactlyOne()) {
                if (arg.equalsIgnoreCase("add")) {
                    return args.tabCompleteDatatype(TabPlayerDataType.INSTANCE);
                } else if (arg.equalsIgnoreCase("remove")) {
                    return args.tabCompleteDatatype(FriendDataType.INSTANCE);
                }
            } else {
                return new TabCompleteHelper()
                        .sortAlphabetically()
                        .prepend("add", "remove", "list", "clear")
                        .filterPrefix(arg)
                        .stream();
            }
        }
        return Stream.empty();
    }
    
    @Override
    public String getShortDesc() {
        return "Позволяет управлять списком друзей";
    }

    
    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "РЎ помощью этой команды можно добавлять/удалять друзей РІ список",
                "",
                "РСЃРїРѕР»СЊР·РѕРІР°РЅРёРµ:",
                "> friend add <name> - Добавляет имя РІ список друзей.",
                "> friend remove <name> - Удаляет имя из списка друзей.",
                "> friend list - Возвращает список друзей",
                "> friend clear - Очищает список друзей."
        );
    }
}

