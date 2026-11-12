package tech.lightclient.ui.gui.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.utils.render.CursorHelper;
import tech.lightclient.client.utils.render.color.basic.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.ui.gui.elements.ListAppearAnimator;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

/**
 * Раздел Events: ивенты FunTime и MixerGrief с таймерами и быстрым переходом на сервер.
 * Данные тянутся в фоновом потоке, отрисовка — только чтение готовых списков.
 */
public class EventsPanel {

    private static final String FT_API = "https://api.funtime.su/method/events-info";
    /** Токен FunTime API. Публичный ключ клиента, лежит в коде как и в референсе. */
    private static final String FT_TOKEN = "1477d51.2fa08175b7e6df549477e2d933cdf9cf";
    private static final String MG_API = "http://api-v1.mixergrief.pw:8000/api/events";
    private static final int MAX_PER_REQ = 30;
    private static final long REFRESH_MS = 10_000L;

    private static final int[] FT_SERVERS_121 = {
            100, 101, 102, 103, 104, 105, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210,
            300, 301, 302, 303, 400, 401, 500, 501, 600, 700, 800, 900, 904
    };
    private static final int[] FT_SERVERS_1165 = {
            1001, 1002, 1003, 1004, 1005, 2001, 2002, 2003, 2004, 2005,
            3001, 3002, 3003, 4001, 4002, 5001, 5002, 6001, 6002, 6003, 6004
    };

    private final List<EventCard> ftCards = new CopyOnWriteArrayList<>();
    private final List<EventCard> mgCards = new CopyOnWriteArrayList<>();
    private final List<EventCard> mgMines = new CopyOnWriteArrayList<>();

    private volatile long fetchTimestamp = 0L;
    private volatile boolean fetching = false;
    private volatile String ftStatus = "Загрузка...";
    private volatile String mgStatus = "Загрузка...";
    private long lastFetch = 0L;

    private final List<float[]> buttonBoxes = new ArrayList<>();
    private final List<Integer> buttonServers = new ArrayList<>();

    /** Поэтапное появление/исчезновение карточек по каждой колонке. */
    private final ListAppearAnimator<EventCard> ftAnim = new ListAppearAnimator<>(EventsPanel::cardKey);
    private final ListAppearAnimator<EventCard> mgAnim = new ListAppearAnimator<>(EventsPanel::cardKey);
    private final ListAppearAnimator<EventCard> mineAnim = new ListAppearAnimator<>(EventsPanel::cardKey);
    private long lastDrawTime = 0L;

    /** Ключ карточки: не зависит от таймера, поэтому обновление данных не перезапускает анимацию. */
    private static String cardKey(EventCard card) {
        return card.server() + '|' + card.eventId() + '|' + card.eventType();
    }

    public record EventCard(String server, int serverNum, String version, String eventId,
                            String phase, int secondsLeft, String loot, String eventType) {

        String displayName() {
            return switch (eventId) {
                case "vulkan" -> "Вулкан";
                case "beacon" -> "Маяк";
                case "hellm" -> "Адский маяк";
                case "airdrop" -> "Аирдроп";
                case "myst_beacon" -> "Мист. маяк";
                case "meteor_rain" -> "Метеоритный дождь";
                case "ожидание" -> "Ожидание ивента";
                case "supply_drop" -> "Сброс припасов";
                case "boss" -> "Босс";
                case "treasure" -> "Сокровище";
                case "raid" -> "Рейд";
                default -> eventId;
            };
        }

        String statusStr(long elapsedSec) {
            int remaining = "LOOTING".equals(phase)
                    ? Math.max(0, 60 - (int) elapsedSec)
                    : Math.max(0, secondsLeft - (int) elapsedSec);
            String time = formatTime(remaining);
            if (phase == null) return "До спавна: " + time;
            return switch (phase) {
                case "ACTIVATING" -> "Появится через: " + time;
                case "RUNNING" -> "До активации: " + time;
                case "OPENED" -> "До открытия: " + time;
                case "LOOTING" -> "До закрытия: " + time;
                case "WAITING" -> "До спавна: " + time;
                default -> "Статус: " + time;
            };
        }

        private static String formatTime(int sec) {
            int m = sec / 60, s = sec % 60;
            return m > 0 ? m + " мин " + s + " сек" : s + " сек";
        }
    }

