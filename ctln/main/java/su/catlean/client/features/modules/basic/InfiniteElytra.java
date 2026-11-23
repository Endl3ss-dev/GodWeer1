// deobf by pusia
package su.catlean.client.features.modules.basic;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class InfiniteElytra extends Module {
   @NotNull
   public static final InfiniteElytra INSTANCE = new InfiniteElytra();

   private InfiniteElytra() {
      super("infinite-elytra", SubCategories.getSubCategory4(), null, 4, null);
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getLocalPlayer().isFallFlying()) {
         if (WorldUtil.INSTANCE.getValue() >= 18) {
            InventoryManager instance = InventoryManager.INSTANCE;
            Item[] items = new Item[]{
               Items.NETHERITE_CHESTPLATE,
               Items.DIAMOND_CHESTPLATE,
               Items.IRON_CHESTPLATE,
               Items.GOLDEN_CHESTPLATE,
               Items.LEATHER_CHESTPLATE,
               Items.CHAINMAIL_CHESTPLATE
            };
            SearchInvResult searchInvResult = instance.computeSearchInvResult(items);
            if (!searchInvResult.isFlag()) {
               this.updateText10("no-armor-hotbar");
            }

            InventoryManager.defaultImpl(InventoryManager.INSTANCE, searchInvResult.getValue(), 0, 2, null);
            TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, 0, (byte)0, false, () -> {
               InventoryManager instance2 = InventoryManager.INSTANCE;
               Item[] items2 = new Item[]{Items.ELYTRA};
               SearchInvResult searchInvResult2 = instance2.computeSearchInvResult(items2);
               if (!searchInvResult.isFlag()) {
                  INSTANCE.updateText10("no-elytra-hotbar");
               }

               InventoryManager.defaultImpl(InventoryManager.INSTANCE, searchInvResult2.getValue(), 0, 2, null);
               TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, 1, (byte)0, false, () -> {
                  Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
                  PacketUtil.applySend(serverboundPlayerCommandPacket);
                  MinecraftHolder.getLocalPlayer().startFallFlying();
               }, 6, null);
            }, 6, null);
            WorldUtil.INSTANCE.setValue(0);
         }
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundSoundPacket
         && Intrinsics.areEqual(((ClientboundSoundPacket)receivePacket.getPacket()).getSound().value(), SoundEvents.ARMOR_EQUIP_ELYTRA)) {
         receivePacket.cancel();
      }
   }
}