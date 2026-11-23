// deobf by pusia
package su.catlean.api.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.collections.Grouping;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.inventory.ItemClusterGrouping;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.world.EntityPositionUtil;

public final class ItemCluster {
   @NotNull
   private final List items;

   public ItemCluster(List items) {
      this.items = items;
   }

   @NotNull
   public final List getItems() {
      return this.items;
   }

   @NotNull
   public final Vec3 getVec3() {
      if (this.items.isEmpty()) {
         return Vec3.ZERO;
      } else {
         double value = 0.0;
         double value2 = 0.0;
         double value3 = 0.0;

         for (ItemEntity item : (Iterable<ItemEntity>)this.items) {
            value += Render2D.INSTANCE.computeAmount(item.xo, EntityPositionUtil.computeVec3((Entity)item).x);
            value2 += Render2D.INSTANCE.computeAmount(item.yo, EntityPositionUtil.computeVec3((Entity)item).y);
            value3 += Render2D.INSTANCE.computeAmount(item.zo, EntityPositionUtil.computeVec3((Entity)item).z);
         }

         int size = this.items.size();
         return new Vec3(value / size, value2 / size + 0.7, value3 / size);
      }
   }

   @NotNull
   public final List getItems2() {
      Iterable items2 = this.items;
      boolean flag = false;
      Grouping itemClusterGrouping = new ItemClusterGrouping(items2);
      Integer integer = 0;
      Grouping grouping = itemClusterGrouping;
      Map<Object, Integer> linkedHashMap = new LinkedHashMap();
      Iterator sourceIterator = itemClusterGrouping.sourceIterator();

      while (sourceIterator.hasNext()) {
         Object object = sourceIterator.next();
         Object keyOf = grouping.keyOf(object);
         Integer object2 = linkedHashMap.get(keyOf);
         boolean flag4 = object2 == null && !linkedHashMap.containsKey(keyOf);
         Integer object3 = flag4 ? integer : object2;
         ItemEntity itemEntity = (ItemEntity)object;
         int value = ((Number)object3).intValue();
         Integer count = value + itemEntity.getItem().getCount();
         linkedHashMap.put(keyOf, count);
      }

      flag = false;
      var arrayList = new ArrayList(linkedHashMap.size());

      for (Map.Entry<Object, Integer> entrySetItem : linkedHashMap.entrySet()) {
         String key = (String)entrySetItem.getKey();
         int value2 = ((Number)entrySetItem.getValue()).intValue();
         arrayList.add(key + " x" + value2);
      }

      return arrayList;
   }

   @NotNull
   public final List getItems3() {
      return this.items;
   }

   @NotNull
   @Override
   public String toString() {
      return "ItemCluster(items=" + this.items + ")";
   }

   @Override
   public int hashCode() {
      return this.items.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof ItemCluster itemCluster) ? false : Intrinsics.areEqual(this.items, itemCluster.items);
      }
   }
}