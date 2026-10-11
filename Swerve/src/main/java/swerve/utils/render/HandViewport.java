package swerve.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import swerve.Swerve;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

// Экранная привязка рук от первого лица: точка руки, цена мирового юнита и матрица руки —
// в GUI-единицах (тех же, в которых живёт курсор и HUD). Нужно ViewModel для наведения,
// двойного клика, перетаскивания и запасного варианта обводки.
//
// Два снимка на руку: базовый (до смещений и масштаба) — по нему считается, сколько мировых
// юнитов стоит один пиксель курсора, иначе рука при перетаскивании «плыла» бы не за мышью;
// и видимый (после смещений) — точка, по которой выбирается рука и рисуется запасная рамка.
// Пиксель-точный ответ «курсор над рукой?» даёт не проекция, а маска силуэта (HandOutline).
//
// NDC -> GUI напрямую, без пикселей фреймбуфера: иначе на HiDPI (screenWidth != getWidth)
// курсор и рука оказывались в разных системах координат и клик по руке не проходил.
// Матрицы и временные массивы переиспользуются: ноль аллокаций в кадре.
public final class HandViewport {

    private static final int MAIN = 0;
    private static final int OFF = 1;
    private static final int SLOTS = 2;

    private static final float BASIS_STEP = 0.25F;
    private static final long STALE_NANOS = 250_000_000L;
    private static final float MIN_UNITS = 1.0E-3F;
    private static final float MIN_DEPTH = 1.0E-4F;

    private static final Matrix4f COMBINED = new Matrix4f();
    private static final Matrix4f VIEW = new Matrix4f();
    private static final float[] POINT = new float[2];
    private static final float[] BASIS_X = new float[2];
    private static final float[] BASIS_Y = new float[2];

    private static final Matrix4f[] BASE_MATRIX = {new Matrix4f(), new Matrix4f()};
    private static final float[] BASE_DEPTH = new float[SLOTS];
    private static final boolean[] baseCaptured = new boolean[SLOTS];

    private static final boolean[] captured = new boolean[SLOTS];
    private static final long[] capturedAt = new long[SLOTS];
    private static final float[] anchorX = new float[SLOTS];
    private static final float[] anchorY = new float[SLOTS];
    private static final float[] unitX = new float[SLOTS];
    private static final float[] unitY = new float[SLOTS];

    private static boolean wanted;
    private static String lastFailure = "";
    private static long lastFailureLog;

    private HandViewport() {
    }

    // Снимки нужны только пока открыт чат: в игре они считаются впустую каждый кадр.
    public static void setWanted(boolean value) {
        wanted = value;
    }

    public static boolean isWanted() {
        return wanted;
    }

    // Базовая матрица руки: проекция * modelview * поза руки до смещений ViewModel,
    // плюс глубина точки крепления в пространстве глаза (для цены пикселя в юнитах).
    public static void captureBase(InteractionHand hand, PoseStack poseStack, float handX, float handY, float handZ) {
        int slot = slot(hand);
        if (slot < 0 || poseStack == null) {
            return;
        }

        PoseStack.Pose pose = poseStack.last();
        Matrix4fc projection = RenderSystem.getProjectionMatrix();
        Matrix4fc modelView = RenderSystem.getModelViewMatrix();
        if (pose == null || projection == null || modelView == null) {
            return;
        }

        COMBINED.set(projection).mul(modelView).mul(pose.pose());
        BASE_MATRIX[slot].set(COMBINED);

        VIEW.set(modelView).mul(pose.pose());
        float depth = -(VIEW.m20() * handX + VIEW.m21() * handY + VIEW.m22() * handZ + VIEW.m23());
        BASE_DEPTH[slot] = depth > MIN_DEPTH ? depth : 0.0F;
        baseCaptured[slot] = true;
    }

