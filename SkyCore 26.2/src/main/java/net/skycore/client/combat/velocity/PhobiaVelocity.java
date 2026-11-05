package net.skycore.client.combat.velocity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.module.movement.VelocityModule;

/**
 * Phobia — {@code Phobia/src/main/java/ruhack/phobia/system/modulesystem/impl/rage/Velocity.java}
 * (MC 1.21.11). Настройка «Режим»: {@code New Grim / Old Grim / Matrix / Vanilla}.
 *
 * <p>Пакеты обрабатываются только когда игрок не в воде и не в лаве; {@code ccCooldown}
 * уменьшается на каждом входящем пакете и блокирует обработку, пока больше нуля.
 * <ul>
 *   <li>{@code Vanilla} — отмена пакета скорости;</li>
 *   <li>{@code Old Grim} — отмена пакета скорости и {@code grimTicks = 6};</li>
 *   <li>{@code New Grim} — отмена пакета скорости и флаг {@code true};</li>
 *   <li>{@code Matrix} — пакеты отменяются по очереди: первый подряд, второй — с запоминанием
 *       {@code (-0.1x, y, -0.1z)} и применением на следующем тике; на тике при
 *       {@code hurtTime > 0} в воздухе горизонтальная скорость разворачивается вдоль взгляда,
 *       а спринт мигает каждый второй тик.</li>
 * </ul>
 *
 * <p><b>Важно:</b> в источнике тик {@code New Grim} и отмена пингов {@code Old Grim} сравнивают
 * режим со строками {@code "NewGrim"} / {@code "OldGrim"} (без пробела), а значения настройки —
 * {@code "New Grim"} / {@code "Old Grim"}; {@code SelectSetting#isSelected} сравнивает строки через
 * {@code equals}, поэтому обе ветки в Phobia не выполняются никогда. Перенесено один в один:
 * константы {@link #SOURCE_NEW_GRIM_KEY} и {@link #SOURCE_OLD_GRIM_KEY} оставлены с той же
 * опечаткой, чтобы поведение совпадало с исходным клиентом. Правильные сравнения есть в
 * {@link ReleonVelocity} — там источник написан без опечатки.
 */
public final class PhobiaVelocity implements VelocityHandler {
   /** Опечатка исходника: сравнение идёт со строкой без пробела. */
   static final String SOURCE_NEW_GRIM_KEY = "NewGrim";
   /** Опечатка исходника: сравнение идёт со строкой без пробела. */
   static final String SOURCE_OLD_GRIM_KEY = "OldGrim";

   private final VelocityModule module;
   private boolean flag;
   private int grimTicks;
   private int ccCooldown;
   private Vec3 pendingVelocity;

   public PhobiaVelocity(VelocityModule module) {
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

      if (this.module.is(VelocityModule.PHOBIA_MATRIX)) {
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

      if (this.module.is(VelocityModule.PHOBIA_VANILLA)) {
         action.cancel();
         return;
      }

      if (this.module.is(VelocityModule.PHOBIA_OLD_GRIM)) {
         action.cancel();
         this.grimTicks = 6;
         return;
      }

      if (this.module.is(VelocityModule.PHOBIA_NEW_GRIM)) {
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

      // Опечатка исходника: "OldGrim" никогда не совпадёт с "Old Grim".
      if (this.module.is(SOURCE_OLD_GRIM_KEY) && packet instanceof ClientboundPingPacket && this.grimTicks > 0) {
         this.grimTicks--;
         return true;
      }
      return false;
   }

   @Override
   public void onTick(LocalPlayer player) {
      if (player == null || this.inWater(player)) {
         return;
      }
      if (this.module.is(VelocityModule.PHOBIA_MATRIX)) {
         this.matrixTick(player);
      }
      // Опечатка исходника: "NewGrim" никогда не совпадёт с "New Grim".
      if (this.module.is(SOURCE_NEW_GRIM_KEY) && this.flag) {
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
         double speed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
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
      this.flag = false;
      this.grimTicks = 0;
      this.ccCooldown = 0;
      this.pendingVelocity = null;
   }
}
