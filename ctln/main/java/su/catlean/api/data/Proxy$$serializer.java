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
import kotlinx.serialization.internal.IntSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class Proxy$$serializer implements GeneratedSerializer {
   @NotNull
   public static final Proxy$$serializer INSTANCE = new Proxy$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private Proxy$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, Proxy value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      Proxy.applyEncodeStringElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final Proxy computeProxy(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      String decodeStringElement = null;
      String decodeStringElement2 = null;
      int decodeIntElement = 0;
      String decodeStringElement3 = null;
      String decodeStringElement4 = null;
      boolean decodeBooleanElement = false;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      if (beginStructure.decodeSequentially()) {
         decodeStringElement = beginStructure.decodeStringElement(serialDescriptor2, 0);
         value |= 1;
         decodeStringElement2 = beginStructure.decodeStringElement(serialDescriptor2, 1);
         value |= 2;
         decodeIntElement = beginStructure.decodeIntElement(serialDescriptor2, 2);
         value |= 4;
         decodeStringElement3 = beginStructure.decodeStringElement(serialDescriptor2, 3);
         value |= 8;
         decodeStringElement4 = beginStructure.decodeStringElement(serialDescriptor2, 4);
         value |= 16;
         decodeBooleanElement = beginStructure.decodeBooleanElement(serialDescriptor2, 5);
         value |= 32;
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
                  decodeIntElement = beginStructure.decodeIntElement(serialDescriptor2, 2);
                  value |= 4;
                  break;
               case 3:
                  decodeStringElement3 = beginStructure.decodeStringElement(serialDescriptor2, 3);
                  value |= 8;
                  break;
               case 4:
                  decodeStringElement4 = beginStructure.decodeStringElement(serialDescriptor2, 4);
                  value |= 16;
                  break;
               case 5:
                  decodeBooleanElement = beginStructure.decodeBooleanElement(serialDescriptor2, 5);
                  value |= 32;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new Proxy(value, decodeStringElement, decodeStringElement2, decodeIntElement, decodeStringElement3, decodeStringElement4, decodeBooleanElement, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{
         StringSerializer.INSTANCE,
         StringSerializer.INSTANCE,
         IntSerializer.INSTANCE,
         StringSerializer.INSTANCE,
         StringSerializer.INSTANCE,
         BooleanSerializer.INSTANCE
      };
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (Proxy)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeProxy(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.ve", INSTANCE, 6);
      pluginGeneratedSerialDescriptor.addElement("id", false);
      pluginGeneratedSerialDescriptor.addElement("ip", false);
      pluginGeneratedSerialDescriptor.addElement("port", false);
      pluginGeneratedSerialDescriptor.addElement("user", false);
      pluginGeneratedSerialDescriptor.addElement("password", false);
      pluginGeneratedSerialDescriptor.addElement("socks4", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}