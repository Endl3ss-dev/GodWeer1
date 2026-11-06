/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.melee;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class MeleeKeyframe {
    private static final int STRIDE = 3;
    private final float[] times;
    private final float[] pre;
    private final float[] post;
    private final boolean[] smooth;

    MeleeKeyframe(float[] times, float[] pre, float[] post, boolean[] smooth) {
        this.times = times;
        this.pre = pre;
        this.post = post;
        this.smooth = smooth;
    }

    public float lastTime() {
        return this.times[this.times.length - 1];
    }

    public void sample(float time, float[] out, int offset) {
        int last = this.times.length - 1;
        if (time <= this.times[0]) {
            MeleeKeyframe.copy(this.pre, 0, out, offset);
            return;
        }
        if (time >= this.times[last]) {
            MeleeKeyframe.copy(this.post, last, out, offset);
            return;
        }
        int left = this.indexOf(time, last);
        float span = this.times[left + 1] - this.times[left];
        if (span <= 0.0f) {
            MeleeKeyframe.copy(this.post, left, out, offset);
            return;
        }
        float t = (time - this.times[left]) / span;
        if (this.smooth[left]) {
            this.spline(left, t, out, offset, last);
        } else {
            this.interpolate(left, t, out, offset);
        }
    }

    private int indexOf(float time, int hi) {
        int lo = 0;
        int high = hi;
        while (lo < high) {
            int mid = lo + high + 1 >>> 1;
            if (this.times[mid] <= time) {
                lo = mid;
                continue;
            }
            high = mid - 1;
        }
        return lo;
    }

    private void interpolate(int index, float t, float[] out, int offset) {
        int base = index * 3;
        int next = base + 3;
        for (int k = 0; k < 3; ++k) {
            float v = this.post[base + k];
            out[offset + k] = v + (this.pre[next + k] - v) * t;
        }
    }

    private void spline(int index, float t, float[] out, int offset, int last) {
        int prev = Math.max(index - 1, 0) * 3;
        int cur = index * 3;
        int next = cur + 3;
        int after = Math.min(index + 2, last) * 3;
        float t2 = t * t;
        float t3 = t2 * t;
        for (int k = 0; k < 3; ++k) {
            float p0 = this.post[prev + k];
            float p1 = this.post[cur + k];
            float p2 = this.pre[next + k];
            float p3 = this.pre[after + k];
            out[offset + k] = 0.5f * (2.0f * p1 + (p2 - p0) * t + (2.0f * p0 - 5.0f * p1 + 4.0f * p2 - p3) * t2 + (-p0 + 3.0f * p1 - 3.0f * p2 + p3) * t3);
        }
    }

    private static void copy(float[] src, int keyIndex, float[] out, int offset) {
        int base = keyIndex * 3;
        out[offset] = src[base];
        out[offset + 1] = src[base + 1];
        out[offset + 2] = src[base + 2];
    }
}

