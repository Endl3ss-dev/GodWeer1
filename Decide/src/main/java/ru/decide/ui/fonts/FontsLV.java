/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.fonts;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.fonts.LvFont;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public final class FontsLV {
    public static final LvFont MONTSERRAT_MEDIUM = new LvFont(Fonts.sf_medium);
    public static final LvFont SMALL_PIXEL = new LvFont(Fonts.sf_bold);
    public static final LvFont DECIDE = new LvFont(Fonts.gui);

    private FontsLV() {
    }
}

