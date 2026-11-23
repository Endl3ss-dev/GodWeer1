// deobf by pusia
package su.catlean.client.render.font;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import su.catlean.CatLean;

public final class Fonts {
   public static FontRenderer fontRenderer;
   public static FontRenderer fontRenderer2;
   public static FontRenderer fontRenderer3;
   public static FontRenderer fontRenderer4;
   public static FontRenderer fontRenderer5;
   public static FontRenderer fontRenderer6;
   public static FontRenderer fontRenderer7;
   public static FontRenderer fontRenderer8;
   public static FontRenderer fontRenderer9;
   public static FontRenderer fontRenderer10;
   @NotNull
   private static final List<FontRenderer> items = new ArrayList();

   @NotNull
   public static final FontRenderer getFontRenderer() {
      if (fontRenderer != null) {
         return fontRenderer;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("SF_M");
         return null;
      }
   }

   public static final void updateFontRenderer(FontRenderer fontRenderer) {
      Fonts.fontRenderer = fontRenderer;
   }

   @NotNull
   public static final FontRenderer getFontRenderer2() {
      if (fontRenderer2 != null) {
         return fontRenderer2;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("SF_M2");
         return null;
      }
   }

   public static final void updateFontRenderer2(FontRenderer fontRenderer) {
      fontRenderer2 = fontRenderer;
   }

   @NotNull
   public static final FontRenderer getFontRenderer3() {
      if (fontRenderer3 != null) {
         return fontRenderer3;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("SF_M3");
         return null;
      }
   }

   public static final void updateFontRenderer3(FontRenderer fontRenderer) {
      fontRenderer3 = fontRenderer;
   }

   @NotNull
   public static final FontRenderer getFontRenderer4() {
      if (fontRenderer4 != null) {
         return fontRenderer4;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("SF_M4");
         return null;
      }
   }

   public static final void updateFontRenderer4(FontRenderer fontRenderer) {
      fontRenderer4 = fontRenderer;
   }

   @NotNull
   public static final FontRenderer getFontRenderer5() {
      if (fontRenderer5 != null) {
         return fontRenderer5;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("SF_B");
         return null;
      }
   }

   public static final void updateFontRenderer5(FontRenderer fontRenderer) {
      fontRenderer5 = fontRenderer;
   }

   @NotNull
   public static final FontRenderer getFontRenderer6() {
      if (fontRenderer6 != null) {
         return fontRenderer6;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("SF_B2");
         return null;
      }
   }

   public static final void updateFontRenderer6(FontRenderer fontRenderer) {
      fontRenderer6 = fontRenderer;
   }

   @NotNull
   public static final FontRenderer getFontRenderer7() {
      if (fontRenderer7 != null) {
         return fontRenderer7;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("SF_B3");
         return null;
      }
   }

   public static final void updateFontRenderer7(FontRenderer fontRenderer) {
      fontRenderer7 = fontRenderer;
   }

   @NotNull
   public static final FontRenderer getFontRenderer8() {
      if (fontRenderer8 != null) {
         return fontRenderer8;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("SF_B4");
         return null;
      }
   }

   public static final void updateFontRenderer8(FontRenderer fontRenderer) {
      fontRenderer8 = fontRenderer;
   }

   @NotNull
   public static final FontRenderer getFontRenderer9() {
      if (fontRenderer9 != null) {
         return fontRenderer9;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("SF_SB");
         return null;
      }
   }

   public static final void updateFontRenderer9(FontRenderer fontRenderer) {
      fontRenderer9 = fontRenderer;
   }

   @NotNull
   public static final FontRenderer getFontRenderer10() {
      if (fontRenderer10 != null) {
         return fontRenderer10;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("SF");
         return null;
      }
   }

   public static final void updateFontRenderer10(FontRenderer fontRenderer) {
      fontRenderer10 = fontRenderer;
   }

   @NotNull
   public static final List<FontRenderer> getItems() {
      return items;
   }

   @NotNull
   public static final Font computeFont(float amount, String name) {
      try {
         return Font.createFont(0, Objects.requireNonNull(CatLean.class.getClassLoader().getResourceAsStream("assets/catlean/fonts/" + name + ".ttf")))
            .deriveFont(0, amount);
      } catch (java.awt.FontFormatException | java.io.IOException exception) {
         throw new RuntimeException(exception);
      }
   }

   @NotNull
   public static final FontRenderer computeFontRenderer(float amount, String name) {
      FontRenderer fontRenderer11 = new FontRenderer(computeFont(amount, name), computeFont(amount, "noto"), amount);
      items.add(fontRenderer11);
      return fontRenderer11;
   }
}