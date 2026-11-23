// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class FlagEvent extends Event {
   @Nullable
   private ClientboundPlayerPositionPacket pos;

   public FlagEvent() {
   }

   @Nullable
   public final ClientboundPlayerPositionPacket getPos() {
      return this.pos;
   }

   public final void setPos(ClientboundPlayerPositionPacket clientboundPlayerPositionPacket) {
      this.pos = clientboundPlayerPositionPacket;
   }

   public final boolean call(ClientboundPlayerPositionPacket pos) {
      this.setCancelled(false);
      this.pos = pos;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}