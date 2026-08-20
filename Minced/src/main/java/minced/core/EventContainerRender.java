/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.screen.ScreenHandler
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenHandler;
import sLM._4ZXYEo;

public class EventContainerRender
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_218;

    int field9128;
    ScreenHandler field9129;
    DrawContext field9130;
    int field9131;

    public String toString() {
        return _4ZXYEo.ф4("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2609()), (int)this.method2607(), (int)this.method2608(), (String)String.valueOf(this.method2606()));
    }

    public void method2601(int param1) {
        this.field9128 = param1;
    }

    public void method2602(ScreenHandler param1) {
        this.field9129 = param1;
    }

    public void method2603(DrawContext param1) {
        this.field9130 = param1;
    }

    public int hashCode() {
        int v2 = 59 + this.method2607();
        v2 = v2 * 59 + this.method2608();
        DrawContext v3 = this.method2609();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        ScreenHandler v4 = this.method2606();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    protected boolean method2604(Object param1) {
        return param1 instanceof EventContainerRender;
    }

    public void method2605(int param1) {
        this.field9131 = param1;
    }

    public ScreenHandler method2606() {
        return this.field9129;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventContainerRender)) {
            return false;
        }
        EventContainerRender v2 = (EventContainerRender)param1;
        if (!v2.method2604(this)) {
            return false;
        }
        if (this.method2607() != v2.method2607()) {
            return false;
        }
        if (this.method2608() != v2.method2608()) {
            return false;
        }
        DrawContext v3 = this.method2609();
        DrawContext v4 = v2.method2609();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        ScreenHandler v5 = this.method2606();
        ScreenHandler v6 = v2.method2606();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public int method2607() {
        return this.field9128;
    }

    public int method2608() {
        return this.field9131;
    }

    public EventContainerRender(DrawContext param1, int param2, int param3, ScreenHandler param4) {
        this.field9130 = param1;
        this.field9128 = param2;
        this.field9131 = param3;
        this.field9129 = param4;
    }

    public DrawContext method2609() {
        return this.field9130;
    }

    static {
        cfr_renamed_218 = "EventContainerRender(stack=\u0001, guiLeft=\u0001, guiTop=\u0001, container=\u0001)";
    }
}

