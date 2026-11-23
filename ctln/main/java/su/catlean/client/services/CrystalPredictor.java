// deobf by pusia
package su.catlean.client.services;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.network.PostTasksProcessEvent;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.features.ui.panel.ThemeColorMap;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.EnchantmentUtil;
import su.catlean.utils.inventory.InventoryUtil;

public final class CrystalPredictor implements Service {
   @NotNull
   public static final CrystalPredictor INSTANCE = new CrystalPredictor();
   public static final double amount = -1.0E-7;
   @NotNull
   private static final AtomicInteger atomicInteger = new AtomicInteger();
   @NotNull
   private static final CrystalPredictor.ThreadLocalMutableBlockPos threadLocalMutableBlockPos = new CrystalPredictor.ThreadLocalMutableBlockPos();
   @NotNull
   private static final VoxelShape voxelShape = Block.column(14.0, 0.0, 14.0);
   @Nullable
   private static DamageSource damageSource;
   private static boolean flag = true;

   private CrystalPredictor() {
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      CrystalPredictor.flag = flag;
   }

   @Flow
   private final void onPostTasksProcess(PostTasksProcessEvent postTasksProcessEvent) {
      atomicInteger.incrementAndGet();
   }

   public final float computeAmount(Vec3 explosionPos, Player target, int value, boolean flag, List ghost, List solid) {
      AABB boundingBox2 = value > 0 ? PlayerPredictionService.INSTANCE.fetchBoundingBox2(target, value) : target.getBoundingBox();
      double distanceTo = boundingBox2.getCenter().add(0.0, -boundingBox2.getYsize() / 2.0, 0.0).distanceTo(explosionPos) / 12.0;
      return this.getDifficulty() != Difficulty.PEACEFUL && !(distanceTo > 1.0) && !target.isCreative()
         ? this.computeAmount(target, distanceTo, this.computeAmount(explosionPos, boundingBox2, flag, ghost, solid))
         : 0.0F;
   }

   public static float defaultImpl(
      CrystalPredictor crystalPredictor, Vec3 vec3, Player player, int value, boolean flag, List items, List items2, int value2, Object object
   ) {
      if ((value2 & 2) != 0) {
         player = (Player)MinecraftHolder.getLocalPlayer();
      }

      if ((value2 & 4) != 0) {
         value = 0;
      }

      if ((value2 & 8) != 0) {
         flag = false;
      }

      if ((value2 & 16) != 0) {
         items = CollectionsKt.emptyList();
      }

      if ((value2 & 32) != 0) {
         items2 = CollectionsKt.emptyList();
      }

      return crystalPredictor.computeAmount(vec3, player, value, flag, items, items2);
   }

