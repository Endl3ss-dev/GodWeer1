/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 *  net.minecraft.class_1044
 *  net.minecraft.class_156
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 */
package ru.decide.cosmetics;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_156;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import ru.decide.cosmetics.loader.CosmeticLoader;
import ru.decide.cosmetics.model.CosmeticModel;
import ru.decide.cosmetics.sync.PlayerCosmeticsSync;

@Environment(value=EnvType.CLIENT)
public final class CosmeticManager {
    private static final CosmeticManager INSTANCE = new CosmeticManager();
    private static final int MAX_SCAN_INDEX = 256;
    private static final int MAX_EQUIPPED = 5;
    public static final class_2960 DEFAULT_DECIDEVISUAL_CAPE = class_2960.method_60655((String)"decide", (String)"textures/cape.png");
    private final List<CosmeticEntry> entries = new ArrayList<CosmeticEntry>();
    private final Map<String, Integer> selectedByType = new LinkedHashMap<String, Integer>();
    private final Map<Integer, CosmeticModel> loadedModelCache = new ConcurrentHashMap<Integer, CosmeticModel>();
    private final Map<class_2960, CapeAnimationInfo> capeAnimationCache = new ConcurrentHashMap<class_2960, CapeAnimationInfo>();
    private boolean customCapeEnabled = true;
    private File configFile;
    private Runnable selectionListener;
    private boolean initialized;

    private CosmeticManager() {
    }

