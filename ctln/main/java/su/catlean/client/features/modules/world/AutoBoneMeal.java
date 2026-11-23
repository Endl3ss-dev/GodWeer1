// deobf by pusia
package su.catlean.client.features.modules.world;

import net.minecraft.world.level.block.Block;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.misc.AutoSoup;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.LookRotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.player.PlayerUtil;

public final class AutoBoneMeal extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(AutoBoneMeal.class, "switchMode", "getSwitchMode()Lsu/catlean/uv;", 0), new MutablePropertyReference1Impl(AutoBoneMeal.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(AutoBoneMeal.class, "group", "getGroup()Lsu/catlean/nl;", 0), new MutablePropertyReference1Impl(AutoBoneMeal.class, "wheat", "getWheat()Z", 0), new MutablePropertyReference1Impl(AutoBoneMeal.class, "carrot", "getCarrot()Z", 0), new MutablePropertyReference1Impl(AutoBoneMeal.class, "potato", "getPotato()Z", 0), new MutablePropertyReference1Impl(AutoBoneMeal.class, "melon", "getMelon()Z", 0), new MutablePropertyReference1Impl(AutoBoneMeal.class, "pumpkin", "getPumpkin()Z", 0), new MutablePropertyReference1Impl(AutoBoneMeal.class, "beetRoot", "getBeetRoot()Z", 0) };
   @NotNull
   public static final AutoBoneMeal INSTANCE = new AutoBoneMeal();
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(INSTANCE, "switch", AutoSoup.Switch.SILENT_FULL, null, null, 12, null);
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 0, new IntRange(0, 10), null, null, 24, null);
   @NotNull
   private static final GroupSetting selection = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "selection", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting wheat = SettingFactory.defaultImpl(INSTANCE, "wheat", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting carrot = SettingFactory.defaultImpl(INSTANCE, "carrot", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting potato = SettingFactory.defaultImpl(INSTANCE, "potato", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting melon = SettingFactory.defaultImpl(INSTANCE, "melon", false, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting pumpkin = SettingFactory.defaultImpl(INSTANCE, "pumpkin", false, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting beetroot = SettingFactory.defaultImpl(INSTANCE, "beetroot", false, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();

   private AutoBoneMeal() {
      super("auto-bone-meal", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final AutoSoup.Switch getSwitchMode() {
      return (AutoSoup.Switch)switchValue.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final GroupValue getGroup() {
      return (GroupValue)selection.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getWheat() {
      return (Boolean)wheat.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getCarrot() {
      return (Boolean)carrot.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getPotato() {
      return (Boolean)potato.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getMelon() {
      return (Boolean)melon.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getPumpkin() {
      return (Boolean)pumpkin.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getBeetRoot() {
      return (Boolean)beetroot.computeObject(this, $$delegatedProperties[8]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (playerUtil.testValue2(this.getDelay())) {
         Iterable items = InteractionManager.INSTANCE.getItems();
         boolean flag = false;
         var arrayList = new ArrayList();

         for (Object object : items) {
            BlockPos blockPos = (BlockPos)object;
            BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(blockPos);
            if (INSTANCE.getItems().contains(blockState.getBlock())
               && MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(Vec3iUtil.computeVec3(blockPos).add(0.0, 0.4375, 0.0))
                  < MinecraftHolder.getLocalPlayer().blockInteractionRange()
               && ((CropBlock)blockState.getBlock()).getAge(blockState) != ((CropBlock)blockState.getBlock()).getMaxAge()) {
               arrayList.add(object);
            }
         }

         items = arrayList;
         flag = false;
         arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items, 10));

         for (Object object2 : items) {
            BlockPos blockPos2 = (BlockPos)object2;
            arrayList.add(new BlockHitResult(Vec3iUtil.computeVec3(blockPos2).add(0.0, 0.4375, 0.0), Direction.UP, blockPos2, false));
         }

         items = arrayList;
         flag = false;
         List sortedWith = CollectionsKt.sortedWith(items, new AutoBoneMeal.Ordering());
         SearchInvResult searchInvResult = InventoryManager.INSTANCE
            .computeSearchInvResult(arg -> Intrinsics.areEqual(arg.getItem(), Items.BONE_MEAL), this.getSwitchMode());
         if (sortedWith.isEmpty() || !searchInvResult.isFlag()) {
            return;
         }

         BlockHitResult first = (BlockHitResult)CollectionsKt.first(sortedWith);
         RotationManager.INSTANCE.updateRotationAction(new LookRotationAction(Rotation.vectorUtil.computeRotation(first.getLocation()), 0, () -> {
            InventoryManager.INSTANCE.applyGetInventory(searchInvResult.getValue(), INSTANCE.getSwitchMode(), () -> {
               MinecraftHolder.getMultiPlayerGameMode().useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, first);
               MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
               return Unit.INSTANCE;
            });
            return Unit.INSTANCE;
         }, 2, null));
      }
   }

   private final List<Block> getItems() {
      List<Block> arrayList = new ArrayList();
      if (this.getWheat()) {
         arrayList.add(Blocks.WHEAT);
      }

      if (this.getCarrot()) {
         arrayList.add(Blocks.CARROTS);
      }

      if (this.getPotato()) {
         arrayList.add(Blocks.POTATOES);
      }

      if (this.getPumpkin()) {
         arrayList.add(Blocks.PUMPKIN_STEM);
      }

      if (this.getMelon()) {
         arrayList.add(Blocks.MELON_STEM);
      }

      if (this.getBeetRoot()) {
         arrayList.add(Blocks.BEETROOTS);
      }

      return arrayList;
   }

   static {
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         BlockHitResult object3 = (BlockHitResult)object;
         Comparable distanceToSqr = MinecraftHolder.getLocalPlayer().getEyePosition().distanceToSqr(object3.getLocation());
         object3 = (BlockHitResult)object2;
         Comparable comparable = distanceToSqr;
         return ComparisonsKt.compareValues(comparable, MinecraftHolder.getLocalPlayer().getEyePosition().distanceToSqr(object3.getLocation()));
      }
   }
}