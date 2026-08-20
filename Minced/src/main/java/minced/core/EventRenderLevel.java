/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.client.util.math.MatrixStack;

public class EventRenderLevel
extends EventBase
implements CancellableEvent {
    float field9027;
    MatrixStack I;

    public float method2495() {
        return this.field9027;
    }

    public EventRenderLevel(MatrixStack param1, float param2) {
        this.I = param1;
        this.field9027 = param2;
    }

    public MatrixStack method2496() {
        return this.I;
    }
}



