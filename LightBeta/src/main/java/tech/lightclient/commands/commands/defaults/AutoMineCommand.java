package tech.lightclient.commands.commands.defaults;

import tech.lightclient.LightClient;
import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.impl.player.AutoMine;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AutoMineCommand extends Command {
    public AutoMineCommand() {
        super("automine");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        AutoMine autoMine = LightClient.getFunctionManager().getAutoMine();
        if (autoMine == null) return;

        if (!args.hasAny()) return;
        String action = args.getString().toLowerCase(Locale.US);
        if (action.equals("pos1")) {
            autoMine.setPos1(mc.player.getBlockPos());
            send("pos1 установлена");
        } else if (action.equals("pos2")) {
            autoMine.setPos2(mc.player.getBlockPos());
            send("pos2 установлена");
        }
    }

    private void send(String text) {
        Function.sendMessage(text);
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            String arg = args.getString();
            return Stream.of("pos1", "pos2").filter(s -> s.startsWith(arg.toLowerCase(Locale.US)));
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Настройка области AutoMine";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                ".automine pos1 - ставит первую точку по текущей позиции",
                ".automine pos2 - ставит вторую точку по текущей позиции"
        );
    }
}

