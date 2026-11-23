// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import java.awt.Color;
import kotlin.jvm.internal.MutablePropertyReference0Impl;

public final class ColorEditor$setting$prop extends MutablePropertyReference0Impl {
   public ColorEditor$setting$prop(Object object) {
      super(object, ColorEditor.class, "setting", "getSetting()Ljava/awt/Color;", 0);
   }

   public Object get() {
      return ((ColorEditor)this.receiver).getSetting();
   }

   public void set(Object object) {
      ((ColorEditor)this.receiver).updateColor((Color)object);
   }
}