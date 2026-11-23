// deobf by pusia
package su.catlean.utils.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import org.joml.Quaternionfc;

public final class PoseUtil {
   public static final void render(PoseStack self, Quaternionfc q) {
      Pose last = self.last();
      last.pose().rotate(q);
   }
}