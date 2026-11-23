package net.skycore.client.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.event.PlayerTickStartEvent;
import net.skycore.client.event.Subscription;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;

/**
 * Slab Pass: crosses "slab - empty gap - slab" barriers (total height two
 * blocks) and plain two-block walls where placing blocks is not possible.
 *
 * <p>Modes:
 * <ul>
 *   <li>{@code Over} — walking into a wall whose top is above a normal jump
 *       (1.25 blocks) but at most ~2.7 blocks high applies a single vertical
 *       impulse sized to clear that top; if the vanilla jump overwrote the
 *       impulse in the same tick it is re-applied once after the tick.</li>
 *   <li>{@code Through} — a one-block-high gap between two slabs is crossed
 *       crawling: the swimming pose (0.6 block high) is forced while the gap
 *       pattern is ahead or the player still cannot stand up. Once inside,
 *       vanilla itself keeps the crawl pose, because standing does not fit.
 *       This needs a modern (1.14+) server core; 1.8 backends have no
 *       crawling and will not accept it.</li>
 *   <li>{@code Auto} — uses Through when the slab-gap-slab pattern is
 *       detected ahead, otherwise Over.</li>
 * </ul>
 *
 * <p>Note SkyCore's swapped event names: {@link PlayerTickEndEvent} fires
 * before the player's physics tick, {@link PlayerTickStartEvent} at its tail.
 */
public final class SlabPassModule extends ClientModule {
   public static final SlabPassModule INSTANCE = new SlabPassModule();

   private final ModeSetting mode = this.add(new ModeSetting("Mode", "Auto", "Auto", "Over", "Through"));
   private final NumberSetting boost = this.add(new NumberSetting("Boost", 1.0, 0.8, 1.3, 0.05));

   private Subscription prePhysicsSubscription;
   private Subscription tickTailSubscription;
   private boolean boostPending;
   private double pendingVelocity;
   private boolean crawling;

   private SlabPassModule() {
      super("Slab Pass", "Crosses slab-gap-slab barriers over the top or through the gap", ModuleCategory.MOVEMENT);
      this.boost.visibleWhen(() -> !this.mode.is("Through"));
   }

   @Override
   protected void onEnable() {
      this.boostPending = false;
      this.crawling = false;
      if (this.prePhysicsSubscription != null) {
         this.prePhysicsSubscription.close();
      }
      this.prePhysicsSubscription = SkycoreClient.events().subscribe(PlayerTickEndEvent.class, event -> this.onPrePhysics());
      if (this.tickTailSubscription != null) {
         this.tickTailSubscription.close();
      }
      this.tickTailSubscription = SkycoreClient.events().subscribe(PlayerTickStartEvent.class, event -> this.onTickTail());
   }

   @Override
   protected void onDisable() {
      this.boostPending = false;
      this.crawling = false;
      if (this.prePhysicsSubscription != null) {
         this.prePhysicsSubscription.close();
         this.prePhysicsSubscription = null;
      }
      if (this.tickTailSubscription != null) {
         this.tickTailSubscription.close();
         this.tickTailSubscription = null;
      }
   }

   private void onPrePhysics() {
      if (!this.isEnabled()) {
         return;
      }
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null || minecraft.level == null || minecraft.gui.screen() != null) {
         return;
      }
      if (player.getAbilities().flying || player.isFallFlying() || player.isPassenger() || player.isInWater()) {
         this.crawling = false;
         return;
      }

      Direction facing = player.getDirection();
      boolean wantsForward = this.wantsForward(player, facing);

      if (!this.mode.is("Over")) {
         boolean engaged = this.hasSlit(minecraft, player, facing) && wantsForward
            || this.crawling && !this.canStand(minecraft, player);
         if (engaged) {
            if (player.onGround()) {
               player.setPose(Pose.SWIMMING);
               player.refreshDimensions();
               this.crawling = true;
            }
            return;
         }
         this.crawling = false;
      }

