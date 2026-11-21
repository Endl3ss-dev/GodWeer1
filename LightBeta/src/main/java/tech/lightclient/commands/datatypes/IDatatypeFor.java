package tech.lightclient.commands.datatypes;

import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.commands.exception.CommandNotEnoughArgumentsException;

public interface IDatatypeFor<T> extends IDatatype  {
    T get(IDatatypeContext datatypeContext) throws CommandException, CommandNotEnoughArgumentsException;
}
