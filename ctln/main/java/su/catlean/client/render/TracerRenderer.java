// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.CameraType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector4d;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;

public final class TracerRenderer {
   @NotNull
   public static final TracerRenderer INSTANCE = new TracerRenderer();

   private TracerRenderer() {
   }

   public final void render(GuiGraphicsExtractor context, Color lineColor) {
      Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
      var arrayList = new ArrayList();

      for (Object object : entitiesForRendering) {
         if (object instanceof EndCrystal) {
            arrayList.add(object);
         }
      }

      List list = arrayList;
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, list.size() * 8, false, 4, null);
      Iterable iterable = list;

      for (Object object2 : iterable) {
         EndCrystal endCrystal = (EndCrystal)object2;
         Vec3[] vec3s = Render2D.INSTANCE.computeVec3s((Entity)endCrystal);
         Vector4d vector4d = null;
         int value = 0;

         for (int j = vec3s.length; value < j; value++) {
            Vec3 vec3 = vec3s[value];
            Vec3 window = Render2D.INSTANCE.fetchWindow(new Vec3(vec3.x, vec3.y, vec3.z));
            if (window.z > 0.0 && window.z < 1.0) {
               if (vector4d == null) {
                  vector4d = new Vector4d(window.x, window.y, window.z, 0.0);
               }

               vector4d.x = Math.min(window.x, vector4d.x);
               vector4d.y = Math.min(window.y, vector4d.y);
               vector4d.z = Math.max(window.x, vector4d.z);
               vector4d.w = Math.max(window.y, vector4d.w);
            }
         }

         if (vector4d != null) {
            float value2 = (float)vector4d.x;
            float value3 = (float)vector4d.y;
            float value4 = (float)vector4d.z;
            float value5 = (float)vector4d.w;
            Render2D.INSTANCE.updateMeshBuilder2(meshBuilder, value2, value3, value4, value5, lineColor);
         }
      }

      MeshBuilder.defaultImpl(
         meshBuilder, Pipelines.INSTANCE.getRenderPipeline22(), null, null, null, new Matrix3x2f((Matrix3x2fc)context.pose()), null, null, 110, null
      );
   }

   public final void render(GuiGraphicsExtractor context, Color lineColor, Color friendLineColor) {
      Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
      var arrayList = new ArrayList();

      for (Object object : entitiesForRendering) {
         Entity entity = (Entity)object;
         if (entity instanceof Player
            && (
               !Intrinsics.areEqual(entity, MinecraftHolder.getLocalPlayer())
                  || MinecraftHolder.getMinecraft().options.getCameraType() != CameraType.FIRST_PERSON
            )) {
            arrayList.add(object);
         }
      }

      List list = arrayList;
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, list.size() * 8, false, 4, null);
      Iterable iterable = list;

      for (Object object2 : iterable) {
         Entity entity2 = (Entity)object2;
         Vec3[] vec3s = Render2D.INSTANCE.computeVec3s(entity2);
         Vector4d vector4d = null;
         int value = 0;

         for (int j = vec3s.length; value < j; value++) {
            Vec3 vec3 = vec3s[value];
            Vec3 window = Render2D.INSTANCE.fetchWindow(new Vec3(vec3.x, vec3.y, vec3.z));
            if (window.z > 0.0 && window.z < 1.0) {
               if (vector4d == null) {
                  vector4d = new Vector4d(window.x, window.y, window.z, 0.0);
               }

               vector4d.x = Math.min(window.x, vector4d.x);
               vector4d.y = Math.min(window.y, vector4d.y);
               vector4d.z = Math.max(window.x, vector4d.z);
               vector4d.w = Math.max(window.y, vector4d.w);
            }
         }

         if (vector4d != null) {
            float value2 = (float)vector4d.x;
            float value3 = (float)vector4d.y;
            float value4 = (float)vector4d.z;
            float value5 = (float)vector4d.w;
            Render2D instance = Render2D.INSTANCE;
            Color friendLineColor2 = PlayerNameUtil.checkGetGameProfile((Player)entity2) ? friendLineColor : lineColor;
            instance.updateMeshBuilder2(meshBuilder, value2, value3, value4, value5, friendLineColor2);
         }
      }

      MeshBuilder.defaultImpl(
         meshBuilder, Pipelines.INSTANCE.getRenderPipeline22(), null, null, null, new Matrix3x2f((Matrix3x2fc)context.pose()), null, null, 110, null
      );
   }
}