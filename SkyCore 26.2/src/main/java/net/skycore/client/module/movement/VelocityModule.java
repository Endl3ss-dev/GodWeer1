package net.skycore.client.module.movement;

import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.velocity.AlphaDlcVelocity;
import net.skycore.client.combat.velocity.LegacySkyCoreVelocity;
import net.skycore.client.combat.velocity.LightBetaVelocity;
import net.skycore.client.combat.velocity.MincedVelocity;
import net.skycore.client.combat.velocity.NuclearVelocity;
import net.skycore.client.combat.velocity.PhobiaVelocity;
import net.skycore.client.combat.velocity.PrivateMovementVelocity;
import net.skycore.client.combat.velocity.PrivateVelocity;
import net.skycore.client.combat.velocity.ReleonVelocity;
import net.skycore.client.combat.velocity.RockstarVelocity;
import net.skycore.client.combat.velocity.SadnesVelocity;
import net.skycore.client.combat.velocity.VelocityAction;
import net.skycore.client.combat.velocity.VelocityHandler;
import net.skycore.client.combat.velocity.WildVelocity;
import net.skycore.client.event.PacketSendEvent;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.MultiSelectSetting;
import net.skycore.client.setting.NumberSetting;

/**
 * Velocity — все обходы отбрасывания из клиентов в папке GodWeer, собранные в один модуль.
 *
 * <p>Каждый вариант перенесён из своего клиента один в один и подписан именем источника:
 * {@code Клиент [Режим клиента]}. Состояние обходов не пересекается — у каждого свой класс
 * в {@code net.skycore.client.combat.velocity}, и при смене режима предыдущий обход сбрасывается.
 *
 * <table border="1">
 *   <caption>Источники</caption>
 *   <tr><th>Режим</th><th>Источник</th></tr>
 *   <tr><td>{@code Wild [...]}</td><td>{@code 3}/{@code Wild}: modules/combat/Velocity.java</td></tr>
 *   <tr><td>{@code AlphaDLC [Cancel]}</td><td>AlphaDLC: module/list/combat/Velocity.java</td></tr>
 *   <tr><td>{@code LightBeta [...]}</td><td>LightBeta: modules/impl/combat/NoVelocity.java</td></tr>
 *   <tr><td>{@code Minced [...]}</td><td>Minced: module/combat/NoVelocity.java</td></tr>
 *   <tr><td>{@code Nuclear [...]}</td><td>Nuclear: module/impl/combat/Velocity.java</td></tr>
 *   <tr><td>{@code Phobia [...]}</td><td>Phobia: modulesystem/impl/rage/Velocity.java</td></tr>
 *   <tr><td>{@code Private [...]}</td><td>Private: module/impl/combat/Velocity.java</td></tr>
 *   <tr><td>{@code PrivateMove [...]}</td><td>Private: module/impl/movement/Velocity.java</td></tr>
 *   <tr><td>{@code Releon [...]}</td><td>Releon: module/impl/combat/Velocity.java</td></tr>
 *   <tr><td>{@code Rockstar [...]}</td><td>Rockstar: module/combat/Velocity.java</td></tr>
 *   <tr><td>{@code Sadnes [...]}</td><td>Sadnes: feature/impl/combat/VelocityControl.java</td></tr>
 *   <tr><td>{@code SkyCoreLegacy [...]}</td><td>SkyCore (старый): modules/impl/combat/NoVelocity.java</td></tr>
 * </table>
 *
 * <p>Пакет скорости не отменяется на уровне сети: ванильный обработчик применяет его, после чего
 * {@link VelocityAction} возвращает прежнюю скорость или масштабирует применённую. Для локального
 * игрока результат совпадает с правкой пакета, но не требует доступа к закрытым полям пакета.
 */
public final class VelocityModule extends ClientModule {
   public static final String WILD_VANILLA = "Wild [Vanilla]";
   public static final String WILD_LAG = "Wild [Lag]";
   public static final String WILD_FUNTIME = "Wild [Funtime]";

