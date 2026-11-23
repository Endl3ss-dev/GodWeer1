// deobf by pusia
package su.catlean.client.features.modules.esp;

import com.mojang.authlib.GameProfile;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.client.services.ChatStaffScanner;
import su.catlean.utils.MinecraftHolder;

public final class PlayerListNames {
   @NotNull
   private final GameProfile profile;
   private boolean flag;
   @NotNull
   private final PlayerInfo playerInfo;
   @Nullable
   private String text;

   public PlayerListNames(GameProfile profile, boolean flag) {
      this.profile = profile;
      this.flag = flag;
      this.playerInfo = new PlayerInfo(this.profile, false);
   }

   @NotNull
   public final GameProfile getProfile() {
      return this.profile;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   @NotNull
   public final PlayerInfo getPlayerInfo() {
      return this.playerInfo;
   }

   @Nullable
   public final String getText() {
      if (this.text == null) {
         this.text = ChatStaffScanner.INSTANCE.fetchTabListDisplayName(this.playerInfo).getString();
      }

      return this.text;
   }

   private final String getText2() {
      String name = this.profile.name();
      Player name2 = ChatStaffScanner.INSTANCE.fetchName(name);
      if (name2 != null) {
         this.flag = false;
         return ChatFormatting.YELLOW.toString() + " (near)" + ChatFormatting.RESET;
      } else {
         PlayerInfo playerInfo2 = MinecraftHolder.getClientPacketListener().getPlayerInfo(name);
         if (MinecraftHolder.getClientPacketListener().getOnlinePlayers().stream().map(arg -> arg.getProfile().name()).noneMatch(arg2 -> arg2.equals(name))) {
            this.flag = true;
            return ChatFormatting.RED.toString() + " Spec!" + ChatFormatting.RESET;
         } else if (playerInfo2 != null && playerInfo2.getGameMode() == GameType.SPECTATOR) {
            this.flag = false;
            return ChatFormatting.YELLOW.toString() + " (gm3)" + ChatFormatting.RESET;
         } else {
            this.flag = false;
            return "";
         }
      }
   }

   @NotNull
   public final String getText3() {
      return this.getText2() + " " + this.getText();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object != null && Intrinsics.areEqual(this.getClass(), object.getClass())) {
         PlayerListNames object2 = (PlayerListNames)object;
         return Intrinsics.areEqual(this.profile.name(), object2.profile.name());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.getClass().hashCode();
   }
}