// deobf by pusia
package su.catlean.client.services.discord.ipc;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlinx.serialization.KSerializer;
import org.jetbrains.annotations.NotNull;

public final class IpcCommandCompanion {
   private IpcCommandCompanion() {
   }

   @NotNull
   public final KSerializer getKSerializer() {
      return this.getKSerializer2();
   }

   private final KSerializer getKSerializer2() {
      return (KSerializer)IpcCommand.getLazy().getValue();
   }

   public IpcCommandCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}