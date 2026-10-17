package swerve.ui.menu;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import swerve.Swerve;
import swerve.utils.render.ShaderUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;

import org.joml.Matrix4f;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * MSDF-текст меню (порт Arcanic Visuals, GPL-3.0).
 *
 * <p>Метрики взяты напрямую из json-атласа (advance в em), как в оригинале: привычные
 * шрифты Swerve используют другую конвенцию шага, и вёрстка меню от неё «поехала» бы.
 * Глифы рисуются шейдером core/fonts/fonts, те же атласы, что и в Arcanic.
 */
public final class MenuText {

    private MenuText() {
    }

    /** Начертания меню: те же три MSDF-атласа, что и в Arcanic. */
    public enum Weight {
        MEDIUM("arcmedium"),
        SEMIBOLD("arcsemibold"),
        BOLD("arcbold");

        private final String file;

        Weight(String file) {
            this.file = file;
        }
    }

    /** Один глиф: advance, плоскость (em, y вверх) и рамка в атласе (0..1, y вниз). */
    private record Glyph(
            float advance, float l, float b, float r, float t,
            float u0, float v0, float u1, float v1, boolean ink) {
    }

    private static final class Face {
        final ResourceLocation atlas;
        final Int2ObjectMap<Glyph> glyphs = new Int2ObjectOpenHashMap<>();
        float width;
        float height;
        float range;

        Face(String name) {
            atlas = Swerve.id("textures/font/msdf/" + name + ".png");
            try (var in = MenuText.class.getResourceAsStream("/assets/swerve/menu/msdf/" + name + ".json")) {
                if (in == null) {
                    throw new IllegalStateException("MSDF font " + name + " not found");
                }
                JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                        .getAsJsonObject();
                JsonObject a = root.getAsJsonObject("atlas");
                width = a.get("width").getAsFloat();
                height = a.get("height").getAsFloat();
                range = a.get("distanceRange").getAsFloat();
                for (var e : root.getAsJsonArray("glyphs")) {
                    JsonObject o = e.getAsJsonObject();
                    float adv = o.get("advance").getAsFloat();
                    if (!o.has("planeBounds") || !o.has("atlasBounds")) {
                        glyphs.put(o.get("unicode").getAsInt(), new Glyph(adv, 0, 0, 0, 0, 0, 0, 0, 0, false));
                        continue;
                    }
                    JsonObject p = o.getAsJsonObject("planeBounds");
                    JsonObject q = o.getAsJsonObject("atlasBounds");
                    glyphs.put(
                            o.get("unicode").getAsInt(),
                            new Glyph(
                                    adv,
                                    p.get("left").getAsFloat(),
                                    p.get("bottom").getAsFloat(),
                                    p.get("right").getAsFloat(),
                                    p.get("top").getAsFloat(),
                                    q.get("left").getAsFloat() / width,
                                    1 - q.get("top").getAsFloat() / height,
                                    q.get("right").getAsFloat() / width,
                                    1 - q.get("bottom").getAsFloat() / height,
                                    true));
                }
            } catch (Exception ex) {
                throw new IllegalStateException("MSDF font " + name, ex);
            }
        }
    }

    private static final Face[] FACES = new Face[Weight.values().length];

    private static Face face(Weight weight) {
        int i = weight.ordinal();
        Face f = FACES[i];
        if (f == null) {
            FACES[i] = f = new Face(weight.file);
        }
        return f;
    }

    /** Ширина строки в пикселях (advance из атласа, em -> px). */
    public static float width(String s, Weight weight, int px) {
        if (s == null || s.isEmpty()) {
            return 0f;
        }
        Face f = face(weight);
        float sum = 0f;
        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            i += Character.charCount(cp);
            Glyph g = f.glyphs.get(cp);
            sum += g != null ? g.advance : .5f;
        }
        return sum * px;
    }

    /**
     * Рисует строку базовой линией в {@code baseline}.
     *
     * <p>Перед отрисовкой буфер GuiGraphics сбрасывается: в 1.21.4 GUI рисуется сразу,
     * а MSDF-глифы идут отдельным вызовом — без сброса текст оказался бы под заливками.
     */
    public static void draw(GuiGraphics g, String s, int x, int baseline, Weight weight, int px, int colour) {
        if (s == null || s.isEmpty() || (colour >>> 24) == 0) {
            return;
        }
        Face f = face(weight);
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(f.atlas);
        if (texture == null) {
            return;
        }
        texture.setFilter(true, false);

        var shader = ShaderUtils.get(ShaderUtils.FONTS_MSDF);
        if (shader == null) {
            return;
        }

        g.flush();

        Matrix4f matrix = g.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        float pen = x;
        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            i += Character.charCount(cp);
            Glyph gl = f.glyphs.get(cp);
            if (gl == null) {
                pen += .5f * px;
                continue;
            }
            if (gl.ink()) {
                float x0 = pen + gl.l() * px;
                float x1 = pen + gl.r() * px;
                float y0 = baseline - gl.t() * px;
                float y1 = baseline - gl.b() * px;
                buffer.addVertex(matrix, x0, y0, 0f).setUv(gl.u0(), gl.v0()).setColor(colour);
                buffer.addVertex(matrix, x0, y1, 0f).setUv(gl.u0(), gl.v1()).setColor(colour);
                buffer.addVertex(matrix, x1, y1, 0f).setUv(gl.u1(), gl.v1()).setColor(colour);
                buffer.addVertex(matrix, x1, y0, 0f).setUv(gl.u1(), gl.v0()).setColor(colour);
            }
            pen += gl.advance() * px;
        }

        MeshData mesh = buffer.build();
        if (mesh == null) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        setUniform(shader, "TextureSize", f.width, f.height);
        setUniform(shader, "Range", f.range);
        setUniform(shader, "Thickness", 0f);
        setUniform(shader, "EdgeStrength", .5f);
        setUniform(shader, "Color", 1f, 1f, 1f, 1f);
        setUniform(shader, "OutlineThickness", 0f);

        RenderSystem.setShaderTexture(0, texture.getId());
        RenderSystem.setShader(ShaderUtils.FONTS_MSDF);
        BufferUploader.drawWithShader(mesh);

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void setUniform(net.minecraft.client.renderer.CompiledShaderProgram shader, String name, float value) {
        var uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(value);
        }
    }

    private static void setUniform(net.minecraft.client.renderer.CompiledShaderProgram shader, String name,
                                   float x, float y) {
        var uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(x, y);
        }
    }

    private static void setUniform(net.minecraft.client.renderer.CompiledShaderProgram shader, String name,
                                   float x, float y, float z, float w) {
        var uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(x, y, z, w);
        }
    }
}
