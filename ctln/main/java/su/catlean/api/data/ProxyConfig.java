// deobf by pusia
package su.catlean.api.data;

import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.ArrayListSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Serializable
public final class ProxyConfig {
   @NotNull
   public static final ProxyConfig.Companion companion = new ProxyConfig.Companion(null);
   @NotNull
   private final List proxies;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   public ProxyConfig(List proxies) {
      this.proxies = proxies;
   }

   @NotNull
   public final List getProxies() {
      return this.proxies;
   }

   @NotNull
   public final List getProxies2() {
      return this.proxies;
   }

   @NotNull
   @Override
   public String toString() {
      return "ProxyConfig(proxies=" + this.proxies + ")";
   }

   @Override
   public int hashCode() {
      return this.proxies.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof ProxyConfig proxyConfig) ? false : Intrinsics.areEqual(this.proxies, proxyConfig.proxies);
      }
   }

   @JvmStatic
   public static final void applyEncodeSerializableElement(ProxyConfig proxyConfig, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      Lazy[] lazys2 = lazys;
      compositeEncoder.encodeSerializableElement(serialDescriptor, 0, (SerializationStrategy)lazys2[0].getValue(), proxyConfig.proxies);
   }

   public ProxyConfig(int value, List items, SerializationConstructorMarker serializationConstructorMarker) {
      if (1 != (1)) {
         PluginExceptionsKt.throwMissingFieldException(value, 1, ProxyConfig$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.proxies = items;
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> (KSerializer)(new ArrayListSerializer((KSerializer)Proxy$$serializer.INSTANCE)))
      };
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)ProxyConfig$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}