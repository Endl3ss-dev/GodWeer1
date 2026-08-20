package fun.volon.mixin;

import fun.volon.api.utils.render.SkyShaderRenderer;
import fun.volon.client.modules.impl.render.WorldTweaks;
import net.minecraft.client.render.SkyRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRendering.class)
public class SkyRenderingMixin {

    @Inject(method = "renderSky(FFF)V", at = @At("HEAD"), cancellable = true)
    private void renderShaderSky(float red, float green, float blue, CallbackInfo ci) {
        WorldTweaks worldTweaks = WorldTweaks.INSTANCE;
        if (worldTweaks == null || !worldTweaks.isShaderEnabled()) {
            return;
        }

        SkyShaderRenderer.INSTANCE.renderSky();
        ci.cancel();
    }

    @Inject(method = "close()V", at = @At("HEAD"))
    private void closeShaderSky(CallbackInfo ci) {
        SkyShaderRenderer.INSTANCE.close();
    }
}
