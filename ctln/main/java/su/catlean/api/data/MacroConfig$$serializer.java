// deobf by pusia
package su.catlean.api.data;

import java.util.List;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Lazy;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.UnknownFieldException;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeDecoder;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class MacroConfig$$serializer implements GeneratedSerializer {
   @NotNull
   public static final MacroConfig$$serializer INSTANCE = new MacroConfig$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private MacroConfig$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, MacroConfig value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      MacroConfig.applyEncodeSerializableElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final MacroConfig computeMacroConfig(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      List decodeSerializableElement = null;
      List decodeSerializableElement2 = null;
      List decodeSerializableElement3 = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      Lazy[] lazys = MacroConfig.getLazys();
      if (beginStructure.decodeSequentially()) {
         decodeSerializableElement = (List)beginStructure.decodeSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)lazys[0].getValue(), decodeSerializableElement);
         value |= 1;
         decodeSerializableElement2 = (List)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)lazys[1].getValue(), decodeSerializableElement2);
         value |= 2;
         decodeSerializableElement3 = (List)beginStructure.decodeSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)lazys[2].getValue(), decodeSerializableElement3);
         value |= 4;
      } else {
         while (flag) {
            int decodeElementIndex = beginStructure.decodeElementIndex(serialDescriptor2);
            switch (decodeElementIndex) {
               case -1:
                  flag = false;
                  break;
               case 0:
                  decodeSerializableElement = (List)beginStructure.decodeSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)lazys[0].getValue(), decodeSerializableElement);
                  value |= 1;
                  break;
               case 1:
                  decodeSerializableElement2 = (List)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)lazys[1].getValue(), decodeSerializableElement2);
                  value |= 2;
                  break;
               case 2:
                  decodeSerializableElement3 = (List)beginStructure.decodeSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)lazys[2].getValue(), decodeSerializableElement3);
                  value |= 4;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new MacroConfig(value, decodeSerializableElement, decodeSerializableElement2, decodeSerializableElement3, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      Lazy[] lazys = MacroConfig.getLazys();
      return new KSerializer[]{(KSerializer)lazys[0].getValue(), (KSerializer)lazys[1].getValue(), (KSerializer)lazys[2].getValue()};
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (MacroConfig)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeMacroConfig(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.te", INSTANCE, 3);
      pluginGeneratedSerialDescriptor.addElement("settingMacros", true);
      pluginGeneratedSerialDescriptor.addElement("chatMacros", true);
      pluginGeneratedSerialDescriptor.addElement("itemMacros", true);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}