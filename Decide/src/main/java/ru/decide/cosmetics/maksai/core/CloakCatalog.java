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
package ru.decide.cosmetics.maksai.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.maksai.core.CloakEntry;

@Environment(value=EnvType.CLIENT)
public final class CloakCatalog {
    public static final String RESOURCE = "/assets/decide/cosmetics/catalog/cloaks.json";
    private final List<CloakEntry> supported;
    private final Map<Integer, CloakEntry> byId;
    private final int unsupportedCount;
    private final String error;

    private CloakCatalog(List<CloakEntry> supported, int unsupportedCount, String error) {
        this.supported = Collections.unmodifiableList(supported);
        HashMap<Integer, CloakEntry> m = new HashMap<Integer, CloakEntry>();
        for (CloakEntry e : supported) {
            m.putIfAbsent(e.id, e);
        }
        this.byId = m;
        this.unsupportedCount = unsupportedCount;
        this.error = error;
    }

    public static CloakCatalog empty(String error) {
        return new CloakCatalog(List.of(), 0, error);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static CloakCatalog loadFromResource() {
        try (InputStream in = CloakCatalog.class.getResourceAsStream(RESOURCE);){
            if (in == null) {
                CloakCatalog cloakCatalog = CloakCatalog.empty("catalog_missing");
                return cloakCatalog;
            }
            CloakCatalog var1 = CloakCatalog.parse(in);
            return var1;
        }
        catch (IOException | RuntimeException var5) {
            return CloakCatalog.empty("catalog_unreadable: " + var5.getClass().getSimpleName());
        }
    }

    public static CloakCatalog parse(InputStream in) {
        try {
            CloakCatalog var16;
            try (InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8);){
                JsonObject root = JsonParser.parseReader((Reader)r).getAsJsonObject();
                JsonArray arr = root.getAsJsonArray("cloaks");
                ArrayList<CloakEntry> ok = new ArrayList<CloakEntry>();
                int bad = 0;
                for (JsonElement el : arr) {
                    try {
                        JsonObject o = el.getAsJsonObject();
                        if (!"ok".equals(CloakCatalog.str(o, "status"))) {
                            ++bad;
                            continue;
                        }
                        int k = o.get("k").getAsInt();
                        int frames = o.get("frames").getAsInt();
                        int ft = o.get("frametime").getAsInt();
                        if (k >= 1 && k <= 64 && frames >= 1 && frames <= 4096) {
                            ok.add(new CloakEntry(o.get("id").getAsInt(), CloakCatalog.str(o, "name"), CloakCatalog.strList(o, "colors"), CloakCatalog.strList(o, "tags"), k, frames, ft));
                            continue;
                        }
                        ++bad;
                    }
                    catch (RuntimeException var13) {
                        ++bad;
                    }
                }
                var16 = new CloakCatalog(ok, bad, null);
            }
            return var16;
        }
        catch (IOException | RuntimeException var15) {
            return CloakCatalog.empty("catalog_corrupt: " + var15.getClass().getSimpleName());
        }
    }

    private static String str(JsonObject o, String k) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : null;
    }

    private static List<String> strList(JsonObject o, String k) {
        ArrayList<String> out = new ArrayList<String>();
        if (o.has(k) && o.get(k).isJsonArray()) {
            for (JsonElement e : o.getAsJsonArray(k)) {
                out.add(e.getAsString());
            }
        }
        return out;
    }

    public List<CloakEntry> all() {
        return this.supported;
    }

    public CloakEntry byId(int id) {
        return this.byId.get(id);
    }

    public int unsupportedCount() {
        return this.unsupportedCount;
    }

    public String error() {
        return this.error;
    }

    public List<CloakEntry> search(String query) {
        if (query != null && !query.isBlank()) {
            ArrayList<CloakEntry> out = new ArrayList<CloakEntry>();
            for (CloakEntry e : this.supported) {
                if (!e.matches(query)) continue;
                out.add(e);
            }
            return out;
        }
        return this.supported;
    }
}

