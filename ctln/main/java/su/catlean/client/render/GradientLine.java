// deobf by pusia
package su.catlean.client.render;

import java.awt.Color;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class GradientLine {
   @NotNull
   private final Vec3 start;
   @NotNull
   private final Vec3 end;
   @NotNull
   private final Color startColor;
   @NotNull
   private final Color endColor;
   private final boolean flag;

   public GradientLine(Vec3 start, Vec3 end, Color startColor, Color endColor, boolean flag) {
      this.start = start;
      this.end = end;
      this.startColor = startColor;
      this.endColor = endColor;
      this.flag = flag;
   }

   public GradientLine(Vec3 vec3, Vec3 vec32, Color color, Color color2, boolean flag, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 16) != 0) {
         flag = false;
      }

      this(vec3, vec32, color, color2, flag);
   }

   @NotNull
   public final Vec3 getStart() {
      return this.start;
   }

   @NotNull
   public final Vec3 getEnd() {
      return this.end;
   }

   @NotNull
   public final Color getStartColor() {
      return this.startColor;
   }

   @NotNull
   public final Color getEndColor() {
      return this.endColor;
   }

   public final boolean isFlag() {
      return this.flag;
   }
}