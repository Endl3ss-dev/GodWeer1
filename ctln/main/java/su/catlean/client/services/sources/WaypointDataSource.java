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
import su.catlean.api.data.WayPointConfig;
import su.catlean.api.service.sources.DataSource;
import su.catlean.client.services.ClientDirectories;
import su.catlean.utils.MinecraftHolder;

public final class WaypointDataSource implements DataSource {
   @NotNull
   private final Json json;
   @NotNull
   private final File file;
   public WayPointConfig wayPointConfig;

   public WaypointDataSource(Json json) {
      this.json = json;
      this.file = new File(ClientDirectories.getFile(), "waypoints.ctlon");
   }

   @NotNull
   public final WayPointConfig getWayPointConfig() {
      if (this.wayPointConfig != null) {
         return this.wayPointConfig;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("data");
         return null;
      }
   }

   public final void updateWayPointConfig(WayPointConfig wayPointConfig) {
      this.wayPointConfig = wayPointConfig;
   }

   @Override
   public void applyCreateNewFile() {
      WaypointDataSource thisValue = this;
      WayPointConfig wayPointConfig2;
      boolean created;
      try {
         created = this.file.createNewFile();
      } catch (java.io.IOException exception) {
         throw new RuntimeException(exception);
      }
      if (created) {
         wayPointConfig2 = new WayPointConfig(new ArrayList());
      } else {
         WaypointDataSource thisValue2 = this;

         WayPointConfig decodeFromString;
         try {
            thisValue = thisValue2;
            Json json2 = this.json;
            String textdefault = KotlinCompat.readText(this.file, null, 1, null);
            json2.getSerializersModule();
            decodeFromString = (WayPointConfig)json2.decodeFromString((DeserializationStrategy)WayPointConfig.companion.getKSerializer(), textdefault);
         } catch (Exception exception) {
            thisValue = this;
            MinecraftHolder.getCatlean().warn("Waypoints are not loaded from file for some reasons.");
            decodeFromString = new WayPointConfig(new ArrayList());
         }

         wayPointConfig2 = decodeFromString;
      }

      thisValue.updateWayPointConfig(wayPointConfig2);
   }

   @Override
   public void applyEncodeToString() {
      File file2 = this.file;
      Json json2 = this.json;
      WayPointConfig wayPointConfig2 = this.getWayPointConfig();
      json2.getSerializersModule();
      KotlinCompat.writeText(file2, json2.encodeToString((SerializationStrategy)WayPointConfig.companion.getKSerializer(), wayPointConfig2), null, 2, null);
   }
}