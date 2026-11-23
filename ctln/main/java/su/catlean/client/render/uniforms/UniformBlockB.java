// deobf by pusia
package su.catlean.client.render.uniforms;

import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.renderer.DynamicUniformStorage;
import org.jetbrains.annotations.NotNull;

public final class UniformBlockB {
   @NotNull
   public static final UniformBlockB INSTANCE = new UniformBlockB();
   private static final int value = new Std140SizeCalculator().putVec2().putFloat().putFloat().putVec4().putFloat().putVec4().putFloat().get();
   @NotNull
   private static final DynamicUniformStorage dynamicUniformStorage;

   private UniformBlockB() {
   }

   @NotNull
   public final DynamicUniformStorage getDynamicUniformStorage() {
      return dynamicUniformStorage;
   }

   public final void getDynamicUniformStorage2() {
      dynamicUniformStorage.endFrame();
   }

   static {
      // [deobf] original clinit decrypted the storage label via the ZKM DES shell; value lost in
      // sanitization. Neutral label used (GL debug name only).
      dynamicUniformStorage = new DynamicUniformStorage("uniformB", value, 16);
   }
}