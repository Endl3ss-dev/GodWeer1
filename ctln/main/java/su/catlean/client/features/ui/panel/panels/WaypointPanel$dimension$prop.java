// deobf by pusia
package su.catlean.client.features.ui.panel.panels;

import kotlin.jvm.internal.MutablePropertyReference0Impl;
import su.catlean.api.data.WayPoint;

public final class WaypointPanel$dimension$prop extends MutablePropertyReference0Impl {
   public WaypointPanel$dimension$prop(Object object) {
      super(object, WayPoint.class, "dimension", "getDimension()I", 0);
   }

   public Object get() {
      return ((WayPoint)this.receiver).getValue4();
   }

   public void set(Object object) {
      ((WayPoint)this.receiver).setValue4(((Number)object).intValue());
   }
}