   public static final String ALPHADLC_CANCEL = "AlphaDLC [Cancel]";

   public static final String LIGHTBETA_CANCEL = "LightBeta [Cancel]";
   public static final String LIGHTBETA_JUMP = "LightBeta [Jump]";
   public static final String LIGHTBETA_HWTEST = "LightBeta [HwTest]";

   public static final String MINCED_NORMAL = "Minced [Обычный]";
   public static final String MINCED_GRIM = "Minced [Грим]";

   public static final String NUCLEAR_CANCEL = "Nuclear [Cancel]";
   public static final String NUCLEAR_GRIM = "Nuclear [Grim]";
   public static final String NUCLEAR_GRIM_AIR = "Nuclear [Grim Air]";

   public static final String PHOBIA_NEW_GRIM = "Phobia [New Grim]";
   public static final String PHOBIA_OLD_GRIM = "Phobia [Old Grim]";
   public static final String PHOBIA_MATRIX = "Phobia [Matrix]";
   public static final String PHOBIA_VANILLA = "Phobia [Vanilla]";

   public static final String PRIVATE_MATRIX = "Private [Matrix]";
   public static final String PRIVATE_VULCAN = "Private [Vulcan]";
   public static final String PRIVATE_INTAVE = "Private [Intave]";
   public static final String PRIVATE_AAC_V4 = "Private [AAC v4]";
   public static final String PRIVATE_NCP = "Private [NCP]";
   public static final String PRIVATE_REVERSE = "Private [Reverse]";
   // Ветки исходного switch, которых нет в списке режимов Private (мёртвый код источника).
   public static final String PRIVATE_PACKET = "Private [Packet]";
   public static final String PRIVATE_AAC_PUSH = "Private [AAC Push]";
   public static final String PRIVATE_GRIM = "Private [Grim]";

   public static final String PM_GRIM = "PrivateMove [Grim]";
   public static final String PM_GRIM_LIGHT = "PrivateMove [Grim Light]";
   public static final String PM_GRIM_HEAVY = "PrivateMove [Grim Heavy]";
   public static final String PM_GRIM_STABLE = "PrivateMove [Grim Stable]";
   public static final String PM_GRIM_ADVANCED = "PrivateMove [Grim Advanced]";
   public static final String PM_MATRIX = "PrivateMove [Matrix]";
   public static final String PM_VULCAN = "PrivateMove [Vulcan]";
   public static final String PM_INTAVE = "PrivateMove [Intave]";
   public static final String PM_VERUS = "PrivateMove [Verus]";
   public static final String PM_NCP = "PrivateMove [NCP]";
   public static final String PM_AAC = "PrivateMove [AAC]";
   public static final String PM_SPARTAN = "PrivateMove [Spartan]";
   public static final String PM_VANILLA = "PrivateMove [Vanilla]";
   public static final String PM_CUSTOM = "PrivateMove [Custom]";
   public static final String PM_ZERO = "PrivateMove [Zero]";
   public static final String PM_REDUCE = "PrivateMove [Reduce]";
   public static final String PM_JUMP_RESET = "PrivateMove [Jump Reset]";

   public static final String RELEON_NEW_GRIM = "Releon [New Grim]";
   public static final String RELEON_OLD_GRIM = "Releon [Old Grim]";
   public static final String RELEON_MATRIX = "Releon [Matrix]";
   public static final String RELEON_NORMAL = "Releon [Normal]";

   public static final String ROCKSTAR_CANCEL = "Rockstar [Отмена]";
   public static final String ROCKSTAR_COMPENSATION = "Rockstar [Компенсация]";
   public static final String ROCKSTAR_MODIFY = "Rockstar [Изменение]";

   public static final String SADNES_NORMAL = "Sadnes [normal]";
   public static final String SADNES_RIDING_SPOOF = "Sadnes [riding_spoof]";
   public static final String SADNES_GRIM_STANDING = "Sadnes [grim_standing]";
   public static final String SADNES_JUMP_RESET = "Sadnes [jump_reset]";

