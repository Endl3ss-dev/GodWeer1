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
 *  net.minecraft.class_156
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_7833
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
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
import java.util.Random;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
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
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;

@ModuleInfo(name="Trails", desc="\u041a\u0440\u0430\u0441\u0438\u0432\u044b\u0439 \u044d\u0444\u0444\u0435\u043a\u0442 \u0437\u0430\u0434\u0435 \u0438\u0433\u0440\u043e\u043a\u0430 \u043f\u0440\u0438 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u0438", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class Trails
extends Module
implements ModulePreview {
    public ButtonSetting previewButton = PreviewSettings.button(this);
    public final ModeSetting colorMode = new ModeSetting((Module)this, "\u0426\u0432\u0435\u0442", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439");
    public final ModeSetting rotateMode = new ModeSetting((Module)this, "\u0412\u0440\u0430\u0449\u0435\u043d\u0438\u0435", "\u0414\u0432\u0438\u0436\u0435\u043d\u0438\u0435", "\u041a\u0430\u043c\u0435\u0440\u0430");
    public final SliderSetting dashLength = new SliderSetting(this, "\u0414\u043b\u0438\u043d\u0430 \u0448\u043b\u0435\u0439\u0444\u0430", 0.75f, 0.5f, 1.5f, 0.01f);
    public final SliderSetting moveLerp = new SliderSetting(this, "Move Lerp", 0.2f, 0.1f, 0.5f, 0.01f);
    public final BooleanSetting motionSmooth = new BooleanSetting((Module)this, "\u0421\u0433\u043b\u0430\u0436\u0438\u0432\u0430\u043d\u0438\u0435", false);
    public final BooleanSetting dashSegs = new BooleanSetting((Module)this, "\u0421\u0435\u0433\u043c\u0435\u043d\u0442\u044b", false);
    public final BooleanSetting dashDots = new BooleanSetting((Module)this, "\u0422\u043e\u0447\u043a\u0438", true);
    public final BooleanSetting lighting = new BooleanSetting((Module)this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", true);
    public final BooleanSetting firstPerson = new BooleanSetting((Module)this, "\u0412\u0438\u0434\u0435\u0442\u044c \u043e\u0442 \u043f\u0435\u0440\u0432\u043e\u0433\u043e \u043b\u0438\u0446\u0430", false);
    private final PreviewSettings previewSettings = PreviewSettings.withoutInterval(this, 4.0f, 0.0f);
    private static final double PREVIEW_ORBIT_MS = 4000.0;
    private static final int DASH_TEX_COUNT = 21;
    private static final long DASH_FRAME_TIME_MS = 80L;
    private static final int[] GROUP_SIZES = new int[]{11, 23, 32, 16, 32};
    private static final List<class_2960> DASH_TEXTURES = new ArrayList<class_2960>();
    private static final List<List<class_2960>> DASH_ANIM_GROUPS = new ArrayList<List<class_2960>>();
    private static final class_2960 BLOOM_TEX = class_2960.method_60655((String)"decide", (String)"textures/particles/glow.png");
    private static final RenderPipeline SPARK_PIPELINE;
    private static final class_1921 SPARK_LAYER;
    private static final RenderPipeline CUBIC_PIPELINE;
    private static final Function<class_2960, class_1921> CUBIC_LAYER;
    private final List<DashCubic> cubics = new ArrayList<DashCubic>();
    private final List<DashCubic> filteredCache = new ArrayList<DashCubic>();
    private final Random rng = new Random(1234567891L);
    private final class_9799 allocator = new class_9799(262144);
    private final Vector3f renderRight = new Vector3f();
    private final Vector3f renderUp = new Vector3f();
    private final Quaternionf bloomRotCache = new Quaternionf();
    private float stateAnim = 0.0f;
    private float lightAnim = 0.0f;
    private double prevX;
    private double prevY;
    private double prevZ;
    private boolean prevInit = false;
    private class_1309 previewEntity;
    private double previewPrevX;
    private double previewPrevY;
    private double previewPrevZ;
    private boolean previewPrevInit = false;

    @Override
    protected void onEnable() {
        this.cubics.clear();
        this.filteredCache.clear();
        this.stateAnim = 0.0f;
        this.prevInit = false;
    }

    @Override
    protected void onDisable() {
        this.cubics.clear();
        this.filteredCache.clear();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.cubics.clear();
        this.filteredCache.clear();
        this.prevInit = false;
        this.previewPrevInit = false;
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
    public boolean previewControlsDummy() {
        return true;
    }

    @Override
    public void previewStart(PreviewContext ctx) {
        this.previewEntity = ctx.dummy();
        this.previewPrevInit = false;
    }

    @Override
    public void previewTick(PreviewContext ctx) {
        this.previewEntity = ctx.dummy();
        if (this.previewEntity == null) {
            return;
        }
        double angle = (double)ctx.elapsedMs() / 4000.0 * Math.PI * 2.0;
        double radius = 1.6;
        class_243 anchor = ctx.anchor();
        this.previewEntity.method_5814(anchor.field_1352 + Math.cos(angle) * radius, anchor.field_1351, anchor.field_1350 + Math.sin(angle) * radius);
        float yaw = (float)Math.toDegrees(angle);
        this.previewEntity.method_36456(yaw);
        this.previewEntity.method_5636(yaw);
        this.previewEntity.method_5847(yaw);
    }

    @Override
    public void previewSpawn(PreviewContext ctx) {
    }

    @Override
    public void previewStop() {
        this.previewEntity = null;
        this.previewPrevInit = false;
        this.cubics.clear();
        this.filteredCache.clear();
    }

    @EventHandler
    public void onTick(EventTick e) {
        DashCubic c;
        int i;
        if (Trails.mc.field_1724 == null) {
            return;
        }
        this.stateAnim += (1.0f - this.stateAnim) * 0.08f;
        this.lightAnim += ((this.lighting.getValue() != false ? 1.0f : 0.0f) - this.lightAnim) * 0.08f;
        if (!this.prevInit) {
            this.prevX = Trails.mc.field_1724.method_23317();
            this.prevY = Trails.mc.field_1724.method_23318();
            this.prevZ = Trails.mc.field_1724.method_23321();
            this.prevInit = true;
            return;
        }
        long nowMs = System.currentTimeMillis();
        int size = this.cubics.size();
        for (i = 0; i < size; ++i) {
            c = this.cubics.get(i);
            c.cachedTimePC = Math.min(1.0f, (float)(nowMs - c.startTime) / (float)c.base.rMTime);
            if (c.cachedTimePC >= 1.0f && c.alphaTarget != 0.0f) {
                c.alphaTarget = 0.0f;
            }
            c.alphaAnim += (c.alphaTarget - c.alphaAnim) * 0.08f;
        }
        for (i = this.cubics.size() - 1; i >= 0; --i) {
            c = this.cubics.get(i);
            if (!(c.cachedTimePC >= 1.0f) || c.alphaTarget != 0.0f || !(c.alphaAnim < 0.02f)) continue;
            this.cubics.remove(i);
        }
        this.filteredCache.clear();
        int n = this.cubics.size();
        for (i = 0; i < n; ++i) {
            DashCubic c2 = this.cubics.get(i);
            if (!(c2.alphaAnim > 0.05f)) continue;
            this.filteredCache.add(c2);
        }
        float cameraPitch = Trails.mc.field_1773.method_19418().method_19329();
        boolean smooth = this.motionSmooth.getValue();
        int fsz = this.filteredCache.size();
        for (int i2 = 0; i2 < fsz; ++i2) {
            DashCubic next;
            DashCubic cur = this.filteredCache.get(i2);
            DashCubic dashCubic = next = smooth && i2 + 1 < fsz ? this.filteredCache.get(i2 + 1) : null;
            if (next != null && next.base.entityId != cur.base.entityId) {
                next = null;
            }
            cur.motionProcess(next, nowMs, cameraPitch);
        }
        double lx = this.prevX;
        double ly = this.prevY;
        double lz = this.prevZ;
        double lr = this.moveLerp.getValue().doubleValue();
        this.prevX = class_3532.method_16436((double)lr, (double)this.prevX, (double)Trails.mc.field_1724.method_23317());
        this.prevY = class_3532.method_16436((double)lr, (double)this.prevY, (double)Trails.mc.field_1724.method_23318());
        this.prevZ = class_3532.method_16436((double)lr, (double)this.prevZ, (double)Trails.mc.field_1724.method_23321());
        if (!Trails.mc.field_1724.method_5767()) {
            this.spawnFor((class_1309)Trails.mc.field_1724, lx, ly, lz);
        }
        this.tickPreviewTrail(lr);
    }

    private void tickPreviewTrail(double lr) {
        if (this.previewEntity == null || this.previewEntity.method_31481()) {
            this.previewPrevInit = false;
            return;
        }
        if (!this.previewPrevInit) {
            this.previewPrevX = this.previewEntity.method_23317();
            this.previewPrevY = this.previewEntity.method_23318();
            this.previewPrevZ = this.previewEntity.method_23321();
            this.previewPrevInit = true;
            return;
        }
        double lx = this.previewPrevX;
        double ly = this.previewPrevY;
        double lz = this.previewPrevZ;
        this.previewPrevX = class_3532.method_16436((double)lr, (double)this.previewPrevX, (double)this.previewEntity.method_23317());
        this.previewPrevY = class_3532.method_16436((double)lr, (double)this.previewPrevY, (double)this.previewEntity.method_23318());
        this.previewPrevZ = class_3532.method_16436((double)lr, (double)this.previewPrevZ, (double)this.previewEntity.method_23321());
        this.spawnFor(this.previewEntity, lx, ly, lz);
    }

    private void spawnFor(class_1309 entity, double lx, double ly, double lz) {
        if (entity == null) {
            return;
        }
        double dx = entity.method_23317() - lx;
        double dy = entity.method_23318() - ly;
        double dz = entity.method_23321() - lz;
        if (dx * dx + dz * dz < 0.0064) {
            return;
        }
        double speed = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int count = (int)class_3532.method_15363((float)((float)(speed / (double)0.4f)), (float)1.0f, (float)16.0f);
        for (int i = 0; i < count; ++i) {
            int rMTime = (int)((float)(550 + this.rng.nextInt(300)) * this.dashLength.getValue().floatValue());
            this.cubics.add(new DashCubic(this, new DashBase(entity, 0.04f, (float)i / (float)count, rMTime)));
        }
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        float aPC;
        if (Trails.mc.field_1724 == null || this.stateAnim < 0.05f || this.filteredCache.isEmpty()) {
            return;
        }
        float ticks = e.getTickDelta();
        float alphaPC = this.stateAnim;
        float lightPC = this.lightAnim;
        int fsz = this.filteredCache.size();
        long nowMs = System.currentTimeMillis();
        boolean hideOwnTrail = this.firstPerson.getValue() == false && Trails.mc.field_1690.method_31044().method_31034();
        int localPlayerId = Trails.mc.field_1724.method_5628();
        class_4587 matrix = e.getMatrixStack();
        class_243 cam = Trails.mc.method_1561().field_4686.method_71156();
        double camX = cam.field_1352;
        double camY = cam.field_1351;
        double camZ = cam.field_1350;
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        float fCamYaw = Trails.mc.field_1773.method_19418().method_19330();
        float fCamPitch = Trails.mc.field_1773.method_19418().method_19329();
        Quaternionf camRot = Trails.mc.field_1773.method_19418().method_23767();
        camRot.transform(this.renderRight.set(1.0f, 0.0f, 0.0f));
        camRot.transform(this.renderUp.set(0.0f, 1.0f, 0.0f));
        this.bloomRotCache.identity().rotateY((float)Math.toRadians(-fCamYaw)).rotateX((float)Math.toRadians(fCamPitch));
        for (int i = 0; i < fsz; ++i) {
            class_2960 tex;
            DashCubic cubic = this.filteredCache.get(i);
            if (hideOwnTrail && cubic.base.entityId == localPlayerId || (tex = cubic.dashTexture.getCurrent(nowMs)) == null) continue;
            aPC = cubic.alphaAnim * alphaPC;
            float ext = 4.224f * aPC * 0.3f;
            int col = cubic.color;
            matrix.method_22903();
            matrix.method_22904(cubic.getRenderX(ticks) - camX, cubic.getRenderY(ticks) - camY, cubic.getRenderZ(ticks) - camZ);
            this.applyBodyRotation(matrix, cubic.rotate, fCamYaw, fCamPitch);
            matrix.method_22905(-0.1f, -0.1f, 0.1f);
            Trails.drawTexQuad(immediate.method_73477(CUBIC_LAYER.apply(tex)), matrix.method_23760().method_23761(), -ext, -ext, ext, ext, ColorUtil.multDark(ColorUtil.overCol(col, -1, 0.4f), aPC));
            matrix.method_22909();
        }
        class_4588 bloomBuf = immediate.method_73477(CUBIC_LAYER.apply(BLOOM_TEX));
        for (int i = 0; i < fsz; ++i) {
            DashCubic cubic = this.filteredCache.get(i);
            if (hideOwnTrail && cubic.base.entityId == localPlayerId) continue;
            aPC = cubic.alphaAnim * alphaPC;
            float base = 4.224f * aPC * 0.3f;
            float timePcOf = 1.0f - cubic.cachedTimePC;
            int col = cubic.color;
            int baseCol = ColorUtil.overCol(col, -1, 0.18f);
            matrix.method_22903();
            matrix.method_22904(cubic.getRenderX(ticks) - camX, cubic.getRenderY(ticks) - camY, cubic.getRenderZ(ticks) - camZ);
            matrix.method_22907((Quaternionfc)this.bloomRotCache);
            matrix.method_22905(-0.1f, -0.1f, 0.1f);
            Matrix4f mat = matrix.method_23760().method_23761();
            Trails.drawTexQuad(bloomBuf, mat, -base * 6.0f, -base * 6.0f, base * 6.0f, base * 6.0f, ColorUtil.swapAlpha(baseCol, 15.0f * aPC));
            Trails.drawTexQuad(bloomBuf, mat, -base * 2.2f, -base * 2.2f, base * 2.2f, base * 2.2f, ColorUtil.swapAlpha(baseCol, 70.0f * aPC));
            Trails.drawTexQuad(bloomBuf, mat, -base * 1.1f, -base * 1.1f, base * 1.1f, base * 1.1f, ColorUtil.swapAlpha(baseCol, 140.0f * aPC));
            if (lightPC > 0.0f) {
                float aMul = aPC * lightPC;
                float bigExt = base * (1.0f + 8.0f * timePcOf * aMul);
                Trails.drawTexQuad(bloomBuf, mat, -bigExt, -bigExt, bigExt, bigExt, ColorUtil.swapAlpha(ColorUtil.multDark(baseCol, aMul / 4.0f), 95.0f * aMul));
            }
            matrix.method_22909();
        }
        immediate.method_22993();
    }

    private void applyBodyRotation(class_4587 m, float[] rotate, float camYaw, float camPitch) {
        if (this.rotateMode.is("\u0414\u0432\u0438\u0436\u0435\u043d\u0438\u0435")) {
            m.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-rotate[0]));
            m.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(rotate[1]));
        } else {
            m.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-camYaw));
            m.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(camPitch));
        }
    }

    private static void drawSparkQuad(class_4588 buf, Matrix4f mat, Vector3f right, Vector3f up, float s, float cx, float cy, float cz, int color) {
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        int a = color >> 24 & 0xFF;
        float rx = right.x * s;
        float ry = right.y * s;
        float rz = right.z * s;
        float ux = up.x * s;
        float uy = up.y * s;
        float uz = up.z * s;
        buf.method_22918((Matrix4fc)mat, cx - rx + ux, cy - ry + uy, cz - rz + uz).method_1336(r, g, b, a);
        buf.method_22918((Matrix4fc)mat, cx + rx + ux, cy + ry + uy, cz + rz + uz).method_1336(r, g, b, a);
        buf.method_22918((Matrix4fc)mat, cx + rx - ux, cy + ry - uy, cz + rz - uz).method_1336(r, g, b, a);
        buf.method_22918((Matrix4fc)mat, cx - rx - ux, cy - ry - uy, cz - rz - uz).method_1336(r, g, b, a);
    }

    private static void drawTexQuad(class_4588 buf, Matrix4f mat, float x1, float y1, float x2, float y2, int color) {
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        int a = color >> 24 & 0xFF;
        buf.method_22918((Matrix4fc)mat, x1, y1, 0.0f).method_1336(r, g, b, a).method_22913(0.0f, 1.0f);
        buf.method_22918((Matrix4fc)mat, x2, y1, 0.0f).method_1336(r, g, b, a).method_22913(1.0f, 1.0f);
        buf.method_22918((Matrix4fc)mat, x2, y2, 0.0f).method_1336(r, g, b, a).method_22913(1.0f, 0.0f);
        buf.method_22918((Matrix4fc)mat, x1, y2, 0.0f).method_1336(r, g, b, a).method_22913(0.0f, 0.0f);
    }

    private int getDashColor() {
        if (this.colorMode.is("\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439")) {
            return Color.getHSBColor((float)this.rng.nextInt(255) / 255.0f, 1.0f, 1.0f).getRGB() | 0xFF000000;
        }
        return ColorUtil.fade(0);
    }

    private static float computeMotionYaw(double mx, double mz) {
        int y = (int)(Math.toDegrees(Math.atan2(mz, mx)) - 90.0);
        return y < 0 ? (float)(y + 360) : (float)y;
    }

    static {
        for (int i = 1; i <= 21; ++i) {
            DASH_TEXTURES.add(class_2960.method_60655((String)"decide", (String)("textures/dash_cubes/dashcubic" + i + ".png")));
        }
        for (int g = 1; g <= GROUP_SIZES.length; ++g) {
            ArrayList<class_2960> grp = new ArrayList<class_2960>();
            for (int f = 1; f <= GROUP_SIZES[g - 1]; ++f) {
                grp.add(class_2960.method_60655((String)"decide", (String)("textures/dash_cubes/group_dashs/group" + g + "/dashcubic" + f + ".png")));
            }
            DASH_ANIM_GROUPS.add(grp);
        }
        SPARK_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/trails/spark")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
        SPARK_LAYER = class_1921.method_75940((String)"trails_spark", (class_12247)class_12247.method_75927((RenderPipeline)SPARK_PIPELINE).method_75929(16384).method_75938());
        CUBIC_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/trails/cubic")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
        CUBIC_LAYER = class_156.method_34866(tex -> class_1921.method_75940((String)"trails_cubic", (class_12247)class_12247.method_75927((RenderPipeline)CUBIC_PIPELINE).method_75934("Sampler0", tex).method_75937().method_75929(1536).method_75938()));
    }

    @Environment(value=EnvType.CLIENT)
    private class DashCubic {
        float alphaAnim = 0.0f;
        float alphaTarget = 1.0f;
        float cachedTimePC = 0.0f;
        final long startTime = System.currentTimeMillis();
        final DashBase base;
        final int color;
        final float[] rotate = new float[2];
        final List<DashSpark> sparks = new ArrayList<DashSpark>(4);
        final DashTexture dashTexture;

        DashCubic(Trails trails, DashBase base) {
            this.base = base;
            this.color = trails.getDashColor();
            this.dashTexture = new DashTexture(trails);
            this.recomputeRotate(IMinecraft.mc.field_1773.method_19418().method_19329());
        }

        void recomputeRotate(float cameraPitch) {
            double spd = Math.sqrt(this.base.motionX * this.base.motionX + this.base.motionZ * this.base.motionZ);
            if (spd < 5.0E-4) {
                this.rotate[0] = (float)(360.0 * Math.random());
                this.rotate[1] = cameraPitch;
            } else {
                float motYaw = Trails.computeMotionYaw(this.base.motionX, this.base.motionZ);
                this.rotate[0] = motYaw - 60.0f - (this.base.prevYaw - this.base.yaw) * 3.0f;
                float diff = Math.abs(motYaw + 26.3f - this.base.yaw) % 360.0f;
                if (diff > 180.0f) {
                    diff = 360.0f - diff;
                }
                this.rotate[1] = diff < 10.0f || diff > 160.0f ? -90.0f : cameraPitch;
            }
        }

        double getRenderX(float pt) {
            return this.base.prevPosX + (this.base.posX - this.base.prevPosX) * (double)pt;
        }

        double getRenderY(float pt) {
            return this.base.prevPosY + (this.base.posY - this.base.prevPosY) * (double)pt;
        }

        double getRenderZ(float pt) {
            return this.base.prevPosZ + (this.base.posZ - this.base.prevPosZ) * (double)pt;
        }

        void motionProcess(DashCubic next, long nowMs, float cameraPitch) {
            this.base.prevPosX = this.base.posX;
            this.base.prevPosY = this.base.posY;
            this.base.prevPosZ = this.base.posZ;
            this.base.motionX = (next != null ? next.base.motionX : this.base.motionX) / 1.05;
            this.base.posX += 5.0 * this.base.motionX;
            this.base.motionY = (next != null ? next.base.motionY : this.base.motionY) / 1.05;
            this.base.posY = this.base.posY + 5.0 * this.base.motionY / (this.base.motionY < 0.0 ? 1.0 : 3.5);
            this.base.motionZ = (next != null ? next.base.motionZ : this.base.motionZ) / 1.05;
            this.base.posZ += 5.0 * this.base.motionZ;
            this.recomputeRotate(cameraPitch);
            this.sparks.forEach(s -> s.update(nowMs));
            for (int j = this.sparks.size() - 1; j >= 0; --j) {
                if (!this.sparks.get((int)j).dead) continue;
                this.sparks.remove(j);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static class DashBase {
        final int entityId;
        double motionX;
        double motionY;
        double motionZ;
        double posX;
        double posY;
        double posZ;
        double prevPosX;
        double prevPosY;
        double prevPosZ;
        final int rMTime;
        final float yaw;
        final float prevYaw;

        DashBase(class_1309 entity, float speed, float offsetPC, int rmTime) {
            this.entityId = entity.method_5628();
            this.rMTime = rmTime;
            this.yaw = entity.method_36454();
            this.prevYaw = entity.field_5982;
            double mx = -(entity.field_6038 - entity.method_23317());
            double my = -(entity.field_5971 - entity.method_23318());
            double mz = -(entity.field_5989 - entity.method_23321());
            this.motionX = mx;
            this.motionY = my;
            this.motionZ = mz;
            float h = entity.method_17682();
            float swimDiv = entity.method_5869() ? 2.4f : 1.0f;
            this.posX = entity.field_6038 - mx * (double)offsetPC + (-0.0875 + 0.175 * Math.random());
            this.posY = entity.field_5971 - my * (double)offsetPC + ((double)(h / swimDiv) / 3.0 + (double)(h / swimDiv) / 4.0 * Math.random() * 2.7);
            this.posZ = entity.field_5989 - mz * (double)offsetPC + (-0.0875 + 0.175 * Math.random());
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionX *= (double)speed;
            this.motionY *= (double)speed;
            this.motionZ *= (double)speed;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private class DashTexture {
        private final List<class_2960> textures;
        private final boolean animated;
        private final long spawnTime = System.currentTimeMillis();
        private final long animDur;

        DashTexture(Trails trails) {
            boolean doAnim;
            this.animated = doAnim = trails.rng.nextInt(100) > 40 && !DASH_ANIM_GROUPS.isEmpty();
            if (doAnim) {
                this.textures = DASH_ANIM_GROUPS.get(trails.rng.nextInt(DASH_ANIM_GROUPS.size()));
                this.animDur = (long)((float)(550 + trails.rng.nextInt(300)) * trails.dashLength.getValue().floatValue());
            } else {
                this.textures = DASH_TEXTURES.isEmpty() ? List.of() : List.of(DASH_TEXTURES.get(trails.rng.nextInt(DASH_TEXTURES.size())));
                this.animDur = 0L;
            }
        }

        class_2960 getCurrent(long nowMs) {
            if (this.textures.isEmpty()) {
                return null;
            }
            if (this.animated && this.textures.size() > 1 && this.animDur > 0L) {
                int idx = (int)((nowMs - this.spawnTime) / 80L % (long)this.textures.size());
                return this.textures.get(idx);
            }
            return this.textures.get(0);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static class DashSpark {
        double posX;
        double posY;
        double posZ;
        double prevX;
        double prevY;
        double prevZ;
        boolean dead = false;
        float cachedAlphaPC = 1.0f;
        final double sinYaw;
        final double cosYaw;
        final double cosPitchOffset;
        final double speed = Math.random() / 50.0;
        final long startTime = System.currentTimeMillis();

        DashSpark() {
            double yaw = Math.random() * Math.PI * 2.0;
            double pitch = Math.toRadians(-90.0 + Math.random() * 180.0 - 90.0);
            this.sinYaw = Math.sin(yaw);
            this.cosYaw = Math.cos(yaw);
            this.cosPitchOffset = Math.cos(pitch);
        }

        void update(long nowMs) {
            float timePC = class_3532.method_15363((float)((float)(nowMs - this.startTime) / 1000.0f), (float)0.0f, (float)1.0f);
            this.dead = timePC >= 1.0f;
            this.cachedAlphaPC = 1.0f - timePC;
            this.prevX = this.posX;
            this.prevY = this.posY;
            this.prevZ = this.posZ;
            this.posX += this.sinYaw * this.speed;
            this.posY += this.cosPitchOffset * this.speed;
            this.posZ += this.cosYaw * this.speed;
        }

        double getRenderX(float pt) {
            return this.prevX + (this.posX - this.prevX) * (double)pt;
        }

        double getRenderY(float pt) {
            return this.prevY + (this.posY - this.prevY) * (double)pt;
        }

        double getRenderZ(float pt) {
            return this.prevZ + (this.posZ - this.prevZ) * (double)pt;
        }
    }
}

