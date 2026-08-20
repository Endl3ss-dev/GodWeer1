/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.time.Duration;
import minced.core.ClientState;
import minced.enums.FORWARD;

public class FadeAnimation {
    Duration field11683;
    FORWARD field11684;
    float field11685;
    ClientState O = new ClientState();
    float field11686;

    public FadeAnimation(float param1, Duration param2) {
        this.field11684 = FORWARD.FORWARD;
        this.field11685 = param1;
        this.field11683 = param2;
        this.method3304();
    }

    public Duration method3287() {
        return this.field11683;
    }

    public void method3288(boolean param1) {
        this.method3301(param1 ? FORWARD.FORWARD : FORWARD.BACKWARD);
    }

    public FadeAnimation method3289(ClientState param1) {
        this.O = param1;
        return this;
    }

    public FadeAnimation method3290(Duration param1) {
        this.field11683 = param1;
        return this;
    }

    public FadeAnimation method3291(FORWARD param1) {
        this.field11684 = param1;
        return this;
    }

    public FadeAnimation method3292(float param1) {
        this.field11685 = param1;
        return this;
    }

    public boolean method3293() {
        return this.O.method3110(this.field11683.toMillis());
    }

    public float method3294() {
        float v1 = Math.min(1.0f, (float)this.O.method3112() / (float)this.field11683.toMillis());
        return this.field11685 * (this.field11684 == FORWARD.FORWARD ? v1 : 1.0f - v1);
    }

    public void method3295(float param1) {
        if (this.field11686 != param1) {
            this.field11685 = this.method3294();
            this.field11686 = param1;
            this.method3304();
        }
    }

    public FORWARD method3296() {
        return this.field11684;
    }

    public FadeAnimation method3297(float param1) {
        this.field11686 = param1;
        return this;
    }

    public float method3298() {
        return this.field11686;
    }

    public boolean method3299() {
        return this.field11684 == FORWARD.FORWARD;
    }

    public boolean method3300() {
        return this.field11684 == FORWARD.BACKWARD;
    }

    public void method3301(FORWARD param1) {
        if (this.field11684 != param1) {
            this.field11684 = param1;
            this.method3304();
        }
    }

    public boolean method3302(FORWARD param1) {
        return this.method3293() && this.field11684 == param1;
    }

    public float method3303() {
        return this.field11685;
    }

    public void method3304() {
        this.O.method3114();
    }

    public ClientState method3305() {
        return this.O;
    }
}

