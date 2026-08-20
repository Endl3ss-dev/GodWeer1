package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.screen.hud.IScalable;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ArrayListRenderer implements IRenderer, IScalable, IConfigurableHud {
    private final Minecraft mc = Minecraft.getInstance();
    private final DragSetting drag;

    private float totalHeight = 0;
    private float maxWidth = 0;

    @Setter
    @Getter
    private float glowRadius = 10f;

    @Setter
    @Getter
    private float glowAlpha = 0.5f;

    @Setter
    @Getter
    private boolean scalingEnabled = false;

    @Setter
    @Getter
    private float scale = 0.9f;

    @Setter
    @Getter
    private boolean gradientText = true;

    @Setter
    @Getter
    private boolean enableOutline = false;

    @Setter
    @Getter
    private float outlineWidth = 1.0f;

    @Setter
    @Getter
    private boolean blurEnabled = true;

    @Setter
    @Getter
    private float blurStrength = 10f;

    public boolean showCombat = true;
    public boolean showMovement = true;
    public boolean showRender = true;
    public boolean showPlayer = true;
    public boolean showMisc = true;
    public boolean rightAlign = false;
    public boolean showShadow = true;
    public float fontSize = 8f;
    public float spacing = 11f;
    public float padding = 3f;

    public ArrayListRenderer(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Font Size", DragComponent.SettingType.SLIDER,
                        () -> this.fontSize,
                        v -> this.fontSize = v,
                        6f, 12f, 0.5f),

                new DragComponent.HudSetting("Spacing", DragComponent.SettingType.SLIDER,
                        () -> this.spacing,
                        v -> this.spacing = v,
                        8f, 16f, 0.5f),

                new DragComponent.HudSetting("Padding", DragComponent.SettingType.SLIDER,
                        () -> this.padding,
                        v -> this.padding = v,
                        0f, 10f, 0.5f),

                new DragComponent.HudSetting("Glow Radius", DragComponent.SettingType.SLIDER,
                        () -> this.glowRadius,
                        v -> this.glowRadius = v,
                        0f, 20f, 1f),

                new DragComponent.HudSetting("Glow Alpha", DragComponent.SettingType.SLIDER,
                        () -> this.glowAlpha,
                        v -> this.glowAlpha = v,
                        0f, 1f, 0.05f),

                new DragComponent.HudSetting("Blur Strength", DragComponent.SettingType.SLIDER,
                        () -> this.blurStrength,
                        v -> this.blurStrength = v,
                        0f, 20f, 1f),

                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER,
                        () -> this.outlineWidth,
                        v -> this.outlineWidth = v,
                        0.5f, 3.0f, 0.1f),

                new DragComponent.HudSetting("Gradient Text", DragComponent.SettingType.BOOLEAN,
                        () -> this.gradientText ? 1f : 0f,
                        v -> this.gradientText = v > 0.5f),

                new DragComponent.HudSetting("Show Shadow", DragComponent.SettingType.BOOLEAN,
                        () -> this.showShadow ? 1f : 0f,
                        v -> this.showShadow = v > 0.5f),

                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN,
                        () -> this.enableOutline ? 1f : 0f,
                        v -> this.enableOutline = v > 0.5f),

                new DragComponent.HudSetting("Show Combat", DragComponent.SettingType.BOOLEAN,
                        () -> this.showCombat ? 1f : 0f,
                        v -> this.showCombat = v > 0.5f),

                new DragComponent.HudSetting("Show Movement", DragComponent.SettingType.BOOLEAN,
                        () -> this.showMovement ? 1f : 0f,
                        v -> this.showMovement = v > 0.5f),

                new DragComponent.HudSetting("Show Render", DragComponent.SettingType.BOOLEAN,
                        () -> this.showRender ? 1f : 0f,
                        v -> this.showRender = v > 0.5f),

                new DragComponent.HudSetting("Show Player", DragComponent.SettingType.BOOLEAN,
                        () -> this.showPlayer ? 1f : 0f,
                        v -> this.showPlayer = v > 0.5f),

                new DragComponent.HudSetting("Show Misc", DragComponent.SettingType.BOOLEAN,
                        () -> this.showMisc ? 1f : 0f,
                        v -> this.showMisc = v > 0.5f)
        );
    }

    @Override
    public void render(Render2DEvent event) {
        MatrixStack stack = event.getMatrix();
        if (mc.player == null || mc.world == null) return;

        List<Module> activeModules = Excellent.inst().moduleManager().values().stream()
                .filter(module -> module.getAnimation().getValue() > 0.01f)
                .filter(this::shouldShowCategory)
                .sorted(Comparator.comparingDouble(m -> -Fonts.SF_MEDIUM.getWidth(m.getName(), fontSize)))
                .collect(Collectors.toList());

        if (activeModules.isEmpty()) return;

        float currentY = drag.position.y;
        float x = drag.position.x;
        boolean isRightSide = rightAlign || x > mc.getMainWindow().getScaledWidth() / 2f;

        float calculatedMaxWidth = 0;
        float calculatedHeight = 0;

        for (Module module : activeModules) {
            float moduleWidth = Fonts.SF_MEDIUM.getWidth(module.getName(), fontSize) + padding * 2;
            if (moduleWidth > calculatedMaxWidth) {
                calculatedMaxWidth = moduleWidth;
            }
            calculatedHeight += spacing * module.getAnimation().getValue();
        }

        maxWidth = calculatedMaxWidth;
        totalHeight = calculatedHeight;

        drag.size.set(maxWidth, totalHeight);
        if (drag.position.x <= 0 && drag.position.y <= 0)
            drag.position.set(3, 36.5f);

        applyScale(stack, x + maxWidth / 2f, currentY + totalHeight / 2f);

        Theme theme = Theme.getInstance();
        int themeColor = theme != null ? theme.clientColor() : ColorUtil.rgba(100, 180, 255, 255);

        int index = 0;
        for (Module module : activeModules) {
            double anim = module.getAnimation().getValue();
            float moduleHeight = spacing * (float) anim;

            if (moduleHeight > 0.1f && anim > 0.01f) {
                float textWidth = Fonts.SF_MEDIUM.getWidth(module.getName(), fontSize);
                float textHeight = Fonts.SF_MEDIUM.getHeight(module.getName(), fontSize);

                float textX;
                if (isRightSide) {
                    textX = (float) (x + maxWidth - textWidth - padding + (textWidth * (1 - anim)));
                } else {
                    textX = (float) (x + padding - (textWidth * (1 - anim)));
                }

                float textY = currentY + (moduleHeight / 2f) - (textHeight / 2f);

                int currentColor = gradientText ? getGradientColor(index, activeModules.size(), themeColor) : themeColor;
                int glowColor = ColorUtil.multAlpha(currentColor, (float) (glowAlpha * anim));

                if (showShadow) {
                    RenderUtil.Shadow.drawShadow(
                            stack,
                            textX - 1,
                            textY - 1,
                            textWidth + 2,
                            textHeight + 2,
                            glowRadius,
                            (float) anim * glowAlpha,
                            glowColor,
                            Round.of(3)
                    );
                }

                if (enableOutline) {
                    int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                    float halfWidth = outlineWidth / 2f;
                    RenderUtil.Rounded.roundedOutline(
                            stack,
                            textX - padding - halfWidth,
                            textY - padding / 2 - halfWidth,
                            textWidth + padding * 2 + outlineWidth,
                            textHeight + padding + outlineWidth,
                            outlineWidth,
                            ColorUtil.multAlpha(outlineColor, (float) anim),
                            Round.of(3)
                    );
                }

                stack.push();
                stack.translate(0, 0, 1);

                int textColorWithAlpha = ColorUtil.multAlpha(currentColor, (float) anim);
                Fonts.SF_MEDIUM.draw(stack, module.getName(), textX, textY, textColorWithAlpha, fontSize);

                stack.pop();
            }

            currentY += moduleHeight;
            index++;
        }

        removeScale(stack);
    }

    private boolean shouldShowCategory(Module module) {
        Category category = module.getCategory();
        return switch (category) {
            case COMBAT -> showCombat;
            case MOVEMENT -> showMovement;
            case RENDER -> showRender;
            case PLAYER -> showPlayer;
            case MISC -> showMisc;
            default -> true;
        };
    }

    private int getGradientColor(int index, int total, int baseColor) {
        if (total <= 1) return baseColor;

        float progress = (float) index / Math.max(1, total - 1);

        int r = (baseColor >> 16) & 0xFF;
        int g = (baseColor >> 8) & 0xFF;
        int b = baseColor & 0xFF;

        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        float hueShift = progress * 0.15f;
        hsb[0] = (hsb[0] + hueShift) % 1.0f;
        hsb[1] = Math.min(1.0f, hsb[1] * (1.0f + progress * 0.2f));
        hsb[2] = Math.min(1.0f, hsb[2] * (1.0f - progress * 0.1f));

        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        return (0xFF << 24) | (rgb & 0xFFFFFF);
    }
}