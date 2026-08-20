/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  sg.ec.Ср$Ч
 */
package minced.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import minced.core.ConfigFile;
import sg.ec.sr;

public class Macro
extends ConfigFile
{
    Object data;
    static String field6851 = "temp\\macros.file";
    static String field6852;
    static String field6853;
    static String field6854;
    static String field6855;
    static String field6856;
    static String field6857;
    static String field6858;
    static String field6859;

    @SuppressWarnings("unchecked")
    public List<sr.ch> method1708() {
        return new ArrayList<>((Collection<sr.ch>)this.data);
    }

    public Macro() {
        super(field6851);
    }

    public void method1709(String param12) {
        if (param12 == null) {
            return;
        }
        boolean v2 = ((List)this.data).removeIf(param1 -> ((String)((sr.ch)param1).ДЧ()).equalsIgnoreCase(param12.trim()));
        if (v2) {
            this.save();
        }
    }

    @Override
    protected void resetData() {
        this.data = new ArrayList();
    }

    private void cfr_renamed_0(JsonObject param1, String param2, Consumer param3) {
        JsonElement v4 = param1.get(param2);
        if (v4 != null && !v4.isJsonNull()) {
            param3.accept(v4);
        }
    }

    public void method1710() {
        if (!((List)this.data).isEmpty()) {
            ((List)this.data).clear();
            this.save();
        }
    }

    @Override
    protected void handleLoadException(Exception param1) {
        if (this.data == null) {
            this.resetData();
        }
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject v1 = new JsonObject();
        JsonArray v2 = new JsonArray();
        for (sr.ch v4 : (Iterable<sr.ch>)((List)this.data)) {
            JsonObject v5 = new JsonObject();
            v5.addProperty(field6854, (String)v4.ДЧ());
            v5.addProperty(field6855, (Number)v4.l());
            v5.addProperty(field6856, v4.Д5());
            v2.add((JsonElement)v5);
        }
        v1.add(field6857, (JsonElement)v2);
        return v1;
    }

    public void cfr_renamed_0(String param1, int param2, String param3) {
        if (param1 == null || param3 == null) {
            return;
        }
        this.method1709(param1);
        ((List)this.data).add(new sr.ch(param1.trim(), param2, param3));
        this.save();
    }

    @Override
    protected void readJson(JsonObject param1) {
        ((List)this.data).clear();
        JsonArray v2 = param1.getAsJsonArray(field6858);
        if (v2 != null) {
            for (JsonElement v4 : v2) {
                if (!v4.isJsonObject()) continue;
                JsonObject v5 = v4.getAsJsonObject();
                this.cfr_renamed_0(v5, field6859, param2 -> this.cfr_renamed_0(v5, field6853, param32 -> this.cfr_renamed_0(v5, field6852, param3 -> ((List)this.data).add(new sr.ch(((JsonElement)param2).getAsString(), ((JsonElement)param32).getAsInt(), ((JsonElement)param3).getAsString())))));
            }
        }
    }

    static {
        field6854 = "name";
        field6855 = "key";
        field6856 = "cmd";
        field6857 = "macros";
        field6858 = "macros";
        field6859 = "name";
        field6853 = "key";
        field6852 = "cmd";
    }
}



