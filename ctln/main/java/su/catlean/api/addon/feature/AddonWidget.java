// deobf by pusia
package su.catlean.api.addon.feature;

import org.jetbrains.annotations.NotNull;

public abstract class AddonWidget {
   @NotNull
   private final String id;
   @NotNull
   private final AddonSettings settings;

   public AddonWidget(String id) {
      this.id = id;
      this.settings = new AddonSettings();
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final AddonSettings getSettings() {
      return this.settings;
   }

   public abstract void render(AddonWidgetContext addonWidgetContext);

   public void update() {
   }
}