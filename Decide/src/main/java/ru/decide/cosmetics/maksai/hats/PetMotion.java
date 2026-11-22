/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.maksai.hats;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.maksai.hats.Molang;

@Environment(value=EnvType.CLIENT)
public final class PetMotion {
    static final double[] A = new double[]{-0.0625, 0.0, 0.0, 0.0, 0.0, -0.0625, 0.0, 1.5, 0.0, 0.0, 0.0625, 0.0, 0.0, 0.0, 0.0, 1.0};
    static final double[] IA = new double[]{-16.0, 0.0, 0.0, 0.0, 0.0, -16.0, 0.0, 24.0, 0.0, 0.0, 16.0, 0.0, 0.0, 0.0, 0.0, 1.0};
    private int[] platformBones = new int[0];
    final Bone[] bones;
    final Map<String, Clip> clips;
    final Controller[] controllers;
    final Active[] active;
    final Molang.Env env = new Molang.Env();
    final Map<String, Double> options;
    final Map<String, Double> optionDefaults;
    float[] previous;
    float[] current;
    long lastTick;
    double clock;
    boolean failed;

    void setPlatformBones(int[] indices) {
        this.platformBones = (int[])indices.clone();
    }

    public int platformBoneCount() {
        return this.platformBones.length;
    }

    public PetMotion(Bone[] b, Map<String, Clip> c, Controller[] controllers, Map<String, Double> options) {
        if (b.length != 0 && b.length <= 1000) {
            int i;
            this.bones = b;
            this.clips = c;
            this.controllers = controllers;
            this.options = new HashMap<String, Double>(options);
            this.optionDefaults = Map.copyOf(options);
            this.active = new Active[controllers.length];
            for (i = 0; i < this.active.length; ++i) {
                this.active[i] = new Active();
            }
            this.current = new float[b.length * 12];
            for (i = 0; i < b.length; ++i) {
                this.current[i * 12 + 10] = 1.0f;
                this.current[i * 12 + 5] = 1.0f;
                this.current[i * 12] = 1.0f;
            }
        } else {
            throw new IllegalArgumentException("bone limit");
        }
        this.previous = (float[])this.current.clone();
    }

    public Map<String, Double> optionValues() {
        return Collections.unmodifiableMap(new TreeMap<String, Double>(this.options));
    }

    public void setBooleanOption(String key, boolean value) {
        if (!this.options.containsKey(key)) {
            throw new IllegalArgumentException("Unknown option");
        }
        this.options.put(key, value ? 1.0 : 0.0);
    }

    public void resetOptions() {
        this.options.clear();
        this.options.putAll(this.optionDefaults);
    }

    public int boneCount() {
        return this.bones.length;
    }

    public boolean failed() {
        return this.failed;
    }

    public Set<String> unsupported() {
        return Set.copyOf(this.env.unsupported);
    }

    public List<String> activeClips() {
        return Arrays.stream(this.active).map(a -> a.clip).filter(Objects::nonNull).toList();
    }

    public float[] testPose() {
        return (float[])this.current.clone();
    }

    double[] neutral() {
        double[] v = new double[this.bones.length * 9];
        for (int i = 0; i < this.bones.length; ++i) {
            Arrays.fill(v, i * 9 + 6, i * 9 + 9, 1.0);
        }
        return v;
    }

    static double time(Clip c, double t) {
        return c.duration > 0.0 ? (c.loop ? t % c.duration : Math.min(t, c.duration)) : t;
    }

