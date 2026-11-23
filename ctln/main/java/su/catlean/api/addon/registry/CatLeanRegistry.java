// deobf by pusia
package su.catlean.api.addon.registry;

import org.jetbrains.annotations.Nullable;
import su.catlean.api.addon.feature.AddonAuraRotation;
import su.catlean.api.addon.feature.AddonCommand;
import su.catlean.api.addon.feature.AddonModule;
import su.catlean.api.addon.feature.AddonWidget;
import su.catlean.api.category.Category;
import su.catlean.api.category.SubCategory;

public interface CatLeanRegistry {
   void registerCategory(Category category);

   void registerModule(AddonModule addonModule);

   void registerCommand(AddonCommand addonCommand);

   void registerWidget(AddonWidget addonWidget);

   void registerAuraRotation(AddonAuraRotation addonAuraRotation);

   @Nullable
   Category category(String text);

   @Nullable
   SubCategory subCategory(String text);
}