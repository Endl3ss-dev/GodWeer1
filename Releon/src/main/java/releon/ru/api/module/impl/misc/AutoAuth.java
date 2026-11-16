package releon.ru.api.module.impl.misc;

import java.util.Locale;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import releon.ru.api.events.annotation.SubscribeEvent;
import releon.ru.api.events.impl.PacketEvent;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;
import releon.ru.api.settings.impl.StringSetting;

public final class AutoAuth extends Module {
   private final StringSetting password = this.register(new StringSetting("Password", "Server auth password.", "ReleonPass123", 64));

   public AutoAuth() {
      super("Auto Auth", "Automatically sends /login and /register commands.", ModuleCategory.MISC);
   }

   @SubscribeEvent
   private void onPacket(PacketEvent event) {
      if (event.isReceive() && mc.player != null && mc.world != null && mc.player.networkHandler != null) {
         if (event.getPacket() instanceof GameMessageS2CPacket packet) {
            String pass = this.password.getValue() == null ? "" : this.password.getValue().trim();
            if (pass.length() >= 4) {
               String message = packet.content().getString().toLowerCase(Locale.ROOT);
               if (!message.contains("войдите") && !message.contains("/login")) {
                  if (message.contains("зарегистрируйтесь") || message.contains("регистрация") || message.contains("/reg")) {
                     mc.player.networkHandler.sendChatCommand("reg " + pass + " " + pass);
                  }
               } else {
                  mc.player.networkHandler.sendChatCommand("login " + pass);
               }
            }
         }
      }
   }
}
