// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

public final class ConfigAction {
   @NotNull
   private final String id;
   @NotNull
   private final Function1 action;

   public ConfigAction(String id, Function1 action) {
      this.id = id;
      this.action = action;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final Function1 getAction() {
      return this.action;
   }
}