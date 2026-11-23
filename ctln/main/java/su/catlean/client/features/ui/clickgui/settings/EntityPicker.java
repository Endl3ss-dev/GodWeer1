// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import net.minecraft.world.item.Item;
import java.util.List;
import java.util.Optional;
import kotlin.collections.CollectionsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.EntitiesValue;
import su.catlean.client.features.ui.clickgui.popup.SearchablePopup;

public final class EntityPicker extends SearchablePopup {
   @NotNull
   private final Setting setting;

   public EntityPicker(Setting setting) {
      super("Select Entities");
      this.setting = setting;
   }

   @NotNull
   public final Setting getSetting() {
      return this.setting;
   }

   @NotNull
   @Override
   public List getItems() {
      return CollectionsKt.toList((Iterable)BuiltInRegistries.ENTITY_TYPE);
   }

   @NotNull
   public String fetchDescription(EntityType item) {
      return item.getDescription().getString();
   }

   public boolean checkContains(EntityType item) {
      return ((EntitiesValue)this.setting.getObject2()).getEntities().contains(item);
   }

   public void render(GuiGraphicsExtractor context, float amount, float amount2, EntityType entity) {
      Optional<Holder<Item>> byId = SpawnEggItem.byId(entity);
      context.pose().pushMatrix();
      context.pose().translate(amount + 8.0F, amount2);
      context.item(byId.isPresent() ? new ItemStack((Holder)byId.get()) : new ItemStack((ItemLike)Items.BARRIER), 0, 0);
      context.pose().popMatrix();
   }

   public void applyContains(EntityType item) {
      List<EntityType> entities = ((EntitiesValue)this.setting.getObject2()).getEntities();
      if (entities.contains(item)) {
         entities.remove(item);
      } else {
         entities.add(item);
      }
   }

   @Override
   public String computeText(Object object) {
      return this.fetchDescription((EntityType)object);
   }

   @Override
   public boolean testObject(Object object) {
      return this.checkContains((EntityType)object);
   }

   @Override
   public void render(GuiGraphicsExtractor guiGraphicsExtractor, float amount, float amount2, Object object) {
      this.render(guiGraphicsExtractor, amount, amount2, (EntityType)object);
   }

   @Override
   public void updateObject(Object object) {
      this.applyContains((EntityType)object);
   }
}