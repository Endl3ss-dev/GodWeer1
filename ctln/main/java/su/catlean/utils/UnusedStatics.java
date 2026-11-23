// deobf by pusia
package su.catlean.utils;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlinx.serialization.KSerializer;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.services.discord.ipc.IpcEvent;

public final class UnusedStatics {
   private UnusedStatics() {
   }

   @NotNull
   public final KSerializer getKSerializer() {
      return this.getKSerializer2();
   }

   private final KSerializer getKSerializer2() {
      return (KSerializer)IpcEvent.getLazy().getValue();
   }

   public UnusedStatics(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}