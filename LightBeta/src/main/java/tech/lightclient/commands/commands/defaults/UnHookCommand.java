package tech.lightclient.commands.commands.defaults;

import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.exception.CommandException;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
public class UnHookCommand extends Command {
    public UnHookCommand() {
        super("unhook");
    }
    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String action = args.hasAny() ? args.getString().toLowerCase(Locale.ROOT) : "status";
        switch (action) {
            case "switch" -> {
                args.requireMax(0);
            }
            case "status" -> {
                args.requireMax(0);
            }
            case "target" -> {
                if (!args.hasAny()) {
                    return;
                }
                String target = args.getString();
                args.requireMax(0);
            }
            case "stop" -> {
                args.requireMax(0);
            }
            default -> printUsage();
        }
    }
    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasExactlyOne()) {
            String prefix = args.peekString(0).toLowerCase(Locale.ROOT);
            return Stream.of("status", "switch", "target", "stop")
                    .filter(s -> s.startsWith(prefix))
                    .sorted();
        }
        if (args.hasExactly(2) && "target".equalsIgnoreCase(args.peekString(0))) {
            String prefix = args.peekString(1).toLowerCase(Locale.ROOT);
            return Stream.of("last")
                    .filter(s -> s.startsWith(prefix))
                    .sorted();
        }
        return Stream.empty();
    }
    @Override
    public String getShortDesc() {
        return "Управление режимом UnHook.";
    }
    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Прокси работает в отдельном процессе, переживает закрытие клиента.",
                "",
                "> unhook status              — статус прокси-процесса",
                "> unhook switch              — переключить управление между клиентами",
                "> unhook target <host:port>  — задать цель",
                "> unhook stop                — остановить прокси + сервер"
        );
    }
    private void printUsage() {
        logDirect(Formatting.WHITE + "unhook status / switch / target <addr> / stop");
    }
}
