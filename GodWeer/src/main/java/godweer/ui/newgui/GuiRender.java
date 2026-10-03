package godweer.ui.newgui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import godweer.GodWeer;
import godweer.utils.color.ColorUtils;
import godweer.utils.render.RenderUtils;
import godweer.utils.render.ShaderUtils;

import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.ShaderProgram;

import org.joml.Matrix4f;

// Графический фасад newgui: скругления, обводки, градиенты и «стекло» (blur + градиент + обводка).
// Сигнатуры повторяют RenderUtil из исходного клиента, чтобы код компонентов переносился дословно.
public final class GuiRender {

    private static boolean outlineShaderWarned;

    private GuiRender() {
    }

    public static void roundedRect(PoseStack stack, float x, float y, float width, float height,
                                   float radius, int color) {
        RenderUtils.drawRoundedRect(stack, x, y, width, height, radius, color);
    }

    public static void smooth(PoseStack stack, float x, float y, float width, float height,
                              float radius, int color) {
        RenderUtils.drawRoundedRect(stack, x, y, width, height, radius, color);
    }

    public static void roundedOutline(PoseStack stack, float x, float y, float width, float height,
                                      float outline, int color, float radius) {
        if (width <= 0.0f || height <= 0.0f) {
            return;
        }

        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.ROUNDED_RECT_OUTLINE);
        if (shader == null) {
            if (!outlineShaderWarned) {
                outlineShaderWarned = true;
                GodWeer.LOGGER.error("Shader 'rounded_rect_outline' is not compiled, outline fallback in use");
            }
            RenderUtils.drawRoundedRect(stack, x, y, width, height, radius, color);
            return;
        }

        ShaderUtils.set(shader, "Size", width, height);
        ShaderUtils.set(shader, "Radius", radius, radius, radius, radius);
        ShaderUtils.set(shader, "Smoothness", 1.0f);
        ShaderUtils.set(shader, "Outline", outline);
        ShaderUtils.set(shader, "ColorModulator", 1.0f, 1.0f, 1.0f, 1.0f);
        ShaderUtils.setColor(shader, "TopLeftColor", color);
        ShaderUtils.setColor(shader, "BottomLeftColor", color);
        ShaderUtils.setColor(shader, "TopRightColor", color);
        ShaderUtils.setColor(shader, "BottomRightColor", color);

