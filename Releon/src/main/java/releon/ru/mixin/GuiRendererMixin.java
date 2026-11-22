package releon.ru.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat.IndexType;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.utils.render.item.RenderItem;
import releon.ru.utils.render.ui.blur.BlurFramebuffer;
import releon.ru.utils.render.ui.glass.GlassRenderer;
import releon.ru.utils.render.ui.image.ImageRenderer;
import releon.ru.utils.render.ui.outline.outline360.Outline360Renderer;
import releon.ru.utils.render.ui.outline.outlinedefault.DefaultOutlineRenderer;
import releon.ru.utils.render.ui.outline.outlineglass.GlassOutlineRenderer;
import releon.ru.utils.render.ui.rectangle.rectdefault.DefaultRectangleRenderer;
import releon.ru.utils.render.ui.rectangle.recthalficon.HalfIconRectangleRenderer;
import releon.ru.utils.render.ui.rectangle.recthalftone.HalftoneRectangleRenderer;
import releon.ru.utils.render.ui.ripple.RippleRenderer;
import releon.ru.utils.render.ui.zippy.ZippyRenderer;

@Mixin({GuiRenderer.class})
public abstract class GuiRendererMixin {
   private RenderPass releon$currentRenderPass;
   private boolean releon$blurDrawActive;
   private boolean releon$glassDrawActive;
   private boolean releon$glassOutlineDrawActive;
   private boolean releon$rectangleDrawActive;
   private boolean releon$halfIconRectangleDrawActive;
   private boolean releon$halftoneRectangleDrawActive;
   private boolean releon$zippyDrawActive;
   private boolean releon$outlineDrawActive;
   private boolean releon$outline360DrawActive;
   private boolean releon$imageDrawActive;
   private boolean releon$itemDrawActive;
   private boolean releon$rippleDrawActive;

   public GuiRendererMixin() {
   }

   @Inject(
      method = {"render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"},
      at = {@At("HEAD")}
   )
   private void releon$beginBlurFrame(GpuBufferSlice fogBuffer, CallbackInfo ci) {
      BlurFramebuffer.getInstance().beginGuiFrame();
      GlassRenderer.getInstance().beginGuiFrame();
      GlassOutlineRenderer.getInstance().beginGuiFrame();
      DefaultRectangleRenderer.getInstance().beginGuiFrame();
      HalfIconRectangleRenderer.getInstance().beginGuiFrame();
      HalftoneRectangleRenderer.getInstance().beginGuiFrame();
      ZippyRenderer.getInstance().beginGuiFrame();
      DefaultOutlineRenderer.getInstance().beginGuiFrame();
      Outline360Renderer.getInstance().beginGuiFrame();
      ImageRenderer.getInstance().beginGuiFrame();
      RippleRenderer.getInstance().beginGuiFrame();
      RenderItem.beginGuiFrame();
   }

   @Inject(
      method = {"prepare"},
      at = {@At("HEAD")}
   )
   private void releon$preparePendingBlurResources(CallbackInfo ci) {
      BlurFramebuffer.getInstance().preparePending();
   }

   @Inject(
      method = {"prepare"},
      at = {@At("RETURN")}
   )
   private void releon$prepareRenderUniforms(CallbackInfo ci) {
      BlurFramebuffer.getInstance().prepareBuffers();
      GlassRenderer.getInstance().prepareBuffers();
      GlassOutlineRenderer.getInstance().prepareBuffers();
      DefaultRectangleRenderer.getInstance().prepareBuffers();
      HalfIconRectangleRenderer.getInstance().prepareBuffers();
      HalftoneRectangleRenderer.getInstance().prepareBuffers();
      ZippyRenderer.getInstance().prepareBuffers();
      DefaultOutlineRenderer.getInstance().prepareBuffers();
      Outline360Renderer.getInstance().prepareBuffers();
      ImageRenderer.getInstance().prepareBuffers();
      RippleRenderer.getInstance().prepareBuffers();
      RenderItem.prepareBuffers();
   }

   @Inject(
      method = {"renderPreparedDraws"},
      at = {@At("HEAD")}
   )
   private void releon$prepareBlurCapture(GpuBufferSlice fogBuffer, CallbackInfo ci) {
      BlurFramebuffer.getInstance().prepareGuiDraw();
   }

