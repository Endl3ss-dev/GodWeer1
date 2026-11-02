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
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4608
 *  net.minecraft.class_7833
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.decide.manager.rotation;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_1921;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.decide.manager.event_impl.EventMoveInput;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.manager.rotation.Component;
import ru.decide.manager.rotation.FreeLookUtil;
import ru.decide.manager.rotation.Rotation;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.aura.GCDUtil;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.ServerUtil;
import ru.decide.utils.player.MoveUtil;

@Environment(value=EnvType.CLIENT)
public class RotationProcess
extends Component {
    public static RotationTask currentTask = RotationTask.IDLE;
    public static float currentYawSpeed;
    public static float currentPitchSpeed;
    public static float currentYawReturnSpeed;
    public static float currentPitchReturnSpeed;
    public static int currentPriority;
    public static int currentTimeout;
    public static int idleTicks;
    public static Rotation targetRotation;
    private final class_9799 boxAllocator = new class_9799(262144);
    private float animationNurik = 0.0f;
    private long currentTimeSpirits = 0L;
    public class_1309 target = null;
    public Animation alpha = new Animation();
    public Animation alpha_2 = new Animation();
    public static final RenderPipeline ROMB_ESP_PIPELINE;
    public static final Function<class_2960, class_1921> ROMB_ESP;
    private static final RenderPipeline RING_FILL_PIPELINE;
    private static final RenderPipeline RING_LINE_PIPELINE;
    private static final class_1921 RING_FILL_LAYER;
    private static final class_1921 RING_LINE_LAYER;
    private final JitterPreset[] jitterPresets = new JitterPreset[]{new JitterPreset(this){

        @Override
        public float getYaw(long t) {
            return (float)((Math.sin((double)t / 80.0) + Math.cos((double)t / 35.0) * 0.35) * 13.0);
        }

        @Override
        public float getPitch(long t) {
            return (float)((Math.cos((double)t / 95.0) + Math.sin((double)t / 42.0) * 0.4) * 11.0);
        }
    }, new JitterPreset(this){

        @Override
        public float getYaw(long t) {
            return (float)(Math.sin((double)t / 55.0 + Math.sin((double)t / 350.0)) * 15.0);
        }

        @Override
        public float getPitch(long t) {
            return (float)(Math.cos((double)t / 65.0 + Math.cos((double)t / 280.0)) * 14.0);
        }
    }, new JitterPreset(this){

        @Override
        public float getYaw(long t) {
            return (float)((Math.cos((double)t / 42.0) * 0.7 + Math.sin((double)t / 120.0)) * 17.0);
        }

        @Override
        public float getPitch(long t) {
            return (float)((Math.sin((double)t / 58.0) * 0.5 + Math.cos((double)t / 140.0)) * 12.0);
        }
    }, new JitterPreset(this){

        @Override
        public float getYaw(long t) {
            return (float)(Math.sin((double)t / 25.0) * (9.0 + Math.sin((double)t / 300.0) * 4.0));
        }

        @Override
        public float getPitch(long t) {
            return (float)(Math.cos((double)t / 48.0) * (13.0 + Math.cos((double)t / 250.0) * 3.0));
        }
    }, new JitterPreset(this){

        @Override
        public float getYaw(long t) {
            return (float)((Math.sin((double)t / 70.0) + Math.sin((double)t / 17.0) * 0.25) * 16.0);
        }

        @Override
        public float getPitch(long t) {
            return (float)((Math.cos((double)t / 90.0) + Math.cos((double)t / 23.0) * 0.2) * 13.0);
        }
    }, new JitterPreset(this){

        @Override
        public float getYaw(long t) {
            return (float)((Math.cos((double)t / 110.0) * 0.8 + Math.sin((double)t / 38.0) * 0.6) * 12.0);
        }

        @Override
        public float getPitch(long t) {
            return (float)((Math.sin((double)t / 100.0) * 0.7 + Math.cos((double)t / 29.0) * 0.4) * 15.0);
        }
    }, new JitterPreset(this){

        @Override
        public float getYaw(long t) {
            return (float)(Math.sin((double)t / 170.0 + 1.2) * 21.0);
        }

        @Override
        public float getPitch(long t) {
            return (float)((Math.cos((double)t / 52.0) + Math.sin((double)t / 240.0)) * 10.0);
        }
    }, new JitterPreset(this){

        @Override
        public float getYaw(long t) {
            return (float)((Math.cos((double)t / 36.0) * 0.45 + Math.sin((double)t / 145.0)) * 18.0);
        }

        @Override
        public float getPitch(long t) {
            return (float)((Math.sin((double)t / 44.0) * 0.35 + Math.cos((double)t / 180.0)) * 14.0);
        }
    }, new JitterPreset(this){

        @Override
        public float getYaw(long t) {
            return (float)(Math.sin((double)t / 60.0 + Math.cos((double)t / 180.0)) * 14.0);
        }

        @Override
        public float getPitch(long t) {
            return (float)(Math.cos((double)t / 73.0 + Math.sin((double)t / 210.0)) * 17.0);
        }
    }, new JitterPreset(this){

        @Override
        public float getYaw(long t) {
            return (float)((Math.sin((double)t / 48.0) * 0.9 + Math.cos((double)t / 140.0) * 0.5) * 15.0);
        }

        @Override
        public float getPitch(long t) {
            return (float)((Math.cos((double)t / 84.0) * 0.8 + Math.sin((double)t / 32.0) * 0.3) * 12.0);
        }
    }};
    private final SpeedPreset[] speedPresets = new SpeedPreset[]{new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 40.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 12.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 55.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 15.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 30.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 10.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 48.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 18.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 60.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 14.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 35.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 16.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 50.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 13.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 44.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 17.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 58.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 11.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 32.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 19.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 46.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 12.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 53.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 15.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 37.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 18.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 42.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 13.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 57.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 16.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 34.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 11.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 49.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 20.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 62.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 14.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 39.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 17.0f;
        }
    }, new SpeedPreset(this){

        @Override
        public float getYawSpeed() {
            return 52.0f;
        }

        @Override
        public float getPitchSpeed() {
            return 12.0f;
        }
    }};
    private float currentSpeedYaw = 0.0f;
    private float currentSpeedPitch = 0.0f;
    float jitterIntensity = 0.0f;
    private long jitterSwitchTime = ThreadLocalRandom.current().nextLong(1000L, 2001L);
    private long nextJitterSwitch = System.currentTimeMillis() + this.jitterSwitchTime;
    private int currentJitterPreset = 0;
    private int nextJitterPreset = 1;
    private long speedSwitchTime = ThreadLocalRandom.current().nextLong(2000L, 4001L);
    private long nextSpeedSwitch = System.currentTimeMillis() + this.speedSwitchTime;
    private int currentSpeedPreset = 0;
    private int nextSpeedPreset = 1;
    private static final long JITTER_SWITCH_TIME = 1000L;
    private float lastYawJitter;
    private float lastPitchJitter;
    private static final long SPEED_SWITCH_TIME = 2500L;
    private float lastYawSpeed;
    private float lastPitchSpeed;
    int tick;

    public static boolean isRotating() {
        return !currentTask.equals((Object)RotationTask.IDLE);
    }

    private void resetRotation() {
        Rotation targetRotation = new Rotation(FreeLookUtil.freeYaw, FreeLookUtil.freePitch);
        if (ServerUtil.isHolyWorld()) {
            this.stopRotation();
        } else if (RotationProcess.updateRotation(targetRotation, currentYawReturnSpeed, currentPitchReturnSpeed)) {
            this.stopRotation();
        }
    }

    public static void resetParentTimeout() {
        currentTimeout = 0;
        currentTask = RotationTask.IDLE;
        currentPriority = 0;
        FreeLookUtil.setActive(false);
    }

    @EventHandler
    public void onEventMovement(EventMoveInput eventMoveInput) {
        if (currentTask.equals((Object)RotationTask.RESET)) {
            MoveUtil.fixMovement(eventMoveInput, RotationProcess.mc.field_1724.method_36454(), RotationProcess.mc.field_1773.method_19418().method_19330());
        }
    }

    @EventHandler
    public void onEvent(EventTick event) {
        if (currentTask.equals((Object)RotationTask.AIM) && idleTicks > currentTimeout) {
            currentTask = RotationTask.RESET;
        }
        if (currentTask.equals((Object)RotationTask.RESET)) {
            if (ServerUtil.isHolyWorld() || ServerUtil.isCopyTime()) {
                this.stopRotation();
            } else {
                this.resetRotation();
            }
        }
        ++idleTicks;
    }

    public static void update(Rotation target, float yawSpeed, float pitchSpeed, float yawReturnSpeed, float pitchReturnSpeed, int timeout, int priority, boolean clientRotation) {
        if (currentPriority > priority) {
            return;
        }
        if (currentTask.equals((Object)RotationTask.IDLE) && !clientRotation) {
            FreeLookUtil.active = true;
        }
        currentYawSpeed = yawSpeed;
        currentPitchSpeed = pitchSpeed;
        currentYawReturnSpeed = yawReturnSpeed;
        currentPitchReturnSpeed = pitchReturnSpeed;
        currentTimeout = timeout;
        currentPriority = priority;
        currentTask = RotationTask.AIM;
        targetRotation = target;
        RotationProcess.updateRotation(target, yawSpeed, pitchSpeed);
    }

    public static void update(Rotation targetRotation, float turnSpeed, float returnSpeed, int timeout, int priority) {
        RotationProcess.update(targetRotation, turnSpeed, turnSpeed, returnSpeed, returnSpeed, timeout, priority, false);
    }

    static boolean updateRotation(Rotation targetRotation, float yawSpeed, float pitchSpeed) {
        if (RotationProcess.mc.field_1724 == null) {
            return false;
        }
        Rotation currentRotation = new Rotation((class_1297)RotationProcess.mc.field_1724);
        float pitchDelta = targetRotation.pitch - currentRotation.pitch;
        float yawDelta = class_3532.method_15393((float)(targetRotation.yaw - currentRotation.yaw));
        float clampedYaw = Math.min(Math.abs(yawDelta), yawSpeed);
        float clampedPitch = Math.min(Math.abs(pitchDelta), pitchSpeed);
        RotationProcess.mc.field_1724.method_36456(RotationProcess.mc.field_1724.field_6241 += GCDUtil.getSensitivity(class_3532.method_15363((float)yawDelta, (float)(-clampedYaw), (float)clampedYaw)));
        RotationProcess.mc.field_1724.method_36457(class_3532.method_15363((float)(RotationProcess.mc.field_1724.method_36455() + GCDUtil.getSensitivity(class_3532.method_15363((float)pitchDelta, (float)(-clampedPitch), (float)clampedPitch))), (float)-90.0f, (float)90.0f));
        idleTicks = 0;
        return new Rotation((class_1297)RotationProcess.mc.field_1724).getDelta(targetRotation) < 1.0f;
    }

    public void stopRotation() {
        currentTask = RotationTask.IDLE;
        currentPriority = 0;
        FreeLookUtil.setActive(false);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.boxAllocator.method_60809();
    }

    private void renderCorner(class_4587 matrices, class_4588 consumer, float x, float y, float rotation, float scale, int c1, int c2, int c3, int c4, float alpha) {
        matrices.method_22903();
        matrices.method_46416(x, y, 0.0f);
        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(rotation));
        matrices.method_22905(scale * 0.6f, scale * 0.6f, 1.0f);
        Matrix4f mat = matrices.method_23760().method_23761();
        RotationProcess.drawGradientQuad(consumer, mat, c1, c2, c3, c4, (int)(255.0f * alpha));
        matrices.method_22909();
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

    private static void drawGradientQuad(class_4588 buffer, Matrix4f matrix, int color, int color2, int color3, int color4, int alpha) {
        buffer.method_22918((Matrix4fc)matrix, -0.5f, -0.5f, 0.0f).method_39415(ColorUtil.replAlpha(color, alpha)).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        buffer.method_22918((Matrix4fc)matrix, 0.5f, -0.5f, 0.0f).method_39415(ColorUtil.replAlpha(color2, alpha)).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        buffer.method_22918((Matrix4fc)matrix, 0.5f, 0.5f, 0.0f).method_39415(ColorUtil.replAlpha(color3, alpha)).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
        buffer.method_22918((Matrix4fc)matrix, -0.5f, 0.5f, 0.0f).method_39415(ColorUtil.replAlpha(color4, alpha)).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(0.0f, 0.0f, 1.0f);
    }

    static {
        ROMB_ESP_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation("pipeline/wtex").withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color").withSampler("Sampler0").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withCull(false).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).build());
        ROMB_ESP = class_156.method_34866(texture -> {
            class_12247 setup = class_12247.method_75927((RenderPipeline)ROMB_ESP_PIPELINE).method_75934("Sampler0", texture).method_75937().method_75929(1536).method_75938();
            return class_1921.method_75940((String)"wtex", (class_12247)setup);
        });
        RING_FILL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"ring_esp_fill")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
        RING_LINE_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"ring_esp_line")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_29344).withCull(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
        RING_FILL_LAYER = class_1921.method_75940((String)"ring_esp_fill", (class_12247)class_12247.method_75927((RenderPipeline)RING_FILL_PIPELINE).method_75929(65536).method_75938());
        RING_LINE_LAYER = class_1921.method_75940((String)"ring_esp_line", (class_12247)class_12247.method_75927((RenderPipeline)RING_LINE_PIPELINE).method_75929(16384).method_75938());
    }

    @Environment(value=EnvType.CLIENT)
    private static interface JitterPreset {
        public float getYaw(long var1);

        public float getPitch(long var1);
    }

    @Environment(value=EnvType.CLIENT)
    private static interface SpeedPreset {
        public float getYawSpeed();

        public float getPitchSpeed();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum RotationTask {
        AIM,
        RESET,
        IDLE;

    }
}

