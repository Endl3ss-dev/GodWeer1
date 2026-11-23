// deobf by pusia
package su.catlean.utils.inventory;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import su.catlean.utils.MinecraftHolder;

public final class EnchantmentUtil {
   public static final boolean checkContains(ItemStack stack, ResourceKey enchantment) {
      return EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet().contains(MinecraftHolder.getClientLevel().registryAccess().get(enchantment).get());
   }

   public static final int fetchItemEnchantmentLevel(ItemStack stack, ResourceKey enchantment) {
      return EnchantmentHelper.getItemEnchantmentLevel((Holder)MinecraftHolder.getClientLevel().registryAccess().get(enchantment).get(), (ItemInstance)stack);
   }
}