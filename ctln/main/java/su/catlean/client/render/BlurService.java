// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import su.catlean.client.render.uniforms.UniformBlockD;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.MeshBuilder;

public final class BlurService {
   @NotNull
   public static final BlurService INSTANCE = new BlurService();
   private static long time;
   @NotNull
   private static final GpuTextureView[] gpuTextureViews;
   @NotNull
   private static Vec2 vec2;

   private BlurService() {
   }

   @NotNull
   public final GpuTextureView getGpuTextureView() {
      return gpuTextureViews[0];
   }

   private final GpuTextureView fetchDevice(int value) {
      double value2 = 1 / Math.pow(2.0, value);
      int width = (int)(MinecraftHolder.getMinecraft().getWindow().getWidth() * value2);
      int height = (int)(MinecraftHolder.getMinecraft().getWindow().getHeight() * value2);
      return RenderSystem.getDevice()
         .createTextureView(RenderSystem.getDevice().createTexture("ctl-blurservice-fb-" + value, 15, GpuFormat.RGBA8_UNORM, width, height, 1, 1));
   }

   public final void applyMainRenderTarget() {
      if (MinecraftHolder.getTime() - time > 1000.0 / this.getValue()) {
         this.applyGetHeight(
            gpuTextureViews[1], MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().getColorTextureView(), Pipelines.INSTANCE.getRenderPipeline8()
         );

         for (int i = 1; i < 3; i++) {
            this.applyGetHeight(gpuTextureViews[i + 1], gpuTextureViews[i], Pipelines.INSTANCE.getRenderPipeline8());
         }

         for (int i2 = 3; 0 < i2; i2--) {
            this.applyGetHeight(gpuTextureViews[i2 - 1], gpuTextureViews[i2], Pipelines.INSTANCE.getRenderPipeline9());
         }

         if (MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width != vec2.x
            || MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height != vec2.y) {
            vec2 = new Vec2(
               MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
            );
            int value = 0;

            for (int i3 = gpuTextureViews.length; value < i3; value++) {
               gpuTextureViews[value].close();
               gpuTextureViews[value] = this.fetchDevice(value);
            }
         }

         time = MinecraftHolder.getTime();
      }
   }

   private final void applyGetHeight(GpuTextureView gpuTextureView, GpuTextureView gpuTextureView2, RenderPipeline renderPipeline) {
      MeshBuilder.defaultImpl(
         new MeshBuilder(null, 0, false, 7, null).getMeshBuilder(),
         renderPipeline,
         gpuTextureView,
         null,
         UniformBlockD.INSTANCE
            .getDynamicUniformStorage()
            .writeUniform(new BlurService.UniformData(0.5F / gpuTextureView.getWidth(0), 0.5F / gpuTextureView.getHeight(0), 5.0F)),
         null,
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", gpuTextureView2)),
         null,
         176,
         null
      );
   }

   private final int getValue() {
      GLFWVidMode glfwGetVideoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
      return glfwGetVideoMode != null ? glfwGetVideoMode.refreshRate() : 60;
   }

   static {
      int value = 0;

      GpuTextureView[] gpuTextureViews2;
      for (gpuTextureViews2 = new GpuTextureView[6]; value < 6; value++) {
         gpuTextureViews2[value] = INSTANCE.fetchDevice(value);
      }

      gpuTextureViews = gpuTextureViews2;
      vec2 = new Vec2(0.0F, 0.0F);
   }

   public static final class UniformData implements DynamicUniform {
      private final float amount;
      private final float amount2;
      private final float amount3;

      public UniformData(float amount, float amount2, float amount3) {
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
      }

      public final float getAmount() {
         return this.amount;
      }

      public final float getAmount2() {
         return this.amount2;
      }

      public final float getAmount3() {
         return this.amount3;
      }

      public void write(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putVec2(this.amount, this.amount2).putFloat(this.amount3);
      }

      public final float getAmount4() {
         return this.amount;
      }

      public final float getAmount5() {
         return this.amount2;
      }

      public final float getAmount6() {
         return this.amount3;
      }

      @NotNull
      public final BlurService.UniformData computeUniformData(float amount, float amount2, float amount3) {
         return new BlurService.UniformData(amount, amount2, amount3);
      }

      @NotNull
      @Override
      public String toString() {
         float amount32 = this.amount3;
         float amount22 = this.amount2;
         float amount4 = this.amount;
         return "UniformData(halfTexelSizeX=" + amount4 + ", halfTexelSizeY=" + amount22 + ", offset=" + amount32 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Float.hashCode(this.amount);
         hashCode = hashCode * 31 + Float.hashCode(this.amount2);
         return hashCode * 31 + Float.hashCode(this.amount3);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof BlurService.UniformData uniformData)) {
            return false;
         } else if (Float.compare(this.amount, uniformData.amount) != 0) {
            return false;
         } else {
            return Float.compare(this.amount2, uniformData.amount2) != 0 ? false : Float.compare(this.amount3, uniformData.amount3) == 0;
         }
      }
   }
}