   public static final String LEGACY_NORMAL = "SkyCoreLegacy [Обычный]";
   public static final String LEGACY_RW = "SkyCoreLegacy [RW]";

   private static final String SADNES_PREFIX = "Sadnes [";
   private static final String PRIVATE_MOVE_PREFIX = "PrivateMove [";
   private static final String GRIM_PREFIX = "PrivateMove [Grim";

   private static final String[] MODES = {
           WILD_VANILLA, WILD_LAG, WILD_FUNTIME,
           ALPHADLC_CANCEL,
           LIGHTBETA_CANCEL, LIGHTBETA_JUMP, LIGHTBETA_HWTEST,
           MINCED_NORMAL, MINCED_GRIM,
           NUCLEAR_CANCEL, NUCLEAR_GRIM, NUCLEAR_GRIM_AIR,
           PHOBIA_NEW_GRIM, PHOBIA_OLD_GRIM, PHOBIA_MATRIX, PHOBIA_VANILLA,
           PRIVATE_MATRIX, PRIVATE_VULCAN, PRIVATE_INTAVE, PRIVATE_AAC_V4, PRIVATE_NCP, PRIVATE_REVERSE,
           PRIVATE_PACKET, PRIVATE_AAC_PUSH, PRIVATE_GRIM,
           PM_GRIM, PM_GRIM_LIGHT, PM_GRIM_HEAVY, PM_GRIM_STABLE, PM_GRIM_ADVANCED, PM_MATRIX, PM_VULCAN,
           PM_INTAVE, PM_VERUS, PM_NCP, PM_AAC, PM_SPARTAN, PM_VANILLA, PM_CUSTOM, PM_ZERO, PM_REDUCE,
           PM_JUMP_RESET,
           RELEON_NEW_GRIM, RELEON_OLD_GRIM, RELEON_MATRIX, RELEON_NORMAL,
           ROCKSTAR_CANCEL, ROCKSTAR_COMPENSATION, ROCKSTAR_MODIFY,
           SADNES_NORMAL, SADNES_RIDING_SPOOF, SADNES_GRIM_STANDING, SADNES_JUMP_RESET,
           LEGACY_NORMAL, LEGACY_RW
   };

   public static final VelocityModule INSTANCE = new VelocityModule();

   private final ModeSetting mode = this.add(new ModeSetting("Режим", NUCLEAR_CANCEL, MODES));

   private final BooleanSetting mincedSkipSpeed = this.add(
           new BooleanSetting("Minced: пропускать при активных спидах", true));
   private final BooleanSetting nuclearDisableOnFlag = this.add(
           new BooleanSetting("Nuclear: вырубить после флага", false));

   private final NumberSetting privateHorizontal = this.add(
           new NumberSetting("Private: горизонталь %", 0.0, 0.0, 100.0, 1.0));
   private final NumberSetting privateVertical = this.add(
           new NumberSetting("Private: вертикаль %", 0.0, 0.0, 100.0, 1.0));

   private final NumberSetting movementHorizontal = this.add(
           new NumberSetting("PrivateMove: горизонтальный %", 0.0, 0.0, 100.0, 1.0));
   private final NumberSetting movementVertical = this.add(
           new NumberSetting("PrivateMove: вертикальный %", 0.0, 0.0, 100.0, 1.0));
   private final NumberSetting movementChance = this.add(
           new NumberSetting("PrivateMove: шанс срабатывания %", 100.0, 0.0, 100.0, 1.0));
   private final NumberSetting movementGrimDelay = this.add(
           new NumberSetting("PrivateMove: задержка (мс)", 150.0, 50.0, 500.0, 10.0));
   private final NumberSetting movementReduction = this.add(
           new NumberSetting("PrivateMove: уменьшение силы", 0.8, 0.1, 1.0, 0.05));
   private final BooleanSetting movementOnlyGround = this.add(
           new BooleanSetting("PrivateMove: только на земле", false));
   private final BooleanSetting movementExplosions = this.add(
           new BooleanSetting("PrivateMove: блокировать взрывы", true));
   private final BooleanSetting movementDebug = this.add(
           new BooleanSetting("PrivateMove: отладка", false));

