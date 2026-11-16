/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.animation.satoshi.Animation;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public enum Theme {
    Purple("Purple", new Color(216, 68, 234).getRGB(), new Color(-1725690581, true).getRGB(), ColorUtil.getColor(240), ColorUtil.getColor(160), ColorUtil.getColor(24, 24, 27), new Color(216, 68, 234).getRGB()),
    NIGHT("Blue", new Color(9151231).getRGB(), new Color(-1726867157, true).getRGB(), ColorUtil.getColor(240), ColorUtil.getColor(160), ColorUtil.getColor(24, 24, 27), new Color(9151231).getRGB()),
    AKAR("Red", new Color(0xFF8B8B).getRGB(), new Color(-1726280692, true).getRGB(), ColorUtil.getColor(240), ColorUtil.getColor(160), ColorUtil.getColor(24, 24, 27), new Color(0xFF8B8B).getRGB()),
    VIOLKA("Violet", new Color(10521599).getRGB(), new Color(-1726672860, true).getRGB(), ColorUtil.getColor(240), ColorUtil.getColor(160), ColorUtil.getColor(24, 24, 27), new Color(10521599).getRGB()),
    TOXIS("Toxis", new Color(9961355).getRGB(), new Color(-1726996979, true).getRGB(), ColorUtil.getColor(240), ColorUtil.getColor(160), ColorUtil.getColor(24, 24, 27), new Color(12189579).getRGB()),
    Decide("DecideVisual", new Color(0xDFDFDF).getRGB(), new Color(-1728053248, true).getRGB(), ColorUtil.getColor(240), ColorUtil.getColor(160), ColorUtil.getColor(24, 24, 27), new Color(0xDFDFDF).getRGB()),
    Turquoise("Turquoise", new Color(96, 255, 198).getRGB(), new Color(-1726797010, true).getRGB(), ColorUtil.getColor(240), ColorUtil.getColor(160), ColorUtil.getColor(24, 24, 27), new Color(96, 255, 198).getRGB());

    private final String name;
    private final int client;
    private final int rect;
    private final int text;
    private final int text_dark;
    private final int rect_two;
    private final int text_client_c;
    public Animation animation = new EaseInOutQuad(300, 1.0);

    private Theme(String name, int client, int rect, int text, int text_dark, int rect_two, int text_client_c) {
        this.name = name;
        this.client = client;
        this.rect = rect;
        this.text = text;
        this.text_dark = text_dark;
        this.rect_two = rect_two;
        this.text_client_c = text_client_c;
    }

    public String getName() {
        return this.name;
    }

    public int getClient() {
        return this.client;
    }

    public int getRect() {
        return this.rect;
    }

    public int getText() {
        return this.text;
    }

    public int getText_dark() {
        return this.text_dark;
    }

    public int getRect_two() {
        return this.rect_two;
    }

    public int getText_client_c() {
        return this.text_client_c;
    }
}

