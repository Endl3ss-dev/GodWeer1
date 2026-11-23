// deobf by pusia
package su.catlean.api.data;

import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.services.discord.ipc.IpcCommand;

@Serializable
public final class FramePayload {
   @NotNull
   public static final FramePayload.Companion companion = new FramePayload.Companion(null);
   @NotNull
   private final IpcCommand cmd;
   @NotNull
   private final FrameArgs args;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   public FramePayload(IpcCommand cmd, FrameArgs args) {
      this.cmd = cmd;
      this.args = args;
   }

   @NotNull
   public final IpcCommand getCmd() {
      return this.cmd;
   }

   @NotNull
   public final FrameArgs getArgs() {
      return this.args;
   }

   @NotNull
   public final IpcCommand getCmd2() {
      return this.cmd;
   }

   @NotNull
   public final FrameArgs getArgs2() {
      return this.args;
   }

   @NotNull
   @Override
   public String toString() {
      return "FramePayload(cmd=" + this.cmd + ", args=" + this.args + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.cmd.hashCode();
      return hashCode * 31 + this.args.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof FramePayload framePayload)) {
         return false;
      } else {
         return this.cmd != framePayload.cmd ? false : Intrinsics.areEqual(this.args, framePayload.args);
      }
   }

   @JvmStatic
   public static final void applyEncodeSerializableElement(FramePayload framePayload, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      Lazy[] lazys2 = lazys;
      compositeEncoder.encodeSerializableElement(serialDescriptor, 0, (SerializationStrategy)lazys2[0].getValue(), framePayload.cmd);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 1, (SerializationStrategy)FrameArgs$$serializer.INSTANCE, framePayload.args);
   }

   public FramePayload(int value, IpcCommand ipcCommand, FrameArgs frameArgs, SerializationConstructorMarker serializationConstructorMarker) {
      if (3 != (3)) {
         PluginExceptionsKt.throwMissingFieldException(value, 3, FramePayload$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.cmd = ipcCommand;
      this.args = frameArgs;
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> IpcCommand.Companion.getKSerializer()), null};
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)FramePayload$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}