package net.skycore.client.module.movement;

import net.minecraft.client.player.LocalPlayer;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.RotationController;
import net.skycore.client.event.ClientTickEvent;
import net.skycore.client.event.PacketReceiveEvent;
import net.skycore.client.event.PacketSendEvent;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.event.PlayerTickStartEvent;
import net.skycore.client.movement.speed.AlphaDlcSpeed;
import net.skycore.client.movement.speed.LegacySkyCoreSpeed;
import net.skycore.client.movement.speed.NuclearSpeed;
import net.skycore.client.movement.speed.PhobiaSpeed;
import net.skycore.client.movement.speed.PrivateSpeed;
import net.skycore.client.movement.speed.ReleonSpeed;
import net.skycore.client.movement.speed.RockstarSpeed;
import net.skycore.client.movement.speed.SpeedFlyingSpeed;
import net.skycore.client.movement.speed.SpeedHandler;
import net.skycore.client.movement.speed.SpeedTimer;
import net.skycore.client.movement.speed.SwerveSpeed;
import net.skycore.client.movement.speed.WildSpeed;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.util.SliderSetting;

/**
 * Speed-моды, перенесённые из клиентов-источников один-в-один.
 * Имя режима = клиент-источник + его собственный режим.
 */
public final class SpeedModule extends ClientModule {
   public static final SpeedModule INSTANCE = new SpeedModule();

   public static final String NUCLEAR_COLLISION = "Nuclear [Collision]";
   public static final String NUCLEAR_MATRIX = "Nuclear [Matrix]";
   public static final String NUCLEAR_META_HVH = "Nuclear [MetaHvH]";
   public static final String NUCLEAR_HOLY_WORLD = "Nuclear [HolyWorld]";

   public static final String ROCKSTAR_ENTITIES = "Rockstar [Сущности]";

   public static final String PHOBIA_VANILLA = "Phobia [Vanilla]";
   public static final String PHOBIA_GRIM = "Phobia [Grim]";
   public static final String PHOBIA_FUNTIME = "Phobia [FunTime]";
   public static final String PHOBIA_FUNTIME_ELYTRA = "Phobia [Funtime Elytra]";
   public static final String PHOBIA_HOLY_WORLD = "Phobia [HolyWorld]";

   public static final String ALPHADLC_RW = "AlphaDLC [RW]";
   public static final String ALPHADLC_COLLISION = "AlphaDLC [Collision]";

   public static final String RELEON_VANILLA = "Releon [Vanilla]";
   public static final String RELEON_GRIM = "Releon [Grim]";
   public static final String RELEON_REALLY_WORLD = "Releon [ReallyWorld]";
   public static final String RELEON_HOLY_WORLD = "Releon [HolyWorld]";
   public static final String RELEON_FUNTIME = "Releon [FunTime]";
   public static final String RELEON_SPOOKY_TIME = "Releon [SpookyTime]";

   public static final String LEGACY_RW = "SkyCoreLegacy [RW]";
   public static final String LEGACY_COLLISION = "SkyCoreLegacy [Collision]";

   public static final String SWERVE_MULTIPLIER = "Swerve [Множитель]";

   public static final String PRIVATE_HVH = "Private [HvH]";
   public static final String PRIVATE_ENTITY = "Private [Entity]";
   public static final String PRIVATE_SP_DUEL = "Private [SP-DUEL]";
   public static final String PRIVATE_NCP = "Private [NCP]";
   public static final String PRIVATE_MATRIX = "Private [Matrix703]";
   public static final String PRIVATE_AAC = "Private [AAC5]";

   public static final String WILD_VANILLA = "Wild [Vanilla]";
   public static final String WILD_ST_DUEL = "Wild [ST duel]";
   public static final String WILD_HW = "Wild [HW]";
   public static final String WILD_ARES_ENTITY = "Wild [Ares-Entity]";
   public static final String WILD_GRIM_ENTITY = "Wild [Grim-Entity]";
   public static final String WILD_TARGET_STRAFE = "Wild [TargetStrafe]";

