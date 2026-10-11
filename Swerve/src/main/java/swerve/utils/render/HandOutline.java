package swerve.utils.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import swerve.Swerve;
import swerve.managers.ThemeManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.ShaderProgram;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

// Обводка рук по силуэту — та же схема, что в референсе (Kimiko ShaderHands), но на
// пайплайне 1.21.4: RenderPipeline/GpuBuffer там нет, поэтому маска собирается из
// глубины, а не из отдельного рендера рук.
//
// Кадр: до отрисовки рук копируется глубина мира, после — глубина мира с руками.
// Разница глубин и есть силуэт рук (пиксели, где рука закрыла мир): он не зависит ни от
// проекции, ни от анимации, ни от предмета в руке. Дальше маска размывается кавасе-цепочкой,
// и кольцо «блюр минус маска» рисуется аддитивно поверх рук.
//
// Та же маска в уменьшенном виде читается в память и даёт пиксель-точный ответ на вопрос
// «курсор над рукой?» — именно так референс решает, можно ли руку хватать.
//
// Пайплайн живёт только пока открыт чат и модуль включён; в остальное время стоимость нулевая.
public final class HandOutline {

    private static final int GRID_WIDTH = 256;
    private static final int GRID_HEIGHT = 144;
    private static final int BLUR_STEPS = 3;
    private static final int COVER_THRESHOLD = 110;
    private static final float MASK_THRESHOLD = 0.0005F;
    private static final float GLOW_EXPOSURE = 2.4F;
    private static final long STALE_NANOS = 250_000_000L;

    private static final PoseStack QUAD_STACK = new PoseStack();
    private static final Matrix4f INVERSE = new Matrix4f();
    private static final Vector4f CORNER = new Vector4f();
    private static final float[] CORNER_X = new float[4];
    private static final float[] CORNER_Y = new float[4];
    private static final float[] CORNER_Z = new float[4];
    private static final byte[] GRID_PIXELS = new byte[GRID_WIDTH * GRID_HEIGHT * 4];

    private static TextureTarget depthBefore;
    private static TextureTarget depthAfter;
    private static TextureTarget mask;
    private static TextureTarget blurA;
    private static TextureTarget blurB;
    private static TextureTarget grid;
    private static RenderTarget bloom;

    private static ByteBuffer gridBuffer;
    private static boolean cornersValid;

    private static int frameWidth = -1;
    private static int frameHeight = -1;

    private static boolean wanted;
    private static boolean hooked;
    private static boolean depthTaken;
    private static boolean maskReady;
    private static boolean broken;

    private static float outlineAlpha;
    private static float outlineWidth = 3.0F;

    private static long maskAt;
    private static long wantedSince;
    private static long lastReport;

    private HandOutline() {
    }

    // Состояние приходит из ViewModel: нужен ли пайплайн в этом кадре и как рисовать обводку.
    public static void setWanted(boolean value) {
        if (wanted == value) {
            return;
        }
        wanted = value;
        wantedSince = value ? System.nanoTime() : 0L;
        if (!value) {
            release();
        }
    }

    public static void setOutline(float alpha, float width) {
        outlineAlpha = alpha < 0.0F ? 0.0F : (alpha > 1.0F ? 1.0F : alpha);
        outlineWidth = width < 1.0F ? 1.0F : (width > 8.0F ? 8.0F : width);
    }

    public static boolean isShaderReady() {
        return !broken;
    }

    public static boolean isMaskReady() {
        return maskReady && System.nanoTime() - maskAt < STALE_NANOS;
    }

    // HEAD renderHandsWithItems: в буфере только мир, руки ещё не нарисованы.
    public static void beforeHands() {
        hooked = true;
        depthTaken = false;
        if (!wanted) {
            return;
        }

        try {
            RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
            if (main == null || !ensureTargets()) {
                return;
            }

            depthBefore.copyDepthFrom(main);
            main.bindWrite(false);
            depthTaken = true;
        } catch (Throwable throwable) {
            fail("depth copy before hands", throwable);
        }
    }

