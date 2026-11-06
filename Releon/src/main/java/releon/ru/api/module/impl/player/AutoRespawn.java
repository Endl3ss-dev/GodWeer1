package releon.ru.api.module.impl.player;

import net.minecraft.client.MinecraftClient;
import releon.ru.api.events.annotation.SubscribeEvent;
import releon.ru.api.events.impl.DeathScreenEvent;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;
import releon.ru.api.settings.impl.ModeSetting;

public final class AutoRespawn extends Module {
   private final ModeSetting mode = this.register(new ModeSetting("Mode", "Respawn mode.", "Default", "Default"));

   public AutoRespawn() {
      super("Auto Respawn", "Automatically respawns after death.", ModuleCategory.PLAYER);
   }

   @SubscribeEvent
   private void onDeathScreen(DeathScreenEvent event) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (this.mode.is("Default") && client.player != null) {
         client.player.requestRespawn();
         client.setScreen(null);
      }
   }
}
