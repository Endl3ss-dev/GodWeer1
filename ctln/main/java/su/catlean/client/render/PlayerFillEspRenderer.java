// deobf by pusia
package su.catlean.client.render;

import java.awt.Color;
import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.CameraType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.esp.PlayerEsp;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.EntityPositionUtil;

public final class PlayerFillEspRenderer {
   @NotNull
   public static final PlayerFillEspRenderer INSTANCE = new PlayerFillEspRenderer();

   private PlayerFillEspRenderer() {
   }

   public final void applyGetBoundingBox(PlayerEsp.RenderMode mode, Color fillColor, Color lineColor, int value, int value2) {
      Vec3 vec3;
      label63: {
         Entity cameraEntity = MinecraftHolder.getMinecraft().getCameraEntity();
         if (cameraEntity != null) {
            vec3 = EntityPositionUtil.computeVec3(cameraEntity);
            if (vec3 != null) {
               break label63;
            }
         }

         vec3 = EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer());
      }

      Vec3 vec32 = vec3;
      Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
      var arrayList = new ArrayList();
      boolean flag = false;

      for (Object object : entitiesForRendering) {
         if (object instanceof EndCrystal) {
            arrayList.add(object);
         }
      }

      entitiesForRendering = arrayList;
      arrayList = new ArrayList();
      flag = false;

      for (Object object2 : entitiesForRendering) {
         EndCrystal endCrystal = (EndCrystal)object2;
         if (Math.hypot(vec32.x - endCrystal.getX(), vec32.z - endCrystal.getZ()) < value2 && Math.abs(vec32.y - endCrystal.getY()) < value) {
            arrayList.add(object2);
         }
      }

      for (Object object3 : arrayList) {
         EndCrystal endCrystal2 = (EndCrystal)object3;
         if (mode == PlayerEsp.RenderMode.BOTH || mode == PlayerEsp.RenderMode.FILL) {
            Render3D.defaultImpl2(Render3D.INSTANCE, endCrystal2.getBoundingBox(), fillColor, null, 4, null);
         }

         if (mode == PlayerEsp.RenderMode.BOTH || mode == PlayerEsp.RenderMode.OUTLINE) {
            Render3D.defaultImpl3(Render3D.INSTANCE, endCrystal2.getBoundingBox(), lineColor, null, 4, null);
         }
      }
   }

   public final void applyGetXsize(
      PlayerEsp.RenderMode mode,
      Color fillColor,
      Color lineColor,
      Color friendFillColor,
      Color friendLineColor,
      int value,
      int value2,
      Color sFillColor,
      Color sLineColor,
      Color sFriendFillColor,
      Color sFriendLineColor
   ) {
      Vec3 vec3;
      label80: {
         Entity cameraEntity = MinecraftHolder.getMinecraft().getCameraEntity();
         if (cameraEntity != null) {
            vec3 = EntityPositionUtil.computeVec3(cameraEntity);
            if (vec3 != null) {
               break label80;
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

      for (Object object2 : arrayList) {
         Entity entity2 = (Entity)object2;
         AABB boundingBox = entity2.getBoundingBox();
         Vec3 position = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position();
         String text = "null cannot be cast to non-null type net.minecraft.world.entity.player.Player";
         Vec3 vec33 = Render3D.defaultImpl(Render3D.INSTANCE, (Player)entity2, 0.0F, 2, null).add(position.x, position.y, position.z);
         Vec3 vec34 = vec33.add(-boundingBox.getXsize() / 2.0, 0.0, -boundingBox.getZsize() / 2.0);
         Vec3 xsize = vec33.add(boundingBox.getXsize() / 2.0, boundingBox.getYsize(), boundingBox.getZsize() / 2.0);
         boundingBox = new AABB(vec34, xsize);
         if (mode == PlayerEsp.RenderMode.BOTH || mode == PlayerEsp.RenderMode.FILL) {
            Render3D instance = Render3D.INSTANCE;
            Color friendFillColor2 = PlayerNameUtil.checkGetGameProfile((Player)entity2) ? friendFillColor : fillColor;
            Color sFriendFillColor2 = PlayerNameUtil.checkGetGameProfile((Player)entity2) ? sFriendFillColor : sFillColor;
            Color color = friendFillColor2;
            instance.testAABB2(boundingBox, color, sFriendFillColor2);
         }

         if (mode == PlayerEsp.RenderMode.BOTH || mode == PlayerEsp.RenderMode.OUTLINE) {
            Render3D instance2 = Render3D.INSTANCE;
            Color friendLineColor2 = PlayerNameUtil.checkGetGameProfile((Player)entity2) ? friendLineColor : lineColor;
            Color sFriendLineColor2 = PlayerNameUtil.checkGetGameProfile((Player)entity2) ? sFriendLineColor : sLineColor;
            Color color2 = friendLineColor2;
            instance2.testAABB3(boundingBox, color2, sFriendLineColor2);
         }
      }
   }
}