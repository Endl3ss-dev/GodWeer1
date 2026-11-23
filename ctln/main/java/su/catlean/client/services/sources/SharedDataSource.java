// deobf by pusia
package su.catlean.client.services.sources;

import su.catlean.utils.KotlinCompat;
import java.io.File;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.json.Json;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SharedConfig;
import su.catlean.api.service.sources.DataSource;
import su.catlean.client.services.ClientDirectories;
import su.catlean.utils.MinecraftHolder;

public final class SharedDataSource implements DataSource {
   @NotNull
   private final Json json;
   @NotNull
   private final File file;
   public SharedConfig sharedConfig;

   public SharedDataSource(Json json) {
      this.json = json;
      this.file = new File(ClientDirectories.getFile(), "shared.ctlon");
   }

   @NotNull
   public final SharedConfig getSharedConfig() {
      if (this.sharedConfig != null) {
         return this.sharedConfig;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("data");
         return null;
      }
   }

   public final void updateSharedConfig(SharedConfig sharedConfig) {
      this.sharedConfig = sharedConfig;
   }

   @Override
   public void applyCreateNewFile() {
      SharedDataSource thisValue = this;
      SharedConfig sharedConfig2;
      boolean created;
      try {
         created = this.file.createNewFile();
      } catch (java.io.IOException exception) {
         throw new RuntimeException(exception);
      }
      if (created) {
         sharedConfig2 = new SharedConfig(null, null, null, null, false, 31, null);
      } else {
         SharedDataSource thisValue2 = this;

         SharedConfig decodeFromString;
         try {
            thisValue = thisValue2;
            Json json2 = this.json;
            String textdefault = KotlinCompat.readText(this.file, null, 1, null);
            json2.getSerializersModule();
            decodeFromString = (SharedConfig)json2.decodeFromString((DeserializationStrategy)SharedConfig.companion.getKSerializer(), textdefault);
         } catch (Exception exception) {
            thisValue = this;
            MinecraftHolder.getCatlean().warn("Shared data are not loaded from file for some reasons.");
            decodeFromString = new SharedConfig(null, null, null, null, false, 31, null);
         }

         sharedConfig2 = decodeFromString;
      }

      SharedConfig sharedConfig3 = sharedConfig2;
      thisValue.updateSharedConfig(sharedConfig3);
   }

   @Override
   public void applyEncodeToString() {
      File file2 = this.file;
      Json json2 = this.json;
      SharedConfig sharedConfig2 = this.getSharedConfig();
      json2.getSerializersModule();
      KotlinCompat.writeText(file2, json2.encodeToString((SerializationStrategy)SharedConfig.companion.getKSerializer(), sharedConfig2), null, 2, null);
   }
}