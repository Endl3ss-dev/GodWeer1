// deobf by pusia
package su.catlean.api.event.events.world;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class FireWorkVectorEvent extends Event {
   @NotNull
   public static final FireWorkVectorEvent INSTANCE = new FireWorkVectorEvent();
   public static Vec3 vector;

   private FireWorkVectorEvent() {
   }

   @NotNull
   public final Vec3 getVector() {
      if (vector != null) {
         return vector;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("vector");
         return null;
      }
   }

   public final void setVector(Vec3 vec3) {
      vector = vec3;
   }

   public final boolean call(Vec3 vec) {
      this.setVector(vec);
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}