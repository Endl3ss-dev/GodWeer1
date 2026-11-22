package tech.lightclient.modules.impl.render;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.utils.render3d.ShaderTestRenderer;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ColorSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;

/**
 * ShaderTest - полная копия функционала ShaderEsp (Kawase-свечение, заливка
 * обычная/Caustic/Water, контур), но применённая к рукам и предмету от первого лица.
 * Сам рендер выполняет {@link ShaderTestRenderer} через хуки в GameRendererMixin.
 */
@FunctionAdd(name = "ShaderHands", alias = "Shader Hands", category = Category.Render,
        description = "Шейдерное свечение и заливка на руках (как ShaderEsp)")
public class ShaderTest extends Function {
    public static final String COLOR_THEME = "Тема";
    public static final String COLOR_CUSTOM = "Свои";

    private static ShaderTest instance;

    private final ModeSetting colorMode = new ModeSetting("Цвет", COLOR_THEME, COLOR_THEME, COLOR_CUSTOM);
    private final ColorSetting glowColor1 = new ColorSetting("Цвет 1", 0xFF8A98FF);
    private final ColorSetting glowColor2 = new ColorSetting("Цвет 2", 0xFFFF6BAC);
    private final SliderSetting glowSaturation = new SliderSetting("Насыщенность", 1.40f, 0.5f, 3.0f, 0.05f);

    private final SliderSetting glowRadius = new SliderSetting("Радиус свечения", 4.0f, 1.0f, 6.0f, 1.0f);
    private final SliderSetting glowExposure = new SliderSetting("Сила свечения", 2.0f, 0.5f, 5.0f, 0.05f);

    private final ModeSetting fillMode = new ModeSetting("Заливка", "Выкл", "Выкл", "Обычная", "Caustic", "Water");
    private final SliderSetting fillAlpha = new SliderSetting("Прозрачность заливки", 0.35f, 0.0f, 1.0f, 0.01f)
            .setVisible(() -> !fillMode.is("Выкл"));
    private final ColorSetting fillColor = new ColorSetting("Цвет заливки", 0xFF8A98FF)
            .setVisible(() -> !fillMode.is("Выкл"));

    private boolean isEffectFill() {
        return fillMode.is("Caustic") || fillMode.is("Water");
    }

    private final SliderSetting effectSpeed = new SliderSetting("Скорость эффекта", 1.0f, 0.1f, 5.0f, 0.1f)
            .setVisible(this::isEffectFill);
    private final SliderSetting effectScale = new SliderSetting("Размер эффекта", 5.0f, 1.0f, 20.0f, 0.5f)
            .setVisible(this::isEffectFill);
    private final SliderSetting effectIntensity = new SliderSetting("Интенсивность эффекта", 0.01f, 0.001f, 0.05f, 0.001f)
            .setVisible(this::isEffectFill);

    private final BooleanSetting outline = new BooleanSetting("Контур", true);
    private final SliderSetting outlineThickness = new SliderSetting("Толщина контура", 1.0f, 0.5f, 3.0f, 0.5f)
            .setVisible(outline::get);

    public ShaderTest() {
        instance = this;
        glowColor1.setVisible(() -> colorMode.is(COLOR_CUSTOM));
        glowColor2.setVisible(() -> colorMode.is(COLOR_CUSTOM));
        addSettings(
                colorMode, glowColor1, glowColor2, glowSaturation,
                glowRadius, glowExposure,
                fillMode, fillAlpha, fillColor,
                effectSpeed, effectScale, effectIntensity,
                outline, outlineThickness
        );
    }

    public static ShaderTest getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ShaderTestRenderer.shutdown();
    }

    // ==== Параметры для рендерера ====

    public boolean useFill() {
        return !fillMode.is("Выкл") && getFillAlpha() > 0.005f;
    }

    public String getFillMode() {
        return fillMode.get();
    }

    public float getEffectSpeed() {
        return effectSpeed.get();
    }

    public float getEffectScale() {
        return effectScale.get();
    }

    public float getEffectIntensity() {
        return effectIntensity.get();
    }

    public float getFillAlpha() {
        return fillAlpha.get();
    }

    public int getFillColor() {
        return fillColor.getColor().getRGB();
    }

    public boolean useOutline() {
        return outline.get();
    }

    public float getOutlineThickness() {
        return outlineThickness.get();
    }

    public int getGlowRadiusPasses() {
        return Math.max(1, Math.min(6, Math.round(glowRadius.get())));
    }

    public float getGlowExposure() {
        return glowExposure.get();
    }

    public float getGlowSaturation() {
        return glowSaturation.get();
    }

    public int getGlowColor1() {
        return colorMode.is(COLOR_THEME) ? ClientColors.GRADIENT_START.getRGB() : glowColor1.getColor().getRGB();
    }

    public int getGlowColor2() {
        return colorMode.is(COLOR_THEME) ? ClientColors.GRADIENT_END.getRGB() : glowColor2.getColor().getRGB();
    }

    public int getOutlineColor() {
        return colorMode.is(COLOR_THEME) ? ClientColors.GRADIENT_START.getRGB() : glowColor1.getColor().getRGB();
    }
}
