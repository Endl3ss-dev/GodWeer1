// deobf by pusia
package su.catlean.client.services.rotation.modes;

import kotlin.ranges.RangesKt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.addon.feature.AddonAuraRotation;
import su.catlean.api.data.Rotation;

public final class CustomRotation extends RotationStrategy {
   @NotNull
   public static final CustomRotation INSTANCE = new CustomRotation();
   @Nullable
   private static volatile AddonAuraRotation addonAuraRotation;

   private CustomRotation() {
   }

   public final void updateAddonAuraRotation(AddonAuraRotation rotation) {
      addonAuraRotation = rotation;
   }

   @NotNull
   @Override
   public Rotation fetchBoundingBox(Entity target, Rotation currentRotation, boolean flag) {
      if (addonAuraRotation == null) {
         return currentRotation;
      } else {
         AddonAuraRotation addonAuraRotation2 = addonAuraRotation;
         su.catlean.api.addon.feature.Rotation rotation = addonAuraRotation2.getRotation(
            target, new su.catlean.api.addon.feature.Rotation(currentRotation.getAmount(), currentRotation.getAmount2()), flag
         );
         return new Rotation(rotation.getYaw(), RangesKt.coerceIn(rotation.getPitch(), -90.0F, 90.0F), currentRotation.isFlag(), currentRotation.getMoveFix());
      }
   }

   @NotNull
   @Override
   public Vec3 fetchEyePosition(Entity target) {
      if (addonAuraRotation != null) {
         Vec3 point = addonAuraRotation.getPoint(target);
         if (point != null) {
            return point;
         }
      }

      return target.getEyePosition();
   }

   @Override
   public boolean isFlag3() {
      return addonAuraRotation != null ? addonAuraRotation.needRayCast() : true;
   }

   @Override
   public boolean isFlag4() {
      return addonAuraRotation != null ? addonAuraRotation.needExtend() : true;
   }
}