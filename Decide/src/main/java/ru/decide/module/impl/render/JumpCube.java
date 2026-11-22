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
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_7833
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.decide.module.impl.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.decide.manager.event_impl.EventJump;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.preview.ModulePreview;
import ru.decide.module.api.preview.PreviewContext;
import ru.decide.module.api.preview.PreviewSettings;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;

@ModuleInfo(name="Jump Cube", desc="\u041a\u0443\u0431\u0438\u043a\u0438 \u0432\u043e\u043a\u0440\u0443\u0433 \u0438\u0433\u0440\u043e\u043a\u0430 - \u043f\u0440\u044b\u0433\u0430\u044e\u0442 \u0438 \u043a\u0440\u0443\u0442\u044f\u0442\u0441\u044f", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class JumpCube
extends Module
implements ModulePreview {
    public ButtonSetting previewButton = PreviewSettings.button(this);
    public ModeSetting typeColor = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439");
    public ColorSetting tintColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", -16711681).setVisible(() -> this.typeColor.is("\u0421\u0432\u043e\u0439"));
    public SliderSetting count = new SliderSetting(this, "\u041a\u0443\u0431\u0438\u043a\u043e\u0432", 3.0f, 1.0f, 10.0f, 1.0f);
    public SliderSetting size = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440", 0.18f, 0.05f, 0.4f, 0.01f);
    private final PreviewSettings previewSettings = PreviewSettings.none(this);
    private static final int RES_PX = 16;
    private static final long LIFETIME_MS = 2000L;
    private static final double SPAWN_MIN_DST = 0.6;
    private static final double SPAWN_MAX_DST = 2.6;
    private static final double MAX_OWNER_DIST = 6.0;
    private static final long RANGE_FADE_MS = 400L;
    private static final int[] JUMP_YAWS = new int[]{0, 90, 180, 270};
    private final Random random = new Random();
    private final List<Cube> cubes = new ArrayList<Cube>();
    private final List<Cube> visible = new ArrayList<Cube>();
    private final class_9799 allocator = new class_9799(65536);
    private static final class_2960 GLOW_TEX_BIG = class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_1.png");
    private static final class_2960 GLOW_TEX_SMALL = class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_2.png");
    private static final RenderPipeline FILL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"jump_cube_fill")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    private static final class_1921 FILL_LAYER = class_1921.method_75940((String)"jump_cube_fill", (class_12247)class_12247.method_75927((RenderPipeline)FILL_PIPELINE).method_75929(8192).method_75938());
    private static final RenderPipeline LINE_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"jump_cube_lines")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_29344).withCull(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    private static final class_1921 LINE_LAYER = class_1921.method_75940((String)"jump_cube_lines", (class_12247)class_12247.method_75927((RenderPipeline)LINE_PIPELINE).method_75929(4096).method_75938());
    private static final RenderPipeline GLOW_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"jump_cube_glow")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    private static final class_1921 GLOW_LAYER_BIG = class_1921.method_75940((String)"jump_cube_glow_big", (class_12247)class_12247.method_75927((RenderPipeline)GLOW_PIPELINE).method_75934("Sampler0", GLOW_TEX_BIG).method_75937().method_75929(4096).method_75938());
    private static final class_1921 GLOW_LAYER_SMALL = class_1921.method_75940((String)"jump_cube_glow_small", (class_12247)class_12247.method_75927((RenderPipeline)GLOW_PIPELINE).method_75934("Sampler0", GLOW_TEX_SMALL).method_75937().method_75929(4096).method_75938());

    @Override
    protected void onDisable() {
        this.cubes.clear();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.cubes.clear();
    }

    @EventHandler
    public void onJump(EventJump e) {
        for (Cube c : this.cubes) {
            c.tryStartJump();
        }
    }

    @Override
    public PreviewSettings previewSettings() {
        return this.previewSettings;
    }

    @Override
    public void previewSpawn(PreviewContext ctx) {
    }

    @Override
    public void previewTick(PreviewContext ctx) {
        for (Cube c : this.cubes) {
            c.tryStartJump();
        }
    }

    @Override
    public void previewStop() {
        this.cubes.clear();
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (JumpCube.mc.field_1724 == null || JumpCube.mc.field_1687 == null) {
            return;
        }
        class_243 playerPos = JumpCube.mc.field_1724.method_73189();
        List<class_2338> candidates = this.getPlaceableAround(playerPos, 0.6, 2.6, 3);
        int toSpawn = this.count.getValue().intValue();
        for (int i = 0; i < toSpawn && !candidates.isEmpty(); ++i) {
            int idx = this.random.nextInt(candidates.size());
            class_243 sp = this.findSpawnPoint(candidates.get(idx), playerPos);
            if (sp == null) continue;
            this.cubes.add(new Cube(sp));
        }
        this.cubes.forEach(Cube::updateLogic);
        this.cubes.removeIf(Cube::isDead);
    }

    @EventHandler
    public void onRender(EventRender3D e) {
        if (JumpCube.mc.field_1724 == null || JumpCube.mc.field_1687 == null || this.cubes.isEmpty()) {
            return;
        }
        float pt = e.getTickDelta();
        class_243 cam = JumpCube.mc.field_1773.method_19418().method_71156();
        class_4587 pose = e.getMatrixStack();
        float s = this.size.getValue().floatValue();
        int base = this.typeColor.is("\u0422\u0435\u043c\u0430") ? ColorUtil.getClientColor1(1) : this.tintColor.getValue();
        int cr = base >> 16 & 0xFF;
        int cg = base >> 8 & 0xFF;
        int cb = base & 0xFF;
        this.visible.clear();
        for (Cube c : this.cubes) {
            float alpha;
            if (c.isDead() || (alpha = c.getAlpha()) < 0.01f) continue;
            double wx = c.spawnPos.field_1352 - cam.field_1352;
            double wy = c.spawnPos.field_1351 + c.getJumpYOffset(pt) - cam.field_1351;
            double wz = c.spawnPos.field_1350 - cam.field_1350;
            pose.method_22903();
            pose.method_22904(wx, wy, wz);
            if (c.jumpTicksMax > 0) {
                float tLerped = class_3532.method_16439((float)pt, (float)c.prevJumpTicks, (float)c.jumpTicks);
                float phase = 1.0f - tLerped / (float)c.jumpTicksMax;
                pose.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees((float)c.jumpYaw));
                pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(90.0f * phase));
            }
            pose.method_22905(alpha, alpha, alpha);
            c.cubeMatrix.set((Matrix4fc)pose.method_23760().method_23761());
            pose.method_22909();
            pose.method_22903();
            pose.method_22904(wx, wy, wz);
            pose.method_22907((Quaternionfc)JumpCube.mc.field_1773.method_19418().method_23767());
            pose.method_22905(alpha, alpha, alpha);
            c.glowMatrix.set((Matrix4fc)pose.method_23760().method_23761());
            pose.method_22909();
            c.cachedAlpha = alpha;
            this.visible.add(c);
        }
        if (this.visible.isEmpty()) {
            return;
        }
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        class_4588 buf = immediate.method_73477(FILL_LAYER);
        for (Cube c : this.visible) {
            int fa = (int)(c.cachedAlpha * 0.2f * 255.0f);
            if (fa <= 0) continue;
            JumpCube.drawCubeFill(buf, c.cubeMatrix, s, cr, cg, cb, fa);
        }
        buf = immediate.method_73477(LINE_LAYER);
        for (Cube c : this.visible) {
            int ea = (int)(c.cachedAlpha * 255.0f);
            if (ea <= 0) continue;
            JumpCube.drawCubeEdges(buf, c.cubeMatrix, s, cr, cg, cb, ea);
        }
        buf = immediate.method_73477(GLOW_LAYER_BIG);
        for (Cube c : this.visible) {
            JumpCube.drawGlow(buf, c.glowMatrix, cr, cg, cb, (int)(35.0f * c.cachedAlpha), s * 5.5f);
        }
        buf = immediate.method_73477(GLOW_LAYER_SMALL);
        for (Cube c : this.visible) {
            JumpCube.drawGlow(buf, c.glowMatrix, cr, cg, cb, (int)(40.0f * c.cachedAlpha), s * 1.8f);
        }
        immediate.method_22993();
    }

    private List<class_2338> getPlaceableAround(class_243 center, double minDst, double maxDst, int offsetDown) {
        ArrayList<class_2338> result = new ArrayList<class_2338>();
        if (JumpCube.mc.field_1687 == null) {
            return result;
        }
        double xE = center.field_1352;
        double yE = center.field_1351 + 1.0;
        double zE = center.field_1350;
        for (double x = xE - maxDst; x < xE + maxDst; x += 1.0) {
            for (double z = zE - maxDst; z < zE + maxDst; z += 1.0) {
                for (double y = yE - (double)offsetDown; y < yE; y += 1.0) {
                    class_2338 pos = class_2338.method_49637((double)x, (double)y, (double)z);
                    if (!this.canPlace(pos) || result.contains(pos)) continue;
                    result.add(pos);
                }
            }
        }
        return result;
    }

    private boolean canPlace(class_2338 pos) {
        if (JumpCube.mc.field_1687 == null) {
            return false;
        }
        if (!JumpCube.mc.field_1687.method_22347(pos)) {
            return false;
        }
        if (JumpCube.mc.field_1687.method_22347(pos.method_10074())) {
            return false;
        }
        return JumpCube.mc.field_1687.method_8335(null, new class_238(pos)).isEmpty();
    }

    private class_243 findSpawnPoint(class_2338 pos, class_243 playerPos) {
        double resOff = 0.0625;
        double half = resOff / 2.0;
        for (int att = 64; att > 0; --att) {
            class_243 v = new class_243((double)pos.method_10263() + this.random.nextDouble(), (double)pos.method_10264() + this.random.nextDouble(), (double)pos.method_10260() + this.random.nextDouble());
            double dst = v.method_1022(playerPos);
            if (!(dst >= 0.6) || !(dst <= 2.6)) continue;
            double sx = Math.floor(v.field_1352 * 16.0) / 16.0 + half;
            double sy = Math.floor(v.field_1351) + half - resOff;
            double sz = Math.floor(v.field_1350 * 16.0) / 16.0 + half;
            return new class_243(sx, sy, sz);
        }
        return null;
    }

    private static void drawCubeFill(class_4588 b, Matrix4f m, float s, int r, int g, int bl, int a) {
        float h = s / 2.0f;
        b.method_22918((Matrix4fc)m, -h, -h, -h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, -h, -h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, -h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, -h, -h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, -h, h, -h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, h, -h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, -h, h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, -h, -h, -h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, -h, -h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, h, -h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, -h, h, -h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, -h, -h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, -h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, -h, h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, -h, -h, -h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, -h, -h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, -h, h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, -h, h, -h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, -h, -h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, -h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, h, h).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, h, h, -h).method_1336(r, g, bl, a);
    }

    private static void drawCubeEdges(class_4588 b, Matrix4f m, float s, int r, int g, int bl, int a) {
        float h = s / 2.0f;
        JumpCube.line(b, m, -h, -h, -h, h, -h, -h, r, g, bl, a);
        JumpCube.line(b, m, h, -h, -h, h, -h, h, r, g, bl, a);
        JumpCube.line(b, m, h, -h, h, -h, -h, h, r, g, bl, a);
        JumpCube.line(b, m, -h, -h, h, -h, -h, -h, r, g, bl, a);
        JumpCube.line(b, m, -h, h, -h, h, h, -h, r, g, bl, a);
        JumpCube.line(b, m, h, h, -h, h, h, h, r, g, bl, a);
        JumpCube.line(b, m, h, h, h, -h, h, h, r, g, bl, a);
        JumpCube.line(b, m, -h, h, h, -h, h, -h, r, g, bl, a);
        JumpCube.line(b, m, -h, -h, -h, -h, h, -h, r, g, bl, a);
        JumpCube.line(b, m, h, -h, -h, h, h, -h, r, g, bl, a);
        JumpCube.line(b, m, h, -h, h, h, h, h, r, g, bl, a);
        JumpCube.line(b, m, -h, -h, h, -h, h, h, r, g, bl, a);
    }

    private static void line(class_4588 b, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int bl, int a) {
        b.method_22918((Matrix4fc)m, x1, y1, z1).method_1336(r, g, bl, a);
        b.method_22918((Matrix4fc)m, x2, y2, z2).method_1336(r, g, bl, a);
    }

    private static void drawGlow(class_4588 buf, Matrix4f m, int r, int g, int b, int alpha, float s) {
        if (alpha <= 0) {
            return;
        }
        buf.method_22918((Matrix4fc)m, -s, -s, 0.0f).method_1336(r, g, b, alpha).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        buf.method_22918((Matrix4fc)m, s, -s, 0.0f).method_1336(r, g, b, alpha).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        buf.method_22918((Matrix4fc)m, s, s, 0.0f).method_1336(r, g, b, alpha).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        buf.method_22918((Matrix4fc)m, -s, s, 0.0f).method_1336(r, g, b, alpha).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
    }

    @Environment(value=EnvType.CLIENT)
    private class Cube {
        final class_243 spawnPos;
        final long spawnTime = System.currentTimeMillis();
        float cachedAlpha;
        int jumpTicks = 0;
        int prevJumpTicks = 0;
        int jumpTicksMax = 0;
        int jumpYaw = 0;
        double jumpHeight = 0.0;
        boolean outOfRange = false;
        long outOfRangeStart = 0L;
        final Matrix4f cubeMatrix = new Matrix4f();
        final Matrix4f glowMatrix = new Matrix4f();

        Cube(class_243 spawnPos) {
            this.spawnPos = spawnPos;
        }

        void updateLogic() {
            this.prevJumpTicks = this.jumpTicks;
            if (this.jumpTicks > 0) {
                --this.jumpTicks;
            }
            if (IMinecraft.mc.field_1724 != null && IMinecraft.mc.field_1724.method_73189().method_1022(this.spawnPos) > 6.0 && !this.outOfRange) {
                this.outOfRange = true;
                this.outOfRangeStart = System.currentTimeMillis();
            }
        }

        void tryStartJump() {
            if (this.jumpTicks <= 0) {
                this.jumpTicksMax = this.jumpTicks = (int)(14.0f * (0.5f + 0.5f * JumpCube.this.random.nextFloat()));
                this.jumpHeight = (double)(2 + JumpCube.this.random.nextInt(11)) / 16.0;
                this.jumpYaw = JUMP_YAWS[JumpCube.this.random.nextInt(JUMP_YAWS.length)];
            }
        }

        float getTimePc() {
            return class_3532.method_15363((float)((float)(System.currentTimeMillis() - this.spawnTime) / 2000.0f), (float)0.0f, (float)1.0f);
        }

        float getRangeFade() {
            if (!this.outOfRange) {
                return 1.0f;
            }
            return class_3532.method_15363((float)(1.0f - (float)(System.currentTimeMillis() - this.outOfRangeStart) / 400.0f), (float)0.0f, (float)1.0f);
        }

        float getAlpha() {
            float t = this.getTimePc();
            float a = t < 0.1f ? t / 0.1f : (t > 0.8f ? 1.0f - (t - 0.8f) / 0.2f : 1.0f);
            return class_3532.method_15363((float)a, (float)0.0f, (float)1.0f) * this.getRangeFade();
        }

        double getJumpYOffset(float pt) {
            if (this.jumpTicksMax <= 0) {
                return 0.0;
            }
            float t = class_3532.method_16439((float)pt, (float)this.prevJumpTicks, (float)this.jumpTicks) / (float)this.jumpTicksMax;
            if (t > 0.5f) {
                t = 1.0f - t;
            }
            return (double)t * 2.0 * this.jumpHeight;
        }

        boolean isDead() {
            return this.getTimePc() >= 1.0f || this.outOfRange && this.getRangeFade() <= 0.01f;
        }
    }
}

