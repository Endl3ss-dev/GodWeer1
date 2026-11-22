package tech.lightclient.commands.commands;

import tech.lightclient.commands.ICommandSystem;
import tech.lightclient.commands.argparser.IArgParserManager;
import tech.lightclient.commands.commands.argparser.ArgParserManager;

public enum CommandSystem implements ICommandSystem {
    INSTANCE;

    @Override
    public IArgParserManager getParserManager() {
        return ArgParserManager.INSTANCE;
    }
}