      if (!this.mode.is("Through")) {
         this.tryBoost(minecraft, player, facing, wantsForward);
      }
   }

   private void onTickTail() {
      if (!this.boostPending) {
         return;
      }
      this.boostPending = false;
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null || player.onGround()) {
         return;
      }
      // The vanilla jump may have replaced our impulse with its own 0.42 in
      // the same tick; restore the stronger one once, right after the tick.
      if (player.getDeltaMovement().y < this.pendingVelocity - 0.12) {
         Vec3 movement = player.getDeltaMovement();
         player.setDeltaMovement(movement.x, this.pendingVelocity, movement.z);
      }
   }

   private void tryBoost(Minecraft minecraft, LocalPlayer player, Direction facing, boolean wantsForward) {
      if (!player.onGround() || !wantsForward) {
         return;
      }
      double top = this.obstacleTop(minecraft, player, facing);
      if (Double.isNaN(top)) {
         return;
      }
      double clearance = top - player.getY();
      if (clearance <= 1.3 || clearance > 2.7) {
         return;
      }
      double velocity = Math.sqrt(0.165 * (clearance + 0.18)) * this.boost.get().doubleValue();
      velocity = Mth.clamp(velocity, 0.5, 0.9);
      Vec3 movement = player.getDeltaMovement();
      player.setDeltaMovement(movement.x, velocity, movement.z);
      this.pendingVelocity = velocity;
      this.boostPending = true;
   }

   /** Forward input held, or the player already moves towards the wall. */
   private boolean wantsForward(LocalPlayer player, Direction facing) {
      if (player.input != null && player.input.keyPresses.forward()) {
         return true;
      }
      Vec3 movement = player.getDeltaMovement();
      return movement.x * facing.getStepX() + movement.z * facing.getStepZ() > 0.08;
   }

   /**
    * Detects the slab-gap-slab profile in the column ahead: collision low
    * (bottom slab), none in the middle (the empty gap), collision high
    * (the slab above the gap).
    */
   private boolean hasSlit(Minecraft minecraft, LocalPlayer player, Direction facing) {
      double feetY = player.getY();
      boolean lower = this.collides(minecraft, player, facing, feetY + 0.02, feetY + 0.48);
      boolean middle = this.collides(minecraft, player, facing, feetY + 0.52, feetY + 1.48);
      boolean upper = this.collides(minecraft, player, facing, feetY + 1.52, feetY + 1.98);
      return lower && !middle && upper;
   }

   /** Highest collision top in the column ahead, or NaN when there is none. */
   private double obstacleTop(Minecraft minecraft, LocalPlayer player, Direction facing) {
      AABB probe = this.columnProbe(player, facing, player.getY() + 0.05, player.getY() + 3.0);
      double top = Double.NaN;
      for (var shape : minecraft.level.getBlockCollisions(player, probe)) {
         for (AABB box : shape.toAabbs()) {
            if (Double.isNaN(top) || box.maxY > top) {
               top = box.maxY;
            }
         }
      }
      return top;
   }

   private boolean collides(Minecraft minecraft, LocalPlayer player, Direction facing, double minY, double maxY) {
      AABB probe = this.columnProbe(player, facing, minY, maxY);
      return minecraft.level.getBlockCollisions(player, probe).iterator().hasNext();
   }

   /** The player's own box shifted towards the wall, with the Y band replaced. */
   private AABB columnProbe(LocalPlayer player, Direction facing, double minY, double maxY) {
      AABB box = player.getBoundingBox();
      double shiftX = facing.getStepX() * 0.7;
      double shiftZ = facing.getStepZ() * 0.7;
      return new AABB(box.minX + shiftX, minY, box.minZ + shiftZ,
         box.maxX + shiftX, maxY, box.maxZ + shiftZ);
   }

   private boolean canStand(Minecraft minecraft, LocalPlayer player) {
      AABB standing = player.getDimensions(Pose.STANDING).makeBoundingBox(player.position());
      return minecraft.level.noCollision(player, standing);
   }

   public String suffix() {
      return this.mode.get();
   }
}
