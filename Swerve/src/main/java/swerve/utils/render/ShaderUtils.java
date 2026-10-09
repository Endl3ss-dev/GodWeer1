package swerve.utils.render;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import swerve.Swerve;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.client.renderer.ShaderProgram;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ShaderUtils {

    private static final Set<String> FAILED = ConcurrentHashMap.newKeySet();

    public static final ShaderProgram ROUNDED_RECT = register("rect", "rounded_rect", DefaultVertexFormat.POSITION_COLOR);
    public static final ShaderProgram ROUNDED_RECT_OUTLINE = register("rect", "rounded_rect_outline", DefaultVertexFormat.POSITION_COLOR);
    public static final ShaderProgram RING_ARC = register("ring_arc", "ring_arc", DefaultVertexFormat.POSITION_COLOR);
    public static final ShaderProgram ROUNDED_TEXTURE = register("texture", "texture_rect", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram LIQUID_GLASS = register("liquidglass", "liquid", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram KAWASE_DOWN = register("kawase_down", "kawase_down", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram KAWASE_UP = register("kawase_up", "kawase_up", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram GRADIENT_RECT = register("gradient_rect", "gradient", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram GRADIENT6_RECT = register("gradient6", "gradient", DefaultVertexFormat.POSITION_COLOR);
    public static final ShaderProgram SHADOW_RECT = register("shadow_rect", "shadow", DefaultVertexFormat.POSITION_COLOR);
    public static final ShaderProgram SHADOW6_RECT = register("shadow6", "shadow", DefaultVertexFormat.POSITION_COLOR);
    public static final ShaderProgram FONTS_MSDF = register("fonts", "fonts", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram FACE = register("face", "face", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram SCAN_EFFECT = register("sonar", "scan_effect", DefaultVertexFormat.POSITION_TEX);

    public static final ShaderProgram BLOCK_OVERLAY = register("blockoverlay", "block_overlay", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram BLOCK_OVERLAY_BALATRO = register("blockoverlay", "block_overlay_balatro", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram BLOCK_OVERLAY_PLASMA = register("blockoverlay", "block_overlay_plasma", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram BLOCK_OVERLAY_AURORA = register("blockoverlay", "block_overlay_aurora", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram BLOCK_OVERLAY_WARP = register("blockoverlay", "block_overlay_warp", DefaultVertexFormat.POSITION_TEX_COLOR);

    public static final ShaderProgram WORLD_SHADER = register("worldshader", "world_shader", DefaultVertexFormat.POSITION_TEX_COLOR);

    public static final ShaderProgram SKY_SUMMER = register("skyshader", "sky_summer", DefaultVertexFormat.POSITION);
    public static final ShaderProgram SKY_WATER = register("skyshader", "sky_water", DefaultVertexFormat.POSITION);
    public static final ShaderProgram SKY_OCEAN = register("skyshader", "sky_ocean", DefaultVertexFormat.POSITION);
    public static final ShaderProgram SKY_CAUSTIC = register("skyshader", "caustic", DefaultVertexFormat.POSITION);
    public static final ShaderProgram SKY_PLASMA = register("skyshader", "sky_plasma", DefaultVertexFormat.POSITION);
    public static final ShaderProgram SKY_SHADER = register("skyshader", "sky_shader", DefaultVertexFormat.POSITION);

    public static final ShaderProgram CHAMS_FILL = register("chams", "chams_fill", DefaultVertexFormat.POSITION_TEX_COLOR);

    public static final ShaderProgram HANDS_MASK_DIFF = register("hands", "hands_mask_diff", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram HANDS_OVERLAY = register("hands", "hands_overlay", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram HANDS_GLOW = register("hands", "hands_glow", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram HANDS_KAWASE_DOWN = register("hands", "hands_kawase_down", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram HANDS_KAWASE_UP = register("hands", "hands_kawase_up", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram HANDS_SMOKE = register("hands", "hands_smoke", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram HANDS_ITEM_MOTION_TRAIL = register("hands", "hands_item_motion_trail", DefaultVertexFormat.POSITION_TEX_COLOR);

    public static final ShaderProgram ESP_GLOW = register("shaderesp", "glow", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram ESP_FILL = register("shaderesp", "fill", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram TRAIL = register("trail", "trail", DefaultVertexFormat.POSITION_TEX_COLOR);
    public static final ShaderProgram CHINA_HAT = register("chinahat", "chinahat", DefaultVertexFormat.POSITION_TEX_COLOR);

    private ShaderUtils() {
    }

    public static ShaderProgram register(String directory, String name, VertexFormat format) {
        return new ShaderProgram(Swerve.id("core/" + directory + "/" + name), format, ShaderDefines.EMPTY);
    }

    public static CompiledShaderProgram get(ShaderProgram program) {
        try {
            return Minecraft.getInstance().getShaderManager().getProgram(program);
        } catch (Throwable t) {
            if (FAILED.add(program.configId().toString())) {
                Swerve.LOGGER.error("Failed to compile shader '{}'", program.configId(), t);
            }
            return null;
        }
    }

    public static CompiledShaderProgram bind(ShaderProgram program) {
        return RenderSystem.setShader(program);
    }

    public static void set(CompiledShaderProgram shader, String name, int value) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(value);
        }
    }

    public static void set(CompiledShaderProgram shader, String name, float value) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(value);
        }
    }

    public static void set(CompiledShaderProgram shader, String name, float x, float y) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(x, y);
        }
    }

    public static void set(CompiledShaderProgram shader, String name, float x, float y, float z) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(x, y, z);
        }
    }

    public static void set(CompiledShaderProgram shader, String name, float x, float y, float z, float w) {
        Uniform uniform = shader.getUniform(name);
        if (uniform != null) {
            uniform.set(x, y, z, w);
        }
    }

    public static void setColor(CompiledShaderProgram shader, String name, int argb) {
        Uniform uniform = shader.getUniform(name);
        if (uniform == null) {
            return;
        }

        int alpha = (argb >>> 24) & 0xFF;
        uniform.set(
                ((argb >> 16) & 0xFF) / 255.0f,
                ((argb >> 8) & 0xFF) / 255.0f,
                (argb & 0xFF) / 255.0f,
                alpha / 255.0f
        );
    }
}
