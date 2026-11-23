// deobf by pusia
package su.catlean.utils.render;

import su.catlean.utils.KotlinCompat;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.math.MathKt;
import kotlin.text.StringsKt;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;
import su.catlean.api.data.Rectangle;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.event.events.render.FlipFrameEvent;
import su.catlean.api.event.events.render.Release2DEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.RenderScreenEvent;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.render.BlurService;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.render.uniforms.UniformBlockA;
import su.catlean.client.render.uniforms.UniformBlockC;
import su.catlean.client.render.uniforms.UniformBlockD;
import su.catlean.client.render.uniforms.UniformBlockE;
import su.catlean.client.render.uniforms.UniformBlockF;
import su.catlean.client.render.uniforms.UniformBlockG;
import su.catlean.client.render.uniforms.UniformBlockH;
import su.catlean.client.render.uniforms.UniformBlockI;
import su.catlean.client.render.uniforms.UniformBlockJ;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.GuiGraphicsAccessor;
import su.catlean.mixins.accessors.NativeImageAccessor;
import su.catlean.utils.MinecraftHolder;

public final class Render2D {
   @NotNull
   public static final Render2D INSTANCE = new Render2D();
   @NotNull
   private static SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();
   @NotNull
   private static SvgTexture svgTexture = new SvgTexture("hhlogo", 8, 8);
   @NotNull
   private static final CopyOnWriteArrayList<Function0> items = new CopyOnWriteArrayList();
   @NotNull
   private static final Matrix4f matrix4f = new Matrix4f();
   @NotNull
   private static final Matrix4f matrix4f2 = new Matrix4f();
   @NotNull
   private static int[] values = new int[4];
   private static int value = -1;
   private static float amount = 1.0F;
   @NotNull
   private static final ArrayDeque arrayDeque = new ArrayDeque();

   private Render2D() {
   }

   @NotNull
   public final SubmitNodeStorage getSubmitNodeStorage() {
      return submitNodeStorage;
   }

   public final void updateSubmitNodeStorage(SubmitNodeStorage submitNodeStorage) {
      Render2D.submitNodeStorage = submitNodeStorage;
   }

   @NotNull
   public final SvgTexture getSvgTexture() {
      return svgTexture;
   }

   @NotNull
   public final Matrix4f getMatrix4f() {
      return matrix4f;
   }

   @NotNull
   public final Matrix4f getMatrix4f2() {
      return matrix4f2;
   }

   @NotNull
   public final int[] getValues() {
      return values;
   }

   public final float getAmount() {
      return amount;
   }

   public final void setAmount(float amount) {
      Render2D.amount = amount;
   }

   @NotNull
   public final ArrayDeque getArrayDeque() {
      return arrayDeque;
   }

   public final void applyClose() {
      while (!arrayDeque.isEmpty()) {
         ((GpuBuffer)arrayDeque.removeFirst()).close();
      }
   }

   @Flow
   private final void onTick(TickEvent tickEvent) {
      int guiScale = MinecraftHolder.getMinecraft().getWindow().getGuiScale();
      if (guiScale != value) {
         for (Object item : Fonts.getItems()) {
            FontRenderer fontRenderer = (FontRenderer)item;
            fontRenderer.close();
            fontRenderer.applyDeriveFont();
         }

         value = guiScale;
      }
   }

   @Flow
   private final void onRelease2D(Release2DEvent release2DEvent) {
      this.applyClose();
   }

   @Flow(
      priority = 20
   )
   private final void onRender2D(Render2DEvent render2DEvent) {
      BlurService.INSTANCE.applyMainRenderTarget();

      for (Object item : items) {
         Function0 function0 = (Function0)item;
         function0.invoke();
      }

      items.clear();
   }

   @Flow
   private final void onRenderScreen(RenderScreenEvent renderScreenEvent) {
      Screen screen = MinecraftHolder.getMinecraft().gui.screen();
      if (screen != null) {
         Screen screen2 = screen;
         if (screen2 instanceof ClickGuiScreen) {
            ClickGuiScreen.INSTANCE.render(renderScreenEvent.getContext());
         } else if (KotlinCompat.startsWith(screen2.getClass().getName(), "su.catlean.", false, 2, null)) {
            screen2.extractRenderState(renderScreenEvent.getContext(), renderScreenEvent.getMouseX(), renderScreenEvent.getMouseY(), renderScreenEvent.getDelta());
         }
      }
   }

