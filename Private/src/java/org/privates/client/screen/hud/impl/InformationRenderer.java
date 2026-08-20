package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.interfaces.IWindow;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.screen.hud.IScalable;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.player.MoveUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.text.SimpleDateFormat;
import java.util.*;

public class InformationRenderer implements IRenderer, IWindow, IScalable, IConfigurableHud {
    private final DragSetting drag;
    @Setter
    @Getter
    private boolean blurEnabled = true;
    @Setter
    @Getter
    private float blurStrength = 10f;
    @Setter
    @Getter
    private boolean scalingEnabled = false;
    @Setter
    @Getter
    private float scale = 0.9f;
    @Setter
    @Getter
    private boolean enableOutline = false;
    @Setter
    @Getter
    private float outlineWidth = 1.0f;

    public boolean dynamicRects = false;

    private final List<Boolean> displayElements = new ArrayList<>(Arrays.asList(true, true, true, true, true, true, true, true));

    private float animatedX, animatedY, animatedWidth, animatedHeight;
    private float animatedTopWidth, animatedBottomWidth, animatedTopX, animatedBottomX;

    private float lastX = 0;
    private float lastY = 0;
    private float smoothVelocityX = 0;
    private float smoothVelocityY = 0;
    private float currentRotation = 0;
    private float targetRotation = 0;
    private float springOffsetX = 0;
    private float springOffsetY = 0;
    private float dragScaleEffect = 1.0f;
    private boolean isDragging = false;
    private long lastMoveTime = 0;

