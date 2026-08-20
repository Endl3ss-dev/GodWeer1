/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.RenderTickCounter
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import sLM._4ZXYEo;

public class EventHotbarRender
extends EventBase
implements CancellableEvent {
    static String фс;

    DrawContext field9120;
    RenderTickCounter field9121;

    public void l(DrawContext param1) {
        this.field9120 = param1;
    }

    public EventHotbarRender(DrawContext param1, RenderTickCounter param2) {
        this.field9120 = param1;
        this.field9121 = param2;
    }

    protected boolean method2593(Object param1) {
        return param1 instanceof EventHotbarRender;
    }

    public DrawContext method2594() {
        return this.field9120;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventHotbarRender)) {
            return false;
        }
        EventHotbarRender v2 = (EventHotbarRender)param1;
        if (!v2.method2593(this)) {
            return false;
        }
        DrawContext v3 = this.method2594();
        DrawContext v4 = v2.method2594();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        RenderTickCounter v5 = this.method2595();
        RenderTickCounter v6 = v2.method2595();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public RenderTickCounter method2595() {
        return this.field9121;
    }

    public int hashCode() {
        DrawContext v3 = this.method2594();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        RenderTickCounter v4 = this.method2595();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    public void method2596(RenderTickCounter param1) {
        this.field9121 = param1;
    }

    public String toString() {
        return _4ZXYEo.ъ("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2594()), (String)String.valueOf(this.method2595()));
    }

    static {
        фс = "EventHotbarRender(stack=\u0001, partialTicks=\u0001)";
    }
}

