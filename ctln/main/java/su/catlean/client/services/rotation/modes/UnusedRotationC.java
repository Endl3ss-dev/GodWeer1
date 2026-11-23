// deobf by pusia
package su.catlean.client.services.rotation.modes;

import kotlin.ranges.RangesKt;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.utils.MinecraftHolder;

public final class UnusedRotationC extends RotationStrategy {
   @NotNull
   public static final UnusedRotationC INSTANCE = new UnusedRotationC();
   private static int value;

   private UnusedRotationC() {
   }

   @NotNull
   @Override
   public Rotation fetchBoundingBox(Entity target, Rotation currentRotation, boolean flag) {
      if (flag) {
         value = Aura.INSTANCE.isFlag3() ? 1 : 4;
      } else if (value > 0) {
         int value2 = value;
         value = value2 + -1;
      }

      Vec3 eyePosition = this.fetchEyePosition(target);
      double wrapDegrees = Mth.wrapDegrees(
         Math.toDegrees(Math.atan2(eyePosition.z - MinecraftHolder.getLocalPlayer().getZ(), eyePosition.x - MinecraftHolder.getLocalPlayer().getX()))
            - 90
            - currentRotation.getAmount()
      );
      double amount2 = -Math.toDegrees(
            Math.atan2(
               eyePosition.y
                  - (
                     Aura.INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).y
                        + MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose())
                  ),
               Math.hypot(eyePosition.x - MinecraftHolder.getLocalPlayer().getX(), eyePosition.z - MinecraftHolder.getLocalPlayer().getZ())
            )
         )
         - currentRotation.getAmount2();
      double coerceAtLeast = !MinecraftHolder.getLocalPlayer().isFallFlying() ? RangesKt.coerceAtLeast(Math.abs(wrapDegrees / 1.5), 35.0) : 180.0;
      double random = !MinecraftHolder.getLocalPlayer().isFallFlying()
         ? Math.max(Math.abs(amount2) - 15.0, 0.0) / 2.0 + (MinecraftHolder.getLocalPlayer().tickCount % 25 == 0 ? 2.0 * Math.random() : 0.0)
         : 90.0;
      double amount = currentRotation.getAmount() + RangesKt.coerceIn(wrapDegrees, -coerceAtLeast, coerceAtLeast);
      double amount22 = currentRotation.getAmount2() + RangesKt.coerceIn(amount2, -random, random) + Math.sin(wrapDegrees);
      return value > 0
         ? new Rotation((float)amount, (float)amount22, false, null, 12, null)
         : new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);
   }

   @Override
   public boolean isFlag4() {
      return false;
   }
}