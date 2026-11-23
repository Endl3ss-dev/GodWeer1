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
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.BlocksValue;
import su.catlean.api.setting.values.GroupValue;

public final class BlocksSetting extends Setting {
   public BlocksSetting(String id, BlocksValue defaultValue, GroupValue groupValue, Function0 visible) {
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
         Iterable block = (Iterable)BuiltInRegistries.BLOCK;
         Iterator iterator = block.iterator();

         Object object2;
         while (true) {
            if (!iterator.hasNext()) {
               object2 = null;
               break;
            }

            Object object3 = iterator.next();
            Block block2 = (Block)object3;
            if (Intrinsics.areEqual(block2.getDescriptionId(), text)) {
               object2 = object3;
               break;
            }
         }

         object2 = (Block)object2;
         if ((Block)object2 != null) {
            Block block3 = (Block)object2;
            arrayList.add(block3);
         }
      }

      List mutableList = CollectionsKt.toMutableList(arrayList);
      this.setObject2(new BlocksValue(mutableList));
   }

   @NotNull
   @Override
   public String toString() {
      Json json = Setting.companion.getJson();
      Iterable blocks = ((BlocksValue)this.getObject2()).getBlocks();
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(blocks, 10));

      for (Object object : blocks) {
         Block block = (Block)object;
         arrayList.add(block.getDescriptionId());
      }

      List list = arrayList;
      json.getSerializersModule();
      return json.encodeToString((SerializationStrategy)(new ArrayListSerializer(StringSerializer.INSTANCE)), list);
   }

   private static final boolean isFlag() {
      return true;
   }
}