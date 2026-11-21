package tech.lightclient.modules.api.settings.impl;

import tech.lightclient.modules.api.settings.api.Setting;

import java.util.function.Supplier;

public class SliderSetting extends Setting<Float> {

    public float min;
    public float max;
    public float increment;

    /** Unit drawn after the number, e.g. "ms". Empty means a bare number. */
    private String postfix = "";

    public SliderSetting(String name, float defaultVal, float min, float max, float increment) {
        super(name, defaultVal);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public String getPostfix() {
        return postfix;
    }

    public SliderSetting setPostfix(String postfix) {
        this.postfix = postfix == null ? "" : postfix;
        return this;
    }

    @Override
    public SliderSetting setVisible(Supplier<Boolean> bool) {
        return (SliderSetting) super.setVisible(bool);
    }
}
