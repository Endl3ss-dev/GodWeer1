package tech.lightclient.ui.hud.watermark;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.client.IMinecraft;
import tech.lightclient.client.utils.client.other.ClientUtility;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.client.utils.server.ServerTPSManager;
import tech.lightclient.ui.hud.CustomBossBarHud;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class WatermarkBase implements IMinecraft {
    public static final String SETTINGS_SCOPE = "Watermark";
    public static final String SETTING_SHOW_NICKNAME = "showNickname";
    public static final String SETTING_SHOW_FPS = "showFps";
    public static final String SETTING_SHOW_PING = "showPing";
    public static final String SETTING_SHOW_TIME = "showTime";
    public static final String SETTING_SHOW_TPS = "showTps";
    public static final String SETTING_SHOW_SERVER_IP = "showServerIp";
    public static final String SETTING_CENTERING = "centering";
    public static final String SETTING_VARIANT = "variant";
    public static final String VARIANT_DEFAULT = "Дефолт";
    public static final String VARIANT_NEW = "Новый";

    public static float width;
    public static float height;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    static float animatedCenteredY = Float.NaN;
    final Dragging dragging;
    final Map<EntryType, TextSwapAnimation> textAnimations = new EnumMap<>(EntryType.class);

    protected WatermarkBase(Dragging dragging) {
        this.dragging = dragging;
    }

    protected static List<WatermarkEntry> buildVisibleEntries(String user) {
        List<WatermarkEntry> parts = new ArrayList<>();

        if (isEnabled(SETTING_SHOW_NICKNAME, true)) {
            parts.add(new WatermarkEntry(EntryType.NICK, " " + user));
        }
        if (isEnabled(SETTING_SHOW_FPS, true)) {
            parts.add(new WatermarkEntry(EntryType.FPS, ClientUtility.getFPS() + " fps"));
        }
        if (isEnabled(SETTING_SHOW_PING, true)) {
            parts.add(new WatermarkEntry(EntryType.PING, ClientUtility.getPing() + " ms"));
        }
        if (isEnabled(SETTING_SHOW_SERVER_IP, true)) {
            parts.add(new WatermarkEntry(EntryType.SERVER_IP, currentServerLabel()));
        }
        if (isEnabled(SETTING_SHOW_TIME, true)) {
            parts.add(new WatermarkEntry(EntryType.TIME, LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        }
        if (isEnabled(SETTING_SHOW_TPS, false)) {
            parts.add(new WatermarkEntry(EntryType.TPS, String.format(Locale.US, "%.2f tps", ServerTPSManager.getInstance().getTPS())));
        }

        if (parts.isEmpty()) {
            parts.add(new WatermarkEntry(EntryType.NICK, user));
        }

        return parts;
    }

    protected static List<WatermarkLine> toLines(List<WatermarkEntry> parts) {
        List<WatermarkLine> lines = new ArrayList<>();
        for (WatermarkEntry part : parts) {
            switch (part.type) {
                case NICK -> lines.add(new WatermarkLine(part.type, "Р”", 20f, 2f, part.value));
                case FPS -> lines.add(new WatermarkLine(part.type, "Р™", 22f, 3f, part.value));
                case PING -> lines.add(new WatermarkLine(part.type, "Р·", 21f, 3f, part.value));
                case TIME -> lines.add(new WatermarkLine(part.type, "СЉ", 21f, 3f, part.value));
                case TPS -> lines.add(new WatermarkLine(part.type, "f", 22f, 3f, part.value));
                case SERVER_IP -> lines.add(new WatermarkLine(part.type, "СЏ", 21f, 3f, part.value));
            }
        }
        return lines;
    }

    protected List<RenderLine> buildRenderLines(List<WatermarkLine> lines) {
        List<RenderLine> renderLines = new ArrayList<>();
        for (WatermarkLine line : lines) {
            AnimatedText text = getAnimatedText(line.type, line.valueText);
            renderLines.add(new RenderLine(line, text));
        }
        return renderLines;
    }

    protected AnimatedText getAnimatedText(EntryType type, String text) {
        TextSwapAnimation animation = textAnimations.computeIfAbsent(type, t -> new TextSwapAnimation(120));
        animation.update(text);
        return animation.snapshot();
    }

    protected static void renderAnimatedText(EventRender.Screen.Hud event, float x, float baselineY, AnimatedText text) {
        event.getRenderer().text(FontRegistry.DELTA_HUD, x, baselineY, 17f, text.current, ClientColors.TEXT.getRGB());
    }

    protected static boolean isEnabled(String key, boolean defaultValue) {
        return DraggingManager.getHudBoolean(SETTINGS_SCOPE, key, defaultValue);
    }

    protected static float getBossBarOffset(float scaleFactor) {
        if (CustomBossBarHud.shouldUseCustomBossBar()) {
            return CustomBossBarHud.getOccupiedHeight() / Math.max(scaleFactor, 0.0001f);
        }

        if (mc == null || mc.inGameHud == null || mc.inGameHud.getBossBarHud() == null) {
            return 0f;
        }

        int bossBarCount = mc.inGameHud.getBossBarHud().bossBars.size();
        if (bossBarCount <= 0) {
            return 0f;
        }

        return (bossBarCount * 19f + 8f) / Math.max(scaleFactor, 0.0001f);
    }

    protected static float animateTowards(float current, float target, float speed) {
        if (!Float.isFinite(current)) {
            return target;
        }
        float factor = Math.max(0.01f, Math.min(1f, speed));
        return current + (target - current) * factor;
    }

    protected static float centeredTextY(float y, float height, float size) {
        if (FontRegistry.DELTA_HUD == null) {
            return y + height * 0.5f;
        }
        float offset = FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', size);
        return y + height * 0.5f + offset + 0.5f;
    }

    protected static String currentServerLabel() {
        if (mc.getCurrentServerEntry() == null || mc.getCurrentServerEntry().address == null || mc.getCurrentServerEntry().address.isEmpty()) {
            return " 127.0.0.1";
        }
        return " " + mc.getCurrentServerEntry().address;
    }

    protected static final class WatermarkLine {
        protected final EntryType type;
        protected final String iconText;
        protected final float iconSize;
        protected final float iconYOffset;
        protected final String valueText;

        private WatermarkLine(EntryType type, String iconText, float iconSize, float iconYOffset, String valueText) {
            this.type = type;
            this.iconText = iconText;
            this.iconSize = iconSize;
            this.iconYOffset = iconYOffset;
            this.valueText = valueText;
        }
    }

    protected static final class RenderLine {
        protected final EntryType type;
        protected final String iconText;
        protected final float iconSize;
        protected final float iconYOffset;
        protected final AnimatedText text;

        private RenderLine(WatermarkLine line, AnimatedText text) {
            this.type = line.type;
            this.iconText = line.iconText;
            this.iconSize = line.iconSize;
            this.iconYOffset = line.iconYOffset;
            this.text = text;
        }
    }

    protected static final class WatermarkEntry {
        protected final EntryType type;
        protected final String value;

        private WatermarkEntry(EntryType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    protected enum EntryType {
        NICK,
        FPS,
        PING,
        TIME,
        TPS,
        SERVER_IP
    }

    protected static final class AnimatedText {
        protected final String current;
        protected final String previous;
        protected final float progress;
        protected final float maxWidth;

        private AnimatedText(String current, String previous, float progress, float maxWidth) {
            this.current = current;
            this.previous = previous;
            this.progress = progress;
            this.maxWidth = maxWidth;
        }
    }

    protected static final class TextSwapAnimation {
        protected final int durationMs;
        protected String current;
        protected String previous;
        protected long startTimeMs;

        private TextSwapAnimation(int durationMs) {
            this.durationMs = Math.max(1, durationMs);
        }

        private void update(String nextText) {
            if (current == null) {
                current = nextText;
                previous = null;
                startTimeMs = 0L;
                return;
            }

            if (!current.equals(nextText)) {
                previous = current;
                current = nextText;
                startTimeMs = System.currentTimeMillis();
            }
        }

        private AnimatedText snapshot() {
            float progress = getProgress();
            float currentWidth = current == null ? 0f : FontRegistry.DELTA_HUD.getWidth(current, 17f);
            float previousWidth = previous == null ? 0f : FontRegistry.DELTA_HUD.getWidth(previous, 17f);
            float maxWidth = Math.max(currentWidth, previousWidth);
            return new AnimatedText(current == null ? "" : current, previous, progress, maxWidth);
        }

        private float getProgress() {
            if (previous == null) {
                return 1f;
            }
            long elapsed = System.currentTimeMillis() - startTimeMs;
            if (elapsed >= durationMs) {
                previous = null;
                return 1f;
            }
            float t = elapsed / (float) durationMs;
            return Math.max(0f, Math.min(1f, t));
        }
    }
}

