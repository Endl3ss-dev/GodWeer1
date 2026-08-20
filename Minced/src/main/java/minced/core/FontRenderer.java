/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextColor
 *  org.joml.Matrix4f
 *  sg.ec.Тй$Ч
 */
package minced.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.Flushable;
import java.util.Locale;
import java.util.Map;
import minced.core.ColorUtil;
import minced.core.Shaders;
import minced.core.ScissorUtil;
import minced.core.AbstractFontRenderer;
import minced.core.Glyph;
import minced.gui.StyledTextGlyphs;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.joml.Matrix4f;
import sg.ec.tj;

public final class FontRenderer
extends AbstractFontRenderer {
    static int ШФ;

    static float _9;
    static float __;
    static String cfr_renamed_226 = "maxWidth";
    static String field9306 = "width";
    static float _8;
    static float field9307;
    static double field9308;
    static double field9309;
    static double field9310;
    static double _6;
    static double field9311;
    static float field9312;
    static float field9313;
    static float field9314;
    static float field9315;
    static float cfr_renamed_227;
    static float field9316;
    static float field9317;
    static float field9318;
    static float field9319;
    static float field9320;
    static float field9321;
    static double field9322;
    static float field9323;
    static float field9324;
    static float field9325;
    static float field9326;
    static String field9327;
    static String field9328;
    static float field9329;
    static float field9330;
    static float field9331;
    static float field9332;
    static float field9333;
    float field9334;
    static char[] field9335;
    // fontHeight/9297/9298/9299/9300/9301/9303 are inherited from AbstractFontRenderer; re-declaring them
    // here shadowed the parent's copies, so state written by this class (atlas texture id, sizes,
    // glyph map) was invisible to the parent's bind/UV/upload methods — text drew as black boxes.
    static int[] field9336;

    public void method2706(DrawContext param1, String param2, float param3, float param4, int param5, float param6) {
        float v7;
        if (param2 == null || param2.isEmpty()) {
            return;
        }
        float v8 = v7 = param3 * 2.0f;
        float v9 = (param4 -= _9) * 2.0f;
        float[] v10 = ColorUtil.method1849(param5);
        float v11 = v10[0];
        float v12 = v10[1];
        float v13 = v10[2];
        float v14 = v10[3];
        if (v14 <= __) {
            return;
        }
        GlStateManager._enableBlend();
        GlStateManager._blendFunc((int)770, (int)771);
        ShaderProgram v15 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8353);
        v15.getUniform(cfr_renamed_226).set(param6 * 2.0f);
        v15.getUniform(field9306).set((param3 + param6) * 2.0f);
        RenderSystem.setShaderTexture((int)0, (int)this.textureId);
        this.bindTexture();
        param1.getMatrices().push();
        param1.getMatrices().scale(_8, field9307, 1.0f);
        Matrix4f v16 = param1.getMatrices().peek().getPositionMatrix();
        BufferBuilder v17 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        int v18 = param2.length();
        for (int v19 = 0; v19 < v18; ++v19) {
            char v20 = param2.charAt(v19);
            v8 += this.renderGlyph(v17, v16, v20, v8, v9, v11, v12, v13, v14);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v17.end());
        param1.getMatrices().pop();
        this.unbindTexture();
        GlStateManager._disableBlend();
    }

    public void method2708(DrawContext param1, String param2, double param3, double param5, int param7, boolean param8, boolean param9, boolean param10, boolean param11, int param12) {
        if (param8) {
            FontRenderer.method2712(param1, this, param2, param3 - field9308, param5, param12);
        }
        if (param9) {
            FontRenderer.method2712(param1, this, param2, param3 + field9309, param5, param12);
        }
        if (param10) {
            FontRenderer.method2712(param1, this, param2, param3, param5 - field9310, param12);
        }
        if (param11) {
            FontRenderer.method2712(param1, this, param2, param3, param5 + _6, param12);
        }
        this.method2711(param1, param2, param3, param5, param7);
    }

    public float method2709(DrawContext param1, Text param2, double param3, double param5, int param7) {
        if (param2 == null) {
            return 0.0f;
        }
        float v8 = (float)param3 * 2.0f;
        float[] v9 = new float[]{v8};
        float v10 = (float)(param5 -= field9311) * 2.0f;
        GlStateManager._enableBlend();
        GlStateManager._blendFunc((int)770, (int)771);
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture((int)0, (int)this.textureId);
        this.bindTexture();
        param1.getMatrices().push();
        param1.getMatrices().scale(field9312, field9313, 1.0f);
        Matrix4f v11 = param1.getMatrices().peek().getPositionMatrix();
        BufferBuilder v12 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        int v13 = ColorUtil.cfr_renamed_0(param7);
        float v14 = (float)v13 / field9314;
        if (v14 <= field9315) {
            return 0.0f;
        }
        OrderedText v15 = param2.asOrderedText();
        v15.accept((p8, p9, p10) -> {
            String v132_text;
            int v202_font_id;
            int v11_color = param7;
            TextColor v12_textColor = p9.getColor();
            if (v12_textColor != null) {
                int v132_rgb = v12_textColor.getRgb();
                v11_color = ColorUtil.method1855(v132_rgb, v13);
            }
            if (StyledTextGlyphs.isCustomFontMarker(p10, p9) && p9.getFont() != null) {
                String v132_font_str = p9.getFont().toString();
                v202_font_id = StyledTextGlyphs.getFontColor(v132_font_str, v14);
                if (v202_font_id != -1) {
                    v11_color = v202_font_id;
                }
            }
            String v202_widget_text = StyledTextGlyphs.getGlyphText(p10);
            v132_text = StyledTextGlyphs.isCustomFontMarker(p10, p9) ? StyledTextGlyphs.getStyledFontAlias(p9) : (v202_widget_text != null ? v202_widget_text : new String(Character.toChars(p10)));
            if (v132_text == null || v132_text.isEmpty()) {
                return true;
            }
            if (v202_widget_text != null && !v202_widget_text.isEmpty()) {
                int v15_len = v132_text.length();
                for (int v16 = 0; v16 < v15_len; ++v16) {
                    char v17 = v132_text.charAt(v16);
                    int v18 = StyledTextGlyphs.getGlyphColor(p10, v16, v15_len, v14, v11_color);
                    float[] v19 = ColorUtil.method1849(v18);
                    v9[0] = v9[0] + this.renderGlyph(v12, v11, v17, v9[0], v10, v19[0], v19[1], v19[2], v19[3]);
                }
            } else {
                float[] v15_colors = ColorUtil.method1849(v11_color);
                for (int v16 = 0; v16 < v132_text.length(); ++v16) {
                    char v17 = v132_text.charAt(v16);
                    v9[0] = v9[0] + this.renderGlyph(v12, v11, v17, v9[0], v10, v15_colors[0], v15_colors[1], v15_colors[2], v15_colors[3]);
                }
            }
            return true;
        });
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v12.end());
        param1.getMatrices().pop();
        this.unbindTexture();
        GlStateManager._disableBlend();
        return (v9[0] - v8) / 2.0f;
    }

    public void method2710(DrawContext param1, String param2, float param3, float param4, float param5, int param6, boolean param7, tj.ch param8) {
        boolean v19;
        float v9 = this.method2719(param2);
        float v10 = this.method2718();
        ScissorUtil.method2621(param3, param4 - 1.0f, param5, v10 + cfr_renamed_227);
        long v11 = System.currentTimeMillis();
        float v13 = field9316;
        float v14 = v9 + field9317;
        float v15 = v14 / v13 * field9318;
        float v16 = field9319;
        float v17 = v15 + v16;
        float v18 = 0.0f;
        boolean bl = v19 = v9 > param5;
        if (v19) {
            if (param7 && !param8.cfr_renamed_228) {
                param8.cfr_renamed_0 = v11;
                param8.cfr_renamed_228 = true;
            }
            if (param8.cfr_renamed_228) {
                long v20 = v11 - param8.cfr_renamed_0;
                if ((float)v20 < v17) {
                    v18 = (float)v20 < v15 ? (float)v20 / field9320 * v13 : 0.0f;
                } else {
                    param8.cfr_renamed_228 = false;
                    v18 = 0.0f;
                }
            }
        }
        this.method2711(param1, param2, param3 - v18, param4, param6);
        if (v19 && v18 > 0.0f) {
            this.method2711(param1, param2, param3 - v18 + v9 + field9321, param4, param6);
        }
        ScissorUtil.method2620();
    }

    public float method2711(DrawContext param1, String param2, double param3, double param5, int param7) {
        return FontRenderer.method2712(param1, this, param2, param3, param5, param7);
    }

    private static float method2712(DrawContext param0, FontRenderer param1, String param2, double param3, double param5, int param7) {
        float v8;
        if (param2 == null || param2.isEmpty()) {
            return 0.0f;
        }
        float v9 = v8 = (float)param3 * 2.0f;
        float v10 = (float)(param5 -= field9322) * 2.0f;
        float[] v11 = ColorUtil.method1849(param7);
        float v12 = v11[0];
        float v13 = v11[1];
        float v14 = v11[2];
        float v15 = v11[3];
        if (v15 <= field9323) {
            return 0.0f;
        }
        GlStateManager._enableBlend();
        GlStateManager._blendFunc((int)770, (int)771);
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture((int)0, (int)param1.textureId);
        param1.bindTexture();
        param0.getMatrices().push();
        param0.getMatrices().scale(field9324, field9325, 1.0f);
        Matrix4f v16 = param0.getMatrices().peek().getPositionMatrix();
        BufferBuilder v17 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        int v18 = param2.length();
        for (int v19 = 0; v19 < v18; ++v19) {
            char v20 = param2.charAt(v19);
            v9 += param1.renderGlyph(v17, v16, v20, v9, v10, v12, v13, v14, v15);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v17.end());
        param0.getMatrices().pop();
        param1.unbindTexture();
        GlStateManager._disableBlend();
        return (v9 - v8) / 2.0f;
    }

    public void method2715(DrawContext param1, Text param2, float param3, float param4, int param5, float param6) {
        if (param2 == null) {
            return;
        }
        float v7 = param3 * 2.0f;
        float[] v8 = new float[]{v7};
        float v9 = (param4 -= field9326) * 2.0f;
        GlStateManager._enableBlend();
        GlStateManager._blendFunc((int)770, (int)771);
        ShaderProgram v10 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8353);
        v10.getUniform(field9327).set(param6 * 2.0f);
        v10.getUniform(field9328).set((param3 + param6) * 2.0f);
        RenderSystem.setShaderTexture((int)0, (int)this.textureId);
        this.bindTexture();
        param1.getMatrices().push();
        param1.getMatrices().scale(field9329, field9330, 1.0f);
        Matrix4f v11 = param1.getMatrices().peek().getPositionMatrix();
        BufferBuilder v12 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        int v13 = ColorUtil.cfr_renamed_0(param5);
        float v14 = (float)v13 / field9331;
        if (v14 <= field9332) {
            param1.getMatrices().pop();
            this.unbindTexture();
            GlStateManager._disableBlend();
            return;
        }
        OrderedText v15 = param2.asOrderedText();
        v15.accept((p8, p9, p10) -> {
            String v132_text;
            int v202_font_id;
            int v11_color = param5;
            TextColor v12_textColor = p9.getColor();
            if (v12_textColor != null) {
                int v132_rgb = v12_textColor.getRgb();
                v11_color = ColorUtil.method1855(v132_rgb, v13);
            }
            if (StyledTextGlyphs.isCustomFontMarker(p10, p9) && p9.getFont() != null) {
                String v132_font_str = p9.getFont().toString();
                v202_font_id = StyledTextGlyphs.getFontColor(v132_font_str, v14);
                if (v202_font_id != -1) {
                    v11_color = v202_font_id;
                }
            }
            String v202_widget_text = StyledTextGlyphs.getGlyphText(p10);
            v132_text = StyledTextGlyphs.isCustomFontMarker(p10, p9) ? StyledTextGlyphs.getStyledFontAlias(p9) : (v202_widget_text != null ? v202_widget_text : new String(Character.toChars(p10)));
            if (v132_text == null || v132_text.isEmpty()) {
                return true;
            }
            if (v202_widget_text != null && !v202_widget_text.isEmpty()) {
                int v15_len = v132_text.length();
                for (int v16 = 0; v16 < v15_len; ++v16) {
                    char v17 = v132_text.charAt(v16);
                    int v18 = StyledTextGlyphs.getGlyphColor(p10, v16, v15_len, v14, v11_color);
                    float[] v19 = ColorUtil.method1849(v18);
                    v8[0] = v8[0] + this.renderGlyph(v12, v11, v17, v8[0], v9, v19[0], v19[1], v19[2], v19[3]);
                }
            } else {
                float[] v15_colors = ColorUtil.method1849(v11_color);
                for (int v16 = 0; v16 < v132_text.length(); ++v16) {
                    char v17 = v132_text.charAt(v16);
                    v8[0] = v8[0] + this.renderGlyph(v12, v11, v17, v8[0], v9, v15_colors[0], v15_colors[1], v15_colors[2], v15_colors[3]);
                }
            }
            return true;
        });
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v12.end());
        param1.getMatrices().pop();
        this.unbindTexture();
        GlStateManager._disableBlend();
    }

    public float method2716(Text param1) {
        if (param1 == null) {
            return 0.0f;
        }
        float[] v2 = new float[]{0.0f};
        OrderedText v3 = param1.asOrderedText();
        v3.accept((param2, param3, param4) -> {
            String v6 = StyledTextGlyphs.getGlyphText(param4);
            String v5 = StyledTextGlyphs.isCustomFontMarker(param4, param3) ? StyledTextGlyphs.getStyledFontAlias(param3) : (v6 != null ? v6 : new String(Character.toChars(param4)));
            if (v5 == null || v5.isEmpty()) {
                return true;
            }
            for (int v7 = 0; v7 < v5.length(); ++v7) {
                char v8 = v5.charAt(v7);
                v2[0] = v2[0] + (this.getGlyphWidth(v8) + this.getCharacterSpacing());
            }
            return true;
        });
        return (v2[0] - this.getCharacterSpacing()) / 2.0f;
    }

    public float method2717(DrawContext param1, Text param2, double param3, double param5, int param7) {
        return this.method2709(param1, param2, param3, param5, param7);
    }

    public float method2718() {
        return this.getFontHeight() / 2.0f - field9333;
    }

    public FontRenderer(String param1, int param2, float param3, boolean param4) {
        Font v5 = AbstractFontRenderer.loadFont(param1, 0, param2);
        this.fontName = v5.getFontName(Locale.ENGLISH);
        this.field9334 = param3;
        this.antialias = param4;
        this.atlasWidth = 1024;
        this.atlasHeight = 1024;
        BufferedImage v6 = new BufferedImage(1024, 1024, 2);
        Graphics2D v7 = this.createGraphics(v6, v5);
        FontMetrics v8 = v7.getFontMetrics();
        this.fontHeight = v8.getHeight();
        int v9 = 0;
        int v10 = 0;
        for (char v15 : field9335) {
            Rectangle2D v16 = v8.getStringBounds(String.valueOf(v15), v7);
            int v17 = v16.getBounds().width;
            int v18 = v16.getBounds().height;
            int v19 = v17 + 4;
            int v20 = v18 + 4;
            if (v9 + v19 >= 1024) {
                v9 = 0;
                v10 += v20;
            }
            Glyph v21 = new Glyph();
            v21.ы = v17;
            v21.Ю = v18;
            v21.х = v9 + 2;
            v21.ъ = v10 + 2;
            v7.drawString(String.valueOf(v15), v9 + 2, v10 + 2 + v8.getAscent());
            this.glyphs.put(Character.valueOf(v15), v21);
            v9 += v19;
        }
        BufferedImage bufferedImage = v6;
        RenderSystem.recordRenderCall(() -> this.uploadTexture(bufferedImage));
        v7.dispose();
    }

    public float method2719(String param1) {
        if (param1 == null || param1.isEmpty()) {
            return 0.0f;
        }
        float v2 = 0.0f;
        int v3 = 0;
        while (v3 < param1.length()) {
            char[] v62;
            int v4 = param1.codePointAt(v3);
            v3 += Character.charCount(v4);
            String v5 = StyledTextGlyphs.getGlyphText(v4);
            if (v5 != null) {
                if (v5.isEmpty()) continue;
                for (int v62_idx = 0; v62_idx < v5.length(); ++v62_idx) {
                    char v7 = v5.charAt(v62_idx);
                    v2 += this.getGlyphWidth(v7) + this.getCharacterSpacing();
                }
                continue;
            }
            for (char v10 : v62 = Character.toChars(v4)) {
                v2 += this.getGlyphWidth(v10) + this.getCharacterSpacing();
            }
        }
        return (v2 - this.getCharacterSpacing()) / 2.0f;
    }

    @Override
    public float getCharacterSpacing() {
        return this.field9334;
    }

    public static void method2720() {
        int v2;
        field9336 = new int[]{31, 256, 1024, 1106, 9728, 9984, 9632, 9728};
        int v0 = field9336[1] - field9336[0] + (field9336[3] - field9336[2]) + (field9336[5] - field9336[4]) + (field9336[7] - field9336[6]);
        field9335 = new char[v0];
        int v1 = 0;
        for (v2 = field9336[0]; v2 < field9336[1]; ++v2) {
            FontRenderer.field9335[v1++] = (char)v2;
        }
        for (v2 = field9336[2]; v2 < field9336[3]; ++v2) {
            FontRenderer.field9335[v1++] = (char)v2;
        }
        for (v2 = field9336[4]; v2 < field9336[5]; ++v2) {
            FontRenderer.field9335[v1++] = (char)v2;
        }
        for (v2 = field9336[6]; v2 < field9336[7]; ++v2) {
            FontRenderer.field9335[v1++] = (char)v2;
        }
    }

    static {
        ШФ = 0;
        field9308 = 0.5;
        field9309 = 0.5;
        field9310 = 0.5;
        _6 = 0.5;
        _9 = 3.0f;
        __ = 0.05f;
        _8 = 0.5f;
        field9307 = 0.5f;
        field9326 = 3.0f;
        field9327 = "width";
        field9328 = "maxWidth";
        field9329 = 0.5f;
        field9330 = 0.5f;
        field9331 = 255.0f;
        field9332 = 0.05f;
        cfr_renamed_227 = 3.0f;
        field9316 = 40.0f;
        field9317 = 10.0f;
        field9318 = 1000.0f;
        field9319 = 600.0f;
        field9320 = 1000.0f;
        field9321 = 10.0f;
        field9322 = 3.0;
        field9323 = 0.05f;
        field9324 = 0.5f;
        field9325 = 0.5f;
        field9311 = 3.0;
        field9312 = 0.5f;
        field9313 = 0.5f;
        field9314 = 255.0f;
        field9315 = 0.05f;
        field9333 = 3.0f;
        FontRenderer.method2720();
    }
}



