package fun.volon.api.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import fun.volon.api.QClient;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.render.blur.BlurProgram;
import fun.volon.api.utils.render.glow.GlowCallback;
import fun.volon.api.utils.render.glow.GlowProgram;
import fun.volon.api.utils.scissor.ScissorUtils;
import fun.volon.client.modules.impl.render.Interface;
import fun.volon.client.modules.impl.render.base.HudRectStyle;
import fun.volon.client.ui.clickgui.ClickGui;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
@SuppressWarnings("all")
public class RenderUtils implements QClient {

    private static final ConcurrentHashMap<String, Identifier> skinCache = new ConcurrentHashMap<>();
    private static final UUID DEFAULT_SKIN_UUID = new UUID(0L, 0L);

    private int getHudBackgroundAlpha() {
        return 150;
    }

    private int applyHudAlpha(int color) {
        int baseAlpha = ColorUtils.getAlpha(color);
        int alpha = (baseAlpha <= 0 ? 255 : baseAlpha) * getHudBackgroundAlpha() / 255;
        return ColorUtils.setAlphaColor(color, MathHelper.clamp(alpha, 0, 255));
    }

    public boolean isLiquidGlassHudEnabled() {
        Interface interfaceModule = Interface.INSTANCE;
        return interfaceModule != null
                && interfaceModule.isEnable()
                && interfaceModule.liquidGlassHud != null
                && interfaceModule.liquidGlassHud.isState();
    }

    public boolean isLiquidGlassHudActive() {
        if (!isLiquidGlassHudEnabled()) {
            return false;
        }
        if (mc == null) {
            return false;
        }
        Screen screen = mc.currentScreen;
        return screen == null || screen instanceof ChatScreen || screen instanceof ClickGui;
    }

    public boolean isLiquidGlassClickGuiActive() {
        return false;
    }

    public HudRectStyle getDefaultHudRectStyle() {
        return HudRectStyle.SHADER;
    }

