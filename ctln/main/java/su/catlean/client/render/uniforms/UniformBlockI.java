// deobf by pusia
package su.catlean.client.render.uniforms;

import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.renderer.DynamicUniformStorage;
import org.jetbrains.annotations.NotNull;

public final class UniformBlockI {
   @NotNull
   public static final UniformBlockI INSTANCE = new UniformBlockI();
   private static final int value = new Std140SizeCalculator().putVec2().putFloat().putInt().get();
   @NotNull
   private static final DynamicUniformStorage dynamicUniformStorage;

   private UniformBlockI() {
   }

   public final int getValue() {
      return value;
   }

   @NotNull
   public final DynamicUniformStorage getDynamicUniformStorage() {
      return dynamicUniformStorage;
   }

   static {
      // [deobf] original clinit decrypted the storage label via the ZKM DES shell; value lost in
      // sanitization. Neutral label used (GL debug name only).
      dynamicUniformStorage = new DynamicUniformStorage("uniformI", value, 16);
   }
}