    // Вызывается из renderArmWithItem после того, как смещения и масштаб руки уже в матрице.
    public static void capture(InteractionHand hand, PoseStack poseStack, float handX, float handY, float handZ) {
        int slot = slot(hand);
        if (slot < 0 || poseStack == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        if (width <= 0 || height <= 0) {
            fail(slot, "gui size is " + width + "x" + height);
            return;
        }

        PoseStack.Pose pose = poseStack.last();
        if (pose == null) {
            fail(slot, "empty pose stack");
            return;
        }

        Matrix4fc projection = RenderSystem.getProjectionMatrix();
        Matrix4fc modelView = RenderSystem.getModelViewMatrix();
        if (projection == null || modelView == null) {
            fail(slot, "no projection/modelview in RenderSystem");
            return;
        }

        COMBINED.set(projection).mul(modelView).mul(pose.pose());

        if (!project(COMBINED, handX, handY, handZ, POINT, width, height)) {
            fail(slot, "anchor projection rejected");
            return;
        }

        float centerX = POINT[0];
        float centerY = POINT[1];

        if (!project(COMBINED, handX + BASIS_STEP, handY, handZ, BASIS_X, width, height)
                || !project(COMBINED, handX, handY + BASIS_STEP, handZ, BASIS_Y, width, height)) {
            fail(slot, "basis projection rejected");
            return;
        }

        float pixelsX = Math.abs(BASIS_X[0] - centerX) / BASIS_STEP;
        float pixelsY = Math.abs(BASIS_Y[1] - centerY) / BASIS_STEP;
        if (pixelsX < MIN_UNITS || pixelsY < MIN_UNITS) {
            fail(slot, "degenerate scale " + pixelsX + "/" + pixelsY);
            return;
        }

        anchorX[slot] = centerX;
        anchorY[slot] = centerY;
        unitX[slot] = pixelsX;
        unitY[slot] = pixelsY;
        captured[slot] = true;
        capturedAt[slot] = System.nanoTime();
        lastFailure = "";
    }

    public static boolean isReady(InteractionHand hand) {
        int slot = slot(hand);
        if (slot < 0 || !captured[slot]) {
            return false;
        }
        if (System.nanoTime() - capturedAt[slot] > STALE_NANOS) {
            captured[slot] = false;
            return false;
        }
        return true;
    }

    public static boolean isBaseReady(InteractionHand hand) {
        int slot = slot(hand);
        return slot >= 0 && baseCaptured[slot] && BASE_DEPTH[slot] > MIN_DEPTH;
    }

    public static boolean isAnyReady() {
        return isReady(InteractionHand.MAIN_HAND) || isReady(InteractionHand.OFF_HAND);
    }

    // Матрица руки без смещений — для пересчёта пикселей курсора в мировые юниты.
    public static Matrix4fc matrix(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? null : BASE_MATRIX[slot];
    }

    // Глубина точки крепления руки в юнитах (0.72 у ванильной руки).
    public static float depth(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : BASE_DEPTH[slot];
    }

    public static float anchorX(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : anchorX[slot];
    }

    public static float anchorY(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : anchorY[slot];
    }

    public static float pixelsPerUnitY(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : unitY[slot];
    }

    // Курсор в GUI-единицах: учитывает и guiScale, и соотношение окно/фреймбуфер (HiDPI).
    public static float mouseGuiX(Minecraft minecraft) {
        return (float) (minecraft.mouseHandler.xpos() * frameRatio(minecraft) / guiScale(minecraft));
    }

    public static float mouseGuiY(Minecraft minecraft) {
        return (float) (minecraft.mouseHandler.ypos() * frameRatio(minecraft) / guiScale(minecraft));
    }

    public static double guiScale(Minecraft minecraft) {
        return Math.max(1.0D, minecraft.getWindow().getGuiScale());
    }

    private static double frameRatio(Minecraft minecraft) {
        int screenWidth = minecraft.getWindow().getScreenWidth();
        int frameWidth = minecraft.getWindow().getWidth();
        if (screenWidth <= 0 || frameWidth <= 0) {
            return 1.0D;
        }
        return (double) frameWidth / (double) screenWidth;
    }

    // HUD рисуется сразу после рук: сбрасываем привязки в конце кадра, чтобы обводка не
    // оставалась висеть там, где руки в этом кадре уже не рисовались (F5, прицел, hideGui).
    public static void consume() {
        captured[MAIN] = false;
        captured[OFF] = false;
    }

    public static void reset() {
        consume();
        baseCaptured[MAIN] = false;
        baseCaptured[OFF] = false;
        BASE_DEPTH[MAIN] = 0.0F;
        BASE_DEPTH[OFF] = 0.0F;
        anchorX[MAIN] = 0.0F;
        anchorY[MAIN] = 0.0F;
        anchorX[OFF] = 0.0F;
        anchorY[OFF] = 0.0F;
        unitX[MAIN] = 0.0F;
        unitY[MAIN] = 0.0F;
        unitX[OFF] = 0.0F;
        unitY[OFF] = 0.0F;
        lastFailure = "";
    }

    // Один раз в 5 секунд сообщает, почему привязка не снялась — иначе «обводка не работает»
    // выглядит как магия без признаков жизни.
    public static void reportStale() {
        long now = System.currentTimeMillis();
        if (now - lastFailureLog < 5_000L) {
            return;
        }
        lastFailureLog = now;
        Swerve.LOGGER.warn("ViewModel: hand anchor is not captured ({})",
                lastFailure.isEmpty() ? "renderArmWithItem did not reach the inject point" : lastFailure);
    }

    private static void fail(int slot, String reason) {
        captured[slot] = false;
        lastFailure = reason;
    }

    private static int slot(InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            return MAIN;
        }
        return hand == InteractionHand.OFF_HAND ? OFF : -1;
    }

    private static boolean project(Matrix4fc matrix, float x, float y, float z, float[] out, int width, int height) {
        float clipX = matrix.m00() * x + matrix.m01() * y + matrix.m02() * z + matrix.m03();
        float clipY = matrix.m10() * x + matrix.m11() * y + matrix.m12() * z + matrix.m13();
        float clipW = matrix.m30() * x + matrix.m31() * y + matrix.m32() * z + matrix.m33();
        // w <= 0 — точка за камерой: проекция вывернулась бы наизнанку и обводка улетела бы в другой край.
        if (clipW <= 1.0E-6F) {
            return false;
        }

        float normalizedX = clipX / clipW;
        float normalizedY = clipY / clipW;
        if (normalizedX < -4.0F || normalizedX > 4.0F || normalizedY < -4.0F || normalizedY > 4.0F) {
            return false;
        }

        out[0] = (normalizedX * 0.5F + 0.5F) * (float) width;
        out[1] = (1.0F - (normalizedY * 0.5F + 0.5F)) * (float) height;
        return true;
    }
}
