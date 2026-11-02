/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_1921
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_2350
 *  net.minecraft.class_2350$class_2351
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_638
 *  net.minecraft.class_9799
 *  org.joml.FrustumIntersection
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package ru.decide.module.impl.render.voidbutterflies;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_638;
import net.minecraft.class_9799;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public final class VoidButterflyRenderer {
    private static final int BUTTERFLIES_PER_DENSITY = 72;
    private static final int SPAWN_ATTEMPTS_PER_DENSITY = 64;
    private static final int MAX_SPAWNS_PER_TICK = 5;
    private static final int FADE_OUT_KEEP_TICKS = 12;
    private static final float RESET_DISTANCE_SQ_FACTOR = 4.0f;
    private static final float FADE_IN_TICKS = 14.0f;
    private static final float FADE_OUT_TICKS = 22.0f;
    private static final float FADE_REMOVE_TICKS = 12.0f;
    private static final float MIN_ALPHA = 0.005f;
    private static final float FRUSTUM_SCALE = 3.0f;
    private static final int WING_POINTS = 6;
    private static final class_2350[] DIRECTIONS = class_2350.values();
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(class_2960.method_60655((String)"decide", (String)"void_butterfly")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withVertexShader(class_2960.method_60655((String)"decide", (String)"core/void_butterfly")).withFragmentShader(class_2960.method_60655((String)"decide", (String)"core/void_butterfly")).withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final class_1921 LAYER = class_1921.method_75940((String)"void_butterfly", (class_12247)class_12247.method_75927((RenderPipeline)PIPELINE).method_75937().method_75929(32768).method_75938());
    private final List<Butterfly> butterflies = new ArrayList<Butterfly>();
    private final Random random = new Random();
    private final class_2338.class_2339 scratch = new class_2338.class_2339();
    private final FrustumIntersection frustum = new FrustumIntersection();
    private final Matrix4f frustumMatrix = new Matrix4f();
    private final Vector3f cameraRight = new Vector3f();
    private final Vector3f cameraUp = new Vector3f();
    private final class_9799 allocator = new class_9799(131072);
    private class_638 world;
    private class_243 lastCamera;
    private int tick;
    private int directionCursor;

    public void reset() {
        this.butterflies.clear();
        this.world = null;
        this.lastCamera = null;
        this.tick = 0;
    }

    public void update(float radius, float density) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null) {
            this.reset();
            return;
        }
        class_243 camera = mc.field_1773.method_19418().method_71156();
        if (this.world != mc.field_1687 || this.lastCamera != null && this.lastCamera.method_1025(camera) > (double)(radius * radius * 4.0f)) {
            this.reset();
            this.world = mc.field_1687;
        }
        this.lastCamera = camera;
        ++this.tick;
        int target = Math.round(72.0f * density);
        Iterator<Butterfly> iterator = this.butterflies.iterator();
        while (iterator.hasNext()) {
            Butterfly butterfly = iterator.next();
            if (this.tick - butterfly.spawnTick >= butterfly.lifetime || butterfly.fadeTick >= 0 && this.tick - butterfly.fadeTick >= 12) {
                iterator.remove();
                continue;
            }
            if (butterfly.fadeTick >= 0 || !(butterfly.anchor.method_1025(camera) > (double)((radius + 2.0f) * (radius + 2.0f))) && mc.field_1687.method_8320(butterfly.pos).equals((Object)butterfly.state) && !this.isInsideSolid(this.animatedPos(butterfly, this.tick))) continue;
            butterfly.fadeTick = this.tick;
        }
        if (this.butterflies.size() >= target) {
            return;
        }
        int attempts = Math.round(64.0f * density);
        int spawned = 0;
        while (attempts-- > 0 && spawned < 5 && this.butterflies.size() < target) {
            if (!this.spawn(camera, radius)) continue;
            ++spawned;
        }
    }

    public void render(Matrix4f view, Matrix4f projection, float tickDelta, float radius, int colorFrom, int colorTo) {
        if (this.world == null || this.butterflies.isEmpty()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || this.world != mc.field_1687 || view == null || projection == null) {
            return;
        }
        float time = (float)this.tick + tickDelta;
        class_243 camera = mc.field_1773.method_19418().method_71156();
        this.frustumMatrix.set((Matrix4fc)projection).mul((Matrix4fc)view);
        this.frustum.set((Matrix4fc)this.frustumMatrix);
        Quaternionf cameraRotation = mc.field_1773.method_19418().method_23767();
        this.cameraRight.set(1.0f, 0.0f, 0.0f).rotate((Quaternionfc)cameraRotation);
        this.cameraUp.set(0.0f, 1.0f, 0.0f).rotate((Quaternionfc)cameraRotation);
        class_243 right = new class_243((double)this.cameraRight.x, (double)this.cameraRight.y, (double)this.cameraRight.z);
        class_243 up = new class_243((double)this.cameraUp.x, (double)this.cameraUp.y, (double)this.cameraUp.z);
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        class_4588 consumer = immediate.method_73477(LAYER);
        for (Butterfly butterfly : this.butterflies) {
            float age = time - (float)butterfly.spawnTick;
            float fade = class_3532.method_15363((float)(age / 14.0f), (float)0.0f, (float)1.0f) * class_3532.method_15363((float)(((float)butterfly.lifetime - age) / 22.0f), (float)0.0f, (float)1.0f);
            if (butterfly.fadeTick >= 0) {
                fade *= class_3532.method_15363((float)(1.0f - (time - (float)butterfly.fadeTick) / 12.0f), (float)0.0f, (float)1.0f);
            }
            class_243 rel = this.animatedPos(butterfly, time).method_1020(camera);
            double distance = rel.method_1033();
            fade *= class_3532.method_15363((float)((float)(((double)(radius + 1.0f) - distance) / 2.0)), (float)0.0f, (float)1.0f);
            if ((fade *= class_3532.method_15363((float)((float)((distance - 0.2) / 0.6)), (float)0.0f, (float)1.0f)) <= 0.005f || !this.frustum.testSphere((float)rel.field_1352, (float)rel.field_1351, (float)rel.field_1350, butterfly.scale * 3.0f)) continue;
            int color = ColorUtil.interpolateColor(colorFrom, colorTo, 0.5f + 0.5f * (float)Math.sin(butterfly.phase));
            float r = (float)(color >> 16 & 0xFF) / 255.0f;
            float g = (float)(color >> 8 & 0xFF) / 255.0f;
            float b = (float)(color & 0xFF) / 255.0f;
            float flap = 0.25f + 0.7f * (0.5f + 0.5f * (float)Math.sin(age * 0.6f + butterfly.phase));
            class_243 axisUp = butterfly.up.method_1021(Math.cos(flap) * (double)butterfly.scale);
            class_243 axisNormal = butterfly.normal.method_1021(Math.sin(flap) * (double)butterfly.scale);
            class_243 axisSide = butterfly.side.method_1021((double)(butterfly.scale * 0.82f));
            float pulse = 0.5f + 0.5f * (float)Math.sin(age * 0.22f + butterfly.phase * 2.0f);
            float pulse3 = pulse * pulse * pulse;
            this.quad(consumer, view, rel, right.method_1021((double)butterfly.scale * 1.9), up.method_1021((double)butterfly.scale * 1.9), 4.0f, 5.0f, r, g, b, fade * (0.2f + 0.18f * pulse3));
            this.quad(consumer, view, rel, axisUp.method_1019(axisNormal), axisSide, 0.0f, 1.0f, r, g, b, fade);
            this.quad(consumer, view, rel, axisUp.method_1021(-1.0).method_1019(axisNormal), axisSide, 0.0f, 1.0f, r, g, b, fade);
            this.quad(consumer, view, rel.method_1019(butterfly.normal.method_1021(0.004)), butterfly.up.method_1021((double)butterfly.scale * 0.085), butterfly.side.method_1021((double)butterfly.scale * 0.65), 2.0f, 3.0f, r, g, b, fade);
            float veins = fade * (0.28f + 0.72f * pulse3);
            if (!(veins > 0.01f)) continue;
            this.wingVeins(consumer, view, rel, butterfly, age, 1.0f, r, g, b, veins);
            this.wingVeins(consumer, view, rel, butterfly, age, -1.0f, r, g, b, veins);
        }
        immediate.method_22993();
    }

    private class_243 animatedPos(Butterfly butterfly, float time) {
        float age = Math.max(0.0f, time - (float)butterfly.spawnTick);
        float life = age / (float)butterfly.lifetime;
        double out = 0.17 + 0.5 * Math.sin(Math.PI * (double)Math.min(1.0f, life));
        double sway = 0.12 * Math.sin((double)age * 0.075 + (double)butterfly.phase);
        return butterfly.anchor.method_1019(butterfly.normal.method_1021(out)).method_1019(butterfly.up.method_1021(sway)).method_1019(butterfly.side.method_1021(sway * 0.55 * Math.cos((double)age * 0.06 + (double)butterfly.phase)));
    }

    private boolean isChunkLoaded(class_2338 pos) {
        return this.world.method_2935().method_12123(pos.method_10263() >> 4, pos.method_10260() >> 4);
    }

    private boolean isInsideSolid(class_243 pos) {
        class_2338 blockPos = class_2338.method_49637((double)pos.field_1352, (double)pos.field_1351, (double)pos.field_1350);
        if (!this.isChunkLoaded(blockPos)) {
            return true;
        }
        class_265 shape = this.world.method_8320(blockPos).method_26218((class_1922)this.world, blockPos);
        if (shape.method_1110()) {
            return false;
        }
        double x = pos.field_1352 - (double)blockPos.method_10263();
        double y = pos.field_1351 - (double)blockPos.method_10264();
        double z = pos.field_1350 - (double)blockPos.method_10260();
        for (class_238 box : shape.method_1090()) {
            if (!(x > box.field_1323) || !(x < box.field_1320) || !(y > box.field_1322) || !(y < box.field_1325) || !(z > box.field_1321) || !(z < box.field_1324)) continue;
            return true;
        }
        return false;
    }

    private boolean spawn(class_243 camera, float radius) {
        int bx = class_3532.method_15357((double)(camera.field_1352 + (this.random.nextDouble() * 2.0 - 1.0) * (double)radius));
        int by = class_3532.method_15357((double)(camera.field_1351 + (this.random.nextDouble() * 2.0 - 1.0) * (double)radius));
        int bz = class_3532.method_15357((double)(camera.field_1350 + (this.random.nextDouble() * 2.0 - 1.0) * (double)radius));
        this.scratch.method_10103(bx, by, bz);
        if (!this.isChunkLoaded((class_2338)this.scratch)) {
            return false;
        }
        class_2680 state = this.world.method_8320((class_2338)this.scratch);
        if (state.method_26215()) {
            return false;
        }
        class_265 shape = state.method_26218((class_1922)this.world, (class_2338)this.scratch);
        if (shape.method_1110()) {
            return false;
        }
        List boxes = shape.method_1090();
        class_238 box = (class_238)boxes.get(this.random.nextInt(boxes.size()));
        class_2350 direction = DIRECTIONS[this.directionCursor++ % DIRECTIONS.length];
        class_243 normal = new class_243((double)direction.method_10148(), (double)direction.method_10164(), (double)direction.method_10165());
        double x = class_3532.method_16436((double)(0.12 + this.random.nextDouble() * 0.76), (double)box.field_1323, (double)box.field_1320);
        double y = class_3532.method_16436((double)(0.12 + this.random.nextDouble() * 0.76), (double)box.field_1322, (double)box.field_1325);
        double z = class_3532.method_16436((double)(0.12 + this.random.nextDouble() * 0.76), (double)box.field_1321, (double)box.field_1324);
        switch (direction) {
            case field_11033: {
                y = box.field_1322;
                break;
            }
            case field_11036: {
                y = box.field_1325;
                break;
            }
            case field_11043: {
                z = box.field_1321;
                break;
            }
            case field_11035: {
                z = box.field_1324;
                break;
            }
            case field_11039: {
                x = box.field_1323;
                break;
            }
            case field_11034: {
                x = box.field_1320;
                break;
            }
        }
        class_243 anchor = new class_243((double)bx + x, (double)by + y, (double)bz + z);
        if (anchor.method_1025(camera) > (double)(radius * radius)) {
            return false;
        }
        if (this.isInsideSolid(anchor.method_1019(normal.method_1021(0.025)))) {
            return false;
        }
        if (this.isInsideSolid(anchor.method_1019(normal.method_1021(0.35)))) {
            return false;
        }
        class_243 tangent = direction.method_10166() == class_2350.class_2351.field_11052 ? new class_243(1.0, 0.0, 0.0) : new class_243(0.0, 1.0, 0.0);
        class_243 across = normal.method_1036(tangent);
        double angle = this.random.nextDouble() * Math.PI * 2.0;
        class_243 up = tangent.method_1021(Math.cos(angle)).method_1019(across.method_1021(Math.sin(angle)));
        class_243 side = normal.method_1036(up);
        this.butterflies.add(new Butterfly(new class_2338(bx, by, bz), state, anchor, normal, up, side, this.tick, Math.round(180.0f * (0.75f + this.random.nextFloat() * 0.5f)), 0.13f + this.random.nextFloat() * 0.1f, this.random.nextFloat() * ((float)Math.PI * 2)));
        return true;
    }

    private void wingVeins(class_4588 consumer, Matrix4f view, class_243 center, Butterfly butterfly, float age, float side, float r, float g, float b, float alpha) {
        float phase = age * 0.18f + butterfly.phase + side * 1.7f;
        double prevX = 0.0;
        double prevY = 0.0;
        double prevZ = 0.0;
        float prevAlpha = 0.0f;
        for (int i = 0; i <= 6; ++i) {
            float t = (float)i / 6.0f;
            float sweep = -1.25f + t * 2.5f;
            float bump = (float)Math.sin((double)t * Math.PI);
            float flutter = (float)Math.sin((float)i * 2.17f + phase) * (float)Math.sin((float)i * 1.43f - phase * 0.7f);
            double along = (double)(side * butterfly.scale) * (0.3 + Math.cos(sweep) + (double)flutter * 0.18 * (double)bump);
            double chord = (double)butterfly.scale * (Math.sin(sweep) * 1.12 + (double)flutter * 0.12 * (double)bump);
            double lift = (double)butterfly.scale * (0.2 + 0.16 * Math.sin(phase + (float)i * 1.9f));
            double px = center.field_1352 + butterfly.up.field_1352 * along + butterfly.side.field_1352 * chord + butterfly.normal.field_1352 * lift;
            double py = center.field_1351 + butterfly.up.field_1351 * along + butterfly.side.field_1351 * chord + butterfly.normal.field_1351 * lift;
            double pz = center.field_1350 + butterfly.up.field_1350 * along + butterfly.side.field_1350 * chord + butterfly.normal.field_1350 * lift;
            float segmentAlpha = side * (0.12f + 0.88f * bump);
            if (i > 0) {
                this.streak(consumer, view, prevX, prevY, prevZ, px, py, pz, butterfly.scale * 0.12f, r, g, b, prevAlpha, segmentAlpha);
            }
            if (i == 3) {
                double ax = px + (butterfly.up.field_1352 * (double)side * 0.45 + butterfly.normal.field_1352 * 0.24) * (double)butterfly.scale;
                double ay = py + (butterfly.up.field_1351 * (double)side * 0.45 + butterfly.normal.field_1351 * 0.24) * (double)butterfly.scale;
                double az = pz + (butterfly.up.field_1350 * (double)side * 0.45 + butterfly.normal.field_1350 * 0.24) * (double)butterfly.scale;
                double qx = ax + (butterfly.up.field_1352 * (double)side * 0.22 + butterfly.normal.field_1352 * 0.4) * (double)butterfly.scale;
                double qy = ay + (butterfly.up.field_1351 * (double)side * 0.22 + butterfly.normal.field_1351 * 0.4) * (double)butterfly.scale;
                double qz = az + (butterfly.up.field_1350 * (double)side * 0.22 + butterfly.normal.field_1350 * 0.4) * (double)butterfly.scale;
                this.streak(consumer, view, px, py, pz, ax, ay, az, butterfly.scale * 0.09f, r, g, b, side * 0.7f, side * 0.4f);
                this.streak(consumer, view, ax, ay, az, qx, qy, qz, butterfly.scale * 0.07f, r, g, b, side * 0.4f, 0.0f);
            }
            prevX = px;
            prevY = py;
            prevZ = pz;
            prevAlpha = segmentAlpha;
        }
    }

    private void streak(class_4588 consumer, Matrix4f view, double ax, double ay, double az, double bx, double by, double bz, double halfWidth, float r, float g, float b, float alphaA, float alphaB) {
        double nx = ay * bz - az * by;
        double ny = az * bx - ax * bz;
        double nz = ax * by - ay * bx;
        double length = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length < 1.0E-12) {
            return;
        }
        double scale = halfWidth / length;
        this.vertex(consumer, view, ax - (nx *= scale), ay - (ny *= scale), az - (nz *= scale), 6.0f, 0.0f, r, g, b, alphaA);
        this.vertex(consumer, view, ax + nx, ay + ny, az + nz, 6.0f, 1.0f, r, g, b, alphaA);
        this.vertex(consumer, view, bx + nx, by + ny, bz + nz, 7.0f, 1.0f, r, g, b, alphaB);
        this.vertex(consumer, view, bx - nx, by - ny, bz - nz, 7.0f, 0.0f, r, g, b, alphaB);
    }

    private void quad(class_4588 consumer, Matrix4f view, class_243 center, class_243 axisU, class_243 axisV, float uMin, float uMax, float r, float g, float b, float a) {
        this.vertex(consumer, view, center.field_1352 - axisU.field_1352 - axisV.field_1352, center.field_1351 - axisU.field_1351 - axisV.field_1351, center.field_1350 - axisU.field_1350 - axisV.field_1350, uMin, 0.0f, r, g, b, a);
        this.vertex(consumer, view, center.field_1352 - axisU.field_1352 + axisV.field_1352, center.field_1351 - axisU.field_1351 + axisV.field_1351, center.field_1350 - axisU.field_1350 + axisV.field_1350, uMin, 1.0f, r, g, b, a);
        this.vertex(consumer, view, center.field_1352 + axisU.field_1352 + axisV.field_1352, center.field_1351 + axisU.field_1351 + axisV.field_1351, center.field_1350 + axisU.field_1350 + axisV.field_1350, uMax, 1.0f, r, g, b, a);
        this.vertex(consumer, view, center.field_1352 + axisU.field_1352 - axisV.field_1352, center.field_1351 + axisU.field_1351 - axisV.field_1351, center.field_1350 + axisU.field_1350 - axisV.field_1350, uMax, 0.0f, r, g, b, a);
    }

    private void vertex(class_4588 consumer, Matrix4f view, double x, double y, double z, float u, float v, float r, float g, float b, float a) {
        consumer.method_22918((Matrix4fc)view, (float)x, (float)y, (float)z).method_22913(u, v).method_22915(r, g, b, a);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Butterfly {
        final class_2338 pos;
        final class_2680 state;
        final class_243 anchor;
        final class_243 normal;
        final class_243 up;
        final class_243 side;
        final int spawnTick;
        final int lifetime;
        final float scale;
        final float phase;
        int fadeTick = -1;

        Butterfly(class_2338 pos, class_2680 state, class_243 anchor, class_243 normal, class_243 up, class_243 side, int spawnTick, int lifetime, float scale, float phase) {
            this.pos = pos;
            this.state = state;
            this.anchor = anchor;
            this.normal = normal;
            this.up = up;
            this.side = side;
            this.spawnTick = spawnTick;
            this.lifetime = lifetime;
            this.scale = scale;
            this.phase = phase;
        }
    }
}