    /** Category / search bar background in ClickGUI — matches HUD when liquid glass is on. */
    public void drawClickGuiPanel(MatrixStack matrices, float x, float y, float width, float height,
                                  float radius, int themeColor, int shadeColor) {
        if (isLiquidGlassClickGuiActive()) {
            drawHudLiquidGlass(matrices, x, y, width, height, radius, themeColor, true, getDefaultHudRectStyle());
            return;
        }
        drawRoundedRect(matrices, x, y, width, height, radius, ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.07f), 0.72f));
        if (((shadeColor >> 24) & 0xFF) > 0) {
            drawRoundedRect(matrices, x, y, width, height, radius, shadeColor);
        }
    }

    /** Module / theme row background in ClickGUI. */
    public void drawClickGuiModuleBackground(MatrixStack matrices, float x, float y, float width, float height,
                                             boolean enabled, int themeColor, int shadeColor) {
        if (isLiquidGlassClickGuiActive()) {
            drawDefaultHudThemedPanel(matrices, x, y, width, height, 4f, 5f, themeColor, getDefaultHudRectStyle());
            return;
        }
        if (enabled) {
            drawRoundedRect(matrices, x - 0.5f, y - 0.5f, width + 1f, height + 1f, 5,
                    ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.17f), 0.78f));
            drawGradientRect(matrices, x + 0.5f, y, width - 1f, height, 4,
                    ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.15f), 0.78f),
                    ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.1f), 0.78f), false);
        } else {
            drawRoundedRect(matrices, x - 0.5f, y - 0.5f, width + 1f, height + 1f, 5,
                    ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.10f), 0.74f));
            drawGradientRect(matrices, x + 0.5f, y, width - 1f, height, 4,
                    ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.09f), 0.74f),
                    ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.08f), 0.74f), false);
        }
        if (((shadeColor >> 24) & 0xFF) > 0) {
            drawRoundedRect(matrices, x + 0.5f, y, width - 1f, height, 4, shadeColor);
        }
    }

    public void drawBuffer(BufferBuilder buffer) {
        if (buffer == null) {
            return;
        }
        try {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        } catch (IllegalStateException ignored) {
        }
    }

    public void resetGuiRenderState() {
        mc.getFramebuffer().beginWrite(false);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
    }

    public void drawHudLiquidGlass(MatrixStack matrices, float x, float y, float width, float height, float radius, int themeColor, boolean useBlockOverlayShader) {
        drawHudLiquidGlass(matrices, x, y, width, height, radius, radius, radius, radius, themeColor, useBlockOverlayShader, HudRectStyle.SHADER);
    }

    public void drawHudLiquidGlass(MatrixStack matrices, float x, float y, float width, float height, float radius,
                                   int themeColor, boolean useBlockOverlayShader, HudRectStyle rectStyle) {
        drawHudLiquidGlass(matrices, x, y, width, height, radius, radius, radius, radius, themeColor, useBlockOverlayShader, rectStyle);
    }

    public void drawHudLiquidGlass(MatrixStack matrices, float x, float y, float width, float height,
                                   float topLeft, float topRight, float bottomRight, float bottomLeft, int themeColor) {
        drawHudLiquidGlass(matrices, x, y, width, height, topLeft, topRight, bottomRight, bottomLeft, themeColor, true, HudRectStyle.SHADER);
    }

    public void drawHudLiquidGlass(MatrixStack matrices, float x, float y, float width, float height,
                                   float topLeft, float topRight, float bottomRight, float bottomLeft,
                                   int themeColor, boolean useBlockOverlayShader) {
        drawHudLiquidGlass(matrices, x, y, width, height, topLeft, topRight, bottomRight, bottomLeft, themeColor, useBlockOverlayShader, HudRectStyle.SHADER);
    }

    public void drawHudLiquidGlass(MatrixStack matrices, float x, float y, float width, float height,
                                   float topLeft, float topRight, float bottomRight, float bottomLeft,
                                   int themeColor, boolean useBlockOverlayShader, HudRectStyle rectStyle) {
        if (!isLiquidGlassHudActive() || width <= 0.5f || height <= 0.5f) {
            return;
        }

        HudRectStyle style = rectStyle != null ? rectStyle : HudRectStyle.SHADER;

        drawHudLiquidGlassUnderlay(matrices, x, y, width, height, topLeft, topRight, bottomRight, bottomLeft, themeColor, style);

        if (useBlockOverlayShader && HudBlockOverlayShaderRenderer.INSTANCE.isFrameActive()) {
            HudBlockOverlayShaderRenderer renderer = HudBlockOverlayShaderRenderer.INSTANCE;
            renderer.appendMask(matrices, x, y, width, height, topLeft, topRight, bottomRight, bottomLeft);
            renderer.renderPanelOverlay(matrices, x, y, width, height, style);
        }

        if (style == HudRectStyle.THREADS && width >= 22f && height >= 12f) {
            long seed = HudGlassThreadRenderer.seedForRect(x, y, width, height);
            HudGlassThreadRenderer.draw(matrices, x, y, width, height, themeColor, seed);
        }
    }

    private void drawHudLiquidGlassUnderlay(MatrixStack matrices, float x, float y, float width, float height,
                                            float topLeft, float topRight, float bottomRight, float bottomLeft,
                                            int themeColor, HudRectStyle style) {
        BlurProgram.getInstance().setBlurOffset(HudMotionTracker.getBlurOffsetBoost());
        BlurProgram.getInstance().request();

        int fresnelColor = ColorUtils.setAlphaColor(
                ColorUtils.interpolateColor(themeColor, ColorUtils.getThemeColor(), 0.4f),
                255
        );
        int tintColor = ColorUtils.rgba(245, 248, 255, 255);
        float globalAlpha = MathHelper.clamp(getHudBackgroundAlpha() / 255f * 0.94f, 0.3f, 1f);
        float motion = HudMotionTracker.getMotionAmount();
        float distort = 0.016f + motion * 0.052f;
        float glassAlpha = style == HudRectStyle.THREADS ? globalAlpha * 0.58f : globalAlpha * 0.78f;

        drawLiquidGlass(
                matrices, x, y, width, height,
                topLeft, topRight, bottomRight, bottomLeft,
                tintColor,
                glassAlpha,
                2.75f,
                fresnelColor,
                0.34f,
                false,
                0.52f,
                distort,
                0.15f,
                false
        );
        resetGuiRenderState();
    }

    public void drawHudSurface(MatrixStack matrices, float x, float y, float width, float height, float radius, int fallbackColor, int themeColor) {
        if (isLiquidGlassHudActive()) {
            drawHudLiquidGlass(matrices, x, y, width, height, radius, themeColor, true);
            return;
        }
        drawRoundedRect(matrices, x, y, width, height, radius, applyHudAlpha(fallbackColor));
    }

    public void drawHudItem(DrawContext context, ItemStack stack, float x, float y, float scale, float z) {
        if (context == null || stack == null || stack.isEmpty()) {
            return;
        }

        MatrixStack matrices = context.getMatrices();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        matrices.push();
        matrices.translate(x, y, z);
        matrices.scale(scale, scale, 1.0f);
        context.drawItem(stack, 0, 0);
        matrices.pop();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(true);
    }

    public void drawGradient6Rect(MatrixStack matrices, float x, float y, float width, float height,
                                  float radius,
                                  int leftTopColor, int leftBottomColor,
                                  int centerTopColor, int centerBottomColor,
                                  int rightTopColor, int rightBottomColor) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.gradient6Rect);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform radiusUniform = shader.getUniform("Radius");
        GlUniform smoothnessUniform = shader.getUniform("Smoothness");
        GlUniform leftTopColorUniform = shader.getUniform("LeftTopColor");
        GlUniform leftBottomColorUniform = shader.getUniform("LeftBottomColor");
        GlUniform centerTopColorUniform = shader.getUniform("CenterTopColor");
        GlUniform centerBottomColorUniform = shader.getUniform("CenterBottomColor");
        GlUniform rightTopColorUniform = shader.getUniform("RightTopColor");
        GlUniform rightBottomColorUniform = shader.getUniform("RightBottomColor");

        if (sizeUniform != null) sizeUniform.set(width, height);
        if (radiusUniform != null) radiusUniform.set(radius, radius, radius, radius);
        if (smoothnessUniform != null) smoothnessUniform.set(1.0f);

        if (leftTopColorUniform != null) {
            int a = (leftTopColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            leftTopColorUniform.set(
                    ((leftTopColor >> 16) & 0xFF) / 255f,
                    ((leftTopColor >> 8) & 0xFF) / 255f,
                    (leftTopColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (leftBottomColorUniform != null) {
            int a = (leftBottomColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            leftBottomColorUniform.set(
                    ((leftBottomColor >> 16) & 0xFF) / 255f,
                    ((leftBottomColor >> 8) & 0xFF) / 255f,
                    (leftBottomColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (centerTopColorUniform != null) {
            int a = (centerTopColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            centerTopColorUniform.set(
                    ((centerTopColor >> 16) & 0xFF) / 255f,
                    ((centerTopColor >> 8) & 0xFF) / 255f,
                    (centerTopColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (centerBottomColorUniform != null) {
            int a = (centerBottomColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            centerBottomColorUniform.set(
                    ((centerBottomColor >> 16) & 0xFF) / 255f,
                    ((centerBottomColor >> 8) & 0xFF) / 255f,
                    (centerBottomColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (rightTopColorUniform != null) {
            int a = (rightTopColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            rightTopColorUniform.set(
                    ((rightTopColor >> 16) & 0xFF) / 255f,
                    ((rightTopColor >> 8) & 0xFF) / 255f,
                    (rightTopColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (rightBottomColorUniform != null) {
            int a = (rightBottomColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            rightBottomColorUniform.set(
                    ((rightBottomColor >> 16) & 0xFF) / 255f,
                    ((rightBottomColor >> 8) & 0xFF) / 255f,
                    (rightBottomColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        buffer.vertex(matrix, x, y, 0).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x, y + height, 0).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x + width, y + height, 0).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x + width, y, 0).color(1f, 1f, 1f, 1f);

        RenderSystem.setShader(ShaderUtils.gradient6Rect);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
    }

//    public void OutlineRect(MatrixStack matrices, float x, float y, float width, float height, float radius, float sizeBorder, int color) {
//        drawRoundedRectOutline(matrices, x, y,  width, height, radius, radius, radius, radius, sizeBorder, color);
//    }

    public void drawShadow(MatrixStack matrices, float x, float y, float width, float height,
                           float radius, float softness,
                           int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shadowRect);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float extendedWidth = width + softness * 2.0f;
        float extendedHeight = height + softness * 2.0f;
        float drawX = x - softness;
        float drawY = y - softness;

        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform softnessUniform = shader.getUniform("Softness");
        GlUniform radiusUniform = shader.getUniform("Radius");
        GlUniform topLeftColorUniform = shader.getUniform("TopLeftColor");
        GlUniform topRightColorUniform = shader.getUniform("TopRightColor");
        GlUniform bottomLeftColorUniform = shader.getUniform("BottomLeftColor");
        GlUniform bottomRightColorUniform = shader.getUniform("BottomRightColor");

        if (sizeUniform != null) sizeUniform.set(extendedWidth, extendedHeight);
        if (softnessUniform != null) softnessUniform.set(softness);
        if (radiusUniform != null) radiusUniform.set(radius);

        if (topLeftColorUniform != null) {
            int a = (topLeftColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            topLeftColorUniform.set(
                    ((topLeftColor >> 16) & 0xFF) / 255f,
                    ((topLeftColor >> 8) & 0xFF) / 255f,
                    (topLeftColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (topRightColorUniform != null) {
            int a = (topRightColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            topRightColorUniform.set(
                    ((topRightColor >> 16) & 0xFF) / 255f,
                    ((topRightColor >> 8) & 0xFF) / 255f,
                    (topRightColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (bottomLeftColorUniform != null) {
            int a = (bottomLeftColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            bottomLeftColorUniform.set(
                    ((bottomLeftColor >> 16) & 0xFF) / 255f,
                    ((bottomLeftColor >> 8) & 0xFF) / 255f,
                    (bottomLeftColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (bottomRightColorUniform != null) {
            int a = (bottomRightColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            bottomRightColorUniform.set(
                    ((bottomRightColor >> 16) & 0xFF) / 255f,
                    ((bottomRightColor >> 8) & 0xFF) / 255f,
                    (bottomRightColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        buffer.vertex(matrix, drawX, drawY, 0).texture(0, 0);
        buffer.vertex(matrix, drawX, drawY + extendedHeight, 0).texture(0, 1);
        buffer.vertex(matrix, drawX + extendedWidth, drawY + extendedHeight, 0).texture(1, 1);
        buffer.vertex(matrix, drawX + extendedWidth, drawY, 0).texture(1, 0);

        RenderSystem.setShader(ShaderUtils.shadowRect);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
    }

    public void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius,
                           int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        drawShadow(matrices, x, y, width, height, radius, 10.0f, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
    }

    public void drawShadow(MatrixStack matrices, float x, float y, float width, float height,
                           float radius, float softness, int color) {
        drawShadow(matrices, x, y, width, height, radius, softness, color, color, color, color);
    }

    public void drawShadow(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
        drawShadow(matrices, x, y, width, height, radius, 10.0f, color, color, color, color);
    }

    public void drawShadow(MatrixStack matrices, float x, float y, float width, float height, int color) {
        drawShadow(matrices, x, y, width, height, 0.0f, 10.0f, color, color, color, color);
    }

    public void drawShadow(MatrixStack matrices, float x, float y, float width, float height,
                           float radius, float softness, int topColor, int bottomColor) {
        drawShadow(matrices, x, y, width, height, radius, softness, topColor, topColor, bottomColor, bottomColor);
    }

    public void drawShadowHorizontal(MatrixStack matrices, float x, float y, float width, float height,
                                     float radius, float softness, int leftColor, int rightColor) {
        drawShadow(matrices, x, y, width, height, radius, softness, leftColor, rightColor, leftColor, rightColor);
    }

    public void drawShadow(MatrixStack matrices, float x, float y, float width, float height,
                           float radius, float softness, float offsetX, float offsetY, int color) {
        drawShadow(matrices, x + offsetX, y + offsetY, width, height, radius, softness, color, color, color, color);
    }

    public void drawShadow(MatrixStack matrices, float x, float y, float width, float height,
                           float radius, float softness, float offsetX, float offsetY,
                           int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        drawShadow(matrices, x + offsetX, y + offsetY, width, height, radius, softness,
                topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
    }

    public void drawShadowOuter(MatrixStack matrices, float x, float y, float width, float height,
                                float radius, float softness, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shadowOuter);
        if (shader == null) {
            RenderSystem.disableBlend();
            return;
        }

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float extendedWidth = width + softness * 2.0f;
        float extendedHeight = height + softness * 2.0f;
        float drawX = x - softness;
        float drawY = y - softness;

        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform softnessUniform = shader.getUniform("Softness");
        GlUniform radiusUniform = shader.getUniform("Radius");

        if (sizeUniform != null) sizeUniform.set(extendedWidth, extendedHeight);
        if (softnessUniform != null) softnessUniform.set(softness);
        if (radiusUniform != null) radiusUniform.set(radius);

        int a = (color >> 24) & 0xFF;
        if (a == 0) a = 255;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float af = a / 255f;

        String[] names = {"TopLeftColor", "TopRightColor", "BottomLeftColor", "BottomRightColor"};
        for (String name : names) {
            GlUniform u = shader.getUniform(name);
            if (u != null) u.set(r, g, b, af);
        }

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(matrix, drawX, drawY, 0).texture(0, 0);
        buffer.vertex(matrix, drawX, drawY + extendedHeight, 0).texture(0, 1);
        buffer.vertex(matrix, drawX + extendedWidth, drawY + extendedHeight, 0).texture(1, 1);
        buffer.vertex(matrix, drawX + extendedWidth, drawY, 0).texture(1, 0);

        RenderSystem.setShader(ShaderUtils.shadowOuter);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
    }

    public void drawShadow6(MatrixStack matrices, float x, float y, float width, float height,
                            float radius, float softness,
                            int leftTopColor, int leftBottomColor,
                            int centerTopColor, int centerBottomColor,
                            int rightTopColor, int rightBottomColor) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.shadow6Rect);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float extendedWidth = width + softness * 2.0f;
        float extendedHeight = height + softness * 2.0f;
        float drawX = x - softness;
        float drawY = y - softness;

        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform softnessUniform = shader.getUniform("Softness");
        GlUniform radiusUniform = shader.getUniform("Radius");
        GlUniform leftTopColorUniform = shader.getUniform("LeftTopColor");
        GlUniform leftBottomColorUniform = shader.getUniform("LeftBottomColor");
        GlUniform centerTopColorUniform = shader.getUniform("CenterTopColor");
        GlUniform centerBottomColorUniform = shader.getUniform("CenterBottomColor");
        GlUniform rightTopColorUniform = shader.getUniform("RightTopColor");
        GlUniform rightBottomColorUniform = shader.getUniform("RightBottomColor");

        if (sizeUniform != null) sizeUniform.set(extendedWidth, extendedHeight);
        if (softnessUniform != null) softnessUniform.set(softness);
        if (radiusUniform != null) radiusUniform.set(radius);

        if (leftTopColorUniform != null) {
            int a = (leftTopColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            leftTopColorUniform.set(
                    ((leftTopColor >> 16) & 0xFF) / 255f,
                    ((leftTopColor >> 8) & 0xFF) / 255f,
                    (leftTopColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (leftBottomColorUniform != null) {
            int a = (leftBottomColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            leftBottomColorUniform.set(
                    ((leftBottomColor >> 16) & 0xFF) / 255f,
                    ((leftBottomColor >> 8) & 0xFF) / 255f,
                    (leftBottomColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (centerTopColorUniform != null) {
            int a = (centerTopColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            centerTopColorUniform.set(
                    ((centerTopColor >> 16) & 0xFF) / 255f,
                    ((centerTopColor >> 8) & 0xFF) / 255f,
                    (centerTopColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (centerBottomColorUniform != null) {
            int a = (centerBottomColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            centerBottomColorUniform.set(
                    ((centerBottomColor >> 16) & 0xFF) / 255f,
                    ((centerBottomColor >> 8) & 0xFF) / 255f,
                    (centerBottomColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (rightTopColorUniform != null) {
            int a = (rightTopColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            rightTopColorUniform.set(
                    ((rightTopColor >> 16) & 0xFF) / 255f,
                    ((rightTopColor >> 8) & 0xFF) / 255f,
                    (rightTopColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        if (rightBottomColorUniform != null) {
            int a = (rightBottomColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            rightBottomColorUniform.set(
                    ((rightBottomColor >> 16) & 0xFF) / 255f,
                    ((rightBottomColor >> 8) & 0xFF) / 255f,
                    (rightBottomColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        buffer.vertex(matrix, drawX, drawY, 0).texture(0, 0);
        buffer.vertex(matrix, drawX, drawY + extendedHeight, 0).texture(0, 1);
        buffer.vertex(matrix, drawX + extendedWidth, drawY + extendedHeight, 0).texture(1, 1);
        buffer.vertex(matrix, drawX + extendedWidth, drawY, 0).texture(1, 0);

        RenderSystem.setShader(ShaderUtils.shadow6Rect);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
    }

    public void drawTexture(MatrixStack matrices, Identifier texture, float x, float y, float width, float height, float u1, float v1, float u2, float v2, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, texture);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) alpha = 255;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = alpha / 255f;

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(matrix, x, y, 0).texture(u1, v1).color(r, g, b, a);
        buffer.vertex(matrix, x, y + height, 0).texture(u1, v2).color(r, g, b, a);
        buffer.vertex(matrix, x + width, y + height, 0).texture(u2, v2).color(r, g, b, a);
        buffer.vertex(matrix, x + width, y, 0).texture(u2, v1).color(r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.disableBlend();
    }

    public void drawImage(MatrixStack matrices, Identifier texture, float x, float y, float width, float height, int color) {
        drawTexture(matrices, texture, x, y, width, height, 0.0f, 0.0f, 1.0f, 1.0f, color);
    }

    public void drawImage(MatrixStack matrices, String namespace, String path, float x, float y, float width, float height, int color) {
        drawImage(matrices, Identifier.of(namespace, path), x, y, width, height, color);
    }

    public void drawSprite(MatrixStack matrices, Sprite sprite, float x, float y, float size, int color) {
        drawTexture(matrices, sprite.getAtlasId(), x, y, size, size,
                sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), color);
    }

    public void drawPlayerHead(MatrixStack matrices, PlayerEntity player, float x, float y, float size, float radius, float hurtPercent) {
        if (player == null) return;
        Identifier skinTexture = getSkinTexture(player);
        drawHeadInternal(matrices, skinTexture, x, y, size, radius, 1.0f, hurtPercent);
    }

    public void drawPlayerHead(MatrixStack matrices, String username, float x, float y, float size, float radius) {
        drawPlayerHead(matrices, username, x, y, size, radius, 1.0f, 0.0f);
    }

    public void drawPlayerHead(MatrixStack matrices, String username, float x, float y, float size, float radius, float alpha, float hurtPercent) {
        if (username == null || username.isEmpty()) return;
        Identifier skinTexture = getSkinTextureByName(username);
        drawHeadInternal(matrices, skinTexture, x, y, size, radius, alpha, hurtPercent);
    }

    public void drawPlayerHead(MatrixStack matrices, UUID uuid, float x, float y, float size, float radius) {
        drawPlayerHead(matrices, uuid, x, y, size, radius, 1.0f, 0.0f);
    }

    public void drawPlayerHead(MatrixStack matrices, UUID uuid, float x, float y, float size, float radius, float alpha, float hurtPercent) {
        if (uuid == null) return;
        Identifier skinTexture = getSkinTextureByUUID(uuid);
        drawHeadInternal(matrices, skinTexture, x, y, size, radius, alpha, hurtPercent);
    }

    public void drawPlayerHead(MatrixStack matrices, PlayerListEntry entry, float x, float y, float size, float radius) {
        drawPlayerHead(matrices, entry, x, y, size, radius, 1.0f, 0.0f);
    }

    public void drawPlayerHead(MatrixStack matrices, PlayerListEntry entry, float x, float y, float size, float radius, float alpha, float hurtPercent) {
        if (entry == null) return;
        Identifier skinTexture = entry.getSkinTextures().texture();
        if (skinTexture == null) {
            skinTexture = DefaultSkinHelper.getSkinTextures(entry.getProfile().getId()).texture();
        }
        drawHeadInternal(matrices, skinTexture, x, y, size, radius, alpha, hurtPercent);
    }

    public void drawPlayerHead(MatrixStack matrices, Identifier skinTexture, float x, float y, float size, float radius) {
        drawHeadInternal(matrices, skinTexture, x, y, size, radius, 1.0f, 0.0f);
    }

    private void drawHeadInternal(MatrixStack matrices, Identifier skinTexture, float x, float y, float size, float radius, float alpha, float hurtPercent) {
        if (skinTexture == null) {
            skinTexture = DefaultSkinHelper.getSkinTextures(DEFAULT_SKIN_UUID).texture();
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, skinTexture);

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.face);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        GlUniform locationUniform = shader.getUniform("location");
        GlUniform sizeUniform = shader.getUniform("size");
        GlUniform radiusUniform = shader.getUniform("radius");
        GlUniform alphaUniform = shader.getUniform("alpha");
        GlUniform uUniform = shader.getUniform("u");
        GlUniform vUniform = shader.getUniform("v");
        GlUniform wUniform = shader.getUniform("w");
        GlUniform hUniform = shader.getUniform("h");
        GlUniform hurtPercentUniform = shader.getUniform("hurtPercent");

        if (locationUniform != null) locationUniform.set(x, y);
        if (sizeUniform != null) sizeUniform.set(size, size);
        if (radiusUniform != null) radiusUniform.set(radius);
        if (alphaUniform != null) alphaUniform.set(alpha);
        if (uUniform != null) uUniform.set(8.0f / 64.0f);
        if (vUniform != null) vUniform.set(8.0f / 64.0f);
        if (wUniform != null) wUniform.set(8.0f / 64.0f);
        if (hUniform != null) hUniform.set(8.0f / 64.0f);
        if (hurtPercentUniform != null) hurtPercentUniform.set(hurtPercent);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        buffer.vertex(matrix, x, y, 0).texture(0, 0);
        buffer.vertex(matrix, x, y + size, 0).texture(0, 1);
        buffer.vertex(matrix, x + size, y + size, 0).texture(1, 1);
        buffer.vertex(matrix, x + size, y, 0).texture(1, 0);

        RenderSystem.setShader(ShaderUtils.face);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        drawHeadOverlay(matrices, skinTexture, x, y, size, radius, alpha, hurtPercent);

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.disableBlend();
    }

    private void drawHeadOverlay(MatrixStack matrices, Identifier skinTexture, float x, float y, float size, float radius, float alpha, float hurtPercent) {
        RenderSystem.setShaderTexture(0, skinTexture);

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.face);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        GlUniform locationUniform = shader.getUniform("location");
        GlUniform sizeUniform = shader.getUniform("size");
        GlUniform radiusUniform = shader.getUniform("radius");
        GlUniform alphaUniform = shader.getUniform("alpha");
        GlUniform uUniform = shader.getUniform("u");
        GlUniform vUniform = shader.getUniform("v");
        GlUniform wUniform = shader.getUniform("w");
        GlUniform hUniform = shader.getUniform("h");
        GlUniform hurtPercentUniform = shader.getUniform("hurtPercent");

        if (locationUniform != null) locationUniform.set(x, y);
        if (sizeUniform != null) sizeUniform.set(size, size);
        if (radiusUniform != null) radiusUniform.set(radius);
        if (alphaUniform != null) alphaUniform.set(alpha);
        if (uUniform != null) uUniform.set(40.0f / 64.0f);
        if (vUniform != null) vUniform.set(8.0f / 64.0f);
        if (wUniform != null) wUniform.set(8.0f / 64.0f);
        if (hUniform != null) hUniform.set(8.0f / 64.0f);
        if (hurtPercentUniform != null) hurtPercentUniform.set(hurtPercent);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        buffer.vertex(matrix, x, y, 0).texture(0, 0);
        buffer.vertex(matrix, x, y + size, 0).texture(0, 1);
        buffer.vertex(matrix, x + size, y + size, 0).texture(1, 1);
        buffer.vertex(matrix, x + size, y, 0).texture(1, 0);

        RenderSystem.setShader(ShaderUtils.face);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    private Identifier getSkinTexture(PlayerEntity player) {
        if (mc.getNetworkHandler() == null) {
            return DefaultSkinHelper.getSkinTextures(player.getUuid()).texture();
        }

        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (entry != null) {
            return entry.getSkinTextures().texture();
        }

        return DefaultSkinHelper.getSkinTextures(player.getUuid()).texture();
    }

    private Identifier getSkinTextureByName(String username) {
        String key = username.toLowerCase(Locale.ROOT);
        Identifier cachedTexture = skinCache.get(key);
        if (cachedTexture != null) {
            return cachedTexture;
        }

        if (mc.getNetworkHandler() != null) {
            for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
                if (entry.getProfile().getName().equalsIgnoreCase(username)) {
                    Identifier texture = entry.getSkinTextures().texture();
                    skinCache.put(key, texture);
                    return texture;
                }
            }
        }

        if (mc.world != null) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player.getName().getString().equalsIgnoreCase(username)) {
                    Identifier texture = getSkinTexture(player);
                    skinCache.put(key, texture);
                    return texture;
                }
            }
        }

        Identifier texture = DefaultSkinHelper.getSkinTextures(UUID.nameUUIDFromBytes(username.getBytes())).texture();
        skinCache.put(key, texture);
        return texture;
    }

    private Identifier getSkinTextureByUUID(UUID uuid) {
        String key = uuid.toString();

        if (skinCache.containsKey(key)) {
            return skinCache.get(key);
        }

        if (mc.getNetworkHandler() != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(uuid);
            if (entry != null) {
                Identifier texture = entry.getSkinTextures().texture();
                skinCache.put(key, texture);
                return texture;
            }
        }

        if (mc.world != null) {
            PlayerEntity player = mc.world.getPlayerByUuid(uuid);
            if (player != null) {
                Identifier texture = getSkinTexture(player);
                skinCache.put(key, texture);
                return texture;
            }
        }

        return DefaultSkinHelper.getSkinTextures(uuid).texture();
    }

    public void clearSkinCache() {
        skinCache.clear();
    }

    public void removeSkinFromCache(String username) {
        skinCache.remove(username.toLowerCase(Locale.ROOT));
    }

    public void drawRoundedRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
        drawRoundedRect(matrices, x, y, width, height, radius, radius, radius, radius, color);
    }

    public void drawDefaultHudElementRects(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
        drawDefaultHudElementRects(matrices, x, y, width, height, themeColor, HudRectStyle.SHADER);
    }

    public void drawDefaultHudElementRects(MatrixStack matrices, float x, float y, float width, float height, int themeColor, HudRectStyle rectStyle) {
        HudRectStyle style = rectStyle != null ? rectStyle : HudRectStyle.SHADER;
        if (isLiquidGlassHudActive()) {
            drawHudLiquidGlass(matrices, x, y, width, height, 3.0f, 3.5f, 3.5f, 3.0f, themeColor, true, style);
            if (style != HudRectStyle.THREADS) {
                drawHudLiquidGlass(matrices, x + width - 14.5f, y + 3.0f, 10.0f, 10.0f, 2.0f, themeColor, true, style);
            }
            return;
        }
        drawDefaultHudThemedPanel(matrices, x, y, width, height, 3.0f, 3.5f, themeColor, style);
        // Icon background removed - icons now have transparent background
    }

    public void drawHudSquarePattern(MatrixStack matrices, float x, float y, float width, float height, int themeColor) {
        if (width <= 6.0f || height <= 6.0f) return;

        float clipX = x - 1;
        float clipY = y + 1.0f;
        float clipW = Math.max(1.0f, width - 2.0f);
        float clipH = Math.max(1.0f, height - 2.0f);
        float themeAlphaMul = ((themeColor >>> 24) & 0xFF) / 255.0f;
        if (themeAlphaMul <= 0.001f) {
            return;
        }

        if (clipH <= 20.0f) {
            final float[][] compactSlots = {
                    {0.05f, 0.08f, 8.6f},
                    {0.92f, 0.10f, 8.8f},
                    {0.16f, 0.78f, 6.3f},
                    {0.77f, 0.80f, 6.5f},
                    {0.31f, 0.18f, 6.0f},
                    {0.58f, 0.74f, 5.8f},
                    {0.45f, 0.45f, 5.1f},
                    {0.86f, 0.46f, 5.3f},
                    {0.23f, 0.52f, 4.9f},
                    {0.67f, 0.30f, 5.0f},
                    {0.11f, 0.34f, 5.5f},
                    {0.38f, 0.70f, 5.2f},
                    {0.72f, 0.16f, 5.7f},
                    {0.95f, 0.68f, 5.1f}
            };

            float desiredCount = Math.min(
                    compactSlots.length,
                    3.7f + Math.max(0.0f, (clipW - 84.0f) / 32.0f)
            );
            int outlineColorBase = ColorUtils.setAlphaColor(
                    ColorUtils.darken(themeColor, 0.62f),
                    Math.max(0, Math.min(255, (int) (82.0f * themeAlphaMul)))
            );

            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(clipX, clipY, clipW, clipH);
            try {
                for (int i = 0; i < compactSlots.length; i++) {
                    float reveal = desiredCount - i;
                    if (reveal <= 0.0f) {
                        continue;
                    }

                    float alphaMul = Math.max(0.0f, Math.min(1.0f, reveal));
                    alphaMul = alphaMul * alphaMul * (3.0f - 2.0f * alphaMul);
                    if (alphaMul <= 0.02f) {
                        continue;
                    }

                    float size = compactSlots[i][2];
                    float px = clipX + 0.8f + compactSlots[i][0] * Math.max(1.0f, clipW - size + 1.6f);
                    float py = clipY - 1.2f + compactSlots[i][1] * Math.max(1.0f, clipH - size + 2.4f);

                    int outlineAlpha = Math.max(0, Math.min(255, (int) (86.0f * alphaMul * themeAlphaMul)));
                    if (outlineAlpha <= 0) {
                        continue;
                    }
                    int outlineColor = ColorUtils.setAlphaColor(outlineColorBase, outlineAlpha);
                    drawRoundedRectOutline(matrices, px, py, size, size, 0.0f, 0.5f, outlineColor, outlineColor, outlineColor, outlineColor);
                }
            } finally {
                ScissorUtils.unset();
                ScissorUtils.pop();
            }
            return;
        }

        final float[][] slots = {
                {0.05f, 4.0f, 9.6f},
                {0.87f, 4.0f, 9.2f},
                {0.50f, 8.0f, 7.4f},
                {0.18f, 13.0f, 6.2f},
                {0.72f, 13.0f, 6.0f},
                {0.07f, 21.0f, 5.6f},
                {0.91f, 21.0f, 5.8f},
                {0.24f, 30.0f, 5.4f},
                {0.66f, 30.0f, 5.5f},
                {0.04f, 38.0f, 6.8f},
                {0.90f, 38.0f, 7.0f},
                {0.15f, 47.0f, 5.4f},
                {0.78f, 47.0f, 5.5f},
                {0.08f, 56.0f, 5.1f},
                {0.92f, 56.0f, 5.2f},
                {0.23f, 65.0f, 5.8f},
                {0.69f, 65.0f, 5.9f},
                {0.52f, 71.0f, 7.2f},
                {0.06f, 74.0f, 7.6f},
                {0.88f, 74.0f, 7.4f},
                {0.14f, 85.0f, 5.7f},
                {0.82f, 85.0f, 5.8f},
                {0.09f, 97.0f, 6.5f},
                {0.90f, 98.0f, 6.6f}
        };

        int baseCount = 10;
        float extraHeight = Math.max(0.0f, clipH - 24.0f);
        float desiredCount = Math.min(slots.length, baseCount + (extraHeight / 10.0f));
        float panelAlpha = Math.max(0.0f, Math.min(1.0f, (clipH - 10.0f) / 16.0f));
        panelAlpha = panelAlpha * panelAlpha * (3.0f - 2.0f * panelAlpha);
        int outlineColorBase = ColorUtils.setAlphaColor(ColorUtils.darken(themeColor, 0.72f), Math.max(0, Math.min(255, (int) (40.0f * themeAlphaMul))));

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(clipX, clipY, clipW, clipH);
        try {
            for (int i = 0; i < slots.length; i++) {
                float reveal = desiredCount - i;
                if (reveal <= 0.0f) {
                    continue;
                }

                float alphaMul = Math.max(0.0f, Math.min(1.0f, reveal));
                alphaMul = alphaMul * alphaMul * (3.0f - 2.0f * alphaMul);
                alphaMul *= panelAlpha;
                if (alphaMul <= 0.015f) {
                    continue;
                }

                float size = slots[i][2];
                float px = clipX + 2.0f + slots[i][0] * Math.max(1.0f, clipW - size - 4.0f);
                float py = clipY + slots[i][1];
                float bottomLimit = clipY + clipH - 1.0f;
                if (py >= bottomLimit) {
                    continue;
                }
                if (py + size > bottomLimit) {
                    float visible = Math.max(0.0f, Math.min(1.0f, (bottomLimit - py) / Math.max(1.0f, size)));
                    visible = visible * visible * (3.0f - 2.0f * visible);
                    alphaMul *= visible;
                    if (alphaMul <= 0.015f) {
                        continue;
                    }
                }

                int outlineAlpha = Math.max(0, Math.min(255, (int) (58.0f * alphaMul * themeAlphaMul)));
                if (outlineAlpha <= 0) {
                    continue;
                }
                int outlineColor = ColorUtils.setAlphaColor(outlineColorBase, outlineAlpha);
                drawRoundedRectOutline(matrices, px, py, size, size, 0.0f, 0.55f, outlineColor, outlineColor, outlineColor, outlineColor);
            }
        } finally {
            ScissorUtils.unset();
            ScissorUtils.pop();
        }
    }

    public void drawDefaultHudInfoBox(MatrixStack matrices, float x, float y, float width, int outerColor, int innerColor) {
        if (isLiquidGlassHudActive()) {
            int theme = ColorUtils.interpolateColor(outerColor, innerColor, 0.5f);
            drawHudLiquidGlass(matrices, x - 0.25f, y - 1.25f, width + 0.5f, 9.0f, 1.3f, theme, true);
            return;
        }
        drawRoundedRect(matrices, x - 0.25f, y - 1.25f, width + 0.5f, 9.0f, 1.3f, applyHudAlpha(outerColor));
        drawRoundedRect(matrices, x, y - 1.0f, width, 8.5f, 1.0f, applyHudAlpha(innerColor));
    }

    public void drawDefaultHudPanel(MatrixStack matrices, float x, float y, float width, float height,
                                    float gradientRadius, float borderRadius,
                                    int borderColor, int topColor, int bottomColor) {
        drawDefaultHudPanel(matrices, x, y, width, height, gradientRadius, borderRadius, borderColor, topColor, bottomColor, true);
    }

    public void drawDefaultHudPanel(MatrixStack matrices, float x, float y, float width, float height,
                                    float gradientRadius, float borderRadius,
                                    int borderColor, int topColor, int bottomColor, boolean useBlockOverlayShader) {
        drawDefaultHudPanel(matrices, x, y, width, height, gradientRadius, borderRadius, borderColor, topColor, bottomColor, useBlockOverlayShader, HudRectStyle.SHADER);
    }

    public void drawDefaultHudPanel(MatrixStack matrices, float x, float y, float width, float height,
                                    float gradientRadius, float borderRadius,
                                    int borderColor, int topColor, int bottomColor,
                                    boolean useBlockOverlayShader, HudRectStyle rectStyle) {
        HudRectStyle style = rectStyle != null ? rectStyle : HudRectStyle.SHADER;
        if (isLiquidGlassHudActive() && useBlockOverlayShader) {
            int theme = ColorUtils.interpolateColor(topColor, bottomColor, 0.5f);
            drawHudLiquidGlass(matrices, x, y, width, height, gradientRadius, borderRadius, borderRadius, gradientRadius, theme, true, style);
            return;
        }
        if (isLiquidGlassHudEnabled() && !useBlockOverlayShader) {
            drawRoundedRect(matrices, x - 0.5f, y - 0.5f, width + 1.0f, height + 1.0f, borderRadius, applyHudAlpha(borderColor));
            drawGradientRect(matrices, x, y, width, height, gradientRadius, applyHudAlpha(topColor), applyHudAlpha(bottomColor));
            if (style == HudRectStyle.THREADS && width >= 22f && height >= 12f) {
                int theme = ColorUtils.interpolateColor(topColor, bottomColor, 0.5f);
                long seed = HudGlassThreadRenderer.seedForRect(x, y, width, height);
                HudGlassThreadRenderer.draw(matrices, x, y, width, height, theme, seed);
            }
            return;
        }
        drawRoundedRect(matrices, x - 0.5f, y - 0.5f, width + 1.0f, height + 1.0f, borderRadius, applyHudAlpha(borderColor));
        drawGradientRect(matrices, x, y, width, height, gradientRadius, applyHudAlpha(topColor), applyHudAlpha(bottomColor));
        if (style == HudRectStyle.THREADS && width >= 22f && height >= 12f) {
            int theme = ColorUtils.interpolateColor(topColor, bottomColor, 0.5f);
            long seed = HudGlassThreadRenderer.seedForRect(x, y, width, height);
            HudGlassThreadRenderer.draw(matrices, x, y, width, height, theme, seed);
        }
    }

    public void drawDefaultHudThemedPanel(MatrixStack matrices, float x, float y, float width, float height,
                                          float gradientRadius, float borderRadius, int themeColor) {
        drawDefaultHudThemedPanel(matrices, x, y, width, height, gradientRadius, borderRadius, themeColor, HudRectStyle.SHADER);
    }

    public void drawDefaultHudThemedPanel(MatrixStack matrices, float x, float y, float width, float height,
                                          float gradientRadius, float borderRadius, int themeColor, HudRectStyle rectStyle) {
        HudRectStyle style = rectStyle != null ? rectStyle : HudRectStyle.SHADER;
        if (isLiquidGlassHudActive()) {
            drawHudLiquidGlass(matrices, x, y, width, height, gradientRadius, borderRadius, borderRadius, gradientRadius, themeColor, true, style);
            return;
        }
        drawDefaultHudPanel(
                matrices, x, y, width, height, gradientRadius, borderRadius,
                ColorUtils.rgba(0, 0, 0, 255),
                ColorUtils.rgba(0, 0, 0, 255),
                ColorUtils.rgba(0, 0, 0, 255)
        );
        if (style == HudRectStyle.THREADS && width >= 22f && height >= 12f) {
            long seed = HudGlassThreadRenderer.seedForRect(x, y, width, height);
            HudGlassThreadRenderer.draw(matrices, x, y, width, height, themeColor, seed);
        }
    }

    public void drawWaveHudHeader(MatrixStack matrices, float x, float y, float width, float height, float radius,
                                  float shadowRadius, float shadowSoftness,
                                  int leftTop, int leftBottom, int centerTop, int centerBottom, int rightTop, int rightBottom) {
        drawShadow6(matrices, x, y, width, height, shadowRadius, shadowSoftness,
                leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
        drawGradient6Rect(matrices, x, y, width, height, radius,
                leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
    }

    public void drawWaveHudPanel(MatrixStack matrices, float x, float y, float width, float height, int bgColor,
                                 float headerHeight, float headerRadius, float shadowRadius, float shadowSoftness,
                                 int leftTop, int leftBottom, int centerTop, int centerBottom, int rightTop, int rightBottom) {
        if (isLiquidGlassHudActive()) {
            int theme = ColorUtils.interpolateColor(leftTop, rightBottom, 0.5f);
            float bodyY = y + headerHeight;
            float bodyH = height - headerHeight;
            if (bodyH > 0.5f) {
                drawHudLiquidGlass(matrices, x, bodyY, width, bodyH, 0f, 0f, 0f, 0f, theme, true);
            }
            drawWaveHudHeader(matrices, x, y, width, headerHeight, headerRadius, shadowRadius, shadowSoftness,
                    leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
            return;
        }
        drawRoundedRect(matrices, x, y, width, height, 0, bgColor);
        drawWaveHudHeader(matrices, x, y, width, headerHeight, headerRadius, shadowRadius, shadowSoftness,
                leftTop, leftBottom, centerTop, centerBottom, rightTop, rightBottom);
    }

    public void drawTargetHudWaveFrame(MatrixStack matrices, float x, float y, float width, float height,
                                       float padding, float entityBoxSize, float alpha) {
        drawRoundedRect(matrices, x, y, width, height, 0, ColorUtils.applyAlpha(ColorUtils.rgba(40, 40, 40, 255), alpha));
        drawRoundedRect(matrices, x + padding, y + padding, width - padding * 2, height - padding * 2, 0, ColorUtils.applyAlpha(ColorUtils.rgba(20, 20, 20, 255), alpha));
        drawRoundedRect(matrices, x + padding + 2f, y + padding + 2f, entityBoxSize, entityBoxSize, 0, ColorUtils.applyAlpha(ColorUtils.rgba(40, 40, 40, 255), alpha));
        drawRoundedRect(matrices, x + padding + 3f, y + padding + 3f, entityBoxSize - 2, entityBoxSize - 2, 0, ColorUtils.applyAlpha(ColorUtils.rgba(25, 25, 25, 255), alpha));
    }

    public void drawTargetHudDefaultPlaceholder(MatrixStack matrices, float x, float y, float alpha) {
        drawRoundedRect(matrices, x - 1, y - 1, 22.0f, 22.0f, 1f, ColorUtils.applyAlpha(ColorUtils.rgba(21, 21, 21, 255), alpha));
    }

    public void drawTargetHudHealthBars(MatrixStack matrices, float x, float y, float width, float trailProgress, float progress, int themeColor, int themecolor2, float alpha) {
        drawRoundedRect(matrices, x, y, width, 5.5f, 1.25f, ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.5f), alpha * 0.8f));
        drawRoundedRect(matrices, x, y, width * trailProgress, 5.5f, 1.25f, ColorUtils.applyAlpha(ColorUtils.darken(themeColor, 0.8f), alpha * 0.8f));
        drawGradientRect(matrices, x, y, width * progress, 5.5f, 1.25f, ColorUtils.applyAlpha(themeColor, alpha), ColorUtils.applyAlpha(themecolor2, alpha), true);
    }

    public void drawTargetHudGoldenBars(MatrixStack matrices, float x, float y, float width, float height, float trailProgress, float progress, float alpha, float goldenAlpha) {
        int goldenColor = ColorUtils.rgba(255, 215, 0, 255);
        drawRoundedRect(matrices, x, y, width * trailProgress, height, 1.25f, ColorUtils.applyAlpha(ColorUtils.darken(goldenColor, 0.65f), alpha * goldenAlpha * 0.8f));
        drawGradientRect(matrices, x, y, width * progress, height, 1.25f,  ColorUtils.applyAlpha(ColorUtils.darken(goldenColor, 0.55f), alpha * goldenAlpha), ColorUtils.applyAlpha(goldenColor, alpha * goldenAlpha), true);
    }

    public void drawTargetHudHeartBase(MatrixStack matrices, float x, float y, float alpha) {
        drawRoundedRect(matrices, x, y, 6.2f, 4.5f, 0, ColorUtils.applyAlpha(ColorUtils.rgba(0, 0, 0, 255), alpha));
    }

    public void drawTargetHudHeartFill(MatrixStack matrices, float x, float y, float width, int heartColor, int shadowColor) {
        drawShadow(matrices, x + 1, y + 1, width, 2f, 0, 8, shadowColor);
        drawRoundedRect(matrices, x, y, width + 1.2f, 4.5f, 0, heartColor);
    }

    public void drawKeyStrokeRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
        if (isLiquidGlassHudActive()) {
            drawHudLiquidGlass(matrices, x, y, width, height, radius, ColorUtils.getThemeColor(), true);
            return;
        }
        drawRoundedRect(matrices, x, y, width, height, radius, color);
    }

    public void drawRoundedRect(MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        ShaderProgram shader = RenderSystem.setShader(ShaderUtils.roundedRect);
        if (shader == null) {
            drawRoundedRectFallback(matrices, x, y, width, height, color);
            return;
        }

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform radiusUniform = shader.getUniform("Radius");
        GlUniform smoothnessUniform = shader.getUniform("Smoothness");

        float smoothness = 1.0f;
        if (sizeUniform != null) sizeUniform.set(width, height);
        if (radiusUniform != null) radiusUniform.set(topLeft, bottomLeft, topRight, bottomRight);
        if (smoothnessUniform != null) smoothnessUniform.set(smoothness);

        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) alpha = 255;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = alpha / 255f;

        float horizontalPadding = -smoothness / 2.0f + smoothness * 2.0f;
        float verticalPadding   =  smoothness / 2.0f + smoothness;
        float ax = x - horizontalPadding / 2.0f;
        float ay = y - verticalPadding / 2.0f;
        float aw = width + horizontalPadding;
        float ah = height + verticalPadding;

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, ax, ay, 0).color(r, g, b, a);
        buffer.vertex(matrix, ax, ay + ah, 0).color(r, g, b, a);
        buffer.vertex(matrix, ax + aw, ay + ah, 0).color(r, g, b, a);
        buffer.vertex(matrix, ax + aw, ay, 0).color(r, g, b, a);

        drawBuffer(buffer);
        RenderSystem.disableBlend();
    }

    private void drawRoundedRectFallback(MatrixStack matrices, float x, float y, float width, float height, int color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) {
            alpha = 255;
        }
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = alpha / 255f;

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y, 0).color(r, g, b, a);
        buffer.vertex(matrix, x, y + height, 0).color(r, g, b, a);
        buffer.vertex(matrix, x + width, y + height, 0).color(r, g, b, a);
        buffer.vertex(matrix, x + width, y, 0).color(r, g, b, a);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        drawBuffer(buffer);
    }

    public void drawRoundCircle(MatrixStack matrices, float x, float y, float radius, int color) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        drawRoundedRect(matrices, x - (radius / 2), y - (radius / 2), radius, radius, (radius / 2) - 0.5f, color);
    }

    public void drawRingArc(MatrixStack matrices, float x, float y, float size, float thickness, float startDeg, float endDeg, int color) {
        if (size <= 0f || thickness <= 0f) return;

        float radius = size / 2f;
        float start = (float) Math.toRadians(startDeg);
        float end = (float) Math.toRadians(endDeg);
        float twoPi = (float) (Math.PI * 2.0);
        if (start < 0f) start += twoPi;
        if (end < 0f) end += twoPi;
        while (end < start) end += twoPi;
        if (end - start <= 0.0001f) {
            end = start + twoPi;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.ringArc);

        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform radiusUniform = shader.getUniform("Radius");
        GlUniform thicknessUniform = shader.getUniform("Thickness");
        GlUniform startUniform = shader.getUniform("StartAngle");
        GlUniform endUniform = shader.getUniform("EndAngle");
        GlUniform smoothnessUniform = shader.getUniform("Smoothness");
        GlUniform colorModulatorUniform = shader.getUniform("ColorModulator");

        if (sizeUniform != null) sizeUniform.set(size, size);
        if (radiusUniform != null) radiusUniform.set(radius);
        if (thicknessUniform != null) thicknessUniform.set(thickness);
        if (startUniform != null) startUniform.set(start);
        if (endUniform != null) endUniform.set(end);
        if (smoothnessUniform != null) smoothnessUniform.set(Math.min(1.0f, thickness * 0.5f));
        if (colorModulatorUniform != null) colorModulatorUniform.set(1.0f, 1.0f, 1.0f, 1.0f);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) alpha = 255;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = alpha / 255f;

        buffer.vertex(matrix, x, y, 0).color(r, g, b, a);
        buffer.vertex(matrix, x, y + size, 0).color(r, g, b, a);
        buffer.vertex(matrix, x + size, y + size, 0).color(r, g, b, a);
        buffer.vertex(matrix, x + size, y, 0).color(r, g, b, a);

        RenderSystem.setShader(ShaderUtils.ringArc);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
    }

    public void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.gradientRect);
        if (shader == null) {
            drawRoundedRectFallback(matrices, x, y, width, height, topLeftColor);
            return;
        }

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform radiusUniform = shader.getUniform("Radius");
        GlUniform smoothnessUniform = shader.getUniform("Smoothness");
        GlUniform colorModulatorUniform = shader.getUniform("ColorModulator");
        GlUniform topLeftColorUniform = shader.getUniform("TopLeftColor");
        GlUniform bottomLeftColorUniform = shader.getUniform("BottomLeftColor");
        GlUniform topRightColorUniform = shader.getUniform("TopRightColor");
        GlUniform bottomRightColorUniform = shader.getUniform("BottomRightColor");

        if (sizeUniform != null) sizeUniform.set(width, height);
        if (radiusUniform != null) radiusUniform.set(topLeft, bottomLeft, topRight, bottomRight);
        if (smoothnessUniform != null) smoothnessUniform.set(1.0f);
        if (colorModulatorUniform != null) colorModulatorUniform.set(1.0f, 1.0f, 1.0f, 1.0f);

        int tlAlpha = (topLeftColor >> 24) & 0xFF;
        if (topLeftColorUniform != null) topLeftColorUniform.set(
                ((topLeftColor >> 16) & 0xFF) / 255f,
                ((topLeftColor >> 8) & 0xFF) / 255f,
                (topLeftColor & 0xFF) / 255f,
                tlAlpha / 255f
        );

        int blAlpha = (bottomLeftColor >> 24) & 0xFF;
        if (bottomLeftColorUniform != null) bottomLeftColorUniform.set(
                ((bottomLeftColor >> 16) & 0xFF) / 255f,
                ((bottomLeftColor >> 8) & 0xFF) / 255f,
                (bottomLeftColor & 0xFF) / 255f,
                blAlpha / 255f
        );

        int trAlpha = (topRightColor >> 24) & 0xFF;
        if (topRightColorUniform != null) topRightColorUniform.set(
                ((topRightColor >> 16) & 0xFF) / 255f,
                ((topRightColor >> 8) & 0xFF) / 255f,
                (topRightColor & 0xFF) / 255f,
                trAlpha / 255f
        );

        int brAlpha = (bottomRightColor >> 24) & 0xFF;
        if (bottomRightColorUniform != null) bottomRightColorUniform.set(
                ((bottomRightColor >> 16) & 0xFF) / 255f,
                ((bottomRightColor >> 8) & 0xFF) / 255f,
                (bottomRightColor & 0xFF) / 255f,
                brAlpha / 255f
        );

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        buffer.vertex(matrix, x, y, 0).texture(0, 0).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x, y + height, 0).texture(0, 1).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x + width, y + height, 0).texture(1, 1).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x + width, y, 0).texture(1, 0).color(1f, 1f, 1f, 1f);

        RenderSystem.setShader(ShaderUtils.gradientRect);
        drawBuffer(buffer);
    }

    public void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
    }

    public void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int topColor, int bottomColor) {
        drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, topColor, topColor, bottomColor, bottomColor);
    }

    public void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, int topColor, int bottomColor) {
        drawGradientRect(matrices, x, y, width, height, 0, 0, 0, 0, topColor, topColor, bottomColor, bottomColor);
    }

    public void drawGradientRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int leftColor, int rightColor, boolean horizontal) {
        if (horizontal) {
            drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, leftColor, rightColor, leftColor, rightColor);
        } else {
            drawGradientRect(matrices, x, y, width, height, radius, radius, radius, radius, leftColor, leftColor, rightColor, rightColor);
        }
    }

    public void drawRoundedRectOutline(MatrixStack matrices, float x, float y, float width, float height,
                                       float topLeft, float topRight, float bottomRight, float bottomLeft,
                                       float outline, int outlineColor) {
        drawRoundedRectOutline(matrices, x, y, width, height, topLeft, topRight, bottomRight, bottomLeft, outline,
                outlineColor, outlineColor, outlineColor, outlineColor);
    }

    public void drawRoundedRectOutline(MatrixStack matrices, float x, float y, float width, float height,
                                       float radius, float outline, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        drawRoundedRectOutline(matrices, x, y, width, height, radius, radius, radius, radius, outline,
                topLeftColor, topRightColor, bottomLeftColor, bottomRightColor);
    }

    public void drawRoundedRectOutline(MatrixStack matrices, float x, float y, float width, float height,
                                       float topLeft, float topRight, float bottomRight, float bottomLeft,
                                       float outline, int topLeftColor, int topRightColor, int bottomLeftColor, int bottomRightColor) {
        if (outline <= 0) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.roundedRectOutline);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform radiusUniform = shader.getUniform("Radius");
        GlUniform smoothnessUniform = shader.getUniform("Smoothness");
        GlUniform colorModulatorUniform = shader.getUniform("ColorModulator");
        GlUniform outlineUniform = shader.getUniform("Outline");
        GlUniform topLeftColorUniform = shader.getUniform("TopLeftColor");
        GlUniform bottomLeftColorUniform = shader.getUniform("BottomLeftColor");
        GlUniform topRightColorUniform = shader.getUniform("TopRightColor");
        GlUniform bottomRightColorUniform = shader.getUniform("BottomRightColor");

        if (sizeUniform != null) sizeUniform.set(width, height);
        if (radiusUniform != null) radiusUniform.set(topLeft, bottomLeft, topRight, bottomRight);
        if (smoothnessUniform != null) smoothnessUniform.set(1.0f);
        if (colorModulatorUniform != null) colorModulatorUniform.set(1.0f, 1.0f, 1.0f, 1.0f);
        if (outlineUniform != null) outlineUniform.set(outline);

        if (topLeftColorUniform != null) {
            int a = (topLeftColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            topLeftColorUniform.set(
                    ((topLeftColor >> 16) & 0xFF) / 255f,
                    ((topLeftColor >> 8) & 0xFF) / 255f,
                    (topLeftColor & 0xFF) / 255f,
                    a / 255f
            );
        }
        if (bottomLeftColorUniform != null) {
            int a = (bottomLeftColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            bottomLeftColorUniform.set(
                    ((bottomLeftColor >> 16) & 0xFF) / 255f,
                    ((bottomLeftColor >> 8) & 0xFF) / 255f,
                    (bottomLeftColor & 0xFF) / 255f,
                    a / 255f
            );
        }
        if (topRightColorUniform != null) {
            int a = (topRightColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            topRightColorUniform.set(
                    ((topRightColor >> 16) & 0xFF) / 255f,
                    ((topRightColor >> 8) & 0xFF) / 255f,
                    (topRightColor & 0xFF) / 255f,
                    a / 255f
            );
        }
        if (bottomRightColorUniform != null) {
            int a = (bottomRightColor >> 24) & 0xFF;
            if (a == 0) a = 255;
            bottomRightColorUniform.set(
                    ((bottomRightColor >> 16) & 0xFF) / 255f,
                    ((bottomRightColor >> 8) & 0xFF) / 255f,
                    (bottomRightColor & 0xFF) / 255f,
                    a / 255f
            );
        }

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        buffer.vertex(matrix, x, y, 0).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x, y + height, 0).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x + width, y + height, 0).color(1f, 1f, 1f, 1f);
        buffer.vertex(matrix, x + width, y, 0).color(1f, 1f, 1f, 1f);

        RenderSystem.setShader(ShaderUtils.roundedRectOutline);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
    }

    public void drawBlur(MatrixStack matrices, float x, float y, float width, float height,
                         float topLeft, float topRight, float bottomRight, float bottomLeft, int color) {
        if (BlurProgram.getBuffer2() == null) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.roundedTexture);

        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform radiusUniform = shader.getUniform("Radius");
        GlUniform smoothnessUniform = shader.getUniform("Smoothness");
        GlUniform colorModulatorUniform = shader.getUniform("ColorModulator");

        if (sizeUniform != null) sizeUniform.set(width, height);
        if (radiusUniform != null) radiusUniform.set(topLeft, bottomLeft, topRight, bottomRight);
        if (smoothnessUniform != null) smoothnessUniform.set(0.5f);
        if (colorModulatorUniform != null) colorModulatorUniform.set(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.setShaderTexture(0, BlurProgram.getTexture());
        RenderSystem.setShader(ShaderUtils.roundedTexture);

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        float u1 = x / screenWidth;
        float v1 = (screenHeight - y) / screenHeight;
        float u2 = (x + width) / screenWidth;
        float v2 = (screenHeight - y - height) / screenHeight;

        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) alpha = 255;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = alpha / 255f;

        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        builder.vertex(matrix, x, y, 0).texture(u1, v1).color(r, g, b, a);
        builder.vertex(matrix, x, y + height, 0).texture(u1, v2).color(r, g, b, a);
        builder.vertex(matrix, x + width, y + height, 0).texture(u2, v2).color(r, g, b, a);
        builder.vertex(matrix, x + width, y, 0).texture(u2, v1).color(r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.disableBlend();
    }

    public void drawBlur(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
        drawBlur(matrices, x, y, width, height, radius, radius, radius, radius, color);
    }

    public static void startGlow(float radius, int color, GlowCallback callback, MatrixStack matrices) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        if (a == 0) a = 255;
        GlowProgram.getInstance().begin(radius, new java.awt.Color(r, g, b, a));
        callback.render();
        GlowProgram.getInstance().end(matrices, callback);
    }

    public static void startGlow(float radius, float intensity, int color, GlowCallback callback, MatrixStack matrices) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        if (a == 0) a = 255;
        GlowProgram.getInstance().begin(radius, intensity, new java.awt.Color(r, g, b, a));
        callback.render();
        GlowProgram.getInstance().end(matrices, callback);
    }

    public void drawBlur(MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, float blurStrength, int color) {
        BlurProgram.getInstance().request();
        if (BlurProgram.getBuffer2() == null) return;

        BlurProgram.getInstance().setBlurOffset(blurStrength);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.roundedTexture);

        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform radiusUniform = shader.getUniform("Radius");
        GlUniform smoothnessUniform = shader.getUniform("Smoothness");
        GlUniform colorModulatorUniform = shader.getUniform("ColorModulator");

        if (sizeUniform != null) sizeUniform.set(width, height);
        if (radiusUniform != null) radiusUniform.set(topLeft, bottomLeft, topRight, bottomRight);
        if (smoothnessUniform != null) smoothnessUniform.set(0.5f);
        if (colorModulatorUniform != null) colorModulatorUniform.set(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.setShaderTexture(0, BlurProgram.getTexture());
        RenderSystem.setShader(ShaderUtils.roundedTexture);

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        float u1 = x / screenWidth;
        float v1 = (screenHeight - y) / screenHeight;
        float u2 = (x + width) / screenWidth;
        float v2 = (screenHeight - y - height) / screenHeight;

        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) alpha = 255;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = alpha / 255f;

        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        builder.vertex(matrix, x, y, 0).texture(u1, v1).color(r, g, b, a);
        builder.vertex(matrix, x, y + height, 0).texture(u1, v2).color(r, g, b, a);
        builder.vertex(matrix, x + width, y + height, 0).texture(u2, v2).color(r, g, b, a);
        builder.vertex(matrix, x + width, y, 0).texture(u2, v1).color(r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.disableBlend();
    }

    public void drawBlur(MatrixStack matrices, float x, float y, float width, float height, float radius, float blurStrength, int color) {
        drawBlur(matrices, x, y, width, height, radius, radius, radius, radius, blurStrength, color);
    }

    public void drawLiquidGlass(MatrixStack matrices, float x, float y, float width, float height, float topLeft, float topRight, float bottomRight, float bottomLeft, int color, float globalAlpha, float fresnelPower, int fresnelColor, float baseAlpha, boolean fresnelInvert, float fresnelMix, float distortStrength, float squirt, boolean clean) {
        int textureId;
        if (clean) {
            textureId = mc.getFramebuffer().getColorAttachment();
        } else {
            BlurProgram.getInstance().request();
            if (BlurProgram.getBuffer1() == null) return;
            textureId = BlurProgram.getTexture();
            if (textureId == 0) return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        RenderSystem.setShaderTexture(0, textureId);

        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(ShaderUtils.liquidGlass);

        GlUniform globalAlphaUniform = shader.getUniform("GlobalAlpha");
        GlUniform sizeUniform = shader.getUniform("Size");
        GlUniform radiusUniform = shader.getUniform("Radius");
        GlUniform smoothnessUniform = shader.getUniform("Smoothness");
        GlUniform fresnelPowerUniform = shader.getUniform("FresnelPower");
        GlUniform fresnelColorUniform = shader.getUniform("FresnelColor");
        GlUniform fresnelAlphaUniform = shader.getUniform("FresnelAlpha");
        GlUniform baseAlphaUniform = shader.getUniform("BaseAlpha");
        GlUniform fresnelInvertUniform = shader.getUniform("FresnelInvert");
        GlUniform fresnelMixUniform = shader.getUniform("FresnelMix");
        GlUniform distortStrengthUniform = shader.getUniform("DistortStrength");
        GlUniform cornerSmoothnessUniform = shader.getUniform("CornerSmoothness");
        GlUniform motionVectorUniform = shader.getUniform("MotionVector");
        GlUniform motionStrengthUniform = shader.getUniform("MotionStrength");

        float motionAmount = HudMotionTracker.getMotionAmount();
        float motionStrength = motionAmount * (0.0045f + distortStrength * 1.35f);
        float motionX = HudMotionTracker.getMotionVelX();
        float motionY = HudMotionTracker.getMotionVelY();

        if (globalAlphaUniform != null) globalAlphaUniform.set(globalAlpha);
        if (sizeUniform != null) sizeUniform.set(width, height);
        if (radiusUniform != null) radiusUniform.set(topLeft, bottomLeft, topRight, bottomRight);
        if (smoothnessUniform != null) smoothnessUniform.set(0.5f);
        if (fresnelPowerUniform != null) fresnelPowerUniform.set(fresnelPower);

        int fAlpha = (fresnelColor >> 24) & 0xFF;
        if (fAlpha == 0) fAlpha = 255;
        if (fresnelColorUniform != null) fresnelColorUniform.set(
                ((fresnelColor >> 16) & 0xFF) / 255f,
                ((fresnelColor >> 8) & 0xFF) / 255f,
                (fresnelColor & 0xFF) / 255f
        );
        if (fresnelAlphaUniform != null) fresnelAlphaUniform.set(fAlpha / 255f);
        if (baseAlphaUniform != null) baseAlphaUniform.set(baseAlpha);
        if (fresnelInvertUniform != null) fresnelInvertUniform.set(fresnelInvert ? 1 : 0);
        if (fresnelMixUniform != null) fresnelMixUniform.set(fresnelMix);
        if (distortStrengthUniform != null) distortStrengthUniform.set(distortStrength);
        if (cornerSmoothnessUniform != null) cornerSmoothnessUniform.set(squirt);
        if (motionVectorUniform != null) motionVectorUniform.set(motionX, motionY);
        if (motionStrengthUniform != null) motionStrengthUniform.set(motionStrength);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float screenWidth = (float) mc.getWindow().getFramebufferWidth();
        float screenHeight = (float) mc.getWindow().getFramebufferHeight();
        float scaleFactor = (float) mc.getWindow().getScaleFactor();

        float scaledX = x * scaleFactor;
        float scaledY = y * scaleFactor;
        float scaledW = width * scaleFactor;
        float scaledH = height * scaleFactor;

        float u1 = scaledX / screenWidth;
        float v1 = 1.0f - (scaledY / screenHeight);
        float u2 = (scaledX + scaledW) / screenWidth;
        float v2 = 1.0f - ((scaledY + scaledH) / screenHeight);

        int alpha = (color >> 24) & 0xFF;
        if (alpha == 0) alpha = 255;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = alpha / 255f;

        RenderSystem.setShader(ShaderUtils.liquidGlass);

        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        builder.vertex(matrix, x, y, 0).texture(u1, v1).color(r, g, b, a);
        builder.vertex(matrix, x, y + height, 0).texture(u1, v2).color(r, g, b, a);
        builder.vertex(matrix, x + width, y + height, 0).texture(u2, v2).color(r, g, b, a);
        builder.vertex(matrix, x + width, y, 0).texture(u2, v1).color(r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
