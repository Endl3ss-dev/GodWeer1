package org.privates.client.managers.module.impl.client;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Customize;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.misc.AutoBuySpookyGui;
import org.privates.client.managers.module.impl.misc.SpookyFarmGui;
import org.privates.client.managers.module.impl.player.ChangelogGui;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.screen.WelcomeThemeSelectorGui;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;

import java.util.ArrayList;

@Customize

@Category1

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Client", category = Category.RENDER, description = "Кастомизируйте темы и другие части клиента основная функция визуалов и биндов", autoEnabled = true, allowDisable = false)

public class Theme extends Module {
    public static Theme getInstance() {
        return Instance.get(Theme.class);
    }

    private final ColorSetting client = new ColorSetting(this, "Основной", ColorUtil.getColor(101, 121, 252));
    private final ColorSetting clickguiBlur = new ColorSetting(this, "Цвет блюра ClickGui", ColorUtil.getColor(4, 11, 54));
    private final ColorSetting text = new ColorSetting(this, "Цвет текста", ColorUtil.getColor(129, 146, 252));
    private final ColorSetting enabledText = new ColorSetting(this, "Цвет включенной функции", ColorUtil.getColor(101, 121, 252));
    private final ColorSetting disabledText = new ColorSetting(this, "Цвет выключенной функции", ColorUtil.getColor(255, 255, 255));
    private final ColorSetting secondary = new ColorSetting(this, "Вторичный цвет", ColorUtil.getColor(45, 60, 85));
    private final ColorSetting background = new ColorSetting(this, "Задний фон", ColorUtil.getColor(0, 0, 0));
    private final ColorSetting icon = new ColorSetting(this, "Иконки", ColorUtil.getColor(120, 180, 240));

    private final MultiItemSetting itemsCounter = new MultiItemSetting(this, "Предметы для itemcount", new ArrayList<>());

    public MultiItemSetting itemsCounter() {
        return itemsCounter;
    }

    private final SliderSetting backgroundAlpha = new SliderSetting(this, "Прозрачность ректа", 0f, 0f, 255f, 1f);
    private final SliderSetting blurStrength = new SliderSetting(this, "Сила Blur", 8.5f, 0f, 66f, 1.5f);
    private final SliderSetting liquidGlassStrength = new SliderSetting(this, "Сила искажения стекла", 4, 0f, 10, .5f);

    private final ModeSetting shaderType = new ModeSetting(this, "Тип шейдера", "Blur", "LiquidGlass", "Искаженное стекло", "PixelBlur", "Алкоголь");

    private final MultiBooleanSetting blurElements = new MultiBooleanSetting(this, "Blur для элементов",
            BooleanSetting.of("Watermark", true),
            BooleanSetting.of("TargetHud", true),
            BooleanSetting.of("Keybinds", true),
            BooleanSetting.of("Potions", true),
            BooleanSetting.of("ItemBinds", true),
            BooleanSetting.of("Cooldowns", true),
            BooleanSetting.of("Keystrokes", true),
            BooleanSetting.of("ScoreBoard", true),
            BooleanSetting.of("ArmorHud", true),
            BooleanSetting.of("ItemCount", true),
            BooleanSetting.of("StaffList", true),
            BooleanSetting.of("InventoryHud", true),
            BooleanSetting.of("MotionGraph", true),
            BooleanSetting.of("NearbyPlayers", true),
            BooleanSetting.of("BossBar", true)
    );

