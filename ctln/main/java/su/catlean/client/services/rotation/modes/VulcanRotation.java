// deobf by pusia
package su.catlean.client.services.rotation.modes;

import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.basic.AntiKnockBack;
import su.catlean.client.services.InteractionManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;

public final class VulcanRotation extends RotationStrategy {
   @NotNull
   public static final VulcanRotation INSTANCE = new VulcanRotation();

   private VulcanRotation() {
   }

   @NotNull
   @Override
   public Rotation fetchBoundingBox(Entity target, Rotation currentRotation, boolean flag) {
      Vec3 eyePosition = this.fetchEyePosition(target);
      double wrapDegrees = Mth.wrapDegrees(
            Math.toDegrees(Math.atan2(eyePosition.z - MinecraftHolder.getLocalPlayer().getZ(), eyePosition.x - MinecraftHolder.getLocalPlayer().getX()))
               - 90
               - currentRotation.getAmount()
         )
         + MathUtil.defaultImpl(-1.0F, 1.0F, false, 4, null);
      double amount2 = -Math.toDegrees(
            Math.atan2(
               eyePosition.y
                  - (
                     AntiKnockBack.INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).y
                        + MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose())
                  ),
               Math.hypot(eyePosition.x - MinecraftHolder.getLocalPlayer().getX(), eyePosition.z - MinecraftHolder.getLocalPlayer().getZ())
            )
         )
         - currentRotation.getAmount2();
      InteractionManager instance = InteractionManager.INSTANCE;
      Rotation rotation = new Rotation(currentRotation.getAmount(), currentRotation.getAmount2(), false, null, 12, null);
      float attackRange = Aura.INSTANCE.getAttackRange() + 2.0F;
      boolean flag2 = !Aura.INSTANCE.getIgnoreWalls();
      float value = attackRange;
      Rotation rotation2 = rotation;
      boolean checkDistanceToSqr = instance.checkDistanceToSqr(target, rotation2, value, flag2);
      double defaultImpl = !MinecraftHolder.getLocalPlayer().isFallFlying()
         ? (checkDistanceToSqr ? MathUtil.defaultImpl(0.15, 0.21, false, 4, null) : Math.abs(wrapDegrees) / MathUtil.defaultImpl(1.4F, 1.8F, false, 4, null))
         : 180.0;
      double random = !MinecraftHolder.getLocalPlayer().isFallFlying()
         ? Math.max(Math.abs(amount2) - 2.0, 0.0) / 2.0 + (MinecraftHolder.getLocalPlayer().tickCount % 25 == 0 ? 2.0 * Math.random() : 0.0)
         : 90.0;
      double value2 = Math.abs(wrapDegrees) > 0.3 ? wrapDegrees : 0.0;
      double amount = currentRotation.getAmount()
         + Math.clamp(value2, -defaultImpl, defaultImpl)
         + Math.sin(Math.toRadians((double)MinecraftHolder.getLocalPlayer().tickCount * 20.0F)) * 0.5F;
      double amount22 = currentRotation.getAmount2() + Math.clamp(amount2, -random, random) + Math.sin(Math.toRadians(amount)) * 1.5F;
      double amount3 = amount - currentRotation.getAmount();
      double roundToInt = Math.abs(MathKt.roundToInt(amount3) - amount3);
      if (roundToInt < 1.0) {
         amount += MinecraftHolder.getLocalPlayer().tickCount % 2 == 0 ? 1 : -2;
      }

      return new Rotation((float)amount, (float)amount22, false, null, 12, null);
   }

   @NotNull
   @Override
   public Vec3 fetchEyePosition(Entity target) {
      AABB boundingBox = target.getBoundingBox();
      return new Vec3(
         RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().x, boundingBox.minX, boundingBox.maxX),
         RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().y, boundingBox.minY, boundingBox.maxY),
         RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getEyePosition().z, boundingBox.minZ, boundingBox.maxZ)
      );
   }
}