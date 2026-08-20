/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  sLM.4ZXYEo
 */
package minced.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import minced.core.PathUtil;
import minced.core.CryptoUtil;
import sLM._4ZXYEo;

public abstract class ConfigFile {
    static String шы;
    static String шЮ;
    static String шl;

    File file;
    static Gson GSON;
    static String CONFIG_ROOT;
    boolean encrypted;
    Object data;

    public String getAbsolutePath() {
        return this.file.getAbsolutePath();
    }

    public static void initializeSerializer() {
        GSON = new GsonBuilder().setPrettyPrinting().create();
        CONFIG_ROOT = PathUtil.field7558;
    }

    public void load() {
        this.ensureParentDirectory();
        this.resetData();
        this.loadFromDisk();
    }

    public void save() {
        try {
            this.ensureParentDirectory();
            JsonObject v1 = this.writeJson();
            String v2 = GSON.toJson((JsonElement)v1);
            String v3 = this.encrypted ? CryptoUtil.method1965(v2) : v2;
            Files.writeString(this.file.toPath(), (CharSequence)v3, new OpenOption[0]);
        } catch (Exception exception) {
            this.handleSaveException(exception);
        }
    }

    protected void setData(Object param1) {
        this.data = param1;
    }

    protected ConfigFile(String param1) {
        this(param1, true);
    }

    protected void ensureParentDirectory() {
        File v1 = this.file.getParentFile();
        if (v1 != null && !v1.exists()) {
            v1.mkdirs();
        }
    }

    public void loadFromDisk() {
        if (!this.file.exists()) {
            return;
        }
        try {
            String v1 = Files.readString(this.file.toPath());
            String v2 = this.encrypted ? CryptoUtil.method1964(v1) : v1;
            JsonObject v3 = new JsonParser().parse(v2).getAsJsonObject();
            this.readJson(v3);
        } catch (Exception exception) {
            this.handleLoadException(exception);
        }
    }

    protected abstract void readJson(JsonObject var1);

    public boolean delete() {
        return this.file.exists() && this.file.delete();
    }

    protected abstract JsonObject writeJson();

    public boolean exists() {
        return this.file.exists();
    }

    public String getFileName() {
        return this.file.getName();
    }

    protected abstract void resetData();

    public Object getData() {
        return this.data;
    }

    protected void handleLoadException(Exception param1) {
    }

    protected void handleSaveException(Exception param1) {
    }

    protected ConfigFile(String param1, boolean param2) {
        this.file = new File((String)((Object)_4ZXYEo.А("makeConcatWithConstants", new Object[]{}, (String)CONFIG_ROOT, (String)param1)));
        this.encrypted = param2;
    }

    static {
        CONFIG_ROOT = "C:\\Minced\\";
        шы = "\u0001\u0001";
        шЮ = "Failed to load config from \u0001: \u0001";
        шl = "Failed to save config to \u0001: \u0001";
        ConfigFile.initializeSerializer();
    }
}



