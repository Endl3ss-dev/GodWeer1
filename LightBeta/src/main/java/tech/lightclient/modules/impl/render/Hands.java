package tech.lightclient.modules.impl.render;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.utils.render3d.HandsRenderer;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ColorSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;

import java.util.Locale;

@FunctionAdd(name = "Hands", alias = "Hands", category = Category.Render, description = "Shader effects for first-person hands")
public class Hands extends Function {
    public static final String MODE_SHADER_2 = "Shader 2";
    public static final String MODE_SHADER_3 = "Shader 3";
    public static final String SURFACE_FILL = "Fill";
    public static final String SURFACE_MIRROR = "Mirror";
    public static final String COLOR_THEME = "Theme";
    public static final String COLOR_CUSTOM = "Custom";
    public static final String COLOR_AUTO = "Auto";
    public static final String COLOR_GRADIENT = "Gradient";

    private final ModeSetting mode = new ModeSetting("Mode", MODE_SHADER_2, MODE_SHADER_2, MODE_SHADER_3);

    private final ModeSetting surfaceMode = new ModeSetting("Surface", SURFACE_FILL, SURFACE_FILL, SURFACE_MIRROR);
    private final BooleanSetting fillRainbow = new BooleanSetting("Rainbow Fill", false);
    private final ModeSetting fillColorMode = new ModeSetting("Fill Color", COLOR_THEME, COLOR_THEME, COLOR_CUSTOM);
    private final ColorSetting fillColor = new ColorSetting("Fill Custom", 0xFFFF5454);
    private final SliderSetting fillAlpha = new SliderSetting("Fill Alpha", 0.80f, 0.0f, 1.0f, 0.01f);
    private final BooleanSetting keepShading = new BooleanSetting("Keep Shading", true);
    private final SliderSetting shadingStrength = new SliderSetting("Shading", 0.30f, 0.0f, 1.0f, 0.01f);
    private final SliderSetting rainbowSpeed = new SliderSetting("Rainbow Speed", 0.40f, 0.0f, 2.0f, 0.01f);
    private final SliderSetting rainbowScale = new SliderSetting("Rainbow Scale", 1.00f, 0.2f, 3.0f, 0.05f);

    private final ModeSetting mirrorColorMode = new ModeSetting("Mirror Color", COLOR_THEME, COLOR_THEME, COLOR_CUSTOM);
    private final ColorSetting mirrorColor = new ColorSetting("Mirror Custom", 0xFF8A98FF);
    private final SliderSetting mirrorMix = new SliderSetting("Mirror Mix", 0.49f, 0.0f, 0.5f, 0.01f);

    private final SliderSetting glowRadius = new SliderSetting("Glow Radius", 4.0f, 1.0f, 6.0f, 1.0f);
    private final SliderSetting glowExposure = new SliderSetting("Glow Power", 2.0f, 0.5f, 5.0f, 0.05f);
    private final BooleanSetting outerGlow = new BooleanSetting("Outer Glow", true);
    private final BooleanSetting autoGlowColor = new BooleanSetting("Auto Glow Color", false);
    private final SliderSetting glowSaturation = new SliderSetting("Auto Saturation", 1.40f, 0.5f, 3.0f, 0.05f);
    private final ModeSetting glowColorMode = new ModeSetting("Glow Color", COLOR_THEME, COLOR_THEME, COLOR_CUSTOM);
    private final ColorSetting glowColor1 = new ColorSetting("Glow Color 1", 0xFF8A98FF);
    private final ColorSetting glowColor2 = new ColorSetting("Glow Color 2", 0xFFFF6BAC);

    private final BooleanSetting trail = new BooleanSetting("Trail", true);
    private final SliderSetting trailFade = new SliderSetting("Trail Fade", 0.009f, 0.002f, 0.2f, 0.001f);
    private final SliderSetting trailRise = new SliderSetting("Trail Rise", 0.14f, 0.0f, 1.5f, 0.01f);
    private final SliderSetting trailSway = new SliderSetting("Trail Sway", 0.025f, 0.0f, 0.2f, 0.001f);
    private final SliderSetting trailTurbulence = new SliderSetting("Trail Turbulence", 0.0f, 0.0f, 0.6f, 0.01f);
    private final SliderSetting trailFlicker = new SliderSetting("Trail Flicker", 0.0f, 0.0f, 0.2f, 0.01f);
    private final BooleanSetting hitBurst = new BooleanSetting("Hit Burst", true);
    private final SliderSetting hitBurstStrength = new SliderSetting("Hit Power", 2.5f, 0.5f, 10.0f, 0.1f);

