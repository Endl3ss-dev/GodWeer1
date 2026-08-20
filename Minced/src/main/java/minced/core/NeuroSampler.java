/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterable
 */
package minced.core;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterable;
import java.util.ArrayList;
import java.util.Collection;
import minced.core.InterpolationUtil;
import minced.core.AngleUtil;
import minced.core.EasingUtil;

public class NeuroSampler {
    static int DEFAULT_MAX_SAMPLES;
    static int HARD_CAP_SAMPLES;
    static int MIN_CAP_SAMPLES;

    ArrayList<EasingUtil> samples = new ArrayList<EasingUtil>();
    static float field8408;
    static float field8409;
    static float field8410;
    AngleUtil rotationPoint = new AngleUtil(field8420, field8421, field8422);
    static float field8411;
    static float field8412;
    static float field8413;
    AngleUtil hitPoint = new AngleUtil(field8423, field8424, field8425);
    InterpolationUtil behavior = new InterpolationUtil();
    static float field8414;
    static float field8415;
    static float field8416;
    static float field8417;
    static float field8418;
    static float field8419;
    static float field8420;
    static float field8421;
    static float field8422;
    static float field8423;
    static float field8424;
    static float field8425;
    int targetMaxSamples = 30000;
    static float field8426;
    static float field8427;
    static float field8428;
    static float field8429;
    static float field8430;
    static float field8431;
    static int field8432;
    static int field8433;
    static int field8434;

    public void cfr_renamed_0() {
        this.samples = new ArrayList();
        this.rotationPoint = new AngleUtil(field8408, field8409, field8410);
        this.hitPoint = new AngleUtil(field8411, field8412, field8413);
        this.behavior = new InterpolationUtil();
    }

    public boolean method2280(EasingUtil param1) {
        this.method2281();
        if (param1 == null || !param1.method2680() || this.method2286()) {
            return false;
        }
        param1.method2679();
        this.samples.add(param1);
        return true;
    }

    public void method2281() {
        if (this.samples == null) {
            this.samples = new ArrayList();
        }
        if (this.rotationPoint == null) {
            this.rotationPoint = new AngleUtil(field8414, field8415, field8416);
        }
        if (this.hitPoint == null) {
            this.hitPoint = new AngleUtil(field8417, field8418, field8419);
        }
        if (this.behavior == null) {
            this.behavior = new InterpolationUtil();
        }
    }

    public int method2282() {
        this.method2281();
        return Math.min(this.samples.size(), this.method2284());
    }

    public void method2283() {
        this.method2281();
        this.samples.removeIf(param0 -> param0 == null || !param0.method2680());
        int v1 = this.method2284();
        if (this.samples.size() > v1) {
            this.samples = new ArrayList(this.samples.subList(this.samples.size() - v1, this.samples.size()));
        }
        this.samples.forEach(param0 -> ((EasingUtil)param0).method2679());
        this.rotationPoint.resetIfInvalid(field8426, field8427, field8428);
        this.hitPoint.resetIfInvalid(field8429, field8430, field8431);
        this.behavior.method2293();
    }

    public int method2284() {
        if (this.targetMaxSamples < 1000 || this.targetMaxSamples > field8432) {
            this.targetMaxSamples = 30000;
        }
        return this.targetMaxSamples;
    }

    public void method2285(int param1) {
        if (param1 < 1000) {
            param1 = 1000;
        }
        if (param1 > field8433) {
            param1 = field8434;
        }
        this.targetMaxSamples = param1;
    }

    public boolean method2286() {
        this.method2281();
        return this.samples.size() >= this.method2284();
    }

    static {
        DEFAULT_MAX_SAMPLES = 0;
        HARD_CAP_SAMPLES = 0;
        MIN_CAP_SAMPLES = 0;
        field8420 = 0.5f;
        field8421 = 0.72f;
        field8422 = 0.5f;
        field8423 = 0.5f;
        field8424 = 0.68f;
        field8425 = 0.5f;
        field8433 = 100000;
        field8434 = 100000;
        field8432 = 100000;
        field8414 = 0.5f;
        field8415 = 0.72f;
        field8416 = 0.5f;
        field8417 = 0.5f;
        field8418 = 0.68f;
        field8419 = 0.5f;
        field8426 = 0.5f;
        field8427 = 0.72f;
        field8428 = 0.5f;
        field8429 = 0.5f;
        field8430 = 0.68f;
        field8431 = 0.5f;
        field8408 = 0.5f;
        field8409 = 0.72f;
        field8410 = 0.5f;
        field8411 = 0.5f;
        field8412 = 0.68f;
        field8413 = 0.5f;
    }
}



