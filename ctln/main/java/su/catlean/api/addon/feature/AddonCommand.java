// deobf by pusia
package su.catlean.api.addon.feature;

import org.jetbrains.annotations.NotNull;

public abstract class AddonCommand {
   @NotNull
   private final String name;

   public AddonCommand(String name) {
      this.name = name;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   public abstract int execute(AddonCommandContext addonCommandContext);
}