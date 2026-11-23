// deobf by pusia
package su.catlean.client.render.uniforms;

import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.renderer.DynamicUniformStorage;
import org.jetbrains.annotations.NotNull;

public final class UniformBlockF {
   @NotNull
   public static final UniformBlockF INSTANCE = new UniformBlockF();
   private static final int value = new Std140SizeCalculator()
      .putFloat()
      .putVec4()
      .putVec4()
      .putInt()
      .putInt()
      .putFloat()
      .putFloat()
      .putInt()
      .putFloat()
      .putVec2()
      .putFloat()
      .putFloat()
      .get();
   @NotNull
   private static final DynamicUniformStorage dynamicUniformStorage;

   private UniformBlockF() {
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
      dynamicUniformStorage = new DynamicUniformStorage("uniformF", value, 16);
   }
}