package net.skycore.client.gui.decide;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.util.BeginFrameHelper;
import net.skycore.client.util.CaptureLevelProjectionHelper;
import net.skycore.client.util.CurrentGuiGraphicsExtractorHelper;
import net.skycore.client.util.GuiPipelines;
import net.skycore.client.util.ScissorAreaHelper;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class DecideCloseAnimation {
    public static final int MODE_NONE = 0;
    public static final int MODE_3D = 1;
    public static final int MODE_SHATTER = 2;

    private static final long DURATION_3D_FROZEN_MS = 900L;
    private static final long DURATION_3D_FADE_MS = 600L;
    private static final long DURATION_3D_TOTAL_MS = 1500L;
    private static final float PANEL_3D_DISTANCE = 2.6f;
    private static final int GRID_3D_X = 8;
    private static final int GRID_3D_Y = 6;

    private static final long DURATION_SHATTER_MS = 700L;
    private static final int MAX_VERTICES = 2048;

    private static TextureTarget snapshotTarget;
    private static boolean pendingCapture;
    private static int pendingMode;
    private static float pendingNx1;
    private static float pendingNy1;
    private static float pendingNx2;
    private static float pendingNy2;

    private static int activeMode = MODE_NONE;
    private static long startTimeMs;
    private static float nx1;
    private static float ny1;
    private static float nx2;
    private static float ny2;

    // 3D world-space panel state (zero per-frame allocations)
    private static double savedCamX;
    private static double savedCamY;
    private static double savedCamZ;
    private static final Matrix4f SAVED_CAM_TO_WORLD = new Matrix4f();
    private static float baseHalfW;
    private static float baseHalfH;
    private static final float[] GRID_SX = new float[(GRID_3D_X + 1) * (GRID_3D_Y + 1)];
    private static final float[] GRID_SY = new float[(GRID_3D_X + 1) * (GRID_3D_Y + 1)];
    private static final boolean[] GRID_OK = new boolean[(GRID_3D_X + 1) * (GRID_3D_Y + 1)];
    private static final Vector4f TMP_VEC = new Vector4f();
    private static final Matrix4f LIVE_PROJ = new Matrix4f();

    // Shatter Voronoi state (zero per-frame allocations)
    private static final List<VoronoiOfQuad.Polygon> SHARDS = new ArrayList<>();
    private static float[] baseAngle = new float[0];
    private static float[] baseRadius = new float[0];
    private static float[] shardFade = new float[0];
    private static float[] shardRadial = new float[0];
    private static float centerX;
    private static float centerY;
    private static float halfExtentX = 1.0f;
    private static float halfExtentY = 1.0f;
    private static float aspect = 1.0f;

    // Preallocated triangle buffers: x, y, u, v (4 floats/vertex) + argb (1 int/vertex)
    private static final float[] VERT_XYUV = new float[MAX_VERTICES * 4];
    private static final int[] VERT_COLOR = new int[MAX_VERTICES];

    private DecideCloseAnimation() {
    }

    public static void cancel() {
        pendingCapture = false;
        activeMode = MODE_NONE;
        SHARDS.clear();
    }

    public static boolean isActive() {
        return activeMode != MODE_NONE;
    }

    public static boolean armCapture(int mode, float panelX, float panelY, float panelW, float panelH,
                                     float guiW, float guiH) {
        if (guiW <= 1.0f || guiH <= 1.0f || panelW <= 2.0f || panelH <= 2.0f) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || mc.player == null || mc.gameRenderer == null) {
            return false;
        }
        if (ensureSnapshotTarget(mc) == null) {
            return false;
        }
        float x0 = DecideDraw.clamp(panelX / guiW, 0.0f, 1.0f);
        float y0 = DecideDraw.clamp(panelY / guiH, 0.0f, 1.0f);
        float x1 = DecideDraw.clamp((panelX + panelW) / guiW, 0.0f, 1.0f);
        float y1 = DecideDraw.clamp((panelY + panelH) / guiH, 0.0f, 1.0f);
        if (x1 - x0 <= 0.01f || y1 - y0 <= 0.01f) {
            return false;
        }

        if (mode == MODE_3D) {
            CameraRenderState cam = mc.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
            Matrix4f proj = CaptureLevelProjectionHelper.levelProjectionCopy();
            if (!cam.initialized || proj == null || Math.abs(proj.m11()) < 1.0E-4f || Math.abs(proj.m00()) < 1.0E-4f) {
                return false;
            }
            Vec3 pos = cam.pos;
            savedCamX = pos.x();
            savedCamY = pos.y();
            savedCamZ = pos.z();
            SAVED_CAM_TO_WORLD.set(cam.viewRotationMatrix).invert();
            baseHalfW = (PANEL_3D_DISTANCE / Math.abs(proj.m00())) * (x1 - x0);
            baseHalfH = (PANEL_3D_DISTANCE / Math.abs(proj.m11())) * (y1 - y0);
        } else if (mode == MODE_SHATTER) {
            if (!buildShards(x0, y0, x1, y1, guiW / guiH)) {
                return false;
            }
        } else {
            return false;
        }

        pendingMode = mode;
        pendingNx1 = x0;
        pendingNy1 = y0;
        pendingNx2 = x1;
        pendingNy2 = y1;
        pendingCapture = true;
        return true;
    }

    public static void queueCaptureMarker() {
        GuiGraphicsExtractor extractor = CurrentGuiGraphicsExtractorHelper.currentGuiGraphicsExtractor();
        if (extractor == null || !pendingCapture) {
            return;
        }
        BeginFrameHelper.customElement(new CaptureMarkerState(extractor.pose(), BeginFrameHelper.activeScissor()));
    }

    public static void captureNow(RenderTarget sourceTarget) {
        if (!pendingCapture) {
            return;
        }
        pendingCapture = false;
        if (snapshotTarget == null || sourceTarget == null || sourceTarget.getColorTexture() == null) {
            return;
        }
        if (snapshotTarget.width != sourceTarget.width || snapshotTarget.height != sourceTarget.height) {
            return;
        }
        RenderSystem.getDevice()
                .createCommandEncoder()
                .copyTextureToTexture(
                        sourceTarget.getColorTexture(),
                        snapshotTarget.getColorTexture(),
                        0, 0, 0, 0, 0,
                        sourceTarget.width,
                        sourceTarget.height
                );
        nx1 = pendingNx1;
        ny1 = pendingNy1;
        nx2 = pendingNx2;
        ny2 = pendingNy2;
        activeMode = pendingMode;
        startTimeMs = System.currentTimeMillis();
    }

    public static boolean consumeReadyToCloseScreen() {
        return !pendingCapture && activeMode != MODE_NONE;
    }

    private static GpuTextureView ensureSnapshotTarget(Minecraft mc) {
        if (RenderSystem.tryGetDevice() == null || mc.gameRenderer == null) {
            return null;
        }
        RenderTarget main = mc.gameRenderer.mainRenderTarget();
        if (main == null || main.getColorTexture() == null || main.width <= 1 || main.height <= 1) {
            return null;
        }
        if (snapshotTarget == null || snapshotTarget.width != main.width || snapshotTarget.height != main.height) {
            if (snapshotTarget != null) {
                snapshotTarget.destroyBuffers();
            }
            snapshotTarget = new TextureTarget("skycore-decide-close-snapshot", main.width, main.height, false, GpuFormat.RGBA8_UNORM);
        }
        return snapshotTarget.getColorTextureView();
    }

    private static boolean buildShards(float x0, float y0, float x1, float y1, float screenAspect) {
        centerX = (x0 + x1) * 0.5f;
        centerY = (y0 + y1) * 0.5f;
        aspect = Math.max(0.001f, screenAspect);
        halfExtentX = Math.max(0.001f, (x1 - x0) * 0.5f * aspect);
        halfExtentY = Math.max(0.001f, (y1 - y0) * 0.5f);
        Random random = new Random();
        int count = 22 + random.nextInt(7);
        VoronoiOfQuad voronoi = VoronoiOfQuad.spread(
                x0, y0, x1, y1,
                x0 - 0.004f, y0 - 0.004f, x1 + 0.004f, y1 + 0.004f,
                count, aspect, 0.45f, 0.32f, 0.68f, random
        );
        SHARDS.clear();
        SHARDS.addAll(voronoi.getPolygons());
        if (SHARDS.isEmpty()) {
            return false;
        }
        int n = SHARDS.size();
        baseAngle = new float[n];
        baseRadius = new float[n];
        shardFade = new float[n];
        shardRadial = new float[n];
        for (int i = 0; i < n; i++) {
            VoronoiOfQuad.Vec2f c = SHARDS.get(i).center;
            float dx = (c.x - centerX) * aspect;
            float dy = c.y - centerY;
            baseAngle[i] = (float) Math.atan2(dy, dx);
            baseRadius[i] = (float) Math.sqrt(dx * dx + dy * dy);
            shardFade[i] = 0.45f * random.nextFloat();
            shardRadial[i] = 0.65f + 0.7f * random.nextFloat();
        }
        return true;
    }

    public static void renderHud(int guiWidth, int guiHeight) {
        if (activeMode == MODE_NONE || snapshotTarget == null || guiWidth <= 0 || guiHeight <= 0) {
            return;
        }
        GuiGraphicsExtractor extractor = CurrentGuiGraphicsExtractorHelper.currentGuiGraphicsExtractor();
        if (extractor == null) {
            return;
        }
        GpuTextureView view = snapshotTarget.getColorTextureView();
        if (view == null) {
            cancel();
            return;
        }
        if (activeMode == MODE_3D) {
            render3D(extractor, view, guiWidth, guiHeight);
        } else if (activeMode == MODE_SHATTER) {
            renderShatter(extractor, view, guiWidth, guiHeight);
        }
    }

    private static void render3D(GuiGraphicsExtractor extractor, GpuTextureView view, int guiWidth, int guiHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || mc.player == null || mc.gameRenderer == null) {
            cancel();
            return;
        }
        long elapsed = System.currentTimeMillis() - startTimeMs;
        if (elapsed >= DURATION_3D_TOTAL_MS) {
            cancel();
            return;
        }
        float t = elapsed < DURATION_3D_FROZEN_MS
                ? 0.0f
                : DecideDraw.clamp((float) (elapsed - DURATION_3D_FROZEN_MS) / (float) DURATION_3D_FADE_MS, 0.0f, 1.0f);
        float shrink = 1.0f - t * t * t;
        if (shrink <= 0.01f) {
            cancel();
            return;
        }
        CameraRenderState cam = mc.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
        Matrix4f projCopy = CaptureLevelProjectionHelper.levelProjectionCopy();
        if (!cam.initialized || projCopy == null) {
            return;
        }
        LIVE_PROJ.set(projCopy);
        Vec3 livePos = cam.pos;
        float camDx = (float) (savedCamX - livePos.x());
        float camDy = (float) (savedCamY - livePos.y());
        float camDz = (float) (savedCamZ - livePos.z());

        float hw = baseHalfW * shrink;
        float hh = baseHalfH * shrink;
        int stride = GRID_3D_X + 1;
        for (int gy = 0; gy <= GRID_3D_Y; gy++) {
            float fy = (float) gy / (float) GRID_3D_Y;
            float localY = hh - 2.0f * hh * fy;
            for (int gx = 0; gx <= GRID_3D_X; gx++) {
                float fx = (float) gx / (float) GRID_3D_X;
                float localX = -hw + 2.0f * hw * fx;
                int idx = gy * stride + gx;
                TMP_VEC.set(localX, localY, -PANEL_3D_DISTANCE, 1.0f);
                SAVED_CAM_TO_WORLD.transform(TMP_VEC);
                TMP_VEC.set(TMP_VEC.x + camDx, TMP_VEC.y + camDy, TMP_VEC.z + camDz, 1.0f);
                cam.viewRotationMatrix.transform(TMP_VEC);
                LIVE_PROJ.transform(TMP_VEC);
                if (TMP_VEC.w <= 0.02f) {
                    GRID_OK[idx] = false;
                } else {
                    float ndcX = TMP_VEC.x / TMP_VEC.w;
                    float ndcY = TMP_VEC.y / TMP_VEC.w;
                    GRID_SX[idx] = (ndcX + 1.0f) * 0.5f * guiWidth;
                    GRID_SY[idx] = (1.0f - ndcY) * 0.5f * guiHeight;
                    GRID_OK[idx] = Math.abs(ndcX) <= 3.0f && Math.abs(ndcY) <= 3.0f;
                }
            }
        }

        int vertCount = 0;
        int color = 0xFFFFFFFF;
        for (int gy = 0; gy < GRID_3D_Y; gy++) {
            float v0 = 1.0f - (ny1 + (ny2 - ny1) * ((float) gy / GRID_3D_Y));
            float v1 = 1.0f - (ny1 + (ny2 - ny1) * ((float) (gy + 1) / GRID_3D_Y));
            for (int gx = 0; gx < GRID_3D_X; gx++) {
                int i00 = gy * stride + gx;
                int i10 = i00 + 1;
                int i01 = i00 + stride;
                int i11 = i01 + 1;
                if (!GRID_OK[i00] || !GRID_OK[i10] || !GRID_OK[i01] || !GRID_OK[i11]) {
                    continue;
                }
                if (vertCount + 6 > MAX_VERTICES) {
                    break;
                }
                float u0 = nx1 + (nx2 - nx1) * ((float) gx / GRID_3D_X);
                float u1 = nx1 + (nx2 - nx1) * ((float) (gx + 1) / GRID_3D_X);
                vertCount = pushVert(vertCount, GRID_SX[i00], GRID_SY[i00], u0, v0, color);
                vertCount = pushVert(vertCount, GRID_SX[i01], GRID_SY[i01], u0, v1, color);
                vertCount = pushVert(vertCount, GRID_SX[i11], GRID_SY[i11], u1, v1, color);
                vertCount = pushVert(vertCount, GRID_SX[i00], GRID_SY[i00], u0, v0, color);
                vertCount = pushVert(vertCount, GRID_SX[i11], GRID_SY[i11], u1, v1, color);
                vertCount = pushVert(vertCount, GRID_SX[i10], GRID_SY[i10], u1, v0, color);
            }
        }
        if (vertCount >= 3) {
            BeginFrameHelper.customElement(new SnapshotTriangleState(
                    extractor.pose(), view, vertCount, guiWidth, guiHeight, BeginFrameHelper.activeScissor()
            ));
        }
    }

    private static void renderShatter(GuiGraphicsExtractor extractor, GpuTextureView view, int guiWidth, int guiHeight) {
        long elapsed = System.currentTimeMillis() - startTimeMs;
        if (elapsed >= DURATION_SHATTER_MS) {
            cancel();
            return;
        }
        float p = DecideDraw.clamp((float) elapsed / (float) DURATION_SHATTER_MS, 0.0f, 1.0f);
        float globalAlpha = p <= 0.75f ? 1.0f : DecideDraw.clamp(1.0f - (p - 0.75f) / 0.25f, 0.0f, 1.0f);
        float g = 1.0f - p;
        float push = 1.0f - g * g * g;

        int vertCount = 0;
        int n = SHARDS.size();
        for (int i = 0; i < n; i++) {
            VoronoiOfQuad.Polygon poly = SHARDS.get(i);
            float shardAlpha = DecideDraw.clamp(1.0f - p * (0.9f + shardFade[i]), 0.0f, 1.0f) * globalAlpha;
            if (shardAlpha <= 0.004f) {
                continue;
            }
            float radius = baseRadius[i] * (1.0f + 0.34f * shardRadial[i] * push) + 0.028f * shardRadial[i] * push;
            float pivotX = centerX + (float) Math.cos(baseAngle[i]) * radius / aspect;
            float pivotY = centerY + (float) Math.sin(baseAngle[i]) * radius;
            float offsetX = pivotX - poly.center.x;
            float offsetY = pivotY - poly.center.y;
            float edgeX = DecideDraw.clamp((poly.center.x - centerX) * aspect / halfExtentX, -1.0f, 1.0f);
            float edgeY = DecideDraw.clamp((poly.center.y - centerY) / halfExtentY, -1.0f, 1.0f);
            float yawAngle = (float) Math.toRadians(26.0f * edgeX * push);
            float pitchAngle = (float) Math.toRadians(-26.0f * edgeY * push);
            float bowlZ = 0.1f * (1.0f - Math.min(1.0f, edgeX * edgeX + edgeY * edgeY)) * push;
            float yawSin = (float) Math.sin(yawAngle);
            float yawCos = (float) Math.cos(yawAngle);
            float pitchSin = (float) Math.sin(pitchAngle);
            float pitchCos = (float) Math.cos(pitchAngle);
            ArrayList<VoronoiOfQuad.Vec2f> pts = poly.list;
            int corners = pts.size();
            if (corners < 3) {
                continue;
            }
            int argb = DecideDraw.getColor(255, shardAlpha);
            VoronoiOfQuad.Vec2f v0 = pts.get(0);
            for (int t = 1; t + 1 < corners && vertCount + 3 <= MAX_VERTICES; t++) {
                vertCount = putShatterVert(vertCount, v0, offsetX, offsetY, pivotX, pivotY,
                        yawSin, yawCos, pitchSin, pitchCos, bowlZ, argb, guiWidth, guiHeight);
                vertCount = putShatterVert(vertCount, pts.get(t), offsetX, offsetY, pivotX, pivotY,
                        yawSin, yawCos, pitchSin, pitchCos, bowlZ, argb, guiWidth, guiHeight);
                vertCount = putShatterVert(vertCount, pts.get(t + 1), offsetX, offsetY, pivotX, pivotY,
                        yawSin, yawCos, pitchSin, pitchCos, bowlZ, argb, guiWidth, guiHeight);
            }
        }

        if (vertCount >= 3) {
            BeginFrameHelper.customElement(new SnapshotTriangleState(
                    extractor.pose(), view, vertCount, guiWidth, guiHeight, BeginFrameHelper.activeScissor()
            ));
        }
    }

    private static int putShatterVert(int vertIndex, VoronoiOfQuad.Vec2f src,
                                      float offsetX, float offsetY, float pivotX, float pivotY,
                                      float yawSin, float yawCos, float pitchSin, float pitchCos,
                                      float bowlZ, int argb, int guiWidth, int guiHeight) {
        float localX = (src.x + offsetX - pivotX) * aspect;
        float localY = src.y + offsetY - pivotY;
        float rotatedX = localX * yawCos;
        float rotatedZ = -localX * yawSin;
        float finalY = localY * pitchCos - rotatedZ * pitchSin;
        float finalZ = localY * pitchSin + rotatedZ * pitchCos + bowlZ;
        float posX = pivotX + rotatedX / aspect;
        float posY = pivotY + finalY;
        float perspective = 1.0f / Math.max(0.2f, 1.0f + finalZ * 0.9f);
        posX = 0.5f + (posX - 0.5f) * perspective;
        posY = 0.5f + (posY - 0.5f) * perspective;
        float sx = posX * guiWidth;
        float sy = posY * guiHeight;
        float uvX = src.x;
        float uvY = 1.0f - src.y;
        return pushVert(vertIndex, sx, sy, uvX, uvY, argb);
    }

    private static int pushVert(int vertIndex, float x, float y, float u, float v, int color) {
        int base = vertIndex * 4;
        VERT_XYUV[base] = x;
        VERT_XYUV[base + 1] = y;
        VERT_XYUV[base + 2] = u;
        VERT_XYUV[base + 3] = v;
        VERT_COLOR[vertIndex] = color;
        return vertIndex + 1;
    }

    private static final class CaptureMarkerState extends ScissorAreaHelper {
        CaptureMarkerState(Matrix3x2fc pose, ScreenRectangle scissor) {
            super(pose, scissor, 0.0f, 0.0f, 1.0f, 1.0f);
        }

        @Override
        public RenderPipeline pipeline() {
            return GuiPipelines.DECIDE_CAPTURE_MARKER;
        }

        @Override
        public TextureSetup textureSetup() {
            return TextureSetup.noTexture();
        }

        @Override
        public void buildVertices(VertexConsumer vc) {
            for (int i = 0; i < 4; i++) {
                vc.addVertex(0.0f, 0.0f, 0.0f)
                        .setColor(0)
                        .setUv(0.0f, 0.0f)
                        .setUv1(0, 0)
                        .setUv2(0, 0)
                        .setNormal(0.0f, 0.0f, 1.0f)
                        .setLineWidth(0.0f);
            }
        }
    }

    private static final class SnapshotTriangleState extends ScissorAreaHelper {
        private final TextureSetup setup;
        private final int vertexCount;

        SnapshotTriangleState(Matrix3x2fc pose, GpuTextureView view, int vertexCount,
                              float guiW, float guiH, ScreenRectangle scissor) {
            super(pose, scissor, 0.0f, 0.0f, guiW, guiH);
            this.setup = TextureSetup.singleTexture(view, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
            this.vertexCount = vertexCount;
        }

        @Override
        public RenderPipeline pipeline() {
            return GuiPipelines.TEXTURE_TRIANGLES;
        }

        @Override
        public TextureSetup textureSetup() {
            return this.setup;
        }

        @Override
        public void buildVertices(VertexConsumer vc) {
            int n = this.vertexCount;
            for (int i = 0; i < n; i++) {
                int base = i * 4;
                float x = VERT_XYUV[base];
                float y = VERT_XYUV[base + 1];
                float u = VERT_XYUV[base + 2];
                float v = VERT_XYUV[base + 3];
                vc.addVertex(transformX(x, y), transformY(x, y), 0.0f)
                        .setColor(VERT_COLOR[i])
                        .setUv(u, v)
                        .setUv1(0, 0)
                        .setUv2(0, 0)
                        .setNormal(0.0f, 0.0f, 1.0f)
                        .setLineWidth(0.0f);
            }
        }
    }
}
