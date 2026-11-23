// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.gofra.Gofra;

public final class AfterElytraEvent extends ElytraEvent {
   @NotNull
   public static final AfterElytraEvent INSTANCE = new AfterElytraEvent();

   private AfterElytraEvent() {
   }

   public final boolean call(Vec3 vec) {
      this.setCancelled(false);
      this.setVec(vec);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}