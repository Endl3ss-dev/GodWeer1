/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 */
package ru.decide.cosmetics.render;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;

@Environment(value=EnvType.CLIENT)
public final class CosmeticCapeData {
    private final int rawId;
    private final String name;
    private final class_2960 texture;
    private final int textureWidth;
    private final int textureHeight;
    private final int frameCount;
    private final int frameWidth;
    private final int frameHeight;
    private final int frameTime;
    private final boolean atlasFrame;

    public CosmeticCapeData(int rawId, String name, class_2960 texture, int textureWidth, int textureHeight, int frameCount, int frameWidth, int frameHeight, int frameTime) {
        this(rawId, name, texture, textureWidth, textureHeight, frameCount, frameWidth, frameHeight, frameTime, false);
    }

    public CosmeticCapeData(int rawId, String name, class_2960 texture, int textureWidth, int textureHeight, int frameCount, int frameWidth, int frameHeight, int frameTime, boolean atlasFrame) {
        this.rawId = rawId;
        this.name = name;
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.frameCount = frameCount;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameTime = frameTime;
        this.atlasFrame = atlasFrame;
    }

    @Generated
    public int getRawId() {
        return this.rawId;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public class_2960 getTexture() {
        return this.texture;
    }

    @Generated
    public int getTextureWidth() {
        return this.textureWidth;
    }

    @Generated
    public int getTextureHeight() {
        return this.textureHeight;
    }

    @Generated
    public int getFrameCount() {
        return this.frameCount;
    }

    @Generated
    public int getFrameWidth() {
        return this.frameWidth;
    }

    @Generated
    public int getFrameHeight() {
        return this.frameHeight;
    }

    @Generated
    public int getFrameTime() {
        return this.frameTime;
    }

    @Generated
    public boolean isAtlasFrame() {
        return this.atlasFrame;
    }
}

