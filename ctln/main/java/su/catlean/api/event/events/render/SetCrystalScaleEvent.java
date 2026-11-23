// deobf by pusia
package su.catlean.api.event.events.render;

import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.properties.Delegates;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class SetCrystalScaleEvent extends Event {
   @NotNull
   public static final SetCrystalScaleEvent INSTANCE = new SetCrystalScaleEvent();
   public static final KProperty<Object>[] $$delegatedProperties;
   private static float scale = 1.0F;
   @NotNull
   private static final ReadWriteProperty<Object, Object> age$delegate = Delegates.INSTANCE.notNull();

   private SetCrystalScaleEvent() {
   }

   public final float getScale() {
      return scale;
   }

   public final void setScale(float amount) {
      scale = amount;
   }

   public final float getAge() {
      return ((Number)age$delegate.getValue(this, $$delegatedProperties[0])).floatValue();
   }

   public final void setAge(float amount) {
      age$delegate.setValue(this, $$delegatedProperties[0], amount);
   }

   public final boolean call(float amount) {
      scale = 1.0F;
      this.setCancelled(false);
      this.setAge(amount);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }

   static {
      KProperty[] delegatedProperties = new KProperty[]{new MutablePropertyReference1Impl(SetCrystalScaleEvent.class, "age", "getAge()F", 0)};
      $$delegatedProperties = delegatedProperties;
   }
}