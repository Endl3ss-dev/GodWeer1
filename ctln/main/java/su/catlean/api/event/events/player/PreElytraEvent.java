// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.gofra.Gofra;

public final class PreElytraEvent extends ElytraEvent {
   @NotNull
   public static final PreElytraEvent INSTANCE = new PreElytraEvent();

   private PreElytraEvent() {
   }

   public final boolean call(Vec3 vec) {
      this.setCancelled(false);
      this.setVec(vec);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}