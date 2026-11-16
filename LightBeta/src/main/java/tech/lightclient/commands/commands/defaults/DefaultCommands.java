package tech.lightclient.commands.commands.defaults;

import tech.lightclient.commands.ICommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class DefaultCommands {

    public static List<ICommand> createAll() {
        List<ICommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand(),
                new ConfigCommand(),
                new FriendCommand(),
                new GpsCommand(),
                new StaffCommand(),
                new BindCommand(),
                new IRCCommand(),
                new GlobalsCommand(),
                new UnHookCommand(),
                new RCTCommand(),
                new PrefixCommand(),
                new AiCommand(),
                new AutoMineCommand(),
                new AutoSellCommand(),
                new AHCommand(),
                new AimBotCommand(),
                new NeuroCommand(),
                new TNeuroCommand(),
                new ThemeCommand(),
                new BlockEspCommand(),
                new TpCommand(),
                new MaxDamageCommand(),
                new AutoDamageCommand(),
                new LegitNeuroCommand()
        ));
        return Collections.unmodifiableList(commands);
    }
}
