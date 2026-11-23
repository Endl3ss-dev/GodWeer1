// deobf by pusia
package su.catlean.client.render.uniforms;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class ProjectionUniform implements DynamicUniform {
   @NotNull
   private final Matrix4f projection;
   @NotNull
   private final Matrix4f view;

   public ProjectionUniform(Matrix4f projection, Matrix4f view) {
      this.projection = projection;
      this.view = view;
   }

   public void write(ByteBuffer buffer) {
      Std140Builder intoBuffer = Std140Builder.intoBuffer(buffer);
      intoBuffer.putMat4f((Matrix4fc)this.projection);
      intoBuffer.putMat4f((Matrix4fc)this.view);
   }
}