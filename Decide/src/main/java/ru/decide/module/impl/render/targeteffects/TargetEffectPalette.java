/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 */
package ru.decide.module.impl.render.targeteffects;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import ru.decide.utils.colors.ColorUtil;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface TargetEffectPalette {
    public int at(float var1);

    public static TargetEffectPalette gradient(int from, int to) {
        return t -> ColorUtil.overCol(from, to, 0.5f - 0.5f * class_3532.method_15362((double)(t * ((float)Math.PI * 2))));
    }
}

