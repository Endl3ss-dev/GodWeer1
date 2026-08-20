/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import minced.core.ConfigFile;
import net.minecraft.util.Identifier;
import sg.ec.chI;
import sg.ec.chg;

public class BlockESP
extends ConfigFile {
    static String field6860 = "temp\\blockesp.file";
    static String field6861;
    static String IO;
    static String field6862;
    static String field6863;
    static String field6864;
    static String field6865;

    @SuppressWarnings("unchecked")
    private List<chg.ch> getList() {
        return (List<chg.ch>) this.getData();
    }

    public boolean method1711(String param12) {
        if (param12 == null) {
            return false;
        }
        Identifier v2 = chI.И(param12.trim());
        if (v2 == null) {
            return getList().removeIf(param1 -> ((String)param1.ф2()).equalsIgnoreCase(param12.trim()));
        }
        boolean v3 = getList().removeIf(param2 -> {
            Identifier v3_id = chI.И((String)param2.ф2());
            if (v3_id != null && v3_id.equals(v2)) {
                return true;
            }
            return ((String)param2.ф2()).equalsIgnoreCase(param12.trim());
        });
        if (v3) {
            this.save();
        }
        return v3;
    }

    public List<chg.ch> method1713() {
        return new ArrayList<>(getList());
    }

    public BlockESP() {
        super(field6860);
    }

    @Override
    protected void handleLoadException(Exception param1) {
        if (this.getData() == null) {
            this.resetData();
        }
    }

    public void method1714(String param12, int param2) {
        if (param12 == null) {
            return;
        }
        Identifier v3 = chI.И(param12);
        if (v3 == null) {
            return;
        }
        String v4 = v3.toString();
        getList().removeIf(param1 -> {
            Identifier v2 = chI.И((String)param1.ф2());
            return v2 != null && v2.equals(v3);
        });
        getList().add(new chg.ch(v4, param2));
        this.save();
    }

    @Override
    protected void readJson(JsonObject param1) {
        getList().clear();
        JsonArray v2 = param1.getAsJsonArray(field6861);
        if (v2 != null) {
            for (JsonElement v4 : v2) {
                if (!v4.isJsonObject()) continue;
                JsonObject v5 = v4.getAsJsonObject();
                this.method1720(v5, IO, param22 -> this.method1720(v5, field6862, param2 -> getList().add(new chg.ch(param22.getAsString(), param2.getAsInt()))));
            }
        }
        this.method1719();
    }

    public void method1717() {
        if (!getList().isEmpty()) {
            getList().clear();
            this.save();
        }
    }

    private void method1719() {
        boolean v2;
        LinkedHashMap<Identifier, Integer> v1 = new LinkedHashMap<>();
        for (chg.ch v3 : getList()) {
            Identifier identifier = chI.И((String)v3.ф2());
            if (identifier == null) continue;
            v1.put(identifier, ((Number)v3.фВ()).intValue());
        }
        boolean bl = v2 = v1.size() != getList().size();
        if (!v2) {
            for (chg.ch ч : getList()) {
                Identifier v5 = chI.И((String)ч.ф2());
                if (v5 != null && v5.toString().equals(ч.ф2())) continue;
                v2 = true;
                break;
            }
        }
        if (!v2) {
            return;
        }
        getList().clear();
        for (Map.Entry<Identifier, Integer> entry : v1.entrySet()) {
            getList().add(new chg.ch(entry.getKey().toString(), entry.getValue()));
        }
        this.save();
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject v1 = new JsonObject();
        JsonArray v2 = new JsonArray();
        for (chg.ch v4 : getList()) {
            JsonObject v5 = new JsonObject();
            v5.addProperty(field6863, (String)v4.ф2());
            v5.addProperty(field6864, (Number)v4.фВ());
            v2.add(v5);
        }
        v1.add(field6865, v2);
        return v1;
    }

    private void method1720(JsonObject param1, String param2, Consumer<JsonElement> param3) {
        JsonElement v4 = param1.get(param2);
        if (v4 != null && !v4.isJsonNull()) {
            param3.accept(v4);
        }
    }

    @Override
    protected void resetData() {
        this.setData(new ArrayList<chg.ch>());
    }

    static {
        field6863 = "block";
        field6864 = "color";
        field6865 = "blockesp";
        field6861 = "blockesp";
        IO = "block";
        field6862 = "color";
    }
}


