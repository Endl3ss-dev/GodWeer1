package never.lose.modules.api.setting;

import java.util.function.Supplier;

public class SliderSetting extends Setting {

    private double value;
    private final double min;
    private final double max;
    private final double step;

    public SliderSetting(String name, double defaultValue, double min, double max, double step) {
        super(name);
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public double getValue() {
        return value;
    }

    public int getIntValue() {
        return (int) value;
    }

    public float getFloatValue() {
        return (float) value;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    public void setValue(double value) {
        double next = Math.max(min, Math.min(max, value));
        if (this.value == next) {
            return;
        }
        this.value = next;
        notifyChanged();
    }

    @Override
    public String getValueAsString() {
        return Double.toString(value);
    }

    @Override
    public void setValueFromString(String value) {
        try {
            setValue(Double.parseDouble(value));
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    public SliderSetting setVisible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
