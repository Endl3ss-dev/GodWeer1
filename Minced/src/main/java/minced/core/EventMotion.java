/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventBase;
import sLM._4ZXYEo;

public class EventMotion
extends EventBase
implements CancellableEvent {
    static String cfr_renamed_210;

    double field9031;
    float field9032;
    double field9033;
    boolean field9034;
    boolean field9035;
    double field9036;
    float field9037;
    boolean field9038;

    public void method2505(double param1) {
        this.field9031 = param1;
    }

    public void method2506(float param1) {
        this.field9032 = param1;
    }

    public void method2507(double param1) {
        this.field9033 = param1;
    }

    public String toString() {
        return _4ZXYEo.фл("makeConcatWithConstants", new Object[]{}, (double)this.method2518(), (double)this.method2508(), (double)this.method2513(), (float)this.method2514(), (float)this.method2515(), (boolean)this.method2516(), (boolean)this.method2521(), (boolean)this.method2519());
    }

    public double method2508() {
        return this.field9031;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof EventMotion)) {
            return false;
        }
        EventMotion v2 = (EventMotion)param1;
        if (!v2.method2520(this)) {
            return false;
        }
        if (Double.compare(this.method2518(), v2.method2518()) != 0) {
            return false;
        }
        if (Double.compare(this.method2508(), v2.method2508()) != 0) {
            return false;
        }
        if (Double.compare(this.method2513(), v2.method2513()) != 0) {
            return false;
        }
        if (Float.compare(this.method2514(), v2.method2514()) != 0) {
            return false;
        }
        if (Float.compare(this.method2515(), v2.method2515()) != 0) {
            return false;
        }
        if (this.method2516() != v2.method2516()) {
            return false;
        }
        if (this.method2521() != v2.method2521()) {
            return false;
        }
        return this.method2519() == v2.method2519();
    }

    public void method2509(boolean param1) {
        this.field9034 = param1;
    }

    public void method2510(boolean param1) {
        this.field9035 = param1;
    }

    public void method2511(double param1) {
        this.field9036 = param1;
    }

    public void method2512(float param1) {
        this.field9037 = param1;
    }

    public EventMotion(double param1, double param3, double param5, float param7, float param8, boolean param9, boolean param10, boolean param11) {
        this.field9033 = param1;
        this.field9031 = param3;
        this.field9036 = param5;
        this.field9032 = param7;
        this.field9037 = param8;
        this.field9034 = param9;
        this.field9035 = param10;
        this.field9038 = param11;
    }

    public double method2513() {
        return this.field9036;
    }

    public float method2514() {
        return this.field9032;
    }

    public float method2515() {
        return this.field9037;
    }

    public boolean method2516() {
        return this.field9034;
    }

    public void method2517(boolean param1) {
        this.field9038 = param1;
    }

    public double method2518() {
        return this.field9033;
    }

    public boolean method2519() {
        return this.field9038;
    }

    public int hashCode() {
        long v3 = Double.doubleToLongBits(this.method2518());
        int v2 = 59 + (int)(v3 >>> 32 ^ v3);
        long v5 = Double.doubleToLongBits(this.method2508());
        v2 = v2 * 59 + (int)(v5 >>> 32 ^ v5);
        long v7 = Double.doubleToLongBits(this.method2513());
        v2 = v2 * 59 + (int)(v7 >>> 32 ^ v7);
        v2 = v2 * 59 + Float.floatToIntBits(this.method2514());
        v2 = v2 * 59 + Float.floatToIntBits(this.method2515());
        v2 = v2 * 59 + (this.method2516() ? 79 : 97);
        v2 = v2 * 59 + (this.method2521() ? 79 : 97);
        v2 = v2 * 59 + (this.method2519() ? 79 : 97);
        return v2;
    }

    protected boolean method2520(Object param1) {
        return param1 instanceof EventMotion;
    }

    public boolean method2521() {
        return this.field9035;
    }

    static {
        cfr_renamed_210 = "EventMotion(x=\u0001, y=\u0001, z=\u0001, yaw=\u0001, pitch=\u0001, onGround=\u0001, isSneaking=\u0001, isSprinting=\u0001)";
    }
}



