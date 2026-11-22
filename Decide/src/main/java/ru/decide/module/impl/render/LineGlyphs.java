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
 *  net.minecraft.class_12245
 *  net.minecraft.class_12246
 *  net.minecraft.class_12247
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2902$class_2903
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
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12245;
import net.minecraft.class_12246;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2902;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;

@ModuleInfo(name="Line Glyphs", desc="\u0414\u043b\u0438\u043d\u043d\u044b\u0435 \u0441\u043b\u0443\u0447\u0430\u0439\u043d\u043e \u043f\u043e\u0432\u043e\u0440\u0430\u0447\u0438\u0432\u0430\u044e\u0449\u0438\u0435\u0441\u044f \u043b\u0438\u043d\u0438\u0438", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class LineGlyphs
extends Module {
    private static final RenderPipeline LINE_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56859}).withLocation("pipeline/line_glyphs").withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withVertexFormat(class_290.field_63455, VertexFormat.class_5596.field_29344).build());
    private static final class_1921 LINE_LAYER = class_1921.method_75940((String)"line_glyphs", (class_12247)class_12247.method_75927((RenderPipeline)LINE_PIPELINE).method_75930(class_12245.field_63976).method_75931(class_12246.field_63983).method_75938());
    private static final float LIFETIME = 15.0f;
    private static final float FADE_DURATION = 2.0f;
    public SliderSetting count = new SliderSetting(this, "\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e", 40.0f, 5.0f, 120.0f, 1.0f);
    public SliderSetting speed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 0.6f, 0.2f, 5.0f, 0.1f);
    public SliderSetting width = new SliderSetting(this, "\u0422\u043e\u043b\u0449\u0438\u043d\u0430", 1.2f, 0.8f, 6.0f, 0.1f);
    public BooleanSetting themeColor = new BooleanSetting((Module)this, "\u0426\u0432\u0435\u0442 \u043e\u0442 \u0442\u0435\u043c\u044b", true);
    public ColorSetting color = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", -6234881).setVisible(() -> this.themeColor.getValue() == false);
    private final class_9799 allocator = new class_9799(262144);
    private final Deque<Glyph> glyphs = new ArrayDeque<Glyph>();
    private final Random random = new Random();

    @EventHandler
    public void onTick(EventTick event) {
        if (!this.isEnabled() || LineGlyphs.mc.field_1724 == null || LineGlyphs.mc.field_1687 == null) {
            return;
        }
        long now = System.currentTimeMillis();
        float step = (float)((double)this.speed.getValue().floatValue() * 0.14);
        for (Glyph glyph : this.glyphs) {
            if ((float)(now - glyph.birthTime) / 1000.0f >= 15.0f) {
                glyph.reset(now);
                continue;
            }
            glyph.update(step);
        }
        int targetCount = (int)this.count.getValue().floatValue();
        while (this.glyphs.size() < targetCount) {
            this.glyphs.addLast(this.spawnGlyph(now));
        }
    }

    private Glyph spawnGlyph(long now) {
        class_243 playerPos = LineGlyphs.mc.field_1724.method_73189();
        double angle = this.random.nextDouble() * Math.PI * 2.0;
        double distance = 3.0 + this.random.nextDouble() * 6.0;
        class_243 origin = playerPos.method_1031(Math.cos(angle) * distance, 0.0, Math.sin(angle) * distance).method_1031(0.0, 1.0 + this.random.nextDouble() * 4.0, 0.0);
        double cardinal = (double)Math.round(this.random.nextDouble() * 4.0) % 4.0 * 1.5707963267948966;
        return new Glyph(origin, cardinal, now);
    }

    @EventHandler
    public void onRender(EventRender3D e) {
        if (!this.isEnabled() || LineGlyphs.mc.field_1724 == null || LineGlyphs.mc.field_1687 == null) {
            return;
        }
        if (this.glyphs.isEmpty()) {
            return;
        }
        int selected = this.themeColor.getValue() != false ? ColorUtil.fade(1) : this.color.getValue();
        class_243 camera = LineGlyphs.mc.field_1773.method_19418().method_71156();
        float lineWidth = this.width.getValue().floatValue();
        Matrix4f matrix = e.getMatrixStack().method_23760().method_23761();
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        class_4588 buffer = immediate.method_73477(LINE_LAYER);
        for (Glyph glyph : this.glyphs) {
            float age = (float)(System.currentTimeMillis() - glyph.birthTime) / 1000.0f;
            float remaining = 15.0f - age;
            float fade = remaining < 2.0f ? Math.max(0.0f, remaining / 2.0f) : 1.0f;
            glyph.drawPath(buffer, matrix, camera, selected, lineWidth, fade);
        }
        immediate.method_22993();
    }

    @Override
    protected void onEnable() {
        this.glyphs.clear();
    }

    @Override
    protected void onDisable() {
        this.glyphs.clear();
    }

    @Environment(value=EnvType.CLIENT)
    private class Glyph {
        private class_243 pos;
        private double yaw;
        private int turnTimer;
        private boolean rising;
        private double targetY = -1.0;
        private long birthTime;
        private final Deque<class_243> path = new ArrayDeque<class_243>();
        private static final int MAX_PATH = 36;
        private static final float MAX_RADIUS = 14.0f;

        Glyph(class_243 pos, double yaw, long birthTime) {
            this.pos = pos;
            this.yaw = yaw;
            this.turnTimer = 0;
            this.birthTime = birthTime;
            this.path.addLast(pos);
        }

        void reset(long now) {
            class_243 playerPos = IMinecraft.mc.field_1724 != null ? IMinecraft.mc.field_1724.method_73189() : this.pos;
            double angle = LineGlyphs.this.random.nextDouble() * Math.PI * 2.0;
            double distance = 3.0 + LineGlyphs.this.random.nextDouble() * 6.0;
            this.pos = playerPos.method_1031(Math.cos(angle) * distance, 0.0, Math.sin(angle) * distance).method_1031(0.0, 1.0 + LineGlyphs.this.random.nextDouble() * 4.0, 0.0);
            this.yaw = (double)Math.round(LineGlyphs.this.random.nextDouble() * 4.0) % 4.0 * 1.5707963267948966;
            this.turnTimer = 0;
            this.rising = false;
            this.targetY = -1.0;
            this.birthTime = now;
            this.path.clear();
            this.path.addLast(this.pos);
        }

        void update(float step) {
            if (this.rising) {
                double groundY = this.groundTop(this.pos.field_1352, this.pos.field_1350);
                double nextY = this.pos.field_1351 + (double)step * 0.5;
                if (nextY >= this.targetY) {
                    nextY = this.targetY;
                    this.rising = false;
                }
                if (nextY < groundY) {
                    nextY = groundY;
                }
                this.pos = new class_243(this.pos.field_1352, nextY, this.pos.field_1350);
                this.path.addLast(this.pos);
                if (this.path.size() > 36) {
                    this.path.removeFirst();
                }
                return;
            }
            if (this.turnTimer <= 0) {
                this.turnTimer = 60;
                this.chooseTurn();
                if (LineGlyphs.this.random.nextDouble() < 0.3) {
                    this.rising = true;
                    this.targetY = this.pos.field_1351 + 0.3 + LineGlyphs.this.random.nextDouble() * 0.7;
                }
            }
            --this.turnTimer;
            class_243 playerPos = IMinecraft.mc.field_1724.method_73189();
            double dist = Math.hypot(this.pos.field_1352 - playerPos.field_1352, this.pos.field_1350 - playerPos.field_1350);
            if (dist > 14.0) {
                this.yaw = this.snapCardinal(Math.atan2(playerPos.field_1350 - this.pos.field_1350, playerPos.field_1352 - this.pos.field_1352));
                this.rising = false;
                this.turnTimer = 60;
            }
            this.pos = new class_243(this.pos.field_1352 + Math.cos(this.yaw) * (double)step, this.pos.field_1351, this.pos.field_1350 + Math.sin(this.yaw) * (double)step);
            this.path.addLast(this.pos);
            if (this.path.size() > 36) {
                this.path.removeFirst();
            }
        }

        private void chooseTurn() {
            this.yaw = LineGlyphs.this.random.nextBoolean() ? (this.yaw += 1.5707963267948966) : (this.yaw -= 1.5707963267948966);
        }

        private double snapCardinal(double angle) {
            return (double)Math.round(angle / 1.5707963267948966) * 1.5707963267948966;
        }

        private double groundTop(double x, double z) {
            if (IMinecraft.mc.field_1687 == null) {
                return this.pos.field_1351;
            }
            int top = IMinecraft.mc.field_1687.method_8624(class_2902.class_2903.field_13202, (int)Math.floor(x), (int)Math.floor(z));
            return (double)top + 0.05;
        }

        void drawPath(class_4588 buffer, Matrix4f m, class_243 camera, int color, float width, float fade) {
            class_243 prev = null;
            for (class_243 point : this.path) {
                if (prev != null) {
                    class_243 a = prev.method_1020(camera);
                    class_243 b = point.method_1020(camera);
                    this.line(buffer, m, color, a.field_1352, a.field_1351, a.field_1350, b.field_1352, b.field_1351, b.field_1350, width, fade);
                }
                prev = point;
            }
        }

        private void line(class_4588 buffer, Matrix4f m, int color, double x1, double y1, double z1, double x2, double y2, double z2, float width, float fade) {
            float dx = (float)(x2 - x1);
            float dy = (float)(y2 - y1);
            float dz = (float)(z2 - z1);
            float length = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (length <= 1.0E-4f) {
                return;
            }
            int alpha = (int)(255.0f * Math.min(1.0f, Math.max(0.0f, fade)));
            int c = ColorUtil.replAlpha(color, alpha);
            buffer.method_22918((Matrix4fc)m, (float)x1, (float)y1, (float)z1).method_39415(c).method_22914(dx / length, dy / length, dz / length).method_75298(width);
            buffer.method_22918((Matrix4fc)m, (float)x2, (float)y2, (float)z2).method_39415(c).method_22914(dx / length, dy / length, dz / length).method_75298(width);
        }
    }
}

