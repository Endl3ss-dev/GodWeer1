// deobf by pusia
package su.catlean.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
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
import net.minecraft.client.model.object.crystal.EndCrystalModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionfc;
import su.catlean.client.features.modules.cosmetic.CrystalChams;
import su.catlean.client.features.modules.esp.LogoutSpots;
import su.catlean.client.features.modules.esp.PlayerEsp;
import su.catlean.client.render.vertex.ModelVertexConsumer;
import su.catlean.mixins.accessors.EndCrystalRendererAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.EntityPositionUtil;

public final class PlayerEspRenderer {
   @NotNull
   public static final PlayerEspRenderer INSTANCE = new PlayerEspRenderer();

   private PlayerEspRenderer() {
   }

   public final void render(PoseStack stack, PlayerEsp.RenderMode mode, Color fillColor, Color lineColor, int value, int value2) {
      Vec3 vec3;
      label79: {
         Entity cameraEntity = MinecraftHolder.getMinecraft().getCameraEntity();
         if (cameraEntity != null) {
            vec3 = EntityPositionUtil.computeVec3(cameraEntity);
            if (vec3 != null) {
               break label79;
            }
         }

         vec3 = EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer());
      }

      Vec3 vec32 = vec3;
      Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
      boolean flag = false;
      var arrayList = new ArrayList();
      boolean flag2 = false;

      for (Object object : entitiesForRendering) {
         if (object instanceof EndCrystal) {
            arrayList.add(object);
         }
      }

      entitiesForRendering = arrayList;
      flag = false;
      arrayList = new ArrayList();
      flag2 = false;

      for (Object object2 : entitiesForRendering) {
         EndCrystal endCrystal = (EndCrystal)object2;
         if (Math.hypot(vec32.x - endCrystal.getX(), vec32.z - endCrystal.getZ()) < value2 && Math.abs(vec32.y - endCrystal.getY()) < value) {
            arrayList.add(object2);
         }
      }

      List list = arrayList;
      if (!list.isEmpty()) {
         if (mode == PlayerEsp.RenderMode.BOTH || mode == PlayerEsp.RenderMode.FILL) {
            MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, list.size() * 72, false, 4, null);
            Iterable iterable = list;

            for (Object object3 : iterable) {
               EndCrystal endCrystal2 = (EndCrystal)object3;
               INSTANCE.render2(
                  stack,
                  (Entity)endCrystal2,
                  fillColor,
                  PlayerEsp.RenderMode.FILL,
                  meshBuilder,
                  CrystalChams.INSTANCE.computeAmount(endCrystal2.tickCount + Render3D.INSTANCE.getAmount4())
               );
            }

            MeshBuilder.defaultImpl(meshBuilder, Pipelines.INSTANCE.getRenderPipeline22(), null, null, stack.last().pose(), null, null, null, 118, null);
         }

