/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1101
 *  net.minecraft.class_1113
 *  net.minecraft.class_2960
 *  net.minecraft.class_3414
 *  net.minecraft.class_3419
 */
package ru.decide.module.impl.render.weather;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1101;
import net.minecraft.class_1113;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import ru.decide.module.impl.render.Weather;

@Environment(value=EnvType.CLIENT)
public final class RainSound
extends class_1101 {
    private final Weather module;

    public RainSound(Weather module) {
        super(class_3414.method_47908((class_2960)class_2960.method_60655((String)"decide", (String)"rain_continuous")), class_3419.field_15252, class_1113.method_43221());
        this.module = module;
        this.field_5446 = true;
        this.field_5451 = 0;
        this.field_5442 = 0.01f;
        this.field_18936 = true;
    }

    public void stopSound() {
        this.method_24876();
    }

    public void method_16896() {
        if (this.module.isEnabled() && this.module.mode() == Weather.Mode.RAIN) {
            float target = this.module.rainSound.getValue().floatValue() / 100.0f * (this.module.rainIntensity.getValue().floatValue() / 100.0f);
            this.field_5442 = Math.max(0.001f, target);
        } else {
            this.method_24876();
        }
    }
}