    private final ThemeSetting themePreset = new ThemeSetting(this, "Градиент квадрата",
            new ThemeSetting.ThemePreset("Midnight", ColorUtil.getColor(0, 0, 0)),
            new ThemeSetting.ThemePreset("Charcoal", ColorUtil.getColor(25, 25, 28)),
            new ThemeSetting.ThemePreset("Slate", ColorUtil.getColor(30, 32, 38)),
            new ThemeSetting.ThemePreset("Graphite", ColorUtil.getColor(28, 30, 35)),
            new ThemeSetting.ThemePreset("Steel", ColorUtil.getColor(35, 38, 42)),
            new ThemeSetting.ThemePreset("Onyx", ColorUtil.getColor(22, 24, 28)),
            new ThemeSetting.ThemePreset("Shadow", ColorUtil.getColor(18, 20, 25)),
            new ThemeSetting.ThemePreset("Carbon", ColorUtil.getColor(24, 26, 30)),
            new ThemeSetting.ThemePreset("Deep Navy", ColorUtil.getColor(15, 18, 28), ColorUtil.getColor(25, 30, 45)),
            new ThemeSetting.ThemePreset("Subtle Purple", ColorUtil.getColor(28, 25, 35), ColorUtil.getColor(35, 30, 45)),
            new ThemeSetting.ThemePreset("Soft Blue", ColorUtil.getColor(20, 25, 35), ColorUtil.getColor(25, 32, 42)),
            new ThemeSetting.ThemePreset("Muted Violet", ColorUtil.getColor(30, 28, 38), ColorUtil.getColor(38, 32, 48)),
            new ThemeSetting.ThemePreset("Gentle Teal", ColorUtil.getColor(22, 30, 32), ColorUtil.getColor(28, 38, 40)),
            new ThemeSetting.ThemePreset("Calm Green", ColorUtil.getColor(25, 32, 28), ColorUtil.getColor(30, 38, 32)),
            new ThemeSetting.ThemePreset("Warm Gray", ColorUtil.getColor(35, 32, 30), ColorUtil.getColor(42, 38, 35)),
            new ThemeSetting.ThemePreset("Cool Gray", ColorUtil.getColor(30, 32, 35), ColorUtil.getColor(35, 38, 42)),
            new ThemeSetting.ThemePreset("Dusk", ColorUtil.getColor(25, 22, 30), ColorUtil.getColor(35, 28, 40)),
            new ThemeSetting.ThemePreset("Dawn", ColorUtil.getColor(32, 28, 25), ColorUtil.getColor(40, 35, 30)),
            new ThemeSetting.ThemePreset("Twilight", ColorUtil.getColor(22, 25, 32), ColorUtil.getColor(28, 32, 42)),
            new ThemeSetting.ThemePreset("Overcast", ColorUtil.getColor(35, 35, 38), ColorUtil.getColor(42, 42, 45))
    );

    private final BooleanSetting customThemeEnabled = new BooleanSetting(this, "Кастомная тема", false);
    private final ColorSetting customColor1 = new ColorSetting(this, "Кастом цвет 1", ColorUtil.getColor(25, 25, 30));
    private final ColorSetting customColor2 = new ColorSetting(this, "Кастом цвет 2", ColorUtil.getColor(35, 35, 40));

    private final BooleanSetting animatedGradient = new BooleanSetting(this, "Анимация градиента", false);

    private final ModeSetting gradientQuality = new ModeSetting(this, "Качество градиента",
            "Balanced", "Soft Blend", "Sharp Contrast", "Subtle Fade");

    private final ModeSetting animationSpeed = new ModeSetting(this, "Скорость анимации",
            "Medium", "Slow", "Fast", "Very Fast");

    private final BindSetting spookyFarm = new BindSetting(this, "GUI CraftFarm", -1);
    private final BindSetting changeLog = new BindSetting(this, "GUI ChangeLog", -1);
    private final BindSetting autobuyGui = new BindSetting(this, "GUI AutoBuy", -1);

    private boolean wasSpookyFarmKeyPressed = false;
    private boolean wasAutoBuyKeyPressed = false;
    private boolean wasChangeKeyPressed = false;

    private float gradientAnimationTime = 0f;

    public boolean isLiquidGlassEnabled() {
        return shaderType.getValue().equals("LiquidGlass");
    }

    public ModeSetting shaderType() {
        return shaderType;
    }

    public void drawClientRect(MatrixStack matrix, float x, float y, float width, float height, float alpha, float radius) {
        x = (float) Mathf.step(x, 0.5);
        y = (float) Mathf.step(y, 0.5);
        width = (float) Mathf.step(width, 0.5);
        height = (float) Mathf.step(height, 0.5);

        RenderUtil.Rounded.smooth(matrix, x, y, width, height, ColorUtil.replAlpha(backgroundColor(), alpha), Round.of(radius));
    }

    public int getThemeColor(float progress) {
        if (customThemeEnabled.getValue()) {
            if (animatedGradient.getValue()) {
                float animProgress = getAnimatedGradientProgress();
                return ColorUtil.interpolateColor(customColor1.getValue(), customColor2.getValue(),
                        (float) Math.sin(animProgress + progress * Math.PI) * 0.5f + 0.5f);
            }
            return ColorUtil.interpolateColor(customColor1.getValue(), customColor2.getValue(), progress);
        }

        ThemeSetting.ThemePreset preset = themePreset.getValue();
        if (animatedGradient.getValue() && preset.isGradient()) {
            float animProgress = getAnimatedGradientProgress();
            return ColorUtil.interpolateColor(preset.getColor1(), preset.getColor2(),
                    (float) Math.sin(animProgress + progress * Math.PI) * 0.5f + 0.5f);
        }

        return preset.getColor(progress);
    }