   private static final String[] MODES = {
      NUCLEAR_COLLISION, NUCLEAR_MATRIX, NUCLEAR_META_HVH, NUCLEAR_HOLY_WORLD,
      ROCKSTAR_ENTITIES,
      PHOBIA_VANILLA, PHOBIA_GRIM, PHOBIA_FUNTIME, PHOBIA_FUNTIME_ELYTRA, PHOBIA_HOLY_WORLD,
      ALPHADLC_RW, ALPHADLC_COLLISION,
      RELEON_VANILLA, RELEON_GRIM, RELEON_REALLY_WORLD, RELEON_HOLY_WORLD, RELEON_FUNTIME, RELEON_SPOOKY_TIME,
      LEGACY_RW, LEGACY_COLLISION,
      SWERVE_MULTIPLIER,
      PRIVATE_HVH, PRIVATE_ENTITY, PRIVATE_SP_DUEL, PRIVATE_NCP, PRIVATE_MATRIX, PRIVATE_AAC,
      WILD_VANILLA, WILD_ST_DUEL, WILD_HW, WILD_ARES_ENTITY, WILD_GRIM_ENTITY, WILD_TARGET_STRAFE
   };

   private final ModeSetting mode = this.add(new ModeSetting("Режим", NUCLEAR_COLLISION, MODES));

   private final SliderSetting nuclearMatrixSpeed = this.add(
      new SliderSetting("Nuclear: скорость Matrix", 0.36, 0.10, 0.7, 0.01));
   private final SliderSetting nuclearCollisionSpeed = this.add(
      new SliderSetting("Nuclear: скорость Collision", 1.1, 0.5, 2.0, 0.05));
   private final BooleanSetting nuclearAutojump = this.add(new BooleanSetting("Nuclear: авто-прыжок", true));

   private final SliderSetting rockstarSpeed = this.add(new SliderSetting("Rockstar: скорость", 8.0, 1.0, 20.0, 0.1));
   private final SliderSetting rockstarRange = this.add(new SliderSetting("Rockstar: дистанция", 3.0, 0.5, 10.0, 0.1));
   private final SliderSetting rockstarExpand = this.add(new SliderSetting("Rockstar: расширение", 0.5, 0.1, 2.0, 0.1));
   private final BooleanSetting rockstarOnlyPlayers = this.add(new BooleanSetting("Rockstar: только игроки", true));
   private final BooleanSetting rockstarRequireMoving = this.add(new BooleanSetting("Rockstar: требовать движение", true));

   private final SliderSetting phobiaSpeed = this.add(new SliderSetting("Phobia: скорость", 1.5, 1.0, 5.0, 0.1));
   private final BooleanSetting phobiaDiving = this.add(new BooleanSetting("Phobia: пикирование", false));

   private final SliderSetting releonSpeed = this.add(new SliderSetting("Releon: скорость", 1.5, 1.0, 5.0, 0.1));

   private final BooleanSetting privateOvertake = this.add(new BooleanSetting("Private: перегон игрока", false));
   private final SliderSetting privateOvertakeDistance = this.add(
      new SliderSetting("Private: дистанция перегона", 0.3, 0.1, 1.0, 0.05));
   private final SliderSetting privateChaseDistance = this.add(
      new SliderSetting("Private: дистанция догона", 0.15, 0.05, 0.5, 0.05));

   private final SliderSetting wildTarget = this.add(new SliderSetting("Wild: пиковая скорость (BPS)", 7.0, 3.0, 15.0, 1.0));
   private final SliderSetting wildPending = this.add(new SliderSetting("Wild: сила ускорения", 0.8, 0.1, 2.0, 0.1));
   private final SliderSetting wildPrevious = this.add(new SliderSetting("Wild: радиус стрейфа", 2.0, 0.5, 5.0, 0.1));

