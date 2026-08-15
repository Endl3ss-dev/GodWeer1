package antileak.base.client.modules.settings.implement;

import lombok.Getter;
import lombok.Setter;
import antileak.base.client.modules.settings.Setting;

import java.awt.*;
import java.util.function.Supplier;

@Getter
@Setter
public class ColorSetting extends Setting {

    private float hue = 0;
    private float saturation = 1;
    private float brightness = 1;
    private float alpha = 1;
    private int[] presets;

    public ColorSetting(String name, String description) {
        super(name);
    }

    public ColorSetting setColor(int color) {
        float[] hsb = Color.RGBtoHSB(
                (color >> 16) & 0xFF,
                (color >> 8) & 0xFF,
                color & 0xFF,
                null
        );

        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        alpha = ((color >> 24) & 0xFF) / 255f;
        return this;
    }

    public ColorSetting presets(int... presets) {
        this.presets = presets;
        return this;
    }

    public ColorSetting visible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }

    public int getColor() {
        return (getColorWithAlpha() & 0x00FFFFFF) | (Math.round(alpha * 255) << 24);
    }

    public int getColorWithAlpha() {
        return Color.HSBtoRGB(hue, saturation, brightness);
    }
}
