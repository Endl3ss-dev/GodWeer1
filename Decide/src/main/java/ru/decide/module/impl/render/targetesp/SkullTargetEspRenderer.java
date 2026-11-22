/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
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
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import ru.decide.module.impl.render.targetesp.RhombTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.TargetEspMath;
import ru.decide.module.impl.render.targetesp.TargetEspPipelines;
import ru.decide.module.impl.render.targetesp.TargetEspRenderContext;

@Environment(value=EnvType.CLIENT)
public final class SkullTargetEspRenderer {
    private static final class_2960 SKULL_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/features/targetesp/skull.png");

    private SkullTargetEspRenderer() {
    }

    public static void render(class_4587 stack, class_4597.class_4598 provider, TargetEspRenderContext context) {
        class_4588 consumer = provider.method_73477(TargetEspPipelines.ROMB_ESP.apply(SKULL_TEXTURE));
        Quaternionf camRot = TargetEspPipelines.cameraRotation();
        float impact = TargetEspMath.easeOutCubic(context.chainImpactProgress());
        float scale = 0.48f * (1.0f - 0.14f * impact);
        float verticalOffset = context.target().method_17682() * 0.5f;
        float alpha = Math.min(1.0f, context.alpha() * 1.85f);
        int c1 = RhombTargetEspRenderer.withAlpha(context.primaryColor(), (int)(255.0f * alpha));
        int c2 = RhombTargetEspRenderer.withAlpha(context.secondaryColor(), (int)(255.0f * alpha));
        int c3 = RhombTargetEspRenderer.withAlpha(context.primaryColor(), (int)(250.0f * alpha));
        int c4 = RhombTargetEspRenderer.withAlpha(context.secondaryColor(), (int)(250.0f * alpha));
        stack.method_22903();
        stack.method_46416(0.0f, verticalOffset, 0.0f);
        stack.method_22907((Quaternionfc)camRot);
        stack.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(180.0f));
        stack.method_22905(scale, scale, 1.0f);
        Matrix4f entry = stack.method_23760().method_23761();
        TargetEspPipelines.textured(consumer, entry, -0.8f, -0.8f, 0.0f, 0.0f, 0.0f, c1);
        TargetEspPipelines.textured(consumer, entry, -0.8f, 0.8f, 0.0f, 0.0f, 1.0f, c2);
        TargetEspPipelines.textured(consumer, entry, 0.8f, 0.8f, 0.0f, 1.0f, 1.0f, c3);
        TargetEspPipelines.textured(consumer, entry, 0.8f, -0.8f, 0.0f, 1.0f, 0.0f, c4);
        stack.method_22909();
    }
}

