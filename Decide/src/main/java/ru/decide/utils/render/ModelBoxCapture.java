/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatArrayList
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10017
 *  net.minecraft.class_11658
 *  net.minecraft.class_11659
 *  net.minecraft.class_11661
 *  net.minecraft.class_11683
 *  net.minecraft.class_11788
 *  net.minecraft.class_11793
 *  net.minecraft.class_12075
 *  net.minecraft.class_12247
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1920
 *  net.minecraft.class_1921
 *  net.minecraft.class_2338
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_4618
 *  net.minecraft.class_638
 *  net.minecraft.class_742
 *  net.minecraft.class_761
 *  net.minecraft.class_7920
 *  net.minecraft.class_898
 *  net.minecraft.class_9799
 */
package ru.decide.utils.render;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10017;
import net.minecraft.class_11658;
import net.minecraft.class_11659;
import net.minecraft.class_11661;
import net.minecraft.class_11683;
import net.minecraft.class_11788;
import net.minecraft.class_11793;
import net.minecraft.class_12075;
import net.minecraft.class_12247;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1920;
import net.minecraft.class_1921;
import net.minecraft.class_2338;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_4618;
import net.minecraft.class_638;
import net.minecraft.class_742;
import net.minecraft.class_761;
import net.minecraft.class_7920;
import net.minecraft.class_898;
import net.minecraft.class_9799;
import ru.decide.mixin.RenderLayerAccessor;
import ru.decide.mixin.RenderSetupAccessor;
import ru.decide.mixin.TextureSpecAccessor;
import ru.decide.mixin.WorldRendererAccessor;

@Environment(value=EnvType.CLIENT)
public final class ModelBoxCapture {
    private static final int MAX_VERTICES = 24000;
    private static final float CORNER_EPS = 1.0E-4f;
    private static final float MIN_HALF = 0.004f;
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final class_11661 STORAGE = new class_11661();
    private static final class_4618 OUTLINE_SOURCE = new class_4618();
    private static final class_11683 MODEL_RENDERER = new class_11683();
    private static final class_11793 MODEL_PART_RENDERER = new class_11793();
    private static final Map<class_1921, class_2960> TEXTURE_CACHE = new HashMap<class_1921, class_2960>();
    private static final CapturingConsumer CONSUMER = new CapturingConsumer();
    private static final CaptureSource SOURCE = new CaptureSource(CONSUMER);
    private static float[] volumes = new float[64];

    private ModelBoxCapture() {
    }

    public static List<Box> capture(class_1309 entity, float partialTick, boolean skinOnly) {
        class_2960 class_29602;
        if (skinOnly && entity instanceof class_742) {
            class_742 player = (class_742)entity;
            class_29602 = player.method_52814().comp_1626().comp_3627();
        } else {
            class_29602 = null;
        }
        class_2960 skin = class_29602;
        int fallbackLight = ModelBoxCapture.entityLight(entity);
        List<Box> rendered = ModelBoxCapture.captureRendered(entity, partialTick, skin, fallbackLight);
        if (rendered.isEmpty() && entity instanceof class_742) {
            class_742 player = (class_742)entity;
            rendered = ModelBoxCapture.captureHumanoidFallback(player, partialTick, fallbackLight);
        }
        return skinOnly ? ModelBoxCapture.removeOverlays(rendered) : rendered;
    }

    private static List<Box> removeOverlays(List<Box> boxes) {
        int size = boxes.size();
        if (size < 2) {
            return boxes;
        }
        if (volumes.length < size) {
            volumes = new float[size];
        }
        float[] vol = volumes;
        for (int i = 0; i < size; ++i) {
            float[] half = boxes.get(i).half();
            vol[i] = half[0] * half[1] * half[2];
        }
        ArrayList<Box> kept = new ArrayList<Box>(size);
        block1: for (int i = 0; i < size; ++i) {
            Box a = boxes.get(i);
            float va = vol[i];
            for (int j = 0; j < size; ++j) {
                if (i == j) continue;
                Box b = boxes.get(j);
                float dx = a.cx - b.cx;
                float dy = a.cy - b.cy;
                float dz = a.cz - b.cz;
                if (!(dx * dx + dy * dy + dz * dz > 9.0E-4f) && va > vol[j] * 1.05f) continue block1;
            }
            kept.add(a);
        }
        return kept;
    }

