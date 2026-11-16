/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1309
 */
package ru.decide.module.impl.render.targetesp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1309;

@Environment(value=EnvType.CLIENT)
public record TargetEspRenderContext(class_1309 target, float alpha, float partialTicks, long frameTimeMs, int primaryColor, int secondaryColor, float hurtProgress, float chainImpactProgress) {
}