    private final BooleanSetting outline = new BooleanSetting("Outline", false);
    private final SliderSetting outlineThickness = new SliderSetting("Outline Width", 1.0f, 0.5f, 3.0f, 0.5f);
    private final ColorSetting outlineColor = new ColorSetting("Outline Custom", 0xFF8A98FF);

    private final ModeSetting shader3ColorMode = new ModeSetting("Shader3 Color", COLOR_GRADIENT, COLOR_GRADIENT, COLOR_AUTO, COLOR_CUSTOM);
    private final ColorSetting shader3Color1 = new ColorSetting("Shader3 Color 1", 0xFF8A98FF);
    private final ColorSetting shader3Color2 = new ColorSetting("Shader3 Color 2", 0xFFFF6BAC);
    private final SliderSetting cameraFollow = new SliderSetting("Camera Follow", 0.60f, 0.0f, 1.5f, 0.01f);
    private final SliderSetting swapBurst = new SliderSetting("Swap Burst", 2.5f, 0.0f, 6.0f, 0.1f);
    private final SliderSetting corePower = new SliderSetting("Core Power", 0.35f, 0.0f, 1.0f, 0.01f);

    public Hands() {
        addSettings(
                mode,
                surfaceMode, fillRainbow, fillColorMode, fillColor, fillAlpha, keepShading, shadingStrength, rainbowSpeed, rainbowScale,
                mirrorColorMode, mirrorColor, mirrorMix,
                glowRadius, glowExposure, outerGlow, autoGlowColor, glowSaturation, glowColorMode, glowColor1, glowColor2,
                trail, trailFade, trailRise, trailSway, trailTurbulence, trailFlicker, hitBurst, hitBurstStrength,
                outline, outlineThickness, outlineColor,
                shader3ColorMode, shader3Color1, shader3Color2, cameraFollow, swapBurst, corePower
        );

        surfaceMode.setVisible(this::isShader2);
        fillRainbow.setVisible(() -> isShader2() && isFillSurface());
        fillColorMode.setVisible(() -> isShader2() && isFillSurface() && !fillRainbow.get());
        fillColor.setVisible(() -> isShader2() && isFillSurface() && !fillRainbow.get() && fillColorMode.is(COLOR_CUSTOM));
        fillAlpha.setVisible(() -> isShader2() && isFillSurface());
        keepShading.setVisible(() -> isShader2() && isFillSurface());
        shadingStrength.setVisible(() -> isShader2() && isFillSurface() && keepShading.get());
        rainbowSpeed.setVisible(() -> isShader2() && isFillSurface() && fillRainbow.get());
        rainbowScale.setVisible(() -> isShader2() && isFillSurface() && fillRainbow.get());
        mirrorColorMode.setVisible(() -> isShader2() && isMirrorSurface());
        mirrorColor.setVisible(() -> isShader2() && isMirrorSurface() && mirrorColorMode.is(COLOR_CUSTOM));
        mirrorMix.setVisible(() -> isShader2() && isMirrorSurface());

        outerGlow.setVisible(this::isShader2);
        autoGlowColor.setVisible(this::isShader2);
        glowSaturation.setVisible(() -> isShader2() && autoGlowColor.get());
        glowColorMode.setVisible(() -> isShader2() && !autoGlowColor.get());
        glowColor1.setVisible(() -> isShader2() && !autoGlowColor.get() && glowColorMode.is(COLOR_CUSTOM));
        glowColor2.setVisible(() -> isShader2() && !autoGlowColor.get() && glowColorMode.is(COLOR_CUSTOM));

        trailFade.setVisible(this::isTrailVisible);
        trailRise.setVisible(this::isTrailVisible);
        trailSway.setVisible(this::isTrailVisible);
        trailTurbulence.setVisible(() -> isTrailVisible() && isShader2());
        trailFlicker.setVisible(() -> isTrailVisible() && isShader2());
        hitBurst.setVisible(this::isTrailVisible);
        hitBurstStrength.setVisible(() -> isTrailVisible() && hitBurst.get());

        outline.setVisible(this::isShader2);
        outlineThickness.setVisible(() -> isShader2() && outline.get());
        outlineColor.setVisible(() -> isShader2() && outline.get() && !autoGlowColor.get() && glowColorMode.is(COLOR_CUSTOM));

        shader3ColorMode.setVisible(this::isShader3);
        shader3Color1.setVisible(() -> isShader3() && !shader3ColorMode.is(COLOR_AUTO));
        shader3Color2.setVisible(() -> isShader3() && shader3ColorMode.is(COLOR_GRADIENT));
        cameraFollow.setVisible(() -> isShader3() && trail.get());
        swapBurst.setVisible(() -> isShader3() && trail.get());
        corePower.setVisible(this::isShader3);
    }