   @Redirect(
      method = {"render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/systems/RenderPass;setPipeline(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)V"
      )
   )
   private void releon$trackPipeline(RenderPass renderPass, RenderPipeline pipeline) {
      this.releon$currentRenderPass = renderPass;
      this.releon$blurDrawActive = BlurFramebuffer.getInstance().isBlurPipeline(pipeline);
      this.releon$glassDrawActive = GlassRenderer.getInstance().isGlassPipeline(pipeline);
      this.releon$glassOutlineDrawActive = GlassOutlineRenderer.getInstance().isGlassOutlinePipeline(pipeline);
      this.releon$rectangleDrawActive = DefaultRectangleRenderer.getInstance().isRectanglePipeline(pipeline);
      this.releon$halfIconRectangleDrawActive = HalfIconRectangleRenderer.getInstance().isHalfIconRectanglePipeline(pipeline);
      this.releon$halftoneRectangleDrawActive = HalftoneRectangleRenderer.getInstance().isHalftoneRectanglePipeline(pipeline);
      this.releon$zippyDrawActive = ZippyRenderer.getInstance().isZippyPipeline(pipeline);
      this.releon$outlineDrawActive = DefaultOutlineRenderer.getInstance().isOutlinePipeline(pipeline);
      this.releon$outline360DrawActive = Outline360Renderer.getInstance().isOutline360Pipeline(pipeline);
      this.releon$imageDrawActive = ImageRenderer.getInstance().isImagePipeline(pipeline);
      this.releon$rippleDrawActive = RippleRenderer.getInstance().isRipplePipeline(pipeline);
      this.releon$itemDrawActive = RenderItem.isItemPipeline(pipeline);
      renderPass.setPipeline(pipeline);
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V",
         shift = Shift.BEFORE
      )}
   )
   private void releon$bindBlurParams(@Coerce Object draw, RenderPass renderPass, GpuBuffer gpuBuffer, IndexType indexType, CallbackInfo ci) {
      if (this.releon$blurDrawActive && this.releon$currentRenderPass != null) {
         BlurFramebuffer.getInstance().bindBlurParams(this.releon$currentRenderPass);
      }

      if (this.releon$glassDrawActive && this.releon$currentRenderPass != null) {
         GlassRenderer.getInstance().bindParams(this.releon$currentRenderPass);
      }

      if (this.releon$glassOutlineDrawActive && this.releon$currentRenderPass != null) {
         GlassOutlineRenderer.getInstance().bindParams(this.releon$currentRenderPass);
      }

      if (this.releon$rectangleDrawActive && this.releon$currentRenderPass != null) {
         DefaultRectangleRenderer.getInstance().bindParams(this.releon$currentRenderPass);
      }

      if (this.releon$halfIconRectangleDrawActive && this.releon$currentRenderPass != null) {
         HalfIconRectangleRenderer.getInstance().bindParams(this.releon$currentRenderPass);
      }

      if (this.releon$halftoneRectangleDrawActive && this.releon$currentRenderPass != null) {
         HalftoneRectangleRenderer.getInstance().bindParams(this.releon$currentRenderPass);
      }

      if (this.releon$zippyDrawActive && this.releon$currentRenderPass != null) {
         ZippyRenderer.getInstance().bindParams(this.releon$currentRenderPass);
      }

      if (this.releon$outlineDrawActive && this.releon$currentRenderPass != null) {
         DefaultOutlineRenderer.getInstance().bindParams(this.releon$currentRenderPass);
      }

      if (this.releon$outline360DrawActive && this.releon$currentRenderPass != null) {
         Outline360Renderer.getInstance().bindParams(this.releon$currentRenderPass);
      }

      if (this.releon$imageDrawActive && this.releon$currentRenderPass != null) {
         ImageRenderer.getInstance().bindParams(this.releon$currentRenderPass);
      }

      if (this.releon$rippleDrawActive && this.releon$currentRenderPass != null) {
         RippleRenderer.getInstance().bindParams(this.releon$currentRenderPass);
      }

      if (this.releon$itemDrawActive && this.releon$currentRenderPass != null) {
         RenderItem.bindParams(this.releon$currentRenderPass);
      }
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V"},
      at = {@At("RETURN")}
   )
   private void releon$clearTrackedPipeline(@Coerce Object draw, RenderPass renderPass, GpuBuffer gpuBuffer, IndexType indexType, CallbackInfo ci) {
      this.releon$currentRenderPass = null;
      this.releon$blurDrawActive = false;
      this.releon$glassDrawActive = false;
      this.releon$glassOutlineDrawActive = false;
      this.releon$rectangleDrawActive = false;
      this.releon$halfIconRectangleDrawActive = false;
      this.releon$halftoneRectangleDrawActive = false;
      this.releon$zippyDrawActive = false;
      this.releon$outlineDrawActive = false;
      this.releon$outline360DrawActive = false;
      this.releon$imageDrawActive = false;
      this.releon$itemDrawActive = false;
      this.releon$rippleDrawActive = false;
   }
}
