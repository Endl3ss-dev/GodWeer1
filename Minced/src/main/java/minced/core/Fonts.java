/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.core.FontRenderer;

public class Fonts {
    public static FontRenderer[] field12486;
    static String field12487;
    static float field12488;
    public static FontRenderer[] I;
    static String field12489;
    static float field12490;
    static FontRenderer[] field12491;
    static String field12492;
    static float field12493;
    public static FontRenderer[] field12494;
    static String field12495;
    public static FontRenderer[] field12496;
    static String field12497;
    static FontRenderer[] field12498;
    static String field12499;
    public static FontRenderer[] field12500;
    static String field12501;
    static FontRenderer[] field12502;
    static String field12503;
    static FontRenderer[] field12504;
    static String field12505;
    static FontRenderer[] field12506;
    static String field12507;
    static FontRenderer[] field12508;
    static String field12509;

    private static void method3555(FontRenderer[] param0, String param1, float param2) {
        for (int v3 = 1; v3 < param0.length; ++v3) {
            param0[v3] = new FontRenderer(param1, v3, param2, true);
        }
    }

    public static void method3556() {
        // The shipped jar had these arrays allocated by the dreamix protection VM, which is not
        // present in a plain source build; allocate them here if nothing else has.
        if (field12486 == null) {
            Fonts.method3557();
        }
        Fonts.method3555(field12486, field12487, field12488);
        Fonts.method3555(I, field12489, field12490);
        Fonts.method3555(field12491, field12492, field12493);
        Fonts.method3555(field12494, field12495, 0.0f);
        Fonts.method3555(field12496, field12497, 0.0f);
        Fonts.method3555(field12498, field12499, 0.0f);
        Fonts.method3555(field12500, field12501, 0.0f);
        Fonts.method3555(field12502, field12503, 0.0f);
        Fonts.method3555(field12504, field12505, 0.0f);
        Fonts.method3555(field12506, field12507, 0.0f);
        Fonts.method3555(field12508, field12509, 0.0f);
    }

    public static void method3557() {
        field12486 = new FontRenderer[33];
        field12491 = new FontRenderer[28];
        field12494 = new FontRenderer[38];
        I = new FontRenderer[38];
        field12496 = new FontRenderer[52];
        field12506 = new FontRenderer[42];
        field12500 = new FontRenderer[42];
        field12502 = new FontRenderer[42];
        field12498 = new FontRenderer[42];
        field12504 = new FontRenderer[42];
        field12508 = new FontRenderer[42];
    }

    static {
        field12487 = "sf_medium.ttf";
        field12488 = -1.0f;
        field12489 = "sf_bold.ttf";
        field12490 = -1.0f;
        field12492 = "sf_regular.ttf";
        field12493 = -1.0f;
        field12495 = "sf_semibold.ttf";
        field12497 = "icons.ttf";
        field12499 = "sfprolight.ttf";
        field12501 = "iconsminced_old.ttf";
        field12503 = "iconsminced.ttf";
        field12505 = "guiIcons.ttf";
        field12507 = "waypoint_icons.ttf";
        field12509 = "new_gui_icons.ttf";
    }
}



