/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_7833
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.decide.module.impl.render.targetesp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.decide.module.impl.render.TargetEsp;
import ru.decide.module.impl.render.targetesp.TargetEspRenderContext;
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public class CrossTargetEspRenderer {
    private static final int CROSS_COUNT = 3;
    private static final float CROSS_SIZE = 0.2f;
    private static final double CROSS_RADIUS = 1.4;
    private static final float ORBIT_SPEED = 90.0f;
    private static final float SELF_SPIN_SPEED = 135.0f;

    public static void render(class_4587 matrices, class_4597.class_4598 immediate, TargetEspRenderContext ctx, float speedMultiplier) {
        if (ctx.target() == null) {
            return;
        }
        double timeSec = (double)(ctx.frameTimeMs() % 100000L) / 1000.0;
        float orbitAngle = (float)(timeSec * 90.0 * (double)speedMultiplier % 360.0);
        float selfSpin = (float)(timeSec * 135.0 * (double)speedMultiplier % 360.0);
        double angleStep = 2.0943951023931953;
        double entityRadius = Math.max(0.2, (double)ctx.target().method_17681() / 2.0);
        double entityHeight = Math.max(0.2, (double)ctx.target().method_17682());
        double currentRadius = entityRadius * 1.4 * 1.5;
        double yOffset = entityHeight * 0.5;
        float thickness = 0.025f;
        float alpha = ctx.alpha();
        int primaryColor = ctx.primaryColor();
        class_4588 fillBuf = immediate.method_73477(TargetEsp.RING_FILL_LAYER);
        for (int i = 0; i < 3; ++i) {
            double angle = Math.toRadians(orbitAngle) + (double)i * angleStep;
            double x = Math.cos(angle) * currentRadius;
            double z = Math.sin(angle) * currentRadius;
            matrices.method_22903();
            matrices.method_22904(x, yOffset, z);
            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(selfSpin));
            Matrix4f matrix = matrices.method_23760().method_23761();
            CrossTargetEspRenderer.drawFillBox(fillBuf, matrix, -thickness, -0.2f, -thickness, thickness, 0.3f, thickness, primaryColor, alpha);
            CrossTargetEspRenderer.drawFillBox(fillBuf, matrix, -0.16000001f, 0.07f, -thickness, 0.16000001f, 0.07f + thickness * 2.0f, thickness, primaryColor, alpha);
            matrices.method_22909();
        }
    }

    private static void drawFillBox(class_4588 buf, Matrix4f m, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int color, float alpha) {
        int c = ColorUtil.replAlpha(color, (int)(class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f) * 255.0f));
        buf.method_22918((Matrix4fc)m, minX, maxY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, maxY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, maxY, maxZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, minX, maxY, maxZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, minX, minY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, minX, minY, maxZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, minY, maxZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, minY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, minX, minY, maxZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, minY, maxZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, maxY, maxZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, minX, maxY, maxZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, minY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, minX, minY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, minX, maxY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, maxY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, minX, minY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, minX, minY, maxZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, minX, maxY, maxZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, minX, maxY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, minY, maxZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, minY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, maxY, minZ).method_39415(c);
        buf.method_22918((Matrix4fc)m, maxX, maxY, maxZ).method_39415(c);
    }
}

