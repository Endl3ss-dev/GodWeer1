// deobf by pusia
package su.catlean.client.services.rotation.modes;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.services.InteractionManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.world.EntityPositionUtil;
import su.catlean.utils.world.WorldUtil;

public final class DefaultRotation extends RotationStrategy {
   @NotNull
   public static final DefaultRotation INSTANCE = new DefaultRotation();

   private DefaultRotation() {
   }

   @NotNull
   @Override
   public Rotation fetchBoundingBox(Entity target, Rotation currentRotation, boolean flag) {
      Vec3 eyePosition = this.fetchEyePosition(target);
      target.setBoundingBox(target.getBoundingBox().inflate(-0.15, 0.0, -0.15));
      InteractionManager instance = InteractionManager.INSTANCE;
      Rotation rotation = new Rotation(WorldUtil.INSTANCE.getAmount4(), WorldUtil.INSTANCE.getAmount5(), false, null, 12, null);
      float attackRange = Aura.INSTANCE.getAttackRange() + 2.0F;
      boolean flag2 = !Aura.INSTANCE.getIgnoreWalls();
      float value = attackRange;
      Rotation rotation2 = rotation;
      boolean checkDistanceToSqr = instance.checkDistanceToSqr(target, rotation2, value, flag2);
      target.setBoundingBox(target.getBoundingBox().inflate(0.15, 0.0, 0.15));
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
                     EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).y
                        + MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose())
                  ),
               Math.hypot(eyePosition.x - MinecraftHolder.getLocalPlayer().getX(), eyePosition.z - MinecraftHolder.getLocalPlayer().getZ())
            )
         )
         - currentRotation.getAmount2();
      double defaultImpl = !MinecraftHolder.getLocalPlayer().isFallFlying() && Aura.INSTANCE.getAttackRange() < 4.0
         ? (checkDistanceToSqr ? MathUtil.defaultImpl(0.15, 0.21, false, 4, null) : Math.abs(wrapDegrees) / MathUtil.defaultImpl(1.4F, 1.8F, false, 4, null))
         : 360.0;
      double random = !MinecraftHolder.getLocalPlayer().isFallFlying() && Aura.INSTANCE.getAttackRange() < 4.0
         ? Math.max(Math.abs(amount2) - 15.0, 0.0) / 2.0 + (MinecraftHolder.getLocalPlayer().tickCount % 25 == 0 ? 2.0 * Math.random() : 0.0)
         : 180.0;
      double amount = currentRotation.getAmount() + Math.clamp(wrapDegrees, -defaultImpl, defaultImpl);
      double amount22 = currentRotation.getAmount2() + Math.clamp(amount2, -random, random) + Math.sin(Math.toRadians(wrapDegrees));
      return new Rotation((float)amount, (float)amount22, false, null, 12, null);
   }

   @NotNull
   @Override
   public Vec3 fetchEyePosition(Entity target) {
      if (MinecraftHolder.getLocalPlayer().isFallFlying()) {
         return target.getEyePosition();
      } else {
         double xsize = target.getBoundingBox().getXsize() / 2.1 * Math.sin(Math.toRadians(target.tickCount));
         double ysize = target.getBoundingBox().getYsize() / 8 * Math.sin(Math.toRadians(target.tickCount + 10.0));
         double zsize = target.getBoundingBox().getZsize() / 2.1 * -Math.cos(Math.toRadians(target.tickCount));
         return EntityPositionUtil.computeVec3(target).add(xsize, target.getBbHeight() / 2.0 + ysize, zsize);
      }
   }
}