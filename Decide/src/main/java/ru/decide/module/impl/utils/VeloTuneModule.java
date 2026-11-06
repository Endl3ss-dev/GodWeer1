/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.optimization.velotune.perf.VeloTuneConfig;

@ModuleInfo(name="VeloTune", category=Category.UTILITIES, desc="\u041e\u043f\u0442\u0438\u043c\u0438\u0437\u0430\u0446\u0438\u044f \u0440\u0435\u043d\u0434\u0435\u0440\u0430: LOD \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439, \u043f\u0440\u043e\u043f\u0443\u0441\u043a \u0447\u0430\u0441\u0442\u0438\u0446, \u043a\u044d\u0448\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435 \u043f\u043e\u0437, HUD-\u0431\u0443\u0444\u0435\u0440", key=-1)
@Environment(value=EnvType.CLIENT)
public class VeloTuneModule
extends Module {
    public BooleanSetting enabled = new BooleanSetting((Module)this, "\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c VeloTune", true).onAction(this::syncToConfig);
    public SliderSetting targetFps = new SliderSetting(this, "\u0426\u0435\u043b\u0435\u0432\u043e\u0439 FPS", 360.0f, 30.0f, 360.0f, 10.0f).onAction(this::syncToConfig);
    public SliderSetting maxPressure = new SliderSetting(this, "\u041c\u0430\u043a\u0441. \u0434\u0430\u0432\u043b\u0435\u043d\u0438\u0435", 3.0f, 0.0f, 3.0f, 1.0f).onAction(this::syncToConfig);
    public DelimiterSetting extremeHeader = new DelimiterSetting(this, "\u042d\u043a\u0441\u0442\u0440\u0438\u043c \u0440\u0435\u0436\u0438\u043c");
    public BooleanSetting extremeEnabled = new BooleanSetting((Module)this, "\u042d\u043a\u0441\u0442\u0440\u0438\u043c \u0432\u043a\u043b\u044e\u0447\u0451\u043d", false).onAction(this::syncToConfig);
    public SliderSetting entityDistance = ((SliderSetting)new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439", 48.0f, 8.0f, 256.0f, 1.0f).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting playerDistance = ((SliderSetting)new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0438\u0433\u0440\u043e\u043a\u043e\u0432", 64.0f, 8.0f, 256.0f, 1.0f).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting blockEntityDistance = ((SliderSetting)new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0431\u043b\u043e\u043a-\u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439", 12.0f, 8.0f, 256.0f, 1.0f).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting verticalCull = ((SliderSetting)new SliderSetting(this, "\u0412\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u044c\u043d\u0430\u044f \u043e\u0442\u0441\u0435\u0447\u043a\u0430", 20.0f, 0.0f, 256.0f, 1.0f).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting particleDistance = ((SliderSetting)new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0447\u0430\u0441\u0442\u0438\u0446", 12.0f, 8.0f, 128.0f, 1.0f).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting maxPlayers = ((SliderSetting)new SliderSetting(this, "\u041c\u0430\u043a\u0441. \u0438\u0433\u0440\u043e\u043a\u043e\u0432", 128.0f, 1.0f, 128.0f, 1.0f).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting maxEntities = ((SliderSetting)new SliderSetting(this, "\u041c\u0430\u043a\u0441. \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439", 128.0f, 1.0f, 2048.0f, 1.0f).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public DelimiterSetting playersHeader = new DelimiterSetting(this, "\u0418\u0433\u0440\u043e\u043a\u0438");
    public BooleanSetting poseCache = new BooleanSetting((Module)this, "\u041a\u044d\u0448 \u043f\u043e\u0437", true).onAction(this::syncToConfig);
    public SliderSetting nearPose = ((SliderSetting)new SliderSetting(this, "\u0411\u043b\u0438\u0436\u043d\u044f\u044f \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u043f\u043e\u0437", 24.0f, 8.0f, 128.0f, 1.0f).setVisible(() -> this.poseCache.getValue())).onAction(this::syncToConfig);
    public SliderSetting midPose = ((SliderSetting)new SliderSetting(this, "\u0421\u0440\u0435\u0434\u043d\u044f\u044f \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u043f\u043e\u0437", 48.0f, 8.0f, 192.0f, 1.0f).setVisible(() -> this.poseCache.getValue())).onAction(this::syncToConfig);
    public SliderSetting farPose = ((SliderSetting)new SliderSetting(this, "\u0414\u0430\u043b\u044c\u043d\u044f\u044f \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u043f\u043e\u0437", 96.0f, 8.0f, 256.0f, 1.0f).setVisible(() -> this.poseCache.getValue())).onAction(this::syncToConfig);
    public SliderSetting featureDistance = new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0444\u0438\u0447", 96.0f, 24.0f, 256.0f, 1.0f).onAction(this::syncToConfig);
    public SliderSetting labelDistance = new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0442\u0430\u0431\u043b\u0438\u0447\u0435\u043a", 64.0f, 16.0f, 256.0f, 1.0f).onAction(this::syncToConfig);
    public BooleanSetting emergencyCull = new BooleanSetting((Module)this, "\u042d\u043a\u0441\u0442\u0440. \u043e\u0442\u0441\u0435\u0447\u0435\u043d\u0438\u0435 \u0438\u0433\u0440\u043e\u043a\u043e\u0432", true).onAction(this::syncToConfig);
    public SliderSetting emergencyDistance = ((SliderSetting)new SliderSetting(this, "\u042d\u043a\u0441\u0442\u0440. \u0434\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f", 256.0f, 64.0f, 512.0f, 1.0f).setVisible(() -> this.emergencyCull.getValue())).onAction(this::syncToConfig);
    public SliderSetting maxCachedPlayers = new SliderSetting(this, "\u041c\u0430\u043a\u0441. \u043a\u044d\u0448\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0445", 1024.0f, 32.0f, 4096.0f, 32.0f).onAction(this::syncToConfig);
    public DelimiterSetting particlesHeader = new DelimiterSetting(this, "\u0427\u0430\u0441\u0442\u0438\u0446\u044b");
    public BooleanSetting particlesEnabled = new BooleanSetting((Module)this, "\u041e\u0433\u0440\u0430\u043d\u0438\u0447\u0435\u043d\u0438\u0435 \u0447\u0430\u0441\u0442\u0438\u0446", true).onAction(this::syncToConfig);
    public SliderSetting particleBudget = ((SliderSetting)new SliderSetting(this, "\u0411\u044e\u0434\u0436\u0435\u0442 \u0447\u0430\u0441\u0442\u0438\u0446/\u043a\u0430\u0434\u0440", 1536.0f, 64.0f, 16384.0f, 64.0f).setVisible(() -> this.particlesEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting nearParticleDist = ((SliderSetting)new SliderSetting(this, "\u0411\u043b\u0438\u0436\u043d\u044f\u044f \u0434\u0438\u0441\u0442. \u0447\u0430\u0441\u0442\u0438\u0446", 32.0f, 8.0f, 96.0f, 1.0f).setVisible(() -> this.particlesEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting hardParticleDist = ((SliderSetting)new SliderSetting(this, "\u0414\u0430\u043b\u044c\u043d\u044f\u044f \u0434\u0438\u0441\u0442. \u0447\u0430\u0441\u0442\u0438\u0446", 160.0f, 8.0f, 512.0f, 1.0f).setVisible(() -> this.particlesEnabled.getValue())).onAction(this::syncToConfig);
    public DelimiterSetting renderHeader = new DelimiterSetting(this, "\u041e\u043f\u0442\u0438\u043c\u0438\u0437\u0430\u0446\u0438\u0438 \u0440\u0435\u043d\u0434\u0435\u0440\u0430");
    public BooleanSetting hideFeatures = ((BooleanSetting)new BooleanSetting((Module)this, "\u041f\u0440\u044f\u0447\u0430\u0442\u044c \u0444\u0438\u0447\u0438 \u0438\u0433\u0440\u043e\u043a\u043e\u0432", false).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public BooleanSetting preserveEquipment = ((BooleanSetting)new BooleanSetting((Module)this, "\u0421\u043e\u0445\u0440. \u044d\u043a\u0438\u043f\u0438\u0440\u043e\u0432\u043a\u0443", true).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public BooleanSetting simplifyEquip = ((BooleanSetting)new BooleanSetting((Module)this, "\u0423\u043f\u0440\u043e\u0441\u0442\u0438\u0442\u044c \u044d\u043a\u0438\u043f\u0438\u0440\u043e\u0432\u043a\u0443", false).setVisible(() -> this.extremeEnabled.getValue() != false && this.preserveEquipment.getValue() == false)).onAction(this::syncToConfig);
    public BooleanSetting hideLabels = ((BooleanSetting)new BooleanSetting((Module)this, "\u041f\u0440\u044f\u0447\u0430\u0442\u044c \u0442\u0430\u0431\u043b\u0438\u0447\u043a\u0438", false).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public BooleanSetting preserveLabels = ((BooleanSetting)new BooleanSetting((Module)this, "\u0421\u043e\u0445\u0440. \u0442\u0430\u0431\u043b\u0438\u0447\u043a\u0438", true).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public BooleanSetting disableOutlines = ((BooleanSetting)new BooleanSetting((Module)this, "\u041e\u0442\u043a\u043b. \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435", false).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public BooleanSetting disableShadows = ((BooleanSetting)new BooleanSetting((Module)this, "\u041e\u0442\u043a\u043b. \u0442\u0435\u043d\u0438", false).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public BooleanSetting disableClouds = ((BooleanSetting)new BooleanSetting((Module)this, "\u041e\u0442\u043a\u043b. \u043e\u0431\u043b\u0430\u043a\u0430", false).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public BooleanSetting disableWeather = ((BooleanSetting)new BooleanSetting((Module)this, "\u041e\u0442\u043a\u043b. \u043f\u043e\u0433\u043e\u0434\u0443", false).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public BooleanSetting cullChunks = ((BooleanSetting)new BooleanSetting((Module)this, "\u041e\u0442\u0441\u0435\u043a\u0430\u0442\u044c \u0447\u0430\u043d\u043a\u0438", false).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public BooleanSetting cullBlockEntities = ((BooleanSetting)new BooleanSetting((Module)this, "\u041e\u0442\u0441\u0435\u043a\u0430\u0442\u044c \u0431\u043b\u043e\u043a-\u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438", false).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public DelimiterSetting hudHeader = new DelimiterSetting(this, "HUD \u0438 \u0432\u0432\u043e\u0434");
    public BooleanSetting nameTagBuffer = ((BooleanSetting)new BooleanSetting((Module)this, "\u0411\u0443\u0444\u0435\u0440 \u0442\u0430\u0431\u043b\u0438\u0447\u0435\u043a", false).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting nameTagHz = ((SliderSetting)new SliderSetting(this, "\u0427\u0430\u0441\u0442\u043e\u0442\u0430 \u0442\u0430\u0431\u043b\u0438\u0447\u0435\u043a (\u0413\u0446)", 5.0f, 1.0f, 240.0f, 1.0f).setVisible(() -> this.extremeEnabled.getValue() != false && this.nameTagBuffer.getValue() != false)).onAction(this::syncToConfig);
    public BooleanSetting hudBuffer = ((BooleanSetting)new BooleanSetting((Module)this, "\u0411\u0443\u0444\u0435\u0440 HUD", false).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting hudHz = ((SliderSetting)new SliderSetting(this, "\u0427\u0430\u0441\u0442\u043e\u0442\u0430 HUD (\u0413\u0446)", 20.0f, 1.0f, 240.0f, 1.0f).setVisible(() -> this.extremeEnabled.getValue() != false && this.hudBuffer.getValue() != false)).onAction(this::syncToConfig);
    public BooleanSetting rawInput = ((BooleanSetting)new BooleanSetting((Module)this, "Raw Input \u0431\u0443\u0444\u0435\u0440", false).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting mouseHz = ((SliderSetting)new SliderSetting(this, "\u0427\u0430\u0441\u0442\u043e\u0442\u0430 \u043c\u044b\u0448\u0438 (\u0413\u0446)", 240.0f, 0.0f, 360.0f, 10.0f).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting poseHz = ((SliderSetting)new SliderSetting(this, "\u0427\u0430\u0441\u0442\u043e\u0442\u0430 \u043f\u043e\u0437 (\u0413\u0446)", 60.0f, 1.0f, 120.0f, 1.0f).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting poseInterval = ((SliderSetting)new SliderSetting(this, "\u0418\u043d\u0442\u0435\u0440\u0432\u0430\u043b \u043f\u043e\u0437 (\u043a\u0430\u0434\u0440\u044b)", 1.0f, 1.0f, 120.0f, 1.0f).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);
    public SliderSetting particleBudgetExtreme = ((SliderSetting)new SliderSetting(this, "\u0411\u044e\u0434\u0436\u0435\u0442 \u0447\u0430\u0441\u0442\u0438\u0446 \u044d\u043a\u0441\u0442\u0440.", 32.0f, 16.0f, 2048.0f, 16.0f).setVisible(() -> this.extremeEnabled.getValue())).onAction(this::syncToConfig);

    @Override
    protected void onEnable() {
        this.syncToConfig();
        VeloTuneManager.saveConfig();
    }

    @Override
    protected void onDisable() {
        VeloTuneConfig config = VeloTuneManager.config();
        config.enabled = false;
        VeloTuneManager.saveConfig();
    }

    public void syncToConfig() {
        VeloTuneConfig config = VeloTuneManager.config();
        config.enabled = this.enabled.getValue();
        config.adaptive.targetFps = this.targetFps.getValue().intValue();
        config.adaptive.maxPressureLevel = this.maxPressure.getValue().intValue();
        config.extreme.enabled = this.extremeEnabled.getValue();
        config.extreme.entityRenderDistance = this.entityDistance.getValue().floatValue();
        config.extreme.playerRenderDistance = this.playerDistance.getValue().floatValue();
        config.extreme.blockEntityRenderDistance = this.blockEntityDistance.getValue().floatValue();
        config.extreme.verticalCullDepth = this.verticalCull.getValue().floatValue();
        config.extreme.particleRenderDistance = this.particleDistance.getValue().floatValue();
        config.extreme.maxRenderedPlayers = this.maxPlayers.getValue().intValue();
        config.extreme.maxRenderedEntities = this.maxEntities.getValue().intValue();
        config.players.poseCache = this.poseCache.getValue();
        config.players.nearPoseDistance = this.nearPose.getValue().floatValue();
        config.players.midPoseDistance = this.midPose.getValue().floatValue();
        config.players.farPoseDistance = this.farPose.getValue().floatValue();
        config.players.featureDistance = this.featureDistance.getValue().floatValue();
        config.players.labelDistance = this.labelDistance.getValue().floatValue();
        config.players.emergencyCullDistantPlayers = this.emergencyCull.getValue();
        config.players.emergencyCullDistance = this.emergencyDistance.getValue().floatValue();
        config.players.maxCachedPlayers = this.maxCachedPlayers.getValue().intValue();
        config.particles.enabled = this.particlesEnabled.getValue();
        config.particles.spawnBudgetPerFrame = this.particleBudget.getValue().intValue();
        config.particles.nearDistance = this.nearParticleDist.getValue().floatValue();
        config.particles.hardDistance = this.hardParticleDist.getValue().floatValue();
        config.extreme.hidePlayerFeatures = this.hideFeatures.getValue();
        config.extreme.preservePlayerEquipment = this.preserveEquipment.getValue();
        config.extreme.simplifyEquipmentRendering = this.simplifyEquip.getValue();
        config.extreme.hidePlayerLabels = this.hideLabels.getValue();
        config.extreme.preservePlayerLabels = this.preserveLabels.getValue();
        config.extreme.disableGlowingOutlines = this.disableOutlines.getValue();
        config.extreme.disableEntityShadows = this.disableShadows.getValue();
        config.extreme.disableClouds = this.disableClouds.getValue();
        config.extreme.disableWeather = this.disableWeather.getValue();
        config.extreme.cullChunkSections = this.cullChunks.getValue();
        config.extreme.cullBlockEntities = this.cullBlockEntities.getValue();
        config.extreme.nameTagTextBuffer = this.nameTagBuffer.getValue();
        config.extreme.nameTagTextUpdatesPerSecond = this.nameTagHz.getValue().intValue();
        config.extreme.hudBuffer = this.hudBuffer.getValue();
        config.extreme.hudUpdatesPerSecond = this.hudHz.getValue().intValue();
        config.extreme.rawInputBuffer = this.rawInput.getValue();
        config.extreme.mouseUpdatesPerSecond = this.mouseHz.getValue().intValue();
        config.extreme.playerPoseUpdatesPerSecond = this.poseHz.getValue().intValue();
        config.extreme.playerPoseInterval = this.poseInterval.getValue().intValue();
        config.extreme.particleBudgetPerFrame = this.particleBudgetExtreme.getValue().intValue();
        VeloTuneManager.saveConfig();
    }

    public void loadFromConfig() {
        VeloTuneConfig config = VeloTuneManager.config();
        this.enabled.set(config.enabled);
        this.targetFps.set(Float.valueOf(config.adaptive.targetFps));
        this.maxPressure.set(Float.valueOf(config.adaptive.maxPressureLevel));
        this.extremeEnabled.set(config.extreme.enabled);
        this.entityDistance.set(Float.valueOf((float)config.extreme.entityRenderDistance));
        this.playerDistance.set(Float.valueOf((float)config.extreme.playerRenderDistance));
        this.blockEntityDistance.set(Float.valueOf((float)config.extreme.blockEntityRenderDistance));
        this.verticalCull.set(Float.valueOf((float)config.extreme.verticalCullDepth));
        this.particleDistance.set(Float.valueOf((float)config.extreme.particleRenderDistance));
        this.maxPlayers.set(Float.valueOf(config.extreme.maxRenderedPlayers));
        this.maxEntities.set(Float.valueOf(config.extreme.maxRenderedEntities));
        this.poseCache.set(config.players.poseCache);
        this.nearPose.set(Float.valueOf((float)config.players.nearPoseDistance));
        this.midPose.set(Float.valueOf((float)config.players.midPoseDistance));
        this.farPose.set(Float.valueOf((float)config.players.farPoseDistance));
        this.featureDistance.set(Float.valueOf((float)config.players.featureDistance));
        this.labelDistance.set(Float.valueOf((float)config.players.labelDistance));
        this.emergencyCull.set(config.players.emergencyCullDistantPlayers);
        this.emergencyDistance.set(Float.valueOf((float)config.players.emergencyCullDistance));
        this.maxCachedPlayers.set(Float.valueOf(config.players.maxCachedPlayers));
        this.particlesEnabled.set(config.particles.enabled);
        this.particleBudget.set(Float.valueOf(config.particles.spawnBudgetPerFrame));
        this.nearParticleDist.set(Float.valueOf((float)config.particles.nearDistance));
        this.hardParticleDist.set(Float.valueOf((float)config.particles.hardDistance));
        this.hideFeatures.set(config.extreme.hidePlayerFeatures);
        this.preserveEquipment.set(config.extreme.preservePlayerEquipment);
        this.simplifyEquip.set(config.extreme.simplifyEquipmentRendering);
        this.hideLabels.set(config.extreme.hidePlayerLabels);
        this.preserveLabels.set(config.extreme.preservePlayerLabels);
        this.disableOutlines.set(config.extreme.disableGlowingOutlines);
        this.disableShadows.set(config.extreme.disableEntityShadows);
        this.disableClouds.set(config.extreme.disableClouds);
        this.disableWeather.set(config.extreme.disableWeather);
        this.cullChunks.set(config.extreme.cullChunkSections);
        this.cullBlockEntities.set(config.extreme.cullBlockEntities);
        this.nameTagBuffer.set(config.extreme.nameTagTextBuffer);
        this.nameTagHz.set(Float.valueOf(config.extreme.nameTagTextUpdatesPerSecond));
        this.hudBuffer.set(config.extreme.hudBuffer);
        this.hudHz.set(Float.valueOf(config.extreme.hudUpdatesPerSecond));
        this.rawInput.set(config.extreme.rawInputBuffer);
        this.mouseHz.set(Float.valueOf(config.extreme.mouseUpdatesPerSecond));
        this.poseHz.set(Float.valueOf(config.extreme.playerPoseUpdatesPerSecond));
        this.poseInterval.set(Float.valueOf(config.extreme.playerPoseInterval));
        this.particleBudgetExtreme.set(Float.valueOf(config.extreme.particleBudgetPerFrame));
    }
}

