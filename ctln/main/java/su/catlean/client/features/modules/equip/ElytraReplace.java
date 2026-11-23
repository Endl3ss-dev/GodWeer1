// deobf by pusia
package su.catlean.client.features.modules.equip;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.NotificationManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.chat.MessageType;
import su.catlean.utils.inventory.ContainerUtil;

public final class ElytraReplace extends Module {
   @NotNull
   public static final ElytraReplace INSTANCE = new ElytraReplace();

   private ElytraReplace() {
      super("elytra-replace", SubCategories.getSubCategory9(), null, 4, null);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      ItemStack itemBySlot = MinecraftHolder.getLocalPlayer().getItemBySlot(EquipmentSlot.CHEST);
      if (itemBySlot.is(Items.ELYTRA) && itemBySlot.getMaxDamage() - itemBySlot.getDamageValue() <= 2) {
         SearchInvResult inventory2 = su.catlean.client.services.InventoryManager.INSTANCE
            .fetchInventory2(arg -> arg.getItem().components().has(DataComponents.GLIDER) ? arg.getMaxDamage() - arg.getDamageValue() > 2 : false);
         if (inventory2.isFlag()) {
            ContainerUtil.defaultImpl(inventory2.getValue(), 0, null, false, 14, null);
            ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
            ContainerUtil.defaultImpl(inventory2.getValue(), 0, null, true, 6, null);
            NotificationManager.defaultImpl(
               NotificationManager.INSTANCE,
               INSTANCE.getText3(),
               ChatUtil.defaultImpl(INSTANCE, "replaced", new Object[0], false, 4, null),
               2,
               MessageType.INFO,
               null,
               16,
               null
            );
            ChatUtil.defaultImpl(INSTANCE, ChatUtil.defaultImpl(INSTANCE, "replaced", new Object[0], false, 4, null), false, 2, null);
         }
      }
   }
}