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
 *  net.minecraft.class_156
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
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
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_156;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.Particles;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.colors.ColorUtil;

@ModuleInfo(name="World Cubes", desc="\u0421\u0435\u0442\u043a\u0430 \u043f\u043e \u043c\u0438\u0440\u0443 \u0441\u043e \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435\u043c", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class WorldCubes
extends Module {
    public ModeSetting typeColor = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439");
    public ColorSetting tintColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", -16711681).setVisible(() -> this.typeColor.is("\u0421\u0432\u043e\u0439"));
    public SliderSetting range = new SliderSetting(this, "\u0420\u0430\u0434\u0438\u0443\u0441 (\u044f\u0447\u0435\u0439\u043a\u0438)", 4.0f, 1.0f, 30.0f, 1.0f);
    public SliderSetting vRange = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 (\u044f\u0447\u0435\u0439\u043a\u0438)", 2.0f, 1.0f, 20.0f, 1.0f);
    public SliderSetting lineAlphaS = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u043b\u0438\u043d\u0438\u0439", 0.22f, 0.04f, 1.0f, 0.02f);
    public BooleanSetting showGlow = new BooleanSetting((Module)this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u0432 \u0443\u0433\u043b\u0430\u0445", true);
    public SliderSetting glowSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 0.08f, 0.02f, 1.0f, 0.02f).setVisible(() -> this.showGlow.getValue());
    public SliderSetting glowBright = new SliderSetting(this, "\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 1.8f, 0.5f, 4.0f, 0.1f).setVisible(() -> this.showGlow.getValue());
    private final Animation shiftAnim = new Animation();
    private int currOx;
    private int currOy;
    private int currOz;
    private int prevOx;
    private int prevOy;
    private int prevOz;
    private boolean firstFrame = true;
    private static final int CELL = 12;
    private final class_9799 allocator = new class_9799(262144);
    public static final RenderPipeline ROMB_ESP_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation("pipeline/wtex").withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color").withSampler("Sampler0").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withCull(false).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).build());
    public static final Function<class_2960, class_1921> ROMB_ESP = class_156.method_34866(texture -> {
        class_12247 setup = class_12247.method_75927((RenderPipeline)ROMB_ESP_PIPELINE).method_75934("Sampler0", texture).method_75937().method_75929(1536).method_75938();
        return class_1921.method_75940((String)"wtex", (class_12247)setup);
    });
    private static final RenderPipeline LINE_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"world_cubes_grid")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_29344).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    private static final class_1921 LINE_LAYER = class_1921.method_75940((String)"world_cubes_lines", (class_12247)class_12247.method_75927((RenderPipeline)LINE_PIPELINE).method_75929(16384).method_75938());

    public int getColor() {
        if (this.typeColor.is("\u0422\u0435\u043c\u0430")) {
            return ColorUtil.getClientColor1(1);
        }
        return this.tintColor.getValue();
    }

    @Override
    protected void onEnable() {
        this.firstFrame = true;
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        if (WorldCubes.mc.field_1724 == null || WorldCubes.mc.field_1687 == null) {
            return;
        }
        int ox = WorldCubes.snapDown(WorldCubes.mc.field_1724.method_23317());
        int oy = WorldCubes.snapDown(WorldCubes.mc.field_1724.method_23318());
        int oz = WorldCubes.snapDown(WorldCubes.mc.field_1724.method_23321());
        if (this.firstFrame) {
            this.currOx = ox;
            this.currOy = oy;
            this.currOz = oz;
            this.prevOx = ox;
            this.prevOy = oy;
            this.prevOz = oz;
            this.shiftAnim.set(1.0);
            this.firstFrame = false;
        } else if (ox != this.currOx || oy != this.currOy || oz != this.currOz) {
            this.prevOx = this.currOx;
            this.prevOy = this.currOy;
            this.prevOz = this.currOz;
            this.currOx = ox;
            this.currOy = oy;
            this.currOz = oz;
            this.shiftAnim.set(0.0);
            this.shiftAnim.run(1.0, 0.25, Easings.QUAD_OUT);
        }
        this.shiftAnim.update();
        float t = this.shiftAnim.get();
        int col = this.getColor();
        int cr = col >> 16 & 0xFF;
        int cg = col >> 8 & 0xFF;
        int cb = col & 0xFF;
        float baseAlpha = this.lineAlphaS.getValue().floatValue() * 255.0f;
        int rI = this.range.getValue().intValue();
        int vI = this.vRange.getValue().intValue();
        class_243 cam = WorldCubes.mc.field_1773.method_19418().method_71156();
        class_4587 stack = e.getMatrixStack();
        Matrix4f mat = stack.method_23760().method_23761();
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        class_4588 lb = immediate.method_73477(LINE_LAYER);
        if (t < 0.999f) {
            int oldA = (int)(baseAlpha * (1.0f - t));
            this.drawLines(lb, mat, cam, this.prevOx, this.prevOy, this.prevOz, cr, cg, cb, oldA, rI, vI);
        }
        int newA = (int)(baseAlpha * t);
        this.drawLines(lb, mat, cam, this.currOx, this.currOy, this.currOz, cr, cg, cb, newA, rI, vI);
        if (this.showGlow.getValue().booleanValue()) {
            float gs = this.glowSize.getValue().floatValue();
            float brite = this.glowBright.getValue().floatValue();
            int newGlowA = Math.min(255, (int)(15.0f * t * brite));
            this.drawGlows(immediate, ROMB_ESP.apply(Particles.ParticleType.BLOOM.texture()), mat, cam, this.currOx, this.currOy, this.currOz, cr, cg, cb, newGlowA, gs * 2.0f, rI, vI);
            newGlowA = Math.min(255, (int)(255.0f * t * brite));
            this.drawGlows(immediate, ROMB_ESP.apply(Particles.ParticleType.CIRCLE.texture()), mat, cam, this.currOx, this.currOy, this.currOz, cr, cg, cb, newGlowA, gs, rI, vI);
        }
        immediate.method_22993();
    }

    private void drawLines(class_4588 lb, Matrix4f mat, class_243 cam, int ox, int oy, int oz, int cr, int cg, int cb, int alpha, int rI, int vI) {
        int y;
        float rz;
        if (alpha <= 0) {
            return;
        }
        int xMin = ox - rI * 12;
        int xMax = ox + rI * 12;
        int zMin = oz - rI * 12;
        int zMax = oz + rI * 12;
        int yMin = oy - (vI - 1) * 12;
        int yMax = oy + vI * 12;
        for (int x = xMin; x <= xMax; x += 12) {
            for (int z = zMin; z <= zMax; z += 12) {
                float rx = (float)((double)x - cam.field_1352);
                rz = (float)((double)z - cam.field_1350);
                lb.method_22918((Matrix4fc)mat, rx, (float)((double)yMin - cam.field_1351), rz).method_1336(cr, cg, cb, alpha);
                lb.method_22918((Matrix4fc)mat, rx, (float)((double)yMax - cam.field_1351), rz).method_1336(cr, cg, cb, alpha);
            }
        }
        for (y = yMin; y <= yMax; y += 12) {
            float ry = (float)((double)y - cam.field_1351);
            for (int z = zMin; z <= zMax; z += 12) {
                rz = (float)((double)z - cam.field_1350);
                lb.method_22918((Matrix4fc)mat, (float)((double)xMin - cam.field_1352), ry, rz).method_1336(cr, cg, cb, alpha);
                lb.method_22918((Matrix4fc)mat, (float)((double)xMax - cam.field_1352), ry, rz).method_1336(cr, cg, cb, alpha);
            }
        }
        for (y = yMin; y <= yMax; y += 12) {
            float ry = (float)((double)y - cam.field_1351);
            for (int x = xMin; x <= xMax; x += 12) {
                float rx = (float)((double)x - cam.field_1352);
                lb.method_22918((Matrix4fc)mat, rx, ry, (float)((double)zMin - cam.field_1350)).method_1336(cr, cg, cb, alpha);
                lb.method_22918((Matrix4fc)mat, rx, ry, (float)((double)zMax - cam.field_1350)).method_1336(cr, cg, cb, alpha);
            }
        }
    }

    private void drawGlows(class_4597.class_4598 immediate, class_1921 glowLayer, Matrix4f mat, class_243 cam, int ox, int oy, int oz, int cr, int cg, int cb, int alpha, float gs, int rI, int vI) {
        if (alpha <= 0) {
            return;
        }
        int xMin = ox - rI * 12;
        int xMax = ox + rI * 12;
        int zMin = oz - rI * 12;
        int zMax = oz + rI * 12;
        int yMin = oy - (vI - 1) * 12;
        int yMax = oy + vI * 12;
        class_4184 camera = WorldCubes.mc.field_1773.method_19418();
        Quaternionf q = camera.method_23767();
        Vector3f c0 = new Vector3f(-gs, -gs, 0.0f).rotate((Quaternionfc)q);
        Vector3f c1 = new Vector3f(gs, -gs, 0.0f).rotate((Quaternionfc)q);
        Vector3f c2 = new Vector3f(gs, gs, 0.0f).rotate((Quaternionfc)q);
        Vector3f c3 = new Vector3f(-gs, gs, 0.0f).rotate((Quaternionfc)q);
        float yawRad = (float)Math.toRadians(camera.method_19330());
        float pitchRad = (float)Math.toRadians(camera.method_19329());
        float cosPitch = (float)Math.cos(pitchRad);
        float lx = (float)(-Math.sin(yawRad) * (double)cosPitch);
        float ly = (float)(-Math.sin(pitchRad));
        float lz = (float)(Math.cos(yawRad) * (double)cosPitch);
        class_4588 gb = immediate.method_73477(glowLayer);
        for (int x = xMin; x <= xMax; x += 12) {
            for (int y = yMin; y <= yMax; y += 12) {
                for (int z = zMin; z <= zMax; z += 12) {
                    float rx = (float)((double)x - cam.field_1352);
                    float ry = (float)((double)y - cam.field_1351);
                    float rz = (float)((double)z - cam.field_1350);
                    if (rx * lx + ry * ly + rz * lz < -gs * 2.0f) continue;
                    gb.method_22918((Matrix4fc)mat, rx + c0.x, ry + c0.y, rz + c0.z).method_1336(cr, cg, cb, alpha).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    gb.method_22918((Matrix4fc)mat, rx + c1.x, ry + c1.y, rz + c1.z).method_1336(cr, cg, cb, alpha).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    gb.method_22918((Matrix4fc)mat, rx + c2.x, ry + c2.y, rz + c2.z).method_1336(cr, cg, cb, alpha).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    gb.method_22918((Matrix4fc)mat, rx + c3.x, ry + c3.y, rz + c3.z).method_1336(cr, cg, cb, alpha).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                }
            }
        }
    }

    private static int snapDown(double v) {
        return (int)Math.floor(v / 12.0) * 12;
    }
}

