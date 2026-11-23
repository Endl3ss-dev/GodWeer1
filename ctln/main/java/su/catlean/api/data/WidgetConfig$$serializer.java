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
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class WidgetConfig$$serializer implements GeneratedSerializer {
   @NotNull
   public static final WidgetConfig$$serializer INSTANCE = new WidgetConfig$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private WidgetConfig$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, WidgetConfig value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      WidgetConfig.applyEncodeSerializableElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final WidgetConfig computeWidgetConfig(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      String decodeStringElement = null;
      List decodeSerializableElement = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      Lazy[] lazys = WidgetConfig.getLazys();
      if (beginStructure.decodeSequentially()) {
         decodeStringElement = beginStructure.decodeStringElement(serialDescriptor2, 0);
         value |= 1;
         decodeSerializableElement = (List)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)lazys[1].getValue(), decodeSerializableElement);
         value |= 2;
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
                  decodeSerializableElement = (List)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)lazys[1].getValue(), decodeSerializableElement);
                  value |= 2;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new WidgetConfig(value, decodeStringElement, decodeSerializableElement, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      Lazy[] lazys = WidgetConfig.getLazys();
      return new KSerializer[]{StringSerializer.INSTANCE, (KSerializer)lazys[1].getValue()};
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (WidgetConfig)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeWidgetConfig(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.t4", INSTANCE, 2);
      pluginGeneratedSerialDescriptor.addElement("id", false);
      pluginGeneratedSerialDescriptor.addElement("widgets", true);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}