package tech.lightclient.ui.hud.star.impl;

import com.adl.nativeprotect.User;

import net.minecraft.client.gui.screen.ChatScreen;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.client.other.ClientUtility;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.CustomBossBarHud;
import tech.lightclient.ui.hud.star.HudElement;
import tech.lightclient.ui.hud.star.PopupMenu;
import tech.lightclient.ui.hud.star.StarUtil;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Ватермарка в стиле нового HUD: отдельные «пиллы» — логотип, информация
 * игрока и строка сервера. Текст — шрифт проекта (DELTA_HUD), таймер —
 * формат проекта (HH:mm:ss), фон — жидкое стекло проекта.
 */
public class WatermarkElement extends HudElement {
    private static final float PILL_H = 18.0f;
    private static final float GAP_Y = 4.0f;
    private static final float TOP_GAP_X = 5.0f;
    private static final float PAD_X = 6.0f;
    private static final float TEXT_SIZE = 7.0f;
    private static final float ICON_SIZE = 8.0f;
    private static final float BRAND_SIZE = 15.0f;
    private static final float DOT_SIZE = 2.0f;
    private static final float DOT_GAP = 6.0f;
    private static final float ROUNDING = 3.8f;

    /** Таймер проекта — формат с секундами, как в текущей ватермарке клиента. */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final int DOT_COLOR = 0xFF6E6E6E;
    private static final String GLYPH_USER = "A";
    private static final String GLYPH_FPS = "i";
    private static final String GLYPH_CLOCK = "d";
    private static final String GLYPH_SERVER = "u";
    private static final String GLYPH_WIFI = "o";
    private static final String GLYPH_BRAND = "a";

    public WatermarkElement(Function module) {
        super(module, "Star Ватермарка", "images/etc/island_logo.png", 6.0f, 6.0f);
    }

    @Override
    protected void update(EventRender.Screen.Hud event) {
        Layout layout = buildLayout();

        width = layout.totalWidth;
        height = layout.bottomVisible ? PILL_H * 2.0f + GAP_Y : PILL_H;
        x = Interface.scalePos(scope(), dragging.getX());
        y = Interface.scalePos(scope(), dragging.getY()) + getBossBarOffset();
        setSelfVisible(showWatermark());
    }

    @Override
    protected void draw(EventRender.Screen.Hud event, float alpha) {
        Layout layout = buildLayout();
        Renderer2D r = event.getRenderer();

        // Пилла логотипа — без свечения кромок, чтобы квадрат выглядел чисто.
        drawPill(r, x, y, layout.logoWidth, PILL_H, alpha, false);
        float brandWidth = FontRegistry.LIGHT_TTF.getWidth(GLYPH_BRAND, BRAND_SIZE);
        float brandX = x + (layout.logoWidth - brandWidth) / 2.0f;
        // Тот же тематический ореол, что у иконок биндов и эффектов.
        // Радиус ограничен чёрным квадратиком и рисуется до самого глифа.
        drawIconGlow(r, x + layout.logoWidth / 2.0f, y + PILL_H / 2.0f,
                Math.min(BRAND_SIZE * 0.85f, layout.logoWidth / 2.0f - 0.5f), alpha);
        drawGlyph(r, GLYPH_BRAND, brandX, y, BRAND_SIZE, alpha);

        // Информационная пилла: ник, fps, таймер проекта.
        float infoX = x + layout.logoWidth + TOP_GAP_X;
        drawPill(r, infoX, y, layout.infoWidth, PILL_H, alpha, true);
        float cursor = infoX + PAD_X;
        boolean placed = false;
        if (layout.showNickname) {
            cursor = drawSegmentGlyph(r, GLYPH_USER, cursor, y, layout.username, alpha);
            placed = true;
        }
        if (layout.showFps) {
            if (placed) cursor = drawDot(r, cursor, y, alpha);
            cursor = drawSegmentGlyph(r, GLYPH_FPS, cursor, y, layout.fps, alpha);
            placed = true;
        }
        if (layout.showTime) {
            if (placed) cursor = drawDot(r, cursor, y, alpha);
            drawGlyph(r, GLYPH_CLOCK, cursor, y, ICON_SIZE, alpha);
            cursor += FontRegistry.LIGHT_TTF.getWidth(GLYPH_CLOCK, ICON_SIZE) + 4.0f;
            r.text(FontRegistry.DELTA_HUD, cursor, textBaseline(y), TEXT_SIZE, layout.timeText, StarUtil.text(alpha));
            placed = true;
        }

        // Нижняя пилла: сервер и пинг.
        if (layout.bottomVisible) {
            float bottomY = y + PILL_H + GAP_Y;
            drawPill(r, x, bottomY, layout.bottomWidth, PILL_H, alpha, true);
            float bottomCursor = x + PAD_X;
            bottomCursor = drawSegmentGlyph(r, GLYPH_SERVER, bottomCursor, bottomY, layout.serverText, alpha);
            if (layout.showPing && layout.pingText != null) {
                bottomCursor = drawDot(r, bottomCursor, bottomY, alpha);
                drawGlyph(r, GLYPH_WIFI, bottomCursor, bottomY, ICON_SIZE, alpha);
                bottomCursor += FontRegistry.LIGHT_TTF.getWidth(GLYPH_WIFI, ICON_SIZE) + 4.0f;
                bottomCursor = drawValue(r, bottomCursor, bottomY, layout.pingText, alpha);
                r.text(FontRegistry.DELTA_HUD, bottomCursor,
                        StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', bottomY, PILL_H, TEXT_SIZE - 0.5f),
                        TEXT_SIZE - 0.5f, "ms", accent(alpha));
            }
        }
    }

