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
public final class SettingValue {
   @NotNull
   public static final SettingValue.Companion companion = new SettingValue.Companion(null);
   @NotNull
   private final String id;
   @NotNull
   private final String value;

   public SettingValue(String id, String value) {
      this.id = id;
      this.value = value;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getValue() {
      return this.value;
   }

   @NotNull
   public final String getId2() {
      return this.id;
   }

   @NotNull
   public final String getValue2() {
      return this.value;
   }

   @NotNull
   @Override
   public String toString() {
      return "SettingValue(id=" + this.id + ", value=" + this.value + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      return hashCode * 31 + this.value.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof SettingValue settingValue)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.id, settingValue.id) ? false : Intrinsics.areEqual(this.value, settingValue.value);
      }
   }

   @JvmStatic
   public static final void applyEncodeStringElement(SettingValue settingValue, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      compositeEncoder.encodeStringElement(serialDescriptor, 0, settingValue.id);
      compositeEncoder.encodeStringElement(serialDescriptor, 1, settingValue.value);
   }

   public SettingValue(int value, String text, String text2, SerializationConstructorMarker serializationConstructorMarker) {
      if (3 != (3)) {
         PluginExceptionsKt.throwMissingFieldException(value, 3, SettingValue$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.id = text;
      this.value = text2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)SettingValue$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}