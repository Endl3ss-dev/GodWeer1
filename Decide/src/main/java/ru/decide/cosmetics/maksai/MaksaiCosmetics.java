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
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.minecraft.class_2960
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.cosmetics.maksai;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.class_2960;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.cosmetics.maksai.companions.CompanionRuntime;
import ru.decide.cosmetics.maksai.core.CloakCatalog;
import ru.decide.cosmetics.maksai.core.CloakEntry;
import ru.decide.cosmetics.maksai.hats.HatManager;

@Environment(value=EnvType.CLIENT)
public final class MaksaiCosmetics {
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:Maksai");
    private static final int CLOAK_INDEX_BASE = 200000;
    private static final int HAT_INDEX_BASE = 300000;
    private static CloakCatalog catalog = CloakCatalog.empty("not_loaded");
    private static final Map<Integer, HatManager.Entry> hatByIndex = new HashMap<Integer, HatManager.Entry>();
    private static final Set<Integer> failedSelects = new HashSet<Integer>();
    private static final Map<Integer, Mesh> meshCache = new ConcurrentHashMap<Integer, Mesh>();
    private static final Set<Integer> meshFailed = ConcurrentHashMap.newKeySet();
    private static volatile boolean renderReady;
    private static boolean initialized;
    private static final float HEAD_BAND_Y = -0.2555f;