    @Override
    public void onEnable() {
        HandsRenderer.clearTrail();
    }

    @Override
    public void onDisable() {
        HandsRenderer.clearTrail();
    }

    public boolean isShader2() {
        return mode.is(MODE_SHADER_2);
    }

    public boolean isShader3() {
        return mode.is(MODE_SHADER_3);
    }

    public boolean isFillSurface() {
        return surfaceMode.is(SURFACE_FILL);
    }

    public boolean isMirrorSurface() {
        return surfaceMode.is(SURFACE_MIRROR);
    }

    public boolean isTrailVisible() {
        return trail.get();
    }

    public int getGlowRadiusPasses() {
        return Math.max(1, Math.min(6, Math.round(glowRadius.get())));
    }

    public float getGlowExposure() {
        return glowExposure.get();
    }

    public boolean isOuterGlow() {
        return isShader3() || outerGlow.get();
    }

    public boolean useAutoGlowColor() {
        return isShader3() ? shader3ColorMode.is(COLOR_AUTO) : autoGlowColor.get();
    }

    public float getGlowSaturation() {
        return isShader3() ? 1.4f : glowSaturation.get();
    }

    public int getGlowColor1() {
        if (isShader3()) {
            return shader3ColorMode.is(COLOR_AUTO) ? getThemePrimaryColor() : shader3Color1.getColor().getRGB();
        }
        return glowColorMode.is(COLOR_THEME) ? getThemePrimaryColor() : glowColor1.getColor().getRGB();
    }

    public int getGlowColor2() {
        if (isShader3()) {
            if (shader3ColorMode.is(COLOR_AUTO)) {
                return getThemeSecondaryColor();
            }
            return shader3ColorMode.is(COLOR_GRADIENT) ? shader3Color2.getColor().getRGB() : shader3Color1.getColor().getRGB();
        }
        return glowColorMode.is(COLOR_THEME) ? getThemeSecondaryColor() : glowColor2.getColor().getRGB();
    }

    public boolean hasTrail() {
        return trail.get();
    }

    public float getTrailFade() {
        return trailFade.get();
    }

    public float getTrailRise() {
        return trailRise.get();
    }

    public float getTrailSway() {
        return trailSway.get();
    }

    public float getTrailTurbulence() {
        return isShader3() ? 0.0f : trailTurbulence.get();
    }

    public float getTrailFlicker() {
        return isShader3() ? 0.0f : trailFlicker.get();
    }

    public boolean useHitBurst() {
        return hitBurst.get();
    }

    public float getHitBurstStrength() {
        return hitBurstStrength.get();
    }

    public float getCameraFollow() {
        return isShader3() ? cameraFollow.get() : 0.0f;
    }

    public boolean useSwapBurst() {
        return isShader3() && swapBurst.get() > 0.0f;
    }

    public float getSwapBurstStrength() {
        return swapBurst.get();
    }

    public boolean hasOutline() {
        return isShader2() && outline.get();
    }

