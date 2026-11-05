package net.skycore.client.combat.velocity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot;
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.level.ClipContext;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.module.movement.VelocityModule;

/**
 * Sadnes — {@code Sadnes/src/main/java/tech/sadnes/client/feature/impl/combat/VelocityControl.java}
 * (MC 1.21.11). Настройки: мультивыбор «От чего отменять» ({@code from_hits / from_explosions /
 * from_wind_charge}), режим {@code normal / riding_spoof / grim_standing / jump_reset},
 * «Задержка постановки» и «Пауза при флаге (сек)».
 *
 * <p>Перенос один в один:
 * <ul>
 *   <li>{@code normal} — отмена пакета скорости своего игрока;</li>
 *   <li>{@code riding_spoof} — отмена пакета скорости и пакет позиции с {@code y = -999}
 *       ({@code riding_spoof} для античитов, которые считают игрока ездящим);</li>
 *   <li>{@code grim_standing} — сложный обход: после урона пакет скорости отменяется,
 *       входящие пакеты откладываются, клиент ставит блок вниз под себя, ждёт обновления блока
 *       и ответа на пинг, всё это время тик игрока «замораживается»; флаг Grim (сообщение в чате
 *       со словами grim и fail/flag) ставит паузу на {@code pauseOnFlag} секунд;</li>
 *   <li>{@code jump_reset} — скорость не отменяется: запоминается направление удара, и пока игрок
 *       в воздухе и не «отлетел» назад, ввод подменяется на движение в сторону удара.</li>
 * </ul>
 *
 * <p>Отличия переноса, которых не избежать в 26.2:
 * <ul>
 *   <li>источник отменяет событие тика игрока ({@code PlayerTickEvent#setCancelled}); в SkyCore
 *       такого события нет — вместо этого на время постановки блока обнуляется ввод игрока;</li>
 *   <li>{@code DeferredPacketQueue} источника перенесён как локальная очередь с повторной
 *       обработкой пакета через {@code Packet#handle} на клиентском потоке;</li>
 *   <li>{@code from_wind_charge} сохранён в настройках, но отделить ветряной заряд от обычного
 *       взрыва можно только по частицам взрыва, а этот API в 26.2 не подтверждён, поэтому
 *       настройка пока не влияет на поведение (сам взрыв при этом отменяется по {@code from_explosions}).</li>
 * </ul>
 */
public final class SadnesVelocity implements VelocityHandler {
   private static final int MAX_TICKS = 20;

   private final VelocityModule module;
   private volatile boolean damageReceived;
   private volatile boolean delayingPackets;
   private volatile boolean waitingToPlace;
   private volatile boolean awaitingPong;
   private volatile boolean placing;
   private volatile boolean interactedThisTick;
   private volatile boolean pendingJump;
   private volatile boolean jumpResetActive;
   private boolean leftGround;
   private volatile Vec3 knockbackDirection;
   private Vec3 knockbackOrigin;
   private int jumpResetTicks;
   private volatile int placeDelayTicks;
   private volatile int blockUpdateTicks = -1;
   private volatile int pongTicks = -1;
   private volatile BlockPos placedPos;
   private volatile long flagPauseUntil;
   private int placingTicks;
   private BlockHitResult pendingPlaceHit;
   private final List<Packet<?>> deferred = new ArrayList<>();

   public SadnesVelocity(VelocityModule module) {
      this.module = module;
   }

   private boolean has(String source) {
      return this.module.sadnesSources().isSelected(source);
   }

