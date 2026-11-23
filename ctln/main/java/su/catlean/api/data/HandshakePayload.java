// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Serializable
public final class HandshakePayload {
   @NotNull
   public static final HandshakePayload.Companion companion = new HandshakePayload.Companion(null);
   private final int value;
   @NotNull
   private final String clientId;

   public HandshakePayload(int value, String clientId) {
      this.value = value;
      this.clientId = clientId;
   }

   public final int getValue() {
      return this.value;
   }

   @NotNull
   public final String getClientId() {
      return this.clientId;
   }

   public final int getValue2() {
      return this.value;
   }

   @NotNull
   public final String getClientId2() {
      return this.clientId;
   }

   @NotNull
   @Override
   public String toString() {
      return "HandshakePayload(v=" + this.value + ", clientId=" + this.clientId + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Integer.hashCode(this.value);
      return hashCode * 31 + this.clientId.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof HandshakePayload handshakePayload)) {
         return false;
      } else {
         return this.value != handshakePayload.value ? false : Intrinsics.areEqual(this.clientId, handshakePayload.clientId);
      }
   }

   @JvmStatic
   public static final void applyEncodeIntElement(HandshakePayload handshakePayload, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      compositeEncoder.encodeIntElement(serialDescriptor, 0, handshakePayload.value);
      compositeEncoder.encodeStringElement(serialDescriptor, 1, handshakePayload.clientId);
   }

   public HandshakePayload(int value, int value2, String text, SerializationConstructorMarker serializationConstructorMarker) {
      if (3 != (3)) {
         PluginExceptionsKt.throwMissingFieldException(value, 3, HandshakePayload$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.value = value2;
      this.clientId = text;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)HandshakePayload$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}