    private Layout buildLayout() {
        Layout l = new Layout();

        String rawUser = User.getInstance().profile("username");
        l.username = rawUser == null || rawUser.isEmpty() ? "Player" : rawUser;
        l.showNickname = boolSetting(scope(), "showNickname", true);
        l.showFps = boolSetting(scope(), "showFps", true);
        l.showTime = boolSetting(scope(), "showTime", true);
        l.showPing = boolSetting(scope(), "showPing", true);
        l.showServer = boolSetting(scope(), "showServerIp", true);
        l.fps = ClientUtility.getFPS();
        l.timeText = LocalTime.now().format(TIME_FORMAT);
        l.serverText = currentServerLabel();
        l.pingText = currentPingText();
        l.bottomVisible = l.showServer && !l.serverText.isEmpty();

        // Если пользователь выключил всё — оставляем ник, чтобы пилла не пустела.
        boolean anyTop = l.showNickname || l.showFps || l.showTime;
        float topContent = 0.0f;
        boolean placed = false;
        if (l.showNickname || !anyTop) {
            topContent += segmentGlyphWidth(l.username);
            placed = true;
        }
        if (l.showFps) {
            if (placed) topContent += DOT_GAP * 2.0f + DOT_SIZE;
            topContent += segmentGlyphWidth(l.fps);
            placed = true;
        }
        if (l.showTime) {
            if (placed) topContent += DOT_GAP * 2.0f + DOT_SIZE;
            topContent += FontRegistry.LIGHT_TTF.getWidth(GLYPH_CLOCK, ICON_SIZE) + 4.0f
                    + FontRegistry.DELTA_HUD.getWidth(l.timeText, TEXT_SIZE);
        }

        // Логотип впритык к краям своего квадратика.
        l.logoWidth = Math.max(PILL_H, FontRegistry.LIGHT_TTF.getWidth(GLYPH_BRAND, BRAND_SIZE) + 1.5f);
        l.infoWidth = PAD_X * 2.0f + topContent;

        float bottomContent = 0.0f;
        if (l.bottomVisible) {
            bottomContent += segmentGlyphWidth(l.serverText);
            if (l.showPing && l.pingText != null) {
                bottomContent += DOT_GAP * 2.0f + DOT_SIZE
                        + FontRegistry.LIGHT_TTF.getWidth(GLYPH_WIFI, ICON_SIZE) + 4.0f
                        + FontRegistry.DELTA_HUD.getWidth(l.pingText, TEXT_SIZE)
                        + FontRegistry.DELTA_HUD.getWidth("ms", TEXT_SIZE - 0.5f);
            }
        }
        l.bottomWidth = PAD_X * 2.0f + bottomContent;

        l.totalWidth = Math.max(l.logoWidth + TOP_GAP_X + l.infoWidth, l.bottomVisible ? l.bottomWidth : 0.0f);
        return l;
    }

    /** Ширина сегмента «иконка + значение» в информационной пилле. */
    private float segmentGlyphWidth(String text) {
        return FontRegistry.LIGHT_TTF.getWidth(GLYPH_USER, ICON_SIZE) + 4.0f
                + FontRegistry.DELTA_HUD.getWidth(text, TEXT_SIZE);
    }

    private float drawDot(Renderer2D r, float px, float py, float alpha) {
        float cx = px + DOT_GAP;
        float cy = py + PILL_H / 2.0f - DOT_SIZE / 2.0f;
        r.rect(cx, cy, DOT_SIZE, DOT_SIZE, DOT_SIZE / 2.0f,
                ClientColors.applyAlpha(DOT_COLOR, alpha));
        return px + DOT_GAP + DOT_SIZE + DOT_GAP;
    }

