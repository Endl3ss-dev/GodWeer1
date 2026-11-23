// deobf by pusia
package su.catlean.api.event.events.world;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class VelocityMultiplierEvent extends Event {
   @NotNull
   public static final VelocityMultiplierEvent INSTANCE = new VelocityMultiplierEvent();
   private static Block block;

   private VelocityMultiplierEvent() {
   }

   @NotNull
   public final Block getBlock() {
      if (block != null) {
         return block;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("block");
         return null;
      }
   }

   public final boolean call(Block block) {
      this.setCancelled(false);
      VelocityMultiplierEvent.block = block;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}