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
package ru.decide.cosmetics.maksai.hats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class WingAnimation {
    private final JsonObject[] bones;
    private final double duration;
    private final boolean loop;
    private static final double[] A = new double[]{-0.0625, 0.0, 0.0, 0.0, 0.0, -0.0625, 0.0, 1.5, 0.0, 0.0, 0.0625, 0.0, 0.0, 0.0, 0.0, 1.0};
    private static final double[] IA = new double[]{-16.0, 0.0, 0.0, 0.0, 0.0, -16.0, 0.0, 24.0, 0.0, 0.0, 16.0, 0.0, 0.0, 0.0, 0.0, 1.0};

    private WingAnimation(JsonObject[] b, double d, boolean l) {
        this.bones = b;
        this.duration = d;
        this.loop = l;
    }

    public int boneCount() {
        return this.bones.length;
    }

    public static WingAnimation parse(JsonObject o) throws IOException {
        JsonArray a = o.getAsJsonArray("bones");
        if (!a.isEmpty() && a.size() <= 1000) {
            JsonObject[] b = new JsonObject[a.size()];
            for (int i = 0; i < b.length; ++i) {
                b[i] = a.get(i).getAsJsonObject();
                int p = b[i].get("parent").getAsInt();
                if (p >= -1 && p < b.length && p != i) continue;
                throw new IOException("Wing parent");
            }
            double d = o.get("duration").getAsDouble();
            if (Double.isFinite(d) && !(d < 0.0)) {
                WingAnimation m = new WingAnimation(b, d, o.get("loop").getAsBoolean());
                m.pose(0.0);
                return m;
            }
            throw new IOException("Wing duration");
        }
        throw new IOException("Wing bone count");
    }

    private static double[] numbers(JsonArray a, int count) {
        if (a.size() != count) {
            throw new IllegalArgumentException("Wing vector");
        }
        double[] r = new double[count];
        for (int i = 0; i < count; ++i) {
            r[i] = a.get(i).getAsDouble();
            if (Double.isFinite(r[i])) continue;
            throw new IllegalArgumentException("Wing number");
        }
        return r;
    }

    private static double expression(JsonArray code, double t) {
        if (!code.isEmpty() && code.size() <= 2048) {
            double[] stack = new double[code.size() + 3];
            int n = 0;
            for (JsonElement token : code) {
                int count;
                if (token.getAsJsonPrimitive().isNumber()) {
                    stack[n++] = token.getAsDouble();
                    continue;
                }
                String op = token.getAsString();
                if (op.equals("t")) {
                    stack[n++] = t;
                    continue;
                }
                switch (op) {
                    case "clamp": 
                    case "movewave_sin": 
                    case "movewave": {
                        int n2 = 3;
                        break;
                    }
                    case "+": 
                    case "-": 
                    case "*": 
                    case "/": 
                    case "%": 
                    case "pow": 
                    case "min": 
                    case "max": 
                    case "mod": 
                    case "lopsided_wave": 
                    case "linear_wave": {
                        int n2 = 2;
                        break;
                    }
                    default: {
                        int n2 = count = 1;
                    }
                }
                if (n < count) {
                    throw new IllegalArgumentException("Wing stack");
                }
                double a = stack[n -= count];
                double b = count > 1 ? stack[n + 1] : 0.0;
                double c = count > 2 ? stack[n + 2] : 0.0;
                double v = switch (op) {
                    case "neg" -> -a;
                    case "+" -> a + b;
                    case "-" -> a - b;
                    case "*" -> a * b;
                    case "/" -> {
                        if (Math.abs(b) > 1.0E-10) {
                            yield a / b;
                        }
                        yield 0.0;
                    }
                    case "%", "mod" -> {
                        if (b != 0.0) {
                            yield a % b;
                        }
                        yield 0.0;
                    }
                    case "pow" -> Math.pow(a, b);
                    case "min" -> Math.min(a, b);
                    case "max" -> Math.max(a, b);
                    case "clamp" -> Math.max(b, Math.min(c, a));
                    case "sin" -> Math.sin(Math.toRadians(a));
                    case "cos" -> Math.cos(Math.toRadians(a));
                    case "tan" -> Math.tan(Math.toRadians(a));
                    case "abs" -> Math.abs(a);
                    case "sqrt" -> Math.sqrt(Math.max(0.0, a));
                    case "floor" -> Math.floor(a);
                    case "ceil" -> Math.ceil(a);
                    case "round" -> Math.floor(a + 0.5);
                    case "sign" -> Math.signum(a);
                    case "exp" -> Math.exp(a);
                    case "ln" -> Math.log(Math.max(a, 1.0E-20));
                    case "movewave_sin", "movewave" -> Math.sin(Math.toRadians(t * a + c)) * b;
                    case "lopsided_wave" -> Math.sin(Math.toRadians(a));
                    case "linear_wave" -> 1.0 - Math.abs(((a + b) % 360.0 + 360.0) % 360.0 / 180.0 - 1.0);
                    case "easeinoutsine" -> -(Math.cos(Math.PI * a) - 1.0) / 2.0;
                    case "easeinoutquad" -> {
                        if (a < 0.5) {
                            yield 2.0 * a * a;
                        }
                        yield 1.0 - Math.pow(-2.0 * a + 2.0, 2.0) / 2.0;
                    }
                    case "easeinoutquint" -> {
                        if (a < 0.5) {
                            yield 16.0 * Math.pow(a, 5.0);
                        }
                        yield 1.0 - Math.pow(-2.0 * a + 2.0, 5.0) / 2.0;
                    }
                    case "easeoutback" -> 1.0 + 2.70158 * Math.pow(a - 1.0, 3.0) + 1.70158 * Math.pow(a - 1.0, 2.0);
                    default -> throw new IllegalArgumentException("Unknown wing operation");
                };
                if (!Double.isFinite(v)) {
                    throw new IllegalArgumentException("Wing expression result");
                }
                stack[n++] = v;
            }
            if (n != 1) {
                throw new IllegalArgumentException("Wing expression result");
            }
            return stack[0];
        }
        throw new IllegalArgumentException("Wing expression limit");
    }

    private static double[] key(JsonObject k, double t) {
        JsonArray a = k.getAsJsonArray("expr");
        if (a.size() != 3) {
            throw new IllegalArgumentException("Wing channel");
        }
        return new double[]{WingAnimation.expression(a.get(0).getAsJsonArray(), t), WingAnimation.expression(a.get(1).getAsJsonArray(), t), WingAnimation.expression(a.get(2).getAsJsonArray(), t)};
    }

    private static double[] channel(JsonObject ch, double t) {
        JsonArray a = ch.getAsJsonArray("keys");
        if (a.isEmpty()) {
            throw new IllegalArgumentException("Wing keys");
        }
        JsonObject first = a.get(0).getAsJsonObject();
        if (a.size() != 1 && !(t <= first.get("time").getAsDouble())) {
            for (int i = 1; i < a.size(); ++i) {
                JsonObject right = a.get(i).getAsJsonObject();
                double end = right.get("time").getAsDouble();
                if (!(t <= end)) continue;
                JsonObject left = a.get(i - 1).getAsJsonObject();
                double start = left.get("time").getAsDouble();
                double w = (t - start) / (end - start);
                double[] v = WingAnimation.key(left, t);
                double[] b = WingAnimation.key(right, t);
                for (int j = 0; j < 3; ++j) {
                    int n = j;
                    v[n] = v[n] + (b[j] - v[j]) * w;
                }
                return v;
            }
            return WingAnimation.key(a.get(a.size() - 1).getAsJsonObject(), t);
        }
        return WingAnimation.key(first, t);
    }

    public float[] pose(double time) {
        double t = this.duration > 0.0 ? (this.loop ? time % this.duration : Math.min(time, this.duration)) : time;
        double[][] cache = new double[this.bones.length][];
        boolean[] visiting = new boolean[this.bones.length];
        float[] out = new float[this.bones.length * 12];
        for (int i = 0; i < this.bones.length; ++i) {
            double[] m = WingAnimation.mul(WingAnimation.mul(WingAnimation.mul(A, this.world(i, t, cache, visiting)), WingAnimation.numbers(this.bones[i].getAsJsonArray("inverseBind"), 16)), IA);
            for (int j = 0; j < 12; ++j) {
                out[i * 12 + j] = (float)m[j];
                if (Float.isFinite(out[i * 12 + j])) continue;
                throw new IllegalArgumentException("Wing pose");
            }
        }
        return out;
    }

    private double[] world(int i, double t, double[][] cache, boolean[] visiting) {
        if (cache[i] != null) {
            return cache[i];
        }
        if (visiting[i]) {
            throw new IllegalArgumentException("Wing bone cycle");
        }
        visiting[i] = true;
        JsonObject b = this.bones[i];
        double[] pivot = WingAnimation.numbers(b.getAsJsonArray("pivot"), 3);
        double[] r = WingAnimation.numbers(b.getAsJsonArray("baseRotation"), 3);
        double[] delta = WingAnimation.channel(b.getAsJsonObject("rotation"), t);
        double[] s = WingAnimation.channel(b.getAsJsonObject("scale"), t);
        double[] p = WingAnimation.channel(b.getAsJsonObject("position"), t);
        for (int j = 0; j < 3; ++j) {
            int n = j;
            r[n] = r[n] + delta[j];
        }
        double[] scale = WingAnimation.identity();
        scale[0] = s[0];
        scale[5] = s[1];
        scale[10] = s[2];
        double[] m = WingAnimation.mul(WingAnimation.translation(p), WingAnimation.mul(WingAnimation.rotation(r, pivot), WingAnimation.mul(WingAnimation.translation(pivot), WingAnimation.mul(scale, WingAnimation.translation(new double[]{-pivot[0], -pivot[1], -pivot[2]})))));
        int parent = b.get("parent").getAsInt();
        if (parent >= 0) {
            m = WingAnimation.mul(this.world(parent, t, cache, visiting), m);
        }
        visiting[i] = false;
        cache[i] = m;
        return m;
    }

    private static double[] identity() {
        return new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
    }

    private static double[] translation(double[] p) {
        double[] m = WingAnimation.identity();
        m[3] = p[0];
        m[7] = p[1];
        m[11] = p[2];
        return m;
    }

    private static double[] rotation(double[] r, double[] p) {
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
        return WingAnimation.mul(WingAnimation.translation(p), WingAnimation.mul(WingAnimation.mul(rz, WingAnimation.mul(ry, rx)), WingAnimation.translation(new double[]{-p[0], -p[1], -p[2]})));
    }

    private static double[] mul(double[] a, double[] b) {
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
}

