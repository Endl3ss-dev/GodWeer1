// deobf by pusia
package su.catlean.client.services.discord.ipc;

import kotlinx.serialization.KSerializer;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.internal.EnumsKt;
import org.jetbrains.annotations.NotNull;

@Serializable
public enum IpcCommand {
   DISPATCH,
   SET_ACTIVITY;

   @NotNull
   public static final IpcCommandCompanion Companion = new IpcCommandCompanion(null);
   @NotNull
   private static final Lazy<KSerializer<IpcCommand>> lazy = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> EnumsKt.createSimpleEnumSerializer("su.catlean.md", values()));
   private static final IpcCommand[] ipcCommands = $values();
   private static final EnumEntries<IpcCommand> enumEntries = EnumEntriesKt.enumEntries(ipcCommands);

   private IpcCommand() {
   }

   @NotNull
   public static EnumEntries<IpcCommand> getEnumEntries() {
      return enumEntries;
   }

   private static final IpcCommand[] $values() {
      return new IpcCommand[]{DISPATCH, SET_ACTIVITY};
   }

   public static final Lazy<KSerializer<IpcCommand>> getLazy() {
      return lazy;
   }
}