   @Flow
   private final void onFlipFrame(FlipFrameEvent flipFrameEvent) {
      UniformBlockH.INSTANCE.getDynamicUniformStorage().endFrame();
      UniformBlockD.INSTANCE.getDynamicUniformStorage().endFrame();
      UniformBlockA.INSTANCE.getDynamicUniformStorage().endFrame();
      UniformBlockC.INSTANCE.getDynamicUniformStorage().endFrame();
      UniformBlockE.INSTANCE.getDynamicUniformStorage().endFrame();
      UniformBlockF.INSTANCE.getDynamicUniformStorage().endFrame();
      UniformBlockG.INSTANCE.getDynamicUniformStorage().endFrame();
      UniformBlockJ.INSTANCE.getDynamicUniformStorage().endFrame();
      UniformBlockI.INSTANCE.getDynamicUniformStorage().endFrame();
   }

   public final void updateMeshBuilder(MeshBuilder polygon, float amount, float amount2, float amount3, float amount4, Color[] colors) {
      MeshBuilder.defaultImpl(
            MeshBuilder.defaultImpl(
                  MeshBuilder.defaultImpl(
                        MeshBuilder.defaultImpl(polygon, amount, amount2 + amount4, 0.0F, 4, null).fetchAlpha(colors[0]),
                        amount + amount3,
                        amount2 + amount4,
                        0.0F,
                        4,
                        null
                     )
                     .fetchAlpha(colors.length > 1 ? colors[1] : colors[0]),
                  amount + amount3,
                  amount2,
                  0.0F,
                  4,
                  null
               )
               .fetchAlpha(colors.length > 1 ? colors[2] : colors[0]),
            amount,
            amount2,
            0.0F,
            4,
            null
         )
         .fetchAlpha(colors.length > 1 ? colors[3] : colors[0]);
   }

