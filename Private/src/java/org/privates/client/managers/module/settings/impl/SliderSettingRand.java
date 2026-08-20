package org.privates.client.managers.module.settings.impl;

import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.settings.Setting;

import java.util.function.Supplier;

public class SliderSettingRand extends Setting<SliderSettingRand.RangeValue> {
    public float min;
    public float max;
    public float increment;
    private RangeValue cachedValue;

    public static class RangeValue {
        private float minValue;
        private float maxValue;

        public RangeValue(float minValue, float maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public float getMinValue() {
            return minValue;
        }

        public void setMinValue(float minValue) {
            this.minValue = minValue;
        }

        public float getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(float maxValue) {
            this.maxValue = maxValue;
        }
    }

    public SliderSettingRand(Module parent, String name, float minValue, float maxValue, float min, float max, float increment) {
        super(parent, name, new RangeValue(minValue, maxValue));
        this.min = min;
        this.max = max;
        this.increment = increment;
        cachedValue = new RangeValue(minValue, maxValue);
    }

    public SliderSettingRand(Module parent, String name, float minValue, float maxValue, float min, float max, float increment, String description) {
        super(parent, name, new RangeValue(minValue, maxValue), description);
        this.min = min;
        this.max = max;
        this.increment = increment;
        cachedValue = new RangeValue(minValue, maxValue);
    }

    @Override
    public SliderSettingRand set(RangeValue value) {
        SliderSettingRand set = (SliderSettingRand) super.set(value);
        this.cachedValue = new RangeValue(value.getMinValue(), value.getMaxValue());
        return set;
    }

    @Override
    public SliderSettingRand setVisible(Supplier<Boolean> value) {
        return (SliderSettingRand) super.setVisible(value);
    }

    @Override
    public SliderSettingRand onAction(Runnable action) {
        return (SliderSettingRand) super.onAction(() -> {
            action.run();
            RangeValue current = super.getValue();
            this.cachedValue = new RangeValue(current.getMinValue(), current.getMaxValue());
        });
    }

    @Override
    public SliderSettingRand onSetVisible(Runnable action) {
        return (SliderSettingRand) super.onSetVisible(action);
    }

    @Override
    public RangeValue getValue() {
        if (cachedValue == null) {
            cachedValue = super.getValue();
        }
        return cachedValue;
    }

    public SliderSettingRand setDescription(String description) {
        super.setDescription(description);
        return this;
    }
}