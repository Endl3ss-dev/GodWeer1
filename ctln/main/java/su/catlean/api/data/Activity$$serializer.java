// deobf by pusia
package su.catlean.api.data;

import java.util.List;
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
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
@Deprecated(
   message = "This synthesized declaration should not be used directly",
   level = DeprecationLevel.HIDDEN
)
public final class Activity$$serializer implements GeneratedSerializer {
   @NotNull
   public static final Activity$$serializer INSTANCE = new Activity$$serializer();
   @NotNull
   private static final SerialDescriptor serialDescriptor;

   private Activity$$serializer() {
   }

   public final void applyBeginStructure(Encoder encoder, Activity value) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      CompositeEncoder beginStructure = encoder.beginStructure(serialDescriptor2);
      Activity.applyEncodeNullableSerializableElement(value, beginStructure, serialDescriptor2);
      beginStructure.endStructure(serialDescriptor2);
   }

   @NotNull
   public final Activity computeActivity(Decoder decoder) {
      SerialDescriptor serialDescriptor2 = serialDescriptor;
      boolean flag = true;
      int value = 0;
      String decodeNullableSerializableElemen = null;
      String decodeNullableSerializableElemen2 = null;
      ActivityTimestamp decodeSerializableElement = null;
      ActivityAsset decodeNullableSerializableElemen3 = null;
      List decodeNullableSerializableElemen4 = null;
      CompositeDecoder beginStructure = decoder.beginStructure(serialDescriptor2);
      Lazy[] lazys = Activity.getLazys();
      if (beginStructure.decodeSequentially()) {
         decodeNullableSerializableElemen = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 0, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen);
         value |= 1;
         decodeNullableSerializableElemen2 = (String)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 1, (DeserializationStrategy)StringSerializer.INSTANCE, decodeNullableSerializableElemen2);
         value |= 2;
         decodeSerializableElement = (ActivityTimestamp)beginStructure.decodeSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)ActivityTimestamp$$serializer.INSTANCE, decodeSerializableElement);
         value |= 4;
         decodeNullableSerializableElemen3 = (ActivityAsset)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 3, (DeserializationStrategy)ActivityAsset$$serializer.INSTANCE, decodeNullableSerializableElemen3);
         value |= 8;
         decodeNullableSerializableElemen4 = (List)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 4, (DeserializationStrategy)lazys[4].getValue(), decodeNullableSerializableElemen4);
         value |= 16;
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
                  decodeSerializableElement = (ActivityTimestamp)beginStructure.decodeSerializableElement(serialDescriptor2, 2, (DeserializationStrategy)ActivityTimestamp$$serializer.INSTANCE, decodeSerializableElement);
                  value |= 4;
                  break;
               case 3:
                  decodeNullableSerializableElemen3 = (ActivityAsset)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 3, (DeserializationStrategy)ActivityAsset$$serializer.INSTANCE, decodeNullableSerializableElemen3);
                  value |= 8;
                  break;
               case 4:
                  decodeNullableSerializableElemen4 = (List)beginStructure.decodeNullableSerializableElement(serialDescriptor2, 4, (DeserializationStrategy)lazys[4].getValue(), decodeNullableSerializableElemen4);
                  value |= 16;
                  break;
               default:
                  throw new UnknownFieldException(decodeElementIndex);
            }
         }
      }

      beginStructure.endStructure(serialDescriptor2);
      return new Activity(value, decodeNullableSerializableElemen, decodeNullableSerializableElemen2, decodeSerializableElement, decodeNullableSerializableElemen3, decodeNullableSerializableElemen4, null);
   }

   @NotNull
   public final SerialDescriptor getDescriptor() {
      return serialDescriptor;
   }

   @NotNull
   public final KSerializer[] childSerializers() {
      Lazy[] lazys = Activity.getLazys();
      return new KSerializer[]{
         BuiltinSerializersKt.getNullable((KSerializer)StringSerializer.INSTANCE),
         BuiltinSerializersKt.getNullable((KSerializer)StringSerializer.INSTANCE),
         ActivityTimestamp$$serializer.INSTANCE,
         BuiltinSerializersKt.getNullable((KSerializer)ActivityAsset$$serializer.INSTANCE),
         BuiltinSerializersKt.getNullable((KSerializer)lazys[4].getValue())
      };
   }

   @NotNull
   public KSerializer[] typeParametersSerializers() {
      return new KSerializer[0];
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyBeginStructure(encoder, (Activity)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeActivity(decoder);
   }

   static {
      PluginGeneratedSerialDescriptor pluginGeneratedSerialDescriptor = new PluginGeneratedSerialDescriptor("su.catlean.dh", INSTANCE, 5);
      pluginGeneratedSerialDescriptor.addElement("details", false);
      pluginGeneratedSerialDescriptor.addElement("state", false);
      pluginGeneratedSerialDescriptor.addElement("timestamps", false);
      pluginGeneratedSerialDescriptor.addElement("assets", false);
      pluginGeneratedSerialDescriptor.addElement("buttons", false);
      serialDescriptor = (SerialDescriptor)pluginGeneratedSerialDescriptor;
   }
}