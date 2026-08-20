/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.EasingFunction;
import minced.core.EasingConstants;
import sLM._4ZXYEo;

public class SmoothValue
implements EasingFunction {
    @Override
    public double O(double p0) { return 0.0; }

    static String cfr_renamed_285;

    double field11742;
    double field11743;
    long field11744;
    EasingFunction field11745 = EasingConstants.field11775;
    static String field11746;
    static double field11747;
    boolean field11748 = false;
    double field11749;
    double field11750;
    boolean field11751 = false;
    double field11752;
    Runnable field11753;
    static double field11754;

    public SmoothValue method3337(double param1) {
        this.field11742 = param1;
        return this;
    }

    public double method3338() {
        return this.field11742;
    }

    public SmoothValue method3339(double param1) {
        this.field11743 = param1;
        return this;
    }

    public SmoothValue method3340(long param1) {
        this.field11744 = param1;
        return this;
    }

    public SmoothValue method3341(EasingFunction param1) {
        this.field11745 = param1;
        return this;
    }

    public void method3342(double param1) {
        this.method3345(param1, 0.0);
        this.method3367();
        this.method3346(param1);
    }

    public SmoothValue method3343(double param1, double param3, EasingFunction param5, boolean param6) {
        if (this.method3360(param6, param1)) {
            if (this.method3363()) {
                System.out.println(field11746);
            }
        } else {
            this.method3341(param5).method3339(param3 * field11747).method3340(System.currentTimeMillis()).method3356(this.method3351()).method3337(param1);
            this.field11748 = this.method3357() == this.method3338();
            if (this.method3363()) {
                System.out.println((String)((Object)_4ZXYEo.фк("makeConcatWithConstants", new Object[]{}, (double)this.method3338(), (double)this.method3351(), (double)this.method3347())));
            }
        }
        return this;
    }

    public boolean method3344() {
        return !this.method3349();
    }

    public double cfr_renamed_4() {
        return this.field11749;
    }

    public SmoothValue method3345(double param1, double param3) {
        return this.method3343(param1, param3, EasingConstants.field11775, false);
    }

    public SmoothValue method3346(double param1) {
        this.field11750 = param1;
        return this;
    }

    public double method3347() {
        return this.field11743;
    }

    public boolean method3348() {
        return this.field11748;
    }

    public boolean method3349() {
        return this.method3350() >= 1.0;
    }

    public double method3350() {
        return (double)(System.currentTimeMillis() - this.method3354()) / this.method3347();
    }

    public double method3351() {
        return this.field11750;
    }

    public float method3352() {
        return (float)this.cfr_renamed_4();
    }

    public SmoothValue method3353(boolean param1) {
        this.field11751 = param1;
        return this;
    }

    public long method3354() {
        return this.field11744;
    }

    public double method3355(double param1, double param3, double param5) {
        return param1 + (param3 - param1) * param5;
    }

    public SmoothValue method3356(double param1) {
        this.field11752 = param1;
        return this;
    }

    public double method3357() {
        return this.field11752;
    }

    public EasingFunction method3358() {
        return this.field11745;
    }

    public SmoothValue method3359(double param1, double param3, boolean param5) {
        return this.method3343(param1, param3, EasingConstants.field11775, param5);
    }

    public boolean method3360(boolean param1, double param2) {
        return param1 && this.method3344() && (param2 == this.method3357() || param2 == this.method3338() || param2 == this.method3351());
    }

    public SmoothValue method3361(double param1, double param3, EasingFunction param5) {
        return this.method3343(param1, param3, param5, false);
    }

    public SmoothValue method3362(Runnable param1) {
        this.field11753 = param1;
        return this;
    }

    public boolean method3363() {
        return this.field11751;
    }

    public float method3364() {
        return (float)this.method3351();
    }

    public Runnable method3365() {
        return this.field11753;
    }

    public SmoothValue method3366(double param1) {
        this.field11749 = param1;
        return this;
    }

    public boolean method3367() {
        this.method3366(this.method3351());
        boolean v1 = this.method3344();
        if ((double)(System.currentTimeMillis() - this.method3354()) > this.method3347() / field11754) {
            this.field11748 = this.method3357() == this.method3338();
        }
        if (v1) {
            this.method3346(this.method3355(this.method3357(), this.method3338(), this.method3358().O(this.method3350())));
        } else {
            this.method3340(0L);
            this.method3346(this.method3338());
            if (this.field11753 != null) {
                this.field11753.run();
                this.field11753 = null;
            }
        }
        return v1;
    }

    static {
        field11746 = "Animate cancelled due to target val equals from val";
        field11747 = 1000.0;
        cfr_renamed_285 = "#animate {\n    to value: \u0001\n    from value: \u0001\n    duration: \u0001\n}";
        field11754 = 1.5;
    }
}

