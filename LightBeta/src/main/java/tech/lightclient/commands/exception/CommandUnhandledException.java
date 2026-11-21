package tech.lightclient.commands.exception;

import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.commands.ICommand;
import tech.lightclient.commands.argument.ICommandArgument;

import java.util.List;

public class CommandUnhandledException extends RuntimeException implements ICommandException, ILogger {

    public CommandUnhandledException(String message) {
        super(message);
    }

    public CommandUnhandledException(Throwable cause) {
        super(cause);
    }

    @Override
    public void handle(ICommand command, List<ICommandArgument> args) {
    }
}