    public float getOutlineThickness() {
        return outlineThickness.get();
    }

    public int getOutlineColor() {
        return glowColorMode.is(COLOR_THEME) ? getThemePrimaryColor() : outlineColor.getColor().getRGB();
    }

    public int getOutlineColorMode() {
        if (isShader2() && isFillSurface() && fillRainbow.get()) {
            return 1;
        }
        return useAutoGlowColor() ? 2 : 0;
    }

    public int getFillMode() {
        if (isShader3()) {
            return 0;
        }
        return isMirrorSurface() ? 2 : 1;
    }

    public int getFillColor() {
        if (isMirrorSurface()) {
            return mirrorColorMode.is(COLOR_THEME) ? getThemePrimaryColor() : mirrorColor.getColor().getRGB();
        }
        return fillColorMode.is(COLOR_THEME) ? getThemePrimaryColor() : fillColor.getColor().getRGB();
    }

    public float getFillAlpha() {
        return fillAlpha.get();
    }

    public boolean useFillRainbow() {
        return isShader2() && isFillSurface() && fillRainbow.get();
    }

    public float getRainbowSpeed() {
        return rainbowSpeed.get();
    }

    public float getRainbowScale() {
        return rainbowScale.get();
    }

    public boolean keepFillShading() {
        return isShader2() && isFillSurface() && keepShading.get();
    }

    public float getShadingStrength() {
        return shadingStrength.get();
    }

    public float getMirrorMix() {
        return mirrorMix.get();
    }

    public float getCorePower() {
        return isShader3() ? corePower.get() : 0.0f;
    }

    public int getThemePrimaryColor() {
        return ClientColors.GRADIENT_START.getRGB();
    }

    public int getThemeSecondaryColor() {
        return ClientColors.GRADIENT_END.getRGB();
    }

    public int getHeldItemColor() {
        if (mc.player == null) {
            return getThemePrimaryColor();
        }
        ItemStack stack = mc.player.getMainHandStack();
        if (stack.isEmpty()) {
            stack = mc.player.getOffHandStack();
        }
        if (stack.isEmpty()) {
            return getThemePrimaryColor();
        }
        int color = resolveStackColor(stack);
        return color == -1 ? getThemePrimaryColor() : (0xFF000000 | color);
    }

    private int resolveStackColor(ItemStack stack) {
        DyedColorComponent dyed = stack.get(DataComponentTypes.DYED_COLOR);
        if (dyed != null) {
            return dyed.rgb();
        }

        Text customName = stack.get(DataComponentTypes.CUSTOM_NAME);
        int nameColor = readTextColor(customName);
        if (nameColor != -1) {
            return nameColor;
        }

        String path = Registries.ITEM.getId(stack.getItem()).getPath().toLowerCase(Locale.ROOT);
        if (path.contains("diamond")) return 0x55DDE0;
        if (path.contains("netherite")) return 0x5B4A67;
        if (path.contains("gold")) return 0xFFD45A;
        if (path.contains("iron")) return 0xD8DEE8;
        if (path.contains("stone")) return 0x8A8A8A;
        if (path.contains("wood")) return 0xB98245;
        if (path.contains("emerald")) return 0x35D06F;
        if (path.contains("redstone")) return 0xE23B3B;
        if (path.contains("lapis")) return 0x3156D4;
        if (path.contains("copper")) return 0xD17A45;
        if (path.contains("amethyst")) return 0xB06CFF;
        if (path.contains("ender") || path.contains("prismarine")) return 0x45E0C0;
        if (path.contains("blaze") || path.contains("fire_charge")) return 0xFF8A2A;
        if (path.contains("breeze") || path.contains("wind")) return 0xB8E8FF;
        return -1;
    }

    private int readTextColor(Text text) {
        if (text == null) {
            return -1;
        }
        TextColor color = text.getStyle().getColor();
        if (color != null) {
            return color.getRgb();
        }
        for (Text sibling : text.getSiblings()) {
            int siblingColor = readTextColor(sibling);
            if (siblingColor != -1) {
                return siblingColor;
            }
        }
        return -1;
    }
}
