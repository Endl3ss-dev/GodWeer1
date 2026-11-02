/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3419
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 */
package ru.decide.utils.render.killeffect.swing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import ru.decide.utils.render.killeffect.swing.SwingKillRender;

@Environment(value=EnvType.CLIENT)
public final class SwingLightningEffect {
    private static final SwingLightningEffect INSTANCE = new SwingLightningEffect();
    private static final int LIFE = 20;
    private static final int SEGMENTS = 22;
    private static final float HEIGHT = 14.0f;
    private static final float JITTER = 0.6f;
    private static final int[] BRANCHES = new int[]{5, 11, 16};
    private static final int BRANCH_SEGMENTS = 7;
    private static final float[] FLICKER = new float[]{1.0f, 0.35f, 1.0f, 0.9f, 0.25f, 0.8f, 0.5f, 0.25f, 0.1f, 0.0f};
    private final List<Bolt> bolts = new ArrayList<Bolt>();
    private final List<Spark> sparks = new ArrayList<Spark>();
    private final Random random = new Random();
    private final class_9799 allocator = new class_9799(32768);
    private final float[] boltX = new float[23];
    private final float[] boltY = new float[23];
    private final float[] boltZ = new float[23];
    private class_4597.class_4598 provider;

    private SwingLightningEffect() {
    }

    public static SwingLightningEffect get() {
        return INSTANCE;
    }

