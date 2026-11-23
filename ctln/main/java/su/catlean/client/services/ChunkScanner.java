// deobf by pusia
package su.catlean.client.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.enums.HoleType;
import su.catlean.api.enums.Safety;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.basic.AntiKnockBack;
import su.catlean.client.features.modules.basic.HoleAnchor;
import su.catlean.client.features.modules.esp.HoleEsp;
import su.catlean.client.features.modules.protect.SurroundBlockInfo;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;

public final class ChunkScanner implements Service {
   @NotNull
   public static final ChunkScanner INSTANCE = new ChunkScanner();
   @NotNull
   private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
   @Nullable
   private static Future future;
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final Set items;
   @NotNull
   private static final Set items2;
   @NotNull
   private static List<Object> items3 = CollectionsKt.emptyList();
   @NotNull
   private static final List items4;
   @NotNull
   private static final List items5;
   @NotNull
   private static final List items6;

   private ChunkScanner() {
   }

   private final int getValue() {
      return Math.min(32, Math.max(HoleAnchor.INSTANCE.getHRange(), HoleEsp.INSTANCE.getRangeXZ()));
   }

   private final int getValue2() {
      return Math.min(32, Math.max(HoleAnchor.INSTANCE.getVRange(), HoleEsp.INSTANCE.getRangeY()));
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (stopWatch.testValue(1000)) {
         if (!items3.isEmpty()) {
            items3 = CollectionsKt.emptyList();
         }
      } else {
         future = CompletableFuture.<List>supplyAsync(() -> INSTANCE.getItems2(), executorService).exceptionally(arg -> CollectionsKt.emptyList());
      }
   }

   @NotNull
   public final List getItems() {
      stopWatch.update();
      if (future != null ? future.isDone() : false) {
         try {
            items3 = (List)future.get();
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
         } catch (java.util.concurrent.ExecutionException exception) {
            throw new RuntimeException(exception);
         }
      }

      return items3;
   }

   public final boolean checkOffset(BlockPos pos) {
      stopWatch.update();

      for (SurroundBlockInfo item : (Iterable<SurroundBlockInfo>)this.getItems()) {
         if (Intrinsics.areEqual(pos, item.getPos())) {
            return true;
         }

         if (item.getShape() == HoleType.DOUBLE_X && Intrinsics.areEqual(pos, item.getPos().offset(1, 0, 0))) {
            return true;
         }

         if (item.getShape() == HoleType.DOUBLE_Z && Intrinsics.areEqual(pos, item.getPos().offset(0, 0, 1))) {
            return true;
         }

         if (item.getShape() == HoleType.QUAD
            && (
               Intrinsics.areEqual(pos, item.getPos().offset(0, 0, 1))
                  || Intrinsics.areEqual(pos, item.getPos().offset(1, 0, 1))
                  || Intrinsics.areEqual(pos, item.getPos().offset(1, 0, 0))
            )) {
            return true;
         }
      }

      return false;
   }

   @NotNull
   public final BlockState fetchSections(int value, int value2, int value3) {
      try {
         LevelChunk chunk = MinecraftHolder.getClientLevel().getChunk(value >> 4, value3 >> 4);
         int sectionIndex = MinecraftHolder.getClientLevel().getSectionIndex(value2);
         return sectionIndex >= 0 && sectionIndex < chunk.getSections().length && !chunk.getSections()[sectionIndex].hasOnlyAir()
            ? chunk.getSections()[sectionIndex].getBlockState(value & 15, value2 & 15, value3 & 15)
            : Blocks.AIR.defaultBlockState();
      } catch (Throwable exception) {
         return Blocks.AIR.defaultBlockState();
      }
   }

   private final BlockState computeBlockState(int value, int value2, int value3) {
      return this.fetchSections(value, value2, value3);
   }

   private final boolean checkCanBeReplaced(int value, int value2, int value3) {
      return this.computeBlockState(value, value2, value3).canBeReplaced();
   }

   private final boolean checkContains(ChunkAccess chunkAccess, int value, int value2, int value3) {
      Block block = this.fetchSections(value, value2, value3).getBlock();
      return items2.contains(block) || items.contains(block);
   }

