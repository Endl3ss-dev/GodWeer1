package net.skycore.client.module.combat;

import java.util.Comparator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.ActionStop;
import net.skycore.client.combat.AuraRotation;
import net.skycore.client.combat.CriticalHelper;
import net.skycore.client.combat.FreeLookController;
import net.skycore.client.combat.InputPredictor;
import net.skycore.client.combat.MouseSensitivity;
import net.skycore.client.combat.RayTraceHelper;
import net.skycore.client.combat.Rotation;
import net.skycore.client.combat.RotationController;
import net.skycore.client.combat.RotationManager;
import net.skycore.client.combat.ServerRotationManager;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.event.WorldChangeEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.module.ModuleManager;
import net.skycore.client.module.movement.AirStuckModule;
import net.skycore.client.module.movement.AutoJumpModule;
import net.skycore.client.setting.Setting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.util.CaptureLevelProjectionHelper;
import net.skycore.client.util.ScoreboardModeHelper;
import net.skycore.client.setting.BooleanSetting;

public final class AttackAuraModule extends ClientModule {
   private final ModeSetting movementCorrection;
   private final ModeSetting v2Correction;
   private final RockstarAim rockstar = new RockstarAim();
   private static final int ROCKSTAR_PRIORITY = 5;
   private static final String ELYTRA_PATH = "ElytraTarget";
   private enum AimPhase { PHYSICS, FRAME, ATTACK_SLOT }
   private String aimPath;
   private boolean visibleAim;
   private Entity aimedTarget;
   private Rotation ownedRotation;
   private final BooleanSetting clientLook;
   private int lastRotateTick;
   private final AuraRotation rotations;
   private long lastAttackTime;
   private final BooleanSetting hitThroughDoors;
   private final ModeSetting sortMode;
   private final BooleanSetting onlyCriticals;
   private final BooleanSetting smartCriticals;
   private final ModeSetting rotationBehavior;
   private final BooleanSetting breakShield;
   private boolean canCrit;
   private final BooleanSetting releaseShield;
   private final BooleanSetting rayTrace;
   private int skycoreNextAttackTick;
   private final ModeSetting rotationMode = this.add(new ModeSetting("Rotation mode", "ReallyWorld",
      RockstarAim.Mode.alongside("ReallyWorld", "HvH", "Funtime", "HolyWorldLegit")));
   private boolean hitImminent;
   private final NumberSetting pursuitRange;
   private final BooleanSetting bypassWalls;
   private long attackCounter;
   private Entity target;
   private final BooleanSetting disableOnDeath;
   private final BooleanSetting ignoreEating;
   private final NumberSetting attackRange;
   private static final long ATTACK_DELAY_MS = 473L;
   private final BooleanSetting tpsSync;
   private final AuraTargets targets = new AuraTargets();
   private final AuraClicks clicks = new AuraClicks();
   private final AuraBurst burst = new AuraBurst();
   private final AuraSprint sprintReset = new AuraSprint();
   private final AuraCameraJitter jitter = new AuraCameraJitter();
   private final AuraFakeRat fakeRat = new AuraFakeRat();
   private final AuraJumpPeak jumpPeak = new AuraJumpPeak();
   private final BooleanSetting throughWalls;
   private int skycoreTick;

