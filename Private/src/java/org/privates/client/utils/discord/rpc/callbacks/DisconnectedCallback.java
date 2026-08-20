package org.privates.client.utils.discord.rpc.callbacks;

import com.sun.jna.Callback;

public interface DisconnectedCallback extends Callback {
    void apply(int code, String message);
}

