package net.skycore.client.movement.speed;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

 
public final class SpeedFlyingSpeed {
   private static float override = Float.NaN;

   public static void set(float value) {
      override = Float.isFinite(value) && value > 0.0F ? value : Float.NaN;
   }

   public static void clear() {
      override = Float.NaN;
   }

   public static boolean isSet() {
      return !Float.isNaN(override);
   }

   public static float apply(Player player, float original) {
      if (Float.isNaN(override)) {
         return original;
      }

      LocalPlayer self = Minecraft.getInstance().player;
      if (self == null || self != player) {
         return original;
      }

      return self.getAbilities().flying ? original : override;
   }

   private SpeedFlyingSpeed() {
   }
}
