package swerve.cosmetics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import swerve.Swerve;

/**
 * Bedrock-геометрия гардероба с её анимацией.
 *
 * <p>Порт из Arcanic: кости с кубами, дорожки анимации (поворот/позиция/масштаб),
 * сборка квадов в буфер. В кадре буферы переиспользуются — новых объектов нет.
 */
public final class CosmeticGeometry {

    private static final Map<Integer, CosmeticGeometry> CACHE = new HashMap<>();

    /** Квад: 4 вершины по (x, y, z, u, v) и нормаль — 23 float на квад. */
    private static final int STRIDE = 23;

    private static final String[] FACES = {"west", "east", "down", "up", "north", "south"};
    private static final float[][] NORMALS = {{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};

    private final ResourceLocation texture;
    private final List<Bone> roots = new ArrayList<>();
    private final Matrix4f[] stack = new Matrix4f[48];
    private final Vector3f tmp = new Vector3f();
    private final float[] key = new float[3];

    private int quadCount;
    private boolean animated;
    private float length = 1;
    private int frames = 1;
    private float frameMs = 150;
    private float[] buffer;

    private CosmeticGeometry(ResourceLocation texture) {
        this.texture = texture;
    }

    public ResourceLocation texture() {
        return texture;
    }

    public static CosmeticGeometry get(int n) {
        CosmeticGeometry geo = CACHE.get(n);
        if (geo == null) {
            geo = load(n);
            CACHE.put(n, geo);
        }
        return geo;
    }

    /** Сдвиг текстуры для кадровой анимации (полоса кадров сверху вниз). */
    public float frameShift(long now) {
        return frames <= 1 ? 0 : (float) ((now / (long) Math.max(1, frameMs)) % frames) / frames;
    }

    /** Позовые квады модели: 4 * (x, y, z, u, v) + нормаль на каждый квад. */
    public float[] pose(long now) {
        if (buffer != null && !animated) {
            return buffer;
        }
        if (buffer == null) {
            buffer = new float[quadCount * STRIDE];
        }
        float time = length <= 0 ? 0 : (now % (long) Math.max(1, length * 1000)) / 1000f;
        int[] at = {0};
        Matrix4f root = matrix(0).identity();
        for (int i = 0; i < roots.size(); i++) {
            bone(roots.get(i), root, 1, time, at);
        }
        return buffer;
    }

    private Matrix4f matrix(int depth) {
        if (stack[depth] == null) {
            stack[depth] = new Matrix4f();
        }
        return stack[depth];
    }

    private void bone(Bone b, Matrix4f parent, int depth, float time, int[] at) {
        if (depth >= stack.length - 2) {
            return;
        }
        Matrix4f m = matrix(depth).set(parent);
        float rx = b.rx, ry = b.ry, rz = b.rz, sx = 1, sy = 1, sz = 1;
        if (animated) {
            if (b.pos != null) {
                b.pos.at(time, key);
                if (key[0] == key[0]) {
                    m.translate(-key[0] / 16f, key[1] / 16f, key[2] / 16f);
                }
            }
            if (b.rot != null) {
                b.rot.at(time, key);
                if (key[0] == key[0]) {
                    rx += (float) Math.toRadians(-key[0]);
                    ry += (float) Math.toRadians(-key[1]);
                    rz += (float) Math.toRadians(key[2]);
                }
            }
            if (b.scl != null) {
                b.scl.at(time, key);
                if (key[0] == key[0]) {
                    sx = key[0];
                    sy = key[1];
                    sz = key[2];
                }
            }
        }
        m.translate(b.px, b.py, b.pz);
        if (rz != 0) {
            m.rotateZ(rz);
        }
        if (ry != 0) {
            m.rotateY(ry);
        }
        if (rx != 0) {
            m.rotateX(rx);
        }
        if (sx != 1 || sy != 1 || sz != 1) {
            m.scale(sx, sy, sz);
        }
        m.translate(-b.px, -b.py, -b.pz);
        for (Cube c : b.cubes) {
            Matrix4f cm = m;
            if (c.rx != 0 || c.ry != 0 || c.rz != 0) {
                cm = matrix(depth + 1).set(m).translate(c.px, c.py, c.pz);
                if (c.rz != 0) {
                    cm.rotateZ(c.rz);
                }
                if (c.ry != 0) {
                    cm.rotateY(c.ry);
                }
                if (c.rx != 0) {
                    cm.rotateX(c.rx);
                }
                cm.translate(-c.px, -c.py, -c.pz);
            }
            for (int f = 0; f < 6; f++) {
                if (!c.has[f]) {
                    continue;
                }
                int o = at[0];
                at[0] += STRIDE;
                for (int k = 0; k < 4; k++) {
                    int s = (f * 4 + k) * 5;
                    cm.transformPosition(c.v[s], c.v[s + 1], c.v[s + 2], tmp);
                    buffer[o + k * 5] = tmp.x;
                    buffer[o + k * 5 + 1] = tmp.y;
                    buffer[o + k * 5 + 2] = tmp.z;
                    buffer[o + k * 5 + 3] = c.v[s + 3];
                    buffer[o + k * 5 + 4] = c.v[s + 4];
                }
                cm.transformDirection(NORMALS[f][0], NORMALS[f][1], NORMALS[f][2], tmp);
                if (tmp.lengthSquared() > 1e-10f) {
                    tmp.normalize();
                }
                buffer[o + 20] = tmp.x;
                buffer[o + 21] = tmp.y;
                buffer[o + 22] = tmp.z;
            }
        }
        for (int i = 0; i < b.kids.size(); i++) {
            bone(b.kids.get(i), m, depth + 1, time, at);
        }
    }

    // ---- чтение модели

    private static CosmeticGeometry load(int n) {
        try (var in = CosmeticGeometry.class.getResourceAsStream("/assets/swerve/cosmetics/geo/cosmetic_" + n + ".json")) {
            if (in == null) {
                return null;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject g = root.getAsJsonObject("model")
                    .getAsJsonArray("minecraft:geometry")
                    .get(0)
                    .getAsJsonObject();
            JsonObject d = g.getAsJsonObject("description");
            float tw = num(d, "texture_width", 64), th = num(d, "texture_height", 64);
            CosmeticGeometry geo = new CosmeticGeometry(CosmeticCatalogue.texture(n));
            Map<String, Bone> byName = new HashMap<>();
            List<Bone> all = new ArrayList<>();
            if (g.has("bones")) {
                for (JsonElement be : g.getAsJsonArray("bones")) {
                    JsonObject bo = be.getAsJsonObject();
                    Bone b = new Bone();
                    b.name = bo.get("name").getAsString();
                    b.parentName = bo.has("parent") ? bo.get("parent").getAsString() : null;
                    float[] p = vec(bo, "pivot", 0, 0, 0), r = vec(bo, "rotation", 0, 0, 0);
                    b.px = -p[0] / 16f;
                    b.py = p[1] / 16f;
                    b.pz = p[2] / 16f;
                    b.rx = (float) Math.toRadians(-r[0]);
                    b.ry = (float) Math.toRadians(-r[1]);
                    b.rz = (float) Math.toRadians(r[2]);
                    if (bo.has("cubes")) {
                        for (JsonElement ce : bo.getAsJsonArray("cubes")) {
                            Cube c = cube(ce.getAsJsonObject(), tw, th);
                            b.cubes.add(c);
                            for (boolean h : c.has) {
                                if (h) {
                                    geo.quadCount++;
                                }
                            }
                        }
                    }
                    byName.put(b.name, b);
                    all.add(b);
                }
            }
            for (Bone b : all) {
                Bone parent = b.parentName == null ? null : byName.get(b.parentName);
                if (parent != null && parent != b) {
                    parent.kids.add(b);
                } else {
                    geo.roots.add(b);
                }
            }
            if (root.has("textureAnimation")) {
                JsonObject ta = root.getAsJsonObject("textureAnimation");
                geo.frames = Math.max(1, (int) num(ta, "frameCount", 1));
                geo.frameMs = Math.max(1, num(ta, "frameTime", 3)) * 50;
            }
            if (root.has("animation")) {
                animation(geo, root.getAsJsonObject("animation"), byName);
            }
            return geo;
        } catch (Exception e) {
            Swerve.LOGGER.warn("cosmetic model {}: {}", n, e.toString());
            return null;
        }
    }

    private static Cube cube(JsonObject o, float tw, float th) {
        float[] origin = vec(o, "origin", 0, 0, 0);
        float[] size = vec(o, "size", 1, 1, 1);
        float[] pivot = vec(o, "pivot", origin[0], origin[1], origin[2]);
        float[] rot = vec(o, "rotation", 0, 0, 0);
        float inflate = num(o, "inflate", 0);
        Cube c = new Cube();
        c.px = -pivot[0] / 16f;
        c.py = pivot[1] / 16f;
        c.pz = pivot[2] / 16f;
        c.rx = (float) Math.toRadians(-rot[0]);
        c.ry = (float) Math.toRadians(-rot[1]);
        c.rz = (float) Math.toRadians(rot[2]);
        float x = origin[0] - inflate;
        float y = origin[1] - inflate;
        float z = origin[2] - inflate;
        float w = size[0] + inflate * 2;
        float h = size[1] + inflate * 2;
        float dp = size[2] + inflate * 2;
        float x0 = -(x + w) / 16f;
        float y0 = y / 16f;
        float z0 = z / 16f;
        float x1 = -x / 16f;
        float y1 = (y + h) / 16f;
        float z1 = (z + dp) / 16f;
        JsonElement uv = o.get("uv");
        for (int f = 0; f < 6; f++) {
            float u, v, du, dv;
            if (uv != null && uv.isJsonObject()) {
                JsonObject face = uv.getAsJsonObject().has(FACES[f])
                        ? uv.getAsJsonObject().getAsJsonObject(FACES[f])
                        : null;
                if (face == null || !face.has("uv")) {
                    continue;
                }
                float[] a = vec(face, "uv", 0, 0);
                float[] s = vec(face, "uv_size", 0, 0);
                u = a[0] / tw;
                v = a[1] / th;
                du = s[0] / tw;
                dv = s[1] / th;
            } else {
                // классическая раскладка: верх и низ сверху, четыре бока в ряд под ними
                float bu = 0, bv = 0;
                if (uv != null && uv.isJsonArray()) {
                    bu = uv.getAsJsonArray().get(0).getAsFloat();
                    bv = uv.getAsJsonArray().get(1).getAsFloat();
                }
                float sw = size[0], sh = size[1], sd = size[2];
                switch (f) {
                    case 0 -> {
                        u = bu + sd + sw;
                        v = bv + sd;
                        du = sd;
                        dv = sh;
                    }
                    case 1 -> {
                        u = bu;
                        v = bv + sd;
                        du = sd;
                        dv = sh;
                    }
                    case 2 -> {
                        u = bu + sd + sw;
                        v = bv;
                        du = sw;
                        dv = sd;
                    }
                    case 3 -> {
                        u = bu + sd;
                        v = bv;
                        du = sw;
                        dv = sd;
                    }
                    case 4 -> {
                        u = bu + sd;
                        v = bv + sd;
                        du = sw;
                        dv = sh;
                    }
                    default -> {
                        u = bu + sd + sw + sd;
                        v = bv + sd;
                        du = sw;
                        dv = sh;
                    }
                }
                u /= tw;
                v /= th;
                du /= tw;
                dv /= th;
            }
            float u1 = u + du, v1 = v + dv;
            float[][] p = switch (f) {
                case 0 -> new float[][] {
                        {x0, y1, z0, u, v}, {x0, y1, z1, u1, v}, {x0, y0, z1, u1, v1}, {x0, y0, z0, u, v1}
                };
                case 1 -> new float[][] {
                        {x1, y1, z1, u, v}, {x1, y1, z0, u1, v}, {x1, y0, z0, u1, v1}, {x1, y0, z1, u, v1}
                };
                case 2 -> new float[][] {
                        {x1, y0, z0, u, v}, {x1, y0, z1, u, v1}, {x0, y0, z1, u1, v1}, {x0, y0, z0, u1, v}
                };
                case 3 -> new float[][] {
                        {x0, y1, z0, u, v}, {x0, y1, z1, u1, v}, {x1, y1, z1, u1, v1}, {x1, y1, z0, u, v1}
                };
                case 4 -> new float[][] {
                        {x1, y1, z0, u, v}, {x0, y1, z0, u1, v}, {x0, y0, z0, u1, v1}, {x1, y0, z0, u, v1}
                };
                default -> new float[][] {
                        {x0, y1, z1, u, v}, {x1, y1, z1, u1, v}, {x1, y0, z1, u1, v1}, {x0, y0, z1, u, v1}
                };
            };
            for (int k = 0; k < 4; k++) {
                System.arraycopy(p[k], 0, c.v, (f * 4 + k) * 5, 5);
            }
            c.has[f] = true;
        }
        return c;
    }

    private static void animation(CosmeticGeometry geo, JsonObject anim, Map<String, Bone> byName) {
        if (!anim.has("animations") || !anim.get("animations").isJsonObject()) {
            return;
        }
        JsonObject a = null, first = null;
        for (var e : anim.getAsJsonObject("animations").entrySet()) {
            if (!e.getValue().isJsonObject()) {
                continue;
            }
            String name = e.getKey().toLowerCase(Locale.ROOT);
            if (name.contains("gui")) {
                continue;
            }
            if (first == null) {
                first = e.getValue().getAsJsonObject();
            }
            if (name.contains("idle")) {
                a = e.getValue().getAsJsonObject();
                break;
            }
        }
        if (a == null) {
            a = first;
        }
        if (a == null) {
            return;
        }
        geo.length = num(a, "animation_length", 0);
        if (!a.has("bones") || !a.get("bones").isJsonObject()) {
            return;
        }
        float longest = 0;
        for (var e : a.getAsJsonObject("bones").entrySet()) {
            Bone b = byName.get(e.getKey());
            if (b == null || !e.getValue().isJsonObject()) {
                continue;
            }
            JsonObject ch = e.getValue().getAsJsonObject();
            b.rot = track(ch.get("rotation"));
            b.pos = track(ch.get("position"));
            b.scl = track(ch.get("scale"));
            for (Track t : new Track[] {b.rot, b.pos, b.scl}) {
                if (t != null) {
                    geo.animated = true;
                    longest = Math.max(longest, t.t()[t.t().length - 1]);
                }
            }
        }
        if (geo.length <= 0) {
            geo.length = longest;
        }
        if (geo.length <= 0) {
            geo.animated = false;
        }
    }

    private static Track track(JsonElement e) {
        if (e == null || e.isJsonNull()) {
            return null;
        }
        List<float[]> keys = new ArrayList<>();
        if (e.isJsonObject() && !e.getAsJsonObject().has("vector") && !e.getAsJsonObject().has("post")) {
            for (var k : e.getAsJsonObject().entrySet()) {
                float[] v = value(k.getValue());
                if (v == null) {
                    continue;
                }
                try {
                    keys.add(new float[] {Float.parseFloat(k.getKey()), v[0], v[1], v[2]});
                } catch (NumberFormatException ignored) {
                }
            }
        } else {
            float[] v = value(e);
            if (v != null) {
                keys.add(new float[] {0, v[0], v[1], v[2]});
            }
        }
        if (keys.isEmpty()) {
            return null;
        }
        keys.sort((p, q) -> Float.compare(p[0], q[0]));
        float[] t = new float[keys.size()];
        float[][] v = new float[keys.size()][];
        for (int i = 0; i < t.length; i++) {
            t[i] = keys.get(i)[0];
            v[i] = new float[] {keys.get(i)[1], keys.get(i)[2], keys.get(i)[3]};
        }
        return new Track(t, v);
    }

    private static float[] value(JsonElement e) {
        try {
            if (e.isJsonObject()) {
                JsonObject o = e.getAsJsonObject();
                e = o.has("vector") ? o.get("vector") : o.has("post") ? o.get("post") : null;
                if (e == null) {
                    return null;
                }
            }
            if (e.isJsonArray()) {
                JsonArray a = e.getAsJsonArray();
                if (a.size() < 3) {
                    return null;
                }
                return new float[] {a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat()};
            }
            if (e.isJsonPrimitive()) {
                float f = e.getAsFloat();
                return new float[] {f, f, f};
            }
        } catch (RuntimeException ignored) {
        }
        return null;
    }

    private static float[] vec(JsonObject o, String k, float... def) {
        if (!o.has(k) || !o.get(k).isJsonArray()) {
            return def;
        }
        JsonArray a = o.getAsJsonArray(k);
        float[] v = def.clone();
        for (int i = 0; i < v.length && i < a.size(); i++) {
            try {
                v[i] = a.get(i).getAsFloat();
            } catch (RuntimeException ignored) {
            }
        }
        return v;
    }

    private static float num(JsonObject o, String k, float def) {
        return o.has(k) && o.get(k).isJsonPrimitive() ? o.get(k).getAsFloat() : def;
    }

    // ---- части модели

    /** Кость: своя pivot-точка, поворот и дочерние кости. */
    private static final class Bone {
        String name, parentName;
        final List<Bone> kids = new ArrayList<>();
        final List<Cube> cubes = new ArrayList<>();
        float px, py, pz, rx, ry, rz;
        Track rot, pos, scl;
    }

    /** Куб: 6 граней по 4 вершины (x, y, z, u, v); отсутствующая грань помечена в has[]. */
    private static final class Cube {
        float px, py, pz, rx, ry, rz;
        final float[] v = new float[6 * 4 * 5];
        final boolean[] has = new boolean[6];
    }

    /** Дорожка анимации: время в секундах и вектор на каждый ключ. */
    private record Track(float[] t, float[][] v) {
        void at(float time, float[] out) {
            int n = t.length;
            if (n == 0) {
                out[0] = out[1] = out[2] = Float.NaN;
                return;
            }
            if (time <= t[0]) {
                System.arraycopy(v[0], 0, out, 0, 3);
                return;
            }
            if (time >= t[n - 1]) {
                System.arraycopy(v[n - 1], 0, out, 0, 3);
                return;
            }
            int i = 1;
            while (t[i] < time) {
                i++;
            }
            float k = (time - t[i - 1]) / Math.max(1e-6f, t[i] - t[i - 1]);
            for (int c = 0; c < 3; c++) {
                out[c] = v[i - 1][c] + (v[i][c] - v[i - 1][c]) * k;
            }
        }
    }
}
