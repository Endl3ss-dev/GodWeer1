package org.privates.client.managers.command.api;


import org.privates.client.managers.command.DispatchResult;

public interface CommandDispatcher {
    DispatchResult dispatch(String command);
}
