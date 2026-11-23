// deobf by pusia
package su.catlean.api.addon.feature;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class AddonAuraRotation {
   public AddonAuraRotation() {
   }

   @NotNull
   public abstract Rotation getRotation(Entity entity, Rotation rotation, boolean flag);

   @NotNull
   public abstract Vec3 getPoint(Entity entity);

   public boolean needRayCast() {
      return true;
   }

   public boolean needExtend() {
      return true;
   }
}