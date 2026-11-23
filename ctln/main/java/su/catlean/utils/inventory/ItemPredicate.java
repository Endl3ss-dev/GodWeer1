// deobf by pusia
package su.catlean.utils.inventory;

import net.minecraft.world.item.ItemStack;

public interface ItemPredicate {
   boolean checkGetItem(ItemStack itemStack);
}