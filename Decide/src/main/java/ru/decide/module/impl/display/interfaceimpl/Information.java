/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.display.interfaceimpl;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.impl.display.InterFace;
import ru.decide.module.impl.display.interfaceimpl.element;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.ServerUtil;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.RollingText;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class Information
implements element {
    private static float S = 1.0f;
    private static float TEXT = 5.5f * S;
    private static float ICON = 4.5f * S;
    private static float H = 16.0f * S;
    private static float RADIUS = 5.0f * S;
    private static float ICON_X = 6.0f * S;
    private static float ICON_Y = 5.7f * S;
    private static float TEXT_X = 14.0f * S;
    private static float TEXT_Y = 4.4f * S;
    private static float BLOCK = 15.0f * S;
    private static float AFTER_SEP = 7.0f * S;
    private static float AFTER_ICON = 9.0f * S;
    private static final String SEPARATOR = " ; ";
    private final RollingText coordX = new RollingText(3.0f);
    private final RollingText coordY = new RollingText(3.0f);
    private final RollingText coordZ = new RollingText(3.0f);
    private final RollingText bpsText = new RollingText(3.0f);
    private final RollingText tpsText = new RollingText(3.0f);

    @Override
    public void onRender(DragSetting dragSetting, InterFace interFace) {
        S = InterFace.getInstance().sizeHud.getValue().floatValue() * 1.1f;
        TEXT = 5.5f * S;
        ICON = 4.5f * S;
        H = 16.0f * S;
        RADIUS = 5.0f * S;
        ICON_X = 6.0f * S;
        ICON_Y = 5.7f * S;
        TEXT_X = 14.0f * S;
        TEXT_Y = 4.4f * S;
        BLOCK = 15.0f * S;
        AFTER_SEP = 7.0f * S;
        AFTER_ICON = 9.0f * S;
        float x = dragSetting.position.x;
        float y = dragSetting.position.y;
        double dx = Information.mc.field_1724.method_23317() - Information.mc.field_1724.field_6014;
        double dz = Information.mc.field_1724.method_23321() - Information.mc.field_1724.field_5969;
        float bps = (float)(Math.sqrt(dx * dx + dz * dz) * 20.0);
        Font fonts = Fonts.sf_regular;
        this.coordX.set(String.valueOf((int)Information.mc.field_1724.method_23317()));
        this.coordY.set(String.valueOf((int)Information.mc.field_1724.method_23318()));
        this.coordZ.set(String.valueOf((int)Information.mc.field_1724.method_23321()));
        this.bpsText.set(String.format(Locale.US, "%.1f", Float.valueOf(bps)));
        this.tpsText.set(String.format(Locale.US, "%.1f", Float.valueOf(ServerUtil.TPS)));
        float coordsW = this.coordX.width(fonts, TEXT) + this.coordY.width(fonts, TEXT) + this.coordZ.width(fonts, TEXT) + fonts.getWidth("xyz", TEXT) + fonts.getWidth(SEPARATOR, TEXT) * 2.0f;
        float bpsW = this.bpsText.width(fonts, TEXT) + fonts.getWidth("bps", TEXT);
        float tpsW = this.tpsText.width(fonts, TEXT) + fonts.getWidth("tps", TEXT);
        float w = BLOCK + coordsW;
        float w2 = 57.0f * S + coordsW + bpsW + tpsW;
        RenderUtil.Render2D.glow(x, y, w2, H - 0.5f, ColorUtil.getColor(0, 0.1f), RADIUS, 8.0f, 1.0f);
        RenderUtil.Blur.blur(x, y, w2, H, 1.0f, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.35f, 1.0f)));
        Fonts.decide_2.draw("W", x + ICON_X, y + ICON_Y, ICON, ColorUtil.getClientColor(1));
        float cursor = x + TEXT_X;
        cursor += this.drawValue(fonts, this.coordX, "x", cursor, y + TEXT_Y);
        cursor += this.drawSeparator(fonts, cursor, y + TEXT_Y);
        cursor += this.drawValue(fonts, this.coordY, "y", cursor, y + TEXT_Y);
        cursor += this.drawSeparator(fonts, cursor, y + TEXT_Y);
        cursor += this.drawValue(fonts, this.coordZ, "z", cursor, y + TEXT_Y);
        float x2 = x + w + 2.0f * S;
        Fonts.icon.draw("C", x2, y + ICON_Y, ICON, ThemeColor.getSeparatorColor());
        Fonts.decide_2.draw("H", x2 += AFTER_SEP, y + ICON_Y, ICON, ThemeColor.getHudColor());
        this.drawValue(fonts, this.bpsText, "bps", x2 += AFTER_ICON, y + TEXT_Y);
        float x3 = x + w + 20.0f * S + bpsW;
        Fonts.icon.draw("C", x3, y + ICON_Y, ICON, ThemeColor.getSeparatorColor());
        Fonts.decide_2.draw("V", x3 += AFTER_SEP, y + ICON_Y, ICON, ThemeColor.getHudColor());
        this.drawValue(fonts, this.tpsText, "tps", x3 += AFTER_ICON, y + TEXT_Y);
        dragSetting.size.set(w2, H);
    }

    private float drawValue(Font font, RollingText value, String suffix, float x, float y) {
        value.draw(font, x, y, TEXT, ThemeColor.getTextColor());
        float width = value.width(font, TEXT);
        font.draw(suffix, x + width, y, TEXT, ColorUtil.getColor(220));
        return width + font.getWidth(suffix, TEXT);
    }

    private float drawSeparator(Font font, float x, float y) {
        font.draw(SEPARATOR, x, y, TEXT, ThemeColor.getDarkTextColor());
        return font.getWidth(SEPARATOR, TEXT);
    }
}

