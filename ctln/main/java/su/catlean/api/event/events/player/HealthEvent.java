// deobf by pusia
package su.catlean.api.event.events.player;

import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.properties.Delegates;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class HealthEvent extends Event {
   @NotNull
   public static final HealthEvent INSTANCE = new HealthEvent();
   public static final KProperty<Object>[] $$delegatedProperties;
   @NotNull
   private static final ReadWriteProperty<Object, Object> health$delegate = Delegates.INSTANCE.notNull();

   private HealthEvent() {
   }

   public final float getHealth() {
      return ((Number)health$delegate.getValue(this, $$delegatedProperties[0])).floatValue();
   }

   public final void setHealth(float amount) {
      health$delegate.setValue(this, $$delegatedProperties[0], amount);
   }

   public final boolean call(float amount) {
      this.setHealth(amount);
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }

   static {
      KProperty[] delegatedProperties = new KProperty[]{new MutablePropertyReference1Impl(HealthEvent.class, "health", "getHealth()F", 0)};
      $$delegatedProperties = delegatedProperties;
   }
}