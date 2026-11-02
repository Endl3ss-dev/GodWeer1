package tech.lightclient.commands.datatypes;

import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.commands.exception.CommandNotEnoughArgumentsException;

import java.util.stream.Stream;

public interface IDatatype extends ILogger {
    Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException, CommandNotEnoughArgumentsException;
}