   private final float computeAmount(Player player, double amount, double amount2) {
      if (damageSource == null) {
         damageSource = new DamageSource((Holder)MinecraftHolder.getClientLevel().damageSources().damageTypes.getOrThrow(DamageTypes.EXPLOSION), null, null);
      }

      float amount3 = this.computeAmount(amount, amount2);
      if (this.getDifficulty() == Difficulty.EASY) {
         amount3 = (float)Math.min(amount3 / 2.0 + 1.0, (double)amount3);
      } else if (this.getDifficulty() == Difficulty.HARD) {
         amount3 *= 1.5F;
      }

      amount3 = CombatRules.getDamageAfterAbsorb(
         (LivingEntity)player, amount3, damageSource, player.getArmorValue(), (float)player.getAttribute(Attributes.ARMOR_TOUGHNESS).getValue()
      );
      if (player.hasEffect(MobEffects.RESISTANCE)) {
         int amplifier = 25 - (player.getEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
         amount3 = (float)Math.max((double)(amount3 * amplifier / 25.0F), 0.0);
      }

      if (amount3 <= 0.0F) {
         amount3 = 0.0F;
      } else {
         float amount4 = AutoCrystal.INSTANCE.getAssumeBestArmor() && !Intrinsics.areEqual(player, MinecraftHolder.getLocalPlayer())
            ? this.computeAmount(InventoryUtil.fetchInventory(player))
            : this.computeValue(InventoryUtil.fetchInventory(player));
         if (amount4 > 0.0F) {
            amount3 = CombatRules.getDamageAfterMagicAbsorb(amount3, amount4);
         }
      }

      return amount3;
   }

   private final float computeAmount(Iterable items) {
      float value = 0.0F;

      for (Object item : items) {
         ItemStack itemStack = (ItemStack)item;
         if (!itemStack.isEmpty()) {
            value += 8.0F;
         }
      }

      return value;
   }

   private final float computeAmount(double amount, double amount2) {
      double amount3 = (1.0 - amount) * amount2;
      return (float)((amount3 * amount3 + amount3) / 2.0 * 7.0 * 12.0 + 1.0);
   }

   private final float computeAmount(Vec3 vec3, AABB aABB, boolean flag, List items, List items2) {
      Pair pair = this.computePair(vec3, aABB, flag, items, items2);
      if ((float)((Number)pair.getFirst()).intValue() / ((Number)pair.getSecond()).intValue() == 1.0F) {
         return 1.0F;
      } else if ((float)((Number)pair.getFirst()).intValue() / ((Number)pair.getSecond()).intValue() == 0.0F) {
         return 0.0F;
      } else {
         double maxX = 1.0 / ((aABB.maxX - aABB.minX) * 2.0 + 1.0);
         double maxY = 1.0 / ((aABB.maxY - aABB.minY) * 2.0 + 1.0);
         double maxZ = 1.0 / ((aABB.maxZ - aABB.minZ) * 2.0 + 1.0);
         double value = (1.0 - Math.floor(1.0 / maxX) * maxX) / 2.0;
         double value2 = (1.0 - Math.floor(1.0 / maxZ) * maxZ) / 2.0;
         if (maxX >= 0.0 && maxY >= 0.0 && maxZ >= 0.0) {
            int value3 = ((Number)pair.getFirst()).intValue();
            int value4 = ((Number)pair.getSecond()).intValue();

            for (double value5 = 0.0; value5 <= 1.0; value5 += maxX) {
               for (double value6 = 0.0; value6 <= 1.0; value6 += maxY) {
                  for (double value7 = 0.0; value7 <= 1.0; value7 += maxZ) {
                     double value8 = Mth.lerp(value5, aABB.minX, aABB.maxX);
                     double value9 = Mth.lerp(value6, aABB.minY, aABB.maxY);
                     double value10 = Mth.lerp(value7, aABB.minZ, aABB.maxZ);
                     if (!this.testAmount(value5, value6, value7)) {
                        if (this.computeBlockHitResult(new Vec3(value8 + value, value9, value10 + value2), vec3, flag, items, items2) == null) {
                           value3++;
                        }

                        value4++;
                     }
                  }
               }
            }

            return (float)value3 / value4;
         } else {
            return 0.0F;
         }
      }
   }

   private final boolean testAmount(double amount, double amount2, double amount3) {
      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 2; j++) {
            for (int k = 0; k < 2; k++) {
               if (amount == i && amount2 == j && amount3 == k) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private final Pair computePair(Vec3 vec3, AABB aABB, boolean flag, List items, List items2) {
      double maxX = 1.0 / ((aABB.maxX - aABB.minX) * 2.0 + 1.0);
      double maxZ = 1.0 / ((aABB.maxZ - aABB.minZ) * 2.0 + 1.0);
      double value = (1.0 - Math.floor(1.0 / maxX) * maxX) / 2.0;
      double value2 = (1.0 - Math.floor(1.0 / maxZ) * maxZ) / 2.0;
      int value3 = 0;
      int value4 = 0;

      for (double value5 = 0.0; value5 <= 1.0; value5++) {
         for (double value6 = 0.0; value6 <= 1.0; value6++) {
            for (double value7 = 0.0; value7 <= 1.0; value7++) {
               double value8 = Mth.lerp(value5, aABB.minX, aABB.maxX);
               double value9 = Mth.lerp(value6, aABB.minY, aABB.maxY);
               double value10 = Mth.lerp(value7, aABB.minZ, aABB.maxZ);
               Vec3 vec32 = new Vec3(value8 + value, value9, value10 + value2);
               if (this.computeBlockHitResult(vec32, vec3, flag, items, items2) == null) {
                  value3++;
               }

               value4++;
            }
         }
      }

      return new Pair(value3, value4);
   }

   private final BlockHitResult fetchBlock(ThemeColorMap themeColorMap, BlockPos blockPos, Vec3 vec3, Vec3 vec32, boolean flag, List items, List items2) {
      VoxelShape voxelShape2 = null;
      float amount2 = 0.0F;
      if (items.contains(blockPos)) {
         return null;
      } else if (items2.contains(blockPos)) {
         return Shapes.block().clip(vec3, vec32, blockPos);
      } else {
         long longValue = blockPos.asLong();
         byte map = themeColorMap.getMap().get(longValue);
         if (CrystalPredictor.flag && map != 0) {
            voxelShape2 = this.computeVoxelShape(map);
            amount2 = this.computeAmount(map);
         } else {
            BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(blockPos);
            voxelShape2 = blockState.getCollisionShape((BlockGetter)MinecraftHolder.getClientLevel(), blockPos);
            amount2 = Intrinsics.areEqual(blockState.getBlock(), Blocks.GLOWSTONE) ? 600.0F : blockState.getBlock().getExplosionResistance();
            if (voxelShape2.isEmpty() || Intrinsics.areEqual(blockState.getBlock(), Blocks.ENDER_CHEST) || Intrinsics.areEqual(voxelShape2, Shapes.block())) {
               byte explosionResistance = this.fetchExplosionResistance(blockState.getBlock(), voxelShape2.isEmpty());
               ((Map)themeColorMap.getMap()).put(longValue, explosionResistance);
            }
         }

         return amount2 < 600.0F && flag ? null : voxelShape2.clip(vec3, vec32, blockPos);
      }
   }

   private final float computeAmount(byte value) {
      return value != 1 && value != 2 ? 600.0F : 0.0F;
   }

   private final VoxelShape computeVoxelShape(byte value) {
      return value == 1 ? Shapes.empty() : (value == 4 ? voxelShape : Shapes.block());
   }

   private final byte fetchExplosionResistance(Block block, boolean flag) {
      if (flag) {
         return 1;
      } else if (Intrinsics.areEqual(block, Blocks.ENDER_CHEST)) {
         return 4;
      } else {
         return (byte)(block.getExplosionResistance() >= 600.0F ? 3 : 2);
      }
   }

   private final ThemeColorMap getThemeColorMap() {
      ThemeColorMap threadLocalMutableBlockPo = (ThemeColorMap)threadLocalMutableBlockPos.get();
      int atomicIntegerItem = atomicInteger.get();
      if (threadLocalMutableBlockPo.getValue() != atomicIntegerItem) {
         threadLocalMutableBlockPo.getMap().clear();
         threadLocalMutableBlockPo.setValue(atomicIntegerItem);
      }

      return threadLocalMutableBlockPo;
   }

   @Nullable
   public final BlockHitResult computeBlockHitResult(Vec3 start, Vec3 end, boolean flag, List ghost, List solid) {
      ThemeColorMap themeColorMap = this.getThemeColorMap();
      if (Intrinsics.areEqual(start, end)) {
         return null;
      } else {
         double value = Mth.lerp(-1.0E-7, end.x, start.x);
         double value2 = Mth.lerp(-1.0E-7, end.y, start.y);
         double value3 = Mth.lerp(-1.0E-7, end.z, start.z);
         double value4 = Mth.lerp(-1.0E-7, start.x, end.x);
         double value5 = Mth.lerp(-1.0E-7, start.y, end.y);
         double value6 = Mth.lerp(-1.0E-7, start.z, end.z);
         int value7 = Mth.floor(value4);
         int value8 = Mth.floor(value5);
         int value9 = Mth.floor(value6);
         MutableBlockPos mutableBlockPos = new MutableBlockPos(value7, value8, value9);
         BlockHitResult block = this.fetchBlock(themeColorMap, (BlockPos)mutableBlockPos, start, end, flag, ghost, solid);
         if (block != null) {
            return block;
         } else {
            double value10 = value - value4;
            double value11 = value2 - value5;
            double value12 = value3 - value6;
            int sign = Mth.sign(value10);
            int sign2 = Mth.sign(value11);
            int sign3 = Mth.sign(value12);
            double maX_VALUE = sign == 0 ? Double.MAX_VALUE : sign / value10;
            double maX_VALUE2 = sign2 == 0 ? Double.MAX_VALUE : sign2 / value11;
            double maX_VALUE3 = sign3 == 0 ? Double.MAX_VALUE : sign3 / value12;
            double frac = maX_VALUE * (sign > 0 ? 1.0 - Mth.frac(value4) : Mth.frac(value4));
            double frac2 = maX_VALUE2 * (sign2 > 0 ? 1.0 - Mth.frac(value5) : Mth.frac(value5));
            double frac3 = maX_VALUE3 * (sign3 > 0 ? 1.0 - Mth.frac(value6) : Mth.frac(value6));

            while (frac <= 1.0 || frac2 <= 1.0 || frac3 <= 1.0) {
               if (frac < frac2) {
                  if (frac < frac3) {
                     value7 += sign;
                     frac += maX_VALUE;
                  } else {
                     value9 += sign3;
                     frac3 += maX_VALUE3;
                  }
               } else if (frac2 < frac3) {
                  value8 += sign2;
                  frac2 += maX_VALUE2;
               } else {
                  value9 += sign3;
                  frac3 += maX_VALUE3;
               }

               BlockHitResult block2 = this.fetchBlock(themeColorMap, (BlockPos)mutableBlockPos.set(value7, value8, value9), start, end, flag, ghost, solid);
               if (block2 != null) {
                  return block2;
               }
            }

            return null;
         }
      }
   }

   public static BlockHitResult defaultImpl(
      CrystalPredictor crystalPredictor, Vec3 vec3, Vec3 vec32, boolean flag, List items, List items2, int value, Object object
   ) {
      if ((value & 4) != 0) {
         flag = false;
      }

      if ((value & 8) != 0) {
         items = CollectionsKt.emptyList();
      }

      if ((value & 16) != 0) {
         items2 = CollectionsKt.emptyList();
      }

      return crystalPredictor.computeBlockHitResult(vec3, vec32, flag, items, items2);
   }

   public final boolean checkGetType(Vec3 point, Vec3 from, List ghost, List solid) {
      BlockHitResult blockHitResult = this.computeBlockHitResult(from, point, false, ghost, solid);
      if (blockHitResult != null) {
         Type type = blockHitResult.getType();
         if (type != null) {
            return type == Type.MISS;
         }
      }

      return Type.MISS == Type.MISS;
   }

   public static boolean defaultImpl(CrystalPredictor crystalPredictor, Vec3 vec3, Vec3 vec32, List items, List items2, int value, Object object) {
      if ((value & 2) != 0) {
         vec32 = InteractionManager.INSTANCE.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer());
      }

      if ((value & 4) != 0) {
         items = CollectionsKt.emptyList();
      }

      if ((value & 8) != 0) {
         items2 = CollectionsKt.emptyList();
      }

      return crystalPredictor.checkGetType(vec3, vec32, items, items2);
   }

   private final int computeValue(Iterable items) {
      int value = 0;

      for (Object item : items) {
         ItemStack itemStack = (ItemStack)item;
         value += INSTANCE.computeValue(itemStack);
      }

      return value;
   }

   private final int computeValue(ItemStack itemStack) {
      if (itemStack != null && !itemStack.isEmpty()) {
         int itemEnchantmentLevel = EnchantmentUtil.fetchItemEnchantmentLevel(itemStack, Enchantments.BLAST_PROTECTION);
         int itemEnchantmentLevel2 = EnchantmentUtil.fetchItemEnchantmentLevel(itemStack, Enchantments.PROTECTION);
         return itemEnchantmentLevel * 2 + itemEnchantmentLevel2;
      } else {
         return 0;
      }
   }

   @NotNull
   public final Difficulty getDifficulty() {
      return MinecraftHolder.getClientLevel().getDifficulty();
   }

   public static final AtomicInteger getAtomicInteger() {
      return atomicInteger;
   }

   public static final class ThreadLocalMutableBlockPos extends ThreadLocal {
      public ThreadLocalMutableBlockPos() {
      }

      public ThemeColorMap getThemeColorMap() {
         return new ThemeColorMap(new Long2ByteOpenHashMap(), CrystalPredictor.getAtomicInteger().get());
      }

      @Override
      public Object initialValue() {
         return this.getThemeColorMap();
      }
   }
}