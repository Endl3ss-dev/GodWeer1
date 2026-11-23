// deobf by pusia
package su.catlean.client.services.rotation.modes;

import kotlin.ranges.RangesKt;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.world.EntityPositionUtil;

public final class HvhRotation extends RotationStrategy {
   @NotNull
   public static final HvhRotation INSTANCE = new HvhRotation();

   private HvhRotation() {
   }

   @NotNull
   @Override
   public Rotation fetchBoundingBox(Entity target, Rotation currentRotation, boolean flag) {
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
                     EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).y
                        + MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose())
                  ),
               Math.hypot(eyePosition.x - MinecraftHolder.getLocalPlayer().getX(), eyePosition.z - MinecraftHolder.getLocalPlayer().getZ())
            )
         )
         - currentRotation.getAmount2();
      double value = MinecraftHolder.getLocalPlayer().isFallFlying() ? 360.0 : 360.0;
      double value2 = !flag && !MinecraftHolder.getLocalPlayer().isFallFlying() ? 180.0 : 180.0;
      double amount = currentRotation.getAmount() + Math.clamp(wrapDegrees, -value, value);
      double amount22 = currentRotation.getAmount2() + Math.clamp(amount2, -value2, value2) + Math.sin(Math.toRadians(wrapDegrees));
      return new Rotation((float)amount, (float)amount22, false, null, 12, null);
   }

   @NotNull
   @Override
   public Vec3 fetchEyePosition(Entity target) {
      if (MinecraftHolder.getLocalPlayer().isFallFlying()) {
         return target.getEyePosition();
      } else {
         AABB boundingBox = target.getBoundingBox();
         return new Vec3(
            RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().x, boundingBox.minX, boundingBox.maxX),
            RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().y, boundingBox.minY, boundingBox.maxY),
            RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().z, boundingBox.minZ, boundingBox.maxZ)
         );
      }
   }
}