package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.api.annotations.Category1;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.discord.rpc.DiscordManager;
import org.privates.client.utils.other.Instance;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ModuleInfo(name = "DiscordRPC", category = Category.MISC, description = "Discord Rich Presence для клиента")
public class DiscordRPCModule extends Module {

    DiscordManager discordManager = new DiscordManager();

    public static DiscordRPCModule getInstance() {
        return Instance.get(DiscordRPCModule.class);
    }

    @Override
    protected void onEnable() {
        discordManager.start();
    }

    @Override
    protected void onDisable() {
        discordManager.stop();
    }
}

