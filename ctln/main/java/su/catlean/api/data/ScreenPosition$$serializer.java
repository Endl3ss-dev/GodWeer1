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
import kotlinx.serialization.internal.FloatSerializer;
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class ScreenPosition$$serializer implements GeneratedSerializer {
   @NotNull
   public static final ScreenPosition$$serializer INSTANCE = new ScreenPosition$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private ScreenPosition$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, ScreenPosition value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      ScreenPosition.applyCompare(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final ScreenPosition computeScreenPosition(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      float decodeFloatElement = 0.0F;
      float decodeFloatElement2 = 0.0F;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      if (beginStructure.decodeSequentially()) {
         decodeFloatElement = beginStructure.decodeFloatElement(serialDescriptor2, 0);
         value |= 1;
         decodeFloatElement2 = beginStructure.decodeFloatElement(serialDescriptor2, 1);
         value |= 2;
      } else {
         while (flag) {
            int decodeElementIndex = beginStructure.decodeElementIndex(serialDescriptor2);
            switch (decodeElementIndex) {
               case -1:
                  flag = false;
                  break;
               case 0:
                  decodeFloatElement = beginStructure.decodeFloatElement(serialDescriptor2, 0);
                  value |= 1;
                  break;
               case 1:
                  decodeFloatElement2 = beginStructure.decodeFloatElement(serialDescriptor2, 1);
                  value |= 2;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new ScreenPosition(value, decodeFloatElement, decodeFloatElement2, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{FloatSerializer.INSTANCE, FloatSerializer.INSTANCE};
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (ScreenPosition)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeScreenPosition(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.i4", INSTANCE, 2);
      pluginGeneratedSerialDescriptor.addElement("x", true);
      pluginGeneratedSerialDescriptor.addElement("y", true);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}