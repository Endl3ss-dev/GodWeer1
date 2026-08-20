package fun.volon.client.modules.impl.render.base.implement;

import fun.volon.Volon;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.animation.AnimationUtils;
import fun.volon.api.utils.animation.Easings;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.notification.NotificationManager;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.impl.render.Interface;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;

import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Notifications extends InterfaceProcessing {
    private static final float STATE_ICON_GAP = 4f;
    private static final int STATE_ICON_SIZE = 14;
    /** icon1 MSDF: cross in circle */
    private static final String GLYPH_STATE_OFF = "j";
    /** icon1 MSDF: checkmark in circle */
    private static final String GLYPH_STATE_ON = "k";
    private static final int COLOR_STATE_OFF = ColorUtils.rgba(220, 70, 70, 255);
    private static final int COLOR_STATE_ON = ColorUtils.rgba(80, 220, 80, 255);

    private static final float V2_RADIUS    = 4f;
    private static final float V2_PAD_X     = 4f;
    private static final float V2_ICON_GAP  = 3f;
    private static final float V2_HEIGHT    = 16f;
    private static final int   V2_TEXT_SIZE = 13;
    private static final int   V2_ICON_SIZE = 14;
    private static final int   V2_BG_R = 7, V2_BG_G = 7, V2_BG_B = 9;
    private static final int   V2_TEXT_WHITE = ColorUtils.rgba(255, 255, 255, 255);
    private static final float V2_TEXT_BASE_Y = (V2_HEIGHT - V2_TEXT_SIZE) * 0.5f + 4.8f;
    private static final float V2_ICON_BASE_Y = (V2_HEIGHT - V2_ICON_SIZE) * 0.5f + 4.8f;

    private final Map<NotificationManager.Entry, AnimationUtils> appearAnimations = new HashMap<>();
    private final Map<NotificationManager.Entry, Float> currentYPositions = new HashMap<>();
    private final Set<NotificationManager.Entry> activeEntriesScratch = new HashSet<>();
    private long lastRenderTime = System.currentTimeMillis();
    private float previewAlpha = 0f;
    private float currentMaxTextWidth = 0f;

    public Notifications(Draggable draggable) {
        super(draggable);
    }

    private Font issue(int size) { return Fonts.getFont("suisse", size); }
    private Font icons(int size) { return Fonts.getFont("icon", size); }
    private Font icon1(int size) {
        Font font = Fonts.getFont("icon1", size);
        return font != null ? font : Fonts.getFont("icon", size);
    }

    private String getEntryText(NotificationManager.Entry entry) {
        if (entry.isCustom()) {
            return entry.customText;
        }
        return entry.moduleName;
    }

    private String getStateGlyph(boolean enabled) {
        return enabled ? GLYPH_STATE_ON : GLYPH_STATE_OFF;
    }

    private int getStateColor(boolean enabled) {
        return enabled ? COLOR_STATE_ON : COLOR_STATE_OFF;
    }

    private float getStateIconWidth() {
        Font font = icon1(STATE_ICON_SIZE);
        if (font == null) {
            return 0f;
        }
        return Math.max(font.getWidth(GLYPH_STATE_ON), font.getWidth(GLYPH_STATE_OFF));
    }

    private String getWaveBodyText(NotificationManager.Entry entry) {
        if (entry.isCustom()) {
            return entry.customText;
        }
        return entry.moduleName;
    }

    private Interface iface() {
        return Interface.INSTANCE;
    }

    private float alphaPercent() {
        Interface i = iface();
        if (i == null) return 0.57f;
        float p = i.notificationsAlpha.get() / 100f;
        if (p < 0f) return 0f;
        if (p > 1f) return 1f;
        return p;
    }

    private static int clamp255(int v) {
        return v < 0 ? 0 : (v > 255 ? 255 : v);
    }

    private void drawV2Panel(MatrixStack ms, float x, float y, float w, float h, float alphaMul) {
        Interface i = iface();

        if (i != null && i.notificationsShadow.isState()) {
            float strength = i.notificationsShadowStrength.get();
            int sa = clamp255((int) (255f * strength / 100f * alphaMul));
            RenderUtils.drawShadowOuter(ms, x, y, w, h, V2_RADIUS, 10f,
                    ColorUtils.rgba(0, 0, 0, sa));
        }
        if (isBlurEnabled()) {
            BLUR.render(ms, x, y, w, h, V2_RADIUS, BLUR_STRENGTH);
        }
        int bgAlpha = clamp255((int) (255f * alphaPercent() * alphaMul));
        if (bgAlpha > 0) {
            RenderUtils.drawRoundedRect(ms, x, y, w, h, V2_RADIUS,
                    ColorUtils.rgba(V2_BG_R, V2_BG_G, V2_BG_B, bgAlpha));
        }
        if (i != null && i.notificationsOutline.isState()) {
            int oa = clamp255((int) (220 * alphaMul));
            RenderUtils.drawRoundedRectOutline(ms, x, y, w, h,
                    V2_RADIUS, V2_RADIUS, V2_RADIUS, V2_RADIUS, 0.6f,
                    ColorUtils.rgba(0, 0, 0, oa));
        }
    }

    private float entryWidth(String statusText, boolean hasItemIcon) {
        float textW = issue(V2_TEXT_SIZE).getWidth(statusText);
        float iconW = hasItemIcon ? V2_ICON_SIZE : getStateIconWidth();
        return V2_PAD_X + iconW + V2_ICON_GAP + textW + V2_PAD_X;
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        DefaultStyle(eventRender);
        super.onRender(eventRender);
    }

    private void DefaultStyle(EventRender.Default eventRender) {
        if (mc == null) return;

        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastRenderTime) / 1000f;
        lastRenderTime = currentTime;

        List<NotificationManager.Entry> entries = NotificationManager.getActive();
        boolean isChatOpen = mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen;

        boolean shouldRender = !entries.isEmpty() || isChatOpen;

        float targetPreviewAlpha = isChatOpen ? 0.7f : 0f;
        float alphaSpeed = 8f;
        previewAlpha += (targetPreviewAlpha - previewAlpha) * Math.min(1f, alphaSpeed * deltaTime);

        if (!shouldRender && previewAlpha < 0.01f) {
            appearAnimations.clear();
            currentYPositions.clear();
            previewAlpha = 0f;
            return;
        }

        float baseX = draggable.getX();
        float baseY = draggable.getY();

        float scaleMul = 1f;
        if (iface() != null) {
            scaleMul = iface().notificationsScale.get() / 100f;
            if (scaleMul < 0.5f) scaleMul = 0.5f;
            if (scaleMul > 2.0f) scaleMul = 2.0f;
        }

        int colorTheme;
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            colorTheme = Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        } else {
            colorTheme = ColorUtils.getThemeColor();
        }

        long now = System.currentTimeMillis();
        float height = V2_HEIGHT;
        float spacing = 3f;
        float lerpSpeed = 12f;

        String previewText = "Кликни на меня для открытия настроек!";
        float previewFullW = entryWidth(previewText, false);

        float targetMaxWidth = previewFullW;
        for (NotificationManager.Entry entry : entries) {
            AnimationUtils anim = appearAnimations.computeIfAbsent(entry, e -> new AnimationUtils(0f, 12f, Easings.QUAD_OUT));
            long age = now - entry.startTime;
            anim.update(1f);
            float appear = anim.getValue();
            float alpha = appear;
            if (age > NotificationManager.DURATION_MS - 200) {
                alpha = (1f - (age - (NotificationManager.DURATION_MS - 200)) / 200f) * appear;
            }
            if (alpha <= 0.01f) {
                continue;
            }
            String text = getEntryText(entry);
            String statusText = entry.isCustom() ? text : ("\"" + text + "\" был " + (entry.enabled ? "включен" : "выключен"));
            float w = entryWidth(statusText, entry.hasItemIcon());
            if (w > targetMaxWidth) targetMaxWidth = w;
        }

        float widthLerpSpeed = 10f;
        currentMaxTextWidth += (targetMaxWidth - currentMaxTextWidth) * Math.min(1f, widthLerpSpeed * deltaTime);
        if (Math.abs(targetMaxWidth - currentMaxTextWidth) < 0.1f) {
            currentMaxTextWidth = targetMaxWidth;
        }

        float maxWidth = currentMaxTextWidth;
        float overallCenterX = baseX + maxWidth * 0.5f;

        MatrixStack rootMs = eventRender.getContext().getMatrices();
        rootMs.push();
        rootMs.translate(baseX, baseY, 0);
        rootMs.scale(scaleMul, scaleMul, 1.0f);
        rootMs.translate(-baseX, -baseY, 0);

        float targetY = baseY;

        if (previewAlpha > 0.01f) {
            float renderY = targetY;
            float alpha = previewAlpha;
            float scale = 0.86f + 0.14f * alpha;

            float panelW = entryWidth(previewText, false);
            float panelX = overallCenterX - panelW * 0.5f;

            MatrixStack ms = eventRender.getContext().getMatrices();
            ms.push();
            ms.translate(overallCenterX, renderY + height * 0.5f, 0);
            ms.scale(scale, scale, 1.0f);
            ms.translate(-overallCenterX, -(renderY + height * 0.5f), 0);

            drawV2Panel(ms, panelX, renderY, panelW, height, alpha);

            int textColor = ColorUtils.setAlphaColor(V2_TEXT_WHITE, clamp255((int) (255 * alpha)));
            int iconColor = ColorUtils.setAlphaColor(colorTheme, clamp255((int) (255 * alpha)));

            Font stateFont = icon1(V2_ICON_SIZE);
            if (stateFont != null) {
                float glyphW = stateFont.getWidth(GLYPH_STATE_ON);
                float iconDrawX = panelX + V2_PAD_X + (getStateIconWidth() - glyphW) * 0.5f;
                stateFont.drawStringWithShadow(ms, GLYPH_STATE_ON, iconDrawX, renderY + V2_ICON_BASE_Y, iconColor);
            }

            float textX = panelX + V2_PAD_X + getStateIconWidth() + V2_ICON_GAP;
            issue(V2_TEXT_SIZE).drawStringWithShadow(ms, previewText, textX, renderY + V2_TEXT_BASE_Y, textColor);

            ms.pop();

            targetY += height + spacing;
        }

        for (NotificationManager.Entry entry : entries) {
            AnimationUtils anim = appearAnimations.computeIfAbsent(entry, e -> new AnimationUtils(0f, 12f, Easings.QUAD_OUT));
            long age = now - entry.startTime;
            anim.update(1f);
            float appear = anim.getValue();
            float alpha = appear;
            if (age > NotificationManager.DURATION_MS - 200) {
                alpha = (1f - (age - (NotificationManager.DURATION_MS - 200)) / 200f) * appear;
            }
            if (alpha <= 0f) {
                targetY += height + spacing;
                continue;
            }

            Float currentY = currentYPositions.get(entry);
            if (currentY == null) {
                currentY = targetY;
            }

            float diff = targetY - currentY;
            if (Math.abs(diff) > 0.01f) {
                currentY = currentY + diff * Math.min(1f, lerpSpeed * deltaTime);
            } else {
                currentY = targetY;
            }
            currentYPositions.put(entry, currentY);

            String text = getEntryText(entry);
            String statusText = entry.isCustom() ? text : ("\"" + text + "\" был " + (entry.enabled ? "включен" : "выключен"));

            float slide = 6f * (1f - appear);
            float renderY = currentY + slide;
            float scale = 0.86f + 0.14f * alpha;

            float panelW = entryWidth(statusText, entry.hasItemIcon());
            float panelX = overallCenterX - panelW * 0.5f;

            MatrixStack ms = eventRender.getContext().getMatrices();
            ms.push();
            ms.translate(overallCenterX, renderY + height * 0.5f, 0);
            ms.scale(scale, scale, 1.0f);
            ms.translate(-overallCenterX, -(renderY + height * 0.5f), 0);

            drawV2Panel(ms, panelX, renderY, panelW, height, alpha);

            int textColor = ColorUtils.setAlphaColor(V2_TEXT_WHITE, clamp255((int) (255 * alpha)));

            if (entry.hasItemIcon()) {
                ms.push();
                float itemIconSize = V2_ICON_SIZE;
                float itemX = panelX + V2_PAD_X + (getStateIconWidth() - 16f) * 0.5f;
                float itemY = renderY + (height - 16f) * 0.5f;
                ms.translate(itemX, itemY, 0);
                ms.scale(itemIconSize / 16f, itemIconSize / 16f, 1.0f);
                eventRender.getContext().drawItem(entry.itemStack, 0, 0);
                ms.pop();
            } else {
                Font stateFont = icon1(V2_ICON_SIZE);
                if (stateFont != null && !entry.isCustom()) {
                    int stateColor = ColorUtils.setAlphaColor(getStateColor(entry.enabled), clamp255((int) (255 * alpha)));
                    String glyph = getStateGlyph(entry.enabled);
                    float glyphW = stateFont.getWidth(glyph);
                    float iconDrawX = panelX + V2_PAD_X + (getStateIconWidth() - glyphW) * 0.5f;
                    stateFont.drawStringWithShadow(ms, glyph, iconDrawX, renderY + V2_ICON_BASE_Y, stateColor);
                }
            }

            float textX = panelX + V2_PAD_X + getStateIconWidth() + V2_ICON_GAP;
            issue(V2_TEXT_SIZE).drawStringWithShadow(ms, statusText, textX, renderY + V2_TEXT_BASE_Y, textColor);

            ms.pop();

            targetY += height + spacing;
        }

        activeEntriesScratch.clear();
        activeEntriesScratch.addAll(entries);
        appearAnimations.keySet().removeIf(entry -> !activeEntriesScratch.contains(entry));
        currentYPositions.keySet().removeIf(entry -> !activeEntriesScratch.contains(entry));

        rootMs.pop();

        draggable.setWidth(maxWidth * scaleMul);
        draggable.setHeight(Math.max(1f, targetY - baseY) * scaleMul);
    }

    private void WaveStyle(EventRender.Default eventRender) {
        if (mc == null) return;

        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastRenderTime) / 1000f;
        lastRenderTime = currentTime;

        List<NotificationManager.Entry> entries = NotificationManager.getActive();
        if (entries.isEmpty()) {
            appearAnimations.clear();
            currentYPositions.clear();
            return;
        }

        int time = (int) ((System.currentTimeMillis() % 2000) / 2000f * 360f);

        int leftTop = ColorUtils.getThemeColor(time);
        int leftBottom = ColorUtils.getThemeColor(time + 30);
        int centerTop = ColorUtils.getThemeColor(time + 90);
        int centerBottom = ColorUtils.getThemeColor(time + 120);
        int rightTop = ColorUtils.getThemeColor(time + 180);
        int rightBottom = ColorUtils.getThemeColor(time + 210);

        long now = System.currentTimeMillis();

        float spacing = 4f;
        float lerpSpeed = 14f;
        float screenW = mc.getWindow().getScaledWidth();
        float screenH = mc.getWindow().getScaledHeight();
        float rightPadding = 5f;
        float bottomPadding = 5f;

        float stackOffset = 0f;
        float maxWidth = 120f;

        for (NotificationManager.Entry entry : entries) {
            String title = "Notify";
            String body = getWaveBodyText(entry);
            String leftGlyph = entry.isCustom() ? GLYPH_STATE_OFF : (entry.categoryIcon != null && !entry.categoryIcon.isEmpty() ? entry.categoryIcon : "?");
            float iconW = entry.isCustom() ? icon1(14).getWidth(leftGlyph) : icons(14).getWidth(leftGlyph);
            float titleW = issue(15).getWidth(title);
            float bodyW = issue(13).getWidth(body);
            float stateW = entry.isCustom() ? 0f : getStateIconWidth() + STATE_ICON_GAP;
            float width = Math.max(120f, Math.max(titleW + iconW + 18f, bodyW + stateW + 14f));
            maxWidth = Math.max(maxWidth, width);
        }

        for (NotificationManager.Entry entry : entries) {
            AnimationUtils anim = appearAnimations.computeIfAbsent(entry, e -> new AnimationUtils(0f, 12f, Easings.QUAD_OUT));
            anim.update(1f);
            float appear = anim.getValue();

            long age = now - entry.startTime;
            float alphaMul = appear;
            if (age > NotificationManager.DURATION_MS - 200) {
                alphaMul = (1f - (age - (NotificationManager.DURATION_MS - 200)) / 200f) * appear;
            }
            if (alphaMul <= 0.01f) continue;

            String title = "Notify";
            String body = getWaveBodyText(entry);
            String leftGlyph = entry.isCustom() ? GLYPH_STATE_OFF : (entry.categoryIcon != null && !entry.categoryIcon.isEmpty() ? entry.categoryIcon : "?");

            float iconW = entry.isCustom() ? icon1(14).getWidth(leftGlyph) : icons(14).getWidth(leftGlyph);
            float titleW = issue(15).getWidth(title);
            float bodyW = issue(13).getWidth(body);
            float stateW = entry.isCustom() ? 0f : getStateIconWidth() + STATE_ICON_GAP;
            float width = Math.max(120f, Math.max(titleW + iconW + 18f, bodyW + stateW + 14f));
            float height = 24f;

            float x = screenW - width - rightPadding;
            float targetY = screenH - bottomPadding - height - stackOffset;

            Float currentY = currentYPositions.get(entry);
            if (currentY == null) currentY = targetY;
            currentY += (targetY - currentY) * Math.min(1f, lerpSpeed * deltaTime);
            currentYPositions.put(entry, currentY);

            float y = currentY;
            float scale = 0.86f + 0.14f * alphaMul;

            int bg = ColorUtils.rgba(25, 25, 25, (int) (150 * alphaMul));
            int txt = ColorUtils.setAlphaColor(-1, (int) (255 * alphaMul));
            int iconCol = entry.isCustom()
                    ? ColorUtils.setAlphaColor(ColorUtils.rgba(235, 0, 0, 255), (int) (255 * alphaMul))
                    : ColorUtils.setAlphaColor(
                            Volon.INSTANCE.themeStorage.getThemes().getTheme().getColor(0),
                            (int) (255 * alphaMul)
                    );

            float cx = x + width * 0.5f;
            float cy = y + height * 0.5f;
            var ms = eventRender.getContext().getMatrices();
            ms.push();
            ms.translate(cx, cy, 0);
            ms.scale(scale, scale, 1.0f);
            ms.translate(-cx, -cy, 0);

            RenderUtils.drawWaveHudPanel(ms, x, y, width, height - 1.5f, bg,
                    3.5f, 0, 10, 10,
                    leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);

            if (entry.isCustom()) {
                icon1(28).draw(ms, leftGlyph, x + 3f, y + 8, iconCol);
            } else {
                icons(28).draw(ms, leftGlyph, x + 3f, y + 8, iconCol);
            }
            issue(15).draw(ms, title, x + 19, y + 6.5f, txt);
            float bodyX = x + 19;
            issue(13).draw(ms, body, bodyX, y + 15.0f, txt);
            if (!entry.isCustom()) {
                Font stateFont = icon1(STATE_ICON_SIZE);
                if (stateFont != null) {
                    float stateX = bodyX + bodyW + STATE_ICON_GAP;
                    int stateColor = ColorUtils.setAlphaColor(getStateColor(entry.enabled), (int) (255 * alphaMul));
                    stateFont.draw(ms, getStateGlyph(entry.enabled), stateX, y + 15.2f, stateColor);
                }
            }

            ms.pop();

            stackOffset += (height + spacing) * appear;
        }

        appearAnimations.keySet().removeIf(entry -> !entries.contains(entry));
        currentYPositions.keySet().removeIf(entry -> !entries.contains(entry));

        draggable.setWidth(0);
        draggable.setHeight(0);
    }
}
