// deobf by pusia
package su.catlean.client.commands;

import su.catlean.utils.KotlinCompat;
import java.util.Iterator;
import java.util.List;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SuggestionsHelper {
   @NotNull
   private final List suggestions;

   public SuggestionsHelper(List suggestions) {
      this.suggestions = suggestions;
   }

   @Nullable
   public final String computeText(String s) {
      Iterable suggestions2 = this.suggestions;
      Iterator iterator = suggestions2.iterator();

      Object object;
      while (true) {
         if (iterator.hasNext()) {
            Object object2 = iterator.next();
            String text = (String)object2;
            if (!KotlinCompat.startsWith(text, s, false, 2, null)) {
               continue;
            }

            object = object2;
            break;
         }

         object = null;
         break;
      }

      return (String)object;
   }
}