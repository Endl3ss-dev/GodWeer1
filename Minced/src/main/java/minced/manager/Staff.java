/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 */
package minced.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import minced.core.StaffEntry;
import minced.core.ConfigFile;

public class Staff
extends ConfigFile {
    Object data;
    static String field6800;
    static String field6801 = "temp\\staff.file";
    static String field6802;
    static String field6803;

    @Override
    protected void resetData() {
        this.data = new ArrayList();
    }

    public void method1649(String param1) {
        if (param1 == null || param1.trim().isEmpty()) {
            return;
        }
        String v2 = param1.trim();
        if (!this.method1650(v2)) {
            ((List)this.data).add(new StaffEntry(v2));
            this.save();
        }
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject v1 = new JsonObject();
        JsonArray v2 = new JsonArray();
        for (StaffEntry v4 : (Iterable<StaffEntry>)((List)this.data)) {
            v2.add((JsonElement)new JsonPrimitive(v4.method1996()));
        }
        v1.add(field6800, (JsonElement)v2);
        return v1;
    }

    public boolean method1650(String param12) {
        if (param12 == null) {
            return false;
        }
        return ((List)this.data).stream().anyMatch(param1 -> ((StaffEntry)param1).method1996().equalsIgnoreCase(param12.trim()));
    }

    public void method1652(String param12) {
        if (param12 == null) {
            return;
        }
        boolean v2 = ((List)this.data).removeIf(param1 -> ((StaffEntry)param1).method1996().equalsIgnoreCase(param12.trim()));
        if (v2) {
            this.save();
        }
    }

    public Staff() {
        super(field6801);
    }

    @Override
    protected void handleLoadException(Exception param1) {
        if (this.data == null) {
            this.resetData();
        }
    }

    public void method1653() {
        if (!((List)this.data).isEmpty()) {
            ((List)this.data).clear();
            this.save();
        }
    }

    private void method1654(JsonObject param1, String param2, Consumer param3) {
        JsonElement v4 = param1.get(param2);
        if (v4 != null && !v4.isJsonNull()) {
            param3.accept(v4);
        }
    }

    public List method1655() {
        return new ArrayList((Collection)this.data);
    }

    @Override
    protected void readJson(JsonObject param12) {
        ((List)this.data).clear();
        JsonArray v2 = param12.getAsJsonArray(field6802);
        if (v2 != null) {
            for (JsonElement v4 : v2) {
                if (v4.isJsonPrimitive()) {
                    ((List)this.data).add(new StaffEntry(v4.getAsString()));
                    continue;
                }
                if (!v4.isJsonObject()) continue;
                JsonObject v5 = v4.getAsJsonObject();
                this.method1654(v5, field6803, param1 -> ((List)this.data).add(new StaffEntry(((JsonElement)param1).getAsString())));
            }
        }
    }

    static {
        field6800 = "staff";
        field6802 = "staff";
        field6803 = "name";
    }
}



