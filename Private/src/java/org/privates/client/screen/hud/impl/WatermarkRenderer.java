package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Namespaced;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.gif.GifRender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WatermarkRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private final DragSetting drag;

    @Setter
    @Getter
    private boolean blurEnabled = true;
    @Setter
    @Getter
    private float blurStrength = 10f;
    @Setter
    @Getter
    private float scale = 0.9f;

    public boolean showSeparators = true;
    public boolean enableOutline = false;
    public float outlineWidth = 1.0f;
    public boolean gifLogo = false;
    public boolean onlyGif = false;
    public boolean showRect = true;

    private final List<Boolean> displayElements = new ArrayList<>(Arrays.asList(true, true, true));

    private static final String CLIENT_NAME = "PRIVATE";
    private static final String CROSS_SYMBOL = "†";
    private static final String POINT = " • ";

    private String cachedUserName = "";
    private String cachedServerIP = "";
    private float cachedTotalWidth = 0;
    private float cachedTotalHeight = 0;
    private float animatedWidth = 0;
    private float animatedHeight = 0;

    private GifRender privateLogoGif;

    private float lastX = 0, lastY = 0;
    private float smoothVelocityX = 0, smoothVelocityY = 0;
    private float currentRotation = 0, targetRotation = 0;
    private float springOffsetX = 0, springOffsetY = 0;
    private float dragScaleEffect = 1.0f;
    private boolean isDragging = false;
    private long lastMoveTime = 0;

    public WatermarkRenderer(DragSetting drag) {
        this.drag = drag;
    }

    private GifRender getPrivateLogoGif() {
        if (privateLogoGif == null) {
            try {
                privateLogoGif = new GifRender(new Namespaced("texture/privatelogo.gif"));
            } catch (Exception e) {
                gifLogo = false;
            }
        }
        return privateLogoGif;
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER, () -> this.scale, v -> this.scale = v, 0.5f, 2.0f, 0.1f),
                new DragComponent.HudSetting("Display Elements", DragComponent.SettingType.MULTI_BOOLEAN,
                        () -> 0f, v -> {
                },
                        Arrays.asList("Client Name", "Player Name", "Server IP"),
                        displayElements),
                new DragComponent.HudSetting("Gif Logo", DragComponent.SettingType.BOOLEAN, () -> this.gifLogo ? 1f : 0f, v -> this.gifLogo = v > 0.5f),
                new DragComponent.HudSetting("Only Gif", DragComponent.SettingType.BOOLEAN, () -> this.onlyGif ? 1f : 0f, v -> this.onlyGif = v > 0.5f),
                new DragComponent.HudSetting("Show Rect", DragComponent.SettingType.BOOLEAN, () -> this.showRect ? 1f : 0f, v -> this.showRect = v > 0.5f),
                new DragComponent.HudSetting("Separators", DragComponent.SettingType.BOOLEAN, () -> this.showSeparators ? 1f : 0f, v -> this.showSeparators = v > 0.5f),
                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN, () -> this.enableOutline ? 1f : 0f, v -> this.enableOutline = v > 0.5f),
                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER, () -> this.outlineWidth, v -> this.outlineWidth = v, 0.5f, 3.0f, 0.1f)
        );
    }

    private float getScale() {
        return scale;
    }

    private float smoothLerp(float current, float target, float speed) {
        return current + (target - current) * (1.0f - (float) Math.pow(1.0f - speed, 1.5f));
    }

    private float elasticLerp(float current, float target, float speed, float elasticity) {
        float diff = target - current;
        float velocity = diff * speed;
        if (Math.abs(diff) < 0.5f) velocity += diff * elasticity * 0.3f;
        return current + velocity;
    }

    private void updateDragAnimation(float x, float y) {
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

        targetRotation = Math.max(-15f, Math.min(15f, smoothVelocityX * 1.8f - smoothVelocityY * 0.3f));
        currentRotation = elasticLerp(currentRotation, targetRotation, isDragging ? 0.18f : 0.06f, 0.15f);
        if (!isDragging && Math.abs(currentRotation) < 0.1f) currentRotation *= 0.9f;

        springOffsetX = springOffsetX * 0.85f + ((-smoothVelocityX * 0.3f) - springOffsetX) * 0.12f;
        springOffsetY = springOffsetY * 0.85f + ((-smoothVelocityY * 0.2f) - springOffsetY) * 0.12f;
        springOffsetX = Math.max(-5f, Math.min(5f, springOffsetX));
        springOffsetY = Math.max(-5f, Math.min(5f, springOffsetY));

        dragScaleEffect = smoothLerp(dragScaleEffect, isDragging ? 1.02f : 1.0f, 0.15f);

        lastX = x;
        lastY = y;
    }

    private void applyDragTransform(MatrixStack matrix, float x, float y, float width, float height) {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;

        matrix.translate(centerX, centerY, 0);
        matrix.scale(dragScaleEffect, dragScaleEffect, 1);
        matrix.translate(-centerX, -centerY, 0);
        matrix.translate(springOffsetX, springOffsetY, 0);

        if (Math.abs(currentRotation) > 0.01f) {
            float pivotY = y + height * 0.15f;
            matrix.translate(centerX, pivotY, 0);
            matrix.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(currentRotation));
            matrix.translate(-centerX, -pivotY, 0);
        }
    }

    @Override
    public void render(Render2DEvent event) {
        if (mc.player == null) return;

        float s = getScale();
        MatrixStack matrix = event.getMatrix();

        if (onlyGif) {
            renderOnlyGif(matrix, s);
            return;
        }

        String userName = mc.player.getName().getString();
        String serverIP = mc.getCurrentServerData() != null ? mc.getCurrentServerData().serverIP : "";

        boolean showClientName = displayElements.size() > 0 && displayElements.get(0);
        boolean showName = displayElements.size() > 1 && displayElements.get(1);
        boolean showServerIP = displayElements.size() > 2 && displayElements.get(2);

        updateCache(userName, serverIP, s, showClientName, showName, showServerIP);

        animatedWidth = Mathf.fast(animatedWidth, cachedTotalWidth, 14);
        animatedHeight = Mathf.fast(animatedHeight, cachedTotalHeight, 14);

        float x = drag.position.x, y = drag.position.y;

        if (x == 0 && y == 0) {
            x = (mc.getMainWindow().getScaledWidth() / 2f) - (animatedWidth / 2f);
            y = 10f * s;
            drag.position.set(x, y);
            lastX = x;
            lastY = y;
        }

        drag.size.set(animatedWidth, animatedHeight);
        updateDragAnimation(x, y);

        matrix.push();
        applyDragTransform(matrix, x, y, animatedWidth, animatedHeight);

        if (showRect) {
            RenderUtil.CustomStyled.draw(matrix, x, y, animatedWidth, animatedHeight, Round.of(6f * s), blurEnabled, blurStrength);
            if (enableOutline) {
                int themeColor = Theme.getInstance().clientColor();
                int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                float halfWidth = outlineWidth * s / 2f;
                RenderUtil.Rounded.roundedOutline(matrix, x - halfWidth, y - halfWidth, animatedWidth + outlineWidth * s, animatedHeight + outlineWidth * s, outlineWidth * s, outlineColor, Round.of(6f * s));
            }
        }

        renderElements(matrix, x + 5f * s, y - 0.5f + animatedHeight / 2f, Theme.getInstance().clientColor(), userName, serverIP, s, showClientName, showName, showServerIP);
        matrix.pop();
    }

    private void renderOnlyGif(MatrixStack matrix, float s) {
        float gifSize = 60f * s;
        float padding = 4f * s;
        float totalSize = gifSize + padding * 2;

        animatedWidth = Mathf.fast(animatedWidth, totalSize, 14);
        animatedHeight = Mathf.fast(animatedHeight, totalSize, 14);

        float x = drag.position.x, y = drag.position.y;

        if (x == 0 && y == 0) {
            x = (mc.getMainWindow().getScaledWidth() / 2f) - (animatedWidth / 2f);
            y = 10f * s;
            drag.position.set(x, y);
            lastX = x;
            lastY = y;
        }

        drag.size.set(animatedWidth, animatedHeight);
        updateDragAnimation(x, y);

        matrix.push();
        applyDragTransform(matrix, x, y, animatedWidth, animatedHeight);

        if (showRect) {
            RenderUtil.CustomStyled.draw(matrix, x, y, animatedWidth, animatedHeight, Round.of(6f * s), blurEnabled, blurStrength);
            if (enableOutline) {
                int themeColor = Theme.getInstance().clientColor();
                int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                float halfWidth = outlineWidth * s / 2f;
                RenderUtil.Rounded.roundedOutline(matrix, x - halfWidth, y - halfWidth, animatedWidth + outlineWidth * s, animatedHeight + outlineWidth * s, outlineWidth * s, outlineColor, Round.of(6f * s));
            }
        }

        float gifX = x + (animatedWidth - gifSize) / 2f;
        float gifY = y + (animatedHeight - gifSize) - 13 / 2f;

        if (gifLogo && getPrivateLogoGif() != null) {
            getPrivateLogoGif().draw(matrix, gifX, gifY + 3, gifSize, gifSize, 1.0f, 25 * s, true);
        } else {
            RenderUtil.drawImage(matrix, new Namespaced("texture/logo.png"), gifX, gifY, gifSize, gifSize, true, Theme.getInstance().clientColor());
        }

        matrix.pop();
    }

    private void updateCache(String userName, String serverIP, float s, boolean showClientName, boolean showName, boolean showServerIP) {
        cachedUserName = userName;
        cachedServerIP = serverIP;

        boolean hasContent = showClientName || showName || showServerIP;

        if (!hasContent) {
            float crossW = Fonts.icon.getWidth(CROSS_SYMBOL, 17f * s);
            float logoW = (12f + 8f) * s;
            cachedTotalWidth = 3f * s * 2 + crossW + 4f * s + logoW;
            cachedTotalHeight = (12f + 8f) * s + 3f * s * 2;
            return;
        }

        float w = 5f * s;
        w += Fonts.icon.getWidth(CROSS_SYMBOL, 17f * s) + 4f * s;
        w += (12f + 8f) * s + 4f * s;

        if (showClientName) {
            w += font.getWidth(CLIENT_NAME, 8f * s) + 4f * s;
            if (showSeparators && (showName || showServerIP)) w += font.getWidth(POINT, 10f * s) + 4f * s;
        }

        if (showName) {
            w += Fonts.CLICKGUI.getWidth("c", 12f * s) + 4f * s + font.getWidth(userName, 8f * s) + 4f * s;
            if (showSeparators && showServerIP) w += font.getWidth(POINT, 10f * s) + 4f * s;
        }

        if (showServerIP) {
            w += Fonts.iconf.getWidth("a", 12f * s) + 4f * s + font.getWidth(serverIP, 8f * s) + 4f * s;
        }

        w += 5f * s;

        cachedTotalWidth = w;
        cachedTotalHeight = ((12f + 8f) * s + 5f * s * 2 - 2f * s);
    }

    private void renderElements(MatrixStack matrix, float startX, float centerY, int themeColor, String userName, String serverIP, float s, boolean showClientName, boolean showName, boolean showServerIP) {
        boolean hasContent = showClientName || showName || showServerIP;
        float x = startX - 3;

        float crossH = Fonts.icon.getHeight(CROSS_SYMBOL, 17f * s);
        Fonts.icon.draw(matrix, CROSS_SYMBOL, x, centerY - crossH / 2f, themeColor, 17f * s);
        x += Fonts.icon.getWidth(CROSS_SYMBOL, 17f * s) + 4f * s;

        float logoW = (12f + 8f) * s;
        float logoH = (12f + 8f) * s;
        if (gifLogo && getPrivateLogoGif() != null) {
            getPrivateLogoGif().draw(matrix, x - 4f, centerY - logoH / 2f, logoW, logoH, 1.0f, 25 * s, true);
        } else {
            RenderUtil.drawImage(matrix, new Namespaced("texture/logo.png"), x - 1f, centerY - logoH / 2f, logoW, logoH, true, themeColor);
        }
        x += logoW + 4f * s;

        if (!hasContent) return;

        if (showClientName) {
            float clientH = font.getHeight(CLIENT_NAME, 8f * s);
            font.draw(matrix, CLIENT_NAME, x, centerY - clientH / 2f, -1, 8f * s);
            x += font.getWidth(CLIENT_NAME, 8f * s) + 4f * s;

            if (showSeparators && (showName || showServerIP)) {
                float sepH = font.getHeight(POINT, 10f * s);
                font.draw(matrix, POINT, x, centerY - 1 * s - sepH / 2f, ColorUtil.getColor(255, 255, 255, 255), 10f * s);
                x += font.getWidth(POINT, 10f * s) + 4f * s;
            }
        }

        if (showName) {
            float iconH = Fonts.CLICKGUI.getHeight("с", 12f * s);
            Fonts.CLICKGUI.draw(matrix, "c", x, centerY + 1.5f * s - iconH / 2f, themeColor, (12f - 1.5f) * s);
            x += Fonts.CLICKGUI.getWidth("c", 12f * s) + 4f * s;

            float nameH = font.getHeight(userName, 8f * s);
            font.draw(matrix, userName, x, centerY - nameH / 2f, -1, 8f * s);
            x += font.getWidth(userName, 8f * s) + 4f * s;

            if (showSeparators && showServerIP) {
                float sepH = font.getHeight(POINT, 10f * s);
                font.draw(matrix, POINT, x, centerY - 1 * s - sepH / 2f, ColorUtil.getColor(255, 255, 255, 255), 10f * s);
                x += font.getWidth(POINT, 10f * s) + 4f * s;
            }
        }

        if (showServerIP) {
            float ipIconH = Fonts.iconf.getHeight("a", 12f * s);
            Fonts.icon_new.draw(matrix, "m", x, centerY + 1 * s - ipIconH / 2f, themeColor, 12f * s);
            x += Fonts.iconf.getWidth("a", 12f * s) + 4f * s;

            float ipH = font.getHeight(serverIP, 8f * s);
            font.draw(matrix, serverIP, x, centerY - ipH / 2f, -1, 8f * s);
        }
    }
}