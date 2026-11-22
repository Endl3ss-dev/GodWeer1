/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10789
 *  net.minecraft.class_10799
 *  net.minecraft.class_12246
 *  net.minecraft.class_12247
 *  net.minecraft.class_1921
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_2382
 *  net.minecraft.class_239
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_3965
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.decide.module.impl.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collections;
import java.util.List;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10789;
import net.minecraft.class_10799;
import net.minecraft.class_12246;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2382;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.render.GlassBlockRenderer;

@ModuleInfo(name="Glass Block", category=Category.VISUALS, desc="\u0414\u0435\u043b\u0430\u0435\u0442 \u043d\u0430\u0432\u0435\u0434\u0435\u043d\u043d\u044b\u0439 \u0431\u043b\u043e\u043a \u0441\u0442\u0435\u043a\u043b\u044f\u043d\u043d\u044b\u043c")
@Environment(value=EnvType.CLIENT)
public class GlassBlock
extends Module {
    private static GlassBlock instance;
    public BooleanSetting enableBlur = new BooleanSetting((Module)this, "\u0411\u043b\u044e\u0440", true);
    public SliderSetting blurRadius = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0440\u0430\u0437\u043c\u044b\u0442\u0438\u044f", 2.5f, 1.0f, 5.0f, 0.1f).setVisible(() -> this.enableBlur.getValue());
    public SliderSetting blurIterations = new SliderSetting(this, "\u041a\u0430\u0447\u0435\u0441\u0442\u0432\u043e", 3.0f, 1.0f, 5.0f, 1.0f).setVisible(() -> this.enableBlur.getValue());
    public SliderSetting saturation = new SliderSetting(this, "\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c", 0.0f, 0.0f, 2.0f, 0.1f).setVisible(() -> this.enableBlur.getValue());
    public BooleanSetting enableTint = new BooleanSetting((Module)this, "\u041e\u0442\u0442\u0435\u043d\u043e\u043a", false);
    public SliderSetting tintIntensity = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u043e\u0442\u0442\u0435\u043d\u043a\u0430", 0.2f, 0.0f, 0.5f, 0.01f).setVisible(() -> this.enableBlur.getValue() != false && this.enableTint.getValue() != false);
    public ColorSetting tintColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u043e\u0442\u0442\u0435\u043d\u043a\u0430", -16711681).setVisible(() -> this.enableTint.getValue());
    public BooleanSetting enableEdgeGlow = new BooleanSetting((Module)this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u043a\u0440\u0430\u0451\u0432", true);
    public SliderSetting edgeGlowIntensity = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 0.2f, 0.0f, 1.0f, 0.01f).setVisible(() -> this.enableEdgeGlow.getValue());
    public BooleanSetting shimmer = new BooleanSetting((Module)this, "\u0428\u0438\u043c\u043c\u0435\u0440", true).setVisible(() -> this.enableEdgeGlow.getValue());
    public SliderSetting shimmerWidth = new SliderSetting(this, "\u0428\u0438\u0440\u0438\u043d\u0430 \u0448\u0438\u043c\u043c\u0435\u0440\u0430", 0.04f, 0.01f, 0.15f, 0.01f).setVisible(() -> this.enableEdgeGlow.getValue() != false && this.shimmer.getValue() != false);
    public SliderSetting shimmerPeriod = new SliderSetting(this, "\u041f\u0435\u0440\u0438\u043e\u0434 \u0448\u0438\u043c\u043c\u0435\u0440\u0430", 5.0f, 1.0f, 15.0f, 0.5f).setVisible(() -> this.enableEdgeGlow.getValue() != false && this.shimmer.getValue() != false);
    public SliderSetting moveSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0435\u043c\u0435\u0449\u0435\u043d\u0438\u044f", 0.18f, 0.05f, 0.6f, 0.01f);
    public BooleanSetting enableShaderFill = new BooleanSetting((Module)this, "\u0428\u0435\u0439\u0434\u0435\u0440 \u0437\u0430\u043b\u0438\u0432\u043a\u0430", false);
    public ModeSetting shaderMode = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0448\u0435\u0439\u0434\u0435\u0440\u0430", "Full", "WebShader", "Plasma", "ChamsFill", "BaseWarp", "\u041a\u043e\u0441\u043c\u043e\u0441", "\u0413\u0438\u0440\u043e\u0438\u0434", "\u0424\u0435\u043d\u0435\u043b\u044c", "\u041e\u0431\u043b\u0430\u043a\u0430", "\u041d\u0435\u0431\u0443\u043b\u0430").setVisible(() -> this.enableShaderFill.getValue());
    public SliderSetting shaderFillAlpha = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0448\u0435\u0439\u0434\u0435\u0440\u0430", 0.5f, 0.1f, 1.0f, 0.05f).setVisible(() -> this.enableShaderFill.getValue());
    public ColorSetting shaderFillColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0448\u0435\u0439\u0434\u0435\u0440\u0430", -1).setVisible(() -> this.enableShaderFill.getValue());
    private final class_9799 allocator = new class_9799(16384);
    private final Animation moveAnimation = new Animation();
    private final Animation alphaAnimation = new Animation();
    private class_2338 targetPos;
    private class_2338 lastTargetPos;
    private class_243 fromOrigin = class_243.field_1353;
    private class_243 toOrigin = class_243.field_1353;
    private List<class_238> targetBoxes = Collections.emptyList();
    private static final RenderPipeline MASK_PIPELINE;
    private static final class_1921 MASK_LAYER;
    private static final class_2960 CONCRETE_TEXTURE;
    private static final RenderPipeline SHADER_OVERLAY_PIPELINE;
    private static final class_1921 SHADER_OVERLAY_LAYER;
    private static final RenderPipeline SHADER_CHAMS_FILL_PIPELINE;
    private static final class_1921 SHADER_CHAMS_FILL_LAYER;

    public GlassBlock() {
        instance = this;
    }

    public static GlassBlock getInstance() {
        return instance;
    }

    @Override
    protected void onEnable() {
        this.targetPos = null;
        this.lastTargetPos = null;
        this.fromOrigin = class_243.field_1353;
        this.toOrigin = class_243.field_1353;
        this.moveAnimation.set(1.0);
        this.alphaAnimation.set(0.0);
        GlassBlockRenderer renderer = GlassBlockRenderer.getInstance();
        renderer.invalidate();
        renderer.setEnabled(true);
        this.updateRendererSettings();
    }

    @Override
    protected void onDisable() {
        GlassBlockRenderer.getInstance().setEnabled(false);
        this.targetPos = null;
        this.lastTargetPos = null;
        this.targetBoxes = Collections.emptyList();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        this.targetPos = null;
        this.lastTargetPos = null;
        this.targetBoxes = Collections.emptyList();
        GlassBlockRenderer renderer = GlassBlockRenderer.getInstance();
        renderer.invalidate();
        renderer.setEnabled(true);
        this.updateRendererSettings();
    }

    @EventHandler
    public void onRender3D(EventRender3D event) {
        if (!this.isEnabled() || GlassBlock.mc.field_1687 == null || GlassBlock.mc.field_1724 == null) {
            return;
        }
        this.updateTarget();
        this.moveAnimation.update();
        this.alphaAnimation.update();
        if (this.alphaAnimation.get() <= 0.01f || this.targetBoxes.isEmpty()) {
            return;
        }
        if (this.enableShaderFill.getValue().booleanValue()) {
            this.renderShaderOverlay(event, this.getAnimatedOrigin(), this.alphaAnimation.get());
        } else {
            GlassBlockRenderer renderer = GlassBlockRenderer.getInstance();
            this.updateRendererSettings();
            if (!renderer.captureSceneBeforeBlock()) {
                return;
            }
            this.renderMask(event, this.getAnimatedOrigin(), this.alphaAnimation.get());
            renderer.captureSceneAfterBlock();
            renderer.renderGlassEffect();
        }
    }

    private void updateTarget() {
        class_2338 newTarget = this.getLookedBlock();
        if (newTarget != null) {
            List<class_238> boxes;
            class_2680 state = GlassBlock.mc.field_1687.method_8320(newTarget);
            class_265 shape = state.method_26218((class_1922)GlassBlock.mc.field_1687, newTarget);
            if (shape.method_1110()) {
                shape = state.method_26220((class_1922)GlassBlock.mc.field_1687, newTarget);
            }
            this.targetBoxes = boxes = shape.method_1110() ? Collections.singletonList(new class_238(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)) : shape.method_1090();
        }
        if (newTarget == null) {
            this.targetPos = null;
            this.alphaAnimation.run(0.0, this.moveSpeed.getValue().floatValue(), Easings.QUAD_OUT, true);
            return;
        }
        if (!newTarget.equals((Object)this.targetPos)) {
            this.fromOrigin = this.lastTargetPos == null ? class_243.method_24954((class_2382)newTarget) : this.getAnimatedOrigin();
            this.toOrigin = class_243.method_24954((class_2382)newTarget);
            this.targetPos = newTarget;
            this.lastTargetPos = newTarget;
            this.moveAnimation.set(0.0);
            this.moveAnimation.run(1.0, (double)this.moveSpeed.getValue().floatValue(), Easings.QUAD_OUT);
        }
        this.alphaAnimation.run(1.0, this.moveSpeed.getValue().floatValue() * 0.75f, Easings.QUAD_OUT, true);
    }

    private class_2338 getLookedBlock() {
        class_3965 blockHit;
        block5: {
            block4: {
                class_239 hitResult = GlassBlock.mc.field_1765;
                if (!(hitResult instanceof class_3965)) break block4;
                blockHit = (class_3965)hitResult;
                if (hitResult.method_17783() == class_239.class_240.field_1332) break block5;
            }
            return null;
        }
        class_2338 pos = blockHit.method_17777();
        if (GlassBlock.mc.field_1687 == null || GlassBlock.mc.field_1687.method_8320(pos).method_26215()) {
            return null;
        }
        return pos;
    }

    private class_243 getAnimatedOrigin() {
        float t = this.moveAnimation.get();
        return new class_243(this.fromOrigin.field_1352 + (this.toOrigin.field_1352 - this.fromOrigin.field_1352) * (double)t, this.fromOrigin.field_1351 + (this.toOrigin.field_1351 - this.fromOrigin.field_1351) * (double)t, this.fromOrigin.field_1350 + (this.toOrigin.field_1350 - this.fromOrigin.field_1350) * (double)t);
    }

    private void renderMask(EventRender3D event, class_243 origin, float alpha) {
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        class_4588 buffer = immediate.method_73477(MASK_LAYER);
        Matrix4f matrix = event.getMatrixStack().method_23760().method_23761();
        class_243 cam = GlassBlock.mc.field_1773.method_19418().method_71156();
        int color = (int)(255.0f * alpha) << 24 | 0xFFFFFF;
        for (class_238 box : this.targetBoxes) {
            double x0 = origin.field_1352 + box.field_1323 - cam.field_1352;
            double y0 = origin.field_1351 + box.field_1322 - cam.field_1351;
            double z0 = origin.field_1350 + box.field_1321 - cam.field_1350;
            double x1 = origin.field_1352 + box.field_1320 - cam.field_1352;
            double y1 = origin.field_1351 + box.field_1325 - cam.field_1351;
            double z1 = origin.field_1350 + box.field_1324 - cam.field_1350;
            GlassBlock.drawBox(buffer, matrix, x0, y0, z0, x1, y1, z1, color);
        }
        immediate.method_22993();
    }

    private void updateRendererSettings() {
        GlassBlockRenderer renderer = GlassBlockRenderer.getInstance();
        renderer.setBlurEnabled(this.enableBlur.getValue());
        renderer.setBlurRadius(this.blurRadius.getValue().floatValue());
        renderer.setBlurIterations(this.blurIterations.getValue().intValue());
        renderer.setSaturation(this.saturation.getValue().floatValue());
        renderer.setReflect(true);
        if (this.enableBlur.getValue().booleanValue() && this.enableTint.getValue().booleanValue()) {
            renderer.setTintColor(this.tintColor.getValue());
            renderer.setTintIntensity(this.tintIntensity.getValue().floatValue());
        } else {
            renderer.setTintColor(0);
            renderer.setTintIntensity(0.0f);
        }
        renderer.setOutlineEnabled(this.enableEdgeGlow.getValue());
        if (this.enableEdgeGlow.getValue().booleanValue()) {
            float strength = this.edgeGlowIntensity.getValue().floatValue() * 20.0f;
            renderer.setOutlineGlowStrength(strength);
            int outlineRgb = this.enableTint.getValue() != false ? this.tintColor.getValue() : -1;
            renderer.setOutlineColor(outlineRgb);
            renderer.setShimmerEnabled(this.shimmer.getValue());
            renderer.setShimmerWidth(this.shimmerWidth.getValue().floatValue());
            renderer.setShimmerPeriodSec(this.shimmerPeriod.getValue().floatValue());
        }
    }

    private static void drawBox(class_4588 buffer, Matrix4f matrix, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color) {
        float x0 = (float)minX;
        float x1 = (float)maxX;
        float y0 = (float)minY;
        float y1 = (float)maxY;
        float z0 = (float)minZ;
        float z1 = (float)maxZ;
        buffer.method_22918((Matrix4fc)matrix, x0, y0, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y0, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y0, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x0, y0, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x0, y1, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x0, y1, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y1, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y1, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x0, y0, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y0, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y1, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x0, y1, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y0, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x0, y0, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x0, y1, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y1, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x0, y0, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x0, y0, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x0, y1, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x0, y1, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y0, z1).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y0, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y1, z0).method_39415(color);
        buffer.method_22918((Matrix4fc)matrix, x1, y1, z1).method_39415(color);
    }

    private void renderShaderOverlay(EventRender3D event, class_243 origin, float alpha) {
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        boolean chamsFill = this.shaderMode.is("ChamsFill");
        class_1921 layer = chamsFill ? SHADER_CHAMS_FILL_LAYER : SHADER_OVERLAY_LAYER;
        class_4588 buffer = immediate.method_73477(layer);
        Matrix4f matrix = event.getMatrixStack().method_23760().method_23761();
        class_243 cam = GlassBlock.mc.field_1773.method_19418().method_71156();
        int color = this.shaderFillColor.getValue();
        float modeOffset = chamsFill ? 0.0f : (float)this.shaderModeIndex() * 2.0f;
        float inset = 0.001f;
        for (class_238 box : this.targetBoxes) {
            float x0 = (float)(origin.field_1352 + box.field_1323 - cam.field_1352);
            float y0 = (float)(origin.field_1351 + box.field_1322 - cam.field_1351);
            float z0 = (float)(origin.field_1350 + box.field_1321 - cam.field_1350);
            float x1 = (float)(origin.field_1352 + box.field_1320 - cam.field_1352);
            float y1 = (float)(origin.field_1351 + box.field_1325 - cam.field_1351);
            float z1 = (float)(origin.field_1350 + box.field_1324 - cam.field_1350);
            int a = (int)(class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f) * 255.0f);
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            int vertexColor = a << 24 | r << 16 | g << 8 | b;
            GlassBlock.shaderQuad(buffer, matrix, vertexColor, x0, y1 + inset, z1, x1, y1 + inset, z1, x1, y1 + inset, z0, x0, y1 + inset, z0, modeOffset);
            GlassBlock.shaderQuad(buffer, matrix, vertexColor, x0, y0 - inset, z0, x1, y0 - inset, z0, x1, y0 - inset, z1, x0, y0 - inset, z1, modeOffset);
            GlassBlock.shaderQuad(buffer, matrix, vertexColor, x0, y0, z0 - inset, x0, y1, z0 - inset, x1, y1, z0 - inset, x1, y0, z0 - inset, modeOffset);
            GlassBlock.shaderQuad(buffer, matrix, vertexColor, x0, y0, z1 + inset, x1, y0, z1 + inset, x1, y1, z1 + inset, x0, y1, z1 + inset, modeOffset);
            GlassBlock.shaderQuad(buffer, matrix, vertexColor, x0 - inset, y0, z0, x0 - inset, y0, z1, x0 - inset, y1, z1, x0 - inset, y1, z0, modeOffset);
            GlassBlock.shaderQuad(buffer, matrix, vertexColor, x1 + inset, y0, z0, x1 + inset, y1, z0, x1 + inset, y1, z1, x1 + inset, y0, z1, modeOffset);
        }
        immediate.method_22993();
    }

    private static void shaderQuad(class_4588 buffer, Matrix4f matrix, int color, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float modeOffset) {
        GlassBlock.shaderVertex(buffer, matrix, color, x1, y1, z1, 0.0f, 1.0f + modeOffset);
        GlassBlock.shaderVertex(buffer, matrix, color, x2, y2, z2, 1.0f, 1.0f + modeOffset);
        GlassBlock.shaderVertex(buffer, matrix, color, x3, y3, z3, 1.0f, 0.0f + modeOffset);
        GlassBlock.shaderVertex(buffer, matrix, color, x4, y4, z4, 0.0f, 0.0f + modeOffset);
    }

    private static void shaderVertex(class_4588 buffer, Matrix4f matrix, int color, float x, float y, float z, float u, float v) {
        buffer.method_22918((Matrix4fc)matrix, x, y, z).method_22913(u, v).method_39415(color).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
    }

    private int shaderModeIndex() {
        if (this.shaderMode.is("WebShader")) {
            return 2;
        }
        if (this.shaderMode.is("Plasma")) {
            return 4;
        }
        if (this.shaderMode.is("BaseWarp")) {
            return 8;
        }
        if (this.shaderMode.is("\u041a\u043e\u0441\u043c\u043e\u0441")) {
            return 1;
        }
        if (this.shaderMode.is("\u0413\u0438\u0440\u043e\u0438\u0434")) {
            return 3;
        }
        if (this.shaderMode.is("\u0424\u0435\u043d\u0435\u043b\u044c")) {
            return 5;
        }
        if (this.shaderMode.is("\u041e\u0431\u043b\u0430\u043a\u0430")) {
            return 6;
        }
        if (this.shaderMode.is("\u041d\u0435\u0431\u0443\u043b\u0430")) {
            return 7;
        }
        return 0;
    }

    @Generated
    public BooleanSetting getEnableBlur() {
        return this.enableBlur;
    }

    @Generated
    public SliderSetting getBlurRadius() {
        return this.blurRadius;
    }

    @Generated
    public SliderSetting getBlurIterations() {
        return this.blurIterations;
    }

    @Generated
    public SliderSetting getSaturation() {
        return this.saturation;
    }

    @Generated
    public BooleanSetting getEnableTint() {
        return this.enableTint;
    }

    @Generated
    public SliderSetting getTintIntensity() {
        return this.tintIntensity;
    }

    @Generated
    public ColorSetting getTintColor() {
        return this.tintColor;
    }

    @Generated
    public BooleanSetting getEnableEdgeGlow() {
        return this.enableEdgeGlow;
    }

    @Generated
    public SliderSetting getEdgeGlowIntensity() {
        return this.edgeGlowIntensity;
    }

    @Generated
    public BooleanSetting getShimmer() {
        return this.shimmer;
    }

    @Generated
    public SliderSetting getShimmerWidth() {
        return this.shimmerWidth;
    }

    @Generated
    public SliderSetting getShimmerPeriod() {
        return this.shimmerPeriod;
    }

    @Generated
    public SliderSetting getMoveSpeed() {
        return this.moveSpeed;
    }

    @Generated
    public BooleanSetting getEnableShaderFill() {
        return this.enableShaderFill;
    }

    @Generated
    public ModeSetting getShaderMode() {
        return this.shaderMode;
    }

    @Generated
    public SliderSetting getShaderFillAlpha() {
        return this.shaderFillAlpha;
    }

    @Generated
    public ColorSetting getShaderFillColor() {
        return this.shaderFillColor;
    }

    @Generated
    public class_9799 getAllocator() {
        return this.allocator;
    }

    @Generated
    public Animation getMoveAnimation() {
        return this.moveAnimation;
    }

    @Generated
    public Animation getAlphaAnimation() {
        return this.alphaAnimation;
    }

    @Generated
    public class_2338 getTargetPos() {
        return this.targetPos;
    }

    @Generated
    public class_2338 getLastTargetPos() {
        return this.lastTargetPos;
    }

    @Generated
    public class_243 getFromOrigin() {
        return this.fromOrigin;
    }

    @Generated
    public class_243 getToOrigin() {
        return this.toOrigin;
    }

    @Generated
    public List<class_238> getTargetBoxes() {
        return this.targetBoxes;
    }

    static {
        MASK_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"glass_block_mask")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).build());
        MASK_LAYER = class_1921.method_75940((String)"glass_block_mask", (class_12247)class_12247.method_75927((RenderPipeline)MASK_PIPELINE).method_75929(4096).method_75938());
        CONCRETE_TEXTURE = class_2960.method_60655((String)"minecraft", (String)"textures/block/white_concrete.png");
        SHADER_OVERLAY_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"block_overlay_shader")).withVertexShader(class_2960.method_60655((String)"decide", (String)"block_overlay_shader_vertex")).withFragmentShader(class_2960.method_60655((String)"decide", (String)"block_overlay_shader_fragment")).withUniform("Globals", class_10789.field_60031).withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
        SHADER_OVERLAY_LAYER = class_1921.method_75940((String)"client_block_overlay_shader", (class_12247)class_12247.method_75927((RenderPipeline)SHADER_OVERLAY_PIPELINE).method_75934("Sampler0", CONCRETE_TEXTURE).method_75937().method_75931(class_12246.field_63983).method_75929(1536).method_75938());
        SHADER_CHAMS_FILL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"block_overlay_chams_fill")).withVertexShader(class_2960.method_60655((String)"decide", (String)"block_overlay_shader_vertex")).withFragmentShader(class_2960.method_60655((String)"decide", (String)"block_overlay_chams_fill_fragment")).withUniform("Globals", class_10789.field_60031).withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build());
        SHADER_CHAMS_FILL_LAYER = class_1921.method_75940((String)"client_block_overlay_chams_fill", (class_12247)class_12247.method_75927((RenderPipeline)SHADER_CHAMS_FILL_PIPELINE).method_75934("Sampler0", CONCRETE_TEXTURE).method_75937().method_75929(1536).method_75938());
    }
}

