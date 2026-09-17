package godweer.utils.render.msdf;

import godweer.GodWeer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public enum Fonts {

    MainMenu("mainmenu"),
    MainMenu2("mainmenu2"),
    Font("font"),
    Font1("font1"),
    Font2("font2"),
    Font4("font4"),
    Font5("font5"),
    Font6("font6"),
    Font7("font7"),
    Font8("font8"),
    Font9("font9"),
    Theme("theme"),
    HudIcons("hudicons"),
    AltManager("altmanager"),
    ClickGuiIcons("clickguiicons"),
    ClickGuiIcons2("clickguiicons2"),
    Desc("desc"),
    Icon("icon"),
    Icon1("icon1"),
    Icon2("icon2"),

    // Атласы, перенесённые из клиента Private. Отдельный префикс каталога, потому что
    // имена файлов (icon, mainmenu, hud) совпадают с уже существующими в этом энуме.
    PrivateSfBold("private/sf_bold"),
    PrivateSfLight("private/sf_light"),
    PrivateSfMedium("private/sf_medium"),
    PrivateSfRegular("private/sf_regular"),
    PrivateSfSemibold("private/sf_semibold"),
    PrivateSerati("private/serati"),
    PrivateItalic("private/italic"),
    PrivateClickGui("private/clickgui"),
    PrivateHud("private/hud"),
    PrivateDelta("private/delta"),
    PrivateRockstar("private/rockstar"),
    PrivateMainMenu("private/mainmenu"),
    PrivateIconf("private/iconf"),
    PrivateIcons("private/icons"),
    PrivateIcoes("private/icoes"),
    PrivateIconn("private/iconn"),
    PrivateIconDeshux("private/icon_deshux"),
    PrivateIconDimas("private/icon_dimas"),
    PrivateIconNew("private/icon_new"),
    PrivateIconZ("private/iconZ"),
    PrivateIcon("private/icon"),
    PrivateMusicIcons("private/music_icons"),
    PrivateAccount("private/account"),
    PrivateIcomoon("private/icomoon"),
    PrivateEternity("private/eternity");

    private static final int MIN_SIZE = 8;
    private static final int MAX_SIZE = 99;

    private final String directory;
    private final Int2ObjectMap<FontRenderer> sizeCache = new Int2ObjectOpenHashMap<>(16);

    private MsdfFont atlas;
    private boolean failed;

    Fonts(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return this.directory;
    }

    public boolean isLoaded() {
        return this.atlas != null;
    }

    public MsdfFont atlas() {
        if (this.atlas == null && !this.failed) {
            try {
                this.atlas = MsdfFont.builder()
                        .name(this.directory)
                        .data(this.directory)
                        .atlas(this.directory)
                        .build();
            } catch (Exception e) {
                this.failed = true;
                GodWeer.LOGGER.error("Failed to load MSDF font '{}'", this.directory, e);
            }
        }
        return this.atlas;
    }

    // Ленивое создание рендерера на конкретный размер: вместо 20 * 100 объектов при старте
    // держим только реально запрошенные пары «шрифт + размер».
    public FontRenderer size(int size) {
        int clamped = size < MIN_SIZE ? MIN_SIZE : (size > MAX_SIZE ? MAX_SIZE : size);

        FontRenderer cached = this.sizeCache.get(clamped);
        if (cached != null) {
            return cached;
        }

        MsdfFont msdf = atlas();
        if (msdf == null) {
            return null;
        }

        FontRenderer renderer = new FontRenderer(msdf, clamped);
        this.sizeCache.put(clamped, renderer);
        return renderer;
    }

    public FontRenderer get(int size) {
        return size(size);
    }

    public static void init() {
        long start = System.nanoTime();
        int loaded = 0;

        for (Fonts font : values()) {
            if (font.atlas() != null) {
                loaded++;
            }
        }

        GodWeer.LOGGER.info("MSDF fonts loaded: {}/{} in {} ms",
                loaded, values().length, (System.nanoTime() - start) / 1_000_000L);
    }

    public static void reload() {
        for (Fonts font : values()) {
            font.atlas = null;
            font.failed = false;
            font.sizeCache.clear();
        }
    }

    public static Fonts byDirectory(String directory) {
        if (directory == null) {
            return null;
        }

        String clean = directory.endsWith(".ttf") ? directory.substring(0, directory.length() - 4) : directory;
        for (Fonts font : values()) {
            if (font.directory.equalsIgnoreCase(clean)) {
                return font;
            }
        }
        return null;
    }

    public static FontRenderer get(String directory, int size) {
        Fonts font = byDirectory(directory);
        return font == null ? null : font.size(size);
    }
}
