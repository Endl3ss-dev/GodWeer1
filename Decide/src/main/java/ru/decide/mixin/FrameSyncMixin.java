/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.textures.GpuTexture
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package ru.decide.mixin;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.textures.GpuTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.decide.optimization.FrameSyncHandler;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_310.class})
public abstract class FrameSyncMixin {
    @Redirect(method={"method_1523"}, at=@At(value="INVOKE", target="Lcom/mojang/blaze3d/systems/CommandEncoder;clearColorAndDepthTextures(Lcom/mojang/blaze3d/textures/GpuTexture;ILcom/mojang/blaze3d/textures/GpuTexture;D)V"))
    private void framesync$skipClear(CommandEncoder encoder, GpuTexture colorTexture, int clearColor, GpuTexture depthTexture, double clearDepth) {
        if (FrameSyncHandler.beginFrame()) {
            encoder.clearColorAndDepthTextures(colorTexture, clearColor, depthTexture, clearDepth);
        }
    }
}

