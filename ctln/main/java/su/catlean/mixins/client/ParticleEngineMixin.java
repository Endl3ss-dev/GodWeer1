// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.render.RenderParticleEvent;

@Mixin({ParticleEngine.class})
public class ParticleEngineMixin {

   public ParticleEngineMixin() {
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"add(Lnet/minecraft/client/particle/Particle;)V"},
      cancellable = true
   )
   public void addParticleHook(Particle particle, CallbackInfo callbackInfo) {
      if (RenderParticleEvent.INSTANCE.call(particle)) {
         callbackInfo.cancel();
      }
   }
}