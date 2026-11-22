/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2248
 *  net.minecraft.class_2960
 *  net.minecraft.class_7923
 */
package ru.decide.module.api.settings.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.Setting;

@Environment(value=EnvType.CLIENT)
public class BlockListSetting
extends Setting<List<String>> {
    public String input = "";
    private static List<String> ALL_IDS;
    private static String[] SEARCH_KEYS;
    private static final Map<String, String> DISPLAY;
    private static final Map<String, Integer> SWATCH;

    public BlockListSetting(Module parent, String name) {
        super(parent, name, new ArrayList());
    }

    public List<String> getIds() {
        return (List)this.getValue();
    }

    public boolean addId(String raw) {
        String id = BlockListSetting.normalize(raw);
        if (id == null || ((List)this.getValue()).contains(id)) {
            return false;
        }
        ((List)this.getValue()).add(id);
        return true;
    }

    public boolean addCurrentInput() {
        if (this.addId(this.input)) {
            this.input = "";
            return true;
        }
        return false;
    }

    public void remove(String id) {
        ((List)this.getValue()).remove(id);
    }

    public Set<class_2248> resolveBlocks() {
        HashSet<class_2248> blocks = new HashSet<class_2248>();
        for (String id : (List)this.getValue()) {
            class_2960 ident = class_2960.method_12829((String)id);
            if (ident == null) continue;
            class_7923.field_41175.method_17966(ident).ifPresent(blocks::add);
        }
        return blocks;
    }

    public static String normalize(String raw) {
        class_2960 id;
        if (raw == null) {
            return null;
        }
        Object s = raw.trim().toLowerCase();
        if (((String)s).isEmpty()) {
            return null;
        }
        if (!((String)s).contains(":")) {
            s = "minecraft:" + (String)s;
        }
        if ((id = class_2960.method_12829((String)s)) == null || !class_7923.field_41175.method_10250(id)) {
            return null;
        }
        return s;
    }

    public static String shortName(String id) {
        int i = id.indexOf(58);
        return i >= 0 ? id.substring(i + 1) : id;
    }

    private static void buildCache() {
        ArrayList<String> ids = new ArrayList<String>();
        try {
            for (class_2960 id : class_7923.field_41175.method_10235()) {
                ids.add(id.toString());
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        ids.sort(String::compareTo);
        String[] keys = new String[ids.size()];
        for (int i = 0; i < ids.size(); ++i) {
            int sw;
            String name;
            String id = (String)ids.get(i);
            try {
                name = BlockListSetting.computeDisplayName(id);
            }
            catch (Exception e) {
                name = BlockListSetting.shortName(id);
            }
            try {
                sw = BlockListSetting.computeSwatch(id);
            }
            catch (Exception e) {
                sw = -11184811;
            }
            DISPLAY.put(id, name);
            SWATCH.put(id, sw);
            keys[i] = (BlockListSetting.shortName(id) + " " + name + " " + id).toLowerCase();
        }
        SEARCH_KEYS = keys;
        ALL_IDS = ids;
    }

    public static List<String> allIds() {
        if (ALL_IDS == null) {
            BlockListSetting.buildCache();
        }
        return ALL_IDS;
    }

    public static List<String> search(String query) {
        String q;
        if (ALL_IDS == null) {
            BlockListSetting.buildCache();
        }
        String string = q = query == null ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) {
            return ALL_IDS;
        }
        ArrayList<String> out = new ArrayList<String>();
        for (int i = 0; i < ALL_IDS.size(); ++i) {
            if (!SEARCH_KEYS[i].contains(q)) continue;
            out.add(ALL_IDS.get(i));
        }
        return out;
    }

    public static String displayName(String id) {
        return DISPLAY.computeIfAbsent(id, BlockListSetting::computeDisplayName);
    }

    public static int swatchColor(String id) {
        return SWATCH.computeIfAbsent(id, BlockListSetting::computeSwatch);
    }

    private static String computeDisplayName(String id) {
        Optional block;
        class_2960 ident = class_2960.method_12829((String)id);
        if (ident != null && (block = class_7923.field_41175.method_17966(ident)).isPresent()) {
            return ((class_2248)block.get()).method_9518().getString();
        }
        return BlockListSetting.shortName(id);
    }

    private static int computeSwatch(String id) {
        int c;
        Optional block;
        class_2960 ident = class_2960.method_12829((String)id);
        if (ident != null && (block = class_7923.field_41175.method_17966(ident)).isPresent() && (c = ((class_2248)block.get()).method_26403().field_16011) != 0) {
            return 0xFF000000 | c & 0xFFFFFF;
        }
        return -11184811;
    }

    public BlockListSetting setVisible(Supplier<Boolean> value) {
        return (BlockListSetting)super.setVisible(value);
    }

    static {
        DISPLAY = new HashMap<String, String>();
        SWATCH = new HashMap<String, Integer>();
    }
}

