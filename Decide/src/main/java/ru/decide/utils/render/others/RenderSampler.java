/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12137
 */
package ru.decide.utils.render.others;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12137;

@Environment(value=EnvType.CLIENT)
public final class RenderSampler {
    private static class_12137 linear;
    private static class_12137 nearest;
    private static class_12137 linearRepeat;

    private RenderSampler() {
    }

    public static class_12137 nearest() {
        if (nearest == null) {
            nearest = RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST);
        }
        return nearest;
    }

    public static class_12137 linear() {
        if (linear == null) {
            linear = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
        }
        return linear;
    }

    public static class_12137 linearRepeat() {
        if (linearRepeat == null) {
            linearRepeat = RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
        }
        return linearRepeat;
    }
}

