// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.services.NotificationManager;
import su.catlean.utils.animation.Animation;

public final class KeyBind {
   @NotNull
   private final String name;
   @NotNull
   private final String bind;
   @NotNull
   private final String extra;
   @NotNull
   private final Animation animation;
   private boolean flag;

   public KeyBind(String name, String bind, String extra, Animation animation, boolean flag) {
      this.name = name;
      this.bind = bind;
      this.extra = extra;
      this.animation = animation;
      this.flag = flag;
   }

   public KeyBind(String text, String text2, String text3, Animation animation, boolean flag, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 4) != 0) {
         text3 = "";
      }

      if ((value & 8) != 0) {
         animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 250L);
      }

      if ((value & 16) != 0) {
         flag = false;
      }

      this(text, text2, text3, animation, flag);
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @NotNull
   public final String getBind() {
      return this.bind;
   }

   @NotNull
   public final String getExtra() {
      return this.extra;
   }

   @NotNull
   public final Animation getAnimation() {
      return this.animation;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   @NotNull
   public final String getName2() {
      return this.name;
   }

   @NotNull
   public final String getBind2() {
      return this.bind;
   }

   @NotNull
   public final String getExtra2() {
      return this.extra;
   }

   @NotNull
   public final Animation getAnimation2() {
      return this.animation;
   }

   public final boolean isFlag2() {
      return this.flag;
   }

   @NotNull
   @Override
   public String toString() {
      boolean flag2 = this.flag;
      Animation animation2 = this.animation;
      String extra2 = this.extra;
      String bind2 = this.bind;
      String name2 = this.name;
      return "KeyBind(name=" + name2 + ", bind=" + bind2 + ", extra=" + extra2 + ", animation=" + animation2 + ", removing=" + flag2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.name.hashCode();
      hashCode = hashCode * 31 + this.bind.hashCode();
      hashCode = hashCode * 31 + this.extra.hashCode();
      hashCode = hashCode * 31 + this.animation.hashCode();
      return hashCode * 31 + Boolean.hashCode(this.flag);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof KeyBind keyBind)) {
         return false;
      } else if (!Intrinsics.areEqual(this.name, keyBind.name)) {
         return false;
      } else if (!Intrinsics.areEqual(this.bind, keyBind.bind)) {
         return false;
      } else if (!Intrinsics.areEqual(this.extra, keyBind.extra)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.animation, keyBind.animation) ? false : this.flag == keyBind.flag;
      }
   }
}