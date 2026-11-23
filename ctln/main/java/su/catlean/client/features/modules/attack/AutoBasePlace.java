// deobf by pusia
package su.catlean.client.features.modules.attack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpongeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.protect.TrapPlanner;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.ThreadManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerUtil;

public final class AutoBasePlace extends PlaceModule {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoBasePlace.class, "sponges", "getSponges()Z", 0), new PropertyReference1Impl(AutoBasePlace.class, "spongeDelay", "getSpongeDelay()I", 0), new PropertyReference1Impl(AutoBasePlace.class, "onlyNoPositions", "getOnlyNoPositions()Z", 0) };
   @NotNull
   public static final AutoBasePlace INSTANCE = new AutoBasePlace();
   @NotNull
   private static final BooleanSetting sponges = SettingFactory.defaultImpl(INSTANCE, "sponges", false, null, null, 12, null);
   @NotNull
   private static final IntSetting spongeDelay = SettingFactory.defaultImpl(
      INSTANCE, "sponge-delay", 10, new IntRange(0, 60), null, () -> INSTANCE.getSponges(), 8, null
   );
   @NotNull
   private static final BooleanSetting onlyNoPositions = SettingFactory.defaultImpl(INSTANCE, "only-no-positions", false, null, null, 12, null);
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();
   @NotNull
   private static final PlayerUtil playerUtil2 = new PlayerUtil();
   @Nullable
   private static BlockHitResult blockHitResult;
   @Nullable
   private static BlockPos blockPos;

   private AutoBasePlace() {
      super("auto-base-place", SubCategories.getSubCategory2(), 0, false, 12, null);
   }

   private final boolean getSponges() {
      return (Boolean)sponges.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getSpongeDelay() {
      return ((Number)spongeDelay.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final boolean getOnlyNoPositions() {
      return (Boolean)onlyNoPositions.computeObject(this, $$delegatedProperties[2]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (AutoCrystal.INSTANCE.getEnabled() && AutoCrystal.INSTANCE.getPlayer() != null) {
         boolean flag;
         label93: {
            if (this.getSponges()) {
               InventoryManager instance = InventoryManager.INSTANCE;
               Block[] blocks = new Block[]{Blocks.SPONGE};
               if (instance.computeSearchInvResult(blocks).isFlag()) {
                  flag = true;
                  break label93;
               }
            }

            flag = false;
         }

         boolean flag2 = flag;
         ThreadManager instance2 = ThreadManager.INSTANCE;
         byte byteValue = 0;
         boolean flag3 = false;
         instance2.getExecutorService().execute(new AutoBasePlace.Task(byteValue, flag2));
         if (playerUtil.testValue(10)) {
            if (blockHitResult != null) {
               BlockHitResult blockHitResult2 = blockHitResult;
               PlaceModule.defaultImpl(INSTANCE, CollectionsKt.listOf(blockHitResult2.getBlockPos()), false, false, null, false, 30, null);
               blockPos = blockHitResult2.getBlockPos();
               playerUtil.update();
            }
         }

         if (flag2 && blockPos != null && playerUtil2.testValue(this.getSpongeDelay())) {
            BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(blockPos.above());
            if (blockState.isAir() || !blockState.canBeReplaced()) {
               return;
            }

            Iterable enumEntries = (Iterable)AutoBasePlace.EntriesMappings.enumEntries;
            flag3 = false;
            if (enumEntries instanceof Collection && ((Collection)enumEntries).isEmpty()) {
               flag = false;
            } else {
               Iterator iterator = enumEntries.iterator();

               while (true) {
                  if (!iterator.hasNext()) {
                     flag = false;
                     break;
                  }

                  Object object = iterator.next();
                  Direction direction = (Direction)object;
                  if (direction != Direction.UP && MinecraftHolder.getClientLevel().getBlockState(blockPos.relative(direction)).getBlock() instanceof SpongeBlock) {
                     flag = true;
                     break;
                  }
               }
            }

            if (flag) {
               return;
            }

            Iterable enumEntries2 = (Iterable)AutoBasePlace.EntriesMappings.enumEntries;
            var arrayList = new ArrayList();

            for (Object object2 : enumEntries2) {
               Direction direction2 = (Direction)object2;
               if (direction2 != Direction.UP) {
                  arrayList.add(object2);
               }
            }

            for (Direction direction3 : (Iterable<Direction>)arrayList) {
               BlockPos relative = blockPos.relative(direction3);
               if (this.testBlockPos(relative)) {
                  PlaceModule.defaultImpl(this, CollectionsKt.listOf(relative), false, false, CollectionsKt.listOf(Blocks.SPONGE), false, 22, null);
                  playerUtil2.update();
                  break;
               }
            }
         }
      }
   }

   public final boolean testBlockPos(BlockPos pos) {
      return InteractionManager.INSTANCE.checkContainsKey(pos, this.getInteract(), this.getTargetFilter(), this.getRange(), this.getWallRange());
   }

   @NotNull
   @Override
   public List<Block> getItems3() {
      return CollectionsKt.listOf(Blocks.OBSIDIAN);
   }

   public static final void setBlockHitResult(BlockHitResult blockHitResult) {
      AutoBasePlace.blockHitResult = blockHitResult;
   }

   public static final boolean testAutoBasePlace(AutoBasePlace autoBasePlace) {
      return autoBasePlace.getOnlyNoPositions();
   }

   static {
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
   }

   public static final class Task implements Runnable {
      public final int value;
      public final boolean flag;

      public Task(int value, boolean flag) {
         this.value = value;
         this.flag = flag;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
            AutoBasePlace.setBlockHitResult(
               TrapPlanner.computeBlockHitResult(AutoCrystal.INSTANCE, AutoBasePlace.testAutoBasePlace(AutoBasePlace.INSTANCE), this.flag)
            );
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
         }
      }
   }
}