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
public class CircleProgressPipeline
extends UniformArrayPipeline {
    private static final class_2960 PIPELINE_ID = class_2960.method_60655((String)"decide", (String)"pipeline/circle_progress");
    private static final class_2960 VERTEX_SHADER = class_2960.method_60655((String)"decide", (String)"core/circle_progress");
    private static final class_2960 FRAGMENT_SHADER = class_2960.method_60655((String)"decide", (String)"core/circle_progress");
    private static final RenderPipeline PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(PIPELINE_ID).withVertexShader(VERTEX_SHADER).withFragmentShader(FRAGMENT_SHADER).withVertexFormat(class_290.field_60033, VertexFormat.class_5596.field_27379).withUniform("CircleData", class_10789.field_60031).withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
    private static final int MAX_CIRCLES = 64;
    private static final int CIRCLE_SIZE = 64;
    private static final int UNIFORM_RING = 32;

    public CircleProgressPipeline() {
        super("circle_progress", PIPELINE, "CircleData", 64, 64, 32);
    }

    @Override
    public int batchLayer() {
        return 0;
    }

    public void draw(float centerX, float centerY, float radius, float thickness, float progress, int color) {
        if (radius <= 0.0f || thickness <= 0.0f) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.method_1522() == null) {
            return;
        }
        ByteBuffer dataBuffer = this.beginItem();
        float padding = thickness * 0.75f + 1.0f;
        float size = (radius + padding) * 2.0f;
        float drawX = centerX - size / 2.0f;
        float drawY = centerY - size / 2.0f;
        dataBuffer.putFloat(drawX);
        dataBuffer.putFloat(drawY);
        dataBuffer.putFloat(size);
        dataBuffer.putFloat(size);
        dataBuffer.putFloat(radius + padding);
        dataBuffer.putFloat(radius);
        dataBuffer.putFloat(thickness);
        dataBuffer.putFloat(Math.max(0.0f, Math.min(1.0f, progress)));
        dataBuffer.putFloat((float)(color >> 16 & 0xFF) / 255.0f);
        dataBuffer.putFloat((float)(color >> 8 & 0xFF) / 255.0f);
        dataBuffer.putFloat((float)(color & 0xFF) / 255.0f);
        dataBuffer.putFloat((float)(color >> 24 & 0xFF) / 255.0f);
        dataBuffer.putFloat(0.0f);
        dataBuffer.putFloat(0.0f);
        dataBuffer.putFloat(0.0f);
        dataBuffer.putFloat(0.0f);
        this.endItem();
    }
}

