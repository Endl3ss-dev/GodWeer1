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
import org.jetbrains.annotations.NotNull;

@Serializable
public final class FrameArgs {
   @NotNull
   public static final FrameArgs.Companion companion = new FrameArgs.Companion(null);
   private final int value;
   @NotNull
   private final Activity activity;

   public FrameArgs(int value, Activity activity) {
      this.value = value;
      this.activity = activity;
   }

   public final int getValue() {
      return this.value;
   }

   @NotNull
   public final Activity getActivity() {
      return this.activity;
   }

   public final int getValue2() {
      return this.value;
   }

   @NotNull
   public final Activity getActivity2() {
      return this.activity;
   }

   @NotNull
   @Override
   public String toString() {
      return "FrameArgs(pid=" + this.value + ", activity=" + this.activity + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Integer.hashCode(this.value);
      return hashCode * 31 + this.activity.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof FrameArgs frameArgs)) {
         return false;
      } else {
         return this.value != frameArgs.value ? false : Intrinsics.areEqual(this.activity, frameArgs.activity);
      }
   }

   @JvmStatic
   public static final void applyEncodeIntElement(FrameArgs frameArgs, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      compositeEncoder.encodeIntElement(serialDescriptor, 0, frameArgs.value);
      compositeEncoder.encodeSerializableElement(serialDescriptor, 1, (SerializationStrategy)Activity$$serializer.INSTANCE, frameArgs.activity);
   }

   public FrameArgs(int value, int value2, Activity activity, SerializationConstructorMarker serializationConstructorMarker) {
      if (3 != (3)) {
         PluginExceptionsKt.throwMissingFieldException(value, 3, FrameArgs$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.value = value2;
      this.activity = activity;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)FrameArgs$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}