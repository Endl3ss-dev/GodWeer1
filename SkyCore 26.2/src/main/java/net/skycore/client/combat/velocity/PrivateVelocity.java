package net.skycore.client.combat.velocity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.module.movement.VelocityModule;

/**
 * Private (категория Combat) —
 * {@code Private/src/java/org/privates/client/managers/module/impl/combat/Velocity.java}
 * (легаси-маппинги, {@code SEntityVelocityPacket}).
 *
 * <p>Выбираемые в источнике режимы: {@code Matrix / Vulcan / Intave / AAC v4 / NCP / Reverse}.
 * В исходном {@code switch} есть ещё три ветки, которых нет в списке режимов — {@code Packet},
 * {@code AAC Push} и {@code Grim}; они перенесены отдельными режимами
 * {@code Private [Packet]}, {@code Private [AAC Push]}, {@code Private [Grim]}, чтобы ничего не
 * потерять. Слайдеры «Горизонталь %» / «Вертикаль %» в источнике использует только {@code Packet}.
 *
 * <p>Перенос один в один:
 * <ul>
 *   <li>{@code Packet} — при нулевых процентах отмена, иначе компоненты умножаются на проценты;</li>
 *   <li>{@code Matrix} — {@code motionX *= 0.6}, {@code motionZ *= 0.6};</li>
 *   <li>{@code Vulcan} — на земле отправляет подтверждение телепорта
 *       ({@code CConfirmTeleportPacket} → {@code ServerboundAcceptTeleportationPacket}) с растущим
 *       {@code teleportId} и отменяет пакет скорости; телепорт с тем же {@code id}
 *       ({@code SPlayerPositionLookPacket#getTeleportId} → {@code ClientboundPlayerPositionPacket#id()})
 *       отменяется;</li>
 *   <li>{@code Intave} — на тике при {@code hurtTime > 0} обнуляет вертикальную скорость и
 *       выставляет {@code onGround = true};</li>
 *   <li>{@code AAC Push} — при {@code hurtTime > 0} скорость умножается на {@code (-0.5, 1, -0.5)},
 *       пакет всё равно отменяется;</li>
 *   <li>{@code AAC v4} — при {@code hurtTime > 0} и чётном {@code ticksExisted} добавляет
 *       {@code +0.01} к вертикальной скорости и отменяет пакет;</li>
 *   <li>{@code NCP} — при {@code hurtTime > 0} оставляет только вертикальную скорость;</li>
 *   <li>{@code Reverse} — {@code motionX = -motionX}, {@code motionY = 0}, {@code motionZ = -motionZ};</li>
 *   <li>{@code Grim} — на тике при {@code hurtTime > 0} отправляет {@code CPlayerPacket(true)},
 *       то есть {@code StatusOnly} с {@code onGround = true}.</li>
 * </ul>
 */
public final class PrivateVelocity implements VelocityHandler {
   private final VelocityModule module;
   private boolean receivedTeleport;
   private int teleportId;

   public PrivateVelocity(VelocityModule module) {
      this.module = module;
   }

   @Override
   public void onVelocityPacket(VelocityAction action) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         return;
      }

      if (this.module.is(VelocityModule.PRIVATE_PACKET)) {
         double horizontal = this.module.privateHorizontal().get();
         double vertical = this.module.privateVertical().get();
         if (horizontal == 0.0 && vertical == 0.0) {
            action.cancel();
         } else {
            action.scale(horizontal / 100.0, vertical / 100.0, horizontal / 100.0);
         }
         return;
      }

      if (this.module.is(VelocityModule.PRIVATE_MATRIX)) {
         action.scale(0.6, 1.0, 0.6);
         return;
      }

      if (this.module.is(VelocityModule.PRIVATE_VULCAN)) {
         if (player.onGround()) {
            player.connection.send(new ServerboundAcceptTeleportationPacket(++this.teleportId));
            this.receivedTeleport = true;
         }
         action.cancel();
         return;
      }

      if (this.module.is(VelocityModule.PRIVATE_AAC_PUSH)) {
         if (player.hurtTime > 0) {
            Vec3 before = action.before();
            action.set(before.x * -0.5, before.y, before.z * -0.5);
         } else {
            action.cancel();
         }
         return;
      }

      if (this.module.is(VelocityModule.PRIVATE_AAC_V4)) {
         if (player.hurtTime > 0 && player.tickCount % 2 == 0) {
            Vec3 before = action.before();
            action.set(before.x, before.y + 0.01, before.z);
         } else {
            action.cancel();
         }
         return;
      }

      if (this.module.is(VelocityModule.PRIVATE_NCP)) {
         if (player.hurtTime > 0) {
            Vec3 before = action.before();
            action.set(0.0, before.y, 0.0);
         } else {
            action.cancel();
         }
         return;
      }

      if (this.module.is(VelocityModule.PRIVATE_REVERSE)) {
         Vec3 velocity = action.packetVelocity();
         action.set(-velocity.x, 0.0, -velocity.z);
      }
   }

   @Override
   public boolean onInboundPacket(Packet<?> packet) {
      if (this.module.is(VelocityModule.PRIVATE_VULCAN)
              && this.receivedTeleport
              && packet instanceof ClientboundPlayerPositionPacket position
              && position.id() == this.teleportId) {
         this.receivedTeleport = false;
         return true;
      }
      return false;
   }

   @Override
   public void onTick(LocalPlayer player) {
      if (player == null) {
         return;
      }

      if (this.module.is(VelocityModule.PRIVATE_INTAVE)) {
         if (player.hurtTime > 0) {
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(new Vec3(motion.x, 0.0, motion.z));
            player.setOnGround(true);
         }
         return;
      }

      if (this.module.is(VelocityModule.PRIVATE_GRIM) && player.hurtTime > 0) {
         player.connection.send(new StatusOnly(true, false));
      }
   }

   @Override
   public void reset() {
      this.receivedTeleport = false;
      this.teleportId = 0;
   }
}
