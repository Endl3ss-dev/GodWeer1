package never.lose.utils.render;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class RenderConst {

    private RenderConst() {}

    public static ShaderProgram TEXTURE_SHADER;
    public static ShaderProgram MSDF_FONT_SHADER;
    public static ShaderProgram RECTANGLE_SHADER;
    public static ShaderProgram SMOKE_SHADER;
    public static ShaderProgram BLUR_SHADER;
    public static ShaderProgram CIRCLE_SHADER;
    public static ShaderProgram BORDER_SHADER;
    public static ShaderProgram GLOWESP_SHADER;
    public static ShaderProgram PLASMA_SKY_SHADER;

    public static void register() {
        CoreShaderRegistrationCallback.EVENT.register(ctx -> {
            ctx.register(Identifier.of("n2ver", "rectangle"), VertexFormats.POSITION_COLOR,          p -> RECTANGLE_SHADER   = p);
            ctx.register(Identifier.of("n2ver", "border"),    VertexFormats.POSITION_COLOR,          p -> BORDER_SHADER      = p);
            ctx.register(Identifier.of("n2ver", "blur"),      VertexFormats.POSITION_COLOR,          p -> BLUR_SHADER        = p);
            ctx.register(Identifier.of("n2ver", "arc"),       VertexFormats.POSITION_COLOR,          p -> CIRCLE_SHADER      = p);
            ctx.register(Identifier.of("n2ver", "smoke"),     VertexFormats.POSITION_COLOR,          p -> SMOKE_SHADER       = p);
            ctx.register(Identifier.of("n2ver", "glowesp"),   VertexFormats.POSITION_COLOR,          p -> GLOWESP_SHADER     = p);
            ctx.register(Identifier.of("n2ver", "texture"),   VertexFormats.POSITION_TEXTURE_COLOR,  p -> TEXTURE_SHADER     = p);
            ctx.register(Identifier.of("n2ver", "msdf_font"), VertexFormats.POSITION_TEXTURE_COLOR,  p -> MSDF_FONT_SHADER   = p);
            ctx.register(Identifier.of("n2ver", "sky_plasma"), VertexFormats.POSITION_COLOR,         p -> PLASMA_SKY_SHADER  = p);
        });
    }
}