package fun.volon.api.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import fun.volon.Volon;
import fun.volon.api.QClient;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.client.modules.impl.render.Interface;
import fun.volon.client.modules.impl.render.base.HudRectStyle;

/**
 * Accumulates HUD panel masks and applies the BlockOverlay shader once per frame (GUI space).
 */
public final class HudBlockOverlayShaderRenderer implements QClient {

    public static final HudBlockOverlayShaderRenderer INSTANCE = new HudBlockOverlayShaderRenderer();

    private final BlockOverlayShaderRenderer shader = new BlockOverlayShaderRenderer();
    private Framebuffer maskBuffer;
    private int fbWidth = -1;
    private int fbHeight = -1;
    private boolean frameActive;
    private boolean hasMask;

    private HudBlockOverlayShaderRenderer() {
    }

    public boolean isFrameActive() {
        return frameActive;
    }

    public void beginFrame() {
        ensureMaskBuffer();
        if (maskBuffer == null) {
            return;
        }

        frameActive = true;
        hasMask = false;
        maskBuffer.setClearColor(0f, 0f, 0f, 0f);
        maskBuffer.clear();
    }

    public void appendMask(MatrixStack matrices, float x, float y, float width, float height,
                           float topLeft, float topRight, float bottomRight, float bottomLeft) {
        if (!frameActive || maskBuffer == null || width <= 0.5f || height <= 0.5f) {
            return;
        }

        maskBuffer.beginWrite(false);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        RenderUtils.drawRoundedRect(
                matrices, x, y, width, height,
                topLeft, topRight, bottomRight, bottomLeft,
                0xFFFFFFFF
        );

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        mc.getFramebuffer().beginWrite(false);
        hasMask = true;
        RenderUtils.resetGuiRenderState();
    }

    /** Draws block-overlay on a HUD panel using the same UV math as liquid glass (GUI coords → mask texture). */
    public void renderPanelOverlay(MatrixStack matrices, float x, float y, float width, float height, HudRectStyle rectStyle) {
        if (!frameActive || !hasMask || maskBuffer == null || width <= 0.5f || height <= 0.5f) {
            return;
        }

        int color1 = resolveThemeColor1();
        int color2 = resolveThemeColor2();
        float alpha = resolveHudAlpha();
        HudRectStyle style = rectStyle != null ? rectStyle : HudRectStyle.SHADER;
        BlockOverlayShaderRenderer.Params params = style == HudRectStyle.THREADS
                ? BlockOverlayShaderRenderer.Params.hudThreads(color1, color2, alpha)
                : BlockOverlayShaderRenderer.Params.hud(color1, color2, alpha);
        renderPanelOverlay(matrices, x, y, width, height, maskBuffer, params, false);
    }

    private void renderPanelOverlay(MatrixStack matrices, float x, float y, float width, float height,
                                   Framebuffer mask, BlockOverlayShaderRenderer.Params params, boolean outlineOnly) {
        var shaderProgram = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.blockOverlay);
        if (shaderProgram == null) {
            return;
        }

        mc.getFramebuffer().beginWrite(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        RenderSystem.setShader(ShaderUtils.blockOverlay);
        RenderSystem.setShaderTexture(0, mask.getColorAttachment());

        float fbW = Math.max(1, mc.getWindow().getFramebufferWidth());
        float fbH = Math.max(1, mc.getWindow().getFramebufferHeight());
        float scale = (float) mc.getWindow().getScaleFactor();

        float scaledX = x * scale;
        float scaledY = y * scale;
        float scaledW = width * scale;
        float scaledH = height * scale;

        float u1 = scaledX / fbW;
        float v1 = 1.0f - (scaledY / fbH);
        float u2 = (scaledX + scaledW) / fbW;
        float v2 = 1.0f - ((scaledY + scaledH) / fbH);

        var shader = shaderProgram;
        var u = shader.getUniform("texelSize");
        if (u != null) u.set(1.0f / fbW, 1.0f / fbH);
        u = shader.getUniform("color");
        if (u != null) u.set(ColorUtils.redf(params.color1()), ColorUtils.greenf(params.color1()), ColorUtils.bluef(params.color1()));
        u = shader.getUniform("color2");
        if (u != null) u.set(ColorUtils.redf(params.color2()), ColorUtils.greenf(params.color2()), ColorUtils.bluef(params.color2()));
        u = shader.getUniform("time");
        if (u != null) u.set((System.currentTimeMillis() % 100000L) / 1000.0f);
        u = shader.getUniform("speed");
        if (u != null) u.set(params.waveSpeed());
        u = shader.getUniform("scale");
        if (u != null) u.set(params.waveScale());
        u = shader.getUniform("outline");
        if (u != null) u.set(params.outline());
        u = shader.getUniform("glow");
        if (u != null) u.set(params.glow());
        u = shader.getUniform("fill");
        if (u != null) u.set(params.fill());
        u = shader.getUniform("alpha");
        if (u != null) u.set(params.alpha());
        u = shader.getUniform("outlineOnly");
        if (u != null) u.set(outlineOnly ? 1f : 0f);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR
        );
        buffer.vertex(matrix, x, y, 0).texture(u1, v1).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x, y + height, 0).texture(u1, v2).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x + width, y + height, 0).texture(u2, v2).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x + width, y, 0).texture(u2, v1).color(1f, 1f, 1f, 1f);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderUtils.resetGuiRenderState();
    }

    public void appendMask(MatrixStack matrices, float x, float y, float width, float height, float radius) {
        appendMask(matrices, x, y, width, height, radius, radius, radius, radius);
    }

    public void finishFrame() {
        frameActive = false;
        hasMask = false;
    }

    public void destroy() {
        frameActive = false;
        hasMask = false;
        shader.destroy();
        if (maskBuffer != null) {
            maskBuffer.delete();
            maskBuffer = null;
        }
        fbWidth = -1;
        fbHeight = -1;
    }

    private void ensureMaskBuffer() {
        int w = mc.getWindow().getFramebufferWidth();
        int h = mc.getWindow().getFramebufferHeight();
        if (maskBuffer == null || fbWidth != w || fbHeight != h) {
            if (maskBuffer != null) {
                maskBuffer.delete();
            }
            maskBuffer = new SimpleFramebuffer(w, h, false);
            fbWidth = w;
            fbHeight = h;
        }
    }

    private float resolveHudAlpha() {
        return 150f / 255f; // фиксированная прозрачность 150
    }

    private int resolveThemeColor1() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null || Volon.INSTANCE.themeStorage.getThemes() == null) {
            return ColorUtils.getThemeColor(0);
        }
        var theme = Volon.INSTANCE.themeStorage.getThemes().getTheme();
        if (theme == null || theme.color == null || theme.color.length == 0) {
            return ColorUtils.getThemeColor(0);
        }
        if (!"Rainbow".equals(theme.getName())) {
            return theme.color[0];
        }
        return ColorUtils.getThemeColor();
    }

    private int resolveThemeColor2() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null || Volon.INSTANCE.themeStorage.getThemes() == null) {
            return ColorUtils.getThemeColor(180);
        }
        var theme = Volon.INSTANCE.themeStorage.getThemes().getTheme();
        if (theme == null) {
            return ColorUtils.getThemeColor(180);
        }
        if (!"Rainbow".equals(theme.getName())) {
            int base = theme.color != null && theme.color.length > 0 ? theme.color[0] : ColorUtils.getThemeColor(0);
            return base;
        }
        return ColorUtils.getThemeColor(180);
    }
}
