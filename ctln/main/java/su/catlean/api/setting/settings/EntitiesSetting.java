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
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.EntitiesValue;
import su.catlean.api.setting.values.GroupValue;

public final class EntitiesSetting extends Setting {
   public EntitiesSetting(String id, EntitiesValue defaultValue, GroupValue groupValue, Function0 visible) {
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
         Iterable entitY_TYPE = (Iterable)BuiltInRegistries.ENTITY_TYPE;
         Iterator iterator = entitY_TYPE.iterator();

         Object object2;
         while (true) {
            if (!iterator.hasNext()) {
               object2 = null;
               break;
            }

            Object object3 = iterator.next();
            EntityType entityType = (EntityType)object3;
            if (Intrinsics.areEqual(entityType.getDescriptionId(), text)) {
               object2 = object3;
               break;
            }
         }

         object2 = (EntityType)object2;
         if ((EntityType)object2 != null) {
            EntityType entityType2 = (EntityType)object2;
            arrayList.add(entityType2);
         }
      }

      List mutableList = CollectionsKt.toMutableList(arrayList);
      this.setObject2(new EntitiesValue(mutableList));
   }

   @NotNull
   @Override
   public String toString() {
      Json json = Setting.companion.getJson();
      Iterable entities = ((EntitiesValue)this.getObject2()).getEntities();
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(entities, 10));

      for (Object object : entities) {
         EntityType entityType = (EntityType)object;
         arrayList.add(entityType.getDescriptionId());
      }

      List list = arrayList;
      json.getSerializersModule();
      return json.encodeToString((SerializationStrategy)(new ArrayListSerializer(StringSerializer.INSTANCE)), list);
   }

   private static final boolean isFlag() {
      return true;
   }
}