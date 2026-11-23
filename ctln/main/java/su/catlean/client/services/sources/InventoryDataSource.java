// deobf by pusia
package su.catlean.client.services.sources;

import su.catlean.utils.KotlinCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.json.Json;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.InventorySnapshot;
import su.catlean.client.services.ClientDirectories;
import su.catlean.utils.MinecraftHolder;

public final class InventoryDataSource extends ProfileDataSource {
   @NotNull
   private final SharedDataSource sharedSource;
   @NotNull
   private final File file;
   public InventorySnapshot inventorySnapshot;
   public List items;

   public InventoryDataSource(Json json, SharedDataSource sharedSource) {
      super(json);
      this.sharedSource = sharedSource;
      this.file = new File(ClientDirectories.getFile(), "inventories");
   }

   @NotNull
   @Override
   public File getFile() {
      return this.file;
   }

   @NotNull
   public final InventorySnapshot getInventorySnapshot() {
      if (this.inventorySnapshot != null) {
         return this.inventorySnapshot;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("current");
         return null;
      }
   }

   public final void updateInventorySnapshot(InventorySnapshot inventorySnapshot) {
      this.inventorySnapshot = inventorySnapshot;
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
      Object var50 = null;
      InventorySnapshot var51 = null;
      this.getFile().mkdirs();
      InventoryDataSource thisValue = this;

      List arrayList;
      InventoryDataSource thisValue2;
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
                  arrayList3.add(json.decodeFromString((DeserializationStrategy)InventorySnapshot.companion.getKSerializer(), textdefault));
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
         MinecraftHolder.getCatlean().warn("Inventories are not loaded from file for some reasons.");
         arrayList = new ArrayList();
      }

      thisValue2.updateItems((java.util.List)arrayList);
      Iterable items2 = this.getItems();
      Iterator iterator = items2.iterator();

      while (true) {
         if (iterator.hasNext()) {
            Object object2 = iterator.next();
            InventorySnapshot inventorySnapshot2 = (InventorySnapshot)object2;
            if (!Intrinsics.areEqual(inventorySnapshot2.getId(), this.sharedSource.getSharedConfig().getInventoriesConfig())) {
               continue;
            }

            var51 = inventorySnapshot2;
            break;
         }

         var51 = null;
         break;
      }

      thisValue2 = this;
      InventorySnapshot inventorySnapshot3 = (InventorySnapshot)var51;
      if ((InventorySnapshot)var51 == null) {
         InventorySnapshot inventorySnapshot4 = new InventorySnapshot(this.sharedSource.getSharedConfig().getInventoriesConfig(), new LinkedHashMap());
         this.getItems().add(inventorySnapshot4);
         thisValue2 = this;
         inventorySnapshot3 = inventorySnapshot4;
      }

      thisValue2.updateInventorySnapshot(inventorySnapshot3);
   }

   @Override
   public void applyEncodeToString() {
      for (Object item : this.getItems()) {
         InventorySnapshot inventorySnapshot2 = (InventorySnapshot)item;
         File file2 = new File(this.getFile(), inventorySnapshot2.getId() + ".ctlon");
         try {
            file2.createNewFile();
         } catch (java.io.IOException exception) {
            throw new RuntimeException(exception);
         }
         Json json = this.getJson();
         json.getSerializersModule();
         KotlinCompat.writeText(file2, json.encodeToString((SerializationStrategy)InventorySnapshot.companion.getKSerializer(), inventorySnapshot2), null, 2, null);
      }
   }
}