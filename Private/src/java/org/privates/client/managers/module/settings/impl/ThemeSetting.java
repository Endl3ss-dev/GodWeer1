package org.privates.client.managers.module.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.settings.Setting;
import org.privates.client.utils.render.color.ColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class ThemeSetting extends Setting<ThemeSetting.ThemePreset> {

    private final List<ThemePreset> presets = new ArrayList<>();
    private ThemePreset cachedValue;

    public ThemeSetting(Module parent, String name, ThemePreset... presets) {
        super(parent, name, presets[0]);
        this.presets.addAll(List.of(presets));
        this.set(presets[0]);
        cachedValue = presets[0];
    }

    public int getIndex() {
        return presets.indexOf(getValue());
    }

    public boolean is(String name) {
        return getValue().getName().equalsIgnoreCase(name) && getVisible().get();
    }

    @Override
    public ThemeSetting set(ThemePreset value) {
        ThemeSetting set = (ThemeSetting) super.set(value);
        this.cachedValue = super.getValue();
        return set;
    }

    @Override
    public ThemeSetting setVisible(Supplier<Boolean> value) {
        return (ThemeSetting) super.setVisible(value);
    }

    @Override
    public ThemeSetting onAction(Runnable action) {
        return (ThemeSetting) super.onAction(() -> {
            action.run();
            this.cachedValue = super.getValue();
        });
    }

    @Override
    public ThemeSetting onSetVisible(Runnable action) {
        return (ThemeSetting) super.onSetVisible(action);
    }

    @Override
    public ThemePreset getValue() {
        if (cachedValue == null) {
            cachedValue = super.getValue();
        }
        return cachedValue;
    }

    @Getter
    @Setter
    public static class ThemePreset {
        private final String name;
        private int color1;
        private int color2;
        private boolean isGradient;

        private float gradientV;

        public ThemePreset(String name, int color) {
            this.name = name;
            this.color1 = color;
            this.color2 = color;
            this.isGradient = false;
        }

        public ThemePreset(String name, int color1, int color2) {
            this.name = name;
            this.color1 = color1;
            this.color2 = color2;
            this.isGradient = true;
        }

        public int getColor(float progress) {
            if (!isGradient) return color1;
            return ColorUtil.interpolate(color1, color2, progress);
        }
    }
}