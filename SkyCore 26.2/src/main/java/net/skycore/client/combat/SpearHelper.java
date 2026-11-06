package net.skycore.client.combat;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.skycore.client.config.FriendManager;
import net.skycore.client.module.combat.AntiBotModule;
import net.skycore.client.util.ChargedHelper;

/**
 * Общая расчётная часть копья (1.21.11 / 26.2) для модулей {@code Extended Reach},
 * {@code Instant Lunge}, {@code Spear Damage Indicator} и {@code Spear Mace Double Tap}.
 *
 * <p>Числа — из Minecraft Wiki (Spear, Lunge): импульс «Рывка» {@code 0.458 × N}
 * блоков/тик, урон заряда {@code floor(множитель × скорость сближения в бл/с)} плюс
 * атрибутивный урон предмета, порог урона 4.6 бл/с, дальность копья 2…4.5 блока.
 *
 * <p>Совместимость с 26.2: имена предметов и зачарований ({@code Items.*_SPEAR},
 * {@code Enchantments.LUNGE}, {@code ResourceKey.create}) не используются — копьё
 * опознаётся по {@link ChargedHelper#kind(ItemStack)}, тир — по строке id, уровень
 * зачарования — через кэшированный {@link Holder} после первого обнаружения в мире.
 */
public final class SpearHelper {
   public static final String KIND = "spear";
   public static final String LUNGE_PATH = "lunge";
   public static final String DENSITY_PATH = "density";
   public static final String BREACH_PATH = "breach";
   public static final double LUNGE_BLOCKS_PER_TICK = 0.458;
   public static final double CHARGE_DAMAGE_SPEED = 4.6;
   public static final double MIN_REACH = 2.05;
   public static final int HOTBAR_SIZE = 9;
   public static final double TICKS_PER_SECOND = 20.0;
   public static final float AIM_HEIGHT = 0.85F;

   private static Object cachedEnchantWorld;
   private static Holder<Enchantment> cachedLungeHolder;
   private static Holder<Enchantment> cachedDensityHolder;
   private static Holder<Enchantment> cachedBreachHolder;

   private SpearHelper() {
   }

   public static boolean isSpear(ItemStack stack) {
      return stack != null && !stack.isEmpty() && KIND.equals(ChargedHelper.kind(stack));
   }

   /** Путь предмета без неймспейса, например {@code "netherite_spear"}. */
   public static String path(ItemStack stack) {
      String id = ChargedHelper.id(stack);
      int colon = id.indexOf(58);
      return colon >= 0 ? id.substring(colon + 1) : id;
   }

