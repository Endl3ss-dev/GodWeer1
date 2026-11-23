// deobf by pusia
package su.catlean.api.setting.values;

import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.UnknownFieldException;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeDecoder;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import kotlinx.serialization.internal.BooleanSerializer;
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.IntSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class Bind$$serializer implements GeneratedSerializer {
   @NotNull
   public static final Bind$$serializer INSTANCE = new Bind$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private Bind$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, Bind value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      Bind.applyShouldEncodeElementDefault(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final Bind computeBind(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      int decodeIntElement = 0;
      boolean decodeBooleanElement = false;
      boolean decodeBooleanElement2 = false;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      if (beginStructure.decodeSequentially()) {
         decodeIntElement = beginStructure.decodeIntElement(serialDescriptor2, 0);
         value |= 1;
         decodeBooleanElement = beginStructure.decodeBooleanElement(serialDescriptor2, 1);
         value |= 2;
         decodeBooleanElement2 = beginStructure.decodeBooleanElement(serialDescriptor2, 2);
         value |= 4;
      } else {
         while (flag) {
            int decodeElementIndex = beginStructure.decodeElementIndex(serialDescriptor2);
            switch (decodeElementIndex) {
               case -1:
                  flag = false;
                  break;
               case 0:
                  decodeIntElement = beginStructure.decodeIntElement(serialDescriptor2, 0);
                  value |= 1;
                  break;
               case 1:
                  decodeBooleanElement = beginStructure.decodeBooleanElement(serialDescriptor2, 1);
                  value |= 2;
                  break;
               case 2:
                  decodeBooleanElement2 = beginStructure.decodeBooleanElement(serialDescriptor2, 2);
                  value |= 4;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new Bind(value, decodeIntElement, decodeBooleanElement, decodeBooleanElement2, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{IntSerializer.INSTANCE, BooleanSerializer.INSTANCE, BooleanSerializer.INSTANCE};
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (Bind)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeBind(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.dd", INSTANCE, 3);
      pluginGeneratedSerialDescriptor.addElement("key", true);
      pluginGeneratedSerialDescriptor.addElement("hold", true);
      pluginGeneratedSerialDescriptor.addElement("mouse", true);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}