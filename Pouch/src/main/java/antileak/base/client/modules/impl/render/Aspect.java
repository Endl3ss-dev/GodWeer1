package antileak.base.client.modules.impl.render;

import antileak.base.api.events.EventLink;
import antileak.base.api.events.Priority;
import antileak.base.api.events.implement.EventAspectRatio;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.FloatSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;

public final class Aspect extends Module {

    public static final Aspect INSTANCE = new Aspect();

    private final ModeSetting mode = new ModeSetting("Режим", "Выключено", "16:9", "16:10", "4:3", "5:4", "21:9", "Кастом", "Выключено");
    private final FloatSetting customRatio = new FloatSetting("Кастом", 1.78f, 0.5f, 3.0f, 0.01f);

    public Aspect() {
        super("Aspect", "Меняет соотношение сторон экрана", ModuleCategory.RENDER);
        addSettings(mode, customRatio);
    }

    public float getCustomAspectRatio() {
        if (!isEnable()) return 0f;

        if (mode.is("16:9")) return 16f / 9f;
        if (mode.is("16:10")) return 16f / 10f;
        if (mode.is("4:3")) return 4f / 3f;
        if (mode.is("5:4")) return 5f / 4f;
        if (mode.is("21:9")) return 21f / 9f;
        if (mode.is("Кастом")) return customRatio.getValue().floatValue();

        return 0f;
    }

    @EventLink(priority = Priority.HIGH)
    public void onAspectRatio(EventAspectRatio event) {
        if (isEnable() && !mode.is("Выключено")) {
            event.cancel();
        }
    }
}
