package releon.ru.api.module.impl.misc;

import java.util.Locale;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.CopyToClipboard;
import releon.ru.api.events.annotation.SubscribeEvent;
import releon.ru.api.events.impl.PacketEvent;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;

public final class DeathCoords extends Module {
   public DeathCoords() {
      super("Death Coords", "Prints death coordinates to chat.", ModuleCategory.MISC);
   }

   @SubscribeEvent
   private void onPacket(PacketEvent event) {
      if (event.isReceive() && mc.player != null && mc.world != null) {
         if (event.getPacket() instanceof DeathMessageS2CPacket) {
            String coords = String.format(Locale.ROOT, "%.0f %.0f %.0f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            Text text = Text.literal(coords).styled(style -> style.withClickEvent(new CopyToClipboard(coords)));
            mc.player.sendMessage(text, false);
         }
      }
   }
}
