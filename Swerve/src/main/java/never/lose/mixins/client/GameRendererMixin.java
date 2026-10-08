package never.lose.mixins.client;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import never.lose.managers.ModuleManager;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "renderHand", at = @At("HEAD"))
    private void endless$onRenderHandPre(Camera camera, float tickDelta, Matrix4f positionMatrix, CallbackInfo ci) {
        ModuleManager.get().dispatchRenderHandPre();
    }

    @Inject(method = "renderHand", at = @At("RETURN"))
    private void endless$onRenderHandPost(Camera camera, float tickDelta, Matrix4f positionMatrix, CallbackInfo ci) {
        ModuleManager.get().dispatchRenderHandPost();
    }
}