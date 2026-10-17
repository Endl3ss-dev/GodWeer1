package swerve.ui.menu;

/**
 * Палитры меню (порт Arcanic Visuals, GPL-3.0 — см. Px/MenuText).
 *
 * <p>Каждый токен цвета плавно перетекает из старой палитры в новую: при смене темы
 * запоминается палитра «на экране сейчас» и последние FADE_MS миллисекунд цвета смешиваются.
 */
public final class MenuTheme {

    private MenuTheme() {
    }

    /** fx: 0 none, 1 aurora, 2 matrix rain, 3 synthwave grid, 4 prism smoke, 5 vortex, 6 neon beams. */
    public record Palette(
            String name,
            int bg,
            int side,
            int card,
            int control,
            int border,
            int text,
            int muted,
            int dim,
            int a,
            int b,
            boolean light,
            int fx) {

        public boolean animated() {
            return fx != 0;
        }
    }

    public static final Palette[] PALETTES = {
            // ночь: холодный почти-чёрный, волосяные белые границы и серые акценты с фиолетовым
            new Palette(
                    "Обсидиан",
                    0xF2090A0E,
                    0xFF0C0D12,
                    0xFF12141C,
                    0xFF1E2028,
                    0x18FFFFFF,
                    0xFFF4F6FA,
                    0xFFB0B8C4,
                    0xFF757D8A,
                    0xFF8B5CF6,
                    0xFFB79CFF,
                    false,
                    0),
            new Palette(
                    "Аметист",
                    0xF20E0A16,
                    0xFF140F1F,
                    0xFF181224,
                    0xFF241B33,
                    0x1AE9D5FF,
                    0xFFF6F1FF,
                    0xFFA898C2,
                    0xFF6B5C85,
                    0xFFA855F7,
                    0xFFE9D5FF,
                    false,
                    0),
            new Palette(
                    "Полночь",
                    0xF2080C16,
                    0xFF0D1220,
                    0xFF101727,
                    0xFF19233A,
                    0x18BFDBFE,
                    0xFFF0F5FF,
                    0xFF93A3C2,
                    0xFF56637E,
                    0xFF3B82F6,
                    0xFF93C5FD,
                    false,
                    0),
            new Palette(
                    "Закат",
                    0xF2120C0A,
                    0xFF1A110E,
                    0xFF1E1410,
                    0xFF2B1D17,
                    0x18FED7AA,
                    0xFFFFF5EE,
                    0xFFC2A190,
                    0xFF7E6355,
                    0xFFF97316,
                    0xFFFDBA74,
                    false,
                    0),
            new Palette(
                    "Изумруд",
                    0xF2081210,
                    0xFF0D1916,
                    0xFF0F1D1A,
                    0xFF172B26,
                    0x18A7F3D0,
                    0xFFEFFFF9,
                    0xFF8FB8AA,
                    0xFF55786C,
                    0xFF10B981,
                    0xFF6EE7B7,
                    false,
                    0),
            new Palette(
                    "Светлая",
                    0xF5F6F6F9,
                    0xFFFFFFFF,
                    0xFFFFFFFF,
                    0xFFEEEEF3,
                    0x16000000,
                    0xFF15151A,
                    0xFF696978,
                    0xFFA2A2AE,
                    0xFF7C3AED,
                    0xFFA78BFA,
                    true,
                    0),
            new Palette(
                    "Аврора",
                    0xF20B0916,
                    0xC4110E1E,
                    0xBC151127,
                    0xD0211B38,
                    0x22E0E7FF,
                    0xFFF5F3FF,
                    0xFFA5A0C8,
                    0xFF67628A,
                    0xFF8B5CF6,
                    0xFF22D3EE,
                    false,
                    1),
            new Palette(
                    "Матрица",
                    0xF4040906,
                    0xC4071009,
                    0xBC0A140D,
                    0xD0132219,
                    0x2638FF7A,
                    0xFFE9FFF0,
                    0xFF8FBF9E,
                    0xFF4F7A5E,
                    0xFF22C55E,
                    0xFF86EFAC,
                    false,
                    2),
            new Palette(
                    "Синтвейв",
                    0xF40B0514,
                    0xC4140A22,
                    0xBC170C28,
                    0xD0261440,
                    0x2AFF5FD2,
                    0xFFFDF0FF,
                    0xFFC4A0D8,
                    0xFF7D5E92,
                    0xFFEC4899,
                    0xFFA855F7,
                    false,
                    3),
            new Palette(
                    "Призма",
                    0xF40A0716,
                    0xC4120E26,
                    0xBC171230,
                    0xD022193F,
                    0x26D8B4FE,
                    0xFFF5F0FF,
                    0xFFAFA2D2,
                    0xFF6E6392,
                    0xFFA78BFA,
                    0xFFF0ABFC,
                    false,
                    4),
            new Palette(
                    "Вихрь",
                    0xF4050A14,
                    0xC40A1222,
                    0xBC0D1830,
                    0xD0142440,
                    0x2693C5FD,
                    0xFFF0F6FF,
                    0xFF9FB4D2,
                    0xFF5D7092,
                    0xFF38BDF8,
                    0xFF818CF8,
                    false,
                    5),
            new Palette(
                    "Неон",
                    0xF406060E,
                    0xC40C0C1A,
                    0xBC0F0F22,
                    0xD01A1A33,
                    0x2822D3EE,
                    0xFFF0FDFF,
                    0xFF9EC3D0,
                    0xFF5E7A88,
                    0xFF22D3EE,
                    0xFFF472B6,
                    false,
                    6),
            // добавленные палитры: тот же стиль (тёмная база, волосяные границы, один яркий акцент)
            new Palette(
                    "Графит",
                    0xF20A0B0D,
                    0xFF101216,
                    0xFF15181D,
                    0xFF1E2228,
                    0x1A22D3EE,
                    0xFFF1F3F7,
                    0xFFA9B0BC,
                    0xFF6E7682,
                    0xFF22D3EE,
                    0xFFA5F3FC,
                    false,
                    0),
            new Palette(
                    "Кобальт",
                    0xF2070A14,
                    0xFF0C1120,
                    0xFF101629,
                    0xFF18203A,
                    0x1A60A5FA,
                    0xFFEFF4FF,
                    0xFF93A0C0,
                    0xFF586480,
                    0xFF3B82F6,
                    0xFF93C5FD,
                    false,
                    0),
            new Palette(
                    "Титан",
                    0xF2080B0A,
                    0xFF0F1411,
                    0xFF131A16,
                    0xFF1B2620,
                    0x1AA3E635,
                    0xFFF1FFF5,
                    0xFFA6C0AF,
                    0xFF5E7466,
                    0xFF84CC16,
                    0xFFD9F99D,
                    false,
                    0),
            new Palette(
                    "Рубин",
                    0xF20C0709,
                    0xFF140B0F,
                    0xFF1A0F14,
                    0xFF26151C,
                    0x1FF43F5E,
                    0xFFFFF2F5,
                    0xFFC3A0AC,
                    0xFF7A5B66,
                    0xFFF43F5E,
                    0xFFFDA4AF,
                    false,
                    0),
            new Palette(
                    "Океан",
                    0xF2050B0E,
                    0xFF0A1418,
                    0xFF0E1B20,
                    0xFF16272E,
                    0x1A22D3EE,
                    0xFFF0FDFF,
                    0xFF9EC2D4,
                    0xFF5B7E8C,
                    0xFF06B6D4,
                    0xFF67E8F9,
                    false,
                    0),
            new Palette(
                    "Золото",
                    0xF20C0904,
                    0xFF15110A,
                    0xFF1B150C,
                    0xFF261E12,
                    0x1AF59E0B,
                    0xFFFFF8EC,
                    0xFFC7B393,
                    0xFF7C674B,
                    0xFFF59E0B,
                    0xFFFCD34D,
                    false,
                    0),
            new Palette(
                    "Индиго",
                    0xF2090812,
                    0xFF100D1B,
                    0xFF151129,
                    0xFF1E1839,
                    0x1A818CF8,
                    0xFFF4F3FF,
                    0xFFA9A3C8,
                    0xFF615B84,
                    0xFF6366F1,
                    0xFFA5B4FC,
                    false,
                    0),
            new Palette(
                    "Хром",
                    0xF00A0B0C,
                    0xFF111316,
                    0xFF17191C,
                    0xFF212428,
                    0x1AFFFFFF,
                    0xFFF7F8FA,
                    0xFFB4BAC4,
                    0xFF767E88,
                    0xFFE5E7EB,
                    0xFFFFFFFF,
                    false,
                    0),
    };

