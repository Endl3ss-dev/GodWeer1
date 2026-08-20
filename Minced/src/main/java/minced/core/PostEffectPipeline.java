/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.SimpleFramebuffer
 *  net.minecraft.client.gl.Uniform
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.JsonHelper
 *  org.joml.Matrix4f
 */
package minced.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import minced.core.ResourceUtil;
import minced.core.PostProcessPass;
import minced.core.ClientUtil;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.Uniform;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.joml.Matrix4f;

public class PostEffectPipeline
implements Consumer {
    static String field8076;

    @Override
    public void accept(Object param1) {
        this.method2084();
    }

    Framebuffer field8077;
    HashMap var_java_util_HashMap_I = new HashMap();
    Identifier field8078;
    ArrayList<PostProcessPass> field8079;
    static String field8080;
    static String field8081;
    ArrayList<SimpleFramebuffer> var_java_util_ArrayList_I = new ArrayList<>();
    Matrix4f field8082;
    static float field8083;
    static float field8084;
    static String field8085;
    static String field8086;
    static String field8087;
    static String field8088;
    static String field8089;
    static String field8090;
    static String field8091;
    static String field8092;
    static String field8093;

    public Framebuffer method2071(String param1) {
        return param1.equals(field8076) ? this.field8077 : (Framebuffer)this.var_java_util_HashMap_I.get(param1);
    }

    public PostEffectPipeline(String param1) {
        this(Identifier.ofVanilla((String)param1));
    }

    public PostProcessPass method2072(String param1, Framebuffer param2, Framebuffer param3, boolean param4) {
        PostProcessPass v5 = new PostProcessPass(this, param1, param2, param3, param4, this.field8078.getNamespace());
        this.field8079.add(v5);
        return v5;
    }

    public void method2074(String param1, Framebuffer param2) {
        Framebuffer v3 = (Framebuffer)this.var_java_util_HashMap_I.get(param1);
        for (PostProcessPass v5 : this.field8079) {
            if (v5.field8039.equals(v3)) {
                v5.field8039 = param2;
            }
            if (!v5.field8040.equals(v3)) continue;
            v5.field8040 = param2;
        }
        this.var_java_util_HashMap_I.put(param1, param2);
    }

    public void method2075() {
        JsonObject v1 = ResourceUtil.method2056(this.field8078);
        JsonArray v2 = v1.getAsJsonArray(field8080);
        JsonArray v3 = v1.getAsJsonArray(field8081);
        v2.forEach(this::method2082);
        v3.forEach(this::method2080);
    }

    public PostEffectPipeline(Identifier param1) {
        this.field8079 = new ArrayList();
        this.field8078 = param1;
        this.field8077 = ClientUtil.I.getFramebuffer();
        this.method2075();
        this.method2076(ClientUtil.I.getWindow().getFramebufferWidth(), ClientUtil.I.getWindow().getFramebufferHeight());
    }

    public void method2076(int param1, int param2) {
        this.method2078();
        for (PostProcessPass v4 : this.field8079) {
            v4.field8044 = this.field8082;
        }
        for (SimpleFramebuffer v4 : this.var_java_util_ArrayList_I) {
            v4.resize(param1, param2);
        }
    }

    public int method2077() {
        return this.field8077.textureWidth;
    }

    public void method2078() {
        float v1 = this.field8077.textureWidth;
        float v2 = this.field8077.textureHeight;
        this.field8082 = new Matrix4f().setOrtho(0.0f, v1, 0.0f, v2, field8083, field8084);
    }

    public void method2079(String param1, int param2, int param3) {
        SimpleFramebuffer v4 = new SimpleFramebuffer(param2, param3, true);
        v4.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.var_java_util_HashMap_I.put(param1, v4);
        if (param2 == this.method2077() && param3 == this.method2081()) {
            this.var_java_util_ArrayList_I.add(v4);
        }
    }

    public void method2080(JsonElement param1) {
        JsonObject v2 = JsonHelper.asObject((JsonElement)param1, (String)field8085);
        String v3 = JsonHelper.getString((JsonObject)v2, (String)field8086);
        String v4 = JsonHelper.getString((JsonObject)v2, (String)field8087);
        String v5 = JsonHelper.getString((JsonObject)v2, (String)field8088);
        Framebuffer v7 = this.method2071(v4);
        Framebuffer v8 = this.method2071(v5);
        PostProcessPass v9 = this.method2072(v3, v7, v8, false);
        JsonArray v10 = JsonHelper.getArray((JsonObject)v2, (String)field8089, null);
        if (v10 != null) {
            v10.forEach(param2 -> this.method2083(v9, (JsonElement)param2));
        }
    }

    public int method2081() {
        return this.field8077.textureHeight;
    }

    public void method2082(JsonElement param1) {
        if (JsonHelper.isString((JsonElement)param1)) {
            String v2 = param1.getAsString();
            this.method2079(v2, this.method2077(), this.method2081());
        }
    }

    public void method2083(PostProcessPass param1, JsonElement param2) {
        JsonObject v3 = JsonHelper.asObject((JsonElement)param2, (String)field8090);
        String v4 = JsonHelper.getString((JsonObject)v3, (String)field8091);
        Uniform v5 = param1.field8041.method2093(v4, null);
        if (v5 == null) {
            return;
        }
        JsonArray v6 = JsonHelper.getArray((JsonObject)v3, (String)field8092);
        List<Float> v7 = Stream.of(v6).map(param0 -> Float.valueOf(JsonHelper.asFloat((JsonElement)param0, (String)field8093))).toList();
        switch (v7.size()) {
            case 1: {
                v5.set(v7.get(0).floatValue());
            }
            case 2: {
                v5.set(v7.get(0).floatValue(), v7.get(1).floatValue());
            }
            case 3: {
                v5.set(v7.get(0).floatValue(), v7.get(1).floatValue(), v7.get(2).floatValue());
            }
            case 4: {
                v5.setAndFlip(v7.get(0).floatValue(), v7.get(1).floatValue(), v7.get(2).floatValue(), v7.get(3).floatValue());
            }
        }
    }

    public void method2084() {
        for (PostProcessPass v2 : this.field8079) {
            v2.method2058();
        }
    }

    static {
        field8083 = 0.1f;
        field8084 = 1000.0f;
        field8080 = "targets";
        field8081 = "passes";
        field8076 = "minecraft:main";
        field8085 = "pass";
        field8086 = "name";
        field8087 = "intarget";
        field8088 = "outtarget";
        field8089 = "uniforms";
        field8090 = "uniform";
        field8091 = "name";
        field8092 = "values";
        field8093 = "value";
    }
}



