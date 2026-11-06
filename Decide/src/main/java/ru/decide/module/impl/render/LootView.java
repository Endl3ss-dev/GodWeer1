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
 *  net.minecraft.class_1297
 *  net.minecraft.class_1542
 *  net.minecraft.class_1799
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_638
 */
package ru.decide.module.impl.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1799;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_638;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.lootview.LootShape;
import ru.decide.module.impl.render.lootview.LootViewShapes;
import ru.decide.utils.colors.ColorUtil;

@ModuleInfo(name="Loot View", desc="\u0420\u0438\u0441\u0443\u0435\u0442 \u043c\u0435\u0440\u0446\u0430\u044e\u0449\u0438\u0435 \u0438\u0433\u043b\u044b \u043f\u043e \u0441\u0438\u043b\u0443\u044d\u0442\u0443 \u0432\u044b\u0431\u0440\u043e\u0448\u0435\u043d\u043d\u044b\u0445 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public final class LootView
extends Module {
    private static final int SIDES = 8;
    private static final String DENSITY_LOW = "\u041d\u0438\u0437\u043a\u0430\u044f";
    private static final String DENSITY_MEDIUM = "\u0421\u0440\u0435\u0434\u043d\u044f\u044f";
    private static final String DENSITY_HIGH = "\u0412\u044b\u0441\u043e\u043a\u0430\u044f";
    private static final float[] COS = new float[8];
    private static final float[] SIN = new float[8];
    private static final RenderPipeline LOOT_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/loot_view")).withVertexShader("core/position_color").withFragmentShader("core/position_color").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).build());
    private static final class_1921 LOOT_LAYER = class_1921.method_75940((String)"client_loot_view", (class_12247)class_12247.method_75927((RenderPipeline)LOOT_PIPELINE).method_75937().method_75929(8192).method_75938());
    public final SliderSetting distance = new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f", 32.0f, 8.0f, 64.0f, 1.0f);
    public final SliderSetting needleHeight = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 \u0438\u0433\u043b", 1.0f, 0.4f, 1.6f, 0.05f);
    public final SliderSetting silhouetteScale = new SliderSetting(this, "\u041c\u0430\u0441\u0448\u0442\u0430\u0431", 1.0f, 0.5f, 1.5f, 0.05f);
    public final ModeSetting density = new ModeSetting((Module)this, "\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c", "\u0421\u0440\u0435\u0434\u043d\u044f\u044f", "\u041d\u0438\u0437\u043a\u0430\u044f", "\u0412\u044b\u0441\u043e\u043a\u0430\u044f");

    @Override
    protected void onDisable() {
        LootViewShapes.clear();
    }

    @EventHandler
    public void onRender(EventRender3D event) {
        class_4184 camera;
        class_638 level = LootView.mc.field_1687;
        if (level == null || LootView.mc.field_1724 == null) {
            return;
        }
        class_4184 class_41842 = camera = LootView.mc.field_1773 == null ? null : LootView.mc.field_1773.method_19418();
        if (camera == null) {
            return;
        }
        class_243 cam = camera.method_71156();
        float partialTicks = event.getTickDelta();
        double maxDist = this.distance.getValue().floatValue();
        double maxDistSqr = maxDist * maxDist;
        int pointCount = this.density.is(DENSITY_LOW) ? 7 : (this.density.is(DENSITY_HIGH) ? 15 : 11);
        class_1921 layer = LOOT_LAYER;
        class_4597.class_4598 provider = mc.method_22940().method_23000();
        class_4587.class_4665 pose = event.getMatrixStack().method_23760();
        float time = (float)(System.nanoTime() / 1000000L % 3600000L) / 1000.0f;
        class_4588 consumer = null;
        for (class_1297 entity : level.method_18112()) {
            double distSqr;
            class_1799 stack;
            if (!(entity instanceof class_1542)) continue;
            class_1542 item = (class_1542)entity;
            if (entity.method_31481() || (stack = item.method_6983()).method_7960() || (distSqr = entity.method_5649(cam.field_1352, cam.field_1351, cam.field_1350)) > maxDistSqr) continue;
            if (consumer == null) {
                consumer = provider.method_73477(layer);
            }
            double actualDistance = Math.sqrt(distSqr);
            float fade = class_3532.method_15363((float)((float)((maxDist - actualDistance) / (maxDist * (double)0.2f))), (float)0.0f, (float)1.0f);
            this.renderNeedles(pose, consumer, item, cam.field_1352, cam.field_1351, cam.field_1350, partialTicks, time, pointCount, fade, (float)actualDistance);
        }
        if (consumer != null) {
            provider.method_22994(layer);
        }
    }

    private void renderNeedles(class_4587.class_4665 pose, class_4588 consumer, class_1542 entity, double camX, double camY, double camZ, float partialTicks, float time, int pointCount, float fade, float distanceToCamera) {
        class_1799 stack = entity.method_6983();
        LootShape shape = LootViewShapes.get(stack, pointCount);
        if (shape == null) {
            return;
        }
        class_243 pos = entity.method_30950(partialTicks);
        float baseX = (float)(pos.field_1352 - camX);
        float baseY = (float)(pos.field_1351 - camY) + 0.02f;
        float baseZ = (float)(pos.field_1350 - camZ);
        float grow = class_3532.method_15363((float)(((float)entity.method_6985() + partialTicks) / 12.0f), (float)0.0f, (float)1.0f);
        float growEase = 1.0f - (1.0f - grow) * (1.0f - grow) * (1.0f - grow);
        float height = this.needleHeight.getValue().floatValue() * 1.2f * growEase;
        float scale = this.silhouetteScale.getValue().floatValue() * (0.4f + 0.6f * growEase);
        float seedBase = (float)(entity.method_5628() & 0xFFFF) * 0.137f;
        int color = shape.color();
        float distanceFactor = class_3532.method_15363((float)(distanceToCamera / 16.0f), (float)0.0f, (float)4.0f);
        float adaptiveThickness = 0.01275f * (1.0f + distanceFactor * 0.045f);
        float minimumThickness = distanceToCamera * 0.0012f;
        float baseSpikeRadius = Math.max(adaptiveThickness, minimumThickness);
        int count = shape.count();
        for (int i = 0; i < count; ++i) {
            int k;
            int j;
            float cycle;
            float period = 1.1f + 0.5f * LootView.hash((float)i * 7.31f + seedBase);
            float local = time / period + LootView.hash((float)i * 3.7f + seedBase) * 4.0f;
            float p = local - (cycle = (float)Math.floor(local));
            float env = p < 0.3f ? p / 0.3f : (p > 0.7f ? (1.0f - p) / 0.3f : 1.0f);
            env *= env * (3.0f - 2.0f * env);
            if ((env = LootView.smoothstep(0.0f, 1.0f, env)) <= 0.015f) continue;
            float spread = 0.175f;
            float jx = (LootView.hash((float)i * 12.9898f + cycle * 78.233f + seedBase) - 0.5f) * spread;
            float jz = (LootView.hash((float)i * 45.164f + cycle * 94.673f + seedBase) - 0.5f) * spread;
            float wx = baseX + shape.xs()[i] * scale + jx;
            float wz = baseZ + shape.zs()[i] * scale + jz;
            float hRand = 0.7f + 0.6f * LootView.hash((float)i * 27.17f + cycle * 17.929f + seedBase);
            float h = height * shape.jitter()[i] * hRand * env;
            if (h <= 0.01f) continue;
            float alpha = 0.9f * env * fade;
            float headRadius = 0.041399997f * (0.4f + 0.6f * env);
            float spikeRadius = baseSpikeRadius * (0.5f + 0.5f * env);
            int headCenterColor = ColorUtil.applyOpacity(color, Math.min(1.0f, alpha * 1.25f));
            int headEdgeColor = ColorUtil.applyOpacity(color, alpha * 0.3f);
            int baseColor = ColorUtil.applyOpacity(color, alpha);
            int apexColor = ColorUtil.applyOpacity(ColorUtil.interpolateColor(color, -1, 0.45f), alpha * 0.2f);
            float headY = baseY + 0.004f;
            float apexY = baseY + h;
            for (j = 0; j < 8; ++j) {
                k = (j + 1) % 8;
                float ex0 = wx + COS[j] * headRadius;
                float ez0 = wz + SIN[j] * headRadius;
                float ex1 = wx + COS[k] * headRadius;
                float ez1 = wz + SIN[k] * headRadius;
                consumer.method_56824(pose, wx, headY, wz).method_39415(headCenterColor);
                consumer.method_56824(pose, ex0, headY, ez0).method_39415(headEdgeColor);
                consumer.method_56824(pose, ex1, headY, ez1).method_39415(headEdgeColor);
                consumer.method_56824(pose, ex1, headY, ez1).method_39415(headEdgeColor);
            }
            for (j = 0; j < 8; ++j) {
                k = (j + 1) % 8;
                float bx0 = wx + COS[j] * spikeRadius;
                float bz0 = wz + SIN[j] * spikeRadius;
                float bx1 = wx + COS[k] * spikeRadius;
                float bz1 = wz + SIN[k] * spikeRadius;
                consumer.method_56824(pose, bx0, baseY, bz0).method_39415(baseColor);
                consumer.method_56824(pose, bx1, baseY, bz1).method_39415(baseColor);
                consumer.method_56824(pose, wx, apexY, wz).method_39415(apexColor);
                consumer.method_56824(pose, wx, apexY, wz).method_39415(apexColor);
            }
            float haloRadius = spikeRadius * 2.8f;
            int haloBaseColor = ColorUtil.applyOpacity(color, alpha * 0.16f);
            int haloApexColor = ColorUtil.applyOpacity(color, 0.0f);
            float haloApexY = baseY + h * 0.92f;
            for (int j2 = 0; j2 < 8; ++j2) {
                int k2 = (j2 + 1) % 8;
                float bx0 = wx + COS[j2] * haloRadius;
                float bz0 = wz + SIN[j2] * haloRadius;
                float bx1 = wx + COS[k2] * haloRadius;
                float bz1 = wz + SIN[k2] * haloRadius;
                consumer.method_56824(pose, bx0, baseY, bz0).method_39415(haloBaseColor);
                consumer.method_56824(pose, bx1, baseY, bz1).method_39415(haloBaseColor);
                consumer.method_56824(pose, wx, haloApexY, wz).method_39415(haloApexColor);
                consumer.method_56824(pose, wx, haloApexY, wz).method_39415(haloApexColor);
            }
        }
    }

    private static float hash(float value) {
        float s = (float)Math.sin(value) * 43758.547f;
        return s - (float)Math.floor(s);
    }

    private static float smoothstep(float edge0, float edge1, float value) {
        float t = class_3532.method_15363((float)((value - edge0) / (edge1 - edge0)), (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    static {
        for (int i = 0; i < 8; ++i) {
            LootView.COS[i] = (float)Math.cos((float)Math.PI * 2 * (float)i / 8.0f);
            LootView.SIN[i] = (float)Math.sin((float)Math.PI * 2 * (float)i / 8.0f);
        }
    }
}

