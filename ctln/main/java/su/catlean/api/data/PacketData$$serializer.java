// deobf by pusia
package su.catlean.api.data;

import kotlin.Deprecated;
import kotlin.DeprecationLevel;
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
import kotlinx.serialization.internal.IntSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class PacketData$$serializer implements GeneratedSerializer {
   @NotNull
   public static final PacketData$$serializer INSTANCE = new PacketData$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private PacketData$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, PacketData value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      PacketData.applyEncodeNullableSerializableElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final PacketData computePacketData(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      IPCUser decodeNullableSerializableElemen = null;
      Integer decodeNullableSerializableElemen2 = null;
      String decodeNullableSerializableElemen3 = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      if (beginStructure.decodeSequentially()) {
         decodeNullableSerializableElemen = (IPCUser)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)IPCUser$$serializer.INSTANCE, decodeNullableSerializableElemen);
         value |= 1;
         decodeNullableSerializableElemen2 = (Integer)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)IntSerializer.INSTANCE, decodeNullableSerializableElemen2);
         value |= 2;
         decodeNullableSerializableElemen3 = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen3);
         value |= 4;
      } else {
         while (flag) {
            int decodeElementIndex = beginStructure.decodeElementIndex(serialDescriptor2);
            switch (decodeElementIndex) {
               case -1:
                  flag = false;
                  break;
               case 0:
                  decodeNullableSerializableElemen = (IPCUser)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)IPCUser$$serializer.INSTANCE, decodeNullableSerializableElemen);
                  value |= 1;
                  break;
               case 1:
                  decodeNullableSerializableElemen2 = (Integer)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)IntSerializer.INSTANCE, decodeNullableSerializableElemen2);
                  value |= 2;
                  break;
               case 2:
                  decodeNullableSerializableElemen3 = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen3);
                  value |= 4;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new PacketData(value, decodeNullableSerializableElemen, decodeNullableSerializableElemen2, decodeNullableSerializableElemen3, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{
         BuiltinSerializersKt.getNullable((KSerializer)IPCUser$$serializer.INSTANCE),
         BuiltinSerializersKt.getNullable((KSerializer)IntSerializer.INSTANCE),
         BuiltinSerializersKt.getNullable((KSerializer)StringSerializer.INSTANCE)
      };
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (PacketData)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computePacketData(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.k0", INSTANCE, 3);
      pluginGeneratedSerialDescriptor.addElement("user", false);
      pluginGeneratedSerialDescriptor.addElement("code", false);
      pluginGeneratedSerialDescriptor.addElement("message", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}