// deobf by pusia
package su.catlean.client.services;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jdk7.AutoCloseableKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.BlockPosWithFacing;
import su.catlean.api.data.BreakData;
import su.catlean.api.data.Rotation;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.player.BoxShrinker;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.client.services.rotation.SilentRotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.InputPacketUtil;
import su.catlean.utils.world.EntityPositionUtil;
import su.catlean.utils.world.WorldUtil;

public final class InteractionManager implements Service {
   @NotNull
   public static final InteractionManager INSTANCE = new InteractionManager();
   @NotNull
   private static final Map map;
   @NotNull
   private static final Map<BlockPos, Long> map2 = new LinkedHashMap();
   @NotNull
   private static Vec3 vec3 = Vec3.ZERO;

   private InteractionManager() {
   }

   @NotNull
   public final Map<BlockPos, Long> getMap2() {
      return map2;
   }

   @Flow(
      priority = 10
   )
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      Map hashMap = new HashMap(map2);

      for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)hashMap.entrySet()) {
         BlockPos key = (BlockPos)entrySetItem.getKey();
         long value = ((Number)entrySetItem.getValue()).longValue();
         if ((float)(System.currentTimeMillis() - value) > ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) * 2.0F) {
            map2.remove(key);
         }
      }

      vec3 = MinecraftHolder.getLocalPlayer().getEyePosition();
   }

   @NotNull
   public final Vec3 fetchEyePosition(Entity entity) {
      return Intrinsics.areEqual(entity, MinecraftHolder.getLocalPlayer()) ? vec3 : entity.getEyePosition();
   }

   @Nullable
   public final RotationAction fetchLocation(
      BlockPos bp,
      int value,
      PlaceModule.Interact interact,
      PlaceModule.PlaceMode mode,
      TargetFilter ignore,
      float amount,
      float amount2,
      int value2,
      PlaceModule.Switch switchValue,
      List calcGhost,
      boolean flag
   ) {
      BlockHitResult item = this.fetchItem(bp, value, interact, ignore, amount, amount2, calcGhost);
      if (item == null) {
         return null;
      } else {
         BlockHitResult blockHitResult = item;
         Function0 function0 = () -> {
            int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
            if (value != -1) {
               switch (InteractionManager.WhenMappings.values[switchValue.ordinal()]) {
                  case 1:
                  case 2:
                     InventoryManager.INSTANCE.applyGetInventory2(value);
                     break;
                  case 3:
                     InventoryManager.INSTANCE.applyGetInventory(value);
                     break;
                  default:
                     throw new NoWhenBranchMatchedException();
               }
            }

            InteractionManager instance = INSTANCE;
            Block block = MinecraftHolder.getClientLevel().getBlockState(blockHitResult.getBlockPos()).getBlock();
            boolean flag2 = instance.testBlock(block) && !MinecraftHolder.getLocalPlayer().isShiftKeyDown();
            if (flag2) {
               InputPacketUtil.INSTANCE.applyWrite(true);
            }

            if (mode == PlaceModule.PlaceMode.Normal) {
               MinecraftHolder.getMultiPlayerGameMode().useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, blockHitResult);
            } else {
               INSTANCE.applyCurrentSequence(arg -> (Packet)(new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, blockHitResult, arg)));
            }

            Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
            PacketUtil.applySend(serverboundSwingPacket);
            map2.put(bp, MinecraftHolder.getTime());
            if (flag2) {
               InputPacketUtil.INSTANCE.applyWrite(false);
            }

            if (selectedSlot != -1) {
               switch (InteractionManager.WhenMappings.values[switchValue.ordinal()]) {
                  case 2:
                     InventoryManager.INSTANCE.applyGetInventory2(selectedSlot);
                     break;
                  case 3:
                     InventoryManager.INSTANCE.applyGetInventory(value);
               }
            }

            return Unit.INSTANCE;
         };
         return interact == PlaceModule.Interact.GRIM
            ? new SilentRotationAction(Rotation.vectorUtil.computeRotation(blockHitResult.getLocation()).computeRotation(flag), value2, function0)
            : new RotationAction(Rotation.vectorUtil.computeRotation(blockHitResult.getLocation()).computeRotation(flag), value2, function0);
      }
   }

   public static RotationAction defaultImpl(
      InteractionManager interactionManager,
      BlockPos blockPos,
      int value,
      PlaceModule.Interact interact,
      PlaceModule.PlaceMode placeMode,
      TargetFilter targetFilter,
      float amount,
      float amount2,
      int value2,
      PlaceModule.Switch switchValue,
      List items,
      boolean flag,
      int value3,
      Object object
   ) {
      if ((value3 & 512) != 0) {
         items = new ArrayList();
      }

      if ((value3 & 1024) != 0) {
         flag = false;
      }

      return interactionManager.fetchLocation(blockPos, value, interact, placeMode, targetFilter, amount, amount2, value2, switchValue, items, flag);
   }

   public final boolean checkContainsKey(BlockPos bp, PlaceModule.Interact interact, TargetFilter ignore, float amount, float amount2) {
      return map2.containsKey(bp) ? false : defaultImpl(this, bp, -1, interact, ignore, amount, amount2, null, 64, null) != null;
   }

   public final void applyCurrentSequence(PredictiveAction packetCreator) {
      var startPredicting = (AutoCloseable)MinecraftHolder.getClientLevel().getBlockStatePredictionHandler().startPredicting();
      Throwable throwable = null;

      try {
         BlockStatePredictionHandler blockStatePredictionHandler = (BlockStatePredictionHandler)startPredicting;
         MinecraftHolder.getLocalPlayer().connection.send(packetCreator.predict(blockStatePredictionHandler.currentSequence()));
         Unit instance = Unit.INSTANCE;
      } catch (Throwable exception) {
         throwable = exception;
         throw exception;
      } finally {
         AutoCloseableKt.closeFinally(startPredicting, throwable);
      }
   }

   @Nullable
   public final BlockHitResult fetchItem(
      BlockPos bp, int value, PlaceModule.Interact interact, TargetFilter ignore, float amount, float amount2, List calcGhost
   ) {
      if (BreakingBlocksService.INSTANCE.checkContainsKey(bp)) {
         return null;
      } else {
         ArrayList<Entity> arrayList = Lists.newArrayList(
            MinecraftHolder.getClientLevel()
               .getEntitiesOfClass(Entity.class, BoxShrinker.INSTANCE.getEnabled() ? new AABB(bp).inflate(-1.0E-12, 0.0, -1.0E-12) : new AABB(bp))
         );
         if (ignore == TargetFilter.ALL) {
            Iterable<Entity> iterable = arrayList;
            var arrayList2 = new ArrayList();

            for (Object object : iterable) {
               Entity entity = (Entity)object;
               if (!(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrb)) {
                  arrayList2.add(object);
               }
            }

            Iterator iterator = arrayList2.iterator();
            if (iterator.hasNext()) {
               Entity entity2 = (Entity)iterator.next();
               return null;
            }
         }

         if (ignore == TargetFilter.ONLY_CRYSTALS) {
            Iterable<Entity> iterable2 = arrayList;
            var arrayList3 = new ArrayList();

            for (Object object2 : iterable2) {
               Entity entity3 = (Entity)object2;
               if (!(entity3 instanceof EndCrystal) && !(entity3 instanceof ItemEntity) && !(entity3 instanceof ExperienceOrb)) {
                  arrayList3.add(object2);
               }
            }

            Iterator iterator2 = arrayList3.iterator();
            if (iterator2.hasNext()) {
               Entity entity4 = (Entity)iterator2.next();
               return null;
            }
         }

         BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(bp);
         if (!blockState.canBeReplaced()) {
            return null;
         } else if (interact == PlaceModule.Interact.AirPlace) {
            return defaultImpl(
               this,
               new ClipContext(
                  this.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()),
                  Vec3iUtil.computeVec3(bp),
                  net.minecraft.world.level.ClipContext.Block.COLLIDER,
                  Fluid.NONE,
                  (Entity)MinecraftHolder.getLocalPlayer()
               ),
               bp,
               null,
               4,
               null
            );
         } else {
            List items = this.computeItems(bp, calcGhost);
            Item item = (0 <= value ? value < 9 : false) ? MinecraftHolder.getLocalPlayer().getInventory().getItem(value).getItem() : Items.OBSIDIAN;
            if (Intrinsics.areEqual(item, Items.ITEM_FRAME)) {
               items.removeIf(arg -> ((BlockPosWithFacing)arg).getFacing() == Direction.UP || ((BlockPosWithFacing)arg).getFacing() == Direction.DOWN);
            }

            if (Intrinsics.areEqual(item, Items.VINE) && Intrinsics.areEqual(blockState.getBlock(), Blocks.VINE)) {
               return null;
            } else {
               if (Intrinsics.areEqual(item, Items.LADDER)) {
                  items.removeIf(
                     arg2 -> {
                        var value2 = arrayList;
                        boolean flag5;
                        if (value2 instanceof Collection && value2.isEmpty()) {
                           flag5 = false;
                        } else {
                           for (Object object3 : value2) {
                              Entity entity5 = (Entity)object3;
                              if (entity5.distanceToSqr(
                                    Vec3iUtil.computeVec3(((BlockPosWithFacing)arg2).getPosition()).relative(((BlockPosWithFacing)arg2).getFacing(), 0.5)
                                 )
                                 < 0.5) {
                                 return true;
                              }
                           }

                           flag5 = false;
                        }

                        return flag5;
                     }
                  );
               }

               for (BlockPosWithFacing blockPosWithFacing : (Iterable<BlockPosWithFacing>)items) {
                  if (interact == PlaceModule.Interact.Strict || interact == PlaceModule.Interact.GRIM) {
                     List<Direction> items2 = this.computeItems(blockPosWithFacing.getPosition());
                     if (items2.isEmpty()) {
                        return null;
                     }

                     if (!items2.contains(blockPosWithFacing.getFacing())) {
                        continue;
                     }
                  }

                  Vec3 facing = this.fetchX(blockPosWithFacing.getFacing(), blockPosWithFacing.getPosition(), amount2, amount, interact == PlaceModule.Interact.GRIM);
                  if (facing != null) {
                     return new BlockHitResult(facing, blockPosWithFacing.getFacing(), blockPosWithFacing.getPosition(), false);
                  }
               }

               return null;
            }
         }
      }
   }

   public static BlockHitResult defaultImpl(
      InteractionManager interactionManager,
      BlockPos blockPos,
      int value,
      PlaceModule.Interact interact,
      TargetFilter targetFilter,
      float amount,
      float amount2,
      List items,
      int value2,
      Object object
   ) {
      if ((value2 & 64) != 0) {
         items = new ArrayList();
      }

      return interactionManager.fetchItem(blockPos, value, interact, targetFilter, amount, amount2, items);
   }

   @NotNull
   public final List computeItems(BlockPos bp, List calcGhost) {
      Map map3 = map;
      var arrayList = new ArrayList();

      for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)map3.entrySet()) {
         Direction key = (Direction)entrySetItem.getKey();
         BlockPos value = (BlockPos)entrySetItem.getValue();
         BlockPos offset = bp.offset((Vec3i)value);
         BlockPosWithFacing blockPosWithFacing = MinecraftHolder.getClientLevel().getBlockState(offset).canBeReplaced()
               && !map2.containsKey(offset)
               && !calcGhost.contains(offset)
            ? null
            : new BlockPosWithFacing(offset, key);
         if (blockPosWithFacing != null) {
            BlockPosWithFacing blockPosWithFacing2 = blockPosWithFacing;
            arrayList.add(blockPosWithFacing2);
         }
      }

      return CollectionsKt.toMutableList(arrayList);
   }

   @Nullable
   public final BlockPosWithFacing computeBlockPosWithFacing(BlockPos blockPos) {
      Map map3 = map;

      for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)map3.entrySet()) {
         Direction key = (Direction)entrySetItem.getKey();
         BlockPos value = (BlockPos)entrySetItem.getValue();
         BlockPos offset = blockPos.offset((Vec3i)value);
         if (!MinecraftHolder.getClientLevel().getBlockState(offset).canBeReplaced()) {
            return new BlockPosWithFacing(offset, key);
         }
      }

      return null;
   }

   public final float computeAmount(Vec3 vec) {
      return (float)vec.distanceToSqr(MinecraftHolder.getLocalPlayer().getEyePosition());
   }

   @NotNull
   public final InteractionManager.DirectionDeltas computeDirectionDeltas(BlockPos bp) {
      Vec3 vec32 = Vec3iUtil.computeVec3(bp);
      return new InteractionManager.DirectionDeltas(
         this.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()).x - vec32.add(0.5, 0.0, 0.0).x,
         this.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()).x - vec32.add(-0.5, 0.0, 0.0).x,
         this.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()).z - vec32.add(0.0, 0.0, 0.5).z,
         this.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()).z - vec32.add(0.0, 0.0, -0.5).z,
         this.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()).y - vec32.add(0.0, 0.5, 0.0).y,
         this.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()).y - vec32.add(0.0, -0.5, 0.0).y
      );
   }

   @NotNull
   public final List<Direction> computeItems(BlockPos bp) {
      List<Direction> arrayList = new ArrayList();
      InteractionManager.DirectionDeltas directionDeltas = this.computeDirectionDeltas(bp);
      if (directionDeltas.getAmount() > 0.0 && MinecraftHolder.getClientLevel().getBlockState(bp.east()).canBeReplaced()) {
         arrayList.add(Direction.EAST);
      }

      if (directionDeltas.getAmount2() < 0.0 && MinecraftHolder.getClientLevel().getBlockState(bp.west()).canBeReplaced()) {
         arrayList.add(Direction.WEST);
      }

      if (directionDeltas.getAmount3() > 0.0 && MinecraftHolder.getClientLevel().getBlockState(bp.south()).canBeReplaced()) {
         arrayList.add(Direction.SOUTH);
      }

      if (directionDeltas.getAmount4() < 0.0 && MinecraftHolder.getClientLevel().getBlockState(bp.north()).canBeReplaced()) {
         arrayList.add(Direction.NORTH);
      }

      if (directionDeltas.getAmount5() > 0.0 && MinecraftHolder.getClientLevel().getBlockState(bp.above()).canBeReplaced()) {
         arrayList.add(Direction.UP);
      }

      if (directionDeltas.getAmount6() < 0.0 && MinecraftHolder.getClientLevel().getBlockState(bp.below()).canBeReplaced()) {
         arrayList.add(Direction.DOWN);
      }

      return arrayList;
   }

   @Nullable
   public final BreakData computeBreakData(BlockPos bp, PlaceModule.Interact interact) {
      if (interact == PlaceModule.Interact.Vanilla) {
         return new BreakData(Direction.UP, Vec3iUtil.computeVec3(bp).add(0.0, 0.5, 0.0));
      } else if (interact != PlaceModule.Interact.Strict && interact != PlaceModule.Interact.GRIM) {
         return null;
      } else {
         float value = 999.0F;
         Direction direction = Direction.UP;
         Vec3 vec32 = null;

         for (Direction enumEntry : (Iterable<Direction>)InteractionManager.EntriesMappings.enumEntries) {
            double value2 = enumEntry.getUnitVec3i().getX() * 0.5;
            double value3 = enumEntry.getUnitVec3i().getY() * 0.5;
            double value4 = enumEntry.getUnitVec3i().getZ() * 0.5;
            Vec3 defaultImpl = Vec3iUtil.computeVec3(bp)
               .add(
                  Math.abs(value2) == 0.5 ? value2 : MathUtil.defaultImpl((ClosedRange)RangesKt.rangeTo(-0.1, 0.1), false, 2, null),
                  Math.abs(value3) == 0.5 ? value3 : MathUtil.defaultImpl((ClosedRange)RangesKt.rangeTo(-0.1, 0.1), false, 2, null),
                  Math.abs(value4) == 0.5 ? value4 : MathUtil.defaultImpl((ClosedRange)RangesKt.rangeTo(-0.1, 0.1), false, 2, null)
               );
            float amount = this.computeAmount(defaultImpl);
            if (value > amount) {
               direction = enumEntry;
               vec32 = defaultImpl;
               value = amount;
            }
         }

         return vec32 == null ? null : new BreakData(direction, vec32);
      }
   }

   @Nullable
   public final Vec3 fetchX(Direction dir, BlockPos bp, float amount, float amount2, boolean flag) {
      AABB aabb = this.computeAABB(dir);
      List<Vec3> arrayList = new ArrayList();
      if (aabb.maxX - aabb.minX == 0.0) {
         for (double minY = aabb.minY; minY < aabb.maxY; minY += 0.1) {
            for (double minZ = aabb.minZ; minZ < aabb.maxZ; minZ += 0.1) {
               Vec3 vec32 = new Vec3(bp.getX() + aabb.minX, bp.getY() + minY, bp.getZ() + minZ);
               if (!this.checkClip(vec32, bp, amount, amount2)) {
                  arrayList.add(vec32);
               }
            }
         }
      }

      if (aabb.maxY - aabb.minY == 0.0) {
         for (double minX = aabb.minX; minX < aabb.maxX; minX += 0.1) {
            for (double minZ2 = aabb.minZ; minZ2 < aabb.maxZ; minZ2 += 0.1) {
               Vec3 vec33 = new Vec3(bp.getX() + minX, bp.getY() + aabb.minY, bp.getZ() + minZ2);
               if (!this.checkClip(vec33, bp, amount, amount2)) {
                  arrayList.add(vec33);
               }
            }
         }
      }

      if (aabb.maxZ - aabb.minZ == 0.0) {
         for (double minX2 = aabb.minX; minX2 < aabb.maxX; minX2 += 0.1) {
            for (double minY2 = aabb.minY; minY2 < aabb.maxY; minY2 += 0.1) {
               Vec3 vec34 = new Vec3(bp.getX() + minX2, bp.getY() + minY2, bp.getZ() + aabb.minZ);
               if (!this.checkClip(vec34, bp, amount, amount2)) {
                  arrayList.add(vec34);
               }
            }
         }
      }

      if (arrayList.isEmpty()) {
         return null;
      } else {
         Vec3 random;
         if (flag) {
            random = (Vec3)CollectionsKt.random(arrayList, (Random)Random.Default);
         } else {
            Iterable<Vec3> iterable = arrayList;
            Iterator<Vec3> iterator = iterable.iterator();
            if (!iterator.hasNext()) {
               throw new NoSuchElementException();
            }

            Vec3 object = iterator.next();
            Vec3 object2;
            if (!iterator.hasNext()) {
               object2 = object;
            } else {
               Vec3 vec35 = (Vec3)object;
               double distanceToSqr = MinecraftHolder.getLocalPlayer().getEyePosition().distanceToSqr(vec35);

               do {
                  Vec3 object3 = iterator.next();
                  Vec3 vec36 = (Vec3)object3;
                  double distanceToSqr2 = MinecraftHolder.getLocalPlayer().getEyePosition().distanceToSqr(vec36);
                  if (Double.compare(distanceToSqr, distanceToSqr2) > 0) {
                     object = object3;
                     distanceToSqr = distanceToSqr2;
                  }
               } while (iterator.hasNext());

               object2 = object;
            }

            random = (Vec3)object2;
         }

         return random;
      }
   }

   @NotNull
   public final AABB computeAABB(Direction dir) {
      return switch (InteractionManager.WhenMappings.values2[dir.ordinal()]) {
         case 1 -> new AABB(0.15, 0.0, 0.15, 0.85, 0.0, 0.85);
         case 2 -> new AABB(0.15, 1.0, 0.15, 0.85, 1.0, 0.85);
         case 3 -> new AABB(0.15, 0.15, 0.0, 0.85, 0.85, 0.0);
         case 4 -> new AABB(0.15, 0.15, 1.0, 0.85, 0.85, 1.0);
         case 5 -> new AABB(0.0, 0.15, 0.15, 0.0, 0.85, 0.85);
         case 6 -> new AABB(1.0, 0.15, 0.15, 1.0, 0.85, 0.85);
         default -> throw new NoWhenBranchMatchedException();
      };
   }

   public final boolean checkClip(Vec3 point, BlockPos bp, float amount, float amount2) {
      ClipContext clipContext = new ClipContext(
         this.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()),
         point,
         net.minecraft.world.level.ClipContext.Block.COLLIDER,
         Fluid.NONE,
         (Entity)MinecraftHolder.getLocalPlayer()
      );
      BlockHitResult clip = MinecraftHolder.getClientLevel().clip(clipContext);
      float amount3 = this.computeAmount(point);
      return clip.getType() == Type.BLOCK && !Intrinsics.areEqual(clip.getBlockPos(), bp) && amount3 > amount * amount ? true : amount3 > amount2 * amount2;
   }

   public final boolean testBlock(Block b) {
      return b instanceof BaseEntityBlock
         || Intrinsics.areEqual(b, Blocks.CRAFTING_TABLE)
         || b instanceof TrapDoorBlock
         || b instanceof LeverBlock
         || b instanceof ButtonBlock
         || b instanceof DoorBlock;
   }

   @NotNull
   public final BlockHitResult fetchFrom(ClipContext context, BlockPos block, BlockState state) {
      return (BlockHitResult)BlockGetter.traverseBlocks(context.getFrom(), context.getTo(), context, (arg, arg2) -> {
         BlockState defaultBlockState = !arg2.equals(block) ? Blocks.AIR.defaultBlockState() : state;
         Vec3 from = arg.getFrom();
         Vec3 vec32 = arg.getTo();
         VoxelShape blockShape = arg.getBlockShape(defaultBlockState, (BlockGetter)MinecraftHolder.getClientLevel(), arg2);
         BlockHitResult clipWithInteractionOverride = MinecraftHolder.getClientLevel().clipWithInteractionOverride(from, vec32, arg2, blockShape, defaultBlockState);
         VoxelShape empty = Shapes.empty();
         BlockHitResult clip = empty.clip(from, vec32, arg2);
         double maX_VALUE = clipWithInteractionOverride == null ? Double.MAX_VALUE : arg.getFrom().distanceToSqr(clipWithInteractionOverride.getLocation());
         double maX_VALUE2 = clip == null ? Double.MAX_VALUE : arg.getFrom().distanceToSqr(clip.getLocation());
         return maX_VALUE <= maX_VALUE2 ? clipWithInteractionOverride : clip;
      }, arg3 -> {
         Vec3 subtract = arg3.getFrom().subtract(arg3.getTo());
         return BlockHitResult.miss(arg3.getTo(), Direction.getApproximateNearest(subtract.x, subtract.y, subtract.z), BlockPos.containing((Position)arg3.getTo()));
      });
   }

   public static BlockHitResult defaultImpl(
      InteractionManager interactionManager, ClipContext clipContext, BlockPos blockPos, BlockState blockState, int value, Object object
   ) {
      if ((value & 4) != 0) {
         blockState = Blocks.OBSIDIAN.defaultBlockState();
      }

      return interactionManager.fetchFrom(clipContext, blockPos, blockState);
   }

   public final boolean checkDistanceToSqr(Entity entity, Rotation rotation, float amount, boolean flag) {
      double amount2 = amount;
      double square = Mth.square(amount2);
      Vec3 vec32 = EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer())
         .add(0.0, MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose()), 0.0);
      HitResult hitResult = null;
      double distanceToSqr = 0.0;
      if (flag) {
         hitResult = WorldUtil.INSTANCE.computeHitResult(amount, rotation.getAmount(), rotation.getAmount2());
         distanceToSqr = hitResult.getLocation().distanceToSqr(vec32);
         if (hitResult.getType() != Type.MISS) {
            square = distanceToSqr;
            amount2 = Math.sqrt(distanceToSqr);
         }
      } else {
         hitResult = null;
      }

      Vec3 vec33 = WorldUtil.INSTANCE.computeVec3(rotation.getAmount2(), rotation.getAmount());
      Vec3 vec34 = vec32.add(vec33.x * amount2, vec33.y * amount2, vec33.z * amount2);
      AABB inflate = MinecraftHolder.getLocalPlayer().getBoundingBox().expandTowards(vec33.scale(amount2)).inflate(1.0, 1.0, 1.0);
      EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
         (Entity)MinecraftHolder.getLocalPlayer(), vec32, vec34, inflate, arg -> arg.isPickable() && Intrinsics.areEqual(arg, entity), square
      );
      if (entityHitResult == null) {
         return false;
      } else {
         HitResult hitResult2 = (HitResult)(hitResult != null && !(entityHitResult.getLocation().distanceToSqr(vec32) < distanceToSqr) ? hitResult : (HitResult)entityHitResult);
         HitResult location = this.fetchLocation(hitResult2, vec32, amount);
         return location instanceof EntityHitResult && Intrinsics.areEqual(((EntityHitResult)location).getEntity(), entity);
      }
   }

   private final HitResult fetchLocation(HitResult hitResult, Vec3 vec3, float amount) {
      if (!hitResult.getLocation().closerThan((Position)vec3, amount)) {
         Vec3 location = hitResult.getLocation();
         return (HitResult)BlockHitResult.miss(
            location, Direction.getApproximateNearest(location.x - vec3.x, location.y - vec3.y, location.z - vec3.z), BlockPos.containing((Position)location)
         );
      } else {
         return hitResult;
      }
   }

   @NotNull
   public final List getItems() {
      BlockPos containing = BlockPos.containing((Position)MinecraftHolder.getLocalPlayer().getEyePosition());
      int blockInteractionRange = (int)Math.ceil(MinecraftHolder.getLocalPlayer().blockInteractionRange());
      Iterable intRange = (Iterable)(new IntRange(containing.getX() - blockInteractionRange, containing.getX() + blockInteractionRange));
      var arrayList = new ArrayList();
      Iterator iterator = intRange.iterator();

      while (iterator.hasNext()) {
         int nextInt = ((IntIterator)iterator).nextInt();
         int value = nextInt;
         Iterable intRange2 = (Iterable)(new IntRange(containing.getY() - blockInteractionRange, containing.getY() + blockInteractionRange));
         var arrayList2 = new ArrayList();
         Iterator iterator2 = intRange2.iterator();

         while (iterator2.hasNext()) {
            int nextInt2 = ((IntIterator)iterator2).nextInt();
            int value2 = nextInt2;
            Iterable intRange3 = (Iterable)(new IntRange(containing.getZ() - blockInteractionRange, containing.getZ() + blockInteractionRange));
            var arrayList3 = new ArrayList();
            Iterator iterator3 = intRange3.iterator();

            while (iterator3.hasNext()) {
               int nextInt3 = ((IntIterator)iterator3).nextInt();
               BlockPos blockPos = new BlockPos(value, value2, nextInt3);
               arrayList3.add(blockPos);
            }

            var value3 = arrayList3;
            CollectionsKt.addAll(arrayList2, value3);
         }

         var value4 = arrayList2;
         CollectionsKt.addAll(arrayList, value4);
      }

      return arrayList;
   }

   @NotNull
   public final Rotation computeRotation(Direction direction) {
      float value = switch (InteractionManager.WhenMappings.values2[direction.ordinal()]) {
         case 3 -> 180.0F;
         case 4 -> 0.0F;
         case 5 -> 90.0F;
         case 6 -> -90.0F;
         default -> 0.0F;
      };

      float value2 = switch (InteractionManager.WhenMappings.values2[direction.ordinal()]) {
         case 1 -> 88.0F;
         case 2 -> -88.0F;
         default -> -10.0F;
      };
      int amount4 = (int)(WorldUtil.INSTANCE.getAmount4() / 360.0F);
      return new Rotation(value + amount4 * 360, value2, false, null, 12, null);
   }

   static {
      Pair[] pairs = new Pair[]{
         TuplesKt.to(Direction.UP, new BlockPos(0, -1, 0)),
         TuplesKt.to(Direction.DOWN, new BlockPos(0, 1, 0)),
         TuplesKt.to(Direction.EAST, new BlockPos(-1, 0, 0)),
         TuplesKt.to(Direction.WEST, new BlockPos(1, 0, 0)),
         TuplesKt.to(Direction.NORTH, new BlockPos(0, 0, 1)),
         TuplesKt.to(Direction.SOUTH, new BlockPos(0, 0, -1))
      };
      map = MapsKt.mapOf(pairs);
   }

   public static final class DirectionDeltas {
      private final double amount;
      private final double amount2;
      private final double amount3;
      private final double amount4;
      private final double amount5;
      private final double amount6;

      public DirectionDeltas(double amount, double amount2, double amount3, double amount4, double amount5, double amount6) {
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.amount4 = amount4;
         this.amount5 = amount5;
         this.amount6 = amount6;
      }

      public final double getAmount() {
         return this.amount;
      }

      public final double getAmount2() {
         return this.amount2;
      }

      public final double getAmount3() {
         return this.amount3;
      }

      public final double getAmount4() {
         return this.amount4;
      }

      public final double getAmount5() {
         return this.amount5;
      }

      public final double getAmount6() {
         return this.amount6;
      }

      public final double getAmount7() {
         return this.amount;
      }

      public final double getAmount8() {
         return this.amount2;
      }

      public final double getAmount9() {
         return this.amount3;
      }

      public final double getAmount10() {
         return this.amount4;
      }

      public final double getAmount11() {
         return this.amount5;
      }

      public final double getAmount12() {
         return this.amount6;
      }

      @NotNull
      public final InteractionManager.DirectionDeltas computeDirectionDeltas(
         double amount, double amount2, double amount3, double amount4, double amount5, double amount6
      ) {
         return new InteractionManager.DirectionDeltas(amount, amount2, amount3, amount4, amount5, amount6);
      }

      @NotNull
      @Override
      public String toString() {
         double amount62 = this.amount6;
         double amount52 = this.amount5;
         double amount42 = this.amount4;
         double amount32 = this.amount3;
         double amount22 = this.amount2;
         double amount7 = this.amount;
         return "DirectionDeltas(westDelta="
            + amount7
            + ", eastDelta="
            + amount22
            + ", northDelta="
            + amount32
            + ", southDelta="
            + amount42
            + ", upDelta="
            + amount52
            + ", downDelta="
            + amount62
            + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Double.hashCode(this.amount);
         hashCode = hashCode * 31 + Double.hashCode(this.amount2);
         hashCode = hashCode * 31 + Double.hashCode(this.amount3);
         hashCode = hashCode * 31 + Double.hashCode(this.amount4);
         hashCode = hashCode * 31 + Double.hashCode(this.amount5);
         return hashCode * 31 + Double.hashCode(this.amount6);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof InteractionManager.DirectionDeltas directionDeltas)) {
            return false;
         } else if (Double.compare(this.amount, directionDeltas.amount) != 0) {
            return false;
         } else if (Double.compare(this.amount2, directionDeltas.amount2) != 0) {
            return false;
         } else if (Double.compare(this.amount3, directionDeltas.amount3) != 0) {
            return false;
         } else if (Double.compare(this.amount4, directionDeltas.amount4) != 0) {
            return false;
         } else {
            return Double.compare(this.amount5, directionDeltas.amount5) != 0 ? false : Double.compare(this.amount6, directionDeltas.amount6) == 0;
         }
      }
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
   }

   public static final class WhenMappings {
      public static final int[] values;
      public static final int[] values2;

      static {
         int[] values3 = new int[PlaceModule.Switch.values().length];

         try {
            values3[PlaceModule.Switch.Normal.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values3[PlaceModule.Switch.Silent.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values3[PlaceModule.Switch.Alternative.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values3;
         values3 = new int[Direction.values().length];

         try {
            values3[Direction.DOWN.ordinal()] = 1;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values3[Direction.UP.ordinal()] = 2;
         } catch (NoSuchFieldError exception5) {
         }

         try {
            values3[Direction.NORTH.ordinal()] = 3;
         } catch (NoSuchFieldError exception6) {
         }

         try {
            values3[Direction.SOUTH.ordinal()] = 4;
         } catch (NoSuchFieldError exception7) {
         }

         try {
            values3[Direction.WEST.ordinal()] = 5;
         } catch (NoSuchFieldError exception8) {
         }

         try {
            values3[Direction.EAST.ordinal()] = 6;
         } catch (NoSuchFieldError exception9) {
         }

         values2 = values3;
      }
   }
}