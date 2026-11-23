// deobf by pusia
package su.catlean.client.features.modules.attack.aura;

import kotlin.random.Random;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.LiquidBlock;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.rage.MaceElytraTarget;
import su.catlean.client.services.CrystalBreakChecker;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.world.WorldUtil;

public final class AuraMovementHandler implements AuraHandler {
   @NotNull
   public static final AuraMovementHandler INSTANCE = new AuraMovementHandler();
   private static int value;
   private static int value2;
   private static int value3;

   private AuraMovementHandler() {
   }

   public final int getValue() {
      return value;
   }

   public final void setValue(int value) {
      AuraMovementHandler.value = value;
   }

   public final int getValue2() {
      return value2;
   }

   public final void setValue2(int value) {
      value2 = value;
   }

   public final int getValue3() {
      return value3;
   }

   public final void setValue3(int value) {
      value3 = value;
   }

   @Override
   public void update() {
   }

   @Override
   public void applyMax(Rotation rotation) {
      int minCps = (int)(20.0F / Aura.INSTANCE.getMinCps()) - 1;
      int maxCps = (int)(20.0F / Aura.INSTANCE.getMaxCps()) - 1;
      int value4 = Math.min(minCps, maxCps);
      int value5 = Math.max(minCps, maxCps);
      int value6 = value3++;
      value = Aura.INSTANCE.getOldDelay()
         ? RangesKt.coerceIn(
            (int)(
               RangesKt.random(new IntRange(value4, value5), (Random)Random.Default)
                  + Math.sin(MinecraftHolder.getTime() % 100 / 100.0 * 2 * Math.PI) * 1.5
                  + 1.5
            ),
            value4,
            value5
         )
         : this.getValue4();
      value2 = value;
   }

   @Override
   public void update2() {
      int value4 = value;
      value = value4 + -1;
   }

   public final boolean testValue(int value) {
      return (this.checkContaining(value) || Aura.INSTANCE.getOldDelay()) && AuraMovementHandler.value - value <= 0;
   }

   public static boolean defaultImpl(AuraMovementHandler auraMovementHandler, int value, int value2, Object object) {
      if ((value2 & 1) != 0) {
         value = 0;
      }

      return auraMovementHandler.testValue(value);
   }

   public final boolean isFlag() {
      return defaultImpl2(this, 0, 1, null) && MinecraftHolder.getLocalPlayer().getAttackStrengthScale(0.5F) > 0.93;
   }

   private final boolean checkContaining(int value) {
      boolean flag;
      label93: {
         label92: {
            if (!MinecraftHolder.getLocalPlayer().onGround()
               && (WorldUtil.INSTANCE.getAmount3() > 0.0 || MinecraftHolder.getLocalPlayer().getDeltaMovement().y < 0.0031 && value != 0)) {
               double amount3 = WorldUtil.INSTANCE.getAmount3();
               if (1.13 <= amount3 ? !(amount3 <= 1.146) : true) {
                  break label92;
               }
            }

            if (MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
               flag = false;
               break label93;
            }
         }

         flag = true;
      }

      boolean flag2 = flag;
      if (!MinecraftHolder.getLocalPlayer().isInWater()) {
         boolean flag3 = MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.BLINDNESS)
            || MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.LEVITATION)
            || MinecraftHolder.getLocalPlayer().isEyeInFluid(FluidTags.WATER)
            || MinecraftHolder.getLocalPlayer().isInLava() && MinecraftHolder.getLocalPlayer().isEyeInFluid(FluidTags.LAVA)
            || CrystalBreakChecker.INSTANCE.checkGetX((Player)MinecraftHolder.getLocalPlayer())
            || CrystalBreakChecker.INSTANCE.checkGetX2((Player)MinecraftHolder.getLocalPlayer())
            || MinecraftHolder.getLocalPlayer().isSuppressingSlidingDownLadder()
            || MinecraftHolder.getLocalPlayer().getAbilities().flying;
         if (!flag3) {
            return flag2;
         }
      } else {
         boolean flag4 = !MinecraftHolder.getLocalPlayer().isSwimming()
            && MinecraftHolder.getClientLevel()
               .getBlockState(BlockPos.containing((Position)Aura.INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer())))
               .getBlock() instanceof LiquidBlock
            && MinecraftHolder.getClientLevel()
               .getBlockState(BlockPos.containing((Position)Aura.INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer())).above())
               .getBlock() instanceof AirBlock;
         boolean flag5 = !flag4 || MinecraftHolder.getLocalPlayer().getAbilities().flying;
         if (!MinecraftHolder.getLocalPlayer().isSwimming() && !MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
            return true;
         }

         if (!flag5) {
            return flag2;
         }
      }

      return true;
   }

   public static boolean defaultImpl2(AuraMovementHandler auraMovementHandler, int value, int value2, Object object) {
      if ((value2 & 1) != 0) {
         value = 0;
      }

      return auraMovementHandler.checkContaining(value);
   }

   private final int getValue4() {
      if (MaceElytraTarget.INSTANCE.isFlag()) {
         return 10;
      } else {
         double attributeValue = MinecraftHolder.getLocalPlayer().getAttributeValue(Attributes.ATTACK_SPEED);
         int attributeValue2 = (int)(1.0F / MinecraftHolder.getLocalPlayer().getAttributeValue(Attributes.ATTACK_SPEED) * 18);
         attributeValue2 = Math.max(attributeValue2, 10);
         if (attributeValue == 4.0 || (4.4 <= attributeValue ? attributeValue <= 4.401 : false) || (4.8 <= attributeValue ? attributeValue <= 4.801 : false)) {
            attributeValue2 = 10;
         }

         if (Aura.INSTANCE.getSplashWhileOnGround() && !MinecraftHolder.getMinecraft().options.keyJump.isDown() && MinecraftHolder.getLocalPlayer().onGround()) {
            attributeValue2 = 12;
         }

         int value4 = MinecraftHolder.getLocalPlayer().onGround() && !MinecraftHolder.getMinecraft().options.keyJump.isDown() ? 1 : 2;

         return switch (AuraMovementHandler.WhenMappings.values[Aura.INSTANCE.getRandomizeDelay().ordinal()]) {
            case 1 -> value3 % 7 == 3 ? attributeValue2 + value4 : attributeValue2;
            case 2 -> value3 % 5 == 0
               ? (MinecraftHolder.getLocalPlayer().onGround() && !MinecraftHolder.getMinecraft().options.keyJump.isDown() ? 14 : 13)
               : (value3 % 7 == 3 ? attributeValue2 + value4 : attributeValue2);
            default -> attributeValue2;
         };
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[Aura.RandomizeDelay.values().length];

         try {
            values2[Aura.RandomizeDelay.DEFAULT.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Aura.RandomizeDelay.VULCAN.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values2;
      }
   }
}