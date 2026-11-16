/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_2338
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_2680
 *  net.minecraft.class_310
 *  net.minecraft.class_3417
 *  net.minecraft.class_3419
 *  net.minecraft.class_3532
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
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
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.decide.utils.render.killeffect.swing.SwingKillRender;

@Environment(value=EnvType.CLIENT)
public final class SwingGraveEffect {
    private static final SwingGraveEffect INSTANCE = new SwingGraveEffect();
    private static final int LIFE = 110;
    private static final float HEIGHT = 1.5f;
    private static final float WIDTH = 0.85f;
    private static final float THICKNESS = 0.18f;
    private static final int GHOST_START = 12;
    private static final int GHOST_FLIGHT = 55;
    private static final int ARC = 12;
    private static final int[] DIRT_COLORS = new int[]{-11913690, -10730192, -9743816, -8560060};
    private final List<Grave> graves = new ArrayList<Grave>();
    private final List<Dirt> dirt = new ArrayList<Dirt>();
    private final Random random = new Random();
    private final class_9799 allocator = new class_9799(32768);
    private class_4597.class_4598 provider;

    private SwingGraveEffect() {
    }

    public static SwingGraveEffect get() {
        return INSTANCE;
    }

    public synchronized void trigger(class_1309 victim, float scale, float volume, int color) {
        class_3965 hit;
        if (victim == null) {
            return;
        }
        class_243 pos = victim.method_73189();
        double ground = pos.field_1351;
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 != null && (hit = mc.field_1687.method_17742(new class_3959(new class_243(pos.field_1352, pos.field_1351 + 0.5, pos.field_1350), new class_243(pos.field_1352, pos.field_1351 - 24.0, pos.field_1350), class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)mc.field_1724))).method_17783() != class_239.class_240.field_1333) {
            ground = hit.method_17784().field_1351;
        }
        float yaw = mc.field_1724 != null ? (float)Math.atan2(mc.field_1724.method_23317() - pos.field_1352, mc.field_1724.method_23321() - pos.field_1350) : (float)Math.toRadians(victim.method_36454() + 180.0f);
        this.graves.add(new Grave(pos.field_1352, ground, pos.field_1350, yaw, scale, color));
        for (int i = 0; i < 18; ++i) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            float speed = (0.04f + 0.09f * this.random.nextFloat()) * scale;
            this.dirt.add(new Dirt(pos.field_1352 + Math.cos(angle) * 0.25, ground + 0.05, pos.field_1350 + Math.sin(angle) * 0.25, Math.cos(angle) * (double)speed, 0.15 + 0.15 * this.random.nextDouble(), Math.sin(angle) * (double)speed, 40 + this.random.nextInt(40), (0.03f + 0.035f * this.random.nextFloat()) * scale, DIRT_COLORS[this.random.nextInt(DIRT_COLORS.length)]));
        }
        if (volume > 0.0f && mc.field_1687 != null) {
            class_2338 at = class_2338.method_49637((double)pos.field_1352, (double)ground, (double)pos.field_1350);
            mc.field_1687.method_8396((class_1297)mc.field_1724, at, class_3417.field_15211, class_3419.field_15248, volume * 0.7f, 0.8f);
            mc.field_1687.method_8396((class_1297)mc.field_1724, at, class_3417.field_15026, class_3419.field_15248, volume * 0.35f, 0.6f);
        }
    }

    public synchronized void tick() {
        Iterator<Grave> graveIt = this.graves.iterator();
        while (graveIt.hasNext()) {
            if (++graveIt.next().age < 110) continue;
            graveIt.remove();
        }
        if (this.dirt.isEmpty()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        Iterator<Dirt> dirtIt = this.dirt.iterator();
        while (dirtIt.hasNext()) {
            class_2680 below;
            Dirt particle = dirtIt.next();
            ++particle.age;
            if (particle.age >= particle.life) {
                dirtIt.remove();
                continue;
            }
            particle.vy -= 0.022;
            particle.x += particle.vx;
            particle.y += particle.vy;
            particle.z += particle.vz;
            particle.vx *= 0.99;
            particle.vz *= 0.99;
            if (mc.field_1687 == null || (below = mc.field_1687.method_8320(class_2338.method_49637((double)particle.x, (double)(particle.y - 0.04), (double)particle.z))).method_26215() || !(particle.vy < 0.0)) continue;
            particle.y = Math.floor(particle.y - 0.04) + 1.04;
            particle.vy = -particle.vy * 0.35;
            particle.vx *= 0.6;
            particle.vz *= 0.6;
        }
    }

    public synchronized void render(class_4587 matrices, class_243 cam, float partialTick) {
        if (this.graves.isEmpty() && this.dirt.isEmpty()) {
            return;
        }
        if (this.provider == null) {
            this.provider = class_4597.method_22991((class_9799)this.allocator);
        }
        class_4588 buf = this.provider.method_73477(SwingKillRender.solid());
        Matrix4f mat = matrices.method_23760().method_23761();
        for (Grave grave : this.graves) {
            float age = (float)grave.age + partialTick;
            float rise = SwingGraveEffect.smooth(age / 9.0f);
            float sink = SwingGraveEffect.smooth((age - 96.0f) / 14.0f);
            float alpha = class_3532.method_15363((float)rise, (float)0.0f, (float)1.0f) * (1.0f - sink);
            if (alpha <= 0.01f) continue;
            float lift = -(1.0f - rise) * 1.5f * grave.scale - sink * 1.5f * grave.scale;
            this.drawTombstone(buf, mat, cam, grave, lift, alpha);
        }
        if (!this.dirt.isEmpty()) {
            Quaternionf rotation = class_310.method_1551().field_1773.method_19418().method_23767();
            Vector3f right = rotation.transform(new Vector3f(1.0f, 0.0f, 0.0f));
            Vector3f up = rotation.transform(new Vector3f(0.0f, 1.0f, 0.0f));
            for (Dirt particle : this.dirt) {
                float lifeLeft = 1.0f - class_3532.method_15363((float)(((float)particle.age + partialTick) / (float)particle.life), (float)0.0f, (float)1.0f);
                if (lifeLeft <= 0.01f) continue;
                int color = SwingKillRender.alpha(particle.color, lifeLeft);
                SwingGraveEffect.billboardQuad(buf, mat, particle.x - cam.field_1352, particle.y - cam.field_1351, particle.z - cam.field_1350, particle.size, particle.size, right, up, color);
            }
        }
        this.provider.method_22994(SwingKillRender.solid());
        class_4588 glow = this.provider.method_73477(SwingKillRender.additive());
        boolean ghosts = false;
        for (Grave grave : this.graves) {
            float age = (float)grave.age + partialTick;
            float rise = SwingGraveEffect.smooth(age / 9.0f);
            float sink = SwingGraveEffect.smooth((age - 96.0f) / 14.0f);
            float alpha = class_3532.method_15363((float)rise, (float)0.0f, (float)1.0f) * (1.0f - sink);
            if (alpha <= 0.01f) continue;
            ghosts |= this.drawGhost(glow, mat, cam, grave, age, alpha);
        }
        if (ghosts) {
            this.provider.method_22994(SwingKillRender.additive());
        }
    }

    private void drawTombstone(class_4588 buf, Matrix4f mat, class_243 cam, Grave grave, float lift, float alpha) {
        int b;
        int a;
        int next;
        int i;
        int i2;
        float scale = grave.scale;
        float halfWidth = 0.85f * scale * 0.5f;
        float height = 1.5f * scale;
        float radius = halfWidth;
        float straight = height - radius;
        float halfThickness = 0.18f * scale * 0.5f;
        int count = 15;
        double cos = Math.cos(grave.yaw);
        double sin = Math.sin(grave.yaw);
        double[] front = new double[count * 3];
        double[] back = new double[count * 3];
        double[] outlineX = new double[count];
        double[] outlineY = new double[count];
        int k = 0;
        outlineX[k] = -halfWidth;
        outlineY[k++] = 0.0;
        outlineX[k] = -halfWidth;
        outlineY[k++] = straight;
        for (int i3 = 1; i3 < 12; ++i3) {
            double angle = Math.PI - Math.PI * (double)i3 / 12.0;
            outlineX[k] = Math.cos(angle) * (double)radius;
            outlineY[k++] = (double)straight + Math.sin(angle) * (double)radius;
        }
        outlineX[k] = halfWidth;
        outlineY[k++] = straight;
        outlineX[k] = halfWidth;
        outlineY[k] = 0.0;
        double centerLocalX = 0.0;
        double centerLocalY = 0.0;
        for (i2 = 0; i2 < count; ++i2) {
            centerLocalX += outlineX[i2];
            centerLocalY += outlineY[i2];
        }
        centerLocalX /= (double)count;
        centerLocalY /= (double)count;
        for (i2 = 0; i2 < count; ++i2) {
            double lx = outlineX[i2];
            double ly = outlineY[i2] + (double)lift;
            double wx = grave.x + lx * cos + (double)halfThickness * sin;
            double wz = grave.z - lx * sin + (double)halfThickness * cos;
            double bx = grave.x + lx * cos - (double)halfThickness * sin;
            double bz = grave.z - lx * sin - (double)halfThickness * cos;
            front[i2 * 3] = wx - cam.field_1352;
            front[i2 * 3 + 1] = grave.y + ly - cam.field_1351;
            front[i2 * 3 + 2] = wz - cam.field_1350;
            back[i2 * 3] = bx - cam.field_1352;
            back[i2 * 3 + 1] = grave.y + ly - cam.field_1351;
            back[i2 * 3 + 2] = bz - cam.field_1350;
        }
        double centerX = SwingGraveEffect.frontX(grave, centerLocalX, halfThickness) - cam.field_1352;
        double centerY = grave.y + centerLocalY + (double)lift - cam.field_1351;
        double centerZ = SwingGraveEffect.frontZ(grave, centerLocalX, halfThickness) - cam.field_1350;
        double backCenterX = SwingGraveEffect.frontX(grave, centerLocalX, -halfThickness) - cam.field_1352;
        double backCenterZ = SwingGraveEffect.frontZ(grave, centerLocalX, -halfThickness) - cam.field_1350;
        int frontColor = SwingKillRender.argb(0xB8B8B8, alpha);
        int backColor = SwingKillRender.argb(0x707070, alpha);
        int sideColor = SwingKillRender.argb(0x8E8E8E, alpha);
        int darkColor = SwingKillRender.argb(0x3E3E3E, alpha * 0.9f);
        for (i = 0; i < count; ++i) {
            next = (i + 1) % count;
            a = i * 3;
            b = next * 3;
            SwingKillRender.triangle(buf, mat, backCenterX, centerY, backCenterZ, back[b], back[b + 1], back[b + 2], back[a], back[a + 1], back[a + 2], backColor);
        }
        for (i = 0; i < count; ++i) {
            next = (i + 1) % count;
            a = i * 3;
            b = next * 3;
            SwingKillRender.quad(buf, mat, front[a], front[a + 1], front[a + 2], back[a], back[a + 1], back[a + 2], back[b], back[b + 1], back[b + 2], front[b], front[b + 1], front[b + 2], sideColor);
        }
        for (i = 0; i < count; ++i) {
            next = (i + 1) % count;
            a = i * 3;
            b = next * 3;
            SwingKillRender.triangle(buf, mat, centerX, centerY, centerZ, front[a], front[a + 1], front[a + 2], front[b], front[b + 1], front[b + 2], frontColor);
        }
        SwingKillRender.box(buf, mat, grave.x - cam.field_1352, grave.y + (double)(0.09f * scale) - cam.field_1351, grave.z - cam.field_1350, halfWidth * 1.18f, 0.09f * scale, halfThickness * 1.5f, grave.yaw, SwingKillRender.argb(0xA0A0A0, alpha), SwingKillRender.argb(0x7E7E7E, alpha), SwingKillRender.argb(0x5A5A5A, alpha));
        double engrave = halfThickness + 0.006f * scale;
        SwingKillRender.line(buf, mat, SwingGraveEffect.frontX(grave, 0.0, engrave) - cam.field_1352, grave.y + (double)lift + (double)(height * 0.52f) - cam.field_1351, SwingGraveEffect.frontZ(grave, 0.0, engrave) - cam.field_1350, SwingGraveEffect.frontX(grave, 0.0, engrave) - cam.field_1352, grave.y + (double)lift + (double)(height * 0.74f) - cam.field_1351, SwingGraveEffect.frontZ(grave, 0.0, engrave) - cam.field_1350, darkColor, 0.024f * scale);
        SwingKillRender.line(buf, mat, SwingGraveEffect.frontX(grave, (double)(-halfWidth) * 0.22, engrave) - cam.field_1352, grave.y + (double)lift + (double)(height * 0.66f) - cam.field_1351, SwingGraveEffect.frontZ(grave, (double)(-halfWidth) * 0.22, engrave) - cam.field_1350, SwingGraveEffect.frontX(grave, (double)halfWidth * 0.22, engrave) - cam.field_1352, grave.y + (double)lift + (double)(height * 0.66f) - cam.field_1351, SwingGraveEffect.frontZ(grave, (double)halfWidth * 0.22, engrave) - cam.field_1350, darkColor, 0.024f * scale);
        SwingKillRender.line(buf, mat, SwingGraveEffect.frontX(grave, (double)(-halfWidth) * 0.09, engrave) - cam.field_1352, grave.y + (double)lift + (double)(height * 0.26f) - cam.field_1351, SwingGraveEffect.frontZ(grave, (double)(-halfWidth) * 0.09, engrave) - cam.field_1350, SwingGraveEffect.frontX(grave, (double)halfWidth * 0.11, engrave) - cam.field_1352, grave.y + (double)lift + (double)(height * 0.16f) - cam.field_1351, SwingGraveEffect.frontZ(grave, (double)halfWidth * 0.11, engrave) - cam.field_1350, darkColor, 0.018f * scale);
    }

    private static double frontX(Grave grave, double localX, double offset) {
        return grave.x + localX * Math.cos(grave.yaw) + offset * Math.sin(grave.yaw);
    }

    private static double frontZ(Grave grave, double localX, double offset) {
        return grave.z - localX * Math.sin(grave.yaw) + offset * Math.cos(grave.yaw);
    }

    private boolean drawGhost(class_4588 buf, Matrix4f mat, class_243 cam, Grave grave, float age, float alpha) {
        float progress = (age - 12.0f) / 55.0f;
        if (progress <= 0.0f || progress >= 1.0f) {
            return false;
        }
        float scale = grave.scale;
        float ghostAlpha = alpha * SwingGraveEffect.smooth(progress * 5.0f) * (1.0f - SwingGraveEffect.smooth((progress - 0.6f) / 0.4f));
        if (ghostAlpha <= 0.01f) {
            return false;
        }
        float sway = class_3532.method_15374((double)(age * 0.15f)) * 0.15f * scale;
        double frontX = Math.sin(grave.yaw) * (double)0.15f * (double)scale;
        double frontZ = Math.cos(grave.yaw) * (double)0.15f * (double)scale;
        double cx = grave.x + frontX + Math.cos(grave.yaw) * (double)sway - cam.field_1352;
        double cy = grave.y + (double)(1.5f * scale * 0.75f) + (double)(progress * 2.0f * scale) - cam.field_1351;
        double cz = grave.z + frontZ - Math.sin(grave.yaw) * (double)sway - cam.field_1350;
        Quaternionf rotation = class_310.method_1551().field_1773.method_19418().method_23767();
        Vector3f right = rotation.transform(new Vector3f(1.0f, 0.0f, 0.0f));
        Vector3f up = rotation.transform(new Vector3f(0.0f, 1.0f, 0.0f));
        float size = 0.32f * scale;
        int bodyRgb = SwingKillRender.brighten(grave.color & 0xFFFFFF, 1.35f);
        int bodyColor = SwingKillRender.argb(bodyRgb, ghostAlpha * 0.38f);
        int coreColor = SwingKillRender.argb(0xFFFFFF, ghostAlpha * 0.5f);
        int eyeColor = SwingKillRender.argb(0xFFFFFF, ghostAlpha * 0.9f);
        SwingGraveEffect.billboardQuad(buf, mat, cx, cy, cz, size * 0.42f, size * 0.55f, right, up, bodyColor);
        SwingGraveEffect.billboardQuad(buf, mat, cx, cy + (double)(size * 0.55f), cz, size * 0.42f, size * 0.42f, right, up, bodyColor);
        SwingGraveEffect.billboardQuad(buf, mat, cx, cy - (double)(size * 0.42f), cz, size * 0.22f, size * 0.2f, right, up, bodyColor);
        SwingGraveEffect.billboardQuad(buf, mat, cx, cy, cz, size * 0.16f, size * 0.28f, right, up, coreColor);
        SwingGraveEffect.billboardQuad(buf, mat, cx - (double)(size * 0.15f), cy + (double)(size * 0.62f), cz, size * 0.05f, size * 0.07f, right, up, eyeColor);
        SwingGraveEffect.billboardQuad(buf, mat, cx + (double)(size * 0.15f), cy + (double)(size * 0.62f), cz, size * 0.05f, size * 0.07f, right, up, eyeColor);
        return true;
    }

    private static void billboardQuad(class_4588 buf, Matrix4f mat, double cx, double cy, double cz, float halfW, float halfH, Vector3f right, Vector3f up, int color) {
        double rx = right.x() * halfW;
        double ry = right.y() * halfW;
        double rz = right.z() * halfW;
        double ux = up.x() * halfH;
        double uy = up.y() * halfH;
        double uz = up.z() * halfH;
        SwingKillRender.quad(buf, mat, cx - rx - ux, cy - ry - uy, cz - rz - uz, cx + rx - ux, cy + ry - uy, cz + rz - uz, cx + rx + ux, cy + ry + uy, cz + rz + uz, cx - rx + ux, cy - ry + uy, cz - rz + uz, color);
    }

    public synchronized void clear() {
        this.graves.clear();
        this.dirt.clear();
    }

    private static float smooth(float value) {
        float t = class_3532.method_15363((float)value, (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Grave {
        final double x;
        final double y;
        final double z;
        final float yaw;
        final float scale;
        final int color;
        int age;

        Grave(double x, double y, double z, float yaw, float scale, int color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.scale = scale;
            this.color = color;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Dirt {
        double x;
        double y;
        double z;
        double vx;
        double vy;
        double vz;
        final int life;
        final float size;
        final int color;
        int age;

        Dirt(double x, double y, double z, double vx, double vy, double vz, int life, float size, int color) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.life = life;
            this.size = size;
            this.color = color;
        }
    }
}