    public void tick(Map<String, Double> inputs, double dt) {
        if (!this.failed) {
            this.clock += Molang.clamp(dt, 0.0, 0.2);
            this.env.time = this.clock;
            this.env.put("c.tick_random", this.env.random.nextDouble());
            this.env.put("c.tick_random_int", this.env.random.nextInt(32768));
            this.env.values.replaceAll((kx, v) -> kx.startsWith("c.timer") ? Math.max(0.0, v - dt) : v);
            this.env.cache.clear();
            this.env.values.keySet().removeIf(kx -> kx.startsWith("t."));
            this.options.forEach(this.env::put);
            inputs.forEach(this.env::put);
            try {
                for (int i = 0; i < this.active.length; ++i) {
                    Active active = this.active[i];
                    this.env.animTime = active.clip == null ? 0.0 : this.clock - active.start;
                    int matched = 0;
                    for (State s : this.controllers[i].states) {
                        if (!this.clips.containsKey(s.clip) || s.condition.eval(this.env) == 0.0) continue;
                        matched = 1;
                        if (Objects.equals(active.clip, s.clip)) break;
                        active.from = active.current == null ? this.neutral() : (double[])active.current.clone();
                        active.duration = active.current == null ? 0.0 : Math.max(0.0, s.ticks.eval(this.env)) * 0.05;
                        active.ease = s.easing;
                        active.clip = s.clip;
                        active.start = active.blend = this.clock;
                        break;
                    }
                    if (matched != 0) continue;
                    active.clip = null;
                    active.current = null;
                    active.from = null;
                }
                for (Active a : this.active) {
                    if (a.clip == null) continue;
                    Clip c = this.clips.get(a.clip);
                    this.env.animTime = PetMotion.time(c, this.clock - a.start);
                    for (Track t : c.tracks) {
                        t.rotation.scripts(this.env);
                        t.position.scripts(this.env);
                        t.scale.scripts(this.env);
                    }
                }
                double[] combined = this.neutral();
                for (Active ax : this.active) {
                    int k;
                    double w;
                    if (ax.clip == null) continue;
                    Clip c = this.clips.get(ax.clip);
                    this.env.animTime = PetMotion.time(c, this.clock - ax.start);
                    double[] target = this.neutral();
                    for (Track t : c.tracks) {
                        if (t.bone < 0) continue;
                        int k2 = t.bone * 9;
                        double[] r = t.rotation.value(this.env, this.env.animTime);
                        double[] p = t.position.value(this.env, this.env.animTime);
                        double[] sx = t.scale.value(this.env, this.env.animTime);
                        for (int j = 0; j < 3; ++j) {
                            target[k2 + j] = r[j];
                            target[k2 + 3 + j] = p[j];
                            target[k2 + 6 + j] = sx[j];
                        }
                    }
                    double d = w = ax.duration <= 0.0 ? 1.0 : Molang.ease(ax.ease, (this.clock - ax.blend) / ax.duration);
                    if (w < 1.0) {
                        for (k = 0; k < target.length; ++k) {
                            double delta = target[k] - ax.from[k];
                            if (k % 9 < 3) {
                                delta = Molang.wrap(delta);
                            }
                            target[k] = ax.from[k] + delta * w;
                        }
                    }
                    ax.current = target;
                    for (k = 0; k < combined.length; ++k) {
                        if (k % 9 >= 6) {
                            int n = k;
                            combined[n] = combined[n] * target[k];
                            continue;
                        }
                        int n = k;
                        combined[n] = combined[n] + target[k];
                    }
                }
                if (this.options.getOrDefault("option.hide_platform", 0.0) != 0.0) {
                    for (int bone : this.platformBones) {
                        for (int axis = 0; axis < 3; ++axis) {
                            combined[bone * 9 + 6 + axis] = 0.0;
                        }
                    }
                }
                double[][] dArrayArray = new double[this.bones.length][];
                boolean[] visiting = new boolean[this.bones.length];
                float[] out = new float[this.current.length];
                for (int i = 0; i < this.bones.length; ++i) {
                    double[] m = PetMotion.mul(PetMotion.mul(PetMotion.mul(A, this.world(i, combined, dArrayArray, visiting)), this.bones[i].inverseBind), IA);
                    for (int j = 0; j < 12; ++j) {
                        out[i * 12 + j] = (float)m[j];
                        if (Float.isFinite(out[i * 12 + j])) continue;
                        throw new IllegalArgumentException("nonfinite");
                    }
                }
                this.previous = this.current;
                this.current = out;
                this.lastTick = System.nanoTime();
            }
            catch (RuntimeException var20) {
                this.failed = true;
                this.env.unsupported.add("runtime: " + String.valueOf(var20));
            }
        }
    }

    public float[] frame() {
        double w = Molang.clamp((double)(System.nanoTime() - this.lastTick) * 1.0E-9 / 0.05, 0.0, 1.0);
        float[] f = new float[this.current.length];
        for (int i = 0; i < f.length; ++i) {
            f[i] = (float)((double)this.previous[i] + (double)(this.current[i] - this.previous[i]) * w);
        }
        return f;
    }

    double[] world(int i, double[] local, double[][] cache, boolean[] visiting) {
        if (cache[i] != null) {
            return cache[i];
        }
        if (visiting[i]) {
            throw new IllegalArgumentException("bone cycle");
        }
        visiting[i] = true;
        Bone b = this.bones[i];
        int k = i * 9;
        double[] r = (double[])b.rotation.clone();
        double[] p = new double[3];
        double[] s = PetMotion.identity();
        for (int j = 0; j < 3; ++j) {
            int n = j;
            r[n] = r[n] + Molang.wrap(local[k + j]);
            p[j] = Molang.clamp(local[k + 3 + j], -1024.0, 1024.0);
            s[j * 5] = Molang.clamp(local[k + 6 + j], -32.0, 32.0);
        }
        double[] m = PetMotion.mul(PetMotion.translation(p), PetMotion.mul(PetMotion.rotation(r, b.pivot), PetMotion.mul(PetMotion.translation(b.pivot), PetMotion.mul(s, PetMotion.translation(new double[]{-b.pivot[0], -b.pivot[1], -b.pivot[2]})))));
        if (b.parent >= 0) {
            m = PetMotion.mul(this.world(b.parent, local, cache, visiting), m);
        }
        visiting[i] = false;
        cache[i] = m;
        return m;
    }

    static double[] identity() {
        return new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
    }

    static double[] translation(double[] p) {
        double[] m = PetMotion.identity();
        m[3] = p[0];
        m[7] = p[1];
        m[11] = p[2];
        return m;
    }

