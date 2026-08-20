package fun.volon.api.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import fun.volon.api.QClient;
import fun.volon.api.utils.color.ColorUtils;

public final class BlockOverlayShaderRenderer implements QClient {

    private Framebuffer maskBuffer;
    private int fbWidth = -1;
    private int fbHeight = -1;
    private boolean hasMask;

    public boolean hasMask() {
        return hasMask;
    }

    public void clearMask() {
        hasMask = false;
    }

    public void destroy() {
        clearMask();
        if (maskBuffer != null) {
            maskBuffer.delete();
            maskBuffer = null;
        }
        fbWidth = -1;
        fbHeight = -1;
    }

    public void writeMask(Matrix4f matrix, Box localBox) {
        ensureMaskBuffer();
        if (maskBuffer == null) return;

        hasMask = true;
        maskBuffer.setClearColor(0f, 0f, 0f, 0f);
        maskBuffer.clear();
        copyMainDepthToMask();
        maskBuffer.beginWrite(false);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        drawMaskBox(matrix, localBox);
        RenderSystem.depthMask(true);
        RenderSystem.disableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        mc.getFramebuffer().beginWrite(false);
    }

    public void renderOverlay(Params params) {
        if (!hasMask || maskBuffer == null) return;
        renderOverlayFromMask(maskBuffer, params);
    }

    public void renderOverlayFromMask(Framebuffer mask, Params params) {
        if (mask == null || mask.getColorAttachment() == 0) return;

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.blockOverlay);
        if (shader == null) return;

        mc.getFramebuffer().beginWrite(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        RenderSystem.setShader(ShaderUtils.blockOverlay);
        RenderSystem.setShaderTexture(0, mask.getColorAttachment());

        setUniform(shader, "texelSize",
                1.0f / Math.max(1, mc.getWindow().getFramebufferWidth()),
                1.0f / Math.max(1, mc.getWindow().getFramebufferHeight()));
        setUniform(shader, "color", ColorUtils.redf(params.color1()), ColorUtils.greenf(params.color1()), ColorUtils.bluef(params.color1()));
        setUniform(shader, "color2", ColorUtils.redf(params.color2()), ColorUtils.greenf(params.color2()), ColorUtils.bluef(params.color2()));
        setUniform(shader, "time", (System.currentTimeMillis() % 100000L) / 1000.0f);
        setUniform(shader, "speed", params.waveSpeed());
        setUniform(shader, "scale", params.waveScale());
        setUniform(shader, "outline", params.outline());
        setUniform(shader, "glow", params.glow());
        setUniform(shader, "fill", params.fill());
        setUniform(shader, "alpha", params.alpha());
        setUniform(shader, "outlineOnly", 0.0f);

        drawFullscreenQuad();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, 0);
    }

    public record Params(
            int color1,
            int color2,
            float waveSpeed,
            float waveScale,
            float outline,
            float glow,
            float fill,
            float alpha
    ) {
        public static Params defaults(int color1, int color2) {
            return new Params(color1, color2, 1.2f, 1.0f, 1.1f, 1.0f, 0.6f, 1.0f);
        }

        public static Params bright(int color1, int color2) {
            return new Params(color1, color2, 1.35f, 1.0f, 1.65f, 2.6f, 0.95f, 1.0f);
        }

        public static Params predict(int color1, int color2) {
            return new Params(color1, color2, 1.55f, 1.0f, 2.15f, 4.2f, 1.0f, 1.0f);
        }

        public static Params hud(int color1, int color2, float alpha) {
            float boosted = Math.min(1f, alpha * 1.18f);
            return new Params(color1, color2, 1.35f, 1.0f, 1.25f, 1.85f, 0.88f, boosted);
        }

        public static Params hudThreads(int color1, int color2, float alpha) {
            return new Params(color1, color2, 1.2f, 1.0f, 1.05f, 0.45f, 0.32f, Math.min(1f, alpha * 0.92f));
        }
    }

    private void ensureMaskBuffer() {
        int w = mc.getWindow().getFramebufferWidth();
        int h = mc.getWindow().getFramebufferHeight();
        if (maskBuffer == null || fbWidth != w || fbHeight != h) {
            if (maskBuffer != null) {
                maskBuffer.delete();
            }
            maskBuffer = new SimpleFramebuffer(w, h, true);
            fbWidth = w;
            fbHeight = h;
        }
    }

    private void copyMainDepthToMask() {
        if (maskBuffer == null) return;

        int readFbo = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int drawFbo = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        int w = mc.getWindow().getFramebufferWidth();
        int h = mc.getWindow().getFramebufferHeight();

        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mc.getFramebuffer().fbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, maskBuffer.fbo);
        GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h, GL11.GL_DEPTH_BUFFER_BIT, GL11.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readFbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, drawFbo);
    }

    private void drawMaskBox(Matrix4f matrix, Box box) {
        BufferBuilder b = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        int white = 0xFFFFFFFF;

        b.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(white);
        b.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(white);
        b.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(white);
        b.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(white);

        b.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(white);
        b.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(white);
        b.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(white);
        b.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(white);

        b.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(white);
        b.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(white);
        b.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(white);
        b.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(white);

        b.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(white);
        b.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(white);
        b.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(white);
        b.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(white);

        b.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.minZ).color(white);
        b.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.minZ).color(white);
        b.vertex(matrix, (float) box.minX, (float) box.maxY, (float) box.maxZ).color(white);
        b.vertex(matrix, (float) box.minX, (float) box.minY, (float) box.maxZ).color(white);

        b.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.minZ).color(white);
        b.vertex(matrix, (float) box.maxX, (float) box.minY, (float) box.maxZ).color(white);
        b.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(white);
        b.vertex(matrix, (float) box.maxX, (float) box.maxY, (float) box.minZ).color(white);

        BufferRenderer.drawWithGlobalProgram(b.end());
    }

    private void drawFullscreenQuad() {
        BufferBuilder b = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        float width = Math.max(mc.getWindow().getScaledWidth(), 1);
        float height = Math.max(mc.getWindow().getScaledHeight(), 1);
        b.vertex(0, 0, 0).texture(0, 1).color(1f, 1f, 1f, 1f);
        b.vertex(0, height, 0).texture(0, 0).color(1f, 1f, 1f, 1f);
        b.vertex(width, height, 0).texture(1, 0).color(1f, 1f, 1f, 1f);
        b.vertex(width, 0, 0).texture(1, 1).color(1f, 1f, 1f, 1f);
        BufferRenderer.drawWithGlobalProgram(b.end());
    }

    private void setUniform(ShaderProgram shader, String name, float value) {
        GlUniform uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(value);
    }

    private void setUniform(ShaderProgram shader, String name, float x, float y) {
        GlUniform uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(x, y);
    }

    private void setUniform(ShaderProgram shader, String name, float x, float y, float z) {
        GlUniform uniform = shader.getUniform(name);
        if (uniform != null) uniform.set(x, y, z);
    }
}
