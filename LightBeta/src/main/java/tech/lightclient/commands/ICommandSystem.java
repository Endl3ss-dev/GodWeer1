package tech.lightclient.commands;

import tech.lightclient.commands.argparser.IArgParserManager;

public interface ICommandSystem {
    IArgParserManager getParserManager();
}
