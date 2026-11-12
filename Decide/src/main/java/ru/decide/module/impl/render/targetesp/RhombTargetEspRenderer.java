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
 *  org.joml.Quaternionf
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
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import ru.decide.module.impl.render.targetesp.TargetEspPipelines;
import ru.decide.module.impl.render.targetesp.TargetEspRenderContext;

@Environment(value=EnvType.CLIENT)
public final class RhombTargetEspRenderer {
    private static final class_2960 RHOMB_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/world/cube.png");

    private RhombTargetEspRenderer() {
    }

    public static void render(class_4587 stack, class_4597.class_4598 provider, TargetEspRenderContext context, float speed) {
        class_4588 consumer = provider.method_73477(TargetEspPipelines.ROMB_ESP.apply(RHOMB_TEXTURE));
        Quaternionf camRot = TargetEspPipelines.cameraRotation();
        stack.method_22903();
        stack.method_46416(0.0f, context.target().method_17682() / 2.0f, 0.0f);
        stack.method_22907((Quaternionfc)camRot);
        long ms = context.frameTimeMs();
        float timeRotation = (float)(ms % 360000L) / 1000.0f * 90.0f * Math.max(0.05f, speed);
        stack.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(timeRotation));
        stack.method_22905(0.35f, 0.35f, 1.0f);
        int c1 = RhombTargetEspRenderer.withAlpha(context.primaryColor(), (int)(255.0f * context.alpha()));
        int c2 = RhombTargetEspRenderer.withAlpha(context.secondaryColor(), (int)(255.0f * context.alpha()));
        Matrix4f entry = stack.method_23760().method_23761();
        TargetEspPipelines.textured(consumer, entry, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, c2);
        TargetEspPipelines.textured(consumer, entry, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, c1);
        TargetEspPipelines.textured(consumer, entry, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, c2);
        TargetEspPipelines.textured(consumer, entry, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, c1);
        stack.method_22909();
    }

    static int withAlpha(int color, int alpha) {
        return class_3532.method_15340((int)alpha, (int)0, (int)255) << 24 | color & 0xFFFFFF;
    }
}

