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
public final class WayPointConfig {
   @NotNull
   public static final WayPointConfig.Companion companion = new WayPointConfig.Companion(null);
   @NotNull
   private final List wayPoints;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   public WayPointConfig(List wayPoints) {
      this.wayPoints = wayPoints;
   }

   @NotNull
   public final List getWayPoints() {
      return this.wayPoints;
   }

   @NotNull
   public final List getWayPoints2() {
      return this.wayPoints;
   }

   @NotNull
   @Override
   public String toString() {
      return "WayPointConfig(wayPoints=" + this.wayPoints + ")";
   }

   @Override
   public int hashCode() {
      return this.wayPoints.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof WayPointConfig wayPointConfig) ? false : Intrinsics.areEqual(this.wayPoints, wayPointConfig.wayPoints);
      }
   }

   @JvmStatic
   public static final void applyEncodeSerializableElement(WayPointConfig wayPointConfig, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      Lazy[] lazys2 = lazys;
      compositeEncoder.encodeSerializableElement(serialDescriptor, 0, (SerializationStrategy)lazys2[0].getValue(), wayPointConfig.wayPoints);
   }

   public WayPointConfig(int value, List items, SerializationConstructorMarker serializationConstructorMarker) {
      if (1 != (1)) {
         PluginExceptionsKt.throwMissingFieldException(value, 1, WayPointConfig$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.wayPoints = items;
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> (KSerializer)(new ArrayListSerializer((KSerializer)WayPoint$$serializer.INSTANCE)))
      };
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)WayPointConfig$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}