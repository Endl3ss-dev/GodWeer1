// Adapted from Prestige ExtendedReach (dev.zprestige.prestige.eJ / S0314)
package net.skycore.client.module.combat;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.SpearHelper;
import net.skycore.client.config.FriendManager;
import net.skycore.client.event.WorldChangeEvent;
import net.skycore.client.mixin.MinecraftGetRightClickDelayAccessor;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.MultiSelectSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.util.UseItemHelper;

public final class ExtendedReachModule extends ClientModule {
   public static final ExtendedReachModule INSTANCE = new ExtendedReachModule();

   private static final double EXTENDED_RAY_LENGTH = 4.75;
   private static final double MAX_ENTITY_DISTANCE_SQR = 36.0;
   private static final double MAX_FALL_DISTANCE = 1.5;

   private final NumberSetting chance = this.add(
      new NumberSetting("Chance", "Rolls this chance on each extended-reach hit (%).", 100.0, 0.0, 100.0, 1.0, "%")
   );
   private final MultiSelectSetting weapons = this.add(
      new MultiSelectSetting("Weapons", List.of("Sword", "Axe", "Mace"), Set.of("Sword", "Axe", "Mace"))
   );
   private final BooleanSetting worksWithTriggerbot = this.add(
      new BooleanSetting("Works With Triggerbot", "Lets Triggerbot fire on extended-reach targets.", true)
   );

   private final MutableBlockPos rayBlockPos = new MutableBlockPos();
   private Set<String> cachedWeapons;
   private boolean allowSword = true;
   private boolean allowAxe = true;
   private boolean allowMace = true;
   private int previousSlot = -1;
   private int swapBackTicks;
   private boolean suppressNextHeadHook;

   public ExtendedReachModule() {
      super("Extended Reach", "Increases reach by attribute swapping to spear", ModuleCategory.COMBAT);
      SkycoreClient.events().subscribe(WorldChangeEvent.class, this::onWorldChange);
   }

   @Override
   protected void onEnable() {
      this.previousSlot = -1;
      this.swapBackTicks = 0;
      this.suppressNextHeadHook = false;
   }

   @Override
   protected void onDisable() {
      this.restorePreviousSlot();
      this.swapBackTicks = 0;
      this.suppressNextHeadHook = false;
   }

   private void onWorldChange(WorldChangeEvent event) {
      this.previousSlot = -1;
      this.swapBackTicks = 0;
      this.suppressNextHeadHook = false;
   }

