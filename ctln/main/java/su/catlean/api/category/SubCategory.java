// deobf by pusia
package su.catlean.api.category;

import org.jetbrains.annotations.NotNull;

public final class SubCategory {
   @NotNull
   private final String id;

   public SubCategory(String id) {
      this.id = id;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }
}