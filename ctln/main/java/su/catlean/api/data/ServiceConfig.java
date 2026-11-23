// deobf by pusia
package su.catlean.api.data;

import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.collections.CollectionsKt;
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
import org.jetbrains.annotations.NotNull;

@Serializable
public final class ServiceConfig {
   @NotNull
   public static final ServiceConfig.Companion companion = new ServiceConfig.Companion(null);
   @NotNull
   private final String id;
   @NotNull
   private List modules;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   public ServiceConfig(String id, List modules) {
      this.id = id;
      this.modules = modules;
   }

   public ServiceConfig(String text, List items, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         items = CollectionsKt.emptyList();
      }

      this(text, items);
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final List getModules() {
      return this.modules;
   }

   public final void updateItems(List items) {
      this.modules = items;
   }

   @NotNull
   public final String getId2() {
      return this.id;
   }

   @NotNull
   public final List getModules2() {
      return this.modules;
   }

   @NotNull
   @Override
   public String toString() {
      return "ServiceConfig(id=" + this.id + ", modules=" + this.modules + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      return hashCode * 31 + this.modules.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ServiceConfig serviceConfig)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.id, serviceConfig.id) ? false : Intrinsics.areEqual(this.modules, serviceConfig.modules);
      }
   }

   @JvmStatic
   public static final void applyEncodeSerializableElement(ServiceConfig serviceConfig, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      Lazy[] lazys2 = lazys;
      compositeEncoder.encodeStringElement(serialDescriptor, 0, serviceConfig.id);
      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 1) ? true : !Intrinsics.areEqual(serviceConfig.modules, CollectionsKt.emptyList())) {
         compositeEncoder.encodeSerializableElement(serialDescriptor, 1, (SerializationStrategy)lazys2[1].getValue(), serviceConfig.modules);
      }
   }

   public ServiceConfig(int value, String text, List items, SerializationConstructorMarker serializationConstructorMarker) {
      if (1 != (1)) {
         PluginExceptionsKt.throwMissingFieldException(value, 1, ServiceConfig$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.id = text;
      if ((value) == 0) {
         this.modules = CollectionsKt.emptyList();
      } else {
         this.modules = items;
      }
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{
         null, LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> (KSerializer)(new ArrayListSerializer((KSerializer)ModuleValue$$serializer.INSTANCE)))
      };
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)ServiceConfig$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}