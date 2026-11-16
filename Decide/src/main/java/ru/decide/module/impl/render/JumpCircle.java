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
 *  net.minecraft.class_156
 *  net.minecraft.class_1921
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_3959
 *  net.minecraft.class_3959$class_242
 *  net.minecraft.class_3959$class_3960
 *  net.minecraft.class_3965
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_7833
 *  net.minecraft.class_9799
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package ru.decide.module.impl.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1297;
import net.minecraft.class_156;
import net.minecraft.class_1921;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
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
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.jumpcircle.CircleShapes;
import ru.decide.module.impl.render.jumpcircle.JumpCircleRefraction;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easing;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.other.Instance;
import ru.decide.utils.render.JumpSoulsPipeline;
import ru.decide.utils.render.NoopVertexConsumer;

@ModuleInfo(name="Jump Circle", desc="\u041a\u0440\u0443\u0436\u043e\u0447\u0435\u043a \u043f\u043e\u0434 \u0442\u043e\u0431\u043e\u0439 \u043f\u0440\u0438 \u043f\u0440\u0438\u0436\u043a\u0435", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class JumpCircle
extends Module
implements ModulePreview {
    private final List<Circle> circles = new ArrayList<Circle>();
    private final List<JumpCircleRefraction.Ring> refractionRings = new ArrayList<JumpCircleRefraction.Ring>();
    public ButtonSetting previewButton = PreviewSettings.button(this);
    public ModeSetting circleMode = new ModeSetting((Module)this, "\u041a\u0440\u0443\u0433", "Gradient", "Evola", "Ring", "\u0420\u0430\u0437\u043b\u043e\u043c", "\u041f\u0443\u043b\u044c\u0441", "\u0421\u043f\u0438\u0440\u0430\u043b\u044c", "\u041d\u043e\u0432\u0430", "\u041a\u0440\u0443\u0433", "\u041f\u0440\u0435\u043b\u043e\u043c\u043b\u0435\u043d\u0438\u0435");
    public ModeSetting animMode = new ModeSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f", "Bounce", "Smooth", "Elastic", "Spring", "Expo");
    public SliderSetting size_c = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440", 1.0f, 0.2f, 3.0f, 0.1f);
    public BooleanSetting glow = new BooleanSetting((Module)this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", true).setVisible(() -> this.circleMode.is("Gradient"));
    public SliderSetting shapesRadius = new SliderSetting(this, "\u0420\u0430\u0434\u0438\u0443\u0441", 0.5f, 0.3f, 1.0f, 0.05f).setVisible(this::isShapesMode);
    public SliderSetting shapesTime = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f", 1000.0f, 200.0f, 2000.0f, 100.0f).setVisible(this::isShapesMode);
    public SliderSetting shapesAnimSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438", 500.0f, 100.0f, 1000.0f, 50.0f).setVisible(this::isShapesMode);
    public MultiBooleanSetting shapesAnimations = new MultiBooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u0438", new BooleanSetting("\u0420\u0430\u0437\u043c\u0435\u0440", true), new BooleanSetting("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", true), new BooleanSetting("\u0417\u0430\u0442\u0443\u0445\u0430\u043d\u0438\u0435", true)).setVisible(this::isShapesMode);
    public SliderSetting shapesAnimValue = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438", 1.0f, 0.05f, 1.0f, 0.01f).setVisible(() -> this.isShapesMode() && this.shapesAnimations.getValue("\u0420\u0430\u0437\u043c\u0435\u0440"));
    public BooleanSetting shapesCustomColor = new BooleanSetting((Module)this, "\u041a\u0430\u0441\u0442\u043e\u043c \u0446\u0432\u0435\u0442", false).setVisible(this::isShapesMode);
    public ColorSetting shapesColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", 10519295).setVisible(() -> this.isShapesMode() && this.shapesCustomColor.getValue() != false);
    public SliderSetting ringTime = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438", 1500.0f, 500.0f, 5000.0f, 50.0f).setVisible(() -> this.circleMode.is("Ring"));
    public SliderSetting ringThickness = new SliderSetting(this, "\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u043a\u043e\u043b\u044c\u0446\u0430", 0.5f, 0.3f, 0.8f, 0.05f).setVisible(() -> this.circleMode.is("Ring"));
    public ModeSetting ringEasing = new ModeSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u043a\u043e\u043b\u044c\u0446\u0430", "\u041e\u0431\u044b\u0447\u043d\u0430\u044f", "\u042d\u043b\u0430\u0441\u0442\u0438\u0447\u043d\u0430\u044f", "\u041d\u0430\u0437\u0430\u0434").setVisible(() -> this.circleMode.is("Ring"));
    public ModeSetting ringColorMode = new ModeSetting((Module)this, "\u0426\u0432\u0435\u0442 \u043a\u043e\u043b\u044c\u0446\u0430", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u041a\u043b\u0438\u0435\u043d\u0442", "\u0421\u0432\u043e\u0439").setVisible(() -> this.circleMode.is("Ring") || this.circleMode.is("\u0420\u0430\u0437\u043b\u043e\u043c"));
    public ColorSetting ringColor = new ColorSetting((Module)this, "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u043a\u043e\u043b\u044c\u0446\u0430", -1).setVisible(() -> (this.circleMode.is("Ring") || this.circleMode.is("\u0420\u0430\u0437\u043b\u043e\u043c")) && this.ringColorMode.is("\u0421\u0432\u043e\u0439"));
    public BooleanSetting ringSecondColor = new BooleanSetting((Module)this, "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", false).setVisible(() -> (this.circleMode.is("Ring") || this.circleMode.is("\u0420\u0430\u0437\u043b\u043e\u043c")) && this.ringColorMode.is("\u0421\u0432\u043e\u0439"));
    public ColorSetting ringColor2 = new ColorSetting((Module)this, "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442 \u043a\u043e\u043b\u044c\u0446\u0430", -15724528).setVisible(() -> (this.circleMode.is("Ring") || this.circleMode.is("\u0420\u0430\u0437\u043b\u043e\u043c")) && this.ringColorMode.is("\u0421\u0432\u043e\u0439") && this.ringSecondColor.getValue() != false);
    public SliderSetting fractureTime = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u0440\u0430\u0437\u043b\u043e\u043c\u0430", 1950.0f, 500.0f, 5000.0f, 50.0f).setVisible(this::isFracture);
    public SliderSetting fractureCount = new SliderSetting(this, "\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0442\u0440\u0435\u0449\u0438\u043d", 10.0f, 4.0f, 16.0f, 1.0f).setVisible(this::isFracture);
    public SliderSetting fractureChaos = new SliderSetting(this, "\u0425\u0430\u043e\u0441", 1.0f, 0.3f, 2.0f, 0.1f).setVisible(this::isFracture);
    public SliderSetting fractureWidth = new SliderSetting(this, "\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u0442\u0435\u043b\u0430", 1.0f, 0.5f, 2.0f, 0.1f).setVisible(this::isFracture);
    public BooleanSetting fractureGlowEnabled = new BooleanSetting((Module)this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", true).setVisible(this::isFracture);
    public SliderSetting fractureGlow = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 1.0f, 0.1f, 2.5f, 0.1f).setVisible(() -> this.isFracture() && this.fractureGlowEnabled.getValue() != false);
    public SliderSetting fractureGlowHeight = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 \u0441\u0442\u043e\u043b\u0431\u0430", 30.0f, 10.0f, 100.0f, 1.0f).setVisible(() -> this.isFracture() && this.fractureGlowEnabled.getValue() != false);
    public SliderSetting fractureCore = new SliderSetting(this, "\u0411\u0435\u043b\u043e\u0435 \u044f\u0434\u0440\u043e", 100.0f, 0.0f, 100.0f, 5.0f).setVisible(this::isFracture);
    public BooleanSetting fractureSparks = new BooleanSetting((Module)this, "\u0418\u0441\u043a\u0440\u044b", true).setVisible(this::isFracture);
    private static final float[] NO_WALLS = new float[32];
    public SliderSetting refractionRadius = new SliderSetting(this, "\u0420\u0430\u0434\u0438\u0443\u0441 \u043a\u0440\u0443\u0433\u0430", 1.5f, 0.5f, 4.0f, 0.1f).setVisible(this::isRefraction);
    public SliderSetting refractionGlow = new SliderSetting(this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u043a\u0440\u0443\u0433\u0430", 0.35f, 0.05f, 1.5f, 0.05f).setVisible(this::isRefraction);
    public BooleanSetting refractionWave = new BooleanSetting((Module)this, "\u0412\u043e\u043b\u043d\u0430 \u0440\u0430\u0434\u0438\u0443\u0441\u0430", true).setVisible(this::isRefraction);
    public SliderSetting refractionLife = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u043a\u0440\u0443\u0433\u0430", 600.0f, 300.0f, 3000.0f, 50.0f).setVisible(this::isRefraction);
    public SliderSetting refractionStrength = new SliderSetting(this, "\u0418\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u0435", 0.35f, 0.0f, 1.0f, 0.05f).setVisible(this::isRefraction);
    public ModeSetting refractionStyle = new ModeSetting((Module)this, "\u042d\u0444\u0444\u0435\u043a\u0442 \u0438\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u044f", "\u0412\u043e\u043b\u043d\u0430", "\u0412\u043e\u043b\u043d\u0430", "\u0412\u043e\u0434\u0430").setVisible(this::isRefraction);
    public BooleanSetting refractionCustomColor = new BooleanSetting((Module)this, "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u043a\u0440\u0443\u0433\u0430", false).setVisible(this::isRefraction);
    public ColorSetting refractionColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u043a\u0440\u0443\u0433\u0430", -1).setVisible(() -> this.isRefraction() && this.refractionCustomColor.getValue() != false);
    private final PreviewSettings previewSettings = PreviewSettings.of(this, 3.5f, 0.0f, 2.0f);
    class_9799 allocator = new class_9799(262144);
    private static final int QUAD_BUFFER_SIZE_BYTES = 1024;
    private static final RenderPipeline TEXTURED_QUADS_PIPELINE;
    private static final RenderPipeline RING_QUADS_PIPELINE;
    private static final class_1921 RING_QUADS;
    private static final RenderPipeline SHAPES_FILL_PIPELINE;
    private static final class_1921 SHAPES_FILL;
    private static final RenderPipeline SHAPES_LINE_PIPELINE;
    private static final class_1921 SHAPES_LINES;
    private static final class_2960 GRADIENT_TEXTURE;
    private static final class_2960 EVOLA_TEXTURE;
    private static final String[] DESTRA_CIRCLE_MODES;
    private static final class_2960[] DESTRA_CIRCLE_TEXTURES;
    public ModeSetting destraCircleMode = new ModeSetting((Module)this, "\u0422\u0435\u043a\u0441\u0442\u0443\u0440\u0430", DESTRA_CIRCLE_MODES).setVisible(this::isDestraCircle);
    public SliderSetting destraCircleSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u043a\u0440\u0443\u0433\u0430", 3.0f, 0.5f, 3.0f, 0.1f).setVisible(this::isDestraCircle);
    public BooleanSetting destraCircleRotate = new BooleanSetting((Module)this, "\u0412\u0440\u0430\u0449\u0430\u0442\u044c", true).setVisible(this::isDestraCircle);
    public SliderSetting destraCircleRotSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0440\u043e\u0442\u0430\u0446\u0438\u0438", 1.0f, 0.5f, 3.0f, 0.1f).setVisible(() -> this.isDestraCircle() && this.destraCircleRotate.getValue() != false);
    public SliderSetting destraCircleSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 6.0f, 1.0f, 10.0f, 0.1f).setVisible(this::isDestraCircle);
    private static final RenderPipeline DESTRA_CIRCLE_PIPELINE;
    private final CircleShapes circleShapes = new CircleShapes();
    private static final int SHAPES_MAX_CIRCLES = 32;
    private final Matrix4f[] shapesMatrices = new Matrix4f[32];
    private final float[] shapesRadii = new float[32];
    private final float[] shapesAlphas = new float[32];
    private final long[] shapesAges = new long[32];
    private static final float RING_HOVER = 0.05f;
    public static final Function<class_2960, class_1921> ROMB_ESP;
    private static final int SOULS_MAX_CIRCLES = 16;
    private static final double WOBBLE_PERIOD = 628.3185307179587;
    private final Matrix4f fractureInvViewProj;
    private final float[] fractureUniform;

    public JumpCircle() {
        for (int i = 0; i < 32; ++i) {
            this.shapesMatrices[i] = new Matrix4f();
        }
        this.fractureInvViewProj = new Matrix4f();
        this.fractureUniform = new float[412];
    }

    public static JumpCircle getInstance() {
        return Instance.get(JumpCircle.class);
    }

    private boolean isShapesMode() {
        return this.circleMode.is("\u041f\u0443\u043b\u044c\u0441") || this.circleMode.is("\u0421\u043f\u0438\u0440\u0430\u043b\u044c") || this.circleMode.is("\u041d\u043e\u0432\u0430");
    }

    private boolean isFracture() {
        return this.circleMode.is("\u0420\u0430\u0437\u043b\u043e\u043c");
    }

    public boolean isRefraction() {
        return this.circleMode.is("\u041f\u0440\u0435\u043b\u043e\u043c\u043b\u0435\u043d\u0438\u0435");
    }

    private void addRefractionRing() {
        if (JumpCircle.mc.field_1724 == null || JumpCircle.mc.field_1687 == null) {
            return;
        }
        if (this.refractionRings.size() >= 16) {
            this.refractionRings.remove(0);
        }
        double x = JumpCircle.mc.field_1724.method_23317();
        double y = JumpCircle.mc.field_1724.method_23318();
        double z = JumpCircle.mc.field_1724.method_23321();
        float reach = this.refractionRadius.getValue().floatValue() + this.refractionGlow.getValue().floatValue() * 2.0f + 1.0f;
        class_243 origin = new class_243(x, y + 0.6, z);
        float[] walls = new float[32];
        for (int i = 0; i < walls.length; ++i) {
            double angle = (double)i * Math.PI / 16.0;
            class_243 end = new class_243(origin.field_1352 + Math.cos(angle) * (double)reach, origin.field_1351, origin.field_1350 + Math.sin(angle) * (double)reach);
            class_3965 hit = JumpCircle.mc.field_1687.method_17742(new class_3959(origin, end, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)JumpCircle.mc.field_1724));
            walls[i] = hit.method_17783() == class_239.class_240.field_1333 ? 999.0f : (float)hit.method_17784().method_1022(origin);
        }
        this.refractionRings.add(new JumpCircleRefraction.Ring(x, y, z, System.currentTimeMillis(), walls));
    }

    @EventHandler
    public void onRefractionTick(EventTick e) {
        if (this.refractionRings.isEmpty()) {
            return;
        }
        float life = Math.max(1.0f, this.refractionLife.getValue().floatValue());
        long now = System.currentTimeMillis();
        this.refractionRings.removeIf(ring -> (float)(now - ring.bornMs()) > life);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.circles.clear();
        this.refractionRings.clear();
        JumpCircleRefraction.clear();
    }

    public void renderRefraction(class_276 target, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        if (!this.isEnabled() || !this.isRefraction()) {
            JumpCircleRefraction.clear();
            return;
        }
        if (this.refractionRings.isEmpty() || JumpCircle.mc.field_1724 == null || JumpCircle.mc.field_1687 == null) {
            return;
        }
        int color = this.refractionCustomColor.getValue() != false ? this.refractionColor.getValue() : ColorUtil.getClientColor1(1);
        JumpCircleRefraction.render(target, positionMatrix, projectionMatrix, this.refractionRings, new JumpCircleRefraction.Params(this.refractionRadius.getValue().floatValue(), this.refractionGlow.getValue().floatValue(), this.refractionStrength.getValue().floatValue(), this.refractionWave.getValue(), this.refractionStyle.is("\u0412\u043e\u0434\u0430"), color, this.refractionLife.getValue().floatValue()), System.currentTimeMillis());
    }

    private Easing getExpandEasing() {
        return switch (this.animMode.getValue()) {
            case "Smooth" -> Easings.SINE_OUT;
            case "Elastic" -> Easings.ELASTIC_OUT;
            case "Spring" -> Easings.BACK_OUT;
            case "Expo" -> Easings.EXPO_OUT;
            default -> Easings.BACK_OUT;
        };
    }

    private Easing getShrinkEasing() {
        return switch (this.animMode.getValue()) {
            case "Smooth" -> Easings.SINE_IN;
            case "Elastic" -> Easings.ELASTIC_IN;
            case "Spring" -> Easings.BACK_IN;
            case "Expo" -> Easings.EXPO_IN;
            default -> Easings.BACK_IN;
        };
    }

    private class_2960 getTexture() {
        return this.circleMode.is("Evola") ? EVOLA_TEXTURE : GRADIENT_TEXTURE;
    }

    @EventHandler
    public void onJump(EventJump e) {
        if (JumpCircle.mc.field_1724 == null) {
            return;
        }
        if (this.isRefraction()) {
            this.addRefractionRing();
        } else {
            this.circles.add(new Circle(this, JumpCircle.mc.field_1724.method_73189().method_1031(0.0, 0.05, 0.0)));
        }
    }

    private boolean isDestraCircle() {
        return this.circleMode.is("\u041a\u0440\u0443\u0433");
    }

    private long destraCircleLifeMs() {
        float speed = this.destraCircleSpeed.getValue().floatValue();
        return (long)(900.0f + (10.0f - speed + 1.0f) * 90.0f);
    }

    private void renderDestraCircle(class_4587 pose, class_4597.class_4598 immediate) {
        long now = System.currentTimeMillis();
        long life = this.destraCircleLifeMs();
        class_2960 texture = DESTRA_CIRCLE_TEXTURES[0];
        for (int i = 0; i < DESTRA_CIRCLE_MODES.length; ++i) {
            if (!this.destraCircleMode.getValue().equals(DESTRA_CIRCLE_MODES[i])) continue;
            texture = DESTRA_CIRCLE_TEXTURES[i];
            break;
        }
        class_243 cam = JumpCircle.mc.field_1773.method_19418().method_71156();
        int circleColor = this.shapeColor();
        int cr = circleColor >> 16 & 0xFF;
        int cg = circleColor >> 8 & 0xFF;
        int cb = circleColor & 0xFF;
        this.circles.removeIf(c -> now - c.time >= life);
        if (this.circles.isEmpty()) {
            return;
        }
        class_4588 buffer = immediate.method_73477(this.destraCircleLayer(texture));
        Matrix4f base = pose.method_23760().method_23761();
        for (Circle c2 : this.circles) {
            float t = Math.min(1.0f, (float)(now - c2.time) / (float)life);
            float ease = 1.0f - (1.0f - t) * (1.0f - t);
            float size = this.destraCircleSize.getValue().floatValue() * ease;
            float alpha = 1.0f - t;
            if (size <= 0.01f || alpha <= 0.01f) continue;
            float h = size / 2.0f;
            float rotY = this.destraCircleRotate.getValue() != false ? (float)(now - c2.time) / 1000.0f * (360.0f * this.destraCircleRotSpeed.getValue().floatValue()) : 0.0f;
            pose.method_22903();
            pose.method_22904(c2.vector3d.field_1352 - cam.field_1352, c2.vector3d.field_1351 - cam.field_1351, c2.vector3d.field_1350 - cam.field_1350);
            pose.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(rotY));
            pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(90.0f));
            Matrix4f m = pose.method_23760().method_23761();
            float a = alpha;
            buffer.method_22918((Matrix4fc)m, -h, -h, 0.0f).method_22913(0.0f, 0.0f).method_22915((float)cr, (float)cg, (float)cb, a);
            buffer.method_22918((Matrix4fc)m, h, -h, 0.0f).method_22913(1.0f, 0.0f).method_22915((float)cr, (float)cg, (float)cb, a);
            buffer.method_22918((Matrix4fc)m, h, h, 0.0f).method_22913(1.0f, 1.0f).method_22915((float)cr, (float)cg, (float)cb, a);
            buffer.method_22918((Matrix4fc)m, -h, h, 0.0f).method_22913(0.0f, 1.0f).method_22915((float)cr, (float)cg, (float)cb, a);
            pose.method_22909();
        }
    }

    private class_1921 destraCircleLayer(class_2960 texture) {
        class_12247 setup = class_12247.method_75927((RenderPipeline)DESTRA_CIRCLE_PIPELINE).method_75934("Sampler0", texture).method_75937().method_75929(4096).method_75938();
        return class_1921.method_75940((String)"destra_jump_circle", (class_12247)setup);
    }

    @Override
    public PreviewSettings previewSettings() {
        return this.previewSettings;
    }

    @Override
    public void previewSpawn(PreviewContext ctx) {
        if (this.isRefraction()) {
            class_243 anchor = ctx.anchor();
            this.refractionRings.add(new JumpCircleRefraction.Ring(anchor.field_1352, anchor.field_1351, anchor.field_1350, System.currentTimeMillis(), NO_WALLS));
        } else {
            this.circles.add(new Circle(this, ctx.anchor().method_1031(0.0, 0.05, 0.0)));
        }
    }

    @Override
    public void previewStop() {
        this.circles.clear();
        this.refractionRings.clear();
    }

    @EventHandler
    public void onRender(EventRender3D e) {
        if (this.isRefraction()) {
            return;
        }
        if (this.circles.isEmpty()) {
            return;
        }
        long maxLife = this.isShapesMode() ? this.shapesLifetime() : (this.circleMode.is("Ring") ? this.ringTime.getValue().longValue() : (this.isFracture() ? this.fractureTime.getValue().longValue() : 2500L));
        this.circles.removeIf(c -> System.currentTimeMillis() - c.time > maxLife);
        if (this.circles.isEmpty()) {
            return;
        }
        if (this.isFracture()) {
            return;
        }
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        if (this.isShapesMode()) {
            this.renderShapes(e, immediate);
            immediate.method_22993();
            return;
        }
        class_4587 pose = e.getMatrixStack();
        if (this.isDestraCircle()) {
            this.renderDestraCircle(pose, immediate);
            immediate.method_22993();
            return;
        }
        if (this.circleMode.is("Ring")) {
            this.renderRings(e, immediate);
            immediate.method_22993();
            return;
        }
        int alpha = 255;
        for (Circle c2 : this.circles) {
            if (System.currentTimeMillis() - c2.time > 800L && !c2.isBack) {
                c2.animation.run(0.0, 0.8, this.getShrinkEasing());
                c2.animation2.run(0.0, 0.8, this.getShrinkEasing());
                c2.isBack = true;
            }
            c2.animation.update();
            c2.animation2.update();
            float rad = (float)c2.animation2.getValue();
            double posX = c2.vector3d.field_1352 - JumpCircle.mc.field_1773.method_19418().method_71156().field_1352;
            double posY = c2.vector3d.field_1351 - JumpCircle.mc.field_1773.method_19418().method_71156().field_1351;
            double posZ = c2.vector3d.field_1350 - JumpCircle.mc.field_1773.method_19418().method_71156().field_1350;
            float size = this.size_c.getValue().floatValue() * rad;
            pose.method_22903();
            pose.method_22904(posX, posY, posZ);
            pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(90.0f));
            class_4587.class_4665 entry = pose.method_23760();
            Matrix4f matrix4f = entry.method_23761();
            Matrix3f normalMatrix = entry.method_23762();
            class_4588 buffer = immediate.method_73477(ROMB_ESP.apply(this.getTexture()));
            this.drawTexturedQuad(buffer, matrix4f, normalMatrix, -size / 2.0f, -size / 2.0f, size, size, new int[]{ColorUtil.fade(0), ColorUtil.fade(90), ColorUtil.fade(180), ColorUtil.fade(360)}, (int)((float)alpha * c2.animation.get()));
            pose.method_22909();
            if (!this.glow.getValue().booleanValue() || !this.circleMode.is("Gradient")) continue;
            pose.method_22903();
            pose.method_22904(posX, posY, posZ);
            this.drawGlowLayers(immediate, pose, size, c2.animation.get());
            pose.method_22909();
        }
        immediate.method_22993();
    }

    private long shapesLifetime() {
        long animMs = Math.max(1L, this.shapesAnimSpeed.getValue().longValue());
        return animMs * 2L + Math.max(0L, this.shapesTime.getValue().longValue());
    }

    private int shapeColor() {
        return this.shapesThemeColor();
    }

    private int shapesThemeColor() {
        return (this.shapesCustomColor.getValue() != false ? this.shapesColor.getValue() : ColorUtil.getClientColor1(1)) & 0xFFFFFF;
    }

    private void renderShapes(EventRender3D e, class_4597.class_4598 immediate) {
        class_4587 pose = e.getMatrixStack();
        class_243 cam = JumpCircle.mc.field_1773.method_19418().method_71156();
        boolean animAlpha = this.shapesAnimations.getValue("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c");
        boolean animSize = this.shapesAnimations.getValue("\u0420\u0430\u0437\u043c\u0435\u0440");
        boolean fadeOut = this.shapesAnimations.getValue("\u0417\u0430\u0442\u0443\u0445\u0430\u043d\u0438\u0435");
        float animVal = this.shapesAnimValue.getValue().floatValue();
        float rad = this.shapesRadius.getValue().floatValue();
        long holdMs = Math.max(0L, this.shapesTime.getValue().longValue());
        long animMs = Math.max(1L, this.shapesAnimSpeed.getValue().longValue());
        int theme = this.shapesThemeColor();
        long now = System.currentTimeMillis();
        int count = 0;
        for (Circle c : this.circles) {
            long age = Math.max(0L, now - c.time);
            float appear = age >= animMs ? 1.0f : CircleShapes.easeOutCubic((float)age / (float)animMs);
            float fade = fadeOut ? JumpCircle.fadeProgress(age, holdMs, animMs) : 1.0f;
            float alpha = (animAlpha ? appear : 1.0f) * fade;
            float sizeF = (animSize ? 1.0f - animVal + appear * animVal : 1.0f) * (0.65f + appear * 0.35f);
            if (alpha < 0.01f || count >= 32) continue;
            pose.method_22903();
            pose.method_22904(c.vector3d.field_1352 - cam.field_1352, c.vector3d.field_1351 - cam.field_1351, c.vector3d.field_1350 - cam.field_1350);
            pose.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(90.0f));
            this.shapesMatrices[count].set((Matrix4fc)pose.method_23760().method_23761());
            this.shapesRadii[count] = sizeF * rad;
            this.shapesAlphas[count] = alpha;
            this.shapesAges[count] = age;
            ++count;
            pose.method_22909();
        }
        if (count == 0) {
            return;
        }
        this.drawShapesPass(immediate.method_73477(SHAPES_FILL), NoopVertexConsumer.INSTANCE, count, theme);
        this.drawShapesPass(NoopVertexConsumer.INSTANCE, immediate.method_73477(SHAPES_LINES), count, theme);
    }

    private void drawShapesPass(class_4588 fills, class_4588 lines, int count, int theme) {
        for (int i = 0; i < count; ++i) {
            Matrix4f mat = this.shapesMatrices[i];
            float r = this.shapesRadii[i];
            float alpha = this.shapesAlphas[i];
            long age = this.shapesAges[i];
            if (this.circleMode.is("\u041f\u0443\u043b\u044c\u0441")) {
                this.circleShapes.drawPulse(fills, lines, mat, r, alpha, age, theme);
                continue;
            }
            if (this.circleMode.is("\u0421\u043f\u0438\u0440\u0430\u043b\u044c")) {
                this.circleShapes.drawSpiral(fills, lines, mat, r, alpha, age, theme);
                continue;
            }
            this.circleShapes.drawNova(fills, lines, mat, r, alpha, age, theme);
        }
    }

    private static float fadeProgress(long age, long holdMs, long animMs) {
        if (age < animMs + holdMs) {
            return 1.0f;
        }
        long fadeAge = age - animMs - holdMs;
        return fadeAge >= animMs ? 0.0f : 1.0f - CircleShapes.easeInCubic((float)fadeAge / (float)animMs);
    }

    private void renderRings(EventRender3D e, class_4597.class_4598 immediate) {
        if (this.circles.isEmpty()) {
            return;
        }
        class_4587 pose = e.getMatrixStack();
        class_243 cam = JumpCircle.mc.field_1773.method_19418().method_71156();
        class_4588 buf = immediate.method_73477(RING_QUADS);
        long now = System.currentTimeMillis();
        float maxLife = Math.max(1.0f, this.ringTime.getValue().floatValue());
        float maxRadius = Math.max(0.05f, this.size_c.getValue().floatValue());
        float thickness = this.ringThickness.getValue().floatValue();
        int segments = 64;
        for (int i = this.circles.size() - 1; i >= 0; --i) {
            Circle c = this.circles.get(i);
            float delta = (float)(now - c.time) / maxLife;
            if (delta > 1.0f) {
                this.circles.remove(i);
                continue;
            }
            float eased = this.ringEase(delta);
            float currentRadius = Math.max(0.05f, eased * maxRadius);
            float innerRadius = Math.max(0.0f, currentRadius - thickness * (1.0f - delta * 0.5f));
            float alphaFactor = (1.0f - delta) * (delta < 0.1f ? delta / 0.1f : 1.0f);
            float cx = (float)(c.vector3d.field_1352 - cam.field_1352);
            float cy = (float)(c.vector3d.field_1351 - cam.field_1351) + 0.05f;
            float cz = (float)(c.vector3d.field_1350 - cam.field_1350);
            Matrix4f m = pose.method_23760().method_23761();
            for (int s = 0; s < segments; ++s) {
                float a1 = (float)s / (float)segments;
                float a2 = (float)(s + 1) / (float)segments;
                float rad1 = a1 * ((float)Math.PI * 2);
                float rad2 = a2 * ((float)Math.PI * 2);
                float cos1 = class_3532.method_15362((double)rad1);
                float sin1 = class_3532.method_15374((double)rad1);
                float cos2 = class_3532.method_15362((double)rad2);
                float sin2 = class_3532.method_15374((double)rad2);
                int color1 = this.ringColor((int)(a1 * 360.0f), alphaFactor);
                int color2 = this.ringColor((int)(a2 * 360.0f), alphaFactor);
                int innerColor1 = ColorUtil.multAlpha(color1, 0.15f);
                int innerColor2 = ColorUtil.multAlpha(color2, 0.15f);
                float x1 = cx + cos1 * innerRadius;
                float z1 = cz + sin1 * innerRadius;
                float x2 = cx + cos1 * currentRadius;
                float z2 = cz + sin1 * currentRadius;
                float x3 = cx + cos2 * currentRadius;
                float z3 = cz + sin2 * currentRadius;
                float x4 = cx + cos2 * innerRadius;
                float z4 = cz + sin2 * innerRadius;
                buf.method_22918((Matrix4fc)m, x1, cy, z1).method_39415(innerColor1);
                buf.method_22918((Matrix4fc)m, x2, cy, z2).method_39415(color1);
                buf.method_22918((Matrix4fc)m, x3, cy, z3).method_39415(color2);
                buf.method_22918((Matrix4fc)m, x4, cy, z4).method_39415(innerColor2);
                buf.method_22918((Matrix4fc)m, x4, cy, z4).method_39415(innerColor2);
                buf.method_22918((Matrix4fc)m, x3, cy, z3).method_39415(color2);
                buf.method_22918((Matrix4fc)m, x2, cy, z2).method_39415(color1);
                buf.method_22918((Matrix4fc)m, x1, cy, z1).method_39415(innerColor1);
            }
        }
    }

    private float ringEase(float delta) {
        float t = class_3532.method_15363((float)delta, (float)0.0f, (float)1.0f);
        return switch (this.ringEasing.getValue()) {
            case "\u042d\u043b\u0430\u0441\u0442\u0438\u0447\u043d\u0430\u044f" -> JumpCircle.elasticOut(t);
            case "\u041d\u0430\u0437\u0430\u0434" -> JumpCircle.backOut(t);
            default -> t;
        };
    }

    private static float elasticOut(float t) {
        return t != 0.0f && t != 1.0f ? (float)(Math.pow(2.0, -10.0 * (double)t) * Math.sin(((double)t * 10.0 - 0.75) * 2.0943951023931953) + 1.0) : t;
    }

    private static float backOut(float t) {
        return (float)(1.0 + 2.70158 * Math.pow((double)t - 1.0, 3.0) + 1.70158 * Math.pow((double)t - 1.0, 2.0));
    }

    private int ringColor(int angle, float alpha) {
        if (this.ringColorMode.is("\u0420\u0430\u0434\u0443\u0433\u0430")) {
            return ColorUtil.rainbow(8, angle, 1.0f, 1.0f, alpha);
        }
        if (this.ringColorMode.is("\u041a\u043b\u0438\u0435\u043d\u0442")) {
            return ColorUtil.multAlpha(ColorUtil.client(), alpha);
        }
        return ColorUtil.replAlpha((int)this.ringColor.getValue(), (int)(class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f) * 255.0f));
    }

    private void drawGlowLayers(class_4597.class_4598 immediate, class_4587 pose, float radius, float alpha) {
        class_4588 buffer = immediate.method_73477(ROMB_ESP.apply(this.getTexture()));
        int layers = 20;
        float maxHeight = radius * 0.15f;
        float expand = radius * 0.25f;
        Vector3f normal = new Vector3f(0.0f, 1.0f, 0.0f);
        pose.method_23760().method_23762().transform(normal);
        normal.normalize();
        for (int i = 0; i < layers; ++i) {
            float progress = (float)i / (float)layers;
            float yOff = maxHeight * progress;
            float layerAlpha = alpha * (1.0f - progress) * 0.15f;
            if (layerAlpha <= 0.004f) continue;
            float r = radius + expand * progress;
            float half = r / 2.0f;
            int a = (int)(layerAlpha * 255.0f);
            Matrix4f matrix = pose.method_23760().method_23761();
            int c0 = ColorUtil.fade(0);
            int c90 = ColorUtil.fade(90);
            int c180 = ColorUtil.fade(180);
            int c270 = ColorUtil.fade(270);
            buffer.method_22918((Matrix4fc)matrix, -half, yOff, -half).method_1336(c0 >> 16 & 0xFF, c0 >> 8 & 0xFF, c0 & 0xFF, a).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(normal.x, normal.y, normal.z);
            buffer.method_22918((Matrix4fc)matrix, -half, yOff, half).method_1336(c90 >> 16 & 0xFF, c90 >> 8 & 0xFF, c90 & 0xFF, a).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(normal.x, normal.y, normal.z);
            buffer.method_22918((Matrix4fc)matrix, half, yOff, half).method_1336(c180 >> 16 & 0xFF, c180 >> 8 & 0xFF, c180 & 0xFF, a).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(normal.x, normal.y, normal.z);
            buffer.method_22918((Matrix4fc)matrix, half, yOff, -half).method_1336(c270 >> 16 & 0xFF, c270 >> 8 & 0xFF, c270 & 0xFF, a).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(normal.x, normal.y, normal.z);
        }
    }

    private void drawTexturedQuad(class_4588 buffer, Matrix4f matrix, Matrix3f normalMatrix, float x, float y, float width, float height, int[] ints, int alpha) {
        Vector3f normal = new Vector3f(0.0f, 0.0f, 1.0f);
        normalMatrix.transform(normal);
        normal.normalize();
        float x1 = x;
        float y1 = y;
        float x2 = x + width;
        float y2 = y + height;
        buffer.method_22918((Matrix4fc)matrix, x1, y1, 0.0f).method_39415(ColorUtil.replAlpha(ints[0], alpha)).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(normal.x, normal.y, normal.z);
        buffer.method_22918((Matrix4fc)matrix, x2, y1, 0.0f).method_39415(ColorUtil.replAlpha(ints[1], alpha)).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(normal.x, normal.y, normal.z);
        buffer.method_22918((Matrix4fc)matrix, x2, y2, 0.0f).method_39415(ColorUtil.replAlpha(ints[2], alpha)).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(normal.x, normal.y, normal.z);
        buffer.method_22918((Matrix4fc)matrix, x1, y2, 0.0f).method_39415(ColorUtil.replAlpha(ints[3], alpha)).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(normal.x, normal.y, normal.z);
    }

    public void renderSouls(class_276 target, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        if (!this.isEnabled() || !this.isFracture()) {
            return;
        }
        if (target == null || positionMatrix == null || projectionMatrix == null) {
            return;
        }
        if (this.circles.isEmpty() || JumpCircle.mc.field_1773 == null || JumpCircle.mc.field_1773.method_19418() == null) {
            return;
        }
        if (target.field_1482 <= 0 || target.field_1481 <= 0) {
            return;
        }
        class_243 cam = JumpCircle.mc.field_1773.method_19418().method_71156();
        float maxLife = Math.max(1.0f, this.fractureTime.getValue().floatValue());
        float maxRadius = Math.max(0.05f, this.size_c.getValue().floatValue() * 0.75f);
        this.fractureInvViewProj.set((Matrix4fc)projectionMatrix).mul((Matrix4fc)positionMatrix).invert();
        float[] data = this.fractureUniform;
        int count = 0;
        float gradientIndex = 0.0f;
        boolean staticPalette = this.isStaticPalette();
        int staticColor = staticPalette ? this.fractureColor(0, 1.0f) : 0;
        long now = System.currentTimeMillis();
        for (int i = this.circles.size() - 1; i >= 0; --i) {
            Circle c = this.circles.get(i);
            float progress = (float)(now - c.time) / maxLife;
            if (progress < 0.0f || progress >= 1.0f) continue;
            if (count >= 16) break;
            float fadeInRaw = class_3532.method_15363((float)(progress / 0.12f), (float)0.0f, (float)1.0f);
            float fadeIn = fadeInRaw * fadeInRaw * (3.0f - 2.0f * fadeInRaw);
            float fadeOut = class_3532.method_15363((float)((1.0f - progress) / 0.08f), (float)0.0f, (float)1.0f);
            float env = fadeIn * fadeOut;
            int base = 28 + count * 8;
            data[base] = (float)(c.vector3d.field_1352 - cam.field_1352);
            data[base + 1] = (float)(c.vector3d.field_1351 - cam.field_1351);
            data[base + 2] = (float)(c.vector3d.field_1350 - cam.field_1350);
            data[base + 3] = progress;
            data[base + 4] = maxRadius;
            data[base + 5] = c.seed;
            data[base + 6] = env;
            data[base + 7] = maxRadius * this.fractureGlowHeight.getValue().floatValue() / 100.0f * 1.2f;
            int cbase = 156 + count * 16;
            if (staticPalette) {
                JumpCircle.putColor(data, cbase, staticColor);
                JumpCircle.putColor(data, cbase + 4, staticColor);
                JumpCircle.putColor(data, cbase + 8, staticColor);
                JumpCircle.putColor(data, cbase + 12, staticColor);
            } else {
                int idx = (int)gradientIndex;
                JumpCircle.putColor(data, cbase, this.fractureColor(idx, 1.0f));
                JumpCircle.putColor(data, cbase + 4, this.fractureColor(90 + idx, 1.0f));
                JumpCircle.putColor(data, cbase + 8, this.fractureColor(180 + idx, 1.0f));
                JumpCircle.putColor(data, cbase + 12, this.fractureColor(270 + idx, 1.0f));
            }
            gradientIndex += 45.0f * (1.0f - progress);
            ++count;
        }
        if (count == 0) {
            return;
        }
        double wobbleSeconds = (double)now / 1000.0;
        wobbleSeconds -= Math.floor(wobbleSeconds / 628.3185307179587) * 628.3185307179587;
        data[0] = count;
        data[1] = (float)wobbleSeconds;
        data[2] = this.fractureCount.getValue().floatValue();
        data[3] = this.fractureChaos.getValue().floatValue();
        data[4] = this.fractureWidth.getValue().floatValue();
        data[5] = this.fractureGlowEnabled.getValue() != false ? this.fractureGlow.getValue().floatValue() : 0.0f;
        data[6] = this.fractureSparks.getValue() != false ? 1.0f : 0.0f;
        data[7] = class_3532.method_15363((float)(this.fractureCore.getValue().floatValue() / 100.0f), (float)0.0f, (float)1.0f);
        this.fractureInvViewProj.get(data, 12);
        JumpSoulsPipeline.apply(target, data);
    }

    private boolean isStaticPalette() {
        if (!this.ringColorMode.is("\u0421\u0432\u043e\u0439")) {
            return false;
        }
        if (!this.ringSecondColor.getValue().booleanValue()) {
            return true;
        }
        return this.ringColor.getValue() == this.ringColor2.getValue();
    }

    private int fractureColor(int angle, float rawAlpha) {
        int second;
        if (this.ringColorMode.is("\u0420\u0430\u0434\u0443\u0433\u0430")) {
            return ColorUtil.rainbow(8, angle, 1.0f, 1.0f, rawAlpha);
        }
        if (this.ringColorMode.is("\u041a\u043b\u0438\u0435\u043d\u0442")) {
            int client = ColorUtil.client();
            return ColorUtil.multAlpha(ColorUtil.interpolateColor(client, ColorUtil.multDark(client, 0.45f), (float)((angle % 360 + 360) % 360) / 360.0f), rawAlpha);
        }
        int first = this.ringColor.getValue();
        int n = second = this.ringSecondColor.getValue() != false ? this.ringColor2.getValue() : first;
        if (first == second) {
            return ColorUtil.multAlpha(first, rawAlpha);
        }
        return ColorUtil.multAlpha(ColorUtil.fadeBetween(8.0f, angle, first, second), rawAlpha);
    }

    private static void putColor(float[] dst, int offset, int argb) {
        dst[offset] = (float)(argb >> 16 & 0xFF) / 255.0f;
        dst[offset + 1] = (float)(argb >> 8 & 0xFF) / 255.0f;
        dst[offset + 2] = (float)(argb & 0xFF) / 255.0f;
        dst[offset + 3] = (float)(argb >>> 24 & 0xFF) / 255.0f;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        this.circles.clear();
        this.refractionRings.clear();
        JumpCircleRefraction.clear();
        JumpSoulsPipeline.clear();
    }

    static {
        Arrays.fill(NO_WALLS, 999.0f);
        TEXTURED_QUADS_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/world/textured_quads")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
        RING_QUADS_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/world/ring_quads")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
        RING_QUADS = class_1921.method_75940((String)"ring_quads", (class_12247)class_12247.method_75927((RenderPipeline)RING_QUADS_PIPELINE).method_75937().method_75929(16384).method_75938());
        SHAPES_FILL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/world/circle_shapes_fill")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
        SHAPES_FILL = class_1921.method_75940((String)"circle_shapes_fill", (class_12247)class_12247.method_75927((RenderPipeline)SHAPES_FILL_PIPELINE).method_75937().method_75929(32768).method_75938());
        SHAPES_LINE_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/world/circle_shapes_lines")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_29344).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
        SHAPES_LINES = class_1921.method_75940((String)"circle_shapes_lines", (class_12247)class_12247.method_75927((RenderPipeline)SHAPES_LINE_PIPELINE).method_75929(32768).method_75938());
        GRADIENT_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/visuals/jump_new.png");
        EVOLA_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/visuals/jump_circle.png");
        DESTRA_CIRCLE_MODES = new String[]{"\u041a\u0440\u0443\u0433 1", "\u041a\u0440\u0443\u0433 2", "\u041a\u0440\u0443\u0433 3", "\u041a\u0440\u0443\u0433 4", "\u041a\u0440\u0443\u0433 5"};
        DESTRA_CIRCLE_TEXTURES = new class_2960[]{class_2960.method_60655((String)"decide", (String)"textures/jumpcircle/circle1.png"), class_2960.method_60655((String)"decide", (String)"textures/jumpcircle/circle2.png"), class_2960.method_60655((String)"decide", (String)"textures/jumpcircle/circle3.png"), class_2960.method_60655((String)"decide", (String)"textures/jumpcircle/circle4.png"), class_2960.method_60655((String)"decide", (String)"textures/jumpcircle/bubble.png")};
        DESTRA_CIRCLE_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/world/destra_jump_circle")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
        ROMB_ESP = class_156.method_34866(texture -> {
            class_12247 setup = class_12247.method_75927((RenderPipeline)TEXTURED_QUADS_PIPELINE).method_75934("Sampler0", texture).method_75937().method_75929(1536).method_75938();
            return class_1921.method_75940((String)"wtex", (class_12247)setup);
        });
    }

    @Environment(value=EnvType.CLIENT)
    private class Circle {
        private final class_243 vector3d;
        private final long time;
        private final float seed;
        private final Animation animation = new Animation();
        private final Animation animation2 = new Animation();
        private boolean isBack;

        public Circle(JumpCircle jumpCircle, class_243 vector3d) {
            this.vector3d = vector3d;
            this.time = System.currentTimeMillis();
            this.seed = (float)(Math.random() * 1000.0);
            this.animation2.run(1.0, 0.8, jumpCircle.getExpandEasing());
            this.animation.run(1.0, 0.4, Easings.SINE_OUT);
        }
    }
}