         if (mode == PlayerEsp.RenderMode.BOTH || mode == PlayerEsp.RenderMode.OUTLINE) {
            VertexFormat positioN_COLOR = DefaultVertexFormat.POSITION_COLOR;
            int size = list.size() * 144;
            boolean flag5 = true;
            int value3 = size;
            VertexFormat vertexFormat = positioN_COLOR;
            MeshBuilder meshBuilder2 = new MeshBuilder(vertexFormat, value3, flag5);
            Iterable iterable2 = list;

            for (Object object4 : iterable2) {
               EndCrystal endCrystal3 = (EndCrystal)object4;
               INSTANCE.render2(
                  stack,
                  (Entity)endCrystal3,
                  lineColor,
                  PlayerEsp.RenderMode.OUTLINE,
                  meshBuilder2,
                  CrystalChams.INSTANCE.computeAmount(endCrystal3.tickCount + Render3D.INSTANCE.getAmount4())
               );
            }

            MeshBuilder.defaultImpl(meshBuilder2, Pipelines.INSTANCE.getRenderPipeline25(), null, null, stack.last().pose(), null, null, null, 118, null);
         }
      }
   }

   public final void render(
      PoseStack stack, PlayerEsp.RenderMode mode, Color fillColor, Color lineColor, Color friendFillColor, Color friendLineColor, int value, int value2
   ) {
      Vec3 vec3;
      label86: {
         Entity cameraEntity = MinecraftHolder.getMinecraft().getCameraEntity();
         if (cameraEntity != null) {
            vec3 = EntityPositionUtil.computeVec3(cameraEntity);
            if (vec3 != null) {
               break label86;
            }
         }

         vec3 = EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer());
      }

      Vec3 vec32 = vec3;
      Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
      var arrayList = new ArrayList();

      for (Object object : entitiesForRendering) {
         Entity entity = (Entity)object;
         if (entity instanceof Player
            && (
               !Intrinsics.areEqual(entity, MinecraftHolder.getLocalPlayer())
                  || MinecraftHolder.getMinecraft().options.getCameraType() != CameraType.FIRST_PERSON
            )
            && Math.hypot(vec32.x - ((Player)entity).getX(), vec32.z - ((Player)entity).getZ()) < value2
            && Math.abs(vec32.y - ((Player)entity).getY()) < value) {
            arrayList.add(object);
         }
      }

      List list = arrayList;
      if (!list.isEmpty()) {
         if (mode == PlayerEsp.RenderMode.BOTH || mode == PlayerEsp.RenderMode.FILL) {
            MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, list.size() * 144, false, 4, null);
            Iterable iterable = list;

            for (Object object2 : iterable) {
               Entity entity2 = (Entity)object2;
               PlayerEspRenderer instance = INSTANCE;
               Color friendFillColor2 = PlayerNameUtil.checkGetGameProfile((Player)entity2) ? friendFillColor : fillColor;
               Object object3 = null;
               byte byteValue = 32;
               float value3 = 0.0F;
               PlayerEsp.RenderMode fill = PlayerEsp.RenderMode.FILL;
               Color color = friendFillColor2;
               defaultImpl(instance, stack, entity2, color, fill, meshBuilder, value3, byteValue, object3);
            }

            MeshBuilder.defaultImpl(meshBuilder, Pipelines.INSTANCE.getRenderPipeline22(), null, null, stack.last().pose(), null, null, null, 118, null);
         }

         if (mode == PlayerEsp.RenderMode.BOTH || mode == PlayerEsp.RenderMode.OUTLINE) {
            VertexFormat positioN_COLOR = DefaultVertexFormat.POSITION_COLOR;
            int size = list.size() * 288;
            boolean flag5 = true;
            int value4 = size;
            VertexFormat vertexFormat = positioN_COLOR;
            MeshBuilder meshBuilder2 = new MeshBuilder(vertexFormat, value4, flag5);
            Iterable iterable2 = list;

            for (Object object4 : iterable2) {
               Entity entity3 = (Entity)object4;
               PlayerEspRenderer instance2 = INSTANCE;
               Color friendLineColor2 = PlayerNameUtil.checkGetGameProfile((Player)entity3) ? friendLineColor : lineColor;
               Object object5 = null;
               byte byteValue2 = 32;
               float value5 = 0.0F;
               PlayerEsp.RenderMode outline = PlayerEsp.RenderMode.OUTLINE;
               Color color2 = friendLineColor2;
               defaultImpl(instance2, stack, entity3, color2, outline, meshBuilder2, value5, byteValue2, object5);
            }

            MeshBuilder.defaultImpl(meshBuilder2, Pipelines.INSTANCE.getRenderPipeline25(), null, null, stack.last().pose(), null, null, null, 118, null);
         }
      }
   }

   public final void render(PoseStack stack, PlayerEsp.RenderMode mode, Color fillColor, Color lineColor, ArrayList list, int value, int value2) {
      Vec3 vec3;
      label75: {
         Entity cameraEntity = MinecraftHolder.getMinecraft().getCameraEntity();
         if (cameraEntity != null) {
            vec3 = EntityPositionUtil.computeVec3(cameraEntity);
            if (vec3 != null) {
               break label75;
            }
         }

         vec3 = EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer());
      }

      Vec3 vec32 = vec3;
      Iterable list2 = list;
      var arrayList = new ArrayList();

      for (Object object : list2) {
         Player player = (Player)object;
         if (!Intrinsics.areEqual(player, MinecraftHolder.getLocalPlayer())
            || MinecraftHolder.getMinecraft().options.getCameraType() != CameraType.FIRST_PERSON
               && Math.hypot(vec32.x - player.getX(), vec32.z - player.getZ()) < value2
               && Math.abs(vec32.y - player.getY()) < value) {
            arrayList.add(object);
         }
      }

      List list3 = arrayList;
      if (!list3.isEmpty()) {
         if (mode == PlayerEsp.RenderMode.BOTH || mode == PlayerEsp.RenderMode.FILL) {
            MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, list.size() * 144, false, 4, null);
            Iterable iterable = list3;

            for (Object object2 : iterable) {
               Player player2 = (Player)object2;
               INSTANCE.render(stack, (Entity)player2, fillColor, PlayerEsp.RenderMode.FILL, meshBuilder, 1.0F);
            }

            MeshBuilder.defaultImpl(meshBuilder, Pipelines.INSTANCE.getRenderPipeline22(), null, null, stack.last().pose(), null, null, null, 118, null);
         }

         if (mode == PlayerEsp.RenderMode.BOTH || mode == PlayerEsp.RenderMode.OUTLINE) {
            VertexFormat positioN_COLOR = DefaultVertexFormat.POSITION_COLOR;
            int size = list.size() * 288;
            boolean flag5 = true;
            int value3 = size;
            VertexFormat vertexFormat = positioN_COLOR;
            MeshBuilder meshBuilder2 = new MeshBuilder(vertexFormat, value3, flag5);
            Iterable iterable2 = list3;

            for (Object object3 : iterable2) {
               Player player3 = (Player)object3;
               INSTANCE.render(stack, (Entity)player3, lineColor, PlayerEsp.RenderMode.OUTLINE, meshBuilder2, 1.0F);
            }

            MeshBuilder.defaultImpl(meshBuilder2, Pipelines.INSTANCE.getRenderPipeline25(), null, null, stack.last().pose(), null, null, null, 118, null);
         }
      }
   }

   private final void render(PoseStack poseStack, Entity entity, Color color, PlayerEsp.RenderMode renderMode, MeshBuilder meshBuilder, float amount) {
      if (entity instanceof Player) {
         ModelVertexConsumer modelVertexConsumer = new ModelVertexConsumer(meshBuilder, renderMode, color);
         PlayerModel model = (PlayerModel)((AvatarRenderer)MinecraftHolder.getMinecraft().getEntityRenderDispatcher().getRenderer(entity)).getModel();
         AvatarRenderState renderState = (AvatarRenderState)MinecraftHolder.getMinecraft()
            .getEntityRenderDispatcher()
            .getRenderer(entity)
            .createRenderState(entity, Render3D.INSTANCE.getAmount4());
         poseStack.pushPose();
         Vec3 entityRenderDispatcher = Render3D.INSTANCE.fetchEntityRenderDispatcher((Player)entity, amount);
         double value = entityRenderDispatcher.x;
         double value2 = entityRenderDispatcher.y;
         double value3 = entityRenderDispatcher.z;
         Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-mainCamera.yRot() + 180.0F));
         poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-mainCamera.xRot()));
         poseStack.translate((float)value, (float)value2, (float)value3);
         poseStack.scale(renderState.scale, renderState.scale, renderState.scale);
         LogoutSpots.INSTANCE.render(renderState, poseStack, renderState.bodyRot);
         poseStack.scale(-1.0F, -1.0F, 1.0F);
         poseStack.scale(0.9375F, 0.9375F, 0.9375F);
         poseStack.translate(0.0F, ((Player)entity).isShiftKeyDown() ? -1.37F : -1.501F, 0.0F);
         model.setupAnim(renderState);
         model.jacket.visible = false;
         model.leftSleeve.visible = false;
         model.rightSleeve.visible = false;
         model.leftPants.visible = false;
         model.rightPants.visible = false;
         model.hat.visible = false;
         model.renderToBuffer(poseStack, modelVertexConsumer, 15728880, 0, -1);
         poseStack.popPose();
      }
   }

   public static void defaultImpl(
      PlayerEspRenderer playerEspRenderer,
      PoseStack poseStack,
      Entity entity,
      Color color,
      PlayerEsp.RenderMode renderMode,
      MeshBuilder meshBuilder,
      float amount,
      int value,
      Object object
   ) {
      if ((value & 32) != 0) {
         amount = Render3D.INSTANCE.getAmount4();
      }

      playerEspRenderer.render(poseStack, entity, color, renderMode, meshBuilder, amount);
   }

   public final void render(PoseStack stack, PlayerModel model, AvatarRenderState state, Vec3 pos, Color color, PlayerEsp.RenderMode mode, MeshBuilder polygon) {
      ModelVertexConsumer modelVertexConsumer = new ModelVertexConsumer(polygon, mode, color);
      stack.pushPose();
      Vec3 position = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position();
      double value = pos.x - position.x;
      double value2 = pos.y - position.y;
      double value3 = pos.z - position.z;
      Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
      stack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-mainCamera.yRot() + 180.0F));
      stack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-mainCamera.xRot()));
      stack.translate((float)value, (float)value2, (float)value3);
      stack.scale(state.scale, state.scale, state.scale);
      LogoutSpots.INSTANCE.render(state, stack, state.bodyRot);
      stack.scale(-1.0F, -1.0F, 1.0F);
      stack.scale(0.9375F, 0.9375F, 0.9375F);
      stack.translate(0.0F, -1.501F, 0.0F);
      model.setupAnim(state);
      model.jacket.visible = false;
      model.leftSleeve.visible = false;
      model.rightSleeve.visible = false;
      model.leftPants.visible = false;
      model.rightPants.visible = false;
      model.hat.visible = false;
      model.renderToBuffer(stack, modelVertexConsumer, 15728880, 0, -1);
      stack.popPose();
   }

   private final void render2(PoseStack poseStack, Entity entity, Color color, PlayerEsp.RenderMode renderMode, MeshBuilder meshBuilder, float amount) {
      if (entity instanceof EndCrystal) {
         ModelVertexConsumer modelVertexConsumer = new ModelVertexConsumer(meshBuilder, renderMode, color);
         EntityRenderer renderer = (EntityRenderer)MinecraftHolder.getMinecraft().getEntityRenderDispatcher().getRenderer(entity);
         EndCrystalModel model = ((EndCrystalRendererAccessor)((EndCrystalRenderer)renderer)).getModel();
         EndCrystalRenderState renderState = (EndCrystalRenderState)((EndCrystalRenderer)renderer).createRenderState((EndCrystal)entity, Render3D.INSTANCE.getAmount4());
         Vec3 position = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position();
         double value = ((EndCrystal)entity).getX() - position.x;
         double value2 = ((EndCrystal)entity).getY() - position.y;
         double value3 = ((EndCrystal)entity).getZ() - position.z;
         poseStack.pushPose();
         Camera mainCamera = MinecraftHolder.getMinecraft().gameRenderer.mainCamera();
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-mainCamera.yRot() + 180.0F));
         poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-mainCamera.xRot()));
         poseStack.translate(value, value2, value3);
         poseStack.scale(amount, amount, amount);
         poseStack.scale(2.0F, 2.0F, 2.0F);
         poseStack.translate(0.0F, -0.5F, 0.0F);
         model.setupAnim(renderState);
         model.renderToBuffer(poseStack, modelVertexConsumer, 15728880, 0, -1);
         poseStack.popPose();
      }
   }
}