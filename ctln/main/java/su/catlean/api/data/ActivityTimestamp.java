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
import kotlinx.serialization.internal.LongSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Serializable
public final class ActivityTimestamp {
   @NotNull
   public static final ActivityTimestamp.Companion companion = new ActivityTimestamp.Companion(null);
   private final long time;
   @Nullable
   private final Long longValue;

   public ActivityTimestamp(long time, Long longValue) {
      this.time = time;
      this.longValue = longValue;
   }

   public ActivityTimestamp(long time, Long longValue, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         longValue = null;
      }

      this(time, longValue);
   }

   public final long getTime() {
      return this.time;
   }

   @Nullable
   public final Long getLongValue() {
      return this.longValue;
   }

   public final long getTime2() {
      return this.time;
   }

   @Nullable
   public final Long getLongValue2() {
      return this.longValue;
   }

   @NotNull
   public final ActivityTimestamp computeActivityTimestamp(long time, Long longValue) {
      return new ActivityTimestamp(time, longValue);
   }

   @NotNull
   @Override
   public String toString() {
      Long longValue2 = this.longValue;
      long time2 = this.time;
      return "ActivityTimestamp(start=" + time2 + ", end=" + longValue2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Long.hashCode(this.time);
      return hashCode * 31 + (this.longValue == null ? 0 : this.longValue.hashCode());
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ActivityTimestamp activityTimestamp)) {
         return false;
      } else {
         return this.time != activityTimestamp.time ? false : Intrinsics.areEqual(this.longValue, activityTimestamp.longValue);
      }
   }

   @JvmStatic
   public static final void applyEncodeLongElement(ActivityTimestamp activityTimestamp, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      compositeEncoder.encodeLongElement(serialDescriptor, 0, activityTimestamp.time);
      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 1) ? true : activityTimestamp.longValue != null) {
         compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 1, (SerializationStrategy)LongSerializer.INSTANCE, activityTimestamp.longValue);
      }
   }

   public ActivityTimestamp(int value, long time, Long longValue, SerializationConstructorMarker serializationConstructorMarker) {
      if (1 != (1)) {
         PluginExceptionsKt.throwMissingFieldException(value, 1, ActivityTimestamp$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.time = time;
      if ((value) == 0) {
         this.longValue = null;
      } else {
         this.longValue = longValue;
      }
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)ActivityTimestamp$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}