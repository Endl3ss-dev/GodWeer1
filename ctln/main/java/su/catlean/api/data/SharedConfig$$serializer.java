// deobf by pusia
package su.catlean.api.data;

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
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class SharedConfig$$serializer implements GeneratedSerializer {
   @NotNull
   public static final SharedConfig$$serializer INSTANCE = new SharedConfig$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private SharedConfig$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, SharedConfig value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      SharedConfig.applyShouldEncodeElementDefault(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final SharedConfig computeSharedConfig(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      String decodeStringElement = null;
      String decodeStringElement2 = null;
      String decodeStringElement3 = null;
      String decodeStringElement4 = null;
      boolean decodeBooleanElement = false;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      if (beginStructure.decodeSequentially()) {
         decodeStringElement = beginStructure.decodeStringElement(serialDescriptor2, 0);
         value |= 1;
         decodeStringElement2 = beginStructure.decodeStringElement(serialDescriptor2, 1);
         value |= 2;
         decodeStringElement3 = beginStructure.decodeStringElement(serialDescriptor2, 2);
         value |= 4;
         decodeStringElement4 = beginStructure.decodeStringElement(serialDescriptor2, 3);
         value |= 8;
         decodeBooleanElement = beginStructure.decodeBooleanElement(serialDescriptor2, 4);
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
                  decodeStringElement2 = beginStructure.decodeStringElement(serialDescriptor2, 1);
                  value |= 2;
                  break;
               case 2:
                  decodeStringElement3 = beginStructure.decodeStringElement(serialDescriptor2, 2);
                  value |= 4;
                  break;
               case 3:
                  decodeStringElement4 = beginStructure.decodeStringElement(serialDescriptor2, 3);
                  value |= 8;
                  break;
               case 4:
                  decodeBooleanElement = beginStructure.decodeBooleanElement(serialDescriptor2, 4);
                  value |= 16;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new SharedConfig(value, decodeStringElement, decodeStringElement2, decodeStringElement3, decodeStringElement4, decodeBooleanElement, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{
         StringSerializer.INSTANCE, StringSerializer.INSTANCE, StringSerializer.INSTANCE, StringSerializer.INSTANCE, BooleanSerializer.INSTANCE
      };
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (SharedConfig)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeSharedConfig(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.r", INSTANCE, 5);
      pluginGeneratedSerialDescriptor.addElement("modulesConfig", true);
      pluginGeneratedSerialDescriptor.addElement("widgetsConfig", true);
      pluginGeneratedSerialDescriptor.addElement("inventoriesConfig", true);
      pluginGeneratedSerialDescriptor.addElement("themeConfig", true);
      pluginGeneratedSerialDescriptor.addElement("rpcAsked", true);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}