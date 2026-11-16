package net.skycore.client.gui;

/**
 * Анимированные фоновые эффекты меню, портированные из Arcanic Visuals.
 *
 * <p>Повторяет {@code net.arcanic.visuals.gui.MenuBackdrop}: 5 режимов фона
 * (нет / Nebula / Silk / Contour / Ember) с плавным растворением между ними,
 * градиентными палитрами, зависящими от акцентных цветов темы, и анимацией
 * цикла времени.
 *
 * <p>Адаптировано на инфраструктуру SkyCore 26.2: используется
 * {@link net.skycore.client.util.BeginFrameHelper} вместо собственного
 * конвейера рендеринга Arcanic.
 *
 * <p>Адаптировано из {@code net.arcanic.visuals.gui.MenuBackdrop}
 * (Arcanic Visuals, GPL-3.0).
 */
public final class SkyMenuBackdrop {

    private SkyMenuBackdrop() {
    }

    /** Режимы фона (как в Arcanic {@code MenuBackdrop.NAMES}). */
    public static final String[] NAMES = {"", "Nebula", "Silk", "Contour", "Ember"};

    /** Режим по умолчанию (индекс в {@link #NAMES}). */
    public static int mode = 4;

    /** Анимации включены. */
    public static boolean animations = true;

    // ---------- состояние перехода между режимами ----------

    private static int previous = 4;
    private static int current = 4;
    private static long changed;
    private static boolean unavailable;

    /**
     * Выбирает режим фона с плавным переходом.
     *
     * @param m индекс режима (1..4; 0 будет приведен к 1)
     */
    public static void select(int m) {
        m = Math.max(1, Math.min(4, m));
        SkyPalette.frontBackground = m;
        if (m == current && !animations) return;
        if (m != current && (!animations || changed == 0
                || System.nanoTime() - changed >= 600_000_000L)) {
            previous = current;
            current = m;
            changed = System.nanoTime();
        }
    }

    /**
     * Рисует фон меню с заданной прозрачностью.
     *
     * @param opacity непрозрачность 0..1
     */
    public static void draw(float opacity) {
        if (opacity <= 0) return;
        int m = Math.max(1, Math.min(4, SkyPalette.frontBackground));
        if (m != current && (!animations || changed == 0
                || System.nanoTime() - changed >= 600_000_000L)) {
            previous = current;
            current = m;
            changed = System.nanoTime();
        }

        var mc = net.minecraft.client.Minecraft.getInstance();
        int sw = mc.getWindow().getWidth();
        int sh = mc.getWindow().getHeight();

        float cycle = animations ? (System.nanoTime() % 300_000_000_000L) / 300_000_000_000f : 0f;
        float fade = animations ? Math.min(1, (System.nanoTime() - changed) / 600_000_000f) : 1f;

        // Фоновые цвета в зависимости от режима (как в Arcanic MenuBackdrop)
        int top, bottom;
        switch (current) {
            case 1 -> { // Nebula
                top = mix(SkyPalette.accent(), 0xFF090A0E, 0.35f);
                bottom = mix(SkyPalette.accent2(), 0xFF0C0D12, 0.5f);
            }
            case 2 -> { // Silk
                top = mix(SkyPalette.bg(), SkyPalette.accent(), 0.1f);
                bottom = mix(SkyPalette.card(), SkyPalette.accent2(), 0.15f);
            }
            case 3 -> { // Contour
                top = mix(SkyPalette.dim(), 0xFF12141C, 0.4f);
                bottom = mix(SkyPalette.muted(), 0xFF1E2028, 0.4f);
            }
            case 4 -> { // Ember
                top = SkyPalette.accent();
                bottom = SkyPalette.accent2();
            }
            default -> {
                top = SkyPalette.bg();
                bottom = SkyPalette.card();
            }
        }

        // Интерполяция при переключении режимов (растворение)
        if (fade < 1f && previous != current) {
            int pTop, pBot;
            switch (previous) {
                case 1 -> { pTop = mix(SkyPalette.accent(), 0xFF090A0E, 0.35f); pBot = mix(SkyPalette.accent2(), 0xFF0C0D12, 0.5f); }
                case 2 -> { pTop = mix(SkyPalette.bg(), SkyPalette.accent(), 0.1f); pBot = mix(SkyPalette.card(), SkyPalette.accent2(), 0.15f); }
                case 3 -> { pTop = mix(SkyPalette.dim(), 0xFF12141C, 0.4f); pBot = mix(SkyPalette.muted(), 0xFF1E2028, 0.4f); }
                case 4 -> { pTop = SkyPalette.accent(); pBot = SkyPalette.accent2(); }
                default -> { pTop = SkyPalette.bg(); pBot = SkyPalette.card(); }
            }
            top = mix(pTop, top, fade);
            bottom = mix(pBot, bottom, fade);
        }

        int a = Math.round(Math.max(0, Math.min(1, opacity)) * 255) << 24;
        int topC = a | (top & 0xFFFFFF);
        int bottomC = a | (bottom & 0xFFFFFF);

        // Вертикальный градиент фона во весь экран
        net.skycore.client.util.BeginFrameHelper.rect(0, 0, sw, sh)
                .color(topC, topC, bottomC, bottomC)
                .draw();

        // Дополнительное свечение для режима Ember (как в Arcanic)
        if (current == 4 && animations) {
            float glow = (float) (0.5 + 0.5 * Math.sin(cycle * 6.28f * 2f));
            net.skycore.client.util.BeginFrameHelper.rect(0, 0, sw, sh)
                    .color(mix(SkyPalette.accent(), 0, glow * 0.15f))
                    .blur(40.0f, 0.3f + glow * 0.2f)
                    .draw();
        }
    }

    private static int mix(int a, int b, float t) {
        int aa = a >>> 24, ar = a >> 16 & 255, ag = a >> 8 & 255, ab = a & 255;
        int ba = b >>> 24, br = b >> 16 & 255, bg = b >> 8 & 255, bb = b & 255;
        return ((int) (aa + (ba - aa) * t) << 24)
                | ((int) (ar + (br - ar) * t) << 16)
                | ((int) (ag + (bg - ag) * t) << 8)
                | ((int) (ab + (bb - ab) * t));
    }
}
