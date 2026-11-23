// deobf by pusia
package su.catlean.client.features.modules.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.BoxRenderCommand;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.render.Render2D;

public final class AutoWither extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(AutoWither.class, "timing", "getTiming()Lsu/catlean/u_;", 0), new MutablePropertyReference1Impl(AutoWither.class, "blocksPerAction", "getBlocksPerAction()I", 0), new MutablePropertyReference1Impl(AutoWither.class, "delayPerAction", "getDelayPerAction()I", 0), new MutablePropertyReference1Impl(AutoWither.class, "rotate", "getRotate()Z", 0), new MutablePropertyReference1Impl(AutoWither.class, "placeMode", "getPlaceMode()Lsu/catlean/eb;", 0), new MutablePropertyReference1Impl(AutoWither.class, "interact", "getInteract()Lsu/catlean/tm;", 0), new MutablePropertyReference1Impl(AutoWither.class, "switch", "getSwitch()Lsu/catlean/iu;", 0), new MutablePropertyReference1Impl(AutoWither.class, "range", "getRange()F", 0), new MutablePropertyReference1Impl(AutoWither.class, "wallRange", "getWallRange()F", 0), new MutablePropertyReference1Impl(AutoWither.class, "render", "getRender()Z", 0), new MutablePropertyReference1Impl(AutoWither.class, "pose", "getPose()Lsu/catlean/y8;", 0) };
   @NotNull
   public static final AutoWither INSTANCE = new AutoWither();
   @NotNull
   private static final EnumSetting timing = SettingFactory.defaultImpl(INSTANCE, "timing", PlaceModule.Timing.Vanilla, null, null, 12, null);
   @NotNull
   private static final IntSetting blocksPerAction = SettingFactory.defaultImpl(
      INSTANCE, "blocks-per-action", 6, new IntRange(2, 6), null, () -> INSTANCE.getTiming() == PlaceModule.Timing.Instant, 8, null
   );
   @NotNull
   private static final IntSetting delayPerAction = SettingFactory.defaultImpl(
      INSTANCE, "delay-per-action", 0, new IntRange(0, 10), null, () -> INSTANCE.getTiming() == PlaceModule.Timing.Instant, 8, null
   );
   @NotNull
   private static final BooleanSetting rotate = SettingFactory.defaultImpl(INSTANCE, "rotate", true, null, null, 12, null);
   @NotNull
   private static final EnumSetting placeMode = SettingFactory.defaultImpl(INSTANCE, "place-mode", PlaceModule.PlaceMode.Normal, null, null, 12, null);
   @NotNull
   private static final EnumSetting interact = SettingFactory.defaultImpl(INSTANCE, "interact", PlaceModule.Interact.Strict, null, null, 12, null);
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(INSTANCE, "switch", PlaceModule.Switch.Silent, null, null, 12, null);
   @NotNull
   private static final FloatSetting range = SettingFactory.defaultImpl(INSTANCE, "range", 4.0F, RangesKt.rangeTo(2.0F, 10.0F), null, null, 24, null);
   @NotNull
   private static final FloatSetting wallRange = SettingFactory.defaultImpl(INSTANCE, "wall-range", 4.0F, RangesKt.rangeTo(2.0F, 10.0F), null, null, 24, null);
   @NotNull
   private static final BooleanSetting render = SettingFactory.defaultImpl(INSTANCE, "render", true, null, null, 12, null);
   @NotNull
   private static final EnumSetting pose = SettingFactory.defaultImpl(INSTANCE, "pose", AutoWither.Pose.LYING, null, null, 12, null);
   @NotNull
   private static final List<Pair<BlockPos, Boolean>> items = new ArrayList();
   private static int value;
   @NotNull
   private static StopWatch stopWatch = new StopWatch();

   private AutoWither() {
      super("auto-wither", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final PlaceModule.Timing getTiming() {
      return (PlaceModule.Timing)timing.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getBlocksPerAction() {
      return ((Number)blocksPerAction.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final int getDelayPerAction() {
      return ((Number)delayPerAction.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final boolean getRotate() {
      return (Boolean)rotate.computeObject(this, $$delegatedProperties[3]);
   }

   private final PlaceModule.PlaceMode getPlaceMode() {
      return (PlaceModule.PlaceMode)placeMode.computeObject(this, $$delegatedProperties[4]);
   }

   private final PlaceModule.Interact getInteract() {
      return (PlaceModule.Interact)interact.computeObject(this, $$delegatedProperties[5]);
   }

   private final PlaceModule.Switch getSwitch() {
      return (PlaceModule.Switch)switchValue.computeObject(this, $$delegatedProperties[6]);
   }

   private final float getRange() {
      return ((Number)range.computeObject(this, $$delegatedProperties[7])).floatValue();
   }

   private final float getWallRange() {
      return ((Number)wallRange.computeObject(this, $$delegatedProperties[8])).floatValue();
   }

   private final boolean getRender() {
      return (Boolean)render.computeObject(this, $$delegatedProperties[9]);
   }

   private final AutoWither.Pose getPose() {
      return (AutoWither.Pose)pose.computeObject(this, $$delegatedProperties[10]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getMinecraft().options.keyUse.isDown()
         && stopWatch.testValue2(250)
         && Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.SOUL_SAND)) {
         items.clear();
         HitResult hitResult = MinecraftHolder.getMinecraft().hitResult;
         if (hitResult == null) {
            return;
         }

         HitResult hitResult2 = hitResult;
         if (hitResult2.getType() != Type.BLOCK || !(hitResult2 instanceof BlockHitResult)) {
            return;
         }

         this.applyAbove(((BlockHitResult)hitResult2).getBlockPos().relative(((BlockHitResult)hitResult2).getDirection()));
      }

      if (value > 0) {
         int value2 = value;
         value = value2 + -1;
      } else if (!items.isEmpty()) {
         int blocksPerAction2 = 0;
         blocksPerAction2 = this.getBlocksPerAction();
         Iterator<Pair<BlockPos, Boolean>> iterator = items.iterator();

         while (iterator.hasNext()) {
            Pair pair = (Pair)iterator.next();
            if (blocksPerAction2 <= 0) {
               break;
            }

            InventoryManager instance = InventoryManager.INSTANCE;
            Item[] items2 = new Item[1];
            Item souL_SAND = (boolean)pair.getSecond() ? Items.SOUL_SAND : Items.WITHER_SKELETON_SKULL;
            items2[0] = souL_SAND;
            int value3 = instance.computeSearchInvResult(items2).getValue();
            if (value3 != -1) {
               RotationAction defaultImpl = InteractionManager.defaultImpl(
                  InteractionManager.INSTANCE,
                  (BlockPos)pair.getFirst(),
                  value3,
                  this.getInteract(),
                  this.getPlaceMode(),
                  TargetFilter.ALL,
                  this.getRange(),
                  this.getWallRange(),
                  150,
                  this.getSwitch(),
                  null,
                  false,
                  1536,
                  null
               );
               if (defaultImpl != null) {
                  INSTANCE.updateBlockPos((BlockPos)pair.getFirst(), defaultImpl);
                  iterator.remove();
                  if (INSTANCE.getTiming() != PlaceModule.Timing.Instant) {
                     break;
                  }

                  blocksPerAction2 += -1;
                  value = INSTANCE.getDelayPerAction();
               }
            }
         }
      }
   }

   private final void applyAbove(BlockPos blockPos) {
      boolean flag = this.getPose() == AutoWither.Pose.STANDING;
      switch (AutoWither.WhenMappings.values[MinecraftHolder.getLocalPlayer().getNearestViewDirection().ordinal()]) {
         case 1:
            if (flag) {
               items.add(TuplesKt.to(blockPos.above(), true));
               items.add(TuplesKt.to(blockPos.above().south(), true));
               items.add(TuplesKt.to(blockPos.above().north(), true));
               items.add(TuplesKt.to(blockPos.above().above(), false));
               items.add(TuplesKt.to(blockPos.above().above().south(), false));
               items.add(TuplesKt.to(blockPos.above().above().north(), false));
            } else {
               items.add(TuplesKt.to(blockPos.west(), true));
               items.add(TuplesKt.to(blockPos.west().south(), true));
               items.add(TuplesKt.to(blockPos.west().north(), true));
               items.add(TuplesKt.to(blockPos.west().west().south(), false));
               items.add(TuplesKt.to(blockPos.west().west().north(), false));
               items.add(TuplesKt.to(blockPos.west().west(), false));
            }
            break;
         case 2:
            if (flag) {
               items.add(TuplesKt.to(blockPos.above(), true));
               items.add(TuplesKt.to(blockPos.above().east(), true));
               items.add(TuplesKt.to(blockPos.above().west(), true));
               items.add(TuplesKt.to(blockPos.above().above(), false));
               items.add(TuplesKt.to(blockPos.above().above().east(), false));
               items.add(TuplesKt.to(blockPos.above().above().west(), false));
            } else {
               items.add(TuplesKt.to(blockPos.south(), true));
               items.add(TuplesKt.to(blockPos.south().east(), true));
               items.add(TuplesKt.to(blockPos.south().west(), true));
               items.add(TuplesKt.to(blockPos.south().south().east(), false));
               items.add(TuplesKt.to(blockPos.south().south().west(), false));
               items.add(TuplesKt.to(blockPos.south().south(), false));
            }
            break;
         case 3:
            if (flag) {
               items.add(TuplesKt.to(blockPos.above(), true));
               items.add(TuplesKt.to(blockPos.above().west(), true));
               items.add(TuplesKt.to(blockPos.above().east(), true));
               items.add(TuplesKt.to(blockPos.above().above(), false));
               items.add(TuplesKt.to(blockPos.above().above().west(), false));
               items.add(TuplesKt.to(blockPos.above().above().east(), false));
            } else {
               items.add(TuplesKt.to(blockPos.north(), true));
               items.add(TuplesKt.to(blockPos.north().east(), true));
               items.add(TuplesKt.to(blockPos.north().west(), true));
               items.add(TuplesKt.to(blockPos.north().north().east(), false));
               items.add(TuplesKt.to(blockPos.north().north().west(), false));
               items.add(TuplesKt.to(blockPos.north().north(), false));
            }
            break;
         case 4:
            if (flag) {
               items.add(TuplesKt.to(blockPos.above(), true));
               items.add(TuplesKt.to(blockPos.above().north(), true));
               items.add(TuplesKt.to(blockPos.above().south(), true));
               items.add(TuplesKt.to(blockPos.above().above(), false));
               items.add(TuplesKt.to(blockPos.above().above().north(), false));
               items.add(TuplesKt.to(blockPos.above().above().south(), false));
            } else {
               items.add(TuplesKt.to(blockPos.east(), true));
               items.add(TuplesKt.to(blockPos.east().south(), true));
               items.add(TuplesKt.to(blockPos.east().north(), true));
               items.add(TuplesKt.to(blockPos.east().east().south(), false));
               items.add(TuplesKt.to(blockPos.east().east().north(), false));
               items.add(TuplesKt.to(blockPos.east().east(), false));
            }
      }
   }

   private final void updateBlockPos(BlockPos blockPos, RotationAction rotationAction) {
      if (INSTANCE.getRotate()) {
         if (INSTANCE.getTiming() == PlaceModule.Timing.Instant) {
            RotationManager.INSTANCE.updateRotationAction2(rotationAction);
         } else {
            RotationManager.INSTANCE.updateRotationAction(rotationAction);
         }
      } else {
         rotationAction.getAction().invoke();
         rotationAction.updateCallback(() -> Unit.INSTANCE);
      }

      if (INSTANCE.getRender()) {
         BoxRenderCommand.INSTANCE
            .applyPut(
               blockPos,
               Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 1.0F),
               Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 1.0F),
               PlaceModule.Animation.Fade,
               PlaceModule.Render.BOTH
            );
      }
   }

   static {
   }

   public static enum Pose {
      STANDING,
      LYING;

      private static final AutoWither.Pose[] poses = $values();
      private static final EnumEntries<AutoWither.Pose> enumEntries = EnumEntriesKt.enumEntries(poses);

      private Pose() {
      }

      @NotNull
      public static EnumEntries<AutoWither.Pose> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoWither.Pose[] $values() {
         return new AutoWither.Pose[]{STANDING, LYING};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[Direction.values().length];

         try {
            values2[Direction.EAST.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Direction.NORTH.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[Direction.SOUTH.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[Direction.WEST.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         values = values2;
      }
   }
}