// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import su.catlean.api.enums.ParticleType;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.render.FrameBufferEvent;
import su.catlean.client.features.modules.cosmetic.HandChams;
import su.catlean.client.render.uniforms.ProjectionBuffer;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.GameRendererAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.AtlasTexture;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class EntityPreviewRenderer {
   @NotNull
   public static final EntityPreviewRenderer INSTANCE = new EntityPreviewRenderer();
   private static boolean flag;
   @Nullable
   private static MainTarget mainTarget;

   private EntityPreviewRenderer() {
   }

   @Nullable
   public final MainTarget getMainTarget() {
      return mainTarget;
   }

   public final void setMainTarget(MainTarget mainTarget) {
      EntityPreviewRenderer.mainTarget = mainTarget;
   }

   public final void render(PoseStack stack, Entity target) {
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      this.applyMainRenderTarget();
      double amount4 = Render3D.INSTANCE.getAmount4();
      double value = Mth.lerp(amount4, target.xOld, target.getX()) - mainCamera.position().x;
      double value2 = Mth.lerp(amount4, target.yOld, target.getY()) - mainCamera.position().y;
      double value3 = Mth.lerp(amount4, target.zOld, target.getZ()) - mainCamera.position().z;
      flag = true;
      boolean entityShadow = (Boolean)MinecraftHolder.getMinecraft().options.entityShadows().get();
      MinecraftHolder.getMinecraft().options.entityShadows().set(false);
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(
         ProjectionBuffer.INSTANCE.getProjectionMatrixBuffer().getBuffer(Render3D.INSTANCE.getMatrix4f()), RenderSystem.getProjectionType()
      );
      GofraState.INSTANCE.setModifyBuffer(true);
      EntityRenderState renderState = MinecraftHolder.getMinecraft()
         .getEntityRenderDispatcher()
         .getRenderer(target)
         .createRenderState(target, Render3D.INSTANCE.getAmount4());
      SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();
      CameraRenderState cameraRenderState = ((GameRendererAccessor)MinecraftHolder.getMinecraft().gameRenderer).catlean$getGameRenderState().levelRenderState.cameraRenderState;
      MinecraftHolder.getMinecraft().getEntityRenderDispatcher().submit(renderState, cameraRenderState, value, value2, value3, stack, (SubmitNodeCollector)submitNodeStorage);
      MinecraftHolder.getMinecraft().gameRenderer.featureRenderDispatcher().renderAllFeatures(submitNodeStorage);
      GofraState.INSTANCE.setModifyBuffer(false);
      MinecraftHolder.getMinecraft().options.entityShadows().set(entityShadow);
      RenderSystem.restoreProjectionMatrix();
      flag = false;
   }

   public final void applySetModifyBuffer() {
      if (mainTarget != null) {
         flag = true;
         GofraState.INSTANCE.setModifyBuffer(true);
         HandChams.INSTANCE
            .applyDarker(
               null,
               Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.computeColor(0), 0.4F),
               mainTarget,
               mainTarget,
               false,
               false,
               0.0F,
               0.0F,
               0.0F,
               4.0F,
               0,
               Color.WHITE,
               Color.WHITE,
               0,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F
            );
         GofraState.INSTANCE.setModifyBuffer(false);
         flag = false;
      }
   }

   public final void render(PoseStack matrix, Entity target, float amount) {
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(AtlasTexture.getCatlean()).getTextureView();
      double amount2 = Render2D.INSTANCE.computeAmount(target.xo, target.getX(), (double)Render3D.INSTANCE.getAmount4()) - mainCamera.position().x;
      double amount3 = Render2D.INSTANCE.computeAmount(target.yo, target.getY(), (double)Render3D.INSTANCE.getAmount4()) - mainCamera.position().y;
      double amount4 = Render2D.INSTANCE.computeAmount(target.zo, target.getZ(), (double)Render3D.INSTANCE.getAmount4()) - mainCamera.position().z;
      double amount5 = Render2D.INSTANCE.computeAmount(target.tickCount - 1.0, (double)target.tickCount, (double)Render3D.INSTANCE.getAmount4());
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 48, false, 4, null);
      boolean hasLineOfSight = MinecraftHolder.getLocalPlayer().hasLineOfSight(target);

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 15; j++) {
            double radians = Math.toRadians(((j / 1.5F + amount5) * 8 + i * 120) % 2880);
            double radians2 = Math.sin(Math.toRadians(amount5 * 2.5F + j * (i + 1)) * 3.0F) / 1.8F;
            float value = (float)j / 14;
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(mainCamera.xRot()));
            poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(mainCamera.yRot() + 180.0F));
            poseStack.translate(amount2 + Math.cos(radians) * target.getBbWidth(), amount3 + 1 + radians2, amount4 + Math.sin(radians) * target.getBbWidth());
            poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-mainCamera.yRot()));
            poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(mainCamera.xRot()));
            Matrix4f pose = poseStack.last().pose();
            Color color = Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.computeColor((int)(180 * value)), value * amount);
            float value2 = (float)Math.max((double)(0.24F * value), 0.2);
            ParticleType firefly = ParticleType.FIREFLY;
            meshBuilder.computeMeshBuilder(pose, -value2, value2, 0.0F).computeMeshBuilder(firefly.getAmount(), firefly.getAmount4()).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(pose, value2, value2, 0.0F).computeMeshBuilder(firefly.getAmount3(), firefly.getAmount4()).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(pose, value2, -value2, 0.0F).computeMeshBuilder(firefly.getAmount3(), firefly.getAmount2()).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(pose, -value2, -value2, 0.0F).computeMeshBuilder(firefly.getAmount(), firefly.getAmount2()).fetchAlpha(color);
         }
      }

      matrix.pushPose();
      matrix.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-mainCamera.yRot() + 180.0F));
      matrix.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-mainCamera.xRot()));
      MeshBuilder.defaultImpl(
         meshBuilder,
         hasLineOfSight ? Pipelines.INSTANCE.getRenderPipeline19() : Pipelines.INSTANCE.getRenderPipeline18(),
         null,
         null,
         matrix.last().pose(),
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
         null,
         86,
         null
      );
      matrix.popPose();
   }

   public final void render2(PoseStack matrix, Entity target, float amount) {
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(AtlasTexture.getCatlean()).getTextureView();
      double amount2 = Render2D.INSTANCE.computeAmount(target.xo, target.getX(), (double)Render3D.INSTANCE.getAmount4()) - mainCamera.position().x;
      double amount3 = Render2D.INSTANCE.computeAmount(target.yo, target.getY(), (double)Render3D.INSTANCE.getAmount4()) - mainCamera.position().y;
      double amount4 = Render2D.INSTANCE.computeAmount(target.zo, target.getZ(), (double)Render3D.INSTANCE.getAmount4()) - mainCamera.position().z;
      double amount5 = Render2D.INSTANCE.computeAmount(target.tickCount - 1.0, (double)target.tickCount, (double)Render3D.INSTANCE.getAmount4());
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 48, false, 4, null);
      boolean hasLineOfSight = MinecraftHolder.getLocalPlayer().hasLineOfSight(target);
      byte byteValue = 67;

      for (int i = 0; i < 8; i++) {
         for (int j = 0; j < byteValue; j++) {
            double value = (double)j / byteValue * (Math.PI * 2);
            double bbWidth = Math.cos(value) * target.getBbWidth();
            double bbWidth2 = Math.sin(value) * target.getBbWidth();
            double radians = Math.sin(Math.toRadians((amount5 - i / 2.0F) * 2.5F) * 3.0F) / 1.2F;
            float value2 = (float)j / (byteValue + 1);
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(mainCamera.xRot()));
            poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(mainCamera.yRot() + 180.0F));
            poseStack.translate(amount2 + bbWidth, amount3 + 1 + radians, amount4 + bbWidth2);
            poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-mainCamera.yRot()));
            poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(mainCamera.xRot()));
            Matrix4f pose = poseStack.last().pose();
            Color color = Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.computeColor((int)(180 * value2)), amount / (i + 1));
            float value3 = 0.12F;
            ParticleType firefly = ParticleType.FIREFLY;
            meshBuilder.computeMeshBuilder(pose, -value3, value3, 0.0F).computeMeshBuilder(firefly.getAmount(), firefly.getAmount4()).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(pose, value3, value3, 0.0F).computeMeshBuilder(firefly.getAmount3(), firefly.getAmount4()).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(pose, value3, -value3, 0.0F).computeMeshBuilder(firefly.getAmount3(), firefly.getAmount2()).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(pose, -value3, -value3, 0.0F).computeMeshBuilder(firefly.getAmount(), firefly.getAmount2()).fetchAlpha(color);
         }
      }

      matrix.pushPose();
      matrix.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-mainCamera.yRot() + 180.0F));
      matrix.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-mainCamera.xRot()));
      MeshBuilder.defaultImpl(
         meshBuilder,
         hasLineOfSight ? Pipelines.INSTANCE.getRenderPipeline19() : Pipelines.INSTANCE.getRenderPipeline18(),
         null,
         null,
         matrix.last().pose(),
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
         null,
         86,
         null
      );
      matrix.popPose();
   }

   private final void applyMainRenderTarget() {
      if (mainTarget == null) {
         mainTarget = new MainTarget(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      if (MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width != mainTarget.width
         || MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height != mainTarget.height) {
         mainTarget.resize(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      RenderSystem.getDevice()
         .createCommandEncoder()
         .clearColorAndDepthTextures(mainTarget.getColorTexture(), (Vector4fc)(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F)), mainTarget.getDepthTexture(), 1.0);
   }

   @Flow
   private final void onFrameBuffer(FrameBufferEvent frameBufferEvent) {
      if (mainTarget != null && flag) {
         frameBufferEvent.setFrameBuffer((RenderTarget)mainTarget);
         frameBufferEvent.cancel();
      }
   }
}