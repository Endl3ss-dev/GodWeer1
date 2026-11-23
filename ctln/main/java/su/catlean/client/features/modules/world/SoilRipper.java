// deobf by pusia
package su.catlean.client.features.modules.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
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

public final class SoilRipper extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(SoilRipper.class, "switchMode", "getSwitchMode()Lsu/catlean/uv;", 0), new MutablePropertyReference1Impl(SoilRipper.class, "delay", "getDelay()I", 0) };
   @NotNull
   public static final SoilRipper INSTANCE = new SoilRipper();
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(INSTANCE, "switch", AutoSoup.Switch.SILENT_FULL, null, null, 12, null);
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 0, new IntRange(0, 10), null, null, 24, null);
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();

   private SoilRipper() {
      super("soil-ripper", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final AutoSoup.Switch getSwitchMode() {
      return (AutoSoup.Switch)switchValue.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (playerUtil.testValue2(this.getDelay())) {
         Iterable items = InteractionManager.INSTANCE.getItems();
         boolean flag = false;
         var arrayList = new ArrayList();

         for (Object object : items) {
            BlockPos blockPos = (BlockPos)object;
            Block block = MinecraftHolder.getClientLevel().getBlockState(blockPos).getBlock();
            Block block2 = MinecraftHolder.getClientLevel().getBlockState(blockPos.above()).getBlock();
            if ((Intrinsics.areEqual(block, Blocks.GRASS_BLOCK) || Intrinsics.areEqual(block, Blocks.DIRT) || Intrinsics.areEqual(block, Blocks.DIRT_PATH))
               && Intrinsics.areEqual(block2, Blocks.AIR)
               && MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(Vec3iUtil.computeVec3(blockPos).add(0.0, 0.5, 0.0))
                  < MinecraftHolder.getLocalPlayer().blockInteractionRange()) {
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
         List sortedWith = CollectionsKt.sortedWith(items, new SoilRipper.Ordering());
         SearchInvResult searchInvResult = InventoryManager.INSTANCE.computeSearchInvResult(arg -> arg.getItem() instanceof HoeItem, this.getSwitchMode());
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