   private static void syncEnchantWorld() {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != cachedEnchantWorld) {
         cachedEnchantWorld = level;
         cachedLungeHolder = null;
         cachedDensityHolder = null;
         cachedBreachHolder = null;
      }
   }

   public static void warmHotbarEnchantCache(LocalPlayer player) {
      if (player == null) {
         return;
      }
      syncEnchantWorld();
      for (int i = 0; i < HOTBAR_SIZE; i++) {
         ItemStack stack = player.getInventory().getItem(i);
         if (!stack.isEmpty()) {
            ItemEnchantments enchantments = stack.getEnchantments();
            if (!enchantments.isEmpty()) {
               captureHolders(enchantments);
            }
         }
      }
   }

   private static void captureHolders(ItemEnchantments enchantments) {
      for (var entry : enchantments.entrySet()) {
         Holder<Enchantment> holder = entry.getKey();
         var key = holder.unwrapKey();
         if (key.isPresent()) {
            String p = key.get().identifier().getPath();
            if (LUNGE_PATH.equals(p)) {
               cachedLungeHolder = holder;
            } else if (DENSITY_PATH.equals(p)) {
               cachedDensityHolder = holder;
            } else if (BREACH_PATH.equals(p)) {
               cachedBreachHolder = holder;
            }
         }
      }
   }

   public static int enchantLevel(ItemStack stack, String path) {
      if (stack == null || stack.isEmpty()) {
         return 0;
      }
      ItemEnchantments enchantments = stack.getEnchantments();
      if (enchantments.isEmpty()) {
         return 0;
      }
      syncEnchantWorld();
      if (LUNGE_PATH.equals(path) && cachedLungeHolder != null) {
         return enchantments.getLevel(cachedLungeHolder);
      }
      if (DENSITY_PATH.equals(path) && cachedDensityHolder != null) {
         return enchantments.getLevel(cachedDensityHolder);
      }
      if (BREACH_PATH.equals(path) && cachedBreachHolder != null) {
         return enchantments.getLevel(cachedBreachHolder);
      }
      captureHolders(enchantments);
      if (LUNGE_PATH.equals(path) && cachedLungeHolder != null) {
         return enchantments.getLevel(cachedLungeHolder);
      }
      if (DENSITY_PATH.equals(path) && cachedDensityHolder != null) {
         return enchantments.getLevel(cachedDensityHolder);
      }
      if (BREACH_PATH.equals(path) && cachedBreachHolder != null) {
         return enchantments.getLevel(cachedBreachHolder);
      }
      for (var entry : enchantments.entrySet()) {
         var key = entry.getKey().unwrapKey();
         if (key.isPresent() && path.equals(key.get().identifier().getPath())) {
            return entry.getIntValue();
         }
      }

      return 0;
   }

   public static int lungeLevel(ItemStack stack) {
      return enchantLevel(stack, LUNGE_PATH);
   }

   public static int densityLevel(ItemStack stack) {
      return enchantLevel(stack, DENSITY_PATH);
   }

   public static int breachLevel(ItemStack stack) {
      return enchantLevel(stack, BREACH_PATH);
   }

   /** Множитель заряда по тиру копья. */
   public static float multiplier(String path) {
      if (path.endsWith("netherite_spear")) {
         return 1.2F;
      } else if (path.endsWith("diamond_spear")) {
         return 1.075F;
      } else if (path.endsWith("iron_spear")) {
         return 0.95F;
      } else if (!path.endsWith("copper_spear") && !path.endsWith("stone_spear")) {
         return 0.7F;
      } else {
         return 0.82F;
      }
   }

   /** Задержка, после которой заряд переходит в живую стадию, в тиках. */
   public static int chargeDelayTicks(String path) {
      if (path.endsWith("netherite_spear")) {
         return 9;
      } else if (path.endsWith("diamond_spear")) {
         return 10;
      } else if (path.endsWith("iron_spear")) {
         return 12;
      } else if (path.endsWith("copper_spear")) {
         return 13;
      } else if (!path.endsWith("stone_spear") && !path.endsWith("golden_spear")) {
         return path.endsWith("wooden_spear") ? 15 : 12;
      } else {
         return 14;
      }
   }

   /** Атрибутивный урон предмета: сумма модификаторов {@code ATTACK_DAMAGE}. */
   public static float baseDamage(ItemStack stack) {
      return ChargedHelper.damage(stack);
   }

   /** Скорость, которую даёт «Рывок N» в блоках/сек (без трения). */
   public static double lungeSpeed(int level) {
      return LUNGE_BLOCKS_PER_TICK * (double)Math.max(1, level) * TICKS_PER_SECOND;
   }

   /**
    * Оценка урона заряда. Ничего в игре не меняет — только объясняет игроку,
    * попадёт ли удар: {@code floor(множитель × скорость)} + атрибутивный урон.
    */
   public static float predictedDamage(float multiplier, float baseDamage, double speed) {
      if (speed < CHARGE_DAMAGE_SPEED) {
         return 0.0F;
      }

      return (float)Mth.floor((float)(multiplier * speed)) + baseDamage;
   }

   /** Проекция относительной скорости на направление взгляда, блоков/сек. */
   public static double projectedSpeed(float yaw, float pitch, double dx, double dy, double dz) {
      float yawRad = (float)Math.toRadians(yaw);
      float pitchRad = (float)Math.toRadians(pitch);
      float horizontalScale = -Mth.cos(-pitchRad);
      double viewX = (double)(Mth.sin(-yawRad - (float)Math.PI) * horizontalScale);
      double viewY = (double)Mth.sin(-pitchRad);
      double viewZ = (double)(Mth.cos(-yawRad - (float)Math.PI) * horizontalScale);
      return (dx * viewX + dy * viewY + dz * viewZ) * TICKS_PER_SECOND;
   }

   /** Прицел по цели с упреждением и ограниченным наклоном (длина «Рывка» зависит от него). */
   public static Rotation aim(LocalPlayer player, LivingEntity target, double leadTicks, double maxTilt) {
      var movement = target.getDeltaMovement();
      double dx = target.getX() + movement.x * leadTicks - player.getX();
      double dy = (double)target.getY() + (double)target.getBbHeight() * AIM_HEIGHT
            + movement.y * leadTicks - player.getEyePosition().y;
      double dz = target.getZ() + movement.z * leadTicks - player.getZ();
      double horizontal = Math.hypot(dx, dz);
      float yaw = (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
      float pitch = (float)Mth.wrapDegrees(-Math.toDegrees(Math.atan2(dy, horizontal)));
      float tilt = (float)maxTilt;
      return new Rotation(yaw, Mth.clamp(pitch, -tilt, tilt));
   }

   public static float aimDelta(LocalPlayer player, Rotation rotation) {
      return Math.abs(Mth.wrapDegrees(player.getYRot() - rotation.getYaw()))
            + Math.abs(player.getXRot() - rotation.getPitch());
   }

   /** Лучшее копьё в хотбаре: максимум по множителю заряда плюс бонус за «Рывок». */
   public static int findBestHotbarSlot(LocalPlayer player) {
      int bestSlot = -1;
      float bestValue = -1.0F;

      for (int index = 0; index < HOTBAR_SIZE; ++index) {
         ItemStack stack = player.getInventory().getItem(index);
         if (isSpear(stack)) {
            float value = multiplier(path(stack)) + (float)lungeLevel(stack) * 0.1F;
            if (value > bestValue) {
               bestValue = value;
               bestSlot = index;
            }
         }
      }

      return bestSlot;
   }

   public static LivingEntity findTarget(ClientLevel level, double range) {
      double bestDistance = Double.MAX_VALUE;
      LivingEntity closest = null;

      for (Player entity : level.players()) {
         if (isValidTarget(entity)) {
            double distance = CriticalHelper.getStrictDistance(entity);
            if (distance <= range && distance < bestDistance) {
               bestDistance = distance;
               closest = entity;
            }
         }
      }

      return closest;
   }

   public static boolean isValidTarget(LivingEntity entity) {
      if (entity == null || !entity.isAlive()) {
         return false;
      } else if (!(entity instanceof Player player)) {
         return false;
      } else if (player == Minecraft.getInstance().player) {
         return false;
      } else if (AntiBotModule.isBot(player)) {
         return false;
      } else {
         GameProfile gameProfile = player.getGameProfile();
         return !FriendManager.INSTANCE.isFriend(gameProfile.name());
      }
   }
}
