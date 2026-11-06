/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.maksai.hats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class TextureUvAnimation {
    private final int width;
    private final int height;
    private final int fw;
    private final int fh;
    private final int pad;
    private final int cols;
    private final int[] indices;
    private final int[] times;
    private final long period;

    private TextureUvAnimation(int w, int h, int fw, int fh, int pad, int cols, int[] i, int[] t) {
        this.width = w;
        this.height = h;
        this.fw = fw;
        this.fh = fh;
        this.pad = pad;
        this.cols = cols;
        this.indices = i;
        this.times = t;
        long total = 0L;
        for (int x : t) {
            total += (long)x;
        }
        this.period = total;
    }

    public static TextureUvAnimation parse(JsonObject o) {
        int w = o.get("width").getAsInt();
        int h = o.get("height").getAsInt();
        int fw = o.get("frameWidth").getAsInt();
        int fh = o.get("frameHeight").getAsInt();
        int pad = o.get("padding").getAsInt();
        int cols = o.get("columns").getAsInt();
        int cells = o.get("cells").getAsInt();
        if (w >= 1 && h >= 1 && w <= 4096 && h <= 4096 && fw >= 1 && fh >= 1 && pad >= 0 && pad <= 16 && cols >= 1 && cells >= 1 && cells <= 1024 && (long)cols * ((long)fw + 2L * (long)pad) == (long)w && ((long)(cells + cols) - 1L) / (long)cols * ((long)fh + 2L * (long)pad) == (long)h) {
            JsonArray a = o.getAsJsonArray("indices");
            JsonArray b = o.getAsJsonArray("ticks");
            if (a.size() != 0 && a.size() == b.size() && a.size() <= 4096) {
                int[] i = new int[a.size()];
                int[] t = new int[a.size()];
                for (int j = 0; j < i.length; ++j) {
                    i[j] = a.get(j).getAsInt();
                    t[j] = b.get(j).getAsInt();
                    if (i[j] >= 0 && i[j] < cells && t[j] >= 1 && t[j] <= 12000) continue;
                    throw new IllegalArgumentException("Atlas frame");
                }
                return new TextureUvAnimation(w, h, fw, fh, pad, cols, i, t);
            }
            throw new IllegalArgumentException("Atlas timing");
        }
        throw new IllegalArgumentException("Atlas size");
    }

    public int frameAtTicks(long ticks) {
        long v = Math.floorMod(ticks, this.period);
        for (int j = 0; j < this.indices.length; ++j) {
            if (v < (long)this.times[j]) {
                return this.indices[j];
            }
            v -= (long)this.times[j];
        }
        return this.indices[this.indices.length - 1];
    }

    public Uv at(double seconds) {
        if (!Double.isFinite(seconds)) {
            seconds = 0.0;
        }
        int n = this.frameAtTicks((long)Math.floor(Math.max(0.0, seconds) * 20.0));
        return new Uv((float)(n % this.cols * (this.fw + 2 * this.pad) + this.pad) / (float)this.width, (float)(n / this.cols * (this.fh + 2 * this.pad) + this.pad) / (float)this.height, (float)this.fw / (float)this.width, (float)this.fh / (float)this.height);
    }

    @Environment(value=EnvType.CLIENT)
    public record Uv(float offsetU, float offsetV, float scaleU, float scaleV) {
        public float u(float v) {
            return this.offsetU + this.scaleU * v;
        }

        public float v(float v) {
            return this.offsetV + this.scaleV * v;
        }
    }
}

