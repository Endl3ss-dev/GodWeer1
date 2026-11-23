// deobf by pusia
package su.catlean.client.features.modules.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.player.AttackBlockEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.world.BlockStateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BlocksSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.BlocksValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.AutoDripstope;
import su.catlean.client.features.modules.player.SpeedMine;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class Nuker extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Nuker.class, "filter", "getFilter()Lsu/catlean/n7;", 0), new PropertyReference1Impl(Nuker.class, "sort", "getSort()Lsu/catlean/ij;", 0), new MutablePropertyReference1Impl(Nuker.class, "selectedBlocks", "getSelectedBlocks()Lsu/catlean/yt;", 0), new PropertyReference1Impl(Nuker.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(Nuker.class, "range", "getRange()F", 0), new PropertyReference1Impl(Nuker.class, "yawAngle", "getYawAngle()I", 0), new PropertyReference1Impl(Nuker.class, "ignoreWalls", "getIgnoreWalls()Z", 0), new PropertyReference1Impl(Nuker.class, "flatten", "getFlatten()Z", 0), new PropertyReference1Impl(Nuker.class, "creative", "getCreative()Z", 0), new PropertyReference1Impl(Nuker.class, "avoidLava", "getAvoidLava()Z", 0) };
   @NotNull
   public static final Nuker INSTANCE = new Nuker();
   @NotNull
   private static final EnumSetting filter = SettingFactory.defaultImpl(INSTANCE, "filter", Nuker.Filter.Select, null, null, 12, null);
   @NotNull
   private static final EnumSetting sort = SettingFactory.defaultImpl(INSTANCE, "sort", Nuker.Sort.FOV, null, null, 12, null);
   @NotNull
   private static final BlocksSetting blocks = SettingFactory.defaultImpl(INSTANCE, "blocks", new BlocksValue(null, 1, null), null, null, 12, null);
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 25, new IntRange(0, 1000), null, null, 24, null);
   @NotNull
   private static final FloatSetting range = SettingFactory.defaultImpl(INSTANCE, "range", 4.2F, RangesKt.rangeTo(3.0F, 7.0F), null, null, 24, null);
   @NotNull
   private static final IntSetting degreesPerTick = SettingFactory.defaultImpl(INSTANCE, "degrees-per-tick", 35, new IntRange(5, 180), null, null, 24, null);
   @NotNull
   private static final BooleanSetting ignoreWalls = SettingFactory.defaultImpl(INSTANCE, "ignore-walls", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting flatten = SettingFactory.defaultImpl(INSTANCE, "flatten", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting creative = SettingFactory.defaultImpl(INSTANCE, "creative", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting avoidLava = SettingFactory.defaultImpl(INSTANCE, "avoid-lava", false, null, null, 12, null);
   @Nullable
   private static Block block;
   @Nullable
   private static Nuker.BlockData blockData;
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   private static boolean flag;

   private Nuker() {
      super("nuker", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final Nuker.Filter getFilter() {
      return (Nuker.Filter)filter.computeObject(this, $$delegatedProperties[0]);
   }

   private final Nuker.Sort getSort() {
      return (Nuker.Sort)sort.computeObject(this, $$delegatedProperties[1]);
   }

   private final BlocksValue getSelectedBlocks() {
      return (BlocksValue)blocks.computeObject(this, $$delegatedProperties[2]);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[3])).intValue();
   }

   private final float getRange() {
      return ((Number)range.computeObject(this, $$delegatedProperties[4])).floatValue();
   }

   private final int getYawAngle() {
      return ((Number)degreesPerTick.computeObject(this, $$delegatedProperties[5])).intValue();
   }

   private final boolean getIgnoreWalls() {
      return (Boolean)ignoreWalls.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getFlatten() {
      return (Boolean)flatten.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getCreative() {
      return (Boolean)creative.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getAvoidLava() {
      return (Boolean)avoidLava.computeObject(this, $$delegatedProperties[9]);
   }

   @Flow
   public final void onAttackBlock(AttackBlockEvent e) {
      if (!MinecraftHolder.getClientLevel().isEmptyBlock(e.getPos())) {
         Block block2 = MinecraftHolder.getClientLevel().getBlockState(e.getPos()).getBlock();
         if (this.getFilter() == Nuker.Filter.Select && !Intrinsics.areEqual(block, block2)) {
            block = block2;
         }
      }
   }

   @Flow
   public final void onBlockState(BlockStateEvent e) {
      if (Intrinsics.areEqual(blockData != null ? blockData.getBp() : null, e.getPos()) && e.getState() != null && e.getState().isAir() && !this.getCreative()) {
         blockData = null;
         this.applyIsDown();
      }
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      this.applyIsDown();
      if (blockData != null) {
         Block block2 = MinecraftHolder.getClientLevel().getBlockState(blockData.getBp()).getBlock();
         if (!Intrinsics.areEqual(block2, block) && this.getFilter() == Nuker.Filter.Select
            || WorldUtil.INSTANCE.computeAmount2(Vec3iUtil.computeVec3(blockData.getBp())) > this.getRange() * this.getRange()
            || Intrinsics.areEqual(block2, Blocks.AIR)) {
            blockData = null;
            this.applyIsDown();
         }
      }

      if (!MinecraftHolder.getMinecraft().options.keyAttack.isDown() && blockData != null) {
         Vec3 vec3d = blockData.getVec3d();
         if (!this.getCreative()) {
            double wrapDegrees = Mth.wrapDegrees(
               Math.toDegrees(Math.atan2(vec3d.z - MinecraftHolder.getLocalPlayer().getZ(), vec3d.x - MinecraftHolder.getLocalPlayer().getX()))
                  - 90.0F
                  - WorldUtil.INSTANCE.getAmount4()
            );
            float amount5 = (float)(
                  -Math.toDegrees(
                     Math.atan2(
                        vec3d.y - MinecraftHolder.getLocalPlayer().getEyePosition().y,
                        Math.sqrt(
                           Math.pow(vec3d.x - MinecraftHolder.getLocalPlayer().getX(), 2.0) + Math.pow(vec3d.z - MinecraftHolder.getLocalPlayer().getZ(), 2.0)
                        )
                     )
                  )
               )
               - WorldUtil.INSTANCE.getAmount5();
            float value = Mth.clamp(Mth.abs((float)wrapDegrees), -this.getYawAngle(), this.getYawAngle());
            float value2 = Mth.clamp(amount5, -this.getYawAngle(), this.getYawAngle());
            flag = Mth.abs((float)wrapDegrees) <= this.getYawAngle();
            float amount4 = WorldUtil.INSTANCE.getAmount4() + (wrapDegrees > 0.0 ? value : -value);
            float amount52 = Mth.clamp(WorldUtil.INSTANCE.getAmount5() + value2, -90.0F, 90.0F);
            RotationManager instance = RotationManager.INSTANCE;
            Rotation rotation = new Rotation(amount4, amount52, false, null, 12, null);
            Function0 function0 = () -> Unit.INSTANCE;
            byte byteValue = 0;
            Rotation rotation2 = rotation;
            instance.updateRotationAction(new RotationAction(rotation2, byteValue, function0));
         }

         if (this.getCreative() && stopWatch.testValue2(this.getDelay())) {
            int range2 = (int)((float)Math.floor(this.getRange()) + 1);
            List<BlockPos> arrayList = new ArrayList();
            BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
            int value3 = -range2;
            if (value3 <= range2) {
               while (true) {
                  int value4 = -range2;
                  if (value4 <= range2) {
                     while (true) {
                        int value5 = -range2;
                        if (value5 <= range2) {
                           while (true) {
                              arrayList.add(containing.offset(value3, value4, value5));
                              if (value5 == range2) {
                                 break;
                              }

                              value5++;
                           }
                        }

                        if (value4 == range2) {
                           break;
                        }

                        value4++;
                     }
                  }

                  if (value3 == range2) {
                     break;
                  }

                  value3++;
               }
            }

            Iterable<BlockPos> iterable = arrayList;

            for (BlockPos sortedWithItem : (Iterable<BlockPos>)CollectionsKt.sortedWith(iterable, new Nuker.Ordering2())) {
               if ((!this.getFlatten() || !(sortedWithItem.getY() < MinecraftHolder.getLocalPlayer().getY())) && (!this.getAvoidLava() || !this.checkGetBlock(sortedWithItem))) {
                  BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(sortedWithItem);
                  if (WorldUtil.INSTANCE.computeAmount2(Vec3iUtil.computeVec3(sortedWithItem)) <= this.getRange() * this.getRange()
                     && this.checkContains(blockState.getBlock())) {
                     PacketUtil.applyCurrentSequence(
                        arg -> (Packet)(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, sortedWithItem, Direction.UP, arg))
                     );
                     PacketUtil.applyCurrentSequence(
                        arg2 -> (Packet)(new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, sortedWithItem, Direction.UP, arg2))
                     );
                     MinecraftHolder.getClientLevel().setBlockAndUpdate(sortedWithItem, Blocks.AIR.defaultBlockState());
                     MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
                  }
               }
            }
         }

         if (!this.getCreative() && flag && stopWatch.testValue2(this.getDelay())) {
            this.applyContinueDestroyBlock();
         }
      }
   }

   public final synchronized void applyContinueDestroyBlock() {
      if (blockData != null && !MinecraftHolder.getMinecraft().options.keyAttack.isDown()) {
         if (this.getEnabled() && SpeedMine.INSTANCE.getMode() != SpeedMine.Mode.DAMAGE && !SpeedMine.INSTANCE.isFlag2()) {
            SpeedMine.INSTANCE.applyGetAbilities(blockData.getBp(), blockData.getDir());
         } else {
            if (MinecraftHolder.getMultiPlayerGameMode().getPlayerMode() == GameType.CREATIVE) {
               MinecraftHolder.getMultiPlayerGameMode().startDestroyBlock(blockData.getBp(), blockData.getDir());
            } else {
               MinecraftHolder.getMultiPlayerGameMode().continueDestroyBlock(blockData.getBp(), blockData.getDir());
            }

            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         }
      }
   }

   @Flow
   public final void onRender3D(Render3DEvent event) {
      BlockPos blockPos = null;
      if (!this.getCreative() && blockData != null) {
         blockPos = blockData.getBp();
      }

      if (blockPos != null) {
         Render3D.defaultImpl3(Render3D.INSTANCE, new AABB(blockPos), Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 1.0F), null, 4, null);
         Render3D.defaultImpl2(Render3D.INSTANCE, new AABB(blockPos), Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 0.5F), null, 4, null);
      }
   }

   private final Nuker.BlockData getBlockData() {
      int range2 = (int)((float)Math.floor(this.getRange()) + 1);
      List<BlockPos> arrayList = new ArrayList();
      BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
      int value = -range2;
      if (value <= range2) {
         while (true) {
            int value2 = -range2;
            if (value2 <= range2) {
               while (true) {
                  int value3 = -range2;
                  if (value3 <= range2) {
                     while (true) {
                        arrayList.add(containing.offset(value, value2, value3));
                        if (value3 == range2) {
                           break;
                        }

                        value3++;
                     }
                  }

                  if (value2 == range2) {
                     break;
                  }

                  value2++;
               }
            }

            if (value == range2) {
               break;
            }

            value++;
         }
      }

      Iterable<BlockPos> iterable = arrayList;

      for (BlockPos sortedWithItem : (Iterable<BlockPos>)CollectionsKt.sortedWith(iterable, new Nuker.Ordering())) {
         if ((!SpeedMine.INSTANCE.getEnabled() || !SpeedMine.INSTANCE.checkIsEmpty(sortedWithItem))
            && (!this.getFlatten() || !(sortedWithItem.getY() < MinecraftHolder.getLocalPlayer().getY()))
            && !(WorldUtil.INSTANCE.computeAmount2(Vec3iUtil.computeVec3(sortedWithItem)) > this.getRange() * this.getRange())
            && (!this.getAvoidLava() || !this.checkGetBlock(sortedWithItem))) {
            BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(sortedWithItem);
            if (this.checkContains(blockState.getBlock())) {
               if (this.getIgnoreWalls()) {
                  BlockHitResult defaultImpl = InteractionManager.defaultImpl(
                     InteractionManager.INSTANCE,
                     new ClipContext(
                        MinecraftHolder.getLocalPlayer().getEyePosition(),
                        Vec3iUtil.computeVec3(sortedWithItem),
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        Fluid.NONE,
                        (Entity)MinecraftHolder.getLocalPlayer()
                     ),
                     sortedWithItem,
                     null,
                     4,
                     null
                  );
                  Direction direction = defaultImpl.getDirection();
                  Vec3 location = defaultImpl.getLocation();
                  return new Nuker.BlockData(sortedWithItem, location, direction);
               }

               for (int j = 0; j < 5; j++) {
                  for (int k = 0; k < 5; k++) {
                     for (int index = 0; index < 5; index++) {
                        float value4 = 0.25F;
                        Vec3 vec3 = new Vec3(sortedWithItem.getX() + j * value4, sortedWithItem.getY() + k * value4, sortedWithItem.getZ() + index * value4);
                        BlockHitResult clip = MinecraftHolder.getClientLevel()
                           .clip(
                              new ClipContext(
                                 MinecraftHolder.getLocalPlayer().getEyePosition(),
                                 vec3,
                                 net.minecraft.world.level.ClipContext.Block.OUTLINE,
                                 Fluid.NONE,
                                 (Entity)MinecraftHolder.getLocalPlayer()
                              )
                           );
                        if (clip.getType() == Type.BLOCK && Intrinsics.areEqual(clip.getBlockPos(), sortedWithItem)) {
                           return new Nuker.BlockData(sortedWithItem, vec3, clip.getDirection());
                        }
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   private final boolean checkGetBlock(BlockPos blockPos) {
      for (Direction enumEntry : (Iterable<Direction>)Nuker.EntriesMappings.enumEntries) {
         if (Intrinsics.areEqual(MinecraftHolder.getClientLevel().getBlockState(blockPos.relative(enumEntry)).getBlock(), Blocks.LAVA)) {
            return true;
         }
      }

      return false;
   }

   private final void applyIsDown() {
      if ((block != null || this.getFilter() != Nuker.Filter.Select) && !MinecraftHolder.getMinecraft().options.keyAttack.isDown() && blockData == null) {
         blockData = this.getBlockData();
      }
   }

   private final boolean checkContains(Block block) {
      boolean contains = this.getSelectedBlocks().getBlocks().contains(block);

      return switch (Nuker.WhenMappings.values[this.getFilter().ordinal()]) {
         case 1 -> !Intrinsics.areEqual(block, Blocks.BEDROCK)
            && !Intrinsics.areEqual(block, Blocks.AIR)
            && !Intrinsics.areEqual(block, Blocks.CAVE_AIR)
            && !(block instanceof LiquidBlock);
         case 2 -> Intrinsics.areEqual(block, Nuker.block);
         case 3 -> contains;
         default -> !contains
            && !Intrinsics.areEqual(block, Blocks.BEDROCK)
            && !Intrinsics.areEqual(block, Blocks.AIR)
            && !Intrinsics.areEqual(block, Blocks.CAVE_AIR)
            && !(block instanceof LiquidBlock);
      };
   }

   public static final Nuker.Sort computeSort(Nuker nuker) {
      return nuker.getSort();
   }

   static {
   }

   public static final class BlockData {
      @NotNull
      private final BlockPos bp;
      @NotNull
      private final Vec3 vec3d;
      @NotNull
      private final Direction dir;

      public BlockData(BlockPos bp, Vec3 vec3d, Direction dir) {
         this.bp = bp;
         this.vec3d = vec3d;
         this.dir = dir;
      }

      @NotNull
      public final BlockPos getBp() {
         return this.bp;
      }

      @NotNull
      public final Vec3 getVec3d() {
         return this.vec3d;
      }

      @NotNull
      public final Direction getDir() {
         return this.dir;
      }

      @NotNull
      public final BlockPos getBp2() {
         return this.bp;
      }

      @NotNull
      public final Vec3 getVec3d2() {
         return this.vec3d;
      }

      @NotNull
      public final Direction getDir2() {
         return this.dir;
      }

      @NotNull
      @Override
      public String toString() {
         Direction dir2 = this.dir;
         Vec3 vec3d2 = this.vec3d;
         BlockPos blockPos = this.bp;
         return "BlockData(bp=" + blockPos + ", vec3d=" + vec3d2 + ", dir=" + dir2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.bp.hashCode();
         hashCode = hashCode * 31 + this.vec3d.hashCode();
         return hashCode * 31 + this.dir.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof Nuker.BlockData blockData)) {
            return false;
         } else if (!Intrinsics.areEqual(this.bp, blockData.bp)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.vec3d, blockData.vec3d) ? false : this.dir == blockData.dir;
         }
      }
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
   }

   public static enum Filter {
      Select,
      All,
      BlackList,
      WhiteList;

      private static final Nuker.Filter[] filters = $values();
      private static final EnumEntries<Nuker.Filter> enumEntries = EnumEntriesKt.enumEntries(filters);

      private Filter() {
      }

      @NotNull
      public static EnumEntries<Nuker.Filter> getEnumEntries() {
         return enumEntries;
      }

      private static final Nuker.Filter[] $values() {
         return new Nuker.Filter[]{Select, All, BlackList, WhiteList};
      }
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         BlockPos object3 = (BlockPos)object;
         Comparable amount = Nuker.computeSort(Nuker.INSTANCE) == Nuker.Sort.FOV
            ? AutoDripstope.INSTANCE.computeAmount(object3)
            : WorldUtil.INSTANCE.computeAmount2(Vec3iUtil.computeVec3(object3));
         object3 = (BlockPos)object2;
         Comparable comparable = amount;
         return ComparisonsKt.compareValues(
            comparable,
            Nuker.computeSort(Nuker.INSTANCE) == Nuker.Sort.FOV
               ? AutoDripstope.INSTANCE.computeAmount(object3)
               : WorldUtil.INSTANCE.computeAmount2(Vec3iUtil.computeVec3(object3))
         );
      }
   }

   public static final class Ordering2 implements Comparator {
      public Ordering2() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         BlockPos object3 = (BlockPos)object;
         Comparable amount = Nuker.computeSort(Nuker.INSTANCE) == Nuker.Sort.FOV
            ? AutoDripstope.INSTANCE.computeAmount(object3)
            : WorldUtil.INSTANCE.computeAmount2(Vec3iUtil.computeVec3(object3));
         object3 = (BlockPos)object2;
         Comparable comparable = amount;
         return ComparisonsKt.compareValues(
            comparable,
            Nuker.computeSort(Nuker.INSTANCE) == Nuker.Sort.FOV
               ? AutoDripstope.INSTANCE.computeAmount(object3)
               : WorldUtil.INSTANCE.computeAmount2(Vec3iUtil.computeVec3(object3))
         );
      }
   }

   public static enum Sort {
      FOV,
      DISTANCE;

      private static final Nuker.Sort[] sorts = $values();
      private static final EnumEntries<Nuker.Sort> enumEntries = EnumEntriesKt.enumEntries(sorts);

      private Sort() {
      }

      @NotNull
      public static EnumEntries<Nuker.Sort> getEnumEntries() {
         return enumEntries;
      }

      private static final Nuker.Sort[] $values() {
         return new Nuker.Sort[]{FOV, DISTANCE};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[Nuker.Filter.values().length];

         try {
            values2[Nuker.Filter.All.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Nuker.Filter.Select.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[Nuker.Filter.WhiteList.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}