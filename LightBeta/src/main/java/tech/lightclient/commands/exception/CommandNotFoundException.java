package tech.lightclient.commands.exception;

import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.commands.ICommand;
import tech.lightclient.commands.argument.ICommandArgument;

import java.util.List;

public class CommandNotFoundException extends CommandException implements ILogger {

    public final String command;

    public CommandNotFoundException(String command) {
        super(String.format("Команда не найдена: %s", command));
        this.command = command;
    }

    @Override
    public void handle(ICommand command, List<ICommandArgument> args) {
       logDirect(getMessage());
    }
}
