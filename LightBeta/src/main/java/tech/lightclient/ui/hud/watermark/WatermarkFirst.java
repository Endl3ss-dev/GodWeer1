package tech.lightclient.ui.hud.watermark;

import com.adl.nativeprotect.User;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.HudShadowRenderer;
import tech.lightclient.ui.hud.api.HudElement;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class WatermarkFirst extends WatermarkBase implements HudElement {
    private static final float TOP_HEIGHT = 31.0f;
    private static final float BOTTOM_HEIGHT = 28.5f;
    private static final float ROW_GAP = 6.0f;
    private static final float OUTLINE = 0.9f;
    private static final float RADIUS = 10.5f;
    private static final float LOGO_BLOCK_WIDTH = 31.5f;
    private static final float TOP_CONTENT_LEFT_PADDING = 8.0f;
    private static final float PLAIN_SIDE_PADDING = 10.0f;
    private static final float ENTRY_GAP = 10.5f;
    private static final float ENTRY_PADDING_RIGHT = 10.0f;
    private static final float ICON_TEXT_GAP = 4.0f;
    private static final float TEXT_SIZE = 6.75f;
    private static final float BRAND_ICON_SIZE = 15f;
    private static final float INFO_ICON_SIZE = 8.7f;
    private static final float BRAND_ICON_BASELINE_NUDGE = 13f;
    private static final float INFO_ICON_BASELINE_NUDGE = 8f;

    private static final int OUTLINE_COLOR = rgba(54, 54, 60, 190);
    private static final int PANEL_TOP = rgba(33, 33, 39, 245);
    private static final int PANEL_BOTTOM = rgba(25, 25, 31, 245);
    private static final int LEFT_TOP = rgba(40, 40, 48, 255);
    private static final int LEFT_BOTTOM = rgba(32, 32, 40, 255);
    private static final int SEPARATOR = rgba(96, 96, 102, 120);
    private static final int TEXT_COLOR = rgba(255, 255, 255, 255);
    private static final int MUTED_COLOR = rgba(145, 145, 145, 255);
    private static final int ICON_COLOR = rgba(245, 245, 245, 255);

    public WatermarkFirst(Dragging dragging) {
        super(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());

        String user = User.getInstance().profile("username") == null || User.getInstance().profile("username").isEmpty() ? "unknown" : User.getInstance().profile("username");
        List<RenderLine> allLines = buildRenderLines(toLines(buildVisibleEntries(user)));
        List<RenderLine> topLines = pickLines(allLines, EntryType.NICK, EntryType.FPS, EntryType.PING);
        List<RenderLine> bottomLines = pickLines(allLines, EntryType.SERVER_IP, EntryType.TIME, EntryType.TPS);

        float topWidth = getTopWidth(topLines);
        float bottomWidth = getBottomWidth(bottomLines);
        float totalWidth = Math.max(topWidth, bottomWidth);
        float totalHeight = TOP_HEIGHT;
        if (!bottomLines.isEmpty()) {
            totalHeight += ROW_GAP + BOTTOM_HEIGHT;
        }

        float currentX = x;
        float currentY = y;

        boolean centered = DraggingManager.getHudBoolean(SETTINGS_SCOPE, SETTING_CENTERING, false);
        if (centered) {
            float scaledW = (float) mc.getWindow().getWidth() / scaleFactor;
            currentX = (scaledW - totalWidth) * 0.5f;
            float targetY = 13f + getBossBarOffset(scaleFactor);
            animatedCenteredY = animateTowards(animatedCenteredY, targetY, 0.2f);
            currentY = animatedCenteredY;
            dragging.setX(currentX * scaleFactor);
            dragging.setY(currentY * scaleFactor);
        } else {
            animatedCenteredY = Float.NaN;
        }

        if (!topLines.isEmpty()) {
            renderTopBar(event, currentX, currentY, topWidth, topLines);
        }
        if (!bottomLines.isEmpty()) {
            renderBottomBar(event, currentX, currentY + TOP_HEIGHT + ROW_GAP, bottomWidth, bottomLines);
        }

        width = totalWidth;
        height = totalHeight;
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }

    private void renderTopBar(EventRender.Screen.Hud event, float x, float y, float width, List<RenderLine> lines) {
        drawPanel(event, x, y, width, TOP_HEIGHT);
        event.getRenderer().gradient(
                x + OUTLINE,
                y + OUTLINE,
                LOGO_BLOCK_WIDTH - OUTLINE,
                TOP_HEIGHT - OUTLINE * 2f,
                RADIUS - 0.75f, 0f, 0f, RADIUS - 0.75f,
                LEFT_TOP, LEFT_TOP, LEFT_BOTTOM, LEFT_BOTTOM
        );

        float separatorX = x + LOGO_BLOCK_WIDTH;
        event.getRenderer().rect(separatorX, y + 6f, 0.75f, TOP_HEIGHT - 12f, 0.3f, SEPARATOR);

        float logoBaseline = y + TOP_HEIGHT * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.LIGHT_TTF, 'a', BRAND_ICON_SIZE) + BRAND_ICON_BASELINE_NUDGE;
        float logoX = x + 1.5f + (LOGO_BLOCK_WIDTH - FontRegistry.LIGHT_TTF.getWidth("a", BRAND_ICON_SIZE)) * 0.5f;
        event.getRenderer().text(FontRegistry.LIGHT_TTF, logoX, logoBaseline, BRAND_ICON_SIZE, "a", ICON_COLOR);

        float cursorX = separatorX + TOP_CONTENT_LEFT_PADDING;
        renderEntries(event, lines, cursorX, y, TOP_HEIGHT);
    }

    private void renderBottomBar(EventRender.Screen.Hud event, float x, float y, float width, List<RenderLine> lines) {
        drawPanel(event, x, y, width, BOTTOM_HEIGHT);
        renderEntries(event, lines, x + PLAIN_SIDE_PADDING, y, BOTTOM_HEIGHT);
    }

    private void drawPanel(EventRender.Screen.Hud event, float x, float y, float width, float height) {
        HudShadowRenderer.render(event.getRenderer(), SETTINGS_SCOPE, x, y, width, height, RADIUS, 1f, Interface.getHudScale(SETTINGS_SCOPE));
        Interface.renderHudBackground(event.getRenderer(), x, y, width, height, RADIUS, 1f);
        event.getRenderer().rect(x, y, width, height, RADIUS, OUTLINE_COLOR);
        if (!Interface.isHudBlurBackground()) {
            event.getRenderer().gradient(
                    x + OUTLINE,
                    y + OUTLINE,
                    width - OUTLINE * 2f,
                    height - OUTLINE * 2f,
                    RADIUS - 0.75f,
                    PANEL_TOP, PANEL_TOP, PANEL_BOTTOM, PANEL_BOTTOM
            );
        }
    }

    private float renderEntries(EventRender.Screen.Hud event, List<RenderLine> lines, float startX, float y, float rowHeight) {
        float cursorX = startX;
        for (int i = 0; i < lines.size(); i++) {
            RenderLine line = lines.get(i);
            String icon = resolveIcon(line.type);
            float iconBaseline = y + rowHeight * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.LIGHT_TTF, icon.charAt(0), INFO_ICON_SIZE) + INFO_ICON_BASELINE_NUDGE;
            event.getRenderer().text(FontRegistry.LIGHT_TTF, cursorX, iconBaseline, INFO_ICON_SIZE, icon, ICON_COLOR);
            cursorX += FontRegistry.LIGHT_TTF.getWidth(icon, INFO_ICON_SIZE) + ICON_TEXT_GAP;
            cursorX = renderValueText(event, line, cursorX, y, rowHeight);
            if (i < lines.size() - 1) {
                cursorX += ENTRY_GAP;
                float dividerX = cursorX - ENTRY_GAP * 0.5f;
                event.getRenderer().rect(dividerX, y + rowHeight * 0.28f, 0.75f, rowHeight * 0.44f, 0.3f, SEPARATOR);
            }
        }
        return cursorX;
    }

    private float renderValueText(EventRender.Screen.Hud event, RenderLine line, float x, float y, float rowHeight) {
        float baseline = y + rowHeight * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', TEXT_SIZE) + 0.4f;
        String value = line.text.current;
        String suffix = "";
        if (line.type == EntryType.FPS && value.endsWith(" fps")) {
            value = value.substring(0, value.length() - 4);
            suffix = " fps";
        } else if (line.type == EntryType.PING && value.endsWith(" ms")) {
            value = value.substring(0, value.length() - 3);
            suffix = " ms";
        } else if (line.type == EntryType.TPS && value.endsWith(" tps")) {
            value = value.substring(0, value.length() - 4);
            suffix = " tps";
        }

        event.getRenderer().text(FontRegistry.DELTA_HUD, x, baseline, TEXT_SIZE, value, TEXT_COLOR);
        x += FontRegistry.DELTA_HUD.getWidth(value, TEXT_SIZE);
        if (!suffix.isEmpty()) {
            event.getRenderer().text(FontRegistry.DELTA_HUD, x, baseline, TEXT_SIZE, suffix, MUTED_COLOR);
            x += FontRegistry.DELTA_HUD.getWidth(suffix, TEXT_SIZE);
        }
        return x;
    }

    private float getTopWidth(List<RenderLine> lines) {
        if (lines.isEmpty()) {
            return 0f;
        }
        return LOGO_BLOCK_WIDTH + TOP_CONTENT_LEFT_PADDING + getEntriesWidth(lines) + ENTRY_PADDING_RIGHT;
    }

    private float getBottomWidth(List<RenderLine> lines) {
        if (lines.isEmpty()) {
            return 0f;
        }
        return PLAIN_SIDE_PADDING * 2f + getEntriesWidth(lines);
    }

    private float getEntriesWidth(List<RenderLine> lines) {
        float width = 0f;
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                width += ENTRY_GAP;
            }
            width += getEntryWidth(lines.get(i));
        }
        return width;
    }

    private float getEntryWidth(RenderLine line) {
        String icon = resolveIcon(line.type);
        String value = line.text.current;
        float widthPx = FontRegistry.LIGHT_TTF.getWidth(icon, INFO_ICON_SIZE) + ICON_TEXT_GAP;
        if (line.type == EntryType.FPS && value.endsWith(" fps")) {
            return widthPx
                    + FontRegistry.DELTA_HUD.getWidth(value.substring(0, value.length() - 4), TEXT_SIZE)
                    + FontRegistry.DELTA_HUD.getWidth(" fps", TEXT_SIZE);
        }
        if (line.type == EntryType.PING && value.endsWith(" ms")) {
            return widthPx
                    + FontRegistry.DELTA_HUD.getWidth(value.substring(0, value.length() - 3), TEXT_SIZE)
                    + FontRegistry.DELTA_HUD.getWidth(" ms", TEXT_SIZE);
        }
        if (line.type == EntryType.TPS && value.endsWith(" tps")) {
            return widthPx
                    + FontRegistry.DELTA_HUD.getWidth(value.substring(0, value.length() - 4), TEXT_SIZE)
                    + FontRegistry.DELTA_HUD.getWidth(" tps", TEXT_SIZE);
        }
        return widthPx + FontRegistry.DELTA_HUD.getWidth(value, TEXT_SIZE);
    }

    private List<RenderLine> pickLines(List<RenderLine> lines, EntryType... types) {
        List<RenderLine> picked = new ArrayList<>();
        for (EntryType type : types) {
            for (RenderLine line : lines) {
                if (line.type == type) {
                    picked.add(line);
                    break;
                }
            }
        }
        return picked;
    }

    private static String resolveIcon(EntryType type) {
        return switch (type) {
            case NICK -> "A";
            case FPS -> "i";
            case PING -> "o";
            case SERVER_IP -> "u";
            case TIME -> "d";
            case TPS -> "f";
        };
    }

    private static int rgba(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }
}

