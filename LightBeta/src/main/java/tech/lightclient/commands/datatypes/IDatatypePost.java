package tech.lightclient.commands.datatypes;

import tech.lightclient.commands.exception.CommandException;

public interface IDatatypePost<T, O> extends IDatatype {
    T apply(IDatatypeContext datatypeContext, O original) throws CommandException;
}
