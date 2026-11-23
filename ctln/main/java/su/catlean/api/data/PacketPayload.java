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
import org.jetbrains.annotations.Nullable;
import su.catlean.client.services.discord.ipc.IpcCommand;
import su.catlean.client.services.discord.ipc.IpcEvent;

@Serializable
public final class PacketPayload {
   @NotNull
   public static final PacketPayload.Companion companion = new PacketPayload.Companion(null);
   @Nullable
   private final IpcEvent ipcEvent;
   @Nullable
   private final IpcCommand ipcCommand;
   @NotNull
   private final PacketData packetData;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   @Nullable
   public final IpcEvent getIpcEvent() {
      return this.ipcEvent;
   }

   @Nullable
   public final IpcCommand getIpcCommand() {
      return this.ipcCommand;
   }

   @NotNull
   public final PacketData getPacketData() {
      return this.packetData;
   }

   @Nullable
   public final IpcEvent getIpcEvent2() {
      return this.ipcEvent;
   }

   @Nullable
   public final IpcCommand getIpcCommand2() {
      return this.ipcCommand;
   }

   @NotNull
   public final PacketData getPacketData2() {
      return this.packetData;
   }

   @NotNull
   @Override
   public String toString() {
      PacketData packetData2 = this.packetData;
      IpcCommand ipcCommand2 = this.ipcCommand;
      IpcEvent ipcEvent2 = this.ipcEvent;
      return "PacketPayload(evt=" + ipcEvent2 + ", cmd=" + ipcCommand2 + ", data=" + packetData2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.ipcEvent == null ? 0 : this.ipcEvent.hashCode();
      hashCode = hashCode * 31 + (this.ipcCommand == null ? 0 : this.ipcCommand.hashCode());
      return hashCode * 31 + this.packetData.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof PacketPayload packetPayload)) {
         return false;
      } else if (this.ipcEvent != packetPayload.ipcEvent) {
         return false;
      } else {
         return this.ipcCommand != packetPayload.ipcCommand ? false : Intrinsics.areEqual(this.packetData, packetPayload.packetData);
      }
   }

   @JvmStatic
   public static final void applyEncodeNullableSerializableElement(
      PacketPayload packetPayload, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor
   ) {
      Lazy[] lazys2 = lazys;
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 0, (SerializationStrategy)lazys2[0].getValue(), packetPayload.ipcEvent);
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 1, (SerializationStrategy)lazys2[1].getValue(), packetPayload.ipcCommand);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 2, (SerializationStrategy)PacketData$$serializer.INSTANCE, packetPayload.packetData);
   }

   public PacketPayload(
      int value, IpcEvent ipcEvent, IpcCommand ipcCommand, PacketData packetData, SerializationConstructorMarker serializationConstructorMarker
   ) {
      if (7 != (7)) {
         PluginExceptionsKt.throwMissingFieldException(value, 7, PacketPayload$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.ipcEvent = ipcEvent;
      this.ipcCommand = ipcCommand;
      this.packetData = packetData;
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> IpcEvent.Companion.getKSerializer()),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> IpcCommand.Companion.getKSerializer()),
         null
      };
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)PacketPayload$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}