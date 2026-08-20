package fun.volon.client.modules.impl.render.base.implement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import fun.volon.Volon;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.utils.animation.AnimationUtils;
import fun.volon.api.utils.animation.Easings;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.notification.NotificationManager;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DynamicIsland — пилюля в центре экрана сверху.
 * Показывает: часы | пилюля (уведомление / босс-бар / дефолт) | пинг
 *
 * Источник уведомлений — существующий NotificationManager (модули вкл/выкл).
 */
public class DynamicIsland extends InterfaceProcessing {

    // ── Константы ─────────────────────────────────────────────────────────────
    private static final float PILL_H  = 16f;
    private static final float RADIUS  = 7f;
    private static final float FONT_SZ = 13f;

    // Цвета типов уведомлений (ARGB)
    private static final int COLOR_SUCCESS = ColorUtils.rgba(70,  220, 70,  255);
    private static final int COLOR_ERROR   = ColorUtils.rgba(220, 70,  70,  255);
    private static final int COLOR_WARNING = ColorUtils.rgba(255, 170, 0,   255);
    private static final int COLOR_INFO    = ColorUtils.rgba(66,  165, 245, 255);
    private static final int COLOR_DEFAULT = ColorUtils.rgba(164, 66,  255, 255);

    // ── Анимации ──────────────────────────────────────────────────────────────
    private final AnimationUtils widthAnim  = new AnimationUtils(60f,  10f, Easings.CUBIC_OUT);
    private final AnimationUtils scaleAnim  = new AnimationUtils(0f,   8f,  Easings.BACK_OUT);

    // ── Состояние ─────────────────────────────────────────────────────────────
    /** Последнее уведомление из NotificationManager (кешируем чтобы не мигало) */
    private String  lastNotifText    = null;
    private boolean lastNotifEnabled = false;
    private long    lastNotifExpiry  = 0L;   // когда скрыть (ms)

    private static final Pattern TIMER_RE = Pattern.compile("(\\d+)(?!.*\\d)");

