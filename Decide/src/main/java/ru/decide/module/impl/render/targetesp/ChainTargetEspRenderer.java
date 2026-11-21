/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_7833
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 */
package ru.decide.module.impl.render.targetesp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import ru.decide.module.impl.render.targetesp.RhombTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.TargetEspMath;
import ru.decide.module.impl.render.targetesp.TargetEspPipelines;
import ru.decide.module.impl.render.targetesp.TargetEspRenderContext;
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public final class ChainTargetEspRenderer {
    private static final class_2960 CHAIN_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/features/targetesp/chain.png");

    private ChainTargetEspRenderer() {
    }

    public static void render(class_4587 stack, class_4597.class_4598 provider, TargetEspRenderContext context, float speed) {
        class_4588 consumer = provider.method_73477(TargetEspPipelines.ROMB_ESP.apply(CHAIN_TEXTURE));
        float sizeScale = 1.0f;
        float speedScale = Math.max(0.05f, speed);
        float animationTime = ((float)context.target().field_6012 + context.partialTicks()) * speedScale;
        float impact = TargetEspMath.easeOutCubic(context.chainImpactProgress());
        float baseRadius = context.target().method_17681() * 1.35f * sizeScale;
        float bandHeight = Math.max(0.9f, context.target().method_17682() * 0.54f * sizeScale);
        float compressedRadius = baseRadius * (1.0f - impact * 0.18f);
        float primaryBandHeight = bandHeight * (1.0f - impact * 0.08f);
        float secondaryBandHeight = bandHeight * 0.8f * (1.0f - impact * 0.12f);
        float roll = class_3532.method_15374((double)(animationTime * 0.22f)) * 18.0f;
        float pitch = class_3532.method_15362((double)(animationTime * 0.18f)) * 18.0f;
        float spin = animationTime * 5.0f;
        int startColor = ChainTargetEspRenderer.tintForDamage(context.primaryColor(), context.hurtProgress());
        int endColor = ChainTargetEspRenderer.tintForDamage(context.secondaryColor(), context.hurtProgress());
        stack.method_22903();
        stack.method_46416(0.0f, context.target().method_17682() * 0.52f, 0.0f);
        ChainTargetEspRenderer.renderChainBand(stack, consumer, compressedRadius * 0.9f, primaryBandHeight, roll, pitch, spin, context.alpha(), startColor, endColor, 0.0f, speedScale);
        ChainTargetEspRenderer.renderChainBand(stack, consumer, compressedRadius * 0.98f, secondaryBandHeight, -roll, -pitch, -spin * 0.92f, context.alpha() * 0.92f, endColor, startColor, 0.38f, speedScale);
        stack.method_22909();
    }

    private static void renderChainBand(class_4587 stack, class_4588 consumer, float radius, float bandHeight, float roll, float pitch, float spin, float alpha, int startColor, int endColor, float uvOffset, float speedScale) {
        stack.method_22903();
        stack.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(roll));
        stack.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(pitch));
        Matrix4f entry = stack.method_23760().method_23761();
        float bottomY = -bandHeight * 0.5f;
        float topY = bandHeight * 0.5f;
        int segments = 64;
        float uvRepeats = 4.0f;
        for (int i = 0; i < segments; ++i) {
            float progress0 = (float)i / (float)segments;
            float progress1 = (float)(i + 1) / (float)segments;
            float angle0 = progress0 * 360.0f + spin;
            float angle1 = progress1 * 360.0f + spin;
            float rad0 = angle0 * ((float)Math.PI / 180);
            float rad1 = angle1 * ((float)Math.PI / 180);
            float x0 = class_3532.method_15374((double)rad0) * radius;
            float z0 = class_3532.method_15362((double)rad0) * radius;
            float x1 = class_3532.method_15374((double)rad1) * radius;
            float z1 = class_3532.method_15362((double)rad1) * radius;
            float wobble0 = class_3532.method_15374((double)(progress0 * ((float)Math.PI * 2) + spin * 0.05f)) * (0.06f * speedScale);
            float wobble1 = class_3532.method_15374((double)(progress1 * ((float)Math.PI * 2) + spin * 0.05f)) * (0.06f * speedScale);
            int color0 = ChainTargetEspRenderer.scaleAlpha(ChainTargetEspRenderer.lerpColor(startColor, endColor, progress0), alpha);
            int color1 = ChainTargetEspRenderer.scaleAlpha(ChainTargetEspRenderer.lerpColor(startColor, endColor, progress1), alpha);
            float u0 = progress0 * uvRepeats + uvOffset;
            float u1 = progress1 * uvRepeats + uvOffset;
            TargetEspPipelines.textured(consumer, entry, x0, bottomY + wobble0, z0, u0, 0.0f, color0);
            TargetEspPipelines.textured(consumer, entry, x1, bottomY + wobble1, z1, u1, 0.0f, color1);
            TargetEspPipelines.textured(consumer, entry, x1, topY + wobble1, z1, u1, 1.0f, color1);
            TargetEspPipelines.textured(consumer, entry, x0, topY + wobble0, z0, u0, 1.0f, color0);
        }
        stack.method_22909();
    }

    private static int tintForDamage(int color, float hurt) {
        return ColorUtil.interpolateColor(color, -50630, class_3532.method_15363((float)hurt, (float)0.0f, (float)1.0f));
    }

    private static int lerpColor(int a, int b, float f) {
        return ColorUtil.interpolateColor(a, b, class_3532.method_15363((float)f, (float)0.0f, (float)1.0f));
    }

    private static int scaleAlpha(int color, float factor) {
        int a = color >> 24 & 0xFF;
        return RhombTargetEspRenderer.withAlpha(color, Math.round((float)a * factor));
    }
}

