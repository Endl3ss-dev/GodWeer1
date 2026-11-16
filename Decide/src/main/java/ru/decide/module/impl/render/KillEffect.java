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
 *  net.minecraft.class_742
 *  net.minecraft.class_745
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.decide.module.impl.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import net.minecraft.class_742;
import net.minecraft.class_745;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.decide.manager.event_impl.AttackEvent;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.manager.event_impl.EventPacket;
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
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.other.Instance;
import ru.decide.utils.render.KimikoDissolve;
import ru.decide.utils.render.killeffect.DeathParticles;
import ru.decide.utils.render.killeffect.EffectCatalog;
import ru.decide.utils.render.killeffect.EffectManager;
import ru.decide.utils.render.killeffect.EffectRenderer;
import ru.decide.utils.render.killeffect.FragEffect;
import ru.decide.utils.render.killeffect.LightningFlash;
import ru.decide.utils.render.killeffect.ShardsEffect;
import ru.decide.utils.render.killeffect.SoulEffect;
import ru.decide.utils.render.killeffect.swing.SwingCoinsEffect;
import ru.decide.utils.render.killeffect.swing.SwingGraveEffect;
import ru.decide.utils.render.killeffect.swing.SwingKillEffects;
import ru.decide.utils.render.killeffect.swing.SwingLightningEffect;
import ru.decide.utils.render.killeffect.swing.SwingShatterEffect;

