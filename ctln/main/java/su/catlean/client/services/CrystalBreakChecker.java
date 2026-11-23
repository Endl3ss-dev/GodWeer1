// deobf by pusia
package su.catlean.client.services;

import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.basic.AntiKnockBack;
import su.catlean.utils.MinecraftHolder;

public final class CrystalBreakChecker implements Service {
   @NotNull
   public static final CrystalBreakChecker INSTANCE = new CrystalBreakChecker();

   private CrystalBreakChecker() {
   }

   public final int computeValue(Player target) {
      int defaultImpl = ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null);
      int connection = ServerInfoService.INSTANCE.fetchConnection(target);
      float value = 0.12857F * (defaultImpl + connection) - 7;
      return RangesKt.coerceIn(MathKt.roundToInt(value), 0, 100);
   }

   public final boolean checkGetX(Player self) {
      BlockPos containing = BlockPos.containing((Position)AntiKnockBack.INSTANCE.computeVec3((Entity)self));
      int value = containing.getX() - 2;
      int value2 = containing.getX() + 2;
      if (value <= value2) {
         while (true) {
            int value3 = containing.getY() - 1;
            int value4 = containing.getY() + 4;
            if (value3 <= value4) {
               while (true) {
                  int value5 = containing.getZ() - 2;
                  int value6 = containing.getZ() + 2;
                  if (value5 <= value6) {
                     while (true) {
                        BlockPos blockPos = new BlockPos(value, value3, value5);
                        if (self.getBoundingBox().intersects(new AABB(blockPos))
                           && Intrinsics.areEqual(MinecraftHolder.getClientLevel().getBlockState(blockPos).getBlock(), Blocks.COBWEB)) {
                           return true;
                        }

                        if (value5 == value6) {
                           break;
                        }

                        value5++;
                     }
                  }

                  if (value3 == value4) {
                     break;
                  }

                  value3++;
               }
            }

            if (value == value2) {
               break;
            }

            value++;
         }
      }

      return false;
   }

   public final boolean checkGetX2(Player self) {
      BlockPos containing = BlockPos.containing((Position)AntiKnockBack.INSTANCE.computeVec3((Entity)self));
      int value = containing.getX() - 2;
      int value2 = containing.getX() + 2;
      if (value <= value2) {
         while (true) {
            int value3 = containing.getY() - 1;
            int value4 = containing.getY() + 4;
            if (value3 <= value4) {
               while (true) {
                  int value5 = containing.getZ() - 2;
                  int value6 = containing.getZ() + 2;
                  if (value5 <= value6) {
                     while (true) {
                        BlockPos blockPos = new BlockPos(value, value3, value5);
                        if (self.getBoundingBox().intersects(new AABB(blockPos))
                           && Intrinsics.areEqual(MinecraftHolder.getClientLevel().getBlockState(blockPos).getBlock(), Blocks.SWEET_BERRY_BUSH)) {
                           return true;
                        }

                        if (value5 == value6) {
                           break;
                        }

                        value5++;
                     }
                  }

                  if (value3 == value4) {
                     break;
                  }

                  value3++;
               }
            }

            if (value == value2) {
               break;
            }

            value++;
         }
      }

      return false;
   }

   @NotNull
   public final List<BlockPos> computeItems(Player self) {
      double value = BlockPos.containing(self.getX(), MathKt.roundToInt(self.getY()), self.getZ()).getY();
      AABB boundingBox = self.getBoundingBox();
      float value2 = 1.0E-5F;
      BlockPos[] blockPoss = new BlockPos[]{
         BlockPos.containing(boundingBox.minX, value, boundingBox.minZ),
         BlockPos.containing(boundingBox.minX, value, boundingBox.maxZ - value2),
         BlockPos.containing(boundingBox.maxX - value2, value, boundingBox.minZ),
         BlockPos.containing(boundingBox.maxX - value2, value, boundingBox.maxZ - value2)
      };
      Set<BlockPos> setOf = SetsKt.setOf(blockPoss);
      return CollectionsKt.toList(setOf);
   }

   public final boolean checkContaining(Player self, boolean flag) {
      return Intrinsics.areEqual(
            MinecraftHolder.getClientLevel().getBlockState(BlockPos.containing((Position)AntiKnockBack.INSTANCE.computeVec3((Entity)self))).getBlock(),
            Blocks.COBWEB
         )
         && (
            !flag
               || Intrinsics.areEqual(
                  MinecraftHolder.getClientLevel()
                     .getBlockState(BlockPos.containing((Position)AntiKnockBack.INSTANCE.computeVec3((Entity)self)).above())
                     .getBlock(),
                  Blocks.COBWEB
               )
         );
   }

   public final boolean checkContaining(Player self) {
      return Intrinsics.areEqual(
         MinecraftHolder.getClientLevel().getBlockState(BlockPos.containing((Position)AntiKnockBack.INSTANCE.computeVec3((Entity)self))).getBlock(),
         Blocks.SWEET_BERRY_BUSH
      );
   }
}