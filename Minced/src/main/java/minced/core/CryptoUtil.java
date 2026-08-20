/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;

public class CryptoUtil {
    static byte[] field7928;
    static String field7929;
    static String field7930;

    public static void method1963() {
        field7928 = new byte[]{-54, -2, -70, -66};
    }

    public static String method1964(String param0) {
        if (param0 == null || param0.isEmpty()) {
            return param0;
        }
        byte[] v1 = Base64.getDecoder().decode(param0);
        if (v1.length < field7928.length) {
            return param0;
        }
        boolean v2 = true;
        for (int v3 = 0; v3 < field7928.length; ++v3) {
            if (v1[v3] == field7928[v3]) continue;
            v2 = false;
            break;
        }
        if (!v2) {
            return param0;
        }
        byte[] v3 = new byte[v1.length - field7928.length];
        System.arraycopy(v1, field7928.length, v3, 0, v3.length);
        byte[] v4 = field7929.getBytes(StandardCharsets.UTF_8);
        byte[] v5 = new byte[v3.length];
        for (int v6 = 0; v6 < v3.length; ++v6) {
            v5[v6] = (byte)(v3[v6] ^ v4[v6 % v4.length]);
        }
        return new String(v5, StandardCharsets.UTF_8);
    }

    public static String method1965(String param0) {
        if (param0 == null || param0.isEmpty()) {
            return param0;
        }
        byte[] v1 = param0.getBytes(StandardCharsets.UTF_8);
        byte[] v2 = field7930.getBytes(StandardCharsets.UTF_8);
        byte[] v3 = new byte[v1.length];
        for (int v4 = 0; v4 < v1.length; ++v4) {
            v3[v4] = (byte)(v1[v4] ^ v2[v4 % v2.length]);
        }
        byte[] v4 = new byte[field7928.length + v3.length];
        System.arraycopy(field7928, 0, v4, 0, field7928.length);
        System.arraycopy(v3, 0, v4, field7928.length, v3.length);
        return Base64.getEncoder().encodeToString(v4);
    }

    static {
        field7930 = "kotopesprot";
        field7929 = "kotopesprot";
        CryptoUtil.method1963();
    }
}

