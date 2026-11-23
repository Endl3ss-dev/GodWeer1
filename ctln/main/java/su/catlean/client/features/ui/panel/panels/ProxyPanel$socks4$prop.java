// deobf by pusia
package su.catlean.client.features.ui.panel.panels;

import kotlin.jvm.internal.MutablePropertyReference0Impl;
import su.catlean.api.data.Proxy;

public final class ProxyPanel$socks4$prop extends MutablePropertyReference0Impl {
   public ProxyPanel$socks4$prop(Object object) {
      super(object, Proxy.class, "socks4", "getSocks4()Z", 0);
   }

   public Object get() {
      return ((Proxy)this.receiver).isFlag();
   }

   public void set(Object object) {
      ((Proxy)this.receiver).setFlag((Boolean)object);
   }
}