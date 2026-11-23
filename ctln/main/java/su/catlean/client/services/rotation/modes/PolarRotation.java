// deobf by pusia
package su.catlean.client.services.rotation.modes;

import kotlin.ranges.ClosedRange;
import kotlin.ranges.RangesKt;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.world.EntityPositionUtil;

public final class PolarRotation extends RotationStrategy {
   @NotNull
   public static final PolarRotation INSTANCE = new PolarRotation();
   private static int value;
   @NotNull
   private static Rotation rotation = new Rotation(0.0F, 0.0F, false, null, 12, null);

   private PolarRotation() {
   }

   @NotNull
   @Override
   public Rotation fetchBoundingBox(Entity target, Rotation currentRotation, boolean flag) {
      int value2 = value++;
      Vec3 eyePosition = this.fetchEyePosition(target);
      float amount = 0.0F;
      float amount2 = this.computeAmount(false) + rotation.getAmount2();
      if (flag && !(MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(target.getEyePosition()) > Aura.INSTANCE.getAttackRange() + 0.3F)) {
         value = 0;
         float wrapDegrees = (float)Mth.wrapDegrees(
            Math.toDegrees(Math.atan2(eyePosition.z - MinecraftHolder.getLocalPlayer().getZ(), eyePosition.x - MinecraftHolder.getLocalPlayer().getX()))
               - 90.0F
               - currentRotation.getAmount()
         );
         float amount22 = -(
               (float)Math.toDegrees(
                  Math.atan2(
                     eyePosition.y
                        - (
                           EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).y
                              + MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose())
                        ),
                     Math.hypot(eyePosition.x - MinecraftHolder.getLocalPlayer().getX(), eyePosition.z - MinecraftHolder.getLocalPlayer().getZ())
                  )
               )
            )
            - currentRotation.getAmount2();
         amount = currentRotation.getAmount() + RangesKt.coerceIn(wrapDegrees, -95.0F, 95.0F);
         amount2 = currentRotation.getAmount2() + amount22;
         rotation = new Rotation(amount, amount2, false, null, 12, null);
      } else {
         if (value > 8) {
            float defaultImpl = MathUtil.defaultImpl(25.0F, 45.0F, false, 4, null);
            float coerceIn = RangesKt.coerceIn(
               Mth.wrapDegrees(this.computeAmount(true) + MinecraftHolder.getMinecraft().gameRenderer.mainCamera().yRot() - currentRotation.getAmount()),
               -defaultImpl,
               defaultImpl
            );
            return new Rotation(currentRotation.getAmount() + coerceIn, amount2, false, null, 12, null);
         }

         amount = this.computeAmount(true) + rotation.getAmount();
      }

      int tickCount = MinecraftHolder.getLocalPlayer().tickCount % 400;
      if (0 <= tickCount ? tickCount < 11 : false) {
         amount2 = -90.0F
            + MathUtil.defaultImpl(18.0F, 20.0F, false, 4, null) * (MinecraftHolder.getLocalPlayer().tickCount % 400 / 10.0F)
            + MathUtil.defaultImpl2((ClosedRange)RangesKt.rangeTo(-1.0F, 1.0F), false, 2, null);
      }

      return new Rotation(amount, amount2, false, null, 12, null);
   }

   private final float computeAmount(boolean flag) {
      if (flag) {
         float tickCount = MinecraftHolder.getLocalPlayer().tickCount;
         float amount = (float)Math.sin(tickCount) + MathUtil.computeAmount(-0.1F, 0.1F, false);
         return amount * 15.0F;
      } else {
         float tickCount2 = MinecraftHolder.getLocalPlayer().tickCount + 2.0F;
         float value2 = (float)Math.sin(tickCount2);
         double value3 = 0.2 * Math.sin(0.4 * tickCount2 * 0.7 + 1.23);
         double value4 = 1 + 0.3 * Math.sin(0.4 * tickCount2 * 0.5 + 2.45);
         float value5 = (float)(value4 * value2 + value3);
         return value5 * 6.0F;
      }
   }

   @NotNull
   @Override
   public Vec3 fetchEyePosition(Entity target) {
      Vec3 center = target.getBoundingBox().getCenter();
      return new Vec3(center.x, center.y, center.z);
   }
}