// deobf by pusia
package su.catlean.client.features.ui.panel.panels;

import kotlin.jvm.internal.MutablePropertyReference0Impl;
import su.catlean.client.features.ui.panel.components.PanelComponent;

public final class MacroPanel$hoveringComponent$prop4 extends MutablePropertyReference0Impl {
   public MacroPanel$hoveringComponent$prop4(Object object) {
      super(object, MacroPanel.class, "hoveringComponent", "getHoveringComponent()Lsu/catlean/gd;", 0);
   }

   public Object get() {
      return ((MacroPanel)this.receiver).getPanelComponent();
   }

   public void set(Object object) {
      ((MacroPanel)this.receiver).setPanelComponent((PanelComponent)object);
   }
}