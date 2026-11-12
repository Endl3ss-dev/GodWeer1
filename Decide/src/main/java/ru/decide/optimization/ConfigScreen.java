/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_332
 *  net.minecraft.class_357
 *  net.minecraft.class_364
 *  net.minecraft.class_4185
 *  net.minecraft.class_437
 *  net.minecraft.class_5250
 *  net.minecraft.class_5676
 */
package ru.decide.optimization;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_357;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import net.minecraft.class_5676;
import ru.decide.optimization.ModConfig;
import ru.decide.optimization.RenderLimiter;
import ru.decide.optimization.RenderManager;

@Environment(value=EnvType.CLIENT)
public class ConfigScreen
extends class_437 {
    private static final int MIN_HZ = 30;
    private static final int HZ_RANGE = 210;
    private static final double UNLIMITED_THRESHOLD = 0.999;
    private final class_437 parent;
    private final ModConfig config;
    private class_357 hzSlider;
    private class_5676<Boolean> enableButton;
    private class_5676<Boolean> autoDetectButton;
    private class_5676<Boolean> fpsIndicatorButton;
    private class_357 minFpsSlider;
    private class_5676<Boolean> lowEndModeButton;

    public ConfigScreen(class_437 parent) {
        super((class_2561)class_2561.method_43470((String)"Achrone Optimization"));
        this.parent = parent;
        this.config = ModConfig.getInstance();
    }

    protected void method_25426() {
        this.method_37067();
        int centerX = this.field_22789 / 2;
        int startY = this.field_22790 / 4;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 25;
        class_5250 initialHzText = this.config.isUnlimitedFps() ? class_2561.method_43470((String)"\u041c\u043e\u043d\u0438\u0442\u043e\u0440: Unlimited") : class_2561.method_43470((String)("\u041c\u043e\u043d\u0438\u0442\u043e\u0440: " + this.config.targetMonitorHz + " Hz"));
        double initialHzValue = this.config.isUnlimitedFps() ? 1.0 : ((double)this.config.targetMonitorHz - 30.0) / 210.0;
        this.hzSlider = new class_357(centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight, (class_2561)initialHzText, initialHzValue){

            protected void method_25346() {
                if (this.field_22753 >= 0.999) {
                    this.method_25355((class_2561)class_2561.method_43470((String)"\u041c\u043e\u043d\u0438\u0442\u043e\u0440: Unlimited"));
                } else {
                    int hz = (int)(30.0 + this.field_22753 * 210.0);
                    this.method_25355((class_2561)class_2561.method_43470((String)("\u041c\u043e\u043d\u0438\u0442\u043e\u0440: " + hz + " Hz")));
                }
            }

            protected void method_25344() {
                if (this.field_22753 >= 0.999) {
                    ConfigScreen.this.config.setUnlimitedFps(true);
                } else {
                    int hz = (int)(30.0 + this.field_22753 * 210.0);
                    ConfigScreen.this.config.setTargetMonitorHz(hz);
                }
                ConfigScreen.this.applyConfigToLimiter();
            }
        };
        this.method_37063((class_364)this.hzSlider);
        this.enableButton = class_5676.method_32607((class_2561)class_2561.method_43470((String)"\u0412\u043a\u043b\u044e\u0447\u0451\u043d"), (class_2561)class_2561.method_43470((String)"\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d"), (boolean)this.config.enableRenderLimit).method_32617(centerX - buttonWidth / 2, startY + spacing, buttonWidth, buttonHeight, (class_2561)class_2561.method_43470((String)"\u041e\u0433\u0440\u0430\u043d\u0438\u0447\u0435\u043d\u0438\u0435 \u0440\u0435\u043d\u0434\u0435\u0440\u0430"), (button, enabled) -> {
            this.config.setEnableRenderLimit((boolean)enabled);
            this.applyConfigToLimiter();
        });
        this.method_37063((class_364)this.enableButton);
        this.autoDetectButton = class_5676.method_32607((class_2561)class_2561.method_43470((String)"\u0410\u0432\u0442\u043e: \u0412\u043a\u043b"), (class_2561)class_2561.method_43470((String)"\u0410\u0432\u0442\u043e: \u0412\u044b\u043a\u043b"), (boolean)this.config.autoDetectMonitorHz).method_32617(centerX - buttonWidth / 2, startY + spacing * 2, buttonWidth, buttonHeight, (class_2561)class_2561.method_43470((String)"\u0410\u0432\u0442\u043e\u043e\u043f\u0440\u0435\u0434\u0435\u043b\u0435\u043d\u0438\u0435 Hz"), (button, autoDetect) -> {
            this.config.setAutoDetectMonitorHz((boolean)autoDetect);
            if (autoDetect.booleanValue()) {
                int detectedHz = RenderLimiter.detectMonitorHz();
                this.config.setTargetMonitorHz(detectedHz);
                this.applyConfigToLimiter();
                this.method_25426();
            }
        });
        this.method_37063((class_364)this.autoDetectButton);
        this.minFpsSlider = new class_357(centerX - buttonWidth / 2, startY + spacing * 3, buttonWidth, buttonHeight, (class_2561)class_2561.method_43470((String)("\u041c\u0438\u043d. FPS: " + this.config.minFpsLimit)), ((double)this.config.minFpsLimit - 15.0) / 105.0){

            protected void method_25346() {
                int minFps = (int)(15.0 + this.field_22753 * 105.0);
                this.method_25355((class_2561)class_2561.method_43470((String)("\u041c\u0438\u043d. FPS: " + minFps)));
            }

            protected void method_25344() {
                int minFps = (int)(15.0 + this.field_22753 * 105.0);
                ConfigScreen.this.config.setMinFpsLimit(minFps);
                ConfigScreen.this.applyConfigToLimiter();
            }
        };
        this.method_37063((class_364)this.minFpsSlider);
        this.fpsIndicatorButton = class_5676.method_32607((class_2561)class_2561.method_43470((String)"\u041f\u043e\u043a\u0430\u0437"), (class_2561)class_2561.method_43470((String)"\u0421\u043a\u0440\u044b\u0442\u044c"), (boolean)this.config.showFpsIndicator).method_32617(centerX - buttonWidth / 2, startY + spacing * 4, buttonWidth, buttonHeight, (class_2561)class_2561.method_43470((String)"\u0418\u043d\u0434\u0438\u043a\u0430\u0442\u043e\u0440 FPS"), (button, show) -> this.config.setShowFpsIndicator((boolean)show));
        this.method_37063((class_364)this.fpsIndicatorButton);
        this.lowEndModeButton = class_5676.method_32607((class_2561)class_2561.method_43470((String)"\u0412\u043a\u043b"), (class_2561)class_2561.method_43470((String)"\u0412\u044b\u043a\u043b"), (boolean)this.config.lowEndMode).method_32617(centerX - buttonWidth / 2, startY + spacing * 5, buttonWidth, buttonHeight, (class_2561)class_2561.method_43470((String)"Low-End \u0440\u0435\u0436\u0438\u043c"), (button, lowEnd) -> {
            this.config.setLowEndMode((boolean)lowEnd);
            this.applyConfigToLimiter();
        });
        this.method_37063((class_364)this.lowEndModeButton);
        this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438"), button -> {
            this.config.resetToDefaults();
            this.applyConfigToLimiter();
            this.method_25426();
        }).method_46434(centerX - buttonWidth / 2, startY + spacing * 6, buttonWidth, buttonHeight).method_46431());
        this.method_37063((class_364)class_4185.method_46430((class_2561)class_2561.method_43470((String)"\u0413\u043e\u0442\u043e\u0432\u043e"), button -> this.method_25419()).method_46434(centerX - buttonWidth / 2, startY + spacing * 7, buttonWidth, buttonHeight).method_46431());
    }

    public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
        context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 20, 0xFFFFFF);
        String configuredLimit = this.config.isUnlimitedFps() ? "Unlimited" : this.config.targetMonitorHz + " Hz";
        String info = String.format("Current: %s, %s", configuredLimit, this.config.enableRenderLimit ? "Enabled" : "Disabled");
        context.method_27534(this.field_22793, (class_2561)class_2561.method_43470((String)info), this.field_22789 / 2, this.field_22790 - 30, 0xAAAAAA);
        super.method_25394(context, mouseX, mouseY, delta);
    }

    public void method_25419() {
        this.field_22787.method_1507(this.parent);
    }

    private void applyConfigToLimiter() {
        RenderManager.getInstance().updateSettings(this.config.getEffectiveHz(), this.config.enableRenderLimit);
    }
}

