package net.skycore.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.CancellableEvent;
import net.skycore.client.event.WorldChangeEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.movement.ScaffoldCoachModule;
import net.skycore.client.module.combat.AttackAuraModule;
import net.skycore.client.module.combat.AutoExplosionModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftGameUpdateMixin {
   @Unique
   private long skycore$lastGameUpdate = System.nanoTime();

   @Inject(method = "renderFrame", at = @At("HEAD"))
   private void skycore$gameUpdate(boolean condition, CallbackInfo callbackInfo) {
      ScaffoldCoachModule.INSTANCE.onFrame(System.nanoTime());
      AttackAuraModule aura = SkycoreClient.modules().get(AttackAuraModule.class).filter(ClientModule::isEnabled).orElse(null);
      if (aura == null) return;
      long now = System.nanoTime();
      aura.onRenderFrame(now); // exactly once, outside the existing attack-clock catch-up loop
      long slots = Math.max(0L, (now - this.skycore$lastGameUpdate) / 4166666L);
      this.skycore$lastGameUpdate += slots * 4166666L;
      for (long i = 0; i < Math.min(slots, 65L); i++) aura.onGameUpdate();
   }

   @Inject(method = "setLevel", at = @At("HEAD"))
   private void skycore$worldChange(ClientLevel level, CallbackInfo callbackInfo) {
      SkycoreClient.events().post(new WorldChangeEvent());
   }

   @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
   private void skycore$startUseItem(CallbackInfo callbackInfo) {
      if (ScaffoldCoachModule.INSTANCE.handlesUse()) {
         callbackInfo.cancel();
         return;
      }
      if (AutoExplosionModule.INSTANCE.isEnabled()) {
         CancellableEvent cancellableEvent = new CancellableEvent();
         SkycoreClient.events().post(cancellableEvent);
         if (cancellableEvent.isCancelled()) {
            callbackInfo.cancel();
         }
      }
   }
}
