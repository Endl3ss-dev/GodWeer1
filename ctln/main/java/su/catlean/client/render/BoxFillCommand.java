// deobf by pusia
package su.catlean.client.render;

import java.awt.Color;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public final class BoxFillCommand {
   @NotNull
   private final AABB box;
   @NotNull
   private final Color color;
   @NotNull
   private final Direction side;

   public BoxFillCommand(AABB box, Color color, Direction side) {
      this.box = box;
      this.color = color;
      this.side = side;
   }

   @NotNull
   public final AABB getBox() {
      return this.box;
   }

   @NotNull
   public final Color getColor() {
      return this.color;
   }

   @NotNull
   public final Direction getSide() {
      return this.side;
   }
}