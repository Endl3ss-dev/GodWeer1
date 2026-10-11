package swerve.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.pipeline.RenderTarget;

import swerve.utils.render.HandOutline;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    // RenderType.MAIN_TARGET перепривязывает главный FBO перед каждой отрисовкой:
    // на время захвата силуэта подменяем его, иначе силуэт уходит в основной кадр.
    @ModifyReturnValue(method = "getMainRenderTarget", at = @At("RETURN"))
    private RenderTarget swerve$redirectMainTarget(RenderTarget original) {
        return HandOutline.redirectTarget(original);
    }
}
