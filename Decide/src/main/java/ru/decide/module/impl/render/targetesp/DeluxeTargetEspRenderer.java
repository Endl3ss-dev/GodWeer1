/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1665
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.decide.module.impl.render.targetesp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1665;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.decide.module.impl.render.TargetEsp;
import ru.decide.module.impl.render.targetesp.TargetEspPipelines;
import ru.decide.module.impl.render.targetesp.TargetEspRenderContext;
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public final class DeluxeTargetEspRenderer {
    private static final class_2960 GLOW_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/particles/glow.png");
    private static final class_2960 CHAIN_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/features/targetesp/chain.png");
    private static final int MAX_BOLT_POINTS = 128;
    private final Map<Integer, Float> lightningFade = new HashMap<Integer, Float>();
    private final Map<Integer, Float> chainFade = new HashMap<Integer, Float>();
    private final float[] boltX = new float[128];
    private final float[] boltY = new float[128];
    private final float[] boltZ = new float[128];
    private int boltCount;
    private long lastFrameMs = System.currentTimeMillis();
    private float frameDt;
    private double timeSec;

    public void tick() {
        long now = System.currentTimeMillis();
        this.frameDt = class_3532.method_15363((float)((float)(now - this.lastFrameMs) / 1000.0f), (float)0.0f, (float)0.1f);
        this.lastFrameMs = now;
        this.timeSec += (double)this.frameDt;
    }

    public void clear() {
        this.lightningFade.clear();
        this.chainFade.clear();
        this.lastFrameMs = System.currentTimeMillis();
        this.frameDt = 0.0f;
        this.timeSec = 0.0;
    }

    public void renderLightning(class_4587 stack, class_4597.class_4598 provider, TargetEspRenderContext context, float speed) {
        class_1309 target = context.target();
        if (target == null) {
            return;
        }
        Matrix4f matrix = stack.method_23760().method_23761();
        class_310 mc = class_310.method_1551();
        if (mc.field_1773 == null || mc.field_1773.method_19418() == null) {
            return;
        }
        class_243 cameraPos = mc.field_1773.method_19418().method_71156();
        class_243 center = DeluxeTargetEspRenderer.lerped((class_1297)target, context.partialTicks()).method_1020(cameraPos);
        float height = target.method_17682();
        float width = target.method_17681() * 0.75f + 0.16f;
        float hurt = context.hurtProgress();
        float alpha = context.alpha();
        float rate = Math.max(0.1f, speed);
        class_243[][] threads = new class_243[3][4];
        float[] envelopes = new float[3];
        boolean[] active = new boolean[3];
        for (int thread = 0; thread < 3; ++thread) {
            double cycle = this.timeSec * 1.3 * (double)rate + (double)thread / 3.0;
            int cycleIndex = (int)Math.floor(cycle);
            float phase = (float)(cycle - (double)cycleIndex);
            float envelope = class_3532.method_15363((float)(phase / 0.08f), (float)0.0f, (float)1.0f) * (1.0f - class_3532.method_15363((float)((phase - 0.42f) / 0.3f), (float)0.0f, (float)1.0f)) * class_3532.method_15363((float)(0.85f + 0.15f * DeluxeTargetEspRenderer.smoothNoise(thread, 900, cycleIndex, phase)), (float)0.0f, (float)1.0f) * alpha;
            if (envelope < 0.004f) continue;
            float baseAngle = DeluxeTargetEspRenderer.noise(thread, 11, cycleIndex) * (float)Math.PI;
            float span = 0.6f + 0.5f * Math.abs(DeluxeTargetEspRenderer.noise(thread, 12, cycleIndex));
            float direction = DeluxeTargetEspRenderer.noise(thread, 13, cycleIndex) >= 0.0f ? 1.0f : -1.0f;
            float top = height * (0.85f + 0.05f * DeluxeTargetEspRenderer.noise(thread, 14, cycleIndex));
            float vertical = height * 0.67f;
            float radius = width * (1.0f + 0.12f * DeluxeTargetEspRenderer.noise(thread, 15, cycleIndex));
            float wobble = height * 0.16f * class_3532.method_15374((double)((float)this.timeSec * 1.7f + (float)thread * 2.1f));
            for (int i = 0; i <= 3; ++i) {
                float t = (float)i / 3.0f;
                float angle = baseAngle + (float)this.timeSec * 0.9f + direction * span * ((float)Math.PI * 2) * t;
                threads[thread][i] = center.method_1031((double)(class_3532.method_15362((double)angle) * radius), (double)(top - vertical * t + wobble), (double)(class_3532.method_15374((double)angle) * radius));
            }
            envelopes[thread] = envelope;
            active[thread] = true;
        }
        class_4588 ribbon = provider.method_73477(TargetEsp.RING_FILL_LAYER);
        for (int thread = 0; thread < 3; ++thread) {
            if (!active[thread]) continue;
            for (int segment = 0; segment < 3; ++segment) {
                int color = DeluxeTargetEspRenderer.themeColor((float)thread * 90.0f + (float)segment * 24.0f, hurt, envelopes[thread]);
                this.buildBolt(threads[thread][segment], threads[thread][segment + 1], 4, (thread * 131 + segment) * 17 | 1);
                this.emitBoltRibbon(matrix, ribbon, 0.011f, color);
            }
        }
        class_4588 glow = provider.method_73477(TargetEspPipelines.ROMB_ESP.apply(GLOW_TEXTURE));
        for (int thread = 0; thread < 3; ++thread) {
            if (!active[thread]) continue;
            for (int segment = 0; segment < 3; ++segment) {
                int color = DeluxeTargetEspRenderer.themeColor((float)thread * 90.0f + (float)segment * 24.0f, hurt, envelopes[thread]);
                int glowColor = ColorUtil.replAlpha(color, (int)(class_3532.method_15363((float)envelopes[thread], (float)0.0f, (float)1.0f) * 0.13f * 255.0f));
                this.buildBolt(threads[thread][segment], threads[thread][segment + 1], 4, (thread * 131 + segment) * 17 | 1);
                this.emitBoltGlow(stack, glow, 0.0605f, glowColor);
            }
        }
    }

    public void renderProjectileLightning(class_4587 stack, class_4597.class_4598 provider, float partialTicks, float speed) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        Matrix4f matrix = stack.method_23760().method_23761();
        class_243 cameraPos = mc.field_1773.method_19418().method_71156();
        class_243 hand = DeluxeTargetEspRenderer.handPosition((class_1309)mc.field_1724, cameraPos, partialTicks);
        float rate = Math.max(0.1f, speed);
        this.lightningFade.keySet().removeIf(id -> mc.field_1687.method_8469(id.intValue()) == null);
        ArrayList<GlowMark> glows = new ArrayList<GlowMark>();
        class_4588 ribbon = provider.method_73477(TargetEsp.RING_FILL_LAYER);
        for (class_1297 entity : mc.field_1687.method_18112()) {
            class_243 up;
            class_243 point;
            double distance;
            boolean flying;
            class_1665 projectile;
            if (!(entity instanceof class_1665) || (projectile = (class_1665)entity).method_24921() != mc.field_1724) continue;
            boolean bl = flying = !projectile.method_24828() && projectile.method_18798().method_1027() > 1.0E-5;
            float fade = this.updateFade(this.lightningFade, projectile.method_5628(), flying);
            if (fade < 0.004f || (distance = (point = DeluxeTargetEspRenderer.lerped((class_1297)projectile, partialTicks).method_1020(cameraPos)).method_1022(hand)) < 1.0) continue;
            class_243 direction = point.method_1020(hand).method_1029();
            class_243 side = direction.method_1036(up = Math.abs(direction.field_1351) < 0.9 ? new class_243(0.0, 1.0, 0.0) : new class_243(1.0, 0.0, 0.0));
            if (side.method_1027() < 1.0E-8) continue;
            side = side.method_1029();
            class_243 other = direction.method_1036(side).method_1029();
            int segments = class_3532.method_15340((int)((int)Math.ceil(distance / 2.2)), (int)2, (int)24);
            class_243 previous = hand;
            for (int i = 1; i <= segments; ++i) {
                float t = (float)i / (float)segments;
                float arc = class_3532.method_15374((double)(t * (float)Math.PI));
                float wave = t * 7.0f + (float)this.timeSec * 4.0f * rate;
                double amplitude = Math.min(0.3, distance * 0.06) * (double)arc;
                class_243 current = hand.method_1019(point.method_1020(hand).method_1021((double)t)).method_1019(side.method_1021((double)class_3532.method_15374((double)wave) * amplitude)).method_1019(other.method_1021((double)class_3532.method_15362((double)wave) * amplitude)).method_1031(0.0, -Math.min(0.6, distance * 0.035) * (double)arc, 0.0);
                float pulse = class_3532.method_15363((float)(0.5f + 0.5f * DeluxeTargetEspRenderer.smoothNoise(projectile.method_5628(), 900, (int)(this.timeSec * 10.0), t)), (float)0.0f, (float)1.0f);
                int base = ColorUtil.interpolateColor(ColorUtil.getClientColor1(1), -1, pulse * 0.7f);
                int color = ColorUtil.replAlpha(base, (int)(255.0f * fade));
                DeluxeTargetEspRenderer.ribbonSegment(matrix, ribbon, previous, current, 0.009f, color);
                if ((i & 1) == 0) {
                    glows.add(new GlowMark(current, 0.05f + 0.02f * pulse, ColorUtil.replAlpha(base, (int)(255.0f * fade * 0.22f))));
                }
                previous = current;
            }
            glows.add(new GlowMark(point, 0.067f + 0.02f * class_3532.method_15374((double)((float)this.timeSec * 9.0f + (float)projectile.method_5628())), DeluxeTargetEspRenderer.themeColor(0.0f, 0.0f, 0.45f * fade)));
        }
        class_4588 glow = provider.method_73477(TargetEspPipelines.ROMB_ESP.apply(GLOW_TEXTURE));
        for (GlowMark mark : glows) {
            DeluxeTargetEspRenderer.drawGlow(stack, glow, mark.position(), mark.size(), mark.color());
        }
    }

    public void renderProjectileChains(class_4587 stack, class_4597.class_4598 provider, float partialTicks) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        class_4588 chain = provider.method_73477(TargetEspPipelines.ROMB_ESP.apply(CHAIN_TEXTURE));
        Matrix4f matrix = stack.method_23760().method_23761();
        class_243 cameraPos = mc.field_1773.method_19418().method_71156();
        class_243 hand = DeluxeTargetEspRenderer.handPosition((class_1309)mc.field_1724, cameraPos, partialTicks);
        this.chainFade.keySet().removeIf(id -> mc.field_1687.method_8469(id.intValue()) == null);
        for (class_1297 entity : mc.field_1687.method_18112()) {
            class_243 point;
            double distance;
            boolean flying;
            class_1665 projectile;
            if (!(entity instanceof class_1665) || (projectile = (class_1665)entity).method_24921() != mc.field_1724) continue;
            boolean bl = flying = !projectile.method_24828() && projectile.method_18798().method_1027() > 1.0E-5;
            float fade = this.updateFade(this.chainFade, projectile.method_5628(), flying);
            if (fade < 0.004f || (distance = (point = DeluxeTargetEspRenderer.lerped((class_1297)projectile, partialTicks).method_1020(cameraPos)).method_1022(hand)) < 1.0) continue;
            int steps = 16;
            double sag = distance * 0.06;
            class_243 previous = null;
            for (int i = 0; i <= steps; ++i) {
                float t = (float)i / (float)steps;
                class_243 current = hand.method_1019(point.method_1020(hand).method_1021((double)t)).method_1031(0.0, -sag * 4.0 * (double)t * (1.0 - (double)t), 0.0);
                if (previous != null) {
                    int color = DeluxeTargetEspRenderer.themeColor((float)i * 16.0f, 0.0f, fade);
                    float u0 = (float)(i - 1) / (float)steps * (float)distance / 1.3333f;
                    float u1 = (float)i / (float)steps * (float)distance / 1.3333f;
                    DeluxeTargetEspRenderer.chainSegment(matrix, chain, previous, current, 0.32f, u0, u1, color);
                }
                previous = current;
            }
        }
    }

    private void emitBoltRibbon(Matrix4f matrix, class_4588 ribbon, float halfWidth, int color) {
        if (this.boltCount < 2) {
            return;
        }
        for (int i = 0; i < this.boltCount - 1; ++i) {
            class_243 a = new class_243((double)this.boltX[i], (double)this.boltY[i], (double)this.boltZ[i]);
            class_243 b = new class_243((double)this.boltX[i + 1], (double)this.boltY[i + 1], (double)this.boltZ[i + 1]);
            DeluxeTargetEspRenderer.ribbonSegment(matrix, ribbon, a, b, halfWidth, color);
        }
    }

    private void emitBoltGlow(class_4587 stack, class_4588 glow, float size, int color) {
        for (int i = 0; i < this.boltCount; i += 2) {
            class_243 point = new class_243((double)this.boltX[i], (double)this.boltY[i], (double)this.boltZ[i]);
            DeluxeTargetEspRenderer.drawGlow(stack, glow, point, size, color);
        }
    }

    private void buildBolt(class_243 from, class_243 to, int depth, int seed) {
        this.boltCount = 0;
        this.pushPoint(from.field_1352, from.field_1351, from.field_1350);
        this.subdivide(from.field_1352, from.field_1351, from.field_1350, to.field_1352, to.field_1351, to.field_1350, depth, seed, 0);
    }

    private void subdivide(double ax, double ay, double az, double bx, double by, double bz, int depth, int seed, int level) {
        double s2z;
        double s2y;
        double s2x;
        double s2Length;
        double uz;
        double uy;
        double ux;
        if (this.boltCount >= 127) {
            return;
        }
        if (depth <= 0) {
            this.pushPoint(bx, by, bz);
            return;
        }
        double dx = bx - ax;
        double dy = by - ay;
        double dz = bz - az;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1.0E-6) {
            this.pushPoint(bx, by, bz);
            return;
        }
        dx /= length;
        dy /= length;
        dz /= length;
        if (Math.abs(dy) < 0.9) {
            ux = 0.0;
            uy = 1.0;
            uz = 0.0;
        } else {
            ux = 1.0;
            uy = 0.0;
            uz = 0.0;
        }
        double s1x = dy * uz - dz * uy;
        double s1y = dz * ux - dx * uz;
        double s1z = dx * uy - dy * ux;
        double s1Length = Math.sqrt(s1x * s1x + s1y * s1y + s1z * s1z);
        if (s1Length < 1.0E-6) {
            s1Length = 1.0;
        }
        if ((s2Length = Math.sqrt((s2x = dy * (s1z /= s1Length) - dz * (s1y /= s1Length)) * s2x + (s2y = dz * (s1x /= s1Length) - dx * s1z) * s2y + (s2z = dx * s1y - dy * s1x) * s2z)) < 1.0E-6) {
            s2Length = 1.0;
        }
        s2x /= s2Length;
        s2y /= s2Length;
        s2z /= s2Length;
        double amplitude = length * 0.28;
        double offset1 = (double)DeluxeTargetEspRenderer.noise(seed, level, 1) * amplitude;
        double offset2 = (double)DeluxeTargetEspRenderer.noise(seed, level, 2) * amplitude;
        double mx = (ax + bx) * 0.5 + s1x * offset1 + s2x * offset2;
        double my = (ay + by) * 0.5 + s1y * offset1 + s2y * offset2;
        double mz = (az + bz) * 0.5 + s1z * offset1 + s2z * offset2;
        this.subdivide(ax, ay, az, mx, my, mz, depth - 1, seed * 2 + 7, level + 1);
        this.subdivide(mx, my, mz, bx, by, bz, depth - 1, seed * 2 + 9, level + 1);
    }

    private void pushPoint(double x, double y, double z) {
        if (this.boltCount >= 128) {
            return;
        }
        this.boltX[this.boltCount] = (float)x;
        this.boltY[this.boltCount] = (float)y;
        this.boltZ[this.boltCount] = (float)z;
        ++this.boltCount;
    }

    private static void ribbonSegment(Matrix4f matrix, class_4588 consumer, class_243 a, class_243 b, float halfWidth, int color) {
        class_243 tangent = b.method_1020(a);
        if (tangent.method_1027() < 1.0E-10) {
            return;
        }
        class_243 view = a.method_1019(b).method_1021(0.5);
        class_243 side = tangent.method_1036(view);
        if (side.method_1027() < 1.0E-10) {
            return;
        }
        side = side.method_1029().method_1021((double)halfWidth);
        consumer.method_22918((Matrix4fc)matrix, (float)(a.field_1352 - side.field_1352), (float)(a.field_1351 - side.field_1351), (float)(a.field_1350 - side.field_1350)).method_39415(color);
        consumer.method_22918((Matrix4fc)matrix, (float)(b.field_1352 - side.field_1352), (float)(b.field_1351 - side.field_1351), (float)(b.field_1350 - side.field_1350)).method_39415(color);
        consumer.method_22918((Matrix4fc)matrix, (float)(b.field_1352 + side.field_1352), (float)(b.field_1351 + side.field_1351), (float)(b.field_1350 + side.field_1350)).method_39415(color);
        consumer.method_22918((Matrix4fc)matrix, (float)(a.field_1352 + side.field_1352), (float)(a.field_1351 + side.field_1351), (float)(a.field_1350 + side.field_1350)).method_39415(color);
    }

    private static void chainSegment(Matrix4f matrix, class_4588 consumer, class_243 a, class_243 b, float halfWidth, float u0, float u1, int color) {
        class_243 tangent = b.method_1020(a);
        if (tangent.method_1027() < 1.0E-10) {
            return;
        }
        class_243 view = a.method_1019(b).method_1021(0.5);
        class_243 side = tangent.method_1036(view);
        if (side.method_1027() < 1.0E-10) {
            return;
        }
        side = side.method_1029().method_1021((double)halfWidth);
        consumer.method_22918((Matrix4fc)matrix, (float)(a.field_1352 - side.field_1352), (float)(a.field_1351 - side.field_1351), (float)(a.field_1350 - side.field_1350)).method_22913(u0, 0.0f).method_39415(color).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        consumer.method_22918((Matrix4fc)matrix, (float)(b.field_1352 - side.field_1352), (float)(b.field_1351 - side.field_1351), (float)(b.field_1350 - side.field_1350)).method_22913(u1, 0.0f).method_39415(color).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        consumer.method_22918((Matrix4fc)matrix, (float)(b.field_1352 + side.field_1352), (float)(b.field_1351 + side.field_1351), (float)(b.field_1350 + side.field_1350)).method_22913(u1, 1.0f).method_39415(color).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        consumer.method_22918((Matrix4fc)matrix, (float)(a.field_1352 + side.field_1352), (float)(a.field_1351 + side.field_1351), (float)(a.field_1350 + side.field_1350)).method_22913(u0, 1.0f).method_39415(color).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
    }

    private static void drawGlow(class_4587 stack, class_4588 glow, class_243 position, float size, int color) {
        stack.method_22903();
        stack.method_46416((float)position.field_1352, (float)position.field_1351, (float)position.field_1350);
        stack.method_22907((Quaternionfc)TargetEspPipelines.cameraRotation());
        Matrix4f matrix = stack.method_23760().method_23761();
        TargetEspPipelines.textured(glow, matrix, -size, -size, 0.0f, 0.0f, 0.0f, color);
        TargetEspPipelines.textured(glow, matrix, -size, size, 0.0f, 0.0f, 1.0f, color);
        TargetEspPipelines.textured(glow, matrix, size, size, 0.0f, 1.0f, 1.0f, color);
        TargetEspPipelines.textured(glow, matrix, size, -size, 0.0f, 1.0f, 0.0f, color);
        stack.method_22909();
    }

    private static class_243 handPosition(class_1309 player, class_243 cameraPos, float partialTicks) {
        return new class_243(class_3532.method_16436((double)partialTicks, (double)player.field_6038, (double)player.method_23317()) - cameraPos.field_1352, class_3532.method_16436((double)partialTicks, (double)player.field_5971, (double)player.method_23318()) + (double)(player.method_5751() * 0.75f) - cameraPos.field_1351, class_3532.method_16436((double)partialTicks, (double)player.field_5989, (double)player.method_23321()) - cameraPos.field_1350);
    }

    private static class_243 lerped(class_1297 entity, float partialTicks) {
        return new class_243(class_3532.method_16436((double)partialTicks, (double)entity.field_6038, (double)entity.method_23317()), class_3532.method_16436((double)partialTicks, (double)entity.field_5971, (double)entity.method_23318()), class_3532.method_16436((double)partialTicks, (double)entity.field_5989, (double)entity.method_23321()));
    }

    private float updateFade(Map<Integer, Float> map, int id, boolean flying) {
        float value = map.getOrDefault(id, Float.valueOf(0.0f)).floatValue();
        value += flying ? this.frameDt / 0.15f : -this.frameDt / 0.3f;
        if ((value = class_3532.method_15363((float)value, (float)0.0f, (float)1.0f)) <= 0.0f) {
            map.remove(id);
        } else {
            map.put(id, Float.valueOf(value));
        }
        return value;
    }

    private static int themeColor(float angle, float hurt, float alpha) {
        int first = ColorUtil.getClientColor1((int)angle);
        int mixed = ColorUtil.interpolateColor(first, -44976, class_3532.method_15363((float)hurt, (float)0.0f, (float)1.0f));
        return ColorUtil.replAlpha(mixed, (int)(class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f) * 255.0f));
    }

    private static float noise(int a, int b, int c) {
        int value = a * 668265261 + b * 374761393 + c * -1640531535;
        value ^= value >>> 15;
        value *= -2048144789;
        value ^= value >>> 13;
        return (float)(value >>> 8) / 8388608.0f - 1.0f;
    }

    private static float smoothNoise(int a, int b, int c, float t) {
        float smooth = t * t * (3.0f - 2.0f * t);
        return class_3532.method_16439((float)smooth, (float)DeluxeTargetEspRenderer.noise(a, b, c), (float)DeluxeTargetEspRenderer.noise(a, b, c + 1));
    }

    @Environment(value=EnvType.CLIENT)
    private record GlowMark(class_243 position, float size, int color) {
    }
}

