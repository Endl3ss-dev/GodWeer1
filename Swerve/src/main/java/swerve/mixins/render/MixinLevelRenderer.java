package swerve.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.pipeline.RenderTarget;

import swerve.utils.render.HandOutline;

import net.minecraft.client.renderer.LevelRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {

    // В режиме «Fabulous» предметы рисуются в ITEM_ENTITY_TARGET — его тоже подменяем на время захвата.
    @ModifyReturnValue(method = "getItemEntityTarget", at = @At("RETURN"))
    private RenderTarget swerve$redirectItemTarget(RenderTarget original) {
        return HandOutline.redirectTarget(original);
    }
}
