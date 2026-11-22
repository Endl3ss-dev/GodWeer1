/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 */
package ru.decide.utils.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_276;
import net.minecraft.class_310;
import ru.decide.utils.render.ShaderEspOutlinePipeline;

@Environment(value=EnvType.CLIENT)
public class ShaderEspRenderer {
    private ShaderEspOutlinePipeline outlinePipeline;
    public final ShaderEspOutlinePipeline.Params params = new ShaderEspOutlinePipeline.Params();
    private static ShaderEspRenderer instance;

    public ShaderEspRenderer() {
        instance = this;
    }

    public static ShaderEspRenderer getInstance() {
        if (instance == null) {
            instance = new ShaderEspRenderer();
        }
        return instance;
    }

    private void ensureInitialized() {
        if (this.outlinePipeline == null) {
            this.outlinePipeline = new ShaderEspOutlinePipeline();
        }
    }

    public void renderFromFbo(class_276 maskFbo, int width, int height) {
        if (maskFbo == null) {
            return;
        }
        this.ensureInitialized();
        GpuTextureView maskView = maskFbo.method_71639();
        if (maskView == null) {
            return;
        }
        class_276 fb = class_310.method_1551().method_1522();
        if (fb == null || fb.method_71639() == null) {
            return;
        }
        this.outlinePipeline.render(maskView, fb.method_71639(), width, height, this.params);
    }

    public void invalidate() {
        if (this.outlinePipeline != null) {
            this.outlinePipeline.close();
            this.outlinePipeline = null;
        }
    }

    public void close() {
        this.invalidate();
    }
}

