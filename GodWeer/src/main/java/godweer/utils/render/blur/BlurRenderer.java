package godweer.utils.render.blur;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import godweer.GodWeer;
import godweer.utils.render.ShaderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.ShaderProgram;

import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Порт blur-стека из Private ({@code BlurRenderer} + {@code GaussianBlur}) на рендер-систему 1.21.4.
 * <p>
 * Отличия от источника:
 * <ul>
 *     <li>FBO — {@link BlurFramebuffer} вместо прямого {@code new Framebuffer(...)};</li>
 *     <li>шейдеры — core-шейдеры GodWeer ({@code kawase_down}/{@code kawase_up}/{@code blur/blit}),
 *     а не GLSL-строки, компилируемые вручную;</li>
 *     <li>очередь {@link #registerRenderCall(Runnable)} сохранена 1:1 из Private.</li>
 * </ul>
 * Вызывать только из GUI-рендера (внутри {@code Screen.render}/{@code GameRenderer.render}),
 * потому что квады рисуются в scaled-координатах и опираются на текущую ortho-проекцию.
 */
public final class BlurRenderer {

    public enum Mode {
        KAWASE,
        GAUSSIAN
    }

    private static final float DOWNSCALE = 0.5F;
    private static final float MIN_RADIUS = 1.0F;
    private static final float MAX_RADIUS = 32.0F;
    private static final float MIN_OFFSET = 0.5F;
    private static final int MIN_STEPS = 1;
    private static final int MAX_STEPS = 6;

    private static final PoseStack IDENTITY = new PoseStack();
    private static final BlurFramebuffer BUFFER_A = new BlurFramebuffer();
    private static final BlurFramebuffer BUFFER_B = new BlurFramebuffer();
    private static final Queue<Runnable> RENDER_QUEUE = new ArrayDeque<>(8);

    private static Mode mode = Mode.KAWASE;
    private static boolean captured;
    private static int blurredTexture;
    private static int blurredWidth;
    private static int blurredHeight;

    private BlurRenderer() {
    }

    public static void registerRenderCall(Runnable renderCall) {
        if (renderCall != null) {
            RENDER_QUEUE.add(renderCall);
        }
    }

    public static void setMode(Mode newMode) {
        mode = newMode == null ? Mode.KAWASE : newMode;
    }

    public static Mode getMode() {
        return mode;
    }

    public static int getBlurredTexture() {
        return blurredTexture;
    }

    public static int getBlurredWidth() {
        return blurredWidth;
    }

    public static int getBlurredHeight() {
        return blurredHeight;
    }

    public static boolean isAvailable() {
        return blurredTexture > 0;
    }

    /**
     * Захват кадра в буфер и прогон цепочки размытия. Результат остаётся в {@link #getBlurredTexture()}.
     */
    public static boolean blur(PoseStack stack, float radius) {
        return capture(stack) && finish(stack, radius, 0);
    }

    /**
     * Первая фаза (аналог {@code GaussianBlur.startBlur()} из Private):
     * копия кадра в blur-буфер, после чего выполняются отложенные {@link #registerRenderCall(Runnable)}.
     */
    public static boolean capture(PoseStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();

        int bufferWidth = Math.max(1, (int) (window.getWidth() * DOWNSCALE));
        int bufferHeight = Math.max(1, (int) (window.getHeight() * DOWNSCALE));

        if (!BUFFER_A.ensure(bufferWidth, bufferHeight) || !BUFFER_B.ensure(bufferWidth, bufferHeight)) {
            return false;
        }

        int mainTexture = BlurFramebuffer.mainTextureId();
        if (mainTexture <= 0) {
            return false;
        }

        PoseStack effective = stack == null ? IDENTITY : stack;

        prepareState();

        if (!BUFFER_A.bind()) {
            restoreState();
            return false;
        }

        boolean drawn = blit(mainTexture, effective, 0.0F, 0.0F,
                window.getGuiScaledWidth(), window.getGuiScaledHeight(), 0xFFFFFFFF);
        if (drawn) {
            drainQueue();
        }

        BUFFER_A.unbind();
        restoreState();

        captured = drawn;
        return drawn;
    }

    /**
     * Вторая фаза (аналог {@code GaussianBlur.endBlur(radius, iterations)} из Private).
     *
     * @param iterations 0 — число проходов подбирается по радиусу; иначе жёстко ограничивается 1..6.
     */
    public static boolean finish(PoseStack stack, float radius, int iterations) {
        if (!captured) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        PoseStack effective = stack == null ? IDENTITY : stack;

        float guiWidth = window.getGuiScaledWidth();
        float guiHeight = window.getGuiScaledHeight();
        float clampRadius = radius < MIN_RADIUS ? MIN_RADIUS : (radius > MAX_RADIUS ? MAX_RADIUS : radius);

        int steps = iterations > 0
                ? Math.max(MIN_STEPS, Math.min(iterations, MAX_STEPS))
                : stepsFor(clampRadius);

        prepareState();

        boolean drawn = mode == Mode.GAUSSIAN
                ? gaussianChain(effective, guiWidth, guiHeight, clampRadius)
                : kawaseChain(effective, guiWidth, guiHeight, clampRadius, steps);

        restoreState();
        BlurFramebuffer.bindMain();
        captured = false;

        return drawn;
    }

    /**
     * Вывод уже размытой текстуры. Вызывать после {@link #blur(PoseStack, float)} / {@link #finish}.
     */
    public static boolean drawBlurred(PoseStack stack, float x, float y, float width, float height, int color) {
        if (blurredTexture <= 0) {
            return false;
        }

        PoseStack effective = stack == null ? IDENTITY : stack;

        prepareState();
        boolean drawn = blit(blurredTexture, effective, x, y, width, height, color);
        restoreState();

        return drawn;
    }

    /**
     * Аналог {@code BlurRenderer.draw(int radius)} из Private: размытие всего экрана одним вызовом.
     */
    public static boolean drawBlurredBackground(PoseStack stack, float radius, int color) {
        if (!blur(stack, radius)) {
            return false;
        }

        Window window = Minecraft.getInstance().getWindow();
        return drawBlurred(stack, 0.0F, 0.0F, window.getGuiScaledWidth(), window.getGuiScaledHeight(), color);
    }

    public static void cleanup() {
        BUFFER_A.destroy();
        BUFFER_B.destroy();
        RENDER_QUEUE.clear();
        blurredTexture = 0;
        blurredWidth = 0;
        blurredHeight = 0;
        captured = false;
    }

    private static boolean kawaseChain(PoseStack stack, float guiWidth, float guiHeight, float radius, int steps) {
        float offset = Math.max(MIN_OFFSET, radius / (steps * 2.0F));

        BlurFramebuffer source = BUFFER_A;
        BlurFramebuffer destination = BUFFER_B;

        for (int i = 0; i < steps; i++) {
            if (!applyKawase(ShaderUtils.KAWASE_DOWN, source, destination, offset, stack, guiWidth, guiHeight)) {
                return false;
            }

            BlurFramebuffer swap = source;
            source = destination;
            destination = swap;
        }

        for (int i = 0; i < steps; i++) {
            if (!applyKawase(ShaderUtils.KAWASE_UP, source, destination, offset, stack, guiWidth, guiHeight)) {
                return false;
            }

            BlurFramebuffer swap = source;
            source = destination;
            destination = swap;
        }

        blurredTexture = source.textureId();
        blurredWidth = source.width();
        blurredHeight = source.height();
        return true;
    }

    private static boolean gaussianChain(PoseStack stack, float guiWidth, float guiHeight, float radius) {
        int taps = GaussianKernel.tapsFor(radius);
        float sigma = GaussianKernel.sigmaFor(taps);
        float texelRadius = Math.min(GaussianKernel.MAX_TAPS, sigma * DOWNSCALE * 2.0F);

        if (!applyGaussian(BUFFER_A, BUFFER_B, 1.0F, 0.0F, texelRadius, stack, guiWidth, guiHeight)) {
            return false;
        }

        if (!applyGaussian(BUFFER_B, BUFFER_A, 0.0F, 1.0F, texelRadius, stack, guiWidth, guiHeight)) {
            return false;
        }

        blurredTexture = BUFFER_A.textureId();
        blurredWidth = BUFFER_A.width();
        blurredHeight = BUFFER_A.height();
        return true;
    }

    private static boolean applyKawase(ShaderProgram program, BlurFramebuffer source, BlurFramebuffer destination,
                                       float offset, PoseStack stack, float width, float height) {
        if (!source.isValid() || !destination.bind()) {
            return false;
        }

        CompiledShaderProgram shader = ShaderUtils.get(program);
        if (shader == null) {
            destination.unbind();
            return false;
        }

        RenderSystem.setShader(program);
        ShaderUtils.set(shader, "Resolution", 1.0F / source.width(), 1.0F / source.height());
        ShaderUtils.set(shader, "Offset", offset);
        ShaderUtils.set(shader, "Saturation", 1.0F);
        ShaderUtils.set(shader, "TintIntensity", 0.0F);
        ShaderUtils.set(shader, "TintColor", 1.0F, 1.0F, 1.0F);

        RenderSystem.setShaderTexture(0, source.textureId());
        quad(stack, 0.0F, 0.0F, width, height, 0xFFFFFFFF);
        RenderSystem.setShaderTexture(0, 0);

        destination.unbind();

        return true;
    }

    private static boolean applyGaussian(BlurFramebuffer source, BlurFramebuffer destination,
                                         float directionX, float directionY, float radius,
                                         PoseStack stack, float width, float height) {
        if (!source.isValid() || !destination.bind()) {
            return false;
        }

        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.BLUR_GAUSSIAN);
        if (shader == null) {
            destination.unbind();
            return false;
        }

        RenderSystem.setShader(ShaderUtils.BLUR_GAUSSIAN);
        ShaderUtils.set(shader, "TexelSize", 1.0F / source.width(), 1.0F / source.height());
        ShaderUtils.set(shader, "Direction", directionX, directionY);
        ShaderUtils.set(shader, "Radius", radius);

        RenderSystem.setShaderTexture(0, source.textureId());
        quad(stack, 0.0F, 0.0F, width, height, 0xFFFFFFFF);
        RenderSystem.setShaderTexture(0, 0);

        destination.unbind();

        return true;
    }

    private static boolean blit(int texture, PoseStack stack, float x, float y, float width, float height, int color) {
        if (texture <= 0 || width <= 0.0F || height <= 0.0F) {
            return false;
        }

        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.BLUR_BLIT);
        if (shader == null) {
            return false;
        }

        RenderSystem.setShader(ShaderUtils.BLUR_BLIT);
        RenderSystem.setShaderTexture(0, texture);

        quad(stack, x, y, width, height, color);

        RenderSystem.setShaderTexture(0, 0);
        return true;
    }

    private static void quad(PoseStack stack, float x, float y, float width, float height, int color) {
        Matrix4f matrix = stack.last().pose();

        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >>> 24) & 0xFF) / 255.0F;

        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        // UV берутся «перевёрнутыми» по V: PIL-порядок текстуры кадра — снизу вверх,
        // GUI-координаты считают Y сверху вниз.
        buffer.addVertex(matrix, x, y, 0.0F).setUv(0.0F, 1.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x, y + height, 0.0F).setUv(0.0F, 0.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x + width, y + height, 0.0F).setUv(1.0F, 0.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x + width, y, 0.0F).setUv(1.0F, 1.0F).setColor(red, green, blue, alpha);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }

    private static void drainQueue() {
        while (!RENDER_QUEUE.isEmpty()) {
            Runnable renderCall = RENDER_QUEUE.poll();
            if (renderCall == null) {
                continue;
            }

            try {
                renderCall.run();
            } catch (Throwable throwable) {
                GodWeer.LOGGER.error("Blur render call failed", throwable);
            }
        }
    }

    private static void prepareState() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
    }

    private static void restoreState() {
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static int stepsFor(float radius) {
        if (radius <= 2.0F) {
            return MIN_STEPS;
        }

        int steps = MIN_STEPS + (int) (radius / 4.0F);
        return Math.min(steps, MAX_STEPS);
    }
}
