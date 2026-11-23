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
public final class WayPoint$$serializer implements GeneratedSerializer {
   @NotNull
   public static final WayPoint$$serializer INSTANCE = new WayPoint$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private WayPoint$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, WayPoint value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      WayPoint.applyEncodeIntElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final WayPoint computeWayPoint(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      String decodeStringElement = null;
      int decodeIntElement = 0;
      int decodeIntElement2 = 0;
      int decodeIntElement3 = 0;
      String decodeStringElement2 = null;
      int decodeIntElement4 = 0;
      boolean decodeBooleanElement = false;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      if (beginStructure.decodeSequentially()) {
         decodeStringElement = beginStructure.decodeStringElement(serialDescriptor2, 0);
         value |= 1;
         decodeIntElement = beginStructure.decodeIntElement(serialDescriptor2, 1);
         value |= 2;
         decodeIntElement2 = beginStructure.decodeIntElement(serialDescriptor2, 2);
         value |= 4;
         decodeIntElement3 = beginStructure.decodeIntElement(serialDescriptor2, 3);
         value |= 8;
         decodeStringElement2 = beginStructure.decodeStringElement(serialDescriptor2, 4);
         value |= 16;
         decodeIntElement4 = beginStructure.decodeIntElement(serialDescriptor2, 5);
         value |= 32;
         decodeBooleanElement = beginStructure.decodeBooleanElement(serialDescriptor2, 6);
         value |= 64;
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
                  decodeIntElement = beginStructure.decodeIntElement(serialDescriptor2, 1);
                  value |= 2;
                  break;
               case 2:
                  decodeIntElement2 = beginStructure.decodeIntElement(serialDescriptor2, 2);
                  value |= 4;
                  break;
               case 3:
                  decodeIntElement3 = beginStructure.decodeIntElement(serialDescriptor2, 3);
                  value |= 8;
                  break;
               case 4:
                  decodeStringElement2 = beginStructure.decodeStringElement(serialDescriptor2, 4);
                  value |= 16;
                  break;
               case 5:
                  decodeIntElement4 = beginStructure.decodeIntElement(serialDescriptor2, 5);
                  value |= 32;
                  break;
               case 6:
                  decodeBooleanElement = beginStructure.decodeBooleanElement(serialDescriptor2, 6);
                  value |= 64;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new WayPoint(value, decodeStringElement, decodeIntElement, decodeIntElement2, decodeIntElement3, decodeStringElement2, decodeIntElement4, decodeBooleanElement, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{
         StringSerializer.INSTANCE,
         IntSerializer.INSTANCE,
         IntSerializer.INSTANCE,
         IntSerializer.INSTANCE,
         StringSerializer.INSTANCE,
         IntSerializer.INSTANCE,
         BooleanSerializer.INSTANCE
      };
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (WayPoint)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeWayPoint(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.zs", INSTANCE, 7);
      pluginGeneratedSerialDescriptor.addElement("id", false);
      pluginGeneratedSerialDescriptor.addElement("x", false);
      pluginGeneratedSerialDescriptor.addElement("y", false);
      pluginGeneratedSerialDescriptor.addElement("z", false);
      pluginGeneratedSerialDescriptor.addElement("server", true);
      pluginGeneratedSerialDescriptor.addElement("dimension", true);
      pluginGeneratedSerialDescriptor.addElement("visible", true);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}