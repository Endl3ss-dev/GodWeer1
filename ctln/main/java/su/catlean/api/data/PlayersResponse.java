// deobf by pusia
package su.catlean.api.data;

import java.util.List;
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
import kotlinx.serialization.internal.ArrayListSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;

@Serializable
public final class PlayersResponse {
   @NotNull
   public static final PlayersResponse.Companion companion = new PlayersResponse.Companion(null);
   @NotNull
   private final List items;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   @NotNull
   public final List getItems() {
      return this.items;
   }

   @NotNull
   public final List getItems2() {
      return this.items;
   }

   @NotNull
   @Override
   public String toString() {
      return "PlayersResponse(players=" + this.items + ")";
   }

   @Override
   public int hashCode() {
      return this.items.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof PlayersResponse playersResponse) ? false : Intrinsics.areEqual(this.items, playersResponse.items);
      }
   }

   @JvmStatic
   public static final void applyEncodeSerializableElement(
      PlayersResponse playersResponse, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor
   ) {
      Lazy[] lazys2 = lazys;
      compositeEncoder.encodeSerializableElement(serialDescriptor, 0, (SerializationStrategy)lazys2[0].getValue(), playersResponse.items);
   }

   public PlayersResponse(int value, List items, SerializationConstructorMarker serializationConstructorMarker) {
      if (1 != (1)) {
         PluginExceptionsKt.throwMissingFieldException(value, 1, PlayersResponse$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.items = items;
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> (KSerializer)(new ArrayListSerializer((KSerializer)StringSerializer.INSTANCE)))
      };
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)PlayersResponse$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}