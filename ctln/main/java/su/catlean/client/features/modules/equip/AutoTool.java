// deobf by pusia
package su.catlean.client.features.modules.equip;

import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.CalcBlockBreakingDeltaEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.EnchantmentUtil;

public final class AutoTool extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoTool.class, "silent", "getSilent()Z", 0), new PropertyReference1Impl(AutoTool.class, "swapBack", "getSwapBack()Z", 0), new PropertyReference1Impl(AutoTool.class, "saveItem", "getSaveItem()Z", 0), new PropertyReference1Impl(AutoTool.class, "echestSilk", "getEchestSilk()Z", 0) };
   @NotNull
   public static final AutoTool INSTANCE = new AutoTool();
   @NotNull
   private static final BooleanSetting silent = SettingFactory.defaultImpl(INSTANCE, "silent", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting swapBack = SettingFactory.defaultImpl(INSTANCE, "swap-back", true, null, () -> !INSTANCE.getSilent(), 4, null);
   @NotNull
   private static final BooleanSetting saveItem = SettingFactory.defaultImpl(INSTANCE, "save-item", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting echestSilk = SettingFactory.defaultImpl(INSTANCE, "echest-silk", true, null, null, 12, null);
   private static int value;
   private static boolean flag;
   private static long time;
   @NotNull
   private static final List<Integer> items = new ArrayList();

   private AutoTool() {
      super("auto-tool", SubCategories.getSubCategory9(), null, 4, null);
   }

   private final boolean getSilent() {
      return (Boolean)silent.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getSwapBack() {
      return (Boolean)swapBack.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getSaveItem() {
      return (Boolean)saveItem.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getEchestSilk() {
      return (Boolean)echestSilk.computeObject(this, $$delegatedProperties[3]);
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      if (MinecraftHolder.getMinecraft().hitResult instanceof BlockHitResult) {
         BlockHitResult hitResult = (BlockHitResult)MinecraftHolder.getMinecraft().hitResult;
         BlockPos blockPos = hitResult.getBlockPos();
         if (!MinecraftHolder.getClientLevel().getBlockState(blockPos).isAir()) {
            if (this.fetchInventory(blockPos) != -1 && MinecraftHolder.getMinecraft().options.keyAttack.isDown()) {
               items.add(MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot());
               if (this.getSilent()) {
                  su.catlean.client.services.InventoryManager.INSTANCE.applyGetInventory2(this.fetchInventory(blockPos));
               } else {
                  MinecraftHolder.getLocalPlayer().getInventory().setSelectedSlot(this.fetchInventory(blockPos));
               }

               value = this.fetchInventory(blockPos);
               flag = true;
               time = MinecraftHolder.getTime();
            } else if (flag && !items.isEmpty() && MinecraftHolder.getTime() >= time + 300 && (this.getSwapBack() || this.getSilent())) {
               if (this.getSilent()) {
                  su.catlean.client.services.InventoryManager.INSTANCE.applyGetInventory2(((Number)items.get(0)).intValue());
               } else {
                  MinecraftHolder.getLocalPlayer().getInventory().setSelectedSlot(((Number)items.get(0)).intValue());
               }

               value = ((Number)items.get(0)).intValue();
               items.clear();
               flag = false;
            }
         }
      }
   }

   @Flow
   public final void onCalcBlockBreakingDelta(CalcBlockBreakingDeltaEvent event) {
      if (this.getSilent()) {
         float destroySpeed = event.getState().getDestroySpeed((BlockGetter)MinecraftHolder.getClientLevel(), event.getBlockPos());
         if (destroySpeed < 0.0F) {
            event.setDelta(0.0F);
         } else {
            float destroySpeed2 = this.fetchDestroySpeed(event.getState(), MinecraftHolder.getLocalPlayer().getInventory().getItem(value)) / destroySpeed;
            event.setDelta(
               MinecraftHolder.getLocalPlayer().getInventory().getItem(value).isCorrectToolForDrops(event.getState()) ? destroySpeed2 / 30.0F : destroySpeed2 / 100.0F
            );
            event.cancel();
         }
      }
   }

   private final float fetchDestroySpeed(BlockState blockState, ItemStack itemStack) {
      double destroySpeed = itemStack.getDestroySpeed(blockState);
      int itemEnchantmentLevel = EnchantmentUtil.fetchItemEnchantmentLevel(itemStack, Enchantments.EFFICIENCY);
      return (float)Math.max(destroySpeed + (destroySpeed > 1.0 ? itemEnchantmentLevel * itemEnchantmentLevel + 1.0 : 0.0), 0.0);
   }

   private final int fetchInventory(BlockPos blockPos) {
      int value2 = -1;
      float value3 = 1.0F;

      for (int i = 0; i < 9; i++) {
         ItemStack item = MinecraftHolder.getLocalPlayer().getInventory().getItem(i);
         if (!Intrinsics.areEqual(item, ItemStack.EMPTY)
            && (
               MinecraftHolder.getLocalPlayer().getInventory().getItem(i).getMaxDamage()
                        - MinecraftHolder.getLocalPlayer().getInventory().getItem(i).getDamageValue()
                     > 10
                  || !this.getSaveItem()
            )) {
            BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(blockPos);
            int itemEnchantmentLevel = EnchantmentUtil.fetchItemEnchantmentLevel(item, Enchantments.EFFICIENCY);
            float destroySpeed = item.getDestroySpeed(blockState);
            if (blockState.getBlock() instanceof AirBlock) {
               return -1;
            }

            if (!(blockState.getBlock() instanceof EnderChestBlock) || !this.getEchestSilk()) {
               if (itemEnchantmentLevel + destroySpeed > value3) {
                  value3 = itemEnchantmentLevel + destroySpeed;
                  value2 = i;
               }
            } else if (EnchantmentUtil.checkContains(item, Enchantments.SILK_TOUCH) && itemEnchantmentLevel + destroySpeed > value3) {
               value3 = itemEnchantmentLevel + destroySpeed;
               value2 = i;
            }
         }
      }

      return value2;
   }

   static {
   }
}