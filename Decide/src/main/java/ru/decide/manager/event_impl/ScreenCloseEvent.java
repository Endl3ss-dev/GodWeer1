/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_437
 */
package ru.decide.manager.event_impl;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_437;
import ru.decide.manager.events.CancellableEvent;

@Environment(value=EnvType.CLIENT)
public class ScreenCloseEvent
extends CancellableEvent {
    private class_437 screen;
    private int windowId;

    @Generated
    public class_437 getScreen() {
        return this.screen;
    }

    @Generated
    public int getWindowId() {
        return this.windowId;
    }

    @Generated
    public ScreenCloseEvent(class_437 screen, int windowId) {
        this.screen = screen;
        this.windowId = windowId;
    }
}

