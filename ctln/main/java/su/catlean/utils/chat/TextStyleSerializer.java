// deobf by pusia
package su.catlean.utils.chat;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TextStyleSerializer {
   @NotNull
   public static final TextStyleSerializer INSTANCE = new TextStyleSerializer();

   private TextStyleSerializer() {
   }

   @NotNull
   public final String computeText(Component t) {
      StringBuilder stringBuilder = new StringBuilder();
      int[] values = new int[]{Integer.MIN_VALUE};
      t.visit((arg, arg2) -> {
         if (arg2.length() == 0) {
            return Optional.empty();
         } else {
            TextColor color = arg.getColor();
            int value = color != null ? color.getValue() & 16777215 : -1;
            if (value != values[0]) {
               if (value == -1) {
                  stringBuilder.append('§').append('r');
               } else {
                  stringBuilder.append(INSTANCE.computeText(value));
               }

               values[0] = value;
            }

            stringBuilder.append(arg2);
            return Optional.empty();
         }
      }, Style.EMPTY);
      stringBuilder.append('§').append('r');
      return stringBuilder.toString();
   }

   private final String computeText(int value) {
      Character character = this.computeCharacter(value);
      return character != null ? "§" + character : this.computeText2(value);
   }

   private final String computeText2(int value) {
      Locale root = Locale.ROOT;
      String text = "%06x";
      Object[] objects = new Object[]{value & 16777215};
      String text2 = String.format(root, text, Arrays.copyOf(objects, objects.length));
      StringBuilder stringBuilder = new StringBuilder(14);
      stringBuilder.append('§').append('x');
      int value2 = 0;

      for (int i = text2.length(); value2 < i; value2++) {
         char charAt = text2.charAt(value2);
         stringBuilder.append('§').append(charAt);
      }

      return stringBuilder.toString();
   }

   @Nullable
   public final Character computeCharacter(int value) {
      return switch (value) {
         case 0 -> '0';
         case 170 -> '1';
         case 43520 -> '2';
         case 43690 -> '3';
         case 5592405 -> '8';
         case 5592575 -> '9';
         case 5635925 -> 'a';
         case 5636095 -> 'b';
         case 11141120 -> '4';
         case 11141290 -> '5';
         case 11184810 -> '7';
         case 16733525 -> 'c';
         case 16733695 -> 'd';
         case 16755200 -> '6';
         case 16777045 -> 'e';
         case 16777215 -> 'f';
         default -> null;
      };
   }
}