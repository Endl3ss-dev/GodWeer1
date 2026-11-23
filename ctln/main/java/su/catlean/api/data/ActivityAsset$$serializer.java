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
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class ActivityAsset$$serializer implements GeneratedSerializer {
   @NotNull
   public static final ActivityAsset$$serializer INSTANCE = new ActivityAsset$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private ActivityAsset$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, ActivityAsset value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      ActivityAsset.applyEncodeNullableSerializableElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final ActivityAsset computeActivityAsset(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      String decodeNullableSerializableElemen = null;
      String decodeNullableSerializableElemen2 = null;
      String decodeNullableSerializableElemen3 = null;
      String decodeNullableSerializableElemen4 = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      if (beginStructure.decodeSequentially()) {
         decodeNullableSerializableElemen = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen);
         value |= 1;
         decodeNullableSerializableElemen2 = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen2);
         value |= 2;
         decodeNullableSerializableElemen3 = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen3);
         value |= 4;
         decodeNullableSerializableElemen4 = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 3, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen4);
         value |= 8;
      } else {
         while (flag) {
            int decodeElementIndex = beginStructure.decodeElementIndex(serialDescriptor2);
            switch (decodeElementIndex) {
               case -1:
                  flag = false;
                  break;
               case 0:
                  decodeNullableSerializableElemen = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen);
                  value |= 1;
                  break;
               case 1:
                  decodeNullableSerializableElemen2 = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen2);
                  value |= 2;
                  break;
               case 2:
                  decodeNullableSerializableElemen3 = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen3);
                  value |= 4;
                  break;
               case 3:
                  decodeNullableSerializableElemen4 = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 3, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen4);
                  value |= 8;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new ActivityAsset(value, decodeNullableSerializableElemen, decodeNullableSerializableElemen2, decodeNullableSerializableElemen3, decodeNullableSerializableElemen4, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{
         BuiltinSerializersKt.getNullable((KSerializer)StringSerializer.INSTANCE),
         BuiltinSerializersKt.getNullable((KSerializer)StringSerializer.INSTANCE),
         BuiltinSerializersKt.getNullable((KSerializer)StringSerializer.INSTANCE),
         BuiltinSerializersKt.getNullable((KSerializer)StringSerializer.INSTANCE)
      };
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (ActivityAsset)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeActivityAsset(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.z6", INSTANCE, 4);
      pluginGeneratedSerialDescriptor.addElement("largeImage", false);
      pluginGeneratedSerialDescriptor.addElement("largeText", false);
      pluginGeneratedSerialDescriptor.addElement("smallImage", false);
      pluginGeneratedSerialDescriptor.addElement("smallText", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}