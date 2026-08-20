package org.privates.client.managers.command.api;

public interface ParametersFactory {
    Parameters createParameters(String message, String delimiter);
}
