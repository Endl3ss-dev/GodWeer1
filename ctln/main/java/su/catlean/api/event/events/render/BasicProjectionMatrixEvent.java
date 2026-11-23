// deobf by pusia
package su.catlean.api.event.events.render;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class BasicProjectionMatrixEvent extends Event {
   @NotNull
   public static final BasicProjectionMatrixEvent INSTANCE = new BasicProjectionMatrixEvent();
   private static float ratio = 1.0F;
   private static float zoom = 1.0F;
   private static float zoomX = 1.0F;
   private static float zoomY = 1.0F;
   private static float fov = 1.0F;

   private BasicProjectionMatrixEvent() {
   }

   public final float getRatio() {
      return ratio;
   }

   public final void setRatio(float amount) {
      ratio = amount;
   }

   public final float getZoom() {
      return zoom;
   }

   public final void setZoom(float amount) {
      zoom = amount;
   }

   public final float getZoomX() {
      return zoomX;
   }

   public final void setZoomX(float amount) {
      zoomX = amount;
   }

   public final float getZoomY() {
      return zoomY;
   }

   public final void setZoomY(float amount) {
      zoomY = amount;
   }

   public final float getFov() {
      return fov;
   }

   public final void setFov(float amount) {
      fov = amount;
   }
}