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
public final class WayPointConfig$$serializer implements GeneratedSerializer {
   @NotNull
   public static final WayPointConfig$$serializer INSTANCE = new WayPointConfig$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private WayPointConfig$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, WayPointConfig value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      WayPointConfig.applyEncodeSerializableElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final WayPointConfig computeWayPointConfig(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      byte byteValue = 0;
      List decodeSerializableElement = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      Lazy[] lazys = WayPointConfig.getLazys();
      if (beginStructure.decodeSequentially()) {
         decodeSerializableElement = (List)beginStructure.decodeSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)lazys[0].getValue(), decodeSerializableElement);
         byteValue |= 1;
      } else {
         while (flag) {
            int decodeElementIndex = beginStructure.decodeElementIndex(serialDescriptor2);
            switch (decodeElementIndex) {
               case -1:
                  flag = false;
                  break;
               case 0:
                  decodeSerializableElement = (List)beginStructure.decodeSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)lazys[0].getValue(), decodeSerializableElement);
                  byteValue |= 1;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new WayPointConfig(byteValue, decodeSerializableElement, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      Lazy[] lazys = WayPointConfig.getLazys();
      return new KSerializer[]{(KSerializer)lazys[0].getValue()};
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (WayPointConfig)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeWayPointConfig(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.gg", INSTANCE, 1);
      pluginGeneratedSerialDescriptor.addElement("wayPoints", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}