    static double[] mul(double[] a, double[] b) {
        double[] c = new double[16];
        for (int r = 0; r < 4; ++r) {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 4; ++k) {
                    c[r * 4 + j] = c[r * 4 + j] + a[r * 4 + k] * b[k * 4 + j];
                }
            }
        }
        return c;
    }

    static double[] rotation(double[] r, double[] p) {
        double x = Math.toRadians(-r[0]);
        double y = Math.toRadians(r[1]);
        double z = Math.toRadians(-r[2]);
        double cx = Math.cos(x);
        double sx = Math.sin(x);
        double cy = Math.cos(y);
        double sy = Math.sin(y);
        double cz = Math.cos(z);
        double sz = Math.sin(z);
        double[] rx = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, cx, -sx, 0.0, 0.0, sx, cx, 0.0, 0.0, 0.0, 0.0, 1.0};
        double[] ry = new double[]{cy, 0.0, sy, 0.0, 0.0, 1.0, 0.0, 0.0, -sy, 0.0, cy, 0.0, 0.0, 0.0, 0.0, 1.0};
        double[] rz = new double[]{cz, -sz, 0.0, 0.0, sz, cz, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        return PetMotion.mul(PetMotion.translation(p), PetMotion.mul(PetMotion.mul(rz, PetMotion.mul(ry, rx)), PetMotion.translation(new double[]{-p[0], -p[1], -p[2]})));
    }

    @Environment(value=EnvType.CLIENT)
    public record Bone(int parent, double[] pivot, double[] rotation, double[] inverseBind) {
    }

    @Environment(value=EnvType.CLIENT)
    public record Controller(State[] states) {
    }

    @Environment(value=EnvType.CLIENT)
    static class Active {
        String clip;
        String ease;
        double start;
        double blend;
        double duration;
        double[] from;
        double[] current;

        Active() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Clip(String name, double duration, boolean loop, Track[] tracks) {
    }

    @Environment(value=EnvType.CLIENT)
    public record State(String clip, Molang.Program condition, Molang.Program ticks, String easing) {
    }

    @Environment(value=EnvType.CLIENT)
    public record Track(int bone, Channel rotation, Channel position, Channel scale) {
    }

    @Environment(value=EnvType.CLIENT)
    public static class Channel {
        final Key[] keys;
        double neutral;

        public Channel(Key[] k, double n) {
            this.keys = k;
            this.neutral = n;
        }

        void scripts(Molang.Env e) {
            double time = e.animTime;
            if (this.keys.length != 0) {
                if (time < this.keys[0].time) {
                    Channel.run(this.keys[0].pre, e);
                } else {
                    for (int i = 0; i < this.keys.length; ++i) {
                        Key a = this.keys[i];
                        if (time == a.time) {
                            Channel.run(a.post, e);
                            return;
                        }
                        if (i + 1 >= this.keys.length || !(time < this.keys[i + 1].time)) continue;
                        Channel.run(a.post, e);
                        Channel.run(this.keys[i + 1].pre, e);
                        return;
                    }
                    Channel.run(this.keys[this.keys.length - 1].post, e);
                }
            }
        }

        static void run(Molang.Program[] row, Molang.Env e) {
            for (Molang.Program p : row) {
                if (!p.script()) continue;
                p.eval(e);
            }
        }

        double[] value(Molang.Env e, double t) {
            if (this.keys.length == 0) {
                return new double[]{this.neutral, this.neutral, this.neutral};
            }
            if (t < this.keys[0].time) {
                return Channel.eval(this.keys[0].pre, e);
            }
            for (int i = 0; i < this.keys.length; ++i) {
                Key a = this.keys[i];
                if (t == a.time) {
                    return Channel.eval(a.post, e);
                }
                if (i + 1 >= this.keys.length || !(t < this.keys[i + 1].time)) continue;
                Key b = this.keys[i + 1];
                double w = (t - a.time) / (b.time - a.time);
                double[] x = Channel.eval(a.post, e);
                double[] y = Channel.eval(b.pre, e);
                if (!a.catmull && !b.catmull) {
                    for (int j = 0; j < 3; ++j) {
                        int n = j;
                        x[n] = x[n] + (y[j] - x[j]) * Molang.ease(a.easing, w);
                    }
                } else {
                    double[] p = Channel.eval(this.keys[Math.max((int)0, (int)(i - 1))].post, e);
                    double[] q = Channel.eval(this.keys[Math.min((int)(this.keys.length - 1), (int)(i + 2))].pre, e);
                    for (int j = 0; j < 3; ++j) {
                        x[j] = 0.5 * (2.0 * x[j] + (-p[j] + y[j]) * w + (2.0 * p[j] - 5.0 * x[j] + 4.0 * y[j] - q[j]) * w * w + (-p[j] + 3.0 * x[j] - 3.0 * y[j] + q[j]) * w * w * w);
                    }
                }
                return x;
            }
            return Channel.eval(this.keys[this.keys.length - 1].post, e);
        }

        static double[] eval(Molang.Program[] p, Molang.Env e) {
            return new double[]{p[0].eval(e), p[1].eval(e), p[2].eval(e)};
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Key(double time, Molang.Program[] pre, Molang.Program[] post, String easing, boolean catmull) {
    }
}

