/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.entity.Entity;

public class EventTotemPop
extends EventBase
implements CancellableEvent {
    private Entity cfr_renamed_136;

    public EventTotemPop(Entity param1) {
        this.cfr_renamed_136 = param1;
    }

    public Entity method2450() {
        return this.cfr_renamed_136;
    }
}

