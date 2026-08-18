package nuclear.module.impl.render;

import nuclear.control.events.Event;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.ModeSetting;
import nuclear.module.settings.imp.SliderSetting;

@ModuleDescriptor(name = "CustomModel", type = ModuleCategory.Render, desc = "Изменяет модельку игрока")
public class CustomModel extends Module {
    private final ModeSetting modelType = new ModeSetting("Модель", "Vanilla", "Vanilla", "Slim", "Mini", "Wide");
    private final BooleanSetting onlySelf = new BooleanSetting("Только себя", true);
    private final SliderSetting scale = new SliderSetting("Размер", 1.0f, 0.5f, 1.5f, 0.05f);

    public CustomModel() {
        addSettings(modelType, onlySelf, scale);
    }

    public String getModelType() {
        return modelType.get();
    }

    public boolean isOnlySelf() {
        return onlySelf.get();
    }

    public float getScale() {
        return scale.getValue().floatValue();
    }

    @Override
    public boolean onEvent(Event event) {
        return false;
    }
}
