package releon.ru.mixin;

import net.minecraft.client.gui.screen.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.api.events.impl.DeathScreenEvent;
import releon.ru.manager.Manager;

@Mixin({DeathScreen.class})
public abstract class DeathScreenMixin {
   public DeathScreenMixin() {
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void releon$deathScreenTick(CallbackInfo ci) {
      Manager.postEvent(new DeathScreenEvent());
   }
}
