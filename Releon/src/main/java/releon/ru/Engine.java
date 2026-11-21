package releon.ru;

import lightning.sdk.LightningSDK;
import lightning.sdk.annotation.LgVM;
import lightning.sdk.annotation.LgVM.Level;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import releon.ru.manager.Manager;
import releon.ru.utils.render.item.RenderItem;
import releon.ru.utils.render.ui.Render2D;
import releon.ru.utils.sounds.SoundManager;

public class Engine implements ModInitializer, ClientModInitializer {
   private final Manager manager = new Manager();

   public Engine() {
   }

   @LgVM(Level.FULL)
   public void onInitialize() {
      LightningSDK.setDev(true);
      LightningSDK.requireAuth();
      if (LightningSDK.checkAuthAlive()) {
         SoundManager.init();
         Render2D.init();
         RenderItem.init();
      }
   }

   public void onInitializeClient() {
      this.manager.initClient();
   }
}
