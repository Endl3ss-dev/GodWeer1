package tech.lightclient.commands.commands.defaults;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.client.utils.player.TpGrim;
import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.exception.CommandException;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class TpCommand extends Command implements ILogger {

    private static TpGrim tpGrim;

    public TpCommand() {
        super("tp", "tpgrim");
    }

    private TpGrim getTpGrim() {
        if (tpGrim == null) {
            tpGrim = new TpGrim(
                    msg -> logDirect(msg, Formatting.GRAY),
                    msg -> logDirect(msg, Formatting.GREEN),
                    msg -> logDirect(msg, Formatting.RED)
            );
            LightClient.getEventBus().subscribe(new Object() {
                @tech.lightclient.client.utils.eventbus.EventHandler
                public void onTick(tech.lightclient.client.events.impl.client.UpdateEvent event) {
                    if (mc.player != null && mc.world != null) {
                        tpGrim.tick(mc);
                    }
                }
            });
        }
        return tpGrim;
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (!args.hasAny()) {
            showHelp();
            return;
        }

        String action = args.getString().toLowerCase(Locale.US);
        TpGrim tp = getTpGrim();

        switch (action) {
            case "stop" -> {
                tp.stop();
            }
            case "player" -> {
                if (!args.hasAny()) {
                    logDirect("Использование: .tp player <имя>", Formatting.RED);
                    return;
                }
                String name = args.getString();
                tp.startToPlayer(name);
            }
            default -> {
                try {
                    double x = Double.parseDouble(action);
                    if (!args.hasAny()) {
                        logDirect("Использование: .tp <x> <y> <z>", Formatting.RED);
                        return;
                    }
                    double y = Double.parseDouble(args.getString());
                    if (!args.hasAny()) {
                        logDirect("Использование: .tp <x> <y> <z>", Formatting.RED);
                        return;
                    }
                    double z = Double.parseDouble(args.getString());
                    tp.start(x, y, z);
                } catch (NumberFormatException e) {
                    logDirect("Неверный формат. Использование: .tp <x> <y> <z> | .tp player <имя> | .tp stop", Formatting.RED);
                }
            }
        }
    }

    private void showHelp() {
        logDirect("TpGrim — обход Grim для телепортации", Formatting.GRAY);
        logDirect(".tp <x> <y> <z> — ТП на координаты", Formatting.GRAY);
        logDirect(".tp player <имя> — ТП к игроку", Formatting.GRAY);
        logDirect(".tp stop — остановить ТП", Formatting.GRAY);
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (!args.hasAny()) {
            return Stream.of("stop", "player");
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Обход Grim для телепортации";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "TpGrim — телепортация с обходом Grim.",
                "",
                ".tp <x> <y> <z> — ТП на координаты",
                ".tp player <имя> — ТП к игроку",
                ".tp stop — остановить ТП"
        );
    }
}
