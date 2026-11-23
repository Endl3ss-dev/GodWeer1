// deobf by pusia
package su.catlean.api.event.events.render;

import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.properties.Delegates;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class CrystalYOffsetEvent extends Event {
   @NotNull
   public static final CrystalYOffsetEvent INSTANCE = new CrystalYOffsetEvent();
   public static final KProperty<Object>[] $$delegatedProperties;
   private static float offset;
   @NotNull
   private static final ReadWriteProperty<Object, Object> age$delegate = Delegates.INSTANCE.notNull();

   private CrystalYOffsetEvent() {
   }

   public final float getOffset() {
      return offset;
   }

   public final void setOffset(float amount) {
      offset = amount;
   }

   public final float getAge() {
      return ((Number)age$delegate.getValue(this, $$delegatedProperties[0])).floatValue();
   }

   public final void setAge(float amount) {
      age$delegate.setValue(this, $$delegatedProperties[0], amount);
   }

   public final boolean call(float amount) {
      this.setAge(amount);
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }

   static {
      KProperty[] delegatedProperties = new KProperty[]{new MutablePropertyReference1Impl(CrystalYOffsetEvent.class, "age", "getAge()F", 0)};
      $$delegatedProperties = delegatedProperties;
   }
}