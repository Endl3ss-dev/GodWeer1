// deobf by pusia
package su.catlean.client.features.modules.world;

import net.minecraft.world.item.Item;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

public final class AutoLand extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(AutoLand.class, "switchMode", "getSwitchMode()Lsu/catlean/uv;", 0), new MutablePropertyReference1Impl(AutoLand.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(AutoLand.class, "group", "getGroup()Lsu/catlean/nl;", 0), new MutablePropertyReference1Impl(AutoLand.class, "wheat", "getWheat()Z", 0), new MutablePropertyReference1Impl(AutoLand.class, "carrot", "getCarrot()Z", 0), new MutablePropertyReference1Impl(AutoLand.class, "potato", "getPotato()Z", 0), new MutablePropertyReference1Impl(AutoLand.class, "melon", "getMelon()Z", 0), new MutablePropertyReference1Impl(AutoLand.class, "pumpkin", "getPumpkin()Z", 0), new MutablePropertyReference1Impl(AutoLand.class, "beetRoot", "getBeetRoot()Z", 0), new MutablePropertyReference1Impl(AutoLand.class, "sugarCane", "getSugarCane()Z", 0), new MutablePropertyReference1Impl(AutoLand.class, "netherWart", "getNetherWart()Z", 0) };
   @NotNull
   public static final AutoLand INSTANCE = new AutoLand();
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
   private static final BooleanSetting sugarCane = SettingFactory.defaultImpl(INSTANCE, "sugar-cane", false, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting netherWart = SettingFactory.defaultImpl(INSTANCE, "nether-wart", false, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();

   private AutoLand() {
      super("auto-land", SubCategories.getSubCategory8(), null, 4, null);
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

   private final boolean getSugarCane() {
      return (Boolean)sugarCane.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getNetherWart() {
      return (Boolean)netherWart.computeObject(this, $$delegatedProperties[10]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (playerUtil.testValue2(this.getDelay())) {
         if (this.isFlag()) {
            return;
         }

         if (this.getSugarCane() && this.isFlag3()) {
            return;
         }

         if (this.getNetherWart()) {
            this.isFlag2();
         }
      }
   }

   private final boolean isFlag() {
      Iterable items = InteractionManager.INSTANCE.getItems();
      boolean flag = false;
      var arrayList = new ArrayList();

      for (Object object : items) {
         BlockPos blockPos = (BlockPos)object;
         Block block = MinecraftHolder.getClientLevel().getBlockState(blockPos).getBlock();
         if (Intrinsics.areEqual(block, Blocks.FARMLAND)
            && MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(Vec3iUtil.computeVec3(blockPos).add(0.0, 0.4375, 0.0))
               < MinecraftHolder.getLocalPlayer().blockInteractionRange()
            && MinecraftHolder.getClientLevel().getBlockState(blockPos.above()).isAir()) {
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
      List sortedWith = CollectionsKt.sortedWith(items, new AutoLand.Ordering());
      List<Item> items2 = this.getItems();
      SearchInvResult searchInvResult = InventoryManager.INSTANCE.computeSearchInvResult(arg -> items2.contains(arg.getItem()), this.getSwitchMode());
      if (!sortedWith.isEmpty() && searchInvResult.isFlag()) {
         BlockHitResult first = (BlockHitResult)CollectionsKt.first(sortedWith);
         RotationManager.INSTANCE.updateRotationAction(new LookRotationAction(Rotation.vectorUtil.computeRotation(first.getLocation()), 0, () -> {
            InventoryManager.INSTANCE.applyGetInventory(searchInvResult.getValue(), INSTANCE.getSwitchMode(), () -> {
               MinecraftHolder.getMultiPlayerGameMode().useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, first);
               MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
               return Unit.INSTANCE;
            });
            return Unit.INSTANCE;
         }, 2, null));
         return true;
      } else {
         return false;
      }
   }

   private final boolean isFlag2() {
      Iterable items = InteractionManager.INSTANCE.getItems();
      boolean flag = false;
      var arrayList = new ArrayList();

      for (Object object : items) {
         BlockPos blockPos = (BlockPos)object;
         Block block = MinecraftHolder.getClientLevel().getBlockState(blockPos).getBlock();
         if (Intrinsics.areEqual(block, Blocks.SOUL_SAND)
            && MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(Vec3iUtil.computeVec3(blockPos).add(0.0, 0.5, 0.0))
               < MinecraftHolder.getLocalPlayer().blockInteractionRange()
            && MinecraftHolder.getClientLevel().getBlockState(blockPos.above()).isAir()) {
            arrayList.add(object);
         }
      }

      items = arrayList;
      flag = false;
      arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items, 10));

      for (Object object2 : items) {
         BlockPos blockPos2 = (BlockPos)object2;
         arrayList.add(new BlockHitResult(Vec3iUtil.computeVec3(blockPos2).add(0.0, 0.5, 0.0), Direction.UP, blockPos2, false));
      }

      items = arrayList;
      flag = false;
      List sortedWith = CollectionsKt.sortedWith(items, new AutoLand.Ordering3());
      SearchInvResult searchInvResult = InventoryManager.INSTANCE
         .computeSearchInvResult(arg -> Intrinsics.areEqual(arg.getItem(), Items.NETHER_WART), this.getSwitchMode());
      if (!sortedWith.isEmpty() && searchInvResult.isFlag()) {
         BlockHitResult first = (BlockHitResult)CollectionsKt.first(sortedWith);
         RotationManager.INSTANCE.updateRotationAction(new LookRotationAction(Rotation.vectorUtil.computeRotation(first.getLocation()), 0, () -> {
            InventoryManager.INSTANCE.applyGetInventory(searchInvResult.getValue(), INSTANCE.getSwitchMode(), () -> {
               MinecraftHolder.getMultiPlayerGameMode().useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, first);
               MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
               return Unit.INSTANCE;
            });
            return Unit.INSTANCE;
         }, 2, null));
         return true;
      } else {
         return false;
      }
   }

   private final boolean isFlag3() {
      Iterable items = InteractionManager.INSTANCE.getItems();
      boolean flag = false;
      var arrayList = new ArrayList();

      for (Object object : items) {
         boolean flag2;
         label97: {
            BlockPos blockPos = (BlockPos)object;
            Block block = MinecraftHolder.getClientLevel().getBlockState(blockPos).getBlock();
            if (Intrinsics.areEqual(block, Blocks.SAND)
               || Intrinsics.areEqual(block, Blocks.DIRT)
               || Intrinsics.areEqual(block, Blocks.GRASS_BLOCK)
               || Intrinsics.areEqual(block, Blocks.RED_SAND)) {
               Iterable enumEntries = (Iterable)AutoLand.EntriesMappings.enumEntries;
               var arrayList2 = new ArrayList();

               for (Object object2 : enumEntries) {
                  Direction direction = (Direction)object2;
                  if (direction != Direction.UP && direction != Direction.DOWN) {
                     arrayList2.add(object2);
                  }
               }

               enumEntries = arrayList2;
               if (enumEntries instanceof Collection && ((Collection)enumEntries).isEmpty()) {
                  flag2 = false;
               } else {
                  Iterator iterator = enumEntries.iterator();

                  while (true) {
                     if (!iterator.hasNext()) {
                        flag2 = false;
                        break;
                     }

                     Object object3 = iterator.next();
                     Direction direction2 = (Direction)object3;
                     if (Intrinsics.areEqual(MinecraftHolder.getClientLevel().getBlockState(blockPos.relative(direction2)).getBlock(), Blocks.WATER)) {
                        flag2 = true;
                        break;
                     }
                  }
               }

               if (flag2
                  && MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(Vec3iUtil.computeVec3(blockPos).add(0.0, 0.5, 0.0))
                     < MinecraftHolder.getLocalPlayer().blockInteractionRange()
                  && MinecraftHolder.getClientLevel().getBlockState(blockPos.above()).isAir()) {
                  flag2 = true;
                  break label97;
               }
            }

            flag2 = false;
         }

         if (flag2) {
            arrayList.add(object);
         }
      }

      items = arrayList;
      flag = false;
      arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items, 10));

      for (Object object4 : items) {
         BlockPos blockPos2 = (BlockPos)object4;
         arrayList.add(new BlockHitResult(Vec3iUtil.computeVec3(blockPos2).add(0.0, 0.5, 0.0), Direction.UP, blockPos2, false));
      }

      items = arrayList;
      flag = false;
      List sortedWith = CollectionsKt.sortedWith(items, new AutoLand.Ordering2());
      SearchInvResult searchInvResult = InventoryManager.INSTANCE
         .computeSearchInvResult(arg -> Intrinsics.areEqual(arg.getItem(), Items.SUGAR_CANE), this.getSwitchMode());
      if (!sortedWith.isEmpty() && searchInvResult.isFlag()) {
         BlockHitResult first = (BlockHitResult)CollectionsKt.first(sortedWith);
         RotationManager.INSTANCE.updateRotationAction(new LookRotationAction(Rotation.vectorUtil.computeRotation(first.getLocation()), 0, () -> {
            InventoryManager.INSTANCE.applyGetInventory(searchInvResult.getValue(), INSTANCE.getSwitchMode(), () -> {
               MinecraftHolder.getMultiPlayerGameMode().useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, first);
               MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
               return Unit.INSTANCE;
            });
            return Unit.INSTANCE;
         }, 2, null));
         return true;
      } else {
         return false;
      }
   }

   private final List<Item> getItems() {
      List<Item> arrayList = new ArrayList();
      if (this.getWheat()) {
         arrayList.add(Items.WHEAT_SEEDS);
      }

      if (this.getCarrot()) {
         arrayList.add(Items.CARROT);
      }

      if (this.getPotato()) {
         arrayList.add(Items.POTATO);
      }

      if (this.getPumpkin()) {
         arrayList.add(Items.PUMPKIN_SEEDS);
      }

      if (this.getMelon()) {
         arrayList.add(Items.MELON_SEEDS);
      }

      if (this.getBeetRoot()) {
         arrayList.add(Items.BEETROOT_SEEDS);
      }

      return arrayList;
   }

   static {
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
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

   public static final class Ordering2 implements Comparator {
      public Ordering2() {
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

   public static final class Ordering3 implements Comparator {
      public Ordering3() {
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