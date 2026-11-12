/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1058
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL14
 */
package ru.decide.utils.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1058;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import ru.decide.utils.render.BlurPipeline;
import ru.decide.utils.render.CircleProgressPipeline;
import ru.decide.utils.render.DrawBatcher;
import ru.decide.utils.render.OutlinePipeline;
import ru.decide.utils.render.RectPipeline;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.ScreenBlur;
import ru.decide.utils.render.TexturePipeline;
import ru.decide.utils.render.font.FontRenderer;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class Render2D {
    private final FontRenderer fontRenderer = new FontRenderer();
    private final RectPipeline rectPipeline = new RectPipeline();
    private final CircleProgressPipeline circleProgressPipeline = new CircleProgressPipeline();
    private final OutlinePipeline outlinePipeline = new OutlinePipeline();
    private final TexturePipeline texturePipeline;
    private final BlurPipeline blurPipeline = new BlurPipeline();
    private boolean fontsLoaded = false;
    private static boolean inOverlayMode = false;
    private static boolean savedDepthTest = false;
    private static boolean savedDepthMask = false;
    private static boolean savedBlend = false;

    public Render2D() {
        this.texturePipeline = new TexturePipeline();
    }

    private void ensureFontsLoaded() {
        if (this.fontsLoaded) {
            return;
        }
        this.fontsLoaded = true;
        this.fontRenderer.loadAllFonts(Fonts.getRegistry());
    }

    public TexturePipeline getTexturePipeline() {
        return this.texturePipeline;
    }

    public BlurPipeline getBlurPipeline() {
        return this.blurPipeline;
    }

    public FontRenderer getFontRenderer() {
        this.ensureFontsLoaded();
        return this.fontRenderer;
    }

    public void flushAll() {
        DrawBatcher.flushPending();
        this.blurPipeline.flush();
        this.rectPipeline.flush();
        this.circleProgressPipeline.flush();
        this.fontRenderer.flush();
    }

    public void close() {
        this.rectPipeline.close();
        this.circleProgressPipeline.close();
        this.fontRenderer.close();
        this.texturePipeline.close();
        this.blurPipeline.close();
        this.outlinePipeline.close();
        ScreenBlur.close();
    }

    public OutlinePipeline getOutlinePipeline() {
        return this.outlinePipeline;
    }

    public RectPipeline getRectPipeline() {
        return this.rectPipeline;
    }

    public CircleProgressPipeline getCircleProgressPipeline() {
        return this.circleProgressPipeline;
    }

    public static void beginOverlay() {
        inOverlayMode = true;
        DrawBatcher.setEnabled(true);
        savedDepthTest = GL11.glIsEnabled((int)2929);
        savedDepthMask = GL11.glGetBoolean((int)2930);
        savedBlend = GL11.glIsEnabled((int)3042);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)3042);
        GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
    }

    public static void endOverlay() {
        DrawBatcher.setEnabled(false);
        if (savedDepthMask) {
            GL11.glDepthMask((boolean)true);
        }
        if (savedDepthTest) {
            GL11.glEnable((int)2929);
        } else {
            GL11.glDisable((int)2929);
        }
        if (!savedBlend) {
            GL11.glDisable((int)3042);
        }
        inOverlayMode = false;
    }

    public static void drawSprite(class_1058 sprite, float x, float y, float width, float height, int color, boolean pixelPerfect) {
        if (sprite == null || width == 0.0f || height == 0.0f) {
            return;
        }
        float smoothness = pixelPerfect ? 1.0f : 0.0f;
        RenderUtil.Images.texture(sprite.method_45852(), x, y, width, height, sprite.method_4594(), sprite.method_4593(), sprite.method_4577(), sprite.method_4575(), color, smoothness, 0.0f);
    }
}

