// deobf by pusia
package su.catlean.client.features.modules.attack;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.inventory.EnchantmentUtil;

public final class AttributeSwap extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AttributeSwap.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(AttributeSwap.class, "mace", "getMace()Lsu/catlean/ya;", 0), new PropertyReference1Impl(AttributeSwap.class, "axe", "getAxe()Z", 0), new PropertyReference1Impl(AttributeSwap.class, "axeOnlyBlocking", "getAxeOnlyBlocking()Z", 0), new PropertyReference1Impl(AttributeSwap.class, "knockBack", "getKnockBack()Z", 0), new PropertyReference1Impl(AttributeSwap.class, "axeMaceHit", "getAxeMaceHit()Z", 0), new PropertyReference1Impl(AttributeSwap.class, "axeMaceHitDelay", "getAxeMaceHitDelay()I", 0) };
   @NotNull
   public static final AttributeSwap INSTANCE = new AttributeSwap();
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 0, new IntRange(0, 5), null, null, 24, null);
   @NotNull
   private static final EnumSetting mace = SettingFactory.defaultImpl(INSTANCE, "mace", AttributeSwap.Mace.ANY, null, null, 12, null);
   @NotNull
   private static final BooleanSetting axe = SettingFactory.defaultImpl(INSTANCE, "axe", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting onlyWhenTargetBlocking = SettingFactory.defaultImpl(
      INSTANCE, "only-when-target-blocking", true, null, () -> INSTANCE.getAxe(), 4, null
   );
   @NotNull
   private static final BooleanSetting knockBackItem = SettingFactory.defaultImpl(INSTANCE, "knock-back-item", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting axeMaceHit = SettingFactory.defaultImpl(INSTANCE, "axe-mace-hit", true, null, null, 12, null);
   @NotNull
   private static final IntSetting axeMaceHitDelay = SettingFactory.defaultImpl(
      INSTANCE, "axe-mace-hit-delay", 4, new IntRange(1, 8), null, () -> INSTANCE.getAxeMaceHit(), 8, null
   );
   private static int value = -1;

   private AttributeSwap() {
      super("attribute-swap", SubCategories.getSubCategory2(), null, 4, null);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   private final AttributeSwap.Mace getMace() {
      return (AttributeSwap.Mace)mace.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getAxe() {
      return (Boolean)axe.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getAxeOnlyBlocking() {
      return (Boolean)onlyWhenTargetBlocking.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getKnockBack() {
      return (Boolean)knockBackItem.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getAxeMaceHit() {
      return (Boolean)axeMaceHit.computeObject(this, $$delegatedProperties[5]);
   }

   private final int getAxeMaceHitDelay() {
      return ((Number)axeMaceHitDelay.computeObject(this, $$delegatedProperties[6])).intValue();
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (sendPacket.getPacket() instanceof ServerboundAttackPacket) {
         Entity entity = Criticals.INSTANCE.fetchEntity((ServerboundAttackPacket)sendPacket.getPacket());
         if (entity == null || entity instanceof EndCrystal) {
            return;
         }

         SearchInvResult searchInvResult = null;
         SearchInvResult searchInvResult3 = InventoryManager.INSTANCE.getSearchInvResult3();
         SearchInvResult inventory = InventoryManager.INSTANCE.fetchInventory(arg -> Intrinsics.areEqual(arg.getItem(), Items.MACE));
         SearchInvResult inventory2 = InventoryManager.INSTANCE
            .fetchInventory(
               arg2 -> Intrinsics.areEqual(arg2.getItem(), Items.MACE) && EnchantmentUtil.fetchItemEnchantmentLevel(arg2, Enchantments.BREACH) >= 1
            );
         SearchInvResult inventory3 = InventoryManager.INSTANCE
            .fetchInventory(
               arg3 -> Intrinsics.areEqual(arg3.getItem(), Items.MACE) && EnchantmentUtil.fetchItemEnchantmentLevel(arg3, Enchantments.DENSITY) >= 1
            );
         SearchInvResult inventory4 = InventoryManager.INSTANCE.fetchInventory(arg4 -> EnchantmentUtil.fetchItemEnchantmentLevel(arg4, Enchantments.KNOCKBACK) >= 1);
         if (this.getAxeMaceHit() && MinecraftHolder.getLocalPlayer().getMainHandItem().getItem() instanceof AxeItem && inventory.isFlag()) {
            SearchInvResult searchInvResult2 = inventory3.isFlag() ? inventory3 : inventory;
            TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, this.getAxeMaceHitDelay(), (byte)0, false, () -> {
               InventoryManager.INSTANCE.applyGetInventory2(searchInvResult2.getValue());
               MinecraftHolder.getMultiPlayerGameMode().attack((Player)MinecraftHolder.getLocalPlayer(), entity);
               MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
            }, 6, null);
         } else {
            if (this.getAxe()
               && !(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem() instanceof AxeItem)
               && (
                  !this.getAxeOnlyBlocking()
                     || entity instanceof Player
                        && ((Player)entity).isUsingItem()
                        && (
                           Intrinsics.areEqual(((Player)entity).getOffhandItem().getItem(), Items.SHIELD)
                              || Intrinsics.areEqual(((Player)entity).getMainHandItem().getItem(), Items.SHIELD)
                        )
               )) {
               searchInvResult = searchInvResult3;
            }

            if ((searchInvResult == null || !searchInvResult.isFlag())
               && this.getMace() != AttributeSwap.Mace.OFF
               && !(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem() instanceof MaceItem)) {
               searchInvResult = switch (AttributeSwap.WhenMappings.values[this.getMace().ordinal()]) {
                  case 1 -> inventory2;
                  case 2 -> inventory3;
                  default -> inventory;
               };
            }

            if ((searchInvResult == null || !searchInvResult.isFlag()) && this.getKnockBack()) {
               searchInvResult = inventory4;
            }

            if (searchInvResult != null && searchInvResult.isFlag()) {
               value = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
               searchInvResult.update();
               int value2 = value;
               if (this.getDelay() > 0) {
                  TaskScheduler.defaultImpl(
                     TaskScheduler.INSTANCE, this.getDelay(), (byte)0, false, () -> InventoryManager.INSTANCE.applyGetInventory2(value2), 6, null
                  );
               }
            }
         }
      }
   }

   @Flow
   private final void onAfterSendPacket(AfterSendPacket afterSendPacket) {
      if (afterSendPacket.getPacket() instanceof ServerboundAttackPacket && value != -1 && this.getDelay() == 0) {
         InventoryManager.INSTANCE.applyGetInventory2(value);
         value = -1;
      }
   }

   static {
   }

   public static enum Mace {
      BREACH,
      DENSITY,
      ANY,
      OFF;

      private static final AttributeSwap.Mace[] maces = $values();
      private static final EnumEntries<AttributeSwap.Mace> enumEntries = EnumEntriesKt.enumEntries(maces);

      private Mace() {
      }

      @NotNull
      public static EnumEntries<AttributeSwap.Mace> getEnumEntries() {
         return enumEntries;
      }

      private static final AttributeSwap.Mace[] $values() {
         return new AttributeSwap.Mace[]{BREACH, DENSITY, ANY, OFF};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[AttributeSwap.Mace.values().length];

         try {
            values2[AttributeSwap.Mace.BREACH.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[AttributeSwap.Mace.DENSITY.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values2;
      }
   }
}