    // RETURN renderHandsWithItems: руки отрисованы и батч закрыт, глубина рук в буфере.
    public static void afterHands() {
        if (!wanted || !depthTaken) {
            return;
        }
        depthTaken = false;

        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        if (main == null) {
            return;
        }

        try {
            if (!ensureTargets()) {
                return;
            }

            depthAfter.copyDepthFrom(main);
            main.bindWrite(false);

            if (!buildMask()) {
                main.bindWrite(true);
                return;
            }

            copyGrid();
            readGrid();
            maskReady = true;
            maskAt = System.nanoTime();

            if (outlineAlpha > 0.004F) {
                blur();
                drawGlow(main);
            }

            main.bindWrite(true);
        } catch (Throwable throwable) {
            fail("hand outline pass", throwable);
            try {
                main.bindWrite(true);
            } catch (Throwable ignored) {
                // фреймбуфер восстановится в следующем кадре
            }
        }
    }

    // Курсор над рукой? Координаты — в GUI-единицах, как у мыши и у HUD.
    public static boolean isHandCoveredAt(float guiX, float guiY) {
        if (!isMaskReady()) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        int guiWidth = minecraft.getWindow().getGuiScaledWidth();
        int guiHeight = minecraft.getWindow().getGuiScaledHeight();
        if (guiWidth <= 0 || guiHeight <= 0) {
            return false;
        }

        int cellX = (int) (guiX / (float) guiWidth * (float) GRID_WIDTH);
        int cellY = (int) (guiY / (float) guiHeight * (float) GRID_HEIGHT);

        // Перебор 3x3: маска руки в сетке тонкая, попадание ровно в одну ячейку слишком требовательно.
        for (int offsetY = -1; offsetY <= 1; offsetY++) {
            int row = GRID_HEIGHT - 1 - (cellY + offsetY);
            if (row < 0 || row >= GRID_HEIGHT) {
                continue;
            }
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                int column = cellX + offsetX;
                if (column < 0 || column >= GRID_WIDTH) {
                    continue;
                }
                if ((GRID_PIXELS[(row * GRID_WIDTH + column) * 4 + 3] & 0xFF) >= COVER_THRESHOLD) {
                    return true;
                }
            }
        }

