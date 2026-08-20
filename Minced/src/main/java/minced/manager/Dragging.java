/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package minced.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import minced.core.DraggingState;
import minced.core.BooleanSetting;
import minced.core.CryptoUtil;
import minced.core.Setting;
import minced.core.ModeSetting;
import minced.core.HudWindow;
import minced.core.ConfigFile;

public class Dragging
extends ConfigFile {
    static String cfr_renamed_347;
    static String ЧЫ;
    static String ch8;
    static String ЧЬ;
    static String Чщ;
    static String Чб;
    static String Чо;
    static String ЖШ;

    File file;
    boolean encrypted;
    Object data;
    static String field6804;
    static String field6805;
    static String field6806;
    static String field6807;
    static String field6808;
    static String field6809;
    static String field6810;
    static String field6811;
    static String field6812;
    static String field6813;
    static String field6814;
    static String field6815;
    static String field6816;

    public void method1657(JsonObject param1) {
        if (param1 == null) {
            return;
        }
        this.readJson(param1);
        this.save();
    }

    @Override
    protected void readJson(JsonObject param1) {
        this.method1671(param1);
        this.method1687(param1);
        this.method1663(param1);
    }

    public void method1661(HudWindow param1) {
        ((DraggingState)this.data).method101().remove(param1);
        ((DraggingState)this.data).method101().add(param1);
    }

    public List method1662() {
        return ((DraggingState)this.data).method101();
    }

    private void method1663(JsonObject param1) {
        this.method1688(param1, field6807, param12 -> {
            JsonObject v2 = ((JsonElement)param12).getAsJsonObject();
            v2.entrySet().forEach(param1_84 -> {
                HudWindow v2_85 = (HudWindow)((DraggingState)this.data).method102().get(param1_84.getKey());
                if (v2_85 != null) {
                    JsonObject v3 = ((JsonElement)param1_84.getValue()).getAsJsonObject();
                    v2_85.method3466().forEach(param2 -> this.method1676(v3, (Setting)param2));
                }
            });
        });
    }

    public void method1664(HudWindow param1) {
        JsonObject v3;
        JsonObject v4;
        JsonObject v2 = this.method1670();
        if (v2 != null && v2.has(field6811) && (v4 = (v3 = v2.getAsJsonObject(field6812)).getAsJsonObject(param1.method3491())) != null) {
            param1.method3466().forEach(param2 -> this.method1676(v4, (Setting)param2));
        }
    }

    public Dragging() {
        super(field6813);
    }

    public void method1666(HudWindow param1) {
        String v2 = param1.method3491();
        if (((DraggingState)this.data).method100().containsKey(v2) && ((DraggingState)this.data).method99().containsKey(v2)) {
            param1.O(((Float)((DraggingState)this.data).method100().remove(v2)).floatValue());
            param1.method3471(((Float)((DraggingState)this.data).method99().remove(v2)).floatValue());
        }
        ((DraggingState)this.data).method102().put(v2, param1);
        if (!((DraggingState)this.data).method101().contains(param1)) {
            ((DraggingState)this.data).method101().add(param1);
        }
    }

    private JsonObject method1670() {
        if (!this.file.exists()) {
            return null;
        }
        String v1;
        try {
            v1 = Files.readString(this.file.toPath());
        }
        catch (java.io.IOException v2) {
            // Unreadable file is treated the same as a missing one.
            return null;
        }
        return new JsonParser().parse(this.encrypted ? CryptoUtil.method1964(v1) : v1).getAsJsonObject();
    }

    private void method1671(JsonObject param1) {
        this.method1688(param1, field6808, param12 -> {
            ((DraggingState)this.data).method100().clear();
            ((DraggingState)this.data).method99().clear();
            JsonObject v2 = ((JsonElement)param12).getAsJsonObject();
            v2.entrySet().forEach(param1_132 -> {
                String v2_133 = (String)param1_132.getKey();
                JsonObject v3 = ((JsonElement)param1_132.getValue()).getAsJsonObject();
                float v4 = v3.get(field6809).getAsFloat();
                float v5 = v3.get(field6810).getAsFloat();
                ((DraggingState)this.data).method100().put(v2_133, Float.valueOf(v4));
                ((DraggingState)this.data).method99().put(v2_133, Float.valueOf(v5));
                HudWindow v6 = (HudWindow)((DraggingState)this.data).method102().get(v2_133);
                if (v6 != null) {
                    v6.O(v4);
                    v6.method3471(v5);
                }
            });
        });
    }

    private void method1676(JsonObject param12, Setting param2) {
        this.method1688(param12, param2.getName(), param1 -> {
            if (param2 instanceof BooleanSetting) {
                ((BooleanSetting)param2).setValue(((JsonElement)param1).getAsBoolean());
            } else if (param2 instanceof ModeSetting) {
                ((ModeSetting)param2).setValue(((JsonElement)param1).getAsString());
            }
        });
    }

    private void method1679(JsonObject param1, Setting param2) {
        if (param2 instanceof BooleanSetting) {
            param1.addProperty(param2.getName(), (Boolean)((BooleanSetting)param2).getValue());
        } else if (param2 instanceof ModeSetting) {
            param1.addProperty(param2.getName(), (String)((ModeSetting)param2).getValue());
        }
    }

    public void method1680() {
        ((DraggingState)this.data).method102().values().forEach(param0 -> {
            ((HudWindow)param0).O(((HudWindow)param0).method3472());
            ((HudWindow)param0).method3471(((HudWindow)param0).method3484());
        });
        this.save();
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject v1 = new JsonObject();
        JsonObject v2 = new JsonObject();
        ((DraggingState)this.data).method102().forEach((param1, param2) -> {
            JsonObject v3 = new JsonObject();
            v3.addProperty(field6814, (Number)Float.valueOf(((HudWindow)param2).method3457()));
            v3.addProperty(field6815, (Number)Float.valueOf(((HudWindow)param2).method3496()));
            v2.add((String)param1, (JsonElement)v3);
        });
        JsonArray v3 = new JsonArray();
        ((DraggingState)this.data).method101().forEach(param1 -> v3.add(((HudWindow)param1).method3491()));
        JsonObject v4 = new JsonObject();
        ((DraggingState)this.data).method102().forEach((param22, param3) -> {
            JsonObject v4_188 = new JsonObject();
            ((HudWindow)param3).method3466().forEach(param2 -> this.method1679(v4_188, (Setting)param2));
            if (v4_188.size() > 0) {
                v4_188.add((String)param22, (JsonElement)v4_188);
            }
        });
        v1.add(field6804, (JsonElement)v2);
        v1.add(field6805, (JsonElement)v3);
        v1.add(field6806, (JsonElement)v4);
        return v1;
    }

    public LinkedHashMap method1683() {
        return ((DraggingState)this.data).method102();
    }

    public JsonObject method1684() {
        return this.writeJson();
    }

    public void method1686(HudWindow param12) {
        this.method1662().forEach(param1 -> {
            if (param1 != null && param1 != param12) {
                ((HudWindow)param1).method3477(false);
            }
        });
    }

    private void method1687(JsonObject param1) {
        this.method1688(param1, field6816, param12 -> {
            ((DraggingState)this.data).method101().clear();
            JsonArray v2 = ((JsonElement)param12).getAsJsonArray();
            v2.forEach(param1_220 -> {
                HudWindow v2_221 = (HudWindow)((DraggingState)this.data).method102().get(param1_220.getAsString());
                if (v2_221 != null) {
                    ((DraggingState)this.data).method101().add(v2_221);
                }
            });
            ((DraggingState)this.data).method102().values().forEach(param1_226 -> {
                if (!((DraggingState)this.data).method101().contains(param1_226)) {
                    ((DraggingState)this.data).method101().add(param1_226);
                }
            });
        });
    }

    @Override
    protected void resetData() {
        this.data = new DraggingState();
    }

    private void method1688(JsonObject param1, String param2, Consumer param3) {
        JsonElement v4 = param1.get(param2);
        if (v4 != null && !v4.isJsonNull()) {
            param3.accept(v4);
        }
    }

    static {
        field6813 = "temp\\drags.file";
        field6804 = "positions";
        field6805 = "renderOrder";
        field6806 = "settings";
        field6808 = "positions";
        field6816 = "renderOrder";
        field6807 = "settings";
        field6811 = "settings";
        field6812 = "settings";
        cfr_renamed_347 = "Failed to load settings for draggable \u0001: \u0001";
        ЧЫ = "Failed to save setting \u0001: \u0001";
        ch8 = "Failed to load dragging config: \u0001";
        ЧЬ = "плохаа хуёва ебанаа без тебя \u0001";
        Чщ = "Failed to load setting \u0001: \u0001";
        Чб = "Failed to load settings for \u0001: \u0001";
        Чо = "Failed to parse render order element: \u0001";
        field6809 = "x";
        field6810 = "y";
        ЖШ = "Failed to parse position for \u0001: \u0001";
        field6814 = "x";
        field6815 = "y";
    }
}



