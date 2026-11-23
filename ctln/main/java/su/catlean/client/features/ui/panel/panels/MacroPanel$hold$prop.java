// deobf by pusia
package su.catlean.client.features.ui.panel.panels;

import kotlin.jvm.internal.MutablePropertyReference0Impl;
import su.catlean.client.services.client.macro.SettingMacro;

public final class MacroPanel$hold$prop extends MutablePropertyReference0Impl {
   public MacroPanel$hold$prop(Object object) {
      super(object, SettingMacro.class, "hold", "getHold()Z", 0);
   }

   public Object get() {
      return ((SettingMacro)this.receiver).isFlag2();
   }

   public void set(Object object) {
      ((SettingMacro)this.receiver).setFlag2((Boolean)object);
   }
}