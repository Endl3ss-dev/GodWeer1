/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.melee;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.melee.MeleeClip;
import ru.decide.cosmetics.melee.MeleeKeyframe;
import ru.decide.cosmetics.melee.MeleeTrack;

@Environment(value=EnvType.CLIENT)
public final class MeleeAnimParser {
    private static final String ANIMATIONS = "animations";
    private static final String ANIMATION_LENGTH = "animation_length";
    private static final String BONES = "bones";
    private static final String ROTATION = "rotation";
    private static final String POSITION = "position";
    private static final String SCALE = "scale";
    private static final String SOUND_EFFECTS = "sound_effects";
    private static final String EFFECT = "effect";
    private static final String PRE = "pre";
    private static final String POST = "post";
    private static final String LERP_MODE = "lerp_mode";
    private static final String CATMULLROM = "catmullrom";

    private MeleeAnimParser() {
    }

    public static Map<String, MeleeClip> parse(String json, Map<String, Integer> boneIndex) {
        JsonObject root = JsonParser.parseString((String)json).getAsJsonObject();
        JsonObject animations = root.getAsJsonObject(ANIMATIONS);
        if (animations == null) {
            return Map.of();
        }
        HashMap<String, MeleeClip> clips = new HashMap<String, MeleeClip>();
        for (Map.Entry entry : animations.entrySet()) {
            MeleeClip clip;
            if (!((JsonElement)entry.getValue()).isJsonObject() || (clip = MeleeAnimParser.parseClip((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsJsonObject(), boneIndex)) == null) continue;
            clips.put((String)entry.getKey(), clip);
        }
        return Map.copyOf(clips);
    }

    private static MeleeClip parseClip(String name, JsonObject animation, Map<String, Integer> boneIndex) {
        ArrayList<MeleeTrack> tracks = new ArrayList<MeleeTrack>();
        float maxTime = 0.0f;
        JsonObject bones = animation.getAsJsonObject(BONES);
        if (bones != null) {
            for (Map.Entry entry : bones.entrySet()) {
                Integer target = boneIndex.get(entry.getKey());
                if (target == null || !((JsonElement)entry.getValue()).isJsonObject()) continue;
                JsonObject bone = ((JsonElement)entry.getValue()).getAsJsonObject();
                MeleeKeyframe rotation = MeleeAnimParser.parseChannel(bone.get(ROTATION), 0.0f);
                MeleeKeyframe position = MeleeAnimParser.parseChannel(bone.get(POSITION), 0.0f);
                MeleeKeyframe scale = MeleeAnimParser.parseChannel(bone.get(SCALE), 1.0f);
                if (rotation == null && position == null && scale == null) continue;
                maxTime = Math.max(maxTime, MeleeAnimParser.longestTime(rotation, position, scale));
                tracks.add(new MeleeTrack(target, rotation, position, scale));
            }
        }
        if (tracks.isEmpty()) {
            return null;
        }
        float length = animation.has(ANIMATION_LENGTH) ? animation.get(ANIMATION_LENGTH).getAsFloat() : maxTime;
        return new MeleeClip(name, Math.max(length, 0.0f), tracks.toArray(new MeleeTrack[0]), MeleeAnimParser.soundTimes(animation), MeleeAnimParser.soundNames(animation));
    }

    private static float longestTime(MeleeKeyframe ... channels) {
        float max = 0.0f;
        for (MeleeKeyframe channel : channels) {
            if (channel == null) continue;
            max = Math.max(max, channel.lastTime());
        }
        return max;
    }

    private static MeleeKeyframe parseChannel(JsonElement element2, float neutral) {
        if (element2 == null || element2.isJsonNull()) {
            return null;
        }
        if (!element2.isJsonObject()) {
            float[] value = MeleeAnimParser.readVector(element2, neutral);
            return new MeleeKeyframe(new float[]{0.0f}, value, (float[])value.clone(), new boolean[]{false});
        }
        TreeMap<Float, JsonElement> sorted = new TreeMap<Float, JsonElement>();
        for (Map.Entry entry : element2.getAsJsonObject().entrySet()) {
            try {
                sorted.put(Float.valueOf(Float.parseFloat((String)entry.getKey())), (JsonElement)entry.getValue());
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        if (sorted.isEmpty()) {
            return null;
        }
        int count = sorted.size();
        float[] times = new float[count];
        float[] pre = new float[count * 3];
        float[] post = new float[count * 3];
        boolean[] smooth = new boolean[count];
        int index = 0;
        for (Map.Entry entry : sorted.entrySet()) {
            times[index] = ((Float)entry.getKey()).floatValue();
            MeleeAnimParser.readKeyframe((JsonElement)entry.getValue(), neutral, pre, post, smooth, index);
            ++index;
        }
        return new MeleeKeyframe(times, pre, post, smooth);
    }

    private static void readKeyframe(JsonElement element2, float neutral, float[] pre, float[] post, boolean[] smooth, int index) {
        int base = index * 3;
        if (!element2.isJsonObject()) {
            float[] value = MeleeAnimParser.readVector(element2, neutral);
            MeleeAnimParser.store(value, pre, base);
            MeleeAnimParser.store(value, post, base);
            return;
        }
        JsonObject object = element2.getAsJsonObject();
        smooth[index] = object.has(LERP_MODE) && CATMULLROM.equals(object.get(LERP_MODE).getAsString());
        float[] postValue = object.has(POST) ? MeleeAnimParser.readVector(object.get(POST), neutral) : MeleeAnimParser.readVector(object.get(PRE), neutral);
        float[] preValue = object.has(PRE) ? MeleeAnimParser.readVector(object.get(PRE), neutral) : postValue;
        MeleeAnimParser.store(preValue, pre, base);
        MeleeAnimParser.store(postValue, post, base);
    }

    private static void store(float[] value, float[] target, int base) {
        target[base] = value[0];
        target[base + 1] = value[1];
        target[base + 2] = value[2];
    }

    private static float[] readVector(JsonElement element2, float neutral) {
        float[] result = new float[]{neutral, neutral, neutral};
        if (element2 == null || element2.isJsonNull()) {
            return result;
        }
        if (element2.isJsonPrimitive()) {
            float value;
            result[0] = value = element2.getAsFloat();
            result[1] = value;
            result[2] = value;
            return result;
        }
        if (!element2.isJsonArray()) {
            return result;
        }
        JsonArray array = element2.getAsJsonArray();
        for (int i = 0; i < 3 && i < array.size(); ++i) {
            JsonElement value = array.get(i);
            result[i] = value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber() ? value.getAsFloat() : neutral;
        }
        return result;
    }

    private static float[] soundTimes(JsonObject animation) {
        TreeMap<Float, String> cues = MeleeAnimParser.soundCues(animation);
        float[] times = new float[cues.size()];
        int index = 0;
        for (Float time : cues.keySet()) {
            times[index++] = time.floatValue();
        }
        return times;
    }

    private static String[] soundNames(JsonObject animation) {
        TreeMap<Float, String> cues = MeleeAnimParser.soundCues(animation);
        return cues.values().toArray(new String[0]);
    }

    private static TreeMap<Float, String> soundCues(JsonObject animation) {
        TreeMap<Float, String> cues = new TreeMap<Float, String>();
        JsonObject effects = animation.getAsJsonObject(SOUND_EFFECTS);
        if (effects == null) {
            return cues;
        }
        for (Map.Entry entry : effects.entrySet()) {
            try {
                JsonElement value = (JsonElement)entry.getValue();
                String name = value.isJsonObject() ? value.getAsJsonObject().get(EFFECT).getAsString() : value.getAsString();
                if (name == null || name.isEmpty()) continue;
                cues.put(Float.valueOf(Float.parseFloat((String)entry.getKey())), name);
            }
            catch (Exception exception) {}
        }
        return cues;
    }
}