   private final SliderSetting swerveMultiplier = this.add(new SliderSetting("Swerve: множитель", 1.15, 1.0, 1.6, 0.01));
   private final BooleanSetting swerveOnlyGround = this.add(new BooleanSetting("Swerve: только на земле", false));
   private final BooleanSetting swerveOnlyForward = this.add(new BooleanSetting("Swerve: только вперёд", false));
   private final BooleanSetting swerveAutoSprint = this.add(new BooleanSetting("Swerve: авто-спринт", true));

   private final NuclearSpeed nuclear = new NuclearSpeed(this);
   private final RockstarSpeed rockstar = new RockstarSpeed(this);
   private final PhobiaSpeed phobia = new PhobiaSpeed(this);
   private final AlphaDlcSpeed alphaDlc = new AlphaDlcSpeed(this);
   private final ReleonSpeed releon = new ReleonSpeed(this);
   private final LegacySkyCoreSpeed legacy = new LegacySkyCoreSpeed(this);
   private final SwerveSpeed swerve = new SwerveSpeed(this);
   private final PrivateSpeed privateSpeed = new PrivateSpeed(this);
   private final WildSpeed wildSpeed = new WildSpeed(this);

   private SpeedHandler lastHandler;

   private SpeedModule() {
      super("Speed", "Спиды из клиентов: Nuclear, Rockstar, Phobia, AlphaDLC, Releon, SkyCoreLegacy, Swerve",
         ModuleCategory.MOVEMENT);
      this.nuclearMatrixSpeed.visibleWhen(() -> this.mode.is(NUCLEAR_MATRIX));
      this.nuclearCollisionSpeed.visibleWhen(() -> this.mode.is(NUCLEAR_COLLISION));
      this.nuclearAutojump.visibleWhen(() -> this.mode.is(NUCLEAR_MATRIX) || this.mode.is(NUCLEAR_META_HVH));

      this.rockstarSpeed.visibleWhen(() -> this.mode.is(ROCKSTAR_ENTITIES));
      this.rockstarRange.visibleWhen(() -> this.mode.is(ROCKSTAR_ENTITIES));
      this.rockstarExpand.visibleWhen(() -> this.mode.is(ROCKSTAR_ENTITIES));
      this.rockstarOnlyPlayers.visibleWhen(() -> this.mode.is(ROCKSTAR_ENTITIES));
      this.rockstarRequireMoving.visibleWhen(() -> this.mode.is(ROCKSTAR_ENTITIES));

      this.phobiaSpeed.visibleWhen(() -> this.mode.is(PHOBIA_VANILLA));
      this.phobiaDiving.visibleWhen(() -> this.mode.is(PHOBIA_FUNTIME_ELYTRA));

      this.releonSpeed.visibleWhen(() -> this.mode.is(RELEON_VANILLA));

      this.privateOvertake.visibleWhen(() -> this.mode.is(PRIVATE_ENTITY));
      this.privateOvertakeDistance.visibleWhen(() -> this.mode.is(PRIVATE_ENTITY) && this.privateOvertake.get());
      this.privateChaseDistance.visibleWhen(() -> this.mode.is(PRIVATE_ENTITY) && this.privateOvertake.get());

      this.wildTarget.visibleWhen(() -> this.wildModeAny());
      this.wildPending.visibleWhen(() -> this.wildModeAny());
      this.wildPrevious.visibleWhen(() -> this.mode.is(WILD_TARGET_STRAFE) == false && this.wildModeAny());

      this.swerveMultiplier.visibleWhen(() -> this.mode.is(SWERVE_MULTIPLIER));
      this.swerveOnlyGround.visibleWhen(() -> this.mode.is(SWERVE_MULTIPLIER));
      this.swerveOnlyForward.visibleWhen(() -> this.mode.is(SWERVE_MULTIPLIER));
      this.swerveAutoSprint.visibleWhen(() -> this.mode.is(SWERVE_MULTIPLIER));

      SkycoreClient.events().subscribe(PlayerTickStartEvent.class, this::onTickStart);
      SkycoreClient.events().subscribe(PlayerTickEndEvent.class, this::onTickEnd);
      SkycoreClient.events().subscribe(PlayerInputEvent.class, this::onInput);
      SkycoreClient.events().subscribe(PacketSendEvent.class, this::onPacketSend);
      SkycoreClient.events().subscribe(PacketReceiveEvent.class, this::onPacketReceive);
      SkycoreClient.events().subscribe(ClientTickEvent.class, this::onClientTick);
   }

