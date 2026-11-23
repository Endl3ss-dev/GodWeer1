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
public final class Proxy {
   @NotNull
   public static final Proxy.Companion companion = new Proxy.Companion(null);
   @NotNull
   private String id;
   @NotNull
   private String ip;
   private int value;
   @NotNull
   private String user;
   @NotNull
   private String password;
   private boolean flag;
   private int value2;

   public Proxy(String id, String ip, int value, String user, String password, boolean flag, int value2) {
      this.id = id;
      this.ip = ip;
      this.value = value;
      this.user = user;
      this.password = password;
      this.flag = flag;
      this.value2 = value2;
   }

   public Proxy(
      String text, String text2, int value, String text3, String text4, boolean flag, int value2, int value3, DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value3 & 64) != 0) {
         value2 = -1;
      }

      this(text, text2, value, text3, text4, flag, value2);
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   public final void updateText(String text) {
      this.id = text;
   }

   @NotNull
   public final String getIp() {
      return this.ip;
   }

   public final void updateText2(String text) {
      this.ip = text;
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }

   @NotNull
   public final String getUser() {
      return this.user;
   }

   public final void updateText3(String text) {
      this.user = text;
   }

   @NotNull
   public final String getPassword() {
      return this.password;
   }

   public final void updateText4(String text) {
      this.password = text;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final int getValue2() {
      return this.value2;
   }

   public final void setValue2(int value) {
      this.value2 = value;
   }

   @NotNull
   public final String getId2() {
      return this.id;
   }

   @NotNull
   public final String getIp2() {
      return this.ip;
   }

   public final int getValue3() {
      return this.value;
   }

   @NotNull
   public final String getUser2() {
      return this.user;
   }

   @NotNull
   public final String getPassword2() {
      return this.password;
   }

   public final boolean isFlag2() {
      return this.flag;
   }

   public final int getValue4() {
      return this.value2;
   }

   @NotNull
   @Override
   public String toString() {
      int value22 = this.value2;
      boolean flag2 = this.flag;
      String password2 = this.password;
      String user2 = this.user;
      int value3 = this.value;
      String text = this.ip;
      String text2 = this.id;
      return "Proxy(id=" + text2 + ", ip=" + text + ", port=" + value3 + ", user=" + user2 + ", password=" + password2 + ", socks4=" + flag2 + ", ping=" + value22 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      hashCode = hashCode * 31 + this.ip.hashCode();
      hashCode = hashCode * 31 + Integer.hashCode(this.value);
      hashCode = hashCode * 31 + this.user.hashCode();
      hashCode = hashCode * 31 + this.password.hashCode();
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag);
      return hashCode * 31 + Integer.hashCode(this.value2);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Proxy proxy)) {
         return false;
      } else if (!Intrinsics.areEqual(this.id, proxy.id)) {
         return false;
      } else if (!Intrinsics.areEqual(this.ip, proxy.ip)) {
         return false;
      } else if (this.value != proxy.value) {
         return false;
      } else if (!Intrinsics.areEqual(this.user, proxy.user)) {
         return false;
      } else if (!Intrinsics.areEqual(this.password, proxy.password)) {
         return false;
      } else {
         return this.flag != proxy.flag ? false : this.value2 == proxy.value2;
      }
   }

   @JvmStatic
   public static final void applyEncodeStringElement(Proxy proxy, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      compositeEncoder.encodeStringElement(serialDescriptor, 0, proxy.id);
      compositeEncoder.encodeStringElement(serialDescriptor, 1, proxy.ip);
      compositeEncoder.encodeIntElement(serialDescriptor, 2, proxy.value);
      compositeEncoder.encodeStringElement(serialDescriptor, 3, proxy.user);
      compositeEncoder.encodeStringElement(serialDescriptor, 4, proxy.password);
      compositeEncoder.encodeBooleanElement(serialDescriptor, 5, proxy.flag);
   }

   public Proxy(
      int value, String text, String text2, int value2, String text3, String text4, boolean flag, SerializationConstructorMarker serializationConstructorMarker
   ) {
      if (63 != (63)) {
         PluginExceptionsKt.throwMissingFieldException(value, 63, Proxy$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.id = text;
      this.ip = text2;
      this.value = value2;
      this.user = text3;
      this.password = text4;
      this.flag = flag;
      this.value2 = -1;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)Proxy$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}