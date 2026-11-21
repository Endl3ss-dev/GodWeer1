/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.render;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.manager.event_impl.GlassHandsRenderEvent;
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
import ru.decide.utils.render.GlassHandsRenderer;

@ModuleInfo(name="Glass Hands", category=Category.VISUALS, desc="\u0414\u0435\u043b\u0430\u0435\u0442 \u0440\u0443\u043a\u0438 \u0438 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0441\u0442\u0435\u043a\u043b\u044f\u043d\u043d\u044b\u043c\u0438")
@Environment(value=EnvType.CLIENT)
public class GlassHands
extends Module {
    private static GlassHands instance;
    public BooleanSetting enableBlur = new BooleanSetting((Module)this, "\u0411\u043b\u044e\u0440", true);
    public SliderSetting blurRadius = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0440\u0430\u0437\u043c\u044b\u0442\u0438\u044f", 2.5f, 1.0f, 5.0f, 0.1f).setVisible(() -> this.enableBlur.getValue());
    public SliderSetting blurIterations = new SliderSetting(this, "\u041a\u0430\u0447\u0435\u0441\u0442\u0432\u043e", 3.0f, 1.0f, 5.0f, 1.0f).setVisible(() -> this.enableBlur.getValue());
    public SliderSetting saturation = new SliderSetting(this, "\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c", 0.0f, 0.0f, 2.0f, 0.1f).setVisible(() -> this.enableBlur.getValue());
    public SliderSetting tintIntensity = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u043e\u0442\u0442\u0435\u043d\u043a\u0430", 0.2f, 0.0f, 1.0f, 0.01f).setVisible(() -> this.enableBlur.getValue());
    public SliderSetting edgeSoftness = new SliderSetting(this, "\u0421\u0433\u043b\u0430\u0436\u0438\u0432\u0430\u043d\u0438\u0435 \u043a\u0440\u0430\u0451\u0432", 0.0f, 0.0f, 28.0f, 1.0f).setVisible(() -> this.enableBlur.getValue());
    public ModeSetting typeColor = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0446\u0432\u0435\u0442\u0430", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439");
    public ColorSetting tintColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442", -16711681).setVisible(() -> this.typeColor.is("\u0421\u0432\u043e\u0439"));
    public BooleanSetting enableIce = new BooleanSetting((Module)this, "\u0418\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u0435", false);
    public SliderSetting iceIntensity = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0438\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u044f", 1.0f, 0.0f, 2.0f, 0.05f).setVisible(() -> this.enableIce.getValue());
    public SliderSetting refractReach = new SliderSetting(this, "\u0428\u0438\u0440\u0438\u043d\u0430 \u0438\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u044f", 25.0f, 0.0f, 80.0f, 1.0f).setVisible(() -> this.enableIce.getValue());
    public BooleanSetting enableFire = new BooleanSetting((Module)this, "\u041e\u0433\u043e\u043d\u044c", false);
    public SliderSetting fireIntensity = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u043e\u0433\u043d\u044f", 1.18f, 0.2f, 2.0f, 0.02f).setVisible(() -> this.enableFire.getValue());
    public SliderSetting fireRadius = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u043f\u043b\u0430\u043c\u0435\u043d\u0438", 15.0f, 4.0f, 40.0f, 1.0f).setVisible(() -> this.enableFire.getValue());
    public SliderSetting fireSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043e\u0433\u043d\u044f", 1.1f, 0.2f, 3.0f, 0.05f).setVisible(() -> this.enableFire.getValue());
    public SliderSetting fireHeight = new SliderSetting(this, "\u0412\u044b\u0441\u043e\u0442\u0430 \u0434\u044b\u043c\u0430", 28.0f, 5.0f, 56.0f, 1.0f).setVisible(() -> this.enableFire.getValue());
    public SliderSetting fireTrail = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0434\u044b\u043c\u0430", 1.16f, 0.0f, 2.0f, 0.02f).setVisible(() -> this.enableFire.getValue());
    public ModeSetting fireColorMode = new ModeSetting((Module)this, "\u0426\u0432\u0435\u0442 \u043e\u0433\u043d\u044f", "\u041f\u0440\u0435\u0434\u043c\u0435\u0442", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439").setVisible(() -> this.enableFire.getValue());
    public ColorSetting fireCustomColor = new ColorSetting((Module)this, "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u043e\u0433\u043d\u044f", -32730).setVisible(() -> this.enableFire.getValue() != false && this.fireColorMode.is("\u0421\u0432\u043e\u0439"));
    public BooleanSetting fireBurnThrough = new BooleanSetting((Module)this, "\u041f\u0440\u043e\u0436\u0438\u0433", false).setVisible(() -> this.enableFire.getValue());
    public BooleanSetting enableEdgeGlow = new BooleanSetting((Module)this, "\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u043a\u0440\u0430\u0451\u0432", true);
    public SliderSetting edgeGlowIntensity = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 0.2f, 0.0f, 1.0f, 0.01f).setVisible(() -> this.enableEdgeGlow.getValue());
    public ModeSetting edgeGlowColorMode = new ModeSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439").setVisible(() -> this.enableEdgeGlow.getValue());
    public ColorSetting edgeGlowCustomColor = new ColorSetting((Module)this, "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", -16711681).setVisible(() -> this.enableEdgeGlow.getValue() != false && this.edgeGlowColorMode.is("\u0421\u0432\u043e\u0439"));
    public BooleanSetting shimmer = new BooleanSetting((Module)this, "\u0428\u0438\u043c\u043c\u0435\u0440", true).setVisible(() -> this.enableEdgeGlow.getValue());
    public SliderSetting shimmerWidth = new SliderSetting(this, "\u0428\u0438\u0440\u0438\u043d\u0430 \u0448\u0438\u043c\u043c\u0435\u0440\u0430", 0.04f, 0.01f, 0.15f, 0.01f).setVisible(() -> this.enableEdgeGlow.getValue() != false && this.shimmer.getValue() != false);
    public SliderSetting shimmerPeriod = new SliderSetting(this, "\u041f\u0435\u0440\u0438\u043e\u0434 \u0448\u0438\u043c\u043c\u0435\u0440\u0430", 5.0f, 1.0f, 15.0f, 0.5f).setVisible(() -> this.enableEdgeGlow.getValue() != false && this.shimmer.getValue() != false);

    public int getColor() {
        if (this.typeColor.is("\u0422\u0435\u043c\u0430")) {
            return ColorUtil.getClientColor1(1);
        }
        return this.tintColor.getValue();
    }

    public GlassHands() {
        instance = this;
    }

    public static GlassHands getInstance() {
        return instance;
    }

    @Override
    protected void onEnable() {
        GlassHandsRenderer renderer = GlassHandsRenderer.getInstance();
        renderer.invalidate();
        renderer.setEnabled(true);
        this.applyRendererSettings();
    }

    @Override
    protected void onDisable() {
        GlassHandsRenderer.getInstance().setEnabled(false);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        GlassHandsRenderer renderer = GlassHandsRenderer.getInstance();
        renderer.invalidate();
        renderer.setEnabled(true);
        this.applyRendererSettings();
    }

    @EventHandler
    public void onGlassHandsRender(GlassHandsRenderEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        GlassHandsRenderer renderer = GlassHandsRenderer.getInstance();
        this.applyRendererSettings();
        if (event.getPhase() == GlassHandsRenderEvent.Phase.PRE) {
            renderer.captureSceneBeforeHands();
        } else if (event.getPhase() == GlassHandsRenderEvent.Phase.POST) {
            renderer.captureSceneAfterHands();
            renderer.renderGlassEffect();
        }
    }

    public void applyRendererSettings() {
        GlassHandsRenderer renderer = GlassHandsRenderer.getInstance();
        renderer.setBlurEnabled(this.enableBlur.getValue());
        renderer.setBlurRadius(this.blurRadius.getValue().floatValue());
        renderer.setBlurIterations(this.blurIterations.getValue().intValue());
        renderer.setSaturation(this.saturation.getValue().floatValue());
        renderer.setEdgeSoftness(this.edgeSoftness.getValue().floatValue());
        if (this.enableIce.getValue().booleanValue()) {
            renderer.setReflect(true);
            renderer.setIceIntensity(this.iceIntensity.getValue().floatValue());
            renderer.setFrostScale(this.refractReach.getValue().floatValue());
            renderer.setCrackIntensity(0.5f);
        } else {
            renderer.setReflect(false);
            renderer.setIceIntensity(0.0f);
            renderer.setFrostScale(0.0f);
            renderer.setCrackIntensity(0.0f);
        }
        renderer.setSmokeAmount(0.0f);
        renderer.setFireEnabled(this.enableFire.getValue());
        if (this.enableFire.getValue().booleanValue()) {
            renderer.setFireStrength(this.fireIntensity.getValue().floatValue());
            renderer.setFireRadius(this.fireRadius.getValue().floatValue());
            renderer.setFireSpeed(this.fireSpeed.getValue().floatValue());
            renderer.setFireHeight(this.fireHeight.getValue().floatValue());
            renderer.setFireTrailStrength(this.fireTrail.getValue().floatValue());
            renderer.setFireFillMode(this.fireBurnThrough.getValue());
            if (this.fireColorMode.is("\u041f\u0440\u0435\u0434\u043c\u0435\u0442")) {
                renderer.setFireColorMix(0.0f);
            } else if (this.fireColorMode.is("\u0422\u0435\u043c\u0430")) {
                renderer.setFireColor(ColorUtil.getClientColor1(1) & 0xFFFFFF);
                renderer.setFireColorMix(1.0f);
            } else {
                renderer.setFireColor((int)(this.fireCustomColor.getValue() & 0xFFFFFF));
                renderer.setFireColorMix(1.0f);
            }
        }
        if (this.enableBlur.getValue().booleanValue()) {
            renderer.setTintColor(this.getColor());
            renderer.setTintIntensity(this.tintIntensity.getValue().floatValue());
        } else {
            renderer.setTintColor(0);
            renderer.setTintIntensity(0.0f);
        }
        renderer.setOutlineEnabled(this.enableEdgeGlow.getValue());
        if (this.enableEdgeGlow.getValue().booleanValue()) {
            float strength = this.edgeGlowIntensity.getValue().floatValue() * 20.0f;
            renderer.setOutlineGlowStrength(strength);
            if (this.edgeGlowColorMode.is("\u0422\u0435\u043c\u0430")) {
                renderer.setOutlineUseItemColor(false);
                renderer.setOutlineColor(0xFF000000 | ColorUtil.getClientColor1(1) & 0xFFFFFF);
            } else {
                renderer.setOutlineUseItemColor(false);
                renderer.setOutlineColor(0xFF000000 | this.edgeGlowCustomColor.getValue() & 0xFFFFFF);
            }
            renderer.setShimmerEnabled(this.shimmer.getValue());
            renderer.setShimmerWidth(this.shimmerWidth.getValue().floatValue());
            renderer.setShimmerPeriodSec(this.shimmerPeriod.getValue().floatValue());
        }
    }

    @Generated
    public BooleanSetting getEnableBlur() {
        return this.enableBlur;
    }

    @Generated
    public SliderSetting getBlurRadius() {
        return this.blurRadius;
    }

    @Generated
    public SliderSetting getBlurIterations() {
        return this.blurIterations;
    }

    @Generated
    public SliderSetting getSaturation() {
        return this.saturation;
    }

    @Generated
    public SliderSetting getTintIntensity() {
        return this.tintIntensity;
    }

    @Generated
    public SliderSetting getEdgeSoftness() {
        return this.edgeSoftness;
    }

    @Generated
    public ModeSetting getTypeColor() {
        return this.typeColor;
    }

    @Generated
    public ColorSetting getTintColor() {
        return this.tintColor;
    }

    @Generated
    public BooleanSetting getEnableIce() {
        return this.enableIce;
    }

    @Generated
    public SliderSetting getIceIntensity() {
        return this.iceIntensity;
    }

    @Generated
    public SliderSetting getRefractReach() {
        return this.refractReach;
    }

    @Generated
    public BooleanSetting getEnableFire() {
        return this.enableFire;
    }

    @Generated
    public SliderSetting getFireIntensity() {
        return this.fireIntensity;
    }

    @Generated
    public SliderSetting getFireRadius() {
        return this.fireRadius;
    }

    @Generated
    public SliderSetting getFireSpeed() {
        return this.fireSpeed;
    }

    @Generated
    public SliderSetting getFireHeight() {
        return this.fireHeight;
    }

    @Generated
    public SliderSetting getFireTrail() {
        return this.fireTrail;
    }

    @Generated
    public ModeSetting getFireColorMode() {
        return this.fireColorMode;
    }

    @Generated
    public ColorSetting getFireCustomColor() {
        return this.fireCustomColor;
    }

    @Generated
    public BooleanSetting getFireBurnThrough() {
        return this.fireBurnThrough;
    }

    @Generated
    public BooleanSetting getEnableEdgeGlow() {
        return this.enableEdgeGlow;
    }

    @Generated
    public SliderSetting getEdgeGlowIntensity() {
        return this.edgeGlowIntensity;
    }

    @Generated
    public ModeSetting getEdgeGlowColorMode() {
        return this.edgeGlowColorMode;
    }

    @Generated
    public ColorSetting getEdgeGlowCustomColor() {
        return this.edgeGlowCustomColor;
    }

    @Generated
    public BooleanSetting getShimmer() {
        return this.shimmer;
    }

    @Generated
    public SliderSetting getShimmerWidth() {
        return this.shimmerWidth;
    }

    @Generated
    public SliderSetting getShimmerPeriod() {
        return this.shimmerPeriod;
    }
}

