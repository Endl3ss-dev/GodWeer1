// deobf by pusia
package su.catlean.api.event.events.network;

import net.minecraft.network.protocol.Packet;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class ReceivePacket extends Event {
   @NotNull
   private final Packet<?> packet;

   public ReceivePacket(Packet<?> packet) {
      this.packet = packet;
   }

   @NotNull
   public final Packet<?> getPacket() {
      return this.packet;
   }
}