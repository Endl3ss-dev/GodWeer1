// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL32C;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.client.features.modules.esp.StorageEsp;
import su.catlean.client.features.modules.misc.ClientInterface;
import su.catlean.client.render.uniforms.UniformBlockI;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class WorldRenderer {
   @NotNull
   public static final WorldRenderer INSTANCE = new WorldRenderer();
   @NotNull
   private static List items = new ArrayList();
   @NotNull
   private static List items2 = new ArrayList();
   @NotNull
   private static List items3 = new ArrayList();
   @NotNull
   private static List items4 = new ArrayList();
   @NotNull
   private static List items5 = new ArrayList();
   @NotNull
   private static List items6 = new ArrayList();
   @NotNull
   private static List items7 = new ArrayList();
   @Nullable
   private static MainTarget mainTarget;
   @Nullable
   private static MainTarget mainTarget2;
   private static boolean flag;
   private static boolean flag2;

   private WorldRenderer() {
   }

   @NotNull
   public final List getItems() {
      return items;
   }

   @NotNull
   public final List getItems2() {
      return items2;
   }

   @NotNull
   public final List getItems3() {
      return items3;
   }

   @NotNull
   public final List getItems4() {
      return items4;
   }

   @NotNull
   public final List getItems5() {
      return items5;
   }

   @NotNull
   public final List getItems6() {
      return items6;
   }

   @NotNull
   public final List getItems7() {
      return items7;
   }

   @Flow(
      priority = -10
   )
   private final void onRender3D(Render3DEvent render3DEvent) {
      BoxRenderCommand.INSTANCE.render(render3DEvent.getStack());
      this.render2(render3DEvent.getStack());
      this.render4(render3DEvent.getStack());
      this.render3(render3DEvent.getStack());
      this.render(render3DEvent.getStack());
      items.clear();
      items2.clear();
      items3.clear();
      items4.clear();
      items5.clear();
      items6.clear();
      items7.clear();
   }

   private final void render(PoseStack poseStack) {
      if (!items2.isEmpty()) {
         this.applyMainRenderTarget();
         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, items2.size() * 6, false, 4, null);

         for (Object items2Item : items2) {
            BoxGradientCommand boxGradientCommand = (BoxGradientCommand)items2Item;
            INSTANCE.applyGetEntityRenderDispatcher(meshBuilder, boxGradientCommand.getBox(), boxGradientCommand.getColor(), boxGradientCommand.getColor());
         }

         MeshBuilder.defaultImpl(
            meshBuilder, Pipelines.INSTANCE.getRenderPipeline24(), (RenderTarget)mainTarget, null, poseStack.last().pose(), null, null, null, 116, null
         );
         flag = true;
      }
   }

   private final void render2(PoseStack poseStack) {
      if (!items.isEmpty() || !items4.isEmpty()) {
         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, items.size() * 6 + items4.size(), false, 4, null);

         for (Object item : items) {
            BoxGradientCommand boxGradientCommand = (BoxGradientCommand)item;
            INSTANCE.applyGetEntityRenderDispatcher(meshBuilder, boxGradientCommand.getBox(), boxGradientCommand.getColor(), boxGradientCommand.getColor2());
         }

         for (Object items4Item : items4) {
            BoxFillCommand boxFillCommand = (BoxFillCommand)items4Item;
            INSTANCE.applyGetEntityRenderDispatcher2(boxFillCommand.getBox(), meshBuilder, boxFillCommand.getColor(), boxFillCommand.getSide());
         }

         MeshBuilder.defaultImpl(meshBuilder, Pipelines.INSTANCE.getRenderPipeline22(), null, null, poseStack.last().pose(), null, null, null, 118, null);
      }
   }

   private final void render3(PoseStack poseStack) {
      if (!items7.isEmpty()) {
         GL32C.glEnable(2848);
         GL32C.glHint(3154, 4354);
         VertexFormat positioN_COLOR = DefaultVertexFormat.POSITION_COLOR;
         int size = items7.size();
         boolean flag3 = true;
         int value = size;
         VertexFormat vertexFormat = positioN_COLOR;
         MeshBuilder meshBuilder = new MeshBuilder(vertexFormat, value, flag3);

         for (Object items7Item : items7) {
            GradientLine gradientLine = (GradientLine)items7Item;
            defaultImpl(INSTANCE, meshBuilder, gradientLine.getStart(), gradientLine.getEnd(), gradientLine.getStartColor(), gradientLine.getEndColor(), false, 32, null);
         }

         MeshBuilder.defaultImpl(
            meshBuilder,
            Pipelines.INSTANCE.getRenderPipeline26(),
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
            null,
            poseStack.last().pose(),
            null,
            null,
            null,
            116,
            null
         );
         GL32C.glDisable(2848);
      }
   }

   private final void render4(PoseStack poseStack) {
      if (!items3.isEmpty() || !items5.isEmpty() || !items6.isEmpty()) {
         if (ClientInterface.INSTANCE.getLinesMode() == ClientInterface.LinesMode.NEW) {
            this.applyMainRenderTarget2();
         }

         GL32C.glEnable(2848);
         GL32C.glHint(3154, 4354);
         VertexFormat positioN_COLOR = DefaultVertexFormat.POSITION_COLOR;
         int size = items6.size() + items5.size() * 4 + items3.size() * 12;
         boolean flag3 = true;
         int value = size;
         VertexFormat vertexFormat = positioN_COLOR;
         MeshBuilder meshBuilder = new MeshBuilder(vertexFormat, value, flag3);

         for (Object items3Item : items3) {
            BoxGradientCommand boxGradientCommand = (BoxGradientCommand)items3Item;
            INSTANCE.applyGetEntityRenderDispatcher(boxGradientCommand.getBox(), meshBuilder, boxGradientCommand.getColor(), boxGradientCommand.getColor2());
         }

         for (Object items5Item : items5) {
            BoxFillCommand boxFillCommand = (BoxFillCommand)items5Item;
            INSTANCE.applyGetEntityRenderDispatcher(boxFillCommand.getBox(), meshBuilder, boxFillCommand.getColor(), boxFillCommand.getSide());
         }

         for (Object items6Item : items6) {
            GradientLine gradientLine = (GradientLine)items6Item;
            INSTANCE.applyGetEntityRenderDispatcher(meshBuilder, gradientLine.getStart(), gradientLine.getEnd(), gradientLine.getStartColor(), gradientLine.getEndColor(), gradientLine.isFlag());
         }

         RenderPipeline renderPipeline25 = Pipelines.INSTANCE.getRenderPipeline25();
         RenderTarget mainRenderTarget = ClientInterface.INSTANCE.getLinesMode() == ClientInterface.LinesMode.OLD
            ? MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget()
            : (RenderTarget)mainTarget2;
         MeshBuilder.defaultImpl(meshBuilder, renderPipeline25, mainRenderTarget, null, poseStack.last().pose(), null, null, null, 116, null);
         GL32C.glDisable(2848);
         flag2 = ClientInterface.INSTANCE.getLinesMode() == ClientInterface.LinesMode.NEW;
      }
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      if (mainTarget != null && flag) {
         MeshBuilder.defaultImpl(
            new MeshBuilder(null, 0, false, 7, null).getMeshBuilder(),
            Pipelines.INSTANCE.getRenderPipeline6(),
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
            UniformBlockI.INSTANCE
               .getDynamicUniformStorage()
               .writeUniform(new WorldRenderer.UniformData(new Vec2(1.0F / mainTarget.width, 1.0F / mainTarget.height))),
            null,
            null,
            MapsKt.mapOf(TuplesKt.to("Sampler0", mainTarget.getColorTextureView())),
            null,
            88,
            null
         );
         flag = false;
      }

      if (mainTarget2 != null && flag2) {
         MeshBuilder.defaultImpl(
            new MeshBuilder(null, 0, false, 7, null).getMeshBuilder(),
            Pipelines.INSTANCE.getRenderPipeline7(),
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
            UniformBlockI.INSTANCE
               .getDynamicUniformStorage()
               .writeUniform(new WorldRenderer.UniformData(new Vec2(1.0F / mainTarget2.width, 1.0F / mainTarget2.height))),
            null,
            null,
            MapsKt.mapOf(TuplesKt.to("Sampler0", mainTarget2.getColorTextureView())),
            null,
            88,
            null
         );
         flag2 = false;
      }
   }

   private final void applyGetEntityRenderDispatcher(MeshBuilder meshBuilder, Vec3 vec3, Vec3 vec32, Color color, Color color2, boolean flag) {
      if (flag || Render3D.INSTANCE.getFrustum().pointInFrustum(vec32.x, vec32.y, vec32.z)) {
         Vec3 position = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position();
         float value = (float)(vec3.x - position.x());
         float value2 = (float)(vec3.y - position.y());
         float value3 = (float)(vec3.z - position.z());
         float value4 = (float)(vec32.x - position.x());
         float value5 = (float)(vec32.y - position.y());
         float value6 = (float)(vec32.z - position.z());
         meshBuilder.computeMeshBuilder(value, value2, value3).fetchAlpha(color);
         meshBuilder.computeMeshBuilder(value4, value5, value6).fetchAlpha(color2);
      }
   }

   public static void defaultImpl(
      WorldRenderer worldRenderer, MeshBuilder meshBuilder, Vec3 vec3, Vec3 vec32, Color color, Color color2, boolean flag, int value, Object object
   ) {
      if ((value & 16) != 0) {
         color2 = color;
      }

      if ((value & 32) != 0) {
         flag = false;
      }

      worldRenderer.applyGetEntityRenderDispatcher(meshBuilder, vec3, vec32, color, color2, flag);
   }

   private final void applyGetEntityRenderDispatcher(AABB aABB, MeshBuilder meshBuilder, Color color, Color color2) {
      Vec3 position = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position();
      float minX = (float)(aABB.minX - position.x());
      float minY = (float)(aABB.minY - position.y());
      float minZ = (float)(aABB.minZ - position.z());
      float maxX = (float)(aABB.maxX - position.x());
      float maxY = (float)(aABB.maxY - position.y());
      float maxZ = (float)(aABB.maxZ - position.z());
      meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color2);
      meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color2);
      meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color2);
      meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color2);
      meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color2);
      meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color2);
      meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color2);
      meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color2);
      meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color2);
      meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color2);
      meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color2);
      meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color2);
   }

   private final void applyGetEntityRenderDispatcher(AABB aABB, MeshBuilder meshBuilder, Color color, Direction direction) {
      Vec3 position = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position();
      float minX = (float)(aABB.minX - position.x());
      float minY = (float)(aABB.minY - position.y());
      float minZ = (float)(aABB.minZ - position.z());
      float maxX = (float)(aABB.maxX - position.x());
      float maxY = (float)(aABB.maxY - position.y());
      float maxZ = (float)(aABB.maxZ - position.z());
      switch (WorldRenderer.WhenMappings.values[direction.ordinal()]) {
         case 1:
            meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color);
            break;
         case 2:
            meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
            break;
         case 3:
            meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
            break;
         case 4:
            meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
            break;
         case 5:
            meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
            break;
         case 6:
            meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }
   }

   private final void applyGetEntityRenderDispatcher2(AABB aABB, MeshBuilder meshBuilder, Color color, Direction direction) {
      Vec3 position = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position();
      float minX = (float)(aABB.minX - position.x());
      float minY = (float)(aABB.minY - position.y());
      float minZ = (float)(aABB.minZ - position.z());
      float maxX = (float)(aABB.maxX - position.x());
      float maxY = (float)(aABB.maxY - position.y());
      float maxZ = (float)(aABB.maxZ - position.z());
      switch (WorldRenderer.WhenMappings.values[direction.ordinal()]) {
         case 1:
            meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color);
            break;
         case 2:
            meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
            break;
         case 3:
            meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color);
            break;
         case 4:
            meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color);
            break;
         case 5:
            meshBuilder.computeMeshBuilder(minX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(color);
            break;
         case 6:
            meshBuilder.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(color);
            meshBuilder.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(color);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }
   }

   public final void applyGetEntityRenderDispatcher(MeshBuilder polygon, AABB box, Color c, Color c1) {
      if (Render3D.INSTANCE.getFrustum().isVisible(box)) {
         Vec3 position = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position();
         float minX = (float)(box.minX - position.x());
         float minY = (float)(box.minY - position.y());
         float minZ = (float)(box.minZ - position.z());
         float maxX = (float)(box.maxX - position.x());
         float maxY = (float)(box.maxY - position.y());
         float maxZ = (float)(box.maxZ - position.z());
         polygon.computeMeshBuilder(minX, minY, minZ).fetchAlpha(c);
         polygon.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(c);
         polygon.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(c);
         polygon.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(c);
         polygon.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(c);
         polygon.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(c);
         polygon.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(minX, minY, minZ).fetchAlpha(c);
         polygon.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(c);
         polygon.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(minX, maxY, minZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(minX, maxY, maxZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(maxX, maxY, maxZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(maxX, maxY, minZ).fetchAlpha(c1);
         polygon.computeMeshBuilder(minX, minY, minZ).fetchAlpha(c);
         polygon.computeMeshBuilder(minX, minY, maxZ).fetchAlpha(c);
         polygon.computeMeshBuilder(maxX, minY, maxZ).fetchAlpha(c);
         polygon.computeMeshBuilder(maxX, minY, minZ).fetchAlpha(c);
      }
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

   private final void applyMainRenderTarget2() {
      if (mainTarget2 == null) {
         mainTarget2 = new MainTarget(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      if (MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width != mainTarget2.width
         || MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height != mainTarget2.height) {
         mainTarget2.resize(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      RenderSystem.getDevice()
         .createCommandEncoder()
         .clearColorAndDepthTextures(mainTarget2.getColorTexture(), (Vector4fc)(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F)), mainTarget2.getDepthTexture(), 1.0);
   }

   public static final class UniformData implements DynamicUniform {
      @NotNull
      private final Vec2 halfTexelSize;

      public UniformData(Vec2 halfTexelSize) {
         this.halfTexelSize = halfTexelSize;
      }

      @NotNull
      public final Vec2 getHalfTexelSize() {
         return this.halfTexelSize;
      }

      public void write(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer)
            .putVec2(this.halfTexelSize.x, this.halfTexelSize.y)
            .putFloat(
               Math.abs(
                  Render2D.INSTANCE
                     .computeAmount(
                        (MinecraftHolder.getLocalPlayer().tickCount - 1) / 48.0F,
                        MinecraftHolder.getLocalPlayer().tickCount / 48.0F,
                        Render2D.INSTANCE.getAmount2()
                     )
               )
            )
            .putInt(StorageEsp.INSTANCE.getShaderMode().ordinal());
      }

      @NotNull
      public final Vec2 getHalfTexelSize2() {
         return this.halfTexelSize;
      }

      @NotNull
      @Override
      public String toString() {
         return "UniformData(halfTexelSize=" + this.halfTexelSize + ")";
      }

      @Override
      public int hashCode() {
         return this.halfTexelSize.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else {
            return !(object instanceof WorldRenderer.UniformData uniformData) ? false : Intrinsics.areEqual(this.halfTexelSize, uniformData.halfTexelSize);
         }
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[Direction.values().length];

         try {
            values2[Direction.UP.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Direction.DOWN.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[Direction.NORTH.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[Direction.SOUTH.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[Direction.WEST.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         try {
            values2[Direction.EAST.ordinal()] = 6;
         } catch (NoSuchFieldError exception6) {
         }

         values = values2;
      }
   }
}