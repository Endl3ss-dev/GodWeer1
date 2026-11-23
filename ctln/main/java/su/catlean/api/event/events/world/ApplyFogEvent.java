// deobf by pusia
package su.catlean.api.event.events.world;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import su.catlean.api.event.Event;

public final class ApplyFogEvent extends Event {
   @NotNull
   public static final ApplyFogEvent INSTANCE = new ApplyFogEvent();
   @NotNull
   private static Vector4f colorVec = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private static float eStart;
   private static float eEnd;
   private static float rStart;
   private static float rEnd;

   private ApplyFogEvent() {
   }

   @NotNull
   public final Vector4f getColorVec() {
      return colorVec;
   }

   public final void setColorVec(Vector4f vector4f) {
      colorVec = vector4f;
   }

   public final float getEStart() {
      return eStart;
   }

   public final void setEStart(float amount) {
      eStart = amount;
   }

   public final float getEEnd() {
      return eEnd;
   }

   public final void setEEnd(float amount) {
      eEnd = amount;
   }

   public final float getRStart() {
      return rStart;
   }

   public final void setRStart(float amount) {
      rStart = amount;
   }

   public final float getREnd() {
      return rEnd;
   }

   public final void setREnd(float amount) {
      rEnd = amount;
   }
}