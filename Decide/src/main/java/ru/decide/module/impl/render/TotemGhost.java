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
 *  net.minecraft.class_1309
 *  net.minecraft.class_1921
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2663
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
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
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
import ru.decide.manager.event_impl.EventPacket;
import ru.decide.manager.event_impl.EventRender3D;
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
import ru.decide.utils.colors.ColorUtil;

@ModuleInfo(name="Totem Ghost", desc="\u041f\u0440\u0438\u0437\u0440\u0430\u043a \u043c\u043e\u0434\u0435\u043b\u044c\u043a\u0438 \u043f\u0440\u0438 \u043b\u043e\u043f\u0430\u043d\u0438\u0438 \u0442\u043e\u0442\u0435\u043c\u0430", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class TotemGhost
extends Module
implements ModulePreview {
    public ButtonSetting previewButton = PreviewSettings.button(this);
    public ModeSetting typeColor = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439");
    public ColorSetting tintColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", -16711681).setVisible(() -> this.typeColor.is("\u0421\u0432\u043e\u0439"));
    public SliderSetting rise = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 \u043f\u043e\u0434\u044a\u0451\u043c\u0430", 2.0f, 0.5f, 4.0f, 0.1f);
    public SliderSetting duration = new SliderSetting(this, "\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c", 2.0f, 1.0f, 4.0f, 0.1f);
    private final PreviewSettings previewSettings = PreviewSettings.of(this, 4.0f, 0.0f, 3.0f);
    private static final float[][] PARTS = new float[][]{{-0.25f, 1.5f, -0.25f, 0.25f, 2.0f, 0.25f}, {-0.25f, 0.75f, -0.125f, 0.25f, 1.5f, 0.125f}, {0.25f, 0.75f, -0.125f, 0.5f, 1.5f, 0.125f}, {-0.5f, 0.75f, -0.125f, -0.25f, 1.5f, 0.125f}, {0.0f, 0.0f, -0.125f, 0.25f, 0.75f, 0.125f}, {-0.25f, 0.0f, -0.125f, 0.0f, 0.75f, 0.125f}};
    private static final float[][] PIVOTS = new float[][]{{0.0f, 1.5f, 0.0f}, {0.0f, 0.75f, 0.0f}, {0.375f, 1.4f, 0.0f}, {-0.375f, 1.4f, 0.0f}, {0.125f, 0.75f, 0.0f}, {-0.125f, 0.75f, 0.0f}};
    private static final int PART_COUNT = PARTS.length;
    private static final int[][] FACES = new int[][]{{0, 1, 3, 2}, {4, 5, 7, 6}, {0, 1, 5, 4}, {2, 3, 7, 6}, {0, 2, 6, 4}, {1, 3, 7, 5}};
    private static final int[][] EDGES = new int[][]{{0, 1}, {1, 3}, {3, 2}, {2, 0}, {4, 5}, {5, 7}, {7, 6}, {6, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
    private final List<Ghost> ghosts = new ArrayList<Ghost>();
    private final class_9799 allocator = new class_9799(16384);
    private static final class_2960 GLOW_TEXTURE_BIG = class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_1.png");
    private static final class_2960 GLOW_TEXTURE_SMALL = class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_2.png");
    private static final RenderPipeline FILL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"totem_ghost_fill")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    private static final class_1921 FILL_LAYER = class_1921.method_75940((String)"totem_ghost_fill", (class_12247)class_12247.method_75927((RenderPipeline)FILL_PIPELINE).method_75937().method_75929(4096).method_75938());
    private static final RenderPipeline LINE_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"totem_ghost_lines")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_29344).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    private static final class_1921 LINE_LAYER = class_1921.method_75940((String)"totem_ghost_lines", (class_12247)class_12247.method_75927((RenderPipeline)LINE_PIPELINE).method_75929(4096).method_75938());
    private static final RenderPipeline GLOW_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"totem_ghost_glow")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    private static final class_1921 GLOW_LAYER_BIG = class_1921.method_75940((String)"totem_ghost_glow_big", (class_12247)class_12247.method_75927((RenderPipeline)GLOW_PIPELINE).method_75934("Sampler0", GLOW_TEXTURE_BIG).method_75937().method_75929(1024).method_75938());
    private static final class_1921 GLOW_LAYER_SMALL = class_1921.method_75940((String)"totem_ghost_glow_small", (class_12247)class_12247.method_75927((RenderPipeline)GLOW_PIPELINE).method_75934("Sampler0", GLOW_TEXTURE_SMALL).method_75937().method_75929(1024).method_75938());

    public int getColor() {
        if (this.typeColor.is("\u0422\u0435\u043c\u0430")) {
            return ColorUtil.getClientColor1(1);
        }
        return this.tintColor.getValue();
    }

    @Override
    public void toggle() {
        super.toggle();
        this.ghosts.clear();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.ghosts.clear();
    }

    @Override
    public PreviewSettings previewSettings() {
        return this.previewSettings;
    }

    @Override
    public boolean previewNeedsDummy() {
        return true;
    }

    @Override
    public void previewSpawn(PreviewContext ctx) {
        this.spawnGhost((class_1297)ctx.dummy());
    }

    @Override
    public void previewStop() {
        this.ghosts.clear();
    }

    @EventHandler
    public void onPacket(EventPacket e) {
        class_2596<?> class_25962 = e.getPacket();
        if (!(class_25962 instanceof class_2663)) {
            return;
        }
        class_2663 packet = (class_2663)class_25962;
        if (packet.method_11470() != 35) {
            return;
        }
        if (TotemGhost.mc.field_1687 == null) {
            return;
        }
        class_1297 entity = packet.method_11469((class_1937)TotemGhost.mc.field_1687);
        if (entity == null) {
            return;
        }
        this.spawnGhost(entity);
    }

    public void spawnGhost(class_1297 entity) {
        if (!this.isEnabled() || entity == null) {
            return;
        }
        Ghost g = new Ghost(entity.method_73189(), entity.method_36454());
        if (entity instanceof class_1309) {
            class_1309 le = (class_1309)entity;
            g.bodyYaw = le.method_73188();
            g.headYawDeg = class_3532.method_15363((float)class_3532.method_15393((float)(le.method_5791() - le.method_73188())), (float)-75.0f, (float)75.0f);
            float limbAngle = le.field_42108.method_48569();
            float limbDist = Math.min(le.field_42108.method_48566(), 1.0f);
            g.partPitchDeg[0] = le.method_36455();
            g.partPitchDeg[2] = (float)Math.toDegrees(class_3532.method_15362((double)(limbAngle * 0.6662f + (float)Math.PI)) * 2.0f * limbDist * 0.5f);
            g.partPitchDeg[3] = (float)Math.toDegrees(class_3532.method_15362((double)(limbAngle * 0.6662f)) * 2.0f * limbDist * 0.5f);
            g.partPitchDeg[4] = (float)Math.toDegrees(class_3532.method_15362((double)(limbAngle * 0.6662f)) * 1.4f * limbDist);
            g.partPitchDeg[5] = (float)Math.toDegrees(class_3532.method_15362((double)(limbAngle * 0.6662f + (float)Math.PI)) * 1.4f * limbDist);
        } else {
            g.bodyYaw = entity.method_36454();
        }
        this.ghosts.add(g);
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        if (this.ghosts.isEmpty() || TotemGhost.mc.field_1724 == null || TotemGhost.mc.field_1687 == null) {
            return;
        }
        long now = System.currentTimeMillis();
        long durMs = (long)(this.duration.getValue().floatValue() * 1000.0f);
        float riseH = this.rise.getValue().floatValue();
        int baseColor = this.getColor();
        int cr = baseColor >> 16 & 0xFF;
        int cg = baseColor >> 8 & 0xFF;
        int cb = baseColor & 0xFF;
        class_4587 matrices = e.getMatrixStack();
        class_243 cam = TotemGhost.mc.field_1773.method_19418().method_71156();
        Iterator<Ghost> it = this.ghosts.iterator();
        while (it.hasNext()) {
            Ghost g = it.next();
            float t = (float)(now - g.start) / (float)durMs;
            if (t >= 1.0f) {
                it.remove();
                continue;
            }
            float ease = 1.0f - (1.0f - t) * (1.0f - t) * (1.0f - t);
            float fadeIn = Math.min(t * 8.0f, 1.0f);
            float fadeOut = t > 0.55f ? 1.0f - (t - 0.55f) / 0.45f : 1.0f;
            g.alphaValue = fadeIn * fadeOut;
            double gx = g.x - cam.field_1352;
            double gy = g.y - cam.field_1351 + (double)(ease * riseH);
            double gz = g.z - cam.field_1350;
            matrices.method_22903();
            matrices.method_22904(gx, gy, gz);
            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-g.bodyYaw + t * 40.0f));
            for (int i = 0; i < PART_COUNT; ++i) {
                float yawOff;
                float pitch = g.partPitchDeg[i];
                float f = yawOff = i == 0 ? g.headYawDeg : 0.0f;
                if (pitch == 0.0f && yawOff == 0.0f) {
                    g.partMatrices[i].set((Matrix4fc)matrices.method_23760().method_23761());
                    continue;
                }
                float[] pv = PIVOTS[i];
                matrices.method_22903();
                matrices.method_46416(pv[0], pv[1], pv[2]);
                if (yawOff != 0.0f) {
                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-yawOff));
                }
                if (pitch != 0.0f) {
                    matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(pitch));
                }
                matrices.method_46416(-pv[0], -pv[1], -pv[2]);
                g.partMatrices[i].set((Matrix4fc)matrices.method_23760().method_23761());
                matrices.method_22909();
            }
            matrices.method_22909();
            matrices.method_22903();
            matrices.method_22904(gx, gy + 1.1, gz);
            matrices.method_22907((Quaternionfc)TotemGhost.mc.field_1773.method_19418().method_23767());
            g.glowMatrix.set((Matrix4fc)matrices.method_23760().method_23761());
            matrices.method_22909();
        }
        if (this.ghosts.isEmpty()) {
            return;
        }
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        class_4588 buf = immediate.method_73477(FILL_LAYER);
        for (Ghost g : this.ghosts) {
            int alpha = (int)(g.alphaValue * 0.25f * 255.0f);
            if (alpha <= 0) continue;
            for (int i = 0; i < PART_COUNT; ++i) {
                TotemGhost.drawBoxFill(buf, g.partMatrices[i], PARTS[i], cr, cg, cb, alpha);
            }
        }
        buf = immediate.method_73477(LINE_LAYER);
        for (Ghost g : this.ghosts) {
            int alpha = (int)(g.alphaValue * 0.45f * 255.0f);
            if (alpha <= 0) continue;
            for (int i = 0; i < PART_COUNT; ++i) {
                TotemGhost.drawBoxLines(buf, g.partMatrices[i], PARTS[i], cr, cg, cb, alpha);
            }
        }
        buf = immediate.method_73477(GLOW_LAYER_BIG);
        for (Ghost g : this.ghosts) {
            TotemGhost.drawGlow(buf, g.glowMatrix, cr, cg, cb, (int)(110.0f * g.alphaValue), 1.5f);
        }
        buf = immediate.method_73477(GLOW_LAYER_SMALL);
        for (Ghost g : this.ghosts) {
            TotemGhost.drawGlow(buf, g.glowMatrix, cr, cg, cb, (int)(160.0f * g.alphaValue), 0.7f);
        }
        immediate.method_22993();
    }

    private static float corner(float[] p, int bit, int axis) {
        return (bit >> axis & 1) == 0 ? p[axis] : p[axis + 3];
    }

    private static void drawBoxFill(class_4588 buf, Matrix4f m, float[] p, int r, int g, int b, int alpha) {
        int[][] nArray = FACES;
        int n = nArray.length;
        for (int i = 0; i < n; ++i) {
            int[] face;
            for (int idx : face = nArray[i]) {
                buf.method_22918((Matrix4fc)m, TotemGhost.corner(p, idx, 0), TotemGhost.corner(p, idx, 1), TotemGhost.corner(p, idx, 2)).method_1336(r, g, b, alpha);
            }
        }
    }

    private static void drawBoxLines(class_4588 buf, Matrix4f m, float[] p, int r, int g, int b, int alpha) {
        for (int[] edge : EDGES) {
            buf.method_22918((Matrix4fc)m, TotemGhost.corner(p, edge[0], 0), TotemGhost.corner(p, edge[0], 1), TotemGhost.corner(p, edge[0], 2)).method_1336(r, g, b, alpha);
            buf.method_22918((Matrix4fc)m, TotemGhost.corner(p, edge[1], 0), TotemGhost.corner(p, edge[1], 1), TotemGhost.corner(p, edge[1], 2)).method_1336(r, g, b, alpha);
        }
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
    private static class Ghost {
        final double x;
        final double y;
        final double z;
        final long start;
        float bodyYaw;
        float headYawDeg;
        final float[] partPitchDeg = new float[PART_COUNT];
        float alphaValue;
        final Matrix4f[] partMatrices = new Matrix4f[PART_COUNT];
        final Matrix4f glowMatrix = new Matrix4f();

        Ghost(class_243 pos, float yaw) {
            this.x = pos.field_1352;
            this.y = pos.field_1351;
            this.z = pos.field_1350;
            this.bodyYaw = yaw;
            this.start = System.currentTimeMillis();
            for (int i = 0; i < PART_COUNT; ++i) {
                this.partMatrices[i] = new Matrix4f();
            }
        }
    }
}

