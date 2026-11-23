// deobf by pusia
package su.catlean.api.addon.impl;

import java.util.Iterator;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.addon.feature.AddonAuraRotation;
import su.catlean.api.addon.feature.AddonCommand;
import su.catlean.api.addon.feature.AddonModule;
import su.catlean.api.addon.feature.AddonWidget;
import su.catlean.api.addon.registry.CatLeanRegistry;
import su.catlean.api.category.Category;
import su.catlean.api.category.SubCategory;
import su.catlean.client.commands.impl.AddonsCommand;
import su.catlean.client.services.CommandManager;
import su.catlean.client.services.ModuleManager;
import su.catlean.client.services.WidgetManager;
import su.catlean.client.services.rotation.modes.CustomRotation;

public final class AddonRegistryImpl implements CatLeanRegistry {
   @NotNull
   public static final AddonRegistryImpl INSTANCE = new AddonRegistryImpl();

   private AddonRegistryImpl() {
   }

   @Override
   public void registerModule(AddonModule module) {
      Sequence flatMap = SequencesKt.flatMap(
         CollectionsKt.asSequence(ModuleManager.INSTANCE.getItems()), arg -> CollectionsKt.asSequence(((su.catlean.api.category.Category)arg).getSubCategories())
      );
      Iterator iterator = flatMap.iterator();

      Object object;
      while (true) {
         if (iterator.hasNext()) {
            Object object2 = iterator.next();
            SubCategory subCategory = (SubCategory)object2;
            if (!Intrinsics.areEqual(subCategory.getId(), module.getCategoryId())) {
               continue;
            }

            object = object2;
            break;
         }

         object = null;
         break;
      }

      object = (SubCategory)object;
      if ((SubCategory)object == null) {
         throw new IllegalArgumentException("Unknown addon module category '" + module.getCategoryId() + "' for module '" + module.getId() + "'");
      } else {
         SubCategory subCategory2 = (SubCategory)object;
         ModuleManager.INSTANCE.applyIsEmpty(new AddonModuleAdapter(module, subCategory2));
      }
   }

   @Override
   public void registerCommand(AddonCommand command) {
      CommandManager.INSTANCE.applyIsEmpty(new AddonsCommand(command));
   }

   @Override
   public void registerWidget(AddonWidget widget) {
      WidgetManager.INSTANCE.applyIsEmpty(new AddonWidgetAdapter(widget));
   }

   @Override
   public void registerAuraRotation(AddonAuraRotation rotation) {
      CustomRotation.INSTANCE.updateAddonAuraRotation(rotation);
   }

   // [deobf] Declared in the interface, but missing from the original jar's AddonRegistryImpl
   // (calling these would throw AbstractMethodError in the original). Stubbed 1:1.
   @Override
   public void registerCategory(Category category) {
      throw new UnsupportedOperationException("not implemented in the original jar");
   }

   @Override
   @org.jetbrains.annotations.Nullable
   public Category category(String text) {
      throw new UnsupportedOperationException("not implemented in the original jar");
   }

   @Override
   @org.jetbrains.annotations.Nullable
   public SubCategory subCategory(String text) {
      throw new UnsupportedOperationException("not implemented in the original jar");
   }

   private static final Sequence computeSequence(Category it) {
      return CollectionsKt.asSequence(it.getSubCategories());
   }
}