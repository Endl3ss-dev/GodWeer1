// deobf by pusia
package su.catlean.client.services.sources;

import su.catlean.utils.KotlinCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.json.Json;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.MacroConfig;
import su.catlean.api.service.sources.DataSource;
import su.catlean.client.services.ClientDirectories;
import su.catlean.client.services.client.macro.ChatMacro;
import su.catlean.client.services.client.macro.ItemMacro;
import su.catlean.client.services.client.macro.SettingMacro;
import su.catlean.utils.MinecraftHolder;

public final class MacroDataSource implements DataSource {
   @NotNull
   private final Json json;
   @NotNull
   private final File file;
   public List items;

   public MacroDataSource(Json json) {
      this.json = json;
      this.file = new File(ClientDirectories.getFile(), "macro.ctlon");
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
      MacroDataSource thisValue = this;
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
         MacroDataSource thisValue2 = this;

         List arrayList2;
         try {
            Json json2 = this.json;
            String textdefault = KotlinCompat.readText(this.file, null, 1, null);
            json2.getSerializersModule();
            MacroConfig decodeFromString = (MacroConfig)json2.decodeFromString((DeserializationStrategy)MacroConfig.companion.getKSerializer(), textdefault);
            List arrayList3 = new ArrayList();
            arrayList3.addAll(decodeFromString.getChatMacros());
            arrayList3.addAll(decodeFromString.getItemMacros());
            arrayList3.addAll(decodeFromString.getSettingMacros());
            thisValue = thisValue2;
            arrayList2 = arrayList3;
         } catch (Exception exception) {
            thisValue = this;
            MinecraftHolder.getCatlean().warn("Macros are not loaded from file for some reasons.");
            arrayList2 = new ArrayList();
         }

         arrayList = arrayList2;
      }

      List list = arrayList;
      thisValue.updateItems(list);
   }

   @Override
   public void applyEncodeToString() {
      File file2 = this.file;
      Json json2 = this.json;
      Iterable items2 = this.getItems();
      File file3 = file2;
      var arrayList = new ArrayList();

      for (Object object : items2) {
         if (object instanceof ChatMacro) {
            arrayList.add(object);
         }
      }

      List mutableList = TypeIntrinsics.asMutableList(arrayList);
      Iterable items3 = this.getItems();
      var arrayList2 = new ArrayList();

      for (Object object2 : items3) {
         if (object2 instanceof ItemMacro) {
            arrayList2.add(object2);
         }
      }

      List mutableList2 = TypeIntrinsics.asMutableList(arrayList2);
      Iterable items4 = this.getItems();
      var arrayList3 = new ArrayList();

      for (Object object3 : items4) {
         if (object3 instanceof SettingMacro) {
            arrayList3.add(object3);
         }
      }

      List mutableList3 = TypeIntrinsics.asMutableList(arrayList3);
      MacroConfig macroConfig = new MacroConfig(mutableList3, mutableList, mutableList2);
      json2.getSerializersModule();
      KotlinCompat.writeText(file3, json2.encodeToString((SerializationStrategy)MacroConfig.companion.getKSerializer(), macroConfig), null, 2, null);
   }
}