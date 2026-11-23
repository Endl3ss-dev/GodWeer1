// deobf by pusia
package su.catlean.client.services.rotation.modes;

import kotlin.ranges.RangesKt;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.services.InteractionManager;
import su.catlean.utils.MinecraftHolder;

public final class SnapRotation extends RotationStrategy {
   @NotNull
   public static final SnapRotation INSTANCE = new SnapRotation();

   private SnapRotation() {
   }

   @NotNull
   @Override
   public Rotation fetchBoundingBox(Entity target, Rotation currentRotation, boolean flag) {
      Vec3 eyePosition = this.fetchEyePosition(target);
      double wrapDegrees = Mth.wrapDegrees(
         Math.toDegrees(Math.atan2(eyePosition.z - MinecraftHolder.getLocalPlayer().getZ(), eyePosition.x - MinecraftHolder.getLocalPlayer().getX()))
            - 90.0F
            - currentRotation.getAmount()
      );
      double amount2 = -Math.toDegrees(
            Math.atan2(
               eyePosition.y - MinecraftHolder.getLocalPlayer().getEyePosition().y,
               Math.hypot(eyePosition.x - MinecraftHolder.getLocalPlayer().getX(), eyePosition.z - MinecraftHolder.getLocalPlayer().getZ())
            )
         )
         - currentRotation.getAmount2();
      float amount = currentRotation.getAmount() + (float)wrapDegrees;
      float amount22 = currentRotation.getAmount2() + (float)amount2 + (float)Math.sin(wrapDegrees);
      if (!flag) {
         amount = MinecraftHolder.getLocalPlayer().getYRot();
         amount22 = MinecraftHolder.getLocalPlayer().getXRot();
      }

      if (Aura.INSTANCE.getPriorityManual()) {
         InteractionManager instance = InteractionManager.INSTANCE;
         Entity entity = Aura.INSTANCE.getEntity();
         boolean flag2 = !Aura.INSTANCE.getIgnoreWalls();
         float attackRange = Aura.INSTANCE.getAttackRange();
         Entity entity2 = entity;
         if (instance.checkDistanceToSqr(entity2, currentRotation, attackRange, flag2)) {
            return new Rotation(
               MinecraftHolder.getMinecraft().gameRenderer.mainCamera().yRot(),
               MinecraftHolder.getMinecraft().gameRenderer.mainCamera().xRot(),
               false,
               null,
               12,
               null
            );
         }
      }

      return new Rotation(amount, amount22, false, null, 12, null);
   }

   @Override
   public boolean isFlag4() {
      return false;
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