@ModuleInfo(name="Kill Effect", desc="Kill effects on eliminated targets", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class KillEffect
extends Module
implements ModulePreview {
    public ButtonSetting previewButton = PreviewSettings.button(this);
    private static final Map<String, String> BASIC_NAMES = new LinkedHashMap<String, String>();
    private static final Map<String, String> DISPLAY_NAMES = new LinkedHashMap<String, String>();
    private static final String[] BASIC_VALUES;
    private static final String[] MODEL_VALUES;
    public ModeSetting effectGroup = new ModeSetting((Module)this, "\u0413\u0440\u0443\u043f\u043f\u0430", "\u042d\u0444\u0444\u0435\u043a\u0442\u044b", "3D \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438").onAction(this::clearState);
    public ModeSetting effectType = new ModeSetting((Module)this, "\u042d\u0444\u0444\u0435\u043a\u0442", BASIC_VALUES).setVisible(() -> this.effectGroup.is("\u042d\u0444\u0444\u0435\u043a\u0442\u044b"));
    public ModeSetting modelType = new ModeSetting((Module)this, "3D \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u044f", MODEL_VALUES).setVisible(() -> this.effectGroup.is("3D \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438"));
    public SliderSetting bbScale = new SliderSetting(this, "BB Scale", 1.0f, 0.2f, 3.0f, 0.1f).setVisible(() -> this.effectGroup.is("3D \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438"));
    public ModeSetting typeColor = new ModeSetting((Module)this, "Color mode", "Theme", "Custom").setVisible(this::usesColorSetting);
    public ColorSetting tintColor = new ColorSetting((Module)this, "Color", -16711681).setVisible(() -> this.usesColorSetting() && this.typeColor.is("Custom"));
    public BooleanSetting soulMobs = new BooleanSetting((Module)this, "\u041c\u043e\u0431\u044b", false).setVisible(this::isSoulOrShards);
    public BooleanSetting soulRotate360 = new BooleanSetting((Module)this, "\u041f\u043e\u0432\u043e\u0440\u043e\u0442 \u043d\u0430 360", false).setVisible(this::isSoul);
    public SliderSetting soulRise = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 \u043f\u043e\u0434\u044a\u0451\u043c\u0430", 5.2f, 1.0f, 10.0f, 0.2f).setVisible(this::isSoul);
    public SliderSetting soulTransparency = new SliderSetting(this, "\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 0.6f, 0.1f, 1.0f, 0.05f).setVisible(this::isSoul);
    public SliderSetting soulBrightness = new SliderSetting(this, "\u0421\u0432\u0435\u0442\u043b\u043e\u0442\u0430", 0.35f, 0.0f, 1.0f, 0.05f).setVisible(this::isSoul);
    public SliderSetting soulDuration = new SliderSetting(this, "\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c", 3.0f, 1.0f, 6.0f, 0.1f).setVisible(this::isSoulOrShards);
    public SliderSetting shardDensity = new SliderSetting(this, "\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c \u043e\u0441\u043a\u043e\u043b\u043a\u043e\u0432", 1.0f, 0.3f, 2.0f, 0.1f).setVisible(this::isShards);
    public SliderSetting duration = new SliderSetting(this, "Duration", 2.2f, 1.0f, 5.0f, 0.1f);
    public SliderSetting beamHeight = new SliderSetting(this, "Beam height", 5.2f, 1.5f, 12.0f, 0.5f).setVisible(this::isBeam);
    public SliderSetting beamWidth = new SliderSetting(this, "Beam width", 0.42f, 0.08f, 1.2f, 0.02f).setVisible(this::isBeam);
    public SliderSetting dissolveCount = new SliderSetting(this, "Particles", 1400.0f, 100.0f, 4000.0f, 50.0f).setVisible(this::isKimikoDissolve);
    public SliderSetting dissolveSize = new SliderSetting(this, "Particle size", 0.12f, 0.045f, 0.22f, 0.005f).setVisible(this::isKimikoDissolve);
    public SliderSetting dissolveHold = new SliderSetting(this, "Hold", 550.0f, 0.0f, 2000.0f, 25.0f).setVisible(this::isKimikoDissolve);
    public SliderSetting dissolveFade = new SliderSetting(this, "Fade", 1300.0f, 250.0f, 2400.0f, 25.0f).setVisible(this::isKimikoDissolve);
    public SliderSetting dissolveRise = new SliderSetting(this, "Rise", 1.4f, 0.35f, 3.5f, 0.05f).setVisible(this::isKimikoDissolve);
    public SliderSetting dissolveChaos = new SliderSetting(this, "Chaos", 1.0f, 0.0f, 2.5f, 0.05f).setVisible(this::isKimikoDissolve);
    public BooleanSetting lightningThemeColor = new BooleanSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", true).setVisible(this::isDestraLightning);
    public ColorSetting lightningColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u043c\u043e\u043b\u043d\u0438\u0438", -1).setVisible(() -> this.isDestraLightning() && this.lightningThemeColor.getValue() == false);
    public SliderSetting particleCount = new SliderSetting(this, "\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0447\u0430\u0441\u0442\u0438\u0446", 95.0f, 20.0f, 220.0f, 1.0f).setVisible(this::isDestraParticles);
    public SliderSetting particleSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u0447\u0430\u0441\u0442\u0438\u0446", 0.12f, 0.03f, 0.35f, 0.005f).setVisible(this::isDestraParticles);
    public SliderSetting particleLife = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f \u0436\u0438\u0437\u043d\u0438 \u0447\u0430\u0441\u0442\u0438\u0446", 1.6f, 0.3f, 5.0f, 0.1f).setVisible(this::isDestraParticles);
    public BooleanSetting particleGravity = new BooleanSetting((Module)this, "\u0413\u0440\u0430\u0432\u0438\u0442\u0430\u0446\u0438\u044f", true).setVisible(this::isDestraParticles);
    public SliderSetting fragDuration = new SliderSetting(this, "\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u0444\u0440\u0430\u0433\u0430", 1.15f, 0.45f, 2.5f, 0.05f).setVisible(this::isDestraFrag);
    public BooleanSetting swingLightningPlayersOnly = ((BooleanSetting)new BooleanSetting((Module)this, "\u0422\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0438", false).setVisible(this::isSwingLightning)).ungated();
    public BooleanSetting swingLightningSound = ((BooleanSetting)new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a", true).setVisible(this::isSwingLightning)).ungated();
    public SliderSetting swingLightningVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 0.8f, 0.1f, 1.0f, 0.05f).setVisible(() -> this.isSwingLightning() && this.swingLightningSound.getValue() != false);
    public BooleanSetting swingShatterPlayersOnly = ((BooleanSetting)new BooleanSetting((Module)this, "\u0422\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0438", false).setVisible(this::isSwingShatter)).ungated();
    public BooleanSetting swingShatterSound = ((BooleanSetting)new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a", true).setVisible(this::isSwingShatter)).ungated();
    public SliderSetting swingShatterVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 0.8f, 0.1f, 1.0f, 0.05f).setVisible(() -> this.isSwingShatter() && this.swingShatterSound.getValue() != false);
    public BooleanSetting swingGravePlayersOnly = ((BooleanSetting)new BooleanSetting((Module)this, "\u0422\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0438", false).setVisible(this::isSwingGrave)).ungated();
    public BooleanSetting swingGraveSound = ((BooleanSetting)new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a", true).setVisible(this::isSwingGrave)).ungated();
    public SliderSetting swingGraveVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 0.8f, 0.1f, 1.0f, 0.05f).setVisible(() -> this.isSwingGrave() && this.swingGraveSound.getValue() != false);
    public BooleanSetting swingCoinsPlayersOnly = ((BooleanSetting)new BooleanSetting((Module)this, "\u0422\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0438", false).setVisible(this::isSwingCoins)).ungated();
    public BooleanSetting swingCoinsSound = ((BooleanSetting)new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a", true).setVisible(this::isSwingCoins)).ungated();
    public SliderSetting swingCoinsVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 0.8f, 0.1f, 1.0f, 0.05f).setVisible(() -> this.isSwingCoins() && this.swingCoinsSound.getValue() != false);
    private final PreviewSettings previewSettings;
    private static final byte DEATH_STATUS = 3;
    private static final long KILL_WINDOW_MS = 6500L;
    private static final long DUPLICATE_WINDOW_MS = 750L;
    private static boolean catalogLoaded;
    private final List<KillBeam> beams;
    private final class_9799 allocator;
    private final EffectManager effectManager;
    private class_1309 lastTarget;
    private KillPoint lastPoint;
    private int lastTargetId;
    private long lastAttackTime;
    private int lastSpawnedId;
    private long lastSpawnTime;
    private static final RenderPipeline BEAM_PIPELINE;
    private static final class_1921 BEAM_LAYER;

    public KillEffect() {
        this.swingLightningPlayersOnly.configKey("swingLightningPlayersOnly");
        this.swingLightningSound.configKey("swingLightningSound");
        this.swingLightningVolume.configKey("swingLightningVolume");
        this.swingShatterPlayersOnly.configKey("swingShatterPlayersOnly");
        this.swingShatterSound.configKey("swingShatterSound");
        this.swingShatterVolume.configKey("swingShatterVolume");
        this.swingGravePlayersOnly.configKey("swingGravePlayersOnly");
        this.swingGraveSound.configKey("swingGraveSound");
        this.swingGraveVolume.configKey("swingGraveVolume");
        this.swingCoinsPlayersOnly.configKey("swingCoinsPlayersOnly");
        this.swingCoinsSound.configKey("swingCoinsSound");
        this.swingCoinsVolume.configKey("swingCoinsVolume");
        this.previewSettings = PreviewSettings.of(this, 4.0f, 0.0f, 2.5f);
        this.beams = new ArrayList<KillBeam>();
        this.allocator = new class_9799(32768);
        this.effectManager = new EffectManager();
        this.lastTargetId = -1;
        this.lastSpawnedId = -1;
    }

    private boolean isBeam() {
        return "Beam".equals(this.getEffectId());
    }

    private boolean isSoul() {
        return "soul".equals(this.getEffectId());
    }

    private boolean isShards() {
        return "shards".equals(this.getEffectId());
    }

    private boolean isSoulOrShards() {
        return this.isSoul() || this.isShards();
    }

    private boolean usesColorSetting() {
        return this.isBeam() || this.isSoulOrShards() || this.isDestraStyle() || this.isSwingStyle();
    }

    private boolean isKimikoDissolve() {
        return "kimiko_dissolve".equals(this.getEffectId());
    }

    private boolean isDestraLightning() {
        return "destra_lightning".equals(this.getEffectId());
    }

    private boolean isDestraParticles() {
        return "destra_particles".equals(this.getEffectId());
    }

    private boolean isDestraFrag() {
        return "destra_frag".equals(this.getEffectId());
    }

    private boolean isSwingLightning() {
        return "swing_lightning".equals(this.getEffectId());
    }

    private boolean isSwingShatter() {
        return "swing_shatter".equals(this.getEffectId());
    }

    private boolean isSwingGrave() {
        return "swing_grave".equals(this.getEffectId());
    }

    private boolean isSwingCoins() {
        return "swing_coins".equals(this.getEffectId());
    }

    private boolean isSwingStyle() {
        return this.isSwingLightning() || this.isSwingShatter() || this.isSwingGrave() || this.isSwingCoins();
    }

    private boolean swingPlayersOnly() {
        if (this.isSwingShatter()) {
            return this.swingShatterPlayersOnly.getValue();
        }
        if (this.isSwingGrave()) {
            return this.swingGravePlayersOnly.getValue();
        }
        if (this.isSwingCoins()) {
            return this.swingCoinsPlayersOnly.getValue();
        }
        return this.swingLightningPlayersOnly.getValue();
    }

    private boolean swingSoundEnabled() {
        if (this.isSwingShatter()) {
            return this.swingShatterSound.getValue();
        }
        if (this.isSwingGrave()) {
            return this.swingGraveSound.getValue();
        }
        if (this.isSwingCoins()) {
            return this.swingCoinsSound.getValue();
        }
        return this.swingLightningSound.getValue();
    }

    private float swingVolume() {
        if (this.isSwingShatter()) {
            return this.swingShatterVolume.getValue().floatValue();
        }
        if (this.isSwingGrave()) {
            return this.swingGraveVolume.getValue().floatValue();
        }
        if (this.isSwingCoins()) {
            return this.swingCoinsVolume.getValue().floatValue();
        }
        return this.swingLightningVolume.getValue().floatValue();
    }

    private boolean isDestraStyle() {
        return this.isDestraLightning() || this.isDestraParticles() || this.isDestraFrag();
    }

    private String getEffectId() {
        boolean model = this.effectGroup.is("3D \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438");
        ModeSetting setting = model ? this.modelType : this.effectType;
        String display = setting.getValue();
        if (!setting.values.contains(display)) {
            setting.set(setting.values.get(0));
            display = setting.getValue();
        }
        Map<String, String> names = model ? DISPLAY_NAMES : BASIC_NAMES;
        for (Map.Entry<String, String> e : names.entrySet()) {
            if (!e.getValue().equals(display)) continue;
            return e.getKey();
        }
        return model ? DISPLAY_NAMES.keySet().iterator().next() : "Beam";
    }

    public int getColor() {
        if (this.typeColor.is("Theme")) {
            return ColorUtil.getClientColor1(1);
        }
        return this.tintColor.getValue();
    }

    @Override
    protected void onEnable() {
        if (!catalogLoaded) {
            try {
                EffectCatalog.registerAll();
                catalogLoaded = true;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    protected void onDisable() {
        this.clearState();
    }

    @EventHandler
    public void onDisplay(EventDisplay e) {
        if (!this.isEnabled() || KillEffect.mc.field_1724 == null || KillEffect.mc.field_1687 == null) {
            return;
        }
        if (!this.isDestraFrag()) {
            return;
        }
        FragEffect.get().render(e.getDrawContext(), this.fragDuration.getValue().floatValue());
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        this.clearState();
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
        if (this.isDestraFrag()) {
            FragEffect.get().trigger(this.fragDuration.getValue().floatValue());
        } else if (this.isDestraLightning()) {
            LightningFlash.get().spawn((class_1309)(target != null ? target : KillEffect.mc.field_1724), this.lightningEffectColor());
        } else if (this.isDestraParticles()) {
            DeathParticles.get().spawn((class_1309)(target != null ? target : KillEffect.mc.field_1724), this.particleSettings());
        } else if (this.isKimikoDissolve()) {
            class_745 source;
            Object object = source = target != null ? target : KillEffect.mc.field_1724;
            if (source != null) {
                KimikoDissolve.get().queue((class_1309)source, this.partialTick(), this.dissolveSettings());
            }
        } else if (this.isSoul()) {
            SoulEffect.get().queue((class_1309)(target != null ? target : KillEffect.mc.field_1724));
        } else if (this.isShards()) {
            ShardsEffect.get().spawn(target != null ? target.method_73189() : ctx.anchor(), this.shardsSettings());
        } else if (this.isSwingStyle()) {
            this.triggerSwing((class_1309)(target != null ? target : KillEffect.mc.field_1724), false);
        } else if (!this.isBeam()) {
            class_243 pos = target != null ? target.method_73189() : ctx.anchor();
            this.spawnBBEffect(pos, (class_1309)(target != null ? target : KillEffect.mc.field_1724));
        } else {
            KillPoint p = target != null ? this.point((class_1309)target) : new KillPoint(ctx.anchor(), 0.65f);
            this.beams.add(new KillBeam(p));
        }
    }

    @Override
    public void previewStop() {
        this.clearState();
    }

    @EventHandler
    public void onAttack(AttackEvent e) {
        class_1309 living;
        class_1297 class_12972 = e.getTarget();
        if (!(class_12972 instanceof class_1309) || (living = (class_1309)class_12972) == KillEffect.mc.field_1724) {
            return;
        }
        this.lastTarget = living;
        this.lastTargetId = living.method_5628();
        this.lastAttackTime = System.currentTimeMillis();
        this.lastPoint = this.point(living);
    }

    @EventHandler
    public void onPacket(EventPacket e) {
        class_2596<?> class_25962 = e.getPacket();
        if (!(class_25962 instanceof class_2663)) {
            return;
        }
        class_2663 packet = (class_2663)class_25962;
        if (packet.method_11470() != 3 || KillEffect.mc.field_1687 == null) {
            return;
        }
        class_1297 entity = packet.method_11469((class_1937)KillEffect.mc.field_1687);
        if (!(entity instanceof class_1309)) {
            return;
        }
        class_1309 living = (class_1309)entity;
        this.trySpawn(living, false);
    }

    @EventHandler
    public void onTick(EventTick e) {
        SwingKillEffects.tickAll();
        if (this.lastTarget == null) {
            return;
        }
        if (System.currentTimeMillis() - this.lastAttackTime > 6500L) {
            this.lastTarget = null;
            this.lastPoint = null;
            return;
        }
        if (!this.lastTarget.method_5805() || this.lastTarget.method_31481() || this.lastTarget.method_6032() <= 0.0f) {
            this.trySpawn(this.lastTarget, true);
        }
    }

    private void trySpawn(class_1309 entity, boolean allowCachedPoint) {
        long now = System.currentTimeMillis();
        if (entity.method_5628() != this.lastTargetId || now - this.lastAttackTime > 6500L) {
            return;
        }
        if (this.lastSpawnedId == entity.method_5628() && now - this.lastSpawnTime < 750L) {
            return;
        }
        if (this.isSoulOrShards() && !this.soulMobs.getValue().booleanValue() && !(entity instanceof class_742)) {
            return;
        }
        if (this.isSwingStyle() && this.swingPlayersOnly() && !(entity instanceof class_742)) {
            return;
        }
        if (this.isBeam()) {
            KillPoint p = allowCachedPoint && this.lastPoint != null ? this.lastPoint : this.point(entity);
            this.beams.add(new KillBeam(p));
        } else if (this.isDestraLightning()) {
            LightningFlash.get().spawn(entity, this.lightningEffectColor());
        } else if (this.isDestraParticles()) {
            DeathParticles.get().spawn(entity, this.particleSettings());
        } else if (this.isDestraFrag()) {
            FragEffect.get().trigger(this.fragDuration.getValue().floatValue());
        } else if (this.isKimikoDissolve()) {
            KimikoDissolve.get().queue(entity, this.partialTick(), this.dissolveSettings());
        } else if (this.isSoul()) {
            SoulEffect.get().queue(entity);
        } else if (this.isShards()) {
            ShardsEffect.get().spawn(entity.method_73189(), this.shardsSettings());
        } else if (this.isSwingStyle()) {
            this.triggerSwing(entity, true);
        } else {
            this.spawnBBEffect(entity.method_73189(), entity);
        }
        this.lastSpawnedId = entity.method_5628();
        this.lastSpawnTime = now;
        this.lastTarget = null;
        this.lastPoint = null;
    }

    private void triggerSwing(class_1309 target, boolean allowSound) {
        if (target == null) {
            return;
        }
        float scale = 1.0f;
        float volume = allowSound && this.swingSoundEnabled() ? this.swingVolume() : 0.0f;
        int color = this.getColor();
        if (this.isSwingShatter()) {
            SwingShatterEffect.get().trigger(target, scale, volume, color);
        } else if (this.isSwingGrave()) {
            SwingGraveEffect.get().trigger(target, scale, volume, color);
        } else if (this.isSwingCoins()) {
            SwingCoinsEffect.get().trigger(target, scale, volume, color);
        } else {
            SwingLightningEffect.get().trigger(target, scale, volume, color);
        }
    }

    private void spawnBBEffect(class_243 pos, class_1309 target) {
        String id;
        EffectCatalog.Effect effect;
        if (!catalogLoaded) {
            try {
                EffectCatalog.registerAll();
                catalogLoaded = true;
            }
            catch (Exception e) {
                return;
            }
        }
        if ((effect = EffectCatalog.get(id = this.getEffectId())) == null) {
            return;
        }
        class_2960 skinTexture = null;
        if (target instanceof class_742) {
            class_742 player = (class_742)target;
            skinTexture = player.method_52814().comp_1626().comp_3627();
        }
        double dx = KillEffect.mc.field_1724.method_23317() - pos.field_1352;
        double dz = KillEffect.mc.field_1724.method_23321() - pos.field_1350;
        float yawToPlayer = (float)Math.toDegrees(Math.atan2(dx, dz));
        this.effectManager.spawn(effect, pos, yawToPlayer, skinTexture);
    }

    private KillPoint point(class_1309 entity) {
        return new KillPoint(entity.method_73189(), Math.max(0.65f, entity.method_17681()));
    }

    private float partialTick() {
        try {
            return mc.method_61966().method_60637(false);
        }
        catch (Throwable ignored) {
            return 0.0f;
        }
    }

    private KimikoDissolve.Settings dissolveSettings() {
        return new KimikoDissolve.Settings(this.dissolveCount.getValue().intValue(), this.dissolveSize.getValue().floatValue(), this.dissolveHold.getValue().longValue(), this.dissolveFade.getValue().longValue(), this.dissolveRise.getValue().floatValue(), this.dissolveChaos.getValue().floatValue());
    }

    private SoulEffect.Settings soulSettings() {
        return new SoulEffect.Settings(this.soulRise.getValue().floatValue(), this.soulRotate360.getValue(), this.soulColor(), this.soulDuration.getValue().longValue() * 1000L, this.soulTransparency.getValue().floatValue(), this.soulBrightness.getValue().floatValue());
    }

    private ShardsEffect.Settings shardsSettings() {
        return new ShardsEffect.Settings(this.shardsColor(), this.shardDensity.getValue().floatValue(), this.soulDuration.getValue().longValue() * 1000L);
    }

    private int soulColor() {
        return this.typeColor.is("Custom") ? this.tintColor.getValue() : ColorUtil.getClientColor1(1);
    }

    private int shardsColor() {
        if (this.typeColor.is("Custom")) {
            return this.tintColor.getValue();
        }
        return -1182300161;
    }

    private int lightningEffectColor() {
        if (!this.lightningThemeColor.getValue().booleanValue()) {
            return this.lightningColor.getValue();
        }
        if (this.typeColor.is("Custom")) {
            return this.tintColor.getValue();
        }
        return ColorUtil.getClientColor1(1);
    }

    private DeathParticles.Settings particleSettings() {
        return new DeathParticles.Settings(Math.round(this.particleCount.getValue().floatValue()), this.particleSize.getValue().floatValue(), this.particleLife.getValue().floatValue(), this.particleGravity.getValue(), this.getColor());
    }

    public static KillEffect getInstance() {
        return Instance.get(KillEffect.class);
    }

    private static int applySeed(int base, int seed) {
        float jitter = ((float)(seed & 0xFF) / 255.0f - 0.5f) * 0.55f;
        int r = class_3532.method_15340((int)((int)((float)(base >> 16 & 0xFF) * (1.0f + jitter))), (int)0, (int)255);
        int g = class_3532.method_15340((int)((int)((float)(base >> 8 & 0xFF) * (1.0f + jitter * 0.7f))), (int)0, (int)255);
        int b = class_3532.method_15340((int)((int)((float)(base & 0xFF) * (1.0f + jitter * 1.3f))), (int)0, (int)255);
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        if (KillEffect.mc.field_1724 == null || KillEffect.mc.field_1687 == null) {
            return;
        }
        class_4587 matrices = e.getMatrixStack();
        class_243 cam = KillEffect.mc.field_1773.method_19418().method_71156();
        SwingKillEffects.renderAll(matrices, cam, this.partialTick());
        if (this.isSwingStyle()) {
            return;
        }
        if (this.isDestraParticles()) {
            DeathParticles.get().render(matrices, cam);
            return;
        }
        if (this.isDestraLightning() || this.isDestraFrag()) {
            return;
        }
        if (this.isKimikoDissolve()) {
            KimikoDissolve.get().render(matrices, cam, this.dissolveSettings(), seed -> {
                if (this.typeColor.is("Custom")) {
                    return KillEffect.applySeed(this.tintColor.getValue(), seed);
                }
                return KillEffect.applySeed(ColorUtil.getClientColor1(1), seed);
            }, this.partialTick());
            return;
        }
        if (this.isSoul()) {
            SoulEffect.get().render(matrices, cam, this.soulSettings(), this.partialTick());
            return;
        }
        if (this.isShards()) {
            ShardsEffect.get().render(matrices, cam, this.shardsSettings());
            return;
        }
        if (!this.isBeam()) {
            List<EffectManager.ActiveEffect> active = this.effectManager.active();
            if (!active.isEmpty()) {
                class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
                for (EffectManager.ActiveEffect effect : active) {
                    EffectRenderer.render(matrices, (class_4597)immediate, cam, effect);
                }
                immediate.method_22993();
            }
            return;
        }
        if (this.beams.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        long durMs = (long)(this.duration.getValue().floatValue() * 1000.0f);
        int color = this.getColor();
        int cr = color >> 16 & 0xFF;
        int cg = color >> 8 & 0xFF;
        int cb = color & 0xFF;
        Matrix4f mat = matrices.method_23760().method_23761();
        Iterator<KillBeam> it = this.beams.iterator();
        while (it.hasNext()) {
            KillBeam beam = it.next();
            float progress = (float)(now - beam.start) / (float)durMs;
            if (progress >= 1.0f) {
                it.remove();
                continue;
            }
            float fadeIn = class_3532.method_15363((float)(progress * 5.5f), (float)0.0f, (float)1.0f);
            float fadeOut = progress > 0.5f ? 1.0f - (progress - 0.5f) / 0.5f : 1.0f;
            beam.alpha = KillEffect.smooth(fadeIn) * KillEffect.smooth(fadeOut);
            beam.progress = progress;
        }
        if (this.beams.isEmpty()) {
            return;
        }
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        class_4588 buf = immediate.method_73477(BEAM_LAYER);
        float pulse = 0.9f + 0.1f * class_3532.method_15374((double)((float)(now % 1600L) / 1600.0f * ((float)Math.PI * 2)));
        for (KillBeam beam : this.beams) {
            this.drawBeam(buf, mat, cam, beam, cr, cg, cb, pulse);
        }
        immediate.method_22993();
    }

    private void drawBeam(class_4588 buf, Matrix4f mat, class_243 cam, KillBeam beam, int r, int g, int b, float pulse) {
        if (beam.alpha <= 0.01f) {
            return;
        }
        float grow = KillEffect.smooth(Math.min(beam.progress * 3.5f, 1.0f));
        float h = this.beamHeight.getValue().floatValue() * grow;
        float base = this.beamWidth.getValue().floatValue() * beam.radius;
        float x = (float)(beam.x - cam.field_1352);
        float y = (float)(beam.y - cam.field_1351 + (double)0.035f);
        float z = (float)(beam.z - cam.field_1350);
        float spin = (float)(System.currentTimeMillis() - beam.start) / 1000.0f * 0.85f;
        float coreAlpha = 150.0f * beam.alpha * pulse;
        float softAlpha = 54.0f * beam.alpha;
        KillEffect.drawVerticalPetals(buf, mat, x, y, z, h, base * 0.72f, spin, r, g, b, (int)coreAlpha, 6);
        KillEffect.drawVerticalPetals(buf, mat, x, y, z, h * 0.82f, base * 1.28f, -spin * 0.55f, r, g, b, (int)softAlpha, 8);
        float bloom = 0.55f + 0.45f * grow;
        KillEffect.drawSoftDisc(buf, mat, x, y + 0.01f, z, base * 2.25f * bloom, r, g, b, (int)(42.0f * beam.alpha));
        KillEffect.drawSoftDisc(buf, mat, x, y + h * 0.98f, z, base * 0.92f * beam.alpha, r, g, b, (int)(28.0f * beam.alpha));
        for (int i = 0; i < 3; ++i) {
            float wave = (beam.progress + (float)i * 0.22f) % 1.0f;
            float radius = base * (1.05f + wave * 2.25f);
            int alpha = (int)(52.0f * beam.alpha * (1.0f - wave));
            KillEffect.drawRing(buf, mat, x, y + (float)i * 0.025f, z, radius, 0.035f + wave * 0.045f, r, g, b, alpha);
        }
    }

    private static void drawVerticalPetals(class_4588 buf, Matrix4f mat, float x, float y, float z, float h, float halfWidth, float spin, int r, int g, int b, int alpha, int petals) {
        if (alpha <= 0 || h <= 0.01f) {
            return;
        }
        for (int i = 0; i < petals; ++i) {
            float ang = spin + (float)i / (float)petals * ((float)Math.PI * 2);
            float dx = class_3532.method_15362((double)ang) * halfWidth;
            float dz = class_3532.method_15374((double)ang) * halfWidth;
            int edgeAlpha = Math.max(0, alpha / 3);
            buf.method_22918((Matrix4fc)mat, x - dx, y, z - dz).method_1336(r, g, b, edgeAlpha);
            buf.method_22918((Matrix4fc)mat, x, y + h * 0.08f, z).method_1336(r, g, b, alpha);
            buf.method_22918((Matrix4fc)mat, x, y + h, z).method_1336(r, g, b, 0);
            buf.method_22918((Matrix4fc)mat, x + dx, y, z + dz).method_1336(r, g, b, edgeAlpha);
        }
    }

    private static void drawSoftDisc(class_4588 buf, Matrix4f mat, float x, float y, float z, float radius, int r, int g, int b, int alpha) {
        if (alpha <= 0 || radius <= 0.01f) {
            return;
        }
        int segments = 56;
        for (int i = 0; i < segments; ++i) {
            float a0 = (float)i / (float)segments * ((float)Math.PI * 2);
            float a1 = (float)(i + 1) / (float)segments * ((float)Math.PI * 2);
            buf.method_22918((Matrix4fc)mat, x, y, z).method_1336(r, g, b, alpha);
            buf.method_22918((Matrix4fc)mat, x + class_3532.method_15362((double)a0) * radius, y, z + class_3532.method_15374((double)a0) * radius).method_1336(r, g, b, 0);
            buf.method_22918((Matrix4fc)mat, x + class_3532.method_15362((double)a1) * radius, y, z + class_3532.method_15374((double)a1) * radius).method_1336(r, g, b, 0);
            buf.method_22918((Matrix4fc)mat, x, y, z).method_1336(r, g, b, alpha);
        }
    }

    private static void drawRing(class_4588 buf, Matrix4f mat, float x, float y, float z, float radius, float width, int r, int g, int b, int alpha) {
        if (alpha <= 0 || radius <= width) {
            return;
        }
        int segments = 64;
        for (int i = 0; i < segments; ++i) {
            float a0 = (float)i / (float)segments * ((float)Math.PI * 2);
            float a1 = (float)(i + 1) / (float)segments * ((float)Math.PI * 2);
            float x0 = class_3532.method_15362((double)a0);
            float z0 = class_3532.method_15374((double)a0);
            float x1 = class_3532.method_15362((double)a1);
            float z1 = class_3532.method_15374((double)a1);
            buf.method_22918((Matrix4fc)mat, x + x0 * (radius - width), y, z + z0 * (radius - width)).method_1336(r, g, b, 0);
            buf.method_22918((Matrix4fc)mat, x + x0 * radius, y, z + z0 * radius).method_1336(r, g, b, alpha);
            buf.method_22918((Matrix4fc)mat, x + x1 * radius, y, z + z1 * radius).method_1336(r, g, b, alpha);
            buf.method_22918((Matrix4fc)mat, x + x1 * (radius - width), y, z + z1 * (radius - width)).method_1336(r, g, b, 0);
        }
    }

    private static float smooth(float t) {
        t = class_3532.method_15363((float)t, (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private void clearState() {
        this.beams.clear();
        KimikoDissolve.clear();
        SoulEffect.get().clear();
        ShardsEffect.get().clear();
        DeathParticles.get().clear();
        FragEffect.get().clear();
        LightningFlash.get().clear();
        SwingKillEffects.clearAll();
        this.lastTarget = null;
        this.lastPoint = null;
        this.lastTargetId = -1;
        this.lastSpawnedId = -1;
    }

    static {
        BASIC_NAMES.put("Beam", "Beam");
        BASIC_NAMES.put("soul", "\u0414\u0443\u0448\u0430");
        BASIC_NAMES.put("shards", "\u041e\u0441\u043a\u043e\u043b\u043a\u0438");
        BASIC_NAMES.put("kimiko_dissolve", "\u0420\u0430\u0441\u0442\u0432\u043e\u0440\u0435\u043d\u0438\u0435");
        BASIC_NAMES.put("destra_lightning", "\u041c\u043e\u043b\u043d\u0438\u044f");
        BASIC_NAMES.put("destra_particles", "\u041f\u0430\u0440\u0442\u0438\u043a\u043b\u044b");
        BASIC_NAMES.put("destra_frag", "\u0424\u0440\u0430\u0433");
        BASIC_NAMES.put("swing_lightning", "\u0413\u0440\u043e\u043c");
        BASIC_NAMES.put("swing_shatter", "\u0420\u0430\u0441\u043a\u043e\u043b");
        BASIC_NAMES.put("swing_grave", "\u041d\u0430\u0434\u0433\u0440\u043e\u0431\u0438\u0435");
        BASIC_NAMES.put("swing_coins", "\u041c\u043e\u043d\u0435\u0442\u044b");
        DISPLAY_NAMES.put("shark_attack", "Shark Attack");
        DISPLAY_NAMES.put("ice_shatter", "Ice Shatter");
        DISPLAY_NAMES.put("glass_shatter", "Glass Shatter");
        DISPLAY_NAMES.put("rust_decay", "Rust Decay");
        DISPLAY_NAMES.put("quicksand", "Quicksand");
        DISPLAY_NAMES.put("knockout_ko", "Knockout KO");
        DISPLAY_NAMES.put("bubble_burst", "Bubble Burst");
        DISPLAY_NAMES.put("spectral_fade", "Spectral Fade");
        DISPLAY_NAMES.put("energy_dissipation", "Energy Dissipation");
        DISPLAY_NAMES.put("stone_crumble", "Stone Crumble");
        DISPLAY_NAMES.put("sand_dissolve", "Sand Dissolve");
        DISPLAY_NAMES.put("water_evaporation", "Water Evaporation");
        DISPLAY_NAMES.put("sound_wave_disperse", "Sound Wave Disperse");
        DISPLAY_NAMES.put("digital_disintegration", "Digital Disintegration");
        DISPLAY_NAMES.put("tentacle_grasp", "Tentacle Grasp");
        DISPLAY_NAMES.put("plantfood_feasting", "Plantfood Feasting");
        DISPLAY_NAMES.put("tertis_smash", "Tetris Smash");
        DISPLAY_NAMES.put("light_absorption", "Light Absorption");
        DISPLAY_NAMES.put("astral_projection", "Astral Projection");
        DISPLAY_NAMES.put("arcade_gameover", "Arcade Gameover");
        DISPLAY_NAMES.put("angelic_bless", "Angelic Bless");
        DISPLAY_NAMES.put("liquid_meltdown", "Liquid Meltdown");
        DISPLAY_NAMES.put("hellfire_burn", "Hellfire Burn");
        DISPLAY_NAMES.put("imposter_instinct", "Imposter Instinct");
        DISPLAY_NAMES.put("colorful_explosion", "Colorful Explosion");
        DISPLAY_NAMES.put("feather_scatter", "Feather Scatter");
        DISPLAY_NAMES.put("nature_reclaim", "Nature Reclaim");
        DISPLAY_NAMES.put("dissolve_into_ash", "Dissolve Into Ash");
        DISPLAY_NAMES.put("hologram_flicker_out", "Hologram Flicker Out");
        DISPLAY_NAMES.put("kfx_abstracted", "Abstracted");
        DISPLAY_NAMES.put("kfx_acidic_corrosion", "Acidic Corrosion");
        DISPLAY_NAMES.put("kfx_clockwork_disassembly", "Clockwork Disassembly");
        DISPLAY_NAMES.put("kfx_frost_infection", "Frost Infection");
        DISPLAY_NAMES.put("kfx_graffiti_spray", "Graffiti Spray");
        DISPLAY_NAMES.put("kfx_ink_blots", "Ink Blots");
        DISPLAY_NAMES.put("kfx_magnetic_resonance", "Magnetic Resonance");
        DISPLAY_NAMES.put("kfx_origami_fold", "Origami Fold");
        DISPLAY_NAMES.put("kfx_pure_form", "Pure Form");
        BASIC_VALUES = BASIC_NAMES.values().toArray(new String[0]);
        MODEL_VALUES = DISPLAY_NAMES.values().toArray(new String[0]);
        catalogLoaded = false;
        BEAM_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56860}).withLocation(class_2960.method_60655((String)"decide", (String)"kill_effect_beam")).withVertexFormat(class_290.field_1576, VertexFormat.class_5596.field_27382).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withBlend(BlendFunction.LIGHTNING).build());
        BEAM_LAYER = class_1921.method_75940((String)"kill_effect_beam", (class_12247)class_12247.method_75927((RenderPipeline)BEAM_PIPELINE).method_75929(32768).method_75938());
    }

    @Environment(value=EnvType.CLIENT)
    private record KillPoint(class_243 pos, float radius) {
    }

    @Environment(value=EnvType.CLIENT)
    private static class KillBeam {
        final double x;
        final double y;
        final double z;
        final float radius;
        final long start;
        float alpha;
        float progress;

        KillBeam(KillPoint point) {
            this.x = point.pos.field_1352;
            this.y = point.pos.field_1351;
            this.z = point.pos.field_1350;
            this.radius = point.radius;
            this.start = System.currentTimeMillis();
        }
    }
}

