package tech.lightclient.modules.impl.render;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ColorSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;

@FunctionAdd(name = "ClickGui", alias = "ClickGui", category = Category.Render, description = "Мастер-настройка GUI")
public class ClickGui extends Function {
    private static final float DEFAULT_SIMPLE_BACKGROUND_OPACITY = 0.94f;
    private static final float DEFAULT_SIMPLE_BLUR_STRENGTH = 8f;
    private static final int DEFAULT_ACCENT_COLOR = 0xFF5690FF;
    private static final int DEFAULT_OPEN_BIND = GLFW.GLFW_KEY_RIGHT_SHIFT;

    public static BindSetting openBind = new BindSetting("Клавиша меню", DEFAULT_OPEN_BIND);
    public static ModeSetting scaleMode = new ModeSetting("Масштаб меню", "100%", "100%");
    public static ColorSetting colorSetting = new ColorSetting("Акцентный цвет", DEFAULT_ACCENT_COLOR);
    public static BooleanSetting darkMode = new BooleanSetting("Тёмный", false);

    public static BooleanSetting simpleBlur = new BooleanSetting("Блюр фона", true);
    public static SliderSetting simpleBackgroundOpacity = new SliderSetting(
            "Прозрачность фона",
            DEFAULT_SIMPLE_BACKGROUND_OPACITY,
            0.90f,
            1.00f,
            0.01f
    );
    public static SliderSetting simpleBlurStrength = new SliderSetting(
            "Сила блюра",
            DEFAULT_SIMPLE_BLUR_STRENGTH,
            0.5f,
            20f,
            0.5f
    );

    private static int lastAppliedAccentColor = Integer.MIN_VALUE;

    public ClickGui() {
        super.addSettings(openBind, scaleMode, colorSetting, darkMode);
        setState(true);
        applySelectedAccentColor();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        applySelectedAccentColor();
    }

    @EventHandler
    public void onTick(UpdateEvent e) {
        applySelectedAccentColor();
    }

    public static ModeSetting getScaleModeSetting() {
        return scaleMode;
    }

    public static ColorSetting getColorSetting() {
        return colorSetting;
    }

    public static BindSetting getOpenBindSetting() {
        return openBind;
    }

    public static int getOpenBindKey() {
        Integer bind = openBind != null ? openBind.get() : null;
        return bind != null && bind > 0 ? bind : DEFAULT_OPEN_BIND;
    }

    public static boolean isOpenBindKeyboardKey(int keyCode) {
        int bind = getOpenBindKey();
        return bind < 1000 && keyCode == bind;
    }

    public static boolean isOpenBindPressed(long windowHandle) {
        int bind = getOpenBindKey();
        if (bind >= 1000) {
            int mouseButton = bind - 1000;
            return mouseButton >= 0 && GLFW.glfwGetMouseButton(windowHandle, mouseButton) == GLFW.GLFW_PRESS;
        }
        return GLFW.glfwGetKey(windowHandle, bind) == GLFW.GLFW_PRESS;
    }

    public static void applySelectedAccentColor() {
        if (colorSetting == null) {
            return;
        }

        Integer value = colorSetting.get();
        int argb = value != null ? value : DEFAULT_ACCENT_COLOR;
        if (argb == lastAppliedAccentColor) {
            return;
        }
        lastAppliedAccentColor = argb;

        ClientColors.setIcon(argb);
        ClientColors.setGradientStart(shade(argb, 0.88f));
        ClientColors.setGradientEnd(shade(argb, 1.12f));
    }

    public static float getGuiScaleMultiplier() {
        if (scaleMode == null) {
            return 1.0f;
        }
        return switch (scaleMode.get()) {
            case "100%" -> 1.0f;
            default -> 1.0f;
        };
    }

    public static boolean isSimpleBlurEnabled() {
        return !isDarkModeEnabled() && simpleBlur != null && simpleBlur.get();
    }

    public static BooleanSetting getDarkModeSetting() {
        return darkMode;
    }

    public static boolean isDarkModeEnabled() {
        return darkMode != null && darkMode.get();
    }

    public static boolean isCsMotionBlurEnabled() {
        return isSimpleBlurEnabled();
    }

    public static float getSimpleBackgroundOpacity() {
        if (simpleBackgroundOpacity == null) {
            return DEFAULT_SIMPLE_BACKGROUND_OPACITY;
        }
        float value = simpleBackgroundOpacity.get();
        if (!Float.isFinite(value)) {
            return DEFAULT_SIMPLE_BACKGROUND_OPACITY;
        }
        return Math.max(0.90f, Math.min(1.00f, value));
    }

    public static float getSimpleBlurStrength() {
        if (simpleBlurStrength == null) {
            return DEFAULT_SIMPLE_BLUR_STRENGTH;
        }
        float value = simpleBlurStrength.get();
        if (!Float.isFinite(value) || value < 0.5f) {
            return DEFAULT_SIMPLE_BLUR_STRENGTH;
        }
        return value;
    }

    private static int shade(int argb, float factor) {
        Color color = new Color(argb, true);
        int red = clampChannel(Math.round(color.getRed() * factor));
        int green = clampChannel(Math.round(color.getGreen() * factor));
        int blue = clampChannel(Math.round(color.getBlue() * factor));
        return new Color(red, green, blue, color.getAlpha()).getRGB();
    }

    private static int clampChannel(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
