// deobf by pusia
package su.catlean.api.event.events.world;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class WaterPushEvent extends Event {
   @NotNull
   public static final WaterPushEvent INSTANCE = new WaterPushEvent();
   private static BlockPos pos;
   private static FluidState state;
   private static Fluid fluid;
   public static Vec3 vec;

   private WaterPushEvent() {
   }

   @NotNull
   public final BlockPos getPos() {
      if (pos != null) {
         return pos;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("pos");
         return null;
      }
   }

   @NotNull
   public final FluidState getState() {
      if (state != null) {
         return state;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("state");
         return null;
      }
   }

   @NotNull
   public final Fluid getFluid() {
      if (fluid != null) {
         return fluid;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("fluid");
         return null;
      }
   }

   @NotNull
   public final Vec3 getVec() {
      if (vec != null) {
         return vec;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("vec");
         return null;
      }
   }

   public final void setVec(Vec3 vec3) {
      vec = vec3;
   }

   public final boolean call(BlockPos pos, Fluid fluid, FluidState state, Vec3 vec) {
      this.setCancelled(false);
      WaterPushEvent.pos = pos;
      this.setVec(vec);
      WaterPushEvent.fluid = fluid;
      WaterPushEvent.state = state;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}