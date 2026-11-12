package tech.lightclient.commands.exception;

import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.commands.ICommand;
import tech.lightclient.commands.argument.ICommandArgument;
import net.minecraft.util.Formatting;

import java.util.List;

public interface ICommandException extends ILogger {

    String getMessage();

    default void handle(ICommand command, List<ICommandArgument> args) {
        logDirect(
                this.getMessage(),
                Formatting.RED
        );
    }
}
