// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import su.catlean.api.event.GofraState;
import su.catlean.client.features.modules.cosmetic.HandChams;
import su.catlean.client.features.modules.esp.PlayerEsp;
import su.catlean.client.render.uniforms.ProjectionBuffer;
import su.catlean.mixins.accessors.GameRendererAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render3D;

public final class ShaderEspRenderer {
   @Nullable
   private MainTarget mainTarget;
   @Nullable
   private MainTarget mainTarget2;
   private boolean flag;

   public ShaderEspRenderer() {
   }

   @Nullable
   public final MainTarget getMainTarget() {
      return this.mainTarget;
   }

   public final void setMainTarget(MainTarget mainTarget) {
      this.mainTarget = mainTarget;
   }

   @Nullable
   public final MainTarget getMainTarget2() {
      return this.mainTarget2;
   }

   public final void setMainTarget2(MainTarget mainTarget) {
      this.mainTarget2 = mainTarget;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final void render(PoseStack stack, boolean flag, int value, int value2) {
      this.flag = true;
      this.applyMainRenderTarget();
      this.render2(stack, flag, value, value2);
      this.flag = false;
   }

   public final void updateShaderMode(
      PlayerEsp.ShaderMode mode,
      Color color,
      boolean flag,
      boolean flag2,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      int value,
      Color color1v,
      Color color2v,
      int value2,
      float amount5,
      float amount6,
      float amount7,
      float amount8,
      float amount9,
      float amount10
   ) {
      if (this.mainTarget != null && this.mainTarget2 != null) {
         this.flag = true;
         HandChams.INSTANCE
            .applyDarker(
               mode,
               color,
               this.mainTarget,
               this.mainTarget2,
               flag,
               flag2,
               amount,
               amount2,
               amount3,
               amount4,
               value,
               color1v,
               color2v,
               value2,
               amount5,
               amount6,
               amount7,
               amount8,
               amount9,
               amount10
            );
         this.flag = false;
      }
   }

   private final void applyMainRenderTarget() {
      if (this.mainTarget == null) {
         this.mainTarget = new MainTarget(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      if (MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width != this.mainTarget.width
         || MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height != this.mainTarget.height) {
         this.mainTarget
            .resize(MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height);
      }

      RenderSystem.getDevice()
         .createCommandEncoder()
         .clearColorAndDepthTextures(
            this.mainTarget.getColorTexture(), (Vector4fc)(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F)), this.mainTarget.getDepthTexture(), 0.0
         );
      if (this.mainTarget2 == null) {
         this.mainTarget2 = new MainTarget(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      if (MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width != this.mainTarget2.width
         || MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height != this.mainTarget2.height) {
         this.mainTarget2
            .resize(MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height);
      }

      RenderSystem.getDevice()
         .createCommandEncoder()
         .clearColorAndDepthTextures(
            this.mainTarget2.getColorTexture(), (Vector4fc)(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F)), this.mainTarget2.getDepthTexture(), 0.0
         );
   }

   private final void render2(PoseStack poseStack, boolean flag, int value, int value2) {
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      double amount4 = Render3D.INSTANCE.getAmount4();
      boolean entityShadow = (Boolean)MinecraftHolder.getMinecraft().options.entityShadows().get();
      MinecraftHolder.getMinecraft().options.entityShadows().set(false);
      GofraState.INSTANCE.setModifyBuffer(true);
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(
         ProjectionBuffer.INSTANCE.getProjectionMatrixBuffer().getBuffer(Render3D.INSTANCE.getMatrix4f()), RenderSystem.getProjectionType()
      );
      SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();
      Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
      var arrayList = new ArrayList();

      for (Object object : entitiesForRendering) {
         Entity entity = (Entity)object;
         if (Math.hypot(mainCamera.position().x - entity.getX(), mainCamera.position().z - entity.getZ()) < value2 && Math.abs(mainCamera.position().y - entity.getY()) < value) {
            arrayList.add(object);
         }
      }

      for (Entity entity2 : (Iterable<Entity>)arrayList) {
         if (MinecraftHolder.getMinecraft()
               .getEntityRenderDispatcher()
               .shouldRender(entity2, Render3D.INSTANCE.getFrustum(), mainCamera.position().x, mainCamera.position().y, mainCamera.position().z)
            && (!Intrinsics.areEqual(entity2, MinecraftHolder.getMinecraft().player) || !MinecraftHolder.getMinecraft().options.getCameraType().isFirstPerson())
            && (entity2 instanceof Player || flag)
            && (entity2 instanceof EndCrystal || !flag)) {
            double value3 = Mth.lerp(amount4, entity2.xOld, entity2.getX()) - mainCamera.position().x;
            double value4 = Mth.lerp(amount4, entity2.yOld, entity2.getY()) - mainCamera.position().y;
            double value5 = Mth.lerp(amount4, entity2.zOld, entity2.getZ()) - mainCamera.position().z;
            boolean isInvisible = entity2.isInvisible();
            boolean hasGlowingTag = entity2.hasGlowingTag();
            entity2.setInvisible(false);
            entity2.setGlowingTag(false);

            try {
               EntityRenderState renderState = MinecraftHolder.getMinecraft()
                  .getEntityRenderDispatcher()
                  .getRenderer(entity2)
                  .createRenderState(entity2, Render3D.INSTANCE.getAmount4());
               renderState.outlineColor = 0;
               CameraRenderState cameraRenderState = ((GameRendererAccessor)MinecraftHolder.getMinecraft().gameRenderer).catlean$getGameRenderState().levelRenderState.cameraRenderState;
               MinecraftHolder.getMinecraft().getEntityRenderDispatcher().submit(renderState, cameraRenderState, value3, value4, value5, poseStack, (SubmitNodeCollector)submitNodeStorage);
            } catch (Exception exception) {
            }

            entity2.setInvisible(isInvisible);
            entity2.setGlowingTag(hasGlowingTag);
         }
      }

      GpuTextureView outputColorTextureOverride = RenderSystem.outputColorTextureOverride;
      GpuTextureView outputDepthTextureOverride = RenderSystem.outputDepthTextureOverride;

      try {
         RenderSystem.outputColorTextureOverride = this.mainTarget.getColorTextureView();
         RenderSystem.outputDepthTextureOverride = this.mainTarget.getDepthTextureView();
         MinecraftHolder.getMinecraft().gameRenderer.featureRenderDispatcher().renderAllFeatures(submitNodeStorage);
      } finally {
         RenderSystem.outputColorTextureOverride = outputColorTextureOverride;
         RenderSystem.outputDepthTextureOverride = outputDepthTextureOverride;
      }

      GofraState.INSTANCE.setModifyBuffer(false);
      MinecraftHolder.getMinecraft().options.entityShadows().set(entityShadow);
      RenderSystem.restoreProjectionMatrix();
   }
}