   private final NumberSetting rockstarX = this.add(
           new NumberSetting("Rockstar: скорость по X", 50.0, 0.0, 100.0, 1.0));
   private final NumberSetting rockstarY = this.add(
           new NumberSetting("Rockstar: скорость по Y", 50.0, 0.0, 100.0, 1.0));
   private final NumberSetting rockstarZ = this.add(
           new NumberSetting("Rockstar: скорость по Z", 50.0, 0.0, 100.0, 1.0));

   private final MultiSelectSetting sadnesSources = this.add(
           new MultiSelectSetting(
                   "Sadnes: от чего отменять",
                   List.of("from_hits", "from_explosions", "from_wind_charge"),
                   Set.of("from_hits")));
   private final NumberSetting sadnesStandingDelay = this.add(
           new NumberSetting("Sadnes: задержка постановки", 0.0, 0.0, 10.0, 1.0));
   private final NumberSetting sadnesPauseOnFlag = this.add(
           new NumberSetting("Sadnes: пауза при флаге (с)", 2.0, 0.0, 10.0, 0.5));

   private final BooleanSetting legacyJump = this.add(
           new BooleanSetting("SkyCoreLegacy: прыгать", true));

   private final WildVelocity wild = new WildVelocity(this);
   private final AlphaDlcVelocity alphaDlc = new AlphaDlcVelocity();
   private final LightBetaVelocity lightBeta = new LightBetaVelocity(this);
   private final MincedVelocity minced = new MincedVelocity(this);
   private final NuclearVelocity nuclear = new NuclearVelocity(this);
   private final PhobiaVelocity phobia = new PhobiaVelocity(this);
   private final PrivateVelocity privateCombat = new PrivateVelocity(this);
   private final PrivateMovementVelocity privateMovement = new PrivateMovementVelocity(this);
   private final ReleonVelocity releon = new ReleonVelocity(this);
   private final RockstarVelocity rockstar = new RockstarVelocity(this);
   private final SadnesVelocity sadnes = new SadnesVelocity(this);
   private final LegacySkyCoreVelocity legacy = new LegacySkyCoreVelocity(this);

   private final VelocityAction action = new VelocityAction();
   private VelocityHandler handler;
   private String lastMode;
   private boolean velocityPending;
   private Vec3 capturedVelocity = Vec3.ZERO;

