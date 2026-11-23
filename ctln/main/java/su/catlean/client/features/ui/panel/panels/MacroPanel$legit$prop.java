// deobf by pusia
package su.catlean.client.features.ui.panel.panels;

import kotlin.jvm.internal.MutablePropertyReference0Impl;
import su.catlean.client.services.client.macro.ItemMacro;

public final class MacroPanel$legit$prop extends MutablePropertyReference0Impl {
   public MacroPanel$legit$prop(Object object) {
      super(object, ItemMacro.class, "legit", "getLegit()Z", 0);
   }

   public Object get() {
      return ((ItemMacro)this.receiver).isFlag2();
   }

   public void set(Object object) {
      ((ItemMacro)this.receiver).setFlag2((Boolean)object);
   }
}