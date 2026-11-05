package net.skycore.client.combat.velocity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.module.movement.VelocityModule;

/**
 * Private (категория Combat) —
 * {@code Private/src/java/org/privates/client/managers/module/impl/combat/Velocity.java}
 * (легаси-маппинги, {@code SEntityVelocityPacket}). Настройка «Режим»:
 * {@code Matrix / Vulcan / Intave / AAC v4 / NCP / Reverse}.
 *
 * <p>Перенос один в один:
 * <ul>
 *   <li>{@code Matrix} — {@code motionX *= 0.6}, {@code motionZ *= 0.6};</li>
 *   <li>{@code Vulcan} — на земле отправляет подтверждение телепорта ({@code CConfirmTeleportPacket}
 *       с увеличивающимся {@code teleportId}) и отменяет пакет скорости;</li>
 *   <li>{@code Intave} — на тике при {@code hurtTime > 0} обнуляет вертикальную скорость и
 *       выставляет {@code onGround = true};</li>
 *   <li>{@code AAC v4} — при {@code hurtTime > 0} и чётном {@code tickCount} добавляет
 *       {@code +0.01} к вертикальной скорости и отменяет пакет;</li>
 *   <li>{@code NCP} — при {@code hurtTime > 0} оставляет только вертикальную скорость;</li>
 *   <li>{@code Reverse} — {@code motionX = -motionX}, {@code motionY = 0}, {@code motionZ = -motionZ}.</li>
 * </ul>
 *
 * <p>Слайдеры «Горизонталь %» / «Вертикаль %» источника используются только в ветке {@code Packet},
 * которой нет в списке режимов, поэтому эффекта ни у одного доступного режима они не имеют.
 *
 * <p>Отличие в переносе: источник отменяет пакет телепорта, у которого совпал {@code teleportId};
 * у {@code ClientboundPlayerPositionPacket} в 26.2 аксессор идентификатора не подтверждён, поэтому
 * отменяется первый телепорт после подтверждения — это единственное место, где поведение может
 * отличаться на один пакет.
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
              && packet instanceof ClientboundPlayerPositionPacket) {
         this.receivedTeleport = false;
         return true;
      }
      return false;
   }

   @Override
   public void onTick(LocalPlayer player) {
      if (!this.module.is(VelocityModule.PRIVATE_INTAVE)) {
         return;
      }
      if (player.hurtTime > 0) {
         Vec3 motion = player.getDeltaMovement();
         player.setDeltaMovement(new Vec3(motion.x, 0.0, motion.z));
         player.setOnGround(true);
      }
   }

   @Override
   public void reset() {
      this.receivedTeleport = false;
      this.teleportId = 0;
   }
}
