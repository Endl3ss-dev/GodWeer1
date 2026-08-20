/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.SimpleFramebuffer
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 */
package minced.core;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import minced.core.FramebufferBlit;
import minced.core.PostProcessPass;
import minced.core.PostEffectPipeline;
import minced.core.ClientUtil;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;

public class ShaderEspRenderer
implements Consumer {
    public static java.lang.String field763;
    static String cfr_renamed_161;

    @Override
    public void accept(Object param1) {
    }

    public Framebuffer field8057;
    boolean field8058 = false;
    Framebuffer I;
    String field8059;
    String field8060;
    PostEffectPipeline field8061;
    static String field8062 = "field8062";
    static String field8063 = "field8063";
    static String field8064;
    static String field8065;
    static String field8066;
    static String field8067;
    static String field8068;
    static String field8069;
    static String field8070;
    static String field8071;

    public void method2060() {
        FramebufferBlit.field763 = true;
        RenderSystem.disableBlend();
        this.field8057.endWrite();
        ClientUtil.I.getFramebuffer().beginWrite(false);
    }

    public void method2061() {
        if (this.field8058) {
            return;
        }
        this.field8057 = new SimpleFramebuffer(ClientUtil.I.getWindow().getFramebufferWidth(), ClientUtil.I.getWindow().getFramebufferHeight(), false);
        this.I = new SimpleFramebuffer(ClientUtil.I.getWindow().getFramebufferWidth(), ClientUtil.I.getWindow().getFramebufferHeight(), false);
        this.field8061 = new PostEffectPipeline(Identifier.of((String)this.field8059, this.field8060 + ".json"));
        this.field8061.method2074(field8062, this.field8057);
        this.field8061.method2074(field8063, this.I);
        this.field8058 = true;
    }

    public void method2062() {
        this.field8061.method2074(field8064, this.field8057);
        this.field8061.method2074(field8065, this.I);
        this.method2063();
        this.field8061.method2084();
        RenderSystem.enableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ClientUtil.I.getFramebuffer().beginWrite(false);
        FramebufferBlit.method205(this.I, ClientUtil.I.getWindow().getFramebufferWidth(), ClientUtil.I.getWindow().getFramebufferHeight());
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        this.field8057.clear();
        this.I.clear();
        ClientUtil.I.getFramebuffer().beginWrite(false);
    }

    public ShaderEspRenderer(String param1) {
        this(field8066, param1);
    }

    public void method2063() {
        for (PostProcessPass v2 : this.field8061.field8079) {
            v2.field8041.method2092(field8067, () -> this.field8057.getColorAttachment());
            v2.field8041.method2090(field8068).set(RenderSystem.getShaderGameTime());
            v2.field8041.method2090(field8069).set(RenderSystem.getModelViewMatrix());
        }
    }

    public void method2064() {
        RenderSystem.enableBlend();
        this.field8057.beginWrite(false);
        FramebufferBlit.field763 = false;
    }

    public void method2065() {
        this.field8061.method2074(field8070, ClientUtil.I.getFramebuffer());
        this.field8061.method2074(field8071, ClientUtil.I.getFramebuffer());
        this.method2063();
        this.field8061.method2084();
    }

    public void method2067(Consumer<ShaderProgramLoader> param1) {
        for (PostProcessPass v3 : this.field8061.field8079) {
            param1.accept(v3.field8041);
        }
    }

    public void method2068(int param1, int param2) {
        this.field8057.resize(param1, param2);
        this.I.resize(param1, param2);
        this.field8061.method2076(param1, param2);
    }

    public ShaderEspRenderer(String param1, String param2) {
        this.field8059 = param1;
        this.field8060 = param2;
    }

    static {
        field8066 = "minced";
        cfr_renamed_161 = "\u0001.json";
        field8067 = "PrevSampler";
        field8068 = "GameTime";
        field8069 = "ModelViewMat";
        field8070 = "bufIn";
        field8071 = "bufOut";
        field8064 = "bufIn";
        field8065 = "bufOut";
    }
}



