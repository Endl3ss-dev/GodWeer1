package never.lose.modules.api.setting;

import java.util.function.Supplier;

public class BindSetting extends Setting {

    private int value;

    public BindSetting(String name, Integer defaultValue) {
        super(name);
        this.value = defaultValue;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        if (value == null || this.value == value) {
            return;
        }
        this.value = value;
        notifyChanged();
    }

    @Override
    public String getValueAsString() {
        return Integer.toString(value);
    }

    @Override
    public void setValueFromString(String value) {
        try {
            setValue(Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    public BindSetting setVisible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