    private static int entityLight(class_1309 entity) {
        class_638 world = class_310.method_1551().field_1687;
        if (world == null) {
            return 0xF000F0;
        }
        try {
            return class_761.method_23794((class_1920)world, (class_2338)class_2338.method_49637((double)entity.method_23317(), (double)(entity.method_23318() + (double)(entity.method_17682() * 0.5f)), (double)entity.method_23321()));
        }
        catch (Throwable ignored) {
            return 0xF000F0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static List<Box> captureRendered(class_1309 entity, float partialTick, class_2960 filter, int fallbackLight) {
        class_310 client = class_310.method_1551();
        class_898 dispatcher = client.method_1561();
        if (dispatcher == null) {
            return Collections.emptyList();
        }
        class_761 worldRenderer = client.field_1769;
        if (worldRenderer == null) {
            return Collections.emptyList();
        }
        class_11658 worldRenderState = ((WorldRendererAccessor)worldRenderer).decide$worldRenderState();
        if (worldRenderState == null) {
            return Collections.emptyList();
        }
        class_12075 cameraState = worldRenderState.field_63082;
        if (cameraState == null) {
            return Collections.emptyList();
        }
        ModelBoxCapture.SOURCE.filter = filter;
        CONSUMER.reset(24000, fallbackLight);
        STORAGE.method_72953();
        try {
            class_10017 state = dispatcher.method_72977((class_1297)entity, partialTick);
            if (state == null) {
                List<Box> list = Collections.emptyList();
                return list;
            }
            state.field_61821 = 0;
            state.field_61823.clear();
            dispatcher.method_72976(state, cameraState, 0.0, 0.0, 0.0, new class_4587(), (class_11659)STORAGE);
            for (class_11788 queue : STORAGE.method_73532().values()) {
                try {
                    MODEL_RENDERER.method_73001(queue, (class_4597.class_4598)SOURCE, OUTLINE_SOURCE, (class_4597.class_4598)SOURCE);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                try {
                    MODEL_PART_RENDERER.method_73543(queue, (class_4597.class_4598)SOURCE, OUTLINE_SOURCE, (class_4597.class_4598)SOURCE);
                }
                catch (Throwable throwable) {}
            }
        }
        catch (Throwable ignored) {
            List<Box> list = Collections.emptyList();
            return list;
        }
        finally {
            STORAGE.method_72953();
        }
        return ModelBoxCapture.buildBoxes(CONSUMER);
    }

    private static List<Box> captureHumanoidFallback(class_742 player, float partialTick, int fallbackLight) {
        float[][] parts;
        SkinTexturesView skin = new SkinTexturesView(player);
        class_2960 texture = skin.texturePath;
        boolean slim = skin.slim;
        float yaw = class_3532.method_17821((float)partialTick, (float)player.field_6220, (float)player.field_6283);
        double angle = Math.toRadians(180.0f - yaw);
        float cs = (float)Math.cos(angle);
        float sn = (float)Math.sin(angle);
        CapturingConsumer consumer = CONSUMER;
        consumer.reset(24000, fallbackLight);
        consumer.texture(texture);
        float armW = slim ? 3.0f : 4.0f;
        float armY = slim ? 0.5f : 0.0f;
        float armRX = slim ? -7.0f : -8.0f;
        for (float[] p : parts = new float[][]{{-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, 0.0f, 0.0f, 0.0f}, {-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, 32.0f, 0.0f, 0.5f}, {-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, 16.0f, 16.0f, 0.0f}, {-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, 16.0f, 32.0f, 0.25f}, {armRX, armY, -2.0f, armW, 12.0f, 4.0f, 40.0f, 16.0f, 0.0f}, {armRX, armY, -2.0f, armW, 12.0f, 4.0f, 40.0f, 32.0f, 0.25f}, {4.0f, armY, -2.0f, armW, 12.0f, 4.0f, 32.0f, 48.0f, 0.0f}, {4.0f, armY, -2.0f, armW, 12.0f, 4.0f, 48.0f, 48.0f, 0.25f}, {-3.9f, 12.0f, -2.0f, 4.0f, 12.0f, 4.0f, 0.0f, 16.0f, 0.0f}, {-3.9f, 12.0f, -2.0f, 4.0f, 12.0f, 4.0f, 0.0f, 32.0f, 0.25f}, {-0.1f, 12.0f, -2.0f, 4.0f, 12.0f, 4.0f, 16.0f, 48.0f, 0.0f}, {-0.1f, 12.0f, -2.0f, 4.0f, 12.0f, 4.0f, 0.0f, 48.0f, 0.25f}}) {
            ModelBoxCapture.appendSkinCube(consumer, p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], cs, sn);
        }
        return ModelBoxCapture.buildBoxes(consumer);
    }

    private static void appendSkinCube(CapturingConsumer c, float ox, float oy, float oz, float w, float h, float d, float tu, float tv, float g, float cs, float sn) {
        float x0 = ox - g;
        float y0 = oy - g;
        float z0 = oz - g;
        float x1 = ox + w + g;
        float y1 = oy + h + g;
        float z1 = oz + d + g;
        float u0 = tu;
        float u1 = u0 + d;
        float u2 = u1 + w;
        float u3 = u2 + w;
        float u4 = u2 + d;
        float u5 = u4 + w;
        float v0 = tv;
        float v1 = v0 + d;
        float v2 = v1 + h;
        float[][] v = new float[][]{{x0, y0, z0}, {x1, y0, z0}, {x1, y1, z0}, {x0, y1, z0}, {x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}, {x0, y1, z1}};
        ModelBoxCapture.skinQuad(c, v[5], v[4], v[0], v[1], u1, v0, u2, v1, cs, sn);
        ModelBoxCapture.skinQuad(c, v[2], v[3], v[7], v[6], u2, v1, u3, v0, cs, sn);
        ModelBoxCapture.skinQuad(c, v[0], v[4], v[7], v[3], u0, v1, u1, v2, cs, sn);
        ModelBoxCapture.skinQuad(c, v[1], v[0], v[3], v[2], u1, v1, u2, v2, cs, sn);
        ModelBoxCapture.skinQuad(c, v[5], v[1], v[2], v[6], u2, v1, u4, v2, cs, sn);
        ModelBoxCapture.skinQuad(c, v[4], v[5], v[6], v[7], u4, v1, u5, v2, cs, sn);
    }

    private static void skinQuad(CapturingConsumer c, float[] a, float[] b, float[] cc, float[] d, float uA, float vA, float uB, float vB, float cs, float sn) {
        ModelBoxCapture.skinVertex(c, a, uB, vA, cs, sn);
        ModelBoxCapture.skinVertex(c, b, uA, vA, cs, sn);
        ModelBoxCapture.skinVertex(c, cc, uA, vB, cs, sn);
        ModelBoxCapture.skinVertex(c, d, uB, vB, cs, sn);
    }

    private static void skinVertex(CapturingConsumer c, float[] p, float u, float v, float cs, float sn) {
        float lx = -p[0] / 16.0f;
        float ly = 1.501f - p[1] / 16.0f;
        float lz = p[2] / 16.0f;
        float wx = lx * cs + lz * sn;
        float wz = -lx * sn + lz * cs;
        c.method_22912(wx, ly, wz);
        c.method_22913(u / 64.0f, v / 64.0f);
        c.method_39415(-1);
    }

    private static List<Box> buildBoxes(CapturingConsumer c) {
        int take;
        float[] pos = c.positions.elements();
        float[] uv = c.uvs.elements();
        int[] col = c.colors.elements();
        int[] lit = c.lights.elements();
        int[] ovl = c.overlays.elements();
        int[] tex = c.textures.elements();
        List<class_2960> textureList = c.textureList;
        int quadCount = c.positions.size() / 12;
        ArrayList<Box> result = new ArrayList<Box>(quadCount);
        for (int q = 0; q < quadCount; q += take) {
            take = 1;
            if (q + 6 <= quadCount && ModelBoxCapture.sameTexture(tex, q, 6) && ModelBoxCapture.uniqueCorners(pos, q, 6) == 8) {
                take = 6;
            }
            class_2960 texture = textureList.get(tex[q * 4]);
            result.add(ModelBoxCapture.makeBox(pos, uv, col, lit, ovl, q, take, texture));
        }
        return result;
    }

    private static boolean sameTexture(int[] tex, int q, int count) {
        int first = tex[q * 4];
        for (int i = 1; i < count; ++i) {
            if (tex[(q + i) * 4] == first) continue;
            return false;
        }
        return true;
    }

    private static int uniqueCorners(float[] pos, int q, int count) {
        int n = count * 4;
        int base = q * 12;
        int unique = 0;
        for (int i = 0; i < n; ++i) {
            int io = base + i * 3;
            float ix = pos[io];
            float iy = pos[io + 1];
            float iz = pos[io + 2];
            boolean seen = false;
            for (int j = 0; j < i; ++j) {
                int jo = base + j * 3;
                if (!(Math.abs(pos[jo] - ix) < 1.0E-4f) || !(Math.abs(pos[jo + 1] - iy) < 1.0E-4f) || !(Math.abs(pos[jo + 2] - iz) < 1.0E-4f)) continue;
                seen = true;
                break;
            }
            if (!seen) {
                ++unique;
            }
            if (unique <= 8) continue;
            return unique;
        }
        return unique;
    }

    private static Box makeBox(float[] pos, float[] uv, int[] col, int[] lightList, int[] overlayList, int q, int count, class_2960 texture) {
        float dotAB;
        float bl;
        int n = count * 4;
        int base = q * 12;
        float cx = 0.0f;
        float cy = 0.0f;
        float cz = 0.0f;
        for (int i = 0; i < n; ++i) {
            cx += pos[base + i * 3];
            cy += pos[base + i * 3 + 1];
            cz += pos[base + i * 3 + 2];
        }
        cx /= (float)n;
        cy /= (float)n;
        cz /= (float)n;
        float ax = pos[base + 3] - pos[base];
        float ay = pos[base + 4] - pos[base + 1];
        float az = pos[base + 5] - pos[base + 2];
        float al = (float)Math.sqrt(ax * ax + ay * ay + az * az);
        if (al < 1.0E-6f) {
            ax = 1.0f;
            ay = 0.0f;
            az = 0.0f;
            al = 1.0f;
        }
        ax /= al;
        ay /= al;
        az /= al;
        float bx = pos[base + 9] - pos[base];
        float by = pos[base + 10] - pos[base + 1];
        float bz = pos[base + 11] - pos[base + 2];
        if ((bl = (float)Math.sqrt((bx -= ax * (dotAB = ax * bx + ay * by + az * bz)) * bx + (by -= ay * dotAB) * by + (bz -= az * dotAB) * bz)) < 1.0E-6f) {
            float hx = Math.abs(ay) < 0.9f ? 0.0f : 1.0f;
            float hy = Math.abs(ay) < 0.9f ? 1.0f : 0.0f;
            bx = ay * 0.0f - az * hy;
            bl = (float)Math.sqrt(bx * bx + (by = az * hx - ax * 0.0f) * by + (bz = ax * hy - ay * hx) * bz);
            if (bl < 1.0E-6f) {
                bx = 0.0f;
                by = 1.0f;
                bz = 0.0f;
                bl = 1.0f;
            }
        }
        float nx = ay * (bz /= bl) - az * (by /= bl);
        float ny = az * (bx /= bl) - ax * bz;
        float nz = ax * by - ay * bx;
        float ha = 0.0f;
        float hb = 0.0f;
        float hn = 0.0f;
        float[] verts = new float[n * 3];
        float[] uvs = new float[n * 2];
        for (int i = 0; i < n; ++i) {
            float lx = pos[base + i * 3] - cx;
            float ly = pos[base + i * 3 + 1] - cy;
            float lz = pos[base + i * 3 + 2] - cz;
            verts[i * 3] = lx;
            verts[i * 3 + 1] = ly;
            verts[i * 3 + 2] = lz;
            uvs[i * 2] = uv[(q * 4 + i) * 2];
            uvs[i * 2 + 1] = uv[(q * 4 + i) * 2 + 1];
            ha = Math.max(ha, Math.abs(lx * ax + ly * ay + lz * az));
            hb = Math.max(hb, Math.abs(lx * bx + ly * by + lz * bz));
            hn = Math.max(hn, Math.abs(lx * nx + ly * ny + lz * nz));
        }
        int[] colors = new int[count];
        int[] lights = new int[count];
        int[] overlays = new int[count];
        for (int i = 0; i < count; ++i) {
            colors[i] = col[(q + i) * 4];
            lights[i] = lightList[(q + i) * 4];
            overlays[i] = overlayList[(q + i) * 4];
        }
        return new Box(cx, cy, cz, new float[]{ax, ay, az, bx, by, bz, nx, ny, nz}, new float[]{Math.max(ha, 0.004f), Math.max(hb, 0.004f), Math.max(hn, 0.004f)}, verts, uvs, colors, lights, overlays, texture);
    }

    private static class_2960 textureOf(class_1921 type) {
        class_2960 cached = TEXTURE_CACHE.get(type);
        if (cached != null) {
            return cached;
        }
        if (TEXTURE_CACHE.size() > 512) {
            TEXTURE_CACHE.clear();
        }
        class_2960 resolved = ModelBoxCapture.resolveTexture(type);
        TEXTURE_CACHE.put(type, resolved);
        return resolved;
    }

    private static class_2960 resolveTexture(class_1921 type) {
        try {
            class_12247 setup = ((RenderLayerAccessor)type).decide$getRenderSetup();
            if (setup == null) {
                return null;
            }
            Map<String, Object> textures = ((RenderSetupAccessor)setup).decide$getTextures();
            if (textures == null) {
                return null;
            }
            Object spec = textures.get("Sampler0");
            if (!(spec instanceof TextureSpecAccessor)) {
                return null;
            }
            TextureSpecAccessor specAccessor = (TextureSpecAccessor)spec;
            class_2960 location = specAccessor.decide$location();
            if (location == null) {
                return null;
            }
            if (location.method_12832().contains("glint")) {
                return null;
            }
            return location;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean isFinite(float value) {
        return Float.isFinite(value) && Math.abs(value) < 512.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public record Box(float cx, float cy, float cz, float[] axes, float[] half, float[] verts, float[] uvs, int[] colors, int[] lights, int[] overlays, class_2960 texture) {
        public int quadCount() {
            return this.colors.length;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class CaptureSource
    extends class_4597.class_4598 {
        private static final class_9799 SCRATCH = new class_9799(256);
        private final CapturingConsumer consumer;
        private class_2960 filter;

        CaptureSource(CapturingConsumer consumer) {
            super(SCRATCH, (SequencedMap)Object2ObjectSortedMaps.emptyMap());
            this.consumer = consumer;
        }

        public class_4588 method_73477(class_1921 renderType) {
            class_2960 texture = ModelBoxCapture.textureOf(renderType);
            class_2960 current = this.filter;
            this.consumer.texture((class_2960)(current != null && !Objects.equals(texture, current) ? null : texture));
            return this.consumer;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class CapturingConsumer
    implements class_4588 {
        final FloatArrayList positions = new FloatArrayList();
        final FloatArrayList uvs = new FloatArrayList();
        final IntArrayList colors = new IntArrayList();
        final IntArrayList lights = new IntArrayList();
        final IntArrayList overlays = new IntArrayList();
        final IntArrayList textures = new IntArrayList();
        final List<class_2960> textureList = new ArrayList<class_2960>();
        private int maxVertices;
        private int fallbackLight = 0xF000F0;
        private int textureIndex = -1;
        private boolean accepted;

        private CapturingConsumer() {
        }

        void reset(int maxVertices, int fallbackLight) {
            this.maxVertices = maxVertices;
            this.fallbackLight = fallbackLight;
            this.positions.clear();
            this.uvs.clear();
            this.colors.clear();
            this.lights.clear();
            this.overlays.clear();
            this.textures.clear();
            this.textureList.clear();
            this.textureIndex = -1;
            this.accepted = false;
        }

        void texture(class_2960 texture) {
            if (texture == null) {
                this.textureIndex = -1;
                return;
            }
            int index = this.textureList.indexOf(texture);
            if (index < 0) {
                index = this.textureList.size();
                this.textureList.add(texture);
            }
            this.textureIndex = index;
        }

        public class_4588 method_22912(float x, float y, float z) {
            boolean bl = this.accepted = this.textureIndex >= 0 && this.textures.size() < this.maxVertices && ModelBoxCapture.isFinite(x) && ModelBoxCapture.isFinite(y) && ModelBoxCapture.isFinite(z);
            if (this.accepted) {
                this.positions.add(x);
                this.positions.add(y);
                this.positions.add(z);
                this.uvs.add(0.0f);
                this.uvs.add(0.0f);
                this.colors.add(-1);
                this.lights.add(this.fallbackLight);
                this.overlays.add(class_4608.field_21444);
                this.textures.add(this.textureIndex);
            }
            return this;
        }

        public class_4588 method_1336(int red, int green, int blue, int alpha) {
            if (this.accepted) {
                this.colors.set(this.colors.size() - 1, alpha << 24 | red << 16 | green << 8 | blue);
            }
            return this;
        }

        public class_4588 method_39415(int color) {
            if (this.accepted) {
                this.colors.set(this.colors.size() - 1, color);
            }
            return this;
        }

        public class_4588 method_22913(float u, float v) {
            if (this.accepted) {
                this.uvs.set(this.uvs.size() - 2, u);
                this.uvs.set(this.uvs.size() - 1, v);
            }
            return this;
        }

        public class_4588 method_60796(int u, int v) {
            if (this.accepted) {
                this.overlays.set(this.overlays.size() - 1, u & 0xFFFF | (v & 0xFFFF) << 16);
            }
            return this;
        }

        public class_4588 method_22921(int u, int v) {
            if (this.accepted) {
                this.lights.set(this.lights.size() - 1, u & 0xFFFF | (v & 0xFFFF) << 16);
            }
            return this;
        }

        public class_4588 method_22914(float x, float y, float z) {
            return this;
        }

        public class_4588 method_75298(float width) {
            return this;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record SkinTexturesView(class_2960 texturePath, boolean slim) {
        SkinTexturesView(class_742 player) {
            this(player.method_52814().comp_1626().comp_3627(), player.method_52814().comp_1629() == class_7920.field_41122);
        }
    }
}

