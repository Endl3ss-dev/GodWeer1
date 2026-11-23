package net.skycore.client.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.event.Subscription;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.NumberSetting;

/**
 * Freeze Clip: jump at a wall, hang frozen in the air for a moment, then
 * clip straight through the wall to the first free space behind it.
 *
 * <p>Sequence (driven from {@link PlayerTickEndEvent}, which in SkyCore
 * fires before the player's physics tick):
 * <ol>
 *   <li>Trigger: airborne after a jump, moving/looking into a wall within
 *       reach of the probe, forward input held ({@code Auto}). With Auto
 *       off, enabling the module mid-air freezes immediately.</li>
 *   <li>Frozen: position and velocity are pinned every tick until
 *       {@code Delay} milliseconds pass — or jump is pressed again, which
 *       clips at once.</li>
 *   <li>Clip: the first position behind the wall where the player box no
 *       longer collides (scanned in 5 cm steps up to {@code Distance}) is
 *       teleported to; if there is no exit, the clip is cancelled and the
 *       player simply drops.</li>
 * </ol>
 *
 * <p>This is a position clip: strict movement anticheats can see and
 * reject it. It exists for the servers where it works, not as a legit
 * movement mode — see Slab Pass for the physical ways through.
 */
public final class FreezeClipModule extends ClientModule {
   public static final FreezeClipModule INSTANCE = new FreezeClipModule();

   private enum State {
      IDLE, FROZEN, COOLDOWN
   }

   private final NumberSetting delay = this.add(new NumberSetting("Delay", 1000.0, 0.0, 3000.0, 50.0));
   private final NumberSetting distance = this.add(new NumberSetting("Distance", 3.0, 1.0, 5.0, 0.5));
   private final BooleanSetting auto = this.add(new BooleanSetting("Auto", true));

   private Subscription prePhysicsSubscription;
   private State state = State.IDLE;
   private Vec3 frozenPos;
   private long frozenSinceMillis;
   private int cooldownTicks;
   private boolean jumpWasDown;

   private FreezeClipModule() {
      super("Freeze Clip", "Freezes mid-air at a wall, then clips through it", ModuleCategory.MOVEMENT);
   }

   @Override
   protected void onEnable() {
      this.state = State.IDLE;
      this.frozenPos = null;
      this.cooldownTicks = 0;
      if (this.prePhysicsSubscription != null) {
         this.prePhysicsSubscription.close();
      }
      this.prePhysicsSubscription = SkycoreClient.events().subscribe(PlayerTickEndEvent.class, event -> this.onPrePhysics());
   }

   @Override
   protected void onDisable() {
      this.state = State.IDLE;
      this.frozenPos = null;
      if (this.prePhysicsSubscription != null) {
         this.prePhysicsSubscription.close();
         this.prePhysicsSubscription = null;
      }
   }

   private void onPrePhysics() {
      if (!this.isEnabled()) {
         return;
      }
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player == null || minecraft.level == null || minecraft.gui.screen() != null) {
         this.state = State.IDLE;
         return;
      }

      switch (this.state) {
         case FROZEN -> this.tickFrozen(minecraft, player);
         case COOLDOWN -> {
            if (--this.cooldownTicks <= 0) {
               this.state = State.IDLE;
            }
         }
         case IDLE -> this.tickIdle(minecraft, player);
      }
   }

   private void tickIdle(Minecraft minecraft, LocalPlayer player) {
      if (player.getAbilities().flying || player.isFallFlying() || player.isPassenger()
         || player.isInWater() || player.onGround()) {
         return;
      }
      if (!this.auto.get()) {
         this.beginFreeze(player);
         return;
      }
      Direction facing = player.getDirection();
      if (this.wantsForward(player, facing) && this.wallAhead(minecraft, player, facing)) {
         this.beginFreeze(player);
      }
   }

   private void beginFreeze(LocalPlayer player) {
      this.frozenPos = player.position();
      this.frozenSinceMillis = System.currentTimeMillis();
      this.jumpWasDown = Minecraft.getInstance().options.keyJump.isDown();
      this.pinPlayer(player);
      this.state = State.FROZEN;
   }

   private void tickFrozen(Minecraft minecraft, LocalPlayer player) {
      if (player.getAbilities().flying || player.isFallFlying() || player.isPassenger()) {
         this.state = State.IDLE;
         return;
      }
      this.pinPlayer(player);

      boolean jumpDown = minecraft.options.keyJump.isDown();
      boolean jumpPressed = jumpDown && !this.jumpWasDown;
      this.jumpWasDown = jumpDown;
      boolean delayOver = System.currentTimeMillis() - this.frozenSinceMillis >= this.delay.get().longValue();
      if (jumpPressed || delayOver) {
         this.clipThrough(minecraft, player);
         this.cooldownTicks = 15;
         this.state = State.COOLDOWN;
      }
   }

   private void pinPlayer(LocalPlayer player) {
      if (this.frozenPos != null) {
         player.setPos(this.frozenPos.x, this.frozenPos.y, this.frozenPos.z);
      }
      player.setDeltaMovement(Vec3.ZERO);
      player.fallDistance = 0.0;
   }

   private void clipThrough(Minecraft minecraft, LocalPlayer player) {
      Direction facing = player.getDirection();
      Vec3 start = player.position();
      AABB box = player.getBoundingBox();
      double width = box.maxX - box.minX;
      double height = box.maxY - box.minY;
      double maxDistance = this.distance.get().doubleValue();
      boolean passedSolid = false;
      for (double step = 0.3; step <= maxDistance; step += 0.05) {
         Vec3 candidate = start.add(facing.getStepX() * step, 0.0, facing.getStepZ() * step);
         AABB candidateBox = new AABB(
            candidate.x - width / 2.0, candidate.y, candidate.z - width / 2.0,
            candidate.x + width / 2.0, candidate.y + height, candidate.z + width / 2.0);
         boolean solid = minecraft.level.getBlockCollisions(player, candidateBox).iterator().hasNext();
         if (solid) {
            passedSolid = true;
         } else if (passedSolid) {
            player.setPos(candidate.x, candidate.y, candidate.z);
            player.setDeltaMovement(Vec3.ZERO);
            player.fallDistance = 0.0;
            return;
         }
      }
      // No exit within reach: cancel the clip, the freeze simply ends.
   }

   /** Forward input held, or the player already moves towards the wall. */
   private boolean wantsForward(LocalPlayer player, Direction facing) {
      if (player.input != null && player.input.keyPresses.forward()) {
         return true;
      }
      Vec3 movement = player.getDeltaMovement();
      return movement.x * facing.getStepX() + movement.z * facing.getStepZ() > 0.08;
   }

   /** Any collision in the player's body band right ahead. */
   private boolean wallAhead(Minecraft minecraft, LocalPlayer player, Direction facing) {
      AABB box = player.getBoundingBox();
      double shiftX = facing.getStepX() * 0.7;
      double shiftZ = facing.getStepZ() * 0.7;
      AABB probe = new AABB(box.minX + shiftX, box.minY + 0.1, box.minZ + shiftZ,
         box.maxX + shiftX, box.maxY - 0.1, box.maxZ + shiftZ);
      return minecraft.level.getBlockCollisions(player, probe).iterator().hasNext();
   }
}
