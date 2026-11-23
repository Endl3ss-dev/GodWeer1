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
public final class ActivityButton {
   @NotNull
   public static final ActivityButton.Companion companion = new ActivityButton.Companion(null);
   @NotNull
   private final String label;
   @NotNull
   private final String url;

   public ActivityButton(String label, String url) {
      this.label = label;
      this.url = url;
   }

   @NotNull
   public final String getLabel() {
      return this.label;
   }

   @NotNull
   public final String getUrl() {
      return this.url;
   }

   @NotNull
   public final String getLabel2() {
      return this.label;
   }

   @NotNull
   public final String getUrl2() {
      return this.url;
   }

   @NotNull
   @Override
   public String toString() {
      return "ActivityButton(label=" + this.label + ", url=" + this.url + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.label.hashCode();
      return hashCode * 31 + this.url.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ActivityButton activityButton)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.label, activityButton.label) ? false : Intrinsics.areEqual(this.url, activityButton.url);
      }
   }

   @JvmStatic
   public static final void applyEncodeStringElement(ActivityButton activityButton, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      compositeEncoder.encodeStringElement(serialDescriptor, 0, activityButton.label);
      compositeEncoder.encodeStringElement(serialDescriptor, 1, activityButton.url);
   }

   public ActivityButton(int value, String text, String text2, SerializationConstructorMarker serializationConstructorMarker) {
      if (3 != (3)) {
         PluginExceptionsKt.throwMissingFieldException(value, 3, ActivityButton$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.label = text;
      this.url = text2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)ActivityButton$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}