   public boolean canPlayerCritical() {
      Minecraft instance = Minecraft.getInstance();
      LocalPlayer player = instance.player;
      if (player == null) {
         return false;
      }

      if (!player.hasEffect(MobEffects.LEVITATION)) {
         if (!player.hasEffect(MobEffects.BLINDNESS)) {
            if (!player.isInWater()) {
               if (!player.isEyeInFluid(FluidTags.WATER) && !player.isEyeInFluid(FluidTags.LAVA) && !player.getAbilities().flying) {
                  if (!player.isFallFlying() && !player.onClimbable()) {
                     if (!player.isPassenger() && !player.onGround() && player.getDeltaMovement().y < 0.0 && player.fallDistance > 0.0) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean onlyCriticalsEnabled() {
      return !this.usesClickClock() && this.onlyCriticals.get();
   }

   public long attackCounter() {
      return this.attackCounter;
   }

   private double getMaxAimRange() {
      LocalPlayer instance = Minecraft.getInstance().player;
      double value = this.attackDistance();
      return instance != null && instance.isFallFlying() && ElytraTargetModule.INSTANCE.isEnabled() ? Math.max(value, this.pursuitRange.get().doubleValue()) : value;
   }

   private boolean breakShieldPlayer(LocalPlayer player, Player player2) {
      Minecraft instance = Minecraft.getInstance();
      if (instance.gameMode != null && player2.isBlocking()) {
         int axeSlot = this.findAxeSlot(player);
         if (axeSlot >= 0) {
            int selectedSlot = player.getInventory().getSelectedSlot();
            if (axeSlot < 9) {
               player.getInventory().setSelectedSlot(axeSlot);
               instance.gameMode.attack(player, player2);
               player.swing(InteractionHand.MAIN_HAND);
               player.getInventory().setSelectedSlot(selectedSlot);
            } else {
               instance.gameMode.handleContainerInput(player.inventoryMenu.containerId, axeSlot, selectedSlot, ContainerInput.SWAP, player);
               instance.gameMode.attack(player, player2);
               player.swing(InteractionHand.MAIN_HAND);
               instance.gameMode.handleContainerInput(player.inventoryMenu.containerId, axeSlot, selectedSlot, ContainerInput.SWAP, player);
            }
            return true;
         }
      }
      return false;
   }

   public AttackAuraModule() {
      super("Aura", "Attacks nearby targets with silent aim, retained 1.8 bursts, and sprint resets", ModuleCategory.COMBAT);
      this.rotationBehavior = this.add(new ModeSetting("Rotation behavior", "Smooth", "Smooth", "Snaps"));
      this.addSettings(this.clicks.settings);
      this.addSettings(this.burst.settings);
      this.addSettings(this.sprintReset.settings);
      for (Setting<?> option : this.burst.settings) {
         option.visibleWhen(this::usesBurstClicks);
      }
      this.addSettings(this.targets.profile, this.targets.bedWars, this.targets.standard);
      this.movementCorrection = this.add(new ModeSetting("Movement correction", "Free", "Free", "Focused", "Target", "V2"));
      this.v2Correction = this.add(new ModeSetting("V2 correction type", "Silent", "Silent", "Direct"));
      this.v2Correction.visibleWhen(() -> this.movementCorrection.is("V2"));
      this.attackRange = this.add(new NumberSetting("Attack range", "", 3.0, 2.5, 6.0, 0.1, " blocks"));
      this.pursuitRange = this.add(new NumberSetting("Pursuit range", "", 1.5, 0.0, 3.0, 0.1, " blocks"));
      this.sortMode = this.add(new ModeSetting("Sort Mode", "Distance", "Distance", "Health", "Armor", "All"));
      this.disableOnDeath = this.add(new BooleanSetting("Disable on death", true));
      this.ignoreEating = this.add(new BooleanSetting("Don't attack while eating", false));
      this.tpsSync = this.add(new BooleanSetting("TPS Sync", false));
      this.onlyCriticals = this.add(new BooleanSetting("Only crits", true));
      this.smartCriticals = this.add(new BooleanSetting("Smart crits", false));
      this.releaseShield = this.add(new BooleanSetting("Release shield", true));
      this.breakShield = this.add(new BooleanSetting("Break Shield", true));
      this.bypassWalls = this.add(new BooleanSetting("RW wall bypass", false));
      this.rayTrace = this.add(new BooleanSetting("Ray trace", true));
      this.throughWalls = this.add(new BooleanSetting("Through walls", true));
      this.hitThroughDoors = this.add(new BooleanSetting("Hit through doors", false));
      this.clientLook = this.add(new BooleanSetting("Client Look", false));
      this.addSettings(this.jitter.settings);
      this.addSettings(this.fakeRat.settings);
      this.rotations = new AuraRotation();
      this.canCrit = true;
      this.lastRotateTick = -1;
      this.rotationBehavior.visibleWhen(() -> {
         if (!this.rotationMode.is("ReallyWorld")) {
            if (!this.rotationMode.is("Funtime")) {
               return false;
            }
         }

         return true;
      });
      this.bypassWalls.visibleWhen(() -> {
         return this.rotationMode.is("ReallyWorld");
      });
      this.hitThroughDoors.visibleWhen(() -> {
         Boolean get = this.rayTrace.get();
         if (get) {
            Boolean get2 = this.throughWalls.get();
            if (!get2) {
               return true;
            }
         }

         return false;
      });
      this.tpsSync.visibleWhen(() -> !this.usesClickClock());
      this.onlyCriticals.visibleWhen(() -> !this.usesClickClock());
      this.smartCriticals.visibleWhen(() -> !this.usesClickClock() && this.onlyCriticals.get());
      this.clientLook.visibleWhen(() -> !this.rotationMode.is("HolyWorldLegit") && this.rockstarMode() != RockstarAim.Mode.HOLY_WORLD
         && this.rockstarMode() != RockstarAim.Mode.NONE);
      SkycoreClient.events().subscribe(WorldChangeEvent.class, event -> this.resetState());
      SkycoreClient.events().subscribe(PlayerInputEvent.class, this::onPlayerInput);
      SkycoreClient.events().subscribe(PlayerTickEndEvent.class, this::onPreTick);
   }

   /** Living-only view for TargetHUD, ESP, ElytraTarget and other existing consumers. */
   public LivingEntity currentTarget() {
      return this.isEnabled() && this.target instanceof LivingEntity living ? living : null;
   }

   public boolean hasValidTargetForAutoJump() {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      LivingEntity living = this.currentTarget();
      return player != null && mc.level != null && !mc.isPaused() && player.isAlive()
         && living != null && this.isValidTarget(living);
   }

   private boolean shouldBlockAttack(LocalPlayer player) {
      if (ElytraTargetModule.INSTANCE.isEnabled() && player.isFallFlying()) {
         if (player.isUsingItem()) {
            if (!player.isBlocking()) {
               Boolean get = this.ignoreEating.get();
               if (get) {
                  return true;
               }
            }
         }

         return false;
      } else {
         if (player.isUsingItem()) {
            Boolean get2 = this.ignoreEating.get();
            if (get2 && !player.isFallFlying()) {
               return true;
            }
         }

         return false;
      }
   }

   private Entity findTarget() {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null) {
         return null;
      }
      boolean flying = ElytraTargetModule.INSTANCE.isEnabled() && player.isFallFlying();
      Comparator<Entity> order = this.targetOrder(player, flying);
      Entity best = null;
      for (Entity candidate : mc.level.entitiesForRendering()) {
         if (this.isValidTarget(candidate) && (best == null || order.compare(candidate, best) < 0)) {
            best = candidate;
         }
      }
      return best;
   }

   private Comparator<Entity> targetOrder(LocalPlayer player, boolean flying) {
      Comparator<Entity> distance = flying ? Comparator.comparingDouble(player::distanceTo) : Comparator.comparingDouble(CriticalHelper::getStrictDistance);
      // Projectiles have no health or armor. In attribute-based sorts rank them after living targets, then by distance.
      boolean highestArmorFirst = !flying && this.sortMode.is("All");
      Comparator<Entity> armor = Comparator.comparingDouble(e -> e instanceof LivingEntity living
         ? (highestArmorFirst ? -living.getArmorValue() : living.getArmorValue()) : Double.POSITIVE_INFINITY);
      Comparator<Entity> health = Comparator.comparingDouble(e -> e instanceof LivingEntity living
         ? (flying ? this.skycoreHealth(living) : this.getEntityHealth(living)) : Double.POSITIVE_INFINITY);
      Comparator<Entity> order = switch (this.sortMode.get()) {
         case "All" -> armor.thenComparing(health).thenComparing(distance);
         case "Armor" -> armor.thenComparing(distance);
         case "Health" -> health.thenComparing(distance);
         default -> distance;
      };
      return order.thenComparingInt(Entity::getId);
   }

   private RockstarAim.Mode rockstarMode() { return RockstarAim.Mode.from(this.rotationMode.get()); }

   private String rotationPath(LocalPlayer player) {
      if (this.rockstarMode() != RockstarAim.Mode.NONE && player.isFallFlying() && ElytraTargetModule.INSTANCE.isEnabled()) return ELYTRA_PATH;
      return this.rotationMode.get();
   }

   private boolean inputUnavailable(LocalPlayer player, Minecraft mc) {
      return mc.isPaused() || !player.isAlive() || TargetPearlModule.isRotationActive() || CrystalAuraModule.INSTANCE.isRotating()
         || ServerRotationManager.INSTANCE.isRotating() || this.isManualBlockPlacement(player, mc);
   }

   private boolean rockstarBlocked(LocalPlayer player, Minecraft mc, boolean elytra) {
      return this.inputUnavailable(player, mc) || !RotationManager.canClaim(elytra ? 10 : ROCKSTAR_PRIORITY);
   }

   private void updateRotation(boolean ready, AimPhase phase, long now) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null || this.target == null) {
         this.releaseRotationControl();
         return;
      }
      RockstarAim.Mode mode = this.rockstarMode();
      String path = this.rotationPath(player);
      boolean elytra = ELYTRA_PATH.equals(path);
      boolean visible = !elytra && this.clientLookEnabled();
      if (!path.equals(this.aimPath) || visible != this.visibleAim) this.releaseRotationControl();
      if (mode == RockstarAim.Mode.NONE) return; // never fall through to ReallyWorld, including during flight
      if (mode != null && this.rockstarBlocked(player, mc, elytra)) {
         this.releaseRotationControl();
         return;
      }
      this.aimPath = path;
      this.visibleAim = visible;
      if (mode != null && !elytra) {
         if (phase == AimPhase.ATTACK_SLOT || (phase == AimPhase.FRAME) != (mode == RockstarAim.Mode.HOLY_WORLD)) return;
         if (!this.isValidTarget(this.target) || !this.rockstar.begin(mode, this.target, player.tickCount, phase == AimPhase.FRAME, now)) return;
         this.applyRockstar(mode, player, ready, now);
         return;
      }
      // Native modes (including the existing ElytraTarget prediction) keep their original scheduling.
      if (phase == AimPhase.FRAME || !this.isValidTarget(this.target) || TargetPearlModule.isRotationActive() || CrystalAuraModule.INSTANCE.isRotating()) return;
      Rotation previous = RotationManager.INSTANCE.targetRotation();
      this.rotations.tick(this, player, this.target, this.rotationMode.get(), this.rotationBehavior.get(), ready,
         this.bypassWalls.get(), this.breakShield.get(), this.attackDistance());
      if (RotationManager.INSTANCE.targetRotation() != previous) this.rememberAim(player, RotationManager.INSTANCE.targetRotation());
   }

   private void rememberAim(LocalPlayer player, Rotation submitted) {
      this.ownedRotation = submitted;
      this.aimedTarget = this.target;
      this.lastRotateTick = player.tickCount;
   }

   private void applyRockstar(RockstarAim.Mode mode, LocalPlayer player, boolean ready, long now) {
      Vec3 eyes = player.getEyePosition();
      Rotation current = new Rotation(player.getYRot(), player.getXRot());
      if (mode == RockstarAim.Mode.HOLY_WORLD) {
         if (FreeLookController.isActive()) current = new Rotation(FreeLookController.getFreeYaw(), FreeLookController.getFreePitch());
         float partial = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
         Vec3 center = CaptureLevelProjectionHelper.interpolatedPosition(this.target, partial).add(this.target.getBoundingBox().getCenter().subtract(this.target.position()));
         Rotation view = this.rockstar.holy.frame(now, eyes, center, current, MouseSensitivity.getGCDValue());
         if (RotationManager.updateExact(view, ROCKSTAR_PRIORITY, 2, true)) {
            player.yHeadRot = player.yBodyRot = view.getYaw();
            this.rememberAim(player, view);
         }
         return;
      }
      boolean living = this.target instanceof LivingEntity;
      RockstarAim.Geometry g = new RockstarAim.Geometry(eyes, player.getY(), player.getDeltaMovement(), this.target.getBoundingBox(), this.target.getEyePosition(), living);
      boolean inRange = this.isWithinAttackRange(this.target);
      RockstarAim.Turn turn = switch (mode) {
         case SIMPLE, ONE_TICK, REALLY_WORLD -> this.rockstar.point(mode, g, current, rot -> RayTraceHelper.rayTraceEntity(player,
            this.target, rot.getYaw(), rot.getPitch(), this.attackDistance(), !this.throughWalls.get(), this.hitThroughDoors.get()));
         case VON_TAM, FUN_TIME -> this.rockstar.funTime(g, current, ready && inRange, now);
         case SPOOKY_TIME -> this.rockstar.spookyTime(g, current, inRange, !this.target.onGround() && this.target.fallDistance > 0);
         case SLOTH -> this.rockstar.sloth(g, current, inRange, ready);
         case SLIME_WORLD -> this.rockstar.slimeWorld(g, current, inRange, new RockstarAim.SlimeScene(
            !player.hasLineOfSight(this.target), nearFootBlock(this.target), this.target.onGround(), this.target instanceof LivingEntity e && e.isFallFlying(),
            FreeLookController.isActive() ? FreeLookController.getFreePitch() : player.getXRot()), now);
         default -> throw new IllegalStateException("Not a tick rotation: " + mode);
      };
      RotationManager.update(turn.angles(), turn.yawSpeed(), turn.pitchSpeed(), turn.returnSpeed(), turn.returnSpeed(), 2, ROCKSTAR_PRIORITY, this.visibleAim);
      if (RotationManager.INSTANCE.targetRotation() == turn.angles()) this.rememberAim(player, turn.angles());
   }

   // Adapted from Rockstar/src/main/java/moscow/rockstar/util/game/CombatUtility.java (four corners at foot height).
   private static boolean nearFootBlock(Entity entity) {
      AABB b = entity.getBoundingBox();
      double y = entity.getY();
      return !entity.level().getBlockState(BlockPos.containing(b.minX - 0.05, y, b.minZ - 0.05)).isAir()
         || !entity.level().getBlockState(BlockPos.containing(b.maxX + 0.05, y, b.minZ - 0.05)).isAir()
         || !entity.level().getBlockState(BlockPos.containing(b.minX - 0.05, y, b.maxZ + 0.05)).isAir()
         || !entity.level().getBlockState(BlockPos.containing(b.maxX + 0.05, y, b.maxZ + 0.05)).isAir();
   }

   public void onRenderFrame(long now) {
      if (this.isEnabled() && this.rockstarMode() == RockstarAim.Mode.HOLY_WORLD) this.updateRotation(false, AimPhase.FRAME, now);
   }

   private boolean funtimeCameraHitsTarget(LocalPlayer player) {
      if (this.target == null) {
         return false;
      } else if (!player.getInBlockState().is(Blocks.COBWEB) && !this.target.getInBlockState().is(Blocks.COBWEB)) {
         float yRot = FreeLookController.isActive() ? FreeLookController.getFreeYaw() : player.getYRot();
         float xRot = FreeLookController.isActive() ? FreeLookController.getFreePitch() : player.getXRot();
         int value = !this.throughWalls.get() ? 1 : 0;
         int value2 = value != 0 && this.hitThroughDoors.get() ? 1 : 0;
         return RayTraceHelper.rayTraceEntity(player, this.target, yRot, xRot, this.attackDistance() + 1.0, (value != 0), (value2 != 0));
      } else {
         return true;
      }
   }

   public ModeSetting rotationBehavior() {
      return this.rotationBehavior;
   }

   private static double direction(float value, double value2, double value3) {
      if (value2 < 0.0) {
         value += 180.0F;
      }

      float value4 = 1.0F;
      if (value2 < 0.0) {
         value4 = -0.5F;
      } else if (value2 > 0.0) {
         value4 = 0.5F;
      }

      if (value3 > 0.0) {
         value -= 90.0F * value4;
      }

      if (value3 < 0.0) {
         value += 90.0F * value4;
      }

      return Math.toRadians(value);
   }

   public float getAICooldown() {
      Minecraft instance = Minecraft.getInstance();
      LocalPlayer player = instance.player;
      if (player == null) {
         return 0.93F;
      } else {
         ItemStack mainHandItem = player.getMainHandItem();
         if (!mainHandItem.isEmpty() && mainHandItem.getItem() != Items.AIR) {
            return !mainHandItem.is(ItemTags.AXES) && !mainHandItem.is(Items.WOODEN_SHOVEL) ? 0.93F : 0.95F;
         } else {
            return 0.9F;
         }
      }
   }

   private void updateBeforePhysics(PlayerTickEndEvent playerTickEndEvent) {
      this.skycoreTick++;
      if (this.isEnabled()) {
         if (!CrystalAuraModule.INSTANCE.isRotating()) {
            Minecraft instance = Minecraft.getInstance();
            LocalPlayer player = playerTickEndEvent.player();
            if (player != null && instance.level != null) {
               this.jumpPeak.observe(player.tickCount, player.getDeltaMovement().y, player.onGround(), player.fallDistance,
                  this.isValidAttackCondition());
            }
            if (player == null || instance.level == null) {
               this.resetState();
            } else if (this.disableOnDeath.get() && !player.isAlive()) {
               this.setEnabled(false);
            } else if (this.isManualBlockPlacement(player, instance)) {
               this.hitImminent = false;
               if (this.target != null && this.isValidTarget(this.target)) {
                  this.updateRotation(false, AimPhase.PHYSICS, System.nanoTime());
               }
            } else {
               if (this.target == null || !this.isValidTarget(this.target)) {
                  this.target = this.findTarget();
               }

               if (!this.usesClickClock()) this.refreshCanCrit(player);
               if (this.target == null) {
                  this.hitImminent = false;
                  this.releaseRotationControl();
               } else if (!player.isFallFlying()
                  && (!ElytraTargetModule.INSTANCE.isEnabled() || !player.isFallFlying())
                  && !this.throughWalls.get()
                  && !this.hitThroughDoors.get()
                  && !player.hasLineOfSight(this.target)) {
                  this.target = null;
                  this.hitImminent = false;
                  this.releaseRotationControl();
               } else {
                  int value = this.canAttack() && !this.shouldBlockAttack(player) ? 1 : 0;
                  if (this.rockstarMode() != null || this.usesClickClock() || this.rotationMode.is("ReallyWorld")) {
                     this.updateRotation(value != 0, AimPhase.PHYSICS, System.nanoTime());
                  } else {
                     this.onGameUpdate();
                  }

                  this.hitImminent = ((value != 0 && this.isWithinAttackRange(this.target) ? 1 : 0) != 0);
                  if (this.hitImminent) {
                     if (this.target instanceof LivingEntity living) {
                        AutoMaceModule.prepareForHit(living);
                     }
                     if (this.updateAttack()) {
                        this.lastAttackTime = System.currentTimeMillis();
                     }
                  }
               }
            }
         }
      }
   }

   private void moveToPosition(PlayerInputEvent playerInputEvent, Vec3 position, float value) {
      LocalPlayer instance = Minecraft.getInstance().player;
      if (instance != null) {
         double x = position.x - instance.getX();
         double z = position.z - instance.getZ();
         double computedValue = Mth.wrapDegrees((float)(Math.toDegrees(Math.atan2(z, x)) - 90.0));
         float value2 = 0.0F;
         float value3 = 0.0F;
         double mAX_VALUE = Double.MAX_VALUE;

         for (float index = -1.0F; index <= 1.0F; index++) {
            for (float index2 = -1.0F; index2 <= 1.0F; index2++) {
               if (index != 0.0F || index2 != 0.0F) {
                  double computedValue2 = Math.toDegrees(direction(value, index, index2));
                  double computedValue3 = Math.abs(computedValue - Mth.wrapDegrees((float)computedValue2));
                  if (computedValue3 < mAX_VALUE) {
                     mAX_VALUE = computedValue3;
                     value2 = index;
                     value3 = index2;
                  }
               }
            }
         }

         playerInputEvent.setMoveVector(new Vec2(value3, value2));
         Input input = playerInputEvent.keyPresses();
         playerInputEvent.setKeyPresses(new Input(value2 > 0.0F, value2 < 0.0F, value3 > 0.0F, value3 < 0.0F, input.jump(), input.shift(), input.sprint()));
      }
   }

   public double attackDistance() {
      Minecraft instance = Minecraft.getInstance();
      LocalPlayer player = instance.player;
      if (player != null) {
         if (player.isFallFlying() && ElytraTargetModule.INSTANCE.isEnabled()) {
            return ElytraTargetModule.INSTANCE.attackRange.get();
         }
      }

      float killAuraDistance = AirStuckModule.INSTANCE.getKillAuraDistance();
      double result;
      if (killAuraDistance > 0.0F) {
         result = killAuraDistance;
      } else {
         result = this.attackRange.get();
      }

      return result;
   }

   @Override
   protected void onTick() {
   }

   public ModeSetting rotationMode() {
      return this.rotationMode;
   }

   private int findAxeSlot(LocalPlayer player) {

      for (int value = 0; value < 36; value++) {
         Inventory inventory = player.getInventory();
         ItemStack item = inventory.getItem(value);
         if (!item.isEmpty()) {
            if (item.is(ItemTags.AXES)) {
               return value;
            }
         }
      }

      return -1;
   }

   public void onProfileLoaded() {
      if (this.isEnabled()) {
         this.resetState();
      }
   }

   private void releaseRotationControl() {
      if (this.aimPath == null) return;
      RotationManager manager = RotationManager.INSTANCE;
      if (this.ownedRotation != null && manager.targetRotation() == this.ownedRotation && manager.isRotating()) manager.stopRotation();
      this.ownedRotation = null;
      this.aimedTarget = null;
      this.aimPath = null;
      this.lastRotateTick = -1;
      this.rockstar.reset();
      this.rotations.reset();
   }

   private double skycoreHealth(LivingEntity entity) {
      Minecraft instance = Minecraft.getInstance();
      if (entity instanceof Player && instance.getCurrentServer() != null) {
         if (ScoreboardModeHelper.scoreboardMode()) {
            Scoreboard scoreboard = instance.level.getScoreboard();
            Objective displayObjective = scoreboard.getDisplayObjective(DisplaySlot.BELOW_NAME);
            if (displayObjective != null) {
               String scoreboardName = entity.getScoreboardName();
               ScoreAccess orCreatePlayerScore = scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(scoreboardName), displayObjective);
               return orCreatePlayerScore.get();
            }
         }
      }

      return entity.getHealth();
   }

   private boolean passesRayCheck(LocalPlayer player) {
      int inBlockState = !player.getInBlockState().is(Blocks.COBWEB) && (this.target == null || !this.target.getInBlockState().is(Blocks.COBWEB)) ? 0 : 1;
      if (inBlockState != 0) {
         return true;
      }

      float yRot = RotationController.isActive() ? RotationController.getServerYaw() : player.getYRot();
      float xRot = RotationController.isActive() ? RotationController.getServerPitch() : player.getXRot();
      int value = !this.throughWalls.get() ? 1 : 0;
      int value2 = value != 0 && this.hitThroughDoors.get() ? 1 : 0;
      boolean condition = RayTraceHelper.rayTraceEntity(player, this.target, yRot, xRot, this.attackDistance() + 1.0, (value != 0), (value2 != 0));
      return condition || this.isWallBypassAllowed(player);
   }

   private double getEntityArmor(Player player) {
      return player.getArmorValue();
   }

   private boolean isValidTarget(Entity entity) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || !(entity instanceof LivingEntity || entity instanceof Projectile)) {
         return false;
      }
      boolean inRange = ElytraTargetModule.INSTANCE.isEnabled() && player.isFallFlying()
         ? entity.distanceTo(player) <= 106.0F
         : (entity instanceof Projectile || entity.tickCount >= 3) && CriticalHelper.getStrictDistance(entity) < this.getMaxAimRange();
      return inRange && this.targets.allows(player, entity);
   }

