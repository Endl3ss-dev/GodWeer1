// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.category.SubCategory;
import su.catlean.client.services.NotificationManager;
import su.catlean.utils.animation.Animation;

public final class Selector {
   private final float amount;
   private final float amount2;
   @NotNull
   private final SubCategory subCategory;
   @NotNull
   private final Animation animation;

   public Selector(float amount, float amount2, SubCategory subCategory, Animation animation) {
      this.amount = amount;
      this.amount2 = amount2;
      this.subCategory = subCategory;
      this.animation = animation;
   }

   public Selector(float amount, float amount2, SubCategory subCategory, Animation animation, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 8) != 0) {
         animation = new Animation(NotificationManager.Notification.Type.OUT_EXPO, 600L);
      }

      this(amount, amount2, subCategory, animation);
   }

   public final float getAmount() {
      return this.amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   @NotNull
   public final SubCategory getSubCategory() {
      return this.subCategory;
   }

   @NotNull
   public final Animation getAnimation() {
      return this.animation;
   }

   public final float getAmount3() {
      return this.amount;
   }

   public final float getAmount4() {
      return this.amount2;
   }

   @NotNull
   public final SubCategory getSubCategory2() {
      return this.subCategory;
   }

   @NotNull
   public final Animation getAnimation2() {
      return this.animation;
   }

   @NotNull
   @Override
   public String toString() {
      Animation animation2 = this.animation;
      SubCategory subCategory2 = this.subCategory;
      float amount22 = this.amount2;
      float amount3 = this.amount;
      return "Selector(x=" + amount3 + ", w=" + amount22 + ", subCategory=" + subCategory2 + ", animation=" + animation2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Float.hashCode(this.amount);
      hashCode = hashCode * 31 + Float.hashCode(this.amount2);
      hashCode = hashCode * 31 + this.subCategory.hashCode();
      return hashCode * 31 + this.animation.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Selector selector)) {
         return false;
      } else if (Float.compare(this.amount, selector.amount) != 0) {
         return false;
      } else if (Float.compare(this.amount2, selector.amount2) != 0) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.subCategory, selector.subCategory) ? false : Intrinsics.areEqual(this.animation, selector.animation);
      }
   }
}