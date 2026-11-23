// deobf by pusia
package su.catlean.client.render;

import java.awt.Color;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public final class BoxGradientCommand {
   @NotNull
   private final AABB box;
   @NotNull
   private final Color color;
   @NotNull
   private final Color color2;

   public BoxGradientCommand(AABB box, Color color, Color color2) {
      this.box = box;
      this.color = color;
      this.color2 = color2;
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
   public final Color getColor2() {
      return this.color2;
   }
}