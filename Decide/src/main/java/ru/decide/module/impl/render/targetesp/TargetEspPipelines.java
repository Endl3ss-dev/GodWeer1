/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_156
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4588
 *  net.minecraft.class_4608
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 */
package ru.decide.module.impl.render.targetesp;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_156;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public final class TargetEspPipelines {
    public static final RenderPipeline ROMB_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation("pipeline/wtextesp").withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color").withSampler("Sampler0").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withCull(false).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).build());
    public static final Function<class_2960, class_1921> ROMB_ESP = class_156.method_34866(texture -> class_1921.method_75940((String)"wtextesp", (class_12247)class_12247.method_75927((RenderPipeline)ROMB_PIPELINE).method_75934("Sampler0", texture).method_75937().method_75929(1536).method_75938()));

    private TargetEspPipelines() {
    }

    public static Quaternionf cameraRotation() {
        return class_310.method_1551().field_1773.method_19418().method_23767();
    }

    public static float cameraYaw() {
        return class_310.method_1551().field_1773.method_19418().method_19330();
    }

    public static class_243 cameraPos() {
        return class_310.method_1551().field_1773.method_19418().method_71156();
    }

    public static void textured(class_4588 consumer, Matrix4f matrix, float x, float y, float z, float u, float v, int color) {
        consumer.method_22918((Matrix4fc)matrix, x, y, z).method_39415(color).method_22913(u, v).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
    }
}

