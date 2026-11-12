/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1309
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.decide.module.impl.render.targetesp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.decide.module.impl.render.targetesp.DimaEspPipelines;
import ru.decide.module.impl.render.targetesp.RhombTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.TargetEspRenderContext;

@Environment(value=EnvType.CLIENT)
public final class PentagonTargetEspRenderer {
    private static final float \u0422\u044e = 0.01f;
    private static final float \u0422\u0429 = 0.52f;
    private static final float \u0422\u0444 = 3.1f;
    private static final float \u04222 = 0.04f;
    private static final float \u0422\u0438 = 0.68f;
    private static final float \u04229 = 1.14f;
    private static final float \u0422\u043e = 0.15f;
    private static final float \u04227 = 0.035f;
    private static final float \u0422\u0420 = 1.32f;
    private static final float \u04223 = 0.18f;
    private static final float \u0422\u0442 = 0.5f;
    private static final float \u0422\u041e = 0.55f;
    private static final float \u0422\u043c = 0.48f;
    private static final float \u0422\u043a = 0.16f;
    private static final float \u0422\u0418 = 0.95f;
    private static final float \u0422\u0436 = 0.82f;
    private static final float \u0422\u0428 = 34.0f;
    private static final float \u0422\u044b = 9.0f;
    private static final float \u0422\u04382 = 0.7f;
    private static final float \u04226 = 1.28f;
    private static final float \u0422_ = 0.17f;
    private static final float \u041e\u0436 = 0.82f;
    private static final float \u043a\u0410 = 1.3f;
    private static final float I\u0424 = 0.98f;
    private static final float \u041e8 = 1.0E-6f;
    private static final long START_MS = System.currentTimeMillis();
    private static final float SPRITE_WRAP_SCALE = 1.0f;

    private PentagonTargetEspRenderer() {
    }

    private static float easeOut(float s) {
        float k = 1.0f - class_3532.method_15363((float)s, (float)0.0f, (float)1.0f);
        return 1.0f - k * k * k;
    }

    public static void render(class_4587 stack, class_4597.class_4598 immediate, TargetEspRenderContext ctx, float speed) {
        class_1309 target = ctx.target();
        float anim = class_3532.method_15363((float)PentagonTargetEspRenderer.easeOut(ctx.alpha()), (float)0.0f, (float)1.0f);
        if (target == null || anim <= 0.01f) {
            return;
        }
        float sp = Math.max(0.05f, speed);
        float t = (float)(System.currentTimeMillis() - START_MS) / 1000.0f * sp;
        float height = target.method_17682();
        float radiusScale = class_3532.method_16439((float)anim, (float)0.52f, (float)1.0f);
        float pulse = 1.0f + 0.04f * (float)Math.sin(t * 3.1f);
        float s13 = Math.max(0.68f, target.method_17681() * 1.14f + 0.15f) * radiusScale * pulse;
        float hurt = class_3532.method_15363((float)ctx.hurtProgress(), (float)0.0f, (float)1.0f);
        int rgb = ctx.primaryColor() | 0xFF000000;
        if (hurt > 0.0f) {
            int r = class_3532.method_15340((int)Math.round((float)(rgb >> 16 & 0xFF) + 255.0f * hurt), (int)0, (int)255);
            int g = class_3532.method_15340((int)Math.round((float)(rgb >> 8 & 0xFF) * (1.0f - hurt)), (int)0, (int)255);
            int b = class_3532.method_15340((int)Math.round((float)(rgb & 0xFF) * (1.0f - hurt)), (int)0, (int)255);
            rgb = 0xFF000000 | r << 16 | g << 8 | b;
        }
        Matrix4f m = stack.method_23760().method_23761();
        class_4588 buf = immediate.method_73477(DimaEspPipelines.PENTAGRAM_LAYER);
        class_243[] b1 = PentagonTargetEspRenderer.basis(new class_243(0.0, 1.0, 0.0), -((double)(t * 0.55f)));
        int c1 = RhombTargetEspRenderer.withAlpha(PentagonTargetEspRenderer.brighten(rgb, 0.18f), Math.round(anim * 0.5f * 255.0f));
        PentagonTargetEspRenderer.quad(buf, m, new class_243(0.0, (double)0.035f, 0.0), b1, s13 * 1.32f, c1);
        double spin = t * 1.28f + (float)target.method_5628() * 0.17f;
        double tilt = Math.toRadians(34.0 + 9.0 * Math.sin(t * 0.7f));
        class_243 dir = new class_243(Math.sin(tilt) * Math.cos(spin), Math.cos(tilt), Math.sin(tilt) * Math.sin(spin));
        class_243[] b2 = PentagonTargetEspRenderer.basis(dir, t * 0.82f);
        int c2 = RhombTargetEspRenderer.withAlpha(rgb, Math.round(Math.min(1.0f, anim * 1.11f) * 255.0f));
        PentagonTargetEspRenderer.quad(buf, m, new class_243(0.0, (double)(height * 0.48f), 0.0), b2, s13, c2);
    }

