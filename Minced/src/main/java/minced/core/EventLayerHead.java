/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import sLM._4ZXYEo;

public class EventLayerHead
extends EventBase
implements CancellableEvent {
    static String 西Т;

    LivingEntity field8487;
    MatrixStack field8488;
    EntityModel field8489;

    public String toString() {
        return _4ZXYEo.иэ("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(this.method2337()), (String)String.valueOf(this.method2336()), (String)String.valueOf(this.method2335()));
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventLayerHead)) {
            return false;
        }
        EventLayerHead v2 = (EventLayerHead)param1;
        if (!v2.method2334(this)) {
            return false;
        }
        LivingEntity v3 = this.method2337();
        LivingEntity v4 = v2.method2337();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        MatrixStack v5 = this.method2336();
        MatrixStack v6 = v2.method2336();
        if (v5 == null ? v6 != null : !v5.equals(v6)) {
            return false;
        }
        EntityModel v7 = this.method2335();
        EntityModel v8 = v2.method2335();
        return !(v7 == null ? v8 != null : !v7.equals(v8));
    }

    public EventLayerHead(LivingEntity param1, MatrixStack param2, EntityModel param3) {
        this.field8487 = param1;
        this.field8488 = param2;
        this.field8489 = param3;
    }

    public void method2333(LivingEntity param1) {
        this.field8487 = param1;
    }

    protected boolean method2334(Object param1) {
        return param1 instanceof EventLayerHead;
    }

    public EntityModel method2335() {
        return this.field8489;
    }

    public MatrixStack method2336() {
        return this.field8488;
    }

    public LivingEntity method2337() {
        return this.field8487;
    }

    public void method2338(MatrixStack param1) {
        this.field8488 = param1;
    }

    public void method2339(EntityModel param1) {
        this.field8489 = param1;
    }

    public int hashCode() {
        LivingEntity v3 = this.method2337();
        int v2 = 59 + (v3 == null ? 43 : v3.hashCode());
        MatrixStack v4 = this.method2336();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        EntityModel v5 = this.method2335();
        v2 = v2 * 59 + (v5 == null ? 43 : v5.hashCode());
        return v2;
    }

    static {
        西Т = "EventLayerHead(entity=\u0001, matrix=\u0001, model=\u0001)";
    }
}

