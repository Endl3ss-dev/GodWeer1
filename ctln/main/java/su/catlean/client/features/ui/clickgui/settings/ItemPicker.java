// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.ui.clickgui.popup.SearchablePopup;

public final class ItemPicker extends SearchablePopup {
   @NotNull
   private final List list;
   @NotNull
   private final Function1 clickAction;

   public ItemPicker(List list, Function1 clickAction) {
      super("Select Items");
      this.list = list;
      this.clickAction = clickAction;
   }

   @NotNull
   public final List getList() {
      return this.list;
   }

   @NotNull
   public final Function1 getClickAction() {
      return this.clickAction;
   }

   @NotNull
   @Override
   public List getItems() {
      return CollectionsKt.toList((Iterable)BuiltInRegistries.ITEM);
   }

   @NotNull
   public String fetchHoverName(Item item) {
      return new ItemStack((ItemLike)item).getHoverName().getString();
   }

   public boolean checkContains(Item item) {
      return this.list.contains(item);
   }

   public void render(GuiGraphicsExtractor context, float amount, float amount2, Item item) {
      context.pose().pushMatrix();
      context.pose().translate(amount + 8.0F, amount2);
      context.item(item.getDefaultInstance(), 0, 0);
      context.pose().popMatrix();
   }

   public void applyContains(Item item) {
      this.clickAction.invoke(item);
      List<Item> list2 = this.list;
      if (list2.contains(item)) {
         list2.remove(item);
      } else {
         list2.add(item);
      }
   }

   @Override
   public String computeText(Object object) {
      return this.fetchHoverName((Item)object);
   }

   @Override
   public boolean testObject(Object object) {
      return this.checkContains((Item)object);
   }

   @Override
   public void render(GuiGraphicsExtractor guiGraphicsExtractor, float amount, float amount2, Object object) {
      this.render(guiGraphicsExtractor, amount, amount2, (Item)object);
   }

   @Override
   public void updateObject(Object object) {
      this.applyContains((Item)object);
   }
}