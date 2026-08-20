package fun.volon.client.modules.impl.render;

import fun.volon.Volon;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;
import fun.volon.client.modules.settings.implement.ListSetting;
import fun.volon.client.modules.settings.implement.ModeSetting;

public class WorldTweaks extends Module {

    public static WorldTweaks INSTANCE = new WorldTweaks();

    private final ListSetting worldSettings = new ListSetting("Настройки мира",
            new BooleanSetting("Время", true),
            new BooleanSetting("Фог", true),
            new BooleanSetting("Шейдер", false),
            new BooleanSetting("Северное сияние", false)
    );

    private final FloatSetting timeSetting = new FloatSetting("Время", 12f, 0f, 24f, 1f)
            .visible(() -> worldSettings.is("Время"));

    private final FloatSetting fogDistanceSetting = new FloatSetting("Дистанция фога", 100f, 20f, 200f, 1f)
            .visible(() -> worldSettings.is("Фог"));

    private final ModeSetting shaderMode = new ModeSetting("Тип шейдера", "Nebula", "Nebula", "Caustic", "Space", "Summer", "Plasma", "Balatro")
            .visible(() -> worldSettings.is("Шейдер"));

    private final FloatSetting shaderOpacity = new FloatSetting("Прозрачность шейдера", 70f, 0f, 100f, 5f)
            .visible(() -> worldSettings.is("Шейдер"));

    private final FloatSetting auroraSpeed = new FloatSetting("Скорость сияния", 0.8f, 0.1f, 3.0f, 0.1f)
            .visible(() -> worldSettings.is("Северное сияние"));

    private final FloatSetting auroraIntensity = new FloatSetting("Яркость сияния", 1.0f, 0.1f, 2.0f, 0.1f)
            .visible(() -> worldSettings.is("Северное сияние"));

    private final FloatSetting auroraScale = new FloatSetting("Масштаб сияния", 1.5f, 0.5f, 3.0f, 0.1f)
            .visible(() -> worldSettings.is("Северное сияние"));

    public WorldTweaks() {
        super("CustomWorld", "Настройки мира", ModuleCategory.RENDER);
        addSettings(worldSettings, timeSetting, fogDistanceSetting, shaderMode, shaderOpacity, auroraSpeed, auroraIntensity, auroraScale);
    }

    public boolean isTimeEnabled() {
        return isEnable() && worldSettings.is("Время");
    }

    public boolean isFogEnabled() {
        return isEnable() && worldSettings.is("Фог");
    }

    public long getForcedTime() {
        return ((long) timeSetting.get()) * 1000L;
    }

    public float getFogDistance() {
        return fogDistanceSetting.get();
    }

    public int getFogColor() {
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            return Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        }
        return ColorUtils.getThemeColor();
    }

    public boolean isShaderEnabled() {
        return isEnable() && worldSettings.is("Шейдер");
    }

    public boolean isCausticShader() {
        return shaderMode.is("Caustic");
    }

    public String getShaderMode() {
        return shaderMode.getCurrent();
    }

    public float getShaderOpacity() {
        return shaderOpacity.get() / 100f;
    }

    public int getShaderColor() {
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            return Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        }
        return ColorUtils.getThemeColor();
    }

    public boolean isAuroraEnabled() {
        return isEnable() && worldSettings.is("Северное сияние");
    }

    public float getAuroraSpeed() {
        return auroraSpeed.get();
    }

    public float getAuroraIntensity() {
        return auroraIntensity.get();
    }

    public float getAuroraScale() {
        return auroraScale.get();
    }

    public int getAuroraColor1() {
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            return Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        }
        return ColorUtils.getThemeColor(0);
    }

    public int getAuroraColor2() {
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            return Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        }
        return ColorUtils.getThemeColor(120);
    }
}