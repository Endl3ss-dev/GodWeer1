// deobf by pusia
package su.catlean.client.features.ui.panel.panels;

import kotlin.jvm.internal.MutablePropertyReference0Impl;
import su.catlean.client.features.ui.panel.components.PanelComponent;

public final class WaypointPanel$hoveringComponent$prop2 extends MutablePropertyReference0Impl {
   public WaypointPanel$hoveringComponent$prop2(Object object) {
      super(object, WaypointPanel.class, "hoveringComponent", "getHoveringComponent()Lsu/catlean/gd;", 0);
   }

   public Object get() {
      return ((WaypointPanel)this.receiver).getPanelComponent();
   }

   public void set(Object object) {
      ((WaypointPanel)this.receiver).setPanelComponent((PanelComponent)object);
   }
}