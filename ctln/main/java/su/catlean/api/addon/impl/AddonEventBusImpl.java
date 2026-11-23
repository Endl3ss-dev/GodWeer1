// deobf by pusia
package su.catlean.api.addon.impl;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.addon.registry.CatLeanEventBus;
import su.catlean.gofra.Gofra;

public final class AddonEventBusImpl implements CatLeanEventBus {
   @NotNull
   public static final AddonEventBusImpl INSTANCE = new AddonEventBusImpl();

   private AddonEventBusImpl() {
   }

   @Override
   public void subscribe(Object listener) {
      Gofra.INSTANCE.plug(listener);
   }

   @Override
   public void unsubscribe(Object listener) {
      Gofra.INSTANCE.unplug(listener);
   }

   @Override
   public void post(Object event) {
      Gofra.INSTANCE.drain(event);
   }
}