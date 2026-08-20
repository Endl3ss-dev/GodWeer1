package org.privates.client.managers.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.input.KeyboardPressEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.managers.module.settings.impl.SliderSettingRand;
import org.privates.client.screen.notification.Notification;
import org.privates.client.screen.notification.NotificationColors;
import org.privates.client.screen.notification.NotificationType;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.*;

import java.util.Random;

@Category3
@ModuleInfo(name = "TEST", category = Category.RENDER)
public class TEST extends Module {

    private final ModeSetting mode = new ModeSetting(this, "Mode",
            "Cobweb",
            "Biomorph Cells",
            "Cosmic Nebula",
            "Crystal Facets",
            "Pixelated Glass",
            "Animated Waves",
            "Hexagonal Blur",
            "Frosted Glass",
            "Liquid Glass",
            "Distorted Glass",
            "Saturation",
            "Glow Enhancement",
            "Smooth Motion",
            "Cosmic",
            "Summer",
            "Winter",
            "Autumn");

    private final SliderSetting cobwebSpeed = new SliderSetting(this, "Speed", 0.4f, 0.05f, 2.0f, 0.05f).setVisible(() -> mode.is("Cobweb"));
    private final SliderSetting cobwebIntensity = new SliderSetting(this, "Intensity", 1.0f, 0.2f, 3.0f, 0.1f).setVisible(() -> mode.is("Cobweb"));

    private final SliderSetting cellScale = new SliderSetting(this, "Cell Scale", 5.0f, 2.0f, 15.0f, 0.5f).setVisible(() -> mode.is("Biomorph Cells"));
    private final SliderSetting pulseSpeed = new SliderSetting(this, "Pulse Speed", 1.0f, 0.1f, 4.0f, 0.1f).setVisible(() -> mode.is("Biomorph Cells"));

    private final SliderSetting nebulaDensity = new SliderSetting(this, "Nebula Density", 1.0f, 0.3f, 2.5f, 0.1f).setVisible(() -> mode.is("Cosmic Nebula"));
    private final SliderSetting distIntensity = new SliderSetting(this, "Intensity", 1.0f, 0.1f, 3.0f, 0.1f).setVisible(() -> mode.is("Distorted Glass"));
    private final SliderSetting facetSize = new SliderSetting(this, "Facet Size", 10f, 5f, 30f, 1f).setVisible(() -> mode.is("Crystal Facets"));
    private final SliderSetting pixelSize = new SliderSetting(this, "Pixel Size", 4f, 1f, 20f, 1f).setVisible(() -> mode.is("Pixelated Glass"));
    private final SliderSetting waveStrength = new SliderSetting(this, "Wave Strength", 1.0f, 0.1f, 5.0f, 0.1f).setVisible(() -> mode.is("Animated Waves"));
    private final SliderSetting blurRadius = new SliderSetting(this, "Blur Radius", 2f, 0.5f, 10f, 0.5f).setVisible(() -> mode.is("Hexagonal Blur"));
    private final SliderSetting frostAmount = new SliderSetting(this, "Frost Amount", 1.0f, 0.1f, 3.0f, 0.1f).setVisible(() -> mode.is("Frosted Glass"));

    private final SliderSetting saturationValue = new SliderSetting(this, "Saturation", 1.5f, 0.0f, 3.0f, 0.1f).setVisible(() -> mode.is("Saturation"));
    private final SliderSetting brightness = new SliderSetting(this, "Brightness", 1.0f, 0.5f, 2.0f, 0.1f).setVisible(() -> mode.is("Saturation"));
    private final SliderSetting contrast = new SliderSetting(this, "Contrast", 1.0f, 0.5f, 2.0f, 0.1f).setVisible(() -> mode.is("Saturation"));

    private final SliderSetting glowIntensity = new SliderSetting(this, "Glow Intensity", 1.0f, 0.0f, 3.0f, 0.1f).setVisible(() -> mode.is("Glow Enhancement"));
    private final SliderSetting bloomThreshold = new SliderSetting(this, "Bloom Threshold", 0.6f, 0.1f, 0.9f, 0.05f).setVisible(() -> mode.is("Glow Enhancement"));
    private final SliderSetting shadowDepth = new SliderSetting(this, "Shadow Depth", 1.0f, 0.0f, 2.0f, 0.1f).setVisible(() -> mode.is("Glow Enhancement"));
    private final SliderSetting smoothness = new SliderSetting(this, "Smoothness", 0.55f, 0.05f, 1.0f, 0.05f).setVisible(() -> mode.is("Smooth Motion"));
    private final SliderSetting sharpness = new SliderSetting(this, "Sharpness", 0.45f, 0.0f, 1.0f, 0.05f).setVisible(() -> mode.is("Smooth Motion"));

