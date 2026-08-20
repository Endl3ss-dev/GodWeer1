/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.JsonHelper
 *  sLM.4ZXYEo
 */
package minced.core;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import sLM._4ZXYEo;

public class ResourceUtil {
    static String cfr_renamed_157;
    static String cfr_renamed_158;
    static String cfr_renamed_159;

    public static BufferedReader method2054(Identifier param0) {
        return new BufferedReader(new InputStreamReader(ResourceUtil.method2055(param0), StandardCharsets.UTF_8));
    }

    public static InputStream method2055(Identifier param0) {
        InputStream v1 = ResourceUtil.class.getResourceAsStream("/assets/" + param0.getNamespace() + "/" + param0.getPath());
        if (v1 == null) {
            throw new IllegalArgumentException("Resource not found: " + String.valueOf(param0));
        }
        return v1;
    }

    public static JsonObject method2056(Identifier param0) {
        try {
            BufferedReader v1 = ResourceUtil.method2054(param0);
            JsonObject v2 = JsonHelper.deserialize((Reader)v1);
            if (v1 != null) {
                v1.close();
            }
            return v2;
        }
        catch (java.io.IOException v3) {
            throw new RuntimeException(cfr_renamed_159, v3);
        }
    }

    static {
        cfr_renamed_157 = "/assets/\u0001/\u0001";
        cfr_renamed_158 = "Resource not found: \u0001";
        cfr_renamed_159 = "Failed to load JSON resource: \u0001";
    }
}



