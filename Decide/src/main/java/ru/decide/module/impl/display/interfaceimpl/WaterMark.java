/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  net.minecraft.class_640
 */
package ru.decide.module.impl.display.interfaceimpl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_640;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.impl.display.InterFace;
import ru.decide.module.impl.display.interfaceimpl.element;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.RollingText;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class WaterMark
implements element {
    private static float S = 1.0f;
    private static float TEXT = 5.5f * S;
    private static float ICON = 5.0f * S;
    private static float LOGO = 6.0f * S;
    private static float H = 16.0f * S;
    private static float RADIUS = 5.0f * S;
    private static float ICON_X = 6.0f * S;
    private static float ICON_Y = 5.5f * S;
    private static float TEXT_X = 14.0f * S;
    private static float TEXT_Y = 4.4f * S;
    private static float BLOCK = 15.0f * S;
    private static float AFTER_SEP = 7.0f * S;
    private static float AFTER_ICON = 9.0f * S;
    private final RollingText fpsText = new RollingText(3.0f);
    private final RollingText pingText = new RollingText(3.0f);
    private float bossBarOffset = 0.0f;
    private static final float BOSS_BAR_HEIGHT = 19.0f;
    private static final float LERP_SPEED = 0.15f;

    @Override
    public void onRender(DragSetting dragSetting, InterFace interFace) {
        class_640 entry;
        float x = dragSetting.position.x;
        float y = dragSetting.position.y;
        boolean hasBossBar = WaterMark.mc.field_1705 != null && WaterMark.mc.field_1705.method_1740() != null && !WaterMark.mc.field_1705.method_1740().field_2060.isEmpty();
        float target = hasBossBar ? 19.0f : 0.0f;
        this.bossBarOffset += (target - this.bossBarOffset) * 0.15f;
        if (Math.abs(this.bossBarOffset - target) < 0.1f) {
            this.bossBarOffset = target;
        }
        y += this.bossBarOffset;
        S = InterFace.getInstance().sizeHud.getValue().floatValue() * 1.1f;
        TEXT = 5.5f * S;
        ICON = 6.0f * S;
        LOGO = 6.0f * S;
        H = 16.0f * S;
        RADIUS = 5.0f * S;
        ICON_X = 6.0f * S;
        ICON_Y = 5.5f * S;
        TEXT_X = 14.0f * S;
        TEXT_Y = 4.4f * S;
        BLOCK = 15.0f * S;
        AFTER_SEP = 7.0f * S;
        AFTER_ICON = 9.0f * S;
        Font fonts = Fonts.sf_medium;
        float logoSize = 14.0f * S;
        RenderUtil.Render2D.glow(x, y, H, H - 0.5f, ColorUtil.getColor(0, 0.1f), RADIUS, 8.0f, 1.0f);
        RenderUtil.Blur.blur(x, y, H, H, 1.0f, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.35f, 1.0f)));
        RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/frame/logo.png"), x + (H - logoSize) / 2.0f, y + (H - logoSize) / 2.0f, logoSize, logoSize, ColorUtil.getClientColor(1));
        x += 18.5f * S;
        String user = InterFace.getInstance().userName.getValue();
        int pings = 0;
        if (mc.method_1562() != null && (entry = mc.method_1562().method_2871(WaterMark.mc.field_1724.method_5667())) != null) {
            pings = entry.method_2959();
        }
        this.fpsText.set(String.valueOf(mc.method_47599()));
        this.pingText.set(String.valueOf(pings));
        float fpsW = this.fpsText.width(fonts, TEXT) + fonts.getWidth("fps", TEXT);
        float pingW = this.pingText.width(fonts, TEXT) + fonts.getWidth("ms", TEXT);
        float w = BLOCK + fonts.getWidth(user, TEXT);
        float w2 = 57.0f * S + fonts.getWidth(user, TEXT) + fpsW + pingW;
        RenderUtil.Render2D.glow(x, y, w2, H - 0.5f, ColorUtil.getColor(0, 0.1f), RADIUS, 8.0f, 1.0f);
        RenderUtil.Blur.blur(x, y, w2, H, 1.0f, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.35f, 1.0f)));
        Fonts.decide_2.draw("N", x + ICON_X, y + ICON_Y, ICON, ColorUtil.getClientColor(1));
        fonts.draw(user, x + TEXT_X, y + TEXT_Y, TEXT, ThemeColor.getTextColor());
        float x2 = x + w + 2.0f * S;
        Fonts.icon.draw("C", x2, y + ICON_Y, ICON, ThemeColor.getSeparatorColor());
        RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/hud/fps.png"), (x2 += AFTER_SEP) + 0.5f * S, y + (H - ICON) / 2.0f, ICON, ICON, ColorUtil.getClientColor(1));
        this.drawValue(fonts, this.fpsText, "fps", x2 += AFTER_ICON, y + TEXT_Y);
        float x3 = x + w + 20.0f * S + fpsW;
        Fonts.icon.draw("C", x3, y + ICON_Y, ICON, ThemeColor.getSeparatorColor());
        Fonts.decide_2.draw("R", (x3 += AFTER_SEP) + 0.5f * S, y + ICON_Y, ICON, ColorUtil.getClientColor(1));
        this.drawValue(fonts, this.pingText, "ms", x3 += AFTER_ICON, y + TEXT_Y);
        dragSetting.size.set(18.5f * S + w2, H);
    }

    private float drawValue(Font font, RollingText value, String suffix, float x, float y) {
        value.draw(font, x, y, TEXT, ThemeColor.getTextColor());
        float width = value.width(font, TEXT);
        font.draw(suffix, x + width, y, TEXT, ColorUtil.getColor(200));
        return width + font.getWidth(suffix, TEXT);
    }
}

