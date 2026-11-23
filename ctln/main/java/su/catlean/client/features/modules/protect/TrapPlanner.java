// deobf by pusia
package su.catlean.client.features.modules.protect;

import su.catlean.utils.KotlinCompat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.ConfigState;
import su.catlean.api.data.DamageComparatorResult;
import su.catlean.api.data.DamageData;
import su.catlean.api.data.SearchInvResult;
import su.catlean.client.features.modules.attack.AutoBasePlace;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.features.modules.attack.CrystalInteractPoint;
import su.catlean.client.features.modules.basic.AntiKnockBack;
import su.catlean.client.features.modules.basic.MoveFix;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.CrystalService;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.PlayerPredictionService;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.world.BlockInteractionPoint;
import su.catlean.utils.world.WorldUtil;

public final class TrapPlanner {
   private static final double amount = 0.05;
   private static final double amount2 = 0.95;
   private static final double amount3 = 0.5;
   @NotNull
   private static final Map map;
   @NotNull
   private static final List items;

   @NotNull
   public static final Map getMap() {
      return map;
   }

   @NotNull
   public static final List getItems() {
      return items;
   }

   @Nullable
   public static final DamageData computeDamageData(AutoCrystal self, ConfigState configState) {
      long time = MinecraftHolder.getTime();
      Pair<DamageData, Long> damageData = TuplesKt.to(
         SurroundPlanner.computeDamageData(configState, defaultImpl(self, configState, false, false, 6, null)), MinecraftHolder.getTime() - time
      );
      DamageData component1 = (DamageData)damageData.component1();
      time = ((Number)damageData.component2()).longValue();
      self.setTime2(time);
      return component1;
   }

   @Nullable
   public static final BlockHitResult computeBlockHitResult(AutoCrystal self, boolean flag, boolean flag2) {
      float amount4 = 0.0F;
      float amount5 = 0.0F;
      ConfigState configState = self.getConfigState();
      DamageData damageData = SurroundPlanner.computeDamageData(configState, fetchX(self, configState, false, flag2));
      if (damageData != null) {
         DamageData damageData2 = damageData;
         amount5 = damageData2.getDamageData().getAmount();
      }

      if (flag && amount5 != 0.0F) {
         return null;
      } else {
         damageData = SurroundPlanner.computeDamageData(configState, fetchX(self, configState, true, flag2));
         BlockHitResult object;
         if (damageData != null) {
            DamageData damageData3 = damageData;
            amount4 = damageData3.getDamageData().getAmount();
            object = (BlockHitResult)damageData3.getObject();
         } else {
            object = null;
         }

         BlockHitResult blockHitResult = object;
         DamageData atomicReferenceItem = (DamageData)self.getAtomicReference().get();
         if (atomicReferenceItem != null) {
            DamageComparatorResult damageData4 = atomicReferenceItem.getDamageData();
            if (damageData4 != null) {
               return !(amount4 <= damageData4.getAmount()) && !(amount5 >= amount4) && !(Math.abs(amount4 - amount5) <= 1.0F) ? blockHitResult : null;
            }
         }

         return !(amount4 <= 0.0F) && !(amount5 >= amount4) && !(Math.abs(amount4 - amount5) <= 1.0F) ? blockHitResult : null;
      }
   }

