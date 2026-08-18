package mods.voicechat;

import mods.voicechat.config.ConfigMigrator;
import mods.voicechat.intercompatibility.CommonCompatibilityManager;
import com.darkmagician6.eventapi.EventManager;

public class ForgeVoicechatMod extends Voicechat {

    public ForgeVoicechatMod() {
        initialize();
        EventManager.register(new ConfigMigrator());
        EventManager.register(CommonCompatibilityManager.INSTANCE);
    }
}