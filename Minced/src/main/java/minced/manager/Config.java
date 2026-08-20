/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  sLM.4ZXYEo
 */
package minced.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import minced.core.UserInfo;
import minced.core.PathUtil;
import minced.core.BindSetting;
import minced.core.ColorSetting;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.CryptoUtil;
import minced.core.GroupSetting;
import minced.core.Setting;
import minced.core.ColorListSetting;
import minced.core.ModeSetting;
import minced.core.ConfigMeta;
import minced.core.ConfigFile;
import minced.core.ConfigState;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Eternity;
import minced.enums.TOGGLE;
import sLM._4ZXYEo;

public class Config
extends ConfigFile
{
    static String cfr_renamed_337;
    static String cfr_renamed_338;
    static String cfr_renamed_339;
    static String cfr_renamed_340;
    static String cfr_renamed_341;
    static String cfr_renamed_342;
    static String cfr_renamed_343;
    static String cfr_renamed_344;
    static String cfr_renamed_345;
    static String cfr_renamed_346;

    public static File field1;
    static String field2;
    static String field3;
    Object data;
    static String field5;
    static String field6;
    static String field7;
    static String field8;
    static String field9;
    static String field10;
    static String field11;
    static String field12;
    static String field13;
    static String field14;
    static String field15;
    static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    boolean encrypted;
    static String field18;
    static String field19;
    static String field20;
    static String field21;
    static String field22;
    static String field23;
    static String field24;
    static String field25;
    static String field26;
    static String field27;
    static String field28;
    static String field29;
    static String field30;
    static String field31;
    static String field32;
    static String field33;
    static String field34;
    static String field35;
    static String field36;
    static String field37;
    static String field38;
    static String field39;
    static String field40;
    static String field41;
    static String field42;
    static String field43;
    static String field44;
    static String field45;
    static String field46;
    static String field47;
    static String field48;
    static String field49;
    static String field50;
    static String field51;
    static String field52;
    static String field53;

    public List method1() {
        File[] v1 = field1.listFiles((param0, param1) -> param1.endsWith(field21));
        if (v1 == null) {
            return Collections.emptyList();
        }
        ArrayList<String> v2 = new ArrayList<String>();
        for (File v6 : v1) {
            v2.add(v6.getName().replace(field2, ""));
        }
        return v2;
    }

    private void method2(JsonObject param1, String param2, Consumer param3) {
        JsonElement v4 = param1.get(param2);
        if (v4 != null && !v4.isJsonNull()) {
            param3.accept(v4);
        }
    }

    public boolean method3(String param1) {
        if (param1 == null || param1.trim().isEmpty()) {
            System.err.println(field3);
            return false;
        }
        boolean v2 = new File(field1, (String)((Object)_4ZXYEo.фЕ("makeConcatWithConstants", new Object[]{}, (String)param1.trim()))).delete();
        if (v2 && param1.equals(((ConfigState)this.data).method69())) {
            ((ConfigState)this.data).method68(null);
            this.save();
        }
        return v2;
    }

    public static void method4() {
        field1 = new File((String)((Object)_4ZXYEo.фЙ("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558)));
    }

    private JsonObject method5() {
        JsonObject v1 = new JsonObject();
        Minced.getInstance().getModuleManager().method178().forEach(param22 -> {
            JsonObject v3 = new JsonObject();
            v3.addProperty(field42, (Number)param22.getKey());
            v3.addProperty(field43, Boolean.valueOf(param22.isEnabled()));
            v3.addProperty(field44, param22.getToggleMode().name());
            param22.getSettings().forEach(param2 -> this.method8(v3, (Setting)param2));
            v1.add(param22.getName().toLowerCase(), (JsonElement)v3);
        });
        return v1;
    }

    private void method8(JsonObject param12, Setting param2) {
        if (param2 instanceof BooleanSetting) {
            BooleanSetting v4 = (BooleanSetting)param2;
            JsonObject v5 = new JsonObject();
            v5.addProperty(field8, (Boolean)v4.getValue());
            v5.addProperty(field9, (Number)v4.method1903());
            v5.addProperty(field10, v4.method1901().name());
            param12.add(param2.getName(), (JsonElement)v5);
        } else if (param2 instanceof SliderSetting) {
            param12.addProperty(param2.getName(), (Number)((SliderSetting)param2).getValue());
        } else if (param2 instanceof ModeSetting) {
            param12.addProperty(param2.getName(), (String)((ModeSetting)param2).getValue());
        } else if (param2 instanceof ColorSetting) {
            param12.addProperty(param2.getName(), (Number)((ColorSetting)param2).getValue());
        } else if (param2 instanceof BindSetting) {
            param12.addProperty(param2.getName(), (Number)((BindSetting)param2).getValue());
        } else if (param2 instanceof GroupSetting) {
            GroupSetting v3 = (GroupSetting)param2;
            param12.addProperty(param2.getName(), (String)v3.getValue());
        } else if (param2 instanceof ColorListSetting) {
            param12.addProperty(param2.getName(), (String)((ColorListSetting)param2).getValue());
        } else if (param2 instanceof MultiBooleanSetting) {
            MultiBooleanSetting v4 = (MultiBooleanSetting)param2;
            JsonObject v5 = new JsonObject();
            ((List)v4.getValue()).forEach(param1 -> v5.addProperty(((Setting)param1).getName(), (Boolean)((Setting)param1).getValue()));
            param12.add(param2.getName(), (JsonElement)v5);
        }
    }

    public void method9(String param1) {
        if (param1 == null || param1.trim().isEmpty()) {
            System.err.println(field11);
            return;
        }
        field1.mkdirs();
        String v2Name = param1.trim();
        if (!v2Name.endsWith(field21)) {
            v2Name = v2Name + field21;
        }
        File v2 = new File(field1, v2Name);
        JsonObject v3 = new JsonObject();
        v3.add(field12, (JsonElement)this.method5());
        this.method11(v3);
        if (Minced.getInstance().getDraggingManager() != null) {
            v3.add(field13, (JsonElement)Minced.getInstance().getDraggingManager().method1684());
        }
        v3.addProperty(field14, (Number)System.currentTimeMillis());
        v3.addProperty(field15, UserInfo.method1841());
        String v4 = GSON.toJson((JsonElement)v3);
        String v5 = this.encrypted ? CryptoUtil.method1965(v4) : v4;
        try {
            Files.writeString(v2.toPath(), (CharSequence)v5, new OpenOption[0]);
        }
        catch (java.io.IOException v6) {
            System.err.println(v6);
        }
    }

    private void method11(JsonObject param1) {
        JsonObject v2 = new JsonObject();
        v2.addProperty(field18, Minced.getInstance().getThemeManager().cfr_renamed_272().name());
        v2.addProperty(field19, Minced.getInstance().getThemeManager().method285());
        param1.add(field20, (JsonElement)v2);
    }

    private void method12(JsonObject param1) {
        boolean v2 = Module.isStateChangeSuppressed();
        Module.setStateChangeSuppressed(true);
        Minced.getInstance().getModuleManager().method178().forEach(param22 -> {
            JsonObject v3 = param1.getAsJsonObject(param22.getName().toLowerCase());
            if (v3 == null) {
                return;
            }
            param22.cfr_renamed_4(false);
            this.method2(v3, field5, param1_227 -> param22.setKey(((JsonElement)param1_227).getAsInt()));
            this.method2(v3, field6, param1_228 -> param22.cfr_renamed_4(((JsonElement)param1_228).getAsBoolean()));
            this.method2(v3, field7, param1_229 -> param22.setToggleMode(TOGGLE.valueOf(((JsonElement)param1_229).getAsString())));
            param22.getSettings().forEach(param2 -> this.method19(v3, (Setting)param2));
        });
        Module.setStateChangeSuppressed(v2);
    }

    public void method18() {
        boolean v1 = Module.isStateChangeSuppressed();
        Module.setStateChangeSuppressed(true);
        Minced.getInstance().getModuleManager().method178().forEach(param0 -> {
            param0.cfr_renamed_4(false);
            param0.setKey(-100);
            param0.setToggleMode(TOGGLE.TOGGLE);
            param0.getSettings().forEach(param02 -> {
                if (param02 instanceof BooleanSetting) {
                    BooleanSetting v1_244 = (BooleanSetting)param02;
                    v1_244.setValue((Boolean)v1_244.value);
                    v1_244.cfr_renamed_0(-100);
                    v1_244.method1904(TOGGLE.TOGGLE);
                } else if (param02 instanceof SliderSetting) {
                    SliderSetting v2 = (SliderSetting)param02;
                    v2.method1906(Float.valueOf(v2.field7744));
                } else if (param02 instanceof ModeSetting) {
                    ModeSetting v3 = (ModeSetting)param02;
                    v3.setValue(v3.defaultMode);
                } else if (param02 instanceof ColorSetting) {
                    ColorSetting v4 = (ColorSetting)param02;
                    v4.setValue(v4.field7598);
                } else if (param02 instanceof BindSetting) {
                    BindSetting v5 = (BindSetting)param02;
                    v5.setValue(-1);
                } else if (param02 instanceof GroupSetting) {
                    GroupSetting v6 = (GroupSetting)param02;
                    v6.setValue("");
                } else if (param02 instanceof ColorListSetting) {
                    ColorListSetting v7 = (ColorListSetting)param02;
                    v7.setValue("");
                } else if (param02 instanceof MultiBooleanSetting) {
                    MultiBooleanSetting v8 = (MultiBooleanSetting)param02;
                    ((List)v8.getValue()).forEach(param0_268 -> ((Setting)param0_268).setValue((Boolean)((Setting)param0_268).value));
                }
            });
        });
        Module.setStateChangeSuppressed(v1);
    }

    private void method19(JsonObject param12, Setting param2) {
        JsonElement v3 = param12.get(param2.getName());
        if (v3 == null || v3.isJsonNull()) {
            return;
        }
        if (param2 instanceof SliderSetting) {
            ((SliderSetting)param2).method1906(Float.valueOf(v3.getAsFloat()));
        } else if (param2 instanceof BooleanSetting) {
            if (v3.isJsonObject()) {
                JsonObject v5 = v3.getAsJsonObject();
                BooleanSetting v6 = (BooleanSetting)param2;
                this.method2(v5, field22, param1 -> v6.setValue(((JsonElement)param1).getAsBoolean()));
                this.method2(v5, field23, param1 -> v6.cfr_renamed_0(((JsonElement)param1).getAsInt()));
                this.method2(v5, field24, param1 -> v6.method1904(TOGGLE.valueOf(((JsonElement)param1).getAsString())));
            }
        } else if (param2 instanceof ColorSetting) {
            ((ColorSetting)param2).setValue(v3.getAsInt());
        } else if (param2 instanceof ModeSetting) {
            ((ModeSetting)param2).setValue(v3.getAsString());
        } else if (param2 instanceof BindSetting) {
            ((BindSetting)param2).setValue(v3.getAsInt());
        } else if (param2 instanceof GroupSetting) {
            GroupSetting v4 = (GroupSetting)param2;
            v4.setValue(v3.getAsString());
        } else if (param2 instanceof ColorListSetting) {
            ((ColorListSetting)param2).setValue(v3.getAsString());
        } else if (param2 instanceof MultiBooleanSetting && v3.isJsonObject()) {
            JsonObject v5 = v3.getAsJsonObject();
            MultiBooleanSetting v6 = (MultiBooleanSetting)param2;
            ((List)v6.getValue()).forEach(param1 -> {
                JsonElement v2 = v5.get(((Setting)param1).getName());
                if (v2 != null && !v2.isJsonNull()) {
                    ((Setting)param1).setValue(v2.getAsBoolean());
                }
            });
        }
    }

    private void method20(JsonObject param1) {
        if (param1.has(field25)) {
            Minced.getInstance().getThemeManager().cfr_renamed_0(Eternity.valueOf(param1.get(field26).getAsString()));
        }
        if (param1.has(field27)) {
            Minced.getInstance().getThemeManager().method278(param1.get(field28).getAsString());
        }
    }

    @Override
    public void load() {
        field1.mkdirs();
        super.load();
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject v1 = new JsonObject();
        if (((ConfigState)this.data).method69() != null) {
            v1.addProperty(field29, ((ConfigState)this.data).method69());
        }
        return v1;
    }

    public ConfigMeta method25(String param1) {
        if (param1 == null || param1.trim().isEmpty()) {
            System.err.println(field30);
            return null;
        }
        File v2 = new File(field1, (String)((Object)_4ZXYEo.фч("makeConcatWithConstants", new Object[]{}, (String)param1.trim())));
        if (!v2.exists()) {
            return null;
        }
        String v3;
        try {
            v3 = Files.readString(v2.toPath());
        }
        catch (java.io.IOException v9) {
            // Unreadable config is treated the same as a missing one.
            return null;
        }
        String v4 = this.encrypted ? CryptoUtil.method1964(v3) : v3;
        JsonObject v5 = new JsonParser().parse(v4).getAsJsonObject();
        long v6 = v5.has(field31) && !v5.get(field32).isJsonNull() ? v5.get(field33).getAsLong() : System.currentTimeMillis();
        String v8 = v5.has(field34) && !v5.get(field35).isJsonNull() ? v5.get(field36).getAsString() : field37;
        return new ConfigMeta(param1, v6, v8);
    }

    @Override
    protected void resetData() {
        this.data = new ConfigState();
    }

    @Override
    protected void readJson(JsonObject param1) {
        if (param1.has(field38) && !param1.get(field39).isJsonNull()) {
            ((ConfigState)this.data).method68(param1.get(field40).getAsString());
        }
    }

    public Config() {
        super(field41);
    }

    public void method31(String param1) {
        if (param1 == null || param1.trim().isEmpty()) {
            System.err.println(field45);
            return;
        }
        File v2 = new File(field1, (String)((Object)_4ZXYEo.фп("makeConcatWithConstants", new Object[]{}, (String)param1.trim())));
        if (!v2.exists()) {
            return;
        }
        String v3;
        try {
            v3 = Files.readString(v2.toPath());
        }
        catch (java.io.IOException v9) {
            // Unreadable config is treated the same as a missing one.
            return;
        }
        String v4 = this.encrypted ? CryptoUtil.method1964(v3) : v3;
        JsonObject v5 = new JsonParser().parse(v4).getAsJsonObject();
        if (v5.has(field46)) {
            this.method12(v5.getAsJsonObject(field47));
            ((ConfigState)this.data).method68(param1);
            this.save();
        }
        if (v5.has(field48) && v5.get(field49).isJsonObject() && Minced.getInstance().getDraggingManager() != null) {
            Minced.getInstance().getDraggingManager().method1657(v5.getAsJsonObject(field50));
        }
        if (v5.has(field51) && !v5.get(field52).isJsonNull()) {
            this.method20(v5.getAsJsonObject(field53));
        }
    }

    static {
        field41 = "custom\\";
        field29 = "lastLoadedConfig";
        field38 = "lastLoadedConfig";
        field39 = "lastLoadedConfig";
        field40 = "lastLoadedConfig";
        field2 = ".cfg";
        field45 = "Cannot load config: name is null or empty";
        cfr_renamed_337 = "\u0001.cfg";
        field46 = "module";
        field47 = "module";
        field48 = "hud";
        field49 = "hud";
        field50 = "hud";
        field51 = "theme";
        field52 = "theme";
        field53 = "theme";
        cfr_renamed_338 = "Failed to load config: \u0001 - \u0001";
        field11 = "Cannot save config: name is null or empty";
        cfr_renamed_339 = "\u0001.cfg";
        field12 = "module";
        field13 = "hud";
        field14 = "creationDate";
        field15 = "creator";
        cfr_renamed_340 = "Failed to save config: \u0001 - \u0001";
        field3 = "Cannot delete config: name is null or empty";
        cfr_renamed_341 = "\u0001.cfg";
        field30 = "Cannot get config info: name is null or empty";
        cfr_renamed_342 = "\u0001.cfg";
        field31 = "creationDate";
        field32 = "creationDate";
        field33 = "creationDate";
        field34 = "creator";
        field35 = "creator";
        field36 = "creator";
        field37 = "Unknown";
        field22 = "value";
        field23 = "bind";
        field24 = "togglemode";
        cfr_renamed_343 = "Failed to load setting \u0001: \u0001";
        field8 = "value";
        field9 = "bind";
        field10 = "togglemode";
        cfr_renamed_344 = "Failed to save setting \u0001: \u0001";
        field18 = "palette";
        field19 = "particle";
        field20 = "theme";
        field25 = "palette";
        field26 = "palette";
        field27 = "particle";
        field28 = "particle";
        cfr_renamed_345 = "Failed to load theme from config: \u0001";
        field42 = "key";
        field43 = "enabled";
        field44 = "togglemode";
        field5 = "key";
        field6 = "enabled";
        field7 = "togglemode";
        field21 = ".cfg";
        cfr_renamed_346 = "\u0001custom\\";
        Config.method4();
    }
}


