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
 *  net.minecraft.class_1309
 *  net.minecraft.class_1921
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package ru.decide.module.impl.render.targetesp;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import ru.decide.module.impl.render.targetesp.TargetEspPipelines;
import ru.decide.module.impl.render.targetesp.TargetEspRenderContext;

@Environment(value=EnvType.CLIENT)
public final class CrystalTargetEspRenderer {
    private static final class_2960 BLOOM = class_2960.method_60655((String)"decide", (String)"textures/bloom.png");
    private static final Vector3f[] VERTICES = new Vector3f[]{new Vector3f(0.0f, 1.5f, 0.0f), new Vector3f(0.0f, -1.5f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(-1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, 0.0f, -1.0f)};
    private static final int[][] FACES = new int[][]{{0, 2, 4}, {0, 4, 3}, {0, 3, 5}, {0, 5, 2}, {1, 4, 2}, {1, 3, 4}, {1, 5, 3}, {1, 2, 5}};
    private static final float[] FACE_BRIGHTNESS = new float[]{1.0f, 0.8f, 0.6f, 0.9f, 0.7f, 0.5f, 0.4f, 0.6f};
    private static final float CRYSTAL_SIZE = 0.1f;
    private static final float BLOOM_SIZE = 1.0f;
    public static final RenderPipeline CRYSTAL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"targetesp_crystal")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27379).withCull(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    private static final class_1921 CRYSTAL_LAYER = class_1921.method_75940((String)"targetesp_crystal", (class_12247)class_12247.method_75927((RenderPipeline)CRYSTAL_PIPELINE).method_75929(65536).method_75938());

    private CrystalTargetEspRenderer() {
    }

    public static void render(class_4587 stack, class_4597.class_4598 provider, TargetEspRenderContext context, float speed) {
        class_1309 target = context.target();
        float anim = class_3532.method_15363((float)context.alpha(), (float)0.0f, (float)1.0f);
        if (anim <= 0.01f || target == null) {
            return;
        }
        float moving = (float)(context.frameTimeMs() % 100000L) * 0.2f * Math.max(0.05f, speed);
        float width = target.method_17681() * 1.5f;
        float height = target.method_17682();
        int rgb = context.primaryColor() | 0xFF000000;
        int baseColor = CrystalTargetEspRenderer.withAlpha(rgb, Math.round(255.0f * anim));
        int bloomColor = CrystalTargetEspRenderer.withAlpha(rgb, Math.round(255.0f * anim * 0.2f));
        class_4588 crystal = provider.method_73477(CRYSTAL_LAYER);
        for (int i = 0; i < 360; i += 20) {
            float val = 1.2f - 0.5f * anim;
            float angle = (float)Math.toRadians((float)i + moving * 0.3f);
            float sin = (float)(Math.sin(angle) * (double)width * (double)val);
            float cos = (float)(Math.cos(angle) * (double)width * (double)val);
            float yOff = 0.1f + height * Math.abs((float)Math.sin(i));
            stack.method_22903();
            stack.method_46416(sin, yOff, cos);
            Vector3f dir = new Vector3f(-sin, height * 0.5f - 1.0f, -cos);
            if (dir.lengthSquared() < 1.0E-8f) {
                dir.set(0.0f, 1.0f, 0.0f);
            } else {
                dir.normalize();
            }
            stack.method_22907((Quaternionfc)new Quaternionf().rotationTo((Vector3fc)new Vector3f(0.0f, 1.0f, 0.0f), (Vector3fc)dir));
            CrystalTargetEspRenderer.renderCrystalMesh(stack, crystal, 0.1f, baseColor);
            stack.method_22909();
        }
        class_4588 bloom = provider.method_73477(TargetEspPipelines.ROMB_ESP.apply(BLOOM));
        Quaternionf camRot = TargetEspPipelines.cameraRotation();
        float half = 0.5f;
        for (int i = 0; i < 360; i += 20) {
            float val = 1.2f - 0.5f * anim;
            float angle = (float)Math.toRadians((float)i + moving * 0.3f);
            float sin = (float)(Math.sin(angle) * (double)width * (double)val);
            float cos = (float)(Math.cos(angle) * (double)width * (double)val);
            float yOff = 0.1f + height * Math.abs((float)Math.sin(i));
            stack.method_22903();
            stack.method_46416(sin, yOff, cos);
            stack.method_22907((Quaternionfc)camRot);
            Matrix4f matrix = stack.method_23760().method_23761();
            TargetEspPipelines.textured(bloom, matrix, -half, -half, 0.0f, 0.0f, 0.0f, bloomColor);
            TargetEspPipelines.textured(bloom, matrix, half, -half, 0.0f, 1.0f, 0.0f, bloomColor);
            TargetEspPipelines.textured(bloom, matrix, half, half, 0.0f, 1.0f, 1.0f, bloomColor);
            TargetEspPipelines.textured(bloom, matrix, -half, half, 0.0f, 0.0f, 1.0f, bloomColor);
            stack.method_22909();
        }
    }

    private static void renderCrystalMesh(class_4587 matrices, class_4588 buffer, float size, int color) {
        matrices.method_22903();
        matrices.method_22905(size, size, size);
        Matrix4f matrix = matrices.method_23760().method_23761();
        for (int i = 0; i < FACES.length; ++i) {
            int[] face = FACES[i];
            int shaded = CrystalTargetEspRenderer.applyBrightness(color, FACE_BRIGHTNESS[i]);
            Vector3f v1 = VERTICES[face[0]];
            Vector3f v2 = VERTICES[face[1]];
            Vector3f v3 = VERTICES[face[2]];
            buffer.method_22918((Matrix4fc)matrix, v1.x, v1.y, v1.z).method_39415(shaded);
            buffer.method_22918((Matrix4fc)matrix, v2.x, v2.y, v2.z).method_39415(shaded);
            buffer.method_22918((Matrix4fc)matrix, v3.x, v3.y, v3.z).method_39415(shaded);
        }
        matrices.method_22909();
    }

    private static int applyBrightness(int color, float brightness) {
        int alpha = color >> 24 & 0xFF;
        int red = Math.min(255, Math.max(0, (int)((float)(color >> 16 & 0xFF) * brightness)));
        int green = Math.min(255, Math.max(0, (int)((float)(color >> 8 & 0xFF) * brightness)));
        int blue = Math.min(255, Math.max(0, (int)((float)(color & 0xFF) * brightness)));
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private static int withAlpha(int color, int alpha) {
        return class_3532.method_15340((int)alpha, (int)0, (int)255) << 24 | color & 0xFFFFFF;
    }
}