    private float getAnimatedGradientProgress() {
        float speed = switch (animationSpeed.getValue()) {
            case "Slow" -> 0.0005f;
            case "Medium" -> 0.001f;
            case "Fast" -> 0.002f;
            case "Very Fast" -> 0.004f;
            default -> 0.001f;
        };
        gradientAnimationTime += speed;
        if (gradientAnimationTime > Math.PI * 2) {
            gradientAnimationTime -= (float) (Math.PI * 2);
        }
        return gradientAnimationTime;
    }

    public float getGradientRatio() {
        return switch (gradientQuality.getValue()) {
            case "Soft Blend" -> 0.3f;
            case "Balanced" -> 0.5f;
            case "Sharp Contrast" -> 0.7f;
            case "Subtle Fade" -> 0.2f;
            default -> 0.5f;
        };
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        boolean isSpookyFarmKeyPressed = GLFW.glfwGetKey(mc.getMainWindow().getHandle(), spookyFarm.getValue()) == GLFW.GLFW_PRESS;
        if (isSpookyFarmKeyPressed && !wasSpookyFarmKeyPressed && mc.currentScreen == null) {
            mc.displayScreen(new WelcomeThemeSelectorGui());
        }
        wasSpookyFarmKeyPressed = isSpookyFarmKeyPressed;

        boolean isAutoBuyKeyPressed = GLFW.glfwGetKey(mc.getMainWindow().getHandle(), autobuyGui.getValue()) == GLFW.GLFW_PRESS;
        if (isAutoBuyKeyPressed && !wasAutoBuyKeyPressed && mc.currentScreen == null) {
            mc.displayScreen(new AutoBuySpookyGui());
        }
        wasAutoBuyKeyPressed = isAutoBuyKeyPressed;

        boolean isChangeKeyPressed = GLFW.glfwGetKey(mc.getMainWindow().getHandle(), changeLog.getValue()) == GLFW.GLFW_PRESS;
        if (isChangeKeyPressed && !wasChangeKeyPressed && mc.currentScreen == null) {
            mc.displayScreen(new ChangelogGui());
        }
        wasChangeKeyPressed = isChangeKeyPressed;
    }

    public void openGui() {
        mc.displayScreen(new SpookyFarmGui());
    }

    public void drawClientRect(MatrixStack matrix, float x, float y, float width, float height, float alpha) {
        drawClientRect(matrix, x, y, width, height, alpha, 4);
    }

    public void drawClientRect(MatrixStack matrix, float x, float y, float width, float height) {
        drawClientRect(matrix, x, y, width, height, 1F, 4);
    }

    public int clientColor() {
        return client.getValue();
    }

    public ThemeSetting themePreset() {
        return themePreset;
    }

    public int accentColor() {
        return ColorUtil.multDark(client.getValue(), 0.75F);
    }

    public int secondaryColor() {
        return secondary.getValue();
    }

    public int backgroundColor() {
        return background.getValue();
    }

    public int clickguiBlurColor() {
        return clickguiBlur.getValue();
    }

    public int textColor() {
        return text.getValue();
    }

    public int textAccentColor() {
        return ColorUtil.multDark(text.getValue(), 0.75F);
    }

    public int enabledColor() {
        return enabledText.getValue();
    }

    public int disabledColor() {
        return disabledText.getValue();
    }

    public int iconColor() {
        return icon.getValue();
    }

    public int darkColor() {
        return ColorUtil.multDark(client.getValue(), 0.25F);
    }

    public boolean isBlurEnabled() {
        return true;
    }

    public boolean isAnimationsEnabled() {
        return true;
    }

    public int getSpeed() {
        return 10;
    }

    public BooleanSetting customThemeEnabled() {
        return customThemeEnabled;
    }

    public ColorSetting customColor1() {
        return customColor1;
    }

    public ColorSetting customColor2() {
        return customColor2;
    }

    public BooleanSetting animatedGradient() {
        return animatedGradient;
    }

    public ModeSetting gradientQuality() {
        return gradientQuality;
    }

    public ModeSetting animationSpeed() {
        return animationSpeed;
    }
}