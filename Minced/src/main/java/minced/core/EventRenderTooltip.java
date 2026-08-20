/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.item.ItemStack
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import sLM._4ZXYEo;

public class EventRenderTooltip
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_219;

    public ItemStack field9133;
    public DrawContext field9134;
    public int field9135;
    public int field9136;

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventRenderTooltip)) {
            return false;
        }
        EventRenderTooltip v2 = (EventRenderTooltip)param1;
        if (!v2.method2614(this)) {
            return false;
        }
        if (this.method2616() != v2.method2616()) {
            return false;
        }
        if (this.method2615() != v2.method2615()) {
            return false;
        }
        DrawContext v3 = this.method2613();
        DrawContext v4 = v2.method2613();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        ItemStack v5 = this.method2612();
        ItemStack v6 = v2.method2612();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public int hashCode() {
        int v2 = 59 + this.method2616();
        v2 = v2 * 59 + this.method2615();
        DrawContext v3 = this.method2613();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        ItemStack v4 = this.method2612();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    public ItemStack method2612() {
        return this.field9133;
    }

    public DrawContext method2613() {
        return this.field9134;
    }

    protected boolean method2614(Object param1) {
        return param1 instanceof EventRenderTooltip;
    }

    public EventRenderTooltip(DrawContext param1, ItemStack param2, int param3, int param4) {
        this.field9134 = param1;
        this.field9133 = param2;
        this.field9135 = param3;
        this.field9136 = param4;
    }

    public int method2615() {
        return this.field9136;
    }

    public String toString() {
        return _4ZXYEo.ду("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2613()), (String)String.valueOf(this.method2612()), (int)this.method2616(), (int)this.method2615());
    }

    public int method2616() {
        return this.field9135;
    }

    static {
        cfr_renamed_219 = "EventRenderTooltip(matrixStack=\u0001, stack=\u0001, mouseX=\u0001, mouseY=\u0001)";
    }
}



