package nuclear.module.impl.other;

import nuclear.control.events.Event;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.ModeSetting;

@ModuleDescriptor(name = "VoiceChat", type = ModuleCategory.Other, desc = "Выбор версии VoiceChat")
public class VoiceChat extends Module {
    public static final ModeSetting version = new ModeSetting("Версия", "Auto", "Auto", "Legacy", "Modern");

    public VoiceChat() {
        addSettings(version);
    }

    public static String getSelectedVersion() {
        return version.get();
    }

    @Override
    public boolean onEvent(Event event) {
        return false;
    }
}
