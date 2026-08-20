/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 *  sg.ec.ПЙ$2
 *  sg.ec.ЧК$Ч
 */
package minced.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import minced.core.PathUtil;
import minced.core.ColorUtil;
import minced.core.CryptoUtil;
import minced.core.SkinTextureCache;
import minced.core.ConfigFile;
import minced.enums.Bloom;
import minced.enums.Eternity;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;
import sg.ec.pj;
import sg.ec.chk;

public class Theme
extends ConfigFile
{
    static String уО;
    static String уЙ;
    static String у必;
    static String уъ;
    static String уА;
    static String уЧ;
    static String у诶;

    static String field858;
    static File I;
    boolean encrypted;
    static String field859;
    static String field860;
    static String field861;
    static String field862;
    SkinTextureCache field863;
    int field864;
    static int field865;
    Eternity field866 = Eternity.Eternity;
    Bloom field867 = Bloom.Bloom;
    String field868;
    static String field869;
    static String field870;
    static String field871;
    static String field872;
    static String field873;
    static String field874;
    Gson GSON;
    static String field875;
    static int field876;
    static String field877;
    static String field878;
    static String field879;
    static String field880;
    static String field881;
    static String field882;
    static String field883;
    static String field884;
    static int field885;
    static float field886;
    static int field887;
    static String field888;

    public int method276() {
        return field887 | this.field866.field1081;
    }

    @Override
    public void load() {
        I.mkdirs();
        this.field863.method2031();
        super.load();
    }

    public boolean method277(String param1) {
        if (param1 == null || param1.trim().isEmpty()) {
            System.err.println(field888);
            return false;
        }
        File v2 = new File(I, (String)((Object)_4ZXYEo.х("makeConcatWithConstants", new Object[]{}, (String)param1.trim())));
        return v2.exists() && v2.delete();
    }

    public void cfr_renamed_271() {
        this.field863.method2030();
    }

    public chk.ch cfr_renamed_0(String param12) {
        if (param12 == null || param12.trim().isEmpty()) {
            System.err.println(field858);
            return null;
        }
        File v2 = new File(I, (String)((Object)_4ZXYEo.й("makeConcatWithConstants", new Object[]{}, (String)param12.trim())));
        if (!v2.exists()) {
            return null;
        }
        chk.ch v3 = new chk.ch(param12);
        String v4;
        try {
            v4 = Files.readString(v2.toPath());
        }
        catch (java.io.IOException v9) {
            // Unreadable theme file is treated the same as a missing one.
            return null;
        }
        String v5 = this.encrypted ? CryptoUtil.method1964(v4) : v4;
        JsonObject v6 = new JsonParser().parse(v5).getAsJsonObject();
        this.method287(v6, field859, param1 -> {
            v3.х = ((JsonElement)param1).getAsString();
        });
        this.method287(v6, field860, param1 -> {
            v3.ъ = ((JsonElement)param1).getAsString();
        });
        JsonArray v7 = v6.getAsJsonArray(field861);
        if (v7 != null) {
            v3.sh = new int[v7.size()];
            for (int v8 = 0; v8 < v7.size(); ++v8) {
                v3.sh[v8] = v7.get(v8).getAsInt();
            }
        }
        return v3;
    }

    public void method278(String param1) {
        if (param1 == null || param1.isEmpty()) {
            return;
        }
        if (param1.startsWith(field862)) {
            this.field863.method2017();
            this.method288(param1.substring(7));
            return;
        }
        this.cfr_renamed_0(Bloom.valueOf(param1));
    }

    public int method279() {
        return this.field864;
    }

    public int method281() {
        return field865 | this.field866.field1082;
    }

    public void cfr_renamed_0(Eternity param1) {
        if (param1 == null) {
            return;
        }
        this.field866 = param1;
        this.save();
    }

    public Eternity cfr_renamed_272() {
        return this.field866;
    }

    public Bloom cfr_renamed_273() {
        return this.field867;
    }

    public Identifier cfr_renamed_0(int param1) {
        Identifier v2;
        if (this.cfr_renamed_91() && (v2 = this.field863.method2025(this.field868)) != null) {
            return v2;
        }
        if (param1 == 1 && this.field867.field1080 != null) {
            return this.field867.field1080;
        }
        return this.field867.field1079;
    }

    public void cfr_renamed_0(String param1, pj._2 param2, String param3) {
        if (param1 == null || param1.trim().isEmpty()) {
            System.err.println(field869);
            return;
        }
        if (param2 == null) {
            System.err.println(field870);
            return;
        }
        I.mkdirs();
        File v4 = new File(I, (String)((Object)_4ZXYEo.诶("makeConcatWithConstants", new Object[]{}, (String)param1.trim())));
        JsonObject v5 = new JsonObject();
        v5.addProperty(field871, (String)param2.кн());
        v5.addProperty(field872, param3 != null ? param3 : field873);
        JsonArray v6 = new JsonArray();
        for (int v10 : (int[])param2.Ъ()) {
            v6.add((Number)v10);
        }
        v5.add(field874, (JsonElement)v6);
        String string = GSON.toJson((JsonElement)v5);
        String v8 = this.encrypted ? CryptoUtil.method1965(string) : string;
        try {
            Files.writeString(v4.toPath(), (CharSequence)v8, new OpenOption[0]);
        }
        catch (java.io.IOException v9) {
            System.err.println(v9);
        }
    }

    public static void method282() {
        I = new File((String)((Object)_4ZXYEo.Я("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558)));
    }

    public Theme() {
        super(field875);
        this.field863 = new SkinTextureCache();
        this.field864 = field876 | Eternity.Eternity.field1081;
    }

    public boolean cfr_renamed_359() {
        if (this.cfr_renamed_91()) {
            return false;
        }
        return this.field867.field1080 != null;
    }

    public SkinTextureCache cfr_renamed_270() {
        return this.field863;
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject v1 = new JsonObject();
        v1.addProperty(field877, this.field866.name());
        v1.addProperty(field878, this.method285());
        return v1;
    }

    public List method283() {
        File[] v1 = I.listFiles((param0, param1) -> param1.endsWith(field880));
        if (v1 == null) {
            return Collections.emptyList();
        }
        ArrayList<String> v2 = new ArrayList<String>();
        for (File v6 : v1) {
            v2.add(v6.getName().replace(field879, ""));
        }
        return v2;
    }

    @Override
    protected void resetData() {
    }

    public String method285() {
        if (this.cfr_renamed_91()) {
            return _4ZXYEo.cfr_renamed_5("makeConcatWithConstants", new Object[]{}, (String)this.field868);
        }
        return this.field867.name();
    }

    public boolean cfr_renamed_91() {
        return this.field868 != null;
    }

    @Override
    protected void readJson(JsonObject param1) {
        if (param1.has(field881)) {
            this.cfr_renamed_0(Eternity.valueOf(param1.get(field882).getAsString()));
        }
        if (param1.has(field883)) {
            this.method278(param1.get(field884).getAsString());
        }
    }

    public List method286() {
        return this.field863.method2016();
    }

    private void method287(JsonObject param1, String param2, Consumer param3) {
        JsonElement v4 = param1.get(param2);
        if (v4 != null && !v4.isJsonNull()) {
            param3.accept(v4);
        }
    }

    public void cfr_renamed_0(Bloom param1) {
        if (param1 == null) {
            return;
        }
        this.field867 = param1;
        this.field868 = null;
        this.save();
    }

    public void method288(String param1) {
        if (param1 == null || this.field863.method2018(param1) == null) {
            return;
        }
        this.field868 = param1;
        this.save();
    }

    public String method289() {
        return this.field868;
    }

    public void cfr_renamed_334() {
        this.field863.method2026();
        this.field864 = ColorUtil.method1850(this.field864, field885 | this.field866.field1081, field886);
    }

    static {
        field875 = "temp\\theme.file";
        field876 = -16777216;
        field877 = "Palette";
        field878 = "Particle";
        field881 = "Palette";
        field882 = "Palette";
        field883 = "Particle";
        field884 = "Particle";
        уО = "custom:\u0001";
        field862 = "custom:";
        field885 = -16777216;
        field886 = 0.05f;
        field887 = -16777216;
        field865 = -16777216;
        field879 = ".file";
        field858 = "Cannot load theme: name is null or empty";
        уЙ = "\u0001.file";
        field859 = "presetName";
        field860 = "creator";
        field861 = "presetColors";
        у必 = "Failed to load theme: \u0001 - \u0001";
        field869 = "Cannot save theme: name is null or empty";
        field870 = "Cannot save theme: preset is null";
        уъ = "\u0001.file";
        field871 = "presetName";
        field872 = "creator";
        field873 = "Unknown";
        field874 = "presetColors";
        уА = "Failed to save theme: \u0001 - \u0001";
        field888 = "Cannot delete theme: name is null or empty";
        уЧ = "\u0001.file";
        field880 = ".file";
        у诶 = "\u0001themes\\";
        Theme.method282();
    }
}



