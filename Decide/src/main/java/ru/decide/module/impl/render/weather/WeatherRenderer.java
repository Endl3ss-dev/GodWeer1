/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011$class_1012
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_12137
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  net.minecraft.class_6367
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.module.impl.render.weather;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_12137;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2902;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_6367;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.Weather;
import ru.decide.module.impl.render.weather.PrecipitationMesh;

@Environment(value=EnvType.CLIENT)
public final class WeatherRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"client/Weather");
    private static final int UNIFORM_BYTES = 768;
    private static final int MAP_SIZE = 192;
    private static final int MAP_RADIUS = 96;
    private static final int MAP_BYTES = 147456;
    private static final long MAP_REFRESH_NS = 2000000000L;
    private static final WeatherRenderer INSTANCE = new WeatherRenderer();
    private static RenderPipeline materialsPipeline;
    private static RenderPipeline precipitationPipeline;
    private static boolean initFailed;
    private class_6367 scene;
    private class_6367 terrain;
    private GpuBuffer uniforms;
    private GpuBuffer triangle;
    private GpuTexture topMap;
    private GpuTextureView topMapView;
    private final ByteBuffer topMapBytes = MemoryUtil.memAlloc((int)147456).order(ByteOrder.nativeOrder());
    private int topMapOriginX = Integer.MIN_VALUE;
    private int topMapOriginZ = Integer.MIN_VALUE;
    private long nextTopMap;
    private final PrecipitationMesh mesh = new PrecipitationMesh();
    private final ByteBuffer data = MemoryUtil.memAlloc((int)768).order(ByteOrder.nativeOrder());
    private final Matrix4f inverseProjection = new Matrix4f();
    private final Matrix4f inverseView = new Matrix4f();
    private final long start = System.nanoTime();
    private static final Matrix4f frameView;
    private static final Matrix4f frameProjection;
    private static boolean frameReady;
    private boolean captured;
    private boolean closed;

    private WeatherRenderer() {
    }

    public static WeatherRenderer getInstance() {
        return INSTANCE;
    }

    public static void captureFrame(Matrix4f view, Matrix4f projection) {
        if (view == null || projection == null) {
            frameReady = false;
            return;
        }
        frameView.set((Matrix4fc)view);
        frameProjection.set((Matrix4fc)projection);
        frameReady = true;
    }

    private static boolean active(Weather m) {
        return m.isEnabled() && (m.mode() != Weather.Mode.OFF || m.wetGround.getValue() != false);
    }

    private static boolean materialsNeeded(Weather m) {
        if (!m.isEnabled()) {
            return false;
        }
        return switch (m.mode()) {
            case Weather.Mode.OFF -> m.wetGround.getValue();
            case Weather.Mode.RAIN, Weather.Mode.WINTER, Weather.Mode.SUNRISE, Weather.Mode.DUST, Weather.Mode.SPRING, Weather.Mode.SUMMER, Weather.Mode.AUTUMN -> true;
            case Weather.Mode.SAKURA -> {
                if (m.sakuraTint.getValue().booleanValue() && m.sakuraTintStrength.getValue().floatValue() > 0.0f) {
                    yield true;
                }
                yield false;
            }
            case Weather.Mode.ECLIPSE, Weather.Mode.METEOR, Weather.Mode.BLOOD_MOON -> true;
            default -> true;
        };
    }

    private static float f(SliderSetting s) {
        return s.getValue().floatValue() / 100.0f;
    }

    private static float shaderMode(Weather.Mode mode) {
        return switch (mode) {
            case Weather.Mode.SUMMER -> 3.0f;
            default -> mode.ordinal();
        };
    }

    private static boolean init() {
        if (initFailed) {
            return false;
        }
        if (materialsPipeline != null && precipitationPipeline != null) {
            return true;
        }
        try {
            materialsPipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/weather/materials")).withVertexShader(class_2960.method_60655((String)"decide", (String)"weather/fullscreen")).withFragmentShader(class_2960.method_60655((String)"decide", (String)"weather/materials")).withSampler("SceneColor").withSampler("SceneDepth").withSampler("TerrainDepth").withSampler("TopMap").withUniform("Weather", class_10789.field_60031).withVertexFormat(class_290.field_1592, VertexFormat.class_5596.field_27379).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
            precipitationPipeline = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/weather/precipitation")).withVertexShader(class_2960.method_60655((String)"decide", (String)"weather/precipitation")).withFragmentShader(class_2960.method_60655((String)"decide", (String)"weather/precipitation")).withUniform("Weather", class_10789.field_60031).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27379).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withBlend(BlendFunction.TRANSLUCENT).withDepthWrite(false).withCull(false).build());
            return true;
        }
        catch (Throwable t) {
            initFailed = true;
            LOGGER.error("[Weather] pipeline init failed, effect disabled", t);
            materialsPipeline = null;
            precipitationPipeline = null;
            return false;
        }
    }

    private void ensureTargets(class_276 target) {
        GpuDevice device;
        if (this.scene == null) {
            this.scene = new class_6367("DecideWeather scene", target.field_1482, target.field_1481, true);
            this.terrain = new class_6367("DecideWeather terrain", target.field_1482, target.field_1481, true);
        } else if (this.scene.field_1482 != target.field_1482 || this.scene.field_1481 != target.field_1481) {
            this.scene.method_1234(target.field_1482, target.field_1481);
            this.terrain.method_1234(target.field_1482, target.field_1481);
        }
        if (this.uniforms == null || this.uniforms.isClosed()) {
            device = RenderSystem.getDevice();
            this.uniforms = device.createBuffer(() -> "DecideWeather uniforms", 136, 768L);
            ByteBuffer vertices = MemoryUtil.memAlloc((int)36).order(ByteOrder.nativeOrder());
            vertices.putFloat(-1.0f).putFloat(-1.0f).putFloat(0.0f);
            vertices.putFloat(3.0f).putFloat(-1.0f).putFloat(0.0f);
            vertices.putFloat(-1.0f).putFloat(3.0f).putFloat(0.0f).flip();
            this.triangle = device.createBuffer(() -> "DecideWeather fullscreen triangle", 32, vertices);
            MemoryUtil.memFree((Buffer)vertices);
        }
        if (this.topMap == null || this.topMap.isClosed()) {
            device = RenderSystem.getDevice();
            this.topMap = device.createTexture(() -> "DecideWeather top map", 5, TextureFormat.RGBA8, 192, 192, 1, 1);
            this.topMapView = device.createTextureView(this.topMap);
            for (int i = 0; i < 147456; ++i) {
                this.topMapBytes.put(i, (byte)0);
            }
            this.topMapBytes.clear();
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(this.topMap, this.topMapBytes, class_1011.class_1012.field_4997, 0, 0, 0, 0, 192, 192);
            this.topMapOriginX = Integer.MIN_VALUE;
            this.topMapOriginZ = Integer.MIN_VALUE;
            this.nextTopMap = 0L;
        }
    }

    private void updateTopMap(class_310 client, int ox, int oz) {
        long now = System.nanoTime();
        if (this.topMap == null || ox == this.topMapOriginX && oz == this.topMapOriginZ && now < this.nextTopMap) {
            return;
        }
        this.topMapOriginX = ox;
        this.topMapOriginZ = oz;
        this.nextTopMap = now + 2000000000L;
        int baseX = ox - 96;
        int baseZ = oz - 96;
        this.topMapBytes.clear();
        for (int z = 0; z < 192; ++z) {
            for (int x = 0; x < 192; ++x) {
                int bx = baseX + x;
                int bz = baseZ + z;
                boolean loaded = client.field_1687.method_8393(bx >> 4, bz >> 4);
                int packed = 0;
                int valid = 0;
                if (loaded) {
                    int top = client.field_1687.method_8624(class_2902.class_2903.field_13197, bx, bz) + 512;
                    if (top < 0) {
                        top = 0;
                    } else if (top > 65535) {
                        top = 65535;
                    }
                    packed = top;
                    valid = 255;
                }
                this.topMapBytes.put((byte)(packed & 0xFF)).put((byte)(packed >> 8 & 0xFF)).put((byte)valid).put((byte)-1);
            }
        }
        this.topMapBytes.flip();
        RenderSystem.getDevice().createCommandEncoder().writeToTexture(this.topMap, this.topMapBytes, class_1011.class_1012.field_4997, 0, 0, 0, 0, 192, 192);
    }

    public void captureTerrain(Weather m) {
        this.captured = false;
        if (this.closed || m == null || !WeatherRenderer.active(m)) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client == null) {
            return;
        }
        class_276 target = client.method_1522();
        if (target == null || target.method_30278() == null) {
            return;
        }
        if (!WeatherRenderer.init()) {
            return;
        }
        this.ensureTargets(target);
        if (WeatherRenderer.materialsNeeded(m)) {
            this.terrain.method_29329(target);
        }
        this.captured = true;
    }

    public void render(Weather m) {
        RenderPass pass;
        Matrix4f positionMatrix;
        Matrix4f projectionMatrix;
        boolean needTopMap;
        if (this.closed || m == null || !WeatherRenderer.active(m) || !this.captured) {
            return;
        }
        this.captured = false;
        class_310 client = class_310.method_1551();
        if (client == null || client.field_1687 == null || client.field_1773 == null) {
            return;
        }
        class_276 target = client.method_1522();
        if (target == null) {
            return;
        }
        class_4184 camera = client.field_1773.method_19418();
        if (camera == null) {
            return;
        }
        if (!WeatherRenderer.init()) {
            return;
        }
        this.ensureTargets(target);
        Weather.Mode mode = m.mode();
        boolean spring = mode == Weather.Mode.SPRING;
        boolean summer = mode == Weather.Mode.SUMMER;
        boolean autumn = mode == Weather.Mode.AUTUMN;
        class_243 cameraPos = camera.method_71156();
        int radius = client.field_1690.method_38521() * 16;
        this.mesh.update(client.field_1687, cameraPos, radius, m);
        boolean bl = needTopMap = mode == Weather.Mode.WINTER && m.snowCover.getValue() != false || mode == Weather.Mode.RAIN && (m.wetGround.getValue() != false || m.puddles.getValue().floatValue() > 0.0f);
        if (needTopMap) {
            this.updateTopMap(client, this.mesh.originX(), this.mesh.originZ());
        }
        this.data.clear();
        if (frameReady) {
            projectionMatrix = new Matrix4f((Matrix4fc)frameProjection);
            positionMatrix = new Matrix4f((Matrix4fc)frameView);
        } else {
            float fov = ((Integer)client.field_1690.method_41808().method_41753()).intValue();
            projectionMatrix = client.field_1773.method_22973(fov);
            Quaternionf rotation = camera.method_23767().conjugate(new Quaternionf());
            positionMatrix = new Matrix4f().rotation((Quaternionfc)rotation);
        }
        projectionMatrix.get(0, this.data);
        this.inverseProjection.set((Matrix4fc)projectionMatrix).invert().get(64, this.data);
        positionMatrix.get(128, this.data);
        this.inverseView.set((Matrix4fc)positionMatrix).invert().get(192, this.data);
        this.data.position(256);
        this.data.putFloat((float)cameraPos.field_1352).putFloat((float)cameraPos.field_1351).putFloat((float)cameraPos.field_1350).putFloat((float)(System.nanoTime() - this.start) / 1.0E9f);
        this.data.putFloat(mode == Weather.Mode.RAIN ? WeatherRenderer.f(m.rainIntensity) : 0.0f).putFloat(mode == Weather.Mode.WINTER && m.snowCover.getValue() != false ? 1.0f : 0.0f).putFloat((mode == Weather.Mode.OFF || mode == Weather.Mode.RAIN) && m.wetGround.getValue() != false ? 1.0f : 0.0f).putFloat(WeatherRenderer.f(m.puddles));
        this.data.putFloat(target.field_1482).putFloat(target.field_1481).putFloat(m.splashes.getValue() != false ? 1.0f : 0.0f).putFloat(WeatherRenderer.f(m.wind));
        long timeOfDay = client.field_1687.method_8532();
        float sunAngle = (float)(timeOfDay % 24000L) / 24000.0f;
        float daylight = (float)Math.max(0.08, Math.min(1.0, Math.cos((double)sunAngle * Math.PI * 2.0) * 1.5 + 0.25));
        this.data.putFloat(daylight).putFloat(radius).putFloat(WeatherRenderer.f(m.snowDensity)).putFloat(WeatherRenderer.shaderMode(mode));
        this.data.putFloat(this.mesh.originX()).putFloat(0.0f).putFloat(this.mesh.originZ()).putFloat(0.0f);
        this.data.putFloat(m.quality().ordinal()).putFloat(mode == Weather.Mode.WINTER ? WeatherRenderer.f(m.blizzard) : 0.0f).putFloat(m.splashes.getValue() != false ? WeatherRenderer.f(m.splashDensity) : 0.0f).putFloat(WeatherRenderer.f(m.ringVisibility));
        this.data.putFloat(summer ? 0.95f : WeatherRenderer.f(m.sunlight)).putFloat(summer ? 0.8f : WeatherRenderer.f(m.sunRays)).putFloat(summer ? 0.55f : WeatherRenderer.f(m.dew)).putFloat(summer ? 0.38f : WeatherRenderer.f(m.dustDensity));
        this.data.putFloat(summer ? 0.9f : WeatherRenderer.f(m.dustLight)).putFloat(mode == Weather.Mode.WINTER && m.snowCaves.getValue() != false ? 1.0f : 0.0f).putFloat(summer ? 0.62f : WeatherRenderer.f(m.sunHeight)).putFloat(summer ? 0.28f : WeatherRenderer.f(m.dustSize));
        this.data.putFloat(WeatherRenderer.f(m.sakuraDensity)).putFloat(WeatherRenderer.f(m.sakuraSize)).putFloat(m.sakuraGroundLife.getValue().floatValue() * 0.3f).putFloat(spring ? 0.65f : (autumn ? 0.8f : WeatherRenderer.f(m.sakuraWind)));
        this.data.putFloat(spring ? 0.93f : (autumn ? 0.06f : WeatherRenderer.f(m.sakuraHue))).putFloat(spring ? 0.52f : (autumn ? 0.82f : WeatherRenderer.f(m.sakuraSaturation))).putFloat(spring || autumn ? 0.34f : (m.sakuraTint.getValue() != false ? WeatherRenderer.f(m.sakuraTintStrength) : 0.0f)).putFloat(spring ? 0.68f : (autumn ? 0.78f : WeatherRenderer.f(m.sakuraFlutter)));
        this.data.putFloat(WeatherRenderer.f(m.eclipseDarkness)).putFloat(WeatherRenderer.f(m.eclipseCorona)).putFloat(WeatherRenderer.f(m.eclipseStars)).putFloat(WeatherRenderer.f(m.eclipseDust));
        this.data.putFloat(WeatherRenderer.f(m.meteorRate)).putFloat(WeatherRenderer.f(m.meteorBrightness)).putFloat(WeatherRenderer.f(m.meteorTrail)).putFloat(WeatherRenderer.f(m.meteorStars));
        this.data.putFloat(WeatherRenderer.f(m.meteorDebris)).putFloat(WeatherRenderer.f(m.meteorSize)).putFloat(WeatherRenderer.f(m.meteorVariation)).putFloat(WeatherRenderer.f(m.meteorProximity));
        this.data.putFloat(WeatherRenderer.f(m.bloodMoonSize)).putFloat(WeatherRenderer.f(m.bloodMoonGlow)).putFloat(WeatherRenderer.f(m.bloodSky)).putFloat(WeatherRenderer.f(m.bloodClouds));
        this.data.putFloat(WeatherRenderer.f(m.bloodMist)).putFloat(WeatherRenderer.f(m.bloodGround)).putFloat(WeatherRenderer.f(m.bloodEmbers)).putFloat(WeatherRenderer.f(m.bloodHeight));
        this.data.putFloat(WeatherRenderer.f(m.meteorFog)).putFloat(WeatherRenderer.f(m.meteorFogDistance)).putFloat(WeatherRenderer.f(m.meteorSkyGlow)).putFloat(WeatherRenderer.f(m.meteorNebula));
        this.data.putFloat(0.0f).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
        this.data.putFloat(0.0f).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f);
        this.data.putFloat(mode == Weather.Mode.WINTER && m.snowCover.getValue() != false && m.snowAllSurfaces.getValue() != false ? 1.0f : 0.0f).putFloat(mode == Weather.Mode.AUTUMN && m.autumnWood.getValue() != false ? 1.0f : 0.0f).putFloat(mode == Weather.Mode.AUTUMN && m.autumnLeaves.getValue() != false ? 1.0f : 0.0f).putFloat(mode == Weather.Mode.WINTER && m.frostWood.getValue() != false ? 1.0f : 0.0f).putFloat(mode == Weather.Mode.WINTER && m.snowfall.getValue() != false ? 1.0f : 0.0f).putFloat(mode == Weather.Mode.WINTER && m.snowfall.getValue() != false ? WeatherRenderer.f(m.snowfallDepth) : 0.0f).putFloat(mode == Weather.Mode.WINTER && m.snowfall.getValue() != false ? WeatherRenderer.f(m.snowfallDrift) : 0.0f).putFloat(mode == Weather.Mode.WINTER && m.snowfall.getValue() != false && m.snowfallCrunch.getValue() != false ? 1.0f : 0.0f).putFloat(mode == Weather.Mode.WINTER && m.snowfall.getValue() != false ? WeatherRenderer.f(m.snowfallFootprintDepth) : 0.0f).putFloat(0.0f).putFloat(0.0f).putFloat(0.0f).flip();
        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.writeToBuffer(this.uniforms.slice(), this.data);
        if (WeatherRenderer.materialsNeeded(m) && this.scene != null) {
            encoder.copyTextureToTexture(target.method_30277(), this.scene.method_30277(), 0, 0, 0, 0, 0, target.field_1482, target.field_1481);
            this.scene.method_29329(target);
            pass = encoder.createRenderPass(() -> "DecideWeather materials", target.method_71639(), OptionalInt.empty());
            try {
                pass.setPipeline(materialsPipeline);
                pass.setUniform("Weather", this.uniforms);
                class_12137 sampler = RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST);
                pass.bindTexture("SceneColor", this.scene.method_71639(), sampler);
                pass.bindTexture("SceneDepth", this.scene.method_71640(), sampler);
                pass.bindTexture("TerrainDepth", this.terrain.method_71640(), sampler);
                pass.bindTexture("TopMap", this.topMapView, sampler);
                pass.setVertexBuffer(0, this.triangle);
                pass.draw(0, 3);
            }
            finally {
                if (pass != null) {
                    pass.close();
                }
            }
        }
        if (this.mesh.buffer() != null && this.mesh.vertexCount() > 0 && mode != Weather.Mode.OFF) {
            pass = encoder.createRenderPass(() -> "DecideWeather precipitation", target.method_71639(), OptionalInt.empty(), target.method_71640(), OptionalDouble.empty());
            try {
                pass.setPipeline(precipitationPipeline);
                pass.setUniform("Weather", this.uniforms);
                pass.setVertexBuffer(0, this.mesh.buffer());
                pass.draw(0, this.mesh.vertexCount());
            }
            finally {
                if (pass != null) {
                    pass.close();
                }
            }
        }
    }

    public void close() {
        this.closed = true;
        this.mesh.close();
        if (this.uniforms != null) {
            this.uniforms.close();
            this.uniforms = null;
        }
        if (this.triangle != null) {
            this.triangle.close();
            this.triangle = null;
        }
        if (this.scene != null) {
            this.scene.method_1238();
            this.scene = null;
        }
        if (this.terrain != null) {
            this.terrain.method_1238();
            this.terrain = null;
        }
        if (this.topMap != null) {
            this.topMap.close();
            this.topMap = null;
            this.topMapView = null;
        }
        MemoryUtil.memFree((Buffer)this.data);
        MemoryUtil.memFree((Buffer)this.topMapBytes);
    }

    static {
        frameView = new Matrix4f();
        frameProjection = new Matrix4f();
    }
}

