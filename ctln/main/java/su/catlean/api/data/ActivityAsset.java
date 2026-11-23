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
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Serializable
public final class ActivityAsset {
   @NotNull
   public static final ActivityAsset.Companion companion = new ActivityAsset.Companion(null);
   @Nullable
   private final String text;
   @Nullable
   private final String text2;
   @Nullable
   private final String text3;
   @Nullable
   private final String text4;

   public ActivityAsset(String text, String text2, String text3, String text4) {
      this.text = text;
      this.text2 = text2;
      this.text3 = text3;
      this.text4 = text4;
   }

   @Nullable
   public final String getText() {
      return this.text;
   }

   @Nullable
   public final String getText2() {
      return this.text2;
   }

   @Nullable
   public final String getText3() {
      return this.text3;
   }

   @Nullable
   public final String getText4() {
      return this.text4;
   }

   @Nullable
   public final String getText5() {
      return this.text;
   }

   @Nullable
   public final String getText6() {
      return this.text2;
   }

   @Nullable
   public final String getText7() {
      return this.text3;
   }

   @Nullable
   public final String getText8() {
      return this.text4;
   }

   @NotNull
   public final ActivityAsset computeActivityAsset(String text, String text2, String text3, String text4) {
      return new ActivityAsset(text, text2, text3, text4);
   }

   @NotNull
   @Override
   public String toString() {
      String text42 = this.text4;
      String text32 = this.text3;
      String text22 = this.text2;
      String text5 = this.text;
      return "ActivityAsset(largeImage=" + text5 + ", largeText=" + text22 + ", smallImage=" + text32 + ", smallText=" + text42 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.text == null ? 0 : this.text.hashCode();
      hashCode = hashCode * 31 + (this.text2 == null ? 0 : this.text2.hashCode());
      hashCode = hashCode * 31 + (this.text3 == null ? 0 : this.text3.hashCode());
      return hashCode * 31 + (this.text4 == null ? 0 : this.text4.hashCode());
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ActivityAsset activityAsset)) {
         return false;
      } else if (!Intrinsics.areEqual(this.text, activityAsset.text)) {
         return false;
      } else if (!Intrinsics.areEqual(this.text2, activityAsset.text2)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.text3, activityAsset.text3) ? false : Intrinsics.areEqual(this.text4, activityAsset.text4);
      }
   }

   @JvmStatic
   public static final void applyEncodeNullableSerializableElement(
      ActivityAsset activityAsset, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor
   ) {
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 0, (SerializationStrategy)StringSerializer.INSTANCE, activityAsset.text);
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 1, (SerializationStrategy)StringSerializer.INSTANCE, activityAsset.text2);
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 2, (SerializationStrategy)StringSerializer.INSTANCE, activityAsset.text3);
      compositeEncoder.encodeNullableSerializableElement(serialDescriptor, 3, (SerializationStrategy)StringSerializer.INSTANCE, activityAsset.text4);
   }

   public ActivityAsset(int value, String text, String text2, String text3, String text4, SerializationConstructorMarker serializationConstructorMarker) {
      if (15 != (15)) {
         PluginExceptionsKt.throwMissingFieldException(value, 15, ActivityAsset$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.text = text;
      this.text2 = text2;
      this.text3 = text3;
      this.text4 = text4;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)ActivityAsset$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}