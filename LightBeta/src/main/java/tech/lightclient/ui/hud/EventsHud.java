package tech.lightclient.ui.hud;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.math.MathUtil;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.first.FirstHudTheme;
import org.json.JSONArray;
import org.json.JSONObject;
import net.minecraft.client.gui.screen.ChatScreen;

import java.awt.Color;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventsHud implements HudElement {
    public static final String SETTINGS_SCOPE = "Events";
    public static final String SETTING_ALWAYS_SHOW = "alwaysShow";
    public static float width;
    public static float height;

    private static final String API_URL = "https://api.holyworld.me/v1/events";
    private static final long REFRESH_MS = 60_000L;
    private static final int MAX_ROWS = 8;
    private static final float EXPAND_MS = 260f;

    private static final float DOT_SIZE = 8f;
    private static final float DIVIDER_WIDTH = 1.2f;
    private static final float DIVIDER_HEIGHT = 12f;
    private static final float DIVIDER_RADIUS = 0.45f;
    private static final float ICON_GAP = 4.2f;
    private static final float TEXT_GAP = 4.6f;
    private static final float SERVER_GAP = 6f;
    private static final float ROW_PADDING = 20f;
    private static final float TEXT_Y_OFFSET = 0.3f;
    private static final float SEPARATOR_OFFSET = 0.525f;
    private static final float SEPARATOR_HEIGHT = 1.05f;
    private static final float SEPARATOR_RADIUS = 0.375f;
    private static final float HEADER_TITLE_GAP = 5f;
    private static final float HW_LETTER_GAP = 1.5f;
    private static final String HEADER_HW_TEXT = "HW";
    private static final String HEADER_TITLE_TEXT = "Events";

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "EventsHud-Fetch");
        thread.setDaemon(true);
        return thread;
    });

    private static final AtomicBoolean FETCHING = new AtomicBoolean(false);
    private static volatile List<EventData> events = new ArrayList<>();
    private static volatile boolean loadedOnce = false;
    private static volatile boolean loading = false;
    private static volatile long lastFetchTime = 0L;

    private static final Map<String, String> SERVER_NAMES = buildServerNames();

    private final Dragging dragging;
    private final SimpleLinearAnimation animation = new SimpleLinearAnimation();
    private long lastAnimTime = System.currentTimeMillis();
    private float displayCount = 0f;

    public EventsHud(Dragging dragging) {
        this.dragging = dragging;
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        if (mc == null || mc.player == null || mc.world == null) {
            resetSize();
            return;
        }
        ensureFetched();

        List<EventData> all = new ArrayList<>(events);
        all.sort((a, b) -> {
            int cmp = a.serverName.compareToIgnoreCase(b.serverName);
            if (cmp != 0) {
                return cmp;
            }
            return a.displayName.compareToIgnoreCase(b.displayName);
        });

        boolean chatOpen = mc.currentScreen instanceof ChatScreen;
        boolean alwaysShow = DraggingManager.getHudBoolean(SETTINGS_SCOPE, SETTING_ALWAYS_SHOW, false);
        boolean hasContent = loadedOnce && !all.isEmpty();
        boolean emptyMessage = loadedOnce && all.isEmpty();
        boolean shouldShow = alwaysShow || chatOpen || hasContent || !loadedOnce;

        animation.setDuration(Interface.getAlphaDurationMs());
        if (shouldShow) {
            animation.show();
        } else {
            animation.hide();
        }

        float alpha = animation.getProgress();
        boolean hiding = animation.getTarget() == 0f;
        if (alpha <= 0f && animation.get() == 0) {
            resetSize();
            return;
        }

        float dt = updateAnimTime();
        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());

        boolean mouseActive = mc.currentScreen != null;
        boolean hovered = mouseActive && isHovered(mc.mouse.getX(), mc.mouse.getY(), x, y, width, height, scaleFactor);
        boolean expanded = hovered && !all.isEmpty();

        int totalCount = all.size();
        int maxRows = Math.min(MAX_ROWS, totalCount);
        boolean showFooter = !expanded && totalCount > MAX_ROWS;
        int targetRows;
        if (emptyMessage) {
            targetRows = 1;
        } else if (expanded) {
            targetRows = totalCount;
        } else {
            targetRows = maxRows + (showFooter ? 1 : 0);
        }

        displayCount = animate(displayCount, targetRows, EXPAND_MS, dt);
        float bodyRowCount = Math.max(0f, displayCount);
        boolean bodyVisible = bodyRowCount > 0.01f;

        float targetWidth = measureWidth(all, emptyMessage, showFooter, totalCount, maxRows, bodyRowCount);
        if (Float.isNaN(width) || width == 0f) {
            width = targetWidth;
        }
        width = animate(width, targetWidth, 120f, dt);

        float listStartY = y + FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.ROW_GAP;
        float contentHeight = FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.SIDE_PADDING
                + bodyRowCount * FirstHudTheme.ROW_HEIGHT
                + Math.max(0f, bodyRowCount - 1f) * FirstHudTheme.ROW_GAP;

        float scale = Interface.animatedScale(Interface.isBounceAnimation() ? 0f : 0.8f, alpha, hiding);
        event.getRenderer().pushScale(scale, scale, x + width * 0.5f, y + contentHeight * 0.5f);

        FirstHudTheme.drawPanel(event, SETTINGS_SCOPE, x, y, width, contentHeight, alpha);
        renderHeader(event, x, y, width, alpha);

        if (bodyVisible) {
            if (emptyMessage) {
                float rowProgress = MathUtil.clamp(bodyRowCount - 0f, 0f, 1f);
                float rowHeight = FirstHudTheme.ROW_HEIGHT * rowProgress;
                if (rowHeight > 0.5f) {
                    renderCenteredRow(event, x, listStartY, rowHeight, "На данный момент нету ивентов",
                            FirstHudTheme.MUTED_TEXT_COLOR, rowProgress * alpha);
                }
            } else {
                float currentY = listStartY;
                for (int i = 0; i < totalCount; i++) {
                    float rowProgress = MathUtil.clamp(bodyRowCount - i, 0f, 1f);
                    if (rowProgress <= 0.01f) {
                        break;
                    }
                    float rowHeight = FirstHudTheme.ROW_HEIGHT * rowProgress;
                    if (rowHeight <= 0.5f) {
                        break;
                    }
                    renderRow(event, all.get(i), x, currentY, width, rowHeight, rowProgress * alpha);
                    if (i < totalCount - 1) {
                        drawRowSeparator(event, x, width, currentY, rowProgress * alpha);
                    }
                    currentY += rowHeight + FirstHudTheme.ROW_GAP;
                }
                if (showFooter) {
                    float footerProgress = bodyRowCount > maxRows ? MathUtil.clamp(bodyRowCount - maxRows, 0f, 1f) : 0f;
                    float footerHeight = FirstHudTheme.ROW_HEIGHT * footerProgress;
                    if (footerHeight > 0.5f) {
                        String more = "+" + (totalCount - MAX_ROWS) + " ещё";
                        renderCenteredRow(event, x, currentY, footerHeight, more,
                                FirstHudTheme.MUTED_TEXT_COLOR, footerProgress * alpha);
                    }
                }
            }
        }

        event.getRenderer().popScale();

        height = contentHeight;
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }

    private boolean isHovered(double mouseX, double mouseY, float x, float y, float w, float h, float hudScale) {
        if (w <= 0f || h <= 0f) {
            return false;
        }
        float mx = (float) mouseX / Math.max(0.01f, hudScale);
        float my = (float) mouseY / Math.max(0.01f, hudScale);
        return MathUtil.isHovered(mx, my, x, y, w, h);
    }

    private void renderHeader(EventRender.Screen.Hud event, float x, float y, float width, float alpha) {
        event.getRenderer().gradient(
                x + FirstHudTheme.OUTLINE,
                y + FirstHudTheme.OUTLINE,
                width - FirstHudTheme.OUTLINE * 2f,
                FirstHudTheme.HEADER_HEIGHT - FirstHudTheme.OUTLINE,
                FirstHudTheme.PANEL_RADIUS - FirstHudTheme.OUTLINE, FirstHudTheme.PANEL_RADIUS - FirstHudTheme.OUTLINE, 0f, 0f,
                ClientColors.applyAlpha(FirstHudTheme.HEADER_TOP, alpha),
                ClientColors.applyAlpha(FirstHudTheme.HEADER_TOP, alpha),
                ClientColors.applyAlpha(FirstHudTheme.HEADER_BOTTOM, alpha),
                ClientColors.applyAlpha(FirstHudTheme.HEADER_BOTTOM, alpha)
        );
        event.getRenderer().rect(
                x + FirstHudTheme.SIDE_PADDING,
                y + FirstHudTheme.HEADER_HEIGHT,
                width - FirstHudTheme.SIDE_PADDING * 2f,
                FirstHudTheme.OUTLINE,
                Math.max(0.3f, FirstHudTheme.OUTLINE * 0.4f),
                ClientColors.applyAlpha(FirstHudTheme.DIVIDER, alpha)
        );

        float titleY = y + FirstHudTheme.HEADER_HEIGHT * 0.5f - FirstHudTheme.HEADER_TITLE_Y_OFFSET;
        float textX = x + FirstHudTheme.SIDE_PADDING + FirstHudTheme.HEADER_TEXT_INSET;
        float iconX = x + width - FirstHudTheme.SIDE_PADDING - FirstHudTheme.HEADER_ICON_RIGHT_INSET;
        float iconY = y + FirstHudTheme.HEADER_HEIGHT * 0.5f - FirstHudTheme.HEADER_ICON_Y_OFFSET;

        event.getRenderer().text(FontRegistry.DELTA_HUD, textX, titleY, FirstHudTheme.TITLE_SIZE, HEADER_TITLE_TEXT,
                ClientColors.applyAlpha(FirstHudTheme.TEXT_COLOR, alpha));

        float hwX = textX + FontRegistry.DELTA_HUD.getWidth(HEADER_TITLE_TEXT, FirstHudTheme.TITLE_SIZE) + HEADER_TITLE_GAP;
        float charX = hwX;
        for (int i = 0; i < HEADER_HW_TEXT.length(); i++) {
            String ch = String.valueOf(HEADER_HW_TEXT.charAt(i));
            event.getRenderer().text(FontRegistry.DELTA_HUD, charX, titleY, FirstHudTheme.TITLE_SIZE, ch,
                    hwWaveColor(i, alpha));
            charX += FontRegistry.DELTA_HUD.getWidth(ch, FirstHudTheme.TITLE_SIZE) + HW_LETTER_GAP;
        }

        event.getRenderer().text(FontRegistry.LIGHT_TTF, iconX, iconY, FirstHudTheme.HEADER_ICON_SIZE, "e",
                ClientColors.applyAlpha(FirstHudTheme.ICON_COLOR, alpha));
    }

    private static int hwWaveColor(int index, float alpha) {
        double time = System.currentTimeMillis() / 1000.0;
        float phase = (float) (time * 3.0 + index * Math.PI);
        float hue = 0.55f + (float) Math.sin(phase) * 0.05f;
        float bri = 0.45f + (float) Math.sin(phase) * 0.4f;
        int rgb = Color.HSBtoRGB(hue, 0.85f, bri);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return ClientColors.applyAlpha((255 << 24) | (r << 16) | (g << 8) | b, alpha);
    }

    private void renderRow(EventRender.Screen.Hud event, EventData data, float x, float y, float width, float height,
                           float alpha) {
        float rowCenterY = y + height * 0.5f;
        float textSize = FirstHudTheme.ROW_TEXT_SIZE;
        float textY = rowCenterY + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', textSize) + TEXT_Y_OFFSET;

        float dotX = x + FirstHudTheme.SIDE_PADDING;
        float dotCx = dotX + DOT_SIZE * 0.5f;
        float dotCy = rowCenterY;
        event.getRenderer().circle(dotCx, dotCy, DOT_SIZE * 0.5f, 0f, 1f,
                ClientColors.applyAlpha(rarityColor(data.rare), alpha));

        float dividerX = dotX + DOT_SIZE + ICON_GAP;
        float dividerY = rowCenterY - DIVIDER_HEIGHT * 0.5f;
        event.getRenderer().rect(dividerX, dividerY, DIVIDER_WIDTH, DIVIDER_HEIGHT, DIVIDER_RADIUS,
                ClientColors.applyAlpha(FirstHudTheme.ROW_DIVIDER, alpha));

        float textX = dividerX + TEXT_GAP;
        event.getRenderer().text(FontRegistry.DELTA_HUD, textX, textY, textSize, data.displayName,
                ClientColors.applyAlpha(FirstHudTheme.TEXT_COLOR, alpha));

        float serverX = textX + FontRegistry.DELTA_HUD.getWidth(data.displayName, textSize) + SERVER_GAP;
        event.getRenderer().text(FontRegistry.DELTA_HUD, serverX, textY, textSize, data.serverName,
                ClientColors.applyAlpha(FirstHudTheme.MUTED_TEXT_COLOR, alpha));

        if (data.rare != null && !data.rare.isBlank()) {
            String rarityText = translateRarity(data.rare);
            float rarityWidth = FontRegistry.DELTA_HUD.getWidth(rarityText, textSize);
            float rarityX = x + width - FirstHudTheme.SIDE_PADDING - rarityWidth;
            event.getRenderer().text(FontRegistry.DELTA_HUD, rarityX, textY, textSize, rarityText,
                    ClientColors.applyAlpha(FirstHudTheme.BRIGHT_MUTED_TEXT_COLOR, alpha));
        }
    }

    private void drawRowSeparator(EventRender.Screen.Hud event, float x, float width, float rowY, float alpha) {
        float separatorY = rowY + FirstHudTheme.ROW_HEIGHT + FirstHudTheme.ROW_GAP * 0.5f - SEPARATOR_OFFSET;
        event.getRenderer().rect(
                x + FirstHudTheme.SIDE_PADDING,
                separatorY,
                width - FirstHudTheme.SIDE_PADDING * 2f,
                SEPARATOR_HEIGHT,
                SEPARATOR_RADIUS,
                ClientColors.applyAlpha(FirstHudTheme.SEPARATOR, alpha)
        );
    }

    private void renderCenteredRow(EventRender.Screen.Hud event, float x, float y, float height, String text,
                                   int color, float alpha) {
        float textSize = FirstHudTheme.ROW_TEXT_SIZE;
        float textY = y + height * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', textSize) + TEXT_Y_OFFSET;
        event.getRenderer().text(FontRegistry.DELTA_HUD, x + FirstHudTheme.SIDE_PADDING, textY, textSize, text,
                ClientColors.applyAlpha(color, alpha));
    }

    private float measureWidth(List<EventData> all, boolean emptyMessage, boolean showFooter, int totalCount, int maxRows,
                               float bodyRowCount) {
        float hwWidth = FontRegistry.DELTA_HUD.getWidth(HEADER_HW_TEXT, FirstHudTheme.TITLE_SIZE);
        float eventsWidth = FontRegistry.DELTA_HUD.getWidth(HEADER_TITLE_TEXT, FirstHudTheme.TITLE_SIZE);
        float result = FirstHudTheme.SIDE_PADDING * 2f
                + hwWidth
                + HEADER_TITLE_GAP
                + eventsWidth
                + FirstHudTheme.HEADER_MIN_RIGHT_SPACE
                + 20f;

        int shownEventRows = Math.min(totalCount, (int) Math.ceil(bodyRowCount));
        for (int i = 0; i < shownEventRows; i++) {
            result = Math.max(result, getRowWidth(all.get(i)));
        }

        if (showFooter && bodyRowCount > maxRows) {
            String more = "+" + (totalCount - MAX_ROWS) + " ещё";
            float w = FirstHudTheme.SIDE_PADDING * 2f
                    + FontRegistry.DELTA_HUD.getWidth(more, FirstHudTheme.ROW_TEXT_SIZE)
                    + ROW_PADDING;
            result = Math.max(result, w);
        }
        if (emptyMessage) {
            String msg = "На данный момент нету ивентов";
            float w = FirstHudTheme.SIDE_PADDING * 2f
                    + FontRegistry.DELTA_HUD.getWidth(msg, FirstHudTheme.ROW_TEXT_SIZE)
                    + ROW_PADDING;
            result = Math.max(result, w);
        }
        return result;
    }

    private float getRowWidth(EventData data) {
        float textSize = FirstHudTheme.ROW_TEXT_SIZE;
        String rarityText = (data.rare != null && !data.rare.isBlank()) ? translateRarity(data.rare) : "";
        return FirstHudTheme.SIDE_PADDING * 2f
                + DOT_SIZE
                + ICON_GAP
                + TEXT_GAP
                + FontRegistry.DELTA_HUD.getWidth(data.displayName, textSize)
                + SERVER_GAP
                + FontRegistry.DELTA_HUD.getWidth(data.serverName, textSize)
                + FontRegistry.DELTA_HUD.getWidth(rarityText, textSize)
                + ROW_PADDING;
    }

    private static void ensureFetched() {
        long now = System.currentTimeMillis();
        if (now - lastFetchTime < REFRESH_MS) {
            return;
        }
        if (!FETCHING.compareAndSet(false, true)) {
            return;
        }
        loading = true;
        SCHEDULER.execute(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("User-Agent", "Light")
                        .header("Accept", "application/json")
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();
                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (response.statusCode() == 200) {
                    events = parseEvents(response.body());
                    loadedOnce = true;
                }
            } catch (Throwable ignored) {
            } finally {
                loading = false;
                FETCHING.set(false);
                lastFetchTime = System.currentTimeMillis();
            }
        });
    }

    private static List<EventData> parseEvents(String body) {
        List<EventData> list = new ArrayList<>();
        if (body == null || body.isBlank()) {
            return list;
        }
        try {
            JSONObject root = new JSONObject(body);
            for (String serverId : root.keySet()) {
                JSONArray array = root.optJSONArray(serverId);
                if (array == null) {
                    continue;
                }
                String serverName = SERVER_NAMES.getOrDefault(serverId, fallbackServerName(serverId));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject ev = array.optJSONObject(i);
                    if (ev == null) {
                        continue;
                    }
                    JSONObject meta = ev.optJSONObject("metadata");
                    String displayName = "Ивент";
                    String rare = "";
                    if (meta != null) {
                        displayName = meta.optString("displayName", "");
                        rare = meta.optString("rare", "");
                    }
                    if (displayName.isBlank()) {
                        displayName = ev.optString("id", "Ивент");
                    }
                    list.add(new EventData(serverId, serverName, capitalizeFirst(shortenName(displayName)), rare));
                }
            }
        } catch (Throwable ignored) {
        }
        return list;
    }

    private static String capitalizeFirst(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private static String shortenName(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        String trim = name.trim();
        String custom = NAME_SHORTENINGS.get(trim);
        if (custom != null) {
            return custom;
        }
        int space = trim.indexOf(' ');
        if (space > 0) {
            return trim.substring(space + 1);
        }
        return trim;
    }

    private static final Map<String, String> NAME_SHORTENINGS = new HashMap<>();

    static {
        NAME_SHORTENINGS.put("Ценный груз", "Цн груз");
    }

    private static String translateRarity(String rare) {
        if (rare == null || rare.isBlank()) {
            return "";
        }
        String lower = rare.toLowerCase();
        if (lower.contains("legendary") || lower.contains("легендар")) {
            return "Легендарный";
        }
        if (lower.contains("mythic")) {
            return "Мифический";
        }
        if (lower.contains("epic")) {
            return "Эпический";
        }
        if (lower.contains("explosive") || lower.contains("взрыв")) {
            return "Взрывной";
        }
        if (lower.contains("deadly") || lower.contains("смертельн")) {
            return "Смертельный";
        }
        if (lower.contains("peaceful") || lower.contains("мирн")) {
            return "Мирный";
        }
        if (lower.contains("roskoshni") || lower.contains("роскошн")) {
            return "Роскошный";
        }
        if (lower.contains("rare") || lower.contains("редк")) {
            return "Редкий";
        }
        if (lower.contains("normal") || lower.contains("common") || lower.contains("default") || lower.contains("обычн")) {
            return "Обычный";
        }
        return rare;
    }

    private static int rarityColor(String rare) {
        if (rare == null || rare.isBlank()) {
            return FirstHudTheme.ICON_COLOR;
        }
        String lower = rare.toLowerCase();
        if (lower.contains("legendary") || lower.contains("легендар")) {
            return 0xFFD4AF37;
        }
        if (lower.contains("mythic")) {
            return 0xFFC678DD;
        }
        if (lower.contains("epic")) {
            return 0xFFB23AEE;
        }
        if (lower.contains("explosive") || lower.contains("взрыв")) {
            return 0xFFFF8C42;
        }
        if (lower.contains("deadly") || lower.contains("смертельн")) {
            return 0xFFFF5555;
        }
        if (lower.contains("peaceful") || lower.contains("мирн")) {
            return 0xFF57E389;
        }
        if (lower.contains("roskoshni") || lower.contains("роскошн")) {
            return 0xFFD4AF37;
        }
        if (lower.contains("rare") || lower.contains("редк")) {
            return 0xFF40A6FF;
        }
        if (lower.contains("normal") || lower.contains("common") || lower.contains("default") || lower.contains("обычн")) {
            return 0xFF9DA0A8;
        }
        return FirstHudTheme.ICON_COLOR;
    }

    private static String fallbackServerName(String serverId) {
        if (serverId == null || serverId.isBlank()) {
            return serverId == null ? "?" : serverId;
        }
        if (serverId.startsWith("LITE_NEW_ANARCHY_")) {
            return "Лайт (1.20) #" + serverId.substring("LITE_NEW_ANARCHY_".length());
        }
        if (serverId.startsWith("LITE_ANARCHY_")) {
            String num = serverId.substring("LITE_ANARCHY_".length());
            try {
                int n = Integer.parseInt(num);
                String prefix;
                if (n <= 17) {
                    prefix = "СолоЛайт";
                } else if (n <= 38) {
                    prefix = "ДуоЛайт";
                } else if (n <= 57) {
                    prefix = "ТриоЛайт";
                } else {
                    prefix = "КланЛайт";
                }
                return prefix + " #" + n;
            } catch (NumberFormatException ignored) {
                return "Анархия #" + num;
            }
        }
        return serverId;
    }

    private static Map<String, String> buildServerNames() {
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i <= 17; i++) {
            map.put("LITE_ANARCHY_" + i, "СолоЛайт #" + i);
        }
        for (int i = 18; i <= 38; i++) {
            map.put("LITE_ANARCHY_" + i, "ДуоЛайт #" + i);
        }
        for (int i = 39; i <= 57; i++) {
            map.put("LITE_ANARCHY_" + i, "ТриоЛайт #" + i);
        }
        for (int i = 58; i <= 74; i++) {
            map.put("LITE_ANARCHY_" + i, "КланЛайт #" + i);
        }
        map.put("LITE_NEW_ANARCHY_1", "Лайт (1.20) #1");
        map.put("LITE_NEW_ANARCHY_2", "Лайт (1.20) #2");
        map.put("LITE_NEW_ANARCHY_3", "Лайт (1.20) #3");
        return map;
    }

    private void resetSize() {
        width = 0f;
        height = 0f;
        dragging.setWidth(0f);
        dragging.setHeight(0f);
    }

    private float updateAnimTime() {
        long now = System.currentTimeMillis();
        float dt = (now - lastAnimTime) / 1000f;
        lastAnimTime = now;
        if (!Float.isFinite(dt) || dt < 0f) {
            return 0f;
        }
        return Math.min(dt, 0.05f);
    }

    private static float animate(float value, float target, float durationMs, float dt) {
        if (durationMs <= 0f) {
            return target;
        }
        float duration = Math.max(1e-6f, durationMs / 1000f);
        float k = (float) (-Math.log(0.05f) / duration);
        float t = 1f - (float) Math.exp(-k * Math.max(0f, dt));
        return value + (target - value) * MathUtil.clamp(t, 0f, 1f);
    }

    private record EventData(String serverId, String serverName, String displayName, String rare) {
    }
}
