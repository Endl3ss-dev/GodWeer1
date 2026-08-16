package never.lose.mixins.client;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import never.lose.managers.ModuleManager;
import never.lose.utils.render.Render;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class ModuleRenderMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void endless$dispatchRender(
            RenderTickCounter tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightmapTextureManager lightmapTextureManager,
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        float delta = tickCounter.getTickDelta(true);
        MatrixStack stack = new MatrixStack();
        stack.multiplyPositionMatrix(positionMatrix);
        Render.lastWorldSpaceMatrix.set(positionMatrix);
        Render.lastProjMat.set(projectionMatrix);
        ModuleManager.get().dispatchRender3D(stack, delta);
    }
}
