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
import org.jetbrains.annotations.NotNull;

@Serializable
public final class ModuleValue {
   @NotNull
   public static final ModuleValue.Companion companion = new ModuleValue.Companion(null);
   @NotNull
   private final String id;
   @NotNull
   private final List settings;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   public ModuleValue(String id, List settings) {
      this.id = id;
      this.settings = settings;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final List getSettings() {
      return this.settings;
   }

   @NotNull
   public final String getId2() {
      return this.id;
   }

   @NotNull
   public final List getSettings2() {
      return this.settings;
   }

   @NotNull
   @Override
   public String toString() {
      return "ModuleValue(id=" + this.id + ", settings=" + this.settings + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      return hashCode * 31 + this.settings.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ModuleValue moduleValue)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.id, moduleValue.id) ? false : Intrinsics.areEqual(this.settings, moduleValue.settings);
      }
   }

   @JvmStatic
   public static final void applyEncodeSerializableElement(ModuleValue moduleValue, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      Lazy[] lazys2 = lazys;
      compositeEncoder.encodeStringElement(serialDescriptor, 0, moduleValue.id);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 1, (SerializationStrategy)lazys2[1].getValue(), moduleValue.settings);
   }

   public ModuleValue(int value, String text, List items, SerializationConstructorMarker serializationConstructorMarker) {
      if (3 != (3)) {
         PluginExceptionsKt.throwMissingFieldException(value, 3, ModuleValue$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.id = text;
      this.settings = items;
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{
         null, LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> (KSerializer)(new ArrayListSerializer((KSerializer)SettingValue$$serializer.INSTANCE)))
      };
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)ModuleValue$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}