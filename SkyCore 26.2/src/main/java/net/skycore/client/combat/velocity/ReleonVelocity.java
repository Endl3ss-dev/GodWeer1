package net.skycore.client.combat.velocity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.module.movement.VelocityModule;

/**
 * Releon — {@code Releon/src/main/java/releon/ru/api/module/impl/combat/Velocity.java} (MC 1.21.11).
 * Настройка «Mode»: {@code New Grim / Old Grim / Matrix / Normal}.
 *
 * <p>Логика совпадает с {@link PhobiaVelocity}, но написана без опечатки в сравнениях, поэтому
 * здесь ветки действительно работают:
 * <ul>
 *   <li>{@code Normal} — отмена пакета скорости;</li>
 *   <li>{@code Old Grim} — отмена пакета скорости и {@code grimTicks = 6}; пока счётчик больше нуля,
 *       пинги ({@code CommonPingS2CPacket} в источнике) отменяются и счётчик уменьшается;</li>
 *   <li>{@code New Grim} — отмена пакета скорости и флаг; на тике отправляет полный пакет позиции
 *       и {@code STOP_DESTROY_BLOCK} вниз, но только если {@code ccCooldown <= 0}; телепорт
 *       поднимает {@code ccCooldown = 5};</li>
 *   <li>{@code Matrix} — чередование отмены пакетов и запоминание {@code (-0.1x, y, -0.1z)}.</li>
 * </ul>
 *
 * <p>На тике проверяются только вода и погружение (лава — лишь в обработчике пакетов), как в источнике.
 */
public final class ReleonVelocity implements VelocityHandler {
   private final VelocityModule module;
   private boolean flag;
   private int grimTicks;
   private int ccCooldown;
   private Vec3 pendingVelocity;

   public ReleonVelocity(VelocityModule module) {
      this.module = module;
   }

   @Override
   public void onVelocityPacket(VelocityAction action) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || this.inWater(player)) {
         return;
      }
      if (this.ccCooldown > 0) {
         this.ccCooldown--;
         return;
      }

      if (this.module.is(VelocityModule.RELEON_MATRIX)) {
         if (!this.flag) {
            action.cancel();
            this.flag = true;
         } else {
            this.flag = false;
            action.cancel();
            Vec3 velocity = action.packetVelocity();
            this.pendingVelocity = new Vec3(velocity.x * -0.1, velocity.y, velocity.z * -0.1);
         }
         return;
      }

      if (this.module.is(VelocityModule.RELEON_NORMAL)) {
         action.cancel();
         return;
      }

      if (this.module.is(VelocityModule.RELEON_OLD_GRIM)) {
         action.cancel();
         this.grimTicks = 6;
         return;
      }

      if (this.module.is(VelocityModule.RELEON_NEW_GRIM)) {
         action.cancel();
         this.flag = true;
      }
   }

   @Override
   public boolean onInboundPacket(Packet<?> packet) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null || this.inWater(player)) {
         return false;
      }
      if (this.ccCooldown > 0) {
         this.ccCooldown--;
         return false;
      }

      if (this.module.is(VelocityModule.RELEON_OLD_GRIM) && packet instanceof ClientboundPingPacket && this.grimTicks > 0) {
         this.grimTicks--;
         return true;
      }

      if (packet instanceof ClientboundPlayerPositionPacket && this.module.is(VelocityModule.RELEON_NEW_GRIM)) {
         this.ccCooldown = 5;
      }
      return false;
   }

   @Override
   public void onTick(LocalPlayer player) {
      if (player == null || this.inWater(player) || player.isUnderWater()) {
         return;
      }
      if (this.module.is(VelocityModule.RELEON_MATRIX)) {
         this.matrixTick(player);
      }
      if (this.module.is(VelocityModule.RELEON_NEW_GRIM) && this.flag) {
         this.newGrimTick(player);
      }
      if (this.grimTicks > 0) {
         this.grimTicks--;
      }
   }

   private void matrixTick(LocalPlayer player) {
      if (this.pendingVelocity != null) {
         player.setDeltaMovement(this.pendingVelocity);
         this.pendingVelocity = null;
      }

      if (player.hurtTime > 0 && !player.onGround()) {
         Vec3 motion = player.getDeltaMovement();
         double yaw = Math.toRadians(player.getYRot());
         double speed = Math.hypot(motion.x, motion.z);
         player.setDeltaMovement(new Vec3(-Math.sin(yaw) * speed, motion.y, Math.cos(yaw) * speed));
         player.setSprinting(player.tickCount % 2 != 0);
      }
   }

   private void newGrimTick(LocalPlayer player) {
      if (this.ccCooldown <= 0) {
         player.connection.send(new PosRot(
                 player.getX(), player.getY(), player.getZ(),
                 player.getYRot(), player.getXRot(),
                 player.onGround(), false));
         player.connection.send(new ServerboundPlayerActionPacket(
                 Action.STOP_DESTROY_BLOCK, BlockPos.containing(player.position()), Direction.DOWN));
      }
      this.flag = false;
   }

   /** {@code !isTouchingWater && !isSubmergedInWater && !isInLava} источника. */
   private boolean inWater(LocalPlayer player) {
      return player.isInWater() || player.isUnderWater() || player.isInLava();
   }

   @Override
   public void reset() {
      this.grimTicks = 0;
      this.flag = false;
      this.ccCooldown = 0;
      this.pendingVelocity = null;
   }
}
