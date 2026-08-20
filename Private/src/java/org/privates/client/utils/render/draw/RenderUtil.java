package org.privates.client.utils.render.draw;

import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.Namespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.privates.client.Excellent;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.api.interfaces.IRender;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.render.Hud;
import org.privates.client.managers.module.settings.impl.ThemeSetting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.framebuffer.CustomFramebuffer;
import org.privates.client.utils.render.shader.ShaderManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR_TEX;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

@UtilityClass
public class RenderUtil implements IRender, IMinecraft {

    private static final Tessellator TESSELLATOR = Tessellator.getInstance();
    private static final Stack<Rect> scissorStack = new Stack<>();
    private static final LinkedHashMap<Integer, Integer> shadowCache = new LinkedHashMap<>(200, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
            return size() > 200;
        }
    };
    private static float lastScaleFactor = -1f;
    private static ShaderManager currentShader = null;
    private static final BufferBuilder hudBuffer = TESSELLATOR.getBuffer();
    private static boolean hudBatching = false;
    private static final HudSettings settings = new HudSettings();

    private static class Rect {
        float x, y, width, height;

        Rect(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private static class HudSettings {
        boolean blurEnabled = true;
        boolean shadowEnabled = true;
        int maxBlurRadius = 16;
    }

    public static void setHudSettings(boolean blur, boolean shadow, int maxRadius) {
        settings.blurEnabled = blur;
        settings.shadowEnabled = shadow;
        settings.maxBlurRadius = maxRadius;
    }

    public static void beginHudRender() {
        hudBatching = true;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        hudBuffer.begin(GL11.GL_QUADS, POSITION_COLOR);
    }

    public static void endHudRender() {
        TESSELLATOR.draw();
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        hudBatching = false;
    }

    public static class GLU {
        public static void startBlend() {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }

        public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
            return framebuffer == null || framebuffer.framebufferWidth != mw.getFramebufferWidth() || framebuffer.framebufferHeight != mw.getFramebufferHeight();
        }

        public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
            if (!Minecraft.isScreenMinimized()) {
                if (framebuffer == null || framebuffer.framebufferWidth != mc.getMainWindow().getWidth() || framebuffer.framebufferHeight != mc.getMainWindow().getHeight()) {
                    if (framebuffer != null) {
                        framebuffer.deleteFramebuffer();
                    }
                    return new Framebuffer(mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), true, true);
                }
                return framebuffer;
            }
            return null;
        }

        public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
            if (needsNewFramebuffer(framebuffer)) {
                if (framebuffer != null) {
                    framebuffer.deleteFramebuffer();
                }
                return new Framebuffer(mw.getFramebufferWidth(), mw.getFramebufferHeight(), depth, false);
            }
            return framebuffer;
        }

        public static void endBlend() {
            GlStateManager.disableBlend();
        }

        private static final boolean openGL14 = (mc.player != null);
        public static boolean extBlendFuncSeparate = (mc.player != null);

        public static void glBlendFunc(int sFactorRGB, int dFactorRGB, int sfactorAlpha, int dfactorAlpha) {
            if (openGL14) {
                if (extBlendFuncSeparate) {
                    EXTBlendFuncSeparate.glBlendFuncSeparateEXT(sFactorRGB, dFactorRGB, sfactorAlpha, dfactorAlpha);
                } else {
                    GL14.glBlendFuncSeparate(sFactorRGB, dFactorRGB, sfactorAlpha, dfactorAlpha);
                }
            } else {
                GL11.glBlendFunc(sFactorRGB, dFactorRGB);
            }
        }
    }

    public static void endBlend() {
        GlStateManager.disableBlend();
    }


    public void pushScissor(float x, float y, float width, float height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        Rect last = scissorStack.isEmpty() ? new Rect(0, 0, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight()) : scissorStack.peek();

        float newX = Math.max(x, last.x);
        float newY = Math.max(y, last.y);
        float newWidth = Math.min(x + width, last.x + last.width) - newX;
        float newHeight = Math.min(y + height, last.y + last.height) - newY;

        if (newWidth > 0 && newHeight > 0) {
            scissorStack.push(new Rect(newX, newY, newWidth, newHeight));
        }
        applyScissor();
    }

    public void popScissor() {
        if (!scissorStack.isEmpty()) {
            scissorStack.pop();
        }
        applyScissor();
    }

    private final List<Snowflake> snowflakes = new CopyOnWriteArrayList<>();
    private final Random random = new Random();
    private long lastUpdateTime = System.currentTimeMillis();
    private static String currentBackground = "background2.png";

    public static void setBackground(String backgroundName) {
        currentBackground = backgroundName;
    }

    public static String getCurrentBackground() {
        return currentBackground;
    }

    public static void drawBackground(MatrixStack matrixStack) {
        Theme hud = Theme.getInstance();
        int width = mc.getMainWindow().getScaledWidth();
        int height = mc.getMainWindow().getScaledHeight();
        RectUtil.drawRect(matrixStack, 0, 0, width, height, ColorUtil.multDark(hud.clientColor(), 0.08f));
        drawImage(matrixStack, new Namespaced("texture/" + currentBackground), 0, 0, width, height, true, hud.clientColor());

    }

    private void updateSnowflakes(int width, int height) {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000f;
        lastUpdateTime = currentTime;

        if (deltaTime > 0.1f) deltaTime = 0.016f;

        snowflakes.removeIf(s -> s.y > height + 50);

        while (snowflakes.size() < 80) {
            snowflakes.add(new Snowflake(
                    random.nextFloat() * width,
                    -random.nextFloat() * 50,
                    0.4f + random.nextFloat() * 0.8f,
                    0.5f + random.nextFloat(),
                    random.nextFloat() * (float) Math.PI * 2,
                    0.3f + random.nextFloat() * 0.5f,
                    random.nextFloat() * 0.5f
            ));
        }

        for (Snowflake s : snowflakes) {
            s.time += deltaTime;

            s.y += s.speed * deltaTime * 60f;

            s.x += (float) Math.sin(s.time * s.swingSpeed + s.offset) * s.swingAmplitude * deltaTime * 60f;

            s.rotation += s.rotationSpeed * deltaTime * 60f;
            if (s.rotation > 360f) s.rotation -= 360f;

            float targetAlpha = 1.0f - (s.y / height) * 0.3f;
            s.alpha += (targetAlpha - s.alpha) * 0.05f;
        }
    }

    private void renderSnowflakes(MatrixStack matrixStack, int width, int height) {
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        for (Snowflake s : snowflakes) {
            matrixStack.push();
            matrixStack.translate(s.x, s.y, 0);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(s.rotation));
            matrixStack.scale(s.size, s.size, 1);

            RenderUtil.bindTexture(new Namespaced("particle/snow.png"));
            RectUtil.drawRect(matrixStack, -8, -8, 16, 16, ColorUtil.multAlpha(-1, Math.max(0.3f, Math.min(0.85f, s.alpha))), true, true);

            matrixStack.pop();
        }

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        RenderSystem.enableAlphaTest();
    }

    private static class Snowflake {
        float x, y;
        float size;
        float speed;
        float offset;
        float swingSpeed;
        float swingAmplitude;
        float time;
        float rotation;
        float rotationSpeed;
        float alpha;

        Snowflake(float x, float y, float size, float speed, float offset, float swingSpeed, float swingAmplitude) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.offset = offset;
            this.swingSpeed = swingSpeed;
            this.swingAmplitude = swingAmplitude;
            this.time = 0f;
            this.rotation = 0f;
            this.rotationSpeed = (new Random().nextFloat() - 0.5f) * 2f;
            this.alpha = 0.7f;
        }
    }

    public void drawImage(MatrixStack matrix, ResourceLocation location, float x, float y, float width, float height, boolean bloom, int color) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        bindTexture(location);
        RectUtil.drawRect(matrix, x, y, width, height, color, bloom, true);
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    private void applyScissor() {
        if (scissorStack.isEmpty()) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            return;
        }

        Rect rect = scissorStack.peek();
        float scale = (float) mc.getMainWindow().getScaleFactor();
        if (scale != lastScaleFactor) {
            lastScaleFactor = scale;
        }
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (int) (rect.x * scale),
                (int) ((mc.getMainWindow().getScaledHeight() - (rect.y + rect.height)) * scale),
                (int) (rect.width * scale),
                (int) (rect.height * scale)
        );
    }

    public static void drawShadow(float x, float y, float width, float height, int radius, int color, int i) {
        if (!settings.shadowEnabled || radius < 3) {
            drawSimpleGradient(x, y, width, height, color, i);
            return;
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);
        GlStateManager.disableAlphaTest();
        GL11.glShadeModel(7425);

        x -= radius;
        y -= radius;
        width += radius * 2;
        height += radius * 2;
        x -= 0.25f;
        y += 0.25f;

        int identifier = Objects.hash((int) width, (int) height, radius, color);
        int textureId;

        if (shadowCache.containsKey(identifier)) {
            textureId = shadowCache.get(identifier);
            GlStateManager.bindTexture(textureId);
        } else {
            if (width <= 0) width = 1;
            if (height <= 0) height = 1;

            BufferedImage originalImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D graphics = originalImage.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(radius, radius, (int) (width - radius * 2), (int) (height - radius * 2));
            graphics.dispose();

            int effectiveRadius = Math.min(settings.maxBlurRadius, radius);
            BufferedImage blurredImage = applyBoxBlur(originalImage, effectiveRadius);
            DynamicTexture texture = new DynamicTexture(TextureUtils.toNativeImage(blurredImage));
            texture.setBlurMipmap(true, true);
            textureId = texture.getGlTextureId();
            shadowCache.put(identifier, textureId);
        }

        float[] startColorComponents = ColorUtil.getRGBAf(color);
        float[] i1 = ColorUtil.getRGBAf(i);

        if (hudBatching) {
            hudBuffer.pos(x, y, 0.0f).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).endVertex();
            hudBuffer.pos(x, y + height, 0.0f).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).endVertex();
            hudBuffer.pos(x + width, y + height, 0.0f).color(i1[0], i1[1], i1[2], i1[3]).endVertex();
            hudBuffer.pos(x + width, y, 0.0f).color(i1[0], i1[1], i1[2], i1[3]).endVertex();
        } else {
            BufferBuilder bufferBuilder = TESSELLATOR.getBuffer();
            bufferBuilder.begin(GL11.GL_QUADS, POSITION_COLOR_TEX);
            bufferBuilder.pos(x, y, 0.0f).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).tex(0.0f, 0.0f).endVertex();
            bufferBuilder.pos(x, y + height, 0.0f).color(startColorComponents[0], startColorComponents[1], startColorComponents[2], startColorComponents[3]).tex(0.0f, 1.0f).endVertex();
            bufferBuilder.pos(x + width, y + height, 0.0f).color(i1[0], i1[1], i1[2], i1[3]).tex(1.0f, 1.0f).endVertex();
            bufferBuilder.pos(x + width, y, 0.0f).color(i1[0], i1[1], i1[2], i1[3]).tex(1.0f, 0.0f).endVertex();
            TESSELLATOR.draw();
        }

        GlStateManager.enableAlphaTest();
        GL11.glShadeModel(7424);
        GlStateManager.bindTexture(0);
        GlStateManager.disableBlend();
    }

    private static void drawSimpleGradient(float x, float y, float width, float height, int color1, int color2) {
        float[] c1 = ColorUtil.getRGBAf(color1);
        float[] c2 = ColorUtil.getRGBAf(color2);
        if (hudBatching) {
            hudBuffer.pos(x, y, 0).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
            hudBuffer.pos(x + width, y, 0).color(c2[0], c2[1], c2[2], c2[3]).endVertex();
            hudBuffer.pos(x + width, y + height, 0).color(c2[0], c2[1], c2[2], c2[3]).endVertex();
            hudBuffer.pos(x, y + height, 0).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
        } else {
            BufferBuilder bb = TESSELLATOR.getBuffer();
            bb.begin(GL11.GL_QUADS, POSITION_COLOR);
            bb.pos(x, y, 0).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
            bb.pos(x + width, y, 0).color(c2[0], c2[1], c2[2], c2[3]).endVertex();
            bb.pos(x + width, y + height, 0).color(c2[0], c2[1], c2[2], c2[3]).endVertex();
            bb.pos(x, y + height, 0).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
            TESSELLATOR.draw();
        }
    }

    private static BufferedImage applyBoxBlur(BufferedImage source, int radius) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage blurred = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);

        int[] pixels = source.getRGB(0, 0, width, height, null, 0, width);
        int[] result = new int[pixels.length];
        int windowSize = 2 * radius + 1;
        float divisor = 1f / windowSize;

        for (int pass = 0; pass < 2; pass++) {
            boxBlurHorizontal(pixels, result, width, height, radius, windowSize, divisor);
            boxBlurVertical(result, pixels, width, height, radius, windowSize, divisor);
        }

        blurred.setRGB(0, 0, width, height, pixels, 0, width);
        return blurred;
    }

    private static void boxBlurHorizontal(int[] src, int[] dst, int width, int height, int radius, int windowSize, float divisor) {
        for (int y = 0; y < height; y++) {
            int index = y * width;
            int sumA = 0, sumR = 0, sumG = 0, sumB = 0;

            for (int x = 0; x < windowSize && x < width; x++) {
                int pixel = src[index + x];
                sumA += (pixel >> 24) & 0xFF;
                sumR += (pixel >> 16) & 0xFF;
                sumG += (pixel >> 8) & 0xFF;
                sumB += pixel & 0xFF;
            }

            dst[index] = ((int) (sumA * divisor) << 24) | ((int) (sumR * divisor) << 16) | ((int) (sumG * divisor) << 8) | (int) (sumB * divisor);

            for (int x = windowSize; x < width; x++) {
                if (x - windowSize >= 0) {
                    int prevPixel = src[index + x - windowSize];
                    sumA -= (prevPixel >> 24) & 0xFF;
                    sumR -= (prevPixel >> 16) & 0xFF;
                    sumG -= (prevPixel >> 8) & 0xFF;
                    sumB -= prevPixel & 0xFF;
                }
                int nextPixel = src[index + x];
                sumA += (nextPixel >> 24) & 0xFF;
                sumR += (nextPixel >> 16) & 0xFF;
                sumG += (nextPixel >> 8) & 0xFF;
                sumB += nextPixel & 0xFF;

                dst[index + x] = ((int) (sumA * divisor) << 24) | ((int) (sumR * divisor) << 16) | ((int) (sumG * divisor) << 8) | (int) (sumB * divisor);
            }
        }
    }

    public void clientStyledRectDark(MatrixStack matrix, float x, float y, float width, float height, float alpha, float roundOf) {
        Theme theme = Theme.getInstance();

        if (theme != null && theme.blurStrength().getValue() > 0) {
            int actualBlurStrength = theme.blurStrength().getValue().intValue();
            RenderUtil.Shadow.drawShadow(matrix, x - roundOf / 2, y - roundOf / 2, width + roundOf, height + roundOf, 12, ColorUtil.replAlpha(ColorUtil.getColor(0), (float) Math.pow(alpha, 0.3)));

            BlurRenderer2.registerRenderCall(() -> {
                RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, -1, Round.of(roundOf));
            });
            BlurRenderer2.draw(actualBlurStrength);
        }

        RenderUtil.Rounded.smooth(matrix, x, y, width + 1, height, ColorUtil.getColor(15, alpha * 0.3F), Round.of(roundOf));
    }

    public static void drawMainMenuShader(float width, float height, int mouseX, int mouseY, float al) {


    }

    private static void boxBlurVertical(int[] src, int[] dst, int width, int height, int radius, int windowSize, float divisor) {
        for (int x = 0; x < width; x++) {
            int sumA = 0, sumR = 0, sumG = 0, sumB = 0;

            for (int y = 0; y < windowSize && y < height; y++) {
                int pixel = src[y * width + x];
                sumA += (pixel >> 24) & 0xFF;
                sumR += (pixel >> 16) & 0xFF;
                sumG += (pixel >> 8) & 0xFF;
                sumB += pixel & 0xFF;
            }

            dst[x] = ((int) (sumA * divisor) << 24) | ((int) (sumR * divisor) << 16) | ((int) (sumG * divisor) << 8) | (int) (sumB * divisor);

            for (int y = windowSize; y < height; y++) {
                if (y - windowSize >= 0) {
                    int prevPixel = src[(y - windowSize) * width + x];
                    sumA -= (prevPixel >> 24) & 0xFF;
                    sumR -= (prevPixel >> 16) & 0xFF;
                    sumG -= (prevPixel >> 8) & 0xFF;
                    sumB -= prevPixel & 0xFF;
                }
                int nextPixel = src[y * width + x];
                sumA += (nextPixel >> 24) & 0xFF;
                sumR += (nextPixel >> 16) & 0xFF;
                sumG += (nextPixel >> 8) & 0xFF;
                sumB += nextPixel & 0xFF;

                dst[y * width + x] = ((int) (sumA * divisor) << 24) | ((int) (sumR * divisor) << 16) | ((int) (sumG * divisor) << 8) | (int) (sumB * divisor);
            }
        }
    }

    public void drawArc(MatrixStack matrix, float x, float y, float radius, float start, float end, float width, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(3);
        float step = 6.0f;
        for (float i = end; i >= start; i -= step) {
            GL11.glVertex2f((float) (x + Math.cos(i * Math.PI / 180.0) * (radius * 1.001f)), (float) (y + Math.sin(i * Math.PI / 180.0) * (radius * 1.001f)));
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    public static void drawStrokedCircle(MatrixStack matrixStack, float x, float y, float radius, float width, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(width);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        int segments = Math.max(12, (int) (radius / 5f));

        bufferBuilder.begin(GL11.GL_LINE_LOOP, POSITION_COLOR);
        for (int i = 0; i < segments; i++) {
            double angle = Math.toRadians(i * 360.0 / segments);
            float vx = x + (float) (Math.cos(angle) * radius);
            float vy = y + (float) (Math.sin(angle) * radius);
            bufferBuilder.pos(matrixStack.getLast().getMatrix(), vx, vy, 0).color(red, green, blue, alpha).endVertex();
        }
        tessellator.draw();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public void drawShaderBackground(MatrixStack matrixStack, float alpha) {
        RectUtil.drawRect(matrixStack, 0, 0, mw.getScaledWidth(), mw.getScaledHeight(), ColorUtil.multAlpha(ColorUtil.multDark(Theme.getInstance().clientColor(), 0.025F), alpha));

        final ShaderManager shader = ShaderManager.mainmenuShader;
        if (currentShader != shader) {
            if (currentShader != null) currentShader.unload();
            shader.load();
            currentShader = shader;
        }

        shader.setUniformf("time", (System.currentTimeMillis() - Excellent.inst().loadTime()) / (100F * Theme.getInstance().getSpeed()));
        shader.setUniformf("alpha", alpha);
        shader.setUniformf("color", ColorUtil.getRGBAf(Theme.getInstance().clientColor()));
        shader.setUniformf("resolution", mw.getFramebufferWidth(), mw.getFramebufferHeight());

        start();
        CustomFramebuffer.drawQuads(matrixStack, 0, 0, mw.getScaledWidth(), mw.getScaledHeight());
        stop();
        if (currentShader == shader) {
            currentShader.unload();
            currentShader = null;
        }
    }

    @UtilityClass
    public static class CustomStyled {

        private final ArrayList<Runnable> rects = new ArrayList<>();
        private final ArrayList<Runnable> liquidGlassRects = new ArrayList<>();
        private final ArrayList<Runnable> shaderRects = new ArrayList<>();
        private float gradientOffset = 0f;

        public void register(Runnable rect) {
            rects.add(rect);
        }

        public void draw(MatrixStack matrix, float x, float y, float width, float height, Round round) {
            draw(matrix, x, y, width, height, round, false, 0);
        }

        public void draw(MatrixStack matrix, float x, float y, float width, float height, Round round, boolean useBlur, float blurStrength) {
            Theme theme = Theme.getInstance();

            if (theme != null && theme.isLiquidGlassEnabled()) {
                float avgRadius = (round.LB + round.LT + round.RB + round.RT) / 4f;
                LiquidGlassRenderer.captureBackground();
                LiquidGlassRenderer.drawLiquidGlass(matrix, x, y, width, height, avgRadius / 5,
                        theme.liquidGlassStrength().getValue() * 1000f,
                        theme.blurStrength().getValue() / 33f,
                        ColorUtil.getColor(255, 255, 255, 0f));
            } else {
                if (Excellent.inst().moduleManager().get(Hud.class).blurOptimization().getValue()) {
                    int bgAlpha = theme != null ? theme.backgroundAlpha().getValue().intValue() : 200;
                    int[] colors = getThemeGradientColors(bgAlpha);
                    RenderUtil.Rounded.drawGradientRound(matrix, x, y, width, height, round, colors[0], colors[1], colors[2], colors[3]);
                    int outlineColor = ColorUtil.getColor(255, 255, 255, 15);
                    RenderUtil.Rounded.drawRoundOutline(matrix, x, y, width, height, round, 0.5f, outlineColor);
                } else {
                    int actualBlurStrength = theme != null ? theme.blurStrength().getValue().intValue() : (int) blurStrength;
                    String shaderType = theme != null ? theme.shaderType().getValue() : "Blur";

                    if (useBlur && settings.blurEnabled && theme != null && theme.blurStrength().getValue().floatValue() > 0) {
                        switch (shaderType) {
                            case "Искаженное стекло" -> {
                                DistortedGlassRenderer.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), round));
                                DistortedGlassRenderer.draw(1.0f);
                            }
                            case "PixelBlur" -> {
                                PixelatedGlassRenderer.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), round));
                                PixelatedGlassRenderer.draw(4.0f);
                            }
                            case "Алкоголь" -> {
                                AnimatedWavesRenderer.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), round));
                                AnimatedWavesRenderer.draw(1.5f);
                            }
                            default -> {
                                BlurRenderer.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, -1, round));
                                BlurRenderer.draw(actualBlurStrength);
                            }
                        }
                    }

                    int bgAlpha = theme != null ? theme.backgroundAlpha().getValue().intValue() : 200;
                    int[] colors = getThemeGradientColors(bgAlpha);
                    RenderUtil.Rounded.drawGradientRound(matrix, x, y, width, height, round, colors[0], colors[1], colors[2], colors[3]);
                    int outlineColor = ColorUtil.getColor(255, 255, 255, 15);
                    RenderUtil.Rounded.drawRoundOutline(matrix, x, y, width, height, round, 0.5f, outlineColor);
                }
            }
        }

        public void drawNew(MatrixStack matrix, float x, float y, float width, float height, Round round, boolean useBlur, float blurStrength) {
            Theme theme = Theme.getInstance();
            float avgRadius = (round.LB + round.LT + round.RB + round.RT) / 4f;

            if (theme != null && theme.isLiquidGlassEnabled()) {
                LiquidGlassRenderer.captureBackground();
                LiquidGlassRenderer.drawLiquidGlass(matrix, x, y, width, height, avgRadius / 5,
                        theme.liquidGlassStrength().getValue() * 1000f,
                        theme.blurStrength().getValue() / 33f,
                        ColorUtil.getColor(255, 255, 255, 0f));
            } else {
                if (Excellent.inst().moduleManager().get(Hud.class).blurOptimization().getValue()) {
                    int bgAlpha = theme != null ? theme.backgroundAlpha().getValue().intValue() : 200;
                    int[] colors = getThemeGradientColors(bgAlpha);
                    RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, colors[0], colors[1], colors[2], colors[3], round);
                    int outlineColor = ColorUtil.getColor(255, 255, 255, 15);
                    RenderUtil.Rounded.roundedOutline(matrix, x, y, width, height, 0.5f, outlineColor, round);
                } else {
                    int actualBlurStrength = theme != null ? theme.blurStrength().getValue().intValue() : (int) blurStrength;
                    String shaderType = theme != null ? theme.shaderType().getValue() : "Blur";

                    if (useBlur && settings.blurEnabled && theme != null && theme.blurStrength().getValue().floatValue() > 0) {
                        switch (shaderType) {
                            case "Искаженное стекло" -> {
                                DistortedGlassRenderer.registerRenderCall(() -> {
                                    RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), round);
                                });
                                DistortedGlassRenderer.draw(1.0f);
                            }
                            case "PixelBlur" -> {
                                PixelatedGlassRenderer.registerRenderCall(() -> {
                                    RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), round);
                                });
                                PixelatedGlassRenderer.draw(4.0f);
                            }
                            case "Алкоголь" -> {
                                AnimatedWavesRenderer.registerRenderCall(() -> {
                                    RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), round);
                                });
                                AnimatedWavesRenderer.draw(1.5f);
                            }
                            default -> {
                                BlurRenderer.registerRenderCall(() -> {
                                    RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, -1, round);
                                });
                                BlurRenderer.draw(actualBlurStrength);
                            }
                        }
                    }

                    int bgAlpha = theme != null ? theme.backgroundAlpha().getValue().intValue() : 200;
                    int[] colors = getThemeGradientColors(bgAlpha);
                    RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, colors[0], colors[1], colors[2], colors[3], round);

                    int outlineColor = ColorUtil.getColor(255, 255, 255, 15);
                    RenderUtil.Rounded.roundedOutline(matrix, x, y, width, height, 0.5f, outlineColor, round);
                }
            }
        }

        public void drawGui(MatrixStack matrix, float x, float y, float width, float height, float radius, boolean useBlur, float blurStrength) {
            Theme theme = Theme.getInstance();

            if (theme != null && theme.isLiquidGlassEnabled()) {
                LiquidGlassRenderer.captureBackground();
                LiquidGlassRenderer.drawLiquidGlass(matrix, x, y, width, height, radius / 5,
                        theme.liquidGlassStrength().getValue() * 1000f,
                        theme.blurStrength().getValue() / 33f,
                        ColorUtil.getColor(255, 255, 255, 0f));
            } else {
                int actualBlurStrength = theme != null ? theme.blurStrength().getValue().intValue() : (int) blurStrength;
                String shaderType = theme != null ? theme.shaderType().getValue() : "Blur";

                if (useBlur && settings.blurEnabled && theme != null && theme.blurStrength().getValue().floatValue() > 0) {
                    switch (shaderType) {
                        case "Искаженное стекло" -> {
                            DistortedGlassRenderer.registerRenderCall(() -> {
                                RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), Round.of(radius));
                            });
                            DistortedGlassRenderer.draw(1.0f);
                        }
                        case "PixelBlur" -> {
                            PixelatedGlassRenderer.registerRenderCall(() -> {
                                RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), Round.of(radius));
                            });
                            PixelatedGlassRenderer.draw(4.0f);
                        }
                        case "Алкоголь" -> {
                            AnimatedWavesRenderer.registerRenderCall(() -> {
                                RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), Round.of(radius));
                            });
                            AnimatedWavesRenderer.draw(1.5f);
                        }
                        default -> {
                            BlurRenderer2.registerRenderCall(() -> {
                                RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, -1, Round.of(radius));
                            });
                            BlurRenderer2.draw(actualBlurStrength);
                        }
                    }
                }

                int bgAlpha = theme != null ? theme.backgroundAlpha().getValue().intValue() : 130;
                int[] colors = getThemeGradientColors(bgAlpha);
                RenderUtil.Rounded.drawGradientRound(matrix, x, y, width, height, radius, colors[0], colors[1], colors[2], colors[3]);

                int outlineColor = ColorUtil.getColor(255, 255, 255, 15);
                RenderUtil.Rounded.drawRoundOutline(matrix, x, y, width, height, radius, 0.5f, outlineColor);
            }
        }

        public void renderBlur(int blurStrength) {
            Theme theme = Theme.getInstance();

            if (theme != null && theme.isLiquidGlassEnabled()) {
                for (Runnable runnable : liquidGlassRects)
                    runnable.run();
            } else {
                String shaderType = theme != null ? theme.shaderType().getValue() : "Blur";
                switch (shaderType) {
                    case "Искаженное стекло" -> {
                        DistortedGlassRenderer.registerRenderCall(() -> {
                            for (Runnable runnable : shaderRects)
                                runnable.run();
                        });
                        DistortedGlassRenderer.draw(1.0f);
                    }
                    case "PixelBlur" -> {
                        PixelatedGlassRenderer.registerRenderCall(() -> {
                            for (Runnable runnable : shaderRects)
                                runnable.run();
                        });
                        PixelatedGlassRenderer.draw(4.0f);
                    }
                    case "Алкоголь" -> {
                        AnimatedWavesRenderer.registerRenderCall(() -> {
                            for (Runnable runnable : shaderRects)
                                runnable.run();
                        });
                        AnimatedWavesRenderer.draw(1.5f);
                    }
                    default -> {
                        BlurRenderer.registerRenderCall(() -> {
                            for (Runnable runnable : rects)
                                runnable.run();
                        });
                        BlurRenderer.draw(blurStrength);
                    }
                }
            }
        }

        public void renderRects() {
            Theme theme = Theme.getInstance();

            if (theme != null && theme.isLiquidGlassEnabled()) {
                for (Runnable runnable : liquidGlassRects)
                    runnable.run();
            } else {
                String shaderType = theme != null ? theme.shaderType().getValue() : "Blur";
                if (shaderType.equals("Blur")) {
                    for (Runnable runnable : rects)
                        runnable.run();
                }
            }
        }

        public void clear() {
            rects.clear();
            liquidGlassRects.clear();
            shaderRects.clear();
        }

        private void updateGradientAnimation() {
            Theme theme = Theme.getInstance();
            if (theme == null || !theme.animatedGradient().getValue()) {
                return;
            }

            float speed = switch (theme.animationSpeed().getValue()) {
                case "Slow" -> 0.00015f;
                case "Medium" -> 0.0002f;
                case "Fast" -> 0.0005f;
                case "Very Fast" -> 0.0007f;
                default -> 0.0005f;
            };

            gradientOffset += speed;
            if (gradientOffset >= 2.0f) {
                gradientOffset -= 2.0f;
            }
        }

        private int[] getThemeGradientColors(int bgAlpha) {
            Theme theme = Theme.getInstance();
            if (theme == null) {
                return new int[]{
                        ColorUtil.rgba(25, 25, 30, bgAlpha),
                        ColorUtil.rgba(20, 20, 25, bgAlpha),
                        ColorUtil.rgba(20, 20, 25, bgAlpha),
                        ColorUtil.rgba(15, 15, 20, bgAlpha)
                };
            }

            updateGradientAnimation();

            if (theme.customThemeEnabled().getValue()) {
                int color1 = theme.customColor1().getValue();
                int color2 = theme.customColor2().getValue();

                if (theme.animatedGradient().getValue()) {
                    return buildAnimatedGradientArray(color1, color2, bgAlpha, theme.getGradientRatio());
                }

                return buildGradientArray(color1, color2, bgAlpha, theme.getGradientRatio());
            }

            ThemeSetting.ThemePreset preset = theme.themePreset().getValue();
            String presetName = preset != null ? preset.getName() : "Midnight";

            if (theme.animatedGradient().getValue() && preset != null && preset.isGradient()) {
                return buildAnimatedGradientArray(preset.getColor1(), preset.getColor2(), bgAlpha, theme.getGradientRatio());
            }

            return themeColors(presetName, bgAlpha);
        }

        private int[] buildAnimatedGradientArray(int color1, int color2, int alpha, float ratio) {
            float offset = gradientOffset;

            int topLeft = interpolateGradient(color1, color2, offset, alpha);
            int bottomLeft = interpolateGradient(color1, color2, offset + 0.5f, alpha);
            int topRight = interpolateGradient(color1, color2, offset + 0.5f, alpha);
            int bottomRight = interpolateGradient(color1, color2, offset + 1.0f, alpha);

            return new int[]{topLeft, bottomLeft, topRight, bottomRight};
        }

        private int interpolateGradient(int color1, int color2, float position, int alpha) {
            position = position % 2.0f;

            float progress;
            if (position < 1.0f) {
                progress = (float) Math.sin(position * Math.PI / 2.0);
            } else {
                progress = 1.0f - (float) Math.sin((position - 1.0f) * Math.PI / 2.0);
            }

            int r1 = (color1 >> 16) & 0xFF;
            int g1 = (color1 >> 8) & 0xFF;
            int b1 = color1 & 0xFF;

            int r2 = (color2 >> 16) & 0xFF;
            int g2 = (color2 >> 8) & 0xFF;
            int b2 = color2 & 0xFF;

            int r = (int) (r1 + (r2 - r1) * progress);
            int g = (int) (g1 + (g2 - g1) * progress);
            int b = (int) (b1 + (b2 - b1) * progress);

            return ColorUtil.rgba(r, g, b, alpha);
        }

        private int[] buildGradientArray(int color1, int color2, int alpha, float ratio) {
            int r1 = (color1 >> 16) & 0xFF;
            int g1 = (color1 >> 8) & 0xFF;
            int b1 = color1 & 0xFF;

            int r2 = (color2 >> 16) & 0xFF;
            int g2 = (color2 >> 8) & 0xFF;
            int b2 = color2 & 0xFF;

            int midR = (int) (r1 * ratio + r2 * (1 - ratio));
            int midG = (int) (g1 * ratio + g2 * (1 - ratio));
            int midB = (int) (b1 * ratio + b2 * (1 - ratio));

            int darkR = Math.max(0, (int) (midR * 0.85f));
            int darkG = Math.max(0, (int) (midG * 0.85f));
            int darkB = Math.max(0, (int) (midB * 0.85f));

            return new int[]{
                    ColorUtil.rgba(r1, g1, b1, alpha),
                    ColorUtil.rgba(midR, midG, midB, alpha),
                    ColorUtil.rgba(midR, midG, midB, alpha),
                    ColorUtil.rgba(darkR, darkG, darkB, alpha)
            };
        }

        private int[] themeColors(String presetName, int bgAlpha) {
            int topLeft, bottomLeft, topRight, bottomRight;
            switch (presetName) {
                case "Midnight" -> {
                    topLeft = ColorUtil.rgba(25, 27, 35, bgAlpha);
                    bottomLeft = ColorUtil.rgba(20, 22, 30, bgAlpha);
                    topRight = ColorUtil.rgba(20, 22, 30, bgAlpha);
                    bottomRight = ColorUtil.rgba(15, 17, 25, bgAlpha);
                }
                case "Charcoal" -> {
                    topLeft = ColorUtil.rgba(30, 30, 33, bgAlpha);
                    bottomLeft = ColorUtil.rgba(25, 25, 28, bgAlpha);
                    topRight = ColorUtil.rgba(25, 25, 28, bgAlpha);
                    bottomRight = ColorUtil.rgba(20, 20, 23, bgAlpha);
                }
                case "Slate" -> {
                    topLeft = ColorUtil.rgba(35, 37, 43, bgAlpha);
                    bottomLeft = ColorUtil.rgba(30, 32, 38, bgAlpha);
                    topRight = ColorUtil.rgba(30, 32, 38, bgAlpha);
                    bottomRight = ColorUtil.rgba(25, 27, 33, bgAlpha);
                }
                case "Graphite" -> {
                    topLeft = ColorUtil.rgba(33, 35, 40, bgAlpha);
                    bottomLeft = ColorUtil.rgba(28, 30, 35, bgAlpha);
                    topRight = ColorUtil.rgba(28, 30, 35, bgAlpha);
                    bottomRight = ColorUtil.rgba(23, 25, 30, bgAlpha);
                }
                case "Steel" -> {
                    topLeft = ColorUtil.rgba(40, 43, 47, bgAlpha);
                    bottomLeft = ColorUtil.rgba(35, 38, 42, bgAlpha);
                    topRight = ColorUtil.rgba(35, 38, 42, bgAlpha);
                    bottomRight = ColorUtil.rgba(30, 33, 37, bgAlpha);
                }
                case "Onyx" -> {
                    topLeft = ColorUtil.rgba(27, 29, 33, bgAlpha);
                    bottomLeft = ColorUtil.rgba(22, 24, 28, bgAlpha);
                    topRight = ColorUtil.rgba(22, 24, 28, bgAlpha);
                    bottomRight = ColorUtil.rgba(17, 19, 23, bgAlpha);
                }
                case "Shadow" -> {
                    topLeft = ColorUtil.rgba(23, 25, 30, bgAlpha);
                    bottomLeft = ColorUtil.rgba(18, 20, 25, bgAlpha);
                    topRight = ColorUtil.rgba(18, 20, 25, bgAlpha);
                    bottomRight = ColorUtil.rgba(13, 15, 20, bgAlpha);
                }
                case "Carbon" -> {
                    topLeft = ColorUtil.rgba(29, 31, 35, bgAlpha);
                    bottomLeft = ColorUtil.rgba(24, 26, 30, bgAlpha);
                    topRight = ColorUtil.rgba(24, 26, 30, bgAlpha);
                    bottomRight = ColorUtil.rgba(19, 21, 25, bgAlpha);
                }
                case "Deep Navy" -> {
                    topLeft = ColorUtil.rgba(20, 23, 33, bgAlpha);
                    bottomLeft = ColorUtil.rgba(15, 18, 28, bgAlpha);
                    topRight = ColorUtil.rgba(15, 18, 28, bgAlpha);
                    bottomRight = ColorUtil.rgba(25, 30, 45, bgAlpha);
                }
                case "Subtle Purple" -> {
                    topLeft = ColorUtil.rgba(33, 30, 40, bgAlpha);
                    bottomLeft = ColorUtil.rgba(28, 25, 35, bgAlpha);
                    topRight = ColorUtil.rgba(28, 25, 35, bgAlpha);
                    bottomRight = ColorUtil.rgba(35, 30, 45, bgAlpha);
                }
                case "Soft Blue" -> {
                    topLeft = ColorUtil.rgba(25, 30, 40, bgAlpha);
                    bottomLeft = ColorUtil.rgba(20, 25, 35, bgAlpha);
                    topRight = ColorUtil.rgba(20, 25, 35, bgAlpha);
                    bottomRight = ColorUtil.rgba(25, 32, 42, bgAlpha);
                }
                case "Muted Violet" -> {
                    topLeft = ColorUtil.rgba(35, 33, 43, bgAlpha);
                    bottomLeft = ColorUtil.rgba(30, 28, 38, bgAlpha);
                    topRight = ColorUtil.rgba(30, 28, 38, bgAlpha);
                    bottomRight = ColorUtil.rgba(38, 32, 48, bgAlpha);
                }
                case "Gentle Teal" -> {
                    topLeft = ColorUtil.rgba(27, 35, 37, bgAlpha);
                    bottomLeft = ColorUtil.rgba(22, 30, 32, bgAlpha);
                    topRight = ColorUtil.rgba(22, 30, 32, bgAlpha);
                    bottomRight = ColorUtil.rgba(28, 38, 40, bgAlpha);
                }
                case "Calm Green" -> {
                    topLeft = ColorUtil.rgba(30, 37, 33, bgAlpha);
                    bottomLeft = ColorUtil.rgba(25, 32, 28, bgAlpha);
                    topRight = ColorUtil.rgba(25, 32, 28, bgAlpha);
                    bottomRight = ColorUtil.rgba(30, 38, 32, bgAlpha);
                }
                case "Warm Gray" -> {
                    topLeft = ColorUtil.rgba(40, 37, 35, bgAlpha);
                    bottomLeft = ColorUtil.rgba(35, 32, 30, bgAlpha);
                    topRight = ColorUtil.rgba(35, 32, 30, bgAlpha);
                    bottomRight = ColorUtil.rgba(42, 38, 35, bgAlpha);
                }
                case "Cool Gray" -> {
                    topLeft = ColorUtil.rgba(35, 37, 40, bgAlpha);
                    bottomLeft = ColorUtil.rgba(30, 32, 35, bgAlpha);
                    topRight = ColorUtil.rgba(30, 32, 35, bgAlpha);
                    bottomRight = ColorUtil.rgba(35, 38, 42, bgAlpha);
                }
                case "Dusk" -> {
                    topLeft = ColorUtil.rgba(30, 27, 35, bgAlpha);
                    bottomLeft = ColorUtil.rgba(25, 22, 30, bgAlpha);
                    topRight = ColorUtil.rgba(25, 22, 30, bgAlpha);
                    bottomRight = ColorUtil.rgba(35, 28, 40, bgAlpha);
                }
                case "Dawn" -> {
                    topLeft = ColorUtil.rgba(37, 33, 30, bgAlpha);
                    bottomLeft = ColorUtil.rgba(32, 28, 25, bgAlpha);
                    topRight = ColorUtil.rgba(32, 28, 25, bgAlpha);
                    bottomRight = ColorUtil.rgba(40, 35, 30, bgAlpha);
                }
                case "Twilight" -> {
                    topLeft = ColorUtil.rgba(27, 30, 37, bgAlpha);
                    bottomLeft = ColorUtil.rgba(22, 25, 32, bgAlpha);
                    topRight = ColorUtil.rgba(22, 25, 32, bgAlpha);
                    bottomRight = ColorUtil.rgba(28, 32, 42, bgAlpha);
                }
                case "Overcast" -> {
                    topLeft = ColorUtil.rgba(40, 40, 43, bgAlpha);
                    bottomLeft = ColorUtil.rgba(35, 35, 38, bgAlpha);
                    topRight = ColorUtil.rgba(35, 35, 38, bgAlpha);
                    bottomRight = ColorUtil.rgba(42, 42, 45, bgAlpha);
                }
                default -> {
                    topLeft = ColorUtil.rgba(25, 27, 35, bgAlpha);
                    bottomLeft = ColorUtil.rgba(20, 22, 30, bgAlpha);
                    topRight = ColorUtil.rgba(20, 22, 30, bgAlpha);
                    bottomRight = ColorUtil.rgba(15, 17, 25, bgAlpha);
                }
            }
            return new int[]{topLeft, bottomLeft, topRight, bottomRight};
        }

        public void drawKeybinds(MatrixStack matrix, float x, float y, float width, float height, float radius, boolean useBlur, float blurStrength, boolean onlyTitleGradient, boolean isTitle, float titleHeight) {
            Theme theme = Theme.getInstance();

            if (theme != null && theme.isLiquidGlassEnabled()) {
                LiquidGlassRenderer.captureBackground();
                LiquidGlassRenderer.drawLiquidGlass(matrix, x, y, width, height, radius / 6f,
                        theme.liquidGlassStrength().getValue() * 1000f,
                        theme.blurStrength().getValue() / 33f,
                        ColorUtil.getColor(255, 255, 255, .3f));
            } else {
                int actualBlurStrength = theme != null ? theme.blurStrength().getValue().intValue() : (int) blurStrength;
                String shaderType = theme != null ? theme.shaderType().getValue() : "Blur";

                if (useBlur && settings.blurEnabled && theme != null && theme.blurStrength().getValue().floatValue() > 0) {
                    switch (shaderType) {
                        case "Искаженное стекло" -> {
                            DistortedGlassRenderer.registerRenderCall(() -> {
                                RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), Round.of(radius));
                            });
                            DistortedGlassRenderer.draw(1.0f);
                        }
                        case "PixelBlur" -> {
                            PixelatedGlassRenderer.registerRenderCall(() -> {
                                RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), Round.of(radius));
                            });
                            PixelatedGlassRenderer.draw(4.0f);
                        }
                        case "Алкоголь" -> {
                            AnimatedWavesRenderer.registerRenderCall(() -> {
                                RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, theme.clientColor(), Round.of(radius));
                            });
                            AnimatedWavesRenderer.draw(1.5f);
                        }
                        default -> {
                            BlurRenderer.registerRenderCall(() -> {
                                RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, -1, Round.of(radius));
                            });
                            BlurRenderer.draw(actualBlurStrength);
                        }
                    }
                }

                int bgAlpha = theme != null ? theme.backgroundAlpha().getValue().intValue() : 200;
                int[] colors;

                if (onlyTitleGradient && !isTitle) {
                    colors = new int[]{
                            ColorUtil.rgba(25, 25, 30, bgAlpha),
                            ColorUtil.rgba(20, 20, 25, bgAlpha),
                            ColorUtil.rgba(20, 20, 25, bgAlpha),
                            ColorUtil.rgba(15, 15, 20, bgAlpha)
                    };
                } else {
                    colors = getThemeGradientColors(bgAlpha);
                }

                RenderUtil.Rounded.drawGradientRound(matrix, x, y, width, height, radius, colors[0], colors[1], colors[2], colors[3]);

                int outlineColor = ColorUtil.getColor(255, 255, 255, 15);
                RenderUtil.Rounded.drawRoundOutline(matrix, x, y, width, height, radius, 0.5f, outlineColor);
            }
        }
    }


    @Accessors(fluent = true)
    private record RoundedRect(float x, float y, float width, float height, int color, Round round) {
    }

    public void start() {
        RenderSystem.clearCurrentColor();
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableDepthTest();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        defaultAlphaFunc();
    }

    public void stop() {
        RenderSystem.enableDepthTest();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.clearCurrentColor();
        RenderSystem.shadeModel(GL11.GL_FLAT);
    }

    public void bindTexture(int texture) {
        RenderSystem.bindTexture(texture);
    }

    public void bindTexture(ResourceLocation texture) {
        mc.getTextureManager().bindTexture(texture);
    }

    public void resetColor() {
        RenderSystem.clearCurrentColor();
    }

    public void setColor(final int color, final float alpha) {
        final float red = (float) (color >> 16 & 255) / 255.0F;
        final float green = (float) (color >> 8 & 255) / 255.0F;
        final float blue = (float) (color & 255) / 255.0F;
        RenderSystem.color4f(red, green, blue, alpha);
    }

    public void setColor(int color) {
        setColor(color, (float) (color >> 24 & 255) / 255.0F);
    }

    public void defaultAlphaFunc() {
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0);
    }

    public void drawImage(ResourceLocation location, MatrixStack matrix, float x, float y, float width, float height, int color) {
        drawImage(location, matrix, x, y, width, height, color, color, color, color);
    }

    public void drawImage(ResourceLocation location, MatrixStack matrix, float x, float y, float width, float height, int color1, int color2, int color3, int color4) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        bindTexture(location);
        RectUtil.drawRect(matrix, x, y, width, height, color1, color2, color3, color4, false, true);
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    public void drawTriangle(float x, float y, float width, float height, int color) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        enableSmoothLine();
        GL11.glRotatef(180 + 90, 0F, 0F, 1.0F);

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        RenderUtil.setColor(color);
        GL11.glVertex2f(x, y - 1);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        RenderUtil.setColor(color);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width * 2, y - 1);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_LOOP);
        RenderUtil.setColor(color);
        GL11.glVertex2f(x, y - 1);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_LOOP);
        RenderUtil.setColor(color);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x + width * 2, y - 1);
        GL11.glEnd();

        disableSmoothLine();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glRotatef(-180 - 90, 0F, 0F, 1.0F);
    }

    public void makeScissorBox(float x, float y, float x2, float y2) {
        final int width = (int) (x2 - x);
        final int height = (int) (y2 - y);
        final int scaledWidth = mc.getMainWindow().getScaledWidth();
        final int scaledHeight = mc.getMainWindow().getScaledHeight();
        final int guiScale = (int) mc.getMainWindow().getScaleFactor();
        GL11.glScissor((int) (x * guiScale), (int) ((scaledHeight - (y + height)) * guiScale), width * guiScale, height * guiScale);
    }

    public void drawDiagonalGradient(MatrixStack matrix, float x, float y, float width, float height, int color1, int color2) {
        start();
        RenderSystem.disableTexture();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, POSITION_COLOR);

        float f = (float) (color1 >> 24 & 255) / 255.0F;
        float f1 = (float) (color1 >> 16 & 255) / 255.0F;
        float f2 = (float) (color1 >> 8 & 255) / 255.0F;
        float f3 = (float) (color1 & 255) / 255.0F;

        float f4 = (float) (color2 >> 24 & 255) / 255.0F;
        float f5 = (float) (color2 >> 16 & 255) / 255.0F;
        float f6 = (float) (color2 >> 8 & 255) / 255.0F;
        float f7 = (float) (color2 & 255) / 255.0F;

        bufferbuilder.pos(matrix.getLast().getMatrix(), width, y, 0.0f).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(matrix.getLast().getMatrix(), x, y, 0.0F).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(matrix.getLast().getMatrix(), x, height, 0.0f).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(matrix.getLast().getMatrix(), width, height, 0.0f).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableTexture();
        stop();
    }

    public void enableSmoothLine() {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    public void disableSmoothLine() {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
    }

    public static final List<RectUtil.Vec2fColored> VERTEXES = new ArrayList<>();

    public static void drawRect(MatrixStack matrix, double x, double y, double width, double height, int color1, int color2, int color3, int color4, boolean bloom, boolean texture) {
        VERTEXES.clear();
        VERTEXES.add(new RectUtil.Vec2fColored(x, y, color1));
        VERTEXES.add(new RectUtil.Vec2fColored(x + width, y, color2));
        VERTEXES.add(new RectUtil.Vec2fColored(x + width, y + height, color3));
        VERTEXES.add(new RectUtil.Vec2fColored(x, y + height, color4));
        RectUtil.drawVertexesList2D(matrix, VERTEXES, GL11.GL_POLYGON, texture, bloom);
    }

    @UtilityClass
    public class Rounded {

        private final BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        public void customRound(MatrixStack matrix, float x, float y, float width, float height, boolean shadow, float shadowAlpha, float offset, float value, float smoothness1, float smoothness2, int color1, int color2, int color3, int color4, int outlineColor, Round round) {
            if (width <= 0 || height <= 0) {
                return;
            }
            final ShaderManager shader = ShaderManager.roundedShader;
            if (currentShader != shader) {
                if (currentShader != null) currentShader.unload();
                shader.load();
                currentShader = shader;
            }

            shader.setUniformf("size", width, height);
            shader.setUniformf("round", round.RT, round.RB, round.LT, round.LB);
            shader.setUniformf("smoothness", smoothness1, smoothness2);
            shader.setUniformf("value", value);
            shader.setUniformi("shadow", shadow ? 1 : 0);
            shader.setUniformf("shadowAlpha", shadow ? shadowAlpha : 0);
            shader.setUniformf("color1", ColorUtil.getRGBAf(color1));
            shader.setUniformf("color2", ColorUtil.getRGBAf(color2));
            shader.setUniformf("color3", ColorUtil.getRGBAf(color3));
            shader.setUniformf("color4", ColorUtil.getRGBAf(color4));

            start();
            resetColor();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            CustomFramebuffer.drawQuads(matrix, x - offset, y - offset, width + (offset * 2), height + (offset * 2));
            stop();
            if (currentShader == shader) {
                currentShader.unload();
                currentShader = null;
            }
        }

        public void drawGradientRound(MatrixStack matrix, float x, float y, float width, float height, Round round, int topLeft, int bottomLeft, int topRight, int bottomRight) {
            customRound(matrix, x, y, width, height, false, 0F, 0F, 0F, 0, 1, topLeft, topRight, bottomRight, bottomLeft, 0, round);
        }

        public void drawRoundOutline(MatrixStack matrix, float x, float y, float width, float height, Round round, float outline, int color) {
            roundedOutline(matrix, x, y, width, height, outline, color, round);
        }

        public void drawGradientRound(MatrixStack matrix, float x, float y, float width, float height, float radius, int topLeft, int bottomLeft, int topRight, int bottomRight) {
            customRound(matrix, x, y, width, height, false, 0F, 0F, 0F, 0, 1, topLeft, topRight, bottomRight, bottomLeft, 0, Round.of(radius));
        }

        public void drawRoundOutline(MatrixStack matrix, float x, float y, float width, float height, float radius, float outline, int color) {
            roundedOutline(matrix, x, y, width, height, outline, color, Round.of(radius));
        }

        public void drawLine(MatrixStack matrix, float x1, float y1, float x2, float y2, float width, int color) {
            if (hudBatching) {
                float[] rgba = ColorUtil.getRGBAf(color);
                hudBuffer.pos(x1, y1, 0).color(rgba[0], rgba[1], rgba[2], rgba[3]).endVertex();
                hudBuffer.pos(x2, y2, 0).color(rgba[0], rgba[1], rgba[2], rgba[3]).endVertex();
                return;
            }
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            RenderUtil.enableSmoothLine();
            GL11.glLineWidth(width);

            float[] rgba = ColorUtil.getRGBAf(color);
            RenderSystem.color4f(rgba[0], rgba[1], rgba[2], rgba[3]);

            BufferBuilder bufferBuilder = buffer;
            bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
            bufferBuilder.pos(matrix.getLast().getMatrix(), x1, y1, 0).endVertex();
            bufferBuilder.pos(matrix.getLast().getMatrix(), x2, y2, 0).endVertex();
            TESSELLATOR.draw();

            RenderUtil.disableSmoothLine();
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
            RenderSystem.clearCurrentColor();
        }

        public void roundedOutline(MatrixStack matrix, float x, float y, float width, float height, float outline, int color1, int color2, int color3, int color4, Round round) {
            if (width <= 0 || height <= 0) {
                return;
            }
            final ShaderManager shader = ShaderManager.roundedOutlineShader;
            if (currentShader != shader) {
                if (currentShader != null) currentShader.unload();
                shader.load();
                currentShader = shader;
            }

            shader.setUniformf("size", width, height);
            shader.setUniformf("round", round.RT, round.RB, round.LT, round.LB);
            shader.setUniformf("smoothness", -outline, outline);
            shader.setUniformf("softness", -outline, outline);
            shader.setUniformf("thickness", 0, outline);
            shader.setUniformf("value", outline);
            shader.setUniformf("color1", ColorUtil.getRGBAf(color1));
            shader.setUniformf("color2", ColorUtil.getRGBAf(color2));
            shader.setUniformf("color3", ColorUtil.getRGBAf(color3));
            shader.setUniformf("color4", ColorUtil.getRGBAf(color4));

            start();
            resetColor();
            CustomFramebuffer.drawQuads(matrix, x - outline, y - outline, width + (outline * 2), height + (outline * 2));
            stop();
            if (currentShader == shader) {
                currentShader.unload();
                currentShader = null;
            }
        }

        public void roundedOutline(MatrixStack matrix, float x, float y, float width, float height, float outline, int color, Round round) {
            roundedOutline(matrix, x, y, width, height, outline, color, color, color, color, round);
        }

        public void smooth(MatrixStack matrix, float x, float y, float width, float height, int color1, int color2, int color3, int color4, Round round) {
            customRound(matrix, x, y, width, height, false, 0F, 0F, 0F, -0.5F, 0.5F, color1, color2, color3, color4, 0, round);
        }

        public void smooth(MatrixStack matrix, float x, float y, float width, float height, int color, Round round) {
            customRound(matrix, x, y, width, height, false, 0F, 0F, 0F, -0.5F, 0.5F, color, color, color, color, 0, round);
        }

        public void roundedRect(MatrixStack matrix, float x, float y, float width, float height, int color1, int color2, int color3, int color4, Round round) {
            customRound(matrix, x, y, width, height, false, 0F, 0F, 0F, 0, 1, color1, color2, color3, color4, 0, round);
        }

        public void roundedRect(MatrixStack matrix, float x, float y, float width, float height, int color, Round round) {
            customRound(matrix, x, y, width, height, false, 0F, 0F, 0F, 0, 1, color, color, color, color, 0, round);
        }

        public void roundedWithOutline(MatrixStack stack, float x, float y, float width, float height, float outline, int color, int outlineColor, Round round) {
            roundedOutline(stack, x - outline + .5f, y - outline + .5f, width + outline * 2 - 1, height + outline * 2 - 1, outline, outlineColor, round);
            smooth(stack, x, y, width, height, color, round);
        }

        public void customRoundedRect(MatrixStack matrix, float x, float y, float width, float height, float radius) {
            int deepBlue = ColorUtil.getColor(18, 28, 58, 240);
            int shadowBlue = ColorUtil.getColor(10, 18, 42, 110);
            int accentGlow = ColorUtil.getColor(44, 100, 255, 70);

            if (settings.shadowEnabled) {
                RenderUtil.Shadow.drawShadow(matrix, x, y, width, height, radius * 2.5f, 0.7f, shadowBlue, Round.of(radius));
            }
            ShaderManager.roundedShader.load();
            ShaderManager.roundedShader.setUniformf("size", width, height);
            ShaderManager.roundedShader.setUniformf("round", radius, radius, radius, radius);
            ShaderManager.roundedShader.setUniformf("smoothness", 0.2f, 1f);
            ShaderManager.roundedShader.setUniformf("value", 1f);
            ShaderManager.roundedShader.setUniformi("shadow", 0);
            ShaderManager.roundedShader.setUniformf("shadowAlpha", 0);
            ShaderManager.roundedShader.setUniformf("color1", ColorUtil.getRGBAf(deepBlue));
            ShaderManager.roundedShader.setUniformf("color2", ColorUtil.getRGBAf(deepBlue));
            ShaderManager.roundedShader.setUniformf("color3", ColorUtil.getRGBAf(deepBlue));
            ShaderManager.roundedShader.setUniformf("color4", ColorUtil.getRGBAf(deepBlue));
            RenderUtil.start();
            RenderUtil.resetColor();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            CustomFramebuffer.drawQuads(matrix, x, y, width, height);
            RenderUtil.stop();
            ShaderManager.roundedShader.unload();

            float pulse = (float) (Math.sin(System.currentTimeMillis() / 350.0) * 0.5f + 0.5f);
            int sparkleColor = ColorUtil.interpolateColor(accentGlow, ColorUtil.getColor(120, 180, 255, 140), pulse);

            float sparkleRadius = radius * 1.3f;
            float sparkleSize = Math.max(width, height) * 0.09f + 8f;
            float sparkleX = x + width - sparkleSize - sparkleRadius;
            float sparkleY = y + sparkleRadius;

            ShaderManager.roundedShader.load();
            ShaderManager.roundedShader.setUniformf("size", sparkleSize, sparkleSize);
            ShaderManager.roundedShader.setUniformf("round", sparkleRadius, sparkleRadius, sparkleRadius, sparkleRadius);
            ShaderManager.roundedShader.setUniformf("smoothness", 0.6f, 1f);
            ShaderManager.roundedShader.setUniformf("value", 1f);
            ShaderManager.roundedShader.setUniformi("shadow", 0);
            ShaderManager.roundedShader.setUniformf("shadowAlpha", 0);
            ShaderManager.roundedShader.setUniformf("color1", ColorUtil.getRGBAf(sparkleColor));
            ShaderManager.roundedShader.setUniformf("color2", ColorUtil.getRGBAf(sparkleColor));
            ShaderManager.roundedShader.setUniformf("color3", ColorUtil.getRGBAf(sparkleColor));
            ShaderManager.roundedShader.setUniformf("color4", ColorUtil.getRGBAf(sparkleColor));
            RenderUtil.start();
            RenderUtil.resetColor();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            CustomFramebuffer.drawQuads(matrix, sparkleX, sparkleY, sparkleSize, sparkleSize);
            RenderUtil.stop();
            ShaderManager.roundedShader.unload();
        }

        public void roundedVertical(MatrixStack matrix, float x, float y, float width, float height, int color1, int color2, int color3, int color4, Round round) {
            if (width <= 0 || height <= 0) {
                return;
            }
            final ShaderManager shader = ShaderManager.roundedVerticalShader;
            if (currentShader != shader) {
                if (currentShader != null) currentShader.unload();
                shader.load();
                currentShader = shader;
            }

            shader.setUniformf("size", width, height);
            shader.setUniformf("round", round.RT, round.RB, round.LT, round.LB);
            shader.setUniformf("smoothness", 0, 0);
            shader.setUniformf("value", 0);
            shader.setUniformi("shadow", 0);
            shader.setUniformf("shadowAlpha", 0);
            shader.setUniformf("color1", ColorUtil.getRGBAf(color1));
            shader.setUniformf("color2", ColorUtil.getRGBAf(color2));
            shader.setUniformf("color3", ColorUtil.getRGBAf(color3));
            shader.setUniformf("color4", ColorUtil.getRGBAf(color4));

            start();
            resetColor();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            CustomFramebuffer.drawQuads(matrix, x, y, width, height);
            stop();
            if (currentShader == shader) {
                currentShader.unload();
                currentShader = null;
            }
        }

        public static void gradientRect(MatrixStack stack, float x, float y, float width, float height, int color1, int color2, int color3, int color4, Round round) {
            float[] c1 = ColorUtil.getRGBAf(color1);
            float[] c2 = ColorUtil.getRGBAf(color2);
            float[] c3 = ColorUtil.getRGBAf(color3);
            float[] c4 = ColorUtil.getRGBAf(color4);
            if (hudBatching) {
                hudBuffer.pos(x, height + y, 0.0F).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
                hudBuffer.pos(width + x, height + y, 0.0F).color(c2[0], c2[1], c2[2], c2[3]).endVertex();
                hudBuffer.pos(width + x, y, 0.0F).color(c3[0], c3[1], c3[2], c3[3]).endVertex();
                hudBuffer.pos(x, y, 0.0F).color(c4[0], c4[1], c4[2], c4[3]).endVertex();
                return;
            }
            GlStateManager.enableBlend();
            GlStateManager.disableTexture();

            GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);

            buffer.begin(GL11.GL_QUADS, POSITION_COLOR);
            buffer.pos(stack.getLast().getMatrix(), x, height + y, 0.0F).color(c1[0], c1[1], c1[2], c1[3]).endVertex();
            buffer.pos(stack.getLast().getMatrix(), width + x, height + y, 0.0F).color(c2[0], c2[1], c2[2], c2[3]).endVertex();
            buffer.pos(stack.getLast().getMatrix(), width + x, y, 0.0F).color(c3[0], c3[1], c3[2], c3[3]).endVertex();
            buffer.pos(stack.getLast().getMatrix(), x, y, 0.0F).color(c4[0], c4[1], c4[2], c4[3]).endVertex();
            TESSELLATOR.draw();

            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
        }

        public static void drawSmoothRoundedRect(MatrixStack matrixStack, float x, float y, float width, float height, float radius, float borderSize) {
            BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
            bufferbuilder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);

            matrixStack.push();
            matrixStack.translate(x + radius, y + radius, 0.0D);

            bufferbuilder.pos(matrixStack.getLast().getMatrix(), 0.0F, 0.0F, 0.0F).endVertex();

            int i = 24;
            float f = (float) (Math.PI / (double) i);

            for (int j = 0; j <= i; ++j) {
                float f1 = (float) ((double) j * f);
                bufferbuilder.pos(matrixStack.getLast().getMatrix(), (float) (Math.sin(f1) * (double) radius), (float) ((-Math.cos(f1)) * (double) radius), 0.0F).endVertex();
            }

            Tessellator.getInstance().draw();
            matrixStack.pop();
        }
    }

    @UtilityClass
    public class Styled {
        public void draw(MatrixStack matrix, float x, float y, float width, float height, float radius, int bgColor, int outlineColor, int shadowColor, float blurStrength, boolean useBlur) {
            if (settings.shadowEnabled) {
                RenderUtil.Shadow.drawShadow(matrix, x, y, width, height, 10f, 0.7f, shadowColor, Round.of(radius));
            }

            if (useBlur && settings.blurEnabled) {
                BlurRenderer.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, -1, Round.of(radius)));
                BlurRenderer.draw((int) blurStrength);
            }

            RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, bgColor, Round.of(radius));

            RenderUtil.Rounded.roundedOutline(matrix, x, y, width, height, 0.75f, outlineColor, Round.of(radius));
        }
    }

    @UtilityClass
    public class Shadow {
        public void drawShadow(MatrixStack matrix, float x, float y, float width, float height, float radius, int color) {
            drawShadow(matrix, x, y, width, height, radius, 1f, color, color, color, color);
        }

        public void drawShadow(MatrixStack matrix, float x, float y, float width, float height, float radius, int color1, int color2, int color3, int color4) {
            Rounded.customRound(matrix, x - radius, y - radius, width + (radius * 2F), height + (radius * 2F), true, 1f, 0, radius, -radius, radius, color1, color2, color3, color4, 0, Round.of(radius));
        }

        public void drawShadow(MatrixStack matrix, float x, float y, float width, float height, float radius, int color1, int color2, int color3, int color4, Round round) {
            Rounded.customRound(matrix, x - radius, y - radius, width + (radius * 2F), height + (radius * 2F), true, 1f, 0, radius, -radius, radius, color1, color2, color3, color4, 0, round);
        }

        public void drawShadow(MatrixStack matrix, float x, float y, float width, float height, float radius, int color, Round round) {
            drawShadow(matrix, x, y, width, height, radius, 1f, color, color, color, color, round);
        }

        public void drawShadow(MatrixStack matrix, float x, float y, float width, float height, float radius, float alpha, int color) {
            drawShadow(matrix, x, y, width, height, radius, alpha, color, color, color, color);
        }

        public void drawShadow(MatrixStack matrix, float x, float y, float width, float height, float radius, float alpha, int color1, int color2, int color3, int color4) {
            Rounded.customRound(matrix, x - radius, y - radius, width + (radius * 2F), height + (radius * 2F), true, alpha, 0, radius, -radius, radius, color1, color2, color3, color4, 0, Round.of(radius));
        }

        public void drawShadow(MatrixStack matrix, float x, float y, float width, float height, float radius, float alpha, int color1, int color2, int color3, int color4, Round round) {
            Rounded.customRound(matrix, x - radius, y - radius, width + (radius * 2F), height + (radius * 2F), true, alpha, 0, radius, -radius, radius, color1, color2, color3, color4, 0, round);
        }

        public void drawShadow(MatrixStack matrix, float x, float y, float width, float height, float radius, float alpha, int color, Round round) {
            drawShadow(matrix, x, y, width, height, radius, alpha, color, color, color, color, round);
        }
    }

    @UtilityClass
    public class Texture {

        public void roundedHead(ResourceLocation skin, float x, float y, float width, float height, float radius, float alpha, float hurt) {
            final ShaderManager shader = ShaderManager.roundedHead;
            mc.getTextureManager().bindTexture(skin);
            RenderSystem.enableBlend();
            shader.load();
            shader.setUniformf("size", width, height);
            shader.setUniformf("radius", radius);
            shader.setUniformf("hurt_time", hurt);
            shader.setUniformf("alpha", alpha);

            shader.setUniformf("startX", 4);
            shader.setUniformf("startY", 4);

            shader.setUniformf("endX", 8);
            shader.setUniformf("endY", 8);

            shader.setUniformf("texXSize", 32);
            shader.setUniformf("texYSize", 32);
            CustomFramebuffer.drawQuads(x + 2, y + 2, width, height);
            shader.unload();
            RenderSystem.disableBlend();
        }

        public void customRound(MatrixStack matrix, float x, float y, float width, float height, float value, float offset, float alpha, float smoothness1, float smoothness2, Round round) {
            final ShaderManager shader = ShaderManager.roundedTextureShader;
            if (currentShader != shader) {
                if (currentShader != null) currentShader.unload();
                shader.load();
                currentShader = shader;
            }

            shader.setUniformi("textureIn", 0);
            shader.setUniformf("size", width, height);
            shader.setUniformf("round", round.LB, round.LT, round.RB, round.RT);
            shader.setUniformf("smoothness", smoothness1, smoothness2);
            shader.setUniformf("value", value);
            shader.setUniformf("alpha", alpha);

            start();
            resetColor();
            CustomFramebuffer.drawQuads(matrix, x - offset, y - offset, width + (offset * 2), height + (offset * 2));
            stop();
            if (currentShader == shader) {
                currentShader.unload();
                currentShader = null;
            }
        }

        public void smooth(MatrixStack matrix, float x, float y, float width, float height, float alpha, Round round) {
            customRound(matrix, x, y, width, height, 0, 0, alpha, -0.5F, 0.5F, round);
        }
    }
}