    public synchronized void trigger(class_1309 victim, float scale, float volume, int color) {
        if (victim == null || victim.method_73183() == null) {
            return;
        }
        class_243 pos = victim.method_73189();
        Bolt bolt = new Bolt(pos, scale, color, this.random.nextFloat() * 100.0f);
        this.bolts.add(bolt);
        for (int i = 0; i < 24; ++i) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            float speed = (0.15f + 0.25f * this.random.nextFloat()) * scale;
            this.sparks.add(new Spark(pos.field_1352, pos.field_1351 + 0.1, pos.field_1350, Math.cos(angle) * (double)speed, speed * (0.6f + this.random.nextFloat()), Math.sin(angle) * (double)speed, 10 + this.random.nextInt(10), 0.05f * scale, this.random.nextBoolean() ? color : 0xFFFFFF));
        }
        if (volume > 0.0f) {
            class_310 mc = class_310.method_1551();
            if (mc.field_1687 != null) {
                class_2338 at = class_2338.method_49637((double)pos.field_1352, (double)pos.field_1351, (double)pos.field_1350);
                mc.field_1687.method_8396((class_1297)mc.field_1724, at, class_3417.field_14865, class_3419.field_15248, Math.min(1.0f, volume) * 0.55f, 1.1f + this.random.nextFloat() * 0.2f);
                mc.field_1687.method_8396((class_1297)mc.field_1724, at, (class_3414)class_3417.field_15152.comp_349(), class_3419.field_15248, volume * 0.7f, 1.35f);
            }
        }
    }

    public synchronized void tick() {
        Iterator<Bolt> boltIt = this.bolts.iterator();
        while (boltIt.hasNext()) {
            Bolt bolt = boltIt.next();
            if (++bolt.age < 20) continue;
            boltIt.remove();
        }
        Iterator<Spark> sparkIt = this.sparks.iterator();
        while (sparkIt.hasNext()) {
            Spark spark = sparkIt.next();
            spark.prevX = spark.x;
            spark.prevY = spark.y;
            spark.prevZ = spark.z;
            ++spark.age;
            if (spark.age >= spark.life) {
                sparkIt.remove();
                continue;
            }
            spark.x += spark.vx;
            spark.y += spark.vy;
            spark.z += spark.vz;
            spark.vy -= 0.03;
            spark.vx *= 0.92;
            spark.vy *= 0.92;
            spark.vz *= 0.92;
        }
    }

    public synchronized void render(class_4587 matrices, class_243 cam, float partialTick) {
        if (this.bolts.isEmpty() && this.sparks.isEmpty()) {
            return;
        }
        if (this.provider == null) {
            this.provider = class_4597.method_22991((class_9799)this.allocator);
        }
        class_4588 buf = this.provider.method_73477(SwingKillRender.additive());
        Matrix4f mat = matrices.method_23760().method_23761();
        for (Bolt bolt : this.bolts) {
            this.drawBolt(buf, mat, cam, bolt, partialTick);
        }
        for (Spark spark : this.sparks) {
            float fade = 1.0f - ((float)spark.age + partialTick) / (float)spark.life;
            if (fade <= 0.0f) continue;
            float p = class_3532.method_15363((float)partialTick, (float)0.0f, (float)1.0f);
            double x = class_3532.method_16436((double)p, (double)spark.prevX, (double)spark.x) - cam.field_1352;
            double y = class_3532.method_16436((double)p, (double)spark.prevY, (double)spark.y) - cam.field_1351;
            double z = class_3532.method_16436((double)p, (double)spark.prevZ, (double)spark.z) - cam.field_1350;
            int color = SwingKillRender.argb(spark.color, fade);
            SwingKillRender.line(buf, mat, x, y, z, x - spark.vx * 4.0, y - spark.vy * 4.0, z - spark.vz * 4.0, color, spark.size * (0.4f + fade * 0.6f));
            SwingKillRender.cross(buf, mat, x, y, z, spark.size * 1.6f, SwingKillRender.argb(0xFFFFFF, fade * 0.65f));
        }
        this.provider.method_22994(SwingKillRender.additive());
    }

    private void drawBolt(class_4588 buf, Matrix4f mat, class_243 cam, Bolt bolt, float partialTick) {
        float t = class_3532.method_15363((float)(((float)bolt.age + partialTick) / 20.0f), (float)0.0f, (float)1.0f);
        float fade = (1.0f - t) * (1.0f - t);
        int rgb = bolt.color & 0xFFFFFF;
        float x = (float)(bolt.pos.field_1352 - cam.field_1352);
        float y = (float)(bolt.pos.field_1351 - cam.field_1351);
        float z = (float)(bolt.pos.field_1350 - cam.field_1350);
        SwingKillRender.disc(buf, mat, x, (double)y + 0.03, z, 2.6f * bolt.scale, SwingKillRender.argb(rgb, 0.55f * fade), SwingKillRender.argb(rgb, 0.0f), 36);
        float ringRadius = (0.4f + 2.6f * SwingLightningEffect.smooth(t * 1.5f)) * bolt.scale;
        SwingKillRender.ring(buf, mat, x, (double)y + 0.05, z, ringRadius, 0.16f * bolt.scale, SwingKillRender.argb(rgb, fade), 40);
        float flicker = SwingLightningEffect.flicker((float)bolt.age + partialTick);
        if (flicker <= 0.0f) {
            return;
        }
        float seed = bolt.seed;
        this.shape(seed, bolt.scale);
        for (int layer = 0; layer < 2; ++layer) {
            boolean core = layer == 1;
            int color = core ? SwingKillRender.argb(0xFFFFFF, flicker) : SwingKillRender.argb(rgb, flicker * 0.55f);
            float width = (core ? 0.085f : 0.42f) * bolt.scale;
            float yOffset = y + 0.02f;
            for (int i = 0; i < 22; ++i) {
                SwingKillRender.line(buf, mat, x + this.boltX[i], yOffset + this.boltY[i], z + this.boltZ[i], x + this.boltX[i + 1], yOffset + this.boltY[i + 1], z + this.boltZ[i + 1], color, width);
            }
            for (int branch = 0; branch < BRANCHES.length; ++branch) {
                this.drawBranch(buf, mat, x, yOffset, z, seed, branch, bolt.scale, core ? 0xFFFFFF : rgb, flicker * (core ? 1.0f : 0.55f));
            }
        }
        SwingKillRender.disc(buf, mat, x, (double)y + 0.2, z, 1.35f * bolt.scale, SwingKillRender.argb(0xFFFFFF, flicker * 0.55f), SwingKillRender.argb(rgb, 0.0f), 24);
    }

    private void shape(float seed, float scale) {
        float driftX = (SwingLightningEffect.hash(seed, 101) * 2.0f - 1.0f) * 2.5f * scale;
        float driftZ = (SwingLightningEffect.hash(seed, 102) * 2.0f - 1.0f) * 2.5f * scale;
        float px = 0.0f;
        float pz = 0.0f;
        for (int i = 0; i <= 22; ++i) {
            if (i > 0) {
                px += (SwingLightningEffect.hash(seed, i * 2) * 2.0f - 1.0f) * 0.6f * scale;
                pz += (SwingLightningEffect.hash(seed, i * 2 + 1) * 2.0f - 1.0f) * 0.6f * scale;
            }
            this.boltX[i] = px;
            this.boltZ[i] = pz;
            this.boltY[i] = 14.0f * scale * (1.0f - (float)i / 22.0f);
        }
        float endX = px;
        float endZ = pz;
        int i = 0;
        while (i <= 22) {
            float f = (float)i / 22.0f;
            int n = i;
            this.boltX[n] = this.boltX[n] + (driftX * (1.0f - f) - endX * f);
            int n2 = i++;
            this.boltZ[n2] = this.boltZ[n2] + (driftZ * (1.0f - f) - endZ * f);
        }
    }

    private void drawBranch(class_4588 buf, Matrix4f mat, float x, float y, float z, float seed, int branch, float scale, int rgb, float alpha) {
        int node = BRANCHES[branch];
        float angle = SwingLightningEffect.hash(seed, 200 + branch) * ((float)Math.PI * 2);
        float px = this.boltX[node];
        float py = this.boltY[node];
        float pz = this.boltZ[node];
        for (int strand = 0; strand < 2; ++strand) {
            boolean core = strand == 1;
            float sx = px;
            float sy = py;
            float sz = pz;
            int color = core ? SwingKillRender.argb(0xFFFFFF, alpha) : SwingKillRender.argb(rgb, alpha * 0.5f);
            float width = (core ? 0.045f : 0.22f) * scale;
            for (int i = 0; i < 7; ++i) {
                float falloff = 1.0f - (float)i / 7.0f;
                double a = angle + (SwingLightningEffect.hash(seed, 300 + branch * 20 + i) - 0.5f) * 1.6f;
                float nx = sx + (float)Math.cos(a) * 0.4f * scale;
                float ny = sy - (0.35f + 0.3f * SwingLightningEffect.hash(seed, 400 + branch * 20 + i)) * scale;
                float nz = sz + (float)Math.sin(a) * 0.4f * scale;
                SwingKillRender.line(buf, mat, x + sx, y + sy, z + sz, x + nx, y + ny, z + nz, SwingKillRender.alpha(color, falloff), width * falloff);
                sx = nx;
                sy = ny;
                sz = nz;
            }
        }
    }

    private static float flicker(float age) {
        int index = (int)age;
        if (index >= FLICKER.length - 1) {
            return 0.0f;
        }
        return class_3532.method_16439((float)(age - (float)index), (float)FLICKER[index], (float)FLICKER[index + 1]);
    }

    private static float hash(float seed, int salt) {
        float value = class_3532.method_15374((double)((float)salt * 12.9898f + seed * 78.233f)) * 43758.547f;
        return value - (float)Math.floor(value);
    }

    private static float smooth(float value) {
        float t = class_3532.method_15363((float)value, (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    public synchronized void clear() {
        this.bolts.clear();
        this.sparks.clear();
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Bolt {
        final class_243 pos;
        final float scale;
        final int color;
        final float seed;
        int age;

        Bolt(class_243 pos, float scale, int color, float seed) {
            this.pos = pos;
            this.scale = scale;
            this.color = color;
            this.seed = seed;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Spark {
        double x;
        double y;
        double z;
        double prevX;
        double prevY;
        double prevZ;
        double vx;
        double vy;
        double vz;
        final int life;
        final float size;
        final int color;
        int age;

        Spark(double x, double y, double z, double vx, double vy, double vz, int life, float size, int color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.prevX = x;
            this.prevY = y;
            this.prevZ = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.life = life;
            this.size = size;
            this.color = color;
        }
    }
}

