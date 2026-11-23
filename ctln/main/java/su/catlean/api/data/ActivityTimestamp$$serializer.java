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
import kotlinx.serialization.internal.LongSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class ActivityTimestamp$$serializer implements GeneratedSerializer {
   @NotNull
   public static final ActivityTimestamp$$serializer INSTANCE = new ActivityTimestamp$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private ActivityTimestamp$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, ActivityTimestamp value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      ActivityTimestamp.applyEncodeLongElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final ActivityTimestamp computeActivityTimestamp(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      long decodeLongElement = 0L;
      Long decodeNullableSerializableElemen = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      if (beginStructure.decodeSequentially()) {
         decodeLongElement = beginStructure.decodeLongElement(serialDescriptor2, 0);
         value |= 1;
         decodeNullableSerializableElemen = (Long)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)LongSerializer.INSTANCE, decodeNullableSerializableElemen);
         value |= 2;
      } else {
         while (flag) {
            int decodeElementIndex = beginStructure.decodeElementIndex(serialDescriptor2);
            switch (decodeElementIndex) {
               case -1:
                  flag = false;
                  break;
               case 0:
                  decodeLongElement = beginStructure.decodeLongElement(serialDescriptor2, 0);
                  value |= 1;
                  break;
               case 1:
                  decodeNullableSerializableElemen = (Long)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)LongSerializer.INSTANCE, decodeNullableSerializableElemen);
                  value |= 2;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new ActivityTimestamp(value, decodeLongElement, decodeNullableSerializableElemen, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{LongSerializer.INSTANCE, BuiltinSerializersKt.getNullable((KSerializer)LongSerializer.INSTANCE)};
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (ActivityTimestamp)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeActivityTimestamp(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.oh", INSTANCE, 2);
      pluginGeneratedSerialDescriptor.addElement("start", false);
      pluginGeneratedSerialDescriptor.addElement("end", true);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}