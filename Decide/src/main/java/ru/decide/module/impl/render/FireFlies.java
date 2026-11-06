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
 *  net.minecraft.class_1921
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_2680
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package ru.decide.module.impl.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;

@ModuleInfo(name="Fire Flies", desc="\u0421\u0432\u0435\u0442\u043b\u044f\u0447\u043a\u0438 \u0432\u043e\u043a\u0440\u0443\u0433 \u0438\u0433\u0440\u043e\u043a\u0430", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class FireFlies
extends Module {
    private static final long MAX_PART_ALIVE_TIME = 5500L;
    private static final RenderPipeline FLY_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/fireflies")).withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color").withSampler("Sampler0").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).build());
    private static final RenderPipeline TRAIL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/fireflies_trail")).withVertexShader("core/position_color").withFragmentShader("core/position_color").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).build());
    private static final RenderPipeline SPARK_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/fireflies_spark")).withVertexShader("core/position_color").withFragmentShader("core/position_color").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).build());
    private static final class_2960 FIRE_PART_TEX = class_2960.method_60655((String)"decide", (String)"textures/particles/circle.png");
    private static final class_2960 BLOOM_TEX = class_2960.method_60655((String)"decide", (String)"textures/particles/glow.png");
    private static final class_1921 FLY_LAYER = class_1921.method_75940((String)"decide_fireflies", (class_12247)class_12247.method_75927((RenderPipeline)FLY_PIPELINE).method_75934("Sampler0", FIRE_PART_TEX).method_75937().method_75929(8192).method_75938());
    private static final class_1921 BLOOM_LAYER = class_1921.method_75940((String)"decide_fireflies_bloom", (class_12247)class_12247.method_75927((RenderPipeline)FLY_PIPELINE).method_75934("Sampler0", BLOOM_TEX).method_75937().method_75929(8192).method_75938());
    private static final class_1921 TRAIL_LAYER = class_1921.method_75940((String)"decide_fireflies_trail", (class_12247)class_12247.method_75927((RenderPipeline)TRAIL_PIPELINE).method_75937().method_75929(16384).method_75938());
    private static final class_1921 SPARK_LAYER = class_1921.method_75940((String)"decide_fireflies_spark", (class_12247)class_12247.method_75927((RenderPipeline)SPARK_PIPELINE).method_75937().method_75929(16384).method_75938());
    public ModeSetting colorMode = new ModeSetting((Module)this, "\u0426\u0432\u0435\u0442", "\u0415\u0432\u0440\u043e\u043f\u0430", "\u0421\u0432\u043e\u0439", "\u0422\u0435\u043c\u0430");
    public ColorSetting pickColor = new ColorSetting((Module)this, "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442", ColorUtil.getColor(255, 70, 0, 255)).setVisible(() -> this.colorMode.is("\u0421\u0432\u043e\u0439"));
    public BooleanSetting darkImprint = new BooleanSetting((Module)this, "\u0422\u0451\u043c\u043d\u044b\u0439 \u0441\u043b\u0435\u0434", false);
    public BooleanSetting lighting = new BooleanSetting((Module)this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", false);
    public SliderSetting spawnDelay = new SliderSetting(this, "\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u0441\u043f\u0430\u0432\u043d\u0430", 3.0f, 1.0f, 10.0f, 1.0f);
    private final List<FirePart> fireParts = new ArrayList<FirePart>();
    private static final class_2960 SPARK_GLOW_TEX = class_2960.method_60655((String)"decide", (String)"textures/particles/glow.png");
    private static final class_1921 SPARK_GLOW_LAYER = class_1921.method_75940((String)"decide_fireflies_spark_glow", (class_12247)class_12247.method_75927((RenderPipeline)FLY_PIPELINE).method_75934("Sampler0", SPARK_GLOW_TEX).method_75937().method_75929(8192).method_75938());

    @Override
    protected void onDisable() {
        super.onDisable();
        this.fireParts.clear();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.fireParts.clear();
    }

    private int getPartColor() {
        if (this.colorMode.is("\u0415\u0432\u0440\u043e\u043f\u0430")) {
            return Color.getHSBColor((float)Math.random(), 1.0f, 1.0f).getRGB();
        }
        if (this.colorMode.is("\u0422\u0435\u043c\u0430")) {
            return ColorUtil.client();
        }
        return this.pickColor.getValue();
    }

    private static float getRandom(double min, double max) {
        return (float)ThreadLocalRandom.current().nextDouble(min, Math.max(max, min + 1.0E-6));
    }

    private class_243 generateVecForPart(double rangeXZ, double rangeY) {
        class_243 pos = this.randomAround(rangeXZ, rangeY);
        for (int i = 0; i < 30; ++i) {
            double dx = pos.field_1352 - FireFlies.mc.field_1724.method_23317();
            double dz = pos.field_1350 - FireFlies.mc.field_1724.method_23321();
            if (!this.posBlock(pos.field_1352, pos.field_1351, pos.field_1350) && !(Math.sqrt(dx * dx + dz * dz) < rangeXZ / 3.0)) continue;
            pos = this.randomAround(rangeXZ, rangeY);
        }
        return pos;
    }

    private class_243 randomAround(double rangeXZ, double rangeY) {
        return FireFlies.mc.field_1724.method_73189().method_1031((double)FireFlies.getRandom(-rangeXZ, rangeXZ), (double)FireFlies.getRandom(-rangeY / 3.0, rangeY), (double)FireFlies.getRandom(-rangeXZ, rangeXZ));
    }

    private boolean posBlock(double x, double y, double z) {
        class_2680 state = FireFlies.mc.field_1687.method_8320(class_2338.method_49637((double)x, (double)y, (double)z));
        return !state.method_26215() && state.method_26227().method_15769() && !state.method_45474();
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (FireFlies.mc.field_1724 == null || FireFlies.mc.field_1687 == null) {
            return;
        }
        if (FireFlies.mc.field_1724.field_6012 == 1) {
            this.fireParts.forEach(FirePart::setToRemove);
        }
        this.fireParts.forEach(FirePart::updatePart);
        this.fireParts.removeIf(FirePart::isToRemove);
        if (FireFlies.mc.field_1724.field_6012 % (this.spawnDelay.getValue().intValue() + 1) == 0) {
            int color = this.getPartColor();
            this.fireParts.add(new FirePart(this, this.generateVecForPart(10.0, 4.0), 5500.0f, color));
            this.fireParts.add(new FirePart(this, this.generateVecForPart(6.0, 5.0), 5500.0f, color));
        }
    }

    @EventHandler
    public void onRender(EventRender3D e) {
        if (FireFlies.mc.field_1724 == null || FireFlies.mc.field_1687 == null || this.fireParts.isEmpty()) {
            return;
        }
        float pTicks = e.getTickDelta();
        class_243 cameraPos = FireFlies.mc.field_1773.method_19418().method_71156();
        Quaternionf cameraRotation = FireFlies.mc.field_1773.method_19418().method_23767();
        Vector3f right = cameraRotation.transform(new Vector3f(1.0f, 0.0f, 0.0f));
        Vector3f up = cameraRotation.transform(new Vector3f(0.0f, 1.0f, 0.0f));
        Vector3f forward = cameraRotation.transform(new Vector3f(0.0f, 0.0f, -1.0f));
        class_4587 matrices = e.getMatrixStack();
        Matrix4f matrix = matrices.method_23760().method_23761();
        class_4597.class_4598 consumers = mc.method_22940().method_23000();
        class_4588 trailConsumer = consumers.method_73477(TRAIL_LAYER);
        for (FirePart firePart : this.fireParts) {
            if (this.isBehindCamera(firePart, cameraPos, forward)) continue;
            this.drawTrail(trailConsumer, matrix, firePart, cameraPos);
        }
        consumers.method_22994(TRAIL_LAYER);
        if (this.lighting.getValue().booleanValue()) {
            class_4588 glowConsumer = consumers.method_73477(SPARK_GLOW_LAYER);
            for (FirePart firePart : this.fireParts) {
                if (this.isBehindCamera(firePart, cameraPos, forward)) continue;
                this.drawSparkGlow(glowConsumer, matrix, firePart, cameraPos, right, up, pTicks);
            }
            consumers.method_22994(SPARK_GLOW_LAYER);
        }
        class_4588 sparkConsumer = consumers.method_73477(SPARK_LAYER);
        for (FirePart firePart : this.fireParts) {
            if (this.isBehindCamera(firePart, cameraPos, forward)) continue;
            this.drawSparks(sparkConsumer, matrix, firePart, cameraPos, right, up, pTicks);
        }
        consumers.method_22994(SPARK_LAYER);
        class_4588 class_45882 = consumers.method_73477(FLY_LAYER);
        for (FirePart part : this.fireParts) {
            if (this.isBehindCamera(part, cameraPos, forward)) continue;
            this.drawBody(class_45882, matrix, part, cameraPos, right, up, pTicks, false);
        }
        consumers.method_22994(FLY_LAYER);
        if (this.lighting.getValue().booleanValue()) {
            class_4588 class_45883 = consumers.method_73477(BLOOM_LAYER);
            for (FirePart part : this.fireParts) {
                if (this.isBehindCamera(part, cameraPos, forward)) continue;
                this.drawBody(class_45883, matrix, part, cameraPos, right, up, pTicks, true);
            }
            consumers.method_22994(BLOOM_LAYER);
        }
    }

    private boolean isBehindCamera(FirePart part, class_243 cameraPos, Vector3f forward) {
        float px = (float)(part.pos.field_1352 - cameraPos.field_1352);
        float py = (float)(part.pos.field_1351 - cameraPos.field_1351);
        float pz = (float)(part.pos.field_1350 - cameraPos.field_1350);
        return px * forward.x + py * forward.y + pz * forward.z < -2.0f;
    }

    private void drawSparkGlow(class_4588 consumer, Matrix4f matrix, FirePart part, class_243 cameraPos, Vector3f right, Vector3f up, float pTicks) {
        if (part.sparkParts.isEmpty()) {
            return;
        }
        double dist = FireFlies.mc.field_1724.method_5649(part.pos.field_1352, part.pos.field_1351 + 1.6, part.pos.field_1350);
        float size = 0.035f + 0.02f * class_3532.method_15363((float)(1.0f - ((float)Math.sqrt(dist) - 3.0f) / 10.0f), (float)0.0f, (float)1.0f);
        for (SparkPart spark : part.sparkParts) {
            float life = (float)spark.timePC();
            int color = FireFlies.setAlphaF(part.color, FireFlies.alphaOf(part.color) * (1.0f - life) * 0.3f);
            class_243 pos = new class_243(spark.getRenderPosX(pTicks), spark.getRenderPosY(pTicks), spark.getRenderPosZ(pTicks));
            this.quad(consumer, matrix, pos, cameraPos, right, up, size * (1.4f - life), color);
        }
    }

    private void drawBody(class_4588 consumer, Matrix4f matrix, FirePart part, class_243 cameraPos, Vector3f right, Vector3f up, float pTicks, boolean bloomPass) {
        float alphaPC = part.getAlphaPC();
        if (alphaPC <= 0.004f) {
            return;
        }
        int color = FireFlies.setAlphaF(part.color, FireFlies.alphaOf(part.color) * alphaPC);
        float brightness = FireFlies.brightnessOf(color);
        int bodyColor = FireFlies.lerpToWhiteKeepAlpha(color, FireFlies.alphaOf(color) / 255.0f * brightness * 0.3f);
        class_243 pos = part.getRenderPosVec(pTicks);
        float half = 0.04f * alphaPC;
        if (bloomPass) {
            bodyColor = FireFlies.setAlphaF(bodyColor, FireFlies.alphaOf(bodyColor) * 0.3f);
            half *= 5.0f;
        }
        this.quad(consumer, matrix, pos, cameraPos, right, up, half, bodyColor);
    }

    private void drawTrail(class_4588 consumer, Matrix4f matrix, FirePart part, class_243 cameraPos) {
        if (part.trailParts.size() < 2) {
            return;
        }
        int baseColor = FireFlies.setAlphaF(part.color, FireFlies.alphaOf(part.color) * part.getAlphaPC());
        if (this.darkImprint.getValue().booleanValue()) {
            baseColor = FireFlies.darken(baseColor, 0.3f);
        }
        double distToPart = FireFlies.mc.field_1724.method_5649(part.pos.field_1352, part.pos.field_1351 + 1.6, part.pos.field_1350);
        float widthFactor = class_3532.method_15363((float)(1.0f - ((float)Math.sqrt(distToPart) - 3.0f) / 20.0f), (float)0.0f, (float)1.0f);
        float halfWidth = 0.012f * widthFactor + 1.0E-4f;
        int pointsCount = part.trailParts.size();
        for (int i = 0; i < pointsCount - 1; ++i) {
            TrailPart a = part.trailParts.get(i);
            TrailPart b = part.trailParts.get(i + 1);
            float sizePC = (float)i / (float)pointsCount;
            float sizePC2 = (float)(i + 1) / (float)pointsCount;
            int c1 = this.trailColor(baseColor, part.getAlphaPC(), sizePC);
            int c2 = this.trailColor(baseColor, part.getAlphaPC(), sizePC2);
            this.emitRibbonSegment(consumer, matrix, a, b, cameraPos, halfWidth, c1, c2);
        }
    }

    private int trailColor(int baseColor, float alphaPC, float sizePC) {
        float wave = FireFlies.easeInOutQuadWave(sizePC);
        int c = FireFlies.setAlphaF(baseColor, FireFlies.alphaOf(baseColor) * alphaPC * wave);
        return FireFlies.lerpToWhiteKeepAlpha(c, FireFlies.alphaOf(c) / 255.0f * FireFlies.brightnessOf(c) * 0.25f * sizePC);
    }

    private void emitRibbonSegment(class_4588 consumer, Matrix4f matrix, TrailPart a, TrailPart b, class_243 cameraPos, float halfWidth, int c1, int c2) {
        float ay = (float)(a.y - cameraPos.field_1351);
        float bz = (float)(b.z - cameraPos.field_1350);
        float az = (float)(a.z - cameraPos.field_1350);
        float dz = bz - az;
        float by = (float)(b.y - cameraPos.field_1351);
        float dy = by - ay;
        float cx = ay * dz - az * dy;
        float bx = (float)(b.x - cameraPos.field_1352);
        float ax = (float)(a.x - cameraPos.field_1352);
        float dx = bx - ax;
        float cy = az * dx - ax * dz;
        float cz = ax * dy - ay * dx;
        float len = (float)Math.sqrt(cx * cx + cy * cy + cz * cz);
        if (len < 1.0E-6f) {
            return;
        }
        cx = cx / len * halfWidth;
        cy = cy / len * halfWidth;
        cz = cz / len * halfWidth;
        this.vertex(consumer, matrix, ax - cx, ay - cy, az - cz, c1);
        this.vertex(consumer, matrix, ax + cx, ay + cy, az + cz, c1);
        this.vertex(consumer, matrix, bx + cx, by + cy, bz + cz, c2);
        this.vertex(consumer, matrix, bx - cx, by - cy, bz - cz, c2);
    }

    private void drawSparks(class_4588 consumer, Matrix4f matrix, FirePart part, class_243 cameraPos, Vector3f right, Vector3f up, float pTicks) {
        if (part.sparkParts.size() < 2) {
            return;
        }
        double distToPart = FireFlies.mc.field_1724.method_5649(part.pos.field_1352, part.pos.field_1351 + 1.6, part.pos.field_1350);
        float pointsScale = 0.25f + 5.0f * class_3532.method_15363((float)(1.0f - ((float)Math.sqrt(distToPart) - 3.0f) / 10.0f), (float)0.0f, (float)1.0f);
        float half = pointsScale * 0.0012f;
        float alphaPC = part.getAlphaPC();
        for (SparkPart spark : part.sparkParts) {
            float timePC = (float)spark.timePC();
            int c = FireFlies.lerpKeepNone(-1, part.color, timePC);
            c = FireFlies.setAlphaF(c, FireFlies.alphaOf(part.color) * alphaPC * (1.0f - timePC * timePC * timePC));
            class_243 pos = new class_243(spark.getRenderPosX(pTicks), spark.getRenderPosY(pTicks), spark.getRenderPosZ(pTicks));
            this.quadNoTex(consumer, matrix, pos, cameraPos, right, up, half, c);
        }
    }

    private void quadNoTex(class_4588 consumer, Matrix4f matrix, class_243 pos, class_243 cameraPos, Vector3f right, Vector3f up, float half, int color) {
        float px = (float)(pos.field_1352 - cameraPos.field_1352);
        float py = (float)(pos.field_1351 - cameraPos.field_1351);
        float pz = (float)(pos.field_1350 - cameraPos.field_1350);
        float rx = right.x * half;
        float ry = right.y * half;
        float rz = right.z * half;
        float ux = up.x * half;
        float uy = up.y * half;
        float uz = up.z * half;
        this.vertex(consumer, matrix, px - rx - ux, py - ry - uy, pz - rz - uz, color);
        this.vertex(consumer, matrix, px - rx + ux, py - ry + uy, pz - rz + uz, color);
        this.vertex(consumer, matrix, px + rx + ux, py + ry + uy, pz + rz + uz, color);
        this.vertex(consumer, matrix, px + rx - ux, py + ry - uy, pz + rz - uz, color);
    }

    private void quad(class_4588 consumer, Matrix4f matrix, class_243 pos, class_243 cameraPos, Vector3f right, Vector3f up, float half, int color) {
        float px = (float)(pos.field_1352 - cameraPos.field_1352);
        float py = (float)(pos.field_1351 - cameraPos.field_1351);
        float pz = (float)(pos.field_1350 - cameraPos.field_1350);
        float rx = right.x * half;
        float ry = right.y * half;
        float rz = right.z * half;
        float ux = up.x * half;
        float uy = up.y * half;
        float uz = up.z * half;
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        float a = (float)(color >>> 24 & 0xFF) / 255.0f;
        consumer.method_22918((Matrix4fc)matrix, px - rx - ux, py - ry - uy, pz - rz - uz).method_22913(0.0f, 0.0f).method_22915(r, g, b, a);
        consumer.method_22918((Matrix4fc)matrix, px - rx + ux, py - ry + uy, pz - rz + uz).method_22913(0.0f, 1.0f).method_22915(r, g, b, a);
        consumer.method_22918((Matrix4fc)matrix, px + rx + ux, py + ry + uy, pz + rz + uz).method_22913(1.0f, 1.0f).method_22915(r, g, b, a);
        consumer.method_22918((Matrix4fc)matrix, px + rx - ux, py + ry - uy, pz + rz - uz).method_22913(1.0f, 0.0f).method_22915(r, g, b, a);
    }

    private void vertex(class_4588 consumer, Matrix4f matrix, float x, float y, float z, int color) {
        consumer.method_22918((Matrix4fc)matrix, x, y, z).method_22915((float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >>> 24 & 0xFF) / 255.0f);
    }

    private static float alphaOf(int color) {
        return color >>> 24 & 0xFF;
    }

    private static int setAlphaF(int color, float alpha) {
        int a = class_3532.method_15340((int)Math.round(alpha), (int)0, (int)255);
        return color & 0xFFFFFF | a << 24;
    }

    private static float brightnessOf(int color) {
        float[] hsb = Color.RGBtoHSB(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, null);
        return hsb[2];
    }

    private static int darken(int color, float factor) {
        int r = (int)((float)(color >> 16 & 0xFF) * factor);
        int g = (int)((float)(color >> 8 & 0xFF) * factor);
        int b = (int)((float)(color & 0xFF) * factor);
        return color & 0xFF000000 | r << 16 | g << 8 | b;
    }

    private static int lerpKeepNone(int from, int to, float t) {
        t = class_3532.method_15363((float)t, (float)0.0f, (float)1.0f);
        int r = class_3532.method_48781((float)t, (int)(from >> 16 & 0xFF), (int)(to >> 16 & 0xFF));
        int g = class_3532.method_48781((float)t, (int)(from >> 8 & 0xFF), (int)(to >> 8 & 0xFF));
        int b = class_3532.method_48781((float)t, (int)(from & 0xFF), (int)(to & 0xFF));
        int a = class_3532.method_48781((float)t, (int)(from >>> 24 & 0xFF), (int)(to >>> 24 & 0xFF));
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static int lerpToWhiteKeepAlpha(int color, float t) {
        int a = color >>> 24 & 0xFF;
        int mixed = FireFlies.lerpKeepNone(color, -1, class_3532.method_15363((float)t, (float)0.0f, (float)1.0f));
        return mixed & 0xFFFFFF | a << 24;
    }

    private static float easeInOutQuadWave(float t) {
        float v = 2.0f * t - 1.0f;
        return 1.0f - v * v;
    }

    @Environment(value=EnvType.CLIENT)
    private class FirePart {
        final List<TrailPart> trailParts = new ArrayList<TrailPart>();
        final List<SparkPart> sparkParts = new ArrayList<SparkPart>();
        class_243 prevPos;
        class_243 pos;
        float alphaAnim = 0.0f;
        float alphaTarget = 1.0f;
        int msChangeSideRate = this.getMsChangeSideRate();
        final int color;
        float moveYawSet = FireFlies.getRandom(0.0, 360.0);
        float speed = FireFlies.getRandom(0.08, 0.175);
        float yMotion = FireFlies.getRandom(-0.075, 0.1);
        float moveYaw = this.moveYawSet;
        final float maxAlive;
        final long startTime;
        long rateTimer = this.startTime = System.currentTimeMillis();
        boolean toRemove;

        FirePart(FireFlies fireFlies, class_243 pos, float maxAlive, int color) {
            this.color = color;
            this.pos = pos;
            this.prevPos = pos;
            this.maxAlive = maxAlive;
        }

        float getTimePC() {
            return class_3532.method_15363((float)((float)(System.currentTimeMillis() - this.startTime) / this.maxAlive), (float)0.0f, (float)1.0f);
        }

        float getAlphaPC() {
            return this.alphaAnim;
        }

        class_243 getRenderPosVec(float pTicks) {
            return this.pos.method_1031(-(this.prevPos.field_1352 - this.pos.field_1352) * (double)pTicks, -(this.prevPos.field_1351 - this.pos.field_1351) * (double)pTicks, -(this.prevPos.field_1350 - this.pos.field_1350) * (double)pTicks);
        }

        void updatePart() {
            this.alphaAnim += class_3532.method_15363((float)(this.alphaTarget - this.alphaAnim), (float)-0.02f, (float)0.02f) * 2.0f;
            this.alphaAnim = class_3532.method_15363((float)this.alphaAnim, (float)0.0f, (float)1.0f);
            if (System.currentTimeMillis() - this.rateTimer >= (long)this.msChangeSideRate) {
                this.msChangeSideRate = this.getMsChangeSideRate();
                this.rateTimer = System.currentTimeMillis();
                this.moveYawSet = FireFlies.getRandom(0.0, 360.0);
            }
            this.moveYaw = class_3532.method_16439((float)0.065f, (float)this.moveYaw, (float)this.moveYawSet);
            this.speed /= 1.005f;
            float motionX = -class_3532.method_15374((double)((float)Math.toRadians(this.moveYaw))) * this.speed;
            float motionZ = class_3532.method_15362((double)((float)Math.toRadians(this.moveYaw))) * this.speed;
            this.prevPos = this.pos;
            float scaleBox = 0.1f;
            boolean colliding = !IMinecraft.mc.field_1687.method_18026(new class_238(this.pos.field_1352 - (double)(scaleBox / 2.0f), this.pos.field_1351, this.pos.field_1350 - (double)(scaleBox / 2.0f), this.pos.field_1352 + (double)(scaleBox / 2.0f), this.pos.field_1351 + (double)scaleBox, this.pos.field_1350 + (double)(scaleBox / 2.0f)));
            float delente = colliding ? 0.3f : 1.0f;
            this.yMotion /= 1.02f;
            this.pos = this.pos.method_1031((double)(motionX / delente), (double)(this.yMotion / delente), (double)(motionZ / delente));
            if (this.getTimePC() >= 1.0f) {
                this.alphaTarget = 0.0f;
                if (this.getAlphaPC() < 0.004f) {
                    this.setToRemove();
                }
            }
            this.trailParts.add(new TrailPart(this, 500));
            this.trailParts.removeIf(TrailPart::toRemove);
            for (int i = 0; i < 2; ++i) {
                this.sparkParts.add(new SparkPart(this, 600));
            }
            this.sparkParts.forEach(SparkPart::motionSparkProcess);
            this.sparkParts.removeIf(SparkPart::toRemove);
        }

        void setToRemove() {
            this.toRemove = true;
        }

        boolean isToRemove() {
            return this.toRemove;
        }

        int getMsChangeSideRate() {
            return (int)FireFlies.getRandom(300.5, 900.5);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static class SparkPart {
        double posX;
        double posY;
        double posZ;
        double prevPosX;
        double prevPosY;
        double prevPosZ;
        double speed = Math.random() / 50.0;
        final float radianYaw = (float)Math.random() * 360.0f;
        final float radianPitch = -45.0f + (float)Math.random() * 90.0f;
        final long startTime = System.currentTimeMillis();
        final int maxTime;

        SparkPart(FirePart part, int maxTime) {
            this.maxTime = maxTime;
            this.posX = part.pos.field_1352;
            this.posY = part.pos.field_1351;
            this.posZ = part.pos.field_1350;
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
        }

        double timePC() {
            return class_3532.method_15363((float)((float)(System.currentTimeMillis() - this.startTime) / (float)this.maxTime), (float)0.0f, (float)1.0f);
        }

        boolean toRemove() {
            return this.timePC() >= 1.0;
        }

        void motionSparkProcess() {
            float radYaw = (float)Math.toRadians(this.radianYaw);
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.posX += (double)class_3532.method_15374((double)radYaw) * this.speed;
            this.posY += (double)class_3532.method_15362((double)((float)Math.toRadians(this.radianPitch - 90.0f))) * this.speed;
            this.posZ += (double)class_3532.method_15362((double)radYaw) * this.speed;
            this.speed /= 1.2;
        }

        double getRenderPosX(float pTicks) {
            return this.prevPosX + (this.posX - this.prevPosX) * (double)pTicks;
        }

        double getRenderPosY(float pTicks) {
            return this.prevPosY + (this.posY - this.prevPosY) * (double)pTicks;
        }

        double getRenderPosZ(float pTicks) {
            return this.prevPosZ + (this.posZ - this.prevPosZ) * (double)pTicks;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static class TrailPart {
        final double x;
        final double y;
        final double z;
        final long startTime = System.currentTimeMillis();
        final int maxTime;

        TrailPart(FirePart part, int maxTime) {
            this.maxTime = maxTime;
            this.x = part.pos.field_1352;
            this.y = part.pos.field_1351;
            this.z = part.pos.field_1350;
        }

        boolean toRemove() {
            return System.currentTimeMillis() - this.startTime >= (long)this.maxTime;
        }
    }
}

