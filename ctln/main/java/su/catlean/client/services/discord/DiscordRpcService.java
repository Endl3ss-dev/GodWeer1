// deobf by pusia
package su.catlean.client.services.discord;

import kotlin.TuplesKt;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.client.PostTitleScreenInitEvent;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.basic.MoveFix;
import su.catlean.client.features.modules.misc.Assistant;
import su.catlean.client.features.modules.misc.DiscordRpc;
import su.catlean.client.features.ui.DiscordConfirmScreen;
import su.catlean.client.services.discord.ipc.DiscordPresence;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;

public final class DiscordRpcService implements Service {
   @NotNull
   public static final DiscordRpcService INSTANCE = new DiscordRpcService();
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   private static boolean flag;

   private DiscordRpcService() {
   }

   @Flow
   private final void onTick(TickEvent tickEvent) {
      if (DiscordRpc.INSTANCE.getEnabled()) {
         if (stopWatch.testValue2(5000)) {
            DiscordPresence instance = DiscordPresence.INSTANCE;
            instance.updateLongValue(1324838037661945876L);
            if (INSTANCE.getText().length() > 0) {
               instance.updateText(INSTANCE.getText());
            }

            instance.updatePair(TuplesKt.to("main", "By Pan4ur & 06ED"));
            instance.updatePair3(TuplesKt.to("Get", "https://catlean.su/"));
            instance.updateText2(INSTANCE.getText2());
         }
      } else if (DiscordPresence.INSTANCE.isFlag()) {
         DiscordPresence.INSTANCE.getUnit();
      }
   }

   private final String getText() {
      return MinecraftHolder.getMinecraft().getConnection() != null && MinecraftHolder.getClientPacketListener().getServerData() != null
         ? "Playing on " + MinecraftHolder.getClientPacketListener().getServerData().ip
         : (MinecraftHolder.getMinecraft().gui.screen() instanceof JoinMultiplayerScreen ? "Selecting server..." : "");
   }

   private final String getText2() {
      return "public beta test";
   }

   @Flow
   private final void onPostTitleScreenInit(PostTitleScreenInitEvent postTitleScreenInitEvent) {
      if (!flag && !ProxyManager.INSTANCE.getSharedDataSource().getSharedConfig().isFlag()) {
         Assistant.INSTANCE.updateFlag(true);
         DiscordRpc.INSTANCE.updateFlag(true);
         MoveFix.INSTANCE.updateFlag(true);
         MinecraftHolder.getMinecraft().gui.setScreen(new DiscordConfirmScreen());
         ProxyManager.INSTANCE.getSharedDataSource().getSharedConfig().setFlag(true);
      }

      flag = true;
   }
}