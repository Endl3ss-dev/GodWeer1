package antileak.base.client.modules.impl.render.base.implement;

import antileak.base.api.events.implement.EventRender;
import antileak.base.api.utils.animation.AnimationUtils;
import antileak.base.api.utils.animation.Easings;
import antileak.base.api.utils.color.ColorUtils;
import antileak.base.api.utils.draggable.Draggable;
import antileak.base.api.utils.notification.NotificationManager;
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.api.utils.render.fonts.msdf.Font;
import antileak.base.api.utils.render.fonts.msdf.Fonts;
import antileak.base.client.modules.impl.render.base.InterfaceProcessing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Notifications extends InterfaceProcessing {

    private static final float HEIGHT     = 16f;
    private static final float PAD_X      = 8f;
    private static final float PAD_Y      = 3.5f;
    private static final float CORNER     = 5f;
    private static final float BAR_W      = 3f;
    private static final float BAR_PAD    = 5f;
    private static final float BAR_VERT_PAD = 4f;
    private static final float GAP_Y      = 3f;
    private static final float SLIDE_DIST = 20f;

    private static final float TOGGLE_WIDTH   = 20f;
    private static final float TOGGLE_HEIGHT  = 10f;
    private static final float TOGGLE_CIRCLE_SIZE = 7f;
    private static final float TOGGLE_PADDING = 1.5f;
    private static final float TOGGLE_CORNER  = TOGGLE_HEIGHT * 0.5f;

    private static final int BG_COLOR      = ColorUtils.rgba(15, 15, 15, 220);
    private static final int TEXT_COLOR    = ColorUtils.rgba(230, 230, 230, 255);
    private static final int ENABLED_COL   = ColorUtils.rgba(80, 220, 100, 255);
    private static final int DISABLED_COL  = ColorUtils.rgba(220, 65,  65,  255);
    private static final int TOGGLE_OFF_BG = ColorUtils.rgba(50, 50, 55, 255);
    private static final int TOGGLE_CIRCLE = ColorUtils.rgba(255, 255, 255, 255);

    private final Map<NotificationManager.Entry, AnimationUtils> appearAnims  = new HashMap<>();
    private final Map<NotificationManager.Entry, AnimationUtils> toggleAnims  = new HashMap<>();
    private final Map<NotificationManager.Entry, Float>          currentYPos  = new HashMap<>();
    private final Set<NotificationManager.Entry>                 scratch      = new HashSet<>();
    private long lastRenderTime = System.currentTimeMillis();

    public Notifications(Draggable draggable) {
        super(draggable);
    }

    private Font font() { return Fonts.getFont("suisse", 12); }

    private int getThemeColor() {
        return ColorUtils.getThemeColor();
    }

    private float calcWidth(NotificationManager.Entry entry) {
        Font f = font();
        String full = buildFullText(entry);
        float textWidth = f.getWidth(full);
        float toggleSpace = entry.isCustom() ? 0f : (TOGGLE_WIDTH + 6f);
        return toggleSpace + textWidth + PAD_X * 2 + BAR_PAD + BAR_W + BAR_PAD;
    }

    private String buildFullText(NotificationManager.Entry entry) {
        if (entry.isCustom()) return entry.customText;
        return "Module \u00AB" + entry.moduleName + "\u00BB  " + (entry.enabled ? "Enabled" : "Disabled");
    }

    @Override
    public void onRender(EventRender.Default event) {
        if (mc == null) return;

        long now        = System.currentTimeMillis();
        float deltaTime = (now - lastRenderTime) / 1000f;
        lastRenderTime  = now;

        List<NotificationManager.Entry> entries = NotificationManager.getActive();

        if (entries.isEmpty()) {
            appearAnims.clear();
            currentYPos.clear();
            draggable.setWidth(10);
            draggable.setHeight(10);
            return;
        }

        Font  font   = font();
        float baseX  = draggable.getX();
        float baseY  = draggable.getY();

        float maxWidth = 0;
        for (NotificationManager.Entry entry : entries)
            maxWidth = Math.max(maxWidth, calcWidth(entry));

        float lerpSpeed = 12f;
        float targetY   = baseY;
        var   ms        = event.getContext().getMatrices();

        for (NotificationManager.Entry entry : entries) {

            AnimationUtils anim = appearAnims.computeIfAbsent(
                    entry, e -> new AnimationUtils(0f, 14f, Easings.QUAD_OUT));
            anim.update(1f);
            float appear = anim.getValue();

            AnimationUtils toggleAnim = toggleAnims.computeIfAbsent(
                    entry, e -> new AnimationUtils(entry.enabled ? 1f : 0f, 8f, Easings.CUBIC_OUT));
            toggleAnim.update(entry.enabled ? 1f : 0f);
            float toggleProgress = toggleAnim.getValue();

            long  age          = now - entry.startTime;
            float timerProgress = Math.max(0f, 1f - (float) age / NotificationManager.DURATION_MS);

            float alpha = appear;
            if (age > NotificationManager.DURATION_MS - 200) {
                alpha = (1f - (age - (NotificationManager.DURATION_MS - 200)) / 200f) * appear;
            }
            alpha = Math.max(0f, Math.min(1f, alpha));

            if (alpha <= 0f) {
                targetY += HEIGHT + GAP_Y;
                continue;
            }

            Float cy = currentYPos.get(entry);
            if (cy == null) cy = targetY;
            float diff = targetY - cy;
            if (Math.abs(diff) > 0.01f) cy = cy + diff * Math.min(1f, lerpSpeed * deltaTime);
            else cy = targetY;
            currentYPos.put(entry, cy);

            float entryW = calcWidth(entry);
            float x      = baseX + (maxWidth - entryW) * 0.5f;

            float slideX = SLIDE_DIST * (1f - appear);
            float dx     = x + slideX;
            float dy     = cy;

            boolean disabled = !entry.isCustom() && !entry.enabled;
            int     barColor = disabled ? DISABLED_COL : ENABLED_COL;

            int bgCol  = ColorUtils.setAlphaColor(BG_COLOR,   (int)(255 * alpha));
            int txtCol = ColorUtils.setAlphaColor(TEXT_COLOR, (int)(255 * alpha));
            int stateCol = ColorUtils.setAlphaColor(barColor,  (int)(255 * alpha));

            float rectW = entryW;

            ms.push();

            int themeColor = getThemeColor();
            
            RenderUtils.drawShadow(ms, dx, dy, rectW, HEIGHT, CORNER, 12f,
                    ColorUtils.setAlphaColor(themeColor, (int)(80 * alpha)));

            RenderUtils.drawRoundedRect(ms, dx, dy, rectW, HEIGHT, CORNER, ColorUtils.rgba(20, 20, 20, (int)(230 * alpha)));
            RenderUtils.drawRoundedRect(ms, dx + rectW / 2f - 12f, dy + HEIGHT - 2f, 24f, 2f, 1f, ColorUtils.setAlphaColor(themeColor, (int)(255 * alpha)));

            float textStartX = dx + PAD_X;
            if (!entry.isCustom()) {
                float toggleX = dx + PAD_X;
                float toggleY = dy + (HEIGHT - TOGGLE_HEIGHT) * 0.5f;

                int toggleBgColor = ColorUtils.interpolateColor(
                        ColorUtils.setAlphaColor(TOGGLE_OFF_BG, (int)(255 * alpha)),
                        ColorUtils.setAlphaColor(themeColor, (int)(255 * alpha)),
                        toggleProgress
                );

                if (entry.enabled) {
                    float glowIntensity = toggleProgress;
                    RenderUtils.drawShadow(ms, toggleX, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT,
                            TOGGLE_CORNER, 6f * glowIntensity,
                            ColorUtils.setAlphaColor(themeColor, (int)(120 * glowIntensity * alpha)));
                }

                RenderUtils.drawRoundedRect(ms, toggleX, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT,
                        TOGGLE_CORNER, toggleBgColor);

                float circleX = toggleX + TOGGLE_PADDING +
                        (TOGGLE_WIDTH - TOGGLE_PADDING * 2 - TOGGLE_CIRCLE_SIZE) * toggleProgress;
                float circleY = toggleY + (TOGGLE_HEIGHT - TOGGLE_CIRCLE_SIZE) * 0.5f;
                int circleColor = ColorUtils.setAlphaColor(TOGGLE_CIRCLE, (int)(255 * alpha));

                RenderUtils.drawShadow(ms, circleX, circleY, TOGGLE_CIRCLE_SIZE, TOGGLE_CIRCLE_SIZE,
                        TOGGLE_CIRCLE_SIZE * 0.5f, 2f,
                        ColorUtils.setAlphaColor(ColorUtils.rgba(0, 0, 0, 100), (int)(100 * alpha)));

                RenderUtils.drawRoundedRect(ms, circleX, circleY, TOGGLE_CIRCLE_SIZE, TOGGLE_CIRCLE_SIZE,
                        TOGGLE_CIRCLE_SIZE * 0.5f, circleColor);

                textStartX += TOGGLE_WIDTH + 5f;
            }

            float localSize = font.getSize() * 0.5f;
            float visibleH  = font.getFont().getBaselineHeight() * localSize;
            float textY     = dy + (HEIGHT - visibleH) * 0.5f + 1.5f;
            if (entry.isCustom()) {
                font.draw(ms, entry.customText, textStartX, textY, txtCol);
            } else {
                String basePart  = "Module \u00AB" + entry.moduleName + "\u00BB  ";
                String statePart = entry.enabled ? "Enabled" : "Disabled";
                float  baseW     = font.getWidth(basePart);
                font.draw(ms, basePart,  textStartX,         textY, txtCol);
                font.draw(ms, statePart, textStartX + baseW, textY, stateCol);
            }

            float barX      = dx + rectW - BAR_PAD - BAR_W;
            float barInnerH = HEIGHT - BAR_VERT_PAD * 2f;
            float barCurH   = barInnerH * timerProgress;
            float barBottom = dy + BAR_VERT_PAD + barInnerH;
            float barStartY = barBottom - barCurH;
            if (barCurH > 0.5f) {
                int barCol = ColorUtils.setAlphaColor(barColor, (int)(255 * alpha));
                RenderUtils.drawRoundedRect(ms, barX, barStartY, BAR_W, barCurH, BAR_W * 0.5f, barCol);
            }

            ms.pop();

            targetY += HEIGHT + GAP_Y;
        }

        scratch.clear();
        scratch.addAll(entries);
        appearAnims.keySet().removeIf(e -> !scratch.contains(e));
        toggleAnims.keySet().removeIf(e -> !scratch.contains(e));
        currentYPos.keySet().removeIf(e -> !scratch.contains(e));

        draggable.setWidth(maxWidth);
        draggable.setHeight(Math.max(1f, targetY - baseY));

        super.onRender(event);
    }
}