   public void onSlowFallingClientTick() {
      LocalPlayer instance = Minecraft.getInstance().player;
      if (CriticalsModule.INSTANCE.usesSlowFallingMode(instance)) {
         this.updateBeforePhysics(new PlayerTickEndEvent(instance));
      }
   }

   private void refreshCanCrit(LocalPlayer player) {
      if (this.target == null) {
         this.canCrit = false;
      } else {
         if (AirStuckModule.INSTANCE.isFrozen()) {
            this.canCrit = true;
         } else {
            if (player.fallDistance > 0.1F) {
               InputPredictor inputPredictor = InputPredictor.simulateLocalPlayer(1);
               if (inputPredictor.onGround()) {
                  this.canCrit = false;
                  return;
               }
            }

            this.canCrit = true;
         }
      }
   }

   public boolean isValidAttackCondition() {
      LocalPlayer instance = Minecraft.getInstance().player;
      if (instance == null) {
         return false;
      }

      if (!instance.hasEffect(MobEffects.LEVITATION)) {
         if (!instance.hasEffect(MobEffects.BLINDNESS) && !instance.isInWater()) {
            if (!instance.isEyeInFluid(FluidTags.WATER)) {
               if (!instance.isEyeInFluid(FluidTags.LAVA)) {
                  if (!instance.getInBlockState().is(Blocks.COBWEB) && !instance.getAbilities().flying && !instance.isFallFlying()) {
                     if (!instance.onClimbable() && !instance.isPassenger()) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private void onPreTick(PlayerTickEndEvent event) {
      if (event != null && event.player() != null) {
         if (!CriticalsModule.INSTANCE.usesSlowFallingMode(event.player())) {
            this.updateBeforePhysics(event);
         }
      }
   }

   private boolean isWallBypassAllowed(LocalPlayer player) {
      if (this.target != null) {
         Boolean get = this.throughWalls.get();
         if (!get) {
            if (player.hasLineOfSight(this.target)) {
               return false;
            }

            if (this.rotationMode.is("ReallyWorld")) {
               Boolean get2 = this.bypassWalls.get();
               if (get2) {
                  return true;
               }
            }

            return false;
         }
      }

      return false;
   }

   private boolean updateAttack() {
      Minecraft instance = Minecraft.getInstance();
      LocalPlayer player = instance.player;
      if (!this.isEnabled()
         || player == null
         || instance.level == null
         || instance.gameMode == null
         || this.target == null
         || (this.rockstarMode() != null && this.rockstarBlocked(player, instance, ELYTRA_PATH.equals(this.aimPath)))
         || !this.isValidTarget(this.target)
         || !this.canAttack()
         || this.shouldBlockAttack(player)) {
         return false;
      }

      if (this.shouldApplyRayCheck(player) && !this.passesRayCheck(player)) {
         return false;
      }

      if (Boolean.TRUE.equals(this.releaseShield.get()) && player.isBlocking()) {
         instance.gameMode.releaseUsingItem(player);
      }

      boolean burst = this.usesBurstClicks();
      // An axe swap per swing makes no sense inside a burst, so the shield breaker stays single-hit only.
      if (!burst && this.breakShield.get() && this.target instanceof Player player2 && player2.isBlocking()) {
         this.breakShieldPlayer(player, player2);
      }
      int swings = burst ? this.burst.attacks() : 1;
      for (int i = 0; i < swings; i++) {
         // Vanilla clears the sprint flag on a knockback hit, so the reset has to precede every swing.
         this.sprintReset.beforeAttack(player);
         instance.gameMode.attack(player, this.target);
         player.swing(InteractionHand.MAIN_HAND);
         this.attackCounter++;
      }
      long now = System.nanoTime();
      if (burst) this.burst.recordBurst(now, this.skycoreTick);
      this.jitter.recordHit(now);
      this.lastAttackTime = System.currentTimeMillis();
      this.skycoreNextAttackTick = this.skycoreTick + 1;
      this.hitImminent = false;
      if (this.jumpPeak.isAtPeak()) this.jumpPeak.consumePeak();
      this.rotations.onAttack();
      return true;
   }

   private boolean shouldApplyRayCheck(LocalPlayer player) {
      if (this.rayTrace.get() && player != null) {
         if (!player.isFallFlying()) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected void onDisable() {
      this.resetState();
   }

   private void fixMovement(PlayerInputEvent playerInputEvent, float value) {
      LocalPlayer instance = Minecraft.getInstance().player;
      if (instance != null) {
         Vec2 vec2 = playerInputEvent.moveVector();
         float y = vec2.y;
         float x = vec2.x;
         if (y != 0.0F || x != 0.0F) {
            double computedValue = Mth.wrapDegrees((float)Math.toDegrees(direction(value, y, x)));
            float yRot = RotationController.isActive() ? RotationController.getServerYaw() : instance.getYRot();
            float value2 = 0.0F;
            float value3 = 0.0F;
            double mAX_VALUE = Double.MAX_VALUE;

            for (float index = -1.0F; index <= 1.0F; index++) {
               for (float index2 = -1.0F; index2 <= 1.0F; index2++) {
                  if (index2 != 0.0F || index != 0.0F) {
                     double computedValue2 = Mth.wrapDegrees((float)Math.toDegrees(direction(yRot, index, index2)));
                     double computedValue3 = Math.abs(computedValue - computedValue2);
                     if (computedValue3 < mAX_VALUE) {
                        mAX_VALUE = computedValue3;
                        value2 = index;
                        value3 = index2;
                     }
                  }
               }
            }

            playerInputEvent.setMoveVector(new Vec2(value3, value2));
            Input input = playerInputEvent.keyPresses();
            playerInputEvent.setKeyPresses(new Input(value2 > 0.0F, value2 < 0.0F, value3 > 0.0F, value3 < 0.0F, input.jump(), input.shift(), input.sprint()));
         }
      }
   }

   public long millisSinceAttack() {
      return System.currentTimeMillis() - this.lastAttackTime;
   }

   private void onPlayerInput(PlayerInputEvent playerInputEvent) {
      if (this.isEnabled()) {
         Minecraft instance = Minecraft.getInstance();
         LocalPlayer player = instance.player;
         if (player != null && instance.level != null) {
            if (this.movementCorrection.is("V2")) {
               if (this.ownsActiveAim(player) && !this.inputUnavailable(player, instance) && this.isValidTarget(this.target)) {
                  // RotationController already applies the task yaw to LocalPlayer for travel and jump physics.
                  float view = FreeLookController.isActive() ? FreeLookController.getFreeYaw() : player.getYRot();
                  RockstarMovement.apply(playerInputEvent, view, player.getYRot(), this.v2Correction.is("Silent"));
               }
               return;
            }
            if (this.rockstarMode() != null && !this.ownsActiveAim(player)) return;
            if (this.target == null || !this.isValidTarget(this.target)) {
               this.target = this.findTarget();
            }

            if (this.target != null) {
               if (!TargetPearlModule.isRotationActive()) {
                  int value = !RotationManager.INSTANCE.isRotating() && (!this.rotationMode.is("Funtime") || !this.movementCorrection.is("Focused")) ? 0 : 1;
                  if (value != 0 && !CrystalAuraModule.INSTANCE.isRotating()) {
                     if (this.movementCorrection.is("Free")) {
                        if (!this.clientLookEnabled()) {
                           float freeYaw = this.isLookMatchedMoveCorrection(player) ? player.getYRot() : FreeLookController.getFreeYaw();
                           this.fixMovement(playerInputEvent, freeYaw);
                        }
                     } else if (this.movementCorrection.is("Focused")) {
                        Vec3 closestVec = CriticalHelper.getClosestVec(this.target);
                        float x = (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(closestVec.z, closestVec.x)) - 90.0);
                        this.fixMovement(playerInputEvent, x);
                     } else if (this.movementCorrection.is("Target")) {
                        if (!RotationManager.INSTANCE.isRotating()) {
                           this.updateRotation(this.canAttack(), AimPhase.ATTACK_SLOT, System.nanoTime());
                        }

                        if (!ActionStop.isStop()) {
                           float yRot = player.getYRot();
                           this.moveToPosition(playerInputEvent, this.target.position(), yRot);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean ownsActiveAim(LocalPlayer player) {
      return this.target != null && this.target == this.aimedTarget && this.rotationPath(player).equals(this.aimPath)
         && RotationManager.INSTANCE.isAimingAt(this.ownedRotation);
   }

   public void onGameUpdate() {
      if (this.isEnabled() && Minecraft.getInstance().player != null && this.target != null) {
         long now = System.nanoTime();
         this.updateRotation(this.usesClickClock() ? this.canAttack() : this.hitImminent, AimPhase.ATTACK_SLOT, now);
         this.updateAttack();
      } else {
         this.releaseRotationControl();
      }
   }

   private boolean isWithinAttackRange(Entity entity) {
      LocalPlayer instance = Minecraft.getInstance().player;
      if (instance != null && entity != null) {
         if (ElytraTargetModule.INSTANCE.isEnabled()) {
            if (instance.isFallFlying()) {
               double distance = instance.distanceTo(entity);
               Double value = ElytraTargetModule.INSTANCE.attackRange.get();
               return distance <= value;
            }
         }

         return CriticalHelper.getStrictDistance(entity) < this.attackDistance();
      } else {
         return false;
      }
   }

   private boolean isBypassWallsBlockedByFluid(LocalPlayer player) {
      if (player.isInWater() || player.isInLava() || player.isEyeInFluid(FluidTags.WATER) || player.isEyeInFluid(FluidTags.LAVA)) {
         return true;
      }

      if (this.target == null) {
         return false;
      }

      if (!this.target.isInWater() && !this.target.isInLava() && !this.target.isEyeInFluid(FluidTags.WATER) && !this.target.isEyeInFluid(FluidTags.LAVA)) {
         Minecraft instance = Minecraft.getInstance();
         if (instance.level == null) {
            return false;
         }

         Vec3 eyePosition = player.getEyePosition();
         Vec3 eyePosition2 = this.target.getEyePosition();
         Vec3 position = eyePosition2.subtract(eyePosition);
         double length = position.length();
         if (length < 0.001) {
            return false;
         }

         Vec3 position2 = position.normalize();

         for (double index = 0.0; index < length; index += 0.5) {
            BlockPos blockPos = BlockPos.containing(eyePosition.add(position2.scale(index)));
            FluidState fluidState = instance.level.getFluidState(blockPos);
            if (fluidState.is(FluidTags.WATER) || fluidState.is(FluidTags.LAVA)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private boolean isLookMatchedMoveCorrection(LocalPlayer player) {
      if (!this.rotationMode.is("Holy World")) {
         if (!this.rotationMode.is("HolyWorldLegit")) {
            if (!player.isFallFlying()) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean packetCriticalsEnabled() {
      ModuleManager moduleManager = SkycoreClient.modules();
      var optional = moduleManager.get(CriticalsModule.class);
      return optional.map(ClientModule::isEnabled).orElse(false);
   }

   public boolean rwWallBypassEnabled() {
      return this.bypassWalls.get();
   }

   public boolean usesBurstClicks() {
      return this.clicks.usesBurst();
   }

   /** The retained 1.8 burst mode ignores vanilla cooldown and critical gating. */
   public boolean usesClickClock() {
      return this.usesBurstClicks();
   }

   /** Sampled only by the local render camera, never by ray checks or the rotation controller. */
   public AuraCameraJitter.Offset cameraJitter() {
      Minecraft mc = Minecraft.getInstance();
      if (!this.isEnabled() || !this.jitter.enabled.get() || mc.player == null || mc.level == null || mc.isPaused() || !mc.player.isAlive()) {
         this.jitter.reset();
         return AuraCameraJitter.Offset.NONE;
      }
      // Pre-physics aim can be sampled after vanilla has already advanced the player tick.
      int ticksSinceAim = mc.player.tickCount - this.lastRotateTick;
      boolean aiming = this.jitter.whileAiming.get() && this.lastRotateTick != -1 && ticksSinceAim >= 0 && ticksSinceAim <= 1
         && this.ownsActiveAim(mc.player) && this.isValidTarget(this.target) && !TargetPearlModule.isRotationActive() && !CrystalAuraModule.INSTANCE.isRotating();
      return this.jitter.sample(System.nanoTime(), aiming);
   }

   /** Applies FakeRat to an already-extracted local render state only; the player and packet rotations are untouched. */
   public void applyFakeRatToRenderState(AvatarRenderState state, boolean thirdPerson) {
      Minecraft mc = Minecraft.getInstance();
      if (state == null || !thirdPerson || !this.isEnabled() || mc.player == null || mc.level == null || mc.isPaused() || !mc.player.isAlive()) {
         this.fakeRat.reset();
         return;
      }
      AuraFakeRat.RenderRotation rotation = this.fakeRat.sample(System.nanoTime(), state.yRot, state.xRot);
      if (rotation.applyYaw()) state.yRot = rotation.yaw();
      if (rotation.applyPitch()) state.xRot = rotation.pitch();
   }

   public boolean isHitImminent() {
      return this.isEnabled() && this.hitImminent;
   }

   @Override
   protected void onEnable() {
      this.resetClickEffects();
      this.lastAttackTime = 0L;
      this.canCrit = true;
      this.lastRotateTick = -1;
      this.rotations.reset();
   }

   private double getEntityHealth(LivingEntity entity) {
      float labelHealth = ScoreboardModeHelper.getLabelHealth(entity);
      if (entity instanceof Player player) {
         double entityArmor = this.getEntityArmor(player) / 20.0;
         return labelHealth * entityArmor;
      } else {
         return labelHealth;
      }
   }

   public boolean clientLookEnabled() {
      return this.rotationMode.is("HolyWorldLegit") || this.rockstarMode() == RockstarAim.Mode.HOLY_WORLD || this.clientLook.get();
   }

   public boolean canAttack() {
      Minecraft instance = Minecraft.getInstance();
      LocalPlayer player = instance.player;
      if (player == null) {
         return false;
      }
      if (this.usesBurstClicks()) {
         // The renamed 1.8 mode retains the old one-burst-per-tick and Anti Kick clock.
         return this.burst.ready(System.nanoTime(), this.skycoreTick) && this.isWithinAttackRange(this.target) && !instance.isPaused()
            && !this.isManualBlockPlacement(player, instance) && !CrystalAuraModule.INSTANCE.isRotating() && !TargetPearlModule.isRotationActive();
      }
      if (ElytraTargetModule.INSTANCE.isEnabled() && player.isFallFlying() && this.target != null) {
         return this.isWithinAttackRange(this.target) && player.getAttackStrengthScale(1.5F) > 0.92F && this.skycoreTick >= this.skycoreNextAttackTick;
      }

      float value = this.tpsSync.get() ? 1.0F : 1.5F;
      int attackStrengthScale = this.millisSinceAttack() >= ATTACK_DELAY_MS && player.getAttackStrengthScale(value) > this.getAICooldown() ? 1 : 0;
      // Deflecting a projectile cannot crit; the normal cooldown, reach and ray checks still apply.
      if (this.target instanceof Projectile) {
         return attackStrengthScale != 0;
      }
      if (this.shouldWaitForJumpPeak(player)) {
         // The apex is the single 1.12 hit window; Only crits does not veto this explicitly requested hit.
         return attackStrengthScale != 0 && this.jumpPeak.isAtPeak();
      }
      int value2 = !instance.options.keyJump.isDown() && player.onGround() ? 0 : 1;
      if (CriticalHelper.isJumpBlockedByCeiling()) {
         return attackStrengthScale != 0;
      } else if (AirStuckModule.INSTANCE.isFrozen()) {
         return attackStrengthScale != 0;
      } else {
         return this.packetCriticalsEnabled()
            ? attackStrengthScale != 0
               && (
                  !this.onlyCriticals.get()
                     || !this.isValidAttackCondition()
                     || this.smartCriticals.get() && value2 == 0
                     || this.canPlayerCritical()
                     || CriticalsModule.INSTANCE.allowsAuraCritical(player)
               )
            : attackStrengthScale != 0
               && (
                  !this.onlyCriticals.get()
                     || !this.isValidAttackCondition()
                     || this.smartCriticals.get() && value2 == 0
                     || this.canPlayerCritical() && this.canCrit
               );
      }
   }

   private boolean shouldWaitForJumpPeak(LocalPlayer player) {
      if (!this.clicks.mode.is("1.12") || !(this.target instanceof LivingEntity)) {
         return false;
      }
      if (this.jumpPeak.isJumpArcActive()) {
         return true;
      }
      return AutoJumpModule.INSTANCE.isEnabled() && player.onGround() && this.hasValidTargetForAutoJump()
         && this.isValidAttackCondition() && !CriticalHelper.isJumpBlockedByCeiling();
   }

   private void bypassBlocks(LocalPlayer player, LivingEntity entity) {
      Minecraft instance = Minecraft.getInstance();
      if (instance.level != null && player.connection != null) {
         Vec3 eyePosition = player.getEyePosition();
         Vec3 eyePosition2 = entity.getEyePosition();
         Vec3 position = eyePosition2.subtract(eyePosition);
         double length = position.length();
         if (!(length < 0.001)) {
            Vec3 position2 = position.normalize();

            for (double index = 0.0; index < length; index += 0.5) {
               BlockPos blockPos = BlockPos.containing(eyePosition.add(position2.scale(index)));
               if (!instance.level.getBlockState(blockPos).isAir()) {
                  player.connection.send(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, blockPos, Direction.UP));
                  player.connection.send(new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, blockPos, Direction.UP));
               }
            }
         }
      }
   }

   private boolean isManualBlockPlacement(LocalPlayer player, Minecraft minecraft) {
      if (minecraft.gui.screen() == null) {
         if (minecraft.options != null) {
            if (minecraft.options.keyUse.isDown()) {
               if (player.isFallFlying()) {
                  return false;
               }

               if (!(player.getMainHandItem().getItem() instanceof BlockItem)) {
                  return false;
               }

               if (minecraft.hitResult != null) {
                  Type type = minecraft.hitResult.getType();
                  if (type == Type.BLOCK) {
                     return true;
                  }
               }

               return false;
            }
         }
      }

      return false;
   }

   private void resetClickEffects() {
      this.burst.reset();
      this.jitter.reset();
      this.fakeRat.reset();
      this.jumpPeak.reset();
   }

   /** Missing keys in an older profile must not inherit the previous profile's live effects or Rockstar modes. */
   public void prepareProfileLoad() {
      this.resetState();
      this.rotationMode.reset();
      this.movementCorrection.reset();
      this.v2Correction.reset();
      for (Setting<?> option : this.clicks.settings) option.reset();
      for (Setting<?> option : this.burst.settings) option.reset();
      for (Setting<?> option : this.sprintReset.settings) option.reset();
      for (Setting<?> option : this.jitter.settings) option.reset();
      for (Setting<?> option : this.fakeRat.settings) option.reset();
   }

   private void resetState() {
      this.resetClickEffects();
      this.target = null;
      this.hitImminent = false;
      this.canCrit = false;
      this.lastRotateTick = -1;
      this.releaseRotationControl();
   }
}
