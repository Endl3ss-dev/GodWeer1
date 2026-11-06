/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.melee;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record MeleeHandTransform(float[] rotation, float[] translation, float[] scale) {
    public static final MeleeHandTransform IDENTITY = new MeleeHandTransform(new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, new float[]{1.0f, 1.0f, 1.0f});
}

