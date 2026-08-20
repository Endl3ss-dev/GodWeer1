/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package minced.core;

import minced.core.MathUtil;
import net.minecraft.client.gui.DrawContext;

public abstract class Widget
 {
    boolean cfr_underscore = true;
    float field9387;
    boolean field9347 = true;
    float field9388;
    float field9389;
    float field9386;

    public boolean method2753() {
        return this.cfr_underscore;
    }

    public float method2754() {
        return this.field9387;
    }

    public boolean method2755() {
        return this.field9347;
    }

    public Widget(float param1, float param2, float param3, float param4) {
        this.field9388 = param1;
        this.field9389 = param2;
        this.field9386 = param3;
        this.field9387 = param4;
    }

    public void method2756(float param1) {
        this.field9388 = param1;
    }

    public float method2757() {
        return this.field9388;
    }

    public float method2758() {
        return this.field9389;
    }

    public boolean method2759(double param1, double param3) {
        return MathUtil.cfr_underscore((float)param1, (float)param3, this.field9388, this.field9389, this.field9386, this.field9387);
    }

    public float method2760() {
        return this.field9386;
    }

    public void method2761(float param1, float param2) {
        this.field9388 = param1;
        this.field9389 = param2;
    }

    public boolean method2728(double param1, double param3, int param5) {
        return false;
    }

    public void method2762(boolean param1) {
        this.field9347 = param1;
    }

    public abstract void method2738(DrawContext var1, int var2, int var3, float var4);

    public void method2763(float param1) {
        this.field9389 = param1;
    }

    public void method2764(boolean param1) {
        this.cfr_underscore = param1;
    }

    public void method2765(float param1, float param2) {
        this.field9386 = param1;
        this.field9387 = param2;
    }

    public void method2766(float param1) {
        this.field9387 = param1;
    }

    public void method2767(float param1) {
        this.field9386 = param1;
    }

    public abstract boolean method2736(double var1, double var3, int var5);
}