    // ── Загрузка ───────────────────────────────────────────────

    public void tick() {
        long now = System.currentTimeMillis();
        if (!fetching && now - lastFetch > REFRESH_MS) {
            fetching = true;
            lastFetch = now;
            Thread t = new Thread(this::fetchAll, "SpaceVisuals-EventsFetch");
            t.setDaemon(true);
            t.start();
        }
    }

    private void fetchAll() {
        try {
            List<EventCard> result = new ArrayList<>();
            fetchServers(FT_SERVERS_121, "1.21", result);
            fetchServers(FT_SERVERS_1165, "1.16.5", result);
            result.removeIf(c -> c.eventId == null);
            result.removeIf(c -> "OPENED".equals(c.phase) || "LOOTING".equals(c.phase));
            result.sort(Comparator.comparingInt(EventCard::secondsLeft));
            ftCards.clear();
            ftCards.addAll(result);
            ftStatus = ftCards.isEmpty() ? "Нет активных ивентов" : "";
        } catch (Exception e) {
            ftStatus = "FunTime недоступен";
        }

        try {
            List<EventCard> events = new ArrayList<>();
            List<EventCard> mines = new ArrayList<>();
            fetchMixerGrief(events, mines);
            mgCards.clear();
            mgCards.addAll(events);
            mgMines.clear();
            mgMines.addAll(mines);
            mgStatus = mgCards.isEmpty() && mgMines.isEmpty() ? "Нет данных" : "";
        } catch (Exception e) {
            mgStatus = "MixerGrief недоступен";
        }

        fetchTimestamp = System.currentTimeMillis();
        fetching = false;
    }

