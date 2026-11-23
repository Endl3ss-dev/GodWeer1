// deobf by pusia
package su.catlean.client.features.modules.misc;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.StopUsingItemEvent;
import su.catlean.api.event.events.player.UsingItemEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;

public final class AutoEat extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoEat.class, "hunger", "getHunger()I", 0), new PropertyReference1Impl(AutoEat.class, "swapBack", "getSwapBack()Z", 0), new PropertyReference1Impl(AutoEat.class, "gapple", "getGapple()Z", 0), new PropertyReference1Impl(AutoEat.class, "chorus", "getChorus()Z", 0), new PropertyReference1Impl(AutoEat.class, "rottenFlesh", "getRottenFlesh()Z", 0), new PropertyReference1Impl(AutoEat.class, "spiderEye", "getSpiderEye()Z", 0), new PropertyReference1Impl(AutoEat.class, "pufferfish", "getPufferfish()Z", 0) };
   @NotNull
   public static final AutoEat INSTANCE = new AutoEat();
   @NotNull
   private static final IntSetting intSetting = SettingFactory.defaultImpl(INSTANCE, "Hunger", 8, new IntRange(0, 20), null, null, 24, null);
   @NotNull
   private static final BooleanSetting swapBack = SettingFactory.defaultImpl(INSTANCE, "swap-back", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting gapple = SettingFactory.defaultImpl(INSTANCE, "gapple", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting chorus = SettingFactory.defaultImpl(INSTANCE, "chorus", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting rottenFlesh = SettingFactory.defaultImpl(INSTANCE, "rotten-flesh", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting spiderEye = SettingFactory.defaultImpl(INSTANCE, "spider-eye", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting pufferFish = SettingFactory.defaultImpl(INSTANCE, "puffer-fish", false, null, null, 12, null);
   private static boolean flag;
   private static int value = -1;

   private AutoEat() {
      super("auto-eat", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final int getHunger() {
      return ((Number)intSetting.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   private final boolean getSwapBack() {
      return (Boolean)swapBack.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getGapple() {
      return (Boolean)gapple.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getChorus() {
      return (Boolean)chorus.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getRottenFlesh() {
      return (Boolean)rottenFlesh.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getSpiderEye() {
      return (Boolean)spiderEye.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getPufferfish() {
      return (Boolean)pufferFish.computeObject(this, $$delegatedProperties[6]);
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      flag = false;
      if (MinecraftHolder.getLocalPlayer().getFoodData().getFoodLevel() <= this.getHunger()) {
         boolean isFlag = !this.checkGetComponents(InteractionHand.MAIN_HAND) && !this.checkGetComponents(InteractionHand.OFF_HAND) ? this.isFlag() : true;
         if (isFlag) {
            this.applyIsUsingItem();
         }
      } else if (value != -1 && this.getSwapBack()) {
         InventoryManager.INSTANCE.applyGetInventory2(value);
         value = -1;
      }
   }

   private final void applyIsUsingItem() {
      if (!MinecraftHolder.getLocalPlayer().isUsingItem()) {
         ContainerUtil.defaultImpl(
            this.checkGetComponents(InteractionHand.OFF_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null
         );
      }

      flag = true;
   }

   private final boolean isFlag() {
      for (int i = 0; i < 9; i++) {
         ItemStack item = MinecraftHolder.getLocalPlayer().getInventory().getItem(i);
         if (item.getComponents().has(DataComponents.FOOD)
            && (this.getGapple() || item.getItem() != Items.GOLDEN_APPLE && item.getItem() != Items.ENCHANTED_GOLDEN_APPLE)
            && (this.getChorus() || item.getItem() != Items.CHORUS_FRUIT)
            && (this.getRottenFlesh() || item.getItem() != Items.ROTTEN_FLESH)
            && (this.getSpiderEye() || item.getItem() != Items.SPIDER_EYE)
            && (this.getPufferfish() || item.getItem() != Items.PUFFERFISH)) {
            value = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
            InventoryManager.INSTANCE.applyGetInventory2(i);
            return true;
         }
      }

      return false;
   }

   private final boolean checkGetComponents(InteractionHand interactionHand) {
      ItemStack mainHandItem = interactionHand == InteractionHand.MAIN_HAND
         ? MinecraftHolder.getLocalPlayer().getMainHandItem()
         : MinecraftHolder.getLocalPlayer().getOffhandItem();
      Item item = mainHandItem.getItem();
      return mainHandItem.getComponents().has(DataComponents.FOOD)
         && (this.getGapple() || item != Items.GOLDEN_APPLE && item != Items.ENCHANTED_GOLDEN_APPLE)
         && (this.getChorus() || item != Items.CHORUS_FRUIT)
         && (this.getRottenFlesh() || item != Items.ROTTEN_FLESH)
         && (this.getSpiderEye() || item != Items.SPIDER_EYE)
         && (this.getPufferfish() || item != Items.PUFFERFISH);
   }

   @Flow
   private final void onUsingItem(UsingItemEvent usingItemEvent) {
      if (flag) {
         usingItemEvent.cancel();
      }
   }

   @Flow
   private final void onStopUsingItem(StopUsingItemEvent stopUsingItemEvent) {
      if (flag) {
         stopUsingItemEvent.cancel();
      }
   }

   static {
   }
}