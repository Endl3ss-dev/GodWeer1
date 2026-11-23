// deobf by pusia
package su.catlean.api.data;

import java.awt.Color;
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
public final class ThemeConfig$$serializer implements GeneratedSerializer {
   @NotNull
   public static final ThemeConfig$$serializer INSTANCE = new ThemeConfig$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private ThemeConfig$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, ThemeConfig value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      ThemeConfig.applyEncodeSerializableElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final ThemeConfig computeThemeConfig(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      String decodeStringElement = null;
      Color decodeSerializableElement = null;
      Color decodeSerializableElement2 = null;
      Color decodeSerializableElement3 = null;
      Color decodeSerializableElement4 = null;
      Color decodeSerializableElement5 = null;
      Color decodeSerializableElement6 = null;
      Color decodeSerializableElement7 = null;
      Color decodeSerializableElement8 = null;
      Color decodeSerializableElement9 = null;
      Color decodeSerializableElement10 = null;
      Color decodeSerializableElement11 = null;
      Color decodeSerializableElement12 = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      Lazy[] lazys = ThemeConfig.getLazys();
      if (beginStructure.decodeSequentially()) {
         decodeStringElement = beginStructure.decodeStringElement(serialDescriptor2, 0);
         value |= 1;
         decodeSerializableElement = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)lazys[1].getValue(), decodeSerializableElement);
         value |= 2;
         decodeSerializableElement2 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)lazys[2].getValue(), decodeSerializableElement2);
         value |= 4;
         decodeSerializableElement3 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 3, (DeserializationStrategy)lazys[3].getValue(), decodeSerializableElement3);
         value |= 8;
         decodeSerializableElement4 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 4, (DeserializationStrategy)lazys[4].getValue(), decodeSerializableElement4);
         value |= 16;
         decodeSerializableElement5 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 5, (DeserializationStrategy)lazys[5].getValue(), decodeSerializableElement5);
         value |= 32;
         decodeSerializableElement6 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 6, (DeserializationStrategy)lazys[6].getValue(), decodeSerializableElement6);
         value |= 64;
         decodeSerializableElement7 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 7, (DeserializationStrategy)lazys[7].getValue(), decodeSerializableElement7);
         value |= 128;
         decodeSerializableElement8 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 8, (DeserializationStrategy)lazys[8].getValue(), decodeSerializableElement8);
         value |= 256;
         decodeSerializableElement9 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 9, (DeserializationStrategy)lazys[9].getValue(), decodeSerializableElement9);
         value |= 512;
         decodeSerializableElement10 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 10, (DeserializationStrategy)lazys[10].getValue(), decodeSerializableElement10);
         value |= 1024;
         decodeSerializableElement11 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 11, (DeserializationStrategy)lazys[11].getValue(), decodeSerializableElement11);
         value |= 2048;
         decodeSerializableElement12 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 12, (DeserializationStrategy)lazys[12].getValue(), decodeSerializableElement12);
         value |= 4096;
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
                  decodeSerializableElement = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)lazys[1].getValue(), decodeSerializableElement);
                  value |= 2;
                  break;
               case 2:
                  decodeSerializableElement2 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)lazys[2].getValue(), decodeSerializableElement2);
                  value |= 4;
                  break;
               case 3:
                  decodeSerializableElement3 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 3, (DeserializationStrategy)lazys[3].getValue(), decodeSerializableElement3);
                  value |= 8;
                  break;
               case 4:
                  decodeSerializableElement4 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 4, (DeserializationStrategy)lazys[4].getValue(), decodeSerializableElement4);
                  value |= 16;
                  break;
               case 5:
                  decodeSerializableElement5 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 5, (DeserializationStrategy)lazys[5].getValue(), decodeSerializableElement5);
                  value |= 32;
                  break;
               case 6:
                  decodeSerializableElement6 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 6, (DeserializationStrategy)lazys[6].getValue(), decodeSerializableElement6);
                  value |= 64;
                  break;
               case 7:
                  decodeSerializableElement7 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 7, (DeserializationStrategy)lazys[7].getValue(), decodeSerializableElement7);
                  value |= 128;
                  break;
               case 8:
                  decodeSerializableElement8 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 8, (DeserializationStrategy)lazys[8].getValue(), decodeSerializableElement8);
                  value |= 256;
                  break;
               case 9:
                  decodeSerializableElement9 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 9, (DeserializationStrategy)lazys[9].getValue(), decodeSerializableElement9);
                  value |= 512;
                  break;
               case 10:
                  decodeSerializableElement10 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 10, (DeserializationStrategy)lazys[10].getValue(), decodeSerializableElement10);
                  value |= 1024;
                  break;
               case 11:
                  decodeSerializableElement11 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 11, (DeserializationStrategy)lazys[11].getValue(), decodeSerializableElement11);
                  value |= 2048;
                  break;
               case 12:
                  decodeSerializableElement12 = (Color)beginStructure.decodeSerializableElement(serialDescriptor2, 12, (DeserializationStrategy)lazys[12].getValue(), decodeSerializableElement12);
                  value |= 4096;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new ThemeConfig(value, decodeStringElement, decodeSerializableElement, decodeSerializableElement2, decodeSerializableElement3, decodeSerializableElement4, decodeSerializableElement5, decodeSerializableElement6, decodeSerializableElement7, decodeSerializableElement8, decodeSerializableElement9, decodeSerializableElement10, decodeSerializableElement11, decodeSerializableElement12, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      Lazy[] lazys = ThemeConfig.getLazys();
      return new KSerializer[]{
         StringSerializer.INSTANCE,
         (KSerializer)lazys[1].getValue(),
         (KSerializer)lazys[2].getValue(),
         (KSerializer)lazys[3].getValue(),
         (KSerializer)lazys[4].getValue(),
         (KSerializer)lazys[5].getValue(),
         (KSerializer)lazys[6].getValue(),
         (KSerializer)lazys[7].getValue(),
         (KSerializer)lazys[8].getValue(),
         (KSerializer)lazys[9].getValue(),
         (KSerializer)lazys[10].getValue(),
         (KSerializer)lazys[11].getValue(),
         (KSerializer)lazys[12].getValue()
      };
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (ThemeConfig)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeThemeConfig(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.ng", INSTANCE, 13);
      pluginGeneratedSerialDescriptor.addElement("id", false);
      pluginGeneratedSerialDescriptor.addElement("primaryColor", false);
      pluginGeneratedSerialDescriptor.addElement("secondaryColor", false);
      pluginGeneratedSerialDescriptor.addElement("blurColor", false);
      pluginGeneratedSerialDescriptor.addElement("widgetBlurColor", false);
      pluginGeneratedSerialDescriptor.addElement("outlineColor", false);
      pluginGeneratedSerialDescriptor.addElement("textColor", false);
      pluginGeneratedSerialDescriptor.addElement("nonactiveTextColor", false);
      pluginGeneratedSerialDescriptor.addElement("slidersColor", false);
      pluginGeneratedSerialDescriptor.addElement("nonActiveModuleColor", false);
      pluginGeneratedSerialDescriptor.addElement("moduleColor", false);
      pluginGeneratedSerialDescriptor.addElement("moduleOutlineColor", false);
      pluginGeneratedSerialDescriptor.addElement("nonActiveOutlineModuleColor", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}