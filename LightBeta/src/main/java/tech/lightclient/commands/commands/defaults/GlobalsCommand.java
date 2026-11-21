package tech.lightclient.commands.commands.defaults;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.globals.GlobalsManager;
import tech.lightclient.client.utils.globals.GlobalsMember;
import tech.lightclient.client.utils.globals.GlobalsParty;
import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.modules.impl.utils.Globals;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class GlobalsCommand extends Command {
    protected GlobalsCommand() {
        super("globals");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (!args.hasAny()) {
            help();
            return;
        }
        Globals module = LightClient.getFunctionManager().getGlobals();
        if (module != null && !module.isState()) {
            module.setState(true);
        }
        GlobalsManager manager = Globals.manager();
        String action = args.getString().toLowerCase(Locale.US);
        switch (action) {
            case "party", "patry" -> party(args, manager);
            case "join" -> {
                if (!args.hasAny()) {
                    logDirect(Formatting.RED + ".globals join <код>");
                    return;
                }
                manager.joinParty(args.getString());
                logDirect(Formatting.GRAY + "запрос на вступление отправлен");
            }
            case "accept" -> manager.accept(args.getArgs().get(0).getAs(Integer.class));
            case "reject" -> manager.reject(args.getArgs().get(0).getAs(Integer.class));
            case "point" -> manager.createPoint();
            default -> help();
        }
    }

    private void party(IArgConsumer args, GlobalsManager manager) throws CommandException {
        if (!args.hasAny()) {
            help();
            return;
        }
        String action = args.getString().toLowerCase(Locale.US);
        switch (action) {
            case "create" -> manager.createParty();
            case "disband" -> manager.disbandParty();
            case "list" -> {
                manager.requestList();
                GlobalsParty party = manager.getParty();
                if (party == null) {
                    logDirect(Formatting.RED + "Вы не состоите РІ группе");
                    return;
                }
                logDirect(Formatting.GREEN + "Globals группа " + Formatting.AQUA + party.code());
                for (GlobalsMember member : party.members()) {
                    logDirect(Formatting.GRAY + "- " + member.username() + " / " + member.minecraftName());
                }
            }
            default -> help();
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasExactlyOne()) {
            String prefix = args.getString().toLowerCase(Locale.US);
            return Stream.of("party", "join", "point").filter(v -> v.startsWith(prefix));
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Globals группы";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Система Globals-групп",
                ".globals party create - создать код группы",
                ".globals join <код> - запросить вступление",
                ".globals party disband - расформировать группу",
                ".globals patry list - список участников",
                ".globals point - отправить общую точку"
        );
    }

    private void help() {
        logDirect(Formatting.GRAY + ".globals party create");
        logDirect(Formatting.GRAY + ".globals join <код>");
        logDirect(Formatting.GRAY + ".globals party disband");
        logDirect(Formatting.GRAY + ".globals patry list");
    }
}

