// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import net.minecraft.network.protocol.Packet;

public final class BTPacket {
   @NotNull
   private final Packet packet;
   private final long time;

   public BTPacket(Packet packet, long time) {
      this.packet = packet;
      this.time = time;
   }

   @NotNull
   public final Packet getPacket() {
      return this.packet;
   }

   public final long getTime() {
      return this.time;
   }

   @NotNull
   public final Packet getPacket2() {
      return this.packet;
   }

   public final long getTime2() {
      return this.time;
   }

   @NotNull
   @Override
   public String toString() {
      long time2 = this.time;
      Packet packet2 = this.packet;
      return "BTPacket(packet=" + packet2 + ", timestamp=" + time2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.packet.hashCode();
      return hashCode * 31 + Long.hashCode(this.time);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof BTPacket btPacket)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.packet, btPacket.packet) ? false : this.time == btPacket.time;
      }
   }
}