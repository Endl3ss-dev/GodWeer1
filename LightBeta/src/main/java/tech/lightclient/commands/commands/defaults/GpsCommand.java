package tech.lightclient.commands.commands.defaults;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.client.utils.config.GPSStorage;
import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.datatypes.GpsDataType;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.commands.helpers.Paginator;
import tech.lightclient.commands.helpers.TabCompleteHelper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static tech.lightclient.commands.IChatControl.FORCE_COMMAND_PREFIX;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GpsCommand extends Command implements ILogger {
    final GPSStorage wayRepository;

    protected GpsCommand() {
        super("gps");
        wayRepository = LightClient.getGpsStorage();
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String arg = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        switch (arg) {
            case "zamok" -> handleZamok();
            case "add" -> handleAddWay(args);
            case "remove" -> handleRemoveWay(args);
            case "clear" -> handleClearWays(args);
            case "list" -> handleListWays(label, args);
        }
    }

    private void handleZamok() {
        wayRepository.addOrUpdate("zamok", 0, 100, 0);
        logDirect("GPS zamok: (0, 100, 0)", Formatting.GREEN);
    }

    void handleAddWay(IArgConsumer args) throws CommandException {
        args.requireMin(3);
        String name = args.getString();
        int x = args.getArgs().get(0).getAs(Integer.class);
        int y = args.getArgs().get(1).getAs(Integer.class);
        int z = args.getArgs().get(2).getAs(Integer.class);

        if (wayRepository.exists(name)) {
            logDirect("Метка СЃ таким именем уже есть РІ списке!", Formatting.RED);
            return;
        }

        logDirect("Добавлена метка " + name + ", Координаты:" + " (" + x + ", " + y + ", " + z + ")", Formatting.GRAY);
        wayRepository.add(name, x, y, z);
    }

    
    void handleRemoveWay(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        String name = args.getString();
        if (wayRepository.exists(name)) {
            wayRepository.remove(name);
            logDirect(Formatting.GREEN + "Метка " + Formatting.RED + name + Formatting.GREEN + " была успешна удалена!");
        } else logDirect("Метка СЃ названием '" + name + "' не найдена!");
    }
    
    void handleListWays(String label, IArgConsumer args) throws CommandException {
        args.requireMax(1);
        Paginator.paginate(args, new Paginator<>(wayRepository.getPoints()),
                () -> logDirect("Список меток:"),
                way -> Text.literal(Formatting.GRAY + "Название: " + Formatting.RED + way.getName())
                        .append(Text.literal(Formatting.GRAY + " Координаты: " + Formatting.WHITE + " (" + way.getX() + ", " + way.getY() + ", " + way.getZ() + ")")
                                ), FORCE_COMMAND_PREFIX + label);
    }
    
    void handleClearWays(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        wayRepository.clear();
        logDirect(Formatting.GREEN + "Все метки были удалены.");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            String arg = args.getString();
            if (arg.equalsIgnoreCase("remove")) {
                if (args.hasExactlyOne()) return args.tabCompleteDatatype(GpsDataType.INSTANCE);
            } else if (arg.equalsIgnoreCase("add")) {
                String string = args.has(5) ? "" : args.has(4) ? "z" : args.has(3) ? "y" : args.has(2) ? "x" : "Название";
                return new TabCompleteHelper().sortAlphabetically().prepend(string).stream();
            } else {
                return new TabCompleteHelper().sortAlphabetically().prepend("add", "remove", "list", "clear", "zamok").filterPrefix(arg).stream();
            }
        }
        return Stream.empty();
    }


    @Override
    public String getShortDesc() {
        return "Позволяет ставить метки РІ мире";
    }

    
    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "РЎ помощью этой команды можно добавлять/удалять метки РІ мире",
                "",
                "РСЃРїРѕР»СЊР·РѕРІР°РЅРёРµ:",
                "> way add <name> <x> <y> <z> - Добавляет метку",
                "> way remove <name> - Удаляет метку",
                "> way list - Возвращает список меток",
                "> way clear - Очищает список меток."
        );
    }
}