    public static CosmeticManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        this.scanAndLoadEntries();
        this.configFile = new File(class_310.method_1551().field_1697, "config/decide_cosmetics.json");
        this.loadConfig();
        this.initialized = true;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public File getCustomCapesDir() {
        File dir = new File(class_310.method_1551().field_1697, "decide/capes");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public void openCustomCapesFolder() {
        File dir = this.getCustomCapesDir();
        try {
            class_156.method_668().method_672(dir);
        }
        catch (Exception e) {
            System.err.println("[DecideVisual Cosmetics] Failed to open capes folder: " + e.getMessage());
        }
        this.reloadCustomCapes();
    }

    private void scanAndLoadEntries() {
        this.entries.clear();
        this.entries.add(new CosmeticEntry(999, "DecideVisual Cape", "cape", true));
        this.capeAnimationCache.put(DEFAULT_DECIDEVISUAL_CAPE, new CapeAnimationInfo(1, 1, false));
        for (int i = 0; i < 256; ++i) {
            if (i == 2 || i == 21 || i == 25 || i == 44 || i == 50) continue;
            String path = "/assets/decide/cosmetics/models/cosmetic_" + i + ".json";
            try (InputStream in = CosmeticManager.class.getResourceAsStream(path);){
                if (in == null) continue;
                String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                JsonObject obj = JsonParser.parseString((String)json).getAsJsonObject();
                Object rawName = obj.has("name") ? obj.get("name").getAsString() : "Cosmetic " + (i + 1);
                String rawType = obj.has("type") ? obj.get("type").getAsString() : "";
                int pos = obj.has("pos") ? obj.get("pos").getAsInt() : -1;
                String classified = CosmeticManager.classifyType(i, (String)rawName, rawType, pos);
                boolean isCape = "cape".equalsIgnoreCase(classified);
                CosmeticEntry entry = new CosmeticEntry(i, CosmeticManager.displayName((String)rawName, i), classified, isCape);
                this.entries.add(entry);
                if (!isCape) continue;
                if (obj.has("capeAnimation")) {
                    JsonObject anim = obj.getAsJsonObject("capeAnimation");
                    int frameCount = anim.has("frameCount") ? anim.get("frameCount").getAsInt() : 1;
                    int frameTime = anim.has("frameTime") ? anim.get("frameTime").getAsInt() : 2;
                    int frameW = anim.has("frameWidth") ? anim.get("frameWidth").getAsInt() : 64;
                    int frameH = anim.has("frameHeight") ? anim.get("frameHeight").getAsInt() : 32;
                    boolean isTight = frameW % 22 == 0 && frameH % 17 == 0;
                    this.capeAnimationCache.put(entry.getTexture(), new CapeAnimationInfo(frameCount, frameTime, isTight));
                    continue;
                }
                if (i == 11) {
                    this.capeAnimationCache.put(entry.getTexture(), new CapeAnimationInfo(11, 3, true));
                    continue;
                }
                if (i == 12) {
                    this.capeAnimationCache.put(entry.getTexture(), new CapeAnimationInfo(14, 1, true));
                    continue;
                }
                if (i == 13) {
                    this.capeAnimationCache.put(entry.getTexture(), new CapeAnimationInfo(5, 4, true));
                    continue;
                }
                this.capeAnimationCache.put(entry.getTexture(), new CapeAnimationInfo(1, 1, false));
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.loadCustomCapes();
        this.entries.sort(Comparator.comparingInt(e -> e.index));
        System.out.println("[DecideVisual Cosmetics] Loaded " + this.entries.size() + " cosmetic entries!");
    }

    public synchronized void loadCustomCapes() {
        File dir = this.getCustomCapesDir();
        File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
        if (files == null || files.length == 0) {
            return;
        }
        Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
        for (int i = 0; i < files.length; ++i) {
            File file = files[i];
            try {
                String baseName = file.getName().substring(0, file.getName().length() - 4);
                String cleanId = baseName.toLowerCase().replaceAll("[^a-z0-9_.-]", "_");
                class_2960 tex = class_2960.method_60655((String)"decide", (String)("custom_cape_" + cleanId));
                try (FileInputStream in = new FileInputStream(file);){
                    class_1011 image = class_1011.method_4309((InputStream)in);
                    int w = image.method_4307();
                    int h = image.method_4323();
                    boolean isTight = w % 22 == 0 && h % (w / 22 * 17) == 0;
                    int frameCount = 1;
                    if (isTight) {
                        frameH = w / 22 * 17;
                        frameCount = Math.max(1, h / frameH);
                    } else {
                        frameH = w / 2;
                        if (frameH > 0 && h % frameH == 0) {
                            frameCount = Math.max(1, h / frameH);
                        } else if (h > w) {
                            frameCount = Math.max(1, h / (w / 2));
                        }
                    }
                    int frameTime = 2;
                    File jsonFile = new File(dir, baseName + ".json");
                    if (jsonFile.exists()) {
                        try {
                            JsonObject obj = JsonParser.parseString((String)Files.readString(jsonFile.toPath())).getAsJsonObject();
                            if (obj.has("capeAnimation")) {
                                JsonObject anim = obj.getAsJsonObject("capeAnimation");
                                if (anim.has("frameCount")) {
                                    frameCount = anim.get("frameCount").getAsInt();
                                }
                                if (anim.has("frameTime")) {
                                    frameTime = anim.get("frameTime").getAsInt();
                                }
                            }
                        }
                        catch (Exception obj) {
                            // empty catch block
                        }
                    }
                    CapeAnimationInfo animInfo = new CapeAnimationInfo(frameCount, frameTime, isTight);
                    this.capeAnimationCache.put(tex, animInfo);
                    Runnable reg = () -> {
                        try {
                            class_1043 dynamicTex = new class_1043(() -> "decide_custom_cape", image);
                            class_310.method_1551().method_1531().method_4616(tex, (class_1044)dynamicTex);
                        }
                        catch (Exception ex) {
                            System.err.println("[DecideVisual Cosmetics] Failed to register custom cape texture: " + ex.getMessage());
                        }
                    };
                    if (class_310.method_1551().method_18854()) {
                        reg.run();
                    } else {
                        class_310.method_1551().execute(reg);
                    }
                }
                int customIndex = 10000 + i;
                String displayName = CosmeticManager.formatCustomName(baseName);
                CosmeticEntry entry = new CosmeticEntry(customIndex, displayName, "cape", true, tex, file.getName());
                this.entries.add(entry);
                continue;
            }
            catch (Exception e) {
                System.err.println("[DecideVisual Cosmetics] Error loading custom cape " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    public synchronized void reloadCustomCapes() {
        String equippedCustomFile = null;
        Integer currentCapeIdx = this.selectedByType.get("cape");
        if (currentCapeIdx != null) {
            for (CosmeticEntry e2 : this.entries) {
                if (e2.index != currentCapeIdx || e2.fileName == null) continue;
                equippedCustomFile = e2.fileName;
                break;
            }
        }
        this.entries.removeIf(e -> e.index >= 10000 && e.index < 100000);
        this.loadCustomCapes();
        if (equippedCustomFile != null) {
            for (CosmeticEntry e2 : this.entries) {
                if (!equippedCustomFile.equalsIgnoreCase(e2.fileName)) continue;
                this.selectedByType.put("cape", e2.index);
                break;
            }
        }
    }

    public CapeAnimationInfo getCapeAnimation(class_2960 texture) {
        if (texture == null) {
            return new CapeAnimationInfo(1, 1, false);
        }
        CapeAnimationInfo cached = this.capeAnimationCache.get(texture);
        if (cached != null) {
            return cached;
        }
        String path = texture.method_12832();
        cached = path.contains("cosmetic_11") ? new CapeAnimationInfo(11, 3, true) : (path.contains("cosmetic_12") ? new CapeAnimationInfo(14, 1, true) : (path.contains("cosmetic_13") ? new CapeAnimationInfo(5, 4, true) : new CapeAnimationInfo(1, 1, false)));
        this.capeAnimationCache.put(texture, cached);
        return cached;
    }

    private static String formatCustomName(String name) {
        if (name == null || name.isBlank()) {
            return "Custom Cape";
        }
        String clean = name.replace('_', ' ').replace('-', ' ').trim();
        StringBuilder sb = new StringBuilder();
        for (String part : clean.split("\\s+")) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() <= 1) continue;
            sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    public List<CosmeticEntry> getEntries() {
        return this.entries;
    }

    public List<CosmeticEntry> getFilteredEntries(String category) {
        if (category == null || category.isEmpty() || "all".equalsIgnoreCase(category)) {
            return this.entries;
        }
        ArrayList<CosmeticEntry> filtered = new ArrayList<CosmeticEntry>();
        for (CosmeticEntry e : this.entries) {
            if (!e.type.equalsIgnoreCase(category)) continue;
            filtered.add(e);
        }
        return filtered;
    }

    public List<CosmeticEntry> getEntriesByGroup(String group) {
        ArrayList<CosmeticEntry> filtered = new ArrayList<CosmeticEntry>();
        if (group == null || group.isEmpty()) {
            return filtered;
        }
        for (CosmeticEntry e : this.entries) {
            if (!group.equalsIgnoreCase(e.group)) continue;
            filtered.add(e);
        }
        return filtered;
    }

    public boolean isEquipped(CosmeticEntry entry) {
        if (entry == null) {
            return false;
        }
        Integer eq = this.selectedByType.get(entry.type);
        return eq != null && eq == entry.index;
    }

    public void toggle(CosmeticEntry entry) {
        if (entry == null) {
            return;
        }
        Integer old = this.selectedByType.get(entry.type);
        if (old != null && old == entry.index) {
            if (entry.isCape) {
                this.selectedByType.put("cape", 999);
            } else {
                this.selectedByType.remove(entry.type);
            }
        } else {
            Iterator<String> it;
            if (this.selectedByType.size() >= this.getMaxEquipped() && !this.selectedByType.containsKey(entry.type) && (it = this.selectedByType.keySet().iterator()).hasNext()) {
                it.next();
                it.remove();
            }
            this.selectedByType.put(entry.type, entry.index);
            if (!entry.isCape) {
                this.getModel(entry.index);
            }
        }
        this.saveConfig();
        PlayerCosmeticsSync.onCosmeticsChanged();
        this.notifySelectionChanged();
    }

    public void clearAllEquipped() {
        this.selectedByType.clear();
        this.selectedByType.put("cape", 999);
        this.saveConfig();
        PlayerCosmeticsSync.onCosmeticsChanged();
        this.notifySelectionChanged();
    }

    public Map<String, Integer> getSelectedByType() {
        return Collections.unmodifiableMap(this.selectedByType);
    }

    public void normalizeSelection() {
        boolean changed = false;
        LinkedHashMap<String, Integer> fixed = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> sel : this.selectedByType.entrySet()) {
            String resolved = sel.getKey();
            for (CosmeticEntry e : this.entries) {
                if (e.index != sel.getValue()) continue;
                resolved = e.type;
                break;
            }
            if (!resolved.equals(sel.getKey())) {
                changed = true;
            }
            fixed.put(resolved, sel.getValue());
        }
        if (!changed) {
            return;
        }
        this.selectedByType.clear();
        this.selectedByType.putAll(fixed);
        this.saveConfig();
        PlayerCosmeticsSync.onCosmeticsChanged();
    }

    public CosmeticModel getModel(int index) {
        if (index == 999) {
            return null;
        }
        return this.loadedModelCache.computeIfAbsent(index, k -> {
            String path = "/assets/decide/cosmetics/models/cosmetic_" + k + ".json";
            try (InputStream in = CosmeticManager.class.getResourceAsStream(path);){
                CosmeticModel cosmeticModel2;
                if (in == null) {
                    CosmeticModel cosmeticModel22;
                    CosmeticModel cosmeticModel3 = cosmeticModel22 = null;
                    return cosmeticModel3;
                }
                String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                class_2960 tex = class_2960.method_60655((String)"decide", (String)("textures/cosmetics/cosmetic_" + k + ".png"));
                CosmeticModel cosmeticModel = cosmeticModel2 = CosmeticLoader.getInstance().loadFromJson(json, tex, (int)k);
                return cosmeticModel;
            }
            catch (Exception e) {
                System.err.println("[DecideVisual Cosmetics] Error loading model " + k + ": " + e.getMessage());
                return null;
            }
        });
    }

    public CosmeticModel getModelForEntry(CosmeticEntry entry) {
        if (entry == null || entry.isCape) {
            return null;
        }
        return this.getModel(entry.index);
    }

    public List<CosmeticModel> getEquipped3DModels() {
        ArrayList<CosmeticModel> list = new ArrayList<CosmeticModel>();
        for (Map.Entry<String, Integer> entry : this.selectedByType.entrySet()) {
            CosmeticModel model;
            if ("cape".equalsIgnoreCase(entry.getKey()) || (model = this.getModel(entry.getValue())) == null) continue;
            list.add(model);
        }
        return list;
    }

    public class_2960 getActiveCapeTexture() {
        if (!this.customCapeEnabled) {
            return null;
        }
        Integer capeIndex = this.selectedByType.get("cape");
        if (capeIndex == null || capeIndex == 999) {
            return DEFAULT_DECIDEVISUAL_CAPE;
        }
        return this.getCapeTexture(capeIndex);
    }

    public class_2960 getCapeTexture(int index) {
        if (index == 999) {
            return DEFAULT_DECIDEVISUAL_CAPE;
        }
        for (CosmeticEntry e : this.entries) {
            if (e.index != index) continue;
            return e.getTexture();
        }
        return class_2960.method_60655((String)"decide", (String)("textures/cosmetics/cosmetic_" + index + ".png"));
    }

    public boolean isCustomCapeEnabled() {
        return this.customCapeEnabled;
    }

    public void setCustomCapeEnabled(boolean customCapeEnabled) {
        this.customCapeEnabled = customCapeEnabled;
        this.saveConfig();
        PlayerCosmeticsSync.onCosmeticsChanged();
        this.notifySelectionChanged();
    }

    public int getEquippedCount() {
        return this.selectedByType.size();
    }

    public int getMaxEquipped() {
        return 12;
    }

    private static String displayName(String rawName, int index) {
        Object name = rawName == null || rawName.isBlank() ? "Cosmetic " + (index + 1) : rawName;
        Object object = name;
        if (((String)name).startsWith("pulse_")) {
            name = ((String)name).substring("pulse_".length());
        }
        return ((String)name).replace('_', ' ').trim();
    }

    private static String classifyType(int index, String name, String rawType, int pos) {
        if (rawType != null && !rawType.isBlank()) {
            return rawType.trim().toLowerCase();
        }
        String lower = name == null ? "" : name.toLowerCase();
        String string = lower;
        if (lower.contains("cape")) {
            return "cape";
        }
        if (lower.contains("wing")) {
            return "wings";
        }
        if (lower.contains("pet") || lower.contains("bee") || lower.contains("radish")) {
            return "pet";
        }
        if (lower.contains("hat") || lower.contains("nimb") || pos == 2) {
            return "hat";
        }
        if (index <= 13) {
            return "cape";
        }
        if (index <= 26) {
            return "wings";
        }
        if (index <= 37) {
            return "accessories";
        }
        if (index <= 49) {
            return "pet";
        }
        return "accessories";
    }

    public void addEntry(CosmeticEntry entry) {
        if (entry == null) {
            return;
        }
        this.entries.add(entry);
    }

    public void registerCapeAnimation(class_2960 texture, CapeAnimationInfo info) {
        if (texture != null && info != null) {
            this.capeAnimationCache.put(texture, info);
        }
    }

    public void setSelectionListener(Runnable listener) {
        this.selectionListener = listener;
    }

    private void notifySelectionChanged() {
        if (this.selectionListener != null) {
            try {
                this.selectionListener.run();
            }
            catch (Exception e) {
                System.err.println("[DecideVisual Cosmetics] selection listener error: " + e.getMessage());
            }
        }
    }

    public JsonObject toConfigJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("customCape", Boolean.valueOf(this.customCapeEnabled));
        JsonObject eq = new JsonObject();
        for (Map.Entry<String, Integer> e : this.selectedByType.entrySet()) {
            eq.addProperty(e.getKey(), (Number)e.getValue());
        }
        obj.add("equipped", (JsonElement)eq);
        Integer capeIdx = this.selectedByType.get("cape");
        if (capeIdx != null && capeIdx >= 10000) {
            for (CosmeticEntry e : this.entries) {
                if (e.index != capeIdx || e.fileName == null) continue;
                obj.addProperty("customCapeFile", e.fileName);
                break;
            }
        }
        return obj;
    }

    public void applyConfigJson(JsonObject obj) {
        if (obj == null) {
            return;
        }
        try {
            if (obj.has("customCape")) {
                this.customCapeEnabled = obj.get("customCape").getAsBoolean();
            }
            LinkedHashMap<String, Integer> applied = new LinkedHashMap<String, Integer>();
            if (obj.has("equipped") && obj.get("equipped").isJsonObject()) {
                JsonObject eq = obj.getAsJsonObject("equipped");
                for (String key : eq.keySet()) {
                    applied.put(key, eq.get(key).getAsInt());
                }
            }
            if (obj.has("customCapeFile")) {
                String customFile = obj.get("customCapeFile").getAsString();
                for (CosmeticEntry e : this.entries) {
                    if (e.fileName == null || !customFile.equalsIgnoreCase(e.fileName)) continue;
                    applied.put("cape", e.index);
                    break;
                }
            }
            if (!applied.containsKey("cape")) {
                applied.put("cape", 999);
            }
            this.selectedByType.clear();
            this.selectedByType.putAll(applied);
            for (Map.Entry<String, Integer> e : this.selectedByType.entrySet()) {
                if ("cape".equalsIgnoreCase(e.getKey())) continue;
                this.getModel(e.getValue());
            }
            this.saveConfig();
            PlayerCosmeticsSync.onCosmeticsChanged();
            this.notifySelectionChanged();
        }
        catch (Exception e) {
            System.err.println("[DecideVisual Cosmetics] Failed to apply cosmetics config: " + e.getMessage());
        }
    }

    public void saveConfig() {
        try {
            if (this.configFile == null) {
                return;
            }
            JsonObject obj = this.toConfigJson();
            if (!this.configFile.getParentFile().exists()) {
                this.configFile.getParentFile().mkdirs();
            }
            Files.writeString(this.configFile.toPath(), (CharSequence)obj.toString(), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (Exception e) {
            System.err.println("[DecideVisual Cosmetics] Failed to save cosmetics config: " + e.getMessage());
        }
    }

    public void loadConfig() {
        try {
            if (this.configFile == null || !this.configFile.exists()) {
                this.selectedByType.put("cape", 999);
                return;
            }
            String content = Files.readString(this.configFile.toPath(), StandardCharsets.UTF_8);
            JsonObject obj = JsonParser.parseString((String)content).getAsJsonObject();
            if (obj.has("customCape")) {
                this.customCapeEnabled = obj.get("customCape").getAsBoolean();
            }
            this.selectedByType.clear();
            if (obj.has("equipped")) {
                JsonObject eq = obj.getAsJsonObject("equipped");
                for (String key : eq.keySet()) {
                    int val = eq.get(key).getAsInt();
                    this.selectedByType.put(key, val);
                    if ("cape".equalsIgnoreCase(key)) continue;
                    this.getModel(val);
                }
            }
            if (obj.has("customCapeFile")) {
                String customFile = obj.get("customCapeFile").getAsString();
                for (CosmeticEntry e : this.entries) {
                    if (e.fileName == null || !customFile.equalsIgnoreCase(e.fileName)) continue;
                    this.selectedByType.put("cape", e.index);
                    break;
                }
            }
            if (!this.selectedByType.containsKey("cape")) {
                this.selectedByType.put("cape", 999);
            }
        }
        catch (Exception e) {
            System.err.println("[DecideVisual Cosmetics] Failed to load cosmetics config: " + e.getMessage());
            this.selectedByType.put("cape", 999);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CosmeticEntry {
        public final int index;
        public final String name;
        public final String type;
        public final class_2960 texture;
        public final boolean isCape;
        public final String fileName;
        public String group = "";

        public CosmeticEntry(int index, String name, String type, boolean isCape) {
            this(index, name, type, isCape, index == 999 ? DEFAULT_DECIDEVISUAL_CAPE : class_2960.method_60655((String)"decide", (String)("textures/cosmetics/cosmetic_" + index + ".png")), null);
        }

        public CosmeticEntry(int index, String name, String type, boolean isCape, class_2960 texture, String fileName) {
            this.index = index;
            this.name = name;
            this.type = type;
            this.isCape = isCape;
            this.texture = texture;
            this.fileName = fileName;
        }

        public class_2960 getTexture() {
            return this.texture;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CapeAnimationInfo {
        public final int frameCount;
        public final int frameTime;
        public final boolean isTight;
        public final float baseWidth;
        public final float baseHeight;
        public final boolean atlasFrame;

        public CapeAnimationInfo(int frameCount, int frameTime, boolean isTight) {
            this(frameCount, frameTime, isTight, isTight ? 22.0f : 64.0f, isTight ? 17.0f : 32.0f, false);
        }

        public CapeAnimationInfo(int frameCount, int frameTime, boolean isTight, float baseWidth, float baseHeight, boolean atlasFrame) {
            this.frameCount = Math.max(1, frameCount);
            this.frameTime = Math.max(1, frameTime);
            this.isTight = isTight;
            float f = baseWidth > 0.0f ? baseWidth : (this.baseWidth = isTight ? 22.0f : 64.0f);
            this.baseHeight = baseHeight > 0.0f ? baseHeight : (isTight ? 17.0f : 32.0f);
            this.atlasFrame = atlasFrame;
        }

        public int getCurrentFrame() {
            if (this.frameCount <= 1) {
                return 0;
            }
            long durationMs = (long)this.frameTime * 50L;
            return (int)(System.currentTimeMillis() / durationMs % (long)this.frameCount);
        }

        public float getU(float x) {
            return x / this.baseWidth;
        }

        public float getV(float y, int frame) {
            return ((float)frame + y / this.baseHeight) / (float)this.frameCount;
        }
    }
}

