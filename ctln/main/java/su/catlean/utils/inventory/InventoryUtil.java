// deobf by pusia
package su.catlean.utils.inventory;

import java.util.List;
import kotlin.collections.CollectionsKt;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public final class InventoryUtil {
   @NotNull
   public static final List<ItemStack> fetchInventory(LocalPlayer self) {
      ItemStack[] itemStacks = new ItemStack[]{
         self.getInventory().getItem(39), self.getInventory().getItem(38), self.getInventory().getItem(37), self.getInventory().getItem(36)
      };
      return CollectionsKt.listOf(itemStacks);
   }

   @NotNull
   public static final List<ItemStack> fetchInventory(Player self) {
      ItemStack[] itemStacks = new ItemStack[]{
         self.getInventory().getItem(39), self.getInventory().getItem(38), self.getInventory().getItem(37), self.getInventory().getItem(36)
      };
      return CollectionsKt.mutableListOf(itemStacks);
   }

   @NotNull
   public static final List<ItemStack> fetchDefaultInstance(LivingEntity self) {
      ItemStack[] itemStacks = new ItemStack[]{
         Items.AIR.getDefaultInstance(), Items.AIR.getDefaultInstance(), Items.AIR.getDefaultInstance(), Items.AIR.getDefaultInstance()
      };
      return CollectionsKt.mutableListOf(itemStacks);
   }
}