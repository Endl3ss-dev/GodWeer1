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
import org.jetbrains.annotations.Nullable;

@Serializable
public final class Activity {
   @NotNull
   public static final Activity.Companion companion = new Activity.Companion(null);
   @Nullable
   private final String text;
   @Nullable
   private final String text2;
   @NotNull
   private final ActivityTimestamp timestamps;
   @Nullable
   private final ActivityAsset activityAsset;
   @Nullable
   private final List items;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   public Activity(String text, String text2, ActivityTimestamp timestamps, ActivityAsset activityAsset, List items) {
      this.text = text;
      this.text2 = text2;
      this.timestamps = timestamps;
      this.activityAsset = activityAsset;
      this.items = items;
   }

   @Nullable
   public final String getText() {
      return this.text;
   }

   @Nullable
   public final String getText2() {
      return this.text2;
   }

   @NotNull
   public final ActivityTimestamp getTimestamps() {
      return this.timestamps;
   }

   @Nullable
   public final ActivityAsset getActivityAsset() {
      return this.activityAsset;
   }

   @Nullable
   public final List getItems() {
      return this.items;
   }

   @Nullable
   public final String getText3() {
      return this.text;
   }

   @Nullable
   public final String getText4() {
      return this.text2;
   }

   @NotNull
   public final ActivityTimestamp getTimestamps2() {
      return this.timestamps;
   }

   @Nullable
   public final ActivityAsset getActivityAsset2() {
      return this.activityAsset;
   }

   @Nullable
   public final List getItems2() {
      return this.items;
   }

   @NotNull
   @Override
   public String toString() {
      List items2 = this.items;
      ActivityAsset activityAsset2 = this.activityAsset;
      ActivityTimestamp timestamps2 = this.timestamps;
      String text22 = this.text2;
      String text3 = this.text;
      return "Activity(details=" + text3 + ", state=" + text22 + ", timestamps=" + timestamps2 + ", assets=" + activityAsset2 + ", buttons=" + items2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.text == null ? 0 : this.text.hashCode();
      hashCode = hashCode * 31 + (this.text2 == null ? 0 : this.text2.hashCode());
      hashCode = hashCode * 31 + this.timestamps.hashCode();
      hashCode = hashCode * 31 + (this.activityAsset == null ? 0 : this.activityAsset.hashCode());
      return hashCode * 31 + (this.items == null ? 0 : this.items.hashCode());
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Activity activity)) {
         return false;
      } else if (!Intrinsics.areEqual(this.text, activity.text)) {
         return false;
      } else if (!Intrinsics.areEqual(this.text2, activity.text2)) {
         return false;
      } else if (!Intrinsics.areEqual(this.timestamps, activity.timestamps)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.activityAsset, activity.activityAsset) ? false : Intrinsics.areEqual(this.items, activity.items);
      }
   }

   @JvmStatic
   public static final void applyEncodeNullableSerializableElement(Activity activity, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      Lazy[] lazys2 = lazys;
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 0, (SerializationStrategy)StringSerializer.INSTANCE, activity.text);
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 1, (SerializationStrategy)StringSerializer.INSTANCE, activity.text2);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 2, (SerializationStrategy)ActivityTimestamp$$serializer.INSTANCE, activity.timestamps);
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 3, (SerializationStrategy)ActivityAsset$$serializer.INSTANCE, activity.activityAsset);
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 4, (SerializationStrategy)lazys2[4].getValue(), activity.items);
   }

   public Activity(
      int value,
      String text,
      String text2,
      ActivityTimestamp activityTimestamp,
      ActivityAsset activityAsset,
      List items,
      SerializationConstructorMarker serializationConstructorMarker
   ) {
      if (31 != (31)) {
         PluginExceptionsKt.throwMissingFieldException(value, 31, Activity$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.text = text;
      this.text2 = text2;
      this.timestamps = activityTimestamp;
      this.activityAsset = activityAsset;
      this.items = items;
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{
         null,
         null,
         null,
         null,
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> (KSerializer)(new ArrayListSerializer((KSerializer)ActivityButton$$serializer.INSTANCE)))
      };
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)Activity$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}