   private final boolean checkContains(int value, int value2, int value3) {
      return items2.contains(this.fetchSections(value, value2, value3).getBlock());
   }

   private final boolean checkContains2(int value, int value2, int value3) {
      return items.contains(this.fetchSections(value, value2, value3).getBlock());
   }

   private final SurroundBlockInfo fetchX(int value, int value2, int value3) {
      int value4 = 0;
      int value5 = 0;
      if (this.checkContains2(value - 1, value2, value3)) {
         value4++;
      } else if (this.checkContains(value - 1, value2, value3)) {
         value5++;
      }

      if (this.checkContains2(value, value2, value3 - 1)) {
         value4++;
      } else if (this.checkContains(value, value2, value3 - 1)) {
         value5++;
      }

      if (value4 + value5 < 2) {
         return null;
      } else {
         boolean isAir = this.fetchSections(value, value2, value3 + 1).isAir();
         boolean isAir2 = this.fetchSections(value + 1, value2, value3).isAir();
         if (isAir && isAir2) {
            if (!this.fetchSections(value + 1, value2, value3 + 1).isAir()) {
               return null;
            } else {
               for (Vec3i items4Item : (Iterable<Vec3i>)items4) {
                  if (this.checkContains2(value + items4Item.getX(), value2 + items4Item.getY(), value3 + items4Item.getZ())) {
                     value4++;
                  } else if (this.checkContains(value + items4Item.getX(), value2 + items4Item.getY(), value3 + items4Item.getZ())) {
                     value5++;
                  }
               }

               if (value4 != 8 && value5 != 8 && value4 + value5 != 8) {
                  return null;
               } else {
                  BlockPos blockPos = new BlockPos(value, value2, value3);
                  Safety safe = value5 == 8 ? Safety.SAFE : Safety.UNSAFE;
                  HoleType quad = HoleType.QUAD;
                  Safety safety = safe;
                  BlockPos blockPos2 = blockPos;
                  return new SurroundBlockInfo(blockPos2, safety, quad);
               }
            }
         } else if (isAir && this.checkCanBeReplaced(value, value2, value3 + 1)) {
            for (Vec3i items5Item : (Iterable<Vec3i>)items5) {
               if (this.checkContains2(value + items5Item.getX(), value2 + items5Item.getY(), value3 + items5Item.getZ())) {
                  value4++;
               } else if (this.checkContains(value + items5Item.getX(), value2 + items5Item.getY(), value3 + items5Item.getZ())) {
                  value5++;
               }
            }

            if (value4 != 6 && value5 != 6 && value4 + value5 != 6) {
               return null;
            } else {
               BlockPos blockPos3 = new BlockPos(value, value2, value3);
               Safety safe2 = value5 == 6 ? Safety.SAFE : Safety.UNSAFE;
               HoleType doublE_Z = HoleType.DOUBLE_Z;
               Safety safety2 = safe2;
               BlockPos blockPos4 = blockPos3;
               return new SurroundBlockInfo(blockPos4, safety2, doublE_Z);
            }
         } else if (isAir2 && this.checkCanBeReplaced(value + 1, value2, value3)) {
            for (Vec3i items6Item : (Iterable<Vec3i>)items6) {
               if (this.checkContains2(value + items6Item.getX(), value2 + items6Item.getY(), value3 + items6Item.getZ())) {
                  value4++;
               } else if (this.checkContains(value + items6Item.getX(), value2 + items6Item.getY(), value3 + items6Item.getZ())) {
                  value5++;
               }
            }

            if (value4 != 6 && value5 != 6 && value4 + value5 != 6) {
               return null;
            } else {
               BlockPos blockPos5 = new BlockPos(value, value2, value3);
               Safety safe3 = value5 == 6 ? Safety.SAFE : Safety.UNSAFE;
               HoleType doublE_X = HoleType.DOUBLE_X;
               Safety safety3 = safe3;
               BlockPos blockPos6 = blockPos5;
               return new SurroundBlockInfo(blockPos6, safety3, doublE_X);
            }
         } else {
            if (this.checkContains2(value, value2, value3 + 1)) {
               value4++;
            } else if (this.checkContains(value, value2, value3 + 1)) {
               value5++;
            }

            if (this.checkContains2(value + 1, value2, value3)) {
               value4++;
            } else if (this.checkContains(value + 1, value2, value3)) {
               value5++;
            }

            if (value4 != 4 && value5 != 4 && value4 + value5 != 4) {
               return null;
            } else {
               BlockPos blockPos7 = new BlockPos(value, value2, value3);
               Safety safe4 = value5 == 4 ? Safety.SAFE : Safety.UNSAFE;
               HoleType single = HoleType.SINGLE;
               Safety safety4 = safe4;
               BlockPos blockPos8 = blockPos7;
               return new SurroundBlockInfo(blockPos8, safety4, single);
            }
         }
      }
   }

