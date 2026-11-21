/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3414
 */
package ru.decide.module.impl.utils.consumable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3414;

@Environment(value=EnvType.CLIENT)
public class ConsumptionStateHandler {
    private boolean suppressServerBurp;
    private boolean suppressEquipmentAnimation;
    private class_3414 soundToSuppress;
    private int trackerState;
    private long trackerTriggerTime;
    private volatile boolean waitingForServer;
    private volatile long serverWaitStartTime;

    public void startServerWait() {
        this.waitingForServer = true;
        this.serverWaitStartTime = System.currentTimeMillis();
    }

    public boolean isWaitingForServer() {
        if (this.waitingForServer && System.currentTimeMillis() - this.serverWaitStartTime > 500L) {
            this.waitingForServer = false;
        }
        return this.waitingForServer;
    }

    public void stopServerWait() {
        this.waitingForServer = false;
    }

    public void suppressBurp() {
        this.suppressServerBurp = true;
    }

    public void suppressEquipmentAnimation() {
        this.suppressEquipmentAnimation = true;
    }

    public boolean shouldSuppressBurp() {
        return this.suppressServerBurp;
    }

    public boolean shouldSuppressEquipmentAnimation() {
        return this.suppressEquipmentAnimation;
    }

    public void resetEquipmentAnimation() {
        this.suppressEquipmentAnimation = false;
    }

    public void resetBurpSuppression() {
        this.suppressServerBurp = false;
    }

    public void setSoundSuppression(class_3414 sound) {
        this.soundToSuppress = sound;
    }

    public boolean isSoundSuppressed(class_3414 sound) {
        return this.soundToSuppress != null && this.soundToSuppress.equals((Object)sound);
    }

    public void clearSoundSuppression() {
        this.soundToSuppress = null;
    }
}

