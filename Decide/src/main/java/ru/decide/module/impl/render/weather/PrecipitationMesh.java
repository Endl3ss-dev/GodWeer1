/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_243
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_638
 *  org.lwjgl.system.MemoryUtil
 */
package ru.decide.module.impl.render.weather;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2902;
import net.minecraft.class_638;
import org.lwjgl.system.MemoryUtil;
import ru.decide.module.impl.render.Weather;

@Environment(value=EnvType.CLIENT)
final class PrecipitationMesh
implements AutoCloseable {
    private static final int BUFFER_BYTES = 8000000;
    private static final int VERTEX_BYTES = 24;
    private static final long REFRESH_NS = 3000000000L;
    private static final float[][] CORNERS = new float[][]{{0.0f, 0.0f}, {0.0f, 1.0f}, {1.0f, 1.0f}, {0.0f, 0.0f}, {1.0f, 1.0f}, {1.0f, 0.0f}};
    private GpuBuffer buffer;
    private int vertexCount;
    private int originX;
    private int originZ;
    private int lastRadius;
    private long nextRefresh;
    private class_638 lastWorld;
    private Weather.Mode lastMode;
    private Weather.Quality lastQuality;
    private int lastDensity = -1;
    private int cameraY;
    private final Map<Long, int[]> cavities = new HashMap<Long, int[]>();
    private final class_2338.class_2339 probe = new class_2338.class_2339();
    private final ByteBuffer bytes = MemoryUtil.memAlloc((int)8000000).order(ByteOrder.nativeOrder());

    PrecipitationMesh() {
    }

    void update(class_638 world, class_243 camera, int radius, Weather m) {
        int density;
        Weather.Mode mode = m.mode();
        boolean motes = mode == Weather.Mode.DUST || mode == Weather.Mode.SUNRISE || mode == Weather.Mode.SUMMER || mode == Weather.Mode.ECLIPSE || mode == Weather.Mode.METEOR || mode == Weather.Mode.BLOOD_MOON;
        boolean sakura = mode == Weather.Mode.SAKURA || mode == Weather.Mode.SPRING || mode == Weather.Mode.AUTUMN;
        switch (mode) {
            case SAKURA: 
            case SPRING: 
            case AUTUMN: {
                int n = Math.round(m.sakuraDensity.getValue().floatValue());
                break;
            }
            case WINTER: {
                int n = Math.round(m.snowDensity.getValue().floatValue());
                break;
            }
            case DUST: {
                int n = Math.round(m.dustDensity.getValue().floatValue());
                break;
            }
            case SUNRISE: 
            case SUMMER: {
                int n = Math.round(m.dew.getValue().floatValue());
                break;
            }
            case ECLIPSE: {
                int n = Math.round(m.eclipseDust.getValue().floatValue());
                break;
            }
            case METEOR: {
                int n = Math.max(Math.round(m.meteorRate.getValue().floatValue()), Math.round(m.meteorDebris.getValue().floatValue()));
                break;
            }
            case BLOOD_MOON: {
                int n = Math.round(m.bloodEmbers.getValue().floatValue());
                break;
            }
            case RAIN: {
                int n = Math.round(m.rainIntensity.getValue().floatValue());
                break;
            }
            default: {
                int n = density = 0;
            }
        }
        if (mode == Weather.Mode.OFF || density == 0) {
            this.vertexCount = 0;
            this.lastDensity = -1;
            return;
        }
        if (motes) {
            radius = Math.min(radius, 64);
        }
        if (sakura) {
            radius = Math.min(radius, 80);
        }
        int ox = Math.floorDiv((int)Math.floor(camera.field_1352), 16) * 16;
        int oz = Math.floorDiv((int)Math.floor(camera.field_1350), 16) * 16;
        int camY = (int)Math.floor(camera.field_1351);
        Weather.Quality quality = m.quality();
        long now = System.nanoTime();
        if (this.buffer == null || world != this.lastWorld || mode != this.lastMode || ox != this.originX || oz != this.originZ || this.cameraY != camY || radius != this.lastRadius || this.lastQuality != quality || this.lastDensity != density || now >= this.nextRefresh) {
            this.cameraY = camY;
            this.cavities.clear();
            this.originX = ox;
            this.originZ = oz;
            this.lastRadius = radius;
            this.lastWorld = world;
            this.lastMode = mode;
            this.lastQuality = quality;
            this.lastDensity = density;
            this.nextRefresh = now + 3000000000L;
            this.bytes.clear();
            boolean snow = mode == Weather.Mode.WINTER;
            int q = quality.ordinal();
            this.addBand(this.bytes, world, mode, 0, Math.min(32, radius), 2, sakura ? 9 + q * 4 : (motes ? 10 + q * 3 : (snow ? 14 + q * 4 : 6 + q * 2)));
            if (radius > 32) {
                this.addBand(this.bytes, world, mode, 32, Math.min(96, radius), 6, sakura ? 3 + q : (motes ? 3 : (snow ? 8 + q * 2 : 4 + q)));
            }
            int farStep = Math.max(16, (int)Math.ceil((double)radius / 24.0));
            if (radius > 96) {
                this.addBand(this.bytes, world, mode, 96, radius, farStep, snow ? 6 : 4);
            }
            this.bytes.flip();
            this.vertexCount = this.bytes.remaining() / 24;
            if (this.buffer == null) {
                this.buffer = RenderSystem.getDevice().createBuffer(() -> "DecideWeather drop seeds", 40, (long)this.bytes.capacity());
            }
            if (this.vertexCount > 0) {
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(0L, (long)this.bytes.remaining()), this.bytes);
            }
        }
    }

    private void addBand(ByteBuffer out, class_638 world, Weather.Mode mode, int inner, int outer, int step, int layers) {
        class_2902.class_2903 heightmap = mode == Weather.Mode.WINTER ? class_2902.class_2903.field_13203 : class_2902.class_2903.field_13197;
        boolean roofBlocked = mode == Weather.Mode.RAIN || mode == Weather.Mode.WINTER;
        int sx = Math.floorDiv(this.originX - outer, step) * step;
        int sz = Math.floorDiv(this.originZ - outer, step) * step;
        for (int x = sx; x < this.originX + outer; x += step) {
            for (int z = sz; z < this.originZ + outer; z += step) {
                int dx = x - this.originX;
                int dz = z - this.originZ;
                int distance = dx * dx + dz * dz;
                if (distance < inner * inner || distance > outer * outer || !world.method_8393(x >> 4, z >> 4)) continue;
                for (int layer = 0; layer < layers; ++layer) {
                    boolean rainMode;
                    int bz;
                    if (out.remaining() < 144) {
                        return;
                    }
                    int hash = PrecipitationMesh.hash(x, z, layer);
                    int densitySeed = PrecipitationMesh.hash(x + 37, z - 61, layer + 19) & 0xFF;
                    if ((float)densitySeed / 255.0f > (float)this.lastDensity / 100.0f) continue;
                    float px = (float)x + (float)(hash & 0xFF) / 255.0f * (float)step;
                    float pz = (float)z + (float)(hash >>> 8 & 0xFF) / 255.0f * (float)step;
                    int ground = world.method_8624(heightmap, (int)Math.floor(px), (int)Math.floor(pz));
                    if (roofBlocked && ground > this.cameraY + 1) continue;
                    int ceiling = ground + 96;
                    if (ground > this.cameraY) {
                        int bx = (int)Math.floor(px);
                        long key = (long)bx << 32 ^ (long)(bz = (int)Math.floor(pz)) & 0xFFFFFFFFL;
                        int[] cavity = this.cavities.get(key);
                        if (cavity == null) {
                            int floor = world.method_31607();
                            int roof = ground;
                            if (!world.method_8320((class_2338)this.probe.method_10103(bx, this.cameraY, bz)).method_26215()) {
                                cavity = new int[]{this.cameraY, this.cameraY};
                            } else {
                                int y;
                                for (y = this.cameraY - 1; y >= floor; --y) {
                                    if (world.method_8320((class_2338)this.probe.method_10103(bx, y, bz)).method_26215()) continue;
                                    floor = y + 1;
                                    break;
                                }
                                for (y = this.cameraY + 1; y <= roof; ++y) {
                                    if (world.method_8320((class_2338)this.probe.method_10103(bx, y, bz)).method_26215()) continue;
                                    roof = y;
                                    break;
                                }
                                cavity = new int[]{floor, roof};
                            }
                            this.cavities.put(key, cavity);
                        }
                        if ((ceiling = cavity[1]) <= (ground = cavity[0])) continue;
                    }
                    float[][] bx = CORNERS;
                    bz = bx.length;
                    for (int i = 0; i < bz; ++i) {
                        float[] corner = bx[i];
                        out.putFloat(px - (float)this.originX).putFloat(ground).putFloat(pz - (float)this.originZ);
                        out.putFloat((float)ceiling * 2.0f + corner[0]).putFloat(corner[1]);
                        out.put((byte)(hash >>> 16)).put((byte)(hash >>> 24)).put((byte)PrecipitationMesh.hash(x + 37, z - 61, layer + 19)).put((byte)-1);
                    }
                    boolean bl = rainMode = mode == Weather.Mode.RAIN;
                    if (!rainMode || layer >= (inner == 0 ? 2 : 1) || inner >= 96) continue;
                    for (int jet = 0; jet < 3 && out.remaining() >= 144; ++jet) {
                        for (float[] corner : CORNERS) {
                            out.putFloat(px - (float)this.originX).putFloat(ground).putFloat(pz - (float)this.originZ);
                            out.putFloat((float)ceiling * 2.0f + corner[0]).putFloat(corner[1]);
                            out.put((byte)(hash >>> 16)).put((byte)(hash >>> 24)).put((byte)hash).put((byte)(jet * 32));
                        }
                    }
                }
            }
        }
    }

    private static int hash(int x, int z, int layer) {
        int n = x * 374761393 + z * 668265263 + layer * 1274126177;
        n = (n ^ n >>> 13) * 1274126177;
        return n ^ n >>> 16;
    }

    GpuBuffer buffer() {
        return this.buffer;
    }

    int vertexCount() {
        return this.vertexCount;
    }

    int originX() {
        return this.originX;
    }

    int originZ() {
        return this.originZ;
    }

    @Override
    public void close() {
        if (this.buffer != null) {
            this.buffer.close();
            this.buffer = null;
        }
        MemoryUtil.memFree((Buffer)this.bytes);
    }
}

