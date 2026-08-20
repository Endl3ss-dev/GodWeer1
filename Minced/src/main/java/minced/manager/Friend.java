/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package minced.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import minced.core.FriendEntry;
import minced.core.ConfigFile;

public class Friend
extends ConfigFile {
    static String cfr_renamed_348;

    Object data;
    static String field6842;
    static String field6843;
    static String field6844 = "temp\\friends.file";
    static String field6845;
    static String field6846;
    static String field6847;
    static String field6848;
    static String field6849;
    static String field6850;

    public void method1699() {
        if (!((List)this.data).isEmpty()) {
            ((List)this.data).clear();
            this.save();
        }
    }

    public boolean cfr_renamed_4(String param12) {
        if (param12 == null) {
            return false;
        }
        return ((List)this.data).stream().anyMatch(param1 -> ((FriendEntry)param1).method2633().equalsIgnoreCase(param12.trim()));
    }

    @Override
    protected void resetData() {
        this.data = new ArrayList();
    }

    public void method1703(String param12) {
        if (param12 == null) {
            System.err.println(field6843);
            return;
        }
        boolean v2 = ((List)this.data).removeIf(param1 -> ((FriendEntry)param1).method2633().equalsIgnoreCase(param12.trim()));
        if (v2) {
            this.save();
        }
    }

    @Override
    protected void handleLoadException(Exception param1) {
        if (this.data == null) {
            this.resetData();
        }
    }

    private void method1704(JsonObject param1, String param2, Consumer param3) {
        JsonElement v4 = param1.get(param2);
        if (v4 != null && !v4.isJsonNull()) {
            param3.accept(v4);
        }
    }

    public List method1705() {
        return new ArrayList((Collection)this.data);
    }

    public Friend() {
        super(field6844);
    }

    public void method1707(String param1) {
        if (param1 == null || param1.trim().isEmpty()) {
            System.err.println(field6845);
            return;
        }
        String v2 = param1.trim();
        if (!this.cfr_renamed_4(v2)) {
            ((List)this.data).add(new FriendEntry(v2, LocalDateTime.now()));
            this.save();
        }
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject v1 = new JsonObject();
        JsonArray v2 = new JsonArray();
        for (FriendEntry v4 : (Iterable<FriendEntry>)((List)this.data)) {
            JsonObject v5 = new JsonObject();
            v5.addProperty(field6846, v4.method2633());
            v5.addProperty(field6847, v4.cfr_underscore().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            v2.add((JsonElement)v5);
        }
        v1.add(field6848, (JsonElement)v2);
        return v1;
    }

    @Override
    protected void readJson(JsonObject param1) {
        ((List)this.data).clear();
        JsonArray v2 = param1.getAsJsonArray(field6849);
        if (v2 != null) {
            for (JsonElement v4 : v2) {
                if (!v4.isJsonObject()) continue;
                JsonObject v5 = v4.getAsJsonObject();
                this.method1704(v5, field6850, param22 -> this.method1704(v5, field6842, param2 -> {
                    String v3 = ((JsonElement)param22).getAsString();
                    String v4_126 = ((JsonElement)param2).getAsString();
                    LocalDateTime v5_127 = LocalDateTime.parse(v4_126, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    ((List)this.data).add(new FriendEntry(v3, v5_127));
                }));
            }
        }
    }

    static {
        field6846 = "name";
        field6847 = "addedTime";
        field6848 = "friends";
        field6849 = "friends";
        field6850 = "name";
        field6845 = "Cannot add friend: name is null or empty";
        field6843 = "Cannot remove friend: name is null";
        field6842 = "addedTime";
        cfr_renamed_348 = "Failed to parse friend entry: \u0001";
    }
}



