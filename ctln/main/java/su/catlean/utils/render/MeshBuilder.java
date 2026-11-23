// deobf by pusia
package su.catlean.utils.render;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Map.Entry;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.minecraft.client.renderer.DynamicUniformStorage;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;
import su.catlean.api.data.Box;
import su.catlean.client.render.uniforms.ProjectionUniform;
import su.catlean.client.render.uniforms.UniformBlockC;
import su.catlean.utils.MinecraftHolder;

public final class MeshBuilder {
   @NotNull
   private final VertexFormat format;
   private final boolean flag;
   @NotNull
   private ByteBuffer byteBuffer;
   private final int value;
   @NotNull
   private ByteBuffer byteBuffer2;
   private long time;
   private long time2;
   @NotNull
   private final ByteBuffer byteBuffer3;
   private long time3;
   @NotNull
   private final int[] values;
   private int value2;
   private int value3;
   private int value4;
   private final boolean flag2;
   private final boolean flag3;
   @NotNull
   private final Vector3f vector3f;

   public MeshBuilder(VertexFormat format, int value, boolean flag) {
      this.format = format;
      this.flag = flag;
      this.byteBuffer = BufferUtils.createByteBuffer(this.format.getVertexSize() * value * 4);
      this.value = this.flag ? 2 : 6;
      this.byteBuffer2 = BufferUtils.createByteBuffer(value * this.value * 4);
      this.time = MemoryUtil.memAddress0(this.byteBuffer);
      this.time2 = MemoryUtil.memAddress0(this.byteBuffer2);
      this.byteBuffer3 = MemoryUtil.memAlloc(24);
      this.time3 = this.time;
      this.values = new int[4];
      this.flag2 = Intrinsics.areEqual(this.format, DefaultVertexFormat.POSITION);
      this.flag3 = Intrinsics.areEqual(this.format, DefaultVertexFormat.POSITION_TEX);
      this.vector3f = new Vector3f();
   }

