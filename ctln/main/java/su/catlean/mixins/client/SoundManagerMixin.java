// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.client.CloseSoundManagerEvent;

@Mixin({SoundManager.class})
public class SoundManagerMixin {

   public SoundManagerMixin() {
   }

   @Inject(
      method = {"destroy"},
      at = {@At("HEAD")}
   )
   public void close(CallbackInfo callbackInfo) {
      CloseSoundManagerEvent.INSTANCE.call();
   }
}