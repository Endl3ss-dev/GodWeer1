/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.manager.events.Event;
import ru.decide.manager.events.orbit.ICancellable;

@Environment(value=EnvType.CLIENT)
public class CancellableEvent
extends Event
implements ICancellable {
    private boolean cancelled;

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public void cancel() {
        ICancellable.super.cancel();
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}

