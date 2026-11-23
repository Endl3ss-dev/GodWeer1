// deobf by pusia
package su.catlean.api.data;

import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Lazy;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.UnknownFieldException;
import kotlinx.serialization.builtins.BuiltinSerializersKt;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeDecoder;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.services.discord.ipc.IpcCommand;
import su.catlean.client.services.discord.ipc.IpcEvent;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class PacketPayload$$serializer implements GeneratedSerializer {
   @NotNull
   public static final PacketPayload$$serializer INSTANCE = new PacketPayload$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private PacketPayload$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, PacketPayload value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      PacketPayload.applyEncodeNullableSerializableElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final PacketPayload computePacketPayload(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      IpcEvent decodeNullableSerializableElemen = null;
      IpcCommand decodeNullableSerializableElemen2 = null;
      PacketData decodeSerializableElement = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      Lazy[] lazys = PacketPayload.getLazys();
      if (beginStructure.decodeSequentially()) {
         decodeNullableSerializableElemen = (IpcEvent)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)lazys[0].getValue(), decodeNullableSerializableElemen);
         value |= 1;
         decodeNullableSerializableElemen2 = (IpcCommand)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)lazys[1].getValue(), decodeNullableSerializableElemen2);
         value |= 2;
         decodeSerializableElement = (PacketData)beginStructure.decodeSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)PacketData$$serializer.INSTANCE, decodeSerializableElement);
         value |= 4;
      } else {
         while (flag) {
            int decodeElementIndex = beginStructure.decodeElementIndex(serialDescriptor2);
            switch (decodeElementIndex) {
               case -1:
                  flag = false;
                  break;
               case 0:
                  decodeNullableSerializableElemen = (IpcEvent)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)lazys[0].getValue(), decodeNullableSerializableElemen);
                  value |= 1;
                  break;
               case 1:
                  decodeNullableSerializableElemen2 = (IpcCommand)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)lazys[1].getValue(), decodeNullableSerializableElemen2);
                  value |= 2;
                  break;
               case 2:
                  decodeSerializableElement = (PacketData)beginStructure.decodeSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)PacketData$$serializer.INSTANCE, decodeSerializableElement);
                  value |= 4;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new PacketPayload(value, decodeNullableSerializableElemen, decodeNullableSerializableElemen2, decodeSerializableElement, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      Lazy[] lazys = PacketPayload.getLazys();
      return new KSerializer[]{
         BuiltinSerializersKt.getNullable((KSerializer)lazys[0].getValue()),
         BuiltinSerializersKt.getNullable((KSerializer)lazys[1].getValue()),
         PacketData$$serializer.INSTANCE
      };
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (PacketPayload)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computePacketPayload(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.gj", INSTANCE, 3);
      pluginGeneratedSerialDescriptor.addElement("evt", false);
      pluginGeneratedSerialDescriptor.addElement("cmd", false);
      pluginGeneratedSerialDescriptor.addElement("data", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}