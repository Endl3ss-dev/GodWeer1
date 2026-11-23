// deobf by pusia
package su.catlean.client.services.sources;

import su.catlean.utils.KotlinCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.json.Json;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.ThemeConfig;
import su.catlean.client.services.ClientDirectories;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Colors;

public final class ThemeDataSource extends ProfileDataSource {
   @NotNull
   private final SharedDataSource sharedSource;
   @NotNull
   private final File file;
   public List items;
   public ThemeConfig themeConfig;

   public ThemeDataSource(Json json, SharedDataSource sharedSource) {
      super(json);
      this.sharedSource = sharedSource;
      this.file = new File(ClientDirectories.getFile(), "themes");
   }

   @NotNull
   @Override
   public File getFile() {
      return this.file;
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

   @NotNull
   public final ThemeConfig getThemeConfig() {
      if (this.themeConfig != null) {
         return this.themeConfig;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("current");
         return null;
      }
   }

   public final void updateThemeConfig(ThemeConfig themeConfig) {
      this.themeConfig = themeConfig;
   }

   @Override
   public void applyCreateNewFile() {
      ThemeConfig var49 = null;
      Object var48 = null;
      this.getFile().mkdirs();
      ThemeDataSource thisValue = this;

      List arrayList;
      ThemeDataSource thisValue2;
      try {
         ProfileDataSource thisValue3 = this;
         label62:
         if (thisValue3.getFile().exists() && thisValue3.getFile().isDirectory()) {
            File[] listFiles = thisValue3.getFile().listFiles();
            if (listFiles != null) {
               File[] files = listFiles;
               var arrayList2 = new ArrayList();
               int value = 0;

               for (int i = listFiles.length; value < i; value++) {
                  File file2 = files[value];
                  if (Intrinsics.areEqual(FilesKt.getExtension(file2), "ctlon")) {
                     arrayList2.add(file2);
                  }
               }

               var value2 = arrayList2;
               var arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(value2, 10));

               for (Object object : value2) {
                  File file3 = (File)object;
                  Json json = ProfileDataSource.computeJson(thisValue3);
                  String textdefault = KotlinCompat.readText(file3, null, 1, null);
                  json.getSerializersModule();
                  arrayList3.add(json.decodeFromString((DeserializationStrategy)ThemeConfig.companion.getKSerializer(), textdefault));
               }

               List mutableList = CollectionsKt.toMutableList(arrayList3);
               if (mutableList != null) {
                  var48 = mutableList;
                  break label62;
               }
            }

            var48 = new ArrayList();
         } else {
            var48 = new ArrayList();
         }

         thisValue2 = thisValue;
         arrayList = (java.util.List)var48;
      } catch (Exception exception) {
         thisValue2 = this;
         MinecraftHolder.getCatlean().warn("Themes are not loaded from file for some reasons.");
         arrayList = new ArrayList();
      }

      thisValue2.updateItems((java.util.List)arrayList);
      Iterable items2 = this.getItems();
      Iterator iterator = items2.iterator();

      while (true) {
         if (iterator.hasNext()) {
            Object object2 = iterator.next();
            ThemeConfig themeConfig2 = (ThemeConfig)object2;
            if (!Intrinsics.areEqual(themeConfig2.getId(), this.sharedSource.getSharedConfig().getThemeConfig())) {
               continue;
            }

            var49 = themeConfig2;
            break;
         }

         var49 = null;
         break;
      }

      thisValue2 = this;
      ThemeConfig themeConfig3 = (ThemeConfig)var49;
      if ((ThemeConfig)var49 == null) {
         ThemeConfig themeConfig4 = Colors.getThemeConfig();
         this.getItems().addAll(CollectionsKt.listOf(Colors.getThemeConfig()));
         thisValue2 = this;
         themeConfig3 = themeConfig4;
      }

      thisValue2.updateThemeConfig(themeConfig3);
   }

   @Override
   public void applyEncodeToString() {
      for (Object item : this.getItems()) {
         ThemeConfig themeConfig2 = (ThemeConfig)item;
         File file2 = new File(this.getFile(), themeConfig2.getId() + ".ctlon");
         try {
            file2.createNewFile();
         } catch (java.io.IOException exception) {
            throw new RuntimeException(exception);
         }
         Json json = this.getJson();
         json.getSerializersModule();
         KotlinCompat.writeText(file2, json.encodeToString((SerializationStrategy)ThemeConfig.companion.getKSerializer(), themeConfig2), null, 2, null);
      }
   }
}