   @Override
   protected void onEnable() {
      this.lastHandler = this.activeHandler();
   }

   @Override
   protected void onDisable() {
      for (SpeedHandler handler : this.handlers()) {
         handler.onDisable();
      }

      SpeedFlyingSpeed.clear();
      SpeedTimer.clear();
      this.lastHandler = null;
   }

   private SpeedHandler[] handlers() {
      return new SpeedHandler[]{this.nuclear, this.rockstar, this.phobia, this.alphaDlc, this.releon, this.legacy, this.swerve, this.privateSpeed, this.wildSpeed};
   }

   private SpeedHandler activeHandler() {
      String current = this.mode.get();
      if (current.startsWith("Nuclear [")) {
         return this.nuclear;
      } else if (current.startsWith("Rockstar [")) {
         return this.rockstar;
      } else if (current.startsWith("Phobia [")) {
         return this.phobia;
      } else if (current.startsWith("AlphaDLC [")) {
         return this.alphaDlc;
      } else if (current.startsWith("Releon [")) {
         return this.releon;
      } else if (current.startsWith("SkyCoreLegacy [")) {
         return this.legacy;
      } else if (current.startsWith("Private [")) {
         return this.privateSpeed;
      } else if (current.startsWith("Wild [")) {
         return this.wildSpeed;
      } else {
         return current.startsWith("Swerve [") ? this.swerve : null;
      }
   }

   private void syncHandler() {
      SpeedHandler active = this.activeHandler();
      if (active != this.lastHandler) {
         if (this.lastHandler != null) {
            this.lastHandler.onDisable();
         }

         SpeedFlyingSpeed.clear();
         SpeedTimer.clear();
         this.lastHandler = active;
         if (active != null) {
            active.onEnable();
         }
      }
   }

   private void onTickStart(PlayerTickStartEvent event) {
      if (!this.isEnabled()) {
         return;
      }

      this.syncHandler();
      SpeedHandler active = this.activeHandler();
      LocalPlayer player = event.player();
      if (active != null && player != null) {
         active.onTickStart(player);
      }
   }

   private void onTickEnd(PlayerTickEndEvent event) {
      if (!this.isEnabled()) {
         return;
      }

      SpeedHandler active = this.activeHandler();
      LocalPlayer player = event.player();
      if (active != null && player != null) {
         active.onTickEnd(player);
      }
   }

   private void onInput(PlayerInputEvent event) {
      if (!this.isEnabled()) {
         return;
      }

      SpeedHandler active = this.activeHandler();
      if (active != null) {
         active.onInput(event);
      }
   }

   private void onPacketSend(PacketSendEvent event) {
      if (!this.isEnabled() || event.isCancelled()) {
         return;
      }

      SpeedHandler active = this.activeHandler();
      if (active != null) {
         active.onPacketSend(event);
      }
   }

   private void onPacketReceive(PacketReceiveEvent event) {
      if (!this.isEnabled() || event.isCancelled()) {
         return;
      }

      SpeedHandler active = this.activeHandler();
      if (active != null) {
         active.onPacketReceive(event);
      }
   }

   private void onClientTick(ClientTickEvent event) {
      if (!this.isEnabled()) {
         return;
      }

      SpeedHandler active = this.activeHandler();
      if (active != null) {
         active.onClientTick();
      }
   }

   /** Вызывается из LivingEntitySpeedTravelMixin до ванильной интеграции (PlayerTravelEvent источников). */
   public void onTravelPre(LocalPlayer player) {
      if (!this.isEnabled() || player == null) {
         return;
      }

      SpeedHandler active = this.activeHandler();
      if (active != null) {
         active.onTravelPre(player);
      }
   }

