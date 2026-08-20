package fun.volon.api.utils.rpc.callbacks;

import com.sun.jna.Callback;
import fun.volon.api.utils.rpc.utils.DiscordUser;

public interface JoinRequestCallback extends Callback {
    void apply(DiscordUser var1);
}