    public InformationRenderer(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER, () -> this.scale, v -> this.scale = v, 0.5f, 2.0f, 0.1f),
                new DragComponent.HudSetting("Display Elements", DragComponent.SettingType.MULTI_BOOLEAN,
                        () -> 0f,
                        v -> {
                        },
                        Arrays.asList("XYZ", "FPS", "Ping", "BPS", "Time", "TPS", "Direction", "Biome"),
                        displayElements),
                new DragComponent.HudSetting("Dynamic Rects", DragComponent.SettingType.BOOLEAN, () -> this.dynamicRects ? 1f : 0f, v -> this.dynamicRects = v > 0.5f),
                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN, () -> this.enableOutline ? 1f : 0f, v -> this.enableOutline = v > 0.5f),
                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER, () -> this.outlineWidth, v -> this.outlineWidth = v, 0.5f, 3.0f, 0.1f)
        );
    }

    private float getResolutionScale() {
        int w = mc.getMainWindow().getWidth(), h = mc.getMainWindow().getHeight();
        if (w >= 3840 && h >= 2160) return 1f;
        else if (w >= 2560 && h >= 1440) return 1.0f;
        else if (w >= 1920 || h >= 1080) return 1f;
        else return 1f;
    }

    private float smoothLerp(float current, float target, float speed) {
        float diff = target - current;
        float factor = 1.0f - (float) Math.pow(1.0f - speed, 1.5f);
        return current + diff * factor;
    }

    private float elasticLerp(float current, float target, float speed, float elasticity) {
        float diff = target - current;
        float velocity = diff * speed;
        if (Math.abs(diff) < 0.5f) {
            velocity += diff * elasticity * 0.3f;
        }
        return current + velocity;
    }

    private void updateDragAnimation(float x, float y, float width, float height) {
        float instantVelocityX = x - lastX;
        float instantVelocityY = y - lastY;

        boolean isMoving = Math.abs(instantVelocityX) > 0.01f || Math.abs(instantVelocityY) > 0.01f;

        if (isMoving) {
            lastMoveTime = System.currentTimeMillis();
            isDragging = true;
        } else if (System.currentTimeMillis() - lastMoveTime > 50) {
            isDragging = false;
        }

        float velocitySmoothing = isDragging ? 0.25f : 0.08f;
        smoothVelocityX = smoothLerp(smoothVelocityX, instantVelocityX, velocitySmoothing);
        smoothVelocityY = smoothLerp(smoothVelocityY, instantVelocityY, velocitySmoothing);

        float tiltSensitivity = 1.8f;
        float maxTilt = 15.0f;

        float horizontalInfluence = smoothVelocityX * tiltSensitivity;
        float verticalInfluence = smoothVelocityY * 0.3f;

        targetRotation = horizontalInfluence - verticalInfluence;
        targetRotation = Math.max(-maxTilt, Math.min(maxTilt, targetRotation));

        float rotationSpeed = isDragging ? 0.18f : 0.06f;
        currentRotation = elasticLerp(currentRotation, targetRotation, rotationSpeed, 0.15f);

        if (!isDragging && Math.abs(currentRotation) < 0.1f) {
            currentRotation *= 0.9f;
        }

        float springStrength = 0.12f;
        float springDamping = 0.85f;

        float targetSpringX = -smoothVelocityX * 0.3f;
        float targetSpringY = -smoothVelocityY * 0.2f;

        springOffsetX = springOffsetX * springDamping + (targetSpringX - springOffsetX) * springStrength;
        springOffsetY = springOffsetY * springDamping + (targetSpringY - springOffsetY) * springStrength;

        float maxSpring = 5.0f;
        springOffsetX = Math.max(-maxSpring, Math.min(maxSpring, springOffsetX));
        springOffsetY = Math.max(-maxSpring, Math.min(maxSpring, springOffsetY));

        float targetDragScale = isDragging ? 1.02f : 1.0f;
        dragScaleEffect = smoothLerp(dragScaleEffect, targetDragScale, 0.15f);

        lastX = x;
        lastY = y;
    }

    private void applyDragTransform(MatrixStack matrix, float x, float y, float width, float height) {
        float centerX = x + width / 2f;
        float pivotX = centerX;
        float pivotY = y + height * 0.15f;

        matrix.translate(centerX, y + height / 2f, 0);
        matrix.scale(dragScaleEffect, dragScaleEffect, 1);
        matrix.translate(-centerX, -(y + height / 2f), 0);

        matrix.translate(springOffsetX, springOffsetY, 0);

        if (Math.abs(currentRotation) > 0.01f) {
            matrix.translate(pivotX, pivotY, 0);
            matrix.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(currentRotation));
            matrix.translate(-pivotX, -pivotY, 0);
        }
    }

    private static class ElementData {
        String icon;
        String iconFont;
        String text;
        float width;

        ElementData(String icon, String iconFont, String text, float width) {
            this.icon = icon;
            this.iconFont = iconFont;
            this.text = text;
            this.width = width;
        }
    }

    @Override
    public void render(Render2DEvent e) {
        float resScale = getResolutionScale();
        float finalScale = resScale * scale;
        MatrixStack s = e.getMatrix();
        float x = drag.position.x, y = drag.position.y;

        String cord = String.format("XYZ: %d, %d, %d", (int) mc.player.getPosX(), (int) mc.player.getPosY(), (int) mc.player.getPosZ());
        String fps = String.format("%d FPS", Minecraft.getDebugFPS());
        String ping = String.format("%d MS", mc.isSingleplayer() ? 0 : Objects.requireNonNull(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(mc.player.getGameProfile().getId())).getResponseTime());
        String bps = String.format("%.1f BPS", MoveUtil.speed() * 20);
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String tps = String.format("%.1f TPS", 20.0f);

        float yaw = mc.player.rotationYaw % 360;
        if (yaw < 0) yaw += 360;
        String dir;
        if (yaw >= 337.5 || yaw < 22.5) dir = "S";
        else if (yaw < 67.5) dir = "SW";
        else if (yaw < 112.5) dir = "W";
        else if (yaw < 157.5) dir = "NW";
        else if (yaw < 202.5) dir = "N";
        else if (yaw < 247.5) dir = "NE";
        else if (yaw < 292.5) dir = "E";
        else dir = "SE";
        String direction = String.format("%s [%.0f°]", dir, yaw);

        Biome biome = mc.world.getBiome(mc.player.getPosition());
        String biomeText = mc.world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(biome).getPath().replace("_", " ");

        float font = 6.5f * finalScale, icon = 9f * finalScale, pad = 5f * finalScale, iconPad = 3f * finalScale, space = 5f * finalScale, gap = 2f * finalScale, line = 12f * finalScale;

        Map<String, ElementData> allElements = new LinkedHashMap<>();
        allElements.put("XYZ", new ElementData("Q", "icon", cord, Fonts.icon.getWidth("Q", icon) + iconPad + Fonts.SF_MEDIUM.getWidth(cord, font)));
        allElements.put("FPS", new ElementData("g", "HUD", fps, Fonts.HUD.getWidth("g", icon) + iconPad + Fonts.SF_MEDIUM.getWidth(fps, font)));
        allElements.put("Ping", new ElementData("f", "HUD", ping, Fonts.HUD.getWidth("f", icon) + iconPad + Fonts.SF_MEDIUM.getWidth(ping, font)));
        allElements.put("BPS", new ElementData("i", "HUD", bps, Fonts.HUD.getWidth("i", icon) + iconPad + Fonts.SF_MEDIUM.getWidth(bps, font)));
        allElements.put("Time", new ElementData("f", "iconf", time, Fonts.iconf.getWidth("f", icon) + iconPad + Fonts.SF_MEDIUM.getWidth(time, font)));
        allElements.put("TPS", new ElementData("m", "icon_new", tps, Fonts.icon_new.getWidth("m", icon) + iconPad + Fonts.SF_MEDIUM.getWidth(tps, font)));
        allElements.put("Direction", new ElementData("J", "icon", direction, Fonts.icon.getWidth("J", icon) + iconPad + Fonts.SF_MEDIUM.getWidth(direction, font)));
        allElements.put("Biome", new ElementData("w", "iconn", biomeText, Fonts.iconn.getWidth("w", icon) + iconPad + Fonts.SF_MEDIUM.getWidth(biomeText, font)));

        List<String> topRowNames = Arrays.asList("XYZ", "FPS", "Ping", "BPS");
        List<String> bottomRowNames = Arrays.asList("Time", "TPS", "Direction", "Biome");

        List<ElementData> topRow = new ArrayList<>();
        List<ElementData> bottomRow = new ArrayList<>();

        for (int i = 0; i < topRowNames.size(); i++) {
            String name = topRowNames.get(i);
            if (i < displayElements.size() && displayElements.get(i)) {
                topRow.add(allElements.get(name));
            }
        }

        for (int i = 0; i < bottomRowNames.size(); i++) {
            String name = bottomRowNames.get(i);
            int idx = i + 4;
            if (idx < displayElements.size() && displayElements.get(idx)) {
                bottomRow.add(allElements.get(name));
            }
        }

        if (topRow.isEmpty() && bottomRow.isEmpty()) return;

        float topWidth, bottomWidth;
        if (dynamicRects) {
            topWidth = 0;
            if (!topRow.isEmpty()) {
                for (int i = 0; i < topRow.size(); i++) {
                    topWidth += topRow.get(i).width + 2 * pad;
                    if (i < topRow.size() - 1) topWidth += space;
                }
            }
            bottomWidth = 0;
            if (!bottomRow.isEmpty()) {
                for (int i = 0; i < bottomRow.size(); i++) {
                    bottomWidth += bottomRow.get(i).width + 2 * pad;
                    if (i < bottomRow.size() - 1) bottomWidth += space;
                }
            }
        } else {
            topWidth = pad * 2;
            if (!topRow.isEmpty()) {
                for (ElementData el : topRow) topWidth += el.width;
                topWidth += space * (topRow.size() - 1);
            }
            bottomWidth = pad * 2;
            if (!bottomRow.isEmpty()) {
                for (ElementData el : bottomRow) bottomWidth += el.width;
                bottomWidth += space * (bottomRow.size() - 1);
            }
        }

        float maxW = Math.max(topWidth, bottomWidth);
        float rectH = line + pad * 2;

        float targetHeight = 0;
        if (!topRow.isEmpty()) targetHeight += rectH;
        if (!bottomRow.isEmpty()) {
            if (!topRow.isEmpty()) targetHeight += gap;
            targetHeight += rectH;
        }

        animatedWidth = Mathf.fast(animatedWidth, maxW, 14);
        animatedHeight = Mathf.fast(animatedHeight, targetHeight, 14);
        animatedTopWidth = Mathf.fast(animatedTopWidth, topWidth, 14);
        animatedBottomWidth = Mathf.fast(animatedBottomWidth, bottomWidth, 14);
        drag.size.set(animatedWidth, animatedHeight);

        if (x == 0 && y == 0) {
            float nx = 6 * finalScale;
            float ny = mc.getMainWindow().getScaledHeight() - drag.size.y - 6 * finalScale;
            drag.position.set(nx, ny);
            x = nx;
            y = ny;
            lastX = x;
            lastY = y;
        }

        updateDragAnimation(x, y, animatedWidth, animatedHeight);

        animatedX = Mathf.fast(animatedX, x, 14);
        animatedY = Mathf.fast(animatedY, y, 14);

        boolean alignRight = x > mc.getMainWindow().getScaledWidth() / 2f;

        s.push();
        applyDragTransform(s, animatedX, animatedY, animatedWidth, animatedHeight);

        float radius = 6f * finalScale;
        float currentY = animatedY;

        float topX = dynamicRects ? (alignRight ? animatedX + (maxW - topWidth) : animatedX) : (animatedX + (maxW - topWidth) / 2f);
        float bottomX = dynamicRects ? (alignRight ? animatedX + (maxW - bottomWidth) : animatedX) : (animatedX + (maxW - bottomWidth) / 2f);

        animatedTopX = Mathf.fast(animatedTopX, topX, 14);
        animatedBottomX = Mathf.fast(animatedBottomX, bottomX, 14);

        if (!topRow.isEmpty()) {
            float rowX = animatedTopX;
            float textCenterOffset = (rectH - Fonts.SF_MEDIUM.getHeight("A", font)) / 2f;
            float textY = currentY + textCenterOffset;
            float iconY = currentY + (rectH - icon) / 2f;
            if (!dynamicRects) {
                RenderUtil.CustomStyled.draw(s, rowX, currentY, animatedTopWidth, rectH, Round.of(radius), blurEnabled, blurStrength);
                if (enableOutline) {
                    int themeColor = Theme.getInstance().clientColor();
                    int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                    float halfWidth = outlineWidth * finalScale / 2f;
                    RenderUtil.Rounded.roundedOutline(s,
                            rowX - halfWidth,
                            currentY - halfWidth,
                            animatedTopWidth + outlineWidth * finalScale,
                            rectH + outlineWidth * finalScale,
                            outlineWidth * finalScale,
                            outlineColor,
                            Round.of(radius));
                }
                float offsetX = rowX + pad;
                for (ElementData el : topRow) {
                    drawElement(s, el, offsetX, textY, iconY, icon, iconPad, font);
                    offsetX += el.width + space;
                }
            } else {
                float currentOffset = 0f;
                for (int i = 0; i < topRow.size(); i++) {
                    ElementData el = topRow.get(i);
                    float rectW = el.width + 2 * pad;
                    float rectThisX = rowX + currentOffset;
                    RenderUtil.CustomStyled.draw(s, rectThisX, currentY, rectW, rectH, Round.of(radius), blurEnabled, blurStrength);
                    if (enableOutline) {
                        int themeColor = Theme.getInstance().clientColor();
                        int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                        float halfWidth = outlineWidth * finalScale / 2f;
                        RenderUtil.Rounded.roundedOutline(s,
                                rectThisX - halfWidth,
                                currentY - halfWidth,
                                rectW + outlineWidth * finalScale,
                                rectH + outlineWidth * finalScale,
                                outlineWidth * finalScale,
                                outlineColor,
                                Round.of(Round.of(radius)));
                    }
                    drawElement(s, el, rectThisX + pad, textY, iconY, icon, iconPad, font);
                    currentOffset += rectW;
                    if (i < topRow.size() - 1) currentOffset += space;
                }
            }
            currentY += rectH;
        }

        if (!bottomRow.isEmpty()) {
            if (!topRow.isEmpty()) currentY += gap;
            float rowX = animatedBottomX;
            float textCenterOffset = (rectH - Fonts.SF_MEDIUM.getHeight("A", font)) / 2f;
            float textY = currentY + textCenterOffset;
            float iconY = currentY + (rectH - icon) / 2f;
            if (!dynamicRects) {
                RenderUtil.CustomStyled.draw(s, rowX, currentY, animatedBottomWidth, rectH, Round.of(radius), blurEnabled, blurStrength);
                if (enableOutline) {
                    int themeColor = Theme.getInstance().clientColor();
                    int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                    float halfWidth = outlineWidth * finalScale / 2f;
                    RenderUtil.Rounded.roundedOutline(s,
                            rowX - halfWidth,
                            currentY - halfWidth,
                            animatedBottomWidth + outlineWidth * finalScale,
                            rectH + outlineWidth * finalScale,
                            outlineWidth * finalScale,
                            outlineColor,
                            Round.of(Round.of(radius)));
                }
                float offsetX = rowX + pad;
                for (ElementData el : bottomRow) {
                    drawElement(s, el, offsetX, textY, iconY, icon, iconPad, font);
                    offsetX += el.width + space;
                }
            } else {
                float currentOffset = 0f;
                for (int i = 0; i < bottomRow.size(); i++) {
                    ElementData el = bottomRow.get(i);
                    float rectW = el.width + 2 * pad;
                    float rectThisX = rowX + currentOffset;
                    RenderUtil.CustomStyled.draw(s, rectThisX, currentY, rectW, rectH, Round.of(radius), blurEnabled, blurStrength);
                    if (enableOutline) {
                        int themeColor = Theme.getInstance().clientColor();
                        int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                        float halfWidth = outlineWidth * finalScale / 2f;
                        RenderUtil.Rounded.roundedOutline(s,
                                rectThisX - halfWidth,
                                currentY - halfWidth,
                                rectW + outlineWidth * finalScale,
                                rectH + outlineWidth * finalScale,
                                outlineWidth * finalScale,
                                outlineColor,
                                Round.of(radius));
                    }
                    drawElement(s, el, rectThisX + pad, textY, iconY, icon, iconPad, font);
                    currentOffset += rectW;
                    if (i < bottomRow.size() - 1) currentOffset += space;
                }
            }
            currentY += rectH;
        }

        s.pop();
    }

    private void drawElement(MatrixStack s, ElementData el, float x, float textY, float iconY, float icon, float iconPad, float font) {
        switch (el.iconFont) {
            case "icon" -> Fonts.icon.draw(s, el.icon, x, iconY, Theme.getInstance().clientColor(), icon);
            case "HUD" -> Fonts.HUD.draw(s, el.icon, x, iconY, Theme.getInstance().clientColor(), icon);
            case "iconf" -> Fonts.iconf.draw(s, el.icon, x, iconY, Theme.getInstance().clientColor(), icon);
            case "icon_new" -> Fonts.icon_new.draw(s, el.icon, x, iconY, Theme.getInstance().clientColor(), icon);
            case "iconn" -> Fonts.iconn.draw(s, el.icon, x, iconY, Theme.getInstance().clientColor(), icon);
        }

        float iconWidth = switch (el.iconFont) {
            case "icon" -> Fonts.icon.getWidth(el.icon, icon);
            case "HUD" -> Fonts.HUD.getWidth(el.icon, icon);
            case "iconf" -> Fonts.iconf.getWidth(el.icon, icon);
            case "icon_new" -> Fonts.icon_new.getWidth(el.icon, icon);
            case "iconn" -> Fonts.iconn.getWidth(el.icon, icon);
            default -> 0;
        };

        Fonts.SF_MEDIUM.draw(s, el.text, x + iconWidth + iconPad, textY, ColorUtil.rgba(255, 255, 255, 255), font);
    }
}