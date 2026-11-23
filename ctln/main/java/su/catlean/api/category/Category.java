// deobf by pusia
package su.catlean.api.category;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class Category {
   @NotNull
   private final String id;
   @NotNull
   private final List subCategories;

   public Category(String id, List subCategories) {
      this.id = id;
      this.subCategories = subCategories;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final List getSubCategories() {
      return this.subCategories;
   }
}