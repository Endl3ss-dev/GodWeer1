package tech.lightclient.ui.gui;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders Minecraft item icons so they can be composited ABOVE the custom ClickGui overlay.
 *
 * <p>The overlay (a {@link tech.lightclient.client.utils.render.main.core.Renderer2D} pass) runs
 * after {@code Framebuffer.blitToScreen()}. Vanilla {@code DrawContext.drawItem} always commits
 * beneath it, because on 1.21.11 the shared {@code GuiRenderState} is flushed by the vanilla
 * {@link GuiRenderer} into {@code mc.getFramebuffer()} once per frame, before the overlay.</p>
 *
 * <p>The vanilla {@link GuiRenderer#render} hard-targets {@code mc.getFramebuffer()} and ignores
 * {@code RenderSystem.outputColorTextureOverride} for ordinary elements, so to capture icons we
 * temporarily redirect {@code mc.getFramebuffer()} to a private full-screen scratch framebuffer
 * (see the {@code getFramebuffer} redirect in MinecraftClientMixin driven by {@link #isRedirectingFramebuffer()}
 * / {@link #redirectFramebuffer()}), draw all requested items there in real GUI coordinates, run
 * the GuiRenderer once, then blit the relevant regions through Renderer2D in the overlay pass.</p>
 */
public final class ItemTextureCache {

    private static final ItemTextureCache INSTANCE = new ItemTextureCache();

    public static ItemTextureCache get() {
        return INSTANCE;
    }

    /** A single icon requested for this frame, positioned in absolute framebuffer pixels. */
    private record Request(ItemStack stack, float absX, float absY, float absSize) {
    }

    private final List<Request> requests = new ArrayList<>();

    private SimpleFramebuffer scratch;
    private int scratchGlId;
    private int scratchW;
    private int scratchH;
    /** True once this frame's captures have been committed into the scratch framebuffer. */
    private boolean captured;

    /** Set only while our capture render is running so MinecraftClientMixin can redirect getFramebuffer(). */
    private static volatile boolean redirecting = false;

    public static boolean isRedirectingFramebuffer() {
        return redirecting;
    }

    public static Framebuffer redirectFramebuffer() {
        return INSTANCE.scratch;
    }

    /**
     * Queue an item icon for this frame. Coordinates are in absolute framebuffer pixels (the same
     * space Renderer2D bakes into its transform). Returns immediately; the capture happens on the
     * GUI phase and the icon is blitted by {@link #drawCaptured}.
     */
    public void requestIcon(ItemStack stack, float absX, float absY, float absSize) {
        if (stack == null || stack.isEmpty() || absSize <= 0f
                || !Float.isFinite(absX) || !Float.isFinite(absY) || !Float.isFinite(absSize)) {
            return;
        }
        requests.add(new Request(stack.copy(), absX, absY, absSize));
    }

    /**
     * Blit a previously requested icon from the scratch framebuffer through Renderer2D, so it lands
     * on top of the overlay. Must be called during the overlay pass, AFTER {@link #flushCaptures}
     * ran for this frame (order is guaranteed: capture happens in the GUI phase, overlay runs later).
     */
    public void drawCaptured(tech.lightclient.client.utils.render.main.core.Renderer2D r,
                              float absX, float absY, float absSize, int tintRgba) {
        drawCaptured(r, absX, absY, absSize, absX, absY, absSize, tintRgba);
    }

    public void drawCaptured(tech.lightclient.client.utils.render.main.core.Renderer2D r,
                             float sourceAbsX, float sourceAbsY, float sourceAbsSize,
                             float destinationX, float destinationY, float destinationSize, int tintRgba) {
        if (!captured || scratchGlId <= 0 || scratchW <= 0 || scratchH <= 0) {
            return;
        }
        // Renderer2D works in the same absolute-framebuffer space; the scratch holds the icon at the
        // exact absolute coordinates we drew it, so the UV region matches the destination rectangle.
        float u0 = sourceAbsX / scratchW;
        float u1 = (sourceAbsX + sourceAbsSize) / scratchW;
        // Minecraft framebuffer textures are stored bottom-up, so screen row y maps to tex v = 1 - y/H.
        // Invert the region's V coordinates directly (a plain v0<->v1 swap would only mirror locally).
        float v0 = 1f - (sourceAbsY / scratchH);
        float v1 = 1f - ((sourceAbsY + sourceAbsSize) / scratchH);
        r.drawRgbaTextureRegion(scratchGlId, destinationX, destinationY, destinationSize, destinationSize,
                u0, v0, u1, v1, tintRgba, false);
    }

    /**
     * Commit all queued icons into the scratch framebuffer. Must run in the vanilla GUI phase while
     * the supplied {@link GuiRenderer}/{@link GuiRenderState} are live (see GameRendererMixin).
     */
    public void flushCaptures(DrawContext frameContext, GuiRenderer guiRenderer,
                              GuiRenderState guiState, GpuBufferSlice fogBuffer) {
        captured = false;
        if (requests.isEmpty() || frameContext == null || guiRenderer == null || guiState == null) {
            requests.clear();
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        int fbW = mc.getWindow().getFramebufferWidth();
        int fbH = mc.getWindow().getFramebufferHeight();
        double windowScale = mc.getWindow().getScaleFactor();
        if (fbW <= 0 || fbH <= 0 || windowScale <= 0d || !ensureScratch(fbW, fbH)) {
            requests.clear();
            return;
        }

        try {
            // Clear the scratch to fully transparent so only item pixels survive the overlay blend.
            var encoder = RenderSystem.getDevice().createCommandEncoder();
            encoder.clearColorTexture(scratch.getColorAttachment(), 0);

            // GuiRenderer.render() clears the shared state itself, so it is empty here; be defensive.
            guiState.clear();

            // Draw each item at its real GUI position (absolute px / window scale) so the scratch
            // framebuffer mirrors the on-screen layout 1:1 and region blits line up exactly.
            var matrices = frameContext.getMatrices();
            for (Request request : requests) {
                float guiX = (float) (request.absX() / windowScale);
                float guiY = (float) (request.absY() / windowScale);
                float guiItemScale = (float) (request.absSize() / (16f * windowScale));
                if (!Float.isFinite(guiX) || !Float.isFinite(guiY) || !Float.isFinite(guiItemScale) || guiItemScale <= 0f) {
                    continue;
                }
                matrices.pushMatrix();
                try {
                    matrices.translate(guiX, guiY);
                    matrices.scale(guiItemScale, guiItemScale);
                    frameContext.drawItem(request.stack(), 0, 0);
                } finally {
                    matrices.popMatrix();
                }
            }

            // Redirect the vanilla GuiRenderer to our scratch framebuffer for this single render.
            redirecting = true;
            try {
                guiRenderer.render(fogBuffer);
            } finally {
                redirecting = false;
            }
            // render() clears the shared state; vanilla will repopulate it next frame.
            captured = true;
        } catch (Throwable ignored) {
            // Any failure just leaves icons hidden this frame; never break the render loop.
            redirecting = false;
            captured = false;
            guiState.clear();
        } finally {
            requests.clear();
        }
    }

    private boolean ensureScratch(int width, int height) {
        if (scratch != null && scratchW == width && scratchH == height && scratchGlId > 0) {
            return true;
        }
        deleteScratch();
        try {
            scratch = new SimpleFramebuffer("light_item_scratch", width, height, false);
            scratchGlId = glId(scratch);
            scratchW = width;
            scratchH = height;
            return scratchGlId > 0;
        } catch (Throwable ignored) {
            deleteScratch();
            return false;
        }
    }

    private void deleteScratch() {
        if (scratch != null) {
            try {
                scratch.delete();
            } catch (Throwable ignored) {
            }
        }
        scratch = null;
        scratchGlId = 0;
        scratchW = 0;
        scratchH = 0;
    }

    private static int glId(SimpleFramebuffer framebuffer) {
        GpuTexture texture = framebuffer.getColorAttachment();
        return texture instanceof GlTexture glTexture ? glTexture.getGlId() : 0;
    }
}
