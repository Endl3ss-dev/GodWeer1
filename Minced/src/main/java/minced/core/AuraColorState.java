/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.function.Function;

public final class AuraColorState {
    int field7967;
    float field7968;
    static float field7969 = 255.0f;
    float field7970;
    float field7971;
    static float field7972 = 255.0f;
    long field7973;
    static float field7974 = 255.0f;

    public AuraColorState(int param1) {
        this.field7967 = Math.max(1, param1);
    }

    public void method1990() {
        if (this.field7968 == field7969) {
            return;
        }
        this.method1995();
        this.field7971 = this.field7970;
        this.field7968 = field7972;
        this.field7973 = System.currentTimeMillis();
    }

    private static float method1991(float param0) {
        float v1 = Math.max(0.0f, Math.min(1.0f, param0));
        float v2 = 1.0f - v1;
        return 1.0f - v2 * v2 * v2;
    }

    public float method1992() {
        this.method1995();
        return this.field7970 / field7974;
    }

    public void method1993() {
        if (this.field7968 == 0.0f) {
            return;
        }
        this.method1995();
        this.field7971 = this.field7970;
        this.field7968 = 0.0f;
        this.field7973 = System.currentTimeMillis();
    }

    public void method1994(int param1) {
        this.field7967 = Math.max(1, param1);
    }

    private void method1995() {
        long v1 = System.currentTimeMillis();
        long v3 = v1 - this.field7973;
        if (v3 <= 0L) {
            this.field7970 = this.field7971;
            return;
        }
        if (v3 >= (long)this.field7967) {
            this.field7970 = this.field7968;
            return;
        }
        float v5 = (float)v3 / (float)this.field7967;
        float v6 = AuraColorState.method1991(v5);
        this.field7970 = this.field7971 + (this.field7968 - this.field7971) * v6;
    }
}