   public final void render(Matrix3x2fStack matrices, float amount, float amount2, float amount3, float amount4, Color[] colors) {
      MeshBuilder.defaultImpl(
         MeshBuilder.defaultImpl(
               MeshBuilder.defaultImpl(
                     MeshBuilder.defaultImpl(
                           MeshBuilder.defaultImpl(
                                 new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, 0, false, 6, null), amount, amount2 + amount4, 0.0F, 4, null
                              )
                              .fetchAlpha(colors[0]),
                           amount + amount3,
                           amount2 + amount4,
                           0.0F,
                           4,
                           null
                        )
                        .fetchAlpha(colors.length > 1 ? colors[1] : colors[0]),
                     amount + amount3,
                     amount2,
                     0.0F,
                     4,
                     null
                  )
                  .fetchAlpha(colors.length > 1 ? colors[2] : colors[0]),
               amount,
               amount2,
               0.0F,
               4,
               null
            )
            .fetchAlpha(colors.length > 1 ? colors[3] : colors[0]),
         Pipelines.INSTANCE.getRenderPipeline22(),
         null,
         null,
         null,
         new Matrix3x2f((Matrix3x2fc)matrices),
         null,
         null,
         110,
         null
      );
   }

   public final void render(Matrix3x2fStack matrix3x2fStack, float amount, float amount2, float amount3, float amount4, Color c) {
      VertexFormat positioN_COLOR = DefaultVertexFormat.POSITION_COLOR;
      MeshBuilder.defaultImpl(
         MeshBuilder.defaultImpl(
               MeshBuilder.defaultImpl(
                     MeshBuilder.defaultImpl(
                           MeshBuilder.defaultImpl(
                                 MeshBuilder.defaultImpl(
                                       MeshBuilder.defaultImpl(
                                             MeshBuilder.defaultImpl(
                                                   MeshBuilder.defaultImpl(new MeshBuilder(positioN_COLOR, 4, true), amount, amount2, 0.0F, 4, null).fetchAlpha(c),
                                                   amount,
                                                   amount2 + amount4,
                                                   0.0F,
                                                   4,
                                                   null
                                                )
                                                .fetchAlpha(c),
                                             amount,
                                             amount2 + amount4,
                                             0.0F,
                                             4,
                                             null
                                          )
                                          .fetchAlpha(c),
                                       amount + amount3,
                                       amount2 + amount4,
                                       0.0F,
                                       4,
                                       null
                                    )
                                    .fetchAlpha(c),
                                 amount + amount3,
                                 amount2 + amount4,
                                 0.0F,
                                 4,
                                 null
                              )
                              .fetchAlpha(c),
                           amount + amount3,
                           amount2,
                           0.0F,
                           4,
                           null
                        )
                        .fetchAlpha(c),
                     amount + amount3,
                     amount2,
                     0.0F,
                     4,
                     null
                  )
                  .fetchAlpha(c),
               amount,
               amount2,
               0.0F,
               4,
               null
            )
            .fetchAlpha(c),
         Pipelines.INSTANCE.getRenderPipeline25(),
         null,
         null,
         null,
         new Matrix3x2f((Matrix3x2fc)matrix3x2fStack),
         null,
         null,
         110,
         null
      );
   }

   public final void render(GuiGraphicsExtractor context, Rectangle r1) {
      Vector2f vector2f = new Vector2f();
      Vector2f vector2f2 = new Vector2f();
      context.pose().transformPosition(r1.getAmount(), r1.getAmount2(), vector2f);
      context.pose().transformPosition(r1.getAmount3(), r1.getAmount4(), vector2f2);
      context.enableScissor((int)r1.getAmount(), (int)r1.getAmount2(), (int)r1.getAmount3(), (int)r1.getAmount4());
      ScissorStack instance = ScissorStack.INSTANCE;
      ScreenRectangle peek = ((GuiGraphicsAccessor)context).getScissorStack().peek();
      if (peek == null) {
         String text = "Required value was null.";
         throw new IllegalArgumentException(text.toString());
      } else {
         instance.applyGetHeight(peek);
      }
   }

   public final void render(GuiGraphicsExtractor context) {
      context.disableScissor();
      ScissorStack.INSTANCE.getBox();
   }

   private final void updateMeshBuilder(MeshBuilder meshBuilder, float amount, float amount2, float amount3, float amount4, Color color) {
      meshBuilder.computeMeshBuilder(amount, amount2, 0.0F).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(amount, amount4, 0.0F).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(amount3, amount4, 0.0F).fetchAlpha(color);
      meshBuilder.computeMeshBuilder(amount3, amount2, 0.0F).fetchAlpha(color);
   }

   public final void applyGetTexture(
      Matrix3x2f matrices,
      Identifier id,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      Color color1,
      Color color2,
      Color color3,
      Color color4,
      float amount5,
      float amount6,
      float amount7,
      float amount8,
      boolean flag
   ) {
      GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(id).getTextureView();
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 0, false, 6, null);
      MeshBuilder.defaultImpl(meshBuilder, amount + 0, amount2 + amount4, 0.0F, 4, null).computeMeshBuilder(amount5, amount8).fetchAlpha(color1);
      MeshBuilder.defaultImpl(meshBuilder, amount + amount3, amount2 + amount4, 0.0F, 4, null).computeMeshBuilder(amount7, amount8).fetchAlpha(color2);
      MeshBuilder.defaultImpl(meshBuilder, amount + amount3, amount2 + 0, 0.0F, 4, null).computeMeshBuilder(amount7, amount6).fetchAlpha(color3);
      MeshBuilder.defaultImpl(meshBuilder, amount + 0, amount2 + 0, 0.0F, 4, null).computeMeshBuilder(amount5, amount6).fetchAlpha(color4);
      MeshBuilder.defaultImpl(
         meshBuilder,
         flag ? Pipelines.INSTANCE.getRenderPipeline18() : Pipelines.INSTANCE.getRenderPipeline14(),
         MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
         null,
         null,
         matrices,
         MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
         null,
         76,
         null
      );
   }

   public static void defaultImpl(
      Render2D render2D,
      Matrix3x2f matrix3x2f,
      Identifier identifier,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      Color color,
      Color color2,
      Color color3,
      Color color4,
      float amount5,
      float amount6,
      float amount7,
      float amount8,
      boolean flag,
      int value,
      Object object
   ) {
      if ((value & 64) != 0) {
         color = Color.WHITE;
      }

      if ((value & 128) != 0) {
         color2 = color;
      }

      if ((value & 256) != 0) {
         color3 = color2;
      }

      if ((value & 512) != 0) {
         color4 = color3;
      }

      if ((value & 1024) != 0) {
         amount5 = 0.0F;
      }

      if ((value & 2048) != 0) {
         amount6 = 0.0F;
      }

      if ((value & 4096) != 0) {
         amount7 = 1.0F;
      }

      if ((value & 8192) != 0) {
         amount8 = 1.0F;
      }

      if ((value & 16384) != 0) {
         flag = false;
      }

      render2D.applyGetTexture(
         matrix3x2f, identifier, amount, amount2, amount3, amount4, color, color2, color3, color4, amount5, amount6, amount7, amount8, flag
      );
   }

   public final void updateMeshBuilder(
      MeshBuilder polygon,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      Color color1,
      Color color2,
      Color color3,
      Color color4,
      float amount5,
      float amount6,
      float amount7,
      float amount8
   ) {
      MeshBuilder.defaultImpl(polygon, amount + 0, amount2 + amount4, 0.0F, 4, null).computeMeshBuilder(amount5, amount8).fetchAlpha(color1);
      MeshBuilder.defaultImpl(polygon, amount + amount3, amount2 + amount4, 0.0F, 4, null).computeMeshBuilder(amount7, amount8).fetchAlpha(color2);
      MeshBuilder.defaultImpl(polygon, amount + amount3, amount2 + 0, 0.0F, 4, null).computeMeshBuilder(amount7, amount6).fetchAlpha(color3);
      MeshBuilder.defaultImpl(polygon, amount + 0, amount2 + 0, 0.0F, 4, null).computeMeshBuilder(amount5, amount6).fetchAlpha(color4);
   }

   public static void defaultImpl(
      Render2D render2D,
      MeshBuilder meshBuilder,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      Color color,
      Color color2,
      Color color3,
      Color color4,
      float amount5,
      float amount6,
      float amount7,
      float amount8,
      int value,
      Object object
   ) {
      if ((value & 32) != 0) {
         color = Color.WHITE;
      }

      if ((value & 64) != 0) {
         color2 = color;
      }

      if ((value & 128) != 0) {
         color3 = color2;
      }

      if ((value & 256) != 0) {
         color4 = color3;
      }

      if ((value & 512) != 0) {
         amount5 = 0.0F;
      }

      if ((value & 1024) != 0) {
         amount6 = 0.0F;
      }

      if ((value & 2048) != 0) {
         amount7 = 1.0F;
      }

      if ((value & 4096) != 0) {
         amount8 = 1.0F;
      }

      render2D.updateMeshBuilder(meshBuilder, amount, amount2, amount3, amount4, color, color2, color3, color4, amount5, amount6, amount7, amount8);
   }

   public final void applyGetTexture(
      Matrix4f matrices,
      Identifier id,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      Color color1,
      Color color2,
      Color color3,
      Color color4,
      boolean flag,
      float amount5,
      float amount6,
      float amount7,
      float amount8
   ) {
      float amount42 = amount4 / 2.0F;
      float amount32 = amount3 / 2.0F;
      GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(id).getTextureView();
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 0, false, 6, null);
      MeshBuilder.defaultImpl(meshBuilder, amount - amount42, amount2 + amount42, 0.0F, 4, null).computeMeshBuilder(amount5, amount8).fetchAlpha(color1);
      MeshBuilder.defaultImpl(meshBuilder, amount + amount32, amount2 + amount42, 0.0F, 4, null).computeMeshBuilder(amount7, amount8).fetchAlpha(color2);
      MeshBuilder.defaultImpl(meshBuilder, amount + amount32, amount2 - amount42, 0.0F, 4, null).computeMeshBuilder(amount7, amount6).fetchAlpha(color3);
      MeshBuilder.defaultImpl(meshBuilder, amount - amount32, amount2 - amount42, 0.0F, 4, null).computeMeshBuilder(amount5, amount6).fetchAlpha(color4);
      MeshBuilder.defaultImpl(
         meshBuilder,
         flag ? Pipelines.INSTANCE.getRenderPipeline18() : Pipelines.INSTANCE.getRenderPipeline14(),
         MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
         null,
         matrices,
         (Matrix3x2f)(new Matrix3x2fStack()),
         MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
         null,
         68,
         null
      );
   }

   public final void render(Matrix3x2fStack matrices, float amount, float amount2, float amount3, float amount4, Color color, Color color2) {
      MeshBuilder.defaultImpl(
         MeshBuilder.defaultImpl(
               MeshBuilder.defaultImpl(new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, 0, true, 2, null), amount, amount2, 0.0F, 4, null).fetchAlpha(color),
               amount3,
               amount4,
               0.0F,
               4,
               null
            )
            .fetchAlpha(color2),
         Pipelines.INSTANCE.getRenderPipeline25(),
         null,
         null,
         null,
         new Matrix3x2f((Matrix3x2fc)matrices),
         null,
         null,
         110,
         null
      );
   }

   public static void defaultImpl(
      Render2D render2D,
      Matrix3x2fStack matrix3x2fStack,
      float amount,
      float amount2,
      float amount3,
      float amount4,
      Color color,
      Color color2,
      int value,
      Object object
   ) {
      if ((value & 64) != 0) {
         color2 = color;
      }

      render2D.render(matrix3x2fStack, amount, amount2, amount3, amount4, color, color2);
   }

   private final int computeValue(int value, int value2, float amount) {
      return MathKt.roundToInt(value + (value2 - value) * amount);
   }

   public final double computeAmount(double amount, double amount2, float amount3) {
      return amount + (amount2 - amount) * amount3;
   }

   public final double computeAmount(double amount, double amount2, double amount3) {
      return amount + (amount2 - amount) * amount3;
   }

   public final float computeAmount(float amount, float amount2, float amount3) {
      return amount + (amount2 - amount) * amount3;
   }

   @NotNull
   public final Vec3 fetchWindow(Vec3 pos) {
      Vec3 position;
      label20: {
         Camera camera = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera;
         if (camera != null) {
            position = camera.position();
            if (position != null) {
               break label20;
            }
         }

         position = Vec3.ZERO;
      }

      Vec3 vec3 = position;
      int screenHeight = MinecraftHolder.getMinecraft().getWindow().getScreenHeight() * (this.isFlag() ? 2 : 1);
      Vector3f vector3f = new Vector3f();
      double value2 = pos.x - vec3.x;
      double value3 = pos.y - vec3.y;
      double value4 = pos.z - vec3.z;
      Vector4f matrix4f22 = new Vector4f((float)value2, (float)value3, (float)value4, 1.0F).mul((Matrix4fc)matrix4f2);
      Matrix4f matrix4f3 = new Matrix4f((Matrix4fc)Render3D.INSTANCE.getMatrix4f()).mul((Matrix4fc)matrix4f);
      Vector4f vector4f = new Vector4f((Vector4fc)matrix4f22).mul((Matrix4fc)matrix4f3);
      if (vector4f.w <= 0.0F) {
         return new Vec3(Double.NaN, Double.NaN, -1.0);
      } else {
         matrix4f3.project(matrix4f22.x(), matrix4f22.y(), matrix4f22.z(), values, vector3f);
         return new Vec3(
            (double)vector3f.x / MinecraftHolder.getMinecraft().getWindow().getGuiScale(),
            (double)(screenHeight - vector3f.y) / MinecraftHolder.getMinecraft().getWindow().getGuiScale(),
            vector3f.z
         );
      }
   }

   public final void render(PoseStack matrices, ItemStack stack) {
      if (!stack.isEmpty()) {
         matrices.pushPose();
         matrices.translate(8.0F, 8.0F, 150.0F);

         try {
            matrices.scale(16.0F, -16.0F, 16.0F);
            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            MinecraftHolder.getMinecraft()
               .getItemModelResolver()
               .updateForTopItem(itemStackRenderState, stack, ItemDisplayContext.GUI, (Level)MinecraftHolder.getClientLevel(), null, 0);
            itemStackRenderState.submit(matrices, (SubmitNodeCollector)submitNodeStorage, 15728880, OverlayTexture.NO_OVERLAY, 0);
         } catch (Throwable exception) {
         }

         matrices.popPose();
      }
   }

   public final void applyGetTextureManager(Identifier i, BufferedImage bi) {
      if (RenderSystem.isOnRenderThread()) {
         MinecraftHolder.getMinecraft().getTextureManager().register(i, (AbstractTexture)this.fetchHeight(bi));
      } else {
         this.testCallback(() -> {
            MinecraftHolder.getMinecraft().getTextureManager().register(i, (AbstractTexture)INSTANCE.fetchHeight(bi));
            return Unit.INSTANCE;
         });
      }
   }

   public final boolean testCallback(Function0 function) {
      return items.add(function);
   }

   @NotNull
   public final DynamicTexture fetchHeight(BufferedImage bi) {
      int width = bi.getWidth();
      int height = bi.getHeight();
      NativeImage nativeImage = new NativeImage(Format.RGBA, width, height, false);
      long pixels = ((NativeImageAccessor)(Object)nativeImage).getPixels();
      IntBuffer memIntBuffer = MemoryUtil.memIntBuffer(pixels, nativeImage.getWidth() * nativeImage.getHeight());
      Object object = null;
      WritableRaster raster = bi.getRaster();
      ColorModel colorModel = bi.getColorModel();
      int numBands = raster.getNumBands();
      int dataType = raster.getDataBuffer().getDataType();

      object = switch (dataType) {
         case 0 -> new byte[numBands];
         case 1 -> new short[numBands];
         default -> throw new IllegalArgumentException("Unknown data buffer type: " + dataType);
         case 3 -> new int[numBands];
         case 4 -> new float[numBands];
         case 5 -> new double[numBands];
      };

      for (int i = 0; i < height; i++) {
         for (int j = 0; j < width; j++) {
            raster.getDataElements(j, i, object);
            int alpha = colorModel.getAlpha(object);
            int red = colorModel.getRed(object);
            int green = colorModel.getGreen(object);
            int blue = colorModel.getBlue(object);
            int value2 = alpha << 24 | blue << 16 | green << 8 | red;
            memIntBuffer.put(value2);
         }
      }

      Supplier supplier = () -> "ctl_nibt_" + bi.hashCode();
      DynamicTexture dynamicTexture = new DynamicTexture(supplier, nativeImage);
      dynamicTexture.upload();
      return dynamicTexture;
   }

   @Nullable
   public final Vector4d fetchEyeHeight(Entity ent, float amount) {
      return this.computeVector4d(
         this.computeAmount(ent.xo, ent.getX(), this.getAmount2()),
         this.computeAmount(ent.yo, ent.getY(), this.getAmount2()) + ent.getEyeHeight(ent.getPose()) + amount,
         this.computeAmount(ent.zo, ent.getZ(), this.getAmount2())
      );
   }

   public static Vector4d defaultImpl(Render2D render2D, Entity entity, float amount, int value, Object object) {
      if ((value & 2) != 0) {
         amount = 0.4F;
      }

      return render2D.fetchEyeHeight(entity, amount);
   }

   @Nullable
   public final Vector4d computeVector4d(Vec3 pos) {
      return this.computeVector4d(pos.x, pos.y, pos.z);
   }

   @Nullable
   public final Vector4d computeVector4d(double amount, double amount2, double amount3) {
      Vector4d vector4d = null;
      Vec3 window = this.fetchWindow(new Vec3(amount, amount2, amount3));
      if (window.z > 0.0 && window.z < 1.0) {
         vector4d = new Vector4d(window.x, window.y, Math.max(window.x, window.z), 0.0);
      }

      return vector4d;
   }

   @NotNull
   public final Color computeColor(Color self, float amount) {
      return new Color(self.getRed(), self.getGreen(), self.getBlue(), (int)(255.0F * Math.clamp(amount, 0.0F, 1.0F)));
   }

   @NotNull
   public final Color fetchAlpha(Color self, Color end, float amount) {
      float amount2 = Math.clamp(amount, 0.0F, 1.0F);
      return new Color(
         this.computeValue(self.getRed(), end.getRed(), amount2),
         this.computeValue(self.getGreen(), end.getGreen(), amount2),
         this.computeValue(self.getBlue(), end.getBlue(), amount2),
         this.computeValue(self.getAlpha(), end.getAlpha(), amount2)
      );
   }

   public final float fetchRed(Color self) {
      return self.getRed() / 255.0F;
   }

   public final float fetchGreen(Color self) {
      return self.getGreen() / 255.0F;
   }

   public final float fetchBlue(Color self) {
      return self.getBlue() / 255.0F;
   }

   public final float fetchAlpha(Color self) {
      return self.getAlpha() / 255.0F;
   }

   public final float getAmount2() {
      return MinecraftHolder.getMinecraft().getDeltaTracker().getGameTimeDeltaPartialTick(true);
   }

   public final float computeAmount(float amount, float amount2) {
      return this.computeAmount(amount, amount2, this.getAmount2());
   }

   public final double computeAmount(double amount, double amount2) {
      return this.computeAmount(amount, amount2, this.getAmount2());
   }

   @NotNull
   public final Vec3[] computeVec3s(Entity ent) {
      AABB aabb = Render3D.INSTANCE.computeAABB(ent);
      return new Vec3[]{
         new Vec3(aabb.minX, aabb.minY, aabb.minZ),
         new Vec3(aabb.minX, aabb.maxY, aabb.minZ),
         new Vec3(aabb.maxX, aabb.minY, aabb.minZ),
         new Vec3(aabb.maxX, aabb.maxY, aabb.minZ),
         new Vec3(aabb.minX, aabb.minY, aabb.maxZ),
         new Vec3(aabb.minX, aabb.maxY, aabb.maxZ),
         new Vec3(aabb.maxX, aabb.minY, aabb.maxZ),
         new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ)
      };
   }

   public final void updateMeshBuilder2(MeshBuilder polygon, float amount, float amount2, float amount3, float amount4, Color color) {
      Color black = Color.BLACK;
      this.updateMeshBuilder(polygon, amount - 1.0F, amount2, amount + 0.5F, amount4 + 0.5F, black);
      this.updateMeshBuilder(polygon, amount - 1.0F, amount2 - 0.5F, amount3 + 0.5F, amount2 + 1.0F, black);
      this.updateMeshBuilder(polygon, amount3 - 1.0F, amount2, amount3 + 0.5F, amount4 + 0.5F, black);
      this.updateMeshBuilder(polygon, amount - 1.0F, amount4 - 1.0F, amount3 + 0.5F, amount4 + 0.5F, black);
      this.updateMeshBuilder(polygon, amount - 0.5F, amount2, amount, amount4, color);
      this.updateMeshBuilder(polygon, amount, amount4 - 0.5F, amount3, amount4, color);
      this.updateMeshBuilder(polygon, amount - 0.5F, amount2, amount3, amount2 + 0.5F, color);
      this.updateMeshBuilder(polygon, amount3 - 0.5F, amount2, amount3, amount4, color);
   }

   @NotNull
   public final Color computeColor(int value, int value2, float amount, float amount2) {
      int time = (int)((MinecraftHolder.getTime() / value + value2) % 360);
      float value3 = time / 360.0F;
      Color color = new Color(Color.HSBtoRGB(value3, amount, amount2));
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
   }

   @NotNull
   public final Color fetchHSBColor(int value, int value2) {
      int time = 0;
      time = (int)((MinecraftHolder.getTime() / value + value2) % 360);
      short shortValue = 360;
      time %= shortValue;
      return Color.getHSBColor((float)(shortValue / 360.0) < 0.5 ? -((float)(time / 360.0)) : (float)(time / 360.0), 0.5F, 1.0F);
   }

   private final boolean isFlag() {
      return KotlinCompat.contains(RenderSystem.getDevice().getDeviceInfo().vendorName(), "Apple", false, 2, null);
   }
}