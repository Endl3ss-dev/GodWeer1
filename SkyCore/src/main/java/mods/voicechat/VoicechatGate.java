package mods.voicechat;

import sky.core.SkyCore;
import sky.core.modules.ModuleManager;
import sky.core.modules.impl.miscellaneous.VoiceChat;

public final class VoicechatGate {
    public static boolean isEnabled() {
        ModuleManager moduleManager = SkyCore.getInstance().getModuleManager();
        VoiceChat voiceMode = (VoiceChat) moduleManager.getModule(VoiceChat.class);
        return voiceMode != null && voiceMode.isEnabled();
    }
}


