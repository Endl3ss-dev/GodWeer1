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
 *  net.minecraft.class_10055
 *  net.minecraft.class_10799
 *  net.minecraft.class_11659
 *  net.minecraft.class_12247
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1921
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_3883
 *  net.minecraft.class_3887
 *  net.minecraft.class_4587
 *  net.minecraft.class_591
 *  net.minecraft.class_7833
 *  org.joml.Quaternionfc
 */
package ru.decide.utils.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_10799;
import net.minecraft.class_11659;
import net.minecraft.class_12247;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_591;
import net.minecraft.class_7833;
import org.joml.Quaternionfc;
import ru.decide.Client;
import ru.decide.module.impl.render.ChinaHat;
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public class ChinaHatFeatureRenderer
extends class_3887<class_10055, class_591> {
    private static final float TAU = (float)Math.PI * 2;
    private static final int SEGMENTS = 96;
    private static final RenderPipeline FILL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"china_hat_fill")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27379).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(true).withBlend(BlendFunction.TRANSLUCENT).build());
    private static final class_1921 FILL_LAYER = class_1921.method_75940((String)"decide_china_hat_fill", (class_12247)class_12247.method_75927((RenderPipeline)FILL_PIPELINE).method_75929(16384).method_75938());
    private static final RenderPipeline OUTLINE_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"china_hat_outline")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_29345).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).build());
    private static final class_1921 OUTLINE_LAYER = class_1921.method_75940((String)"decide_china_hat_outline", (class_12247)class_12247.method_75927((RenderPipeline)OUTLINE_PIPELINE).method_75929(4096).method_75938());

    public ChinaHatFeatureRenderer(class_3883<class_10055, class_591> context) {
        super(context);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(class_4587 matrices, class_11659 queue, int light, class_10055 state, float limbAngle, float limbDistance) {
        int colorB;
        int colorA;
        ChinaHat module = ChinaHat.getInstance();
        if (module == null || !module.isEnabled()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        boolean local = ChinaHatFeatureRenderer.isLocalPlayer(state, mc);
        if (!local && !ChinaHatFeatureRenderer.isFriendPlayer(state)) {
            return;
        }
        if (local && mc.field_1690.method_31044().method_31034()) {
            return;
        }
        float radius = module.size.getValue().floatValue();
        float apex = module.apex.getValue().floatValue();
        float lift = module.lift.getValue().floatValue();
        if (module.themeColors.getValue().booleanValue()) {
            int theme;
            colorA = theme = ColorUtil.client();
            colorB = ColorUtil.fadeBetween(theme, -1, 0.35f);
        } else {
            colorA = module.colorA.getValue();
            colorB = module.colorB.getValue();
        }
        int alpha = (int)Math.max(0.0f, Math.min(255.0f, module.opacity.getValue().floatValue()));
        float speed = module.speed.getValue().floatValue();
        boolean outline = module.outline.getValue();
        if (radius <= 0.001f || alpha <= 0) {
            return;
        }
        matrices.method_22903();
        try {
            ((class_591)this.method_17165()).field_3398.method_22703(matrices);
            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(180.0f));
            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(90.0f));
            matrices.method_46416(0.0f, lift, 0.0f);
            float timeShift = (float)(System.currentTimeMillis() % 1000000L) / 1000.0f * speed * 0.05f;
            queue.method_73483(matrices, FILL_LAYER, (pose, vertices) -> {
                for (int i = 0; i < 96; ++i) {
                    float a0 = (float)i * ((float)Math.PI * 2) / 96.0f;
                    float a1 = (float)(i + 1) * ((float)Math.PI * 2) / 96.0f;
                    int color = ChinaHatFeatureRenderer.gradient(colorA, colorB, (float)i / 96.0f + timeShift, alpha);
                    vertices.method_56824(pose, 0.0f, apex, 0.0f).method_39415(color);
                    vertices.method_56824(pose, -class_3532.method_15374((double)a0) * radius, 0.0f, class_3532.method_15362((double)a0) * radius).method_39415(color);
                    vertices.method_56824(pose, -class_3532.method_15374((double)a1) * radius, 0.0f, class_3532.method_15362((double)a1) * radius).method_39415(color);
                }
            });
            if (outline) {
                queue.method_73483(matrices, OUTLINE_LAYER, (pose, vertices) -> {
                    for (int i = 0; i <= 96; ++i) {
                        float angle = (float)i * ((float)Math.PI * 2) / 96.0f;
                        int color = ChinaHatFeatureRenderer.gradient(colorA, colorB, (float)i / 96.0f + timeShift, 255);
                        vertices.method_56824(pose, -class_3532.method_15374((double)angle) * radius, 0.001f, class_3532.method_15362((double)angle) * radius).method_39415(color);
                    }
                });
            }
        }
        finally {
            matrices.method_22909();
        }
    }

    private static int gradient(int from, int to, float t, int alpha) {
        float wrapped = t - (float)class_3532.method_15375((float)t);
        return ColorUtil.replAlpha(ColorUtil.fadeBetween(from, to, wrapped), alpha);
    }

    private static boolean isLocalPlayer(class_10055 state, class_310 mc) {
        return mc.field_1724 != null && state.field_53528 == mc.field_1724.method_5628();
    }

    private static boolean isFriendPlayer(class_10055 state) {
        try {
            class_310 mc = class_310.method_1551();
            if (mc.field_1687 == null) {
                return false;
            }
            class_1297 entity = mc.field_1687.method_8469(state.field_53528);
            if (entity instanceof class_1657) {
                class_1657 player = (class_1657)entity;
                return Client.get().friendManager().isFriend(player.method_5477().getString());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return false;
    }
}

