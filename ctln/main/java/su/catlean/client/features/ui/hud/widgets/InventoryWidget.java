// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import su.catlean.api.event.GofraState;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.render.uniforms.ProjectionBuffer;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.WidgetManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;

public final class InventoryWidget extends Widget {
   @NotNull
   public static final InventoryWidget INSTANCE = new InventoryWidget();
   @Nullable
   private static MainTarget mainTarget;
   private static boolean flag;
   private static long time;

   private InventoryWidget() {
      super("inventory-widget");
   }

   @Nullable
   public final MainTarget getMainTarget() {
      return mainTarget;
   }

   public final void setMainTarget(MainTarget mainTarget) {
      InventoryWidget.mainTarget = mainTarget;
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      InventoryWidget.flag = flag;
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      if (mainTarget == null) {
         mainTarget = new MainTarget(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      if (MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width != mainTarget.width
         || MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height != mainTarget.height) {
         mainTarget.resize(
            MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().width, MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget().height
         );
      }

      String text = "Inventory";
      float amount3 = this.getAmount3();
      Widget.defaultImpl(this, context.pose(), amount3, this.getAmount4(), 145.0F, 13.0F, 0.0F, false, 96, null);
      Render2D instance = Render2D.INSTANCE;
      Matrix3x2fStack pose = context.pose();
      float value = amount3 + 14;
      float amount4 = this.getAmount4() + 3.0F;
      Color[] colors = new Color[]{new Color(1157627903, true)};
      float value2 = 7.5F;
      float value3 = 0.5F;
      float value4 = amount4;
      float value5 = value;
      Matrix3x2fStack matrix3x2fStack = pose;
      instance.render(matrix3x2fStack, value5, value4, value3, value2, colors);
      Widget.defaultImpl(this, this.getSvgTexture2(), context.pose(), amount3 + 2.0F, this.getAmount4(), 10.0F, 10.0F, 0.0F, 64, null);
      this.render(context, text, amount3 + 18.0F, this.getAmount4() + 1.0F, ThemeManager.INSTANCE.getColor7());
      int value6 = 9;
      byte byteValue = 3;

      for (int i = 0; i < byteValue; i++) {
         float amount42 = INSTANCE.getAmount4() + i * 17 + 14;
         Widget.defaultImpl(INSTANCE, context.pose(), amount3, amount42, 145.0F, 16.0F, 0.0F, false, 96, null);
         byte byteValue2 = 9;

         for (int j = 0; j < byteValue2; j++) {
            if (j < 8) {
               Widget.defaultImpl(INSTANCE, context.pose(), amount3 + 16 + j * 16, amount42 + 4.0F, 0.0F, 8, null);
            }

            value6++;
         }
      }

      this.render(context, amount3);
      this.updateAmount(145.0F, 70.0F);
   }

   @Override
   public void render2(GuiGraphicsExtractor context) {
      this.applyGetWindow();
      this.render2(context, this.getAmount3());
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, float amount) {
      if (MinecraftHolder.getTime() - time >= 40L || Intrinsics.areEqual(WidgetManager.INSTANCE.getWidget(), this)) {
         time = MinecraftHolder.getTime();
         GofraState.INSTANCE.setModifyBuffer(true);
         flag = true;
         RenderSystem.getDevice()
            .createCommandEncoder()
            .clearColorAndDepthTextures(mainTarget.getColorTexture(), (Vector4fc)(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F)), mainTarget.getDepthTexture(), 0.0);
         PoseStack poseStack = new PoseStack();
         RenderSystem.backupProjectionMatrix();
         RenderSystem.setProjectionMatrix(
            ProjectionBuffer.INSTANCE
               .getProjectionMatrixBuffer()
               .getBuffer(
                  new Matrix4f()
                     .identity()
                     .setOrtho(
                        0.0F,
                        MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth(),
                        MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight(),
                        0.0F,
                        -160.0F,
                        160.0F,
                        RenderSystem.getDevice().getDeviceInfo().isZZeroToOne()
                     )
               ),
            RenderSystem.getProjectionType()
         );
         MinecraftHolder.getMinecraft().gameRenderer.lighting().setupFor(Entry.ITEMS_FLAT);
         int value = 0;
         value = 9;
         byte byteValue = 3;

         for (int i = 0; i < byteValue; i++) {
            float amount4 = INSTANCE.getAmount4() + i * 17 + 14;
            byte byteValue2 = 9;

            for (int j = 0; j < byteValue2; j++) {
               ItemStack item = MinecraftHolder.getLocalPlayer().getInventory().getItem(value);
               poseStack.pushPose();
               poseStack.translate(amount + 2.5F + j * 16, amount4 + 2.5F, 0.0F);
               poseStack.scale(0.75F, 0.75F, 1.0F);
               Render2D.INSTANCE.render(poseStack, item);
               poseStack.popPose();
               value++;
            }
         }

         GpuTextureView outputColorTextureOverride = RenderSystem.outputColorTextureOverride;
         GpuTextureView outputDepthTextureOverride = RenderSystem.outputDepthTextureOverride;

         try {
            RenderSystem.outputColorTextureOverride = mainTarget.getColorTextureView();
            RenderSystem.outputDepthTextureOverride = mainTarget.getDepthTextureView();
            MinecraftHolder.getMinecraft().gameRenderer.featureRenderDispatcher().renderAllFeatures(Render2D.INSTANCE.getSubmitNodeStorage());
         } finally {
            RenderSystem.outputColorTextureOverride = outputColorTextureOverride;
            RenderSystem.outputDepthTextureOverride = outputDepthTextureOverride;
         }

         Render2D.INSTANCE.updateSubmitNodeStorage(new SubmitNodeStorage());
         RenderSystem.restoreProjectionMatrix();
         flag = false;
         GofraState.INSTANCE.setModifyBuffer(false);
      }
   }

   private final void applyGetWindow() {
      float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth();
      float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight();
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 0, false, 6, null);
      MeshBuilder.defaultImpl(meshBuilder, 0.0F, guiScaledHeight, 0.0F, 4, null).computeMeshBuilder(0.0F, 0.0F).fetchAlpha(Color.WHITE);
      MeshBuilder.defaultImpl(meshBuilder, guiScaledWidth, guiScaledHeight, 0.0F, 4, null).computeMeshBuilder(1.0F, 0.0F).fetchAlpha(Color.WHITE);
      MeshBuilder.defaultImpl(meshBuilder, guiScaledWidth, 0.0F, 0.0F, 4, null).computeMeshBuilder(1.0F, 1.0F).fetchAlpha(Color.WHITE);
      MeshBuilder.defaultImpl(meshBuilder, 0.0F, 0.0F, 0.0F, 4, null).computeMeshBuilder(0.0F, 1.0F).fetchAlpha(Color.WHITE);
      MeshBuilder.defaultImpl(
         meshBuilder,
         Pipelines.INSTANCE.getRenderPipeline14(),
         MinecraftHolder.getMinecraft().gameRenderer.mainRenderTarget(),
         null,
         null,
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", mainTarget.getColorTextureView())),
         null,
         92,
         null
      );
   }

   private final void render2(GuiGraphicsExtractor guiGraphicsExtractor, float amount) {
      int value = 0;
      value = 9;
      byte byteValue = 3;

      for (int i = 0; i < byteValue; i++) {
         float amount4 = INSTANCE.getAmount4() + i * 17 + 14;
         byte byteValue2 = 9;

         for (int j = 0; j < byteValue2; j++) {
            ItemStack item = MinecraftHolder.getLocalPlayer().getInventory().getItem(value);
            if (item.getCount() > 1) {
               FontRenderer.defaultImpl(
                  Fonts.getFontRenderer10(),
                  guiGraphicsExtractor.pose(),
                  String.valueOf(item.getCount()),
                  amount + 2.0F + j * 16 + 4.0F,
                  amount4 + 1.5F + 4.0F,
                  ThemeManager.INSTANCE.getColor7(),
                  false,
                  0,
                  96,
                  null
               );
            }

            value++;
         }
      }

      Fonts.getFontRenderer10().update();
   }
}