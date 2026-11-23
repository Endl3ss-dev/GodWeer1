// deobf by pusia
package su.catlean.client.services.sources;

import su.catlean.utils.KotlinCompat;
import java.io.File;
import java.util.ArrayList;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.json.Json;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.ProxyConfig;
import su.catlean.api.service.sources.DataSource;
import su.catlean.client.services.ClientDirectories;
import su.catlean.utils.MinecraftHolder;

public final class ProxyDataSource implements DataSource {
   @NotNull
   private final Json json;
   @NotNull
   private final File file;
   public ProxyConfig proxyConfig;

   public ProxyDataSource(Json json) {
      this.json = json;
      this.file = new File(ClientDirectories.getFile(), "proxy.ctlon");
   }

   @NotNull
   public final ProxyConfig getProxyConfig() {
      if (this.proxyConfig != null) {
         return this.proxyConfig;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("data");
         return null;
      }
   }

   public final void updateProxyConfig(ProxyConfig proxyConfig) {
      this.proxyConfig = proxyConfig;
   }

   @Override
   public void applyCreateNewFile() {
      ProxyDataSource thisValue = this;
      ProxyConfig proxyConfig2;
      boolean created;
      try {
         created = this.file.createNewFile();
      } catch (java.io.IOException exception) {
         throw new RuntimeException(exception);
      }
      if (created) {
         proxyConfig2 = new ProxyConfig(new ArrayList());
      } else {
         ProxyDataSource thisValue2 = this;

         ProxyConfig decodeFromString;
         try {
            thisValue = thisValue2;
            Json json2 = this.json;
            String textdefault = KotlinCompat.readText(this.file, null, 1, null);
            json2.getSerializersModule();
            decodeFromString = (ProxyConfig)json2.decodeFromString((DeserializationStrategy)ProxyConfig.companion.getKSerializer(), textdefault);
         } catch (Exception exception) {
            thisValue = this;
            MinecraftHolder.getCatlean().warn("Proxies are not loaded from file for some reasons.");
            decodeFromString = new ProxyConfig(new ArrayList());
         }

         proxyConfig2 = decodeFromString;
      }

      thisValue.updateProxyConfig(proxyConfig2);
   }

   @Override
   public void applyEncodeToString() {
      File file2 = this.file;
      Json json2 = this.json;
      ProxyConfig proxyConfig2 = this.getProxyConfig();
      json2.getSerializersModule();
      KotlinCompat.writeText(file2, json2.encodeToString((SerializationStrategy)ProxyConfig.companion.getKSerializer(), proxyConfig2), null, 2, null);
   }
}