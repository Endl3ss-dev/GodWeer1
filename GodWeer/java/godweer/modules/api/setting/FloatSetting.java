package godweer.modules.api.setting;

import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class FloatSetting extends Setting {

    private final float min;
    private final float max;
    private final float increment;

    private float value;
    private boolean active;

    public FloatSetting(String name, float value, float min, float max, float increment) {
        super(name);
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.value = Mth.clamp(value, min, max);
    }

    public float get() {
        return Mth.clamp(this.value, this.min, this.max);
    }

    public float getValue() {
        return get();
    }

    public void setValue(float value) {
        this.value = Mth.clamp(value, this.min, this.max);
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    public float getIncrement() {
        return this.increment;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public FloatSetting visible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
