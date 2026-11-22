/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 */
package ru.decide.cosmetics.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.cosmetics.render.CosmeticCapeData;
import ru.decide.cosmetics.render.CosmeticTextureInfo;

@Environment(value=EnvType.CLIENT)
public final class CosmeticCapes {
    public static final int NO_CAPE_INDEX = 999;

    public static class_2960 getEquippedTexture() {
        Integer index = CosmeticManager.getInstance().getSelectedByType().get("cape");
        if (index == null || index == 999) {
            return null;
        }
        return CosmeticManager.getInstance().getCapeTexture(index);
    }

    public static boolean hasCape() {
        return CosmeticCapes.getEquippedTexture() != null;
    }

    public static CosmeticCapeData getCapeData() {
        class_2960 texture = CosmeticCapes.getEquippedTexture();
        if (texture == null) {
            return null;
        }
        CosmeticManager.CapeAnimationInfo anim = CosmeticManager.getInstance().getCapeAnimation(texture);
        int frames = anim.frameCount;
        int[] size = CosmeticTextureInfo.size(texture);
        int textureWidth = size[0];
        int textureHeight = size[1];
        int frameWidth = textureWidth;
        int frameHeight = textureHeight;
        if (anim.atlasFrame) {
            if (frames > 1 && textureHeight > 0) {
                frameWidth = textureWidth;
                frameHeight = Math.max(1, textureHeight / frames);
            } else {
                frames = 1;
            }
        } else if (frames > 1 && textureWidth > 0 && textureHeight > 0) {
            if (anim.isTight) {
                frameWidth = textureWidth / frames;
                frameHeight = textureHeight / frames;
            } else {
                frameWidth = anim.baseWidth > 0.0f ? (int)anim.baseWidth : textureWidth / frames;
                frameHeight = textureHeight / frames;
            }
        } else {
            frames = 1;
        }
        return new CosmeticCapeData(0, "cape", texture, textureWidth, textureHeight, frames, frameWidth, frameHeight, anim.frameTime, anim.atlasFrame);
    }

    private CosmeticCapes() {
    }
}

