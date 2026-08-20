/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.TypeFilter
 */
package minced.core;

public class EventMoveYaw
 {
    float field12461;
    float field12462;

    public EventMoveYaw(float param1, float param2) {
        this.field12461 = param1;
        this.field12462 = param2;
    }

    public float method3535() {
        return this.field12462;
    }

    public float method3536() {
        return this.field12461;
    }

    public void method3537(float param1) {
        this.field12462 = param1;
    }

    public void method3538(float param1) {
        this.field12461 = param1;
    }
}

