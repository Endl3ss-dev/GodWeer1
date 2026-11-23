// deobf by pusia
package su.catlean.client.services.rotation.modes;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.world.EntityPositionUtil;

public abstract class RotationStrategy {
   @NotNull
   private Vec3 vec3 = Vec3.ZERO;

   public RotationStrategy() {
   }

   @NotNull
   public abstract Rotation fetchBoundingBox(Entity entity, Rotation rotation, boolean flag);

   @NotNull
   public Vec3 fetchEyePosition(Entity target) {
      if (MinecraftHolder.getLocalPlayer().isFallFlying()) {
         return target.getEyePosition();
      } else {
         if (MinecraftHolder.getLocalPlayer().tickCount % 100 == 0) {
            double amount = Render2D.INSTANCE.computeAmount(-0.1, 0.1, Math.random()) * 0.1;
            this.vec3 = new Vec3(amount, amount, amount);
         }

         return EntityPositionUtil.computeVec3(target).add(this.vec3.x, target.getBbHeight() / 2.0 + this.vec3.y, this.vec3.z);
      }
   }

   public boolean isFlag3() {
      return true;
   }

   public boolean isFlag4() {
      return true;
   }
}