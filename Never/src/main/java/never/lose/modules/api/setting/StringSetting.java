package never.lose.modules.api.setting;

import java.util.function.Supplier;

public class StringSetting extends Setting {

    private String value;
    private final int maxLength;

    public StringSetting(String name, String defaultValue) {
        this(name, defaultValue, 128);
    }

    public StringSetting(String name, String defaultValue, int maxLength) {
        super(name);
        this.maxLength = Math.max(1, maxLength);
        this.value = sanitize(defaultValue);
    }

    public String getValue() {
        return value;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setValue(String value) {
        String next = sanitize(value);
        if (next.equals(this.value)) {
            return;
        }
        this.value = next;
        notifyChanged();
    }

    private String sanitize(String value) {
        if (value == null) {
            return "";
        }
        if (value.length() > maxLength) {
            return value.substring(0, maxLength);
        }
        return value;
    }

    @Override
    public String getValueAsString() {
        return value;
    }

    @Override
    public void setValueFromString(String value) {
        setValue(value);
    }

    @Override
    public StringSetting setVisible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
