package never.lose.mixins.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import never.lose.modules.impl.Render.Ambience;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @Shadow
    private static float red;

    @Shadow
    private static float green;

    @Shadow
    private static float blue;

    @Inject(method = "render", at = @At("TAIL"))
    private static void endless$overrideFogColor(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfo ci) {
        float[] rgb = {red, green, blue};
        if (!Ambience.applyFogColor(camera, rgb)) {
            return;
        }
        red = rgb[0];
        green = rgb[1];
        blue = rgb[2];
        RenderSystem.setShaderFogColor(red, green, blue, 1.0F);
    }

    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void endless$overrideFogDistance(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        Ambience.applyFogDistance(camera, viewDistance);
    }
}