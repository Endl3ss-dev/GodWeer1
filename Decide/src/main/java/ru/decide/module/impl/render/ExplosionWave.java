/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1511
 *  net.minecraft.class_2394
 *  net.minecraft.class_2396
 *  net.minecraft.class_2398
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2664
 *  net.minecraft.class_2675
 *  net.minecraft.class_276
 *  net.minecraft.class_2767
 *  net.minecraft.class_3417
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.module.impl.render;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2664;
import net.minecraft.class_2675;
import net.minecraft.class_276;
import net.minecraft.class_2767;
import net.minecraft.class_3417;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.manager.event_impl.CameraPositionEvent;
import ru.decide.manager.event_impl.EventPacket;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.other.Instance;
import ru.decide.utils.render.ExplosionWavePipeline;

@ModuleInfo(name="Explosion Wave", desc="\u0423\u0434\u0430\u0440\u043d\u0430\u044f \u0432\u043e\u043b\u043d\u0430, \u043f\u0440\u0435\u043b\u043e\u043c\u043b\u0435\u043d\u0438\u0435 \u0438 \u0442\u0440\u044f\u0441\u043a\u0430 \u043f\u0440\u0438 \u0432\u0437\u0440\u044b\u0432\u0435", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public final class ExplosionWave
extends Module {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"client/ExplosionWave");
    private static final int MAX_WAVES = 8;
    private static final int WAVE_STRIDE = 12;
    private static final int WAVE_OFFSET = 24;
    private static final double BLAST_DEDUP_SQ = 12.25;
    private static final double CRYSTAL_PROXIMITY_SQ = 16.0;
    public final DelimiterSetting generalSeparator = new DelimiterSetting(this, "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0435");
    public final SliderSetting waveTime = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f \u0432\u043e\u043b\u043d\u044b", 2000.0f, 600.0f, 3000.0f, 50.0f);
    public final SliderSetting waveRadius = new SliderSetting(this, "\u0420\u0430\u0434\u0438\u0443\u0441 \u0432\u043e\u043b\u043d\u044b", 15.0f, 5.0f, 30.0f, 0.5f);
    public final SliderSetting maxDistance = new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f", 50.0f, 10.0f, 96.0f, 1.0f);
    public final DelimiterSetting distortSeparator = new DelimiterSetting(this, "\u0418\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u0435");
    public final SliderSetting distortStrength = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0438\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u044f", 3.0f, 0.2f, 3.0f, 0.1f);
    public final SliderSetting waveThickness = new SliderSetting(this, "\u0422\u043e\u043b\u0449\u0438\u043d\u0430", 25.0f, 10.0f, 60.0f, 1.0f);
    public final BooleanSetting chromatic = new BooleanSetting((Module)this, "\u0425\u0440\u043e\u043c\u0430\u0442\u0438\u043a\u0430", true);
    public final SliderSetting chromaticStrength = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0445\u0440\u043e\u043c\u0430\u0442\u0438\u043a\u0438", 1.5f, 0.2f, 3.0f, 0.1f).setVisible(() -> this.chromatic.getValue());
    public final DelimiterSetting flashSeparator = new DelimiterSetting(this, "\u0412\u0441\u043f\u044b\u0448\u043a\u0430");
    public final BooleanSetting flash = new BooleanSetting((Module)this, "\u0412\u0441\u043f\u044b\u0448\u043a\u0430", true);
    public final SliderSetting flashStrength = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0432\u0441\u043f\u044b\u0448\u043a\u0438", 50.0f, 10.0f, 100.0f, 1.0f).setVisible(() -> this.flash.getValue());
    public final DelimiterSetting shakeSeparator = new DelimiterSetting(this, "\u0422\u0440\u044f\u0441\u043a\u0430");
    public final BooleanSetting shake = new BooleanSetting((Module)this, "\u0422\u0440\u044f\u0441\u043a\u0430 \u044d\u043a\u0440\u0430\u043d\u0430", true);
    public final SliderSetting shakeStrength = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0442\u0440\u044f\u0441\u043a\u0438", 1.5f, 0.2f, 3.0f, 0.1f).setVisible(() -> this.shake.getValue());
    public final SliderSetting shakeTime = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f \u0442\u0440\u044f\u0441\u043a\u0438", 1000.0f, 200.0f, 1500.0f, 50.0f).setVisible(() -> this.shake.getValue());
    private final Queue<PendingBlast> pending = new ArrayDeque<PendingBlast>();
    private final List<Wave> waves = new ArrayList<Wave>();
    private final List<RecentBlast> recentBlasts = new ArrayList<RecentBlast>();
    private final Map<Integer, RecentBlast> crystalTracks = new HashMap<Integer, RecentBlast>();
    private final Matrix4f invViewProj = new Matrix4f();
    private final Matrix4f viewProj = new Matrix4f();
    private final Vector4f projScratch = new Vector4f();
    private final float[] uniformScratch = new float[120];
    private long enabledAtMillis;
    private float shakeAmp;
    private long shakeUntil;

    public static ExplosionWave getInstance() {
        return Instance.get(ExplosionWave.class);
    }

    @Override
    public void setup() {
        super.setup();
        if (ExplosionWavePipeline.deviceReady() && !ExplosionWavePipeline.validate()) {
            LOGGER.warn("[ExplosionWave] pipeline init failed, waves will not render");
        }
    }

    @Override
    protected void onEnable() {
        this.enabledAtMillis = System.currentTimeMillis();
        if (ExplosionWavePipeline.deviceReady() && !ExplosionWavePipeline.validate()) {
            LOGGER.warn("[ExplosionWave] pipeline unavailable, waves will not render");
        }
    }

    public float visualAlpha() {
        if (!this.isEnabled()) {
            return 0.0f;
        }
        long elapsed = System.currentTimeMillis() - this.enabledAtMillis;
        if (elapsed >= 1000L) {
            return 1.0f;
        }
        return Math.max(0.0f, (float)elapsed / 1000.0f);
    }

    @Override
    protected void onDisable() {
        this.pending.clear();
        this.waves.clear();
        this.recentBlasts.clear();
        this.crystalTracks.clear();
        this.shakeAmp = 0.0f;
        this.shakeUntil = 0L;
        ExplosionWavePipeline.clear();
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        class_2767 sound;
        if (event.isSend()) {
            return;
        }
        class_2596<?> packet = event.getPacket();
        if (packet instanceof class_2664) {
            class_2664 explosion = (class_2664)packet;
            if (!ExplosionWave.isWindBurst(explosion)) {
                this.pending.add(new PendingBlast(explosion.comp_2883(), explosion.comp_4594()));
            }
            return;
        }
        if (packet instanceof class_2767 && (sound = (class_2767)packet).method_11894() != null && sound.method_11894().comp_349() == class_3417.field_15152.comp_349()) {
            this.pending.add(new PendingBlast(new class_243(sound.method_11890(), sound.method_11889(), sound.method_11893()), 4.0f));
            return;
        }
        if (packet instanceof class_2675) {
            class_2675 particles = (class_2675)packet;
            class_2394 params = particles.method_11551();
            if (params == null) {
                return;
            }
            class_2396 type = params.method_10295();
            if (type == class_2398.field_11221) {
                this.pending.add(new PendingBlast(new class_243(particles.method_11544(), particles.method_11547(), particles.method_11546()), 4.0f));
            } else if (type == class_2398.field_11236) {
                this.pending.add(new PendingBlast(new class_243(particles.method_11544(), particles.method_11547(), particles.method_11546()), 2.0f));
            }
        }
    }

    private static boolean isWindBurst(class_2664 packet) {
        class_2394 effect = packet.comp_2885();
        if (effect == null) {
            return false;
        }
        class_2396 type = effect.method_10295();
        return type == class_2398.field_47494 || type == class_2398.field_50252 || type == class_2398.field_49139 || type == class_2398.field_49140;
    }

    @EventHandler
    public void onRender(EventRender3D event) {
        this.trackCrystals();
        this.drainPending();
        float life = Math.max(1.0f, this.waveTime.getValue().floatValue());
        for (int i = this.waves.size() - 1; i >= 0; --i) {
            Wave wave = this.waves.get(i);
            if (wave.progress(life) < 1.0f && ExplosionWave.isFiniteAndSafe(wave.pos)) continue;
            this.waves.remove(i);
        }
    }

    private void trackCrystals() {
        class_638 level = ExplosionWave.mc.field_1687;
        if (level == null) {
            return;
        }
        long now = System.currentTimeMillis();
        for (class_1297 entity : level.method_18112()) {
            if (!(entity instanceof class_1511)) continue;
            class_1511 crystal = (class_1511)entity;
            class_243 pos = entity.method_73189();
            if (!ExplosionWave.isFiniteAndSafe(pos)) continue;
            this.crystalTracks.put(crystal.method_5628(), new RecentBlast(pos, now));
        }
        this.crystalTracks.values().removeIf(track -> now - track.time > 2000L);
    }

    private boolean nearCrystal(class_243 center) {
        for (RecentBlast track : this.crystalTracks.values()) {
            if (!(track.pos.method_1025(center) < 16.0)) continue;
            return true;
        }
        return false;
    }

    private void drainPending() {
        PendingBlast blast;
        if (ExplosionWave.mc.field_1724 == null) {
            return;
        }
        long now = System.currentTimeMillis();
        this.recentBlasts.removeIf(recent -> now - recent.time > 400L);
        while ((blast = this.pending.poll()) != null) {
            float amp;
            if (!ExplosionWave.isFiniteAndSafe(blast.center)) continue;
            float maxDist = this.maxDistance.getValue().floatValue();
            double dist = ExplosionWave.mc.field_1724.method_73189().method_1022(blast.center);
            if (dist > (double)maxDist) continue;
            boolean duplicate = false;
            for (RecentBlast recent2 : this.recentBlasts) {
                if (!(recent2.pos.method_1025(blast.center) < 12.25)) continue;
                duplicate = true;
                break;
            }
            if (duplicate || this.nearCrystal(blast.center)) continue;
            this.recentBlasts.add(new RecentBlast(blast.center, now));
            float falloff = (float)Math.pow(1.0 - dist / (double)maxDist, 1.5);
            float power = Math.max(Math.min(blast.power / 4.0f, 2.5f), 0.6f);
            if (this.waves.size() >= 8) {
                this.waves.remove(0);
            }
            this.waves.add(new Wave(blast.center, power, falloff));
            if (!this.shake.getValue().booleanValue() || (amp = 3.5f * this.shakeStrength.getValue().floatValue() * falloff * power) <= 0.05f) continue;
            this.shakeAmp = Math.max(this.shakeAmp, amp);
            this.shakeUntil = Math.max(this.shakeUntil, now + this.shakeTime.getValue().longValue());
        }
    }

    @EventHandler
    public void onCameraPosition(CameraPositionEvent event) {
        if (!this.isEnabled() || !this.shake.getValue().booleanValue() || this.shakeAmp <= 0.01f) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now >= this.shakeUntil) {
            this.shakeAmp = 0.0f;
            return;
        }
        class_243 pos = event.getPos();
        if (pos == null) {
            return;
        }
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        float a = this.shakeAmp;
        event.setPos(new class_243(pos.field_1352 + (double)((rnd.nextFloat() - 0.5f) * a), pos.field_1351 + (double)((rnd.nextFloat() - 0.5f) * a), pos.field_1350 + (double)((rnd.nextFloat() - 0.5f) * a)));
    }

    public void renderWaves(class_276 renderTarget, Matrix4f positionMatrix, Matrix4f projectionMatrix) {
        if (!this.isEnabled() || renderTarget == null || projectionMatrix == null || positionMatrix == null) {
            return;
        }
        if (this.waves.isEmpty()) {
            return;
        }
        if (ExplosionWave.mc.field_1773 == null || ExplosionWave.mc.field_1773.method_19418() == null) {
            return;
        }
        if (renderTarget.field_1482 <= 0 || renderTarget.field_1481 <= 0) {
            return;
        }
        class_4184 camera = ExplosionWave.mc.field_1773.method_19418();
        class_243 cam = camera.method_71156();
        float life = Math.max(1.0f, this.waveTime.getValue().floatValue());
        float maxRadiusSetting = this.waveRadius.getValue().floatValue();
        float baseAmp = 0.022f * this.distortStrength.getValue().floatValue() * this.visualAlpha();
        float aspect = (float)renderTarget.field_1482 / (float)Math.max(1, renderTarget.field_1481);
        this.invViewProj.set((Matrix4fc)projectionMatrix).mul((Matrix4fc)positionMatrix).invert();
        this.viewProj.set((Matrix4fc)projectionMatrix).mul((Matrix4fc)positionMatrix);
        float[] data = this.uniformScratch;
        int count = 0;
        float globalFlash = 0.0f;
        for (Wave wave : this.waves) {
            float fadeOut;
            if (count >= 8) break;
            float progress = wave.progress(life);
            if (progress < 0.0f || progress >= 1.0f) continue;
            float eased = 1.0f - (1.0f - progress) * (1.0f - progress);
            float maxRadius = maxRadiusSetting * wave.power;
            float radius = Math.max(eased * maxRadius, 0.05f);
            float thickness = Math.max(0.3f, maxRadius * this.waveThickness.getValue().floatValue() / 100.0f);
            float fadeIn = class_3532.method_15363((float)(progress / 0.05f), (float)0.0f, (float)1.0f);
            float env = fadeIn * (fadeOut = class_3532.method_15363((float)((1.0f - progress) / 0.4f), (float)0.0f, (float)1.0f)) * wave.falloff;
            if (env <= 0.001f) continue;
            float waveFlash = this.flash.getValue() != false ? Math.max(0.0f, 1.0f - progress / 0.12f) * wave.falloff : 0.0f;
            globalFlash = Math.max(globalFlash, waveFlash * this.flashStrength.getValue().floatValue() / 100.0f);
            float cx = (float)(wave.pos.field_1352 - cam.field_1352);
            float cy = (float)(wave.pos.field_1351 - cam.field_1351);
            float cz = (float)(wave.pos.field_1350 - cam.field_1350);
            this.projScratch.set(cx, cy, cz, 1.0f);
            this.viewProj.transform(this.projScratch);
            float sx = 0.0f;
            float sy = 0.0f;
            float valid = 0.0f;
            if (this.projScratch.w > 0.001f) {
                sx = this.projScratch.x / this.projScratch.w * 0.5f + 0.5f;
                sy = this.projScratch.y / this.projScratch.w * 0.5f + 0.5f;
                valid = 1.0f;
            }
            int base = 24 + count * 12;
            data[base] = cx;
            data[base + 1] = cy;
            data[base + 2] = cz;
            data[base + 3] = radius;
            data[base + 4] = thickness;
            data[base + 5] = baseAmp * env;
            data[base + 6] = env;
            data[base + 7] = waveFlash;
            data[base + 8] = sx;
            data[base + 9] = sy;
            data[base + 10] = valid;
            data[base + 11] = (float)Math.sqrt(cx * cx + cy * cy + cz * cz);
            ++count;
        }
        if (count == 0) {
            return;
        }
        data[0] = count;
        data[1] = aspect;
        data[2] = this.chromatic.getValue() != false ? 0.35f * this.chromaticStrength.getValue().floatValue() : 0.0f;
        data[3] = globalFlash;
        data[4] = 0.35f;
        data[5] = 0.0f;
        data[6] = 0.0f;
        data[7] = 0.0f;
        this.invViewProj.get(data, 8);
        ExplosionWavePipeline.apply(renderTarget, data);
    }

    private static boolean isFiniteAndSafe(class_243 pos) {
        return ExplosionWave.isFiniteAndSafe(pos.field_1352) && ExplosionWave.isFiniteAndSafe(pos.field_1351) && ExplosionWave.isFiniteAndSafe(pos.field_1350);
    }

    private static boolean isFiniteAndSafe(double value) {
        return Double.isFinite(value) && Math.abs(value) <= 3.0E7;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class PendingBlast {
        private final class_243 center;
        private final float power;

        private PendingBlast(class_243 center, float power) {
            this.center = center;
            this.power = power;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Wave {
        private final class_243 pos;
        private final float power;
        private final float falloff;
        private final long time;

        private Wave(class_243 pos, float power, float falloff) {
            this.pos = pos;
            this.power = power;
            this.falloff = falloff;
            this.time = System.currentTimeMillis();
        }

        private float progress(float life) {
            return (float)(System.currentTimeMillis() - this.time) / life;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class RecentBlast {
        private final class_243 pos;
        private final long time;

        private RecentBlast(class_243 pos, long time) {
            this.pos = pos;
            this.time = time;
        }
    }
}