   private final List<SurroundBlockInfo> getItems2() {
      List<SurroundBlockInfo> arrayList = new ArrayList();
      BlockPos containing = BlockPos.containing((Position)AntiKnockBack.INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
      int value = containing.getX() - this.getValue();
      int value2 = containing.getX() + this.getValue();
      int value22 = containing.getY() - this.getValue2();
      int value23 = containing.getY() + this.getValue2();
      int value3 = containing.getZ() - this.getValue();
      int value4 = containing.getZ() + this.getValue();
      int value5 = value;
      if (value <= value2) {
         while (true) {
            int value6 = value22;
            if (value22 <= value23) {
               while (true) {
                  int value7 = value3;
                  if (value3 <= value4) {
                     while (true) {
                        LevelChunk chunk = MinecraftHolder.getClientLevel().getChunk(value5 >> 4, value7 >> 4);
                        BlockState blockState = this.computeBlockState(value5, value6, value7);
                        if (blockState.canBeReplaced()
                           && this.checkCanBeReplaced(value5, value6 + 1, value7)
                           && this.checkCanBeReplaced(value5, value6 + 2, value7)
                           && this.checkContains((ChunkAccess)chunk, value5, value6 - 1, value7)) {
                           SurroundBlockInfo surroundBlockInfo = this.fetchX(value5, value6, value7);
                           if (surroundBlockInfo != null) {
                              SurroundBlockInfo surroundBlockInfo2 = surroundBlockInfo;
                              arrayList.add(surroundBlockInfo2);
                           }
                        }

                        if (value7 == value4) {
                           break;
                        }

                        value7++;
                     }
                  }

                  if (value6 == value23) {
                     break;
                  }

                  value6++;
               }
            }

            if (value5 == value2) {
               break;
            }

            value5++;
         }
      }

      return arrayList;
   }

   static {
      Block[] blocks = new Block[]{Blocks.OBSIDIAN, Blocks.ENDER_CHEST, Blocks.ANCIENT_DEBRIS, Blocks.NETHERITE_BLOCK};
      items = SetsKt.setOf(blocks);
      blocks = new Block[]{Blocks.BEDROCK, Blocks.BARRIER};
      items2 = SetsKt.setOf(blocks);
      Vec3i[] vec3is = new Vec3i[]{new Vec3i(-1, 0, 1), new Vec3i(0, 0, 2), new Vec3i(1, 0, 2), new Vec3i(2, 0, 1), new Vec3i(2, 0, 0), new Vec3i(1, 0, -1)};
      items4 = CollectionsKt.listOf(vec3is);
      Vec3i[] vec3is2 = new Vec3i[]{new Vec3i(-1, 0, 1), new Vec3i(0, 0, 2), new Vec3i(1, 0, 1), new Vec3i(1, 0, 0)};
      items5 = CollectionsKt.listOf(vec3is2);
      Vec3i[] vec3is3 = new Vec3i[]{new Vec3i(0, 0, 1), new Vec3i(1, 0, 1), new Vec3i(2, 0, 0), new Vec3i(1, 0, -1)};
      items6 = CollectionsKt.mutableListOf(vec3is3);
   }
}