        return false;
    }

    // Один раз в 5 секунд объясняет, почему обводки нет: хук не сработал или шейдер не собрался.
    public static void reportStale() {
        long now = System.currentTimeMillis();
        if (now - lastReport < 5_000L) {
            return;
        }
        lastReport = now;

        if (!hooked) {
            Swerve.LOGGER.warn("ViewModel: renderHandsWithItems hook did not fire, hand outline is off");
            return;
        }
        if (broken) {
            Swerve.LOGGER.warn("ViewModel: hand outline shaders are not compiled, see the error above");
            return;
        }
        if (!maskReady) {
            Swerve.LOGGER.warn("ViewModel: hand mask is empty (depth copy or mask pass failed)");
        }
    }

    public static boolean isHooked() {
        return hooked;
    }

    public static long wantedFor() {
        return wantedSince == 0L ? 0L : System.nanoTime() - wantedSince;
    }

    public static void release() {
        destroyTargets();
        bloom = null;
        maskReady = false;
        depthTaken = false;
        frameWidth = -1;
        frameHeight = -1;
    }

    // ------------------------------------------------------------------

    private static boolean ensureTargets() {
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        if (width < 2 || height < 2) {
            return false;
        }

        if (depthBefore != null && frameWidth == width && frameHeight == height) {
            return true;
        }

        destroyTargets();
        frameWidth = width;
        frameHeight = height;

        int halfWidth = Math.max(1, width >> 1);
        int halfHeight = Math.max(1, height >> 1);

        // Глубина нужна как текстура: copyDepthFrom пишет в неё.blit-ом, шейдер читает её же.
        depthBefore = new TextureTarget(width, height, true);
        depthAfter = new TextureTarget(width, height, true);
        mask = new TextureTarget(width, height, false);
        blurA = new TextureTarget(halfWidth, halfHeight, false);
        blurB = new TextureTarget(halfWidth, halfHeight, false);
        grid = new TextureTarget(GRID_WIDTH, GRID_HEIGHT, false);

        mask.setFilterMode(GL11.GL_LINEAR);
        blurA.setFilterMode(GL11.GL_LINEAR);
        blurB.setFilterMode(GL11.GL_LINEAR);
        grid.setFilterMode(GL11.GL_LINEAR);
        return true;
    }

    private static boolean buildMask() {
        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.HANDS_MASK_DIFF);
        if (shader == null) {
            broken = true;
            return false;
        }

        beginPasses();
        mask.bindWrite(true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderTexture(0, depthBefore.getDepthTextureId());
        RenderSystem.setShaderTexture(1, depthAfter.getDepthTextureId());
        ShaderUtils.set(shader, "uThreshold", MASK_THRESHOLD);
        RenderSystem.setShader(ShaderUtils.HANDS_MASK_DIFF);
        quad();
        endPasses();
        return true;
    }

    // Кавасе вниз-вверх: мягкое гало вокруг силуэта, из которого потом вычитается маска.
    private static void blur() {
        CompiledShaderProgram down = ShaderUtils.get(ShaderUtils.HANDS_KAWASE_DOWN);
        CompiledShaderProgram up = ShaderUtils.get(ShaderUtils.HANDS_KAWASE_UP);
        if (down == null || up == null) {
            broken = true;
            return;
        }

        int halfWidth = Math.max(1, frameWidth >> 1);
        int halfHeight = Math.max(1, frameHeight >> 1);
        float step = outlineWidth * 0.9F;

        beginPasses();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(false);

        RenderTarget source = mask;
        RenderTarget target = blurA;
        for (int i = 0; i < BLUR_STEPS; i++) {
            pass(target, source, ShaderUtils.HANDS_KAWASE_DOWN, down, step * (float) (i + 1), halfWidth, halfHeight);
            source = target;
            target = target == blurA ? blurB : blurA;
        }
        for (int i = BLUR_STEPS - 1; i >= 0; i--) {
            pass(target, source, ShaderUtils.HANDS_KAWASE_UP, up, step * (float) (i + 1), halfWidth, halfHeight);
            source = target;
            target = target == blurA ? blurB : blurA;
        }

        bloom = source;
        endPasses();
    }

    private static void pass(RenderTarget target, RenderTarget source, ShaderProgram program,
                             CompiledShaderProgram shader, float offset, int width, int height) {
        target.bindWrite(true);
        RenderSystem.setShaderTexture(0, source.getColorTextureId());
        ShaderUtils.set(shader, "uOffset", offset, offset);
        ShaderUtils.set(shader, "uHalfPixel", 1.0F / (float) width, 1.0F / (float) height);
        ShaderUtils.set(shader, "uSize", (float) width, (float) height);
        ShaderUtils.set(shader, "color", 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(program);
        quad();
    }

    private static void drawGlow(RenderTarget main) {
        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.HANDS_GLOW);
        if (shader == null || bloom == null) {
            broken = shader == null;
            return;
        }

        int theme = ThemeManager.getColor();
        int second = ThemeManager.getColor(1);

        beginPasses();
        main.bindWrite(true);
        RenderSystem.enableBlend();
        // Аддитивно: свечение лежит поверх рук и не гасит их цвет.
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.depthMask(false);
        RenderSystem.setShaderTexture(0, bloom.getColorTextureId());
        RenderSystem.setShaderTexture(1, mask.getColorTextureId());
        ShaderUtils.set(shader, "color", channel(theme, 16), channel(theme, 8), channel(theme, 0));
        ShaderUtils.set(shader, "color2", channel(second, 16), channel(second, 8), channel(second, 0));
        ShaderUtils.set(shader, "exposure", GLOW_EXPOSURE);
        ShaderUtils.set(shader, "alpha", outlineAlpha);
        RenderSystem.setShader(ShaderUtils.HANDS_GLOW);
        quad();

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        endPasses();
    }

    // Маска в сетку 256x144 — столько же, сколько в референсе: достаточно для клика, дёшево для чтения.
    private static void copyGrid() {
        beginPasses();
        grid.bindWrite(true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, mask.getColorTextureId());
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        quad();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        endPasses();
    }

    private static void readGrid() {
        if (gridBuffer == null) {
            gridBuffer = BufferUtils.createByteBuffer(GRID_PIXELS.length);
        }

        grid.bindRead();
        GL11.glReadPixels(0, 0, GRID_WIDTH, GRID_HEIGHT, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, gridBuffer);
        gridBuffer.position(0);
        gridBuffer.get(GRID_PIXELS);
        gridBuffer.position(0);
    }

    // Проходы рисуются полноэкранным квадом, но матрицы трогать нельзя: в 1.21.4 у
    // RenderSystem нет ProjectionType, а активны сейчас перспектива рук и матрица камеры.
    // Поэтому углы квада берутся обратной матрицей (Proj * ModelView)^-1 из NDC-углов:
    // шейдер умножит их обратно теми же матрицами, и квад ляжет ровно на весь viewport
    // любого буфера — своего (маска, блюр, сетка) или главного (свечение).
    private static void beginPasses() {
        Matrix4f projection = RenderSystem.getProjectionMatrix();
        Matrix4f modelView = RenderSystem.getModelViewMatrix();
        if (projection == null || modelView == null) {
            cornersValid = false;
            return;
        }

        INVERSE.set(projection).mul(modelView);
        cornersValid = Math.abs(INVERSE.determinant()) > 1.0E-12F;
        if (!cornersValid) {
            return;
        }

        INVERSE.invert();
        corner(0, -1.0F, -1.0F);
        corner(1, -1.0F, 1.0F);
        corner(2, 1.0F, 1.0F);
        corner(3, 1.0F, -1.0F);

        RenderSystem.disableDepthTest();
        // Обратная матрица может поменять обход вершин — с включённым отсечением квад бы исчез.
        RenderSystem.disableCull();
    }

    private static void corner(int index, float ndcX, float ndcY) {
        CORNER.set(ndcX, ndcY, 0.0F, 1.0F);
        INVERSE.transform(CORNER);
        CORNER_X[index] = CORNER.x();
        CORNER_Y[index] = CORNER.y();
        CORNER_Z[index] = CORNER.z();
    }

    private static void endPasses() {
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.setShaderTexture(1, 0);
    }

    private static void quad() {
        if (!cornersValid) {
            return;
        }

        Matrix4f matrix = QUAD_STACK.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        vertex(buffer, matrix, 0, 0.0F, 0.0F);
        vertex(buffer, matrix, 1, 0.0F, 1.0F);
        vertex(buffer, matrix, 2, 1.0F, 1.0F);
        vertex(buffer, matrix, 3, 1.0F, 0.0F);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }

    private static void vertex(BufferBuilder buffer, Matrix4f matrix, int index, float u, float v) {
        buffer.addVertex(matrix, CORNER_X[index], CORNER_Y[index], CORNER_Z[index])
                .setUv(u, v)
                .setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static float channel(int argb, int shift) {
        return (float) ((argb >> shift) & 0xFF) / 255.0F;
    }

    private static void destroyTargets() {
        depthBefore = destroy(depthBefore);
        depthAfter = destroy(depthAfter);
        mask = destroy(mask);
        blurA = destroy(blurA);
        blurB = destroy(blurB);
        grid = destroy(grid);
    }

    private static TextureTarget destroy(TextureTarget target) {
        if (target == null) {
            return null;
        }
        try {
            target.destroyBuffers();
        } catch (Throwable throwable) {
            Swerve.LOGGER.warn("Failed to release hand outline buffer", throwable);
        }
        return null;
    }

    private static void fail(String what, Throwable throwable) {
        broken = true;
        depthTaken = false;
        Swerve.LOGGER.error("ViewModel: {} failed, hand outline is disabled", what, throwable);
    }
}
