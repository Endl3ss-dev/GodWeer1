/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_1297
 *  net.minecraft.class_156
 *  net.minecraft.class_1667
 *  net.minecraft.class_1684
 *  net.minecraft.class_1685
 *  net.minecraft.class_1921
 *  net.minecraft.class_1922
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_243
 *  net.minecraft.class_2596
 *  net.minecraft.class_2663
 *  net.minecraft.class_290
 *  net.minecraft.class_2902$class_2903
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_5819
 *  net.minecraft.class_745
 *  net.minecraft.class_746
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1297;
import net.minecraft.class_156;
import net.minecraft.class_1667;
import net.minecraft.class_1684;
import net.minecraft.class_1685;
import net.minecraft.class_1921;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_290;
import net.minecraft.class_2902;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_5819;
import net.minecraft.class_745;
import net.minecraft.class_746;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.event_impl.EventPacket;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.MotionEvent;
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
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.MathUtil;
import ru.decide.utils.taskript.StopWatch;

@ModuleInfo(name="Particles", desc="\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0435 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0432\u043e\u043a\u0440\u0443\u0433 \u0438\u0433\u0440\u043e\u043a\u0430", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class Particles
extends Module
implements ModulePreview {
    public ButtonSetting previewButton = PreviewSettings.button(this);
    public BooleanSetting onAttack = new BooleanSetting((Module)this, "\u041f\u0440\u0438 \u0430\u0442\u0430\u043a\u0435", true);
    public SliderSetting onAtSizs = new SliderSetting(this, "\u041a\u043e\u043b \u0432 \u0430\u0442\u0430\u043a\u0435", 20.0f, 5.0f, 50.0f, 1.0f).setVisible(() -> this.onAttack.getValue());
    public BooleanSetting totem = new BooleanSetting((Module)this, "\u041f\u0440\u0438 \u0442\u043e\u0442\u0435\u043c\u0435", true);
    public SliderSetting onTtSizs = new SliderSetting(this, "\u041a\u043e\u043b \u043f\u0440\u0438 \u0442\u043e\u0442\u0435\u043c\u0435", 75.0f, 20.0f, 150.0f, 1.0f).setVisible(() -> this.totem.getValue());
    public BooleanSetting perka = new BooleanSetting((Module)this, "\u041f\u0440\u0438 \u043a\u0438\u0434\u0430\u043d\u0438\u0438", true);
    public SliderSetting onPerkSizs = new SliderSetting(this, "\u041a\u043e\u043b \u043f\u0440\u0438 \u043a\u0438\u0434\u0430\u043d\u0438\u0438", 3.0f, 1.0f, 8.0f, 1.0f).setVisible(() -> this.perka.getValue());
    public BooleanSetting worlds = new BooleanSetting((Module)this, "\u0412 \u043c\u0438\u0440\u0435", true);
    public SliderSetting onworldsSizs = new SliderSetting(this, "\u041a\u043e\u043b \u0432 \u043c\u0438\u0440\u0435", 10.0f, 2.0f, 60.0f, 1.0f).setVisible(() -> this.worlds.getValue());
    public BooleanSetting move = new BooleanSetting((Module)this, "\u041f\u0440\u0438 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u0438", true);
    private static final String[] PARTICLE_MODES = Particles.particleModes();
    private final ModeSetting particleMode = new ModeSetting((Module)this, "\u0422\u0438\u043f \u0447\u0430\u0441\u0442\u0438\u0446", PARTICLE_MODES);
    private final SliderSetting size = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440", 0.5f, 0.0f, 1.0f, 0.1f);
    public BooleanSetting glows = new BooleanSetting((Module)this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u044f", true);
    private final SliderSetting glowsiz = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 0.5f, 0.0f, 1.0f, 0.1f).setVisible(() -> this.glows.getValue());
    public BooleanSetting rotations = new BooleanSetting((Module)this, "\u0420\u043e\u0442\u0430\u0446\u0438\u044f", true);
    private final PreviewSettings previewSettings = PreviewSettings.of(this, 4.0f, 0.0f, 2.0f);
    public final List<Particle> targetParticles = new ArrayList<Particle>();
    public final List<Particle> totemParticles = new ArrayList<Particle>();
    public final List<Particle> frameParticles = new ArrayList<Particle>();
    public final List<Particle> worldParticles = new ArrayList<Particle>();
    private long totemSpawnStartTime = 0L;
    private class_1297 totemTargetEntity = null;
    private int totemParticlesLeftToSpawn = 0;
    private int totalTotemParticlesToSpawn = 0;
    private long lastUpdateTime = System.nanoTime();
    private final class_9799 boxAllocator = new class_9799(4096);
    private static final Map<ParticleType, class_1921> RENDER_LAYER_CACHE = new ConcurrentHashMap<ParticleType, class_1921>();
    private static final class_2960 BLOOM_TEXTURE = class_2960.method_60655((String)"decide", (String)"textures/particles/glow.png");
    private static class_1921 cachedBloomLayer = null;
    private static final RenderPipeline TEXTURED_QUADS_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/world/textured_quads")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
    public static final Function<class_2960, class_1921> ROMB_ESP = class_156.method_34866(texture -> {
        class_12247 setup = class_12247.method_75927((RenderPipeline)TEXTURED_QUADS_PIPELINE).method_75934("Sampler0", texture).method_75937().method_75929(1536).method_75938();
        return class_1921.method_75940((String)"wtex", (class_12247)setup);
    });
    private static final Vector3f REUSABLE_NORMAL = new Vector3f(0.0f, 0.0f, 1.0f);

    private static String[] particleModes() {
        ArrayList<String> modes = new ArrayList<String>(List.of("Client", "Circle", "Bloom", "Star", "Heart", "Snow", "Dollar", "Pumpkin", "Triangle", "Sakura", "Genshin", "Rhombus"));
        for (WeatherParticleStyle style : WeatherParticleStyle.values()) {
            modes.add(style.displayName());
        }
        return modes.toArray(new String[0]);
    }

    @Override
    public void toggle() {
        super.toggle();
        this.clear();
    }

    @EventHandler
    public void onEvent(WorldLoadEvent event) {
        this.clear();
    }

    private void spawnParticle(List<Particle> particles, class_243 position, class_243 velocity, boolean isWorld) {
        WeatherParticleStyle style = WeatherParticleStyle.byName(this.particleMode.getValue());
        float particleSize = (0.05f + this.size.getValue().floatValue() * 0.2f) * (style == null ? 1.0f : style.sizeScale());
        int color = style == null ? ColorUtil.fade(particles.size() * 100) : Particles.weatherColor(style);
        ParticleType type = this.selectedType(style);
        particles.add(new Particle(type, position.method_1031(0.0, (double)particleSize, 0.0), velocity, particles.size(), (int)MathUtil.step(MathUtil.randomValue(0.0f, 360.0f), 15.0f), color, particleSize, 1.1f, isWorld, false));
    }

    private ParticleType selectedType(WeatherParticleStyle style) {
        if (style != null) {
            return style.type();
        }
        return switch (this.particleMode.getValue()) {
            case "Heart" -> ParticleType.HEART;
            case "Star" -> ParticleType.STAR;
            case "Snow" -> ParticleType.SNOOW;
            case "Bloom" -> ParticleType.BLOOM;
            case "Circle" -> ParticleType.CIRCLE;
            case "Client" -> ParticleType.STAR_NEW;
            case "Dollar" -> ParticleType.DOLLAR;
            case "Pumpkin" -> ParticleType.PUMPKIN;
            case "Triangle" -> ParticleType.TRIANGLE;
            case "Sakura" -> ParticleType.SAKURA;
            case "Genshin" -> ParticleType.GEMINI;
            case "Rhombus" -> ParticleType.SIMS;
            default -> ParticleType.BLOOM;
        };
    }

    private static int weatherColor(WeatherParticleStyle style) {
        return ColorUtil.interpolateColor(style.color(), -1, MathUtil.randomValue(0.0f, 0.18f));
    }

    public void spawnParticleTotem(List<Particle> particles, class_243 position, class_243 velocity) {
        WeatherParticleStyle style = WeatherParticleStyle.byName(this.particleMode.getValue());
        float particleSize = (0.05f + this.size.getValue().floatValue() * 0.2f) * (style == null ? 1.0f : style.sizeScale());
        ParticleType type = this.selectedType(style);
        int[] colors = new int[]{ColorUtil.getColor(221, 218, 127), ColorUtil.getColor(127, 221, 144), ColorUtil.getColor(255, 215, 0), ColorUtil.getColor(50, 205, 50)};
        int col = style == null ? colors[MathUtil.randomInt(0, colors.length - 1)] : Particles.weatherColor(style);
        particles.add(new Particle(type, position.method_1031(0.0, (double)particleSize, 0.0), velocity, particles.size(), (int)MathUtil.step(MathUtil.randomValue(0.0f, 360.0f), 15.0f), col, particleSize, 1.1f, false, true));
    }

    @EventHandler
    public void onEvent(AttackEvent event) {
        if (this.onAttack.getValue().booleanValue()) {
            this.spawnAttackBurst(event.getTarget());
        }
    }

    public void spawnAttackBurst(class_1297 target) {
        if (target == null) {
            return;
        }
        float motion = 1.1f;
        int i = 0;
        while ((float)i < this.onAtSizs.getValue().floatValue()) {
            this.spawnParticle(this.targetParticles, new class_243(target.method_23317() + (double)MathUtil.randomValue(-0.035f, 0.035f), target.method_23318() + (double)MathUtil.randomValue(0.1f, target.method_17682()), target.method_23321() + (double)MathUtil.randomValue(-0.035f, 0.035f)), new class_243((double)MathUtil.randomValue(-motion + 0.2f, motion - 0.2f), (double)MathUtil.randomValue(-motion, motion / 2.0f), (double)MathUtil.randomValue(-motion + 0.2f, motion - 0.2f)), false);
            ++i;
        }
    }

    public void spawnTotemBurst(class_1297 target, int count) {
        if (target == null) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            double angle = MathUtil.randomValue(0.0, Math.PI * 2);
            double speedXZ = MathUtil.randomValue(2.5, 4.0) * (double)0.1f;
            this.spawnParticleTotem(this.totemParticles, new class_243(target.method_23317() + MathUtil.randomValue(-0.2, 0.2), target.method_23318() + (double)MathUtil.randomValue(0.1f, target.method_17682()), target.method_23321() + MathUtil.randomValue(-0.2, 0.2)), new class_243(Math.cos(angle) * speedXZ, MathUtil.randomValue(0.0, (double)(target.method_17682() / 4.0f)), Math.sin(angle) * speedXZ));
        }
    }

    public void spawnThrowBurst(class_1297 target) {
        if (target == null) {
            return;
        }
        class_243 pos = target.method_73189().method_1031(0.0, (double)(target.method_17682() * 0.6f), 0.0);
        int i = 0;
        while ((float)i < this.onPerkSizs.getValue().floatValue() * 8.0f) {
            this.spawnParticle(this.frameParticles, pos.method_1031(MathUtil.randomValue(-0.1, 0.1), MathUtil.randomValue(-0.1, 0.1), MathUtil.randomValue(-0.1, 0.1)), new class_243(MathUtil.randomValue(-0.15, 0.15), MathUtil.randomValue(-0.15, 0.15), MathUtil.randomValue(-0.15, 0.15)), true);
            ++i;
        }
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
        class_745 target = ctx.dummy();
        if (target == null) {
            return;
        }
        ArrayList<Runnable> demos = new ArrayList<Runnable>(3);
        if (this.onAttack.getValue().booleanValue()) {
            demos.add(() -> this.lambda$previewSpawn$5((class_1297)target));
        }
        if (this.totem.getValue().booleanValue()) {
            demos.add(() -> this.lambda$previewSpawn$6((class_1297)target));
        }
        if (this.perka.getValue().booleanValue()) {
            demos.add(() -> this.lambda$previewSpawn$7((class_1297)target));
        }
        if (demos.isEmpty()) {
            demos.add(() -> this.lambda$previewSpawn$8((class_1297)target));
        }
        ((Runnable)demos.get(Math.floorMod(ctx.phase(), demos.size()))).run();
    }

    @Override
    public void previewStop() {
        this.clear();
    }

    @EventHandler
    public void onPacket(EventPacket e) {
        class_2596<?> class_25962 = e.getPacket();
        if (!(class_25962 instanceof class_2663)) {
            return;
        }
        class_2663 packet = (class_2663)class_25962;
        if (packet.method_11470() == 35) {
            class_1297 entity = packet.method_11469((class_1937)Particles.mc.field_1687);
            if (entity == null) {
                return;
            }
            if (this.totem.getValue().booleanValue()) {
                this.totemTargetEntity = entity;
                this.totemParticlesLeftToSpawn = this.totalTotemParticlesToSpawn = this.onTtSizs.getValue().intValue();
                this.totemSpawnStartTime = System.currentTimeMillis();
            }
        }
    }

    @EventHandler
    public void onEvent(EventTick e) {
        class_746 player = Particles.mc.field_1724;
        if (player == null || Particles.mc.field_1687 == null) {
            return;
        }
        if (this.perka.getValue().booleanValue()) {
            for (class_1297 entity : Particles.mc.field_1687.method_18112()) {
                boolean isMoving;
                class_1685 trident;
                if (!(entity instanceof class_1684) && !(entity instanceof class_1667) && !(entity instanceof class_1685) || entity instanceof class_1685 && (trident = (class_1685)entity).method_24828()) continue;
                boolean bl = isMoving = entity.field_6038 != entity.method_23317() || entity.field_5971 != entity.method_23318() || entity.field_5989 != entity.method_23321();
                if (!isMoving) continue;
                class_243 pos = entity.method_73189();
                int i = 0;
                while ((float)i < this.onPerkSizs.getValue().floatValue()) {
                    this.spawnParticle(this.frameParticles, new class_243(pos.field_1352 + class_3532.method_15366((class_5819)class_5819.method_43047(), (double)-0.1, (double)0.1), pos.field_1351 + class_3532.method_15366((class_5819)class_5819.method_43047(), (double)-0.1, (double)0.1), pos.field_1350 + class_3532.method_15366((class_5819)class_5819.method_43047(), (double)-0.1, (double)0.1)), new class_243(class_3532.method_15366((class_5819)class_5819.method_43047(), (double)-0.15, (double)0.15), class_3532.method_15366((class_5819)class_5819.method_43047(), (double)-0.15, (double)0.15), class_3532.method_15366((class_5819)class_5819.method_43047(), (double)-0.15, (double)0.15)), true);
                    ++i;
                }
            }
        }
    }

    @EventHandler
    public void onEvent(MotionEvent e) {
        if (this.move.getValue().booleanValue() && this.hasPlayerMoved()) {
            this.spawnParticle(this.frameParticles, new class_243(Particles.mc.field_1724.method_23317() + MathUtil.randomValue(-0.1, 0.1), Particles.mc.field_1724.method_23318() + (double)MathUtil.randomValue(0.0f, Particles.mc.field_1724.method_17682()), Particles.mc.field_1724.method_23321() + MathUtil.randomValue(-0.1, 0.1)), new class_243(MathUtil.randomValue(-0.3, 0.3), MathUtil.randomValue(-0.03, 0.3), MathUtil.randomValue(-0.3, 0.3)), true);
        }
        if (this.worlds.getValue().booleanValue()) {
            if (Particles.mc.field_1687 == null || Particles.mc.field_1724 == null) {
                return;
            }
            int r = 24;
            int i = 0;
            while ((float)i < this.onworldsSizs.getValue().floatValue()) {
                class_243 additional = Particles.mc.field_1724.method_73189().method_1031((double)MathUtil.randomValue(-r, r), 0.0, (double)MathUtil.randomValue(-r, r));
                class_2338 topPos = Particles.mc.field_1687.method_8598(class_2902.class_2903.field_13197, class_2338.method_49638((class_2374)additional));
                double x = (float)topPos.method_10263() + MathUtil.randomValue(-3.0f, 3.0f);
                double z = (float)topPos.method_10260() + MathUtil.randomValue(-3.0f, 3.0f);
                double y = Particles.mc.field_1724.method_23318() + (double)MathUtil.randomValue(Particles.mc.field_1724.method_17682(), r);
                class_243 spawnPos = new class_243(x, y, z);
                while (!Particles.mc.field_1687.method_22347(class_2338.method_49638((class_2374)spawnPos)) && spawnPos.field_1351 < (double)Particles.mc.field_1687.method_31600()) {
                    spawnPos = spawnPos.method_1031(0.0, 1.0, 0.0);
                }
                this.spawnParticle(this.worldParticles, spawnPos, new class_243(Particles.mc.field_1724.method_18798().field_1352 + (double)MathUtil.randomValue(-0.6f, 0.6f), MathUtil.randomValue(-0.01, 0.44), Particles.mc.field_1724.method_18798().field_1350 + (double)MathUtil.randomValue(-0.6f, 0.6f)), true);
                ++i;
            }
        }
        if (this.totemParticlesLeftToSpawn > 0 && this.totemTargetEntity != null) {
            if (this.totemTargetEntity.method_5805() || Particles.mc.field_1687.method_8469(this.totemTargetEntity.method_5628()) != null) {
                long totalDuration;
                long elapsed = System.currentTimeMillis() - this.totemSpawnStartTime;
                if (elapsed < (totalDuration = 1500L)) {
                    int expectedSpawnedSoFar = (int)((double)elapsed / (double)totalDuration * (double)this.totalTotemParticlesToSpawn);
                    int currentSpawned = this.totalTotemParticlesToSpawn - this.totemParticlesLeftToSpawn;
                    int toSpawnThisTick = expectedSpawnedSoFar - currentSpawned;
                    if (toSpawnThisTick > this.totemParticlesLeftToSpawn) {
                        toSpawnThisTick = this.totemParticlesLeftToSpawn;
                    }
                    for (int i = 0; i < toSpawnThisTick; ++i) {
                        double angle = MathUtil.randomValue(0.0, Math.PI * 2);
                        double speedXZ = MathUtil.randomValue(2.5, 4.0) * (double)0.1f;
                        double motionX = Math.cos(angle) * speedXZ;
                        double motionZ = Math.sin(angle) * speedXZ;
                        double motionY = MathUtil.randomValue(0.0, (double)(this.totemTargetEntity.method_17682() / 4.0f));
                        this.spawnParticleTotem(this.totemParticles, new class_243(this.totemTargetEntity.method_23317() + MathUtil.randomValue(-0.2, 0.2), this.totemTargetEntity.method_23318() + (double)MathUtil.randomValue(0.1f, this.totemTargetEntity.method_17682()), this.totemTargetEntity.method_23321() + MathUtil.randomValue(-0.2, 0.2)), new class_243(motionX, motionY, motionZ));
                        --this.totemParticlesLeftToSpawn;
                    }
                } else {
                    for (int i = 0; i < this.totemParticlesLeftToSpawn; ++i) {
                        double angle = MathUtil.randomValue(0.0, Math.PI * 2);
                        double speedXZ = MathUtil.randomValue(2.5, 4.0) * (double)0.1f;
                        double motionX = Math.cos(angle) * speedXZ;
                        double motionZ = Math.sin(angle) * speedXZ;
                        double motionY = MathUtil.randomValue(0.0, (double)(this.totemTargetEntity.method_17682() / 4.0f));
                        this.spawnParticleTotem(this.totemParticles, new class_243(this.totemTargetEntity.method_23317() + MathUtil.randomValue(-0.2, 0.2), this.totemTargetEntity.method_23318() + (double)MathUtil.randomValue(0.1f, this.totemTargetEntity.method_17682()), this.totemTargetEntity.method_23321() + MathUtil.randomValue(-0.2, 0.2)), new class_243(motionX, motionY, motionZ));
                    }
                    this.totemParticlesLeftToSpawn = 0;
                    this.totemTargetEntity = null;
                }
            } else {
                this.totemParticlesLeftToSpawn = 0;
                this.totemTargetEntity = null;
            }
        }
        this.removeExpiredParticles(this.frameParticles, 3000L);
        this.removeExpiredParticles(this.totemParticles, 3500L);
        this.removeExpiredParticles(this.worldParticles, 3500L);
        this.removeExpiredParticles(this.targetParticles, 6000L);
    }

    private boolean hasPlayerMoved() {
        return Particles.mc.field_1724.field_6038 != Particles.mc.field_1724.method_23317() || Particles.mc.field_1724.field_5971 != Particles.mc.field_1724.method_23318() || Particles.mc.field_1724.field_5989 != Particles.mc.field_1724.method_23321();
    }

    private void removeExpiredParticles(List<Particle> particles, long lifespan) {
        particles.removeIf(particle -> particle.time().finished(lifespan));
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.boxAllocator.method_60809();
    }

    @EventHandler
    public void onEvent(EventRender3D event) {
        class_4587 matrix = event.getMatrixStack();
        long now = System.nanoTime();
        double deltaTime = (double)(now - this.lastUpdateTime) / 1.0E9;
        this.lastUpdateTime = now;
        matrix.method_22903();
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.boxAllocator);
        if (this.glows.getValue().booleanValue()) {
            this.renderParticlesGlow(matrix, immediate, this.targetParticles, 2000L, 2500L, deltaTime);
            this.renderParticlesGlow(matrix, immediate, this.totemParticles, 1000L, 1500L, deltaTime);
            this.renderParticlesGlow(matrix, immediate, this.worldParticles, 1000L, 1500L, deltaTime);
            this.renderParticlesGlow(matrix, immediate, this.frameParticles, 750L, 1000L, deltaTime);
        }
        this.renderParticles(matrix, immediate, this.targetParticles, 2000L, 2500L, deltaTime);
        this.renderParticles(matrix, immediate, this.totemParticles, 1000L, 1500L, deltaTime);
        this.renderParticles(matrix, immediate, this.worldParticles, 1000L, 1500L, deltaTime);
        this.renderParticles(matrix, immediate, this.frameParticles, 750L, 1000L, deltaTime);
        immediate.method_22993();
        matrix.method_22909();
    }

    private void renderParticles(class_4587 matrix, class_4597.class_4598 immediate, List<Particle> particles, long fadeInTime, long fadeOutTime, double deltaTime) {
        if (particles.isEmpty()) {
            return;
        }
        matrix.method_22903();
        for (Particle particle : particles) {
            float animValue;
            int alpha;
            particle.update(deltaTime);
            boolean notFinishedFadeIn = !particle.time().finished(fadeInTime);
            boolean finishedFadeOut = particle.time().finished(fadeOutTime);
            if (notFinishedFadeIn) {
                particle.animation().run(1.0, 0.5, Easings.LINEAR, true);
            } else if (finishedFadeOut) {
                particle.animation().run(0.0, 2.0, Easings.LINEAR, true);
            }
            if (particle.animation.isAlive()) {
                particle.animation.update();
            }
            if ((alpha = (int)((animValue = particle.animation.get()) * 255.0f)) <= 0) continue;
            int color = ColorUtil.replAlpha(particle.color(), alpha);
            class_243 v = particle.position();
            this.renderParticle(matrix, immediate, particle, (float)v.field_1352, (float)v.field_1351, (float)v.field_1350, particle.size * animValue, color, alpha);
        }
        matrix.method_22909();
    }

    private void renderParticle(class_4587 matrix, class_4597.class_4598 immediate, Particle particle, float x, float y, float z, float pos, int color, int alpha) {
        matrix.method_22903();
        Particles.setupOrientationMatrix(matrix, x, y, z);
        matrix.method_22907((Quaternionfc)Particles.mc.field_1773.method_19418().method_23767());
        if (this.rotations.getValue().booleanValue()) {
            matrix.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(particle.rotate()));
        }
        class_4587.class_4665 entry = matrix.method_23760();
        Matrix4f matrix4f = entry.method_23761();
        Matrix3f normalMatrix = entry.method_23762();
        class_4588 buffer = immediate.method_73477(ROMB_ESP.apply(particle.type().texture()));
        this.drawTexturedQuad(buffer, matrix4f, normalMatrix, -pos, -pos, pos * 2.0f, pos * 2.0f, color, alpha);
        if (particle.type == ParticleType.BLOOM && this.glows.getValue().booleanValue()) {
            this.drawTexturedQuad(buffer, matrix4f, normalMatrix, -pos / 2.0f, -pos / 2.0f, pos, pos, color, alpha);
        }
        if (this.glows.getValue().booleanValue()) {
            float bloomPos = pos * 3.0f;
            int bloomAlpha = (int)((float)alpha * this.glowsiz.getValue().floatValue() * 0.4f);
            if (bloomAlpha > 0) {
                if (cachedBloomLayer == null) {
                    cachedBloomLayer = ROMB_ESP.apply(BLOOM_TEXTURE);
                }
                class_4588 bloomBuffer = immediate.method_73477(cachedBloomLayer);
                this.drawTexturedQuad(bloomBuffer, matrix4f, normalMatrix, -bloomPos, -bloomPos, bloomPos * 2.0f, bloomPos * 2.0f, color, bloomAlpha);
            }
        }
        matrix.method_22909();
    }

    private void renderParticlesGlow(class_4587 matrix, class_4597.class_4598 immediate, List<Particle> particles, long fadeInTime, long fadeOutTime, double deltaTime) {
        if (particles.isEmpty()) {
            return;
        }
        matrix.method_22903();
        for (Particle particle : particles) {
            float animValue = particle.animation.get();
            int alpha = (int)(animValue * 255.0f * this.glowsiz.getValue().floatValue());
            if (alpha <= 0) continue;
            int color = ColorUtil.replAlpha(particle.color(), alpha);
            class_243 v = particle.position();
            this.renderParticle2(matrix, immediate, particle, (float)v.field_1352, (float)v.field_1351, (float)v.field_1350, particle.size * animValue, color, alpha);
        }
        matrix.method_22909();
    }

    private void renderParticle2(class_4587 matrix, class_4597.class_4598 immediate, Particle particle, float x, float y, float z, float pos, int color, int alpha) {
        matrix.method_22903();
        Particles.setupOrientationMatrix(matrix, x, y, z);
        matrix.method_22907((Quaternionfc)Particles.mc.field_1773.method_19418().method_23767());
        if (this.rotations.getValue().booleanValue()) {
            matrix.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(particle.rotate()));
        }
        class_4587.class_4665 entry = matrix.method_23760();
        Matrix4f matrix4f = entry.method_23761();
        Matrix3f normalMatrix = entry.method_23762();
        class_4588 buffer = immediate.method_73477(ROMB_ESP.apply(ParticleType.BLOOM.texture));
        this.drawTexturedQuad(buffer, matrix4f, normalMatrix, -pos * 3.0f, -pos * 3.0f, pos * 6.0f, pos * 6.0f, color, alpha);
        matrix.method_22909();
    }

    private void drawTexturedQuad(class_4588 buffer, Matrix4f matrix, Matrix3f normalMatrix, float x, float y, float width, float height, int color, int alpha) {
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        REUSABLE_NORMAL.set(0.0f, 0.0f, 1.0f);
        normalMatrix.transform(REUSABLE_NORMAL);
        REUSABLE_NORMAL.normalize();
        float x1 = x;
        float y1 = y;
        float x2 = x + width;
        float y2 = y + height;
        buffer.method_22918((Matrix4fc)matrix, x1, y1, 0.0f).method_1336(r, g, b, alpha).method_22913(0.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(Particles.REUSABLE_NORMAL.x, Particles.REUSABLE_NORMAL.y, Particles.REUSABLE_NORMAL.z);
        buffer.method_22918((Matrix4fc)matrix, x2, y1, 0.0f).method_1336(r, g, b, alpha).method_22913(1.0f, 1.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(Particles.REUSABLE_NORMAL.x, Particles.REUSABLE_NORMAL.y, Particles.REUSABLE_NORMAL.z);
        buffer.method_22918((Matrix4fc)matrix, x2, y2, 0.0f).method_1336(r, g, b, alpha).method_22913(1.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(Particles.REUSABLE_NORMAL.x, Particles.REUSABLE_NORMAL.y, Particles.REUSABLE_NORMAL.z);
        buffer.method_22918((Matrix4fc)matrix, x1, y2, 0.0f).method_1336(r, g, b, alpha).method_22913(0.0f, 0.0f).method_22922(class_4608.field_21444).method_60803(0xF000F0).method_22914(Particles.REUSABLE_NORMAL.x, Particles.REUSABLE_NORMAL.y, Particles.REUSABLE_NORMAL.z);
    }

    public static void setupOrientationMatrix(class_4587 matrix, float x, float y, float z) {
        Particles.setupOrientationMatrix(matrix, (double)x, (double)y, (double)z);
    }

    public static void setupOrientationMatrix(class_4587 matrix, double x, double y, double z) {
        class_243 renderPos = Particles.mc.method_1561().field_4686.method_71156();
        matrix.method_22904(x - renderPos.field_1352, y - renderPos.field_1351, z - renderPos.field_1350);
    }

    private void clear() {
        this.targetParticles.clear();
        this.frameParticles.clear();
        this.totemParticles.clear();
        this.worldParticles.clear();
        this.totemParticlesLeftToSpawn = 0;
        this.totalTotemParticlesToSpawn = 0;
        this.totemTargetEntity = null;
        this.totemSpawnStartTime = 0L;
    }

    public static boolean isBlockSolid(double x, double y, double z) {
        if (Particles.mc.field_1687 == null) {
            return false;
        }
        class_2338 pos = class_2338.method_49637((double)x, (double)y, (double)z);
        return Particles.mc.field_1687.method_8320(pos).method_26234((class_1922)Particles.mc.field_1687, pos);
    }

    private /* synthetic */ void lambda$previewSpawn$8(class_1297 target) {
        this.spawnAttackBurst(target);
    }

    private /* synthetic */ void lambda$previewSpawn$7(class_1297 target) {
        this.spawnThrowBurst(target);
    }

    private /* synthetic */ void lambda$previewSpawn$6(class_1297 target) {
        this.spawnTotemBurst(target, this.onTtSizs.getValue().intValue());
    }

    private /* synthetic */ void lambda$previewSpawn$5(class_1297 target) {
        this.spawnAttackBurst(target);
    }

    @Environment(value=EnvType.CLIENT)
    private static enum WeatherParticleStyle {
        MIST("\u0422\u0443\u043c\u0430\u043d", ParticleType.BLOOM, 12109529, 1.8f),
        STORM("\u0413\u0440\u043e\u0437\u0430", ParticleType.CIRCLE, 6192127, 0.75f),
        ASH("\u041f\u0435\u043f\u0435\u043b", ParticleType.SNOOW, 9208184, 1.0f),
        SANDSTORM("\u041f\u0435\u0441\u0447\u0430\u043d\u0430\u044f \u0431\u0443\u0440\u044f", ParticleType.SPARKLE, 14263387, 0.75f),
        POLLEN("\u041f\u044b\u043b\u044c\u0446\u0430", ParticleType.STAR, 16767334, 0.8f),
        FIREFLY("\u0421\u0432\u0435\u0442\u043b\u044f\u0447\u043a\u0438", ParticleType.BLOOM, 14221163, 1.0f),
        AURORA("\u0421\u0438\u044f\u043d\u0438\u0435", ParticleType.GEMINI, 7334096, 1.7f),
        FROST("\u041c\u043e\u0440\u043e\u0437", ParticleType.SNOOW, 10213631, 1.2f),
        MONSOON("\u041c\u0443\u0441\u0441\u043e\u043d", ParticleType.CIRCLE, 4878292, 0.7f),
        TOXIC("\u0422\u043e\u043a\u0441\u0438\u0447\u043d\u0430\u044f \u043c\u0433\u043b\u0430", ParticleType.BLOOM, 9165116, 1.5f),
        ICE_CALM("\u041b\u0435\u0434\u044f\u043d\u043e\u0439 \u0448\u0442\u0438\u043b\u044c", ParticleType.SIMS, 12120319, 1.2f),
        ASH_RAIN("\u041f\u0435\u043f\u0435\u043b\u044c\u043d\u044b\u0439 \u0434\u043e\u0436\u0434\u044c", ParticleType.CIRCLE, 8028044, 0.8f),
        HONEY_SUNSET("\u041c\u0435\u0434\u043e\u0432\u044b\u0439 \u0437\u0430\u043a\u0430\u0442", ParticleType.BLOOM, 16757581, 1.4f),
        LUNAR_FROST("\u041b\u0443\u043d\u043d\u044b\u0439 \u0438\u043d\u0435\u0439", ParticleType.SNOOW, 13097215, 1.1f),
        MUSHROOM_RAIN("\u0413\u0440\u0438\u0431\u043d\u043e\u0439 \u0434\u043e\u0436\u0434\u044c", ParticleType.CIRCLE, 14256746, 0.8f),
        ACID_FOG("\u041a\u0438\u0441\u043b\u043e\u0442\u043d\u044b\u0439 \u0442\u0443\u043c\u0430\u043d", ParticleType.BLOOM, 11075388, 1.7f),
        FIRE_WHIRL("\u041e\u0433\u043d\u0435\u043d\u043d\u044b\u0439 \u0432\u0438\u0445\u0440\u044c", ParticleType.STAR, 16738842, 1.2f),
        SNOW_MIST("\u0421\u043d\u0435\u0436\u043d\u0430\u044f \u043c\u0433\u043b\u0430", ParticleType.BLOOM, 0xE1EEFF, 1.8f),
        FLOWER_STORM("\u0426\u0432\u0435\u0442\u043e\u0447\u043d\u044b\u0439 \u0448\u0442\u043e\u0440\u043c", ParticleType.SAKURA, 16748241, 1.2f),
        SHADOW_STORM("\u0413\u0440\u043e\u0437\u0430 \u0442\u0435\u043d\u0435\u0439", ParticleType.TRIANGLE, 7032739, 1.0f),
        GOLD_DUST("\u0417\u043e\u043b\u043e\u0442\u0430\u044f \u043f\u044b\u043b\u044c", ParticleType.SPARKLE, 16765514, 0.9f),
        SILVER_RAIN("\u0421\u0435\u0440\u0435\u0431\u0440\u044f\u043d\u044b\u0439 \u0434\u043e\u0436\u0434\u044c", ParticleType.CIRCLE, 14477042, 0.8f),
        CRIMSON_ASH("\u0411\u0430\u0433\u0440\u043e\u0432\u044b\u0439 \u043f\u0435\u043f\u0435\u043b", ParticleType.SNOOW, 12729147, 1.0f),
        EMERALD_HAZE("\u0418\u0437\u0443\u043c\u0440\u0443\u0434\u043d\u0430\u044f \u0434\u044b\u043c\u043a\u0430", ParticleType.BLOOM, 3529099, 1.7f),
        LAVENDER_STORM("\u041b\u0430\u0432\u0430\u043d\u0434\u043e\u0432\u0430\u044f \u0431\u0443\u0440\u044f", ParticleType.STAR, 12033279, 1.0f),
        STAR_DUST("\u0417\u0432\u0451\u0437\u0434\u043d\u0430\u044f \u043f\u044b\u043b\u044c", ParticleType.STAR, 13625855, 0.8f),
        ICE_NEEDLES("\u041b\u0435\u0434\u044f\u043d\u044b\u0435 \u0438\u0433\u043b\u044b", ParticleType.TRIANGLE, 10479871, 0.9f),
        TROPICAL_RAIN("\u0422\u0440\u043e\u043f\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u043b\u0438\u0432\u0435\u043d\u044c", ParticleType.CIRCLE, 3123455, 0.7f),
        VOLCANIC_ASH("\u0412\u0443\u043b\u043a\u0430\u043d\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u043f\u0435\u043f\u0435\u043b", ParticleType.SNOOW, 11749930, 1.1f),
        NORTHERN_LIGHTS("\u0421\u0435\u0432\u0435\u0440\u043d\u043e\u0435 \u0441\u0438\u044f\u043d\u0438\u0435", ParticleType.GEMINI, 4194257, 1.8f),
        DESERT_MIRAGE("\u041c\u0438\u0440\u0430\u0436 \u043f\u0443\u0441\u0442\u044b\u043d\u0438", ParticleType.BLOOM, 16766874, 1.6f),
        FOGGY_NIGHT("\u0422\u0443\u043c\u0430\u043d\u043d\u0430\u044f \u043d\u043e\u0447\u044c", ParticleType.BLOOM, 8491448, 1.8f),
        DAWN_STORM("\u0420\u0430\u0441\u0441\u0432\u0435\u0442\u043d\u044b\u0439 \u0448\u0442\u043e\u0440\u043c", ParticleType.CIRCLE, 16752250, 0.8f),
        CRYSTAL_SNOW("\u0425\u0440\u0443\u0441\u0442\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u043d\u0435\u0433", ParticleType.SIMS, 0xCFF6FF, 1.2f),
        BLOOD_RAIN("\u041a\u0440\u043e\u0432\u0430\u0432\u044b\u0439 \u0434\u043e\u0436\u0434\u044c", ParticleType.CIRCLE, 11730990, 0.8f),
        GHOST_FOG("\u041f\u0440\u0438\u0437\u0440\u0430\u0447\u043d\u044b\u0439 \u0442\u0443\u043c\u0430\u043d", ParticleType.BLOOM, 12447462, 1.9f),
        SUN_RAIN("\u0421\u043e\u043b\u043d\u0435\u0447\u043d\u044b\u0439 \u043b\u0438\u0432\u0435\u043d\u044c", ParticleType.CIRCLE, 16769126, 0.8f),
        SKY_FIRE("\u041d\u0435\u0431\u0435\u0441\u043d\u044b\u0439 \u043e\u0433\u043e\u043d\u044c", ParticleType.STAR, 16754253, 1.2f),
        MOON_DUST("\u041b\u0443\u043d\u043d\u0430\u044f \u043f\u044b\u043b\u044c", ParticleType.STAR, 14542847, 0.9f),
        THUNDER_FRONT("\u0413\u0440\u043e\u0437\u043e\u0432\u043e\u0439 \u0444\u0440\u043e\u043d\u0442", ParticleType.STAR, 5006290, 0.9f),
        FROST_WIND("\u041c\u043e\u0440\u043e\u0437\u043d\u044b\u0439 \u0432\u0435\u0442\u0435\u0440", ParticleType.SPARKLE, 14087423, 0.9f),
        LEAF_FALL("\u041e\u0441\u0435\u043d\u043d\u0438\u0439 \u043b\u0438\u0441\u0442\u043e\u043f\u0430\u0434", ParticleType.SAKURA, 15239730, 1.2f),
        SPRING_RAIN("\u0412\u0435\u0441\u0435\u043d\u043d\u0438\u0439 \u0434\u043e\u0436\u0434\u044c", ParticleType.CIRCLE, 10477690, 0.8f),
        SUMMER_STORM("\u041b\u0435\u0442\u043d\u044f\u044f \u0433\u0440\u043e\u0437\u0430", ParticleType.CIRCLE, 3984383, 0.8f),
        WINTER_BLIZZARD("\u0417\u0438\u043c\u043d\u044f\u044f \u0432\u044c\u044e\u0433\u0430", ParticleType.SNOOW, 15398143, 1.2f),
        BLACK_ICE("\u0427\u0451\u0440\u043d\u044b\u0439 \u043b\u0451\u0434", ParticleType.SIMS, 3037803, 1.1f),
        WHITE_OUT("\u0411\u0435\u043b\u0430\u044f \u043c\u0433\u043b\u0430", ParticleType.BLOOM, 0xFFFFFF, 1.9f),
        PINK_SUNSET("\u0420\u043e\u0437\u043e\u0432\u044b\u0439 \u0437\u0430\u043a\u0430\u0442", ParticleType.BLOOM, 0xFF8FB8, 1.4f),
        TURQUOISE_HAZE("\u0411\u0438\u0440\u044e\u0437\u043e\u0432\u0430\u044f \u0434\u044b\u043c\u043a\u0430", ParticleType.BLOOM, 5103824, 1.7f),
        AMBER_RAIN("\u042f\u043d\u0442\u0430\u0440\u043d\u044b\u0439 \u0434\u043e\u0436\u0434\u044c", ParticleType.CIRCLE, 0xFFB000, 0.8f),
        COPPER_FOG("\u041c\u0435\u0434\u043d\u044b\u0439 \u0442\u0443\u043c\u0430\u043d", ParticleType.BLOOM, 13204037, 1.7f),
        EMERALD_RAIN("\u0418\u0437\u0443\u043c\u0440\u0443\u0434\u043d\u044b\u0439 \u0434\u043e\u0436\u0434\u044c", ParticleType.CIRCLE, 3066993, 0.8f),
        SAPPHIRE_STORM("\u0421\u0430\u043f\u0444\u0438\u0440\u043e\u0432\u044b\u0439 \u0448\u0442\u043e\u0440\u043c", ParticleType.STAR, 3042303, 0.9f),
        AMETHYST_STORM("\u0410\u043c\u0435\u0442\u0438\u0441\u0442\u043e\u0432\u0430\u044f \u0431\u0443\u0440\u044f", ParticleType.STAR, 10771711, 0.9f),
        FIRE_RAIN("\u041e\u0433\u043d\u0435\u043d\u043d\u044b\u0439 \u0434\u043e\u0436\u0434\u044c", ParticleType.CIRCLE, 16731418, 0.8f),
        ASH_MOON("\u041f\u0435\u043f\u0435\u043b\u044c\u043d\u0430\u044f \u043b\u0443\u043d\u0430", ParticleType.BLOOM, 10984368, 1.5f),
        CRYSTAL_DUST("\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u044c\u043d\u0430\u044f \u043f\u044b\u043b\u044c", ParticleType.SPARKLE, 0xDDF9FF, 0.9f),
        THUNDER_STORM("\u0413\u0440\u043e\u043c\u043e\u0432\u043e\u0439 \u0448\u0442\u043e\u0440\u043c", ParticleType.STAR, 8366335, 1.0f),
        QUIET_SNOW("\u0422\u0438\u0445\u0438\u0439 \u0441\u043d\u0435\u0433", ParticleType.SNOOW, 0xF2F7FF, 1.1f),
        LAST_LEAF("\u041f\u043e\u0441\u043b\u0435\u0434\u043d\u0438\u0439 \u043b\u0438\u0441\u0442", ParticleType.SAKURA, 11887901, 1.2f);

        private final String displayName;
        private final ParticleType type;
        private final int color;
        private final float sizeScale;

        private WeatherParticleStyle(String displayName, ParticleType type, int color, float sizeScale) {
            this.displayName = displayName;
            this.type = type;
            this.color = 0xFF000000 | color;
            this.sizeScale = sizeScale;
        }

        static WeatherParticleStyle byName(String name) {
            for (WeatherParticleStyle style : WeatherParticleStyle.values()) {
                if (!style.displayName.equalsIgnoreCase(name)) continue;
                return style;
            }
            return null;
        }

        @Generated
        public String displayName() {
            return this.displayName;
        }

        @Generated
        public ParticleType type() {
            return this.type;
        }

        @Generated
        public int color() {
            return this.color;
        }

        @Generated
        public float sizeScale() {
            return this.sizeScale;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum ParticleType {
        HEART("heart", true),
        STAR("star", true),
        BLOOM("glow", true),
        CIRCLE("circle", true),
        STAR_NEW("client", true),
        SNOOW("snowflake", true),
        DOLLAR("dollar", true),
        PUMPKIN("pumpkin", true),
        TRIANGLE("triangle", true),
        SAKURA("sakura", true),
        GEMINI("genshin", true),
        SIMS("rhombus", true),
        SPARKLE("sparkle", true);

        private final class_2960 texture;
        private final boolean rotatable;

        private ParticleType(String name, boolean rotatable) {
            this.texture = class_2960.method_60655((String)"decide", (String)("textures/particles/" + name + ".png"));
            this.rotatable = rotatable;
        }

        @Generated
        public class_2960 texture() {
            return this.texture;
        }

        @Generated
        public boolean rotatable() {
            return this.rotatable;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Particle {
        private final ParticleType type;
        private class_243 position;
        private class_243 velocity;
        private final int index;
        private final int color;
        private final float size;
        private static final double BASE_VELOCITY = 0.05;
        private final double speedMultiplier;
        private final boolean isWorldParticle;
        private final boolean isTotem;
        private final StopWatch time = new StopWatch();
        private final Animation animation = new Animation();
        private float rotate = 0.0f;

        public Particle(ParticleType type, class_243 position, class_243 velocity, int index, int rotate, int color, float size, double speedMultiplier, boolean isWorldParticle, boolean isTotem) {
            this.type = type;
            this.position = position;
            this.velocity = isTotem ? velocity.method_1021(0.08) : velocity.method_1021(0.05);
            this.index = index;
            this.color = color;
            this.rotate = rotate;
            this.size = size;
            this.speedMultiplier = speedMultiplier;
            this.isWorldParticle = isWorldParticle;
            this.isTotem = isTotem;
            this.time.reset();
        }

        public void update(double deltaTime) {
            double ticks = deltaTime * 120.0;
            if (ticks <= 0.0) {
                return;
            }
            double bounciness = 0.85;
            double gravity = this.isWorldParticle ? 1.0E-4 : 5.0E-4;
            double nextX = this.position.field_1352 + this.velocity.field_1352 * ticks;
            double nextY = this.position.field_1351 + this.velocity.field_1351 * ticks;
            double nextZ = this.position.field_1350 + this.velocity.field_1350 * ticks;
            if (Particles.isBlockSolid(nextX, this.position.field_1351, this.position.field_1350)) {
                this.velocity = new class_243(-this.velocity.field_1352 * bounciness, this.velocity.field_1351, this.velocity.field_1350);
            } else {
                this.position = new class_243(nextX, this.position.field_1351, this.position.field_1350);
            }
            if (Particles.isBlockSolid(this.position.field_1352, nextY, this.position.field_1350)) {
                this.velocity = new class_243(this.velocity.field_1352, -this.velocity.field_1351 * bounciness, this.velocity.field_1350);
            } else {
                this.position = new class_243(this.position.field_1352, nextY, this.position.field_1350);
            }
            if (Particles.isBlockSolid(this.position.field_1352, this.position.field_1351, nextZ)) {
                this.velocity = new class_243(this.velocity.field_1352, this.velocity.field_1351, -this.velocity.field_1350 * bounciness);
            } else {
                this.position = new class_243(this.position.field_1352, this.position.field_1351, nextZ);
            }
            this.velocity = this.velocity.method_1023(0.0, gravity * ticks, 0.0);
            this.rotate = (float)((double)this.rotate + 2.0 * ticks);
        }

        @Generated
        public ParticleType type() {
            return this.type;
        }

        @Generated
        public class_243 position() {
            return this.position;
        }

        @Generated
        public class_243 velocity() {
            return this.velocity;
        }

        @Generated
        public int index() {
            return this.index;
        }

        @Generated
        public int color() {
            return this.color;
        }

        @Generated
        public float size() {
            return this.size;
        }

        @Generated
        public double speedMultiplier() {
            return this.speedMultiplier;
        }

        @Generated
        public boolean isWorldParticle() {
            return this.isWorldParticle;
        }

        @Generated
        public boolean isTotem() {
            return this.isTotem;
        }

        @Generated
        public StopWatch time() {
            return this.time;
        }

        @Generated
        public Animation animation() {
            return this.animation;
        }

        @Generated
        public float rotate() {
            return this.rotate;
        }
    }
}

