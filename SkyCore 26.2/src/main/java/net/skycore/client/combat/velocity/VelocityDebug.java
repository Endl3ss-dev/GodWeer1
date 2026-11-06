package net.skycore.client.combat.velocity;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.util.SendDonationHelper;

/**
 * Сообщения в чат для режимов с настройкой «Отладка».
 *
 * <p>Источники пишут их через свой {@code ChatUtil}; в SkyCore для клиентских сообщений есть
 * {@link SendDonationHelper}, поэтому текст и цвет сохранены, а канал — нет.
 */
public final class VelocityDebug {

   private VelocityDebug() {
   }

   public static void send(String text) {
      if (Minecraft.getInstance().player != null) {
         SendDonationHelper.send(Component.literal(ChatFormatting.GREEN + "[Velocity] " + text));
      }
   }

   public static void received(String prefix, Vec3 velocity) {
      if (Minecraft.getInstance().player != null) {
         SendDonationHelper.send(Component.literal(ChatFormatting.YELLOW + "[Velocity] " + prefix
                 + String.format(Locale.ROOT, " X=%.3f Y=%.3f Z=%.3f", velocity.x, velocity.y, velocity.z)));
      }
   }

   public static void received(double x, double y, double z) {
      if (Minecraft.getInstance().player != null) {
         SendDonationHelper.send(Component.literal(
                 ChatFormatting.YELLOW + String.format(Locale.ROOT, "[Velocity] Received: X=%.2f, Y=%.2f, Z=%.2f", x, y, z)));
      }
   }
}
