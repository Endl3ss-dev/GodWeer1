// deobf by pusia
package su.catlean.api.data;

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
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.IntSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class FrameArgs$$serializer implements GeneratedSerializer {
   @NotNull
   public static final FrameArgs$$serializer INSTANCE = new FrameArgs$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private FrameArgs$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, FrameArgs value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      FrameArgs.applyEncodeIntElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final FrameArgs computeFrameArgs(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      int decodeIntElement = 0;
      Activity decodeSerializableElement = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      if (beginStructure.decodeSequentially()) {
         decodeIntElement = beginStructure.decodeIntElement(serialDescriptor2, 0);
         value |= 1;
         decodeSerializableElement = (Activity)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)Activity$$serializer.INSTANCE, decodeSerializableElement);
         value |= 2;
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
                  decodeSerializableElement = (Activity)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)Activity$$serializer.INSTANCE, decodeSerializableElement);
                  value |= 2;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new FrameArgs(value, decodeIntElement, decodeSerializableElement, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{IntSerializer.INSTANCE, Activity$$serializer.INSTANCE};
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (FrameArgs)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeFrameArgs(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.zq", INSTANCE, 2);
      pluginGeneratedSerialDescriptor.addElement("pid", false);
      pluginGeneratedSerialDescriptor.addElement("activity", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}