// deobf by pusia
package su.catlean.utils.inventory;

import java.util.Iterator;
import kotlin.collections.Grouping;
import net.minecraft.world.entity.item.ItemEntity;
import su.catlean.client.services.rotation.RotationHelpers;

public final class ItemClusterGrouping implements Grouping {
   public final Iterable items;

   public ItemClusterGrouping(Iterable items) {
      this.items = items;
   }

   public Iterator sourceIterator() {
      return this.items.iterator();
   }

   public Object keyOf(Object object) {
      ItemEntity object2 = (ItemEntity)object;
      return RotationHelpers.computeText(object2.getItem().getHoverName());
   }
}