   @Override
   protected void onTick() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null || mc.level == null) {
         this.previousSlot = -1;
         this.swapBackTicks = 0;
         return;
      }

      if (this.swapBackTicks == 0 && this.previousSlot != -1) {
         UseItemHelper.switchToHotbarSlot(this.previousSlot);
         this.previousSlot = -1;
      } else if (this.swapBackTicks > 0) {
         this.swapBackTicks--;
      }
   }

   public boolean canTriggerbotAttack() {
      return this.isEnabled() && this.worksWithTriggerbot.get();
   }

   public void onStartAttack() {
      if (this.suppressNextHeadHook || !this.isEnabled()) {
         return;
      }
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null || mc.gameMode == null) {
         return;
      }
      this.attemptSwapForExtendedHit(mc, player);
   }

   public boolean tryTriggerbotAttack(Minecraft mc, LocalPlayer player) {
      if (!this.canTriggerbotAttack() || player == null || mc.level == null || mc.gameMode == null) {
         return false;
      }
      if (!this.attemptSwapForExtendedHit(mc, player)) {
         return false;
      }
      this.suppressNextHeadHook = true;
      try {
         MinecraftGetRightClickDelayAccessor accessor = (MinecraftGetRightClickDelayAccessor)mc;
         accessor.skycore$setMissTime(0);
         return accessor.skycore$startAttack();
      } finally {
         this.suppressNextHeadHook = false;
      }
   }

   private boolean attemptSwapForExtendedHit(Minecraft mc, LocalPlayer player) {
      if (this.swapBackTicks > 0 || this.previousSlot != -1 || player.fallDistance > MAX_FALL_DISTANCE) {
         return false;
      }
      if (!this.isAllowedWeapon(player.getMainHandItem())) {
         return false;
      }
      int spearSlot = findSpearSlot(player);
      if (spearSlot == -1) {
         return false;
      }
      int selectedSlot = player.getInventory().getSelectedSlot();
      if (selectedSlot == spearSlot) {
         return false;
      }
      LivingEntity target = this.findExtendedTarget(mc, player);
      if (target == null) {
         return false;
      }

      double rollPct = this.chance.get();
      if (rollPct < 100.0 && (rollPct <= 0.0 || ThreadLocalRandom.current().nextDouble(100.0) > rollPct)) {
         this.swapBackTicks = 1;
         return false;
      }

      this.previousSlot = selectedSlot;
      UseItemHelper.switchToHotbarSlot(spearSlot);
      this.swapBackTicks = 1;
      return SpearHelper.isSpear(player.getMainHandItem());
   }

   public LivingEntity findExtendedTarget(Minecraft mc, LocalPlayer player) {
      if (!this.isEnabled()
         || player == null
         || mc.level == null
         || mc.gameMode == null
         || player.isUsingItem()
         || !player.isAlive()
         || player.isSpectator()) {
         return null;
      }
      if (findSpearSlot(player) == -1) {
         return null;
      }
      if (player.getAttackStrengthScale(0.5F) < 1.0F) {
         return null;
      }
      if (mc.hitResult instanceof EntityHitResult) {
         return null;
      }

      double ex = player.getX();
      double ey = player.getEyeY();
      double ez = player.getZ();

      float yawRad = -player.getYRot() * ((float)Math.PI / 180.0F) - (float)Math.PI;
      float pitchRad = -player.getXRot() * ((float)Math.PI / 180.0F);
      float cosPitch = -Mth.cos(pitchRad);
      double dx = Mth.sin(yawRad) * cosPitch * EXTENDED_RAY_LENGTH;
      double dy = Mth.sin(pitchRad) * EXTENDED_RAY_LENGTH;
      double dz = Mth.cos(yawRad) * cosPitch * EXTENDED_RAY_LENGTH;

      double endX = ex + dx;
      double endY = ey + dy;
      double endZ = ez + dz;

      ClientLevel level = mc.level;
      if (!isRayUnobstructed(level, ex, ey, ez, endX, endY, endZ, this.rayBlockPos)) {
         return null;
      }

      List<? extends Player> players = level.players();
      Player closest = null;
      double bestDistSqr = Double.MAX_VALUE;
      String selfName = player.getGameProfile().name();

      for (int i = 0, size = players.size(); i < size; i++) {
         Player candidate = players.get(i);
         if (candidate == null
            || candidate == player
            || !candidate.isAlive()
            || candidate.isSpectator()
            || candidate.isInvulnerable()
            || AntiBotModule.isBot(candidate)) {
            continue;
         }
         String candidateName = candidate.getGameProfile().name();
         if (candidateName.equalsIgnoreCase(selfName) || FriendManager.INSTANCE.isFriend(candidateName)) {
            continue;
         }

         double cdx = candidate.getX() - player.getX();
         double cdy = candidate.getY() - player.getY();
         double cdz = candidate.getZ() - player.getZ();
         double distSqr = cdx * cdx + cdy * cdy + cdz * cdz;
         if (distSqr > MAX_ENTITY_DISTANCE_SQR || distSqr >= bestDistSqr) {
            continue;
         }

         AABB box = candidate.getBoundingBox();
         if (!intersectsSegment(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, ex, ey, ez, dx, dy, dz)) {
            continue;
         }

         if (!isRayUnobstructed(level, ex, ey, ez, candidate.getX(), candidate.getEyeY(), candidate.getZ(), this.rayBlockPos)) {
            continue;
         }

         bestDistSqr = distSqr;
         closest = candidate;
      }

      return closest;
   }

   private boolean isAllowedWeapon(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return false;
      }
      Set<String> selected = this.weapons.get();
      if (selected != this.cachedWeapons) {
         this.cachedWeapons = selected;
         this.allowSword = selected.contains("Sword");
         this.allowAxe = selected.contains("Axe");
         this.allowMace = selected.contains("Mace");
      }
      if (this.allowSword && stack.is(ItemTags.SWORDS)) {
         return true;
      }
      if (this.allowAxe && stack.is(ItemTags.AXES)) {
         return true;
      }
      return this.allowMace && stack.is(Items.MACE);
   }

   private static int findSpearSlot(LocalPlayer player) {
      for (int slot = 0; slot < SpearHelper.HOTBAR_SIZE; slot++) {
         if (SpearHelper.isSpear(player.getInventory().getItem(slot))) {
            return slot;
         }
      }
      return -1;
   }

   private void restorePreviousSlot() {
      int slot = this.previousSlot;
      this.previousSlot = -1;
      if (slot >= 0 && slot < SpearHelper.HOTBAR_SIZE && Minecraft.getInstance().player != null) {
         UseItemHelper.switchToHotbarSlot(slot);
      }
   }

   private static boolean isRayUnobstructed(
      ClientLevel level,
      double x0,
      double y0,
      double z0,
      double x1,
      double y1,
      double z1,
      MutableBlockPos pos
   ) {
      int bx = Mth.floor(x0);
      int by = Mth.floor(y0);
      int bz = Mth.floor(z0);
      int endBx = Mth.floor(x1);
      int endBy = Mth.floor(y1);
      int endBz = Mth.floor(z1);

      double dx = x1 - x0;
      double dy = y1 - y0;
      double dz = z1 - z0;

      int stepX = dx > 0.0 ? 1 : (dx < 0.0 ? -1 : 0);
      int stepY = dy > 0.0 ? 1 : (dy < 0.0 ? -1 : 0);
      int stepZ = dz > 0.0 ? 1 : (dz < 0.0 ? -1 : 0);

      double tDeltaX = stepX == 0 ? Double.MAX_VALUE : Math.abs(1.0 / dx);
      double tDeltaY = stepY == 0 ? Double.MAX_VALUE : Math.abs(1.0 / dy);
      double tDeltaZ = stepZ == 0 ? Double.MAX_VALUE : Math.abs(1.0 / dz);

      double tMaxX = stepX > 0 ? (bx + 1.0 - x0) * tDeltaX : (stepX < 0 ? (x0 - bx) * tDeltaX : Double.MAX_VALUE);
      double tMaxY = stepY > 0 ? (by + 1.0 - y0) * tDeltaY : (stepY < 0 ? (y0 - by) * tDeltaY : Double.MAX_VALUE);
      double tMaxZ = stepZ > 0 ? (bz + 1.0 - z0) * tDeltaZ : (stepZ < 0 ? (z0 - bz) * tDeltaZ : Double.MAX_VALUE);

      for (int steps = 0; steps < 24; steps++) {
         pos.set(bx, by, bz);
         BlockState state = level.getBlockState(pos);
         if (!state.isAir()) {
            VoxelShape shape = state.getCollisionShape(level, pos);
            if (!shape.isEmpty()) {
               AABB bounds = shape.bounds();
               if (intersectsSegment(
                  bx + bounds.minX,
                  by + bounds.minY,
                  bz + bounds.minZ,
                  bx + bounds.maxX,
                  by + bounds.maxY,
                  bz + bounds.maxZ,
                  x0,
                  y0,
                  z0,
                  dx,
                  dy,
                  dz
               )) {
                  return false;
               }
            }
         }

         if (bx == endBx && by == endBy && bz == endBz) {
            return true;
         }

         if (tMaxX < tMaxY) {
            if (tMaxX < tMaxZ) {
               if (tMaxX > 1.0) {
                  return true;
               }
               bx += stepX;
               tMaxX += tDeltaX;
            } else {
               if (tMaxZ > 1.0) {
                  return true;
               }
               bz += stepZ;
               tMaxZ += tDeltaZ;
            }
         } else if (tMaxY < tMaxZ) {
            if (tMaxY > 1.0) {
               return true;
            }
            by += stepY;
            tMaxY += tDeltaY;
         } else {
            if (tMaxZ > 1.0) {
               return true;
            }
            bz += stepZ;
            tMaxZ += tDeltaZ;
         }
      }

      return true;
   }

   private static boolean intersectsSegment(
      double minX,
      double minY,
      double minZ,
      double maxX,
      double maxY,
      double maxZ,
      double ox,
      double oy,
      double oz,
      double dx,
      double dy,
      double dz
   ) {
      double tMin = 0.0;
      double tMax = 1.0;

      if (Math.abs(dx) < 1.0E-9) {
         if (ox < minX || ox > maxX) {
            return false;
         }
      } else {
         double inv = 1.0 / dx;
         double t1 = (minX - ox) * inv;
         double t2 = (maxX - ox) * inv;
         if (t1 > t2) {
            double tmp = t1;
            t1 = t2;
            t2 = tmp;
         }
         if (t1 > tMin) {
            tMin = t1;
         }
         if (t2 < tMax) {
            tMax = t2;
         }
         if (tMin > tMax) {
            return false;
         }
      }

      if (Math.abs(dy) < 1.0E-9) {
         if (oy < minY || oy > maxY) {
            return false;
         }
      } else {
         double inv = 1.0 / dy;
         double t1 = (minY - oy) * inv;
         double t2 = (maxY - oy) * inv;
         if (t1 > t2) {
            double tmp = t1;
            t1 = t2;
            t2 = tmp;
         }
         if (t1 > tMin) {
            tMin = t1;
         }
         if (t2 < tMax) {
            tMax = t2;
         }
         if (tMin > tMax) {
            return false;
         }
      }

      if (Math.abs(dz) < 1.0E-9) {
         if (oz < minZ || oz > maxZ) {
            return false;
         }
      } else {
         double inv = 1.0 / dz;
         double t1 = (minZ - oz) * inv;
         double t2 = (maxZ - oz) * inv;
         if (t1 > t2) {
            double tmp = t1;
            t1 = t2;
            t2 = tmp;
         }
         if (t1 > tMin) {
            tMin = t1;
         }
         if (t2 < tMax) {
            tMax = t2;
         }
         if (tMin > tMax) {
            return false;
         }
      }

      return tMax >= 0.0 && tMin <= 1.0;
   }
}
