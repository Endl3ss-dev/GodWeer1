// deobf by pusia
package su.catlean.mixins.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.world.AfterEntityRemove;
import su.catlean.api.event.events.world.AfterEntitySpawn;
import su.catlean.api.event.events.world.EntityRemove;
import su.catlean.api.event.events.world.EntitySpawn;
import su.catlean.mixins.accessors.ClientLevelAccessor;

@Mixin({ClientLevel.class})
public class ClientLevelMixin {

   public ClientLevelMixin() {
   }

   @Inject(
      method = {"addEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void addEntityHook(Entity entity, CallbackInfo callbackInfo) {
      if (EntitySpawn.INSTANCE.call(entity)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"addEntity"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void addEntityHookPost(Entity entity, CallbackInfo callbackInfo) {
      if (AfterEntitySpawn.INSTANCE.call(entity)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"removeEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void removeEntityHook(int value, RemovalReason removalReason, CallbackInfo callbackInfo) {
      ClientLevel thisValue = (ClientLevel)(Object)this;
      if (thisValue != null && EntityRemove.INSTANCE.call((Entity)((ClientLevelAccessor)thisValue).iGetEntityLookup().getEntityGetter().get(value))) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"removeEntity"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void removeEntityHookPost(int value, RemovalReason removalReason, CallbackInfo callbackInfo) {
      ClientLevel thisValue = (ClientLevel)(Object)this;
      if (thisValue != null && AfterEntityRemove.INSTANCE.call((Entity)((ClientLevelAccessor)thisValue).iGetEntityLookup().getEntityGetter().get(value))) {
         callbackInfo.cancel();
      }
   }
}