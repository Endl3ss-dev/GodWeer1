package tech.lightclient.modules.impl.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ColorSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryStack;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@FunctionAdd(name = "ShaderTestFog", alias = "Shader Test Fog", category = Category.Render,
        description = "Расширенный шейдерный туман неба с несколькими режимами")
public class ShaderTestFog extends Function {

    private static ShaderTestFog instance;

    public final ModeSetting  mode         = new ModeSetting("Режим", "Caustic", "Caustic", "Drain", "Nebula", "Plasma", "Bloom", "Water");
    public final SliderSetting speed       = new SliderSetting("Скорость",      1.0f,  0.1f,   5.0f,  0.1f);
    public final SliderSetting scale       = new SliderSetting("Масштаб",       5.0f,  1.0f,  20.0f,  0.5f);
    public final SliderSetting intensity   = new SliderSetting("Интенсивность", 0.01f, 0.001f, 0.05f, 0.001f);
    public final SliderSetting alpha       = new SliderSetting("Прозрачность",  1.0f,  0.3f,   1.0f,  0.05f);
    public final ColorSetting  skyColor    = new ColorSetting("Цвет", new Color(0, 150, 255).getRGB());
    public final BooleanSetting cancelClouds = new BooleanSetting("Убирать облака", true);

    // pipelines — по одному на каждый режим, аналогично SkyShader
    private RenderPipeline causticPipeline;
    private RenderPipeline drainPipeline;
    private RenderPipeline nebulaPipeline;
    private RenderPipeline plasmaPipeline;
    private RenderPipeline bloomPipeline;
    private RenderPipeline waterPipeline;

    private GpuBuffer vertexBuffer;
    private GpuBuffer timeBuffer;
    private GpuBuffer resolutionBuffer;
    private GpuBuffer colorBuffer;
    private GpuBuffer paramsBuffer;
    private GpuBuffer cameraBuffer;

    private long startMillis = -1L;

    public ShaderTestFog() {
        instance = this;
        addSettings(mode, speed, scale, intensity, alpha, skyColor, cancelClouds);
        WorldRenderEvents.END_MAIN.register(context -> renderFog());
    }

    public static ShaderTestFog getInstance() { return instance; }

    /** Служебный модуль: скрыт из списка функций в ClickGui. */
    @Override
    public boolean isVisible() {
        return false;
    }

    public static boolean shouldCancelSky() {
        return instance != null && instance.isState();
    }

    public static boolean shouldCancelClouds() {
        return instance != null && instance.isState() && instance.cancelClouds.get();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        startMillis = -1L;
    }

    private void renderFog() {
        if (!isState() || mc.player == null || mc.world == null) return;

        ensureInitialized();
        RenderPipeline pipeline = activePipeline();
        if (pipeline == null || vertexBuffer == null) return;

        if (startMillis < 0L) startMillis = System.currentTimeMillis();

        MinecraftClient client = MinecraftClient.getInstance();
        int w = client.getWindow().getFramebufferWidth();
        int h = client.getWindow().getFramebufferHeight();
        if (w <= 0 || h <= 0) return;

        Camera cam = client.gameRenderer.getCamera();
        Color c = new Color(skyColor.get(), true);

        float time     = (System.currentTimeMillis() - startMillis) / 1000.0f;
        float yawRad   = (float) Math.toRadians(-cam.getYaw());
        float pitchRad = (float) Math.toRadians(cam.getPitch());
        float fov      = client.options.getFov().getValue().floatValue();

        GpuDevice device = RenderSystem.getDevice();
        CommandEncoder encoder = device.createCommandEncoder();
        writeUniforms(encoder, time, w, h,
                c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f,
                alpha.get(), speed.get(), scale.get(), intensity.get(),
                yawRad, pitchRad, fov);

        try (RenderPass pass = encoder.createRenderPass(
                () -> "shadertestfog",
                client.getFramebuffer().getColorAttachmentView(),
                OptionalInt.empty(),
                client.getFramebuffer().getDepthAttachmentView(),
                OptionalDouble.empty())) {
            pass.setPipeline(pipeline);
            pass.setUniform("uTime",       timeBuffer.slice());
            pass.setUniform("uResolution", resolutionBuffer.slice());
            pass.setUniform("uColor",      colorBuffer.slice());
            pass.setUniform("uParams",     paramsBuffer.slice());
            pass.setUniform("uCamera",     cameraBuffer.slice());
            pass.setVertexBuffer(0, vertexBuffer);
            pass.draw(0, 3);
        }
    }

