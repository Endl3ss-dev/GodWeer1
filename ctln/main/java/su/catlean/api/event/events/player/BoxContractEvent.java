// deobf by pusia
package su.catlean.api.event.events.player;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class BoxContractEvent extends Event {
   @NotNull
   public static final BoxContractEvent INSTANCE = new BoxContractEvent();
   public static AABB value;

   private BoxContractEvent() {
   }

   @NotNull
   public final AABB getValue() {
      if (value != null) {
         return value;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("value");
         return null;
      }
   }

   public final void setValue(AABB aABB) {
      value = aABB;
   }
}