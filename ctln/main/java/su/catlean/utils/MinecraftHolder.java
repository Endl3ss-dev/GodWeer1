// deobf by pusia
package su.catlean.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MinecraftHolder {
   @NotNull
   private static final Logger catlean = LoggerFactory.getLogger("catlean");

   @NotNull
   public static final Minecraft getMinecraft() {
      return Minecraft.getInstance();
   }

   @NotNull
   public static final LocalPlayer getLocalPlayer() {
      return getMinecraft().player;
   }

   @NotNull
   public static final ClientLevel getClientLevel() {
      return getMinecraft().level;
   }

   @NotNull
   public static final MultiPlayerGameMode getMultiPlayerGameMode() {
      return getMinecraft().gameMode;
   }

   @NotNull
   public static final ClientPacketListener getClientPacketListener() {
      return getMinecraft().getConnection();
   }

   public static final long getTime() {
      return System.currentTimeMillis();
   }

   @NotNull
   public static final Logger getCatlean() {
      return catlean;
   }
}