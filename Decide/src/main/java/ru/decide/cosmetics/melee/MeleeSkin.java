/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 */
package ru.decide.cosmetics.melee;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import ru.decide.cosmetics.melee.MeleeHandTransform;
import ru.decide.cosmetics.melee.MeleeVariant;

@Environment(value=EnvType.CLIENT)
public final class MeleeSkin {
    private static final String MODEL_PATH = "/assets/decide/cosmetic/melee/%s.geo.json";
    private static final String ANIMATION_PATH = "/assets/decide/cosmetic/melee/%s.anim.json";
    private static final String NAMESPACE = "decide";
    private final String key;
    private final String geometryKey;
    private final String displayName;
    private final List<MeleeVariant> variants;
    private final List<String> holyBones;
    private final MeleeHandTransform thirdPersonRightHand;
    private final MeleeHandTransform thirdPersonLeftHand;
    private final class_2960 texture;
    private final class_2960 icon;
    private final int id;
    private int cosmeticIndex = -1;

    MeleeSkin(String key, String geometryKey, String displayName, List<MeleeVariant> variants, List<String> holyBones, MeleeHandTransform thirdPersonRightHand, MeleeHandTransform thirdPersonLeftHand) {
        this.key = key;
        this.geometryKey = geometryKey != null && !geometryKey.isEmpty() ? geometryKey : key;
        this.displayName = displayName;
        this.variants = variants;
        this.holyBones = holyBones;
        this.thirdPersonRightHand = thirdPersonRightHand;
        this.thirdPersonLeftHand = thirdPersonLeftHand;
        this.texture = class_2960.method_60655((String)NAMESPACE, (String)String.format("textures/cosmetic/melee/%s.png", key));
        this.icon = class_2960.method_60655((String)NAMESPACE, (String)String.format("textures/cosmetic/melee/slot/%s.png", key));
        this.id = ("melee_" + key).hashCode();
    }

    public String getKey() {
        return this.key;
    }

    public String getGeometryKey() {
        return this.geometryKey;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<MeleeVariant> getVariants() {
        return this.variants;
    }

    public List<String> getHolyBones() {
        return this.holyBones;
    }

    public MeleeHandTransform getThirdPersonRightHand() {
        return this.thirdPersonRightHand;
    }

    public MeleeHandTransform getThirdPersonLeftHand() {
        return this.thirdPersonLeftHand;
    }

    public class_2960 getTexture() {
        return this.texture;
    }

    public class_2960 getIcon() {
        return this.icon;
    }

    public int getId() {
        return this.id;
    }

    public int getCosmeticIndex() {
        return this.cosmeticIndex;
    }

    void setCosmeticIndex(int cosmeticIndex) {
        this.cosmeticIndex = cosmeticIndex;
    }

    public String modelResource() {
        return String.format(MODEL_PATH, this.geometryKey);
    }

    public String animationResource() {
        return String.format(ANIMATION_PATH, this.geometryKey);
    }
}

