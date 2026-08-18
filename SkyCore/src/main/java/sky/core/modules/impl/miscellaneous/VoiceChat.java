package sky.core.modules.impl.miscellaneous;

import sky.core.utils.managers.impl.notificationmanager.NotificationManager;
import sky.core.modules.Category;
import sky.core.modules.Module;
import sky.core.modules.api.constructors.impl.ClickSetting;
import sky.core.utils.misc.ChatUtil;
import sky.core.utils.render.ColorUtil;
import lombok.Getter;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import mods.voicechat.gui.VoiceChatScreen;
import mods.voicechat.gui.onboarding.OnboardingManager;
import mods.voicechat.voice.client.ClientManager;
import mods.voicechat.voice.client.ClientVoicechat;

@Getter
public class VoiceChat extends Module {
    public ClickSetting voiceModeGui = new ClickSetting("Открыть настройки", () -> {
        mc.displayGuiScreen(new VoiceChatScreen());
    });
    public ClickSetting instructionGui = new ClickSetting("Открыть гайд", () -> {
        OnboardingManager.startOnboarding(null);
    });

    public VoiceChat() {
        super("Voice Chat", "Позволяет общаться с другими игроками с помощью голосового чата", Category.Miscellaneous);
        addSettings(instructionGui, voiceModeGui);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ChatUtil.addText(new StringTextComponent("Перезайдите в мир, для работы Voice Chat").mergeStyle(TextFormatting.GREEN, TextFormatting.BOLD));
        NotificationManager.addNotification("d", "Voice Chat: Rejoin the world to activate", ColorUtil.hex("#54FB54FF"));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ClientVoicechat client = ClientManager.getClient();
        if (client != null) {
            client.close();
        }
        ChatUtil.addText(new StringTextComponent("Подключение разорвано").mergeStyle(TextFormatting.RED, TextFormatting.BOLD));
        NotificationManager.addNotification("d", "Voice Chat: Connection closed", ColorUtil.hex("#FB5454FF"));
    }
}