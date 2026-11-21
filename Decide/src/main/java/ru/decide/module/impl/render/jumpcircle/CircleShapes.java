/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4588
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.decide.module.impl.render.jumpcircle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4588;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public final class CircleShapes {
    private static final float[] SIN_LUT;
    private static final float[] COS_LUT;
    private static final float[] PULSE_LAYER_A;
    private static final float[] PULSE_LEN_M;
    private static final float[] SPIRAL_LAYER_ALPHA;
    private static final float[] ARC_SPEEDS;
    private static final float[] ARC_LEN;
    private static final float[] ARC_R_MULT;
    private static final int[] ORBIT_DOT_COUNTS;
    private static final float[] ORBIT_SPDS;
    private static final int[] ORBIT_DIRS;
    private static final float[] NOVA_LAYER_A;
    private static final float[] NOVA_LAYER_W;
    private static final double TWO_PI_ORBIT_0;
    private static final double TWO_PI_ORBIT_1;
    private static final int SPOKE_SEGS = 10;
    private static final float[] SPOKE_EDGE;
    private static final float[] ARC_GLOW_POW;
    private static final float[] DOT_GLOW_POW;
    private static final float[] CENTER_GLOW_POW;
    private static final float[] HALO_POW;
    private static final float[] ARC_EDGE_LUT;
    private static final float[] NEB_POW;
    private static final float[] PULSE_CENTER_POW;
    private static final int TRAIL_SEGS = 40;
    private static final float[] TRAIL_EDGE;
    private static final float[] TRAIL_ANGLE_OFFSET;
    private static final float[] TRAIL_F;
    private static final float[] TRAIL_ANGLE_OFFSET_B;
    private static final float[] TRAIL_F_B;
    private static final float[] TRAIL_COS_OFFSET;
    private static final float[] TRAIL_SIN_OFFSET;
    private static final float[] TRAIL_COS_OFFSET_B;
    private static final float[] TRAIL_SIN_OFFSET_B;
    private static final float[] SPIRAL_HALO_POW;
    private static final float[] SPIRAL_INNER_POW;
    private static final float[] SPIRAL_TIP_POW;
    private static final float TAIL_COS_DELTA;
    private static final float TAIL_SIN_DELTA;
    private static final int SPIRAL_SEGS = 80;
    private static final float[] SPIRAL_EDGE_POW07;
    private final float[] orbitRsBuf = new float[2];
    private final float[] orbitCos0 = new float[ORBIT_DOT_COUNTS[0]];
    private final float[] orbitSin0 = new float[ORBIT_DOT_COUNTS[0]];
    private final float[] orbitCos1 = new float[ORBIT_DOT_COUNTS[1]];
    private final float[] orbitSin1 = new float[ORBIT_DOT_COUNTS[1]];
    private final double[] arcStartA = new double[ARC_SPEEDS.length];

    public void drawPulse(class_4588 fills, class_4588 lines, Matrix4f mat, float r, float alpha, long elapsed, int theme) {
        int transparent = theme & 0xFFFFFF;
        for (int g = 0; g < 12; ++g) {
            float gf = (float)g / 12.0f;
            float grI = r * (1.0f + gf * 0.7f);
            float grO = r * (1.0f + (gf + 0.083333336f) * 0.7f);
            float ga = alpha * (1.0f - gf) * (1.0f - gf) * 0.18f;
            CircleShapes.batchFilledRing(fills, mat, grI, grO, 48, CircleShapes.col(theme, ga));
        }
        int nebSegs = 52;
        int nebBands = 7;
        double elapsedD = (double)elapsed * 0.0028;
        for (int b = 0; b < nebBands; ++b) {
            float bf = (float)b / (float)nebBands;
            float pulse = (float)(Math.sin(elapsedD + (double)b * 0.95) * 0.5 + 0.5);
            float bandR = r * (0.15f + bf * 0.72f);
            float thick = r * (0.055f + pulse * 0.035f);
            float ba = alpha * NEB_POW[b] * (0.06f + pulse * 0.1f);
            if (!(ba >= 0.004f)) continue;
            CircleShapes.batchFilledRing(fills, mat, bandR - thick, bandR + thick, nebSegs, CircleShapes.col(theme, ba));
        }
        float trailRot = (float)((double)elapsed * 3.5E-4);
        double elapsedTrail = (double)elapsed * 0.003;
        float halfW = r * 0.03f;
        for (int arm = 0; arm < 2; ++arm) {
            float cosBase = (float)Math.cos((double)arm * Math.PI + (double)trailRot);
            float sinBase = (float)Math.sin((double)arm * Math.PI + (double)trailRot);
            for (int seg = 0; seg < 39; ++seg) {
                float edge = TRAIL_EDGE[seg];
                float fA = TRAIL_F[seg];
                float pulse = (float)(Math.sin(elapsedTrail + (double)(fA * 5.0f) + (double)arm * 2.1) * 0.25 + 0.75);
                float sa = alpha * edge * pulse * 0.22f;
                if (sa < 0.004f) continue;
                float coA = cosBase * TRAIL_COS_OFFSET[seg] - sinBase * TRAIL_SIN_OFFSET[seg];
                float siA = sinBase * TRAIL_COS_OFFSET[seg] + cosBase * TRAIL_SIN_OFFSET[seg];
                float coB = cosBase * TRAIL_COS_OFFSET_B[seg] - sinBase * TRAIL_SIN_OFFSET_B[seg];
                float siB = sinBase * TRAIL_COS_OFFSET_B[seg] + cosBase * TRAIL_SIN_OFFSET_B[seg];
                float dxA = -siA;
                float dxB = -siB;
                float fB = TRAIL_F_B[seg];
                float rA = fA * r * 0.88f;
                float rB = fB * r * 0.88f;
                fills.method_22918((Matrix4fc)mat, coA * rA + dxA * halfW, siA * rA + coA * halfW, 0.0f).method_39415(CircleShapes.col(theme, sa));
                fills.method_22918((Matrix4fc)mat, coA * rA - dxA * halfW, siA * rA - coA * halfW, 0.0f).method_39415(CircleShapes.col(theme, sa * 0.15f));
                fills.method_22918((Matrix4fc)mat, coB * rB - dxB * halfW, siB * rB - coB * halfW, 0.0f).method_39415(CircleShapes.col(theme, sa * 0.15f));
                fills.method_22918((Matrix4fc)mat, coB * rB + dxB * halfW, siB * rB + coB * halfW, 0.0f).method_39415(CircleShapes.col(theme, sa));
            }
        }
        float cp = (float)(Math.sin((double)elapsed * 0.005) * 0.5 + 0.5);
        float cpMod = 0.35f + cp * 0.3f;
        for (int g = 0; g < PULSE_CENTER_POW.length; ++g) {
            float gr = 0.006f + (float)g * 0.016f;
            float ga = alpha * PULSE_CENTER_POW[g] * cpMod;
            CircleShapes.batchFilledRingOffset(fills, mat, gr * 0.25f, gr, 20, CircleShapes.col(theme, ga), 0.0f, 0.0f);
        }
        CircleShapes.batchRing(lines, mat, r * 1.12f, 64, CircleShapes.col(theme, alpha * 0.05f));
        CircleShapes.batchRing(lines, mat, r * 1.06f, 64, CircleShapes.col(theme, alpha * 0.14f));
        CircleShapes.batchRing(lines, mat, r * 1.02f, 64, CircleShapes.col(theme, alpha * 0.35f));
        CircleShapes.batchRing(lines, mat, r * 1.0f, 64, CircleShapes.col(theme, alpha * 0.8f));
        CircleShapes.batchRing(lines, mat, r * 0.98f, 64, CircleShapes.col(theme, alpha));
        double rot = (double)elapsed * 6.5E-4;
        double elapsedRay = (double)elapsed * 0.0045;
        for (int i = 0; i < 16; ++i) {
            double angle = 0.39269908169872414 * (double)i + rot;
            float pulse = (float)(Math.sin(elapsedRay + (double)i * 0.6) * 0.5 + 0.5);
            float rLen = r * (0.1f + pulse * 0.35f);
            float rS = r * 1.005f;
            float ca = (float)Math.cos(angle);
            float sa2 = (float)Math.sin(angle);
            float pulsedAlphaBase = 0.2f + pulse * 0.6f;
            for (int l = 0; l < 3; ++l) {
                float la = alpha * pulsedAlphaBase * PULSE_LAYER_A[l];
                lines.method_22918((Matrix4fc)mat, ca * rS, sa2 * rS, 0.0f).method_39415(CircleShapes.col(theme, la));
                lines.method_22918((Matrix4fc)mat, ca * (rS + rLen * PULSE_LEN_M[l]), sa2 * (rS + rLen * PULSE_LEN_M[l]), 0.0f).method_39415(transparent);
            }
        }
        for (int e = 0; e < 3; ++e) {
            float ep = (float)((elapsed + (long)e * 500L) % 1600L) / 1600.0f;
            float er = r * (1.0f + ep * 0.85f);
            float ea = alpha * (1.0f - ep) * (1.0f - ep) * 0.45f;
            if (ea < 0.004f) continue;
            CircleShapes.batchRing(lines, mat, er, 56, CircleShapes.col(theme, ea * 0.4f));
            CircleShapes.batchRing(lines, mat, er * 0.991f, 56, CircleShapes.col(theme, ea * 0.8f));
            CircleShapes.batchRing(lines, mat, er * 0.982f, 56, CircleShapes.col(theme, ea));
        }
    }

    public void drawSpiral(class_4588 fills, class_4588 lines, Matrix4f mat, float r, float alpha, long elapsed, int theme) {
        int gs = 44;
        for (int g = 0; g < SPIRAL_HALO_POW.length; ++g) {
            float gf = (float)g / (float)SPIRAL_HALO_POW.length;
            float gr = r * (1.0f + gf * 0.55f);
            float ga = alpha * SPIRAL_HALO_POW[g] * 0.16f;
            CircleShapes.batchFilledRing(fills, mat, gr * 0.976f, gr, gs, CircleShapes.col(theme, ga));
        }
        for (int i = 0; i < SPIRAL_INNER_POW.length; ++i) {
            float rf = (float)i / (float)SPIRAL_INNER_POW.length;
            float ia = alpha * SPIRAL_INNER_POW[i] * 0.22f;
            CircleShapes.batchFilledRing(fills, mat, r * rf * 0.92f, r * (rf + 0.1f) * 0.92f, gs, CircleShapes.col(theme, ia));
        }
        int spirals = 3;
        for (int s = 0; s < spirals; ++s) {
            double phase = (double)elapsed * 0.0013 * (s % 2 == 0 ? 1.0 : -1.15) + 2.0943951023931953 * (double)s;
            double tipA = 17.27875959474386 + phase;
            float tx = (float)(Math.cos(tipA) * (double)r);
            float ty = (float)(Math.sin(tipA) * (double)r);
            float pulse = (float)(Math.sin((double)elapsed * 0.006 + (double)s * 2.1) * 0.5 + 0.5);
            float pBase = alpha * pulse * 0.75f;
            for (int g = 0; g < SPIRAL_TIP_POW.length; ++g) {
                float gr = 0.015f + (float)g * 0.028f;
                float ga = pBase * SPIRAL_TIP_POW[g];
                CircleShapes.batchFilledRingOffset(fills, mat, gr * 0.4f, gr, 20, CircleShapes.col(theme, ga), tx, ty);
            }
        }
        int segs = 80;
        for (int s = 0; s < spirals; ++s) {
            double dir = s % 2 == 0 ? 1.0 : -1.15;
            double phase = (double)elapsed * 0.0013 * dir + 2.0943951023931953 * (double)s;
            for (int i = 0; i < segs - 1; ++i) {
                float fA = (float)i / (float)segs;
                float fB = (float)(i + 1) / (float)segs;
                float edge = SPIRAL_EDGE_POW07[i];
                float pulse = (float)(Math.sin((double)elapsed * 0.004 + (double)(fA * 8.0f) + (double)s) * 0.06 + 0.94);
                float rA = fA * r * pulse;
                float rB = fB * r * pulse;
                double aA = (double)fA * Math.PI * 5.5 + phase;
                double aB = (double)fB * Math.PI * 5.5 + phase;
                float xA = (float)Math.cos(aA) * rA;
                float yA = (float)Math.sin(aA) * rA;
                float xB = (float)Math.cos(aB) * rB;
                float yB = (float)Math.sin(aB) * rB;
                for (int layer = 0; layer < 3; ++layer) {
                    float sa = alpha * edge * SPIRAL_LAYER_ALPHA[layer] * pulse;
                    if (sa < 0.008f) continue;
                    lines.method_22918((Matrix4fc)mat, xA, yA, 0.0f).method_39415(CircleShapes.col(theme, sa));
                    lines.method_22918((Matrix4fc)mat, xB, yB, 0.0f).method_39415(CircleShapes.col(theme, sa));
                }
            }
        }
        CircleShapes.batchRing(lines, mat, r * 1.1f, 64, CircleShapes.col(theme, alpha * 0.05f));
        CircleShapes.batchRing(lines, mat, r * 1.05f, 64, CircleShapes.col(theme, alpha * 0.15f));
        CircleShapes.batchRing(lines, mat, r * 1.01f, 64, CircleShapes.col(theme, alpha * 0.4f));
        CircleShapes.batchRing(lines, mat, r * 1.0f, 64, CircleShapes.col(theme, alpha * 0.85f));
        CircleShapes.batchRing(lines, mat, r * 0.97f, 64, CircleShapes.col(theme, alpha * 0.5f));
        for (int e = 0; e < 2; ++e) {
            float ep = (float)((elapsed + (long)e * 700L) % 1400L) / 1400.0f;
            float er = r * (1.0f + ep * 0.7f);
            float ea = alpha * (1.0f - ep) * (1.0f - ep) * 0.38f;
            if (ea < 0.004f) continue;
            CircleShapes.batchRing(lines, mat, er, 56, CircleShapes.col(theme, ea * 0.5f));
            CircleShapes.batchRing(lines, mat, er * 0.989f, 56, CircleShapes.col(theme, ea));
        }
    }

    public void drawNova(class_4588 fills, class_4588 lines, Matrix4f mat, float r, float alpha, long elapsed, int theme) {
        double a;
        int i;
        int arcCount = ARC_SPEEDS.length;
        for (int a2 = 0; a2 < arcCount; ++a2) {
            this.arcStartA[a2] = (double)((float)elapsed * ARC_SPEEDS[a2]) + (double)a2 * 1.0471975511965976;
        }
        double orbitBase0 = (float)elapsed * ORBIT_SPDS[0] * (float)ORBIT_DIRS[0];
        double orbitBase1 = (float)elapsed * ORBIT_SPDS[1] * (float)ORBIT_DIRS[1];
        for (i = 0; i < ORBIT_DOT_COUNTS[0]; ++i) {
            a = TWO_PI_ORBIT_0 * (double)i + orbitBase0;
            this.orbitCos0[i] = (float)Math.cos(a);
            this.orbitSin0[i] = (float)Math.sin(a);
        }
        for (i = 0; i < ORBIT_DOT_COUNTS[1]; ++i) {
            a = TWO_PI_ORBIT_1 * (double)i + orbitBase1;
            this.orbitCos1[i] = (float)Math.cos(a);
            this.orbitSin1[i] = (float)Math.sin(a);
        }
        int gs = 52;
        for (int g = 0; g < HALO_POW.length; ++g) {
            float gf = (float)g / (float)HALO_POW.length;
            float gr = r * (1.0f + gf * 0.9f);
            float ga = alpha * HALO_POW[g] * 0.2f;
            CircleShapes.batchFilledRing(fills, mat, gr * 0.976f, gr, gs, CircleShapes.col(theme, ga));
        }
        float webR = r * 0.88f;
        double webRot = (double)elapsed * 8.0E-4;
        for (int sp = 0; sp < 12; ++sp) {
            double angle = 0.5235987755982988 * (double)sp + webRot;
            float pulse3 = (float)(Math.sin((double)elapsed * 0.005 + (double)sp * 0.8) * 0.5 + 0.5);
            float pulseAlpha = 0.08f + pulse3 * 0.12f;
            float ca = (float)Math.cos(angle);
            float si = (float)Math.sin(angle);
            for (int seg = 0; seg < 9; ++seg) {
                float edgeA = SPOKE_EDGE[seg];
                float edgeB = SPOKE_EDGE[seg + 1];
                float sa2 = alpha * edgeA * pulseAlpha;
                float sb = alpha * edgeB * pulseAlpha;
                if (sa2 < 0.004f && sb < 0.004f) continue;
                float fA = (float)seg / 10.0f;
                float fB = (float)(seg + 1) / 10.0f;
                float rA = fA * webR;
                float rB = fB * webR;
                fills.method_22918((Matrix4fc)mat, ca * rA + si * 0.025f, si * rA - ca * 0.025f, 0.0f).method_39415(CircleShapes.col(theme, sa2));
                fills.method_22918((Matrix4fc)mat, ca * rA - si * 0.025f, si * rA + ca * 0.025f, 0.0f).method_39415(CircleShapes.col(theme, sa2));
                fills.method_22918((Matrix4fc)mat, ca * rB - si * 0.025f, si * rB + ca * 0.025f, 0.0f).method_39415(CircleShapes.col(theme, sb));
                fills.method_22918((Matrix4fc)mat, ca * rB + si * 0.025f, si * rB - ca * 0.025f, 0.0f).method_39415(CircleShapes.col(theme, sb));
            }
        }
        for (int wr = 1; wr <= 4; ++wr) {
            float wrf = (float)wr / 4.0f;
            float wRad = webR * wrf;
            float pulse4 = (float)(Math.sin((double)elapsed * 0.004 + (double)wr * 1.2) * 0.5 + 0.5);
            float wa = alpha * (1.0f - wrf * 0.5f) * (0.08f + pulse4 * 0.12f);
            CircleShapes.batchFilledRing(fills, mat, wRad * 0.985f, wRad * 1.015f, 40, CircleShapes.col(theme, wa));
        }
        for (int g = 0; g < CENTER_GLOW_POW.length; ++g) {
            float gr = 0.01f + (float)g * 0.02f;
            float ga = alpha * CENTER_GLOW_POW[g] * 0.65f;
            CircleShapes.batchFilledRingOffset(fills, mat, gr * 0.25f, gr, 20, CircleShapes.col(theme, ga), 0.0f, 0.0f);
        }
        for (int a3 = 0; a3 < arcCount; ++a3) {
            float arcR = r * ARC_R_MULT[a3];
            double tipA = this.arcStartA[a3] + Math.PI * 2 * (double)ARC_LEN[a3];
            float tx = (float)(Math.cos(tipA) * (double)arcR);
            float ty = (float)(Math.sin(tipA) * (double)arcR);
            float pulse = (float)(Math.sin((double)elapsed * 0.007 + (double)a3 * 1.7) * 0.5 + 0.5);
            float pBase = alpha * pulse * 0.85f;
            for (int g = 0; g < ARC_GLOW_POW.length; ++g) {
                float gr = 0.012f + (float)g * 0.022f;
                float ga = pBase * ARC_GLOW_POW[g];
                CircleShapes.batchFilledRingOffset(fills, mat, gr * 0.35f, gr, 16, CircleShapes.col(theme, ga), tx, ty);
            }
        }
        this.orbitRsBuf[0] = r * 1.28f;
        this.orbitRsBuf[1] = r * 1.48f;
        for (int o = 0; o < 2; ++o) {
            int dc = ORBIT_DOT_COUNTS[o];
            float or = this.orbitRsBuf[o];
            float[] oCos = o == 0 ? this.orbitCos0 : this.orbitCos1;
            float[] oSin = o == 0 ? this.orbitSin0 : this.orbitSin1;
            for (int i2 = 0; i2 < dc; ++i2) {
                float dx = oCos[i2] * or;
                float dy = oSin[i2] * or;
                float pulse = (float)(Math.sin((double)elapsed * 0.008 + (double)i2 * 1.3 + (double)o) * 0.5 + 0.5);
                float daB = alpha * (0.3f + pulse * 0.55f) * 0.7f;
                for (int g = 0; g < DOT_GLOW_POW.length; ++g) {
                    float gr = 0.01f + (float)g * 0.018f;
                    float ga = daB * DOT_GLOW_POW[g];
                    CircleShapes.batchFilledRingOffset(fills, mat, gr * 0.3f, gr, 12, CircleShapes.col(theme, ga), dx, dy);
                }
            }
        }
        int arcSegs = 36;
        for (int a4 = 0; a4 < arcCount; ++a4) {
            float arcR = r * ARC_R_MULT[a4];
            int total = (int)((float)arcSegs * ARC_LEN[a4]);
            float pulse = (float)(Math.sin((double)elapsed * 0.005 + (double)a4 * 1.3) * 0.35 + 0.65);
            int totalM1 = total - 1;
            double arcAngleSpan = Math.PI * 2 * (double)ARC_LEN[a4];
            for (int layer = 0; layer < 3; ++layer) {
                float rW = arcR * (1.0f + NOVA_LAYER_W[layer] * 0.012f);
                float layerAlphaPulse = alpha * NOVA_LAYER_A[layer] * pulse;
                for (int seg = 0; seg < totalM1; ++seg) {
                    float edge = ARC_EDGE_LUT[seg * 63 / totalM1];
                    float sa = layerAlphaPulse * edge;
                    if (sa < 0.008f) continue;
                    double aA = this.arcStartA[a4] + (double)seg / (double)total * arcAngleSpan;
                    double aB = this.arcStartA[a4] + (double)(seg + 1) / (double)total * arcAngleSpan;
                    lines.method_22918((Matrix4fc)mat, (float)(Math.cos(aA) * (double)rW), (float)(Math.sin(aA) * (double)rW), 0.0f).method_39415(CircleShapes.col(theme, sa));
                    lines.method_22918((Matrix4fc)mat, (float)(Math.cos(aB) * (double)rW), (float)(Math.sin(aB) * (double)rW), 0.0f).method_39415(CircleShapes.col(theme, sa));
                }
            }
        }
        CircleShapes.batchRing(lines, mat, r * 1.14f, 72, CircleShapes.col(theme, alpha * 0.04f));
        CircleShapes.batchRing(lines, mat, r * 1.08f, 72, CircleShapes.col(theme, alpha * 0.1f));
        CircleShapes.batchRing(lines, mat, r * 1.03f, 72, CircleShapes.col(theme, alpha * 0.25f));
        CircleShapes.batchRing(lines, mat, r * 1.0f, 72, CircleShapes.col(theme, alpha * 0.75f));
        CircleShapes.batchRing(lines, mat, r * 0.98f, 72, CircleShapes.col(theme, alpha));
        CircleShapes.batchRing(lines, mat, r * 0.95f, 72, CircleShapes.col(theme, alpha * 0.4f));
        for (int o = 0; o < 2; ++o) {
            int dc = ORBIT_DOT_COUNTS[o];
            float or = this.orbitRsBuf[o];
            float[] oCos = o == 0 ? this.orbitCos0 : this.orbitCos1;
            float[] oSin = o == 0 ? this.orbitSin0 : this.orbitSin1;
            float cosDelta = TAIL_COS_DELTA;
            float sinDelta = o == 0 ? -TAIL_SIN_DELTA : TAIL_SIN_DELTA;
            for (int i3 = 0; i3 < dc; ++i3) {
                float pulse = (float)(Math.sin((double)elapsed * 0.008 + (double)i3 * 1.3 + (double)o) * 0.5 + 0.5);
                float da = alpha * (0.3f + pulse * 0.55f);
                float px = oCos[i3] * or;
                float py = oSin[i3] * or;
                for (int seg = 1; seg <= 3; ++seg) {
                    float sa = da * (1.0f - (float)seg * 0.28f);
                    float nx = px * cosDelta - py * sinDelta;
                    float ny = px * sinDelta + py * cosDelta;
                    if (sa >= 0.008f) {
                        lines.method_22918((Matrix4fc)mat, px, py, 0.0f).method_39415(CircleShapes.col(theme, sa));
                        lines.method_22918((Matrix4fc)mat, nx, ny, 0.0f).method_39415(CircleShapes.col(theme, sa));
                    }
                    px = nx;
                    py = ny;
                }
            }
        }
        for (int e = 0; e < 3; ++e) {
            float ep = (float)((elapsed + (long)e * 550L) % 1800L) / 1800.0f;
            float er = r * (1.0f + ep);
            float ea = alpha * (1.0f - ep) * (1.0f - ep) * 0.48f;
            if (ea < 0.004f) continue;
            CircleShapes.batchRing(lines, mat, er, 72, CircleShapes.col(theme, ea * 0.3f));
            CircleShapes.batchRing(lines, mat, er * 0.993f, 72, CircleShapes.col(theme, ea * 0.65f));
            CircleShapes.batchRing(lines, mat, er * 0.985f, 72, CircleShapes.col(theme, ea));
        }
    }

    private static void batchRing(class_4588 buf, Matrix4f mat, float r, int segs, int color) {
        float step = 512.0f / (float)segs;
        for (int i = 0; i < segs; ++i) {
            int iA = (int)((float)i * step) & 0x1FF;
            int iB = (int)((float)(i + 1) * step) & 0x1FF;
            buf.method_22918((Matrix4fc)mat, COS_LUT[iA] * r, SIN_LUT[iA] * r, 0.0f).method_39415(color);
            buf.method_22918((Matrix4fc)mat, COS_LUT[iB] * r, SIN_LUT[iB] * r, 0.0f).method_39415(color);
        }
    }

    private static void batchFilledRing(class_4588 buf, Matrix4f mat, float inner, float outer, int segs, int color) {
        CircleShapes.batchFilledRingOffset(buf, mat, inner, outer, segs, color, 0.0f, 0.0f);
    }

    private static void batchFilledRingOffset(class_4588 buf, Matrix4f mat, float inner, float outer, int segs, int color, float cx, float cy) {
        float step = 512.0f / (float)segs;
        for (int i = 0; i < segs; ++i) {
            int iA = (int)((float)i * step) & 0x1FF;
            int iB = (int)((float)(i + 1) * step) & 0x1FF;
            float coA = COS_LUT[iA];
            float siA = SIN_LUT[iA];
            float coB = COS_LUT[iB];
            float siB = SIN_LUT[iB];
            buf.method_22918((Matrix4fc)mat, cx + coA * outer, cy + siA * outer, 0.0f).method_39415(color);
            buf.method_22918((Matrix4fc)mat, cx + coA * inner, cy + siA * inner, 0.0f).method_39415(color);
            buf.method_22918((Matrix4fc)mat, cx + coB * inner, cy + siB * inner, 0.0f).method_39415(color);
            buf.method_22918((Matrix4fc)mat, cx + coB * outer, cy + siB * outer, 0.0f).method_39415(color);
        }
    }

    private static int col(int themeRGB, float a) {
        int ai = Math.max(0, Math.min(255, (int)(a * 255.0f)));
        return themeRGB & 0xFFFFFF | ai << 24;
    }

    public static float easeOutCubic(float x) {
        return 1.0f - (float)Math.pow(1.0f - x, 3.0);
    }

    public static float easeInCubic(float x) {
        return x * x * x;
    }

    static {
        int seg;
        int g;
        int i;
        SIN_LUT = new float[512];
        COS_LUT = new float[512];
        PULSE_LAYER_A = new float[]{0.12f, 0.45f, 1.0f};
        PULSE_LEN_M = new float[]{1.5f, 1.0f, 0.65f};
        SPIRAL_LAYER_ALPHA = new float[]{0.1f, 0.4f, 0.9f};
        ARC_SPEEDS = new float[]{9.0E-4f, -7.0E-4f, 0.0013f, -0.0011f, 6.0E-4f, -0.0015f};
        ARC_LEN = new float[]{0.55f, 0.7f, 0.45f, 0.65f, 0.5f, 0.6f};
        ARC_R_MULT = new float[]{1.0f, 0.96f, 1.03f, 0.98f, 1.01f, 0.95f};
        ORBIT_DOT_COUNTS = new int[]{16, 24};
        ORBIT_SPDS = new float[]{0.001f, 6.0E-4f};
        ORBIT_DIRS = new int[]{1, -1};
        NOVA_LAYER_A = new float[]{0.08f, 0.35f, 0.9f};
        NOVA_LAYER_W = new float[]{3.0f, 1.5f, 0.6f};
        TWO_PI_ORBIT_0 = Math.PI * 2 / (double)ORBIT_DOT_COUNTS[0];
        TWO_PI_ORBIT_1 = Math.PI * 2 / (double)ORBIT_DOT_COUNTS[1];
        SPOKE_EDGE = new float[11];
        ARC_GLOW_POW = new float[8];
        DOT_GLOW_POW = new float[5];
        CENTER_GLOW_POW = new float[10];
        HALO_POW = new float[14];
        ARC_EDGE_LUT = new float[64];
        NEB_POW = new float[7];
        PULSE_CENTER_POW = new float[9];
        TRAIL_EDGE = new float[40];
        TRAIL_ANGLE_OFFSET = new float[40];
        TRAIL_F = new float[40];
        TRAIL_ANGLE_OFFSET_B = new float[40];
        TRAIL_F_B = new float[40];
        TRAIL_COS_OFFSET = new float[40];
        TRAIL_SIN_OFFSET = new float[40];
        TRAIL_COS_OFFSET_B = new float[40];
        TRAIL_SIN_OFFSET_B = new float[40];
        SPIRAL_HALO_POW = new float[10];
        SPIRAL_INNER_POW = new float[10];
        SPIRAL_TIP_POW = new float[7];
        TAIL_COS_DELTA = (float)Math.cos(0.1);
        TAIL_SIN_DELTA = (float)Math.sin(0.1);
        SPIRAL_EDGE_POW07 = new float[80];
        for (i = 0; i < 512; ++i) {
            double a = 0.01227184630308513 * (double)i;
            CircleShapes.SIN_LUT[i] = (float)Math.sin(a);
            CircleShapes.COS_LUT[i] = (float)Math.cos(a);
        }
        for (i = 0; i <= 10; ++i) {
            CircleShapes.SPOKE_EDGE[i] = (float)Math.sin((double)i / 10.0 * Math.PI);
        }
        for (g = 0; g < ARC_GLOW_POW.length; ++g) {
            float v = 1.0f - (float)g / (float)ARC_GLOW_POW.length;
            CircleShapes.ARC_GLOW_POW[g] = v * v;
        }
        for (g = 0; g < DOT_GLOW_POW.length; ++g) {
            float v = 1.0f - (float)g / (float)DOT_GLOW_POW.length;
            CircleShapes.DOT_GLOW_POW[g] = v * v;
        }
        for (g = 0; g < CENTER_GLOW_POW.length; ++g) {
            float v = 1.0f - (float)g / (float)CENTER_GLOW_POW.length;
            CircleShapes.CENTER_GLOW_POW[g] = v * v * v;
        }
        for (g = 0; g < HALO_POW.length; ++g) {
            CircleShapes.HALO_POW[g] = (float)Math.pow(1.0f - (float)g / (float)HALO_POW.length, 2.2f);
        }
        for (i = 0; i < ARC_EDGE_LUT.length; ++i) {
            CircleShapes.ARC_EDGE_LUT[i] = (float)Math.sin((double)i / (double)(ARC_EDGE_LUT.length - 1) * Math.PI);
        }
        for (int b = 0; b < NEB_POW.length; ++b) {
            float v = 1.0f - (float)b / (float)NEB_POW.length;
            CircleShapes.NEB_POW[b] = (float)Math.pow(v, 1.4f);
        }
        for (g = 0; g < PULSE_CENTER_POW.length; ++g) {
            float v = 1.0f - (float)g / (float)PULSE_CENTER_POW.length;
            CircleShapes.PULSE_CENTER_POW[g] = v * v * v;
        }
        for (seg = 0; seg < 40; ++seg) {
            float fA = (float)seg / 40.0f;
            CircleShapes.TRAIL_EDGE[seg] = (float)Math.sin((double)fA * Math.PI);
            CircleShapes.TRAIL_ANGLE_OFFSET[seg] = (float)((double)fA * Math.PI * 1.6);
            CircleShapes.TRAIL_F[seg] = fA;
            float fB = (float)(seg + 1) / 40.0f;
            CircleShapes.TRAIL_ANGLE_OFFSET_B[seg] = (float)((double)fB * Math.PI * 1.6);
            CircleShapes.TRAIL_F_B[seg] = fB;
        }
        for (g = 0; g < SPIRAL_HALO_POW.length; ++g) {
            float v = 1.0f - (float)g / (float)SPIRAL_HALO_POW.length;
            CircleShapes.SPIRAL_HALO_POW[g] = v * v;
        }
        for (g = 0; g < SPIRAL_INNER_POW.length; ++g) {
            float v = 1.0f - (float)g / (float)SPIRAL_INNER_POW.length;
            CircleShapes.SPIRAL_INNER_POW[g] = (float)Math.pow(v, 2.5);
        }
        for (g = 0; g < SPIRAL_TIP_POW.length; ++g) {
            float v = 1.0f - (float)g / (float)SPIRAL_TIP_POW.length;
            CircleShapes.SPIRAL_TIP_POW[g] = v * v;
        }
        for (seg = 0; seg < 40; ++seg) {
            CircleShapes.TRAIL_COS_OFFSET[seg] = (float)Math.cos(TRAIL_ANGLE_OFFSET[seg]);
            CircleShapes.TRAIL_SIN_OFFSET[seg] = (float)Math.sin(TRAIL_ANGLE_OFFSET[seg]);
            CircleShapes.TRAIL_COS_OFFSET_B[seg] = (float)Math.cos(TRAIL_ANGLE_OFFSET_B[seg]);
            CircleShapes.TRAIL_SIN_OFFSET_B[seg] = (float)Math.sin(TRAIL_ANGLE_OFFSET_B[seg]);
        }
        for (i = 0; i < 80; ++i) {
            float fA = (float)i / 80.0f;
            CircleShapes.SPIRAL_EDGE_POW07[i] = (float)Math.pow(Math.sin((double)fA * Math.PI), 0.7f);
        }
    }
}

