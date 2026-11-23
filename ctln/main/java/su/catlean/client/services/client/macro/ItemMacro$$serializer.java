// deobf by pusia
package su.catlean.client.services.client.macro;

import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.UnknownFieldException;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeDecoder;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import kotlinx.serialization.internal.BooleanSerializer;
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.values.Bind;
import su.catlean.api.setting.values.Bind$$serializer;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class ItemMacro$$serializer implements GeneratedSerializer {
   @NotNull
   public static final ItemMacro$$serializer INSTANCE = new ItemMacro$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private ItemMacro$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, ItemMacro value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      ItemMacro.applyEncodeBooleanElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final ItemMacro computeItemMacro(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      String decodeStringElement = null;
      Bind decodeSerializableElement = null;
      boolean decodeBooleanElement = false;
      String decodeStringElement2 = null;
      boolean decodeBooleanElement2 = false;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      if (beginStructure.decodeSequentially()) {
         decodeStringElement = beginStructure.decodeStringElement(serialDescriptor2, 0);
         value |= 1;
         decodeSerializableElement = (Bind)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)Bind$$serializer.INSTANCE, decodeSerializableElement);
         value |= 2;
         decodeBooleanElement = beginStructure.decodeBooleanElement(serialDescriptor2, 2);
         value |= 4;
         decodeStringElement2 = beginStructure.decodeStringElement(serialDescriptor2, 3);
         value |= 8;
         decodeBooleanElement2 = beginStructure.decodeBooleanElement(serialDescriptor2, 4);
         value |= 16;
      } else {
         while (flag) {
            int decodeElementIndex = beginStructure.decodeElementIndex(serialDescriptor2);
            switch (decodeElementIndex) {
               case -1:
                  flag = false;
                  break;
               case 0:
                  decodeStringElement = beginStructure.decodeStringElement(serialDescriptor2, 0);
                  value |= 1;
                  break;
               case 1:
                  decodeSerializableElement = (Bind)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)Bind$$serializer.INSTANCE, decodeSerializableElement);
                  value |= 2;
                  break;
               case 2:
                  decodeBooleanElement = beginStructure.decodeBooleanElement(serialDescriptor2, 2);
                  value |= 4;
                  break;
               case 3:
                  decodeStringElement2 = beginStructure.decodeStringElement(serialDescriptor2, 3);
                  value |= 8;
                  break;
               case 4:
                  decodeBooleanElement2 = beginStructure.decodeBooleanElement(serialDescriptor2, 4);
                  value |= 16;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new ItemMacro(value, decodeStringElement, decodeSerializableElement, decodeBooleanElement, decodeStringElement2, decodeBooleanElement2, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{
         StringSerializer.INSTANCE, Bind$$serializer.INSTANCE, BooleanSerializer.INSTANCE, StringSerializer.INSTANCE, BooleanSerializer.INSTANCE
      };
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (ItemMacro)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeItemMacro(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.vx", INSTANCE, 5);
      pluginGeneratedSerialDescriptor.addElement("id", false);
      pluginGeneratedSerialDescriptor.addElement("bind", false);
      pluginGeneratedSerialDescriptor.addElement("enabled", false);
      pluginGeneratedSerialDescriptor.addElement("item", false);
      pluginGeneratedSerialDescriptor.addElement("legit", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}