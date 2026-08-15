package antileak.base.client.modules.impl.render;

import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.Event3DRender;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.FloatSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

/**
 * SkyShader - рендерит шейдерное небо (Water, Caustic)
 */
public class SkyShader extends Module {

    public static SkyShader INSTANCE = new SkyShader();

    private final ModeSetting mode = new ModeSetting(
            "Режим",
            "Water",
            "Water",
            "Caustic"
    );

    private final FloatSetting speed = new FloatSetting(
            "Скорость",
            1.0f,
            0.1f,
            5.0f,
            0.1f
    );

    private final FloatSetting scale = new FloatSetting(
            "Размер",
            5.0f,
            1.0f,
            20.0f,
            0.5f
    );

    private final FloatSetting intensity = new FloatSetting(
            "Интенсивность",
            0.01f,
            0.001f,
            0.05f,
            0.001f
    );

    private final FloatSetting alpha = new FloatSetting(
            "Прозрачность",
            1.0f,
            0.3f,
            1.0f,
            0.05f
    );

    private long startMillis = -1;

    public SkyShader() {
        super("SkyShader", "Шейдерное небо", ModuleCategory.RENDER);
        addSettings(mode, speed, scale, intensity, alpha);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        startMillis = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        startMillis = -1;
        super.onDisable();
    }

    @EventLink
    public void onRender3D(Event3DRender event) {
        if (!isEnable() || mc.player == null || mc.world == null) return;
        renderSkyShader(event);
    }

    private void renderSkyShader(Event3DRender event) {
        if (startMillis < 0) startMillis = System.currentTimeMillis();

        float time = (System.currentTimeMillis() - startMillis) / 1000.0f;
        float fw = mc.getWindow().getFramebufferWidth();
        float fh = mc.getWindow().getFramebufferHeight();

        // Get stable theme color (non-rainbow)
        int themeColor = getStableThemeColor();
        Color color = new Color(themeColor, true);
        float cr = color.getRed() / 255f;
        float cg = color.getGreen() / 255f;
        float cb = color.getBlue() / 255f;

        // Select shader
        Identifier shaderPath = mode.is("Caustic")
                ? Identifier.of("punch", "core/skyshader/caustic")
                : Identifier.of("punch", "core/skyshader/water");

        ShaderProgramKey key = new ShaderProgramKey(
                shaderPath,
                VertexFormats.POSITION,
                net.minecraft.client.gl.Defines.EMPTY
        );

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(key);
        if (shader == null) return;

        RenderSystem.setShader(key);

        // Set uniforms
        if (shader.getUniform("uTime") != null) shader.getUniform("uTime").set(time);
        if (shader.getUniform("uResolution") != null) shader.getUniform("uResolution").set(fw, fh);
        if (shader.getUniform("uColor") != null) shader.getUniform("uColor").set(cr, cg, cb);
        if (shader.getUniform("uAlpha") != null) shader.getUniform("uAlpha").set(alpha.get());
        if (shader.getUniform("uSpeed") != null) shader.getUniform("uSpeed").set(speed.get());
        if (shader.getUniform("uScale") != null) shader.getUniform("uScale").set(scale.get());
        if (shader.getUniform("uIntensity") != null) shader.getUniform("uIntensity").set(intensity.get());

        // Camera direction
        Camera cam = event.getCamera();
        float yawRad = (float) Math.toRadians(-cam.getYaw());
        float pitchRad = (float) Math.toRadians(cam.getPitch());
        if (shader.getUniform("uCameraDir") != null) shader.getUniform("uCameraDir").set(yawRad, pitchRad);
        if (shader.getUniform("uFov") != null) shader.getUniform("uFov").set((float) mc.options.getFov().getValue().intValue());

        // Save and set projection matrix
        Matrix4f savedProj = new Matrix4f(RenderSystem.getProjectionMatrix());
        RenderSystem.setProjectionMatrix(new Matrix4f(), com.mojang.blaze3d.systems.ProjectionType.ORTHOGRAPHIC);

        // Render setup
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_EQUAL);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        // Render fullscreen quad
        drawFullscreenQuad();

        // Restore state
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.setProjectionMatrix(savedProj, com.mojang.blaze3d.systems.ProjectionType.PERSPECTIVE);
    }

    private void drawFullscreenQuad() {
        Matrix4f identity = new Matrix4f();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        buffer.vertex(identity, -1f, -1f, 1f);
        buffer.vertex(identity, 1f, -1f, 1f);
        buffer.vertex(identity, 1f, 1f, 1f);
        buffer.vertex(identity, -1f, 1f, 1f);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    private int getStableThemeColor() {
        if (antileak.base.Punch.INSTANCE == null || antileak.base.Punch.INSTANCE.themeStorage == null || antileak.base.Punch.INSTANCE.themeStorage.getThemes() == null) {
            return antileak.base.api.utils.color.ColorUtils.getThemeColor(0);
        }
        var theme = antileak.base.Punch.INSTANCE.themeStorage.getThemes().getTheme();
        if (theme == null || theme.color == null || theme.color.length == 0) {
            return antileak.base.api.utils.color.ColorUtils.getThemeColor(0);
        }
        return theme.color[0];
    }

    public boolean shouldCancelClouds() {
        return isEnable();
    }

    public boolean shouldCancelSky() {
        return isEnable();
    }
}
