/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1921
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3419
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package ru.decide.utils.render.killeffect.swing;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import ru.decide.utils.render.ModelBoxCapture;
import ru.decide.utils.render.killeffect.swing.SwingKillRender;

@Environment(value=EnvType.CLIENT)
public final class SwingShatterEffect {
    private static final SwingShatterEffect INSTANCE = new SwingShatterEffect();
    private static final int FLASH_MS = 280;
    private static final int SHARD_MS = 2600;
    private static final int HIDE_MS = 1700;
    private static final int MAX_LIGHT = 0xF000F0;
    private static final float GRAVITY = 7.5f;
    private final List<Pending> pending = new ArrayList<Pending>();
    private final List<Shard> shards = new ArrayList<Shard>();
    private final List<Flash> flashes = new ArrayList<Flash>();
    private final Map<Integer, Long> hidden = new ConcurrentHashMap<Integer, Long>();
    private final Map<class_2960, class_1921> layers = new HashMap<class_2960, class_1921>();
    private final Random random = new Random();
    private final class_9799 allocator = new class_9799(65536);
    private class_4597.class_4598 provider;

    private SwingShatterEffect() {
    }

    public static SwingShatterEffect get() {
        return INSTANCE;
    }

    public static boolean shouldHide(class_1297 entity) {
        return entity != null && INSTANCE.isHidden(entity.method_5628());
    }

    public synchronized void trigger(class_1309 victim, float scale, float volume, int color) {
        if (victim == null) {
            return;
        }
        long now = System.currentTimeMillis();
        this.pending.add(new Pending(victim, scale, color, now));
        this.flashes.add(new Flash(victim.method_73189().method_1031(0.0, (double)victim.method_17682() * 0.5, 0.0), color, now));
        this.hidden.put(victim.method_5628(), now + 1700L);
        if (volume > 0.0f) {
            class_310 mc = class_310.method_1551();
            if (mc.field_1687 != null) {
                class_243 pos = victim.method_73189();
                class_2338 at = class_2338.method_49637((double)pos.field_1352, (double)pos.field_1351, (double)pos.field_1350);
                mc.field_1687.method_8396((class_1297)mc.field_1724, at, class_3417.field_15081, class_3419.field_15248, volume * 0.85f, 0.8f);
                mc.field_1687.method_8396((class_1297)mc.field_1724, at, (class_3414)class_3417.field_15075.comp_349(), class_3419.field_15248, volume * 0.6f, 1.25f);
            }
        }
    }

    public synchronized void tick() {
        long now = System.currentTimeMillis();
        this.hidden.entrySet().removeIf(entry -> (Long)entry.getValue() < now);
        this.flashes.removeIf(flash -> now - flash.born >= 280L);
    }

    public synchronized void render(class_4587 matrices, class_243 cam, float partialTick) {
        this.capturePending(partialTick);
        long now = System.currentTimeMillis();
        this.shards.removeIf(shard -> now - shard.born >= (long)shard.life);
        this.flashes.removeIf(flash -> now - flash.born >= 280L);
        this.simulate(now);
        if (this.shards.isEmpty() && this.flashes.isEmpty()) {
            return;
        }
        if (this.provider == null) {
            this.provider = class_4597.method_22991((class_9799)this.allocator);
        }
        Matrix4f mat = matrices.method_23760().method_23761();
        if (!this.flashes.isEmpty()) {
            class_4588 glow = this.provider.method_73477(SwingKillRender.additive());
            for (Flash flash2 : this.flashes) {
                this.drawFlash(glow, mat, cam, flash2, now);
            }
            this.provider.method_22994(SwingKillRender.additive());
        }
        if (this.shards.isEmpty()) {
            return;
        }
        LinkedHashSet<class_1921> used = new LinkedHashSet<class_1921>();
        for (Shard shard2 : this.shards) {
            float alpha;
            long age;
            if (shard2.box.texture() == null || (age = now - shard2.born) < 0L || (alpha = class_3532.method_15363((float)((float)((long)shard2.life - age) / 420.0f), (float)0.0f, (float)1.0f)) <= 0.01f) continue;
            class_1921 layer = this.layerFor(shard2.box.texture());
            class_4588 buf = this.provider.method_73477(layer);
            used.add(layer);
            this.drawShard(buf, matrices, cam, shard2, alpha);
        }
        for (class_1921 layer : used) {
            this.provider.method_22994(layer);
        }
    }

