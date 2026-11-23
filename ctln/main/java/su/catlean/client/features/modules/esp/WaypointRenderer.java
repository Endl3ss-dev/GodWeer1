// deobf by pusia
package su.catlean.client.features.modules.esp;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import su.catlean.api.data.WayPoint;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.world.EntityPositionUtil;
import su.catlean.utils.world.WorldUtil;

public final class WaypointRenderer {
   @NotNull
   public static final WaypointRenderer INSTANCE = new WaypointRenderer();
   @NotNull
   private static final SvgTexture svgTexture = new SvgTexture("point", 20, 20);

   private WaypointRenderer() {
   }

   @NotNull
   public final List getItems() {
      return ProxyManager.INSTANCE.getWaypointDataSource().getWayPointConfig().getWayPoints();
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      for (Object item : this.getItems()) {
         WayPoint wayPoint = (WayPoint)item;
         if (INSTANCE.checkDimension(wayPoint)) {
            Vec3 vec3 = new Vec3(wayPoint.getValue(), wayPoint.getValue2(), wayPoint.getValue3());
            if (WorldUtil.INSTANCE.checkMainCamera(vec3, ((Number)MinecraftHolder.getMinecraft().options.fov().get()).intValue())) {
               Vec3 window = Render2D.INSTANCE.fetchWindow(vec3);
               Fonts.getFontRenderer6()
                  .render2(
                     render2DEvent.getContext(),
                     wayPoint.getId() + " (" + (int)EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceTo(vec3) + "m)",
                     (float)window.x,
                     (float)window.y,
                     ThemeManager.INSTANCE.getColor7()
                  );
               SvgTexture.defaultImpl(
                  svgTexture, (Matrix3x2f)render2DEvent.getContext().pose(), window.x - 10.0, window.y - 25.0, 0.0F, 0.0F, null, null, 0.0F, 248, null
               );
            }
         }
      }
   }

   private final boolean checkDimension(WayPoint wayPoint) {
      if (wayPoint.isFlag()) {
         if (!Intrinsics.areEqual(wayPoint.getServer(), "localhost")) {
            String text;
            String server;
            label22: {
               server = wayPoint.getServer();
               ClientPacketListener connection = MinecraftHolder.getMinecraft().getConnection();
               if (connection != null) {
                  ServerData serverData = connection.getServerData();
                  if (serverData != null) {
                     text = serverData.ip;
                     break label22;
                  }
               }

               text = null;
            }

            if (!Intrinsics.areEqual(server, text)) {
               return false;
            }
         }

         if (wayPoint.getValue4() == this.computeValue(MinecraftHolder.getClientLevel().dimension().identifier().getPath())) {
            return true;
         }
      }

      return false;
   }

   public final boolean checkRemove(WayPoint wayPoint) {
      return this.getItems().remove(wayPoint);
   }

   public final boolean testWayPoint(WayPoint wayPoint) {
      return this.getItems().add(wayPoint);
   }

   public final int computeValue(String i) {
      return Intrinsics.areEqual(i, "overworld") ? 0 : (Intrinsics.areEqual(i, "the_nether") ? 1 : 2);
   }
}