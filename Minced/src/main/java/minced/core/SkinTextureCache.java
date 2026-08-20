/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.NativeImageBackedTexture
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 *  sg.ec.20
 *  sg.ec.Ч衣$Ч
 */
package minced.core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import minced.core.PathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;
import sg.ec._20;
import sg.ec.chYi;

public class SkinTextureCache {
    static String cfr_renamed_132;
    static String cfr_renamed_133;
    static String cfr_renamed_134;

    long field8023;
    static long field8024 = 1500L;
    static File field8025;
    Map<String, chYi.ch> field8026;
    List<_20> field8027 = new ArrayList<_20>();
    static String field8028;
    static String field8029;
    static String field8030;
    static String field8031;
    static String field8032;

    public List method2016() {
        return Collections.unmodifiableList(this.field8027);
    }

    public void method2017() {
        this.field8023 = System.currentTimeMillis();
        field8025.mkdirs();
        File[] v1 = field8025.listFiles((param0, param1) -> SkinTextureCache.method2019(param1));
        this.field8027.clear();
        if (v1 == null) {
            return;
        }
        ArrayList<String> v2 = new ArrayList<String>();
        for (File v6 : v1) {
            if (!v6.isFile()) continue;
            v2.add(v6.getName().toLowerCase(Locale.ROOT));
        }
        Collections.sort(v2);
        for (String v4 : v2) {
            chYi.ch v6;
            String v5 = SkinTextureCache.method2023(v4);
            v6 = (chYi.ch)this.field8026.get(v4);
            Identifier v7 = v6 != null ? v6.м : Identifier.of((String)field8028, (String)((Object)_4ZXYEo.弟ъ("makeConcatWithConstants", new Object[]{}, (String)SkinTextureCache.method2024(v4))));
            this.field8027.add(new _20(v5, v4, v7));
        }
    }

    public SkinTextureCache() {
        this.field8026 = new HashMap();
    }

    public _20 method2018(String param1) {
        if (param1 == null) {
            return null;
        }
        for (_20 v3 : this.field8027) {
            if (!v3.id().equals(param1)) continue;
            return v3;
        }
        return null;
    }

    private static boolean method2019(String param0) {
        if (param0 == null) {
            return false;
        }
        String v1 = param0.toLowerCase(Locale.ROOT);
        return v1.endsWith(field8029) || v1.endsWith(field8030);
    }

    private chYi.ch method2021(MinecraftClient param1, File param2) throws java.io.IOException {
        InputStream v3 = Files.newInputStream(param2.toPath(), new OpenOption[0]);
        NativeImage v4 = NativeImage.read((InputStream)v3);
        NativeImageBackedTexture v5 = new NativeImageBackedTexture(v4);
        Identifier v6 = Identifier.of((String)field8031, (String)((Object)_4ZXYEo.弟у("makeConcatWithConstants", new Object[]{}, (String)SkinTextureCache.method2024(param2.getName()))));
        param1.getTextureManager().registerTexture(v6, (AbstractTexture)v5);
        chYi.ch v7 = new chYi.ch(v6, v5, param2.lastModified());
        if (v3 != null) {
            v3.close();
        }
        return v7;
    }

    private static String method2023(String param0) {
        return param0.toLowerCase(Locale.ROOT);
    }

    private static String method2024(String param0) {
        StringBuilder v1 = new StringBuilder();
        String v2 = param0.toLowerCase(Locale.ROOT);
        for (int v3 = 0; v3 < v2.length(); ++v3) {
            char v4 = v2.charAt(v3);
            if (v4 >= 'a' && v4 <= 'z' || v4 >= '0' && v4 <= '9' || v4 == '.' || v4 == '_' || v4 == '-') {
                v1.append(v4);
                continue;
            }
            v1.append('_');
        }
        return v1.toString();
    }

    public Identifier method2025(String param1) {
        _20 v2 = this.method2018(param1);
        return v2 != null ? v2.texture() : null;
    }

    public void method2026() {
        long v1 = System.currentTimeMillis();
        if (v1 - this.field8023 >= field8024) {
            this.method2028();
        }
    }

    private void method2027(chYi.ch param1) {
        MinecraftClient v2 = MinecraftClient.getInstance();
        if (v2 != null) {
            v2.getTextureManager().destroyTexture(param1.м);
        }
        param1.н.close();
    }

    public void method2028() {
        this.field8023 = System.currentTimeMillis();
        field8025.mkdirs();
        MinecraftClient v1 = MinecraftClient.getInstance();
        if (v1 == null) {
            return;
        }
        if (!v1.isOnThread()) {
            v1.execute(this::method2028);
            return;
        }
        File[] v2 = field8025.listFiles((param0, param1) -> SkinTextureCache.method2019(param1));
        HashMap<String, File> v3 = new HashMap<String, File>();
        if (v2 != null) {
            for (File v7 : v2) {
                if (!v7.isFile()) continue;
                v3.put(v7.getName().toLowerCase(Locale.ROOT), v7);
            }
        }
        this.field8026.entrySet().removeIf(param2 -> {
            File v9;
            if (v3.containsKey(param2.getKey()) && (v9 = v3.get(param2.getKey())).lastModified() == ((chYi.ch)param2.getValue()).cfr_underscore) {
                return false;
            }
            this.method2027((chYi.ch)param2.getValue());
            return true;
        });
        this.field8027.clear();
        ArrayList v4 = new ArrayList(v3.keySet());
        Collections.sort(v4);
        Iterator v5 = v4.iterator();
        while (v5.hasNext()) {
            File v7;
            String v6 = (String)v5.next();
            v7 = (File)v3.get(v6);
            chYi.ch v8 = (chYi.ch)this.field8026.get(v6);
            if (v8 == null || v8.cfr_underscore != v7.lastModified()) {
                if (v8 != null) {
                    this.method2027(v8);
                    this.field8026.remove(v6);
                }
                try {
                    if ((v8 = this.method2021(v1, v7)) == null) continue;
                }
                catch (java.io.IOException v10) {
                    continue;
                }
                this.field8026.put(v6, v8);
            }
            String v9 = SkinTextureCache.method2023(v6);
            this.field8027.add(new _20(v9, v7.getName(), v8.м));
        }
    }

    public void method2030() {
        field8025.mkdirs();
        try {
            Runtime.getRuntime().exec(new String[]{field8032, field8025.getAbsolutePath()});
        } catch (java.io.IOException v1) {
            // the platform file manager is unavailable; nothing to do
        }
        this.method2028();
    }

    public void method2031() {
        field8025.mkdirs();
        this.method2017();
    }

    public static void method2032() {
        field8025 = new File((String)((Object)_4ZXYEo.弟В("makeConcatWithConstants", new Object[]{}, (String)PathUtil.field7558)));
    }

    static {
        field8032 = "explorer";
        field8028 = "minced";
        cfr_renamed_132 = "custom_particles/\u0001";
        field8031 = "minced";
        cfr_renamed_133 = "custom_particles/\u0001";
        field8029 = ".png";
        field8030 = ".jpg";
        cfr_renamed_134 = "\u0001Particles\\";
        SkinTextureCache.method2032();
    }
}



