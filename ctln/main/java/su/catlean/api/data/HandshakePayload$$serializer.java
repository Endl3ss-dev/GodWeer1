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
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.IntSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class HandshakePayload$$serializer implements GeneratedSerializer {
   @NotNull
   public static final HandshakePayload$$serializer INSTANCE = new HandshakePayload$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private HandshakePayload$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, HandshakePayload value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      HandshakePayload.applyEncodeIntElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final HandshakePayload computeHandshakePayload(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      int decodeIntElement = 0;
      String decodeStringElement = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      if (beginStructure.decodeSequentially()) {
         decodeIntElement = beginStructure.decodeIntElement(serialDescriptor2, 0);
         value |= 1;
         decodeStringElement = beginStructure.decodeStringElement(serialDescriptor2, 1);
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
                  decodeStringElement = beginStructure.decodeStringElement(serialDescriptor2, 1);
                  value |= 2;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new HandshakePayload(value, decodeIntElement, decodeStringElement, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{IntSerializer.INSTANCE, StringSerializer.INSTANCE};
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (HandshakePayload)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeHandshakePayload(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.d0", INSTANCE, 2);
      pluginGeneratedSerialDescriptor.addElement("v", false);
      pluginGeneratedSerialDescriptor.addElement("clientId", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}