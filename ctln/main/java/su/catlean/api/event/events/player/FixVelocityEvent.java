// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class FixVelocityEvent extends Event {
   @NotNull
   public static final FixVelocityEvent INSTANCE = new FixVelocityEvent();
   @NotNull
   private static Vec3 movementInput = new Vec3(0.0, 0.0, 0.0);
   private static float speed;
   private static float yaw;
   @NotNull
   private static Vec3 velocity = new Vec3(0.0, 0.0, 0.0);

   private FixVelocityEvent() {
   }

   @NotNull
   public final Vec3 getMovementInput() {
      return movementInput;
   }

   public final void setMovementInput(Vec3 vec3) {
      movementInput = vec3;
   }

   public final float getSpeed() {
      return speed;
   }

   public final void setSpeed(float amount) {
      speed = amount;
   }

   public final float getYaw() {
      return yaw;
   }

   public final void setYaw(float amount) {
      yaw = amount;
   }

   @NotNull
   public final Vec3 getVelocity() {
      return velocity;
   }

   public final void setVelocity(Vec3 vec3) {
      velocity = vec3;
   }

   public final boolean call(Vec3 movementInput, float amount, float amount2) {
      this.setCancelled(false);
      FixVelocityEvent.movementInput = movementInput;
      speed = amount;
      yaw = amount2;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}