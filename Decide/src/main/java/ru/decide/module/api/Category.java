/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.api;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.animation.satoshi.Animation;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;

@Environment(value=EnvType.CLIENT)
public enum Category {
    VISUALS("\u0412\u0438\u0437\u0443\u0430\u043b\u044b", "Q"),
    HUD("\u0425\u0443\u0434", "H"),
    UTILITIES("\u0423\u0442\u0438\u043b\u0438\u0442\u044b", "L"),
    COSMETICS("\u041a\u043e\u0441\u043c\u0435\u0442\u0438\u043a\u0430", "T"),
    CONFIGS("\u041a\u043e\u043d\u0444\u0438\u0433\u0438", "R"),
    MARKERS("\u041c\u0435\u0442\u043a\u0438", "S");

    private final String name;
    private final String icon;
    public Animation alphaS = new EaseInOutQuad(300, 1.0);
    public Animation alphaS2 = new EaseInOutQuad(300, 1.0);
    public ru.decide.utils.animation.Animation animation = new ru.decide.utils.animation.Animation();

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getIcon() {
        return this.icon;
    }

    @Generated
    public Animation getAlphaS() {
        return this.alphaS;
    }

    @Generated
    public Animation getAlphaS2() {
        return this.alphaS2;
    }

    @Generated
    public ru.decide.utils.animation.Animation getAnimation() {
        return this.animation;
    }

    @Generated
    private Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }
}

