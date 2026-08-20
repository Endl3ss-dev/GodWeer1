/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.enums.MAIN;

class ThemePalette
{
    static int[] field9501;

    public static void method2780() {
        field9501 = new int[MAIN.values().length];
        ThemePalette.field9501[MAIN.MODULE_VISUAL.ordinal()] = 1;
        ThemePalette.field9501[MAIN.INDICATOR.ordinal()] = 2;
        ThemePalette.field9501[MAIN.BUTTON.ordinal()] = 3;
        ThemePalette.field9501[MAIN.LOGO.ordinal()] = 4;
        ThemePalette.field9501[MAIN.SLIDER.ordinal()] = 5;
        ThemePalette.field9501[MAIN.SLIDER_CIRCLE.ordinal()] = 6;
        ThemePalette.field9501[MAIN.INDICATOR_INACTIVE.ordinal()] = 7;
        ThemePalette.field9501[MAIN.BUTTON_INACTIVE.ordinal()] = 8;
        ThemePalette.field9501[MAIN.FIELD_INACTIVE.ordinal()] = 9;
        ThemePalette.field9501[MAIN.HEADER.ordinal()] = 10;
        ThemePalette.field9501[MAIN.MAIN.ordinal()] = 11;
        ThemePalette.field9501[MAIN.WINDOW_BG.ordinal()] = 12;
        ThemePalette.field9501[MAIN.TOOLTIP_BG.ordinal()] = 13;
        ThemePalette.field9501[MAIN.HEADER_TEXT.ordinal()] = 14;
        ThemePalette.field9501[MAIN.TEXT.ordinal()] = 15;
        ThemePalette.field9501[MAIN.WINDOW_TEXT.ordinal()] = 16;
        ThemePalette.field9501[MAIN.LOGO_TEXT.ordinal()] = 17;
        ThemePalette.field9501[MAIN.TOOLTIP.ordinal()] = 18;
        ThemePalette.field9501[MAIN.SLIDER_WINDOW.ordinal()] = 19;
        ThemePalette.field9501[MAIN.TEXT_INACTIVE.ordinal()] = 20;
        ThemePalette.field9501[MAIN.SEPARATOR.ordinal()] = 21;
        ThemePalette.field9501[MAIN.SCROLL_BAR.ordinal()] = 22;
        ThemePalette.field9501[MAIN.FIELD.ordinal()] = 23;
        ThemePalette.field9501[MAIN.OUTLINE.ordinal()] = 24;
    }

    static {
        ThemePalette.method2780();
    }
}



