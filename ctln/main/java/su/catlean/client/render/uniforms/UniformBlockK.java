// deobf by pusia
package su.catlean.client.render.uniforms;

import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.renderer.DynamicUniformStorage;
import org.jetbrains.annotations.NotNull;

public final class UniformBlockK {
   @NotNull
   public static final UniformBlockK INSTANCE = new UniformBlockK();
   private static final int value = new Std140SizeCalculator().putVec2().putVec4().putVec4().putVec4().putVec4().putFloat().putFloat().get();
   @NotNull
   private static final DynamicUniformStorage dynamicUniformStorage;

   private UniformBlockK() {
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
      dynamicUniformStorage = new DynamicUniformStorage("uniformK", value, 16);
   }
}