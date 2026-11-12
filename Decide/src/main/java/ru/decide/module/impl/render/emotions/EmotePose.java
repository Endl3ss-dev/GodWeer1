/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.render.emotions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class EmotePose {
    private final Bone[] bones;
    private final Curve[] curves;
    public final double duration;
    public final boolean loop;
    private final double[][] relative;

    private static double[] nums(JsonArray a) {
        if (a.size() != 16) {
            throw new IllegalArgumentException("matrix");
        }
        double[] v = new double[16];
        for (int i = 0; i < 16; ++i) {
            v[i] = a.get(i).getAsDouble();
            if (Double.isFinite(v[i])) continue;
            throw new IllegalArgumentException("finite");
        }
        return v;
    }

    public EmotePose(Bone[] b, Curve[] c, double d, boolean loop) {
        this.bones = b;
        this.curves = c;
        this.duration = d;
        this.loop = loop;
        this.relative = new double[b.length][];
        for (int i = 0; i < b.length; ++i) {
            if (b[i].parent < -1 || b[i].parent >= b.length || b[i].parent == i) {
                throw new IllegalArgumentException("parent");
            }
            this.relative[i] = b[i].parent < 0 ? b[i].bind : EmotePose.mul(b[b[i].parent].inverse, b[i].bind);
        }
    }

    public static EmotePose parse(JsonObject rig, JsonObject clip) {
        JsonArray bs = rig.getAsJsonArray("bones");
        if (bs.size() > 128) {
            throw new IllegalArgumentException("rig limit");
        }
        Bone[] b = new Bone[bs.size()];
        for (int i = 0; i < b.length; ++i) {
            JsonObject o = bs.get(i).getAsJsonObject();
            b[i] = new Bone(o.get("name").getAsString(), o.get("parent").getAsInt(), EmotePose.nums(o.getAsJsonArray("bind")), EmotePose.nums(o.getAsJsonArray("inverse")));
        }
        ArrayList<Curve> cs = new ArrayList<Curve>();
        for (JsonElement el : clip.getAsJsonArray("tracks")) {
            String ks;
            JsonObject o = el.getAsJsonObject();
            int bone = o.get("bone").getAsInt();
            int axis = o.get("axis").getAsInt();
            if (bone < 0 || bone >= b.length || axis < 0 || axis > 2) {
                throw new IllegalArgumentException("track");
            }
            int ch = switch (ks = o.get("channel").getAsString()) {
                case "location" -> 0;
                case "rotation_euler" -> 1;
                case "scale" -> 2;
                default -> throw new IllegalArgumentException("channel");
            };
            JsonArray keysJson = o.getAsJsonArray("keys");
            Key[] k = new Key[keysJson.size()];
            for (int j = 0; j < k.length; ++j) {
                JsonArray a = keysJson.get(j).getAsJsonArray();
                if (a.size() != 7) {
                    throw new IllegalArgumentException("key");
                }
                k[j] = new Key(a.get(0).getAsDouble(), a.get(1).getAsDouble(), a.get(2).getAsString(), a.get(3).getAsDouble(), a.get(4).getAsDouble(), a.get(5).getAsDouble(), a.get(6).getAsDouble());
                if (j <= 0 || !(k[j].tick < k[j - 1].tick)) continue;
                throw new IllegalArgumentException("key order");
            }
            cs.add(new Curve(bone, ch, axis, k));
        }
        return new EmotePose(b, (Curve[])cs.toArray(Curve[]::new), clip.get("duration").getAsDouble(), clip.get("loop").getAsBoolean());
    }

    public static double bezier(double a, double b, double c, double d, double t) {
        double u = 1.0 - t;
        return u * u * u * a + 3.0 * u * u * t * b + 3.0 * u * t * t * c + t * t * t * d;
    }

    public static double sample(Key[] keys, double tick, double neutral) {
        if (keys.length == 0) {
            return neutral;
        }
        if (tick <= keys[0].tick) {
            return keys[0].value;
        }
        int i = 0;
        while (i + 1 < keys.length) {
            Key a = keys[i];
            Key b = keys[i + 1];
            if (tick <= b.tick) {
                if (a.mode.equals("CONSTANT")) {
                    return a.value;
                }
                double w = (tick - a.tick) / (b.tick - a.tick);
                if (a.mode.equals("SINE")) {
                    return a.value + (b.value - a.value) * (0.5 - 0.5 * Math.cos(Math.PI * w));
                }
                if (!a.mode.equals("BEZIER")) {
                    return a.value + (b.value - a.value) * w;
                }
                double x1 = Math.max(a.tick, Math.min(b.tick, a.rx));
                double x2 = Math.max(a.tick, Math.min(b.tick, b.lx));
                double lo = 0.0;
                double hi = 1.0;
                for (int j = 0; j < 36; ++j) {
                    double mid = (lo + hi) * 0.5;
                    if (EmotePose.bezier(a.tick, x1, x2, b.tick, mid) < tick) {
                        lo = mid;
                        continue;
                    }
                    hi = mid;
                }
                return EmotePose.bezier(a.value, a.ry, b.ly, b.value, (lo + hi) * 0.5);
            }
            ++i;
        }
        return keys[keys.length - 1].value;
    }

    public double[][] pose(double tick) {
        double[][] local;
        if (!Double.isFinite(tick)) {
            throw new IllegalArgumentException("clock");
        }
        if (this.duration > 0.0) {
            tick = this.loop ? (double)Math.floorMod((long)Math.floor(tick * 1000000.0), (long)Math.max(1.0, this.duration * 1000000.0)) / 1000000.0 : Math.max(0.0, Math.min(tick, this.duration));
        }
        for (double[] v : local = new double[this.bones.length][9]) {
            v[8] = 1.0;
            v[7] = 1.0;
            v[6] = 1.0;
        }
        for (Curve c : this.curves) {
            local[c.bone][c.channel * 3 + c.axis] = EmotePose.sample(c.keys, tick, c.channel == 2 ? 1.0 : 0.0);
        }
        double[][] dArrayArray = new double[this.bones.length][];
        boolean[] visiting = new boolean[this.bones.length];
        for (int i = 0; i < this.bones.length; ++i) {
            this.world(i, local, dArrayArray, visiting);
        }
        double[][] out = new double[this.bones.length][];
        for (int i = 0; i < this.bones.length; ++i) {
            out[i] = EmotePose.mul(dArrayArray[i], this.bones[i].inverse);
            for (double x : out[i]) {
                if (Double.isFinite(x)) continue;
                throw new IllegalArgumentException("nonfinite pose");
            }
        }
        return out;
    }

    private double[] world(int i, double[][] local, double[][] cache, boolean[] visiting) {
        if (cache[i] != null) {
            return cache[i];
        }
        if (visiting[i]) {
            throw new IllegalArgumentException("rig cycle");
        }
        visiting[i] = true;
        double[] a = local[i];
        double[] t = EmotePose.identity();
        t[3] = a[0];
        t[7] = a[1];
        t[11] = a[2];
        double[] m = EmotePose.mul(this.relative[i], EmotePose.mul(t, EmotePose.rotation(a[3], a[4], a[5])));
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                m[row * 4 + col] = m[row * 4 + col] * a[6 + col];
            }
        }
        if (this.bones[i].parent >= 0) {
            m = EmotePose.mul(this.world(this.bones[i].parent, local, cache, visiting), m);
        }
        visiting[i] = false;
        cache[i] = m;
        return m;
    }

    public int bone(String name) {
        for (int i = 0; i < this.bones.length; ++i) {
            if (!this.bones[i].name.equals(name)) continue;
            return i;
        }
        return -1;
    }

    public static double[] identity() {
        return new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
    }

    public static double[] mul(double[] a, double[] b) {
        double[] c = new double[16];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    c[i * 4 + j] = c[i * 4 + j] + a[i * 4 + k] * b[k * 4 + j];
                }
            }
        }
        return c;
    }

    static double[] rotation(double x, double y, double z) {
        double cx = Math.cos(x);
        double sx = Math.sin(x);
        double cy = Math.cos(y);
        double sy = Math.sin(y);
        double cz = Math.cos(z);
        double sz = Math.sin(z);
        return new double[]{cz * cy, cz * sy * sx - sz * cx, cz * sy * cx + sz * sx, 0.0, sz * cy, sz * sy * sx + cz * cx, sz * sy * cx - cz * sx, 0.0, -sy, cy * sx, cy * cx, 0.0, 0.0, 0.0, 0.0, 1.0};
    }

    @Environment(value=EnvType.CLIENT)
    public record Bone(String name, int parent, double[] bind, double[] inverse) {
    }

    @Environment(value=EnvType.CLIENT)
    public record Curve(int bone, int channel, int axis, Key[] keys) {
    }

    @Environment(value=EnvType.CLIENT)
    public record Key(double tick, double value, String mode, double lx, double ly, double rx, double ry) {
    }
}

