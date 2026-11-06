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
 *  net.minecraft.class_12247
 *  net.minecraft.class_1921
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_4588
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.decide.module.impl.render.targetesp;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_4588;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public final class DimaEspPipelines {
    private static final String SHADER_ROOT = "core/targetesp/";
    public static final RenderPipeline PENTAGRAM_PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(class_2960.method_60655((String)"decide", (String)"targetesp_dima_pentagram")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withVertexShader(class_2960.method_60655((String)"decide", (String)"core/targetesp/target_esp_pentagram")).withFragmentShader(class_2960.method_60655((String)"decide", (String)"core/targetesp/target_esp_pentagram")).withBlend(BlendFunction.ADDITIVE).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withCull(false).withDepthWrite(false).build();
    public static final class_1921 PENTAGRAM_LAYER = class_1921.method_75940((String)"targetesp_dima_pentagram", (class_12247)class_12247.method_75927((RenderPipeline)PENTAGRAM_PIPELINE).method_75937().method_75929(4096).method_75938());

    public static void fill(class_4588 consumer, Matrix4f matrix, float x0, float y0, float x1, float y1, float z, int color) {
        consumer.method_22918((Matrix4fc)matrix, x0, y0, z).method_39415(color);
        consumer.method_22918((Matrix4fc)matrix, x1, y0, z).method_39415(color);
        consumer.method_22918((Matrix4fc)matrix, x1, y1, z).method_39415(color);
        consumer.method_22918((Matrix4fc)matrix, x0, y1, z).method_39415(color);
    }

    private DimaEspPipelines() {
    }
}