   /** Phobia Funtime Elytra: наклон серверной ротации вниз. */
   public void rotateDown(float pitch) {
      LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
      if (player != null) {
         RotationController.apply(player.getYRot(), pitch);
      }
   }

   public boolean nuclearMode(String name) {
      return this.mode.is("Nuclear [" + name + "]");
   }

   public boolean rockstarMode() {
      return this.mode.is(ROCKSTAR_ENTITIES);
   }

   public boolean phobiaMode(String name) {
      return this.mode.is("Phobia [" + name + "]");
   }

   public boolean alphaMode(String name) {
      return this.mode.is("AlphaDLC [" + name + "]");
   }

   public boolean releonMode(String name) {
      return this.mode.is("Releon [" + name + "]");
   }

   public boolean legacyMode(String name) {
      return this.mode.is("SkyCoreLegacy [" + name + "]");
   }

   public boolean privateMode(String name) {
      return this.mode.is("Private [" + name + "]");
   }

   public boolean wildMode(String name) {
      return this.mode.is("Wild [" + name + "]");
   }

   public boolean wildModeAny() {
      return this.mode.get().startsWith("Wild [");
   }

   public boolean swerveMode() {
      return this.mode.is(SWERVE_MULTIPLIER);
   }

   public double nuclearMatrixSpeed() {
      return this.nuclearMatrixSpeed.get().doubleValue();
   }

   public double nuclearCollisionSpeed() {
      return this.nuclearCollisionSpeed.get().doubleValue();
   }

   public boolean nuclearAutojump() {
      return this.nuclearAutojump.get();
   }

   public double rockstarSpeed() {
      return this.rockstarSpeed.get().doubleValue();
   }

   public double rockstarRange() {
      return this.rockstarRange.get().doubleValue();
   }

   public double rockstarExpand() {
      return this.rockstarExpand.get().doubleValue();
   }

   public boolean rockstarOnlyPlayers() {
      return this.rockstarOnlyPlayers.get();
   }

   public boolean rockstarRequireMoving() {
      return this.rockstarRequireMoving.get();
   }

   public double phobiaSpeed() {
      return this.phobiaSpeed.get().doubleValue();
   }

   public boolean phobiaDiving() {
      return this.phobiaDiving.get();
   }

   public double releonSpeed() {
      return this.releonSpeed.get().doubleValue();
   }

   public boolean privateOvertake() {
      return this.privateOvertake.get();
   }

   public double privateOvertakeDistance() {
      return this.privateOvertakeDistance.get().doubleValue();
   }

   public double privateChaseDistance() {
      return this.privateChaseDistance.get().doubleValue();
   }

   public double wildTarget() {
      return this.wildTarget.get().doubleValue();
   }

   public double wildPending() {
      return this.wildPending.get().doubleValue();
   }

   public double wildPrevious() {
      return this.wildPrevious.get().doubleValue();
   }

   public double swerveMultiplier() {
      return this.swerveMultiplier.get().doubleValue();
   }

   public boolean swerveOnlyGround() {
      return this.swerveOnlyGround.get();
   }

   public boolean swerveOnlyForward() {
      return this.swerveOnlyForward.get();
   }

   public boolean swerveAutoSprint() {
      return this.swerveAutoSprint.get();
   }

   /** Миксин Swerve: множитель ванильного ускорения. */
   public static float swerveAcceleration(float acceleration, net.minecraft.world.entity.LivingEntity entity) {
      return INSTANCE.isEnabled() && INSTANCE.swerveMode()
         ? INSTANCE.swerve.onAcceleration(acceleration, entity)
         : acceleration;
   }

   public static boolean swerveSprinting() {
      return INSTANCE.isEnabled() && INSTANCE.swerveMode() && INSTANCE.swerveAutoSprint();
   }
}
