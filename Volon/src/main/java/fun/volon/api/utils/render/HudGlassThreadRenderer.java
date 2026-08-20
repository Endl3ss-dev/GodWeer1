package fun.volon.api.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import fun.volon.api.QClient;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.scissor.ScissorUtils;
import fun.volon.client.modules.impl.render.Interface;

/**
 * Animated thread web for flat HUD panels (BlockOverlay "Нитки" style).
 */
public final class HudGlassThreadRenderer implements QClient {

    private static final float LINE_SPEED = 1.4f;
    private static final float LINE_JITTER = 0.55f;
    private static final float PANEL_DEPTH = 0.35f;

    private HudGlassThreadRenderer() {
    }

    public static void draw(MatrixStack matrices, float x, float y, float width, float height, int themeColor, long seed) {
        if (width < 22f || height < 12f) {
            return;
        }

        int hudAlpha = getHudAlpha();
        int baseAlpha = Math.max(22, Math.min(255, (int) (hudAlpha * 0.88f)));

        matrices.push();
        matrices.translate(x, y, 0f);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Box box = new Box(0.0, 0.0, 0.0, width, height, PANEL_DEPTH);

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(x, y, width, height);
        try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

            drawAnimatedWeb(matrix, box, seed, themeColor, baseAlpha);
        } finally {
            ScissorUtils.unset();
            ScissorUtils.pop();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            matrices.pop();
            RenderUtils.resetGuiRenderState();
        }
    }

    private static int getHudAlpha() {
        return 150;
    }

    private static void drawAnimatedWeb(Matrix4f matrix, Box box, long seed, int themeColor, int baseAlpha) {
        int strandsPerFace = 3;
        int samples = 12;
        float t = (System.currentTimeMillis() % 100000L) / 1000.0f * LINE_SPEED;
        float lineWidth = 0.85f;
        float bendBase = 2.8f + LINE_JITTER * 9.5f;

        for (int face = 0; face < 6; face++) {
            int[] neighbors = faceNeighbors(face);
            for (int strand = 0; strand < strandsPerFace; strand++) {
                int key = face * 1000 + strand * 53;
                int adj = neighbors[strand % neighbors.length];
                double phase = t * (0.95 + rand01(seed, key + 1) * 0.55) + strand * 0.83 + face * 1.11;
                double edgeT = clamp01(0.5 + Math.sin(phase * 1.37 + rand01(seed, key + 2) * 6.2831853) * 0.38);

                Vec3d pivot = edgePoint(box, face, adj, edgeT, 0.08);
                Vec3d start = facePoint(box, face,
                        clamp01(0.5 + (rand01(seed, key + 3) - 0.5) * 0.46),
                        clamp01(0.5 + (rand01(seed, key + 4) - 0.5) * 0.46),
                        0.08);
                Vec3d end = facePoint(box, adj,
                        clamp01(0.5 + (rand01(seed, key + 5) - 0.5) * 0.46),
                        clamp01(0.5 + (rand01(seed, key + 6) - 0.5) * 0.46),
                        0.08);

                Vec3d[] basisA = faceBasis(face);
                Vec3d[] basisB = faceBasis(adj);
                Vec3d normalA = faceNormal(face);
                Vec3d normalB = faceNormal(adj);
                double bendA = bendBase * (0.7 + rand01(seed, key + 7))
                        * Math.sin(phase * 1.9 + rand01(seed, key + 8) * 6.2831853);
                double bendB = bendBase * (0.7 + rand01(seed, key + 9))
                        * Math.cos(phase * 1.7 + rand01(seed, key + 10) * 6.2831853);

                Vec3d dirA = pivot.subtract(start);
                Vec3d c1a = start.add(dirA.multiply(0.38)).add(basisA[0].multiply(bendA)).add(basisA[1].multiply(-bendA * 0.55));
                Vec3d c2a = start.add(dirA.multiply(0.76)).add(basisA[0].multiply(-bendA * 0.65)).add(basisA[1].multiply(bendA * 0.4));

                Vec3d dirB = end.subtract(pivot);
                Vec3d c1b = pivot.add(dirB.multiply(0.24)).add(basisB[0].multiply(bendB)).add(basisB[1].multiply(bendB * 0.45));
                Vec3d c2b = pivot.add(dirB.multiply(0.62)).add(basisB[0].multiply(-bendB * 0.7)).add(basisB[1].multiply(-bendB * 0.35));

                int alphaLine = Math.max(18, Math.min(255, (int) (baseAlpha * (0.74 + 0.26 * Math.sin(phase * 2.6)))));
                int color = ColorUtils.setAlphaColor(themeColor, alphaLine);
                drawBezierRibbon(matrix, start, c1a, c2a, pivot, normalA, samples, color, lineWidth);
                drawBezierRibbon(matrix, pivot, c1b, c2b, end, normalB, samples, color, lineWidth);
            }
        }
    }

    private static void drawBezierRibbon(Matrix4f matrix, Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, Vec3d faceNormal, int samples, int color, float halfWidth) {
        Vec3d[] points = new Vec3d[samples + 1];
        for (int s = 0; s <= samples; s++) {
            float u = (float) s / (float) samples;
            points[s] = cubicBezier(p0, p1, p2, p3, u);
        }

        BufferBuilder quads = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < samples; i++) {
            Vec3d a = points[i];
            Vec3d b = points[i + 1];
            Vec3d dir = b.subtract(a);
            if (dir.lengthSquared() < 1.0E-6) continue;

            Vec3d perp = faceNormal.crossProduct(dir);
            if (perp.lengthSquared() < 1.0E-6) continue;
            perp = perp.normalize().multiply(halfWidth);

            Vec3d aL = a.add(perp);
            Vec3d aR = a.subtract(perp);
            Vec3d bL = b.add(perp);
            Vec3d bR = b.subtract(perp);

            quads.vertex(matrix, (float) aL.x, (float) aL.y, (float) aL.z).color(color);
            quads.vertex(matrix, (float) aR.x, (float) aR.y, (float) aR.z).color(color);
            quads.vertex(matrix, (float) bR.x, (float) bR.y, (float) bR.z).color(color);
            quads.vertex(matrix, (float) bL.x, (float) bL.y, (float) bL.z).color(color);
        }
        BufferRenderer.drawWithGlobalProgram(quads.end());
    }

    private static Vec3d cubicBezier(Vec3d p0, Vec3d p1, Vec3d p2, Vec3d p3, float t) {
        double it = 1.0 - t;
        double it2 = it * it;
        double t2 = t * t;
        return p0.multiply(it2 * it)
                .add(p1.multiply(3.0 * it2 * t))
                .add(p2.multiply(3.0 * it * t2))
                .add(p3.multiply(t2 * t));
    }

    private static int[] faceNeighbors(int face) {
        return switch (face) {
            case 0, 1 -> new int[]{2, 3, 4, 5};
            case 2, 3 -> new int[]{0, 1, 4, 5};
            default -> new int[]{0, 1, 2, 3};
        };
    }

    private static Vec3d[] faceBasis(int face) {
        return switch (face) {
            case 0, 1 -> new Vec3d[]{new Vec3d(1, 0, 0), new Vec3d(0, 0, 1)};
            case 2, 3 -> new Vec3d[]{new Vec3d(1, 0, 0), new Vec3d(0, 1, 0)};
            default -> new Vec3d[]{new Vec3d(0, 0, 1), new Vec3d(0, 1, 0)};
        };
    }

    private static Vec3d faceNormal(int face) {
        return switch (face) {
            case 0 -> new Vec3d(0, 1, 0);
            case 1 -> new Vec3d(0, -1, 0);
            case 2 -> new Vec3d(0, 0, -1);
            case 3 -> new Vec3d(0, 0, 1);
            case 4 -> new Vec3d(-1, 0, 0);
            default -> new Vec3d(1, 0, 0);
        };
    }

    private static Vec3d edgePoint(Box box, int faceA, int faceB, double t, double inset) {
        double bx = Double.NaN;
        double by = Double.NaN;
        double bz = Double.NaN;

        double[] fixedA = faceFixedCoords(box, faceA, inset);
        if (!Double.isNaN(fixedA[0])) bx = fixedA[0];
        if (!Double.isNaN(fixedA[1])) by = fixedA[1];
        if (!Double.isNaN(fixedA[2])) bz = fixedA[2];

        double[] fixedB = faceFixedCoords(box, faceB, inset);
        if (!Double.isNaN(fixedB[0])) bx = fixedB[0];
        if (!Double.isNaN(fixedB[1])) by = fixedB[1];
        if (!Double.isNaN(fixedB[2])) bz = fixedB[2];

        double tt = clamp01(t);
        if (Double.isNaN(bx)) bx = lerp(box.minX, box.maxX, tt);
        if (Double.isNaN(by)) by = lerp(box.minY, box.maxY, tt);
        if (Double.isNaN(bz)) bz = lerp(box.minZ, box.maxZ, tt);
        return new Vec3d(bx, by, bz);
    }

    private static double[] faceFixedCoords(Box box, int face, double inset) {
        return switch (face) {
            case 0 -> new double[]{Double.NaN, box.maxY - inset, Double.NaN};
            case 1 -> new double[]{Double.NaN, box.minY + inset, Double.NaN};
            case 2 -> new double[]{Double.NaN, Double.NaN, box.minZ + inset};
            case 3 -> new double[]{Double.NaN, Double.NaN, box.maxZ - inset};
            case 4 -> new double[]{box.minX + inset, Double.NaN, Double.NaN};
            default -> new double[]{box.maxX - inset, Double.NaN, Double.NaN};
        };
    }

    private static Vec3d facePoint(Box box, int face, double u, double v, double inset) {
        u = clamp01(u);
        v = clamp01(v);
        return switch (face) {
            case 0 -> new Vec3d(lerp(box.minX, box.maxX, u), box.maxY - inset, lerp(box.minZ, box.maxZ, v));
            case 1 -> new Vec3d(lerp(box.minX, box.maxX, u), box.minY + inset, lerp(box.minZ, box.maxZ, v));
            case 2 -> new Vec3d(lerp(box.minX, box.maxX, u), lerp(box.minY, box.maxY, v), box.minZ + inset);
            case 3 -> new Vec3d(lerp(box.minX, box.maxX, u), lerp(box.minY, box.maxY, v), box.maxZ - inset);
            case 4 -> new Vec3d(box.minX + inset, lerp(box.minY, box.maxY, v), lerp(box.minZ, box.maxZ, u));
            default -> new Vec3d(box.maxX - inset, lerp(box.minY, box.maxY, v), lerp(box.minZ, box.maxZ, u));
        };
    }

    private static double rand01(long seed, int salt) {
        long x = seed + 0x9E3779B97F4A7C15L * (salt + 1L);
        x ^= (x >>> 30);
        x *= 0xBF58476D1CE4E5B9L;
        x ^= (x >>> 27);
        x *= 0x94D049BB133111EBL;
        x ^= (x >>> 31);
        return (double) (x & 0xFFFFFF) / (double) 0x1000000;
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    public static long seedForRect(float x, float y, float width, float height) {
        return ((long) (x * 31.7f) << 32) ^ ((long) (y * 17.3f) << 16) ^ (long) (width * 11.1f + height * 7.9f);
    }
}
