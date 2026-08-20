package fun.volon.client.modules.settings.implement;

import lombok.Getter;
import lombok.Setter;
import fun.volon.client.modules.settings.Setting;

import java.util.function.Supplier;

@Getter
@Setter
public class ColorSetting extends Setting {

    private int value;

    public ColorSetting(String name, int defaultValue) {
        super(name);
        this.value = defaultValue;
    }

    public ColorSetting visible(Supplier<Boolean> state) {
        this.visible = state;
        return this;
    }
}
