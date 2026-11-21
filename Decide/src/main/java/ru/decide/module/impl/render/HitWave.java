/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_12245
 *  net.minecraft.class_12246
 *  net.minecraft.class_12247
 *  net.minecraft.class_1921
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_290
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.decide.module.impl.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12245;
import net.minecraft.class_12246;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_290;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;

@ModuleInfo(name="Hit Wave", desc="\u042d\u0444\u0444\u0435\u043a\u0442 \u0432\u043e\u043b\u043d\u044b \u043f\u0440\u0438 \u0443\u0434\u0430\u0440\u0435", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class HitWave
extends Module {
    private static final RenderPipeline WAVE_LINES_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56859}).withLocation("pipeline/hitwave_lines").withBlend(BlendFunction.TRANSLUCENT).withDepthWrite(false).withVertexFormat(class_290.field_63455, VertexFormat.class_5596.field_29344).build());
    private static final class_1921 WAVE_LINES = class_1921.method_75940((String)"hitwave_lines", (class_12247)class_12247.method_75927((RenderPipeline)WAVE_LINES_PIPELINE).method_75930(class_12245.field_63976).method_75931(class_12246.field_63983).method_75938());
    private static final RenderPipeline WAVE_FILL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation("pipeline/hitwave_fill").withBlend(BlendFunction.TRANSLUCENT).withDepthWrite(false).withCull(false).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).build());
    private static final class_1921 WAVE_FILL = class_1921.method_75940((String)"hitwave_fill", (class_12247)class_12247.method_75927((RenderPipeline)WAVE_FILL_PIPELINE).method_75937().method_75929(1536).method_75938());
    public BooleanSetting fill = new BooleanSetting((Module)this, "\u0417\u0430\u043b\u0438\u0432\u043a\u0430", true);
    public BooleanSetting outline = new BooleanSetting((Module)this, "\u041a\u043e\u043d\u0442\u0443\u0440", true);
    public SliderSetting lineWidth = new SliderSetting(this, "\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u043b\u0438\u043d\u0438\u0439", 2.0f, 0.5f, 5.0f, 0.1f);
    public SliderSetting fillAlpha = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0437\u0430\u043b\u0438\u0432\u043a\u0438", 0.15f, 0.01f, 1.0f, 0.01f);
    public SliderSetting duration = new SliderSetting(this, "\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c (\u0441\u0435\u043a)", 1.5f, 0.5f, 3.0f, 0.1f);
    public SliderSetting maxRadius = new SliderSetting(this, "\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u044b\u0439 \u0440\u0430\u0434\u0438\u0443\u0441", 12.0f, 5.0f, 20.0f, 1.0f);
    public SliderSetting waveWidth = new SliderSetting(this, "\u0428\u0438\u0440\u0438\u043d\u0430 \u0432\u043e\u043b\u043d\u044b", 2.5f, 1.0f, 5.0f, 0.5f);
    public BooleanSetting useThemeColor = new BooleanSetting((Module)this, "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0446\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", true);
    public ColorSetting waveColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0432\u043e\u043b\u043d\u044b", -1).setVisible(() -> this.useThemeColor.getValue() == false);
    private final class_9799 allocator = new class_9799(262144);
    private final List<WaveEffect> waveEffects = new ArrayList<WaveEffect>();

    @EventHandler
    public void onAttack(AttackEvent e) {
        if (e.getTarget() == null || HitWave.mc.field_1687 == null || !this.isEnabled()) {
            return;
        }
        class_243 pos = e.getTarget().method_73189();
        class_2338 basePos = class_2338.method_49637((double)pos.field_1352, (double)(pos.field_1351 - 0.1), (double)pos.field_1350);
        this.waveEffects.add(new WaveEffect(basePos, System.currentTimeMillis()));
    }

    @EventHandler
    public void onRender(EventRender3D e) {
        if (HitWave.mc.field_1687 == null || !this.isEnabled()) {
            return;
        }
        if (this.waveEffects.isEmpty()) {
            return;
        }
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        class_4587 matrices = e.getMatrixStack();
        class_243 camera = HitWave.mc.field_1773.method_19418().method_71156();
        Iterator<WaveEffect> iterator = this.waveEffects.iterator();
        while (iterator.hasNext()) {
            WaveEffect wave = iterator.next();
            if (wave.isExpired()) {
                iterator.remove();
                continue;
            }
            wave.render(matrices, immediate, camera);
        }
        immediate.method_22993();
    }

    @Override
    protected void onDisable() {
        this.waveEffects.clear();
    }

    private int getColor() {
        if (this.useThemeColor.getValue().booleanValue()) {
            return ColorUtil.fade(1);
        }
        return this.waveColor.getValue();
    }

    private static int brighten(int value) {
        return (int)Math.min(255.0f, (float)value + (float)(255 - value) * 0.45f);
    }

    private static int core(int value) {
        return (int)Math.min(255.0f, (float)value + (float)(255 - value) * 0.72f);
    }

    private static void drawBoxFill(class_4597.class_4598 immediate, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, int color) {
        class_4588 buf = immediate.method_73477(WAVE_FILL);
        HitWave.quad(buf, m, x1, y2, z2, x2, y2, z2, x2, y2, z1, x1, y2, z1, color);
        HitWave.quad(buf, m, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, color);
        HitWave.quad(buf, m, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, color);
        HitWave.quad(buf, m, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, color);
        HitWave.quad(buf, m, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1, color);
        HitWave.quad(buf, m, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, color);
    }

    private static void quad(class_4588 buf, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int color) {
        buf.method_22918((Matrix4fc)m, x1, y1, z1).method_39415(color);
        buf.method_22918((Matrix4fc)m, x2, y2, z2).method_39415(color);
        buf.method_22918((Matrix4fc)m, x3, y3, z3).method_39415(color);
        buf.method_22918((Matrix4fc)m, x4, y4, z4).method_39415(color);
    }

    private static void drawBoxOutline(class_4597.class_4598 immediate, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, int color, float width) {
        class_4588 buf = immediate.method_73477(WAVE_LINES);
        HitWave.line(buf, m, x1, y1, z1, x2, y1, z1, color, width);
        HitWave.line(buf, m, x2, y1, z1, x2, y1, z2, color, width);
        HitWave.line(buf, m, x2, y1, z2, x1, y1, z2, color, width);
        HitWave.line(buf, m, x1, y1, z2, x1, y1, z1, color, width);
        HitWave.line(buf, m, x1, y2, z1, x2, y2, z1, color, width);
        HitWave.line(buf, m, x2, y2, z1, x2, y2, z2, color, width);
        HitWave.line(buf, m, x2, y2, z2, x1, y2, z2, color, width);
        HitWave.line(buf, m, x1, y2, z2, x1, y2, z1, color, width);
        HitWave.line(buf, m, x1, y1, z1, x1, y2, z1, color, width);
        HitWave.line(buf, m, x2, y1, z1, x2, y2, z1, color, width);
        HitWave.line(buf, m, x2, y1, z2, x2, y2, z2, color, width);
        HitWave.line(buf, m, x1, y1, z2, x1, y2, z2, color, width);
    }

    private static void line(class_4588 buf, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, int color, float width) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float len = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len <= 1.0E-4f) {
            return;
        }
        buf.method_22918((Matrix4fc)m, x1, y1, z1).method_39415(color).method_22914(dx / len, dy / len, dz / len).method_75298(width);
        buf.method_22918((Matrix4fc)m, x2, y2, z2).method_39415(color).method_22914(dx / len, dy / len, dz / len).method_75298(width);
    }

    @Environment(value=EnvType.CLIENT)
    private class WaveEffect {
        private final class_2338 centerPos;
        private final long startTime;
        private final List<CachedBlock> cachedBlocks = new ArrayList<CachedBlock>();

        WaveEffect(class_2338 centerPos, long startTime) {
            this.centerPos = centerPos;
            this.startTime = startTime;
            int maxR = (int)HitWave.this.maxRadius.getValue().floatValue();
            int maxR2 = maxR * maxR;
            for (int x = -maxR; x <= maxR; ++x) {
                for (int z = -maxR; z <= maxR; ++z) {
                    class_2680 state;
                    class_265 shape;
                    class_2338 checkPos;
                    class_2338 renderPos;
                    float distSq = x * x + z * z;
                    if (distSq > (float)maxR2 || (renderPos = this.findSurface(checkPos = centerPos.method_10069(x, 0, z))) == null || (shape = (state = IMinecraft.mc.field_1687.method_8320(renderPos)).method_26218((class_1922)IMinecraft.mc.field_1687, renderPos)).method_1110()) continue;
                    this.cachedBlocks.add(new CachedBlock(this, renderPos, shape.method_1107(), (float)Math.sqrt(distSq)));
                }
            }
        }

        boolean isExpired() {
            return System.currentTimeMillis() - this.startTime > (long)(HitWave.this.duration.getValue().floatValue() * 1000.0f);
        }

        void render(class_4587 matrices, class_4597.class_4598 immediate, class_243 camera) {
            long elapsed = System.currentTimeMillis() - this.startTime;
            float progress = (float)elapsed / (HitWave.this.duration.getValue().floatValue() * 1000.0f);
            float currentRadius = progress * HitWave.this.maxRadius.getValue().floatValue();
            float waveW = HitWave.this.waveWidth.getValue().floatValue();
            float globalAlpha = (float)Math.pow(1.0f - progress, 0.6);
            int rendered = 0;
            int maxPerFrame = 200;
            float minRadSq = (currentRadius - waveW) * (currentRadius - waveW);
            float maxRadSq = (currentRadius + 0.5f) * (currentRadius + 0.5f);
            int baseColor = HitWave.this.getColor();
            int red = HitWave.this.useThemeColor.getValue() != false ? HitWave.brighten(ColorUtil.red(baseColor)) : ColorUtil.red(baseColor);
            int green = HitWave.this.useThemeColor.getValue() != false ? HitWave.brighten(ColorUtil.green(baseColor)) : ColorUtil.green(baseColor);
            int blue = HitWave.this.useThemeColor.getValue() != false ? HitWave.brighten(ColorUtil.blue(baseColor)) : ColorUtil.blue(baseColor);
            int glowColor = ColorUtil.getColor(red, green, blue, 255);
            int coreColor = ColorUtil.getColor(HitWave.core(red), HitWave.core(green), HitWave.core(blue), 255);
            for (CachedBlock cb : this.cachedBlocks) {
                if (rendered >= maxPerFrame) break;
                float distance = cb.distance;
                if (distance < minRadSq || distance > maxRadSq) continue;
                ++rendered;
                float localAlpha = 1.0f - Math.abs(distance - currentRadius) / waveW;
                if (!((localAlpha = Math.max(0.0f, Math.min(1.0f, localAlpha)) * globalAlpha) > 0.05f)) continue;
                this.drawBlock(matrices, immediate, camera, cb, glowColor, coreColor, localAlpha);
            }
        }

        private void drawBlock(class_4587 matrices, class_4597.class_4598 immediate, class_243 camera, CachedBlock cb, int glowColor, int coreColor, float localAlpha) {
            class_238 worldBox = cb.box.method_996(cb.pos);
            float sx = (float)(worldBox.field_1323 - camera.field_1352);
            float sy = (float)(worldBox.field_1322 - camera.field_1351);
            float sz = (float)(worldBox.field_1321 - camera.field_1350);
            float w = (float)worldBox.method_17939();
            float h = (float)worldBox.method_17940();
            float d = (float)worldBox.method_17941();
            matrices.method_22903();
            matrices.method_46416(sx, sy, sz);
            Matrix4f m = matrices.method_23760().method_23761();
            if (HitWave.this.fill.getValue().booleanValue()) {
                int fAlpha = (int)(HitWave.this.fillAlpha.getValue().floatValue() * localAlpha * 255.0f);
                HitWave.drawBoxFill(immediate, m, 0.0f, 0.0f, 0.0f, w, h, d, ColorUtil.replAlpha(glowColor, fAlpha));
            }
            if (HitWave.this.outline.getValue().booleanValue()) {
                HitWave.drawBoxOutline(immediate, m, 0.0f, 0.0f, 0.0f, w, h, d, ColorUtil.replAlpha(coreColor, (int)(localAlpha * 255.0f)), HitWave.this.lineWidth.getValue().floatValue());
            }
            matrices.method_22909();
        }

        private class_2338 findSurface(class_2338 pos) {
            for (int y = 2; y >= -4; --y) {
                boolean upSolid;
                class_2338 p = pos.method_10086(y);
                class_2680 state = IMinecraft.mc.field_1687.method_8320(p);
                class_2680 upState = IMinecraft.mc.field_1687.method_8320(p.method_10084());
                boolean currentSolid = !state.method_26220((class_1922)IMinecraft.mc.field_1687, p).method_1110();
                boolean bl = upSolid = !upState.method_26220((class_1922)IMinecraft.mc.field_1687, p.method_10084()).method_1110();
                if (!currentSolid || upSolid) continue;
                return p;
            }
            return null;
        }

        @Environment(value=EnvType.CLIENT)
        private class CachedBlock {
            final class_2338 pos;
            final class_238 box;
            final float distance;

            CachedBlock(WaveEffect waveEffect, class_2338 pos, class_238 box, float distance) {
                this.pos = pos;
                this.box = box;
                this.distance = distance;
            }
        }
    }
}

