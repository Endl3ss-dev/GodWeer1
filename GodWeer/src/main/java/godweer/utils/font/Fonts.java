package godweer.utils.font;

import static godweer.utils.render.msdf.Fonts.PrivateAccount;
import static godweer.utils.render.msdf.Fonts.PrivateClickGui;
import static godweer.utils.render.msdf.Fonts.PrivateDelta;
import static godweer.utils.render.msdf.Fonts.PrivateEternity;
import static godweer.utils.render.msdf.Fonts.PrivateHud;
import static godweer.utils.render.msdf.Fonts.PrivateIcoes;
import static godweer.utils.render.msdf.Fonts.PrivateIcomoon;
import static godweer.utils.render.msdf.Fonts.PrivateIcon;
import static godweer.utils.render.msdf.Fonts.PrivateIconDeshux;
import static godweer.utils.render.msdf.Fonts.PrivateIconDimas;
import static godweer.utils.render.msdf.Fonts.PrivateIconNew;
import static godweer.utils.render.msdf.Fonts.PrivateIconZ;
import static godweer.utils.render.msdf.Fonts.PrivateIconf;
import static godweer.utils.render.msdf.Fonts.PrivateIconn;
import static godweer.utils.render.msdf.Fonts.PrivateIcons;
import static godweer.utils.render.msdf.Fonts.PrivateItalic;
import static godweer.utils.render.msdf.Fonts.PrivateMainMenu;
import static godweer.utils.render.msdf.Fonts.PrivateMusicIcons;
import static godweer.utils.render.msdf.Fonts.PrivateRockstar;
import static godweer.utils.render.msdf.Fonts.PrivateSerati;
import static godweer.utils.render.msdf.Fonts.PrivateSfBold;
import static godweer.utils.render.msdf.Fonts.PrivateSfLight;
import static godweer.utils.render.msdf.Fonts.PrivateSfMedium;
import static godweer.utils.render.msdf.Fonts.PrivateSfRegular;
import static godweer.utils.render.msdf.Fonts.PrivateSfSemibold;

/**
 * Порт {@code org.privates.client.utils.render.font.Fonts} из клиента Private.
 * <p>
 * Имена полей оставлены ровно такими, как в источнике ({@code SF_BOLD}, {@code iconf}, {@code CLICKGUI}),
 * чтобы перенос экранов (newgui, figmagui) не требовал правок на каждой строке отрисовки.
 * <p>
 * В отличие от Private здесь это не {@code MsdfFont}, а тонкая обёртка {@link Font} над
 * существующим рендером GodWeer: атлас грузится лениво при первом вызове, рендерер —
 * на каждый запрошенный размер (кэш лежит в {@code godweer.utils.render.msdf.Fonts}).
 */
public final class Fonts {

    public static final Font SF_BOLD = new Font(PrivateSfBold);
    public static final Font SF_LIGHT = new Font(PrivateSfLight);
    public static final Font SF_MEDIUM = new Font(PrivateSfMedium);
    public static final Font SF_REGULAR = new Font(PrivateSfRegular);
    public static final Font SF_SEMIBOLD = new Font(PrivateSfSemibold);
    public static final Font SERATI = new Font(PrivateSerati);
    public static final Font ITALIC = new Font(PrivateItalic);

    public static final Font CLICKGUI = new Font(PrivateClickGui);
    public static final Font HUD = new Font(PrivateHud);
    public static final Font DELTA = new Font(PrivateDelta);
    public static final Font ROCKSTAR = new Font(PrivateRockstar);
    public static final Font MAINMENU = new Font(PrivateMainMenu);

    public static final Font iconf = new Font(PrivateIconf);
    public static final Font icons = new Font(PrivateIcons);
    public static final Font icoes = new Font(PrivateIcoes);
    public static final Font iconn = new Font(PrivateIconn);
    public static final Font icon_deshux = new Font(PrivateIconDeshux);
    public static final Font icon_dimas = new Font(PrivateIconDimas);
    public static final Font icon_new = new Font(PrivateIconNew);
    public static final Font iconZ = new Font(PrivateIconZ);
    public static final Font icon = new Font(PrivateIcon);

    public static final Font MUSIC_ICONS = new Font(PrivateMusicIcons);
    public static final Font ACCOUNT = new Font(PrivateAccount);
    public static final Font ICOMOON = new Font(PrivateIcomoon);
    public static final Font ETERNITY = new Font(PrivateEternity);

    private static final Font[] VALUES = {
            SF_BOLD, SF_LIGHT, SF_MEDIUM, SF_REGULAR, SF_SEMIBOLD, SERATI, ITALIC,
            CLICKGUI, HUD, DELTA, ROCKSTAR, MAINMENU,
            iconf, icons, icoes, iconn, icon_deshux, icon_dimas, icon_new, iconZ, icon,
            MUSIC_ICONS, ACCOUNT, ICOMOON, ETERNITY
    };

    private static final String[] NAMES = {
            "SF_BOLD", "SF_LIGHT", "SF_MEDIUM", "SF_REGULAR", "SF_SEMIBOLD", "SERATI", "ITALIC",
            "CLICKGUI", "HUD", "DELTA", "ROCKSTAR", "MAINMENU",
            "iconf", "icons", "icoes", "iconn", "icon_deshux", "icon_dimas", "icon_new", "iconZ", "icon",
            "MUSIC_ICONS", "ACCOUNT", "ICOMOON", "ETERNITY"
    };

    private static boolean loaded;

    private Fonts() {
    }

    /**
     * Прогрев ссылок: класс {@link Font} дешёвый (атлас остаётся ленивым),
     * поэтому вызов нужен только чтобы гарантировать инициализацию до первого кадра GUI.
     */
    public static void loadFonts() {
        if (loaded) {
            return;
        }

        for (int i = 0, length = VALUES.length; i < length; i++) {
            Font font = VALUES[i];
            if (font == null) {
                throw new IllegalStateException("Font entry #" + i + " is not initialized");
            }
        }

        loaded = true;
    }

    public static Font[] values() {
        return VALUES.clone();
    }

    public static Font byName(String name) {
        if (name == null) {
            return null;
        }

        for (int i = 0, length = NAMES.length; i < length; i++) {
            if (NAMES[i].equalsIgnoreCase(name)) {
                return VALUES[i];
            }
        }

        return null;
    }

    public static int count() {
        return VALUES.length;
    }
}
