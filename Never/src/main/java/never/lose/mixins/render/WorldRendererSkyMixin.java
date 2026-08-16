package never.lose.mixins.render;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import never.lose.modules.impl.Render.Ambience;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererSkyMixin {

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void endless$renderPlasmaSky(
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            float tickDelta,
            Camera camera,
            boolean thickFog,
            Runnable fogCallback,
            CallbackInfo ci
    ) {
        if (Ambience.renderCustomSky(positionMatrix, projectionMatrix, tickDelta, camera, fogCallback)) {
            ci.cancel();
        }
    }
}