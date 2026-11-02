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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.maksai.hats.Molang;
import ru.decide.cosmetics.maksai.hats.PetMotion;

@Environment(value=EnvType.CLIENT)
public final class PetMotionLoader {
    static double[] nums(JsonArray a, int n) {
        if (a.size() != n) {
            throw new IllegalArgumentException("array size");
        }
        double[] r = new double[n];
        for (int i = 0; i < n; ++i) {
            r[i] = a.get(i).getAsDouble();
            if (Double.isFinite(r[i])) continue;
            throw new IllegalArgumentException("nonfinite");
        }
        return r;
    }

    public static PetMotion parse(JsonObject root) {
        JsonArray a = root.getAsJsonArray("bones");
        PetMotion.Bone[] bones = new PetMotion.Bone[a.size()];
        HashMap<String, Integer> ids = new HashMap<String, Integer>();
        for (int i = 0; i < bones.length; ++i) {
            JsonObject b = a.get(i).getAsJsonObject();
            ids.put(b.get("name").getAsString(), i);
            bones[i] = new PetMotion.Bone(b.get("parent").getAsInt(), PetMotionLoader.nums(b.getAsJsonArray("pivot"), 3), PetMotionLoader.nums(b.getAsJsonArray("rotation"), 3), PetMotionLoader.nums(b.getAsJsonArray("inverseBind"), 16));
            if (bones[i].parent() >= -1 && bones[i].parent() < bones.length && bones[i].parent() != i) continue;
            throw new IllegalArgumentException("parent");
        }
        LinkedHashMap<String, PetMotion.Clip> clips = new LinkedHashMap<String, PetMotion.Clip>();
        for (Map.Entry entry : root.getAsJsonObject("animations").entrySet()) {
            double length;
            JsonObject c = ((JsonElement)entry.getValue()).getAsJsonObject();
            ArrayList<PetMotion.Track> tracks = new ArrayList<PetMotion.Track>();
            double d = length = c.has("animation_length") ? c.get("animation_length").getAsDouble() : 0.0;
            if (c.has("bones")) {
                for (Map.Entry tr : c.getAsJsonObject("bones").entrySet()) {
                    JsonObject t = ((JsonElement)tr.getValue()).getAsJsonObject();
                    PetMotion.Channel r = PetMotionLoader.channel(t.get("rotation"), 0.0);
                    PetMotion.Channel p = PetMotionLoader.channel(t.get("position"), 0.0);
                    PetMotion.Channel s = PetMotionLoader.channel(t.get("scale"), 1.0);
                    tracks.add(new PetMotion.Track(ids.getOrDefault(tr.getKey(), -1), r, p, s));
                    if (c.has("animation_length")) continue;
                    for (PetMotion.Channel ch : new PetMotion.Channel[]{r, p, s}) {
                        for (PetMotion.Key k : ch.keys) {
                            length = Math.max(length, k.time());
                        }
                    }
                }
            }
            boolean loop = c.has("loop") && c.get("loop").isJsonPrimitive() && c.get("loop").getAsJsonPrimitive().isBoolean() && c.get("loop").getAsBoolean();
            clips.put((String)entry.getKey(), new PetMotion.Clip((String)entry.getKey(), length, loop, (PetMotion.Track[])tracks.toArray(PetMotion.Track[]::new)));
        }
        ArrayList<PetMotion.Controller> controllers = new ArrayList<PetMotion.Controller>();
        JsonObject sm = root.getAsJsonObject("state_machine");
        if (sm.has("controllers")) {
            for (Object el : sm.getAsJsonArray("controllers")) {
                JsonObject c = el.getAsJsonObject();
                ArrayList<PetMotion.State> states = new ArrayList<PetMotion.State>();
                String ticks = c.has("default_transition_ticks") ? c.get("default_transition_ticks").getAsString() : "0";
                for (JsonElement st : c.getAsJsonArray("states")) {
                    JsonObject s = st.getAsJsonObject();
                    String name = s.get("anim").getAsString();
                    if (!clips.containsKey(name)) continue;
                    states.add(new PetMotion.State(name, Molang.compile(s.has("plays_when") ? s.get("plays_when").getAsString() : "1"), Molang.compile(s.has("transition_ticks") ? s.get("transition_ticks").getAsString() : ticks), s.has("transition_easing") ? s.get("transition_easing").getAsString() : "linear"));
                }
                if (states.isEmpty()) continue;
                controllers.add(new PetMotion.Controller((PetMotion.State[])states.toArray(PetMotion.State[]::new)));
            }
        }
        if (controllers.isEmpty()) {
            String name;
            String string = name = sm.has("anim") ? sm.get("anim").getAsString() : null;
            if (!clips.containsKey(name)) {
                name = clips.containsKey("main") ? "main" : (clips.containsKey("idle") ? "idle" : (String)clips.keySet().iterator().next());
            }
            controllers.add(new PetMotion.Controller(new PetMotion.State[]{new PetMotion.State(name, Molang.compile("1"), Molang.compile("0"), "linear")}));
        }
        HashMap<String, Double> options = new HashMap<String, Double>();
        if (root.has("cosmetic_options")) {
            for (JsonElement el : root.getAsJsonArray("cosmetic_options")) {
                JsonElement d;
                JsonObject o = el.getAsJsonObject();
                if (!o.has("molang_query") || !o.has("default") || !(d = o.get("default")).isJsonPrimitive() || !d.getAsJsonPrimitive().isBoolean() && !d.getAsJsonPrimitive().isNumber()) continue;
                options.put(o.get("molang_query").getAsString(), d.getAsJsonPrimitive().isBoolean() ? (d.getAsBoolean() ? 1.0 : 0.0) : d.getAsDouble());
            }
        }
        PetMotion result = new PetMotion(bones, clips, (PetMotion.Controller[])controllers.toArray(PetMotion.Controller[]::new), options);
        result.setPlatformBones(ids.entrySet().stream().filter(e -> ((String)e.getKey()).toLowerCase(Locale.ROOT).contains("platform")).mapToInt(e -> (Integer)e.getValue()).toArray());
        return result;
    }

