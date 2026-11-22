/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.render.post;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.render.voronoi.VoronoiOfQuad;

@Environment(value=EnvType.CLIENT)
public final class GuiShatterAnimation {
    public static final int MAX_SHARDS = 28;
    public static final int VERTEX_BYTES = 24;
    private static final int MAX_POLY_VERTICES = 24;
    public static final int MAX_VERTICES = 1848;
    private static final int MIN_COUNT = 22;
    private static final int MAX_COUNT = 28;
    private static final float RELAX_STRENGTH = 0.45f;
    private static final float SEED_JITTER = 0.32f;
    private static final float MIN_DISTANCE = 0.68f;
    private static final float EXPAND = 0.34f;
    private static final float SPREAD = 0.028f;
    private static final float MARGIN = 0.004f;
    private static final float CURL_DEGREES = 26.0f;
    private static final float CURL_DEPTH = 0.1f;
    private static final float SCREEN_PERSPECTIVE = 0.9f;
    private static final long GATHER_NS = 360000000L;
    private static final float MAX_BLUR_RADIUS = 9.0f;
    private static final Random RANDOM = new Random();
    private static final State LOCAL = new State();

    private GuiShatterAnimation() {
    }

    public static void begin(float x, float y, float w, float h, float pad, float sw, float sh, long seed) {
        LOCAL.begin(x, y, w, h, pad, sw, sh, seed);
    }

    public static void begin(float x, float y, float w, float h, float pad, float sw, float sh) {
        LOCAL.begin(x, y, w, h, pad, sw, sh, RANDOM.nextLong());
    }

    public static void cancel() {
        LOCAL.cancel();
    }

    public static State create() {
        return new State();
    }

    public static boolean isActive() {
        return LOCAL.isActive();
    }

    public static long seed() {
        return LOCAL.seed();
    }

    public static State local() {
        return LOCAL;
    }

    public static void gather(long ns) {
        LOCAL.gather(ns);
    }

    public static float clamp01(float f) {
        return Math.max(0.0f, Math.min(1.0f, f));
    }

    public static float blurRadius() {
        return LOCAL.blurRadius();
    }

    public static float[] beginRect() {
        return LOCAL.beginRect();
    }

    public static boolean resume() {
        return LOCAL.resume();
    }

    public static float progress(float f) {
        return LOCAL.progress(f);
    }

    public static int buildGeometry(ByteBuffer buf, float p, float sw, float sh, boolean flag) {
        return LOCAL.buildGeometry(buf, p, sw, sh, flag, null);
    }

    public static int buildGeometry(ByteBuffer buf, float p, float sw, float sh, boolean flag, Remap remap) {
        return LOCAL.buildGeometry(buf, p, sw, sh, flag, remap);
    }

    public static float clampSigned(float f) {
        return Math.max(-1.0f, Math.min(1.0f, f));
    }

    public static boolean isGathering() {
        return LOCAL.isGathering();
    }

