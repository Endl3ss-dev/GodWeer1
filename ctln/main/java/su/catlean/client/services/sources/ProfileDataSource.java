// deobf by pusia
package su.catlean.client.services.sources;

import java.io.File;
import kotlinx.serialization.json.Json;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.service.sources.DataSource;

public abstract class ProfileDataSource implements DataSource {
   @NotNull
   private final Json json;

   public ProfileDataSource(Json json) {
      this.json = json;
   }

   @NotNull
   public final Json getJson() {
      return this.json;
   }

   @NotNull
   public abstract File getFile();

   public static final Json computeJson(ProfileDataSource profileDataSource) {
      return profileDataSource.json;
   }
}