    private void simulate(long now) {
        class_310 mc = class_310.method_1551();
        for (Shard shard : this.shards) {
            if (now < shard.born) continue;
            if (shard.lastMs == 0L) {
                shard.lastMs = now;
            }
            float dt = (float)(now - shard.lastMs) / 1000.0f;
            shard.lastMs = now;
            if (dt <= 0.0f) continue;
            dt = Math.min(dt, 0.05f);
            shard.vy -= (double)(7.5f * dt);
            double nx = shard.x + shard.vx * (double)dt;
            double ny = shard.y + shard.vy * (double)dt;
            double nz = shard.z + shard.vz * (double)dt;
            if (mc.field_1687 != null) {
                double top;
                double half;
                class_2338 groundPos;
                class_2338 bodyPos = class_2338.method_49637((double)nx, (double)ny, (double)nz);
                if (!mc.field_1687.method_8320(bodyPos).method_26215()) {
                    nx = shard.x;
                    nz = shard.z;
                    shard.vx *= -0.15;
                    shard.vz *= -0.15;
                }
                if (shard.vy <= 0.0 && !mc.field_1687.method_8320(groundPos = class_2338.method_49637((double)nx, (double)(ny - (half = (double)shard.halfHeight) - 0.02), (double)nz)).method_26215() && ny - half < (top = (double)groundPos.method_10264() + 1.0)) {
                    ny = top + half;
                    if (Math.abs(shard.vy) > 0.9) {
                        shard.vy = -shard.vy * 0.3;
                        shard.vx *= 0.55;
                        shard.vz *= 0.55;
                    } else {
                        shard.vy = 0.0;
                        shard.vx *= 0.8;
                        shard.vz *= 0.8;
                    }
                }
            }
            shard.x = nx;
            shard.y = ny;
            shard.z = nz;
            if (shard.vy == 0.0) {
                shard.spinSpeed *= 0.985f;
            }
            shard.angle += shard.spinSpeed * dt;
        }
    }

