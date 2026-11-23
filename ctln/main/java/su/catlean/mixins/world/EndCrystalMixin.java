// deobf by pusia
package su.catlean.mixins.world;

import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.world.CrystalCreateEvent;

@Mixin({EndCrystal.class})
public class EndCrystalMixin {

   @Shadow
   public int time;
   @Unique
   float startAge = -1.0F;

   public EndCrystalMixin() {
   }

   @Inject(
      method = {"<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V"},
      at = {@At("TAIL")}
   )
   private void tickHook(CallbackInfo callbackInfo) {
      if (CrystalCreateEvent.INSTANCE.call() && this.startAge == -1.0F) {
         this.startAge = this.time;
         this.time = 0;
      }
   }
}