   public MeshBuilder(VertexFormat vertexFormat, int value, boolean flag, int value2, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value2 & 1) != 0) {
         vertexFormat = DefaultVertexFormat.POSITION;
      }

      if ((value2 & 2) != 0) {
         value = 1;
      }

      if ((value2 & 4) != 0) {
         flag = false;
      }

      this(vertexFormat, value, flag);
   }

   public final boolean isFlag() {
      return this.flag;
   }

   @NotNull
   public final MeshBuilder getMeshBuilder() {
      return this.computeMeshBuilder(-1.0F, -1.0F, 0.0F)
         .computeMeshBuilder(-1.0F, 1.0F, 0.0F)
         .computeMeshBuilder(1.0F, 1.0F, 0.0F)
         .computeMeshBuilder(1.0F, -1.0F, 0.0F);
   }

   @NotNull
   public final MeshBuilder computeMeshBuilder(float amount, float amount2, float amount3) {
      this.applyMemAddress0(12);
      MemoryUtil.memPutFloat(this.time3, amount);
      MemoryUtil.memPutFloat(this.time3 + 4, amount2);
      MemoryUtil.memPutFloat(this.time3 + 8, amount3);
      this.time3 += 12;
      if (this.flag2) {
         this.getMeshBuilder2();
      }

      return this;
   }

   public static MeshBuilder defaultImpl(MeshBuilder meshBuilder, float amount, float amount2, float amount3, int value, Object object) {
      if ((value & 4) != 0) {
         amount3 = 0.0F;
      }

      return meshBuilder.computeMeshBuilder(amount, amount2, amount3);
   }

   @NotNull
   public final MeshBuilder computeMeshBuilder(Matrix4f matrix, float amount, float amount2, float amount3) {
      matrix.transformPosition(amount, amount2, amount3, this.vector3f);
      return this.computeMeshBuilder(this.vector3f.x, this.vector3f.y, this.vector3f.z);
   }

   @NotNull
   public final MeshBuilder computeMeshBuilder(Matrix3x2f matrix, float amount, float amount2, float amount3) {
      this.computeMatrix4f(matrix).transformPosition(amount, amount2, amount3, this.vector3f);
      return this.computeMeshBuilder(this.vector3f.x, this.vector3f.y, this.vector3f.z);
   }

   @NotNull
   public final MeshBuilder computeMeshBuilder(float amount, float amount2) {
      this.applyMemAddress0(8);
      MemoryUtil.memPutFloat(this.time3, amount);
      MemoryUtil.memPutFloat(this.time3 + 4, amount2);
      this.time3 += 8;
      if (this.flag3) {
         this.getMeshBuilder2();
      }

      return this;
   }

   @NotNull
   public final MeshBuilder fetchAlpha(Color c) {
      return this.computeMeshBuilder(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
   }

   @NotNull
   public final MeshBuilder computeMeshBuilder(float amount, float amount2, float amount3, float amount4) {
      return this.computeMeshBuilder((int)(amount * 255), (int)(amount2 * 255), (int)(amount3 * 255), (int)(amount4 * 255));
   }

   @NotNull
   public final MeshBuilder computeMeshBuilder(int value, int value2, int value3, int value4) {
      this.applyMemAddress0(4);
      MemoryUtil.memPutByte(this.time3, (byte)value);
      MemoryUtil.memPutByte(this.time3 + 1L, (byte)value2);
      MemoryUtil.memPutByte(this.time3 + 2, (byte)value3);
      MemoryUtil.memPutByte(this.time3 + 3, (byte)RangesKt.coerceAtMost((int)(value4 * Render2D.INSTANCE.getAmount()), 255));
      this.time3 += 4;
      if (!this.flag2) {
         this.getMeshBuilder2();
      }

      return this;
   }

   private final void applyMemAddress0(int value) {
      int time32 = (int)(this.time3 - this.time);
      if (time32 + value > this.byteBuffer.capacity()) {
         ByteBuffer byteBuffer4 = this.byteBuffer;
         int capacity = Math.max(byteBuffer4.capacity() * 2, time32 + value);
         ByteBuffer byteBuffer5 = BufferUtils.createByteBuffer(capacity);
         MemoryUtil.memCopy(MemoryUtil.memAddress0(byteBuffer4), MemoryUtil.memAddress0(byteBuffer5), time32);
         this.byteBuffer = byteBuffer5;
         this.time = MemoryUtil.memAddress0(this.byteBuffer);
         this.time3 = this.time + time32;
      }
   }

   private final void applyMemAddress1(int value) {
      int value42 = this.value4 * 4;
      if (value42 + value > this.byteBuffer2.capacity()) {
         ByteBuffer byteBuffer22 = this.byteBuffer2;
         int capacity = Math.max(byteBuffer22.capacity() * 2, value42 + value);
         ByteBuffer byteBuffer4 = BufferUtils.createByteBuffer(capacity);
         MemoryUtil.memCopy(MemoryUtil.memAddress0(byteBuffer22), MemoryUtil.memAddress0(byteBuffer4), value42);
         this.byteBuffer2 = byteBuffer4;
         this.time2 = MemoryUtil.memAddress0(this.byteBuffer2);
      }
   }

   private final MeshBuilder getMeshBuilder2() {
      int value5 = this.value3++;
      this.values[this.value2] = value5;
      value5 = this.value2++;
      if (this.value2 == (this.flag ? 2 : 4)) {
         value5 = this.flag ? 2 : 6;
         this.applyMemAddress1(value5 * 4);
         long time22 = this.time2 + (long)this.value4 * 4;
         MemoryUtil.memPutInt(time22, this.values[0]);
         MemoryUtil.memPutInt(time22 + 4, this.values[1]);
         if (!this.flag) {
            MemoryUtil.memPutInt(time22 + 8L, this.values[2]);
            MemoryUtil.memPutInt(time22 + 12L, this.values[2]);
            MemoryUtil.memPutInt(time22 + 16L, this.values[3]);
            MemoryUtil.memPutInt(time22 + 20L, this.values[0]);
         }

         this.value4 += value5;
         this.value2 = 0;
      }

      return this;
   }

   @NotNull
   public final Matrix4f computeMatrix4f(Matrix3x2f m) {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.m00(m.m00());
      matrix4f.m01(m.m01());
      matrix4f.m10(m.m10());
      matrix4f.m11(m.m11());
      matrix4f.m30(m.m20());
      matrix4f.m31(m.m21());
      return matrix4f;
   }

   public final void applyGetColorTextureView(
      RenderPipeline pipeline, RenderTarget fb, GpuBufferSlice gpuBufferSlice, Matrix4f matrix4f, Matrix3x2f matrix3x2f, Map samplers, Box box
   ) {
      this.applyAddLast(pipeline, fb.getColorTextureView(), fb.getDepthTextureView(), gpuBufferSlice, matrix4f, matrix3x2f, samplers, box);
   }

   public static void defaultImpl(
      MeshBuilder meshBuilder,
      RenderPipeline renderPipeline,
      RenderTarget renderTarget,
      GpuBufferSlice gpuBufferSlice,
      Matrix4f matrix4f,
      Matrix3x2f matrix3x2f,
      Map map,
      Box box,
      int value,
      Object object
   ) {
      if ((value & 2) != 0) {
         renderTarget = MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget();
      }

      if ((value & 4) != 0) {
         gpuBufferSlice = null;
      }

      if ((value & 8) != 0) {
         matrix4f = null;
      }

      if ((value & 16) != 0) {
         matrix3x2f = null;
      }

      if ((value & 32) != 0) {
         map = MapsKt.emptyMap();
      }

      if ((value & 64) != 0) {
         box = ScissorStack.INSTANCE.getBox2();
      }

      meshBuilder.applyGetColorTextureView(renderPipeline, renderTarget, gpuBufferSlice, matrix4f, matrix3x2f, map, box);
   }

   public final void applyAddLast(
      RenderPipeline pipeline,
      GpuTextureView gpuTextureView,
      GpuTextureView gpuTextureView2,
      GpuBufferSlice gpuBufferSlice,
      Matrix4f matrix4f,
      Matrix3x2f matrix3x2f,
      Map samplers,
      Box box
   ) {
      if (this.value3 != 0) {
         int[] values2;
         if (box != null) {
            if (box.getValue3() == 0 || box.getValue4() == 0) {
               return;
            }

            int[] values3 = new int[]{box.getValue(), box.getValue2(), box.getValue3(), box.getValue4()};
            values2 = values3;
         } else {
            values2 = null;
         }

         int[] values4 = values2;
         GpuDevice device = RenderSystem.getDevice();
         this.byteBuffer.limit((int)(this.time3 - this.time));
         this.byteBuffer2.limit(this.value4 * 4);
         GpuBuffer buffer = device.createBuffer(() -> "ctl-polygon-index-buffer", 64, this.byteBuffer2);
         GpuBuffer buffer2 = device.createBuffer(() -> "ctl-polygon-vertex-buffer", 32, this.byteBuffer);
         Matrix4f matrix4f2 = matrix4f != null && matrix3x2f == null
            ? Render3D.INSTANCE.getMatrix4f()
            : new Matrix4f()
               .identity()
               .setOrtho(
                  0.0F,
                  MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth(),
                  MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight(),
                  0.0F,
                  -100.0F,
                  100.0F,
                  device.getDeviceInfo().isZZeroToOne()
               );
         DynamicUniformStorage dynamicUniformStorage = UniformBlockC.INSTANCE.getDynamicUniformStorage();
         Matrix4f matrix4f3 = matrix4f2;
         Matrix4f matrix4f4 = matrix4f;
         if (matrix4f == null) {
            if (matrix3x2f != null) {
               DynamicUniformStorage dynamicUniformStorage2 = dynamicUniformStorage;
               Matrix4f matrix4f5 = this.computeMatrix4f(matrix3x2f);
               dynamicUniformStorage = dynamicUniformStorage2;
               matrix4f3 = matrix4f2;
               matrix4f4 = matrix4f5;
            } else {
               matrix4f4 = null;
            }

            if (matrix4f4 == null) {
               matrix4f4 = new Matrix4f();
            }
         }

         Matrix4f matrix4f6 = matrix4f4;
         Matrix4f matrix4f7 = matrix4f3;
         GpuBufferSlice writeUniform = dynamicUniformStorage.writeUniform(new ProjectionUniform(matrix4f7, matrix4f6));
         RenderPass renderPass = device.createCommandEncoder()
            .createRenderPass(() -> "ctl-polygon-renderpass", gpuTextureView, Optional.empty(), gpuTextureView2, OptionalDouble.empty());
         if (values4 != null) {
            renderPass.enableScissor(values4[0], values4[1], values4[2], values4[3]);
         }

         renderPass.setPipeline(pipeline);
         renderPass.setUniform("VertexUBO", writeUniform);
         if (gpuBufferSlice != null) {
            renderPass.setUniform("UBO", gpuBufferSlice);
         }

         GpuSampler clampToEdge = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);

         for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)samplers.entrySet()) {
            String key = (String)entrySetItem.getKey();
            GpuTextureView value5 = (GpuTextureView)entrySetItem.getValue();
            renderPass.bindTexture(key, value5, clampToEdge);
         }

         renderPass.setVertexBuffer(0, buffer2.slice());
         renderPass.setIndexBuffer(buffer, IndexType.INT);
         renderPass.drawIndexed(this.value4, 1, 0, 0, 0);
         if (values4 != null) {
            renderPass.disableScissor();
         }

         renderPass.close();
         Render2D.INSTANCE.getArrayDeque().addLast(buffer);
         Render2D.INSTANCE.getArrayDeque().addLast(buffer2);
      }
   }

   public static void defaultImpl(
      MeshBuilder meshBuilder,
      RenderPipeline renderPipeline,
      GpuTextureView gpuTextureView,
      GpuTextureView gpuTextureView2,
      GpuBufferSlice gpuBufferSlice,
      Matrix4f matrix4f,
      Matrix3x2f matrix3x2f,
      Map map,
      Box box,
      int value,
      Object object
   ) {
      if ((value & 16) != 0) {
         matrix4f = null;
      }

      if ((value & 32) != 0) {
         matrix3x2f = null;
      }

      if ((value & 64) != 0) {
         map = MapsKt.emptyMap();
      }

      if ((value & 128) != 0) {
         box = ScissorStack.INSTANCE.getBox2();
      }

      meshBuilder.applyAddLast(renderPipeline, gpuTextureView, gpuTextureView2, gpuBufferSlice, matrix4f, matrix3x2f, map, box);
   }
}