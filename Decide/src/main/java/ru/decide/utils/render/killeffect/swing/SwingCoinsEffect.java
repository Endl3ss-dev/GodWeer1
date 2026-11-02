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
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
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
import net.minecraft.class_2680;
import net.minecraft.class_310;
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
public final class SwingCoinsEffect {
    private static final SwingCoinsEffect INSTANCE = new SwingCoinsEffect();
    private static final int COINS = 16;
    private static final int GEMS = 5;
    private static final int GOLD = -1858757;
    private static final int GOLD_DARK = -6659051;
    private static final int GOLD_LIGHT = -6493;
    private final List<Coin> coins = new ArrayList<Coin>();
    private final Random random = new Random();
    private final class_9799 allocator = new class_9799(32768);
    private class_4597.class_4598 provider;

    private SwingCoinsEffect() {
    }

    public static SwingCoinsEffect get() {
        return INSTANCE;
    }

    public synchronized void trigger(class_1309 victim, float scale, float volume, int color) {
        if (victim == null) {
            return;
        }
        class_243 pos = victim.method_73189();
        double centerY = pos.field_1351 + (double)victim.method_17682() * 0.5;
        for (int i = 0; i < 21; ++i) {
            boolean coin = i < 16;
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            float speed = (0.04f + 0.12f * this.random.nextFloat()) * scale;
            this.coins.add(new Coin(pos.field_1352, centerY, pos.field_1350, Math.cos(angle) * (double)speed, 0.18 + 0.16 * this.random.nextDouble(), Math.sin(angle) * (double)speed, coin, coin ? -1858757 : color, 0.11f + 0.03f * this.random.nextFloat(), 70 + this.random.nextInt(40), this.random.nextFloat() * 360.0f, (this.random.nextFloat() - 0.5f) * 28.0f, this.random.nextFloat() * 6.2832f, scale));
        }
        if (volume > 0.0f) {
            class_310 mc = class_310.method_1551();
            if (mc.field_1687 != null) {
                class_2338 at = class_2338.method_49637((double)pos.field_1352, (double)centerY, (double)pos.field_1350);
                mc.field_1687.method_8396((class_1297)mc.field_1724, at, class_3417.field_15197, class_3419.field_15248, volume * 0.8f, 0.75f);
                mc.field_1687.method_8396((class_1297)mc.field_1724, at, class_3417.field_14627, class_3419.field_15248, volume * 0.9f, 1.35f);
            }
        }
    }

