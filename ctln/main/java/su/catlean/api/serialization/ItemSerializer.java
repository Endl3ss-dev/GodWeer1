// deobf by pusia
package su.catlean.api.serialization;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.descriptors.PrimitiveKind;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.descriptors.SerialDescriptorsKt;
import kotlinx.serialization.descriptors.PrimitiveKind.STRING;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public final class ItemSerializer implements KSerializer {
   @NotNull
   private final SerialDescriptor serialDescriptor = SerialDescriptorsKt.PrimitiveSerialDescriptor("Item", (PrimitiveKind)STRING.INSTANCE);

   public ItemSerializer() {
   }

   @NotNull
   public SerialDescriptor getDescriptor() {
      return this.serialDescriptor;
   }

   @NotNull
   public Item computeItem(Decoder decoder) {
      String decodeString = decoder.decodeString();
      Iterable item = (Iterable)BuiltInRegistries.ITEM;
      Iterator iterator = item.iterator();

      Object air;
      while (true) {
         if (iterator.hasNext()) {
            Object object = iterator.next();
            Item item2 = (Item)object;
            if (!Intrinsics.areEqual(item2.getDescriptionId(), decodeString)) {
               continue;
            }

            air = (Item)object;
            break;
         }

         air = null;
         break;
      }

      air = air;
      if (air == null) {
         air = Items.AIR;
      }

      return (Item)air;
   }

   public void applyEncodeString(Encoder encoder, Item value) {
      encoder.encodeString(value.getDescriptionId());
   }

   public Object deserialize(Decoder decoder) {
      return this.computeItem(decoder);
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyEncodeString(encoder, (Item)object);
   }
}