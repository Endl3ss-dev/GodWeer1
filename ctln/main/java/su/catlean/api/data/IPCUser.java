// deobf by pusia
package su.catlean.api.data;

import su.catlean.utils.KotlinCompat;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Serializable
public final class IPCUser {
   @NotNull
   public static final IPCUser.Companion companion = new IPCUser.Companion(null);
   @NotNull
   private String id;
   @NotNull
   private String username;
   @NotNull
   private String avatar;

   public IPCUser(String id, String username, String avatar) {
      this.id = id;
      this.username = username;
      this.avatar = avatar;
   }

   public IPCUser(String text, String text2, String text3, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 1) != 0) {
         text = "none";
      }

      if ((value & 2) != 0) {
         text2 = "none";
      }

      if ((value & 4) != 0) {
         text3 = "none";
      }

      this(text, text2, text3);
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getUsername() {
      return this.username;
   }

   @NotNull
   public final String getAvatar() {
      return this.avatar;
   }

   @NotNull
   public final String computeText(int value, boolean flag) {
      String text;
      if (KotlinCompat.startsWith(this.avatar, "a_", false, 2, null) && !flag) {
         String avatar2 = this.avatar;
         String text2 = this.id;
         text = "https://cdn.discordapp.com/avatars/" + text2 + "/" + avatar2 + ".gif?size=" + value;
      } else {
         String avatar3 = this.avatar;
         String text3 = this.id;
         text = "https://cdn.discordapp.com/avatars/" + text3 + "/" + avatar3 + ".png?size=" + value;
      }

      return text;
   }

   public static String defaultImpl(IPCUser iPCUser, int value, boolean flag, int value2, Object object) {
      if ((value2 & 1) != 0) {
         value = 128;
      }

      if ((value2 & 2) != 0) {
         flag = false;
      }

      return iPCUser.computeText(value, flag);
   }

   @NotNull
   public final String getId2() {
      return this.id;
   }

   @NotNull
   public final String getUsername2() {
      return this.username;
   }

   @NotNull
   public final String getAvatar2() {
      return this.avatar;
   }

   @NotNull
   @Override
   public String toString() {
      String avatar2 = this.avatar;
      String username2 = this.username;
      String text = this.id;
      return "IPCUser(id=" + text + ", username=" + username2 + ", avatar=" + avatar2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      hashCode = hashCode * 31 + this.username.hashCode();
      return hashCode * 31 + this.avatar.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof IPCUser ipcUser)) {
         return false;
      } else if (!Intrinsics.areEqual(this.id, ipcUser.id)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.username, ipcUser.username) ? false : Intrinsics.areEqual(this.avatar, ipcUser.avatar);
      }
   }

   @JvmStatic
   public static final void applyEncodeStringElement(IPCUser iPCUser, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 0) ? true : !Intrinsics.areEqual(iPCUser.id, "none")) {
         compositeEncoder.encodeStringElement(serialDescriptor, 0, iPCUser.id);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 1) ? true : !Intrinsics.areEqual(iPCUser.username, "none")) {
         compositeEncoder.encodeStringElement(serialDescriptor, 1, iPCUser.username);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 2) ? true : !Intrinsics.areEqual(iPCUser.avatar, "none")) {
         compositeEncoder.encodeStringElement(serialDescriptor, 2, iPCUser.avatar);
      }
   }

   public IPCUser(int value, String text, String text2, String text3, SerializationConstructorMarker serializationConstructorMarker) {
      if ((0) != 0) {
         PluginExceptionsKt.throwMissingFieldException(value, 0, IPCUser$$serializer.INSTANCE.getDescriptor());
      }

      super();
      if ((value) == 0) {
         this.id = "none";
      } else {
         this.id = text;
      }

      if ((value) == 0) {
         this.username = "none";
      } else {
         this.username = text2;
      }

      if ((value) == 0) {
         this.avatar = "none";
      } else {
         this.avatar = text3;
      }
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)IPCUser$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}