/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  sg.ec.2у$Ч
 */
package minced.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import minced.core.ConfigFile;
import sg.ec._2u;

public class Waypoint
extends ConfigFile
{
    Object data;
    static String field6866;
    static String field6867 = "temp\\waypoints.file";
    static String field6868;
    static String field6869;
    static String field6870;
    static String field6871;
    static String field6872;
    static String field6873;
    static String field6874;

    public boolean method1723(String param12, String param2) {
        if (param12 == null || param2 == null) {
            return false;
        }
        List v3 = (List)((Map)this.data).get(param12);
        if (v3 == null) {
            return false;
        }
        boolean v4 = v3.removeIf(param1 -> ((String)((_2u.ch)param1).弟()).equalsIgnoreCase(param2.trim()));
        if (v4) {
            this.save();
        }
        return v4;
    }

    public void method1727(String param12, String param2, double param3, double param5, double param7) {
        if (param12 == null || param2 == null) {
            return;
        }
        String v9 = param2.trim();
        if (v9.isEmpty()) {
            return;
        }
        List v10 = (List)(((Map)this.data).computeIfAbsent(param12, param0 -> new ArrayList()));
        v10.removeIf(param1 -> ((String)((_2u.ch)param1).弟()).equalsIgnoreCase(v9));
        v10.add(new _2u.ch(v9, param3, param5, param7));
        this.save();
    }

    public Waypoint() {
        super(field6867);
    }

    public void method1728(String param1) {
        if (param1 == null) {
            return;
        }
        List v2 = (List)((Map)this.data).get(param1);
        if (v2 != null && !v2.isEmpty()) {
            v2.clear();
            this.save();
        }
    }

    @Override
    protected void handleLoadException(Exception param1) {
        if (this.data == null) {
            this.resetData();
        }
    }

    public List method1729(String param1) {
        return new ArrayList((java.util.Collection)((Map)this.data).getOrDefault(param1, Collections.emptyList()));
    }

    private void method1733(JsonObject param1, String param2, Consumer param3) {
        JsonElement v4 = param1.get(param2);
        if (v4 != null && !v4.isJsonNull()) {
            param3.accept(v4);
        }
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject v1 = new JsonObject();
        for (Map.Entry v3 : (Iterable<java.util.Map.Entry>)(((Map)this.data).entrySet())) {
            String v4 = (String)v3.getKey();
            JsonArray v5 = new JsonArray();
            for (_2u.ch v7 : (Iterable<_2u.ch>)((List)v3.getValue())) {
                JsonObject v8 = new JsonObject();
                v8.addProperty(field6870, (String)v7.弟());
                v8.addProperty(field6871, (Number)v7.Б());
                v8.addProperty(field6872, (Number)v7.f13());
                v8.addProperty(field6873, (Number)v7.С());
                v5.add((JsonElement)v8);
            }
            v1.add(v4, (JsonElement)v5);
        }
        return v1;
    }

    @Override
    protected void resetData() {
        this.data = new HashMap();
    }

    @Override
    protected void readJson(JsonObject param1) {
        ((Map)this.data).clear();
        for (Map.Entry v3 : param1.entrySet()) {
            String v4 = (String)v3.getKey();
            JsonElement v5 = (JsonElement)v3.getValue();
            if (!v5.isJsonArray()) continue;
            JsonArray v6 = v5.getAsJsonArray();
            ArrayList v7 = new ArrayList();
            for (JsonElement v9 : v6) {
                if (!v9.isJsonObject()) continue;
                JsonObject v10 = v9.getAsJsonObject();
                this.method1733(v10, field6874, param3 -> this.method1733(v10, field6868, param4 -> this.method1733(v10, field6866, param5 -> this.method1733(v10, field6869, param6 -> v7.add(new _2u.ch(((JsonElement)param3).getAsString(), ((JsonElement)param4).getAsDouble(), ((JsonElement)param5).getAsDouble(), ((JsonElement)param6).getAsDouble()))))));
            }
            ((Map)this.data).put(v4, v7);
        }
    }

    static {
        field6870 = "name";
        field6871 = "x";
        field6872 = "y";
        field6873 = "z";
        field6874 = "name";
        field6868 = "x";
        field6866 = "y";
        field6869 = "z";
    }
}



