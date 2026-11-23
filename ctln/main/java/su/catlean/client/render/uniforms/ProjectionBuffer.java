// deobf by pusia
package su.catlean.client.render.uniforms;

import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import org.jetbrains.annotations.NotNull;

public final class ProjectionBuffer {
   @NotNull
   public static final ProjectionBuffer INSTANCE = new ProjectionBuffer();
   @NotNull
   private static final ProjectionMatrixBuffer projectionMatrixBuffer;

   private ProjectionBuffer() {
   }

   @NotNull
   public final ProjectionMatrixBuffer getProjectionMatrixBuffer() {
      return projectionMatrixBuffer;
   }

   static {
      // [deobf] the original clinit decrypted a string via the ZKM DES shell into a local
      // (astore_0/aload_0); the sanitized shell lost the value. Buffer names are only GL debug labels,
      // so a neutral literal is used. Recoverable via out/classkeys.tsv replay if ever needed.
      projectionMatrixBuffer = new ProjectionMatrixBuffer("projection");
   }
}