    private void fetchServers(int[] nums, String ver, List<EventCard> out) {
        for (int i = 0; i < nums.length; i += MAX_PER_REQ) {
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < Math.min(i + MAX_PER_REQ, nums.length); j++) {
                if (j > i) sb.append(',');
                sb.append("anarchy").append(nums[j]);
            }
            try {
                fetchBatch(sb.toString(), ver, out);
            } catch (Exception ignored) {
            }
        }
    }

    private void fetchBatch(String servers, String ver, List<EventCard> out) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) URI.create(
                FT_API + "?event-type=all&server-type=" + servers).toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization-Token", FT_TOKEN);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        if (conn.getResponseCode() != 200) return;
        JsonObject json;
        try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
        }
        JsonArray resp = json.getAsJsonArray("response");
        if (resp == null) return;
        for (JsonElement el : resp) {
            JsonObject sObj = el.getAsJsonObject();
            String sName = sObj.get("server").getAsString();
            int num;
            try {
                num = Integer.parseInt(sName.replace("anarchy", ""));
            } catch (Exception e) {
                continue;
            }
            JsonArray evs = sObj.getAsJsonArray("events");
            if (evs == null) continue;
            for (JsonElement evEl : evs) {
                JsonObject ev = evEl.getAsJsonObject();
                int timeLeft = ev.has("time-seconds-left") ? ev.get("time-seconds-left").getAsInt() : 0;
                String id = ev.has("id") && !ev.get("id").isJsonNull() ? ev.get("id").getAsString() : null;
                String phase = ev.has("phase") && !ev.get("phase").isJsonNull() ? ev.get("phase").getAsString() : null;
                String loot = ev.has("loot") && !ev.get("loot").isJsonNull() ? ev.get("loot").getAsString() : null;
                if ("null".equals(loot)) loot = null;
                String evType = ev.has("event-type") ? ev.get("event-type").getAsString() : "system";
                // скрываем мистический маяк и аирдроп
                if (isHiddenEvent(id)) continue;
                // без id, но с таймером — это ожидание следующего ивента, его тоже показываем
                if (id != null || timeLeft > 0) {
                    out.add(new EventCard(sName, num, ver, id != null ? id : "ожидание",
                            phase, timeLeft, loot, evType));
                }
            }
        }
    }

    /**
     * MixerGrief. В референсе запрос шёл через запуск PowerShell — здесь обычный
     * HTTP-запрос из Java: не тянет за собой внешний процесс и работает не только на Windows.
     */
    private void fetchMixerGrief(List<EventCard> evOut, List<EventCard> mineOut) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) URI.create(MG_API).toURL().openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        if (conn.getResponseCode() != 200) return;
        JsonObject json;
        try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(),
                java.nio.charset.StandardCharsets.UTF_8)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (!parsed.isJsonObject()) return;
            json = parsed.getAsJsonObject();
        }

        for (String key : json.keySet()) {
            if (!json.get(key).isJsonObject()) continue;
            JsonObject serverObj = json.getAsJsonObject(key);
            String serverId = serverObj.has("serverId") ? serverObj.get("serverId").getAsString() : key;
            int serverNum = 0;
            try {
                serverNum = Integer.parseInt(serverId.replaceAll("[^0-9]", ""));
            } catch (Exception ignored) {
            }
            final int sNum = serverNum;

            JsonObject events = serverObj.has("events") && serverObj.get("events").isJsonObject()
                    ? serverObj.getAsJsonObject("events") : null;
            if (events != null) {
                int secsUntilNext = events.has("secondsUntilNextEvent")
                        ? events.get("secondsUntilNextEvent").getAsInt() : -1;
                boolean hasActive = false;
                if (events.has("activeEvents") && events.get("activeEvents").isJsonArray()) {
                    for (JsonElement el : events.getAsJsonArray("activeEvents")) {
                        JsonObject ev = el.getAsJsonObject();
                        String type = ev.has("type") ? ev.get("type").getAsString() : "unknown";
                        if (isHiddenEvent(type)) continue; // скрываем мистический маяк и аирдроп
                        String status = ev.has("virtualStatus") ? ev.get("virtualStatus").getAsString()
                                : (ev.has("status") ? ev.get("status").getAsString() : "");
                        evOut.add(new EventCard(serverId, sNum, "MG", mgEventName(type), status, 0, null, "active"));
                        hasActive = true;
                    }
                }
                if (!hasActive && secsUntilNext > 0) {
                    evOut.add(new EventCard(serverId, sNum, "MG", "Ожидание ивента", null, secsUntilNext, null, "system"));
                }
            }

            JsonObject mines = serverObj.has("mines") && serverObj.get("mines").isJsonObject()
                    ? serverObj.getAsJsonObject("mines") : null;
            if (mines != null) {
                for (String mineKey : mines.keySet()) {
                    if (!mines.get(mineKey).isJsonObject()) continue;
                    JsonObject mine = mines.getAsJsonObject(mineKey);
                    String level = mine.has("currentLevel") ? mine.get("currentLevel").getAsString() : "?";
                    level = level.replaceAll("§.", "");
                    int timeToNext = mine.has("timeToNextMine") ? mine.get("timeToNextMine").getAsInt() : 0;
                    mineOut.add(new EventCard("Анархия-" + sNum, sNum, "MG", "Шахта", level, timeToNext, null, "mine"));
                }
            }
        }
        evOut.sort(Comparator.comparingInt(EventCard::secondsLeft));
        mineOut.sort(Comparator.comparingInt(EventCard::secondsLeft));
    }

    /** Мистический маяк и аирдроп в GUI ивентов не показываем. */
    private static boolean isHiddenEvent(String id) {
        if (id == null) return false;
        String s = id.toLowerCase();
        return s.contains("airdrop") || s.contains("myst_beacon") || s.contains("myst");
    }

    private String mgEventName(String type) {
        return switch (type.toUpperCase()) {
            case "METEORITE" -> "Метеорит";
            case "AIRDROP" -> "Аирдроп";
            case "BOSS" -> "Босс";
            case "TREASURE" -> "Сокровище";
            case "RAID" -> "Рейд";
            case "SUPPLY" -> "Припасы";
            case "MYSTIC" -> "Мистический";
            default -> type;
        };
    }

    // ── Отрисовка ──────────────────────────────────────────────

    private static final float GAP = 8f;
    private static final float CARD_H = 74f;
    private static final float CARD_GAP = 7f;
    private static final float SECTION_HEADER_H = 24f;
    /** На сколько карточка выдвигается снизу при появлении. */
    private static final float SLIDE_DISTANCE = 14f;

    /** @return полная высота контента (для скролла) */
    public float draw(Renderer2D r, float x, float y, float w, float h,
                      float scroll, float alpha, int mouseX, int mouseY) {
        buttonBoxes.clear();
        buttonServers.clear();

        float sectionW = w / 2f;
        float colW = (sectionW - GAP * 3f) / 2f;
        int a = (int) (255 * alpha);

        // заголовки разделов
        drawCentered(r, "FunTime", x + sectionW * 0.5f, y + scroll + SECTION_HEADER_H * 0.5f, 13f,
                fade(ClientColors.ICON.getRGB(), a));
        drawCentered(r, "MixerGrief", x + sectionW * 1.5f, y + scroll + SECTION_HEADER_H * 0.5f, 13f,
                fade(ClientColors.ICON.getRGB(), a));

        // вертикальная перегородка между серверами
        r.rect(x + sectionW - 0.5f, y + scroll, 1f, Math.max(0f, h - scroll),
                ColorUtils.rgba(255, 255, 255, (int) (18 * alpha)));

        float contentY = y + scroll + SECTION_HEADER_H;
        long elapsed = fetchTimestamp == 0L ? 0L : (System.currentTimeMillis() - fetchTimestamp) / 1000L;

        float dt = deltaSeconds();
        ftAnim.sync(ftCards);
        mgAnim.sync(mgCards);
        mineAnim.sync(mgMines);
        ftAnim.update(dt);
        mgAnim.update(dt);
        mineAnim.update(dt);

        // раскладка считается без скролла, поэтому прокрутка не тормозит переезд карточек
        float layoutY = y + SECTION_HEADER_H;

        // FunTime — две колонки, карточки кладём в более короткую
        float[] ftColY = {layoutY, layoutY};
        if (ftCards.isEmpty() && !ftStatus.isEmpty()) {
            drawLine(r, ftStatus, x + GAP, contentY + 10f, 11f, fade(MUTED, a));
        }
        for (ListAppearAnimator.Entry<EventCard> entry : ftAnim.entries()) {
            if (!entry.leaving()) {
                int col = ftColY[0] <= ftColY[1] ? 0 : 1;
                ftAnim.place(entry, x + GAP + col * (colW + GAP), ftColY[col], dt);
                ftColY[col] += CARD_H + CARD_GAP;
            }
            drawEntry(r, entry, colW, y, h, scroll, mouseX, mouseY, alpha, elapsed);
        }

        // MixerGrief — колонка ивентов и колонка шахт
        float mgX = x + sectionW + GAP;
        if (mgCards.isEmpty() && mgMines.isEmpty() && !mgStatus.isEmpty()) {
            drawLine(r, mgStatus, mgX, contentY + 10f, 11f, fade(MUTED, a));
        }
        float mgEvY = layoutY;
        for (ListAppearAnimator.Entry<EventCard> entry : mgAnim.entries()) {
            if (!entry.leaving()) {
                mgAnim.place(entry, mgX, mgEvY, dt);
                mgEvY += CARD_H + CARD_GAP;
            }
            drawEntry(r, entry, colW, y, h, scroll, mouseX, mouseY, alpha, elapsed);
        }
        float mgMineY = layoutY;
        for (ListAppearAnimator.Entry<EventCard> entry : mineAnim.entries()) {
            if (!entry.leaving()) {
                mineAnim.place(entry, mgX + colW + GAP, mgMineY, dt);
                mgMineY += CARD_H + CARD_GAP;
            }
            drawEntry(r, entry, colW, y, h, scroll, mouseX, mouseY, alpha, elapsed);
        }

        float maxY = Math.max(Math.max(ftColY[0], ftColY[1]), Math.max(mgEvY, mgMineY));
        return maxY - y;
    }

    /** Рисует карточку с учётом её прогресса появления/исчезновения. */
    private void drawEntry(Renderer2D r, ListAppearAnimator.Entry<EventCard> entry, float colW,
                           float y, float h, float scroll, int mouseX, int mouseY,
                           float alpha, long elapsed) {
        float progress = entry.progress();
        if (progress <= 0.001f) return;
        float cy = entry.y() + scroll + (1f - progress) * SLIDE_DISTANCE;
        if (cy + CARD_H < y || cy > y + h) return;
        drawCard(r, entry.value(), entry.x(), cy, colW, mouseX, mouseY,
                alpha * progress, elapsed, !entry.leaving());
    }

    /** Перезапускает каскад появления (при открытии GUI или переходе на страницу). */
    public void restartAppearAnimations() {
        ftAnim.restart();
        mgAnim.restart();
        mineAnim.restart();
        lastDrawTime = 0L;
    }

    private float deltaSeconds() {
        long now = System.currentTimeMillis();
        if (lastDrawTime == 0L) {
            lastDrawTime = now;
            return 0f;
        }
        float dt = (now - lastDrawTime) / 1000f;
        lastDrawTime = now;
        if (!Float.isFinite(dt) || dt < 0f) return 0f;
        return Math.min(dt, 0.05f);
    }

    private void drawCard(Renderer2D r, EventCard card, float x, float y, float w,
                          int mouseX, int mouseY, float alpha, long elapsed, boolean interactive) {
        int a = (int) (255 * alpha);
        r.rect(x, y, w, CARD_H, 8f, ColorUtils.rgba(255, 255, 255, (int) (10 * alpha)));
        r.rectOutline(x, y, w, CARD_H, 8f, ColorUtils.rgba(255, 255, 255, (int) (16 * alpha)), 1f);

        float pad = 9f;
        float lineY = y + 15f;

        drawLine(r, card.displayName(), x + pad, lineY, 12.5f, fade(ClientColors.ICON.getRGB(), a));
        lineY += 15f;

        String info = "MG".equals(card.version)
                ? "Анархия-" + card.serverNum
                : "Анархия " + card.serverNum + " • " + card.version;
        drawLine(r, info, x + pad, lineY, 11f, fade(SUB, a));
        lineY += 14f;

        drawLine(r, card.statusStr(elapsed), x + pad, lineY, 11f, fade(MUTED, a));
        lineY += 14f;

        if (card.loot != null) {
            drawLine(r, "Лут: " + card.loot, x + pad, lineY, 11f, fade(MUTED, a));
        } else if (card.phase != null && "mine".equals(card.eventType)) {
            drawLine(r, "Редкость: " + card.phase, x + pad, lineY, 11f, fade(MUTED, a));
        }

        // кнопка перехода — для шахт не нужна
        if (!"mine".equals(card.eventType)) {
            String label = "/an" + card.serverNum;
            float ls = 10f;
            float lw = FontRegistry.SF_MEDIUM.getWidth(label, ls);
            float btnW = lw + 14f, btnH = 16f;
            float btnX = x + w - btnW - pad, btnY = y + CARD_H - btnH - 7f;
            boolean hover = interactive
                    && mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
            if (hover) CursorHelper.setHand();
            int bg = hover
                    ? ClientColors.applyAlpha(ClientColors.ICON.getRGB(), alpha)
                    : ColorUtils.rgba(255, 255, 255, (int) (22 * alpha));
            r.rect(btnX, btnY, btnW, btnH, btnH * 0.5f, bg);
            drawCentered(r, label, btnX + btnW * 0.5f, btnY + btnH * 0.5f, ls,
                    fade(hover ? ColorUtils.rgb(18, 16, 34) : 0xFFFFFF, a));
            if (interactive) {
                buttonBoxes.add(new float[]{btnX, btnY, btnW, btnH});
                buttonServers.add(card.serverNum);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        for (int i = 0; i < buttonBoxes.size(); i++) {
            float[] b = buttonBoxes.get(i);
            if (mouseX >= b[0] && mouseX <= b[0] + b[2] && mouseY >= b[1] && mouseY <= b[1] + b[3]) {
                if (mc.player != null) {
                    mc.player.networkHandler.sendChatCommand("an" + buttonServers.get(i));
                }
                return true;
            }
        }
        return false;
    }

    // ── Мелочи ─────────────────────────────────────────────────

    private static final int SUB = ColorUtils.rgb(170, 174, 186);
    private static final int MUTED = ColorUtils.rgb(130, 134, 148);

    private static void drawLine(Renderer2D r, String text, float x, float centerY, float size, int color) {
        FontObject f = FontRegistry.SF_MEDIUM;
        r.text(f, x, centerY + FontRegistry.centeredBaselineOffset(f, 'H', size), size, text, color);
    }

    private static void drawCentered(Renderer2D r, String text, float centerX, float centerY, float size, int color) {
        FontObject f = FontRegistry.SF_MEDIUM;
        float tw = f.getWidth(text, size);
        r.text(f, centerX - tw * 0.5f, centerY + FontRegistry.centeredBaselineOffset(f, 'H', size), size, text, color);
    }

    private static int fade(int rgb, int alpha) {
        return ColorUtils.rgba((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, Math.max(0, Math.min(255, alpha)));
    }
}
