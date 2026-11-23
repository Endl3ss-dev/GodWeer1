// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.lwjgl.opengl.GL32C;
import su.catlean.client.features.modules.world.FreeCam;
import su.catlean.client.services.RotationManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class ModelEspRenderer {
   @NotNull
   public static final ModelEspRenderer INSTANCE = new ModelEspRenderer();

   private ModelEspRenderer() {
   }

   public final void render(PoseStack stack, Color lineColor, Color friendLineColor, boolean flag) {
      Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
      var arrayList = new ArrayList();

      for (Object object : entitiesForRendering) {
         if (object instanceof Player) {
            arrayList.add(object);
         }
      }

      List list = arrayList;
      if (!list.isEmpty()) {
         float amount2 = Render2D.INSTANCE.getAmount2();
         VertexFormat positioN_COLOR = DefaultVertexFormat.POSITION_COLOR;
         int size = 9 * list.size();
         boolean flag3 = true;
         int value = size;
         VertexFormat vertexFormat = positioN_COLOR;
         MeshBuilder meshBuilder = new MeshBuilder(vertexFormat, value, flag3);
         Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();

         for (Object object2 : list) {
            Player player = (Player)object2;
            if (MinecraftHolder.getMinecraft().options.getCameraType() != CameraType.FIRST_PERSON
               || FreeCam.INSTANCE.getEnabled()
               || !Intrinsics.areEqual(MinecraftHolder.getMinecraft().player, player)) {
               stack.pushPose();
               stack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-mainCamera.yRot() + 180.0F));
               stack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-mainCamera.xRot()));
               Color friendLineColor2 = PlayerNameUtil.checkGetGameProfile(player) ? friendLineColor : lineColor;
               Vec3 defaultImpl = Render3D.defaultImpl(Render3D.INSTANCE, player, 0.0F, 2, null);
               PlayerModel model = (PlayerModel)((AvatarRenderer)MinecraftHolder.getMinecraft().getEntityRenderDispatcher().getRenderer((Entity)player))
                  .getModel();
               EntityRenderState renderState = MinecraftHolder.getMinecraft()
                  .getEntityRenderDispatcher()
                  .getRenderer((Entity)player)
                  .createRenderState((Entity)player, Render3D.INSTANCE.getAmount4());
               String text = "null cannot be cast to non-null type net.minecraft.client.renderer.entity.state.AvatarRenderState";
               AvatarRenderState avatarRenderState = (AvatarRenderState)renderState;
               float wrapDegrees = Intrinsics.areEqual(player, MinecraftHolder.getLocalPlayer())
                     && RotationManager.INSTANCE.getRotation4() != null
                     && RotationManager.INSTANCE.getRotation3() != null
                  ? Mth.wrapDegrees(
                     Render2D.INSTANCE
                        .computeAmount(
                           RotationManager.INSTANCE.getRotation4().getAmount(),
                           RotationManager.INSTANCE.getRotation3().getAmount(),
                           Render3D.INSTANCE.getAmount4()
                        )
                  )
                  : Mth.rotLerp(amount2, player.yBodyRotO, player.yBodyRot);
               float amount5 = Intrinsics.areEqual(player, MinecraftHolder.getLocalPlayer()) ? WorldUtil.INSTANCE.getAmount5() : player.getViewXRot(amount2);
               model.setupAnim(avatarRenderState);
               boolean isVisuallySwimming = player.isVisuallySwimming();
               boolean isShiftKeyDown = player.isShiftKeyDown();
               boolean isFallFlying = player.isFallFlying();
               stack.translate(defaultImpl.x, defaultImpl.y, defaultImpl.z);
               if (isVisuallySwimming) {
                  stack.translate(0.0F, 0.35F, 0.0F);
               }

               stack.mulPose((Quaternionfc)Axis.YN.rotationDegrees(wrapDegrees + 180));
               if (isVisuallySwimming || isFallFlying) {
                  stack.mulPose((Quaternionfc)Axis.XN.rotationDegrees(80 + amount5));
               }

               if (isVisuallySwimming) {
                  stack.translate(0.0F, -0.95F, 0.0F);
               }

               Matrix4f pose = stack.last().pose();
               float value2 = isShiftKeyDown ? 0.6F : 0.7F;
               float value3 = isShiftKeyDown ? 0.23F : 0.0F;
               float value4 = value2;
               MeshBuilder alpha = meshBuilder.computeMeshBuilder(pose, 0.0F, value4, value3).fetchAlpha(friendLineColor2);
               Matrix4f pose2 = stack.last().pose();
               float value5 = isShiftKeyDown ? 1.05F : 1.4F;
               float value6 = 0.0F;
               float value7 = value5;
               alpha = alpha.computeMeshBuilder(pose2, 0.0F, value7, value6).fetchAlpha(friendLineColor2);
               Matrix4f pose3 = stack.last().pose();
               float value8 = isShiftKeyDown ? 1.05F : 1.35F;
               float value9 = 0.0F;
               float value10 = value8;
               alpha = alpha.computeMeshBuilder(pose3, -0.37F, value10, value9).fetchAlpha(friendLineColor2);
               Matrix4f pose4 = stack.last().pose();
               float value11 = isShiftKeyDown ? 1.05F : 1.35F;
               float value12 = 0.0F;
               float value13 = value11;
               alpha = alpha.computeMeshBuilder(pose4, 0.37F, value13, value12).fetchAlpha(friendLineColor2);
               Matrix4f pose5 = stack.last().pose();
               float value14 = isShiftKeyDown ? 0.6F : 0.7F;
               float value15 = isShiftKeyDown ? 0.23F : 0.0F;
               float value16 = value14;
               alpha = alpha.computeMeshBuilder(pose5, -0.15F, value16, value15).fetchAlpha(friendLineColor2);
               Matrix4f pose6 = stack.last().pose();
               float value17 = isShiftKeyDown ? 0.6F : 0.7F;
               float value18 = isShiftKeyDown ? 0.23F : 0.0F;
               float value19 = value17;
               alpha.computeMeshBuilder(pose6, 0.15F, value19, value18).fetchAlpha(friendLineColor2);
               stack.pushPose();
               stack.translate(0.0F, isShiftKeyDown ? 1.05F : 1.4F, 0.0F);
               float value20 = 0.3F;
               ModelPart head = model.head;
               INSTANCE.render(head, value20, stack, friendLineColor2, meshBuilder);
               if (flag) {
                  stack.pushPose();
                  stack.translate(0.0F, isShiftKeyDown ? 0.5F : 0.7F, 0.0F);
                  stack.mulPose((Quaternionfc)Axis.ZP.rotation(model.head.zRot));
                  stack.mulPose((Quaternionfc)Axis.YN.rotation(model.head.yRot));
                  stack.mulPose((Quaternionfc)Axis.XN.rotation(1.1F));
                  meshBuilder.computeMeshBuilder(stack.last().pose(), 0.0F, 0.0F, 0.0F)
                     .fetchAlpha(friendLineColor2)
                     .computeMeshBuilder(stack.last().pose(), 0.0F, 0.3F, 0.0F)
                     .fetchAlpha(friendLineColor2);
                  stack.popPose();
               }

               stack.pushPose();
               stack.translate(0.15F, isShiftKeyDown ? 0.6F : 0.7F, isShiftKeyDown ? 0.23F : 0.0F);
               value20 = -0.6F;
               head = model.rightLeg;
               INSTANCE.render(head, value20, stack, friendLineColor2, meshBuilder);
               stack.pushPose();
               stack.translate(-0.15F, isShiftKeyDown ? 0.6F : 0.7F, isShiftKeyDown ? 0.23F : 0.0F);
               value20 = -0.6F;
               head = model.leftLeg;
               INSTANCE.render(head, value20, stack, friendLineColor2, meshBuilder);
               stack.pushPose();
               stack.translate(0.37F, isShiftKeyDown ? 1.05F : 1.35F, 0.0F);
               value20 = -0.55F;
               head = model.rightArm;
               INSTANCE.render(head, value20, stack, friendLineColor2, meshBuilder);
               stack.pushPose();
               stack.translate(-0.37F, isShiftKeyDown ? 1.05F : 1.35F, 0.0F);
               value20 = -0.55F;
               head = model.leftArm;
               INSTANCE.render(head, value20, stack, friendLineColor2, meshBuilder);
               if (isVisuallySwimming) {
                  stack.translate(0.0F, 0.95F, 0.0F);
               }

               if (isVisuallySwimming || isFallFlying) {
                  stack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(90 + amount5));
               }

               if (isVisuallySwimming) {
                  stack.translate(0.0F, -0.35F, 0.0F);
               }

               stack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(wrapDegrees + 180));
               stack.translate(-defaultImpl.x, -defaultImpl.y, -defaultImpl.z);
               stack.popPose();
            }
         }

         GL32C.glEnable(2848);
         GL32C.glHint(3154, 4354);
         MeshBuilder.defaultImpl(meshBuilder, Pipelines.INSTANCE.getRenderPipeline25(), null, null, stack.last().pose(), null, null, null, 118, null);
         GL32C.glDisable(2848);
      }
   }

   private final void render(ModelPart modelPart, float amount, PoseStack poseStack, Color color, MeshBuilder meshBuilder) {
      this.render(poseStack, modelPart);
      meshBuilder.computeMeshBuilder(poseStack.last().pose(), 0.0F, 0.0F, 0.0F)
         .fetchAlpha(color)
         .computeMeshBuilder(poseStack.last().pose(), 0.0F, amount, 0.0F)
         .fetchAlpha(color);
      poseStack.popPose();
   }

   private final void render(PoseStack poseStack, ModelPart modelPart) {
      if (modelPart.zRot != 0.0F) {
         poseStack.mulPose((Quaternionfc)Axis.ZP.rotation(modelPart.zRot));
      }

      if (modelPart.yRot != 0.0F) {
         poseStack.mulPose((Quaternionfc)Axis.YN.rotation(modelPart.yRot));
      }

      if (modelPart.xRot != 0.0F) {
         poseStack.mulPose((Quaternionfc)Axis.XN.rotation(modelPart.xRot));
      }
   }
}