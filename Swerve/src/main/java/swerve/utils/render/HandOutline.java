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

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public final class HandOutline {

    private static final int GRID_WIDTH = 256;
    private static final int GRID_HEIGHT = 144;
    private static final int COVER_THRESHOLD = 110;
    private static final float MASK_THRESHOLD = 0.0005F;
    private static final long STALE_NANOS = 250_000_000L;

    private static final PoseStack QUAD_STACK = new PoseStack();
    private static final byte[] GRID_PIXELS = new byte[GRID_WIDTH * GRID_HEIGHT * 4];

    private static TextureTarget depthBefore;
    private static TextureTarget depthAfter;
    private static TextureTarget mask;
    private static TextureTarget grid;

    private static ByteBuffer gridBuffer;

    private static int frameWidth = -1;
    private static int frameHeight = -1;

    private static boolean wanted;
    private static boolean hooked;
    private static boolean depthTaken;
    private static boolean pendingComposite;
    private static boolean maskReady;
    private static boolean broken;

    private static float outlineAlpha;
    private static float outlineWidth = 3.0F;

    private static long maskAt;
    private static long wantedSince;
    private static long lastReport;

    // [!] glReadPixels — синхронный stall пайплайна, а маска нужна только hover-тесту.
    // Читаем её на движении курсора и минимум раз в 4 кадра: чаще — простой GPU,
    // реже — maskAt устареет и isHandCoveredAt начнёт отвечать false.
    private static final int GRID_REFRESH_MASK = 3;
    private static float lastPointerX = Float.NaN;
    private static float lastPointerY = Float.NaN;
    private static int gridCounter;

    private HandOutline() {
    }

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
        // [!] Нижняя граница совпадает с минимумом настройки «Размер обводки» (0.5):
        // раньше всё, что меньше 1.0, молча подменялось единицей.
        outlineWidth = width < 0.5F ? 0.5F : (width > 8.0F ? 8.0F : width);
    }

    public static boolean isShaderReady() {
        return !broken;
    }

    public static boolean isMaskReady() {
        return maskReady && System.nanoTime() - maskAt < STALE_NANOS;
    }

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

    public static void afterHands() {
        if (!wanted || !depthTaken) {
            return;
        }
        depthTaken = false;

        try {
            RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
            if (main == null || !ensureTargets()) {
                return;
            }

            depthAfter.copyDepthFrom(main);
            main.bindWrite(false);
            pendingComposite = true;
        } catch (Throwable throwable) {
            fail("depth copy after hands", throwable);
        }
    }

    public static void composite() {
        if (!pendingComposite) {
            return;
        }
        pendingComposite = false;

        if (!wanted || depthBefore == null || depthAfter == null || mask == null || grid == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        if (main == null) {
            return;
        }

        float guiWidth = Math.max(1.0F, (float) minecraft.getWindow().getGuiScaledWidth());
        float guiHeight = Math.max(1.0F, (float) minecraft.getWindow().getGuiScaledHeight());

        try {
            if (!buildMask(guiWidth, guiHeight)) {
                main.bindWrite(true);
                return;
            }

            if (gridNeedsRefresh(minecraft)) {
                copyGrid(guiWidth, guiHeight);
                readGrid();
                maskReady = true;
                maskAt = System.nanoTime();
            }

            if (outlineAlpha > 0.004F) {
                drawOutline(main, guiWidth, guiHeight);
            }

            restoreState(main);
        } catch (Throwable throwable) {
            fail("hand outline composite", throwable);
            try {
                restoreState(main);
            } catch (Throwable ignored) {
            }
        }
    }

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

    public static float depthAt(float guiX, float guiY) {
        if (!isMaskReady()) {
            return -1.0F;
        }

        Minecraft minecraft = Minecraft.getInstance();
        int guiWidth = minecraft.getWindow().getGuiScaledWidth();
        int guiHeight = minecraft.getWindow().getGuiScaledHeight();
        if (guiWidth <= 0 || guiHeight <= 0) {
            return -1.0F;
        }

        int column = (int) (guiX / (float) guiWidth * (float) GRID_WIDTH);
        int row = GRID_HEIGHT - 1 - (int) (guiY / (float) guiHeight * (float) GRID_HEIGHT);
        if (column < 0 || column >= GRID_WIDTH || row < 0 || row >= GRID_HEIGHT) {
            return -1.0F;
        }

        int index = (row * GRID_WIDTH + column) * 4;
        if ((GRID_PIXELS[index + 3] & 0xFF) < COVER_THRESHOLD) {
            return -1.0F;
        }

        int high = GRID_PIXELS[index] & 0xFF;
        int low = GRID_PIXELS[index + 1] & 0xFF;
        return (float) (high * 256 + low) / 65535.0F;
    }

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
        pendingComposite = false;
        maskReady = false;
        depthTaken = false;
        frameWidth = -1;
        frameHeight = -1;
        // NaN != NaN — следующий кадр редактора прочитает маску сразу, без ожидания счётчика.
        lastPointerX = Float.NaN;
        lastPointerY = Float.NaN;
        gridCounter = 0;
    }

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

        depthBefore = new TextureTarget(width, height, true);
        depthAfter = new TextureTarget(width, height, true);
        mask = new TextureTarget(width, height, false);
        grid = new TextureTarget(GRID_WIDTH, GRID_HEIGHT, false);

        mask.setFilterMode(GL11.GL_LINEAR);
        grid.setFilterMode(GL11.GL_LINEAR);
        return true;
    }

    private static boolean buildMask(float guiWidth, float guiHeight) {
        try {
            CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.HANDS_MASK_DIFF);
            if (shader == null) {
                broken = true;
                return false;
            }

            mask.bindWrite(true);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT);
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, depthBefore.getDepthTextureId());
            RenderSystem.setShaderTexture(1, depthAfter.getDepthTextureId());
            ShaderUtils.set(shader, "uThreshold", MASK_THRESHOLD);
            RenderSystem.setShader(ShaderUtils.HANDS_MASK_DIFF);
            guiQuad(guiWidth, guiHeight);
            return true;
        } catch (Throwable t) {
            fail("hand mask build", t);
            return false;
        }
    }

    private static void drawOutline(RenderTarget main, float guiWidth, float guiHeight) {
        try {
            CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.BLOCK_OVERLAY);
            if (shader == null) {
                broken = true;
                return;
            }

            int theme = ThemeManager.getColor();
            int second = ThemeManager.getColor(1);

            main.bindWrite(true);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, mask.getColorTextureId());
            ShaderUtils.set(shader, "texelSize", 1.0F / (float) frameWidth, 1.0F / (float) frameHeight);
            ShaderUtils.set(shader, "color", channel(theme, 16), channel(theme, 8), channel(theme, 0));
            ShaderUtils.set(shader, "color2", channel(second, 16), channel(second, 8), channel(second, 0));
            ShaderUtils.set(shader, "time", 0.0F);
            ShaderUtils.set(shader, "speed", 0.0F);
            ShaderUtils.set(shader, "scale", 0.0F);
            ShaderUtils.set(shader, "outline", outlineWidth * widthScale());
            ShaderUtils.set(shader, "glow", 0.0F);
            ShaderUtils.set(shader, "fill", 0.0F);
            ShaderUtils.set(shader, "alpha", outlineAlpha);
            ShaderUtils.set(shader, "outlineOnly", 1.0F);
            RenderSystem.setShader(ShaderUtils.BLOCK_OVERLAY);
            guiQuad(guiWidth, guiHeight);
        } catch (Throwable t) {
            fail("hand outline draw", t);
        }
    }

    private static float widthScale() {
        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getScreenWidth();
        int framebufferWidth = minecraft.getWindow().getWidth();
        double guiScale = Math.max(1.0D, minecraft.getWindow().getGuiScale());
        float ratio = screenWidth > 0 ? (float) framebufferWidth / (float) screenWidth : 1.0F;
        return Math.max(1.0F, (float) guiScale * ratio);
    }

    private static void copyGrid(float guiWidth, float guiHeight) {
        grid.bindWrite(true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, mask.getColorTextureId());
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        guiQuad(guiWidth, guiHeight);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static boolean gridNeedsRefresh(Minecraft minecraft) {
        float pointerX = (float) minecraft.mouseHandler.xpos();
        float pointerY = (float) minecraft.mouseHandler.ypos();
        boolean moved = pointerX != lastPointerX || pointerY != lastPointerY;
        lastPointerX = pointerX;
        lastPointerY = pointerY;
        gridCounter = (gridCounter + 1) & 0xFFFF;
        return moved || (gridCounter & GRID_REFRESH_MASK) == 0;
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

    private static void restoreState(RenderTarget main) {
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.setShaderTexture(1, 0);
        main.bindWrite(true);
    }

    private static void guiQuad(float width, float height) {
        Matrix4f matrix = QUAD_STACK.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        buffer.addVertex(matrix, 0.0F, 0.0F, 0.0F).setUv(0.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.addVertex(matrix, 0.0F, height, 0.0F).setUv(0.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.addVertex(matrix, width, height, 0.0F).setUv(1.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.addVertex(matrix, width, 0.0F, 0.0F).setUv(1.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }

    private static float channel(int argb, int shift) {
        return (float) ((argb >> shift) & 0xFF) / 255.0F;
    }

    private static void destroyTargets() {
        depthBefore = destroy(depthBefore);
        depthAfter = destroy(depthAfter);
        mask = destroy(mask);
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
        pendingComposite = false;
        Swerve.LOGGER.error("ViewModel: {} failed, hand outline is disabled", what, throwable);
    }
}
