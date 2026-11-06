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
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 *  net.minecraft.class_2960
 */
package ru.decide.cosmetics.maksai.hats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_2960;
import ru.decide.cosmetics.maksai.MaksaiCosmetics;
import ru.decide.cosmetics.maksai.hats.LivePetState;
import ru.decide.cosmetics.maksai.hats.PartBindings;
import ru.decide.cosmetics.maksai.hats.PetMotion;
import ru.decide.cosmetics.maksai.hats.PetMotionLoader;
import ru.decide.cosmetics.maksai.hats.TextureUvAnimation;
import ru.decide.cosmetics.maksai.hats.WingAnimation;
import ru.decide.cosmetics.maksai.render.Gfx;

@Environment(value=EnvType.CLIENT)
public final class HatManager {
    private static List<Entry> entries = List.of();
    private static final Map<String, Equipped> active = new LinkedHashMap<String, Equipped>();
    private static boolean initialized;
    private static boolean restored;
    private static final Map<String, Integer> savedIds;
    private static String error;
    private static final Map<Integer, Map<String, Boolean>> savedOptions;
    private static boolean hideHeadwearWithHelmet;
    private static final Map<Integer, TextureUvAnimation> previewMotion;
    private static final Map<Integer, int[]> previewSize;
    private static final LinkedHashMap<Integer, class_2960> previews;
    private static final Set<Integer> failedPreviews;
    private static int previewLoads;

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("decide-cosmetics-hats.json");
    }

    private static InputStream resource(String path) throws IOException {
        InputStream s = HatManager.class.getResourceAsStream("/assets/decide/cosmetics/" + path);
        if (s == null) {
            throw new FileNotFoundException(path);
        }
        return s;
    }

    public static void initialize() {
        if (!initialized) {
            initialized = true;
            try (InputStream s = HatManager.resource("catalog/hats.json");
                 InputStreamReader r = new InputStreamReader(s, StandardCharsets.UTF_8);){
                ArrayList<Entry> list = new ArrayList<Entry>();
                for (JsonElement v : JsonParser.parseReader((Reader)r).getAsJsonArray()) {
                    JsonObject o = v.getAsJsonObject();
                    list.add(new Entry(o.get("id").getAsInt(), o.get("name").getAsString(), o.get("search").getAsString(), o.get("category").getAsString(), o.get("attachment").getAsString(), o.get("animated").getAsBoolean()));
                }
                list.sort(Comparator.comparing(Entry::name, String.CASE_INSENSITIVE_ORDER));
                entries = List.copyOf(list);
                if (Files.isRegularFile(HatManager.configPath(), new LinkOption[0])) {
                    JsonObject c = JsonParser.parseString((String)Files.readString(HatManager.configPath(), StandardCharsets.UTF_8)).getAsJsonObject();
                    if (c.has("hideHeadwearWithHelmet")) {
                        hideHeadwearWithHelmet = c.get("hideHeadwearWithHelmet").getAsBoolean();
                    }
                    if (c.has("options")) {
                        for (Map.Entry item : c.getAsJsonObject("options").entrySet()) {
                            try {
                                int id = Integer.parseInt((String)item.getKey());
                                HashMap<String, Boolean> values = new HashMap<String, Boolean>();
                                for (Map.Entry value : ((JsonElement)item.getValue()).getAsJsonObject().entrySet()) {
                                    if (!((JsonElement)value.getValue()).isJsonPrimitive() || !((JsonElement)value.getValue()).getAsJsonPrimitive().isBoolean()) continue;
                                    values.put((String)value.getKey(), ((JsonElement)value.getValue()).getAsBoolean());
                                }
                                savedOptions.put(id, values);
                            }
                            catch (RuntimeException runtimeException) {}
                        }
                    }
                    if (c.has("selected")) {
                        for (Map.Entry pair : c.getAsJsonObject("selected").entrySet()) {
                            savedIds.put((String)pair.getKey(), ((JsonElement)pair.getValue()).getAsInt());
                        }
                    } else if (c.has("selectedHat") && !c.get("selectedHat").isJsonNull()) {
                        savedIds.put("hat", c.get("selectedHat").getAsInt());
                    }
                }
            }
            catch (Exception var15) {
                error = "\u041e\u0448\u0438\u0431\u043a\u0430 \u043a\u0430\u0442\u0430\u043b\u043e\u0433\u0430 \u0438\u043b\u0438 \u043d\u0430\u0441\u0442\u0440\u043e\u0435\u043a";
                MaksaiCosmetics.LOGGER.warn("Hat initialization failed", (Throwable)var15);
            }
        }
    }

    public static void tick() {
        if (LivePetState.tick()) {
            Map<String, Double> input = LivePetState.snapshot();
            for (Equipped item : HatManager.allEquipped()) {
                if (item.petMotion() == null || item.entry().category().equals("companion")) continue;
                item.petMotion().tick(input, 0.05);
            }
        }
        if (!restored) {
            restored = true;
            savedIds.keySet().removeIf(k -> Set.of("shields", "hand", "neckwear", "sword", "shovel", "wristwear", "belts", "bodywear").contains(k));
            for (Map.Entry<String, Integer> saved : new ArrayList<Map.Entry<String, Integer>>(savedIds.entrySet())) {
                Entry e = entries.stream().filter(x -> x.id() == ((Integer)saved.getValue()).intValue() && x.category().equals(saved.getKey())).findFirst().orElse(null);
                if (e != null) {
                    HatManager.select(e);
                    continue;
                }
                error = "\u0421\u043e\u0445\u0440\u0430\u043d\u0451\u043d\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d";
            }
        }
    }

    public static List<Entry> search(String category, String query) {
        String[] words = query.toLowerCase(Locale.ROOT).trim().split("\\s+");
        return entries.stream().filter(e -> e.category().equals(category)).filter(e -> {
            for (String w : words) {
                if (e.search().contains(w)) continue;
                return false;
            }
            return true;
        }).toList();
    }

    public static Equipped equipped(String category) {
        return active.get(category);
    }

    public static List<Equipped> allEquipped() {
        return List.copyOf(active.values());
    }

    public static List<String> categories() {
        return entries.stream().map(Entry::category).distinct().sorted().toList();
    }

    public static boolean hideHeadwearWithHelmet() {
        return hideHeadwearWithHelmet;
    }

    public static void toggleHelmetCompatibility() {
        hideHeadwearWithHelmet = !hideHeadwearWithHelmet;
        HatManager.save();
    }

    public static Map<String, Double> options(String category) {
        Equipped e = active.get(category);
        return e != null && e.petMotion() != null && e.petMotion().platformBoneCount() != 0 ? e.petMotion().optionValues() : Map.of();
    }

    public static void toggleOption(String category, String key) {
        Map<String, Double> values;
        Equipped e = active.get(category);
        if (e != null && e.petMotion() != null && (values = e.petMotion().optionValues()).containsKey(key)) {
            boolean value = values.get(key) == 0.0;
            e.petMotion().setBooleanOption(key, value);
            savedOptions.computeIfAbsent(e.entry().id(), k -> new HashMap()).put(key, value);
            HatManager.save();
        }
    }

    public static void resetOptions(String category) {
        Equipped e = active.get(category);
        if (e != null && e.petMotion() != null) {
            e.petMotion().resetOptions();
            savedOptions.remove(e.entry().id());
            HatManager.save();
        }
    }

    public static String error() {
        return error;
    }

    public static int size(String category) {
        return (int)entries.stream().filter(e -> e.category().equals(category)).count();
    }

    public static boolean selected(Entry e) {
        Equipped a = active.get(e.category());
        return a != null && a.entry().id() == e.id();
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static boolean select(Entry e) {
        class_2960 registered = null;
        try (InputStream s = HatManager.resource("hats/" + e.id() + "/mesh.json");
             InputStreamReader r = new InputStreamReader(s, StandardCharsets.UTF_8);){
            JsonObject model = JsonParser.parseReader((Reader)r).getAsJsonObject();
            JsonArray a = model.getAsJsonArray("vertices");
            if (!a.isEmpty() && a.size() % 4 == 0 && a.size() <= 80000) {
                JsonArray ids;
                float[] mesh = new float[a.size() * 8];
                int k = 0;
                for (JsonElement v : a) {
                    JsonArray row = v.getAsJsonArray();
                    if (row.size() != 8) {
                        throw new IOException("Invalid vertex");
                    }
                    for (JsonElement number : row) {
                        float f = number.getAsFloat();
                        if (!Float.isFinite(f)) {
                            throw new IOException("Nonfinite vertex");
                        }
                        mesh[k++] = f;
                    }
                }
                JsonArray sourceFrames = model.getAsJsonArray("frames");
                if (sourceFrames.size() > 400) {
                    throw new IOException("Too many animation frames");
                }
                float[][] frames = new float[sourceFrames.size()][];
                int[] boneIndices = new int[]{};
                if (frames.length > 0) {
                    JsonArray sourceIndices = model.getAsJsonArray("boneIndices");
                    if (sourceIndices.size() != a.size()) {
                        throw new IOException("Invalid bone indices");
                    }
                    boneIndices = new int[a.size()];
                    int width = sourceFrames.get(0).getAsJsonArray().size();
                    if (width == 0 || width % 12 != 0 || width > 12000 || (long)width * (long)frames.length > 2000000L) {
                        throw new IOException("Animation too large");
                    }
                    for (int j = 0; j < boneIndices.length; ++j) {
                        boneIndices[j] = sourceIndices.get(j).getAsInt();
                        if (boneIndices[j] >= 0 && boneIndices[j] < width / 12) continue;
                        throw new IOException("Invalid bone index");
                    }
                    for (int jx = 0; jx < frames.length; ++jx) {
                        JsonArray frame = sourceFrames.get(jx).getAsJsonArray();
                        if (frame.size() != width) {
                            throw new IOException("Invalid frame width");
                        }
                        frames[jx] = new float[width];
                        for (int v = 0; v < width; ++v) {
                            frames[jx][v] = frame.get(v).getAsFloat();
                            if (Float.isFinite(frames[jx][v])) continue;
                            throw new IOException("Invalid animation number");
                        }
                    }
                }
                if (boneIndices.length == 0 && model.has("boneIndices") && (ids = model.getAsJsonArray("boneIndices")).size() == a.size()) {
                    boneIndices = new int[ids.size()];
                    for (int j = 0; j < boneIndices.length; ++j) {
                        boneIndices[j] = ids.get(j).getAsInt();
                        if (boneIndices[j] >= 0 && boneIndices[j] <= 4095) continue;
                        throw new IOException("Invalid bone index");
                    }
                }
                WingAnimation motion = null;
                if (model.has("wingAnimation")) {
                    motion = WingAnimation.parse(model.getAsJsonObject("wingAnimation"));
                    JsonArray ids2 = model.getAsJsonArray("boneIndices");
                    if (ids2.size() != a.size()) {
                        throw new IOException("Wing indices");
                    }
                    boneIndices = new int[ids2.size()];
                    for (int jx = 0; jx < boneIndices.length; ++jx) {
                        boneIndices[jx] = ids2.get(jx).getAsInt();
                        if (boneIndices[jx] >= 0 && boneIndices[jx] < motion.boneCount()) continue;
                        throw new IOException("Wing index");
                    }
                }
                PetMotion petMotion = null;
                if (e.category().equals("pet") || model.has("animatedModel")) {
                    try (InputStream f = HatManager.resource("hats/" + e.id() + "/" + (e.category().equals("pet") ? "pet-motion.json" : "motion.json"));
                         InputStreamReader reader = new InputStreamReader(f, StandardCharsets.UTF_8);){
                        petMotion = PetMotionLoader.parse(JsonParser.parseReader((Reader)reader).getAsJsonObject());
                        JsonArray ids3 = model.getAsJsonArray("petBoneIndices");
                        if (ids3.size() != a.size()) {
                            throw new IOException("Pet indices");
                        }
                        int[] petIndices = new int[ids3.size()];
                        for (int jxx = 0; jxx < petIndices.length; ++jxx) {
                            petIndices[jxx] = ids3.get(jxx).getAsInt();
                            if (petIndices[jxx] >= 0 && petIndices[jxx] < petMotion.boneCount()) continue;
                            throw new IOException("Pet bone index");
                        }
                        for (Map.Entry value : savedOptions.getOrDefault(e.id(), Map.of()).entrySet()) {
                            if (!petMotion.optionValues().containsKey(value.getKey())) continue;
                            petMotion.setBooleanOption((String)value.getKey(), (Boolean)value.getValue());
                        }
                        petMotion.tick(LivePetState.snapshot(), 0.0);
                        boneIndices = petIndices;
                    }
                    catch (Exception var27) {
                        petMotion = null;
                        MaksaiCosmetics.LOGGER.warn("Pet controller unavailable, using previous animation: {}", (Object)e.id(), (Object)var27);
                    }
                }
                TextureUvAnimation uvAnimation = model.has("uvAnimation") ? TextureUvAnimation.parse(model.getAsJsonObject("uvAnimation")) : null;
                int fps = model.get("fps").getAsInt();
                if (frames.length <= 0 || fps >= 1 && fps <= 60) {
                    try (InputStream png = HatManager.resource("hats/" + e.id() + "/texture.png");){
                        int textureLimit;
                        class_1011 image = class_1011.method_4309((InputStream)png);
                        int n = textureLimit = !e.category().equals("bandanna") && uvAnimation == null ? 1024 : 4096;
                        if (image.method_4307() > textureLimit || image.method_4323() > textureLimit) {
                            image.close();
                            throw new IOException("Texture too large");
                        }
                        class_1043 texture = Gfx.register(e.texture(), image);
                        if (motion != null || petMotion != null || uvAnimation != null) {
                            texture.method_4524();
                        }
                        registered = e.texture();
                    }
                    if (e.attachment().equals("HEAD")) {
                        mesh = MaksaiCosmetics.adjustHeadMeshY(mesh);
                    }
                    Equipped old = active.put(e.category(), new Equipped(e, mesh, boneIndices, frames, fps, model.get("loop").getAsBoolean(), motion, petMotion, uvAnimation, model.has("bindings") ? PartBindings.parse(model, a.size(), e.category()) : null, System.nanoTime()));
                    error = "";
                    savedIds.put(e.category(), e.id());
                    if (old != null && old.entry().id() != e.id()) {
                        Gfx.release(old.entry().texture());
                    }
                    HatManager.save();
                    boolean bl = true;
                    return bl;
                }
                throw new IOException("Invalid frame rate");
            }
            throw new IOException("Invalid mesh size");
        }
        catch (Exception var30) {
            if (!(registered == null || active.get(e.category()) != null && registered.equals((Object)active.get(e.category()).entry().texture()))) {
                Gfx.release(registered);
            }
            error = "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c: " + e.name();
            MaksaiCosmetics.LOGGER.warn("Hat load failed: {}", (Object)e.id(), (Object)var30);
            return false;
        }
    }

    public static void clear(String category) {
        Equipped old = active.remove(category);
        savedIds.remove(category);
        error = "";
        if (old != null) {
            Gfx.release(old.entry().texture());
        }
        HatManager.save();
    }

    private static void save() {
        try {
            JsonObject o = new JsonObject();
            JsonObject selected = new JsonObject();
            for (Map.Entry<String, Integer> pair : savedIds.entrySet()) {
                selected.addProperty(pair.getKey(), (Number)pair.getValue());
            }
            o.add("selected", (JsonElement)selected);
            Path p = HatManager.configPath();
            Files.createDirectories(p.getParent(), new FileAttribute[0]);
            Path tmp = p.resolveSibling(String.valueOf(p.getFileName()) + ".tmp");
            o.addProperty("hideHeadwearWithHelmet", Boolean.valueOf(hideHeadwearWithHelmet));
            JsonObject options = new JsonObject();
            for (Map.Entry<Integer, Map<String, Boolean>> item : savedOptions.entrySet()) {
                JsonObject values = new JsonObject();
                item.getValue().forEach((arg_0, arg_1) -> ((JsonObject)values).addProperty(arg_0, arg_1));
                options.add(Integer.toString(item.getKey()), (JsonElement)values);
            }
            o.add("options", (JsonElement)options);
            Files.writeString(tmp, (CharSequence)o.toString(), StandardCharsets.UTF_8, new OpenOption[0]);
            try {
                Files.move(tmp, p, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (AtomicMoveNotSupportedException var8) {
                Files.move(tmp, p, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException var9) {
            error = "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0432\u044b\u0431\u043e\u0440";
            MaksaiCosmetics.LOGGER.warn("Hat config save failed", (Throwable)var9);
        }
    }

    public static void beginFrame() {
        previewLoads = 0;
    }

    public static class_2960 preview(Entry e) {
        class_2960 id = previews.get(e.id());
        if (id != null) {
            return id;
        }
        if (previewLoads < 3 && !failedPreviews.contains(e.id())) {
            ++previewLoads;
            try {
                class_2960 var17;
                try (InputStream s = HatManager.resource("hats/" + e.id() + "/preview.png");){
                    class_1011 im = class_1011.method_4309((InputStream)s);
                    TextureUvAnimation motion = null;
                    if (e.category().equals("bandanna") && e.animated()) {
                        try (InputStream meta = HatManager.resource("hats/" + e.id() + "/preview-motion.json");
                             InputStreamReader reader = new InputStreamReader(meta, StandardCharsets.UTF_8);){
                            motion = TextureUvAnimation.parse(JsonParser.parseReader((Reader)reader).getAsJsonObject());
                        }
                    }
                    if (im.method_4307() > 1024 || im.method_4323() > 1024 || im.method_4307() < 1 || im.method_4323() < 1 || motion == null && (im.method_4307() != 128 || im.method_4323() != 128)) {
                        im.close();
                        throw new IOException("Invalid preview");
                    }
                    previewSize.put(e.id(), new int[]{im.method_4307(), im.method_4323()});
                    if (motion != null) {
                        previewMotion.put(e.id(), motion);
                    }
                    Gfx.register(e.preview(), im).method_4524();
                    previews.put(e.id(), e.preview());
                    while (previews.size() > 64) {
                        Iterator<Map.Entry<Integer, class_2960>> it = previews.entrySet().iterator();
                        Map.Entry<Integer, class_2960> first = it.next();
                        it.remove();
                        previewSize.remove(first.getKey());
                        previewMotion.remove(first.getKey());
                        Gfx.release(first.getValue());
                    }
                    var17 = e.preview();
                }
                return var17;
            }
            catch (Exception var15) {
                failedPreviews.add(e.id());
                MaksaiCosmetics.LOGGER.warn("Hat preview missing: {}", (Object)e.id());
                return null;
            }
        }
        return null;
    }

    public static PreviewRegion previewRegion(Entry e) {
        TextureUvAnimation animation = previewMotion.get(e.id());
        int[] size = previewSize.get(e.id());
        if (animation != null && size != null) {
            TextureUvAnimation.Uv uv = animation.at((double)System.nanoTime() * 1.0E-9);
            return new PreviewRegion(uv.offsetU() * (float)size[0], uv.offsetV() * (float)size[1], Math.round(uv.scaleU() * (float)size[0]), Math.round(uv.scaleV() * (float)size[1]), size[0], size[1]);
        }
        return new PreviewRegion(0.0f, 0.0f, 128, 128, 128, 128);
    }

    public static void clearPreviews() {
        for (class_2960 id : previews.values()) {
            Gfx.release(id);
        }
        previews.clear();
        previewMotion.clear();
        previewSize.clear();
        failedPreviews.clear();
    }

    public static void closeAll() {
        HatManager.clearPreviews();
        for (Equipped a : active.values()) {
            Gfx.release(a.entry().texture());
        }
        active.clear();
    }

    static {
        savedIds = new LinkedHashMap<String, Integer>();
        error = "";
        savedOptions = new HashMap<Integer, Map<String, Boolean>>();
        hideHeadwearWithHelmet = true;
        previewMotion = new HashMap<Integer, TextureUvAnimation>();
        previewSize = new HashMap<Integer, int[]>();
        previews = new LinkedHashMap(16, 0.75f, true);
        failedPreviews = new HashSet<Integer>();
    }

    @Environment(value=EnvType.CLIENT)
    public record Entry(int id, String name, String search, String category, String attachment, boolean animated) {
        public class_2960 texture() {
            return class_2960.method_60655((String)"decide", (String)("cosmetics/hats/" + this.id + "/texture.png"));
        }

        public class_2960 preview() {
            return class_2960.method_60655((String)"decide", (String)("cosmetics/hats/" + this.id + "/preview.png"));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Equipped(Entry entry, float[] vertices, int[] boneIndices, float[][] frames, int fps, boolean loop, WingAnimation animation, PetMotion petMotion, TextureUvAnimation uvAnimation, PartBindings bindings, long started) {
    }

    @Environment(value=EnvType.CLIENT)
    public record PreviewRegion(float u, float v, int width, int height, int textureWidth, int textureHeight) {
    }
}

