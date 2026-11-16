package tech.lightclient.commands.commands.defaults;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.client.utils.config.StaffStorage;
import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.datatypes.StaffDataType;
import tech.lightclient.commands.datatypes.TabPlayerDataType;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.commands.helpers.Paginator;
import tech.lightclient.commands.helpers.TabCompleteHelper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static tech.lightclient.commands.IChatControl.FORCE_COMMAND_PREFIX;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffCommand extends Command implements ILogger {
    final StaffStorage staffRepository;

    protected StaffCommand() {
        super("staff");
        staffRepository = LightClient.getStaffStorage();
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String arg = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        switch (arg) {
            case "add" -> handleAddStaff(args);
            case "remove" -> handleRemove(args);
            case "clear" -> handleClearStaffs(args);
            case "list" -> handleList(label, args);
        }
    }

    void handleAddStaff(IArgConsumer args) throws CommandException {
        args.requireMin(1);
        String name = args.getString();

        if (staffRepository.exists(name)) {
            logDirect("Этот модератор уже есть РІ списке!", Formatting.RED);
            return;
        }

        logDirect("Добавлен модератор " + name, Formatting.GRAY);
        staffRepository.add(name);
    }
    
    void handleRemove(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        String name = args.getString();
        if (staffRepository.exists(name)) {
            logDirect(Formatting.GREEN + "Модератор " + Formatting.RED + name + Formatting.GREEN + " был убран из списка!");
            staffRepository.remove(name);
        } else {
            logDirect("Модератор СЃ никнеймом '" + name + "' не найден!");
        }
    }
    
    void handleList(String label, IArgConsumer args) throws CommandException {
        args.requireMax(1);

        Paginator.paginate(
                args, new Paginator<>(staffRepository.getStaffs()),
                () -> logDirect("Список модераторов:"),
                staff -> {
                    String names = staff.getName();
                    MutableText namesComponent = Text.literal(names);
                    namesComponent.setStyle(namesComponent.getStyle().withColor(Formatting.WHITE));
                    return namesComponent;
                },
                FORCE_COMMAND_PREFIX + label
        );
    }
    
    void handleClearStaffs(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        staffRepository.clear();
        logDirect(Formatting.GREEN + "Все ники модераторов были удалены.");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            String arg = args.getString();
            if (args.hasExactlyOne()) {
                if (arg.equalsIgnoreCase("add")) {
                    return args.tabCompleteDatatype(TabPlayerDataType.INSTANCE);
                } else if (arg.equalsIgnoreCase("remove")) {
                    return args.tabCompleteDatatype(StaffDataType.INSTANCE);
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
        return "Позволяет добавлять модераторов РІ список модерации";
    }
    
    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "РЎ помощью этой команды можно добавлять/удалять модераторов РІ список",
                "",
                "РСЃРїРѕР»СЊР·РѕРІР°РЅРёРµ:",
                "> staff add <name> - Добавляет модератора",
                "> staff remove <name> - Удаляет модератора из списка",
                "> staff list - Возвращает список никнеймов модераторов",
                "> staff clear - Очищает список модераторов."
        );
    }
}

