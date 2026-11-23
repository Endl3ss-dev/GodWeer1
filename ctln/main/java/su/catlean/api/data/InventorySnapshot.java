// deobf by pusia
package su.catlean.api.data;

import java.util.Map;
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
import kotlinx.serialization.internal.IntSerializer;
import kotlinx.serialization.internal.LinkedHashMapSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.serialization.ItemSerializer;

@Serializable
public final class InventorySnapshot {
   @NotNull
   public static final InventorySnapshot.Companion companion = new InventorySnapshot.Companion(null);
   @NotNull
   private String id;
   @NotNull
   private Map items;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   public InventorySnapshot(String id, Map items) {
      this.id = id;
      this.items = items;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final Map getItems() {
      return this.items;
   }

   @NotNull
   public final String getId2() {
      return this.id;
   }

   @NotNull
   public final Map getItems2() {
      return this.items;
   }

   @NotNull
   @Override
   public String toString() {
      return "InventorySnapshot(id=" + this.id + ", items=" + this.items + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      return hashCode * 31 + this.items.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof InventorySnapshot inventorySnapshot)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.id, inventorySnapshot.id) ? false : Intrinsics.areEqual(this.items, inventorySnapshot.items);
      }
   }

   @JvmStatic
   public static final void applyEncodeSerializableElement(
      InventorySnapshot inventorySnapshot, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor
   ) {
      Lazy[] lazys2 = lazys;
      compositeEncoder.encodeStringElement(serialDescriptor, 0, inventorySnapshot.id);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 1, (SerializationStrategy)lazys2[1].getValue(), inventorySnapshot.items);
   }

   public InventorySnapshot(int value, String text, Map map, SerializationConstructorMarker serializationConstructorMarker) {
      if (3 != (3)) {
         PluginExceptionsKt.throwMissingFieldException(value, 3, InventorySnapshot$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.id = text;
      this.items = map;
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{
         null,
         LazyKt.lazy(
            LazyThreadSafetyMode.PUBLICATION, () -> (KSerializer)(new LinkedHashMapSerializer((KSerializer)IntSerializer.INSTANCE, new ItemSerializer()))
         )
      };
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)InventorySnapshot$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}