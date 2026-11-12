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
 *  net.minecraft.class_156
 *  net.minecraft.class_1921
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_7833
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
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
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_1921;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
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
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.targetesp.ChainTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.CrossTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.CrystalTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.DeluxeTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.MarkerTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.PentagonTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.RhombTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.SkullTargetEspRenderer;
import ru.decide.module.impl.render.targetesp.TargetEspHoverRenderer;
import ru.decide.module.impl.render.targetesp.TargetEspRenderContext;
import ru.decide.module.impl.render.targetesp.VortexTargetEspRenderer;
import ru.decide.module.impl.render.targetrender.TargetMarker;
import ru.decide.module.impl.render.targetrender.TargetSphere;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.MathUtil;

@ModuleInfo(name="Target Esp", desc="\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u044f \u0442\u0430\u0440\u0433\u0435\u0442\u0430", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class TargetEsp
extends Module
implements ModulePreview {
    public ButtonSetting previewButton = PreviewSettings.button(this);
    public ModeSetting type = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c", "\u041f\u0440\u0438\u0437\u0440\u0430\u043a\u0438", "\u041c\u043e\u043b\u043d\u0438\u0438", "\u041a\u043e\u043b\u044c\u0446\u043e", "\u0411\u0443\u0431\u043b\u0438\u043a", "\u041a\u0443\u0431\u0438\u043a\u0438", "\u0426\u0435\u043f\u044c", "\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b", "\u041c\u0430\u0440\u043a\u0435\u0440 \u043a\u043b\u0438\u0435\u043d\u0442\u0430", "\u0420\u043e\u043c\u0431", "\u0427\u0435\u0440\u0435\u043f", "\u0412\u0438\u0445\u0440\u044c", "\u0421\u043a\u043e\u0431\u044b", "\u041a\u0440\u0435\u0441\u0442\u044b", "\u041f\u0435\u043d\u0442\u0430\u0433\u0440\u0430\u043c\u043c\u0430", "\u0421\u0444\u0435\u0440\u0430", "\u041c\u0430\u0440\u043a\u0435\u0440 \u043c\u0435\u0442\u043a\u0430", "\u0414\u0443\u0448\u0438", "\u041a\u0440\u0443\u0433", "\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u044b\u0435 \u0437\u0432\u0451\u0437\u0434\u043e\u0447\u043a\u0438", "\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u0430\u044f \u043c\u0435\u0442\u043a\u0430", "\u041f\u0438\u043a\u0441\u0435\u043b\u044c\u043d\u044b\u0435 \u0441\u0435\u0440\u0434\u0435\u0447\u043a\u0438");
    public ModeSetting typeGhost = new ModeSetting((Module)this, "\u0422\u0438\u043f \u043f\u0440\u0438\u0437\u0440\u0430\u043a\u043e\u0432", "1", "2", "3", "4").setVisible(() -> this.type.is("\u041f\u0440\u0438\u0437\u0440\u0430\u043a\u0438"));
    public BooleanSetting lightningProjectiles = new BooleanSetting((Module)this, "\u041c\u043e\u043b\u043d\u0438\u0438 \u043a \u0441\u043d\u0430\u0440\u044f\u0434\u0430\u043c", true).setVisible(() -> this.type.is("\u041c\u043e\u043b\u043d\u0438\u0438"));
    public BooleanSetting chainProjectiles = new BooleanSetting((Module)this, "\u0426\u0435\u043f\u044c \u043a \u0441\u043d\u0430\u0440\u044f\u0434\u0430\u043c", true).setVisible(() -> this.type.is("\u0426\u0435\u043f\u044c"));
    private final DeluxeTargetEspRenderer deluxeRenderer = new DeluxeTargetEspRenderer();
    private static final String[] SPHERE_TEXTURE_MODES = new String[]{"\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", "\u0421\u0435\u0440\u0434\u0446\u0435", "\u0417\u0432\u0435\u0437\u0434\u0430", "\u0421\u043d\u0435\u0436\u0438\u043d\u043a\u0430", "\u041a\u0440\u0435\u0441\u0442", "\u041e\u0441\u043a\u043e\u043b\u043a\u0438", "\u0421\u0438\u044f\u043d\u0438\u0435"};
    public SliderSetting sphereCount = new SliderSetting(this, "\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0447\u0430\u0441\u0442\u0438\u0446 \u0441\u0444\u0435\u0440\u044b", 8.0f, 6.0f, 10.0f, 1.0f).setVisible(() -> this.type.is("\u0421\u0444\u0435\u0440\u0430"));
    public SliderSetting sphereAlpha = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0441\u0444\u0435\u0440\u044b", 0.45f, 0.05f, 1.0f, 0.05f).setVisible(() -> this.type.is("\u0421\u0444\u0435\u0440\u0430"));
    public SliderSetting sphereSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u0447\u0430\u0441\u0442\u0438\u0446", 0.2f, 0.05f, 0.3f, 0.01f).setVisible(() -> this.type.is("\u0421\u0444\u0435\u0440\u0430"));
    public SliderSetting sphereSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0447\u0430\u0441\u0442\u0438\u0446", 2.5f, 0.1f, 3.0f, 0.1f).setVisible(() -> this.type.is("\u0421\u0444\u0435\u0440\u0430"));
    public ModeSetting sphereTexture = new ModeSetting((Module)this, "\u0422\u0435\u043a\u0441\u0442\u0443\u0440\u0430 \u0441\u0444\u0435\u0440\u044b", SPHERE_TEXTURE_MODES).setVisible(() -> this.type.is("\u0421\u0444\u0435\u0440\u0430"));
    public ModeSetting markerMode = new ModeSetting((Module)this, "\u0421\u0442\u0438\u043b\u044c \u043c\u0430\u0440\u043a\u0435\u0440\u0430", "\u041a\u0432\u0430\u0434\u0440\u0430\u0442", "\u0422\u0440\u0435\u0443\u0433\u043e\u043b\u044c\u043d\u0438\u043a", "\u041a\u0440\u0443\u0433").setVisible(() -> this.type.is("\u041c\u0430\u0440\u043a\u0435\u0440 \u043c\u0435\u0442\u043a\u0430"));
    public SliderSetting markerSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u043c\u0430\u0440\u043a\u0435\u0440\u0430", 5.0f, 3.0f, 10.0f, 0.1f).setVisible(() -> this.type.is("\u041c\u0430\u0440\u043a\u0435\u0440 \u043c\u0435\u0442\u043a\u0430"));
    public BooleanSetting markerSpin = new BooleanSetting((Module)this, "\u0412\u0440\u0430\u0449\u0430\u0442\u044c \u043c\u0430\u0440\u043a\u0435\u0440", true).setVisible(() -> this.type.is("\u041c\u0430\u0440\u043a\u0435\u0440 \u043c\u0435\u0442\u043a\u0430"));
    public SliderSetting markerSpinSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0432\u0440\u0430\u0449\u0435\u043d\u0438\u044f", 1.0f, 0.1f, 5.0f, 0.1f).setVisible(() -> this.type.is("\u041c\u0430\u0440\u043a\u0435\u0440 \u043c\u0435\u0442\u043a\u0430") && this.markerSpin.getValue() != false);
    public SliderSetting markerSpinAmplitude = new SliderSetting(this, "\u0410\u043c\u043f\u043b\u0438\u0442\u0443\u0434\u0430 \u0432\u0440\u0430\u0449\u0435\u043d\u0438\u044f", 90.0f, 15.0f, 360.0f, 1.0f).setVisible(() -> this.type.is("\u041c\u0430\u0440\u043a\u0435\u0440 \u043c\u0435\u0442\u043a\u0430") && this.markerSpin.getValue() != false);
    public SliderSetting sizeGlow = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 0.35f, 0.1f, 0.75f, 0.05f).setVisible(() -> this.type.is("\u041f\u0440\u0438\u0437\u0440\u0430\u043a\u0438"));
    public SliderSetting sizeGlowOFF = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 0.7f, 0.1f, 2.0f, 0.05f).setVisible(() -> this.type.is("\u041f\u0440\u0438\u0437\u0440\u0430\u043a\u0438"));
    public SliderSetting bublikSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u0431\u0443\u0431\u043b\u0438\u043a\u0430", 1.0f, 0.5f, 2.5f, 0.05f).setVisible(() -> this.type.is("\u0411\u0443\u0431\u043b\u0438\u043a"));
    public SliderSetting targetSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 1.0f, 0.1f, 4.0f, 0.05f).setVisible(() -> !this.type.is("\u0427\u0435\u0440\u0435\u043f"));
    public BooleanSetting onlyVisible = new BooleanSetting((Module)this, "\u0422\u043e\u043b\u044c\u043a\u043e \u0432 \u0437\u043e\u043d\u0435 \u0432\u0438\u0434\u0438\u043c\u043e\u0441\u0442\u0438", true);
    public SliderSetting targetHold = new SliderSetting(this, "\u0414\u0435\u0440\u0436\u0430\u0442\u044c \u0446\u0435\u043b\u044c (\u0441\u0435\u043a)", 10.0f, 0.0f, 30.0f, 0.5f);
    public SliderSetting crossesFillAlpha = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u043a\u0440\u0435\u0441\u0442\u043e\u0432", 0.5f, 0.1f, 1.0f, 0.05f).setVisible(() -> this.type.is("\u041a\u0440\u0435\u0441\u0442\u044b"));
    public SliderSetting hoverSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u044d\u0444\u0444\u0435\u043a\u0442\u0430", 1.0f, 0.5f, 2.0f, 0.05f).setVisible(this::hoverMode);
    public BooleanSetting hoverGround = new BooleanSetting((Module)this, "\u042d\u0444\u0444\u0435\u043a\u0442 \u043d\u0430 \u0437\u0435\u043c\u043b\u0435", true).setVisible(this::hoverMode);
    public BooleanSetting hoverTrails = new BooleanSetting((Module)this, "\u042d\u0444\u0444\u0435\u043a\u0442 \u0432\u043e\u043a\u0440\u0443\u0433 \u0446\u0435\u043b\u0438", true).setVisible(this::hoverMode);
    public BooleanSetting hoverParticles = new BooleanSetting((Module)this, "\u0427\u0430\u0441\u0442\u0438\u0446\u044b", true).setVisible(this::hoverMode);
    public ModeSetting hoverColorMode = new ModeSetting((Module)this, "\u0420\u0430\u0441\u0446\u0432\u0435\u0442\u043a\u0430", "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439").setVisible(this::hoverMode);
    public ColorSetting hoverColor1 = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 1", -14039553).setVisible(() -> this.hoverMode() && this.hoverColorMode.is("\u0421\u0432\u043e\u0439"));
    public ColorSetting hoverColor2 = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 2", -5222913).setVisible(() -> this.hoverMode() && this.hoverColorMode.is("\u0421\u0432\u043e\u0439"));
    private final PreviewSettings previewSettings = PreviewSettings.of(this, 4.0f, 0.0f, 2.0f);
    private final class_9799 boxAllocator = new class_9799(262144);
    private class_1309 previewTarget;
    private final TargetEspHoverRenderer hoverRenderer = new TargetEspHoverRenderer();
    public class_1309 target = null;
    private long targetSeenMs = 0L;
    private class_1309 heldTarget = null;
    public Animation alpha = new Animation();
    public Animation alpha_2 = new Animation();
    private final Animation hurtAnim = new Animation();
    private final long lightningStartNanos = System.nanoTime();
    private float animationNurik = 0.0f;
    private long currentTimeSpirits = 0L;
    public static final RenderPipeline RING_FILL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"ring_esp_fill")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    private static final RenderPipeline RING_LINE_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"ring_esp_line")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_29344).withCull(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    public static final class_1921 RING_FILL_LAYER = class_1921.method_75940((String)"ring_esp_fill", (class_12247)class_12247.method_75927((RenderPipeline)RING_FILL_PIPELINE).method_75929(65536).method_75938());
    public static final class_1921 RING_LINE_LAYER = class_1921.method_75940((String)"ring_esp_line", (class_12247)class_12247.method_75927((RenderPipeline)RING_LINE_PIPELINE).method_75929(16384).method_75938());
    public static final RenderPipeline ROMB_ESP_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation("pipeline/wtex").withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color").withSampler("Sampler0").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withCull(false).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).build());
    public static final Function<class_2960, class_1921> ROMB_ESP = class_156.method_34866(texture -> {
        class_12247 setup = class_12247.method_75927((RenderPipeline)ROMB_ESP_PIPELINE).method_75934("Sampler0", texture).method_75937().method_75929(1536).method_75938();
        return class_1921.method_75940((String)"wtex", (class_12247)setup);
    });

    private int hurtColor(int baseColor, float alphaPC) {
        if (this.target == null) {
            return baseColor;
        }
        this.hurtAnim.update();
        int hurtTicks = this.target.field_6235;
        float hurtPC = (float)Math.sin((double)hurtTicks * 0.15707963267948966);
        this.hurtAnim.run((double)hurtPC, (double)0.1f, Easings.SINE_OUT);
        float atts = this.hurtAnim.get();
        if (atts <= 0.001f) {
            return baseColor;
        }
        int redColor = ColorUtil.getColor(200, 70, 70, (int)(255.0f * alphaPC));
        return ColorUtil.overCol(ColorUtil.multAlpha(baseColor, alphaPC), redColor, atts);
    }

    private int sphereTextureIndex() {
        String mode = this.sphereTexture.getValue();
        for (int i = 0; i < SPHERE_TEXTURE_MODES.length; ++i) {
            if (!SPHERE_TEXTURE_MODES[i].equals(mode)) continue;
            return i;
        }
        return 0;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        this.hoverRenderer.clear();
        this.deluxeRenderer.clear();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.boxAllocator.method_60809();
        this.hoverRenderer.clear();
        this.deluxeRenderer.clear();
    }

    private boolean hoverMode() {
        return TargetEspHoverRenderer.supports(this.type.getValue());
    }

    private boolean hasLineOfSight(class_1309 entity) {
        class_243 to;
        if (entity == null || TargetEsp.mc.field_1724 == null || TargetEsp.mc.field_1687 == null) {
            return false;
        }
        class_243 from = TargetEsp.mc.field_1724.method_33571();
        class_3965 hit = TargetEsp.mc.field_1687.method_17742(new class_3959(from, to = entity.method_5829().method_1005(), class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)TargetEsp.mc.field_1724));
        return hit == null || hit.method_17783() == class_239.class_240.field_1333;
    }

    private class_1309 targeted() {
        class_1309 current;
        Object object = this.previewTarget != null ? this.previewTarget : (current = TargetEsp.mc.field_1692 != null && TargetEsp.mc.field_1692.method_5709() ? TargetEsp.mc.field_1692.method_72393() : null);
        if (current != null && current.method_5767()) {
            return null;
        }
        return current;
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (TargetEsp.mc.field_1724 == null || TargetEsp.mc.field_1687 == null) {
            return;
        }
        this.hoverRenderer.tick(this.type.getValue(), this.targeted(), TargetEsp.mc.field_1724.method_33571(), TargetEsp.mc.field_1724.method_5828(1.0f), this.targetSpeed.getValue().floatValue(), this.hoverParticles.getValue(), this.hoverSize.getValue().floatValue(), this.hoverColorMode.getValue(), this.hoverColor1.getValue(), this.hoverColor2.getValue());
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
    public void previewStart(PreviewContext ctx) {
        this.previewTarget = ctx.dummy();
    }

    @Override
    public void previewTick(PreviewContext ctx) {
        this.previewTarget = ctx.dummy();
    }

    @Override
    public void previewSpawn(PreviewContext ctx) {
        if (ctx.dummy() != null) {
            ctx.dummy().field_6235 = 10;
        }
    }

    @Override
    public void previewStop() {
        this.previewTarget = null;
        this.target = null;
        this.heldTarget = null;
    }

    @EventHandler
    public void onRender(EventRender3D e) {
        boolean newMode;
        int color;
        float hurtPC;
        class_4588 consumer;
        float scale;
        double offY;
        int i;
        int cound;
        int ghostCount;
        class_243 lerpedPos;
        int color4;
        int color2;
        int n8;
        int n7;
        int color3;
        class_4588 consumer2;
        Matrix4f matrix;
        float scale2;
        double particleZ;
        double particleY;
        double particleX;
        int n5;
        float f4;
        float f3;
        float f2;
        int j;
        int i2;
        class_4184 camera;
        int n4;
        int n3;
        int n2;
        int baseColor;
        int redColor;
        int fadeColor;
        float atts;
        float hurtPC2;
        int hurtTicks;
        double z;
        double y;
        double x;
        class_243 cameraPos;
        class_243 lerpedPos2;
        class_4587 matrices;
        long timeDiff;
        boolean keep;
        this.alpha.update();
        this.deluxeRenderer.tick();
        class_1309 currentTarget = this.targeted();
        long nowMs = System.currentTimeMillis();
        if (currentTarget != null) {
            this.heldTarget = currentTarget;
            this.targetSeenMs = nowMs;
        }
        if (TargetEsp.mc.field_1687 == null || TargetEsp.mc.field_1724 == null) {
            return;
        }
        long holdMs = (long)(Math.max(0.0f, this.targetHold.getValue().floatValue()) * 1000.0f);
        boolean bl = keep = this.heldTarget != null && !this.heldTarget.method_31481() && this.heldTarget.method_5805() && nowMs - this.targetSeenMs <= holdMs;
        if (!keep) {
            this.heldTarget = null;
        }
        boolean sight = keep && (this.onlyVisible.getValue() == false || this.hasLineOfSight(this.heldTarget));
        this.target = sight ? this.heldTarget : null;
        this.alpha.run(sight ? 1.0 : 0.0, (double)0.15f, Easings.SINE_OUT);
        float alphaPC = this.alpha.get();
        if (this.hoverMode()) {
            this.hoverRenderer.render(e, this.target, alphaPC, this.hoverGround.getValue(), this.hoverTrails.getValue(), this.hoverSize.getValue().floatValue(), this.type.getValue(), this.hoverColorMode.getValue(), this.hoverColor1.getValue(), this.hoverColor2.getValue(), this.boxAllocator);
            return;
        }
        if (alphaPC > 0.001f && this.target != null && this.type.is("\u041c\u0430\u0440\u043a\u0435\u0440 \u043c\u0435\u0442\u043a\u0430")) {
            TargetMarker.get().renderWorld(e.getMatrixStack(), this.target, e.getTickDelta(), alphaPC, this.markerMode.getValue(), this.markerSize.getValue().floatValue(), this.markerSpin.getValue(), this.markerSpinSpeed.getValue().floatValue(), this.markerSpinAmplitude.getValue().floatValue(), this.hurtColor(ColorUtil.getClientColor1(1), alphaPC));
            return;
        }
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.boxAllocator);
        if (this.type.is("\u041c\u043e\u043b\u043d\u0438\u0438")) {
            if (alphaPC > 0.001f && this.target != null) {
                TargetEspRenderContext lightningCtx = new TargetEspRenderContext(this.target, alphaPC, e.getTickDelta(), System.currentTimeMillis(), this.hurtColor(ColorUtil.getClientColor1(1), alphaPC), this.hurtColor(ColorUtil.fade(180), alphaPC), (float)this.target.field_6235 / 10.0f, 0.0f);
                this.deluxeRenderer.renderLightning(e.getMatrixStack(), immediate, lightningCtx, this.targetSpeed.getValue().floatValue());
            }
            if (this.lightningProjectiles.getValue().booleanValue()) {
                this.deluxeRenderer.renderProjectileLightning(e.getMatrixStack(), immediate, e.getTickDelta(), this.targetSpeed.getValue().floatValue());
            }
        }
        if (alphaPC > 0.001f && this.target != null && this.type.is("\u0421\u0444\u0435\u0440\u0430")) {
            TargetSphere.get().render(e.getMatrixStack(), this.target, e.getTickDelta(), alphaPC, this.sphereCount.getValue().floatValue(), this.sphereAlpha.getValue().floatValue(), this.sphereSize.getValue().floatValue(), this.sphereSpeed.getValue().floatValue(), this.sphereTextureIndex(), this.hurtColor(ColorUtil.getClientColor1(1), alphaPC));
            return;
        }
        if (alphaPC > 0.001f && this.target != null && this.type.is("\u041f\u0440\u0438\u0437\u0440\u0430\u043a\u0438") && this.typeGhost.is("2")) {
            long currentTime = System.currentTimeMillis();
            if (this.currentTimeSpirits == 0L) {
                this.currentTimeSpirits = currentTime;
            }
            if ((timeDiff = currentTime - this.currentTimeSpirits) > 0L) {
                this.animationNurik += (float)(5L * timeDiff) / (float)this.ghostSpeedMs();
            }
            this.currentTimeSpirits = currentTime;
            matrices = e.getMatrixStack();
            lerpedPos2 = this.target.method_30950(e.getTickDelta());
            cameraPos = TargetEsp.mc.field_1773.method_19418().method_71156();
            x = lerpedPos2.field_1352 - cameraPos.field_1352;
            y = lerpedPos2.field_1351 - cameraPos.field_1351;
            z = lerpedPos2.field_1350 - cameraPos.field_1350;
            alphaPC = (float)this.alpha.getValue();
            this.alpha_2.update();
            hurtTicks = this.target.field_6235;
            hurtPC2 = (float)Math.sin((double)hurtTicks * 0.15707963267948966);
            this.alpha_2.run((double)hurtPC2, (double)0.1f, Easings.SINE_OUT);
            atts = this.alpha_2.get();
            fadeColor = ColorUtil.fade(1);
            redColor = ColorUtil.getColor(200, 70, 70, (int)(255.0f * alphaPC));
            baseColor = ColorUtil.overCol(ColorUtil.multAlpha(fadeColor, alphaPC), redColor, atts);
            n2 = 3;
            n3 = 12;
            n4 = 3 * n2;
            matrices.method_22903();
            camera = TargetEsp.mc.field_1773.method_19418();
            for (i2 = 0; i2 < n4; i2 += n2) {
                for (j = 0; j < n3; ++j) {
                    f2 = this.animationNurik + (float)j * 0.1f;
                    f3 = 0.6f;
                    f4 = 0.4f;
                    n5 = (int)Math.pow(i2, 2.0);
                    matrices.method_22903();
                    particleX = x + (double)f3 * Math.sin(f2 + (float)n5);
                    particleY = y + (double)f4 + (double)0.3f * Math.sin(this.animationNurik + (float)j * 0.2f) + (double)(0.2f * (float)i2);
                    particleZ = z + (double)f3 * Math.cos(f2 - (float)n5);
                    matrices.method_22904(particleX, particleY, particleZ);
                    scale2 = (0.006f + (float)j / 2000.0f) * alphaPC;
                    matrices.method_22905(scale2, scale2, scale2);
                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-camera.method_19330()));
                    matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(camera.method_19329()));
                    matrix = matrices.method_23760().method_23761();
                    consumer2 = immediate.method_73477(ROMB_ESP.apply(class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_3.png")));
                    color3 = baseColor;
                    n7 = -20;
                    n8 = 35;
                    consumer2.method_22918((Matrix4fc)matrix, (float)n7, (float)(n7 + n8), 0.0f).method_39415(baseColor).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)(n7 + n8), (float)(n7 + n8), 0.0f).method_39415(baseColor).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)(n7 + n8), (float)n7, 0.0f).method_39415(baseColor).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)n7, (float)n7, 0.0f).method_39415(baseColor).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    n7 = (int)(-20.0f - 20.0f * this.sizeGlowOFF.getValue().floatValue());
                    n8 = (int)(35.0f + 40.0f * this.sizeGlowOFF.getValue().floatValue());
                    consumer2.method_22918((Matrix4fc)matrix, (float)n7, (float)(n7 + n8), 0.0f).method_39415(ColorUtil.replAlpha(baseColor, alphaPC * this.sizeGlow.getValue().floatValue())).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)(n7 + n8), (float)(n7 + n8), 0.0f).method_39415(ColorUtil.replAlpha(baseColor, alphaPC * this.sizeGlow.getValue().floatValue())).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)(n7 + n8), (float)n7, 0.0f).method_39415(ColorUtil.replAlpha(baseColor, alphaPC * this.sizeGlow.getValue().floatValue())).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)n7, (float)n7, 0.0f).method_39415(ColorUtil.replAlpha(baseColor, alphaPC * this.sizeGlow.getValue().floatValue())).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    matrices.method_22909();
                }
            }
            matrices.method_22909();
        }
        if (alphaPC > 0.001f && this.target != null && this.type.is("\u041f\u0440\u0438\u0437\u0440\u0430\u043a\u0438") && this.typeGhost.is("4")) {
            long currentTime = System.currentTimeMillis();
            if (this.currentTimeSpirits == 0L) {
                this.currentTimeSpirits = currentTime;
            }
            if ((timeDiff = currentTime - this.currentTimeSpirits) > 0L) {
                this.animationNurik += (float)(5L * timeDiff) / (float)this.ghostSpeedMs();
            }
            this.currentTimeSpirits = currentTime;
            matrices = e.getMatrixStack();
            lerpedPos2 = this.target.method_30950(e.getTickDelta());
            cameraPos = TargetEsp.mc.field_1773.method_19418().method_71156();
            x = lerpedPos2.field_1352 - cameraPos.field_1352;
            y = lerpedPos2.field_1351 - cameraPos.field_1351;
            z = lerpedPos2.field_1350 - cameraPos.field_1350;
            alphaPC = (float)this.alpha.getValue();
            this.alpha_2.update();
            hurtTicks = this.target.field_6235;
            hurtPC2 = (float)Math.sin((double)hurtTicks * 0.15707963267948966);
            this.alpha_2.run((double)hurtPC2, (double)0.1f, Easings.SINE_OUT);
            atts = this.alpha_2.get();
            fadeColor = ColorUtil.fade(1);
            redColor = ColorUtil.getColor(200, 70, 70, (int)(255.0f * alphaPC));
            baseColor = ColorUtil.overCol(ColorUtil.multAlpha(fadeColor, alphaPC), redColor, atts);
            n2 = 3;
            n3 = 24;
            n4 = 3 * n2;
            matrices.method_22903();
            camera = TargetEsp.mc.field_1773.method_19418();
            for (i2 = 0; i2 < n4; i2 += n2) {
                for (j = 0; j < n3; ++j) {
                    f2 = this.animationNurik + (float)j * 0.05f;
                    f3 = this.target.method_17681();
                    f4 = 0.45f;
                    n5 = (int)Math.pow(i2, 2.0);
                    matrices.method_22903();
                    particleX = x + (double)f3 * Math.sin(f2 + (float)n5);
                    particleY = y + (double)f4 + (double)0.1f * Math.sin(this.animationNurik + (float)j * 0.1f) + (double)(0.2f * (float)i2);
                    particleZ = z + (double)f3 * Math.cos(f2 - (float)n5);
                    matrices.method_22904(particleX, particleY, particleZ);
                    scale2 = (0.009f + (float)j / 2000.0f) * alphaPC;
                    matrices.method_22905(scale2, scale2, scale2);
                    matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-camera.method_19330()));
                    matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(camera.method_19329()));
                    matrix = matrices.method_23760().method_23761();
                    consumer2 = immediate.method_73477(ROMB_ESP.apply(class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_3.png")));
                    color3 = baseColor;
                    n7 = -12;
                    n8 = 16;
                    consumer2.method_22918((Matrix4fc)matrix, (float)n7, (float)(n7 + n8), 0.0f).method_39415(baseColor).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)(n7 + n8), (float)(n7 + n8), 0.0f).method_39415(baseColor).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)(n7 + n8), (float)n7, 0.0f).method_39415(baseColor).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)n7, (float)n7, 0.0f).method_39415(baseColor).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    n7 = (int)(-12.0f - 20.0f * this.sizeGlowOFF.getValue().floatValue());
                    n8 = (int)(16.0f + 40.0f * this.sizeGlowOFF.getValue().floatValue());
                    consumer2.method_22918((Matrix4fc)matrix, (float)n7, (float)(n7 + n8), 0.0f).method_39415(ColorUtil.replAlpha(baseColor, alphaPC * this.sizeGlow.getValue().floatValue())).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)(n7 + n8), (float)(n7 + n8), 0.0f).method_39415(ColorUtil.replAlpha(baseColor, alphaPC * this.sizeGlow.getValue().floatValue())).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)(n7 + n8), (float)n7, 0.0f).method_39415(ColorUtil.replAlpha(baseColor, alphaPC * this.sizeGlow.getValue().floatValue())).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    consumer2.method_22918((Matrix4fc)matrix, (float)n7, (float)n7, 0.0f).method_39415(ColorUtil.replAlpha(baseColor, alphaPC * this.sizeGlow.getValue().floatValue())).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
                    matrices.method_22909();
                }
            }
            matrices.method_22909();
        }
        if (alphaPC > 0.001f && this.target != null && this.type.is("\u041f\u0440\u0438\u0437\u0440\u0430\u043a\u0438") && this.typeGhost.is("1")) {
            long currentTime = System.currentTimeMillis();
            if (this.currentTimeSpirits == 0L) {
                this.currentTimeSpirits = currentTime;
            }
            if ((timeDiff = currentTime - this.currentTimeSpirits) > 0L) {
                this.animationNurik += (float)(5L * timeDiff) / (float)this.ghostSpeedMs();
            }
            this.currentTimeSpirits = currentTime;
            int hurtTicks2 = this.target.field_6235;
            float hurtPC3 = (float)Math.sin((double)hurtTicks2 * 0.15707963267948966);
            int redColor2 = ColorUtil.getColor(255, 100, 100, (int)(255.0f * alphaPC));
            int color5 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(0), alphaPC), redColor2, hurtPC3);
            color2 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(90), alphaPC), redColor2, hurtPC3);
            int color32 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(180), alphaPC), redColor2, hurtPC3);
            color4 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(360), alphaPC), redColor2, hurtPC3);
            class_4587 matrices2 = e.getMatrixStack();
            lerpedPos = this.target.method_30950(e.getTickDelta());
            class_243 cameraPos2 = TargetEsp.mc.field_1773.method_19418().method_71156();
            long time = System.currentTimeMillis();
            double speed = (double)time / (double)this.ghostSpeedMs();
            float radius = this.target.method_17681() + 0.25f - 0.25f * alphaPC;
            ghostCount = 3;
            cound = 10;
            for (i = 0; i < ghostCount; ++i) {
                for (int s = 0; s < cound; ++s) {
                    matrices2.method_22903();
                    float f22 = this.animationNurik + (float)s * 0.1f;
                    double angle = speed + (double)i * (Math.PI * 2 / (double)ghostCount);
                    double offX = Math.cos(angle + (double)f22) * (double)radius;
                    double offZ = Math.sin(angle + (double)f22) * (double)radius;
                    offY = Math.sin(speed + (double)i) * 0.6 + 0.15 * Math.sin(speed + (double)((float)s * 0.11f));
                    matrices2.method_22904(lerpedPos.field_1352 - cameraPos2.field_1352 + offX, lerpedPos.field_1351 - cameraPos2.field_1351 + (double)(this.target.method_17682() / 2.0f) + offY, lerpedPos.field_1350 - cameraPos2.field_1350 + offZ);
                    matrices2.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-TargetEsp.mc.field_1773.method_19418().method_19330()));
                    matrices2.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(TargetEsp.mc.field_1773.method_19418().method_19329()));
                    scale = (0.12f + (float)s / 40.0f) * 1.25f * alphaPC;
                    matrices2.method_22905(scale, scale, scale);
                    Matrix4f matrix2 = matrices2.method_23760().method_23761();
                    consumer = immediate.method_73477(ROMB_ESP.apply(class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_3.png")));
                    TargetEsp.drawGradientQuad(consumer, matrix2, color5, color2, color32, color4, (int)(255.0f * alphaPC));
                    scale = (0.12f + (float)s / 40.0f) * (100.0f * this.sizeGlowOFF.getValue().floatValue()) / 10.0f * alphaPC;
                    matrices2.method_22905(scale, scale, scale);
                    class_4588 consumer22 = immediate.method_73477(ROMB_ESP.apply(class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_2.png")));
                    TargetEsp.drawGradientQuad(consumer22, matrix2, color5, color2, color32, color4, (int)(255.0f * this.sizeGlow.getValue().floatValue() * 1.0f * alphaPC));
                    matrices2.method_22909();
                }
            }
        }
        if (alphaPC > 0.001f && this.target != null && this.type.is("\u041f\u0440\u0438\u0437\u0440\u0430\u043a\u0438") && this.typeGhost.is("3")) {
            long currentTime = System.currentTimeMillis();
            if (this.currentTimeSpirits == 0L) {
                this.currentTimeSpirits = currentTime;
            }
            if ((timeDiff = currentTime - this.currentTimeSpirits) > 0L) {
                this.animationNurik += (float)(5L * timeDiff) / (float)this.ghostSpeedMs();
            }
            this.currentTimeSpirits = currentTime;
            int hurtTicks3 = this.target.field_6235;
            float hurtPC4 = (float)Math.sin((double)hurtTicks3 * 0.15707963267948966);
            int redColor3 = ColorUtil.getColor(255, 100, 100, (int)(255.0f * alphaPC));
            int color6 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(0), alphaPC), redColor3, hurtPC4);
            color2 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(90), alphaPC), redColor3, hurtPC4);
            int color33 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(180), alphaPC), redColor3, hurtPC4);
            color4 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(360), alphaPC), redColor3, hurtPC4);
            class_4587 matrices3 = e.getMatrixStack();
            lerpedPos = this.target.method_30950(e.getTickDelta());
            class_243 cameraPos3 = TargetEsp.mc.field_1773.method_19418().method_71156();
            long time = System.currentTimeMillis();
            double speed = (double)time / (double)this.ghostSpeedMs();
            float radius = this.target.method_17681() + 0.25f - 0.25f * alphaPC;
            ghostCount = 3;
            cound = 9;
            for (i = 0; i < ghostCount; ++i) {
                for (int s = 0; s < cound; ++s) {
                    matrices3.method_22903();
                    float f23 = this.animationNurik + (float)s * 0.1f;
                    double angle = speed + (double)i * (Math.PI * 2 / (double)ghostCount);
                    double offX = Math.cos(angle + (double)f23) * (double)radius;
                    double offZ = Math.sin(angle + (double)f23) * (double)radius;
                    offY = Math.sin(speed) * 0.7 + 0.2 * Math.sin(speed + (double)((float)s * 0.11f));
                    matrices3.method_22904(lerpedPos.field_1352 - cameraPos3.field_1352 + offX, lerpedPos.field_1351 - cameraPos3.field_1351 + (double)(this.target.method_17682() / 2.0f) + offY, lerpedPos.field_1350 - cameraPos3.field_1350 + offZ);
                    matrices3.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-TargetEsp.mc.field_1773.method_19418().method_19330()));
                    matrices3.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(TargetEsp.mc.field_1773.method_19418().method_19329()));
                    scale = (0.12f + (float)s / 50.0f) * 1.5f;
                    matrices3.method_22905(scale, scale, scale);
                    Matrix4f matrix3 = matrices3.method_23760().method_23761();
                    consumer = immediate.method_73477(ROMB_ESP.apply(class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_3.png")));
                    TargetEsp.drawGradientQuad(consumer, matrix3, color6, color2, color33, color4, (int)(255.0f * alphaPC));
                    scale = (0.12f + (float)s / 50.0f) * (100.0f * this.sizeGlowOFF.getValue().floatValue()) / 10.0f;
                    matrices3.method_22905(scale, scale, scale);
                    class_4588 consumer23 = immediate.method_73477(ROMB_ESP.apply(class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_2.png")));
                    TargetEsp.drawGradientQuad(consumer23, matrix3, color6, color2, color33, color4, (int)(255.0f * this.sizeGlow.getValue().floatValue() * 1.0f * alphaPC));
                    matrices3.method_22909();
                }
            }
        }
        if (alphaPC > 0.001f && this.target != null && this.type.is("\u0411\u0443\u0431\u043b\u0438\u043a")) {
            int hurtTicks4 = this.target.field_6235;
            hurtPC = (float)Math.sin((double)hurtTicks4 * 0.15707963267948966);
            this.alpha_2.update();
            this.alpha_2.run((double)hurtPC, (double)0.1f, Easings.SINE_OUT);
            int redColor4 = ColorUtil.getColor(255, 100, 100, (int)(255.0f * alphaPC));
            color = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(0), alphaPC), redColor4, this.alpha_2.get());
            int color22 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(90), alphaPC), redColor4, this.alpha_2.get());
            int color34 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(180), alphaPC), redColor4, this.alpha_2.get());
            int color42 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(360), alphaPC), redColor4, this.alpha_2.get());
            class_4587 matrices4 = e.getMatrixStack();
            class_243 lerpedPos3 = this.target.method_30950(e.getTickDelta());
            class_243 cameraPos4 = TargetEsp.mc.field_1773.method_19418().method_71156();
            double duration = this.speedMs(1400.0);
            double elapsed = (double)System.currentTimeMillis() % duration;
            boolean down = elapsed > duration / 2.0;
            double raw = elapsed / (duration / 2.0);
            raw = down ? raw - 1.0 : 1.0 - raw;
            double progress = raw < 0.5 ? 2.0 * raw * raw : 1.0 - Math.pow(-2.0 * raw + 2.0, 2.0) / 2.0;
            float height = this.target.method_17682();
            float yBase = 0.12f + (float)progress * Math.max(0.2f, height - 0.24f);
            float direction = down ? -1.0f : 1.0f;
            double spin = (double)System.currentTimeMillis() / (this.speedMs(1400.0) * 0.55);
            float radius = this.target.method_17681() - 0.15f;
            int count = (int)(80.0f * alphaPC);
            for (int layer = 2; layer >= 0; --layer) {
                float layerAlpha = alphaPC * (1.0f - (float)layer * 0.28f);
                float yLayer = yBase + direction * (float)layer * 0.075f;
                float radiusLayer = radius + (float)layer * 0.025f;
                for (int i3 = 0; i3 < count; ++i3) {
                    float pc = (float)i3 / (float)count;
                    double angle = (double)(pc * ((float)Math.PI * 2)) + spin + (double)layer * 0.18;
                    double x2 = Math.cos(angle) * (double)radiusLayer;
                    double z2 = Math.sin(angle) * (double)radiusLayer;
                    float size = (0.2f + (float)layer * 0.018f) * this.bublikSize.getValue().floatValue();
                    int localAlpha = (int)((float)(layer == 0 ? 255 : 105) * layerAlpha);
                    if (layer != 0) continue;
                    matrices4.method_22903();
                    matrices4.method_22904(lerpedPos3.field_1352 - cameraPos4.field_1352 + x2, lerpedPos3.field_1351 - cameraPos4.field_1351 + (double)yLayer, lerpedPos3.field_1350 - cameraPos4.field_1350 + z2);
                    matrices4.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(-TargetEsp.mc.field_1773.method_19418().method_19330()));
                    matrices4.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(TargetEsp.mc.field_1773.method_19418().method_19329()));
                    matrices4.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees((float)Math.toDegrees(-angle) + (float)(spin * 80.0)));
                    matrices4.method_22905(size * 1.25f, size * 1.25f, size);
                    TargetEsp.drawGradientQuad(immediate.method_73477(ROMB_ESP.apply(class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_3.png"))), matrices4.method_23760().method_23761(), color, color22, color34, color42, localAlpha);
                    matrices4.method_22909();
                }
            }
        }
        if (alphaPC > 0.001f && this.target != null && this.type.is("\u041a\u043e\u043b\u044c\u0446\u043e")) {
            int hurtTicks5 = this.target.field_6235;
            hurtPC = (float)Math.sin((double)hurtTicks5 * 0.3141592653589793);
            int redColor5 = ColorUtil.getColor(255, 100, 100, (int)(255.0f * alphaPC));
            double duration = this.speedMs(1800.0);
            double elapsed = (double)System.currentTimeMillis() % duration;
            boolean side = elapsed > duration / 2.0;
            double raw = elapsed / (duration / 2.0);
            raw = side ? raw - 1.0 : 1.0 - raw;
            double progress = raw < 0.5 ? 2.0 * raw * raw : 1.0 - Math.pow(-2.0 * raw + 2.0, 2.0) / 2.0;
            float height2 = this.target.method_17682();
            double eased = (double)height2 / 1.7 * (progress > 0.5 ? 1.0 - progress : progress) * (double)(side ? -1 : 1) / 0.7;
            class_4587 matrices5 = e.getMatrixStack();
            class_243 lerpedPos4 = this.target.method_30950(e.getTickDelta());
            class_243 cameraPos5 = TargetEsp.mc.field_1773.method_19418().method_71156();
            matrices5.method_22903();
            matrices5.method_22904(lerpedPos4.field_1352 - cameraPos5.field_1352, lerpedPos4.field_1351 - cameraPos5.field_1351, lerpedPos4.field_1350 - cameraPos5.field_1350);
            Matrix4f matrix4 = matrices5.method_23760().method_23761();
            float radius = this.target.method_17681() - 0.1f + 0.35f - 0.35f * alphaPC;
            float yBase = (float)((double)height2 * progress);
            float yTop = (float)((double)height2 * progress + eased);
            class_4588 fillBuf = immediate.method_73477(RING_FILL_LAYER);
            for (int seg = 0; seg < 360; ++seg) {
                float a0 = (float)Math.toRadians(seg);
                float a1 = (float)Math.toRadians(seg + 1);
                int c0 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(seg), alphaPC), redColor5, hurtPC);
                int c1 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(seg), alphaPC), redColor5, hurtPC);
                float x0 = (float)(Math.cos(a0) * (double)radius);
                float z0 = (float)(Math.sin(a0) * (double)radius);
                float x1 = (float)(Math.cos(a1) * (double)radius);
                float z1 = (float)(Math.sin(a1) * (double)radius);
                fillBuf.method_22918((Matrix4fc)matrix4, x0, yBase, z0).method_39415(ColorUtil.replAlpha(c0, (int)(120.0f * alphaPC)));
                fillBuf.method_22918((Matrix4fc)matrix4, x1, yBase, z1).method_39415(ColorUtil.replAlpha(c1, (int)(120.0f * alphaPC)));
                fillBuf.method_22918((Matrix4fc)matrix4, x1, yTop, z1).method_39415(ColorUtil.replAlpha(c1, 0));
                fillBuf.method_22918((Matrix4fc)matrix4, x0, yTop, z0).method_39415(ColorUtil.replAlpha(c0, 0));
            }
            class_4588 lineBuf = immediate.method_73477(RING_LINE_LAYER);
            for (int seg = 0; seg < 360; ++seg) {
                float a0 = (float)Math.toRadians(seg);
                float a1 = (float)Math.toRadians(seg + 1);
                int c = ColorUtil.multAlpha(ColorUtil.overCol(ColorUtil.multBright(ColorUtil.fade(1), 0.7f), redColor5, hurtPC), alphaPC);
                lineBuf.method_22918((Matrix4fc)matrix4, (float)(Math.cos(a0) * (double)radius), yBase, (float)(Math.sin(a0) * (double)radius)).method_39415(ColorUtil.replAlpha(c, (int)(150.0f * alphaPC)));
                lineBuf.method_22918((Matrix4fc)matrix4, (float)(Math.cos(a1) * (double)radius), yBase, (float)(Math.sin(a1) * (double)radius)).method_39415(ColorUtil.replAlpha(c, (int)(150.0f * alphaPC)));
            }
            matrices5.method_22909();
        }
        if (alphaPC > 0.001f && this.target != null && this.type.is("\u041a\u0443\u0431\u0438\u043a\u0438")) {
            int i4;
            int hurtTicks6 = this.target.field_6235;
            hurtPC = (float)Math.sin((double)hurtTicks6 * 0.3141592653589793);
            this.alpha_2.update();
            this.alpha_2.run((double)hurtPC, (double)0.1f, Easings.SINE_OUT);
            int redColor6 = ColorUtil.getColor(255, 100, 100, (int)(255.0f * alphaPC));
            color = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(1), alphaPC), redColor6, this.alpha_2.get());
            class_4587 matrices6 = e.getMatrixStack();
            class_243 cameraPos6 = TargetEsp.mc.field_1773.method_19418().method_71156();
            class_243 targetPos = this.target.method_30950(e.getTickDelta());
            float time = (float)Math.cos((double)System.currentTimeMillis() / this.speedMs(2000.0)) * 360.0f + this.alpha_2.get() * 20.0f;
            int cound2 = 12;
            float width = this.target.method_17681() * 1.5f;
            float sizeFI = (1.0f - 0.3f * this.alpha_2.get()) * alphaPC;
            class_4184 camera2 = TargetEsp.mc.field_1773.method_19418();
            for (i4 = 0; i4 < 360; i4 += cound2) {
                float val = 0.70000005f;
                float sin = (float)(Math.sin((float)Math.toRadians((float)i4 + time)) * (double)width * (double)val);
                float cos = (float)(Math.cos((float)Math.toRadians((float)i4 + time)) * (double)width * (double)val);
                double x3 = targetPos.field_1352 + (double)sin;
                double z3 = targetPos.field_1350 + (double)cos;
                double y2 = targetPos.field_1351 + (double)this.target.method_17682() * Math.abs(MathUtil.sin(i4));
                matrices6.method_22903();
                matrices6.method_22904(x3 - cameraPos6.field_1352, y2 - cameraPos6.field_1351, z3 - cameraPos6.field_1350);
                matrices6.method_22907((Quaternionfc)camera2.method_23767());
                float gs = 0.6f * sizeFI;
                matrices6.method_22905(gs, gs, gs);
                TargetEsp.drawGradientQuad(immediate.method_73477(ROMB_ESP.apply(class_2960.method_60655((String)"decide", (String)"textures/visuals/particles_1.png"))), matrices6.method_23760().method_23761(), ColorUtil.multAlpha(color, 0.3f), ColorUtil.multAlpha(color, 0.3f), ColorUtil.multAlpha(color, 0.3f), ColorUtil.multAlpha(color, 0.3f), (int)(alphaPC * 0.35f * 255.0f));
                matrices6.method_22909();
            }
            for (i4 = 0; i4 < 360; i4 += cound2) {
                float val = 0.70000005f;
                float sin = (float)(Math.sin((float)Math.toRadians((float)i4 + time)) * (double)width * (double)val);
                float cos = (float)(Math.cos((float)Math.toRadians((float)i4 + time)) * (double)width * (double)val);
                double x4 = targetPos.field_1352 + (double)sin;
                double z4 = targetPos.field_1350 + (double)cos;
                double y3 = targetPos.field_1351 + (double)this.target.method_17682() * Math.abs(MathUtil.sin(i4));
                class_243 cubePos = new class_243(x4, y3, z4);
                Vector3f directionToTarget = new Vector3f((float)(targetPos.field_1352 - cubePos.field_1352), (float)(targetPos.field_1351 - cubePos.field_1351), (float)(targetPos.field_1350 - cubePos.field_1350)).normalize();
                matrices6.method_22903();
                matrices6.method_22904(x4 - cameraPos6.field_1352, y3 - cameraPos6.field_1351, z4 - cameraPos6.field_1350);
                matrices6.method_22907((Quaternionfc)new Quaternionf().rotationTo((Vector3fc)new Vector3f(0.0f, 1.0f, 0.0f), (Vector3fc)directionToTarget));
                Matrix4f matrix5 = matrices6.method_23760().method_23761();
                float size = 0.06f * sizeFI;
                TargetEsp.drawCubeFillTESP(immediate.method_73477(RING_FILL_LAYER), matrix5, size, ColorUtil.replAlpha(color, (int)(alphaPC * 0.2f * 255.0f)));
                matrices6.method_22909();
            }
            for (i4 = 0; i4 < 360; i4 += cound2) {
                float val = 0.70000005f;
                float sin = (float)(Math.sin((float)Math.toRadians((float)i4 + time)) * (double)width * (double)val);
                float cos = (float)(Math.cos((float)Math.toRadians((float)i4 + time)) * (double)width * (double)val);
                double x5 = targetPos.field_1352 + (double)sin;
                double z5 = targetPos.field_1350 + (double)cos;
                double y4 = targetPos.field_1351 + (double)this.target.method_17682() * Math.abs(MathUtil.sin(i4));
                class_243 cubePos = new class_243(x5, y4, z5);
                Vector3f directionToTarget = new Vector3f((float)(targetPos.field_1352 - cubePos.field_1352), (float)(targetPos.field_1351 - cubePos.field_1351), (float)(targetPos.field_1350 - cubePos.field_1350)).normalize();
                matrices6.method_22903();
                matrices6.method_22904(x5 - cameraPos6.field_1352, y4 - cameraPos6.field_1351, z5 - cameraPos6.field_1350);
                matrices6.method_22907((Quaternionfc)new Quaternionf().rotationTo((Vector3fc)new Vector3f(0.0f, 1.0f, 0.0f), (Vector3fc)directionToTarget));
                Matrix4f matrix6 = matrices6.method_23760().method_23761();
                float size = 0.06f * sizeFI;
                TargetEsp.drawCubeOutlineTESP(immediate.method_73477(RING_LINE_LAYER), matrix6, size, ColorUtil.replAlpha(color, (int)(alphaPC * 255.0f)));
                matrices6.method_22909();
            }
        }
        boolean bl2 = newMode = this.type.is("\u0426\u0435\u043f\u044c") || this.type.is("\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b") || this.type.is("\u041c\u0430\u0440\u043a\u0435\u0440 \u043a\u043b\u0438\u0435\u043d\u0442\u0430") || this.type.is("\u0420\u043e\u043c\u0431") || this.type.is("\u0427\u0435\u0440\u0435\u043f") || this.type.is("\u0412\u0438\u0445\u0440\u044c") || this.type.is("\u0421\u043a\u043e\u0431\u044b") || this.type.is("\u041a\u0440\u0435\u0441\u0442\u044b") || this.type.is("\u041f\u0435\u043d\u0442\u0430\u0433\u0440\u0430\u043c\u043c\u0430");
        if (alphaPC > 0.001f && this.target != null && newMode) {
            int hurtTicks7 = this.target.field_6235;
            float hurtPC5 = class_3532.method_15363((float)((float)Math.sin((double)hurtTicks7 * 0.3141592653589793)), (float)0.0f, (float)1.0f);
            int redColor7 = ColorUtil.getColor(255, 100, 100, (int)(255.0f * alphaPC));
            int primaryColor = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(0), alphaPC), redColor7, hurtPC5);
            int secondaryColor = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(180), alphaPC), redColor7, hurtPC5);
            class_4587 matrices7 = e.getMatrixStack();
            class_243 lerpedPos5 = this.target.method_30950(e.getTickDelta());
            class_243 cameraPos7 = TargetEsp.mc.field_1773.method_19418().method_71156();
            matrices7.method_22903();
            matrices7.method_22904(lerpedPos5.field_1352 - cameraPos7.field_1352, lerpedPos5.field_1351 - cameraPos7.field_1351, lerpedPos5.field_1350 - cameraPos7.field_1350);
            TargetEspRenderContext ctx = new TargetEspRenderContext(this.target, alphaPC, e.getTickDelta(), System.currentTimeMillis(), primaryColor, secondaryColor, hurtPC5, hurtPC5);
            float speed = this.targetSpeed.getValue().floatValue();
            if (this.type.is("\u0426\u0435\u043f\u044c")) {
                ChainTargetEspRenderer.render(matrices7, immediate, ctx, speed);
            } else if (this.type.is("\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b")) {
                CrystalTargetEspRenderer.render(matrices7, immediate, ctx, speed);
            } else if (this.type.is("\u041c\u0430\u0440\u043a\u0435\u0440 \u043a\u043b\u0438\u0435\u043d\u0442\u0430")) {
                MarkerTargetEspRenderer.render(matrices7, immediate, ctx, speed);
            } else if (this.type.is("\u0420\u043e\u043c\u0431")) {
                RhombTargetEspRenderer.render(matrices7, immediate, ctx, speed);
            } else if (this.type.is("\u0427\u0435\u0440\u0435\u043f")) {
                SkullTargetEspRenderer.render(matrices7, immediate, ctx);
            } else if (this.type.is("\u0412\u0438\u0445\u0440\u044c")) {
                VortexTargetEspRenderer.configure(1.2f, speed, true, VortexTargetEspRenderer.TEXTURE_VORTEX);
                VortexTargetEspRenderer.render(matrices7, immediate, ctx);
            } else if (this.type.is("\u0421\u043a\u043e\u0431\u044b")) {
                VortexTargetEspRenderer.configure(1.2f, speed, true, VortexTargetEspRenderer.TEXTURE_BRACKETS);
                VortexTargetEspRenderer.render(matrices7, immediate, ctx);
            } else if (this.type.is("\u041a\u0440\u0435\u0441\u0442\u044b")) {
                float oldAlpha = ctx.alpha();
                TargetEspRenderContext crossCtx = new TargetEspRenderContext(ctx.target(), this.crossesFillAlpha.getValue().floatValue(), ctx.partialTicks(), ctx.frameTimeMs(), ctx.primaryColor(), ctx.secondaryColor(), ctx.hurtProgress(), ctx.chainImpactProgress());
                CrossTargetEspRenderer.render(matrices7, immediate, crossCtx, speed);
            } else if (this.type.is("\u041f\u0435\u043d\u0442\u0430\u0433\u0440\u0430\u043c\u043c\u0430")) {
                PentagonTargetEspRenderer.render(matrices7, immediate, ctx, speed);
            }
            matrices7.method_22909();
        }
        if (this.type.is("\u0426\u0435\u043f\u044c") && this.chainProjectiles.getValue().booleanValue()) {
            this.deluxeRenderer.renderProjectileChains(e.getMatrixStack(), immediate, e.getTickDelta());
        }
        immediate.method_22993();
    }

    private long ghostSpeedMs() {
        return Math.max(50L, Math.round(900.0 / (double)this.targetSpeed.getValue().floatValue()));
    }

    private double speedMs(double baseMs) {
        return Math.max(50.0, baseMs / (double)this.targetSpeed.getValue().floatValue());
    }

    private static void drawGradientQuad(class_4588 buffer, Matrix4f matrix, int color, int alpha) {
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
    }

    private static void drawGradientQuad(class_4588 buffer, Matrix4f matrix, int color, int color2, int color3, int color4, int alpha) {
        buffer.method_22918((Matrix4fc)matrix, -0.5f, -0.5f, 0.0f).method_39415(ColorUtil.replAlpha(color, alpha)).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        buffer.method_22918((Matrix4fc)matrix, 0.5f, -0.5f, 0.0f).method_39415(ColorUtil.replAlpha(color2, alpha)).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        buffer.method_22918((Matrix4fc)matrix, 0.5f, 0.5f, 0.0f).method_39415(ColorUtil.replAlpha(color3, alpha)).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        buffer.method_22918((Matrix4fc)matrix, -0.5f, 0.5f, 0.0f).method_39415(ColorUtil.replAlpha(color4, alpha)).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
    }

    private static void drawCubeFillTESP(class_4588 buf, Matrix4f m, float s, int color) {
        buf.method_22918((Matrix4fc)m, -s, s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, -s).method_39415(color);
    }

    private static void drawCubeOutlineTESP(class_4588 buf, Matrix4f m, float s, int color) {
        buf.method_22918((Matrix4fc)m, -s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, -s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, s, s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, -s, s).method_39415(color);
        buf.method_22918((Matrix4fc)m, -s, s, s).method_39415(color);
    }
}

