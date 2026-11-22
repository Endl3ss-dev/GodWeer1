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
package ru.decide.utils.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1101;
import net.minecraft.class_1113;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import ru.decide.utils.sound.EffectSoundController;

@Environment(value=EnvType.CLIENT)
public final class EffectAmbienceSound
extends class_1101 {
    private final String owner;
    private final int token;
    private float targetVolume;
    private float currentVolume;
    private final float fadeSpeed = 0.08f;
    private boolean fadingOut = false;

    public EffectAmbienceSound(String owner, String soundPath, int token, float volume, float pitch) {
        super(class_3414.method_47908((class_2960)class_2960.method_60655((String)"minecraft", (String)soundPath)), class_3419.field_15256, class_1113.method_43221());
        this.owner = owner;
        this.token = token;
        this.targetVolume = Math.max(0.001f, volume);
        this.currentVolume = 0.001f;
        this.field_5446 = true;
        this.field_5451 = 1;
        this.field_18936 = true;
        this.field_5441 = Math.max(0.35f, pitch);
    }

    public void setBaseVolume(float volume) {
        this.targetVolume = Math.max(0.001f, volume);
    }

    public float getBaseVolume() {
        return this.currentVolume;
    }

    public void stopSound() {
        this.fadingOut = true;
    }

    public void method_16896() {
        if (!EffectSoundController.isActive(this.owner, this.token)) {
            this.fadingOut = true;
        }
        if (this.fadingOut) {
            this.currentVolume = Math.max(0.0f, this.currentVolume - 0.08f);
            if (this.currentVolume <= 0.001f) {
                this.method_24876();
                return;
            }
        } else {
            this.currentVolume = Math.min(this.targetVolume, this.currentVolume + 0.08f);
        }
        this.field_5442 = this.currentVolume;
    }
}

