package net.skycore.client.combat.velocity;

import net.minecraft.client.Minecraft;
import net.skycore.client.module.movement.VelocityModule;

/**
 * AlphaDLC — {@code AlphaDLC/src/main/java/zov/alphadlc/module/list/combat/Velocity.java}
 * (MC 1.21.4). Настроек нет: модуль отменяет любой пакет скорости своего игрока.
 *
 * <p>Источник работает на своём {@code EventPacket} и проверяет только
 * {@code packet.getEntityId() == mc.player.getId()}, без проверок воды и полёта — перенесено как есть.
 * В списке режимов SkyCore это единственный вариант клиента: {@code AlphaDLC [Cancel]}.
 */
public final class AlphaDlcVelocity implements VelocityHandler {

   @Override
   public void onVelocityPacket(VelocityAction action) {
      if (Minecraft.getInstance().player != null) {
         action.cancel();
      }
   }
}
