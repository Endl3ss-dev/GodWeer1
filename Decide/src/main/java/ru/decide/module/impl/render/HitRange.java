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
 *  net.minecraft.class_1657
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_268
 *  net.minecraft.class_269
 *  net.minecraft.class_270
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_742
 *  net.minecraft.class_746
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
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_270;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_746;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;

@ModuleInfo(name="Hit Range", desc="\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0440\u0430\u0434\u0438\u0443\u0441 \u0443\u0434\u0430\u0440\u0430", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class HitRange
extends Module {
    private static final float HIT_RADIUS = 3.32f;
    private static final float Y_OFFSET = 0.02f;
    private static final int RHOMBUS_SIDES = 8;
    private static final int CIRCLE_SIDES = 96;
    private static final RenderPipeline RING_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/world/hit_range_ring")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).build());
    private static final class_1921 RING_LAYER = class_1921.method_75940((String)"hit_range_ring", (class_12247)class_12247.method_75927((RenderPipeline)RING_PIPELINE).method_75937().method_75929(16384).method_75938());
    private static final RenderPipeline FILL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/world/hit_range_fill")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27379).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.TRANSLUCENT).build());
    private static final class_1921 FILL_LAYER = class_1921.method_75940((String)"hit_range_fill", (class_12247)class_12247.method_75927((RenderPipeline)FILL_PIPELINE).method_75937().method_75929(8192).method_75938());
    private final class_9799 allocator = new class_9799(65536);
    public ModeSetting mode = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c", "\u0420\u043e\u043c\u0431", "\u041a\u0440\u0443\u0433");
    public ColorSetting color = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", -934234881);
    public ColorSetting rangeColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0432 \u0440\u0430\u0434\u0438\u0443\u0441\u0435", -601161889);
    public SliderSetting fillAlpha = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0437\u0430\u043b\u0438\u0432\u043a\u0438", 0.18f, 0.0f, 1.0f, 0.01f).setVisible(this::isFillVisible);
    public SliderSetting thickness = new SliderSetting(this, "\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u043b\u0438\u043d\u0438\u0438", 0.06f, 0.01f, 0.5f, 0.01f);
    public SliderSetting rotSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0432\u0440\u0430\u0449\u0435\u043d\u0438\u044f", 0.4f, 0.05f, 2.0f, 0.05f);
    public BooleanSetting fill = new BooleanSetting((Module)this, "\u0417\u0430\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u0435", false);
    public BooleanSetting showEnemies = new BooleanSetting((Module)this, "\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0442\u044c \u043d\u0430 \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430\u0445", false);
    private float rotationAngle;

    private boolean isFillVisible() {
        return this.fill.getValue();
    }

    @EventHandler
    public void onRender(EventRender3D event) {
        int i;
        if (HitRange.mc.field_1724 == null || HitRange.mc.field_1687 == null) {
            return;
        }
        float tickDelta = event.getTickDelta();
        class_4587 pose = event.getMatrixStack();
        class_243 cam = HitRange.mc.field_1773.method_19418().method_71156();
        int sides = this.mode.is("\u041a\u0440\u0443\u0433") ? 96 : 8;
        double rotation = Math.toRadians(this.rotationAngle);
        this.rotationAngle = (this.rotationAngle + this.rotSpeed.getValue().floatValue()) % 360.0f;
        float half = this.thickness.getValue().floatValue() / 2.0f;
        class_746 self = HitRange.mc.field_1724;
        boolean anyInRange = this.hasPlayerInRange((class_1657)self);
        Vec3dLocal[] positions = this.collectPositions(tickDelta, (class_1657)self, anyInRange);
        if (positions.length == 0) {
            return;
        }
        Matrix4f[] matrices = new Matrix4f[positions.length];
        for (int i2 = 0; i2 < positions.length; ++i2) {
            Vec3dLocal entry = positions[i2];
            pose.method_22903();
            pose.method_22904(entry.x - cam.field_1352, entry.y + (double)0.02f - cam.field_1351, entry.z - cam.field_1350);
            pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(90.0f));
            matrices[i2] = new Matrix4f((Matrix4fc)pose.method_23760().method_23761());
            pose.method_22909();
        }
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        if (this.fill.getValue().booleanValue()) {
            class_4588 fillBuf = immediate.method_73477(FILL_LAYER);
            for (i = 0; i < positions.length; ++i) {
                this.appendFill(fillBuf, matrices[i], sides, rotation, positions[i].color);
            }
        }
        class_4588 ringBuf = immediate.method_73477(RING_LAYER);
        for (i = 0; i < positions.length; ++i) {
            this.appendRing(ringBuf, matrices[i], sides, rotation, half, positions[i].color);
        }
        immediate.method_22993();
    }

    private Vec3dLocal[] collectPositions(float tickDelta, class_1657 self, boolean anyInRange) {
        int enemies = this.showEnemies.getValue() != false ? HitRange.mc.field_1687.method_18456().size() : 0;
        int size = 1 + enemies;
        Vec3dLocal[] out = new Vec3dLocal[size];
        class_243 pos = self.method_30950(tickDelta);
        out[0] = new Vec3dLocal(pos.field_1352, pos.field_1351, pos.field_1350, anyInRange ? this.rangeColor.getValue() : this.color.getValue());
        if (!this.showEnemies.getValue().booleanValue()) {
            return out;
        }
        int index = 1;
        for (class_742 player : HitRange.mc.field_1687.method_18456()) {
            boolean canReach;
            if (!this.isVisibleTarget(player, self)) continue;
            class_243 p = player.method_30950(tickDelta);
            boolean bl = canReach = player.method_5739((class_1297)self) <= 3.32f;
            if (index >= out.length) break;
            out[index++] = new Vec3dLocal(p.field_1352, p.field_1351, p.field_1350, canReach ? this.rangeColor.getValue() : this.color.getValue());
        }
        return index == out.length ? out : Arrays.copyOf(out, index);
    }

    private boolean hasPlayerInRange(class_1657 owner) {
        if (HitRange.mc.field_1687 == null) {
            return false;
        }
        for (class_742 player : HitRange.mc.field_1687.method_18456()) {
            if (!this.isVisibleTarget(player, owner) || !(player.method_5739((class_1297)owner) <= 3.32f)) continue;
            return true;
        }
        return false;
    }

    private boolean isVisibleTarget(class_742 player, class_1657 owner) {
        return player != null && owner != null && player != owner && player.method_5805() && !player.method_7325() && !this.isTeammate(player, owner);
    }

    private boolean isTeammate(class_742 player, class_1657 owner) {
        if (HitRange.mc.field_1687 == null) {
            return false;
        }
        class_269 scoreboard = HitRange.mc.field_1687.method_8428();
        if (scoreboard == null) {
            return false;
        }
        class_268 ownerTeam = scoreboard.method_1153(owner.method_5820());
        if (ownerTeam == null) {
            return false;
        }
        return ownerTeam.method_1206((class_270)scoreboard.method_1153(player.method_5820()));
    }

    private void appendRing(class_4588 buf, Matrix4f matrix, int sides, double rotation, float half, int color) {
        int glowColor = this.multAlpha(color, 0.25f);
        double outerR = 3.32f + half;
        double innerR = 3.32f - half;
        double glowOuter = 3.32f + half * 3.0f;
        double glowInner = 3.32f - half * 3.0f;
        for (int i = 0; i < sides; ++i) {
            double a1 = rotation + Math.PI * 2 * (double)i / (double)sides;
            double a2 = rotation + Math.PI * 2 * (double)(i + 1) / (double)sides;
            this.ringQuad(buf, matrix, a1, a2, outerR, innerR, color);
            this.ringQuad(buf, matrix, a1, a2, glowOuter, glowInner, glowColor);
        }
    }

    private void ringQuad(class_4588 buf, Matrix4f matrix, double a1, double a2, double outerR, double innerR, int color) {
        buf.method_22918((Matrix4fc)matrix, (float)(outerR * Math.cos(a1)), (float)(outerR * Math.sin(a1)), 0.0f).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, (float)(outerR * Math.cos(a2)), (float)(outerR * Math.sin(a2)), 0.0f).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, (float)(innerR * Math.cos(a2)), (float)(innerR * Math.sin(a2)), 0.0f).method_39415(color);
        buf.method_22918((Matrix4fc)matrix, (float)(innerR * Math.cos(a1)), (float)(innerR * Math.sin(a1)), 0.0f).method_39415(color);
    }

    private void appendFill(class_4588 buf, Matrix4f matrix, int sides, double rotation, int color) {
        int fillColor = this.multAlpha(color, this.fillAlpha.getValue().floatValue());
        for (int i = 0; i < sides; ++i) {
            double a1 = rotation + Math.PI * 2 * (double)i / (double)sides;
            double a2 = rotation + Math.PI * 2 * (double)(i + 1) / (double)sides;
            buf.method_22918((Matrix4fc)matrix, 0.0f, 0.0f, 0.0f).method_39415(fillColor);
            buf.method_22918((Matrix4fc)matrix, (float)((double)3.32f * Math.cos(a1)), (float)((double)3.32f * Math.sin(a1)), 0.0f).method_39415(fillColor);
            buf.method_22918((Matrix4fc)matrix, (float)((double)3.32f * Math.cos(a2)), (float)((double)3.32f * Math.sin(a2)), 0.0f).method_39415(fillColor);
        }
    }

    private int multAlpha(int color, float alphaMul) {
        int alpha = color >> 24 & 0xFF;
        if (alpha == 0) {
            alpha = 255;
        }
        alpha = Math.max(0, Math.min(255, (int)((float)alpha * alphaMul)));
        return color & 0xFFFFFF | alpha << 24;
    }

    @Environment(value=EnvType.CLIENT)
    private record Vec3dLocal(double x, double y, double z, int color) {
    }
}