   private VelocityModule() {
      super("Velocity", "Обходы отбрасывания из 12 клиентов: Wild, AlphaDLC, LightBeta, Minced, Nuclear, "
              + "Phobia, Private (Combat и Movement), Releon, Rockstar, Sadnes и старого SkyCore", ModuleCategory.MOVEMENT);

      this.mincedSkipSpeed.visibleWhen(() -> this.mode.is(MINCED_GRIM));
      this.nuclearDisableOnFlag.visibleWhen(() -> this.mode.is(NUCLEAR_CANCEL)
              || this.mode.is(NUCLEAR_GRIM) || this.mode.is(NUCLEAR_GRIM_AIR));

      this.privateHorizontal.visibleWhen(() -> this.mode.is(PRIVATE_PACKET));
      this.privateVertical.visibleWhen(() -> this.mode.is(PRIVATE_PACKET));

      this.movementHorizontal.visibleWhen(() -> this.mode.is(PM_CUSTOM) || this.mode.is(PM_REDUCE));
      this.movementVertical.visibleWhen(() -> this.mode.is(PM_CUSTOM) || this.mode.is(PM_REDUCE));
      this.movementChance.visibleWhen(() -> this.mode.is(PM_GRIM_ADVANCED) || this.mode.is(PM_MATRIX));
      this.movementGrimDelay.visibleWhen(() -> this.mode.get().startsWith(GRIM_PREFIX));
      this.movementReduction.visibleWhen(() -> this.mode.get().startsWith(GRIM_PREFIX));
      this.movementOnlyGround.visibleWhen(() -> this.mode.is(PM_GRIM_LIGHT) || this.mode.is(PM_GRIM_STABLE));
      this.movementExplosions.visibleWhen(() -> this.mode.get().startsWith(PRIVATE_MOVE_PREFIX));
      this.movementDebug.visibleWhen(() -> this.mode.get().startsWith(PRIVATE_MOVE_PREFIX));

      this.rockstarX.visibleWhen(() -> this.mode.is(ROCKSTAR_MODIFY));
      this.rockstarY.visibleWhen(() -> this.mode.is(ROCKSTAR_MODIFY));
      this.rockstarZ.visibleWhen(() -> this.mode.is(ROCKSTAR_MODIFY));

      this.sadnesSources.visibleWhen(this::isSadnes);
      this.sadnesStandingDelay.visibleWhen(() -> this.mode.is(SADNES_GRIM_STANDING));
      this.sadnesPauseOnFlag.visibleWhen(() -> this.mode.is(SADNES_GRIM_STANDING));

      this.legacyJump.visibleWhen(() -> this.mode.is(LEGACY_RW));

      SkycoreClient.events().subscribe(PlayerTickEndEvent.class, this::onPlayerTick);
      SkycoreClient.events().subscribe(PlayerInputEvent.class, this::onPlayerInput);
      SkycoreClient.events().subscribe(PacketSendEvent.class, this::onPacketSend);
   }

   public boolean is(String mode) {
      return this.mode.is(mode);
   }

   public boolean isOneOf(String... modes) {
      String current = this.mode.get();
      for (String candidate : modes) {
         if (candidate != null && candidate.equalsIgnoreCase(current)) {
            return true;
         }
      }
      return false;
   }

   public boolean isSadnes() {
      return this.mode.get().startsWith(SADNES_PREFIX);
   }

   public BooleanSetting mincedSkipSpeed() {
      return this.mincedSkipSpeed;
   }

   public BooleanSetting nuclearDisableOnFlag() {
      return this.nuclearDisableOnFlag;
   }

   public NumberSetting privateHorizontal() {
      return this.privateHorizontal;
   }

   public NumberSetting privateVertical() {
      return this.privateVertical;
   }

   public NumberSetting movementHorizontal() {
      return this.movementHorizontal;
   }

   public NumberSetting movementVertical() {
      return this.movementVertical;
   }

   public NumberSetting movementChance() {
      return this.movementChance;
   }

   public NumberSetting movementGrimDelay() {
      return this.movementGrimDelay;
   }

   public NumberSetting movementReduction() {
      return this.movementReduction;
   }

   public BooleanSetting movementOnlyGround() {
      return this.movementOnlyGround;
   }

   public BooleanSetting movementExplosions() {
      return this.movementExplosions;
   }

   public BooleanSetting movementDebug() {
      return this.movementDebug;
   }

   public NumberSetting rockstarX() {
      return this.rockstarX;
   }

   public NumberSetting rockstarY() {
      return this.rockstarY;
   }

   public NumberSetting rockstarZ() {
      return this.rockstarZ;
   }

   public MultiSelectSetting sadnesSources() {
      return this.sadnesSources;
   }

   public NumberSetting sadnesStandingDelay() {
      return this.sadnesStandingDelay;
   }

   public NumberSetting sadnesPauseOnFlag() {
      return this.sadnesPauseOnFlag;
   }

   public BooleanSetting legacyJump() {
      return this.legacyJump;
   }

   /**
    * Входящий пакет от {@code Connection}: сюда приходят взрывы, пинги, подтверждения транзакций,
    * обновления блоков и урон. Возврат {@code true} отменяет пакет.
    */
   public boolean onInboundPacket(Packet<?> packet) {
      if (!this.isEnabled() || packet == null) {
         return false;
      }
      VelocityHandler current = this.activeHandler();
      return current != null && current.onInboundPacket(packet);
   }