    public DynamicIsland(Draggable draggable) {
        super(draggable);
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void onRender(EventRender.Default event) {
        if (mc == null || mc.player == null || mc.world == null) return;

        MatrixStack ms = event.getContext().getMatrices();
        int screenW = mc.getWindow().getScaledWidth();

        Font font    = Fonts.getFont("suisse", (int) FONT_SZ);
        Font font12  = Fonts.getFont("suisse", 12);
        Font iconFnt = Fonts.getFont("iconnew", 14);
        if (font == null || font12 == null) return;

        int theme = getThemeColor();

        // ── Обновляем кеш уведомления ─────────────────────────────────────────
        refreshNotifCache();

        long now = System.currentTimeMillis();
        boolean showNotif = lastNotifText != null && now < lastNotifExpiry;

        // ── Определяем контент пилюли ─────────────────────────────────────────
        ClientBossBar bossBar = showNotif ? null : getPvpBossBar();

        String pillText;
        int    dotColor;

        if (showNotif) {
            pillText = lastNotifText;
            dotColor = lastNotifEnabled ? COLOR_SUCCESS : COLOR_ERROR;
        } else if (bossBar != null) {
            pillText = bossBar.getName().getString();
            dotColor = ColorUtils.rgba(200, 50, 50, 255);
        } else {
            pillText = "Volon Client";
            dotColor = theme;
        }

        // ── Ширина пилюли ─────────────────────────────────────────────────────
        float dotSz   = 6f;
        float padH    = 6f;
        float targetW = padH + dotSz + 4f + font.getStringWidth(pillText) + padH;
        widthAnim.update(targetW);
        scaleAnim.update(1f);

        float scale = scaleAnim.getValue();
        float alpha = Math.min(1f, scale * 1.5f);
        float pillW = widthAnim.getValue();
        float pillH = PILL_H;

        // ── Часы ──────────────────────────────────────────────────────────────
        String clockStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        float  clockW   = font.getStringWidth(clockStr);

        // ── Пинг ──────────────────────────────────────────────────────────────
        int     ping     = getPlayerPing();
        boolean showPing = !mc.isInSingleplayer() && ping < 999;
        float   pingW    = showPing ? (4 * 2.5f + 3 * 1.5f) : 0f;

        // ── Позиции ───────────────────────────────────────────────────────────
        float totalW  = clockW + 4f + pillW + (showPing ? 4f + pingW : 0f);
        float originX = screenW / 2f;
        float originY = 5f + pillH / 2f;

        ms.push();
        ms.translate(originX, originY, 0f);
        ms.scale(scale, scale, 1f);
        ms.translate(-originX, -originY, 0f);

        float clockX = originX - totalW / 2f;
        float pillX  = clockX + clockW + 4f;
        float pillY  = 5f;
        float clockY = pillY + (pillH - font.getHeight()) / 2f;

        // Часы
        font.drawString(ms, clockStr, clockX, clockY, ColorUtils.setAlphaColor(0xFFFFFFFF, (int)(255 * alpha)));

        // Фон пилюли — blur если доступен, иначе тёмный прямоугольник
        int bgColor = ColorUtils.rgba(10, 10, 10, (int)(230 * alpha));
        if (RenderUtils.isLiquidGlassHudActive()) {
            RenderUtils.drawHudLiquidGlass(ms, pillX, pillY, pillW, pillH, RADIUS, theme, false, getHudRectStyle());
        } else {
            RenderUtils.drawBlur(ms, pillX, pillY, pillW, pillH, RADIUS, bgColor);
            RenderUtils.drawRoundedRect(ms, pillX, pillY, pillW, pillH, RADIUS, bgColor);
        }

        // Контент пилюли
        if (showNotif) {
            renderNotif(ms, font, pillX, pillY, pillW, pillH, pillText, dotColor, alpha);
        } else if (bossBar != null) {
            renderBossBar(ms, font, font12, pillX, pillY, pillW, pillH, bossBar, alpha, theme);
        } else {
            renderDefault(ms, font, pillX, pillY, pillW, pillH, pillText, dotColor, alpha);
        }

        // Пинг
        if (showPing) {
            renderPing(ms, pillX + pillW + 4f, pillY, pillH, ping, alpha);
        }

        ms.pop();

        // Обновляем draggable (для hover-детекции)
        draggable.setWidth(totalW);
        draggable.setHeight(pillH);
        // Позиция draggable — левый верхний угол всего блока
        draggable.setX(clockX);
        draggable.setY(pillY);

        super.onRender(event);
    }

    // ── Рендер состояний ──────────────────────────────────────────────────────

    private void renderDefault(MatrixStack ms, Font font,
                               float x, float y, float w, float h,
                               String text, int dotColor, float alpha) {
        float dotSz = 6f;
        float dotX  = x + 6f;
        float dotY  = y + (h - dotSz) / 2f;
        RenderUtils.drawRoundedRect(ms, dotX, dotY, dotSz, dotSz, dotSz / 2f,
                ColorUtils.setAlphaColor(dotColor, (int)(255 * alpha)));
        font.drawString(ms, text,
                dotX + dotSz + 4f,
                y + (h - font.getHeight()) / 2f,
                ColorUtils.setAlphaColor(0xFFFFFFFF, (int)(255 * alpha)));
    }

    private void renderNotif(MatrixStack ms, Font font,
                             float x, float y, float w, float h,
                             String text, int boxColor, float alpha) {
        float boxSz = 7f;
        float boxX  = x + 6f;
        float boxY  = y + (h - boxSz) / 2f;
        RenderUtils.drawRoundedRect(ms, boxX, boxY, boxSz, boxSz, 3.5f,
                ColorUtils.setAlphaColor(boxColor, (int)(255 * alpha)));
        font.drawString(ms, text,
                boxX + boxSz + 5f,
                y + (h - font.getHeight()) / 2f,
                ColorUtils.setAlphaColor(0xFFFFFFFF, (int)(255 * alpha)));
    }

    private void renderBossBar(MatrixStack ms, Font font, Font font12,
                               float x, float y, float w, float h,
                               ClientBossBar bar, float alpha, int theme) {
        String raw   = bar.getName().getString();
        Matcher m    = TIMER_RE.matcher(raw);
        String  time = m.find() ? m.group(1) + "s" : "??s";
        String  label = raw.replaceAll(",?\\s*до конца.*$", "")
                           .replaceAll("Телепортация.*", "Телепортация").trim();

        float timeW  = font.getStringWidth(time);
        float labelW = font.getStringWidth(label);
        float boxW   = timeW + 8f;
        float startX = x + (w - boxW - 4f - labelW) / 2f;
        float textY  = y + (h - font.getHeight()) / 2f;

        // Красный бокс с таймером
        RenderUtils.drawRoundedRect(ms, startX - 2f, y + 2f, boxW, h - 4f, 3f,
                ColorUtils.rgba(200, 50, 50, (int)(200 * alpha)));
        font.drawString(ms, time,
                startX + (boxW - timeW) / 2f - 2f, textY,
                ColorUtils.setAlphaColor(0xFFFFFFFF, (int)(255 * alpha)));
        font.drawString(ms, label,
                startX + boxW + 4f, textY,
                ColorUtils.setAlphaColor(0xFFFFFFFF, (int)(255 * alpha)));
    }

    private void renderPing(MatrixStack ms, float x, float y, float h, int ping, float alpha) {
        int     lit     = ping <= 60 ? 4 : ping <= 100 ? 3 : ping <= 150 ? 2 : ping <= 250 ? 1 : 0;
        float[] heights = {3f, 5f, 7f, 9f};
        float   barW    = 2.5f;
        float   gap     = 1.5f;
        float   baseY   = y + h - 2f;
        for (int i = 0; i < 4; i++) {
            float bh = heights[i];
            int   c  = i < lit
                    ? ColorUtils.rgba(255, 255, 255, (int)(210 * alpha))
                    : ColorUtils.rgba(255, 255, 255, (int)(45  * alpha));
            RenderUtils.drawRoundedRect(ms,
                    x + i * (barW + gap), baseY - bh,
                    barW, bh, 0.8f, c);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Берём последнее активное уведомление из NotificationManager.
     * Кешируем его на DISPLAY_MS после исчезновения из очереди.
     */
    private void refreshNotifCache() {
        List<NotificationManager.Entry> active = NotificationManager.getActive();
        if (!active.isEmpty()) {
            NotificationManager.Entry last = active.get(active.size() - 1);
            String text = last.isCustom() ? last.customText : last.moduleName;
            if (!last.isCustom()) {
                text = last.moduleName + (last.enabled ? " вкл" : " выкл");
            }
            lastNotifText    = text;
            lastNotifEnabled = last.enabled;
            // Держим ещё 500ms после того как запись исчезнет из очереди
            lastNotifExpiry  = last.startTime + NotificationManager.DURATION_MS + 500L;
        }
        // Если время вышло — сбрасываем
        if (lastNotifText != null && System.currentTimeMillis() >= lastNotifExpiry) {
            lastNotifText = null;
        }
    }

    private ClientBossBar getPvpBossBar() {
        if (mc.inGameHud == null) return null;
        BossBarHud bbh = mc.inGameHud.getBossBarHud();
        if (bbh == null) return null;
        try {
            // bossBars — package-private поле, получаем через рефлексию
            java.lang.reflect.Field f = BossBarHud.class.getDeclaredField("bossBars");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<java.util.UUID, ClientBossBar> bars =
                    (java.util.Map<java.util.UUID, ClientBossBar>) f.get(bbh);
            if (bars == null) return null;
            for (ClientBossBar bar : bars.values()) {
                String n = bar.getName().getString().toLowerCase();
                if (n.contains("pvp") || n.contains("пвп") || n.contains("кт") || n.contains("kt")
                        || n.contains("телепортация") || n.contains("до конца")) {
                    return bar;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private int getPlayerPing() {
        if (mc.player == null || mc.getNetworkHandler() == null) return 0;
        PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return e != null ? e.getLatency() : 0;
    }

    private int getThemeColor() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null) return ColorUtils.getThemeColor();
        var theme = Volon.INSTANCE.themeStorage.getThemes().getTheme();
        if (theme == null) return ColorUtils.getThemeColor();
        if ("Rainbow".equals(theme.getName())) return ColorUtils.getThemeColor();
        return (theme.color != null && theme.color.length > 0) ? theme.color[0] : ColorUtils.getThemeColor();
    }

    /** Уведомление о прочности брони — вызывается извне */
    public static void checkArmorDurability() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        EquipmentSlot[] slots = {
                EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
        };
        String[] names = {"Ботинки", "Поножи", "Нагрудник", "Шлем"};
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = client.player.getEquippedStack(slots[i]);
            if (stack.isEmpty() || !stack.isDamageable()) continue;
            float pct = (float)(stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage();
            if (pct <= 0.1f && pct > 0f) {
                NotificationManager.pushCustom(names[i] + " — низкая прочность!", "!");
                return;
            }
        }
    }
}
