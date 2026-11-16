package tech.lightclient.client.utils.discord.callbacks;

import com.sun.jna.Callback;
import tech.lightclient.client.utils.discord.utils.DiscordUser;

public interface JoinRequestCallback extends Callback {
    void apply(DiscordUser var1);
}