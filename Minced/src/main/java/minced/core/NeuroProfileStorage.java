/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterable
 *  sLM.4ZXYEo
 */
package minced.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterable;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Collection;
import minced.core.PathUtil;
import minced.core.NeuroSampler;
import sLM._4ZXYEo;

public final class NeuroProfileStorage {
    static String cfr_renamed_178;

    static File field8435;
    static Gson field8436;
    static NeuroSampler field8437;

    public static void method2287() {
        File v0 = field8435.getParentFile();
        if (v0 != null) {
            v0.mkdirs();
        }
        NeuroProfileStorage.method2292().method2283();
        try {
            Files.writeString(field8435.toPath(), (CharSequence)field8436.toJson((Object)NeuroProfileStorage.method2292()), new OpenOption[0]);
        }
        catch (java.io.IOException v1) {
            // Called from the periodic autosave tick; a failed write must not tear down the client.
        }
    }

    public static void method2288() {
    }

    public static void method2289() {
        field8436 = new GsonBuilder().setPrettyPrinting().create();
        field8435 = new File((String)((Object)_4ZXYEo.Че("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558)));
        field8437 = NeuroProfileStorage.method2291();
    }

    private NeuroProfileStorage() {
    }

    public static void method2290() {
        field8437 = new NeuroSampler();
        NeuroProfileStorage.method2287();
    }

    private static NeuroSampler method2291() {
        if (!field8435.exists()) {
            return new NeuroSampler();
        }
        String v0;
        try {
            v0 = Files.readString(field8435.toPath());
        }
        catch (java.io.IOException v2) {
            // Unreadable config falls back to defaults, same as a missing file.
            return new NeuroSampler();
        }
        NeuroSampler v1 = (NeuroSampler)field8436.fromJson(v0, NeuroSampler.class);
        if (v1 == null) {
            v1 = new NeuroSampler();
        }
        v1.method2283();
        return v1;
    }

    public static NeuroSampler method2292() {
        if (field8437 == null) {
            field8437 = new NeuroSampler();
        }
        field8437.method2281();
        return field8437;
    }

    static {
        cfr_renamed_178 = "\u0001temp\\aura_neuro.file";
        NeuroProfileStorage.method2289();
    }
}



