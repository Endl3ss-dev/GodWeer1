package swerve.mixins.render;

import com.mojang.blaze3d.resource.GraphicsResourceAllocator;

import swerve.utils.render.spatial.ScreenSurface;
import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: снимок перспективы уровня (projection + view) — в ней потом рисуется панель.
@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void swerve$spatialCapturePerspective(GraphicsResourceAllocator allocator, DeltaTracker deltaTracker,
                                                  boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                                                  Matrix4f modelView, Matrix4f projection, CallbackInfo ci) {
        if (SpatialState.active() && (SpatialState.shouldCapture() || ScreenSurface.isClosing())) {
            ScreenSurface.capturePerspective(projection, modelView);
        }
    }
}
