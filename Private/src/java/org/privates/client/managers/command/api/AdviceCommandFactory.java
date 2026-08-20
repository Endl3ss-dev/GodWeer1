package org.privates.client.managers.command.api;


import org.privates.client.managers.command.AdviceCommand;

public interface AdviceCommandFactory {
    AdviceCommand adviceCommand(CommandProvider commandProvider);
}
