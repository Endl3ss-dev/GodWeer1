// deobf by pusia
package su.catlean.api.setting.values;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Serializable
public final class Bind {
   @NotNull
   public static final Bind.Companion companion = new Bind.Companion(null);
   private int value;
   private boolean flag;
   private boolean flag2;

   public Bind(int value, boolean flag, boolean flag2) {
      this.value = value;
      this.flag = flag;
      this.flag2 = flag2;
   }

   public Bind(int value, boolean flag, boolean flag2, int value2, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value2 & 1) != 0) {
         value = -1;
      }

      if ((value2 & 2) != 0) {
         flag = false;
      }

      if ((value2 & 4) != 0) {
         flag2 = false;
      }

      this(value, flag, flag2);
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final boolean isFlag2() {
      return this.flag2;
   }

   public final void setFlag2(boolean flag) {
      this.flag2 = flag;
   }

   public final int getValue2() {
      return this.value;
   }

   public final boolean isFlag3() {
      return this.flag;
   }

   public final boolean isFlag4() {
      return this.flag2;
   }

   @NotNull
   public final Bind computeBind(int value, boolean flag, boolean flag2) {
      return new Bind(value, flag, flag2);
   }

   @NotNull
   @Override
   public String toString() {
      boolean flag22 = this.flag2;
      boolean flag3 = this.flag;
      int value2 = this.value;
      return "Bind(key=" + value2 + ", hold=" + flag3 + ", mouse=" + flag22 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Integer.hashCode(this.value);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag);
      return hashCode * 31 + Boolean.hashCode(this.flag2);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Bind bind)) {
         return false;
      } else if (this.value != bind.value) {
         return false;
      } else {
         return this.flag != bind.flag ? false : this.flag2 == bind.flag2;
      }
   }

   @JvmStatic
   public static final void applyShouldEncodeElementDefault(Bind bind, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 0) ? true : bind.value != -1) {
         compositeEncoder.encodeIntElement(serialDescriptor, 0, bind.value);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 1) ? true : bind.flag) {
         compositeEncoder.encodeBooleanElement(serialDescriptor, 1, bind.flag);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 2) ? true : bind.flag2) {
         compositeEncoder.encodeBooleanElement(serialDescriptor, 2, bind.flag2);
      }
   }

   public Bind(int value, int value2, boolean flag, boolean flag2, SerializationConstructorMarker serializationConstructorMarker) {
      if ((0) != 0) {
         PluginExceptionsKt.throwMissingFieldException(value, 0, Bind$$serializer.INSTANCE.getDescriptor());
      }

      super();
      if ((value) == 0) {
         this.value = -1;
      } else {
         this.value = value2;
      }

      if ((value) == 0) {
         this.flag = false;
      } else {
         this.flag = flag;
      }

      if ((value) == 0) {
         this.flag2 = false;
      } else {
         this.flag2 = flag2;
      }
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)Bind$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}