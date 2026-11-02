/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
 *  net.minecraft.class_1113
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.class_1113;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import ru.decide.Client;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.weather.RainSound;
import ru.decide.module.impl.render.weather.WeatherFootprints;
import ru.decide.module.impl.render.weather.WeatherRenderer;
import ru.decide.utils.other.Instance;
import ru.decide.utils.sound.EffectSoundController;

@ModuleInfo(name="Weather", desc="\u041f\u043e\u0433\u043e\u0434\u043d\u044b\u0435 \u0440\u0435\u0436\u0438\u043c\u044b: \u0434\u043e\u0436\u0434\u044c, \u0437\u0438\u043c\u0430, \u0440\u0430\u0441\u0441\u0432\u0435\u0442, \u0441\u0430\u043a\u0443\u0440\u0430, \u0437\u0430\u0442\u043c\u0435\u043d\u0438\u0435 \u0438 \u0434\u0440\u0443\u0433\u0438\u0435", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public final class Weather
extends Module {
    public final DelimiterSetting generalSeparator = new DelimiterSetting(this, "\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0435");
    public final ModeSetting mode = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c", "\u0412\u044b\u043a\u043b", "\u0414\u043e\u0436\u0434\u044c", "\u0417\u0438\u043c\u0430", "\u0420\u0430\u0441\u0441\u0432\u0435\u0442", "\u041f\u044b\u043b\u044c", "\u0421\u0430\u043a\u0443\u0440\u0430", "\u0417\u0430\u0442\u043c\u0435\u043d\u0438\u0435", "\u0417\u0432\u0451\u0437\u0434\u043d\u044b\u0439 \u0434\u043e\u0436\u0434\u044c", "\u041a\u0440\u043e\u0432\u0430\u0432\u0430\u044f \u043b\u0443\u043d\u0430", "\u0412\u0435\u0441\u043d\u0430", "\u041b\u0435\u0442\u043e", "\u041e\u0441\u0435\u043d\u044c");
    public final ModeSetting quality = new ModeSetting((Module)this, "\u041a\u0430\u0447\u0435\u0441\u0442\u0432\u043e", "\u0411\u044b\u0441\u0442\u0440\u043e", "\u0411\u0430\u043b\u0430\u043d\u0441", "\u0412\u044b\u0441\u043e\u043a\u043e\u0435");
    public final BooleanSetting vanillaClouds = new BooleanSetting((Module)this, "\u0412\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0435 \u043e\u0431\u043b\u0430\u043a\u0430", true);
    public final DelimiterSetting soundSeparator = new DelimiterSetting(this, "\u0417\u0432\u0443\u043a \u044d\u0444\u0444\u0435\u043a\u0442\u0430").setVisible(() -> this.mode() != Mode.OFF && this.mode() != Mode.RAIN);
    public final BooleanSetting effectSound = new BooleanSetting((Module)this, "\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0437\u0432\u0443\u043a", true).setVisible(() -> this.mode() != Mode.OFF && this.mode() != Mode.RAIN);
    public final SliderSetting effectSoundVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", 22.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() != Mode.OFF && this.mode() != Mode.RAIN && this.effectSound.getValue() != false);
    public final DelimiterSetting rainSeparator = new DelimiterSetting(this, "\u0414\u043e\u0436\u0434\u044c").setVisible(() -> this.mode() == Mode.RAIN || this.mode() == Mode.OFF);
    public final SliderSetting rainIntensity = new SliderSetting(this, "\u0418\u043d\u0442\u0435\u043d\u0441\u0438\u0432\u043d\u043e\u0441\u0442\u044c \u0434\u043e\u0436\u0434\u044f", 90.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.RAIN);
    public final SliderSetting rainSound = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0434\u043e\u0436\u0434\u044f", 60.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.RAIN);
    public final SliderSetting puddles = new SliderSetting(this, "\u041b\u0443\u0436\u0438", 75.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.RAIN);
    public final BooleanSetting splashes = new BooleanSetting((Module)this, "\u0411\u0440\u044b\u0437\u0433\u0438", true).setVisible(() -> this.mode() == Mode.RAIN);
    public final SliderSetting splashDensity = new SliderSetting(this, "\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c \u0431\u0440\u044b\u0437\u0433", 75.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.RAIN && this.splashes.getValue() != false);
    public final SliderSetting ringVisibility = new SliderSetting(this, "\u0412\u0438\u0434\u0438\u043c\u043e\u0441\u0442\u044c \u043a\u0440\u0443\u0433\u043e\u0432", 100.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.RAIN);
    public final BooleanSetting wetGround = new BooleanSetting((Module)this, "\u041c\u043e\u043a\u0440\u0430\u044f \u0437\u0435\u043c\u043b\u044f", false).setVisible(() -> this.mode() == Mode.RAIN || this.mode() == Mode.OFF);
    public final SliderSetting wind = new SliderSetting(this, "\u0412\u0435\u0442\u0435\u0440", 30.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.RAIN);
    public final DelimiterSetting winterSeparator = new DelimiterSetting(this, "\u0417\u0438\u043c\u0430").setVisible(() -> this.mode() == Mode.WINTER);
    public final SliderSetting snowDensity = new SliderSetting(this, "\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c \u0441\u043d\u0435\u0433\u0430", 100.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.WINTER);
    public final BooleanSetting snowCover = new BooleanSetting((Module)this, "\u0421\u043d\u0435\u0436\u043d\u044b\u0439 \u043f\u043e\u043a\u0440\u043e\u0432", true).setVisible(() -> this.mode() == Mode.WINTER);
    public final BooleanSetting snowCaves = new BooleanSetting((Module)this, "\u0421\u043d\u0435\u0433 \u0432 \u043f\u0435\u0449\u0435\u0440\u0430\u0445", false).setVisible(() -> this.mode() == Mode.WINTER && this.snowCover.getValue() != false);
    public final SliderSetting blizzard = new SliderSetting(this, "\u041c\u0435\u0442\u0435\u043b\u044c", 65.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.WINTER);
    public final BooleanSetting snowfall = new BooleanSetting((Module)this, "\u0421\u043d\u0435\u0433\u043e\u043f\u0430\u0434", false).setVisible(() -> this.mode() == Mode.WINTER);
    public final SliderSetting snowfallDepth = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 \u0441\u0443\u0433\u0440\u043e\u0431\u043e\u0432", 60.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.WINTER && this.snowfall.getValue() != false);
    public final SliderSetting snowfallDrift = new SliderSetting(this, "\u041e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u0441\u0443\u0433\u0440\u043e\u0431\u043e\u0432", 50.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.WINTER && this.snowfall.getValue() != false);
    public final BooleanSetting snowfallCrunch = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u043f\u0440\u043e\u0436\u0438\u043c\u0430\u043d\u0438\u044f", true).setVisible(() -> this.mode() == Mode.WINTER && this.snowfall.getValue() != false);
    public final SliderSetting snowfallFootprintDepth = new SliderSetting(this, "\u0413\u043b\u0443\u0431\u0438\u043d\u0430 \u0441\u043b\u0435\u0434\u043e\u0432", 70.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.WINTER && this.snowfall.getValue() != false);
    public final BooleanSetting footprints = new BooleanSetting((Module)this, "\u0421\u043b\u0435\u0434\u044b", true).setVisible(() -> this.mode() == Mode.WINTER);
    public final SliderSetting footprintTime = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f \u0441\u043b\u0435\u0434\u043e\u0432", 25.0f, 5.0f, 60.0f, 1.0f).setVisible(() -> this.mode() == Mode.WINTER && this.footprints.getValue() != false);
    public final SliderSetting footprintSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u0441\u043b\u0435\u0434\u043e\u0432", 1.0f, 0.6f, 1.6f, 0.05f).setVisible(() -> this.mode() == Mode.WINTER && this.footprints.getValue() != false);
    public final BooleanSetting footprintMobs = new BooleanSetting((Module)this, "\u0421\u043b\u0435\u0434\u044b \u043c\u043e\u0431\u043e\u0432", true).setVisible(() -> this.mode() == Mode.WINTER && this.footprints.getValue() != false);
    public final BooleanSetting footprintSelf = new BooleanSetting((Module)this, "\u0421\u0432\u043e\u0438 \u0441\u043b\u0435\u0434\u044b", true).setVisible(() -> this.mode() == Mode.WINTER && this.footprints.getValue() != false);
    public final BooleanSetting snowAllSurfaces = new BooleanSetting((Module)this, "\u0421\u043d\u0435\u0433 \u043d\u0430 \u0432\u0441\u0435\u0445 \u043f\u043e\u0432\u0435\u0440\u0445\u043d\u043e\u0441\u0442\u044f\u0445", true).setVisible(() -> this.mode() == Mode.WINTER && this.snowCover.getValue() != false);
    public final BooleanSetting frostWood = new BooleanSetting((Module)this, "\u0418\u043d\u0435\u0439 \u043d\u0430 \u0434\u0435\u0440\u0435\u0432\u0435", true).setVisible(() -> this.mode() == Mode.WINTER);
    public final DelimiterSetting autumnSeparator = new DelimiterSetting(this, "\u041e\u0441\u0435\u043d\u044c").setVisible(() -> this.mode() == Mode.AUTUMN);
    public final BooleanSetting autumnWood = new BooleanSetting((Module)this, "\u041e\u0441\u0435\u043d\u043d\u0435\u0435 \u0434\u0435\u0440\u0435\u0432\u043e", true).setVisible(() -> this.mode() == Mode.AUTUMN);
    public final BooleanSetting autumnLeaves = new BooleanSetting((Module)this, "\u041b\u0438\u0441\u0442\u0432\u0430 \u043d\u0430 \u0431\u043b\u043e\u043a\u0430\u0445", true).setVisible(() -> this.mode() == Mode.AUTUMN);
    public final BooleanSetting leafTrail = new BooleanSetting((Module)this, "\u0421\u043b\u0435\u0434 \u0438\u0437 \u043b\u0438\u0441\u0442\u044c\u0435\u0432", true).setVisible(() -> this.mode() == Mode.AUTUMN);
    public final SliderSetting leafTrailTime = new SliderSetting(this, "\u0412\u0440\u0435\u043c\u044f \u043b\u0438\u0441\u0442\u044c\u0435\u0432", 18.0f, 5.0f, 60.0f, 1.0f).setVisible(() -> this.mode() == Mode.AUTUMN && this.leafTrail.getValue() != false);
    public final SliderSetting leafTrailSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u043b\u0438\u0441\u0442\u044c\u0435\u0432", 1.0f, 0.5f, 2.0f, 0.05f).setVisible(() -> this.mode() == Mode.AUTUMN && this.leafTrail.getValue() != false);
    public final BooleanSetting leafTrailMobs = new BooleanSetting((Module)this, "\u041b\u0438\u0441\u0442\u044c\u044f \u043e\u0442 \u043c\u043e\u0431\u043e\u0432", true).setVisible(() -> this.mode() == Mode.AUTUMN && this.leafTrail.getValue() != false);
    public final BooleanSetting leafTrailSelf = new BooleanSetting((Module)this, "\u0421\u0432\u043e\u0438 \u043b\u0438\u0441\u0442\u044c\u044f", true).setVisible(() -> this.mode() == Mode.AUTUMN && this.leafTrail.getValue() != false);
    public final DelimiterSetting sunriseSeparator;
    public final SliderSetting sunlight;
    public final SliderSetting sunRays;
    public final SliderSetting sunHeight;
    public final SliderSetting dew;
    public final DelimiterSetting dustSeparator;
    public final SliderSetting dustDensity;
    public final SliderSetting dustSize;
    public final SliderSetting dustLight;
    public final DelimiterSetting sakuraSeparator;
    public final SliderSetting sakuraDensity;
    public final SliderSetting sakuraSize;
    public final SliderSetting sakuraGroundLife;
    public final SliderSetting sakuraWind;
    public final SliderSetting sakuraHue;
    public final SliderSetting sakuraSaturation;
    public final SliderSetting sakuraFlutter;
    public final BooleanSetting sakuraTint;
    public final SliderSetting sakuraTintStrength;
    public final DelimiterSetting eclipseSeparator;
    public final SliderSetting eclipseDarkness;
    public final SliderSetting eclipseCorona;
    public final SliderSetting eclipseStars;
    public final SliderSetting eclipseDust;
    public final DelimiterSetting meteorSeparator;
    public final SliderSetting meteorRate;
    public final SliderSetting meteorBrightness;
    public final SliderSetting meteorTrail;
    public final SliderSetting meteorStars;
    public final SliderSetting meteorDebris;
    public final SliderSetting meteorSize;
    public final SliderSetting meteorVariation;
    public final SliderSetting meteorProximity;
    public final SliderSetting meteorFog;
    public final SliderSetting meteorFogDistance;
    public final SliderSetting meteorSkyGlow;
    public final SliderSetting meteorNebula;
    public final DelimiterSetting bloodSeparator;
    public final SliderSetting bloodMoonSize;
    public final SliderSetting bloodMoonGlow;
    public final SliderSetting bloodSky;
    public final SliderSetting bloodClouds;
    public final SliderSetting bloodMist;
    public final SliderSetting bloodGround;
    public final SliderSetting bloodEmbers;
    public final SliderSetting bloodHeight;
    private static RainSound rainSoundInstance;
    private static boolean wasWinter;
    private final WeatherFootprints footprintRenderer;
    private boolean configMigrated;
    private static final String[] MUTED_WINTER_PREFIXES;

    public Weather() {
        this.snowDensity.configKey("snowDensity");
        this.snowCover.configKey("snowCover");
        this.snowCaves.configKey("snowCaves");
        this.blizzard.configKey("blizzard");
        this.snowfall.configKey("snowfall");
        this.snowfallDepth.configKey("snowfallDepth");
        this.snowfallDrift.configKey("snowfallDrift");
        this.snowfallCrunch.configKey("snowfallCrunch");
        this.snowfallFootprintDepth.configKey("snowfallFootprintDepth");
        this.footprints.configKey("footprints");
        this.footprintTime.configKey("footprintTime");
        this.footprintSize.configKey("footprintSize");
        this.footprintMobs.configKey("footprintMobs");
        this.footprintSelf.configKey("footprintSelf");
        this.snowAllSurfaces.configKey("snowAllSurfaces");
        this.frostWood.configKey("frostWood");
        this.autumnWood.configKey("autumnWood");
        this.autumnLeaves.configKey("autumnLeaves");
        this.leafTrail.configKey("leafTrail");
        this.leafTrailTime.configKey("leafTrailTime");
        this.leafTrailSize.configKey("leafTrailSize");
        this.leafTrailMobs.configKey("leafTrailMobs");
        this.leafTrailSelf.configKey("leafTrailSelf");
        this.sunriseSeparator = new DelimiterSetting(this, "\u0420\u0430\u0441\u0441\u0432\u0435\u0442").setVisible(() -> this.mode() == Mode.SUNRISE);
        this.sunlight = new SliderSetting(this, "\u0421\u043e\u043b\u043d\u0435\u0447\u043d\u044b\u0439 \u0441\u0432\u0435\u0442", 75.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SUNRISE);
        this.sunRays = new SliderSetting(this, "\u041b\u0443\u0447\u0438 \u0441\u043e\u043b\u043d\u0446\u0430", 65.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SUNRISE);
        this.sunHeight = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 \u0441\u043e\u043b\u043d\u0446\u0430", 35.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SUNRISE);
        this.dew = new SliderSetting(this, "\u0420\u043e\u0441\u0430", 50.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SUNRISE);
        this.dustSeparator = new DelimiterSetting(this, "\u041f\u044b\u043b\u044c").setVisible(() -> this.mode() == Mode.DUST);
        this.dustDensity = new SliderSetting(this, "\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c \u043f\u044b\u043b\u0438", 65.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.DUST);
        this.dustSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u043f\u044b\u043b\u0438", 50.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.DUST);
        this.dustLight = new SliderSetting(this, "\u0421\u0432\u0435\u0442 \u043f\u044b\u043b\u0438", 70.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.DUST);
        this.sakuraSeparator = new DelimiterSetting(this, "\u0421\u0430\u043a\u0443\u0440\u0430").setVisible(() -> this.mode() == Mode.SAKURA);
        this.sakuraDensity = new SliderSetting(this, "\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c \u043b\u0435\u043f\u0435\u0441\u0442\u043a\u043e\u0432", 60.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SAKURA);
        this.sakuraSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u043b\u0435\u043f\u0435\u0441\u0442\u043a\u043e\u0432", 50.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SAKURA);
        this.sakuraGroundLife = new SliderSetting(this, "\u0416\u0438\u0437\u043d\u044c \u043d\u0430 \u0437\u0435\u043c\u043b\u0435", 50.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SAKURA);
        this.sakuraWind = new SliderSetting(this, "\u0412\u0435\u0442\u0435\u0440 \u0441\u0430\u043a\u0443\u0440\u044b", 30.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SAKURA);
        this.sakuraHue = new SliderSetting(this, "\u041e\u0442\u0442\u0435\u043d\u043e\u043a", 96.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SAKURA);
        this.sakuraSaturation = new SliderSetting(this, "\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c", 45.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SAKURA);
        this.sakuraFlutter = new SliderSetting(this, "\u0422\u0440\u0435\u043f\u0435\u0442\u0430\u043d\u0438\u0435", 55.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SAKURA);
        this.sakuraTint = new BooleanSetting((Module)this, "\u0420\u043e\u0437\u043e\u0432\u044b\u0439 \u043e\u0442\u0442\u0435\u043d\u043e\u043a", true).setVisible(() -> this.mode() == Mode.SAKURA);
        this.sakuraTintStrength = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u043e\u0442\u0442\u0435\u043d\u043a\u0430", 60.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.SAKURA && this.sakuraTint.getValue() != false);
        this.eclipseSeparator = new DelimiterSetting(this, "\u0417\u0430\u0442\u043c\u0435\u043d\u0438\u0435").setVisible(() -> this.mode() == Mode.ECLIPSE);
        this.eclipseDarkness = new SliderSetting(this, "\u0422\u044c\u043c\u0430", 70.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.ECLIPSE);
        this.eclipseCorona = new SliderSetting(this, "\u041a\u043e\u0440\u043e\u043d\u0430", 75.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.ECLIPSE);
        this.eclipseStars = new SliderSetting(this, "\u0417\u0432\u0451\u0437\u0434\u044b", 70.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.ECLIPSE);
        this.eclipseDust = new SliderSetting(this, "\u041a\u043e\u0441\u043c\u0438\u0447\u0435\u0441\u043a\u0430\u044f \u043f\u044b\u043b\u044c", 45.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.ECLIPSE);
        this.meteorSeparator = new DelimiterSetting(this, "\u0417\u0432\u0451\u0437\u0434\u043d\u044b\u0439 \u0434\u043e\u0436\u0434\u044c").setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorRate = new SliderSetting(this, "\u0427\u0430\u0441\u0442\u043e\u0442\u0430 \u043c\u0435\u0442\u0435\u043e\u0440\u043e\u0432", 60.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorBrightness = new SliderSetting(this, "\u042f\u0440\u043a\u043e\u0441\u0442\u044c", 80.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorTrail = new SliderSetting(this, "\u0414\u043b\u0438\u043d\u0430 \u0441\u043b\u0435\u0434\u0430", 70.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorStars = new SliderSetting(this, "\u0417\u0432\u0451\u0437\u0434\u044b", 70.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorDebris = new SliderSetting(this, "\u041e\u0441\u043a\u043e\u043b\u043a\u0438", 50.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440", 55.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorVariation = new SliderSetting(this, "\u0420\u0430\u0437\u0431\u0440\u043e\u0441", 75.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorProximity = new SliderSetting(this, "\u0411\u043b\u0438\u0437\u043e\u0441\u0442\u044c", 65.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorFog = new SliderSetting(this, "\u0422\u0443\u043c\u0430\u043d", 60.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorFogDistance = new SliderSetting(this, "\u0414\u0438\u0441\u0442\u0430\u043d\u0446\u0438\u044f \u0442\u0443\u043c\u0430\u043d\u0430", 45.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorSkyGlow = new SliderSetting(this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u043d\u0435\u0431\u0430", 65.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.meteorNebula = new SliderSetting(this, "\u0422\u0443\u043c\u0430\u043d\u043d\u043e\u0441\u0442\u044c", 55.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.METEOR);
        this.bloodSeparator = new DelimiterSetting(this, "\u041a\u0440\u043e\u0432\u0430\u0432\u0430\u044f \u043b\u0443\u043d\u0430").setVisible(() -> this.mode() == Mode.BLOOD_MOON);
        this.bloodMoonSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u043b\u0443\u043d\u044b", 65.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.BLOOD_MOON);
        this.bloodMoonGlow = new SliderSetting(this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u043b\u0443\u043d\u044b", 70.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.BLOOD_MOON);
        this.bloodSky = new SliderSetting(this, "\u041a\u0440\u0430\u0441\u043d\u043e\u0442\u0430 \u043d\u0435\u0431\u0430", 75.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.BLOOD_MOON);
        this.bloodClouds = new SliderSetting(this, "\u041e\u0431\u043b\u0430\u043a\u0430", 65.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.BLOOD_MOON);
        this.bloodMist = new SliderSetting(this, "\u041a\u0440\u0430\u0441\u043d\u0430\u044f \u0434\u044b\u043c\u043a\u0430", 55.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.BLOOD_MOON);
        this.bloodGround = new SliderSetting(this, "\u041e\u0442\u0442\u0435\u043d\u043e\u043a \u0437\u0435\u043c\u043b\u0438", 45.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.BLOOD_MOON);
        this.bloodEmbers = new SliderSetting(this, "\u0423\u0433\u043b\u0438", 35.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.BLOOD_MOON);
        this.bloodHeight = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 \u043b\u0443\u043d\u044b", 58.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.mode() == Mode.BLOOD_MOON);
        this.footprintRenderer = new WeatherFootprints();
        this.configMigrated = false;
    }

    public static Weather get() {
        return Instance.get(Weather.class);
    }

    public static boolean hidesVanillaClouds() {
        try {
            Weather weather = Weather.get();
            return weather != null && weather.isEnabled() && weather.vanillaClouds.getValue() == false;
        }
        catch (RuntimeException ignored) {
            return false;
        }
    }

    @Override
    public void setup() {
        super.setup();
        WorldRenderEvents.BEFORE_ENTITIES.register(context -> WeatherRenderer.getInstance().captureTerrain(this));
        WorldRenderEvents.END_MAIN.register(context -> WeatherRenderer.getInstance().render(this));
        WorldRenderEvents.END_MAIN.register(context -> this.footprintRenderer.render(this, context));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> WeatherRenderer.getInstance().close());
        ClientTickEvents.END_CLIENT_TICK.register(Weather::clientTick);
    }

    @Override
    protected void onEnable() {
        this.configMigrated = false;
    }

    private void migrateConfig() {
        boolean changed = false;
        if (!(this.mode == null || this.mode() != Mode.WINTER || this.snowCover.getValue().booleanValue() || this.footprints.getValue().booleanValue() || this.snowAllSurfaces.getValue().booleanValue() || this.frostWood.getValue().booleanValue())) {
            this.snowCover.set(true);
            this.footprints.set(true);
            this.snowAllSurfaces.set(true);
            this.frostWood.set(true);
            changed = true;
        }
        if (!(this.mode == null || this.mode() != Mode.AUTUMN || this.autumnWood.getValue().booleanValue() || this.autumnLeaves.getValue().booleanValue() || this.leafTrail.getValue().booleanValue())) {
            this.autumnWood.set(true);
            this.autumnLeaves.set(true);
            this.leafTrail.set(true);
            changed = true;
        }
        if (changed) {
            Client.get().configManager().save("auto");
        }
    }

    @Override
    protected void onDisable() {
        this.footprintRenderer.clear();
        EffectSoundController.stop("weather");
    }

    public Mode mode() {
        return Mode.values()[this.mode.getIndex()];
    }

    public Quality quality() {
        return Quality.values()[this.quality.getIndex()];
    }

    public static void clientTick(class_310 client) {
        boolean rain;
        boolean winter;
        Weather module = Weather.get();
        if (module == null || client == null) {
            return;
        }
        if (!module.configMigrated) {
            module.configMigrated = true;
            module.migrateConfig();
        }
        module.footprintRenderer.tick(module, client);
        float soundVolume = module.effectSound.getValue() != false ? module.effectSoundVolume.getValue().floatValue() / 100.0f * 0.32f : 0.0f;
        EffectSoundController.tick("weather", module.isEnabled() && module.mode() != Mode.OFF && module.mode() != Mode.RAIN, module.mode().ordinal(), soundVolume);
        boolean bl = winter = module.isEnabled() && module.mode() == Mode.WINTER;
        if (winter && !wasWinter) {
            for (class_2960 id : client.method_1483().method_4864()) {
                if (!module.muteWinterAmbience(id)) continue;
                client.method_1483().method_4875(id, null);
            }
        }
        wasWinter = winter;
        boolean bl2 = rain = client.field_1687 != null && client.field_1724 != null && module.isEnabled() && module.mode() == Mode.RAIN && module.rainIntensity.getValue().floatValue() > 0.0f && module.rainSound.getValue().floatValue() > 0.0f;
        if (rain) {
            if (rainSoundInstance == null || rainSoundInstance.method_4793() || !client.method_1483().method_4877((class_1113)rainSoundInstance)) {
                rainSoundInstance = new RainSound(module);
                client.method_1483().method_4873((class_1113)rainSoundInstance);
            }
        } else if (rainSoundInstance != null) {
            rainSoundInstance.stopSound();
            rainSoundInstance = null;
        }
    }

    public boolean muteWinterAmbience(class_2960 id) {
        if (!this.isEnabled() || this.mode() != Mode.WINTER) {
            return false;
        }
        if (!"minecraft".equals(id.method_12836())) {
            return false;
        }
        String path = id.method_12832();
        if (path.equals("ambient.cave")) {
            return true;
        }
        for (String prefix : MUTED_WINTER_PREFIXES) {
            if (!path.startsWith(prefix)) continue;
            return true;
        }
        return false;
    }

    static {
        MUTED_WINTER_PREFIXES = new String[]{"ambient.nether_wastes.", "ambient.soul_sand_valley.", "ambient.crimson_forest.", "ambient.warped_forest.", "ambient.basalt_deltas.", "music.nether."};
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Mode {
        OFF,
        RAIN,
        WINTER,
        SUNRISE,
        DUST,
        SAKURA,
        ECLIPSE,
        METEOR,
        BLOOD_MOON,
        SPRING,
        SUMMER,
        AUTUMN;

    }

    @Environment(value=EnvType.CLIENT)
    public static enum Quality {
        FAST,
        BALANCED,
        HIGH;

    }
}

