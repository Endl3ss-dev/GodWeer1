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
import ru.decide.module.impl.render.targetesp.RhombTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.TargetEspPipelines;
import ru.decide.module.impl.render.targetesp.TargetEspRenderContext;

@Environment(value=EnvType.CLIENT)
public final class MarkerTargetEspRenderer {
    private static final class_2960 MARKER_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/features/targetesp/marker.png");

    private MarkerTargetEspRenderer() {
    }

    public static void render(class_4587 stack, class_4597.class_4598 provider, TargetEspRenderContext context, float speed) {
        class_4588 consumer = provider.method_73477(TargetEspPipelines.ROMB_ESP.apply(MARKER_TEXTURE));
        Quaternionf camRot = TargetEspPipelines.cameraRotation();
        stack.method_22903();
        stack.method_46416(0.0f, context.target().method_17682() / 2.0f, 0.0f);
        stack.method_22907((Quaternionfc)camRot);
        float rotation = (float)((long)((float)context.frameTimeMs() * Math.max(0.05f, speed)) % 3600L) / 3600.0f * 360.0f;
        float pulse = 1.0f + class_3532.method_15374((double)((float)(context.frameTimeMs() % 1400L) / 1400.0f * ((float)Math.PI * 2))) * 0.08f;
        stack.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(rotation));
        stack.method_22905(0.65f * pulse, 0.65f * pulse, 1.0f);
        int c1 = RhombTargetEspRenderer.withAlpha(context.primaryColor(), (int)(255.0f * context.alpha()));
        int c2 = RhombTargetEspRenderer.withAlpha(context.secondaryColor(), (int)(255.0f * context.alpha()));
        int c3 = RhombTargetEspRenderer.withAlpha(context.primaryColor(), (int)(215.0f * context.alpha()));
        int c4 = RhombTargetEspRenderer.withAlpha(context.secondaryColor(), (int)(215.0f * context.alpha()));
        Matrix4f entry = stack.method_23760().method_23761();
        TargetEspPipelines.textured(consumer, entry, -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, c1);
        TargetEspPipelines.textured(consumer, entry, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, c2);
        TargetEspPipelines.textured(consumer, entry, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, c3);
        TargetEspPipelines.textured(consumer, entry, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, c4);
        stack.method_22909();
    }
}

