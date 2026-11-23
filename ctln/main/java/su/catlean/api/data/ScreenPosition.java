// deobf by pusia
package su.catlean.api.data;

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
public final class ScreenPosition {
   @NotNull
   public static final ScreenPosition.Companion companion = new ScreenPosition.Companion(null);
   private float amount;
   private float amount2;

   public ScreenPosition(float amount, float amount2) {
      this.amount = amount;
      this.amount2 = amount2;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final void setAmount(float amount) {
      this.amount = amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final void setAmount2(float amount) {
      this.amount2 = amount;
   }

   public final float getAmount3() {
      return this.amount;
   }

   public final float getAmount4() {
      return this.amount2;
   }

   @NotNull
   public final ScreenPosition computeScreenPosition(float amount, float amount2) {
      return new ScreenPosition(amount, amount2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ScreenPosition(x=" + this.amount + ", y=" + this.amount2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Float.hashCode(this.amount);
      return hashCode * 31 + Float.hashCode(this.amount2);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ScreenPosition screenPosition)) {
         return false;
      } else {
         return Float.compare(this.amount, screenPosition.amount) != 0 ? false : Float.compare(this.amount2, screenPosition.amount2) == 0;
      }
   }

   @JvmStatic
   public static final void applyCompare(ScreenPosition screenPosition, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 0) ? true : Float.compare(screenPosition.amount, 0.5F) != 0) {
         compositeEncoder.encodeFloatElement(serialDescriptor, 0, screenPosition.amount);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 1) ? true : Float.compare(screenPosition.amount2, 0.5F) != 0) {
         compositeEncoder.encodeFloatElement(serialDescriptor, 1, screenPosition.amount2);
      }
   }

   public ScreenPosition(int value, float amount, float amount2, SerializationConstructorMarker serializationConstructorMarker) {
      if ((0) != 0) {
         PluginExceptionsKt.throwMissingFieldException(value, 0, ScreenPosition$$serializer.INSTANCE.getDescriptor());
      }

      super();
      if ((value) == 0) {
         this.amount = 0.5F;
      } else {
         this.amount = amount;
      }

      if ((value) == 0) {
         this.amount2 = 0.5F;
      } else {
         this.amount2 = amount2;
      }
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)ScreenPosition$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}