    private static class_243[] basis(class_243 n, double roll) {
        class_243 b1;
        class_243 b0;
        class_243 up = new class_243(0.0, 1.0, 0.0).method_1020(n.method_1021(n.field_1351));
        if (up.method_1027() < (double)1.0E-6f) {
            class_243 helper = Math.abs(n.field_1351) > (double)0.98f ? new class_243(1.0, 0.0, 0.0) : new class_243(0.0, 1.0, 0.0);
            b0 = helper.method_1036(n).method_1029();
            b1 = n.method_1036(b0).method_1029();
        } else {
            b0 = up.method_1029();
            b1 = b0.method_1036(n).method_1029();
        }
        double c = Math.cos(roll);
        double s = Math.sin(roll);
        return new class_243[]{b0.method_1021(c).method_1019(b1.method_1021(s)), b0.method_1021(-s).method_1019(b1.method_1021(c))};
    }

    private static void quad(class_4588 buf, Matrix4f m, class_243 c, class_243[] b, float size, int color) {
        float half = size * 1.0f * 1.3f;
        class_243 v0 = c.method_1020(b[0].method_1021((double)half)).method_1020(b[1].method_1021((double)half));
        class_243 v1 = c.method_1020(b[0].method_1021((double)half)).method_1019(b[1].method_1021((double)half));
        class_243 v2 = c.method_1019(b[0].method_1021((double)half)).method_1019(b[1].method_1021((double)half));
        class_243 v3 = c.method_1019(b[0].method_1021((double)half)).method_1020(b[1].method_1021((double)half));
        buf.method_22918((Matrix4fc)m, (float)v0.field_1352, (float)v0.field_1351, (float)v0.field_1350).method_22913(0.0f, 0.0f).method_39415(color);
        buf.method_22918((Matrix4fc)m, (float)v1.field_1352, (float)v1.field_1351, (float)v1.field_1350).method_22913(0.0f, 1.0f).method_39415(color);
        buf.method_22918((Matrix4fc)m, (float)v2.field_1352, (float)v2.field_1351, (float)v2.field_1350).method_22913(1.0f, 1.0f).method_39415(color);
        buf.method_22918((Matrix4fc)m, (float)v3.field_1352, (float)v3.field_1351, (float)v3.field_1350).method_22913(1.0f, 0.0f).method_39415(color);
    }

    private static int brighten(int color, float amount) {
        int r = class_3532.method_15340((int)Math.round((float)(color >> 16 & 0xFF) + 255.0f * amount), (int)0, (int)255);
        int g = class_3532.method_15340((int)Math.round((float)(color >> 8 & 0xFF) + 255.0f * amount), (int)0, (int)255);
        int b = class_3532.method_15340((int)Math.round((float)(color & 0xFF) + 255.0f * amount), (int)0, (int)255);
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
}

