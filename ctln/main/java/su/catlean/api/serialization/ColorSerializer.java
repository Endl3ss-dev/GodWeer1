// deobf by pusia
package su.catlean.api.serialization;

import java.awt.Color;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.descriptors.PrimitiveKind;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.descriptors.SerialDescriptorsKt;
import kotlinx.serialization.descriptors.PrimitiveKind.INT;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import org.jetbrains.annotations.NotNull;

public final class ColorSerializer implements KSerializer {
   @NotNull
   private final SerialDescriptor serialDescriptor = SerialDescriptorsKt.PrimitiveSerialDescriptor("Color", (PrimitiveKind)INT.INSTANCE);

   public ColorSerializer() {
   }

   @NotNull
   public SerialDescriptor getDescriptor() {
      return this.serialDescriptor;
   }

   public void applyEncodeInt(Encoder encoder, Color value) {
      encoder.encodeInt(value.getRGB());
   }

   @NotNull
   public Color computeColor(Decoder decoder) {
      return new Color(decoder.decodeInt(), true);
   }

   public void serialize(Encoder encoder, Object object) {
      this.applyEncodeInt(encoder, (Color)object);
   }

   public Object deserialize(Decoder decoder) {
      return this.computeColor(decoder);
   }
}