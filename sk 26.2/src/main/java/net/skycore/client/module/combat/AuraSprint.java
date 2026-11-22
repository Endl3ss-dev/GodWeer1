package net.skycore.client.module.combat;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.Setting;

/**
 * Sprint reset, ported from MotherHack Aura#handleSprint
 * (MotherHack/src/main/java/fun/motherhack/modules/impl/combat/Aura.java).
 *
 * <p>Packet mode re-sends START_SPRINTING before every attack. Vanilla clears the sprint flag on a
 * sprint-knockback hit, so refreshing it keeps the server classifying the next hit as a
 * sprint-knockback attack (entity.player.attack.knockback) even while the player stands still.
 * Legit only stops sprinting while airborne, exactly like the source module.
 */
final class AuraSprint {
   static final String NONE = "None";
   static final String LEGIT = "Legit";
   static final String PACKET = "Packet";

   final ModeSetting mode = new ModeSetting("Sprint reset", NONE, NONE, LEGIT, PACKET);
   final Setting<?>[] settings = {mode};

   /** Called immediately before every attack the aura sends. */
   void beforeAttack(LocalPlayer player) {
      if (player == null || player.connection == null) {
         return;
      }
      if (this.mode.is(PACKET)) {
         player.connection.send(new ServerboundPlayerCommandPacket(player, Action.STOP_SPRINTING));
         player.connection.send(new ServerboundPlayerCommandPacket(player, Action.START_SPRINTING));
         player.setSprinting(true);
      } else if (this.mode.is(LEGIT) && player.isSprinting() && !player.onGround()) {
         player.setSprinting(false);
         player.connection.send(new ServerboundPlayerCommandPacket(player, Action.STOP_SPRINTING));
      }
   }
}
