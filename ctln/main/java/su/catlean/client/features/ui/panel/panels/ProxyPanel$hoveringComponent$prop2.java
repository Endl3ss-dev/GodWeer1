// deobf by pusia
package su.catlean.client.features.ui.panel.panels;

import kotlin.jvm.internal.MutablePropertyReference0Impl;
import su.catlean.client.features.ui.panel.components.PanelComponent;

public final class ProxyPanel$hoveringComponent$prop2 extends MutablePropertyReference0Impl {
   public ProxyPanel$hoveringComponent$prop2(Object object) {
      super(object, ProxyPanel.class, "hoveringComponent", "getHoveringComponent()Lsu/catlean/gd;", 0);
   }

   public Object get() {
      return ((ProxyPanel)this.receiver).getPanelComponent();
   }

   public void set(Object object) {
      ((ProxyPanel)this.receiver).setPanelComponent((PanelComponent)object);
   }
}