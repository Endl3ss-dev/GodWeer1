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
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryStack;
import tech.lightclient.client.ClientColors;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@FunctionAdd(name = "SkyShader", alias = "Sky Shader", category = Category.Render, description = "\u0428\u0435\u0439\u0434\u0435\u0440\u043d\u044b\u0439 \u0444\u043e\u043d \u043d\u0435\u0431\u0430")
public class SkyShader extends Function {

    private static SkyShader instance;

    private final ModeSetting mode = new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "Water", "Water", "Caustic", "Thunder", "Pulsar", "Black Hole", "Galaxy");
    private final SliderSetting speed = new SliderSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 1.0f, 0.1f, 5.0f, 0.1f);
    private final SliderSetting scale = new SliderSetting("\u0420\u0430\u0437\u043c\u0435\u0440", 5.0f, 1.0f, 20.0f, 0.5f);
    private final SliderSetting intensity = new SliderSetting("\u0418\u043d\u0442\u0435\u043d\u0441\u0438\u0432\u043d\u043e\u0441\u0442\u044c", 0.01f, 0.001f, 0.05f, 0.001f);
    private final SliderSetting alpha = new SliderSetting("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 1.0f, 0.3f, 1.0f, 0.05f);
    private final SliderSetting thunderInterval = new SliderSetting("\u0418\u043d\u0442\u0435\u0440\u0432\u0430\u043b \u0433\u0440\u043e\u0437\u044b", 4.0f, 1.0f, 10.0f, 0.5f)
            .setVisible(() -> mode.is("Thunder"));
    private final SliderSetting thunderChance = new SliderSetting("\u0428\u0430\u043d\u0441 \u043c\u043e\u043b\u043d\u0438\u0438", 0.65f, 0.0f, 1.0f, 0.05f)
            .setVisible(() -> mode.is("Thunder"));
    private final SliderSetting thunderGlow = new SliderSetting("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u043c\u043e\u043b\u043d\u0438\u0438", 1.0f, 0.1f, 3.0f, 0.1f)
            .setVisible(() -> mode.is("Thunder"));
    private final BooleanSetting cancelClouds = new BooleanSetting("\u0423\u0431\u0438\u0440\u0430\u0442\u044c \u043e\u0431\u043b\u0430\u043a\u0430", true);

    private RenderPipeline waterPipeline;
    private RenderPipeline causticPipeline;
    private RenderPipeline thunderPipeline;
    private RenderPipeline pulsarPipeline;
    private RenderPipeline blackHolePipeline;
    private RenderPipeline galaxyPipeline;
    private GpuBuffer vertexBuffer;
    private GpuBuffer timeBuffer;
    private GpuBuffer resolutionBuffer;
    private GpuBuffer colorBuffer;
    private GpuBuffer color2Buffer;
    private GpuBuffer paramsBuffer;
    private GpuBuffer cameraBuffer;
    private GpuBuffer thunderBuffer;
    private long startMillis = -1L;

    public SkyShader() {
        instance = this;
        addSettings(mode, speed, scale, intensity, alpha, thunderInterval, thunderChance, thunderGlow, cancelClouds);
        WorldRenderEvents.START_MAIN.register(context -> renderSkyShader());
    }

    public static SkyShader getInstance() {
        return instance;
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

    private void renderSkyShader() {
        if (!isState() || mc.player == null || mc.world == null) {
            return;
        }
        ensureInitialized();
        if (waterPipeline == null || causticPipeline == null || thunderPipeline == null || pulsarPipeline == null
                || blackHolePipeline == null || galaxyPipeline == null || vertexBuffer == null) {
            return;
        }
        if (startMillis < 0L) {
            startMillis = System.currentTimeMillis();
        }

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getFramebufferWidth();
        int height = client.getWindow().getFramebufferHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        Camera camera = client.gameRenderer.getCamera();
        Color color = new Color(ClientColors.ICON.getRGB(), true);
        Color color2 = new Color(ClientColors.GRADIENT_END.getRGB(), true);
        float time = (System.currentTimeMillis() - startMillis) / 1000.0f;
        float yawRad = (float) Math.toRadians(-camera.getYaw());
        float pitchRad = (float) Math.toRadians(camera.getPitch());
        float fov = client.options.getFov().getValue().floatValue();

        GpuDevice device = RenderSystem.getDevice();
        CommandEncoder encoder = device.createCommandEncoder();
        writeUniforms(
                encoder,
                time,
                width,
                height,
                color.getRed() / 255f,
                color.getGreen() / 255f,
                color.getBlue() / 255f,
                color2.getRed() / 255f,
                color2.getGreen() / 255f,
                color2.getBlue() / 255f,
                alpha.get(),
                speed.get(),
                scale.get(),
                intensity.get(),
                thunderInterval.get(),
                thunderChance.get(),
                thunderGlow.get(),
                yawRad,
                pitchRad,
                fov
        );

        try (RenderPass pass = encoder.createRenderPass(
                () -> "skyshader",
                client.getFramebuffer().getColorAttachmentView(),
                OptionalInt.empty(),
                client.getFramebuffer().getDepthAttachmentView(),
                OptionalDouble.empty())) {
            pass.setPipeline(getActivePipeline());
            pass.setUniform("uTime", timeBuffer.slice());
            pass.setUniform("uResolution", resolutionBuffer.slice());
            pass.setUniform("uColor", colorBuffer.slice());
            if (mode.is("Black Hole") || mode.is("Galaxy")) {
                pass.setUniform("uColor2", color2Buffer.slice());
            }
            pass.setUniform("uParams", paramsBuffer.slice());
            pass.setUniform("uCamera", cameraBuffer.slice());
            if (mode.is("Thunder")) {
                pass.setUniform("uThunder", thunderBuffer.slice());
            }
            pass.setVertexBuffer(0, vertexBuffer);
            pass.draw(0, 3);
        }
    }

    private RenderPipeline getActivePipeline() {
        if (mode.is("Thunder")) {
            return thunderPipeline;
        }
        if (mode.is("Pulsar")) {
            return pulsarPipeline;
        }
        if (mode.is("Caustic")) {
            return causticPipeline;
        }
        if (mode.is("Black Hole")) {
            return blackHolePipeline;
        }
        if (mode.is("Galaxy")) {
            return galaxyPipeline;
        }
        return waterPipeline;
    }

    private void ensureInitialized() {
        if (waterPipeline != null && causticPipeline != null && thunderPipeline != null && pulsarPipeline != null
                && blackHolePipeline != null && galaxyPipeline != null && vertexBuffer != null) {
            return;
        }

        GpuDevice device = RenderSystem.getDevice();
        VertexFormat format = VertexFormat.builder()
                .add("Position", VertexFormatElement.POSITION)
                .build();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer buffer = stack.malloc(3 * 3 * Float.BYTES);
            buffer.asFloatBuffer()
                    .put(-1f).put(-1f).put(1f)
                    .put(3f).put(-1f).put(1f)
                    .put(-1f).put(3f).put(1f);
            vertexBuffer = format.uploadImmediateVertexBuffer(buffer);
        }

        waterPipeline = buildPipeline("skyshader_water", "skyshader_water");
        causticPipeline = buildPipeline("skyshader_caustic", "skyshader_caustic");
        thunderPipeline = buildThunderPipeline("skyshader_thunder", "skyshader_thunder");
        pulsarPipeline = buildPipeline("skyshader_pulsar", "skyshader_pulsar");
        blackHolePipeline = buildColorPairPipeline("skyshader_blackhole", "skyshader_blackhole");
        galaxyPipeline = buildColorPairPipeline("skyshader_galaxy", "skyshader_galaxy");
        timeBuffer = device.createBuffer(() -> "skyshader_time", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        resolutionBuffer = device.createBuffer(() -> "skyshader_resolution", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        colorBuffer = device.createBuffer(() -> "skyshader_color", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        color2Buffer = device.createBuffer(() -> "skyshader_color2", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        paramsBuffer = device.createBuffer(() -> "skyshader_params", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        cameraBuffer = device.createBuffer(() -> "skyshader_camera", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
        thunderBuffer = device.createBuffer(() -> "skyshader_thunder", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_COPY_DST, 16L);
    }

    private static RenderPipeline buildPipeline(String location, String shaderName) {
        Identifier shader = Identifier.of("light", "core/" + shaderName);
        return RenderPipeline.builder()
                .withLocation(Identifier.of("light", location))
                .withVertexShader(shader)
                .withFragmentShader(shader)
                .withVertexFormat(VertexFormats.POSITION, VertexFormat.DrawMode.TRIANGLES)
                .withUniform("uTime", UniformType.UNIFORM_BUFFER)
                .withUniform("uResolution", UniformType.UNIFORM_BUFFER)
                .withUniform("uColor", UniformType.UNIFORM_BUFFER)
                .withUniform("uParams", UniformType.UNIFORM_BUFFER)
                .withUniform("uCamera", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                .withCull(false)
                .build();
    }

    private static RenderPipeline buildThunderPipeline(String location, String shaderName) {
        Identifier shader = Identifier.of("light", "core/" + shaderName);
        return RenderPipeline.builder()
                .withLocation(Identifier.of("light", location))
                .withVertexShader(shader)
                .withFragmentShader(shader)
                .withVertexFormat(VertexFormats.POSITION, VertexFormat.DrawMode.TRIANGLES)
                .withUniform("uTime", UniformType.UNIFORM_BUFFER)
                .withUniform("uResolution", UniformType.UNIFORM_BUFFER)
                .withUniform("uColor", UniformType.UNIFORM_BUFFER)
                .withUniform("uParams", UniformType.UNIFORM_BUFFER)
                .withUniform("uCamera", UniformType.UNIFORM_BUFFER)
                .withUniform("uThunder", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                .withCull(false)
                .build();
    }

    private static RenderPipeline buildColorPairPipeline(String location, String shaderName) {
        Identifier shader = Identifier.of("light", "core/" + shaderName);
        return RenderPipeline.builder()
                .withLocation(Identifier.of("light", location))
                .withVertexShader(shader)
                .withFragmentShader(shader)
                .withVertexFormat(VertexFormats.POSITION, VertexFormat.DrawMode.TRIANGLES)
                .withUniform("uTime", UniformType.UNIFORM_BUFFER)
                .withUniform("uResolution", UniformType.UNIFORM_BUFFER)
                .withUniform("uColor", UniformType.UNIFORM_BUFFER)
                .withUniform("uColor2", UniformType.UNIFORM_BUFFER)
                .withUniform("uParams", UniformType.UNIFORM_BUFFER)
                .withUniform("uCamera", UniformType.UNIFORM_BUFFER)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(false)
                .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                .withCull(false)
                .build();
    }

    private void writeUniforms(CommandEncoder encoder, float time, int width, int height,
                               float r, float g, float b, float r2, float g2, float b2, float alpha,
                               float speed, float scale, float intensity,
                               float thunderInterval, float thunderChance, float thunderGlow,
                               float yawRad, float pitchRad, float fov) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            encoder.writeToBuffer(timeBuffer.slice(), Std140Builder.onStack(stack, 16).putFloat(time).get());
            encoder.writeToBuffer(resolutionBuffer.slice(), Std140Builder.onStack(stack, 16).putVec2(width, height).get());
            encoder.writeToBuffer(colorBuffer.slice(), Std140Builder.onStack(stack, 16).putVec3(r, g, b).get());
            encoder.writeToBuffer(color2Buffer.slice(), Std140Builder.onStack(stack, 16).putVec3(r2, g2, b2).get());
            encoder.writeToBuffer(paramsBuffer.slice(), Std140Builder.onStack(stack, 16).putVec4(alpha, speed, scale, intensity).get());
            encoder.writeToBuffer(cameraBuffer.slice(), Std140Builder.onStack(stack, 16).putVec4(yawRad, pitchRad, fov, 0f).get());
            encoder.writeToBuffer(thunderBuffer.slice(), Std140Builder.onStack(stack, 16).putVec4(thunderInterval, thunderChance, thunderGlow, 0f).get());
        }
    }
}
