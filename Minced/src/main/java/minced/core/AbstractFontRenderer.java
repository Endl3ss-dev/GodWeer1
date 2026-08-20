/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  net.minecraft.client.render.BufferBuilder
 *  org.joml.Matrix4f
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL30
 */
package minced.core;

import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.Flushable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import minced.core.Fonts;
import minced.core.Glyph;
import net.minecraft.client.render.BufferBuilder;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

public abstract class AbstractFontRenderer
 {
    float fontHeight;
    Map glyphs = new HashMap();
    boolean antialias;
    int atlasWidth;
    int atlasHeight;
    int textureId;
    static int RGBA8_INTERNAL_FORMAT;
    String fontName;
    static String FONT_RESOURCE_ROOT;

    public float getFontHeight() {
        return this.fontHeight;
    }

    public final void unbindTexture() {
        GlStateManager._bindTexture((int)0);
    }

    public abstract float getCharacterSpacing();

    public boolean isAntialias() {
        return this.antialias;
    }

    public float renderGlyph(BufferBuilder param1, Matrix4f param2, char param3, float param4, float param5, float param6, float param7, float param8, float param9) {
        Glyph v10 = (Glyph)this.glyphs.get(Character.valueOf(param3));
        if (v10 == null) {
            return 0.0f;
        }
        float v11 = (float)v10.х / (float)this.atlasWidth;
        float v12 = (float)v10.ъ / (float)this.atlasHeight;
        float v13 = (float)v10.ы / (float)this.atlasWidth;
        float v14 = (float)v10.Ю / (float)this.atlasHeight;
        float v15 = v10.ы;
        float v16 = v10.Ю;
        param1.vertex(param2, param4, param5 + v16, 0.0f).color(param6, param7, param8, param9).texture(v11, v12 + v14);
        param1.vertex(param2, param4 + v15, param5 + v16, 0.0f).color(param6, param7, param8, param9).texture(v11 + v13, v12 + v14);
        param1.vertex(param2, param4 + v15, param5, 0.0f).color(param6, param7, param8, param9).texture(v11 + v13, v12);
        param1.vertex(param2, param4, param5, 0.0f).color(param6, param7, param8, param9).texture(v11, v12);
        return v15 + this.getCharacterSpacing();
    }

    public float getGlyphWidth(char param1) {
        Glyph v2 = (Glyph)this.glyphs.get(Character.valueOf(param1));
        return v2 != null ? (float)v2.ы : 0.0f;
    }

    public int getAtlasWidth() {
        return this.atlasWidth;
    }

    public final void bindTexture() {
        GlStateManager._bindTexture((int)this.textureId);
    }

    public int getAtlasHeight() {
        return this.atlasHeight;
    }

    protected final void uploadTexture(BufferedImage param1) {
        int[] v2 = param1.getRGB(0, 0, param1.getWidth(), param1.getHeight(), null, 0, param1.getWidth());
        ByteBuffer v3 = BufferUtils.createByteBuffer((int)(v2.length * 4));
        int v5 = v2.length;
        for (int v6 = 0; v6 < v5; ++v6) {
            int v7 = v2[v6];
            v3.put((byte)(v7 >> 16 & 0xFF));
            v3.put((byte)(v7 >> 8 & 0xFF));
            v3.put((byte)(v7 & 0xFF));
            v3.put((byte)(v7 >> 24 & 0xFF));
        }
        v3.flip();
        int v4 = GlStateManager._genTexture();
        GlStateManager._bindTexture(v4);
        GlStateManager._texParameter((int)3553, (int)10241, (int)9728);
        GlStateManager._texParameter((int)3553, (int)10240, (int)9728);
        GL30.glTexImage2D((int)3553, (int)0, (int)RGBA8_INTERNAL_FORMAT, (int)param1.getWidth(), (int)param1.getHeight(), (int)0, (int)6408, (int)5121, (ByteBuffer)v3);
        GlStateManager._bindTexture((int)0);
        this.textureId = v4;
    }

    public String getFontName() {
        return this.fontName;
    }

    public Map getGlyphs() {
        return this.glyphs;
    }

    public int getTextureId() {
        return this.textureId;
    }

    public final Graphics2D createGraphics(BufferedImage param1, Font param2) {
        Graphics2D v3 = param1.createGraphics();
        v3.setFont(param2);
        v3.setColor(new Color(255, 255, 255, 0));
        v3.fillRect(0, 0, this.atlasWidth, this.atlasHeight);
        v3.setColor(Color.WHITE);
        if (this.antialias) {
            v3.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            v3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            v3.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        return v3;
    }

    public static Font loadFont(String param0, int param1, int param2) {
        String v3 = FONT_RESOURCE_ROOT.concat(param0);
        try {
            Font v4 = Font.createFont(0, Objects.requireNonNull(Fonts.class.getResourceAsStream(v3))).deriveFont(param1, param2);
            return v4;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    static {
        RGBA8_INTERNAL_FORMAT = 32856;
        FONT_RESOURCE_ROOT = "/assets/minced/fonts/";
    }
}



