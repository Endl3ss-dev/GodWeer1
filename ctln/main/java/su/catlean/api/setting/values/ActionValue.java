// deobf by pusia
package su.catlean.api.setting.values;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

public final class ActionValue {
   @NotNull
   private final Function0 action;

   public ActionValue(Function0 action) {
      this.action = action;
   }

   @NotNull
   public final Function0 getAction() {
      return this.action;
   }
}