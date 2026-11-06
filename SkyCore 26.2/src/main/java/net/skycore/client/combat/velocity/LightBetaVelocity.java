package net.skycore.client.combat.velocity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.skycore.client.module.movement.VelocityModule;

/**
 * LightBeta — {@code LightBeta/src/main/java/tech/lightclient/modules/impl/combat/NoVelocity.java}
 * (MC 1.21.11). Режимы {@code Mode}: {@code Cancel / Jump / HwTest}.
 *
 * <p>Перенос один в один:
 * <ul>
 *   <li>{@code Cancel} — отменяет пакет скорости своего игрока <b>и любой пакет взрыва</b>;</li>
 *   <li>{@code HwTest} — то же плюс поднимает флаг; на следующем тике, если кулдаун (20 тиков)
 *       истёк, отправляет полный пакет позиции и {@code STOP_DESTROY_BLOCK} вниз под себя,
 *       затем обнуляет флаг и запускает кулдаун;</li>
 *   <li>{@code Jump} — пакеты не трогает: если здоровье уменьшилось и игрок на земле,
 *       зажимает прыжок на один тик.</li>
 * </ul>
 *
 * <p>Поля {@code needJump} и {@code lastHealth} в источнике нужны только режиму {@code Jump};
 * {@code needJump} там не читается и перенесено как есть.
 */
public final class LightBetaVelocity implements VelocityHandler {
   private static final int CC_COOLDOWN_TICKS = 20;

   private final VelocityModule module;
   private float lastHealth = -1.0F;
   private boolean needJump;
   private boolean flag;
   private int ccCooldown;

   public LightBetaVelocity(VelocityModule module) {
      this.module = module;
   }

   @Override
   public void onVelocityPacket(VelocityAction action) {
      if (this.module.is(VelocityModule.LIGHTBETA_CANCEL)) {
         action.cancel();
         return;
      }
      if (this.module.is(VelocityModule.LIGHTBETA_HWTEST)) {
         action.cancel();
         this.flag = true;
      }
   }

   @Override
   public boolean onInboundPacket(Packet<?> packet) {
      if (!(packet instanceof ClientboundExplodePacket)) {
         return false;
      }
      if (this.module.is(VelocityModule.LIGHTBETA_CANCEL)) {
         return true;
      }
      if (this.module.is(VelocityModule.LIGHTBETA_HWTEST)) {
         this.flag = true;
         return true;
      }
      return false;
   }

   @Override
   public void onTick(LocalPlayer player) {
      if (this.module.is(VelocityModule.LIGHTBETA_HWTEST)) {
         this.lastHealth = -1.0F;
         if (this.flag) {
            this.handleNewGrimTick(player);
         }
         return;
      }

      this.flag = false;

      if (!this.module.is(VelocityModule.LIGHTBETA_JUMP)) {
         this.lastHealth = -1.0F;
         return;
      }

      float health = player.getHealth();
      if (this.lastHealth < 0.0F) {
         this.lastHealth = health;
         return;
      }

      if (health < this.lastHealth && player.onGround()) {
         Minecraft instance = Minecraft.getInstance();
         if (instance.options != null) {
            instance.options.keyJump.setDown(true);
         }
      }
      this.lastHealth = health;
   }

   private void handleNewGrimTick(LocalPlayer player) {
      if (this.ccCooldown <= 0) {
         player.connection.send(new PosRot(
                 player.getX(), player.getY(), player.getZ(),
                 player.getYRot(), player.getXRot(),
                 player.onGround(), false));
         player.connection.send(new ServerboundPlayerActionPacket(
                 Action.STOP_DESTROY_BLOCK, BlockPos.containing(player.position()), Direction.DOWN));
         this.ccCooldown = CC_COOLDOWN_TICKS;
      } else {
         this.ccCooldown--;
      }
      this.flag = false;
   }

   @Override
   public void reset() {
      this.lastHealth = -1.0F;
      this.needJump = false;
      this.flag = false;
      this.ccCooldown = 0;
   }
}
