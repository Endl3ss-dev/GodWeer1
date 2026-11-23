// deobf by pusia
package su.catlean.client.features.modules;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.category.SubCategory;

public final class SubCategories {
   @NotNull
   private static final SubCategory subCategory = new SubCategory("protect");
   @NotNull
   private static final SubCategory subCategory2 = new SubCategory("attack");
   @NotNull
   private static final SubCategory subCategory3 = new SubCategory("legit");
   @NotNull
   private static final SubCategory subCategory4 = new SubCategory("basic");
   @NotNull
   private static final SubCategory subCategory5 = new SubCategory("rage");
   @NotNull
   private static final SubCategory subCategory6 = new SubCategory("cosmetic");
   @NotNull
   private static final SubCategory subCategory7 = new SubCategory("esp");
   @NotNull
   private static final SubCategory subCategory8 = new SubCategory("world");
   @NotNull
   private static final SubCategory subCategory9 = new SubCategory("equip");
   @NotNull
   private static final SubCategory subCategory10 = new SubCategory("player");
   @NotNull
   private static final SubCategory subCategory11 = new SubCategory("misc");

   @NotNull
   public static final SubCategory getSubCategory() {
      return subCategory;
   }

   @NotNull
   public static final SubCategory getSubCategory2() {
      return subCategory2;
   }

   @NotNull
   public static final SubCategory getSubCategory3() {
      return subCategory3;
   }

   @NotNull
   public static final SubCategory getSubCategory4() {
      return subCategory4;
   }

   @NotNull
   public static final SubCategory getSubCategory5() {
      return subCategory5;
   }

   @NotNull
   public static final SubCategory getSubCategory6() {
      return subCategory6;
   }

   @NotNull
   public static final SubCategory getSubCategory7() {
      return subCategory7;
   }

   @NotNull
   public static final SubCategory getSubCategory8() {
      return subCategory8;
   }

   @NotNull
   public static final SubCategory getSubCategory9() {
      return subCategory9;
   }

   @NotNull
   public static final SubCategory getSubCategory10() {
      return subCategory10;
   }

   @NotNull
   public static final SubCategory getSubCategory11() {
      return subCategory11;
   }
}