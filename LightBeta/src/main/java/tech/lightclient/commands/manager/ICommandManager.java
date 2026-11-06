package tech.lightclient.commands.manager;

import net.minecraft.util.Pair;
import tech.lightclient.commands.ICommand;
import tech.lightclient.commands.argument.ICommandArgument;
import tech.lightclient.commands.registry.Registry;

import java.util.List;
import java.util.stream.Stream;

public interface ICommandManager {
    Registry<ICommand> getRegistry();

    ICommand getCommand(String name);

    boolean execute(String string);

    boolean execute(Pair<String, List<ICommandArgument>> expanded);

    Stream<String> tabComplete(Pair<String, List<ICommandArgument>> expanded);

    Stream<String> tabComplete(String prefix);
}
