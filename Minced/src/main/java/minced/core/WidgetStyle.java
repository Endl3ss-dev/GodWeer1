/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.awt.Color;
import minced.core.Animation;
import minced.core.Easings;
import minced.core.Easing;

public class WidgetStyle {
    long field9502;
    Animation field9503;
    Animation field9504;
    Animation field9505;
    Animation field9506;

    public long method2781() {
        return this.field9502;
    }

    public Animation method2782() {
        return this.field9503;
    }

    public Animation method2783() {
        return this.field9504;
    }

    public void method2784(Color param1) {
        this.field9505.animate(param1.getRed());
        this.field9503.animate(param1.getGreen());
        this.field9506.animate(param1.getBlue());
        this.field9504.animate(param1.getAlpha());
    }

    public void method2785(Easing param1) {
        this.field9505.setEasing(param1);
        this.field9503.setEasing(param1);
        this.field9506.setEasing(param1);
        this.field9504.setEasing(param1);
    }

    public Animation method2786() {
        return this.field9506;
    }

    public void method2787(long param1) {
        this.field9505.setDuration(param1);
        this.field9503.setDuration(param1);
        this.field9506.setDuration(param1);
        this.field9504.setDuration(param1);
    }

    public WidgetStyle(long param1, Color param3) {
        this(param1);
        this.method2790(param3);
    }

    public WidgetStyle(long param1) {
        this.field9502 = Math.max(0L, param1);
        this.field9505 = new Animation(Easings.field11970, param1);
        this.field9503 = new Animation(Easings.field11970, param1);
        this.field9506 = new Animation(Easings.field11970, param1);
        this.field9504 = new Animation(Easings.field11970, param1);
    }

    public Color method2788() {
        return new Color((int)this.field9505.getAnimatedValue(), (int)this.field9503.getAnimatedValue(), (int)this.field9506.getAnimatedValue(), (int)this.field9504.getAnimatedValue());
    }

    public Animation method2789() {
        return this.field9505;
    }

    public void method2790(Color param1) {
        this.field9505.setAnimatedValue(param1.getRed());
        this.field9503.setAnimatedValue(param1.getGreen());
        this.field9506.setAnimatedValue(param1.getBlue());
        this.field9504.setAnimatedValue(param1.getAlpha());
    }
}