   /** Исходящий пакет от {@code Connection}; возврат {@code true} отменяет отправку. */
   public boolean onOutboundPacket(Packet<?> packet) {
      if (!this.isEnabled() || packet == null) {
         return false;
      }
      VelocityHandler current = this.activeHandler();
      return current != null && current.onOutboundPacket(packet);
   }

   /** Вызывается до ванильной обработки пакета скорости: запоминает скорость игрока. */
   public void beforeVelocityPacket(ClientboundSetEntityMotionPacket packet) {
      LocalPlayer player = Minecraft.getInstance().player;
      this.velocityPending = this.isEnabled()
              && player != null
              && Minecraft.getInstance().isSameThread()
              && packet.id() == player.getId();
      if (this.velocityPending) {
         this.capturedVelocity = player.getDeltaMovement();
      }
   }

   /** Вызывается после ванильной обработки пакета скорости: применяет решение обхода. */
   public void afterVelocityPacket(ClientboundSetEntityMotionPacket packet) {
      if (!this.velocityPending) {
         return;
      }
      this.velocityPending = false;

      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || packet.id() != player.getId()) {
         return;
      }
      VelocityHandler current = this.activeHandler();
      if (current == null) {
         return;
      }

      this.action.begin(this.capturedVelocity, player.getDeltaMovement());
      current.onVelocityPacket(this.action);
      this.action.apply(player);
      this.action.reset();
   }

   private VelocityHandler activeHandler() {
      String current = this.mode.get();
      if (current != this.lastMode) {
         if (this.handler != null) {
            this.handler.reset();
         }
         this.handler = this.resolve(current);
         this.lastMode = current;
      }
      return this.handler;
   }

   private VelocityHandler resolve(String mode) {
      if (mode == null) {
         return null;
      }
      if (mode.startsWith("Wild [")) {
         return this.wild;
      }
      if (mode.startsWith("AlphaDLC [")) {
         return this.alphaDlc;
      }
      if (mode.startsWith("LightBeta [")) {
         return this.lightBeta;
      }
      if (mode.startsWith("Minced [")) {
         return this.minced;
      }
      if (mode.startsWith("Nuclear [")) {
         return this.nuclear;
      }
      if (mode.startsWith("Phobia [")) {
         return this.phobia;
      }
      if (mode.startsWith(PRIVATE_MOVE_PREFIX)) {
         return this.privateMovement;
      }
      if (mode.startsWith("Private [")) {
         return this.privateCombat;
      }
      if (mode.startsWith("Releon [")) {
         return this.releon;
      }
      if (mode.startsWith("Rockstar [")) {
         return this.rockstar;
      }
      if (mode.startsWith(SADNES_PREFIX)) {
         return this.sadnes;
      }
      if (mode.startsWith("SkyCoreLegacy [")) {
         return this.legacy;
      }
      return null;
   }

   private void onPlayerTick(PlayerTickEndEvent event) {
      if (!this.isEnabled()) {
         return;
      }
      VelocityHandler current = this.activeHandler();
      if (current != null) {
         current.onTick(event.player());
      }
   }

   private void onPlayerInput(PlayerInputEvent event) {
      if (!this.isEnabled()) {
         return;
      }
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         return;
      }
      VelocityHandler current = this.activeHandler();
      if (current != null) {
         current.onInput(event, player);
      }
   }

   private void onPacketSend(PacketSendEvent event) {
      if (!this.isEnabled() || event.isCancelled()) {
         return;
      }
      if (this.onOutboundPacket(event.getPacket())) {
         event.cancel();
      }
   }

   @Override
   protected void onEnable() {
      if (this.handler != null) {
         this.handler.reset();
      }
      this.handler = null;
      this.lastMode = null;
      this.velocityPending = false;
   }

   @Override
   protected void onDisable() {
      if (this.handler != null) {
         this.handler.reset();
      }
      this.handler = null;
      this.lastMode = null;
      this.velocityPending = false;
   }
}
