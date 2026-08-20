/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  it.unimi.dsi.fastutil.objects.ObjectIterable
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  it.unimi.dsi.fastutil.objects.ObjectSortedSet
 *  net.minecraft.client.gl.Defines
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.render.VertexFormat
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.util.Identifier
 */
package minced.core;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.SequencedSet;
import java.util.Set;
import java.util.SortedSet;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class Shaders {
    public static List field8338;
    public static String field8339 = "light_kawase_up";
    public static ShaderProgramKey field8340;
    public static String field8341 = "light_kawase_down";
    public static ShaderProgramKey field8342;
    public static String field8343 = "rounded_rectangle";
    public static ShaderProgramKey field8344;
    public static String field8345 = "outline";
    public static ShaderProgramKey I;
    public static String field8346 = "blurred_round_rectangle";
    public static ShaderProgramKey field8347;
    public static String field8348 = "rounded_rectangle_gradient";
    public static ShaderProgramKey field8349;
    public static String field8350 = "rounded_rectangle_gradient_glowed";
    public static ShaderProgramKey field8351;
    public static String field8352 = "substring";
    public static ShaderProgramKey field8353;
    public static String field8354 = "rounded_head_texture";
    public static ShaderProgramKey field8355;
    public static String field8356 = "logo";
    public static ShaderProgramKey field8357;
    public static String field8358;
    public static ShaderProgramKey field8359;
    public static String field8360;
    public static ShaderProgramKey field8361;
    public static String field8362;
    public static ShaderProgramKey field8363;
    public static String field8364;
    public static ShaderProgramKey field8365;
    public static String field8366;
    public static ShaderProgramKey field8367;
    public static String field8368 = "block_overlay_sky_plasma";
    public static ShaderProgramKey field8369;
    public static String field8370;
    public static ShaderProgramKey field8371;
    public static String field8372;
    public static ShaderProgramKey field8373;
    public static String field8374;
    public static ShaderProgramKey field8375;
    public static String field8376;
    public static ShaderProgramKey field8377;
    public static String field8378;
    public static ShaderProgramKey field8379;
    public static String field8380;
    public static ShaderProgramKey field8381;
    public static String field8382;
    public static ShaderProgramKey field8383;
    public static String field8384;
    public static ShaderProgramKey field8385;
    public static String field8386;
    public static ShaderProgramKey field8387;
    public static String field8388;
    public static ShaderProgramKey field8389;
    public static String field8390;
    public static ShaderProgramKey field8391;
    // Namespace for Identifier.of(namespace, shaderName) - all shader assets live under
    // assets/minced/shaders/, so without this every ShaderProgramKey resolves to a missing file.
    public static String field8392 = "minced";

    public static void method2263() {
        field8338 = new ArrayList();
        field8340 = Shaders.method2265(field8339, VertexFormats.POSITION_TEXTURE);
        field8342 = Shaders.method2265(field8341, VertexFormats.POSITION_TEXTURE);
        field8344 = Shaders.method2265(field8343, VertexFormats.POSITION_COLOR);
        I = Shaders.method2265(field8345, VertexFormats.POSITION_COLOR);
        field8347 = Shaders.method2265(field8346, VertexFormats.POSITION_COLOR);
        field8349 = Shaders.method2265(field8348, VertexFormats.POSITION_COLOR);
        field8351 = Shaders.method2265(field8350, VertexFormats.POSITION_COLOR);
        field8353 = Shaders.method2265(field8352, VertexFormats.POSITION_TEXTURE_COLOR);
        field8355 = Shaders.method2265(field8354, VertexFormats.POSITION_TEXTURE_COLOR);
        field8357 = Shaders.method2265(field8356, VertexFormats.POSITION_TEXTURE_COLOR);
        field8359 = Shaders.method2265(field8358, VertexFormats.POSITION_COLOR);
        field8361 = Shaders.method2265(field8360, VertexFormats.POSITION);
        field8363 = Shaders.method2265(field8362, VertexFormats.POSITION);
        field8365 = Shaders.method2265(field8364, VertexFormats.POSITION);
        field8367 = Shaders.method2265(field8366, VertexFormats.POSITION);
        field8369 = Shaders.method2265(field8368, VertexFormats.POSITION_COLOR);
        field8371 = Shaders.method2265(field8370, VertexFormats.POSITION_COLOR);
        field8373 = Shaders.method2265(field8372, VertexFormats.POSITION_TEXTURE);
        field8375 = Shaders.method2265(field8374, VertexFormats.POSITION_TEXTURE_COLOR);
        field8377 = Shaders.method2265(field8376, VertexFormats.POSITION_TEXTURE_COLOR);
        field8379 = Shaders.method2265(field8378, VertexFormats.POSITION_TEXTURE_COLOR);
        field8381 = Shaders.method2265(field8380, VertexFormats.POSITION_TEXTURE_COLOR);
        field8383 = Shaders.method2265(field8382, VertexFormats.POSITION_TEXTURE_COLOR);
        field8385 = Shaders.method2265(field8384, VertexFormats.POSITION_TEXTURE_COLOR);
        field8387 = Shaders.method2265(field8386, VertexFormats.POSITION_TEXTURE_COLOR);
        field8389 = Shaders.method2265(field8388, VertexFormats.POSITION_TEXTURE_COLOR);
        field8391 = Shaders.method2265(field8390, VertexFormats.POSITION_TEXTURE_COLOR);
    }

    private static ShaderProgramKey method2264(String param0, VertexFormat param1, Defines param2) {
        ShaderProgramKey v3 = new ShaderProgramKey(Identifier.of((String)field8392, (String)param0), param1, param2);
        field8338.add(v3);
        return v3;
    }

    private static ShaderProgramKey method2265(String param0, VertexFormat param1) {
        return Shaders.method2264(param0, param1, Defines.EMPTY);
    }

    static {
        field8358 = "block_highlight";
        field8360 = "space_sky";
        field8362 = "sky_plasma";
        field8364 = "sky_balatro";
        field8366 = "sky_summer";
        field8370 = "block_overlay_sky_balatro";
        field8372 = "pentagram";
        field8374 = "core/hands/hands_mask_diff";
        field8376 = "core/hands/hands_overlay";
        field8378 = "core/hands/hands_glow";
        field8380 = "core/hands/hands_kawase_down";
        field8382 = "core/hands/hands_kawase_up";
        field8384 = "core/hands/hands_pretty";
        field8386 = "core/hands/hands_trail_displace";
        field8388 = "core/hands/hands_trail_glow";
        field8390 = "core/hands/hands_pick";
        Shaders.method2263();
    }
}



