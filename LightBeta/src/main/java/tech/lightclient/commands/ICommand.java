package tech.lightclient.commands;

import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.exception.CommandException;

import java.util.List;
import java.util.stream.Stream;

public interface ICommand extends ILogger {
    void execute(String label, IArgConsumer args) throws CommandException;

    Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException;

    String getShortDesc();

    List<String> getLongDesc();

    List<String> getNames();

    default boolean hiddenFromHelp() {
        return false;
    }
}
