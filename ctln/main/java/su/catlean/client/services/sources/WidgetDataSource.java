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
import su.catlean.api.data.ModuleValue;
import su.catlean.api.data.SettingValue;
import su.catlean.api.data.WidgetConfig;
import su.catlean.api.setting.Setting;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.services.ClientDirectories;
import su.catlean.utils.MinecraftHolder;

public final class WidgetDataSource extends ProfileDataSource {
   @NotNull
   private final SharedDataSource sharedSource;
   @NotNull
   private final File file;
   public WidgetConfig widgetConfig;
   public List items;

   public WidgetDataSource(Json json, SharedDataSource sharedSource) {
      super(json);
      this.sharedSource = sharedSource;
      this.file = new File(ClientDirectories.getFile(), "widgets");
   }

   @NotNull
   @Override
   public File getFile() {
      return this.file;
   }

   @NotNull
   public final WidgetConfig getWidgetConfig() {
      if (this.widgetConfig != null) {
         return this.widgetConfig;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("current");
         return null;
      }
   }

   public final void updateWidgetConfig(WidgetConfig widgetConfig) {
      this.widgetConfig = widgetConfig;
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
      WidgetConfig var52 = null;
      Object var51 = null;
      this.getFile().mkdirs();
      WidgetDataSource thisValue = this;

      List arrayList;
      WidgetDataSource thisValue2;
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
                  arrayList3.add(json.decodeFromString((DeserializationStrategy)WidgetConfig.companion.getKSerializer(), textdefault));
               }

               List mutableList = CollectionsKt.toMutableList(arrayList3);
               if (mutableList != null) {
                  var51 = mutableList;
                  break label62;
               }
            }

            var51 = new ArrayList();
         } else {
            var51 = new ArrayList();
         }

         thisValue2 = thisValue;
         arrayList = (java.util.List)var51;
      } catch (Exception exception) {
         thisValue2 = this;
         MinecraftHolder.getCatlean().warn("Widgets are not loaded from file for some reasons.");
         arrayList = new ArrayList();
      }

      thisValue2.updateItems((java.util.List)arrayList);
      Iterable items2 = this.getItems();
      Iterator iterator = items2.iterator();

      while (true) {
         if (iterator.hasNext()) {
            Object object2 = iterator.next();
            WidgetConfig widgetConfig2 = (WidgetConfig)object2;
            if (!Intrinsics.areEqual(widgetConfig2.getId(), this.sharedSource.getSharedConfig().getWidgetsConfig())) {
               continue;
            }

            var52 = widgetConfig2;
            break;
         }

         var52 = null;
         break;
      }

      thisValue2 = this;
      WidgetConfig widgetConfig3 = (WidgetConfig)var52;
      if ((WidgetConfig)var52 == null) {
         WidgetConfig widgetConfig4 = new WidgetConfig(this.sharedSource.getSharedConfig().getWidgetsConfig(), null, 2, null);
         this.getItems().add(widgetConfig4);
         thisValue2 = this;
         widgetConfig3 = widgetConfig4;
      }

      thisValue2.updateWidgetConfig(widgetConfig3);
   }

   @Override
   public void applyEncodeToString() {
      for (Object item : this.getItems()) {
         WidgetConfig widgetConfig2 = (WidgetConfig)item;
         File file2 = new File(this.getFile(), widgetConfig2.getId() + ".ctlon");
         try {
            file2.createNewFile();
         } catch (java.io.IOException exception) {
            throw new RuntimeException(exception);
         }
         Json json = this.getJson();
         json.getSerializersModule();
         KotlinCompat.writeText(file2, json.encodeToString((SerializationStrategy)WidgetConfig.companion.getKSerializer(), widgetConfig2), null, 2, null);
      }
   }

   public final void applyRemoveIf(List widgets, String id) {
      Iterable items2 = this.getItems();
      Iterator iterator = items2.iterator();

      Object widgetConfig2;
      while (true) {
         if (iterator.hasNext()) {
            Object object = iterator.next();
            WidgetConfig widgetConfig3 = (WidgetConfig)object;
            if (!Intrinsics.areEqual(widgetConfig3.getId(), id)) {
               continue;
            }

            widgetConfig2 = object;
            break;
         }

         widgetConfig2 = null;
         break;
      }

      widgetConfig2 = (WidgetConfig)widgetConfig2;
      if ((WidgetConfig)widgetConfig2 == null) {
         widgetConfig2 = new WidgetConfig(id, null, 2, null);
      }

      WidgetConfig widgetConfig4 = (WidgetConfig)widgetConfig2;
      Iterable widgets2 = widgets;
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(widgets2, 10));

      for (Object object2 : widgets2) {
         Widget widget = (Widget)object2;
         Iterable items5 = widget.getItems5();
         String text = widget.getId();
         var arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(items5, 10));

         for (Object object3 : items5) {
            Setting setting = (Setting)object3;
            arrayList2.add(new SettingValue(setting.getId(), setting.toString()));
         }

         List list = arrayList2;
         arrayList.add(new ModuleValue(text, list));
      }

      widgetConfig4.updateItems(arrayList);
      this.getItems().removeIf(arg -> Intrinsics.areEqual(((WidgetConfig)arg).getId(), id));
      this.getItems().add(widgetConfig4);
   }
}