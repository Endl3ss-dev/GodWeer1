// deobf by pusia
package su.catlean.api.setting.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.internal.ArrayListSerializer;
import kotlinx.serialization.internal.StringSerializer;
import kotlinx.serialization.json.Json;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.api.setting.values.ItemsValue;

public final class ItemsSetting extends Setting {
   public ItemsSetting(String id, ItemsValue defaultValue, GroupValue groupValue, Function0 visible) {
      super(id, defaultValue, null, groupValue, visible, 4, null);
   }

   @Override
   public void applyDecodeFromString(String setting) {
      Json json = Setting.companion.getJson();
      json.getSerializersModule();
      Iterable decodeFromString = (Iterable)json.decodeFromString((DeserializationStrategy)(new ArrayListSerializer(StringSerializer.INSTANCE)), setting);
      var arrayList = new ArrayList();

      for (Object object : decodeFromString) {
         String text = (String)object;
         Iterable item = (Iterable)BuiltInRegistries.ITEM;
         Iterator iterator = item.iterator();

         Object object2;
         while (true) {
            if (!iterator.hasNext()) {
               object2 = null;
               break;
            }

            Object object3 = iterator.next();
            Item item2 = (Item)object3;
            if (Intrinsics.areEqual(item2.getDescriptionId(), text)) {
               object2 = object3;
               break;
            }
         }

         object2 = (Item)object2;
         if ((Item)object2 != null) {
            Item item3 = (Item)object2;
            arrayList.add(item3);
         }
      }

      List mutableList = CollectionsKt.toMutableList(arrayList);
      this.setObject2(new ItemsValue(mutableList));
   }

   @NotNull
   @Override
   public String toString() {
      Json json = Setting.companion.getJson();
      Iterable items = ((ItemsValue)this.getObject2()).getItems();
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items, 10));

      for (Object object : items) {
         Item item = (Item)object;
         arrayList.add(item.getDescriptionId());
      }

      List list = arrayList;
      json.getSerializersModule();
      return json.encodeToString((SerializationStrategy)(new ArrayListSerializer(StringSerializer.INSTANCE)), list);
   }

   private static final boolean isFlag() {
      return true;
   }
}