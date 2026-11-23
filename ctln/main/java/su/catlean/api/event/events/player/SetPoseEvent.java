// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class SetPoseEvent extends Event {
   @NotNull
   public static final SetPoseEvent INSTANCE = new SetPoseEvent();
   @NotNull
   private static Pose pose = Pose.STANDING;

   private SetPoseEvent() {
   }

   @NotNull
   public final Pose getPose() {
      return pose;
   }

   public final void setPose(Pose pose) {
      SetPoseEvent.pose = pose;
   }
}