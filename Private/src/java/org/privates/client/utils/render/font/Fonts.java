package org.privates.client.utils.render.font;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

@Getter
@RequiredArgsConstructor
public class Fonts {
    public static Font
            SF_BOLD,
            SF_LIGHT,
            SF_MEDIUM,
            SF_REGULAR,
            SF_SEMIBOLD,
            SERATI,
            ITALIC;

    public static Font CLICKGUI, DELTA, ROCKSTAR, iconf, icons, icoes, iconn, icon_deshux, icon_dimas, icon_new, iconZ, HUD, MAINMENU, icon, MUSIC_ICONS, ACCOUNT, ICOMOON, ETERNITY;


    public static void loadFonts() {
        SF_BOLD = new Font("sf_bold");

        SF_LIGHT = new Font("sf_light");
        SF_MEDIUM = new Font("sf_medium");
        SF_REGULAR = new Font("sf_regular");
        SF_SEMIBOLD = new Font("sf_semibold");
        SERATI = new Font("serati");
        ITALIC = new Font("italic");
        icon = new Font("icon");
        icons = new Font("icons");
        ROCKSTAR = new Font("rockstar");

        MAINMENU = new Font("mainmenu");
        DELTA = new Font("delta");


        CLICKGUI = new Font("clickgui");
        HUD = new Font("hud");
        iconf = new Font("iconf");
        icoes = new Font("icoes");

        iconn = new Font("iconn");

        icon_deshux = new Font("icon_deshux");

        icon_dimas = new Font("icon_dimas");

        icon_new = new Font("icon_new");
        iconZ = new Font("iconZ");


        MUSIC_ICONS = new Font("music_icons");
        ACCOUNT = new Font("account");
        ICOMOON = new Font("icomoon");
        ETERNITY = new Font("eternity");
    }

    public static Font valueOf(String name) {
        try {
            Field field = Fonts.class.getDeclaredField(name.toUpperCase());
            return (Font) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            throw new IllegalArgumentException("Font not found: " + name, exception);
        }
    }
}