    private void drawGlyph(Renderer2D r, String glyph, float px, float py, float size, float alpha) {
        // Для TTF-шрифтов битмап глифа лежит выше базовой линии на yOffset,
        // поэтому для центрирования по вертикали смещение вычитается.
        float glyphY = py + PILL_H * 0.5f
                - FontRegistry.centeredBaselineOffset(FontRegistry.LIGHT_TTF, glyph.charAt(0), size);
        r.text(FontRegistry.LIGHT_TTF, px, glyphY, size, glyph, accent(alpha));
    }

    private float drawSegmentGlyph(Renderer2D r, String glyph, float px, float py, String text, float alpha) {
        drawGlyph(r, glyph, px, py, ICON_SIZE, alpha);
        float cursor = px + FontRegistry.LIGHT_TTF.getWidth(glyph, ICON_SIZE) + 4.0f;
        return drawValue(r, cursor, py, text, alpha);
    }

    private float drawValue(Renderer2D r, float px, float py, String text, float alpha) {
        r.text(FontRegistry.DELTA_HUD, px, textBaseline(py), TEXT_SIZE, text, StarUtil.text(alpha));
        return px + FontRegistry.DELTA_HUD.getWidth(text, TEXT_SIZE);
    }

    private float textBaseline(float pillY) {
        return StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', pillY, PILL_H, TEXT_SIZE);
    }

    private void drawPill(Renderer2D r, float px, float py, float pw, float ph, float alpha, boolean accentGlow) {
        drawClientRect(r, scope(), px, py, pw, ph, ROUNDING, alpha, dragging.isDragging(), accentGlow);
    }

    private boolean showWatermark() {
        return mc.player != null || mc.currentScreen instanceof ChatScreen;
    }

    @Override
    public boolean show() {
        return showWatermark();
    }

    @Override
    public String settingName() {
        return "Ватермарка";
    }

    @Override
    public void fillSettingsPopup(PopupMenu popup) {
        popup.checkbox("Ник", () -> boolSetting(scope(), "showNickname", true),
                value -> setBoolSetting(scope(), "showNickname", value));
        popup.checkbox("FPS", () -> boolSetting(scope(), "showFps", true),
                value -> setBoolSetting(scope(), "showFps", value));
        popup.checkbox("Время", () -> boolSetting(scope(), "showTime", true),
                value -> setBoolSetting(scope(), "showTime", value));
        popup.checkbox("Пинг", () -> boolSetting(scope(), "showPing", true),
                value -> setBoolSetting(scope(), "showPing", value));
        popup.checkbox("Сервер", () -> boolSetting(scope(), "showServerIp", true),
                value -> setBoolSetting(scope(), "showServerIp", value));
    }

    private String currentServerLabel() {
        if (mc.getCurrentServerEntry() == null || mc.getCurrentServerEntry().address == null
                || mc.getCurrentServerEntry().address.isEmpty()) {
            return "";
        }
        String address = mc.getCurrentServerEntry().address;
        if (boolSetting(scope(), "compact_server", true)) {
            int colon = address.indexOf(':');
            String host = colon >= 0 ? address.substring(0, colon) : address;
            String[] parts = host.split("\\.");
            return parts.length > 0 ? parts[0] : host;
        }
        return address;
    }

    private static String currentPingText() {
        try {
            if (mc.player != null && mc.getNetworkHandler() != null
                    && mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null) {
                int ping = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency();
                return String.valueOf(ping);
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private float getBossBarOffset() {
        float scaleFactor = Interface.getHudScale(scope());
        if (CustomBossBarHud.shouldUseCustomBossBar()) {
            return CustomBossBarHud.getOccupiedHeight() / Math.max(scaleFactor, 0.0001f);
        }
        if (mc.inGameHud == null || mc.inGameHud.getBossBarHud() == null) {
            return 0.0f;
        }
        int bossBarCount = mc.inGameHud.getBossBarHud().bossBars.size();
        if (bossBarCount <= 0) {
            return 0.0f;
        }
        return (bossBarCount * 19.0f + 8.0f) / Math.max(scaleFactor, 0.0001f);
    }

    private static final class Layout {
        float logoWidth;
        float infoWidth;
        float bottomWidth;
        float totalWidth;
        boolean bottomVisible;
        boolean showNickname;
        boolean showFps;
        boolean showTime;
        boolean showPing;
        boolean showServer;
        String fps;
        String username;
        String timeText;
        String serverText;
        String pingText;
    }
}