    // Season shader settings
    private final SliderSetting cosmicIntensity = new SliderSetting(this, "Intensity", 0.7f, 0.0f, 1.0f, 0.05f).setVisible(() -> mode.is("Cosmic"));
    private final SliderSetting summerIntensity = new SliderSetting(this, "Intensity", 0.7f, 0.0f, 1.0f, 0.05f).setVisible(() -> mode.is("Summer"));
    private final SliderSetting winterIntensity = new SliderSetting(this, "Intensity", 0.7f, 0.0f, 1.0f, 0.05f).setVisible(() -> mode.is("Winter"));
    private final SliderSetting autumnIntensity = new SliderSetting(this, "Intensity", 0.7f, 0.0f, 1.0f, 0.05f).setVisible(() -> mode.is("Autumn"));

    private final SliderSetting size = new SliderSetting(this, "Size", 200f, 50f, 400f, 10f);
    private final SliderSetting radius = new SliderSetting(this, "Radius", 20f, 0f, 50f, 1f);

    private final SliderSettingRand messageDelay = new SliderSettingRand(this, "Message Delay", 0f, 500f, 0f, 500f, 10f, "Задержка отправки сообщения в мс");

    // Test notification binds
    private final BindSetting testPotionNotif = new BindSetting(this, "Test Potion Notif", GLFW.GLFW_KEY_UNKNOWN);
    private final BindSetting testUseNotif = new BindSetting(this, "Test Use Notif", GLFW.GLFW_KEY_UNKNOWN);
    private final BindSetting testHelperNotif = new BindSetting(this, "Test Helper Notif", GLFW.GLFW_KEY_UNKNOWN);

