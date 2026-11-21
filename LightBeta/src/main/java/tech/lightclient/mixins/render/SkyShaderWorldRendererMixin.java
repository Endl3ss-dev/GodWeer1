package tech.lightclient.mixins.render;


import com.mojang.blaze3d.buffers.GpuBufferSlice;
import tech.lightclient.modules.impl.render.SkyShader;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class SkyShaderWorldRendererMixin {
    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void light$cancelSky(FrameGraphBuilder frameGraphBuilder, Camera camera, GpuBufferSlice fog, CallbackInfo ci) {
        if (SkyShader.shouldCancelSky()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private void light$cancelClouds(FrameGraphBuilder frameGraphBuilder, CloudRenderMode mode, Vec3d cameraPos,
                                    long ticks, float tickProgress, int cloudHeight, float cloudRenderDistance,
                                    CallbackInfo ci) {
        if (SkyShader.shouldCancelClouds()) {
            ci.cancel();
        }
    }
}