    static PetMotion.Channel channel(JsonElement v, double n) {
        ArrayList<PetMotion.Key> keys = new ArrayList<PetMotion.Key>();
        if (v != null && !v.isJsonNull()) {
            if (v.isJsonObject() && !v.getAsJsonObject().has("vector") && !v.getAsJsonObject().has("pre") && !v.getAsJsonObject().has("post")) {
                for (Map.Entry e : v.getAsJsonObject().entrySet()) {
                    keys.add(PetMotionLoader.key(Double.parseDouble((String)e.getKey()), (JsonElement)e.getValue()));
                }
                keys.sort(Comparator.comparingDouble(PetMotion.Key::time));
            } else {
                keys.add(PetMotionLoader.key(0.0, v));
            }
            return new PetMotion.Channel((PetMotion.Key[])keys.toArray(PetMotion.Key[]::new), n);
        }
        return new PetMotion.Channel(new PetMotion.Key[0], n);
    }

    static PetMotion.Key key(double time, JsonElement v) {
        JsonElement pre = v;
        JsonElement post = v;
        String ease = "linear";
        boolean cat = false;
        if (v.isJsonObject()) {
            JsonObject o = v.getAsJsonObject();
            if (o.has("vector")) {
                pre = post = o.get("vector");
            } else {
                pre = o.has("pre") ? o.get("pre") : o.get("post");
                JsonElement jsonElement = post = o.has("post") ? o.get("post") : pre;
            }
            if (o.has("easing")) {
                ease = o.get("easing").getAsString();
            }
            cat = o.has("lerp_mode") && o.get("lerp_mode").getAsString().equals("catmullrom");
        }
        return new PetMotion.Key(time, PetMotionLoader.expr(pre), PetMotionLoader.expr(post), ease, cat);
    }

    static Molang.Program[] expr(JsonElement v) {
        int depth = 0;
        while (v != null && v.isJsonObject()) {
            JsonObject wrapper = v.getAsJsonObject();
            if (wrapper.has("vector") && ++depth <= 8) {
                v = wrapper.get("vector");
                continue;
            }
            throw new IllegalArgumentException("Unsupported vector object");
        }
        if (v != null && !v.isJsonNull()) {
            Object[] p = new Molang.Program[3];
            if (v.isJsonArray()) {
                JsonArray a = v.getAsJsonArray();
                if (a.size() != 3) {
                    throw new IllegalArgumentException("vector size");
                }
                for (int i = 0; i < 3; ++i) {
                    JsonElement component = a.get(i);
                    if (!component.isJsonPrimitive()) {
                        throw new IllegalArgumentException("Invalid vector component");
                    }
                    p[i] = Molang.compile(component.getAsString());
                }
            } else {
                if (!v.isJsonPrimitive()) {
                    throw new IllegalArgumentException("Invalid vector");
                }
                Arrays.fill(p, Molang.compile(v.getAsString()));
            }
            return p;
        }
        throw new IllegalArgumentException("missing vector");
    }
}

