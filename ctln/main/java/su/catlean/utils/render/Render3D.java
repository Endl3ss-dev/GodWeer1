// deobf by pusia
package su.catlean.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.ranges.RangesKt;
import net.minecraft.client.Camera;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector4d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL32C;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.render.FrustrumEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.client.features.modules.esp.LogoutSpots;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.render.BoxFillCommand;
import su.catlean.client.render.BoxGradientCommand;
import su.catlean.client.render.GradientLine;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.WorldRenderer;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.render.vertex.PolygonVertexConsumer;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.WidgetManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class Render3D {
   @NotNull
   public static final Render3D INSTANCE = new Render3D();
   @NotNull
   private static Matrix4f matrix4f = new Matrix4f();
   @NotNull
   private static Frustum frustum = new Frustum((Matrix4fc)(new Matrix4f()), new Matrix4f());
   private static float amount;
   private static float amount2;
   private static float amount3;

   private Render3D() {
   }

   @NotNull
   public final Matrix4f getMatrix4f() {
      return matrix4f;
   }

   @NotNull
   public final Frustum getFrustum() {
      return frustum;
   }

   public final float getAmount() {
      return amount;
   }

   public final void setAmount(float amount) {
      Render3D.amount = amount;
   }

   public final float getAmount2() {
      return amount2;
   }

   public final void setAmount2(float amount) {
      amount2 = amount;
   }

   public final float getAmount3() {
      return amount3;
   }

   public final void setAmount3(float amount) {
      amount3 = amount;
   }

   @Flow
   private final void setFrustum(FrustrumEvent frustrumEvent) {
      frustum = frustrumEvent.getFrustrum();
   }

   @Flow(
      priority = 20
   )
   private final void onRender3D(Render3DEvent render3DEvent) {
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      amount = Math.toRadians(mainCamera.yRot());
      amount2 = Math.toRadians(mainCamera.yRot() + 180.0F);
      amount3 = Math.toRadians(mainCamera.xRot());
      GofraState.INSTANCE.setModifyBuffer(false);
      Render2D.INSTANCE.getMatrix4f().set((Matrix4fc)RenderSystem.getModelViewMatrixCopy());
      Render2D.INSTANCE.getMatrix4f2().set((Matrix4fc)render3DEvent.getStack().last().pose());
      GL32C.glGetIntegerv(2978, Render2D.INSTANCE.getValues());
      matrix4f.set((Matrix4fc)render3DEvent.getProjection());
      ThemeManager.INSTANCE.update();
      if (WidgetManager.INSTANCE.getWidget2() == null && ClickGuiScreen.INSTANCE.isFlag() && GLFW.glfwGetPlatform() != 393219) {
         GLFW.glfwSetCursor(MinecraftHolder.getMinecraft().getWindow().handle(), GLFW.glfwCreateStandardCursor(221185));
      }
   }

   public final void applyDistanceTo(String text, Vec3 pos, Color color, float amount, float amount2, boolean flag, FontRenderer font) {
      Vector4d vector4d = Render2D.INSTANCE.computeVector4d(pos);
      if (vector4d != null) {
         Matrix3x2fStack matrix3x2fStack = new Matrix3x2fStack(8);
         matrix3x2fStack.translate((float)vector4d.x, (float)vector4d.y);
         matrix3x2fStack.scale(
            1.0F
               - ((Number)RangesKt.coerceIn(
                        (float)LogoutSpots.INSTANCE.computeVec3(MinecraftHolder.getMinecraft().getCameraEntity()).distanceTo(pos),
                        RangesKt.rangeTo(1.0F, 10.0F)
                     ))
                     .floatValue()
                  / 10.0F
         );
         if (flag) {
            font.render(matrix3x2fStack, text, amount + 1.0F, amount2 + 1.0F, Render2D.INSTANCE.computeColor(Color.BLACK, Render2D.INSTANCE.fetchAlpha(color)));
         }

         font.render(matrix3x2fStack, text, amount, amount2, color);
      }
   }

   public static void defaultImpl(
      Render3D render3D, String text, Vec3 vec3, Color color, float amount, float amount2, boolean flag, FontRenderer fontRenderer, int value, Object object
   ) {
      if ((value & 8) != 0) {
         amount = 0.0F;
      }

      if ((value & 16) != 0) {
         amount2 = 0.0F;
      }

      if ((value & 32) != 0) {
         flag = false;
      }

      if ((value & 64) != 0) {
         fontRenderer = Fonts.getFontRenderer7();
      }

      render3D.applyDistanceTo(text, vec3, color, amount, amount2, flag, fontRenderer);
   }

   public final float getAmount4() {
      return MinecraftHolder.getMinecraft().getDeltaTracker().getGameTimeDeltaPartialTick(true);
   }

   public final boolean testAABB(AABB box, Color color, Color color2) {
      return WorldRenderer.INSTANCE.getItems2().add(new BoxGradientCommand(box, color, color2));
   }

   public static boolean defaultImpl(Render3D render3D, AABB aABB, Color color, Color color2, int value, Object object) {
      if ((value & 4) != 0) {
         color2 = color;
      }

      return render3D.testAABB(aABB, color, color2);
   }

   public final boolean testAABB2(AABB box, Color color, Color color2) {
      return WorldRenderer.INSTANCE.getItems().add(new BoxGradientCommand(box, color, color2));
   }

   public static boolean defaultImpl2(Render3D render3D, AABB aABB, Color color, Color color2, int value, Object object) {
      if ((value & 4) != 0) {
         color2 = color;
      }

      return render3D.testAABB2(aABB, color, color2);
   }

   public final boolean testAABB3(AABB box, Color color, Color color2) {
      return WorldRenderer.INSTANCE.getItems3().add(new BoxGradientCommand(box, color, color2));
   }

   public static boolean defaultImpl3(Render3D render3D, AABB aABB, Color color, Color color2, int value, Object object) {
      if ((value & 4) != 0) {
         color2 = color;
      }

      return render3D.testAABB3(aABB, color, color2);
   }

   public final boolean testAABB(AABB box, Direction side, Color color) {
      return WorldRenderer.INSTANCE.getItems4().add(new BoxFillCommand(box, color, side));
   }

   public final boolean testAABB2(AABB box, Direction side, Color color) {
      return WorldRenderer.INSTANCE.getItems5().add(new BoxFillCommand(box, color, side));
   }

   public final boolean testVec3(Vec3 start, Vec3 end, Color startColor, Color endColor, boolean flag) {
      return WorldRenderer.INSTANCE.getItems6().add(new GradientLine(start, end, startColor, endColor, flag));
   }

   public static boolean defaultImpl(Render3D render3D, Vec3 vec3, Vec3 vec32, Color color, Color color2, boolean flag, int value, Object object) {
      if ((value & 8) != 0) {
         color2 = color;
      }

      if ((value & 16) != 0) {
         flag = false;
      }

      return render3D.testVec3(vec3, vec32, color, color2, flag);
   }

   public final boolean testVec3(Vec3 start, Vec3 end, Color startColor, Color endColor) {
      return WorldRenderer.INSTANCE.getItems7().add(new GradientLine(start, end, startColor, endColor, false, 16, null));
   }

   public static boolean defaultImpl(Render3D render3D, Vec3 vec3, Vec3 vec32, Color color, Color color2, int value, Object object) {
      if ((value & 8) != 0) {
         color2 = color;
      }

      return render3D.testVec3(vec3, vec32, color, color2);
   }

   @NotNull
   public final AABB computeAABB(AABB from, AABB to, float amount) {
      return new AABB(
         Render2D.INSTANCE.computeAmount(from.minX, to.minX, amount),
         Render2D.INSTANCE.computeAmount(from.minY, to.minY, amount),
         Render2D.INSTANCE.computeAmount(from.minZ, to.minZ, amount),
         Render2D.INSTANCE.computeAmount(from.maxX, to.maxX, amount),
         Render2D.INSTANCE.computeAmount(from.maxY, to.maxY, amount),
         Render2D.INSTANCE.computeAmount(from.maxZ, to.maxZ, amount)
      );
   }

   public static AABB defaultImpl(Render3D render3D, AABB aABB, AABB aABB2, float amount, int value, Object object) {
      if ((value & 4) != 0) {
         amount = render3D.getAmount4();
      }

      return render3D.computeAABB(aABB, aABB2, amount);
   }

   @NotNull
   public final Vec3 computeVec3(Vec3 from, Vec3 to, float amount) {
      return new Vec3(
         Render2D.INSTANCE.computeAmount(from.x, to.x, (double)amount),
         Render2D.INSTANCE.computeAmount(from.y, to.y, (double)amount),
         Render2D.INSTANCE.computeAmount(from.z, to.z, (double)amount)
      );
   }

   public static Vec3 defaultImpl(Render3D render3D, Vec3 vec3, Vec3 vec32, float amount, int value, Object object) {
      if ((value & 4) != 0) {
         amount = render3D.getAmount4();
      }

      return render3D.computeVec3(vec3, vec32, amount);
   }

   private final AABB fetchX(Entity entity) {
      double amount4 = Render2D.INSTANCE.computeAmount(entity.xo, entity.getX());
      double amount5 = Render2D.INSTANCE.computeAmount(entity.yo, entity.getY());
      double amount6 = Render2D.INSTANCE.computeAmount(entity.zo, entity.getZ());
      AABB boundingBox = entity.getBoundingBox();
      return new AABB(
         boundingBox.minX - entity.getX() + amount4 - 0.05,
         boundingBox.minY - entity.getY() + amount5,
         boundingBox.minZ - entity.getZ() + amount6 - 0.05,
         boundingBox.maxX - entity.getX() + amount4 + 0.05,
         boundingBox.maxY - entity.getY() + amount5 + 0.15,
         boundingBox.maxZ - entity.getZ() + amount6 + 0.05
      );
   }

   public final void applyMulPose(
      Identifier texture,
      Vec3 pos,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      Color color,
      boolean flag,
      MeshBuilder meshBuilder,
      float amount5,
      float amount6,
      float amount7,
      float amount8,
      boolean flag2
   ) {
      MeshBuilder meshBuilder2 = meshBuilder;
      boolean flag3 = meshBuilder != null;
      PoseStack poseStack = new PoseStack();
      if (!flag3) {
         meshBuilder2 = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 0, false, 6, null);
      }

      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      Vec3 subtract = pos.subtract(mainCamera.position());
      if (flag2) {
         poseStack.translate(subtract.x, subtract.y, subtract.z);
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(amount));
         poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(amount2));
      } else {
         poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(mainCamera.xRot()));
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(mainCamera.yRot() + amount + 180.0F));
         poseStack.translate(subtract.x, subtract.y, subtract.z);
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-(mainCamera.yRot() + amount)));
         if (amount2 == 0.0F) {
            poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(mainCamera.xRot()));
         } else {
            poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(amount2));
         }
      }

      Matrix4f pose = poseStack.last().pose();
      poseStack.translate(-amount4 / 2.0, -amount4 / 2.0, -0.01);
      GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(texture).getTextureView();
      if (flag3) {
         meshBuilder2.computeMeshBuilder(pose, 0.0F, amount4, 0.0F).computeMeshBuilder(amount5, amount8).fetchAlpha(color);
         meshBuilder2.computeMeshBuilder(pose, amount4, amount4, 0.0F).computeMeshBuilder(amount7, amount8).fetchAlpha(color);
         meshBuilder2.computeMeshBuilder(pose, amount4, 0.0F, 0.0F).computeMeshBuilder(amount7, amount6).fetchAlpha(color);
         meshBuilder2.computeMeshBuilder(pose, 0.0F, 0.0F, 0.0F).computeMeshBuilder(amount5, amount6).fetchAlpha(color);
      } else {
         MeshBuilder.defaultImpl(meshBuilder2, 0.0F, amount4, 0.0F, 4, null).computeMeshBuilder(amount5, amount8).fetchAlpha(color);
         MeshBuilder.defaultImpl(meshBuilder2, amount4, amount4, 0.0F, 4, null).computeMeshBuilder(amount7, amount8).fetchAlpha(color);
         MeshBuilder.defaultImpl(meshBuilder2, amount4, 0.0F, 0.0F, 4, null).computeMeshBuilder(amount7, amount6).fetchAlpha(color);
         MeshBuilder.defaultImpl(meshBuilder2, 0.0F, 0.0F, 0.0F, 4, null).computeMeshBuilder(amount5, amount6).fetchAlpha(color);
         MeshBuilder.defaultImpl(
            meshBuilder2,
            flag ? Pipelines.INSTANCE.getRenderPipeline18() : Pipelines.INSTANCE.getRenderPipeline14(),
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
            null,
            pose,
            null,
            MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
            null,
            84,
            null
         );
      }
   }

   public static void defaultImpl(
      Render3D render3D,
      Identifier identifier,
      Vec3 vec3,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      Color color,
      boolean flag,
      MeshBuilder meshBuilder,
      float amount5,
      float amount6,
      float amount7,
      float amount8,
      boolean flag2,
      int value,
      Object object
   ) {
      if ((value & 128) != 0) {
         flag = true;
      }

      if ((value & 256) != 0) {
         meshBuilder = null;
      }

      if ((value & 512) != 0) {
         amount5 = 0.0F;
      }

      if ((value & 1024) != 0) {
         amount6 = 0.0F;
      }

      if ((value & 2048) != 0) {
         amount7 = 1.0F;
      }

      if ((value & 4096) != 0) {
         amount8 = 1.0F;
      }

      if ((value & 8192) != 0) {
         flag2 = false;
      }

      render3D.applyMulPose(identifier, vec3, amount, amount2, amount3, amount4, color, flag, meshBuilder, amount5, amount6, amount7, amount8, flag2);
   }

   public final void render(PoseStack stack, Vec3 pos, Player entity, float amount, boolean flag) {
      GpuTextureView textureView = MinecraftHolder.getMinecraft()
         .getTextureManager()
         .getTexture(((AbstractClientPlayer)entity).getSkin().body().texturePath())
         .getTextureView();
      Color color = amount == 1.0F ? new Color(0.8F, 0.8F, 0.8F, amount) : Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), amount);
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 256, false, 4, null);
      PolygonVertexConsumer polygonVertexConsumer = new PolygonVertexConsumer(meshBuilder, color);
      PlayerModel model = (PlayerModel)((AvatarRenderer)MinecraftHolder.getMinecraft().getEntityRenderDispatcher().getRenderer((Entity)entity)).getModel();
      AvatarRenderState renderState = (AvatarRenderState)MinecraftHolder.getMinecraft()
         .getEntityRenderDispatcher()
         .getRenderer((Entity)entity)
         .createRenderState((Entity)entity, this.getAmount4());
      stack.pushPose();
      double value = pos.x - MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position().x();
      double value2 = pos.y - MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position().y();
      double value3 = pos.z - MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position().z();
      stack.translate((float)value, (float)value2, (float)value3);
      stack.scale(renderState.scale, renderState.scale, renderState.scale);
      LogoutSpots.INSTANCE.render(renderState, stack, renderState.bodyRot);
      stack.scale(-1.0F, -1.0F, 1.0F);
      stack.scale(0.9375F, 0.9375F, 0.9375F);
      stack.translate(0.0F, -1.501F, 0.0F);
      model.setupAnim(renderState);
      model.renderToBuffer(stack, polygonVertexConsumer, 15728880, 0, color.getRGB());
      MeshBuilder.defaultImpl(
         meshBuilder,
         flag ? Pipelines.INSTANCE.getRenderPipeline17() : Pipelines.INSTANCE.getRenderPipeline16(),
         null,
         null,
         new PoseStack().last().pose(),
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
         null,
         86,
         null
      );
      stack.popPose();
   }

   public static void defaultImpl(Render3D render3D, PoseStack poseStack, Vec3 vec3, Player player, float amount, boolean flag, int value, Object object) {
      if ((value & 16) != 0) {
         flag = false;
      }

      render3D.render(poseStack, vec3, player, amount, flag);
   }

   @NotNull
   public final AABB computeAABB(Entity self) {
      return this.fetchX(self);
   }

   @NotNull
   public final Vec3 fetchEntityRenderDispatcher(Player entity, float amount) {
      Vec3 position = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position();
      double amount4 = entity.xo + (entity.getX() - entity.xo) * amount - position.x;
      double amount5 = entity.yo + (entity.getY() - entity.yo) * amount - position.y;
      double amount6 = entity.zo + (entity.getZ() - entity.zo) * amount - position.z;
      return new Vec3(amount4, amount5, amount6);
   }

   public static Vec3 defaultImpl(Render3D render3D, Player player, float amount, int value, Object object) {
      if ((value & 2) != 0) {
         amount = render3D.getAmount4();
      }

      return render3D.fetchEntityRenderDispatcher(player, amount);
   }
}