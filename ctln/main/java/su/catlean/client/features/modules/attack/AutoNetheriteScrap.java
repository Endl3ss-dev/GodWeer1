// deobf by pusia
package su.catlean.client.features.modules.attack;

import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.player.PlayerTargetUtil;

public final class AutoNetheriteScrap extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoNetheriteScrap.class, "placeWithRegion", "getPlaceWithRegion()Lsu/catlean/dd;", 0), new PropertyReference1Impl(AutoNetheriteScrap.class, "placeNormal", "getPlaceNormal()Lsu/catlean/dd;", 0), new PropertyReference1Impl(AutoNetheriteScrap.class, "privateOthers", "getPrivateOthers()Z", 0), new PropertyReference1Impl(AutoNetheriteScrap.class, "checkRange", "getCheckRange()Z", 0) };
   @NotNull
   public static final AutoNetheriteScrap INSTANCE = new AutoNetheriteScrap();
   @NotNull
   private static final BindSetting placeWithRegion = SettingFactory.defaultImpl(
      INSTANCE, "place-with-region", new Bind(0, false, false, 7, null), null, null, 12, null
   );
   @NotNull
   private static final BindSetting placeNormal = SettingFactory.defaultImpl(INSTANCE, "place-normal", new Bind(0, false, false, 7, null), null, null, 12, null);
   @NotNull
   private static final BooleanSetting privateOthers = SettingFactory.defaultImpl(INSTANCE, "private-others", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting checkRange = SettingFactory.defaultImpl(INSTANCE, "check-range", false, null, null, 12, null);
   @NotNull
   private static StopWatch stopWatch = new StopWatch();
   @NotNull
   private static StopWatch stopWatch2 = new StopWatch();
   private static boolean flag;
   @NotNull
   private static List items;

   private AutoNetheriteScrap() {
      super("auto-netherite-scrap", SubCategories.getSubCategory2(), null, 4, null);
   }

   private final Bind getPlaceWithRegion() {
      return (Bind)placeWithRegion.computeObject(this, $$delegatedProperties[0]);
   }

   private final Bind getPlaceNormal() {
      return (Bind)placeNormal.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getPrivateOthers() {
      return (Boolean)privateOthers.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getCheckRange() {
      return (Boolean)checkRange.computeObject(this, $$delegatedProperties[3]);
   }

   @Flow
   private final void onInput(InputEvent inputEvent) {
      if ((inputEvent.getKey() == this.getPlaceWithRegion().getValue() || inputEvent.getKey() == this.getPlaceNormal().getValue())
         && inputEvent.getAction() == InputEvent.Action.Press
         && MinecraftHolder.getMinecraft().gui.screen() == null) {
         if (!stopWatch.testValue(50000)) {
            ChatUtil.defaultImpl(this, ChatUtil.defaultImpl(this, "delay", new Object[0], false, 4, null), false, 2, null);
         } else if (this.getCheckRange()
            && PlayerTargetUtil.defaultImpl(PlayerTargetUtil.INSTANCE, 1.5F, AutoCrystal.TargetSort.DISTANCE, false, false, 12, null) == null) {
            ChatUtil.defaultImpl(this, ChatUtil.defaultImpl(this, "distance", new Object[0], false, 4, null), false, 2, null);
         } else {
            if (inputEvent.getKey() == this.getPlaceWithRegion().getValue()) {
               this.update();
            }

            if (inputEvent.getKey() == this.getPlaceNormal().getValue()) {
               this.applyGetInventory();
            }
         }
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if ((flag || this.getPrivateOthers()) && stopWatch2.testValue(15000)) {
         BlockPos above = BlockPos.containing((Position)MinecraftHolder.getLocalPlayer().getEyePosition()).above();
         if (Intrinsics.areEqual(MinecraftHolder.getClientLevel().getBlockState(above.above()).getBlock(), Blocks.OBSIDIAN)) {
            int value = this.getValue();
            if (value == -1) {
               return;
            }

            if (!this.isFlag()) {
               RotationAction defaultImpl = InteractionManager.defaultImpl(
                  InteractionManager.INSTANCE,
                  above,
                  value,
                  PlaceModule.Interact.Strict,
                  PlaceModule.PlaceMode.Normal,
                  TargetFilter.NONE,
                  4.0F,
                  0.0F,
                  200,
                  PlaceModule.Switch.Alternative,
                  null,
                  false,
                  1536,
                  null
               );
               if (defaultImpl != null) {
                  RotationManager.INSTANCE.updateRotationAction(defaultImpl);
                  stopWatch2.update();
                  flag = false;
               }
            }
         }
      }
   }

   private final int getValue() {
      SearchInvResult searchInvResult3 = InventoryManager.INSTANCE.computeSearchInvResult3(items);
      return searchInvResult3.isFlag() ? searchInvResult3.getValue() : -1;
   }

   private final boolean isFlag() {
      BlockPos above = BlockPos.containing((Position)MinecraftHolder.getLocalPlayer().getEyePosition()).above();

      for (int i = -5; i < 6; i++) {
         for (int j = -5; j < 6; j++) {
            for (int k = -5; k < 6; k++) {
               if (items.contains(MinecraftHolder.getClientLevel().getBlockState(above.offset(i, j, k)).getBlock())) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private final void update() {
      this.applyGetInventory();
      flag = true;
      TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, 10, (byte)0, false, () -> flag = false, 6, null);
   }

   private final void applyGetInventory() {
      SearchInvResult inventory = InventoryManager.INSTANCE.fetchInventory(arg -> Intrinsics.areEqual(arg.getItem(), Items.NETHERITE_SCRAP));
      if (inventory.isFlag()) {
         InventoryManager.INSTANCE.updateValue(inventory.getValue());
         ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
         MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         InventoryManager.INSTANCE.update();
         stopWatch.update();
      } else {
         SearchInvResult inventory2 = InventoryManager.INSTANCE.fetchInventory2(arg2 -> Intrinsics.areEqual(arg2.getItem(), Items.NETHERITE_SCRAP));
         if (inventory2.isFlag()) {
            GuiMove.INSTANCE.setFlag5(true);
            ContainerUtil.defaultImpl(inventory2.getValue(), MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), ContainerInput.SWAP, false, 8, null);
            ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
            int value = inventory2.getValue();
            int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
            Object object = null;
            byte byteValue = 8;
            boolean flag2 = false;
            ContainerInput swap = ContainerInput.SWAP;
            int value2 = selectedSlot;
            int value3 = value;
            ContainerUtil.defaultImpl(value3, value2, swap, flag2, byteValue, object);
            GuiMove.INSTANCE.setFlag5(false);
            stopWatch.update();
         }
      }
   }

   static {
      Block[] blocks = new Block[]{Blocks.COAL_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_ORE, Blocks.ANCIENT_DEBRIS};
      items = CollectionsKt.listOf(blocks);
   }
}