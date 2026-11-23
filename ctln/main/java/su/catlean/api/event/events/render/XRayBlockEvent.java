// deobf by pusia
package su.catlean.api.event.events.render;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class XRayBlockEvent extends Event {
   @NotNull
   private Block block = Blocks.DIRT;

   public XRayBlockEvent() {
   }

   @NotNull
   public final Block getBlock() {
      return this.block;
   }

   public final void setBlock(Block block) {
      this.block = block;
   }

   public final boolean call(Block block) {
      this.setCancelled(false);
      this.block = block;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}