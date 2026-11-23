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
import su.catlean.api.data.ServiceConfig;
import su.catlean.api.data.SettingValue;
import su.catlean.api.setting.Setting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.services.ClientDirectories;
import su.catlean.utils.MinecraftHolder;

public final class ModuleDataSource extends ProfileDataSource {
   @NotNull
   private final SharedDataSource sharedSource;
   @NotNull
   private final File file;
   public List items;
   public ServiceConfig serviceConfig;

   public ModuleDataSource(Json json, SharedDataSource sharedSource) {
      super(json);
      this.sharedSource = sharedSource;
      this.file = new File(ClientDirectories.getFile(), "modules");
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
   public final ServiceConfig getServiceConfig() {
      if (this.serviceConfig != null) {
         return this.serviceConfig;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("current");
         return null;
      }
   }

   public final void updateServiceConfig(ServiceConfig serviceConfig) {
      this.serviceConfig = serviceConfig;
   }

   @Override
   public void applyCreateNewFile() {
      Object var50 = null;
      ServiceConfig var51 = null;
      this.getFile().mkdirs();
      ModuleDataSource thisValue = this;

      List arrayList;
      ModuleDataSource thisValue2;
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
                  arrayList3.add(json.decodeFromString((DeserializationStrategy)ServiceConfig.companion.getKSerializer(), textdefault));
               }

               List mutableList = CollectionsKt.toMutableList(arrayList3);
               if (mutableList != null) {
                  var50 = mutableList;
                  break label62;
               }
            }

            var50 = new ArrayList();
         } else {
            var50 = new ArrayList();
         }

         thisValue2 = thisValue;
         arrayList = (java.util.List)var50;
      } catch (Exception exception) {
         thisValue2 = this;
         MinecraftHolder.getCatlean().warn("Modules are not loaded from file for some reasons.");
         arrayList = new ArrayList();
      }

      thisValue2.updateItems((java.util.List)arrayList);
      Iterable items2 = this.getItems();
      Iterator iterator = items2.iterator();

      while (true) {
         if (iterator.hasNext()) {
            Object object2 = iterator.next();
            ServiceConfig serviceConfig2 = (ServiceConfig)object2;
            if (!Intrinsics.areEqual(serviceConfig2.getId(), this.sharedSource.getSharedConfig().getModulesConfig())) {
               continue;
            }

            var51 = serviceConfig2;
            break;
         }

         var51 = null;
         break;
      }

      thisValue2 = this;
      ServiceConfig serviceConfig3 = (ServiceConfig)var51;
      if ((ServiceConfig)var51 == null) {
         ServiceConfig serviceConfig4 = new ServiceConfig(this.sharedSource.getSharedConfig().getModulesConfig(), null, 2, null);
         this.getItems().add(serviceConfig4);
         thisValue2 = this;
         serviceConfig3 = serviceConfig4;
      }

      thisValue2.updateServiceConfig(serviceConfig3);
   }

   @Override
   public void applyEncodeToString() {
      for (Object item : this.getItems()) {
         ServiceConfig serviceConfig2 = (ServiceConfig)item;
         File file2 = new File(this.getFile(), serviceConfig2.getId() + ".ctlon");
         try {
            file2.createNewFile();
         } catch (java.io.IOException exception) {
            throw new RuntimeException(exception);
         }
         Json json = this.getJson();
         json.getSerializersModule();
         KotlinCompat.writeText(file2, json.encodeToString((SerializationStrategy)ServiceConfig.companion.getKSerializer(), serviceConfig2), null, 2, null);
      }
   }

   public final void applyRemoveIf(List modules, String id) {
      Iterable items2 = this.getItems();
      Iterator iterator = items2.iterator();

      Object serviceConfig2;
      while (true) {
         if (iterator.hasNext()) {
            Object object = iterator.next();
            ServiceConfig serviceConfig3 = (ServiceConfig)object;
            if (!Intrinsics.areEqual(serviceConfig3.getId(), id)) {
               continue;
            }

            serviceConfig2 = object;
            break;
         }

         serviceConfig2 = null;
         break;
      }

      serviceConfig2 = (ServiceConfig)serviceConfig2;
      if ((ServiceConfig)serviceConfig2 == null) {
         serviceConfig2 = new ServiceConfig(id, null, 2, null);
      }

      ServiceConfig serviceConfig4 = (ServiceConfig)serviceConfig2;
      Iterable modules2 = modules;
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(modules2, 10));

      for (Object object2 : modules2) {
         Module module = (Module)object2;
         Iterable items5 = module.getItems5();
         String text = module.getId();
         var arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(items5, 10));

         for (Object object3 : items5) {
            Setting setting = (Setting)object3;
            arrayList2.add(new SettingValue(setting.getId(), setting.toString()));
         }

         List list = arrayList2;
         arrayList.add(new ModuleValue(text, list));
      }

      serviceConfig4.updateItems(arrayList);
      this.getItems().removeIf(arg -> Intrinsics.areEqual(((ServiceConfig)arg).getId(), id));
      this.getItems().add(serviceConfig4);
   }
}