package tech.lightclient.commands.commands;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.EventChat;
import tech.lightclient.client.events.impl.client.TabCompleteEvent;
import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.client.utils.eventbus.EventBus;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.commands.argument.ICommandArgument;
import tech.lightclient.commands.commands.argument.ArgConsumer;
import tech.lightclient.commands.commands.argument.CommandArguments;
import tech.lightclient.commands.commands.manager.CommandRepository;
import tech.lightclient.commands.exception.CommandNotEnoughArgumentsException;
import tech.lightclient.commands.exception.CommandNotFoundException;
import tech.lightclient.commands.helpers.TabCompleteHelper;
import tech.lightclient.commands.manager.ICommandManager;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.stream.Stream;

import static tech.lightclient.commands.IChatControl.FORCE_COMMAND_PREFIX;

public class CommandDispatcher implements ILogger {
    private final ICommandManager manager;
    public static String prefix = ".";

    public CommandDispatcher(EventBus eventBus) {
        this.manager = LightClient.getCommandRepository();
        eventBus.subscribe(this);
    }

    @EventHandler
    public void onChat(EventChat event) {
        String msg = event.getMessage();
        if (LightClient.isHide()) {
            if (!isAllowedWhenUnhooked(msg)) {
                return;
            }
        }

        boolean forceRun = msg.startsWith(FORCE_COMMAND_PREFIX);
        if (msg.startsWith(prefix) || forceRun) {
            event.cancel();
            String commandStr = msg.substring(forceRun ? FORCE_COMMAND_PREFIX.length() : prefix.length());
            if (!runCommand(commandStr) && !commandStr.trim().isEmpty()) {
                new CommandNotFoundException(CommandRepository.expand(commandStr).getLeft()).handle(null, null);
            }
        } else if (runCommand(msg)) {
            event.cancel();
        }
    }

    public boolean runCommand(String msg) {
        if (msg.isEmpty()) {
            Pair<String, List<ICommandArgument>> helpPair = CommandRepository.expand("help");
            return this.manager.execute(helpPair);
        }
        Pair<String, List<ICommandArgument>> pair = CommandRepository.expand(msg);
        String command = pair.getLeft();
        String rest = msg.substring(pair.getLeft().length());
        ArgConsumer argc = new ArgConsumer(this.manager, pair.getRight());
        return this.manager.execute(pair);
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        if (LightClient.isHide()) {
            if (!isAllowedWhenUnhooked(event.prefix)) {
                return;
            }
        }

        String eventPrefix = event.prefix;
        if (!eventPrefix.startsWith(prefix)) {
            return;
        }

        String msg = eventPrefix.substring(prefix.length());
        List<ICommandArgument> args = CommandArguments.from(msg, true);
        Stream<String> stream = tabComplete(msg);
        if (args.size() == 1) {
            stream = stream.map(x -> prefix + x);
        }
        event.completions = stream.toArray(String[]::new);
    }

    public Stream<String> tabComplete(String msg) {
        try {
            List<ICommandArgument> args = CommandArguments.from(msg, true);
            ArgConsumer argc = new ArgConsumer(this.manager, args);
            if (argc.hasAtMost(2)) {
                if (argc.hasExactly(1)) {
                    return new TabCompleteHelper()
                            .addCommands(this.manager)
                            .filterPrefix(argc.getString())
                            .stream();
                }
          /*      Settings.Setting setting = settings.byLowerName.get(argc.getString().toLowerCase(Locale.US));
                if (setting != null && !setting.isJavaOnly()) {
                    if (setting.getValueClass() == Boolean.class) {
                        TabCompleteHelper helper = new TabCompleteHelper();
                        if ((Boolean) setting.value) {
                            helper.append("true", "false");
                        } else {
                            helper.append("false", "true");
                        }
                        return helper.filterPrefix(argc.getString()).stream();
                    } else {
                        return Stream.of(SettingsUtil.settingValueToString(setting));
                    }
                }*/
            }
            return this.manager.tabComplete(msg);
        } catch (CommandNotEnoughArgumentsException ignored) {
            return Stream.empty();
        }
    }

    private boolean isAllowedWhenUnhooked(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }

        String msg = input.trim();
        boolean forceRun = msg.startsWith(FORCE_COMMAND_PREFIX);
        if (forceRun) {
            msg = msg.substring(FORCE_COMMAND_PREFIX.length());
        } else if (msg.startsWith(prefix)) {
            msg = msg.substring(prefix.length());
        } else {
            return false;
        }

        String label = CommandRepository.expand(msg).getLeft();
        return "unhook".equalsIgnoreCase(label);
    }
}


