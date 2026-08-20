/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.core;

import java.io.File;
import java.util.function.ToLongFunction;
import minced.manager.Config;
import sLM._4ZXYEo;

public class ConfigMeta {
    static String cfr_renamed_286;
    static String cfr_renamed_287;

    String field12152;
    File field12153;
    long field12154;
    static String field12155;
    String field12156;
    static String field12157;

    public ConfigMeta(String param1) {
        this.field12152 = param1;
        this.field12153 = new File(Config.field1, (String)((Object)_4ZXYEo.фИ("makeConcatWithConstants", new Object[]{}, (String)param1)));
        this.field12154 = System.currentTimeMillis();
        this.field12156 = field12157;
    }

    public File method3445() {
        return this.field12153;
    }

    public String method3446() {
        return this.field12156;
    }

    public ConfigMeta(String param1, long param2, String param4) {
        this.field12152 = param1;
        this.field12153 = new File(Config.field1, (String)((Object)_4ZXYEo.фс("makeConcatWithConstants", new Object[]{}, (String)param1)));
        this.field12154 = param2;
        this.field12156 = param4 != null ? param4 : field12155;
    }

    public String method3447() {
        return this.field12152;
    }

    public long method3448() {
        return this.field12154;
    }

    static {
        cfr_renamed_286 = "\u0001.cfg";
        field12157 = "Minced.getInstance().getUserInfo().getUsername()";
        cfr_renamed_287 = "\u0001.cfg";
        field12155 = "Unknown";
    }
}



