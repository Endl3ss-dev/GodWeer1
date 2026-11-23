// deobf by pusia
package su.catlean.utils.inventory;

import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;

public final class SearchInvResultCompanion {
   private SearchInvResultCompanion() {
   }

   @NotNull
   public final SearchInvResult getSearchInvResult() {
      return SearchInvResult.getInstance();
   }

   public SearchInvResultCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}