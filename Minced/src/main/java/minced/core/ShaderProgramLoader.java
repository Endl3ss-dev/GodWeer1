/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gl.CompiledShader$Type
 *  net.minecraft.client.gl.GlUniform
 *  net.minecraft.client.gl.Uniform
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.JsonHelper
 *  org.apache.commons.io.IOUtils
 *  sLM.4ZXYEo
 */
package minced.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import minced.core.ShaderUniformPair;
import minced.core.ResourceUtil;
import minced.core.ShaderUniformEntry;
import minced.core.PostEffectPipeline;
import minced.mixin.accessor.CompiledShaderTypeAccessor;
import net.minecraft.client.gl.CompiledShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.Uniform;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import sLM._4ZXYEo;

public class ShaderProgramLoader
implements Consumer {
    static String cfr_renamed_163;

    @Override
    public void accept(Object param1) {
        this.method2097();
    }

    static String cfr_renamed_164;
    static String cfr_renamed_165;
    static String cfr_renamed_166;
    static String cfr_renamed_167;

    static String field8094;
    static String field8095;
    static String field8096;
    ArrayList field8097;
    static int field8098;
    static int field8099;
    static String field8100;
    static String field8101;
    static String field8102;
    static String field8103;
    static String field8104;
    HashMap<String, GlUniform> field8105;
    int field8106 = -1;
    ArrayList field8107 = new ArrayList();
    HashMap field8108;
    String field8109;
    Identifier field8110;
    static String field8111;
    static String field8112;
    static String field8113;
    static String field8114;
    static String field8115;
    static int field8116;
    static int field8117;
    static Uniform field8118;
    static String field8119;
    static int field8120;
    static int field8121;
    static String field8122;
    static String field8123;
    static int field8124;

    public int method2086(String param1, CompiledShader.Type param2) {
        return this.method2094(param1, param2, field8094);
    }

    public ShaderProgramLoader(PostEffectPipeline param1, String param2) {
        this(param1, param2, field8096);
    }

    public void method2088() {
        GlStateManager._glUseProgram((int)0);
        GlStateManager._disableBlend();
        for (int v1 = 0; v1 < this.field8097.size(); ++v1) {
            ShaderUniformEntry v2 = (ShaderUniformEntry)this.field8097.get(v1);
            if (v2.field8037 == null) continue;
            RenderSystem.activeTexture((int)(field8098 + v1));
            RenderSystem.bindTexture((int)0);
        }
        field8099 = -1;
    }

    public void method2089(JsonElement param1) {
        int v9;
        JsonObject v2 = JsonHelper.asObject((JsonElement)param1, (String)field8100);
        String v3 = JsonHelper.getString((JsonObject)v2, (String)field8101);
        String v4 = JsonHelper.getString((JsonObject)v2, (String)field8102);
        int v5 = GlUniform.getTypeIndex((String)v4);
        int v6 = JsonHelper.getInt((JsonObject)v2, (String)field8103);
        JsonArray v7 = JsonHelper.getArray((JsonObject)v2, (String)field8104);
        ArrayList<Float> v8 = new ArrayList<Float>(v7.asList().stream().map(param0 -> Float.valueOf(JsonHelper.asFloat((JsonElement)param0, (String)field8095))).toList());
        if (v6 <= 4 && v8.size() < 4 || v6 > 1 && v8.size() == 1) {
            for (v9 = 1; v9 < Math.max(v6, 4); ++v9) {
                v8.add(v8.getFirst());
            }
        }
        v9 = v6 > 1 && v6 <= 4 && v5 < 8 ? v6 - 1 : 0;
        GlUniform v10 = new GlUniform(v3, v5 + v9, v6);
        if (v5 <= 3) {
            v10.setForDataType(v8.get(0).intValue(), v8.get(1).intValue(), v8.get(2).intValue(), v8.get(3).intValue());
        } else if (v5 <= 7) {
            v10.setForDataType(v8.get(0).floatValue(), v8.get(1).floatValue(), v8.get(2).floatValue(), v8.get(3).floatValue());
        } else {
            float[] v11 = new float[v8.size()];
            for (int v12 = 0; v12 < v8.size(); ++v12) {
                v11[v12] = v8.get(v12).floatValue();
            }
            v10.set(v11);
        }
        this.field8105.put(v3, v10);
    }

    public ShaderProgramLoader(PostEffectPipeline param1, String param2, String param3) {
        int v12;
        this.field8097 = new ArrayList();
        this.field8108 = new HashMap();
        this.field8105 = new HashMap();
        this.field8109 = param2;
        this.field8110 = Identifier.of((String)param3, "shaders/program/" + param2 + ".json");
        JsonObject v4 = ResourceUtil.method2056(this.field8110);
        String v5 = JsonHelper.getString((JsonObject)v4, (String)field8111);
        String v6 = JsonHelper.getString((JsonObject)v4, (String)field8112);
        JsonArray v7 = JsonHelper.getArray((JsonObject)v4, (String)field8113, null);
        JsonArray v8 = JsonHelper.getArray((JsonObject)v4, (String)field8114, null);
        JsonArray v9 = JsonHelper.getArray((JsonObject)v4, (String)field8115, null);
        if (v7 != null) {
            v7.forEach(this::method2096);
        }
        if (v8 != null) {
            v8.forEach(this::method2091);
        }
        if (v9 != null) {
            v9.forEach(this::method2089);
        }
        int v10 = this.method2094(v5, CompiledShader.Type.VERTEX, param3);
        int v11 = this.method2094(v6, CompiledShader.Type.FRAGMENT, param3);
        this.field8106 = v12 = GlStateManager.glCreateProgram();
        GlStateManager.glAttachShader((int)v12, (int)v10);
        GlStateManager.glAttachShader((int)v12, (int)v11);
        GlStateManager.glLinkProgram((int)v12);
        if (GlStateManager.glGetProgrami((int)v12, (int)field8116) == 0) {
            String v13 = GlStateManager.glGetProgramInfoLog((int)v12, (int)field8117);
            throw new RuntimeException((String)((Object)_4ZXYEo.ЦЙ("makeConcatWithConstants", new Object[]{}, (String)v5, (String)v6, (String)v13)));
        }
        Iterator v13 = this.field8097.iterator();
        while (v13.hasNext()) {
            ShaderUniformEntry v14 = (ShaderUniformEntry)v13.next();
            int n = GlUniform.getUniformLocation((int)v12, (CharSequence)v14.field8035);
            if (n == -1) {
                v13.remove();
                continue;
            }
            v14.field8036 = n;
        }
        for (Map.Entry entry : this.field8105.entrySet()) {
            int v16 = GlUniform.getUniformLocation((int)v12, (CharSequence)((CharSequence)entry.getKey()));
            if (v16 == -1) continue;
            ((GlUniform)entry.getValue()).setLocation(v16);
        }
    }

    public Uniform method2090(String param1) {
        return this.method2093(param1, field8118);
    }

    public void method2091(JsonElement param1) {
        String v2 = JsonHelper.asString((JsonElement)param1, (String)field8119);
        this.field8107.add(new ShaderUniformPair(v2, -1));
    }

    public void method2092(String param1, Supplier param2) {
        ShaderUniformEntry v3 = (ShaderUniformEntry)this.field8108.get(param1);
        if (v3 != null) {
            v3.field8037 = param2;
        }
    }

    public Uniform method2093(String param1, Uniform param2) {
        GlUniform v3 = (GlUniform)this.field8105.get(param1);
        return v3 == null ? param2 : v3;
    }

    public int method2094(String param1, CompiledShader.Type param2, String param3) {
        String v4 = ((CompiledShaderTypeAccessor)(Object)param2).getFileExtension();
        Identifier v5 = Identifier.of((String)param3, "shaders/program/" + param1 + v4);
        String v6;
        try {
            InputStream v92 = ResourceUtil.method2055(v5);
            v6 = IOUtils.toString((InputStream)v92, (Charset)StandardCharsets.UTF_8);
            if (v92 != null) {
                v92.close();
            }
        }
        catch (java.io.IOException v7) {
            throw new RuntimeException(v7);
        }
        int shaderId = GlStateManager.glCreateShader((int)param2.getGlType());
        GlStateManager.glShaderSource((int)shaderId, (String)v6);
        GlStateManager.glCompileShader((int)shaderId);
        if (GlStateManager.glGetShaderi((int)shaderId, (int)field8120) == 0) {
            String v8 = GlStateManager.glGetShaderInfoLog((int)shaderId, (int)field8121);
            throw new RuntimeException((String)((Object)_4ZXYEo.ЦЪ("makeConcatWithConstants", new Object[]{}, (String)param1, (int)param2.getGlType(), (String)v8)));
        }
        return shaderId;
    }

    public static void method2095() {
        field8118 = new Uniform();
        field8099 = -1;
    }

    public void method2096(JsonElement param1) {
        JsonObject v2 = JsonHelper.asObject((JsonElement)param1, (String)field8122);
        String v3 = JsonHelper.getString((JsonObject)v2, (String)field8123);
        ShaderUniformEntry v4 = new ShaderUniformEntry(this, v3, -1, null);
        this.field8097.add(v4);
        this.field8108.put(v3, v4);
    }

    public void method2097() {
        if (this.field8106 != field8099) {
            GlStateManager._glUseProgram((int)this.field8106);
            field8099 = this.field8106;
        }
        int v1 = GlStateManager._getActiveTexture();
        for (int v2 = 0; v2 < this.field8097.size(); ++v2) {
            ShaderUniformEntry v3 = (ShaderUniformEntry)this.field8097.get(v2);
            if (v3.field8037 == null) continue;
            Integer v4 = (Integer)v3.field8037.get();
            RenderSystem.activeTexture((int)(field8124 + v2));
            RenderSystem.bindTexture((int)v4);
            GlUniform.uniform1((int)v3.field8036, (int)v2);
        }
        GlStateManager._activeTexture((int)v1);
        for (GlUniform v3 : this.field8105.values()) {
            v3.upload();
        }
    }

    static {
        field8099 = -1;
        field8096 = "minecraft";
        cfr_renamed_163 = "shaders/program/\u0001.json";
        field8111 = "vertex";
        field8112 = "fragment";
        field8113 = "samplers";
        field8114 = "attributes";
        field8115 = "uniforms";
        field8116 = 35714;
        field8117 = 32768;
        cfr_renamed_164 = "Error while linking \u0001 with \u0001\n\u0001";
        field8094 = "minecraft";
        cfr_renamed_165 = "shaders/program/\u0001\u0001";
        cfr_renamed_166 = "Failed to read shader source: \u0001";
        field8120 = 35713;
        field8121 = 32768;
        cfr_renamed_167 = "Error while compiling \u0001 \u0001 shader\n\u0001";
        field8122 = "sampler";
        field8123 = "name";
        field8119 = "attribute";
        field8100 = "uniform";
        field8101 = "name";
        field8102 = "type";
        field8103 = "count";
        field8104 = "values";
        field8124 = 33984;
        field8098 = 33984;
        field8095 = "value";
        ShaderProgramLoader.method2095();
    }
}



