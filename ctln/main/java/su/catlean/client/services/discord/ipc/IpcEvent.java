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
import su.catlean.utils.UnusedStatics;

@Serializable
public enum IpcEvent {
   ERROR,
   READY;

   @NotNull
   public static final UnusedStatics Companion = new UnusedStatics(null);
   @NotNull
   private static final Lazy<KSerializer<IpcEvent>> lazy = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> EnumsKt.createSimpleEnumSerializer("su.catlean.sw", values()));
   private static final IpcEvent[] ipcEvents = $values();
   private static final EnumEntries<IpcEvent> enumEntries = EnumEntriesKt.enumEntries(ipcEvents);

   private IpcEvent() {
   }

   @NotNull
   public static EnumEntries<IpcEvent> getEnumEntries() {
      return enumEntries;
   }

   private static final IpcEvent[] $values() {
      return new IpcEvent[]{ERROR, READY};
   }

   public static final Lazy<KSerializer<IpcEvent>> getLazy() {
      return lazy;
   }
}