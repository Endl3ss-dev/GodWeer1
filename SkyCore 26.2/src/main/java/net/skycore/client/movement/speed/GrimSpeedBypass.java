package net.skycore.client.movement.speed;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

/**
 * Профили обхода GrimAC для Speedv2 (Releon ReallyWorld).
 * Таймер-фаза Releon (заряд 0.2 / буст 1.8) на Grim ломается о Timer/Prediction,
 * поэтому профиль ограничивает множитель, длительность буста и добавляет
 * декларацию позиции пакетами — Grim считает движение по транзакциям, и
 * объявленное смещение он принимает вместо предсказанного.
 */
public final class GrimSpeedBypass {
   public static final String VERSION_2366 = "2.3.66";
   public static final String VERSION_2367 = "2.3.67";
   public static final String VERSION_2368 = "2.3.68";
   public static final String VERSION_2369 = "2.3.69";
   public static final String VERSION_2370 = "2.3.70";
   public static final String VERSION_2371 = "2.3.71";

   public static final String[] VERSIONS = {
      VERSION_2366, VERSION_2367, VERSION_2368, VERSION_2369, VERSION_2370, VERSION_2371
   };

   public static final class Profile {
      public final float maxMultiplier;
      public final long maxBoostMs;
      public final boolean positionPackets;
      public final long setbackCooldownMs;

      private Profile(float maxMultiplier, long maxBoostMs, boolean positionPackets, long setbackCooldownMs) {
         this.maxMultiplier = maxMultiplier;
         this.maxBoostMs = maxBoostMs;
         this.positionPackets = positionPackets;
         this.setbackCooldownMs = setbackCooldownMs;
      }
   }

   public static Profile profile(String version) {
      if (VERSION_2366.equals(version)) {
         return new Profile(1.55F, 1200L, false, 1200L);
      } else if (VERSION_2367.equals(version)) {
         return new Profile(1.60F, 1300L, true, 1200L);
      } else if (VERSION_2368.equals(version)) {
         return new Profile(1.65F, 1400L, true, 1400L);
      } else if (VERSION_2369.equals(version)) {
         return new Profile(1.70F, 1500L, true, 1400L);
      } else if (VERSION_2370.equals(version)) {
         return new Profile(1.75F, 1600L, true, 1600L);
      } else {
         return new Profile(1.80F, 1700L, true, 1600L);
      }
   }

   /** Декларация собственной позиции: Grim сверяет движение с транзакцией, а не только с предсказанием. */
   public static void sendPosition(LocalPlayer player) {
      if (player != null && player.connection != null) {
         player.connection.send(new ServerboundMovePlayerPacket.Pos(
            player.getX(), player.getY(), player.getZ(), player.onGround(), player.horizontalCollision));
      }
   }

   /** Статус-пакет перед прыжком: сбрасывает накопленное предсказание Grim по вертикали. */
   public static void sendStatus(LocalPlayer player) {
      if (player != null && player.connection != null) {
         player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(player.onGround(), player.horizontalCollision));
      }
   }

   private GrimSpeedBypass() {
   }
}