    private static float easeOutCubic(float f) {
        float g = 1.0f - f;
        return 1.0f - g * g * g;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class State {
        private final List<VoronoiOfQuad.Polygon> shards = new ArrayList<VoronoiOfQuad.Polygon>();
        private float[] baseAngle = new float[0];
        private float[] baseRadius = new float[0];
        private float[] fade = new float[0];
        private float[] radial = new float[0];
        private boolean gathering;
        private long gatherStartNs;
        private long gatherNanos = 360000000L;
        private float gatherFrom;
        private float resumeFrom;
        private float lastProgress;
        private float centerX;
        private float centerY;
        private float halfExtentX = 1.0f;
        private float halfExtentY = 1.0f;
        private float aspect = 1.0f;
        private boolean active;
        private long currentSeed;
        private float beginX;
        private float beginY;
        private float beginWidth;
        private float beginHeight;
        private float beginPad;
        private float beginScreenWidth;
        private float beginScreenHeight;

        public void begin(float x, float y, float w, float h, float pad, float sw, float sh, long seed) {
            this.cancel();
            if (w <= 1.0f || h <= 1.0f || sw <= 1.0f || sh <= 1.0f) {
                return;
            }
            Random random = new Random(seed);
            this.currentSeed = seed;
            this.beginX = x;
            this.beginY = y;
            this.beginWidth = w;
            this.beginHeight = h;
            this.beginPad = pad;
            this.beginScreenWidth = sw;
            this.beginScreenHeight = sh;
            float nx1 = GuiShatterAnimation.clamp01(x / sw);
            float ny1 = GuiShatterAnimation.clamp01(y / sh);
            float nx2 = GuiShatterAnimation.clamp01((x + w) / sw);
            float ny2 = GuiShatterAnimation.clamp01((y + h) / sh);
            this.centerX = (nx1 + nx2) * 0.5f;
            this.centerY = (ny1 + ny2) * 0.5f;
            this.halfExtentX = Math.max(0.001f, (nx2 - nx1) * 0.5f);
            this.halfExtentY = Math.max(0.001f, (ny2 - ny1) * 0.5f);
            this.aspect = Math.max(0.001f, sw / sh);
            int count = 22 + random.nextInt(7);
            VoronoiOfQuad voronoi = VoronoiOfQuad.spread(nx1, ny1, nx2, ny2, nx1 - 0.004f, ny1 - 0.004f, nx2 + 0.004f, ny2 + 0.004f, count, this.aspect, 0.45f, 0.32f, 0.68f, random);
            this.shards.clear();
            this.shards.addAll(voronoi.getPolygons());
            int n = this.shards.size();
            this.baseAngle = new float[n];
            this.baseRadius = new float[n];
            this.fade = new float[n];
            this.radial = new float[n];
            for (int i = 0; i < n; ++i) {
                VoronoiOfQuad.Polygon poly = this.shards.get(i);
                float dx = (poly.center.x - this.centerX) * this.aspect;
                float dy = poly.center.y - this.centerY;
                float dist = (float)Math.sqrt(dx * dx + dy * dy);
                this.baseAngle[i] = (float)Math.atan2(dy, dx) + (random.nextFloat() - 0.5f) * 0.35f;
                this.baseRadius[i] = dist;
                this.fade[i] = 0.55f + 0.45f * random.nextFloat();
                this.radial[i] = 0.65f + 0.7f * random.nextFloat();
            }
            this.active = true;
        }

        public void cancel() {
            this.active = false;
            this.gathering = false;
            this.lastProgress = 0.0f;
            this.shards.clear();
        }

        public boolean isActive() {
            return this.active;
        }

        public long seed() {
            return this.currentSeed;
        }

        public void gather(long ns) {
            if (this.active) {
                this.gathering = true;
                this.gatherStartNs = System.nanoTime();
                this.gatherNanos = Math.max(1000000L, ns);
                this.gatherFrom = this.lastProgress;
            }
        }

        public boolean isGathering() {
            return this.active && this.gathering;
        }

        public float blurRadius() {
            return !this.active ? 0.0f : 9.0f * (1.0f - this.lastProgress);
        }

        public float lastProgress() {
            return this.lastProgress;
        }

        public float[] beginRect() {
            return new float[]{this.beginX, this.beginY, this.beginWidth, this.beginHeight, this.beginPad, this.beginScreenWidth, this.beginScreenHeight};
        }

        public boolean resume() {
            if (!this.active) {
                return false;
            }
            this.gathering = false;
            this.resumeFrom = this.lastProgress;
            return true;
        }

        public float progress(float f) {
            if (!this.active) {
                return 0.0f;
            }
            if (this.gathering) {
                long elapsed = System.nanoTime() - this.gatherStartNs;
                float t = GuiShatterAnimation.clamp01((float)elapsed / (float)this.gatherNanos);
                this.lastProgress = this.gatherFrom * (1.0f - GuiShatterAnimation.easeOutCubic(t));
                if (t >= 1.0f) {
                    this.cancel();
                    return 0.0f;
                }
                return this.lastProgress;
            }
            this.lastProgress = f;
            return f;
        }

        public int buildGeometry(ByteBuffer buf, float progressVal, float sw, float sh, boolean flag, Remap remap) {
            if (!this.active || this.shards.isEmpty()) {
                return 0;
            }
            buf.clear();
            int count = 0;
            float p = this.progress(progressVal);
            float ease = GuiShatterAnimation.easeOutCubic(p);
            for (int i = 0; i < this.shards.size(); ++i) {
                VoronoiOfQuad.Polygon poly = this.shards.get(i);
                List<VoronoiOfQuad.Vec2f> verts = poly.getAllVertices();
                if (verts.size() < 3) continue;
                float ang = this.baseAngle[i];
                float rad = this.baseRadius[i] + ease * 0.34f * this.radial[i];
                float ox = this.centerX + (float)Math.cos(ang) * rad / this.aspect - poly.center.x;
                float oy = this.centerY + (float)Math.sin(ang) * rad - poly.center.y;
                float alpha = 1.0f - ease * this.fade[i];
                if (alpha <= 0.0f) continue;
                VoronoiOfQuad.Vec2f v0 = verts.get(0);
                for (int j = 1; j < verts.size() - 1; ++j) {
                    VoronoiOfQuad.Vec2f v1 = verts.get(j);
                    VoronoiOfQuad.Vec2f v2 = verts.get(j + 1);
                    this.putVertex(buf, v0, ox, oy, alpha, sw, sh, remap);
                    this.putVertex(buf, v1, ox, oy, alpha, sw, sh, remap);
                    this.putVertex(buf, v2, ox, oy, alpha, sw, sh, remap);
                    count += 3;
                }
            }
            buf.flip();
            return count;
        }

        private void putVertex(ByteBuffer buf, VoronoiOfQuad.Vec2f vec, float ox, float oy, float alpha, float sw, float sh, Remap remap) {
            float px = (vec.x + ox) * sw;
            float py = (vec.y + oy) * sh;
            float u = vec.x;
            float v = vec.y;
            if (remap != null) {
                px = remap.posOffsetX() + px * remap.posScaleX();
                py = remap.posOffsetY() + py * remap.posScaleY();
                u = remap.uvOffsetX() + u * remap.uvScaleX();
                v = remap.uvOffsetY() + v * remap.uvScaleY();
            }
            buf.putFloat(px);
            buf.putFloat(py);
            buf.putFloat(0.0f);
            buf.putFloat(u);
            buf.putFloat(v);
            buf.putFloat(alpha);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Remap(float posOffsetX, float posOffsetY, float posScaleX, float posScaleY, float uvOffsetX, float uvOffsetY, float uvScaleX, float uvScaleY) {
    }
}

