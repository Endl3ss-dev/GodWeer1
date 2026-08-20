package fun.volon.api.utils.render.blur;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import fun.volon.api.QClient;
import fun.volon.api.utils.render.ShaderUtils;

/**
 * Rounded-rect polar blur (salvaged from Rockstar SRC Ready).
 *
 * <p>Single-pass fragment shader sampling the main framebuffer: 16 angular directions × 5 radial
 * rings (80 taps) averaged, masked by a rounded-box SDF. The main FBO is copied into a temporary
 * linear-filtered capture buffer each draw, then composited back through the mask.</p>
 *
 * <p>Uniforms: {@code Size}, {@code Radius}, {@code Smoothness}, {@code BlurRadius}.</p>
 */
public class Blur implements QClient {

    /** Memoized linear-filtered capture buffer, resized to match the main framebuffer. */
    private static final Supplier<SimpleFramebuffer> CAPTURE = Suppliers.memoize(() -> {
        SimpleFramebuffer fbo = new SimpleFramebuffer(1920, 1024, false);
        setLinear(fbo);
        return fbo;
    });

    private static void setLinear(Framebuffer fbo) {
        RenderSystem.bindTexture(fbo.getColorAttachment());
        RenderSystem.texParameter(3553, 10241, 9729); // GL_TEXTURE_MIN_FILTER <- GL_LINEAR
        RenderSystem.texParameter(3553, 10240, 9729); // GL_TEXTURE_MAG_FILTER <- GL_LINEAR
        RenderSystem.bindTexture(0);
    }

    /**
     * Render a rounded-rect region blurred against the main framebuffer.
     *
     * @param matrices   current GUI matrix stack
     * @param x          top-left x (scaled pixels)
     * @param y          top-left y (scaled pixels)
     * @param width      region width
     * @param height     region height
     * @param radius     corner radius
     * @param blurRadius blur strength (texel-multiplier)
     */
    public void render(MatrixStack matrices, float x, float y, float width, float height,
                       float radius, float blurRadius) {
        if (width <= 0.5f || height <= 0.5f) {
            return;
        }

        Framebuffer main = mc.getFramebuffer();
        if (main == null || main.getColorAttachment() <= 0) {
            return;
        }

        SimpleFramebuffer fbo = CAPTURE.get();
        if (fbo.textureWidth != main.textureWidth || fbo.textureHeight != main.textureHeight) {
            fbo.resize(main.textureWidth, main.textureHeight);
            setLinear(fbo);
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        // 1) Copy the current framebuffer contents into our linear-filtered capture FBO.
        fbo.beginWrite(false);
        main.draw(fbo.textureWidth, fbo.textureHeight);

        // 2) Composite the rounded-rect blur back onto the main framebuffer.
        main.beginWrite(false);

        RenderSystem.setShaderTexture(0, fbo.getColorAttachment());

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.blur);
        pushUniforms(shader, width, height, radius, blurRadius);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(matrix, x, y, 0).color(1f, 1f, 1f, 1f);
        builder.vertex(matrix, x, y + height, 0).color(1f, 1f, 1f, 1f);
        builder.vertex(matrix, x + width, y + height, 0).color(1f, 1f, 1f, 1f);
        builder.vertex(matrix, x + width, y, 0).color(1f, 1f, 1f, 1f);

        RenderSystem.setShader(ShaderUtils.blur);
        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void pushUniforms(ShaderProgram shader, float width, float height, float radius, float blurRadius) {
        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform radiusUniform = shader.getUniform("Radius");
        GlUniform smoothnessUniform = shader.getUniform("Smoothness");
        GlUniform blurRadiusUniform = shader.getUniform("BlurRadius");
        if (sizeUniform != null) sizeUniform.set(width, height);
        if (radiusUniform != null) radiusUniform.set(radius, radius, radius, radius);
        if (smoothnessUniform != null) smoothnessUniform.set(1.0f);
        if (blurRadiusUniform != null) blurRadiusUniform.set(blurRadius);
    }
}
