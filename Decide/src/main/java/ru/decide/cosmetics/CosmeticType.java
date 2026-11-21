/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.utils.animation.satoshi.Animation;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;

@Environment(value=EnvType.CLIENT)
public enum CosmeticType {
    WINGS("\u041a\u0440\u044b\u043b\u044c\u044f", "wings"),
    CAPES("\u041f\u043b\u0430\u0449\u0438", "cape"),
    BOWS("\u041b\u0443\u043a\u0438", "bow"),
    CROSSBOWS("\u0410\u0440\u0431\u0430\u043b\u0435\u0442\u044b", "crossbow"),
    END_CRYSTALS("\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b \u041a\u0440\u0430\u044f", "end_crystal"),
    GOLDEN_APPLES("\u0417\u043e\u043b\u043e\u0442\u044b\u0435 \u044f\u0431\u043b\u043e\u043a\u0438", "golden_apple"),
    ENCHANTED_GOLDEN_APPLES("\u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0435 \u044f\u0431\u043b\u043e\u043a\u0438", "enchanted_golden_apple"),
    KNIVES("\u041d\u043e\u0436\u0438", "knife"),
    HATS("\u0428\u0430\u043f\u043a\u0438", "hat"),
    MASKS("\u041c\u0430\u0441\u043a\u0438", "mask"),
    GLASSES("\u041e\u0447\u043a\u0438", "glasses"),
    AURAS("\u0410\u0443\u0440\u044b", "auras"),
    PETS("\u041f\u0438\u0442\u043e\u043c\u0446\u044b", "pet"),
    BACKPACKS("\u0420\u044e\u043a\u0437\u0430\u043a\u0438", "accessories"),
    ACCESSORIES("\u0410\u043a\u0441\u0435\u0441\u0441\u0443\u0430\u0440\u044b", "watch"),
    BANDANNAS("\u0411\u0430\u043d\u0434\u0430\u043d\u044b", "bandanna"),
    SUITS("\u041a\u043e\u0441\u0442\u044e\u043c\u044b", "suits"),
    SHOES("\u041e\u0431\u0443\u0432\u044c", "shoes"),
    COMPANIONS("\u0421\u043f\u0443\u0442\u043d\u0438\u043a\u0438", "companion");

    private final String name;
    private final String type;
    public final Animation hoverAnim = new EaseInOutQuad(250, 1.0);
    public final Animation selectAnim = new EaseInOutQuad(250, 1.0);

    public int count() {
        return CosmeticManager.getInstance().getFilteredEntries(this.type).size();
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getType() {
        return this.type;
    }

    @Generated
    public Animation getHoverAnim() {
        return this.hoverAnim;
    }

    @Generated
    public Animation getSelectAnim() {
        return this.selectAnim;
    }

    @Generated
    private CosmeticType(String name, String type) {
        this.name = name;
        this.type = type;
    }
}

