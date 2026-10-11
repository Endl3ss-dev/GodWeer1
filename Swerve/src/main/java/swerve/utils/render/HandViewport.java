package swerve.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import swerve.Swerve;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class HandViewport {

    private static final int MAIN = 0;
    private static final int OFF = 1;
    private static final int SLOTS = 2;

    private static final float BASIS_STEP = 0.25F;
    private static final long STALE_NANOS = 250_000_000L;
    private static final float MIN_UNITS = 1.0E-3F;

    private static final Matrix4f COMBINED = new Matrix4f();
    private static final float[] POINT = new float[2];
    private static final float[] BASIS_X = new float[2];
    private static final float[] BASIS_Y = new float[2];

    private static final float[] BASE_PPU_X = new float[SLOTS];
    private static final float[] BASE_PPU_Y = new float[SLOTS];
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

    public static void setWanted(boolean value) {
        wanted = value;
    }

    public static boolean isWanted() {
        return wanted;
    }

    // [!] База редактора: пиксели-на-единицу в той точке poseStack, где ViewModel кладёт
    // своё смещение (до translate/scale). Раньше здесь считались матрица и глубина, которые
    // никто не читал, а drag() делил дельту курсора на уже отмасштабированный ppu — при
    // масштабе руки != 1 рука отставала от курсора ровно в scale раз.
    public static void captureBase(InteractionHand hand, PoseStack poseStack, float handX, float handY, float handZ) {
        int slot = slot(hand);
        if (slot < 0 || poseStack == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        PoseStack.Pose pose = poseStack.last();
        Matrix4fc projection = RenderSystem.getProjectionMatrix();
        Matrix4fc modelView = RenderSystem.getModelViewMatrix();
        if (width <= 0 || height <= 0 || pose == null || projection == null || modelView == null) {
            baseCaptured[slot] = false;
            return;
        }

        COMBINED.set(projection).mul(modelView).mul(pose.pose());

        if (!project(COMBINED, handX, handY, handZ, POINT, width, height)) {
            baseCaptured[slot] = false;
            return;
        }

        float centerX = POINT[0];
        float centerY = POINT[1];

        if (!project(COMBINED, handX + BASIS_STEP, handY, handZ, BASIS_X, width, height)
                || !project(COMBINED, handX, handY + BASIS_STEP, handZ, BASIS_Y, width, height)) {
            baseCaptured[slot] = false;
            return;
        }

        float pixelsX = Math.abs(BASIS_X[0] - centerX) / BASIS_STEP;
        float pixelsY = Math.abs(BASIS_Y[1] - centerY) / BASIS_STEP;
        if (pixelsX < MIN_UNITS || pixelsY < MIN_UNITS) {
            baseCaptured[slot] = false;
            return;
        }

        BASE_PPU_X[slot] = pixelsX;
        BASE_PPU_Y[slot] = pixelsY;
        baseCaptured[slot] = true;
    }

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

    public static float basePixelsPerUnitX(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 || !baseCaptured[slot] ? 0.0F : BASE_PPU_X[slot];
    }

    public static float basePixelsPerUnitY(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 || !baseCaptured[slot] ? 0.0F : BASE_PPU_Y[slot];
    }

    public static boolean isAnyReady() {
        return isReady(InteractionHand.MAIN_HAND) || isReady(InteractionHand.OFF_HAND);
    }

    public static float anchorX(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : anchorX[slot];
    }

    public static float anchorY(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : anchorY[slot];
    }

    public static float pixelsPerUnitX(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : unitX[slot];
    }

    public static float pixelsPerUnitY(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : unitY[slot];
    }

    public static boolean hasAny() {
        return isAnyReady();
    }

    public static float mouseGuiX(Minecraft minecraft) {
        int screenWidth = minecraft.getWindow().getScreenWidth();
        if (screenWidth <= 0) {
            return 0.0F;
        }
        return (float) (minecraft.mouseHandler.xpos() * (double) minecraft.getWindow().getGuiScaledWidth() / (double) screenWidth);
    }

    public static float mouseGuiY(Minecraft minecraft) {
        int screenHeight = minecraft.getWindow().getScreenHeight();
        if (screenHeight <= 0) {
            return 0.0F;
        }
        return (float) (minecraft.mouseHandler.ypos() * (double) minecraft.getWindow().getGuiScaledHeight() / (double) screenHeight);
    }

    public static double guiScale(Minecraft minecraft) {
        return Math.max(1.0D, minecraft.getWindow().getGuiScale());
    }

    public static void consume() {
        captured[MAIN] = false;
        captured[OFF] = false;
    }

    public static void reset() {
        consume();
        baseCaptured[MAIN] = false;
        baseCaptured[OFF] = false;
        BASE_PPU_X[MAIN] = 0.0F;
        BASE_PPU_Y[MAIN] = 0.0F;
        BASE_PPU_X[OFF] = 0.0F;
        BASE_PPU_Y[OFF] = 0.0F;
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
        float clipX = matrix.m00() * x + matrix.m10() * y + matrix.m20() * z + matrix.m30();
        float clipY = matrix.m01() * x + matrix.m11() * y + matrix.m21() * z + matrix.m31();
        float clipW = matrix.m03() * x + matrix.m13() * y + matrix.m23() * z + matrix.m33();
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
