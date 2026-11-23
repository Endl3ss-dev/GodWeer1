// deobf by pusia
package su.catlean.client.services.rotation.modes;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.utils.MinecraftHolder;

public final class NoRotation extends RotationStrategy {
   @NotNull
   public static final NoRotation INSTANCE = new NoRotation();

   private NoRotation() {
   }

   @NotNull
   @Override
   public Rotation fetchBoundingBox(Entity target, Rotation currentRotation, boolean flag) {
      return new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);
   }

   @NotNull
   @Override
   public Vec3 fetchEyePosition(Entity target) {
      return target.getEyePosition();
   }

   @Override
   public boolean isFlag3() {
      return false;
   }

   @Override
   public boolean isFlag4() {
      return false;
   }
}