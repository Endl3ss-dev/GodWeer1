// deobf by pusia
package su.catlean.client.features.modules.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.SettingHolder;
import su.catlean.api.setting.settings.BlocksSetting;
import su.catlean.api.setting.values.BlocksValue;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InteractionManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;

public final class SourceFiller extends PlaceModule {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(SourceFiller.class, "blackList", "getBlackList()Lsu/catlean/api/setting/values/BlocksValue;", 0) };
   @NotNull
   public static final SourceFiller INSTANCE = new SourceFiller();
   @NotNull
   private static final BlocksSetting blackList;

   private SourceFiller() {
      super("source-filler", SubCategories.getSubCategory11(), 0, false, 12, null);
   }

   private final BlocksValue getBlackList() {
      return (BlocksValue)blackList.computeObject(this, $$delegatedProperties[0]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      BlockPos containing = BlockPos.containing((Position)MinecraftHolder.getLocalPlayer().getEyePosition());
      int range = (int)((float)Math.ceil(this.getRange())) + 1;
      List<BlockPos> arrayList = new ArrayList();
      Iterable intRange = (Iterable)(new IntRange(containing.getX() - range, containing.getX() + range));
      Collection arrayList2 = new ArrayList();
      Iterator iterator = intRange.iterator();

      while (iterator.hasNext()) {
         int nextInt = ((IntIterator)iterator).nextInt();
         int value = nextInt;
         Iterable intRange2 = (Iterable)(new IntRange(containing.getY() - range, containing.getY() + range));
         var arrayList3 = new ArrayList();
         Iterator iterator2 = intRange2.iterator();

         while (iterator2.hasNext()) {
            int nextInt2 = ((IntIterator)iterator2).nextInt();
            int value2 = nextInt2;
            Iterable intRange3 = (Iterable)(new IntRange(containing.getZ() - range, containing.getZ() + range));
            var arrayList4 = new ArrayList();
            Iterator iterator3 = intRange3.iterator();

            while (iterator3.hasNext()) {
               int nextInt3 = ((IntIterator)iterator3).nextInt();
               BlockPos blockPos = new BlockPos(value, value2, nextInt3);
               Block block = MinecraftHolder.getClientLevel().getBlockState(blockPos).getBlock();
               if (Intrinsics.areEqual(block, net.minecraft.world.level.block.Blocks.WATER)
                  || Intrinsics.areEqual(block, net.minecraft.world.level.block.Blocks.LAVA)) {
                  FluidState fluidState = MinecraftHolder.getClientLevel().getFluidState(blockPos);
                  if (fluidState.isSource() && INSTANCE.testBlockPos(blockPos)) {
                     arrayList.add(blockPos);
                  }
               }

               Unit instance = Unit.INSTANCE;
               arrayList4.add(instance);
            }

            var value3 = arrayList4;
            CollectionsKt.addAll(arrayList3, value3);
         }

         var value4 = arrayList3;
         CollectionsKt.addAll(arrayList2, value4);
      }

      if (!arrayList.isEmpty()) {
         PlaceModule thisValue = this;
         intRange = arrayList;
         PlaceModule.defaultImpl(thisValue, CollectionsKt.sortedWith(intRange, new SourceFiller.Ordering()), false, false, null, true, 14, null);
      }
   }

   public final boolean testBlockPos(BlockPos pos) {
      return InteractionManager.INSTANCE.checkContainsKey(pos, this.getInteract(), this.getTargetFilter(), this.getRange(), this.getWallRange());
   }

   @NotNull
   @Override
   public List getItems3() {
      return this.getBlackList().getBlocks();
   }

   static {
      SettingHolder instance = INSTANCE;
      Block[] blocks = new Block[]{net.minecraft.world.level.block.Blocks.OBSIDIAN};
      blackList = SettingFactory.defaultImpl(instance, "black-list", new BlocksValue(CollectionsKt.mutableListOf(blocks)), null, null, 12, null);
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         BlockPos object3 = (BlockPos)object;
         Comparable distanceToSqr = MinecraftHolder.getLocalPlayer().distanceToSqr(Vec3iUtil.computeVec3(object3));
         object3 = (BlockPos)object2;
         Comparable comparable = distanceToSqr;
         return ComparisonsKt.compareValues(comparable, MinecraftHolder.getLocalPlayer().distanceToSqr(Vec3iUtil.computeVec3(object3)));
      }
   }
}