package fun.volon.api.storages.implement.helpertstorages;

import lombok.Getter;
import lombok.Setter;
import fun.volon.api.QClient;
import fun.volon.api.utils.color.ColorUtils;

@Getter
@Setter
public class Theme implements QClient {
    private String name;
    public int[] color;

    public int colorMain, colorVisualModules, colorText, colorInactiveText;
    public int colorHeaderBg, colorHeaderText, colorSlider, colorSliderCircle;
    public int colorSliderWindow, colorIndicator, colorInactiveIndicator;
    public int colorButton, colorInactiveButton, colorSeparator;
    public int colorField, colorInactiveField, colorTooltipText;
    public int colorWindowBg, colorIcons, colorClient;

    public Theme(String name, int... color) {
        this.name = name;
        this.color = color;
        int c1 = color.length > 0 ? (color[0] | 0xFF000000) : 0xFFAA44FF;
        int c2 = color.length > 1 ? (color[1] | 0xFF000000) : 0xFF0D0D10;
        applyDefaultsFromColors(c1, c2);
    }

    public void applyDefaultsFromColors(int c1, int c2) {
        colorMain = 0xFF0D0D10;
        colorVisualModules = c1;
        colorText = 0xFFE8E8E8;
        colorInactiveText = 0xFF888899;
        colorHeaderBg = 0xFF0A0A0D;
        colorHeaderText = c1;
        colorSlider = c1;
        colorSliderCircle = 0xFFFFFFFF;
        colorSliderWindow = 0xFF2A2A35;
        colorIndicator = c1;
        colorInactiveIndicator = 0xFF2A2A35;
        colorButton = c1;
        colorInactiveButton = 0xFF2A2A35;
        colorSeparator = 0xFF2A2A35;
        colorField = 0xFF1A1A22;
        colorInactiveField = c2;
        colorTooltipText = 0xFFFFFFFF;
        colorWindowBg = c2;
        colorIcons = c1;
        colorClient = c1;
    }

    public void setAllColors(int c1, int c2,
                             int main, int visualModules, int text, int inactiveText,
                             int headerBg, int headerText, int slider, int sliderCircle,
                             int sliderWindow, int indicator, int inactiveIndicator,
                             int button, int inactiveButton, int separator,
                             int field, int inactiveField, int tooltipText,
                             int windowBg, int icons, int client) {
        if (this.color == null || this.color.length < 2) {
            this.color = new int[]{c1, c2};
        } else {
            this.color[0] = c1;
            this.color[1] = c2;
        }
        colorMain = main;
        colorVisualModules = visualModules;
        colorText = text;
        colorInactiveText = inactiveText;
        colorHeaderBg = headerBg;
        colorHeaderText = headerText;
        colorSlider = slider;
        colorSliderCircle = sliderCircle;
        colorSliderWindow = sliderWindow;
        colorIndicator = indicator;
        colorInactiveIndicator = inactiveIndicator;
        colorButton = button;
        colorInactiveButton = inactiveButton;
        colorSeparator = separator;
        colorField = field;
        colorInactiveField = inactiveField;
        colorTooltipText = tooltipText;
        colorWindowBg = windowBg;
        colorIcons = icons;
        colorClient = client;
    }

    public int getColor(int index) {
        if (this.name.equals("Rainbow")) {
            return ColorUtils.rainbow(10, index, 0.6f, 1, 1);
        }
        return ColorUtils.gradient(5, index, color);
    }
}
