/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.minecraft.class_310
 */
package ru.decide.module.impl.render;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.class_310;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.ShaderSkyRenderer;
import ru.decide.utils.sound.EffectSoundController;

@ModuleInfo(name="Shader Sky", category=Category.VISUALS, desc="Shader sky with blur or procedural shader")
@Environment(value=EnvType.CLIENT)
public class ShaderSky
extends Module {
    private static ShaderSky instance;
    public static final String[] MODE_NAMES;
    public ModeSetting mode = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c", MODE_NAMES);
    public SliderSetting intensity = new SliderSetting(this, "\u0421\u0438\u043b\u0430", 1.0f, 0.1f, 1.0f, 0.05f);
    public SliderSetting speed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 1.0f, 0.1f, 3.0f, 0.1f);
    public SliderSetting scale = new SliderSetting(this, "\u041c\u0430\u0441\u0448\u0442\u0430\u0431", 1.1f, 0.45f, 3.0f, 0.05f);
    public SliderSetting stars = new SliderSetting(this, "\u0417\u0432\u0451\u0437\u0434\u044b", 0.75f, 0.0f, 1.0f, 0.05f);
    public SliderSetting sunAngle = new SliderSetting(this, "\u0421\u043e\u043b\u043d\u0446\u0435", 0.0f, 0.0f, 1.0f, 0.01f).setVisible(() -> this.mode.is("Origin") || this.mode.is("Sunset") || this.mode.is("SolarWind") || this.mode.is("LunarHalo") || this.mode.is("EclipseWave") || this.mode.is("MidnightRain") || this.mode.is("GoldenHour"));
    public ModeSetting typeColor = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439");
    public ColorSetting color1 = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 1", -8788225).setVisible(() -> this.typeColor.is("\u0421\u0432\u043e\u0439"));
    public ColorSetting color2 = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 2", -34074).setVisible(() -> this.typeColor.is("\u0421\u0432\u043e\u0439"));
    public BooleanSetting vanillaSky = new BooleanSetting((Module)this, "\u0412\u0430\u043d\u0438\u043b\u044c\u043d\u043e\u0435 \u043d\u0435\u0431\u043e", false);
    public BooleanSetting effectSound = new BooleanSetting((Module)this, "\u0417\u0432\u0443\u043a \u044d\u0444\u0444\u0435\u043a\u0442\u0430", true);
    public SliderSetting effectSoundVolume = new SliderSetting(this, "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0437\u0432\u0443\u043a\u0430", 12.0f, 0.0f, 100.0f, 1.0f).setVisible(() -> this.effectSound.getValue());

    public ShaderSky() {
        instance = this;
    }

    @Override
    public void setup() {
        super.setup();
        ClientTickEvents.END_CLIENT_TICK.register(ShaderSky::clientTick);
    }

    private static void clientTick(class_310 client) {
        ShaderSky module = instance;
        if (module == null || client == null) {
            return;
        }
        float volume = module.effectSound.getValue() != false ? module.effectSoundVolume.getValue().floatValue() / 100.0f * 0.26f : 0.0f;
        EffectSoundController.tick("shadersky", module.isEnabled(), module.mode.getIndex(), volume);
    }

    public static int getModeId(String name) {
        if ("AmbGalaxy".equalsIgnoreCase(name)) {
            return 18;
        }
        for (int i = 0; i < MODE_NAMES.length; ++i) {
            if (!MODE_NAMES[i].equalsIgnoreCase(name)) continue;
            return i;
        }
        return 0;
    }

    public static ShaderSky getInstance() {
        return instance;
    }

    public static boolean hidesClouds() {
        ShaderSky sky = instance;
        return sky != null && sky.isEnabled() && sky.vanillaSky.getValue() == false;
    }

    public static int geteColor2() {
        if (ShaderSky.getInstance().typeColor.is("\u0422\u0435\u043c\u0430")) {
            return ColorUtil.multDark(ColorUtil.getClientColor1(1), 0.25f);
        }
        return ShaderSky.getInstance().color2.getValue();
    }

    public static int geteColor1() {
        if (ShaderSky.getInstance().typeColor.is("\u0422\u0435\u043c\u0430")) {
            return ColorUtil.getClientColor1(1);
        }
        return ShaderSky.getInstance().color1.getValue();
    }

    @Override
    protected void onEnable() {
        ShaderSkyRenderer renderer = ShaderSkyRenderer.getInstance();
        renderer.invalidate();
        renderer.setEnabled(true);
    }

    @Override
    protected void onDisable() {
        ShaderSkyRenderer.getInstance().setEnabled(false);
        EffectSoundController.stop("shadersky");
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        ShaderSkyRenderer renderer = ShaderSkyRenderer.getInstance();
        renderer.invalidate();
        renderer.setEnabled(true);
    }

    @Generated
    public ModeSetting getMode() {
        return this.mode;
    }

    @Generated
    public SliderSetting getIntensity() {
        return this.intensity;
    }

    @Generated
    public SliderSetting getSpeed() {
        return this.speed;
    }

    @Generated
    public SliderSetting getScale() {
        return this.scale;
    }

    @Generated
    public SliderSetting getStars() {
        return this.stars;
    }

    @Generated
    public SliderSetting getSunAngle() {
        return this.sunAngle;
    }

    @Generated
    public ModeSetting getTypeColor() {
        return this.typeColor;
    }

    @Generated
    public ColorSetting getColor1() {
        return this.color1;
    }

    @Generated
    public ColorSetting getColor2() {
        return this.color2;
    }

    @Generated
    public BooleanSetting getVanillaSky() {
        return this.vanillaSky;
    }

    @Generated
    public BooleanSetting getEffectSound() {
        return this.effectSound;
    }

    @Generated
    public SliderSetting getEffectSoundVolume() {
        return this.effectSoundVolume;
    }

    static {
        MODE_NAMES = new String[]{"Aurora", "Night", "Snow", "Sky", "Star", "Glow", "Full", "WebShader", "Plasma", "ChamsFill", "BaseWarp", "Waves", "PhobAurora", "BlackHole", "Galaxy", "MilkyWay", "Origin", "QuantumNebula", "Space", "SpiralGalaxy", "Darkness", "Pseudo", "Cyclone", "Lightning", "Juice", "Nebula", "Sunset", "Clouds", "SolarStorm", "AuroraVeil", "SolarWind", "FrozenNebula", "EmberSky", "Oceanic", "VoidRift", "Prism", "MeteorShower", "LunarHalo", "ElectricDream", "RoseQuartz", "EmeraldDrift", "EclipseWave", "CosmicDust", "IonStorm", "CelestialBloom", "MidnightRain", "GoldenHour", "Abyssal", "NebulaBloom", "CrystalVeil", "SolarEcho", "LunarTide", "AstralDrift", "NovaPulse", "StarlitOcean", "Frostfire", "AmberDusk", "VioletStorm", "EmeraldNebula", "CrimsonHalo", "AzureRift", "GoldenNebula", "SilverRain", "PhantomSky", "ObsidianWave", "OpalGlow", "SapphireDrift", "RubyNebula", "JadeHorizon", "AmethystSky", "TopazStorm", "OnyxVeil", "PearlGlow", "NeonAbyss", "CosmicBloom", "AstralRain", "VoidBloom", "Starfall", "MoonlitDrift", "Sunfire", "IceNebula", "StormGlass", "Dreamscape", "Ethereal", "AuroraDepth", "CelestialSea", "GalacticWing", "NebulaStorm", "RadiantVoid", "ShimmerSky", "TwilightVeil", "Zenith", "Eventide", "FirstLight", "Nightfall", "Dawnbreak", "Elysium", "Infinity"};
    }
}

