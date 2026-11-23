// deobf by pusia
package su.catlean.client.services.discord.ipc;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

public enum IpcOpcode {
   HANDSHAKE,
   FRAME,
   CLOSE,
   PING,
   PONG;

   private static final IpcOpcode[] ipcOpcodes = $values();
   private static final EnumEntries<IpcOpcode> enumEntries = EnumEntriesKt.enumEntries(ipcOpcodes);

   private IpcOpcode() {
   }

   @NotNull
   public static EnumEntries<IpcOpcode> getEnumEntries() {
      return enumEntries;
   }

   private static final IpcOpcode[] $values() {
      return new IpcOpcode[]{HANDSHAKE, FRAME, CLOSE, PING, PONG};
   }
}