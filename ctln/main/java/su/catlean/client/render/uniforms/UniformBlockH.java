// deobf by pusia
package su.catlean.client.render.uniforms;

import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.renderer.DynamicUniformStorage;
import org.jetbrains.annotations.NotNull;

public final class UniformBlockH {
   @NotNull
   public static final UniformBlockH INSTANCE = new UniformBlockH();
   private static final int value = new Std140SizeCalculator().putVec4().putFloat().putVec4().putVec4().putFloat().get();
   @NotNull
   private static final DynamicUniformStorage dynamicUniformStorage;

   private UniformBlockH() {
   }

   @NotNull
   public final DynamicUniformStorage getDynamicUniformStorage() {
      return dynamicUniformStorage;
   }

   static {
      // [deobf] original clinit decrypted the storage label via the ZKM DES shell; value lost in
      // sanitization. Neutral label used (GL debug name only).
      dynamicUniformStorage = new DynamicUniformStorage("uniformH", value, 16);
   }
}