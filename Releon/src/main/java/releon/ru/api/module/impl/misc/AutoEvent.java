package releon.ru.api.module.impl.misc;

import releon.ru.api.events.annotation.SubscribeEvent;
import releon.ru.api.events.impl.TickEvent;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;
import releon.ru.api.module.impl.combat.aura.util.StopWatch;
import releon.ru.api.settings.impl.NumberSetting;
import releon.ru.utils.network.Network;

public final class AutoEvent extends Module {
   private final NumberSetting triggerDelayMs = this.register(new NumberSetting("Delay ms", "Delay before sending event command.", 350.0, 0.0, 5000.0, 1.0));
   private final StopWatch enterWatch = new StopWatch();
   private final StopWatch sendCooldown = new StopWatch();
   private int lastAnarchy = -1;
   private boolean pendingCommand;

   public AutoEvent() {
      super("AutoEvent", "Auto Event", ModuleCategory.MISC);
   }

   @Override
   protected void onEnable() {
      this.pendingCommand = false;
      this.lastAnarchy = -1;
      this.enterWatch.reset();
      this.sendCooldown.reset();
   }

   @Override
   protected void onDisable() {
      this.pendingCommand = false;
      this.lastAnarchy = -1;
   }

   @SubscribeEvent
   private void onTick(TickEvent event) {
      if (mc.player != null && mc.world != null && mc.player.networkHandler != null) {
         int currentAnarchy = Network.getAnarchyMode();
         boolean enteredAnarchy = currentAnarchy > 0 && (this.lastAnarchy <= 0 || currentAnarchy != this.lastAnarchy);
         if (enteredAnarchy) {
            this.pendingCommand = true;
            this.enterWatch.reset();
         }

         if (currentAnarchy <= 0) {
            this.pendingCommand = false;
         }

         if (this.pendingCommand && this.enterWatch.finished(this.triggerDelayMs.getValue()) && this.sendCooldown.finished(1000.0)) {
            mc.player.networkHandler.sendChatCommand("event delay");
            this.pendingCommand = false;
            this.sendCooldown.reset();
         }

         this.lastAnarchy = currentAnarchy;
      }
   }
}
