package antileak.base.client.modules.impl.misc;

import net.minecraft.text.Text;
import antileak.base.Punch;
import antileak.base.api.utils.replace.ReplaceUtils;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.TextSetting;

public class NameProtect extends Module {

    public static final NameProtect INSTANCE = new NameProtect();
    private final BooleanSetting friends = new BooleanSetting("Скрывать друзей", true);
    private final TextSetting fakeName = new TextSetting("Никнейм", "Punchdlc", 32);

    private NameProtect() {
        super("NameProtect", "Визуально скрывает ник игрока", ModuleCategory.MISC);
        addSettings(friends, fakeName);
    }

    public String patch(String text) {
        if (text == null || !isEnable() || mc == null) {
            return text;
        }

        String clientUsername = mc.getSession().getUsername();
        
        // Заменяем username
        text = text.replace(clientUsername, fakeName.get());
        
        // Заменяем display name если игрок в игре
        if (mc.player != null && mc.world != null) {
            String displayName = mc.player.getDisplayName().getString();
            text = text.replace(displayName, fakeName.get());
        }
        
        // Заменяем друзей если включено
        if (friends.isState() && Punch.INSTANCE != null && Punch.INSTANCE.friendStorage != null) {
            for (String friend : Punch.INSTANCE.friendStorage.getFriends()) {
                text = text.replace(friend, fakeName.get());
            }
        }
        
        return text;
    }

    public String patchIncomingText(String text) {
        return patch(text);
    }

    public Text patchText(Text text) {
        if (text == null || !isEnable() || mc == null) {
            return text;
        }

        Text output = text;
        String replacement = fakeName.get();
        String clientUsername = mc.getSession().getUsername();
        
        // Заменяем username
        output = ReplaceUtils.replace(output, clientUsername, replacement);
        
        // Заменяем display name если игрок в игре
        if (mc.player != null && mc.world != null) {
            String displayName = mc.player.getDisplayName().getString();
            output = ReplaceUtils.replace(output, displayName, replacement);
        }
        
        // Заменяем друзей если включено
        if (friends.isState() && Punch.INSTANCE != null && Punch.INSTANCE.friendStorage != null) {
            for (String friend : Punch.INSTANCE.friendStorage.getFriends()) {
                output = ReplaceUtils.replace(output, friend, replacement);
            }
        }
        
        return output;
    }

    public String getReplacementName() {
        return fakeName.get();
    }

    public boolean shouldHideGrief() {
        return false; // Всегда возвращаем false, так как функция скрытия информации удалена
    }

    public TextSetting getFakeName() {
        return fakeName;
    }
}
