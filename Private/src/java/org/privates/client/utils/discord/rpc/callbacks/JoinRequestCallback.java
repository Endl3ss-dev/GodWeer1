package org.privates.client.utils.discord.rpc.callbacks;

import com.sun.jna.Callback;
import org.privates.client.utils.discord.rpc.utils.DiscordUser;

public interface JoinRequestCallback extends Callback {
    void apply(DiscordUser user);
}

