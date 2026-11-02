/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1309
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

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1309;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import ru.decide.module.impl.render.targetesp.RhombTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.TargetEspPipelines;
import ru.decide.module.impl.render.targetesp.TargetEspRenderContext;
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public final class VortexTargetEspRenderer {
    public static final class_2960 TEXTURE_VORTEX = class_2960.method_60655((String)"decide", (String)"textures/features/targetesp/vortex.png");
    public static final class_2960 TEXTURE_BRACKETS = class_2960.method_60655((String)"decide", (String)"textures/features/targetesp/brackets.png");
    private static final int LAYERS = 3;
    private static final long MODULE_START_MS = System.currentTimeMillis();
    private static final float[] rotSpeed = new float[3];
    private static final float[] pitchSpeed = new float[3];
    private static final float[] pitchPhase = new float[3];
    private static final float[] yawBase = new float[3];
    private static class_1309 seededFor;
    private static final Random RANDOM;
    private static float sizeMul;
    private static float speedMul;
    private static boolean redOnHit;
    private static class_2960 activeTexture;

    private VortexTargetEspRenderer() {
    }

    public static void configure(float size, float speed, boolean redHit) {
        VortexTargetEspRenderer.configure(size, speed, redHit, TEXTURE_VORTEX);
    }

    public static void configure(float size, float speed, boolean redHit, class_2960 texture) {
        sizeMul = class_3532.method_15363((float)size, (float)0.3f, (float)3.0f);
        speedMul = class_3532.method_15363((float)speed, (float)0.0f, (float)4.0f);
        redOnHit = redHit;
        activeTexture = texture != null ? texture : TEXTURE_VORTEX;
    }

    public static void render(class_4587 stack, class_4597.class_4598 provider, TargetEspRenderContext context) {
        int color;
        class_1309 target = context.target();
        if (target == null) {
            return;
        }
        if (seededFor != target) {
            VortexTargetEspRenderer.reseed(target);
        }
        float time = (float)(System.currentTimeMillis() - MODULE_START_MS) / 1000.0f;
        float speed = speedMul;
        float height = target.method_17682();
        float half = target.method_17681() * 2.5f * sizeMul;
        float baseY = height * 0.62f - 0.006f;
        float hurt = redOnHit ? class_3532.method_15363((float)context.hurtProgress(), (float)0.0f, (float)1.0f) : 0.0f;
        int baseColor = ColorUtil.interpolateColor(context.primaryColor(), -50630, hurt);
        float[] layerAlpha = new float[3];
        float[] yawDeg = new float[3];
        float[] pitchDeg = new float[3];
        for (int j = 0; j < 3; ++j) {
            float pulse = (class_3532.method_15374((double)(time * speed * 3.4f + (float)j * 2.0943952f)) + 1.0f) * 0.5f;
            layerAlpha[j] = class_3532.method_16439((float)pulse, (float)0.35f, (float)1.0f);
            yawDeg[j] = -time * 140.0f * speed * rotSpeed[j] + yawBase[j];
            pitchDeg[j] = class_3532.method_15374((double)(time * pitchSpeed[j] + pitchPhase[j])) * 30.0f;
        }
        class_2960 tex = activeTexture;
        class_4588 consumer = provider.method_73477(TargetEspPipelines.ROMB_ESP.apply(tex));
        for (int k = 0; k < 3; ++k) {
            color = RhombTargetEspRenderer.withAlpha(baseColor, (int)(255.0f * context.alpha() * layerAlpha[k]));
            VortexTargetEspRenderer.drawLayer(stack, consumer, half, baseY + 0.006f * (float)k, yawDeg[k], pitchDeg[k], color);
        }
        for (int l = 0; l < 3; ++l) {
            color = RhombTargetEspRenderer.withAlpha(baseColor, (int)(255.0f * context.alpha() * layerAlpha[l] * 0.85f));
            VortexTargetEspRenderer.drawLayer(stack, consumer, half, baseY + 0.006f * (float)l, yawDeg[l], pitchDeg[l], color);
        }
    }

    private static void drawLayer(class_4587 stack, class_4588 consumer, float half, float y, float yawDeg, float pitchDeg, int color) {
        stack.method_22903();
        stack.method_46416(0.0f, y, 0.0f);
        stack.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(90.0f + yawDeg));
        stack.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(pitchDeg));
        Matrix4f pose = stack.method_23760().method_23761();
        TargetEspPipelines.textured(consumer, pose, -half, 0.0f, -half, 0.0f, 0.0f, color);
        TargetEspPipelines.textured(consumer, pose, -half, 0.0f, half, 0.0f, 1.0f, color);
        TargetEspPipelines.textured(consumer, pose, half, 0.0f, half, 1.0f, 1.0f, color);
        TargetEspPipelines.textured(consumer, pose, half, 0.0f, -half, 1.0f, 0.0f, color);
        stack.method_22909();
    }

    public static void reset() {
        seededFor = null;
    }

    private static void reseed(class_1309 target) {
        seededFor = target;
        for (int i = 0; i < 3; ++i) {
            VortexTargetEspRenderer.rotSpeed[i] = 0.75f + RANDOM.nextFloat() * 0.5f;
            VortexTargetEspRenderer.pitchSpeed[i] = 0.8f + RANDOM.nextFloat() * 0.8f;
            VortexTargetEspRenderer.pitchPhase[i] = RANDOM.nextFloat() * ((float)Math.PI * 2);
        }
        for (int attempt = 0; attempt < 64; ++attempt) {
            for (int j = 0; j < 3; ++j) {
                VortexTargetEspRenderer.yawBase[j] = -30.0f + RANDOM.nextFloat() * 60.0f;
            }
            boolean ok = true;
            block3: for (int a = 0; a < 3 && ok; ++a) {
                for (int b = a + 1; b < 3; ++b) {
                    if (!(Math.abs(yawBase[a] - yawBase[b]) < 22.0f)) continue;
                    ok = false;
                    continue block3;
                }
            }
            if (!ok) continue;
            return;
        }
        VortexTargetEspRenderer.yawBase[0] = -30.0f + RANDOM.nextFloat() * 4.0f;
        VortexTargetEspRenderer.yawBase[1] = -2.0f + RANDOM.nextFloat() * 4.0f;
        VortexTargetEspRenderer.yawBase[2] = 30.0f - RANDOM.nextFloat() * 4.0f;
    }

    static {
        RANDOM = new Random();
        sizeMul = 1.2f;
        speedMul = 1.0f;
        redOnHit = true;
        activeTexture = TEXTURE_VORTEX;
    }
}

