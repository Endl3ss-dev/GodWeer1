// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.IntSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Serializable
public final class PacketData {
   @NotNull
   public static final PacketData.Companion companion = new PacketData.Companion(null);
   @Nullable
   private final IPCUser iPCUser;
   @Nullable
   private final Integer integer;
   @Nullable
   private final String text;

   public PacketData(IPCUser iPCUser, Integer integer, String text) {
      this.iPCUser = iPCUser;
      this.integer = integer;
      this.text = text;
   }

   @Nullable
   public final IPCUser getIPCUser() {
      return this.iPCUser;
   }

   @Nullable
   public final Integer getInteger() {
      return this.integer;
   }

   @Nullable
   public final String getText() {
      return this.text;
   }

   @Nullable
   public final IPCUser getIPCUser2() {
      return this.iPCUser;
   }

   @Nullable
   public final Integer getInteger2() {
      return this.integer;
   }

   @Nullable
   public final String getText2() {
      return this.text;
   }

   @NotNull
   public final PacketData computePacketData(IPCUser iPCUser, Integer integer, String text) {
      return new PacketData(iPCUser, integer, text);
   }

   @NotNull
   @Override
   public String toString() {
      String text2 = this.text;
      Integer integer2 = this.integer;
      IPCUser iPCUser2 = this.iPCUser;
      return "PacketData(user=" + iPCUser2 + ", code=" + integer2 + ", message=" + text2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.iPCUser == null ? 0 : this.iPCUser.hashCode();
      hashCode = hashCode * 31 + (this.integer == null ? 0 : this.integer.hashCode());
      return hashCode * 31 + (this.text == null ? 0 : this.text.hashCode());
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof PacketData packetData)) {
         return false;
      } else if (!Intrinsics.areEqual(this.iPCUser, packetData.iPCUser)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.integer, packetData.integer) ? false : Intrinsics.areEqual(this.text, packetData.text);
      }
   }

   @JvmStatic
   public static final void applyEncodeNullableSerializableElement(PacketData packetData, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 0, (SerializationStrategy)IPCUser$$serializer.INSTANCE, packetData.iPCUser);
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 1, (SerializationStrategy)IntSerializer.INSTANCE, packetData.integer);
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 2, (SerializationStrategy)StringSerializer.INSTANCE, packetData.text);
   }

   public PacketData(int value, IPCUser iPCUser, Integer integer, String text, SerializationConstructorMarker serializationConstructorMarker) {
      if (7 != (7)) {
         PluginExceptionsKt.throwMissingFieldException(value, 7, PacketData$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.iPCUser = iPCUser;
      this.integer = integer;
      this.text = text;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)PacketData$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}