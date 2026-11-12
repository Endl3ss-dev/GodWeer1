/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_630
 */
package ru.decide.optimization.velotune.perf;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_630;

@Environment(value=EnvType.CLIENT)
public final class CachedPlayerPose {
    private float[] transforms = new float[0];
    private boolean[] visibility = new boolean[0];
    private int signature;
    private long frame;
    private long capturedNanos;

    public int signature() {
        return this.signature;
    }

    public long frame() {
        return this.frame;
    }

    public long capturedNanos() {
        return this.capturedNanos;
    }

    public void capture(List<class_630> parts, int signature, long frame, long capturedNanos) {
        int count = parts.size();
        int floatCount = count * 9;
        int booleanCount = count * 2;
        if (this.transforms.length != floatCount) {
            this.transforms = new float[floatCount];
        }
        if (this.visibility.length != booleanCount) {
            this.visibility = new boolean[booleanCount];
        }
        int f = 0;
        int b = 0;
        for (class_630 part : parts) {
            this.transforms[f++] = part.field_3657;
            this.transforms[f++] = part.field_3656;
            this.transforms[f++] = part.field_3655;
            this.transforms[f++] = part.field_3654;
            this.transforms[f++] = part.field_3675;
            this.transforms[f++] = part.field_3674;
            this.transforms[f++] = part.field_37938;
            this.transforms[f++] = part.field_37939;
            this.transforms[f++] = part.field_37940;
            this.visibility[b++] = part.field_3665;
            this.visibility[b++] = part.field_38456;
        }
        this.signature = signature;
        this.frame = frame;
        this.capturedNanos = capturedNanos;
    }

    public boolean apply(List<class_630> parts) {
        if (this.transforms.length != parts.size() * 9 || this.visibility.length != parts.size() * 2) {
            return false;
        }
        int f = 0;
        int b = 0;
        for (class_630 part : parts) {
            part.field_3657 = this.transforms[f++];
            part.field_3656 = this.transforms[f++];
            part.field_3655 = this.transforms[f++];
            part.field_3654 = this.transforms[f++];
            part.field_3675 = this.transforms[f++];
            part.field_3674 = this.transforms[f++];
            part.field_37938 = this.transforms[f++];
            part.field_37939 = this.transforms[f++];
            part.field_37940 = this.transforms[f++];
            part.field_3665 = this.visibility[b++];
            part.field_38456 = this.visibility[b++];
        }
        return true;
    }
}