    private void capturePending(float partialTick) {
        if (this.pending.isEmpty()) {
            return;
        }
        ArrayList<Pending> due = new ArrayList<Pending>(this.pending);
        this.pending.clear();
        for (Pending entry : due) {
            List<ModelBoxCapture.Box> boxes;
            class_1309 victim = entry.victim;
            try {
                boxes = ModelBoxCapture.capture(victim, partialTick, false);
            }
            catch (Throwable ignored) {
                continue;
            }
            if (boxes.isEmpty()) continue;
            class_243 origin = new class_243(class_3532.method_16436((double)partialTick, (double)victim.field_6038, (double)victim.method_23317()), class_3532.method_16436((double)partialTick, (double)victim.field_5971, (double)victim.method_23318()), class_3532.method_16436((double)partialTick, (double)victim.field_5989, (double)victim.method_23321()));
            double sx = 0.0;
            double sy = 0.0;
            double sz = 0.0;
            for (ModelBoxCapture.Box box : boxes) {
                sx += (double)box.cx();
                sy += (double)box.cy();
                sz += (double)box.cz();
            }
            int count = Math.max(1, boxes.size());
            class_243 center = new class_243(origin.field_1352 + sx / (double)count, origin.field_1351 + sy / (double)count, origin.field_1350 + sz / (double)count);
            for (ModelBoxCapture.Box box : boxes) {
                if (box.texture() == null) continue;
                class_243 at = new class_243(origin.field_1352 + (double)box.cx(), origin.field_1351 + (double)box.cy(), origin.field_1350 + (double)box.cz());
                class_243 away = at.method_1020(center);
                if (away.method_1027() < 1.0E-5) {
                    away = new class_243(this.random.nextDouble() - 0.5, this.random.nextDouble() * 0.5, this.random.nextDouble() - 0.5);
                }
                away = away.method_1029();
                double speed = (0.6 + this.random.nextDouble() * 1.1) * (double)entry.scale;
                class_243 velocity = away.method_1021(speed).method_1031((this.random.nextDouble() - 0.5) * 0.4, 1.15 + this.random.nextDouble() * 1.1, (this.random.nextDouble() - 0.5) * 0.4);
                Vector3f axis = new Vector3f((float)(this.random.nextDouble() - 0.5), (float)(this.random.nextDouble() - 0.5), (float)(this.random.nextDouble() - 0.5));
                if (axis.lengthSquared() < 1.0E-5f) {
                    axis.set(0.0f, 1.0f, 0.0f);
                }
                axis.normalize();
                float halfHeight = Math.max(0.035f, box.half()[1]);
                this.shards.add(new Shard(box, at, velocity, axis, (float)((this.random.nextDouble() - 0.5) * 640.0), entry.born + (long)this.random.nextInt(180), 2600 + this.random.nextInt(900), halfHeight));
            }
        }
    }

    private void drawShard(class_4588 buf, class_4587 matrices, class_243 cam, Shard shard, float alpha) {
        double x = shard.x - cam.field_1352;
        double y = shard.y - cam.field_1351;
        double z = shard.z - cam.field_1350;
        float angle = (float)Math.toRadians(shard.angle);
        Quaternionf rotation = new Quaternionf().rotationAxis(angle, shard.axis.x, shard.axis.y, shard.axis.z);
        matrices.method_22903();
        matrices.method_22904(x, y, z);
        matrices.method_22907((Quaternionfc)rotation);
        class_4587.class_4665 pose = matrices.method_23760();
        ModelBoxCapture.Box box = shard.box;
        float[] verts = box.verts();
        float[] uvs = box.uvs();
        int[] colors = box.colors();
        int quads = box.quadCount();
        for (int q = 0; q < quads; ++q) {
            int color;
            int n = color = colors.length > q ? colors[q] : -1;
            if (color >>> 24 == 0) {
                color |= 0xFF000000;
            }
            color = SwingKillRender.alpha(color, alpha);
            int o = q * 12;
            float e1y = verts[o + 4] - verts[o + 1];
            float e2z = verts[o + 11] - verts[o + 2];
            float e1z = verts[o + 5] - verts[o + 2];
            float e2y = verts[o + 10] - verts[o + 1];
            float nx = e1y * e2z - e1z * e2y;
            float e2x = verts[o + 9] - verts[o];
            float e1x = verts[o + 3] - verts[o];
            float ny = e1z * e2x - e1x * e2z;
            float nz = e1x * e2y - e1y * e2x;
            float nl = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (nl < 1.0E-6f) {
                nx = 0.0f;
                ny = 1.0f;
                nz = 0.0f;
            } else {
                nx /= nl;
                ny /= nl;
                nz /= nl;
            }
            for (int i = 0; i < 4; ++i) {
                int vi = (q * 4 + i) * 3;
                int ui = (q * 4 + i) * 2;
                if (vi + 2 >= verts.length || ui + 1 >= uvs.length) continue;
                buf.method_56824(pose, verts[vi], verts[vi + 1], verts[vi + 2]).method_39415(color).method_22913(uvs[ui], uvs[ui + 1]).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_60831(pose, nx, ny, nz);
            }
        }
        matrices.method_22909();
    }

