package godweer.modules.api.setting;

import godweer.utils.color.ColorUtils;

import java.util.function.Supplier;

public class ColorSetting extends Setting {

    private int value;

    public ColorSetting(String name, int value) {
        super(name);
        this.value = value;
    }

    public static ColorSetting of(String name, int r, int g, int b) {
        return new ColorSetting(name, ColorUtils.rgba(r, g, b, 255));
    }

    public int getValue() {
        return this.value;
    }

    public void set(int value) {
        this.value = value;
    }

    public int getRed() {
        return ColorUtils.red(this.value);
    }

    public int getGreen() {
        return ColorUtils.green(this.value);
    }

    public int getBlue() {
        return ColorUtils.blue(this.value);
    }

    public int getAlpha() {
        return ColorUtils.alpha(this.value);
    }

    public ColorSetting visible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