        drawQuad(stack, x, y, width, height, ShaderUtils.ROUNDED_RECT_OUTLINE);
    }

    public static void roundedWithOutline(PoseStack stack, float x, float y, float width, float height,
                                          float outline, int color, int outlineColor, float radius) {
        roundedOutline(stack, x - outline + 0.5f, y - outline + 0.5f,
                width + outline * 2.0f - 1.0f, height + outline * 2.0f - 1.0f,
                outline, outlineColor, radius);
        smooth(stack, x, y, width, height, radius, color);
    }

    // Текстурированный квад со скруглением (rounded_texture): используется для лица
    // игрока в рамке TargetHud — картинка обрезается по радиусу на уровне шейдера.
    // Текстура задаётся ResourceLocation (RenderSystem сам резолвит gl-id).
    public static void roundedTexture(PoseStack stack, net.minecraft.resources.ResourceLocation texture,
                                      float x, float y, float width, float height,
                                      float radius,
                                      float minU, float minV, float maxU, float maxV,
                                      int color) {
        if (width <= 0.0f || height <= 0.0f) {
            return;
        }

        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.ROUNDED_TEXTURE);
        if (shader == null) {
            return;
        }

        ShaderUtils.set(shader, "Size", width, height);
        ShaderUtils.set(shader, "Radius", radius, radius, radius, radius);
        ShaderUtils.set(shader, "Smoothness", 1.0f);
        ShaderUtils.setColor(shader, "ColorModulator", color);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderTexture(0, textureId);

        Matrix4f matrix = stack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        buffer.addVertex(matrix, x, y, 0.0f).setUv(minU, minV).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, x, y + height, 0.0f).setUv(minU, maxV).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, x + width, y + height, 0.0f).setUv(maxU, maxV).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, x + width, y, 0.0f).setUv(maxU, minV).setColor(1.0f, 1.0f, 1.0f, 1.0f);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            RenderSystem.setShader(ShaderUtils.ROUNDED_TEXTURE);
            BufferUploader.drawWithShader(mesh);
        }

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    // Градиент по четырём углам через gradient6-шейдер (левая/центральная/правая колонки).
    public static void drawGradientRound(PoseStack stack, float x, float y, float width, float height,
                                         float radius, int topLeft, int bottomLeft, int topRight, int bottomRight) {
        if (width <= 0.0f || height <= 0.0f) {
            return;
        }

        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.GRADIENT6_RECT);
        if (shader == null) {
            RenderUtils.drawRoundedRect(stack, x, y, width, height, radius, topLeft);
            return;
        }

        ShaderUtils.set(shader, "Size", width, height);
        ShaderUtils.set(shader, "Radius", radius, radius, radius, radius);
        ShaderUtils.set(shader, "Smoothness", 1.0f);
        ShaderUtils.setColor(shader, "LeftTopColor", topLeft);
        ShaderUtils.setColor(shader, "LeftBottomColor", bottomLeft);
        ShaderUtils.setColor(shader, "RightTopColor", topRight);
        ShaderUtils.setColor(shader, "RightBottomColor", bottomRight);
        ShaderUtils.setColor(shader, "CenterTopColor", ColorUtils.interpolateColor(topLeft, topRight, 0.5f));
        ShaderUtils.setColor(shader, "CenterBottomColor", ColorUtils.interpolateColor(bottomLeft, bottomRight, 0.5f));

        drawQuad(stack, x, y, width, height, ShaderUtils.GRADIENT6_RECT);
    }

    // «Стекло» из CustomStyled.drawGui (дефолтная ветка исходника): kawase-блюр,
    // градиент фона (пресет Midnight) и белая обводка alpha 15.
    // Параметр blurStrength сохранён для паритета сигнатуры: в исходнике при активной теме
    // используется сила блюра из темы, а не аргумент.
    public static void drawGlass(PoseStack stack, float x, float y, float width, float height,
                                 float radius, boolean useBlur, float ignoredBlurStrength) {
        float blurStrength = GuiTheme.blurStrength();

        if (useBlur && blurStrength > 0.0f) {
            GuiBlur.draw(stack, x, y, width, height, radius, blurStrength);
        }

        int backgroundAlpha = (int) GuiConfig.backgroundAlpha().get();
        if (backgroundAlpha > 0) {
            float hue = GuiStyle.backgroundHue();
            float sat = GuiStyle.backgroundSaturation() / 100.0f * 0.65f;
            float bri = GuiStyle.backgroundBrightness() / 255.0f;
            int topLeft, bottomLeft, topRight, bottomRight;
            if (!GuiStyle.backgroundGradient()) {
                topLeft = bottomLeft = topRight = bottomRight = GuiStyle.hsv(hue, sat, bri, backgroundAlpha);
            } else if (GuiStyle.backgroundDirection().is("Вертикаль")) {
                topLeft = bottomLeft = GuiStyle.hsv(hue, sat, bri * 1.15f, backgroundAlpha);
                topRight = bottomRight = GuiStyle.hsv(hue, sat, bri * 0.75f, backgroundAlpha);
            } else if (GuiStyle.backgroundDirection().is("Горизонталь")) {
                topLeft = topRight = GuiStyle.hsv(hue, sat, bri * 1.15f, backgroundAlpha);
                bottomLeft = bottomRight = GuiStyle.hsv(hue, sat, bri * 0.75f, backgroundAlpha);
            } else {
                topLeft = GuiStyle.hsv(hue, sat, bri * 1.15f, backgroundAlpha);
                topRight = bottomLeft = GuiStyle.hsv(hue, sat, bri, backgroundAlpha);
                bottomRight = GuiStyle.hsv(hue, sat, bri * 0.7f, backgroundAlpha);
            }
            drawGradientRound(stack, x, y, width, height, radius, topLeft, bottomLeft, topRight, bottomRight);
        }

        if (GuiStyle.panelOutline()) {
            roundedOutline(stack, x, y, width, height, GuiStyle.outlineWidth(),
                    ColorUtils.getColor(255, 255, 255, GuiStyle.outlineAlpha()), radius);
        }
    }

    private static void drawQuad(PoseStack stack, float x, float y, float width, float height,
                                 ShaderProgram program) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        Matrix4f matrix = stack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        buffer.addVertex(matrix, x, y, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, x, y + height, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, x + width, y + height, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, x + width, y, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            RenderSystem.setShader(program);
            BufferUploader.drawWithShader(mesh);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}