// deobf by pusia
package su.catlean.mixins.accessors;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({GameRenderer.class})
public interface GameRendererAccessor {

   @Invoker("bobHurt")
   void itiltViewWhenHurt(CameraRenderState cameraRenderState, PoseStack poseStack);

   @Invoker("bobView")
   void ibobView(CameraRenderState cameraRenderState, PoseStack poseStack);

   @Accessor("gameRenderState")
   GameRenderState catlean$getGameRenderState();

   @Accessor("hudProjection")
   Projection catlean$getHudProjection();

   @Accessor("hud3dProjectionMatrixBuffer")
   ProjectionMatrixBuffer catlean$getHud3dProjectionMatrixBuffer();
}