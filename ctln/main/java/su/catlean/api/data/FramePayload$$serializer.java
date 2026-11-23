// deobf by pusia
package su.catlean.api.data;

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
import su.catlean.client.services.discord.ipc.IpcCommand;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class FramePayload$$serializer implements GeneratedSerializer {
   @NotNull
   public static final FramePayload$$serializer INSTANCE = new FramePayload$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private FramePayload$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, FramePayload value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      FramePayload.applyEncodeSerializableElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final FramePayload computeFramePayload(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      IpcCommand decodeSerializableElement = null;
      FrameArgs decodeSerializableElement2 = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      Lazy[] lazys = FramePayload.getLazys();
      if (beginStructure.decodeSequentially()) {
         decodeSerializableElement = (IpcCommand)beginStructure.decodeSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)lazys[0].getValue(), decodeSerializableElement);
         value |= 1;
         decodeSerializableElement2 = (FrameArgs)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)FrameArgs$$serializer.INSTANCE, decodeSerializableElement2);
         value |= 2;
      } else {
         while (flag) {
            int decodeElementIndex = beginStructure.decodeElementIndex(serialDescriptor2);
            switch (decodeElementIndex) {
               case -1:
                  flag = false;
                  break;
               case 0:
                  decodeSerializableElement = (IpcCommand)beginStructure.decodeSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)lazys[0].getValue(), decodeSerializableElement);
                  value |= 1;
                  break;
               case 1:
                  decodeSerializableElement2 = (FrameArgs)beginStructure.decodeSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)FrameArgs$$serializer.INSTANCE, decodeSerializableElement2);
                  value |= 2;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new FramePayload(value, decodeSerializableElement, decodeSerializableElement2, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      Lazy[] lazys = FramePayload.getLazys();
      return new KSerializer[]{(KSerializer)lazys[0].getValue(), FrameArgs$$serializer.INSTANCE};
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (FramePayload)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeFramePayload(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.dg", INSTANCE, 2);
      pluginGeneratedSerialDescriptor.addElement("cmd", false);
      pluginGeneratedSerialDescriptor.addElement("args", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}