    private RenderPipeline activePipeline() {
        switch (mode.get()) {
            case "Drain":  return drainPipeline;
            case "Nebula": return nebulaPipeline;
            case "Plasma": return plasmaPipeline;
            case "Bloom":  return bloomPipeline;
            case "Water":  return waterPipeline;
            default:       return causticPipeline; // Caustic
        }
    }

    private void ensureInitialized() {
        if (causticPipeline != null && vertexBuffer != null) return;

        GpuDevice device = RenderSystem.getDevice();

        // fullscreen triangle — точно как SkyShader
        VertexFormat format = VertexFormat.builder()
                .add("Position", VertexFormatElement.POSITION)
                .build();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer buf = stack.malloc(3 * 3 * Float.BYTES);
            buf.asFloatBuffer()
                    .put(-1f).put(-1f).put(1f)
                    .put( 3f).put(-1f).put(1f)
                    .put(-1f).put( 3f).put(1f);
            vertexBuffer = format.uploadImmediateVertexBuffer(buf);
        }

        // Шейдеры лежат по пути assets/light/shaders/core/shadertestfog_*.{vsh,fsh}
        // Аналогично SkyShader: skyshader_water / skyshader_caustic
        causticPipeline = buildPipeline("shadertestfog_caustic", "shadertestfog_caustic");
        drainPipeline   = buildPipeline("shadertestfog_drain",   "shadertestfog_drain");
        nebulaPipeline  = buildPipeline("shadertestfog_nebula",  "shadertestfog_nebula");
        plasmaPipeline  = buildPipeline("shadertestfog_plasma",  "shadertestfog_plasma");
        bloomPipeline   = buildPipeline("shadertestfog_bloom",   "shadertestfog_bloom");
        waterPipeline   = buildPipeline("shadertestfog_water",   "shadertestfog_water");

        timeBuffer       = device.createBuffer(() -> "stf_time",       GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        resolutionBuffer = device.createBuffer(() -> "stf_resolution", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        colorBuffer      = device.createBuffer(() -> "stf_color",      GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        paramsBuffer     = device.createBuffer(() -> "stf_params",     GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        cameraBuffer     = device.createBuffer(() -> "stf_camera",     GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
    }

    private static RenderPipeline buildPipeline(String location, String shaderName) {
        Identifier shader = Identifier.of("light", "core/" + shaderName);
        return RenderPipeline.builder()
                .withLocation(Identifier.of("light", location))
                .withVertexShader(shader)
                .withFragmentShader(shader)
                .withVertexFormat(VertexFormats.POSITION, VertexFormat.DrawMode.TRIANGLES)
                .withUniform("uTime",       UniformType.UNIFORM_BUFFER)
                .withUniform("uResolution", UniformType.UNIFORM_BUFFER)
                .withUniform("uColor",      UniformType.UNIFORM_BUFFER)
                .withUniform("uParams",     UniformType.UNIFORM_BUFFER)
                .withUniform("uCamera",     UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                .withCull(false)
                .build();
    }

    private void writeUniforms(CommandEncoder encoder,
                                float time, int w, int h,
                                float r, float g, float b,
                                float a, float sp, float sc, float ints,
                                float yaw, float pitch, float fov) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            encoder.writeToBuffer(timeBuffer.slice(),
                    Std140Builder.onStack(stack, 16).putFloat(time).get());
            encoder.writeToBuffer(resolutionBuffer.slice(),
                    Std140Builder.onStack(stack, 16).putVec2(w, h).get());
            encoder.writeToBuffer(colorBuffer.slice(),
                    Std140Builder.onStack(stack, 16).putVec3(r, g, b).get());
            encoder.writeToBuffer(paramsBuffer.slice(),
                    Std140Builder.onStack(stack, 16).putVec4(a, sp, sc, ints).get());
            encoder.writeToBuffer(cameraBuffer.slice(),
                    Std140Builder.onStack(stack, 16).putVec4(yaw, pitch, fov, 0f).get());
        }
    }
}
