// deobf by pusia
package su.catlean.utils.inventory;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

public final class ItemTypeUtil {
   public static final boolean testItemStack(ItemStack item) {
      return item.is(ItemTags.SWORDS);
   }

   public static final boolean testItemStack2(ItemStack item) {
      return item.is(ItemTags.PICKAXES);
   }

   public static final boolean testItemStack3(ItemStack item) {
      return item.is(ItemTags.FOOT_ARMOR) || item.is(ItemTags.LEG_ARMOR) || item.is(ItemTags.CHEST_ARMOR) || item.is(ItemTags.HEAD_ARMOR);
   }
}