   private static final List fetchX(AutoCrystal autoCrystal, ConfigState configState, boolean flag, boolean flag2) {
      BlockPos containing = BlockPos.containing((Position)MinecraftHolder.getLocalPlayer().getEyePosition());
      Vec3 boundingBox = PlayerPredictionService.INSTANCE.fetchBoundingBox((Player)MinecraftHolder.getLocalPlayer(), 1);
      int amount4 = (int)((float)Math.ceil(configState.getAmount()));
      IntRange intRange = new IntRange(containing.getX() - amount4, containing.getX() + amount4);
      IntRange intRange2 = new IntRange(containing.getY() - amount4, containing.getY() + amount4);
      IntRange intRange3 = new IntRange(containing.getZ() - amount4, containing.getZ() + amount4);
      Iterable iterable = (Iterable)intRange;
      var arrayList = new ArrayList();
      Iterator iterator = iterable.iterator();

      while (iterator.hasNext()) {
         int nextInt = ((IntIterator)iterator).nextInt();
         int value = nextInt;
         Iterable iterable2 = (Iterable)intRange2;
         var arrayList2 = new ArrayList();
         Iterator iterator2 = iterable2.iterator();

         while (iterator2.hasNext()) {
            int nextInt2 = ((IntIterator)iterator2).nextInt();
            int value2 = nextInt2;
            Iterable iterable3 = (Iterable)intRange3;
            var arrayList3 = new ArrayList();
            Iterator iterator3 = iterable3.iterator();

            while (iterator3.hasNext()) {
               int nextInt3 = ((IntIterator)iterator3).nextInt();
               BlockPos blockPos = new BlockPos(value, value2, nextInt3);
               DamageData block = fetchBlock(autoCrystal, blockPos, boundingBox, configState, flag, flag2);
               if (block != null) {
                  DamageData damageData = block;
                  arrayList3.add(damageData);
               }
            }

            var value3 = arrayList3;
            CollectionsKt.addAll(arrayList2, value3);
         }

         var value4 = arrayList2;
         CollectionsKt.addAll(arrayList, value4);
      }

      return arrayList;
   }

   public static List defaultImpl(AutoCrystal autoCrystal, ConfigState configState, boolean flag, boolean flag2, int value, Object object) {
      if ((value & 2) != 0) {
         flag = false;
      }

      if ((value & 4) != 0) {
         flag2 = false;
      }

      return fetchX(autoCrystal, configState, flag, flag2);
   }

   @Nullable
   public static final DamageData fetchBlock(AutoCrystal self, BlockPos bp, Vec3 predictedPlayerPos, ConfigState configState, boolean flag, boolean flag2) {
      if (CrystalService.INSTANCE.testBlockPos(bp)) {
         return null;
      } else if (configState.isFlag7() && checkGetItem(bp, configState.getTarget())) {
         return null;
      } else if (self.computeVec3((Entity)configState.getTarget()).distanceTo(Vec3iUtil.computeVec3(bp).add(0.0, 0.5, 0.0)) > 12.0) {
         return null;
      } else {
         BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(bp);
         Block block = blockState.getBlock();
         if (!Intrinsics.areEqual(block, Blocks.OBSIDIAN) && !Intrinsics.areEqual(block, Blocks.BEDROCK) && !flag) {
            return null;
         } else if (flag && !blockState.canBeReplaced()) {
            return null;
         } else if (flag && !AutoBasePlace.INSTANCE.testBlockPos(bp)) {
            return null;
         } else {
            BlockState blockState2 = MinecraftHolder.getClientLevel().getBlockState(bp.above());
            boolean isFlag = false;
            if (configState.isFlag8()
               && (
                  Intrinsics.areEqual(blockState2.getBlock(), Blocks.LAVA)
                     || Intrinsics.areEqual(blockState2.getBlock(), Blocks.TRIPWIRE)
                     || Intrinsics.areEqual(blockState2.getBlock(), Blocks.REDSTONE_WIRE)
                     || Intrinsics.areEqual(blockState2.getBlock(), Blocks.TORCH)
               )) {
               InventoryManager instance = InventoryManager.INSTANCE;
               Item[] items2 = new Item[]{Items.STRING};
               SearchInvResult searchInvResult = instance.computeSearchInvResult(items2);
               instance = InventoryManager.INSTANCE;
               Item[] items3 = new Item[]{Items.REDSTONE};
               SearchInvResult searchInvResult2 = instance.computeSearchInvResult(items3);
               instance = InventoryManager.INSTANCE;
               Item[] items4 = new Item[]{Items.TORCH};
               SearchInvResult searchInvResult3 = instance.computeSearchInvResult(items4);
               SearchInvResult searchInvResult4 = searchInvResult.isFlag() ? searchInvResult : (searchInvResult2.isFlag() ? searchInvResult2 : searchInvResult3);
               isFlag = searchInvResult4.isFlag();
            }

            boolean flag3 = blockState2.isAir()
               || Intrinsics.areEqual(blockState2.getBlock(), Blocks.FIRE) && configState.isFlag4()
               || flag2 && Intrinsics.areEqual(blockState2.getBlock(), Blocks.WATER)
               || isFlag;
            if (flag3 && (!configState.isFlag() || MinecraftHolder.getClientLevel().isEmptyBlock(bp.above(2)))) {
               if (checkAbove(self, bp, true, configState)) {
                  return null;
               } else {
                  DamageComparatorResult absorptionAmount = SurroundPlanner.fetchAbsorptionAmount(self, bp, configState, false, true, flag);
                  if (absorptionAmount == null) {
                     return null;
                  } else {
                     DamageComparatorResult damageComparatorResult = absorptionAmount;
                     BlockHitResult blockHitResult = computeBlockHitResult(bp, configState, flag);
                     if (blockHitResult == null) {
                        return null;
                     } else {
                        BlockHitResult blockHitResult2 = blockHitResult;
                        return !checkDistanceTo(blockHitResult2.getLocation(), bp, predictedPlayerPos, configState) ? null : new DamageData(blockHitResult2, damageComparatorResult);
                     }
                  }
               }
            } else {
               return null;
            }
         }
      }
   }