    private final Random random = new Random();
    private long lastMessageTime = 0;
    private long currentDelay = 0;
    private float time = 0.0f;

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null || !isEnabled()) return;

        MatrixStack matrix = event.getMatrix();

        float sw = mc.getMainWindow().getScaledWidth();
        float sh = mc.getMainWindow().getScaledHeight();

        float rectSize = 2000;
        float x = (sw - rectSize) / 2f;
        float y = (sh - rectSize) / 2f - 50f;
        float rad = radius.getValue();

        int themeColor = Theme.getInstance().clientColor();
        
        // Update time for animated shaders
        time += 0.016f; // ~60 FPS

        switch (mode.getValue()) {

            case "Cobweb" -> {
                CobwebRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, x, y, rectSize, rectSize,
                                ColorUtil.getColor(255, 255, 255, 255), Round.of(rad)));
                CobwebRenderer.draw(cobwebSpeed.getValue(), cobwebIntensity.getValue());
            }

            case "Biomorph Cells" -> {
                BiomorphCellsRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, x, y, rectSize, rectSize,
                                ColorUtil.getColor(255, 255, 255, 255), Round.of(rad)));
                BiomorphCellsRenderer.draw(cellScale.getValue(), pulseSpeed.getValue());
            }

            case "Cosmic Nebula" -> {
                CosmicNebulaRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, x, y, rectSize, rectSize,
                                ColorUtil.getColor(255, 255, 255, 255), Round.of(rad)));
                CosmicNebulaRenderer.draw(nebulaDensity.getValue());
            }

            case "Liquid Glass" -> {
                LiquidGlassRenderer.captureBackground();
                LiquidGlassRenderer.drawLiquidGlass(matrix, x, y, rectSize, rectSize, rad / 6f,
                        Excellent.inst().moduleManager().get(Theme.class).liquidGlassStrength().getValue() * 1000f,
                        Excellent.inst().moduleManager().get(Theme.class).blurStrength().getValue() / 33f,
                        ColorUtil.getColor(255, 255, 255, .3f));
            }

            case "Distorted Glass" -> {
                DistortedGlassRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, x, y, rectSize, rectSize, themeColor, Round.of(rad)));
                DistortedGlassRenderer.draw(distIntensity.getValue());
            }

            case "Crystal Facets" -> {
                CrystalFacetsRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, x, y, rectSize, rectSize, themeColor, Round.of(rad)));
                CrystalFacetsRenderer.draw(facetSize.getValue());
            }

            case "Pixelated Glass" -> {
                PixelatedGlassRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, x, y, rectSize, rectSize, themeColor, Round.of(rad)));
                PixelatedGlassRenderer.draw(pixelSize.getValue());
            }

            case "Animated Waves" -> {
                AnimatedWavesRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, x, y, rectSize, rectSize, themeColor, Round.of(rad)));
                AnimatedWavesRenderer.draw(waveStrength.getValue());
            }

            case "Hexagonal Blur" -> {
                HexagonalBlurRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, x, y, rectSize, rectSize, themeColor, Round.of(rad)));
                HexagonalBlurRenderer.draw(blurRadius.getValue());
            }

            case "Frosted Glass" -> {
                FrostedGlassRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, x, y, rectSize, rectSize, themeColor, Round.of(rad)));
                FrostedGlassRenderer.draw(frostAmount.getValue());
            }

            case "Saturation" -> {
                SaturationRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, x, y, rectSize, rectSize, themeColor, Round.of(rad)));
                SaturationRenderer.draw(saturationValue.getValue(), brightness.getValue(), contrast.getValue());
            }

            case "Glow Enhancement" -> {
                GlowEnhancementRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, x, y, rectSize, rectSize, themeColor, Round.of(rad)));
                GlowEnhancementRenderer.draw(glowIntensity.getValue(), bloomThreshold.getValue(), shadowDepth.getValue());
            }

            case "Smooth Motion" -> {
                SmoothMotionRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, -10, -10, sw + 20, sh + 20,
                                ColorUtil.getColor(255, 255, 255, 255), Round.of(0)));
                SmoothMotionRenderer.draw(smoothness.getValue(), sharpness.getValue(), time);
            }
            
            case "Cosmic" -> {
                CosmicRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, -10, -10, sw + 20, sh + 20,
                                ColorUtil.getColor(255, 255, 255, 255), Round.of(0)));
                CosmicRenderer.draw(time, cosmicIntensity.getValue());
            }
            
            case "Summer" -> {
                SummerRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, -10, -10, sw + 20, sh + 20,
                                ColorUtil.getColor(255, 255, 255, 255), Round.of(0)));
                SummerRenderer.draw(time, summerIntensity.getValue());
            }
            
            case "Winter" -> {
                WinterRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, -10, -10, sw + 20, sh + 20,
                                ColorUtil.getColor(255, 255, 255, 255), Round.of(0)));
                WinterRenderer.draw(time, winterIntensity.getValue());
            }
            
            case "Autumn" -> {
                AutumnRenderer.registerRenderCall(() ->
                        RenderUtil.Rounded.roundedRect(matrix, -10, -10, sw + 20, sh + 20,
                                ColorUtil.getColor(255, 255, 255, 255), Round.of(0)));
                AutumnRenderer.draw(time, autumnIntensity.getValue());
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMessageTime >= currentDelay) {
            float minDelay = messageDelay.getValue().getMinValue();
            float maxDelay = messageDelay.getValue().getMaxValue();
            currentDelay = (long) (minDelay + (maxDelay - minDelay) * random.nextFloat());
            lastMessageTime = currentTime;
        }

        mc.fontRenderer.drawStringWithShadow(matrix, "FPS: " + Minecraft.getDebugFPS(), 5, 5, 0xFFFFFFFF);
    }

    @EventHandler
    public void onKeyPress(KeyboardPressEvent event) {
        if (event.getScreen() != null) return;
        
        if (event.isKey(testPotionNotif.getValue())) {
            sendTestPotionNotification();
        } else if (event.isKey(testUseNotif.getValue())) {
            sendTestUseNotification();
        } else if (event.isKey(testHelperNotif.getValue())) {
            sendTestHelperNotification();
        }
    }

    /**
     * Send test potion notification with effect icon and color
     */
    private void sendTestPotionNotification() {
        String[] potionTests = {
            "Speed II ended",
            "Strength applied",
            "Regeneration II active",
            "Fire Resistance ended"
        };
        
        String text = potionTests[random.nextInt(potionTests.length)];
        Integer color = NotificationColors.getEffectColor(Effects.SPEED);
        
        Excellent.inst().notifManager().add(
            new Notification(NotificationType.EFFECT_END, text, 3000f, Effects.SPEED, color)
        );
    }

    /**
     * Send test use tracker notification with item icon and color
     */
    private void sendTestUseNotification() {
        String[] useTests = {
            "Golden Apple used",
            "Totem activated",
            "Ender Pearl thrown",
            "Chorus Fruit eaten"
        };
        
        String text = useTests[random.nextInt(useTests.length)];
        ItemStack item = new ItemStack(Items.GOLDEN_APPLE);
        Integer color = NotificationColors.getItemColor(Items.GOLDEN_APPLE);
        
        Excellent.inst().notifManager().add(
            new Notification(NotificationType.ITEM_PICKUP, text, 3000f, item, color)
        );
    }

    /**
     * Send test helper notification with various colors
     */
    private void sendTestHelperNotification() {
        String[] helperTests = {
            "Enemy drinking potion",
            "Low health warning",
            "Elytra durability low",
            "Enemy aiming at you"
        };
        
        String text = helperTests[random.nextInt(helperTests.length)];
        Integer color = NotificationColors.getDefaultColor(NotificationType.WARNING);
        
        Excellent.inst().notifManager().add(
            new Notification(NotificationType.WARNING, text, 3000f, null, color)
        );
    }
}
