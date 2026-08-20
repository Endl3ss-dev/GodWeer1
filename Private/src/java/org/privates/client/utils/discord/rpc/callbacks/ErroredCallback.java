package org.privates.client.utils.discord.rpc.callbacks;

import com.sun.jna.Callback;

public interface ErroredCallback extends Callback {
    void apply(int code, String message);
}