   @Nullable
   public static final BlockHitResult computeBlockHitResult(BlockPos bp, ConfigState configState, boolean flag) {
      return configState.isFlag3() ? fetchKey(bp, configState, flag) : fetchY(bp, configState);
   }

   private static final BlockHitResult fetchY(BlockPos blockPos, ConfigState configState) {
      float amount4 = configState.getAmount();
      Direction direction = null;
      int value = MoveFix.INSTANCE.getEnabled() ? blockPos.above().getY() : blockPos.getY();
      if (MinecraftHolder.getLocalPlayer().getEyePosition().y > value) {
         Iterable items2 = items;
         Iterator iterator = items2.iterator();
         if (!iterator.hasNext()) {
            throw new NoSuchElementException();
         }

         Object object = iterator.next();
         Object object2;
         if (!iterator.hasNext()) {
            object2 = object;
         } else {
            Vec3 vec3 = (Vec3)object;
            float amount5 = WorldUtil.INSTANCE.computeAmount(new Vec3(blockPos.getX() + vec3.x, blockPos.getY() + vec3.y + 1.0, blockPos.getZ() + vec3.z));

            do {
               Object object3 = iterator.next();
               Vec3 vec32 = (Vec3)object3;
               float amount6 = WorldUtil.INSTANCE.computeAmount(new Vec3(blockPos.getX() + vec32.x, blockPos.getY() + vec32.y + 1.0, blockPos.getZ() + vec32.z));
               if (Float.compare(amount5, amount6) > 0) {
                  object = object3;
                  amount5 = amount6;
               }
            } while (iterator.hasNext());

            object2 = object;
         }

         Vec3 vec33 = (Vec3)object2;
         Vec3 vec34 = new Vec3(blockPos.getX() + vec33.x, blockPos.getY() + 1.0, blockPos.getZ() + vec33.z);
         if (vec34.distanceToSqr(InteractionManager.INSTANCE.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()))
            <= configState.getAmount() * configState.getAmount()) {
            return new BlockHitResult(vec34, Direction.UP, blockPos, false);
         }
      }

