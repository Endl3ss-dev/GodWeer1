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
public final class PlayersResponse$$serializer implements GeneratedSerializer {
   @NotNull
   public static final PlayersResponse$$serializer INSTANCE = new PlayersResponse$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private PlayersResponse$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, PlayersResponse value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      PlayersResponse.applyEncodeSerializableElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final PlayersResponse computePlayersResponse(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      byte byteValue = 0;
      List decodeSerializableElement = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      Lazy[] lazys = PlayersResponse.getLazys();
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
      return new PlayersResponse(byteValue, decodeSerializableElement, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      Lazy[] lazys = PlayersResponse.getLazys();
      return new KSerializer[]{(KSerializer)lazys[0].getValue()};
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (PlayersResponse)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computePlayersResponse(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.is", INSTANCE, 1);
      pluginGeneratedSerialDescriptor.addElement("players", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}