    private MaksaiCosmetics() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        HatManager.initialize();
        CompanionRuntime.initialize();
        catalog = CloakCatalog.loadFromResource();
        if (catalog.error() != null) {
            LOGGER.warn("\u041a\u0430\u0442\u0430\u043b\u043e\u0433 \u043f\u043b\u0430\u0449\u0435\u0439 \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d: {}", (Object)catalog.error());
        } else {
            LOGGER.info("\u041a\u0430\u0442\u0430\u043b\u043e\u0433 \u043f\u043b\u0430\u0449\u0435\u0439: {} \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u043c\u044b\u0445, {} \u043f\u0440\u043e\u043f\u0443\u0449\u0435\u043d\u043e", (Object)catalog.all().size(), (Object)catalog.unsupportedCount());
        }
        CosmeticManager cm = CosmeticManager.getInstance();
        MaksaiCosmetics.registerCloaks(cm);
        MaksaiCosmetics.registerHats(cm);
        cm.normalizeSelection();
        cm.setSelectionListener(MaksaiCosmetics::reconcile);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            renderReady = true;
            HatManager.tick();
            CompanionRuntime.tick();
            MaksaiCosmetics.reconcile();
        });
        LOGGER.info("\u041a\u043e\u0441\u043c\u0435\u0442\u0438\u043a\u0430 DecideVisuals \u043f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0430: {} \u043f\u043b\u0430\u0449\u0435\u0439, {} \u043c\u0435\u0448-\u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432", (Object)catalog.all().size(), (Object)hatByIndex.size());
    }

    private static void registerCloaks(CosmeticManager cm) {
        for (CloakEntry e : catalog.all()) {
            class_2960 texture = class_2960.method_60655((String)"decide", (String)("cosmetics/cloaks/" + e.id + ".png"));
            int frames = Math.max(1, e.frames);
            cm.registerCapeAnimation(texture, new CosmeticManager.CapeAnimationInfo(frames, Math.max(1, e.frametime), false, 22.0f, 17.0f, true));
            cm.addEntry(new CosmeticManager.CosmeticEntry(200000 + e.id, e.name, "cape", true, texture, null));
        }
    }

    private static void registerHats(CosmeticManager cm) {
        for (String category : HatManager.categories()) {
            String type = MaksaiCosmetics.decidevisualsType(category);
            for (HatManager.Entry e : HatManager.search(category, "")) {
                int index = 300000 + e.id();
                hatByIndex.put(index, e);
                class_2960 preview = class_2960.method_60655((String)"decide", (String)("cosmetics/hats/" + e.id() + "/preview.png"));
                CosmeticManager.CosmeticEntry entry = new CosmeticManager.CosmeticEntry(index, e.name(), type, false, preview, null);
                entry.group = e.category();
                cm.addEntry(entry);
            }
        }
    }

    private static String decidevisualsType(String category) {
        return switch (category) {
            case "dragon_wings" -> "wings";
            case "hat" -> "hat";
            case "mask" -> "mask";
            case "pet" -> "pet";
            case "glasses" -> "glasses";
            case "auras" -> "auras";
            case "backpack" -> "accessories";
            case "watch" -> "watch";
            case "bracelet" -> "watch";
            case "neck" -> "neck";
            case "bandanna" -> "bandanna";
            case "suits" -> "suits";
            case "shoes" -> "shoes";
            case "companion" -> "companion";
            default -> "accessories";
        };
    }

    private static void reconcile() {
        if (!renderReady) {
            return;
        }
        CosmeticManager cm = CosmeticManager.getInstance();
        HashMap<String, HatManager.Entry> desired = new HashMap<String, HatManager.Entry>();
        for (Integer index : cm.getSelectedByType().values()) {
            HatManager.Entry entry = hatByIndex.get(index);
            if (entry == null) continue;
            desired.put(entry.category(), entry);
        }
        for (HatManager.Equipped equipped : HatManager.allEquipped()) {
            HatManager.Entry want = (HatManager.Entry)desired.get(equipped.entry().category());
            if (want != null && want.id() == equipped.entry().id()) continue;
            HatManager.clear(equipped.entry().category());
        }
        for (HatManager.Entry want : desired.values()) {
            HatManager.Equipped current = HatManager.equipped(want.category());
            if (current != null && current.entry().id() == want.id() || failedSelects.contains(want.id()) || HatManager.select(want)) continue;
            failedSelects.add(want.id());
        }
    }

    public static boolean isMaksaiEntry(int index) {
        return hatByIndex.containsKey(index);
    }

    public static Mesh mesh(int index) {
        if (!hatByIndex.containsKey(index) || meshFailed.contains(index)) {
            return null;
        }
        Mesh cached = meshCache.get(index);
        if (cached != null) {
            return cached;
        }
        Mesh loaded = MaksaiCosmetics.loadMesh(hatByIndex.get(index));
        if (loaded == null) {
            meshFailed.add(index);
            return null;
        }
        meshCache.put(index, loaded);
        return loaded;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Mesh loadMesh(HatManager.Entry entry) {
        try (InputStream in = MaksaiCosmetics.class.getResourceAsStream("/assets/decide/cosmetics/hats/" + entry.id() + "/mesh.json");){
            if (in == null) {
                Mesh mesh = null;
                return mesh;
            }
            JsonObject model = JsonParser.parseReader((Reader)new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray source = model.getAsJsonArray("vertices");
            if (source == null || source.isEmpty() || source.size() % 4 != 0 || source.size() > 80000) {
                Mesh mesh = null;
                return mesh;
            }
            float[] vertices = new float[source.size() * 8];
            int k = 0;
            for (JsonElement vertex : source) {
                JsonArray row = vertex.getAsJsonArray();
                if (row.size() != 8) {
                    Iterator iterator = null;
                    return iterator;
                }
                for (JsonElement number : row) {
                    vertices[k++] = number.getAsFloat();
                }
            }
            if ("HEAD".equals(entry.attachment())) {
                vertices = MaksaiCosmetics.adjustHeadMeshY(vertices);
            }
            float[][] frames = new float[][]{};
            int[] boneIndices = new int[]{};
            if (model.has("frames") && model.has("boneIndices")) {
                JsonArray sourceFrames = model.getAsJsonArray("frames");
                JsonArray sourceIndices = model.getAsJsonArray("boneIndices");
                if (sourceFrames.size() > 0 && sourceIndices.size() == source.size()) {
                    int boneCount = sourceFrames.get(0).getAsJsonArray().size() / 12;
                    boneIndices = new int[source.size()];
                    for (int i = 0; i < boneIndices.length; ++i) {
                        boneIndices[i] = sourceIndices.get(i).getAsInt();
                        if (boneIndices[i] >= 0 && boneIndices[i] < boneCount) continue;
                        boneIndices[i] = 0;
                    }
                    frames = new float[sourceFrames.size()][];
                    for (int f = 0; f < sourceFrames.size(); ++f) {
                        JsonArray frame = sourceFrames.get(f).getAsJsonArray();
                        frames[f] = new float[frame.size()];
                        for (int i = 0; i < frame.size(); ++i) {
                            frames[f][i] = frame.get(i).getAsFloat();
                        }
                    }
                }
            }
            int fps = model.has("fps") ? model.get("fps").getAsInt() : 0;
            boolean loop = model.has("loop") && model.get("loop").getAsBoolean();
            Mesh mesh = new Mesh(vertices, boneIndices, frames, fps, loop, entry.texture());
            return mesh;
        }
        catch (Exception exception) {
            LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u043c\u0435\u0448 \u0434\u043b\u044f \u043f\u0440\u0435\u0432\u044c\u044e: {}", (Object)entry.id(), (Object)exception);
            return null;
        }
    }

    public static float[] adjustHeadMeshY(float[] vertices) {
        float minY = Float.MAX_VALUE;
        float maxY = -3.4028235E38f;
        for (int i = 1; i < vertices.length; i += 8) {
            float y = vertices[i];
            if (y < minY) {
                minY = y;
            }
            if (!(y > maxY)) continue;
            maxY = y;
        }
        if (minY >= 0.05f) {
            float dy = -0.2555f - maxY;
            float[] shifted = (float[])vertices.clone();
            for (int i = 1; i < shifted.length; i += 8) {
                int n = i;
                shifted[n] = shifted[n] + dy;
            }
            return shifted;
        }
        return vertices;
    }

    @Environment(value=EnvType.CLIENT)
    public record Mesh(float[] vertices, int[] boneIndices, float[][] frames, int fps, boolean loop, class_2960 texture) {
    }
}