    private void drawFlash(class_4588 buf, Matrix4f mat, class_243 cam, Flash flash, long now) {
        float t = class_3532.method_15363((float)((float)(now - flash.born) / 280.0f), (float)0.0f, (float)1.0f);
        float fade = 1.0f - t;
        int rgb = flash.color & 0xFFFFFF;
        double x = flash.pos.field_1352 - cam.field_1352;
        double y = flash.pos.field_1351 - cam.field_1351;
        double z = flash.pos.field_1350 - cam.field_1350;
        SwingKillRender.disc(buf, mat, x, y, z, 1.9f * fade, SwingKillRender.argb(rgb, 0.65f * fade), SwingKillRender.argb(0xFFFFFF, 0.0f), 32);
        SwingKillRender.disc(buf, mat, x, y, z, 0.55f * fade, SwingKillRender.argb(0xFFFFFF, 0.9f * fade), SwingKillRender.argb(rgb, 0.0f), 24);
        SwingKillRender.ring(buf, mat, x, y, z, (0.4f + 2.4f * SwingShatterEffect.smooth(t)) * 0.9f, 0.12f, SwingKillRender.argb(rgb, fade * 0.85f), 36);
        for (int i = 0; i < 12; ++i) {
            float angle = (float)(Math.PI * 2 * (double)i / 12.0);
            double length = 0.5f + 2.2f * SwingShatterEffect.smooth(t);
            SwingKillRender.line(buf, mat, x, y, z, x + Math.cos(angle) * length, y + (double)(i % 3 - 1) * length * 0.18, z + Math.sin(angle) * length, SwingKillRender.argb(i % 2 == 0 ? 0xFFFFFF : rgb, fade * 0.65f), 0.035f);
        }
    }

    private class_1921 layerFor(class_2960 texture) {
        class_1921 cached = this.layers.get(texture);
        if (cached != null) {
            return cached;
        }
        class_12247 setup = class_12247.method_75927((RenderPipeline)class_10799.field_56904).method_75934("Sampler0", texture).method_75937().method_75929(65536).method_75938();
        class_1921 layer = class_1921.method_75940((String)("swing_shatter/" + texture.method_12836() + "/" + texture.method_12832()), (class_12247)setup);
        this.layers.put(texture, layer);
        return layer;
    }

    private boolean isHidden(int id) {
        Long until = this.hidden.get(id);
        if (until == null) {
            return false;
        }
        if (until < System.currentTimeMillis()) {
            this.hidden.remove(id);
            return false;
        }
        return true;
    }

    public synchronized void clear() {
        this.pending.clear();
        this.shards.clear();
        this.flashes.clear();
        this.hidden.clear();
    }

    private static float smooth(float value) {
        float t = class_3532.method_15363((float)value, (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Pending {
        final class_1309 victim;
        final float scale;
        final int color;
        final long born;

        Pending(class_1309 victim, float scale, int color, long born) {
            this.victim = victim;
            this.scale = scale;
            this.color = color;
            this.born = born;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Flash {
        final class_243 pos;
        final int color;
        final long born;

        Flash(class_243 pos, int color, long born) {
            this.pos = pos;
            this.color = color;
            this.born = born;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Shard {
        final ModelBoxCapture.Box box;
        final Vector3f axis;
        final long born;
        final int life;
        final float halfHeight;
        double x;
        double y;
        double z;
        double vx;
        double vy;
        double vz;
        float angle;
        float spinSpeed;
        long lastMs;

        Shard(ModelBoxCapture.Box box, class_243 origin, class_243 velocity, Vector3f axis, float spinSpeed, long born, int life, float halfHeight) {
            this.box = box;
            this.axis = axis;
            this.born = born;
            this.life = life;
            this.halfHeight = halfHeight;
            this.x = origin.field_1352;
            this.y = origin.field_1351;
            this.z = origin.field_1350;
            this.vx = velocity.field_1352;
            this.vy = velocity.field_1351;
            this.vz = velocity.field_1350;
            this.spinSpeed = spinSpeed;
        }
    }
}

