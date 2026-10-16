package swerve.mixins.render;

import com.mojang.blaze3d.pipeline.RenderTarget;

import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: пока экран снимается в текстуру, перепривязка основного буфера запрещена —
// иначе остаток GUI (эффекты, подсветки) ушёл бы на экран мимо панели. Сама съёмка снимает флаг до возврата.
@Mixin(RenderTarget.class)
public abstract class MixinRenderTarget {

    @Inject(method = "bindWrite", at = @At("HEAD"), cancellable = true)
    private void swerve$spatialBlockMainBind(boolean setViewport, CallbackInfo ci) {
        if (SpatialState.extracting && (Object) this == Minecraft.getInstance().getMainRenderTarget()) {
            ci.cancel();
        }
    }
}