    public static int palette = 0;
    public static final int NIGHT = 0;
    public static final int LIGHT = 5;


    // ---- плавная смена: каждый токен смешивается от старой палитры к новой
    private static Palette from;
    private static long changedAt;
    public static final long FADE_MS = 520;

    public static void setPalette(int i) {
        i = Math.floorMod(i, PALETTES.length);
        if (i == palette) {
            return;
        }
        from = p();
        changedAt = System.currentTimeMillis();
        palette = i;
    }

    public static float transition() {
        if (from == null) {
            return 1;
        }
        float t = (System.currentTimeMillis() - changedAt) / (float) FADE_MS;
        if (t >= 1) {
            from = null;
            return 1;
        }
        return t;
    }

    public static Palette base() {
        return PALETTES[Math.floorMod(palette, PALETTES.length)];
    }

    public static Palette p() {
        Palette to = base();
        float t = transition();
        if (t >= 1) {
            return to;
        }
        float e = t * t * (3 - 2 * t);
        Palette f = from;
        return new Palette(
                to.name(),
                mix(f.bg(), to.bg(), e),
                mix(f.side(), to.side(), e),
                mix(f.card(), to.card(), e),
                mix(f.control(), to.control(), e),
                mix(f.border(), to.border(), e),
                mix(f.text(), to.text(), e),
                mix(f.muted(), to.muted(), e),
                mix(f.dim(), to.dim(), e),
                mix(f.a(), to.a(), e),
                mix(f.b(), to.b(), e),
                e > .5f ? to.light() : f.light(),
                to.fx());
    }

    private static int mix(int a, int b, float t) {
        int aa = a >>> 24;
        int ar = a >> 16 & 255;
        int ag = a >> 8 & 255;
        int ab = a & 255;
        int ba = b >>> 24;
        int br = b >> 16 & 255;
        int bg = b >> 8 & 255;
        int bb = b & 255;
        return (int) (aa + (ba - aa) * t) << 24
                | (int) (ar + (br - ar) * t) << 16
                | (int) (ag + (bg - ag) * t) << 8
                | (int) (ab + (bb - ab) * t);
    }

    public static int accent() {
        return p().a();
    }

    public static int accent2() {
        return p().b();
    }

    public static int text() {
        return p().text();
    }

    public static int muted() {
        return p().muted();
    }

    public static int dim() {
        return p().dim();
    }

    public static int bg() {
        return base().bg();
    }

    public static int card() {
        return p().card();
    }

    public static int control() {
        return p().control();
    }

    public static int border() {
        return p().border();
    }
}