   @Override
   public void onVelocityPacket(VelocityAction action) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         return;
      }

      if (!this.has("from_hits") || !this.module.isSadnes()) {
         return;
      }

      if (this.module.is(VelocityModule.SADNES_NORMAL)) {
         action.cancel();
         return;
      }

      if (this.module.is(VelocityModule.SADNES_RIDING_SPOOF)) {
         action.cancel();
         player.connection.send(new Pos(player.getX(), -999.0, player.getZ(), true, player.horizontalCollision));
         return;
      }

      if (this.module.is(VelocityModule.SADNES_JUMP_RESET)) {
         if (player.onGround()) {
            Vec3 velocity = action.packetVelocity();
            double length = Math.hypot(velocity.x, velocity.z);
            if (length > 1.0E-4) {
               this.knockbackDirection = new Vec3(velocity.x / length, 0.0, velocity.z / length);
               this.knockbackOrigin = new Vec3(player.getX(), 0.0, player.getZ());
               this.pendingJump = true;
               this.jumpResetActive = true;
               this.leftGround = false;
               this.jumpResetTicks = 0;
            }
         }
         return;
      }

      if (this.module.is(VelocityModule.SADNES_GRIM_STANDING)) {
         if (System.currentTimeMillis() < this.flagPauseUntil) {
            action.cancel();
            return;
         }
         if (!this.placing && !this.delayingPackets && this.damageReceived) {
            action.cancel();
            this.delayingPackets = true;
            this.damageReceived = false;
            this.waitingToPlace = true;
            this.placeDelayTicks = (int) Math.round(this.module.sadnesStandingDelay().get());
         }
      }
   }

   @Override
   public boolean onInboundPacket(Packet<?> packet) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || !this.module.isSadnes()) {
         return false;
      }

      if (packet instanceof ClientboundExplodePacket && this.has("from_explosions")) {
         return true;
      }

      if (this.module.is(VelocityModule.SADNES_GRIM_STANDING)) {
         return this.grimStandingPacket(packet, player);
      }

      return false;
   }

   private boolean grimStandingPacket(Packet<?> packet, LocalPlayer player) {
      if (!this.has("from_hits")) {
         return false;
      }

      if (packet instanceof ClientboundSystemChatPacket chat) {
         String text = chat.content().getString().toLowerCase(Locale.ROOT);
         if (text.contains("grim") && (text.contains("fail") || text.contains("flag"))) {
            this.flagPauseUntil = System.currentTimeMillis() + (long) (this.module.sadnesPauseOnFlag().get() * 1000.0);
         }
      }

      if (packet instanceof ClientboundBlockUpdatePacket update
              && this.placing
              && this.placedPos != null
              && update.getPos().equals(this.placedPos)) {
         this.blockUpdateTicks = 1;
         this.waitingToPlace = false;
      }

      if (!this.placing && !this.delayingPackets) {
         if (System.currentTimeMillis() < this.flagPauseUntil) {
            return packet instanceof ClientboundSetEntityMotionPacket motion && motion.id() == player.getId();
         }
         if (packet instanceof ClientboundDamageEventPacket damage && damage.entityId() == player.getId()) {
            this.damageReceived = true;
         }
      } else if (this.delayingPackets && !this.placing) {
         this.defer(packet);
         return true;
      }
      return false;
   }

   @Override
   public boolean onOutboundPacket(Packet<?> packet) {
      if (!this.module.is(VelocityModule.SADNES_GRIM_STANDING) || !this.has("from_hits")) {
         return false;
      }

      if (packet instanceof ServerboundAttackPacket || packet instanceof ServerboundUseItemOnPacket) {
         this.interactedThisTick = true;
      }

      if (this.placing && packet instanceof ServerboundMovePlayerPacket move && move.hasPosition()) {
         return true;
      }

      if (this.awaitingPong && packet instanceof ServerboundPongPacket) {
         this.pongTicks = 1;
      }
      return false;
   }

   @Override
   public void onTick(LocalPlayer player) {
      if (player == null || Minecraft.getInstance().level == null || Minecraft.getInstance().gameMode == null) {
         return;
      }

      if (this.has("from_hits") && this.module.is(VelocityModule.SADNES_JUMP_RESET)) {
         this.jumpResetTick(player);
      } else if (this.has("from_hits") && this.module.is(VelocityModule.SADNES_GRIM_STANDING)) {
         this.grimStandingTick(player);
      } else {
         this.resetState();
      }
   }

   @Override
   public void onInput(PlayerInputEvent event, LocalPlayer player) {
      if (this.module.is(VelocityModule.SADNES_GRIM_STANDING) && this.placing) {
         event.setKeyPresses(new Input(false, false, false, false, false, false, false));
         event.setMoveVector(Vec2.ZERO);
         return;
      }

      if (!this.module.is(VelocityModule.SADNES_JUMP_RESET) || !this.jumpResetActive || this.knockbackDirection == null) {
         return;
      }
      if (this.has("from_hits")) {
         double target = Math.toDegrees(Math.atan2(this.knockbackDirection.x, -this.knockbackDirection.z));
         float bestForward = 0.0F;
         float bestStrafe = 0.0F;
         double bestDelta = Double.MAX_VALUE;

         for (float forward = -1.0F; forward <= 1.0F; forward++) {
            for (float strafe = -1.0F; strafe <= 1.0F; strafe++) {
               if (forward != 0.0F || strafe != 0.0F) {
                  double direction = Math.toDegrees(directionFromInput(player.getYRot(), forward, strafe));
                  double delta = Math.abs(Mth.wrapDegrees(target - direction));
                  if (delta < bestDelta) {
                     bestDelta = delta;
                     bestForward = forward;
                     bestStrafe = strafe;
                  }
               }
            }
         }

         event.setMoveVector(new Vec2(bestStrafe, bestForward));
      } else {
         this.jumpResetActive = false;
      }
   }

   private void jumpResetTick(LocalPlayer player) {
      if (this.pendingJump && player.onGround()) {
         this.pendingJump = false;
      }

      if (!this.jumpResetActive || this.knockbackDirection == null) {
         return;
      }

      if (!player.onGround()) {
         this.leftGround = true;
      }

      double progress = (player.getX() - this.knockbackOrigin.x) * this.knockbackDirection.x
              + (player.getZ() - this.knockbackOrigin.z) * this.knockbackDirection.z;
      boolean movedBack = this.leftGround && progress <= 0.0;
      if (this.leftGround && player.onGround()) {
         this.jumpResetActive = false;
         this.leftGround = false;
      } else if (movedBack || ++this.jumpResetTicks > MAX_TICKS) {
         this.jumpResetActive = false;
         this.leftGround = false;
      }
   }

   private void grimStandingTick(LocalPlayer player) {
      if (this.blockUpdateTicks > 0 && --this.blockUpdateTicks == 0) {
         this.awaitingPong = true;
         this.waitingToPlace = false;
      }

      if (this.pongTicks > 0 && --this.pongTicks == 0) {
         this.placing = false;
         this.awaitingPong = false;
      }

      if (this.waitingToPlace && !this.interactedThisTick && !player.isUsingItem()) {
         if (this.placeDelayTicks > 0) {
            this.placeDelayTicks--;
         } else {
            this.pendingPlaceHit = this.findPlaceTarget(player);
         }
      }

      if (this.pendingPlaceHit != null) {
         this.delayingPackets = false;
         this.flushDeferred();
         Minecraft instance = Minecraft.getInstance();
         if (instance.gameMode != null) {
            instance.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, this.pendingPlaceHit);
         }
         player.swing(InteractionHand.MAIN_HAND);
         player.connection.send(new Rot(player.getYRot(), 90.0F, player.onGround(), player.horizontalCollision));
         this.placedPos = player.blockPosition();
         this.placingTicks = 0;
         this.placing = true;
         this.pendingPlaceHit = null;
         this.waitingToPlace = false;
      }

      if (this.placing) {
         if (++this.placingTicks > MAX_TICKS) {
            this.placing = false;
            this.awaitingPong = false;
            this.waitingToPlace = false;
         }
      }

      this.interactedThisTick = false;
   }

   private BlockHitResult findPlaceTarget(LocalPlayer player) {
      Minecraft instance = Minecraft.getInstance();
      if (instance.level == null) {
         return null;
      }
      Vec3 eye = player.getEyePosition();
      BlockHitResult hit = instance.level.clip(new ClipContext(
              eye, eye.add(0.0, -3.0, 0.0), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
      if (hit.getType() != Type.BLOCK) {
         return null;
      }
      return hit.getBlockPos().relative(hit.getDirection()).equals(player.blockPosition()) ? hit : null;
   }

   private void defer(Packet<?> packet) {
      if (this.deferred.size() < 64) {
         this.deferred.add(packet);
      }
   }

   private void flushDeferred() {
      if (this.deferred.isEmpty()) {
         return;
      }
      List<Packet<?>> queued = new ArrayList<>(this.deferred);
      this.deferred.clear();
      Minecraft instance = Minecraft.getInstance();
      ClientPacketListener listener = instance.getConnection();
      if (listener == null) {
         return;
      }
      instance.execute(() -> {
         for (Packet<?> packet : queued) {
            Packet raw = packet;
            raw.handle(listener);
         }
      });
   }

   /** {@code MoveUtil.getDirectionFromInput(yaw, forward, strafe)} источника. */
   private static float directionFromInput(float yaw, float forward, float strafe) {
      float direction = (float) Math.toRadians(yaw);
      float sin = Mth.sin(direction);
      float cos = Mth.cos(direction);
      return (float) Math.atan2(-sin * strafe + cos * forward, cos * strafe + sin * forward);
   }

   private void resetState() {
      this.damageReceived = false;
      this.delayingPackets = false;
      this.waitingToPlace = false;
      this.awaitingPong = false;
      this.placing = false;
      this.interactedThisTick = false;
      this.pendingJump = false;
      this.jumpResetActive = false;
      this.leftGround = false;
      this.placeDelayTicks = 0;
      this.blockUpdateTicks = -1;
      this.pongTicks = -1;
      this.placingTicks = 0;
      this.pendingPlaceHit = null;
      this.placedPos = null;
      this.deferred.clear();
   }

   @Override
   public void reset() {
      this.flagPauseUntil = 0L;
      this.knockbackDirection = null;
      this.knockbackOrigin = null;
      this.jumpResetTicks = 0;
      this.resetState();
   }
}
