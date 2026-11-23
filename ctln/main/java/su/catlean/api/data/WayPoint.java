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
public final class WayPoint {
   @NotNull
   public static final WayPoint.Companion companion = new WayPoint.Companion(null);
   @NotNull
   private String id;
   private int value;
   private int value2;
   private int value3;
   @NotNull
   private String server;
   private int value4;
   private boolean flag;

   public WayPoint(String id, int value, int value2, int value3, String server, int value4, boolean flag) {
      this.id = id;
      this.value = value;
      this.value2 = value2;
      this.value3 = value3;
      this.server = server;
      this.value4 = value4;
      this.flag = flag;
   }

   public WayPoint(
      String text, int value, int value2, int value3, String text2, int value4, boolean flag, int value5, DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value5 & 16) != 0) {
         text2 = "localhost";
      }

      if ((value5 & 32) != 0) {
         value4 = 0;
      }

      if ((value5 & 64) != 0) {
         flag = true;
      }

      this(text, value, value2, value3, text2, value4, flag);
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   public final void updateText(String text) {
      this.id = text;
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }

   public final int getValue2() {
      return this.value2;
   }

   public final void setValue2(int value) {
      this.value2 = value;
   }

   public final int getValue3() {
      return this.value3;
   }

   public final void setValue3(int value) {
      this.value3 = value;
   }

   @NotNull
   public final String getServer() {
      return this.server;
   }

   public final void updateText2(String text) {
      this.server = text;
   }

   public final int getValue4() {
      return this.value4;
   }

   public final void setValue4(int value) {
      this.value4 = value;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   @NotNull
   public final String getId2() {
      return this.id;
   }

   public final int getValue5() {
      return this.value;
   }

   public final int getValue6() {
      return this.value2;
   }

   public final int getValue7() {
      return this.value3;
   }

   @NotNull
   public final String getServer2() {
      return this.server;
   }

   public final int getValue8() {
      return this.value4;
   }

   public final boolean isFlag2() {
      return this.flag;
   }

   @NotNull
   @Override
   public String toString() {
      boolean flag2 = this.flag;
      int value42 = this.value4;
      String server2 = this.server;
      int value32 = this.value3;
      int value22 = this.value2;
      int value5 = this.value;
      String text = this.id;
      return "WayPoint(id=" + text + ", x=" + value5 + ", y=" + value22 + ", z=" + value32 + ", server=" + server2 + ", dimension=" + value42 + ", visible=" + flag2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      hashCode = hashCode * 31 + Integer.hashCode(this.value);
      hashCode = hashCode * 31 + Integer.hashCode(this.value2);
      hashCode = hashCode * 31 + Integer.hashCode(this.value3);
      hashCode = hashCode * 31 + this.server.hashCode();
      hashCode = hashCode * 31 + Integer.hashCode(this.value4);
      return hashCode * 31 + Boolean.hashCode(this.flag);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof WayPoint wayPoint)) {
         return false;
      } else if (!Intrinsics.areEqual(this.id, wayPoint.id)) {
         return false;
      } else if (this.value != wayPoint.value) {
         return false;
      } else if (this.value2 != wayPoint.value2) {
         return false;
      } else if (this.value3 != wayPoint.value3) {
         return false;
      } else if (!Intrinsics.areEqual(this.server, wayPoint.server)) {
         return false;
      } else {
         return this.value4 != wayPoint.value4 ? false : this.flag == wayPoint.flag;
      }
   }

   @JvmStatic
   public static final void applyEncodeIntElement(WayPoint wayPoint, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      compositeEncoder.encodeStringElement(serialDescriptor, 0, wayPoint.id);
      compositeEncoder.encodeIntElement(serialDescriptor, 1, wayPoint.value);
      compositeEncoder.encodeIntElement(serialDescriptor, 2, wayPoint.value2);
      compositeEncoder.encodeIntElement(serialDescriptor, 3, wayPoint.value3);
      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 4) ? true : !Intrinsics.areEqual(wayPoint.server, "localhost")) {
         compositeEncoder.encodeStringElement(serialDescriptor, 4, wayPoint.server);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 5) ? true : wayPoint.value4 != 0) {
         compositeEncoder.encodeIntElement(serialDescriptor, 5, wayPoint.value4);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 6) ? true : !wayPoint.flag) {
         compositeEncoder.encodeBooleanElement(serialDescriptor, 6, wayPoint.flag);
      }
   }

   public WayPoint(
      int value,
      String text,
      int value2,
      int value3,
      int value4,
      String text2,
      int value5,
      boolean flag,
      SerializationConstructorMarker serializationConstructorMarker
   ) {
      if (15 != (15)) {
         PluginExceptionsKt.throwMissingFieldException(value, 15, WayPoint$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.id = text;
      this.value = value2;
      this.value2 = value3;
      this.value3 = value4;
      if ((value) == 0) {
         this.server = "localhost";
      } else {
         this.server = text2;
      }

      if ((value) == 0) {
         this.value4 = 0;
      } else {
         this.value4 = value5;
      }

      if ((value) == 0) {
         this.flag = true;
      } else {
         this.flag = flag;
      }
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)WayPoint$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}