// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.services.discord.ipc.IpcOpcode;

public final class Packet {
   @NotNull
   private final IpcOpcode opcode;
   @NotNull
   private final PacketPayload data;

   public Packet(IpcOpcode opcode, PacketPayload data) {
      this.opcode = opcode;
      this.data = data;
   }

   @NotNull
   public final IpcOpcode getOpcode() {
      return this.opcode;
   }

   @NotNull
   public final PacketPayload getData() {
      return this.data;
   }

   @NotNull
   public final IpcOpcode getOpcode2() {
      return this.opcode;
   }

   @NotNull
   public final PacketPayload getData2() {
      return this.data;
   }

   @NotNull
   @Override
   public String toString() {
      return "Packet(opcode=" + this.opcode + ", data=" + this.data + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.opcode.hashCode();
      return hashCode * 31 + this.data.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Packet packet)) {
         return false;
      } else {
         return this.opcode != packet.opcode ? false : Intrinsics.areEqual(this.data, packet.data);
      }
   }
}