      if (MinecraftHolder.getLocalPlayer().getEyePosition().y < blockPos.getY() && MinecraftHolder.getClientLevel().isEmptyBlock(blockPos.below())) {
         Iterable items3 = items;
         Iterator iterator2 = items3.iterator();
         if (!iterator2.hasNext()) {
            throw new NoSuchElementException();
         }

         Object object4 = iterator2.next();
         Object object5;
         if (!iterator2.hasNext()) {
            object5 = object4;
         } else {
            Vec3 vec35 = (Vec3)object4;
            float amount7 = WorldUtil.INSTANCE.computeAmount(new Vec3(blockPos.getX() + vec35.x, blockPos.getY() + vec35.y, blockPos.getZ() + vec35.z));

            do {
               Object object6 = iterator2.next();
               Vec3 vec36 = (Vec3)object6;
               float amount8 = WorldUtil.INSTANCE.computeAmount(new Vec3(blockPos.getX() + vec36.x, blockPos.getY() + vec36.y, blockPos.getZ() + vec36.z));
               if (Float.compare(amount7, amount8) > 0) {
                  object4 = object6;
                  amount7 = amount8;
               }
            } while (iterator2.hasNext());

            object5 = object4;
         }

         Vec3 vec37 = (Vec3)object5;
         Vec3 vec38 = new Vec3(blockPos.getX() + vec37.x, blockPos.getY(), blockPos.getZ() + vec37.z);
         if (vec38.distanceToSqr(InteractionManager.INSTANCE.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()))
            <= configState.getAmount() * configState.getAmount()) {
            return new BlockHitResult(vec38, Direction.DOWN, blockPos, false);
         }
      }

      for (Direction item : (Iterable<Direction>)InteractionManager.INSTANCE.computeItems(blockPos)) {
         if (item != Direction.UP && item != Direction.DOWN) {
            Vec3 vec39 = new Vec3(
               blockPos.getX() + 0.5 + item.getUnitVec3i().getX() * 0.5, blockPos.getY() + 0.99, blockPos.getZ() + 0.5 + item.getUnitVec3i().getZ() * 0.5
            );
            if (MinecraftHolder.getClientLevel().getBlockState(blockPos.relative(item)).canBeReplaced()) {
               float amount9 = WorldUtil.INSTANCE.computeAmount(vec39);
               if (amount4 > amount9) {
                  direction = item;
                  amount4 = amount9;
               }
            }
         }
      }

      if (direction == null) {
         return null;
      } else {
         Vec3 vec310 = Vec3iUtil.computeVec3(blockPos)
            .add(direction.getUnitVec3i().getX() * 0.5, direction.getUnitVec3i().getY() * 0.5, direction.getUnitVec3i().getZ() * 0.5);
         return new BlockHitResult(vec310, direction, blockPos, false);
      }
   }

   private static final BlockHitResult fetchKey(BlockPos blockPos, ConfigState configState, boolean flag) {
      for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)(MapsKt.toSortedMap(
            map,
            Comparator.comparing(
               arg -> WorldUtil.INSTANCE
                  .computeAmount(new Vec3(blockPos.getX() + ((Vec3)arg).x, blockPos.getY() + ((Vec3)arg).y, blockPos.getZ() + ((Vec3)arg).z))
            )
         )
         .entrySet())) {
         Vec3 vec3 = new Vec3(
            blockPos.getX() + ((Vec3)entrySetItem.getKey()).x, blockPos.getY() + ((Vec3)entrySetItem.getKey()).y - 0.01, blockPos.getZ() + ((Vec3)entrySetItem.getKey()).z
         );
         if (!(WorldUtil.INSTANCE.computeAmount(vec3) > configState.getAmount())) {
            BlockHitResult blockHitResult = CrystalPredictor.INSTANCE
               .computeBlockHitResult(
                  InteractionManager.INSTANCE.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer()),
                  vec3,
                  false,
                  CollectionsKt.emptyList(),
                  flag ? CollectionsKt.listOf(blockPos) : CollectionsKt.emptyList()
               );
            if (blockHitResult != null && blockHitResult.getType() == Type.BLOCK && Intrinsics.areEqual(blockHitResult.getBlockPos(), blockPos)) {
               return blockHitResult;
            }
         }
      }

      return null;
   }

   private static final boolean checkDistanceTo(Vec3 vec3, BlockPos blockPos, Vec3 vec32, ConfigState configState) {
      Vec3 vec33 = vec32.add(0.0, MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose()), 0.0);
      return configState.isFlag3()
         ? CrystalInteractPoint.INSTANCE.fetchEyePosition(Vec3iUtil.computeVec3(blockPos).add(0.0, 0.5, 0.0), configState, vec33) != null
         : (
            configState.isFlag9()
               ? BlockInteractionPoint.INSTANCE.checkContains(Vec3iUtil.computeVec3(blockPos).add(0.0, 0.5, 0.0), vec3)
               : vec33.distanceTo(vec3) <= configState.getAmount2()
         );
   }

   public static final boolean checkAbove(AutoCrystal self, BlockPos base, boolean flag, ConfigState configState) {
      Object var37 = null;
      AABB aabb;
      boolean flag2;
      label164: {
         label189: {
            AABB aabb2 = MinecraftHolder.getClientPacketListener().getServerData() != null
                  && KotlinCompat.contains(MinecraftHolder.getClientPacketListener().getServerData().ip, "crystalpvp.cc", false, 2, null)
               ? new AABB(base.above())
               : new AABB(base.above()).inflate(0.0, 1.0, 0.0);
            aabb = aabb2;
            if (self.getAtomicReference().get() != null) {
               Iterable enumEntries = (Iterable)TrapPlanner.EntriesMappings.enumEntries;
               if (enumEntries instanceof Collection && ((Collection)enumEntries).isEmpty()) {
                  flag2 = false;
               } else {
                  Iterator iterator = enumEntries.iterator();

                  while (true) {
                     if (!iterator.hasNext()) {
                        flag2 = false;
                        break;
                     }

                     Direction direction;
                     label156: {
                        Object object = iterator.next();
                        direction = (Direction)object;
                        DamageData atomicReferenceItem = (DamageData)self.getAtomicReference().get();
                        if (atomicReferenceItem != null) {
                           BlockHitResult object2 = (BlockHitResult)atomicReferenceItem.getObject();
                           if (object2 != null) {
                              var37 = object2.getBlockPos();
                              if (var37 != null) {
                                 break label156;
                              }
                           }
                        }

                        var37 = BlockPos.ZERO;
                     }

                     if (Intrinsics.areEqual(var37, base.relative(direction))) {
                        flag2 = true;
                        break;
                     }
                  }
               }

               if (flag2) {
                  break label189;
               }
            }

            Iterable enumEntries2 = (Iterable)TrapPlanner.EntriesMappings.enumEntries;
            if (enumEntries2 instanceof Collection && ((Collection)enumEntries2).isEmpty()) {
               flag2 = false;
            } else {
               Iterator iterator2 = enumEntries2.iterator();

               while (true) {
                  if (!iterator2.hasNext()) {
                     flag2 = false;
                     break;
                  }

                  Object object3 = iterator2.next();
                  Direction direction2 = (Direction)object3;
                  if (self.getLinkedList().contains(base.relative(direction2))) {
                     flag2 = true;
                     break;
                  }
               }
            }

            if (!flag2) {
               flag2 = false;
               break label164;
            }
         }

         flag2 = true;
      }

      boolean flag5 = flag2;
      if (self.getMap().containsKey(base.above()) && !flag5) {
         return true;
      } else {
         Iterator iterator3 = configState.getEntities().iterator();

         while (true) {
            if (!iterator3.hasNext()) {
               return false;
            }

            Entity entity = (Entity)iterator3.next();
            if (entity != null && entity.isAlive() && !(entity.distanceToSqr(base.getX() + 0.5, base.getY() + 0.5, base.getZ() + 0.5) > 16.0)) {
               if (entity instanceof Player) {
                  int value = 0;
                  int value2 = self.getValue();
                  if (value <= value2) {
                     while (true) {
                        if (PlayerPredictionService.INSTANCE.fetchBoundingBox2((Player)entity, value).intersects(aabb)) {
                           return true;
                        }

                        if (value == value2) {
                           break;
                        }

                        value++;
                     }
                  }
               }

               if (entity.getBoundingBox().intersects(aabb)
                  && !(entity instanceof ExperienceOrb)
                  && (
                     !(entity instanceof ItemEntity)
                        || !((ItemEntity)entity).isOnFire()
                           && !CrystalService.INSTANCE.checkContainsKey(((ItemEntity)entity).getId())
                           && !self.getMap().containsKey(base.above())
                           && !Intrinsics.areEqual(((ItemEntity)entity).getItem().getItem(), Items.STRING)
                  )) {
                  if (!(entity instanceof EndCrystal)) {
                     break;
                  }

                  if (!CrystalService.INSTANCE.checkContainsKey(((EndCrystal)entity).getId())) {
                     if (CrystalService.INSTANCE.checkContainsKey2(((EndCrystal)entity).getId())) {
                        return true;
                     }

                     if (flag) {
                        if (CrystalInteractPoint.INSTANCE.fetchY(self, self.computeVec3(entity), configState, true, ((EndCrystal)entity).tickCount) != null) {
                           continue;
                        }
                        break;
                     }

                     if (self.computeVec3(entity).distanceToSqr(aabb.getCenter()) > 0.3) {
                        CrystalInteractPoint.INSTANCE.setEndCrystal((EndCrystal)entity);
                     }
                     break;
                  }
               }
            }
         }

         return true;
      }
   }

   public static final boolean checkGetItem(BlockPos bp, Player target) {
      if (!target.isUsingItem()) {
         return false;
      } else if (!Intrinsics.areEqual(target.getOffhandItem().getItem(), Items.SHIELD)
         && !Intrinsics.areEqual(target.getMainHandItem().getItem(), Items.SHIELD)) {
         return false;
      } else {
         Vec3 viewVector = target.calculateViewVector(0.0F, target.yHeadRot);
         Vec3 vectorTo = Vec3iUtil.computeVec3(bp).add(0.0, 0.5, 0.0).vectorTo(AntiKnockBack.INSTANCE.computeVec3((Entity)target));
         return new Vec3(vectorTo.x, 0.0, vectorTo.z).normalize().dot(viewVector) < 0.0;
      }
   }

   static {
      Pair[] pairs = new Pair[]{
         TuplesKt.to(new Vec3(0.5, 1.0, 0.5), true),
         TuplesKt.to(new Vec3(0.5, 0.0, 0.5), true),
         TuplesKt.to(new Vec3(0.5, 0.5, 0.0), true),
         TuplesKt.to(new Vec3(0.5, 0.5, 1.0), true),
         TuplesKt.to(new Vec3(0.0, 0.5, 0.5), true),
         TuplesKt.to(new Vec3(1.0, 0.5, 0.5), true),
         TuplesKt.to(new Vec3(0.05, 0.05, 0.05), false),
         TuplesKt.to(new Vec3(0.05, 0.05, 0.95), false),
         TuplesKt.to(new Vec3(0.05, 0.95, 0.05), false),
         TuplesKt.to(new Vec3(0.05, 0.95, 0.95), false),
         TuplesKt.to(new Vec3(0.95, 0.05, 0.05), false),
         TuplesKt.to(new Vec3(0.95, 0.05, 0.95), false),
         TuplesKt.to(new Vec3(0.95, 0.95, 0.05), false),
         TuplesKt.to(new Vec3(0.95, 0.95, 0.95), false)
      };
      map = MapsKt.mapOf(pairs);
      Vec3[] vec3s = new Vec3[]{
         new Vec3(0.51, 0.0, 0.49),
         new Vec3(0.05, 0.0, 0.05),
         new Vec3(0.05, 0.0, 0.95),
         new Vec3(0.95, 0.0, 0.05),
         new Vec3(0.95, 0.0, 0.95),
         new Vec3(0.475, 0.0, 0.95),
         new Vec3(0.475, 0.0, 0.05),
         new Vec3(0.95, 0.0, 0.025),
         new Vec3(0.05, 0.0, 0.475)
      };
      items = CollectionsKt.listOf(vec3s);
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
   }
}