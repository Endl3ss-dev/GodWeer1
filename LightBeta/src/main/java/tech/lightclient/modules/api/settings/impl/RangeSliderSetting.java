package tech.lightclient.modules.api.settings.impl;

import tech.lightclient.modules.api.settings.api.Setting;

import java.util.function.Supplier;

/**
 * A slider with two handles, used for "pick a range" settings such as a randomised delay.
 * Added for the scripting API's {@code rangeSlider(...)}.
 */
public class RangeSliderSetting extends Setting<float[]> {

    public float min;
    public float max;
    public float increment;

    public RangeSliderSetting(String name, float from, float to, float min, float max, float increment) {
        super(name, new float[]{from, to});
        this.min = min;
        this.max = max;
        this.increment = increment;
        clampAndOrder();
    }

    public float from() {
        return get()[0];
    }

    public float to() {
        return get()[1];
    }

    public void setRange(float from, float to) {
        set(new float[]{from, to});
        clampAndOrder();
    }

    public void setFrom(float from) {
        setRange(from, to());
    }

    public void setTo(float to) {
        setRange(from(), to);
    }

    private void clampAndOrder() {
        float[] v = get();
        if (v == null || v.length < 2) {
            set(new float[]{min, max});
            return;
        }
        float a = Math.max(min, Math.min(max, v[0]));
        float b = Math.max(min, Math.min(max, v[1]));
        if (a > b) {
            float t = a;
            a = b;
            b = t;
        }
        v[0] = a;
        v[1] = b;
    }

    @Override
    public RangeSliderSetting setVisible(Supplier<Boolean> bool) {
        return (RangeSliderSetting) super.setVisible(bool);
    }
}
