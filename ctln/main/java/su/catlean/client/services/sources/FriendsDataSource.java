// deobf by pusia
package su.catlean.client.services.sources;

import su.catlean.utils.KotlinCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.internal.ArrayListSerializer;
import kotlinx.serialization.internal.StringSerializer;
import kotlinx.serialization.json.Json;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.service.sources.DataSource;
import su.catlean.client.services.ClientDirectories;
import su.catlean.utils.MinecraftHolder;

public final class FriendsDataSource implements DataSource {
   @NotNull
   private final Json json;
   @NotNull
   private final File file;
   public List items;

   public FriendsDataSource(Json json) {
      this.json = json;
      this.file = new File(ClientDirectories.getFile(), "homies.ctlon");
   }

   @NotNull
   public final List getItems() {
      if (this.items != null) {
         return this.items;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("data");
         return null;
      }
   }

   public final void updateItems(List items) {
      this.items = items;
   }

   @Override
   public void applyCreateNewFile() {
      FriendsDataSource thisValue = this;
      List arrayList;
      boolean created;
      try {
         created = this.file.createNewFile();
      } catch (java.io.IOException exception) {
         throw new RuntimeException(exception);
      }
      if (created) {
         arrayList = new ArrayList();
      } else {
         FriendsDataSource thisValue2 = this;

         List decodeFromString;
         try {
            thisValue = thisValue2;
            Json json2 = this.json;
            String textdefault = KotlinCompat.readText(this.file, null, 1, null);
            json2.getSerializersModule();
            decodeFromString = (List)json2.decodeFromString((DeserializationStrategy)(new ArrayListSerializer(StringSerializer.INSTANCE)), textdefault);
         } catch (Exception exception) {
            thisValue = this;
            MinecraftHolder.getCatlean().warn("Homies are not loaded from file for some reasons.");
            decodeFromString = new ArrayList();
         }

         arrayList = decodeFromString;
      }

      List list = arrayList;
      thisValue.updateItems(list);
   }

   @Override
   public void applyEncodeToString() {
      File file2 = this.file;
      Json json2 = this.json;
      List items2 = this.getItems();
      json2.getSerializersModule();
      KotlinCompat.writeText(file2, json2.encodeToString((SerializationStrategy)(new ArrayListSerializer(StringSerializer.INSTANCE)), items2), null, 2, null);
   }
}