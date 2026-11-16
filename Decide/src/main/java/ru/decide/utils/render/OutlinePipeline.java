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
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 */
package ru.decide.utils.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import ru.decide.utils.render.UniformArrayPipeline;

@Environment(value=EnvType.CLIENT)
public class OutlinePipeline
extends UniformArrayPipeline {
    private static final class_2960 PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/outline");
    private static final class_2960 VERTEX_SHADER = class_2960.method_60655((String)"decide", (String)"core/outline");
    private static final class_2960 FRAGMENT_SHADER = class_2960.method_60655((String)"decide", (String)"core/outline");
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(PIPELINE_ID).withVertexShader(VERTEX_SHADER).withFragmentShader(FRAGMENT_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("OutlineData", class_10789.field_60031).withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final int MAX_OUTLINES = 64;
    private static final int OUTLINE_SIZE = 208;
    private static final int UNIFORM_RING = 32;

    public OutlinePipeline() {
        super("outline", PIPELINE, "OutlineData", 64, 208, 32);
    }

    @Override
    public int batchLayer() {
        return 1;
    }

    public void drawOutline(float x, float y, float width, float height, int[] colors, float[] thicknesses, float[] radii, float smoothness) {
        int i;
        class_310 client = class_310.method_1551();
        if (client.method_1522() == null) {
            return;
        }
        ByteBuffer dataBuffer = this.beginItem();
        dataBuffer.putFloat(x);
        dataBuffer.putFloat(y);
        dataBuffer.putFloat(width);
        dataBuffer.putFloat(height);
        dataBuffer.putFloat(radii[0]);
        dataBuffer.putFloat(radii[1]);
        dataBuffer.putFloat(radii[2]);
        dataBuffer.putFloat(radii[3]);
        for (i = 0; i < 8; ++i) {
            int color = i < colors.length ? colors[i] : colors[colors.length - 1];
            float a = (float)(color >> 24 & 0xFF) / 255.0f;
            float r = (float)(color >> 16 & 0xFF) / 255.0f;
            float g = (float)(color >> 8 & 0xFF) / 255.0f;
            float b = (float)(color & 0xFF) / 255.0f;
            dataBuffer.putFloat(r);
            dataBuffer.putFloat(g);
            dataBuffer.putFloat(b);
            dataBuffer.putFloat(a);
        }
        for (i = 0; i < 8; ++i) {
            float t = i < thicknesses.length ? thicknesses[i] : thicknesses[thicknesses.length - 1];
            dataBuffer.putFloat(t);
        }
        dataBuffer.putFloat(smoothness);
        dataBuffer.putFloat(0.0f);
        dataBuffer.putFloat(0.0f);
        dataBuffer.putFloat(0.0f);
        this.endItem();
    }
}