    public synchronized void tick() {
        if (this.coins.isEmpty()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        Iterator<Coin> it = this.coins.iterator();
        while (it.hasNext()) {
            class_2680 below;
            Coin coin = it.next();
            coin.prevX = coin.x;
            coin.prevY = coin.y;
            coin.prevZ = coin.z;
            coin.prevSpin = coin.spin;
            ++coin.age;
            if (coin.age >= coin.life) {
                it.remove();
                continue;
            }
            coin.vy -= 0.022;
            coin.vx *= 0.995;
            coin.vz *= 0.995;
            coin.x += coin.vx;
            coin.y += coin.vy;
            coin.z += coin.vz;
            coin.spin += coin.spinSpeed;
            if (mc.field_1687 == null || (below = mc.field_1687.method_8320(class_2338.method_49637((double)coin.x, (double)(coin.y - 0.06), (double)coin.z))).method_26215()) continue;
            coin.y = Math.floor(coin.y - 0.06) + 1.06;
            if (Math.abs(coin.vy) < 0.015) {
                coin.vy = 0.0;
                coin.vx *= 0.75;
                coin.vz *= 0.75;
                continue;
            }
            coin.vy = -coin.vy * 0.42;
            coin.vx *= 0.65;
            coin.vz *= 0.65;
        }
    }

    public synchronized void render(class_4587 matrices, class_243 cam, float partialTick) {
        if (this.coins.isEmpty()) {
            return;
        }
        if (this.provider == null) {
            this.provider = class_4597.method_22991((class_9799)this.allocator);
        }
        class_4588 buf = this.provider.method_73477(SwingKillRender.solid());
        Matrix4f mat = matrices.method_23760().method_23761();
        for (Coin coin : this.coins) {
            float age = (float)coin.age + partialTick;
            float lifeLeft = 1.0f - class_3532.method_15363((float)(age / (float)coin.life), (float)0.0f, (float)1.0f);
            float alpha = class_3532.method_15363((float)(lifeLeft * 2.2f), (float)0.0f, (float)1.0f);
            if (alpha <= 0.01f) continue;
            if (coin.coin) {
                this.drawCoin(buf, mat, cam, coin, partialTick, alpha);
                continue;
            }
            this.drawGem(buf, mat, cam, coin, partialTick, alpha);
        }
        this.provider.method_22994(SwingKillRender.solid());
    }

    private void drawCoin(class_4588 buf, Matrix4f mat, class_243 cam, Coin coin, float partialTick, float alpha) {
        float p = class_3532.method_15363((float)partialTick, (float)0.0f, (float)1.0f);
        double cx = class_3532.method_16436((double)p, (double)coin.prevX, (double)coin.x) - cam.field_1352;
        double cy = class_3532.method_16436((double)p, (double)coin.prevY, (double)coin.y) - cam.field_1351;
        double cz = class_3532.method_16436((double)p, (double)coin.prevZ, (double)coin.z) - cam.field_1350;
        double spin = Math.toRadians(class_3532.method_16439((float)p, (float)coin.prevSpin, (float)coin.spin));
        double nx = Math.sin(spin);
        double ny = 0.28 * Math.sin(spin * 0.5 + (double)coin.seed);
        double nz = Math.cos(spin);
        double nl = Math.sqrt(nx * nx + ny * ny + nz * nz);
        nx /= nl;
        ny /= nl;
        double ux = nz /= nl;
        double uy = 0.0;
        double uz = -nx;
        double ul = Math.sqrt(ux * ux + uz * uz);
        if (ul < 1.0E-6) {
            ux = 1.0;
            uz = 0.0;
            ul = 1.0;
        }
        double vx = ny * (uz /= ul) - nz * uy;
        double vy = nz * (ux /= ul) - nx * uz;
        double vz = nx * uy - ny * ux;
        float radius = coin.scale;
        float thickness = 0.2f * coin.scale;
        int face = SwingKillRender.argb(-1858757, alpha);
        int rim = SwingKillRender.argb(-6659051, alpha);
        int light = SwingKillRender.argb(-6493, alpha * 0.85f);
        double fx = cx + nx * (double)thickness;
        double fy = cy + ny * (double)thickness;
        double fz = cz + nz * (double)thickness;
        double bx = cx - nx * (double)thickness;
        double by = cy - ny * (double)thickness;
        double bz = cz - nz * (double)thickness;
        int segments = 14;
        for (int i = 0; i < segments; ++i) {
            double a0 = Math.PI * 2 * (double)i / (double)segments;
            double a1 = Math.PI * 2 * (double)(i + 1) / (double)segments;
            double c0 = Math.cos(a0);
            double s0 = Math.sin(a0);
            double c1 = Math.cos(a1);
            double s1 = Math.sin(a1);
            SwingKillRender.quad(buf, mat, fx, fy, fz, fx + (ux * c0 + vx * s0) * (double)radius, fy + (uy * c0 + vy * s0) * (double)radius, fz + (uz * c0 + vz * s0) * (double)radius, fx + (ux * c1 + vx * s1) * (double)radius, fy + (uy * c1 + vy * s1) * (double)radius, fz + (uz * c1 + vz * s1) * (double)radius, fx, fy, fz, face);
            SwingKillRender.quad(buf, mat, bx, by, bz, bx + (ux * c1 + vx * s1) * (double)radius, by + (uy * c1 + vy * s1) * (double)radius, bz + (uz * c1 + vz * s1) * (double)radius, bx + (ux * c0 + vx * s0) * (double)radius, by + (uy * c0 + vy * s0) * (double)radius, bz + (uz * c0 + vz * s0) * (double)radius, bx, by, bz, rim);
            double e0x = (ux * c0 + vx * s0) * (double)radius;
            double e0y = (uy * c0 + vy * s0) * (double)radius;
            double e0z = (uz * c0 + vz * s0) * (double)radius;
            double e1x = (ux * c1 + vx * s1) * (double)radius;
            double e1y = (uy * c1 + vy * s1) * (double)radius;
            double e1z = (uz * c1 + vz * s1) * (double)radius;
            SwingKillRender.quad(buf, mat, fx + e0x, fy + e0y, fz + e0z, bx + e0x, by + e0y, bz + e0z, bx + e1x, by + e1y, bz + e1z, fx + e1x, fy + e1y, fz + e1z, rim);
        }
        SwingKillRender.ring(buf, mat, fx, fy, fz, radius * 0.82f, radius * 0.12f, light, 16);
    }

    private void drawGem(class_4588 buf, Matrix4f mat, class_243 cam, Coin coin, float partialTick, float alpha) {
        float p = class_3532.method_15363((float)partialTick, (float)0.0f, (float)1.0f);
        double cx = class_3532.method_16436((double)p, (double)coin.prevX, (double)coin.x) - cam.field_1352;
        double cy = class_3532.method_16436((double)p, (double)coin.prevY, (double)coin.y) - cam.field_1351;
        double cz = class_3532.method_16436((double)p, (double)coin.prevZ, (double)coin.z) - cam.field_1350;
        double angle = Math.toRadians(class_3532.method_16439((float)p, (float)coin.prevSpin, (float)coin.spin));
        float r = 0.8f * coin.scale;
        float topX = (float)cx;
        float topY = (float)(cy + (double)r);
        float topZ = (float)cz;
        float bottomX = (float)cx;
        float bottomY = (float)(cy - (double)r);
        float bottomZ = (float)cz;
        float[][] equator = new float[4][3];
        for (int i = 0; i < 4; ++i) {
            double a = angle + (double)i * Math.PI / 2.0;
            equator[i][0] = (float)(cx + Math.cos(a) * (double)r);
            equator[i][1] = (float)cy;
            equator[i][2] = (float)(cz + Math.sin(a) * (double)r);
        }
        int base = coin.color & 0xFFFFFF;
        int bright = SwingKillRender.argb(SwingKillRender.brighten(base, 1.35f), alpha);
        int mid = SwingKillRender.argb(base, alpha);
        int dark = SwingKillRender.argb(SwingKillRender.brighten(base, 0.55f), alpha);
        for (int i = 0; i < 4; ++i) {
            float[] a = equator[i];
            float[] b = equator[(i + 1) % 4];
            SwingKillRender.quad(buf, mat, topX, topY, topZ, a[0], a[1], a[2], b[0], b[1], b[2], b[0], b[1], b[2], i % 2 == 0 ? bright : mid);
            SwingKillRender.quad(buf, mat, bottomX, bottomY, bottomZ, b[0], b[1], b[2], a[0], a[1], a[2], a[0], a[1], a[2], i % 2 == 0 ? dark : mid);
        }
        SwingKillRender.line(buf, mat, topX, topY, topZ, bottomX, bottomY, bottomZ, SwingKillRender.argb(0xFFFFFF, alpha * 0.55f), 0.006f * coin.scale);
        SwingKillRender.cross(buf, mat, cx, cy, cz, r * 1.7f, SwingKillRender.argb(0xFFFFFF, alpha * 0.28f));
    }

    public synchronized void clear() {
        this.coins.clear();
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Coin {
        double x;
        double y;
        double z;
        double prevX;
        double prevY;
        double prevZ;
        double vx;
        double vy;
        double vz;
        final boolean coin;
        final int color;
        final float scale;
        final int life;
        final float spinSpeed;
        final float seed;
        float spin;
        float prevSpin;
        int age;

        Coin(double x, double y, double z, double vx, double vy, double vz, boolean coin, int color, float scale, int life, float spin, float spinSpeed, float seed, float overallScale) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.coin = coin;
            this.color = color;
            this.scale = scale * overallScale;
            this.life = life;
            this.spin = spin;
            this.prevSpin = spin;
            this.spinSpeed = spinSpeed;
            this.seed = seed;
        }
    }
}

