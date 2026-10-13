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

    private static final float BASIS_STEP = 0.125F;
    private static final float MIN_DETERMINANT = 1.0E-3F;
    private static final long STALE_NANOS = 350_000_000L;

    private static final int FAILURE_NONE = 0;
    private static final int FAILURE_SIZE = 1;
    private static final int FAILURE_MATRIX = 2;
    private static final int FAILURE_PROJECTION = 3;
    private static final int FAILURE_JACOBIAN = 4;

    private static final Matrix4f COMBINED = new Matrix4f();
    private static final float[] PROJECTED = new float[2];

    private static final boolean[] CAPTURED = new boolean[SLOTS];
    private static final boolean[] INVERSE_VALID = new boolean[SLOTS];
    private static final long[] CAPTURED_AT = new long[SLOTS];
    private static final float[] ANCHOR_X = new float[SLOTS];
    private static final float[] ANCHOR_Y = new float[SLOTS];
    private static final float[] INVERSE_00 = new float[SLOTS];
    private static final float[] INVERSE_01 = new float[SLOTS];
    private static final float[] INVERSE_10 = new float[SLOTS];
    private static final float[] INVERSE_11 = new float[SLOTS];
    private static final float[] HIT_RADIUS_SQ = new float[SLOTS];

    private static boolean wanted;
    private static int lastFailure;
    private static long lastFailureLog;

    private HandViewport() {
    }

    public static void beginFrame(boolean value) {
        wanted = value;
        CAPTURED[MAIN] = false;
        CAPTURED[OFF] = false;
        INVERSE_VALID[MAIN] = false;
        INVERSE_VALID[OFF] = false;
        if (!value) {
            lastFailure = FAILURE_NONE;
        }
    }

    public static void capture(InteractionHand hand, PoseStack poseStack,
                               float anchorX, float anchorY, float anchorZ,
                               float offsetX, float offsetY, float scale) {
        if (!wanted || poseStack == null) {
            return;
        }
        int slot = slot(hand);
        if (slot < 0) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        if (width <= 0 || height <= 0) {
            fail(slot, FAILURE_SIZE);
            return;
        }

        PoseStack.Pose pose = poseStack.last();
        Matrix4fc projection = RenderSystem.getProjectionMatrix();
        Matrix4fc modelView = RenderSystem.getModelViewMatrix();
        if (pose == null || projection == null || modelView == null) {
            fail(slot, FAILURE_MATRIX);
            return;
        }

        COMBINED.set(projection).mul(modelView).mul(pose.pose());

        float pointX = anchorX + offsetX;
        float pointY = anchorY + offsetY;
        if (!project(COMBINED, pointX, pointY, anchorZ, width, height)) {
            fail(slot, FAILURE_PROJECTION);
            return;
        }
        float centerX = PROJECTED[0];
        float centerY = PROJECTED[1];

        if (!project(COMBINED, pointX + BASIS_STEP, pointY, anchorZ, width, height)) {
            fail(slot, FAILURE_PROJECTION);
            return;
        }
        float xBasisX = PROJECTED[0];
        float xBasisY = PROJECTED[1];

        if (!project(COMBINED, pointX, pointY + BASIS_STEP, anchorZ, width, height)) {
            fail(slot, FAILURE_PROJECTION);
            return;
        }
        float yBasisX = PROJECTED[0];
        float yBasisY = PROJECTED[1];

        float j00 = (xBasisX - centerX) / BASIS_STEP;
        float j10 = (xBasisY - centerY) / BASIS_STEP;
        float j01 = (yBasisX - centerX) / BASIS_STEP;
        float j11 = (yBasisY - centerY) / BASIS_STEP;
        float determinant = j00 * j11 - j01 * j10;

        ANCHOR_X[slot] = centerX;
        ANCHOR_Y[slot] = centerY;
        CAPTURED[slot] = true;
        CAPTURED_AT[slot] = System.nanoTime();

        float ppuX = (float) Math.sqrt(j00 * j00 + j10 * j10);
        float ppuY = (float) Math.sqrt(j01 * j01 + j11 * j11);
        float radius = clamp(Math.max(ppuX, ppuY) * 0.55F * Math.max(SCALE_FLOOR, scale), 28.0F, 96.0F);
        HIT_RADIUS_SQ[slot] = radius * radius;

        if (Float.isFinite(determinant) && Math.abs(determinant) >= MIN_DETERMINANT) {
            float inverseDeterminant = 1.0F / determinant;
            INVERSE_00[slot] = j11 * inverseDeterminant;
            INVERSE_01[slot] = -j01 * inverseDeterminant;
            INVERSE_10[slot] = -j10 * inverseDeterminant;
            INVERSE_11[slot] = j00 * inverseDeterminant;
            INVERSE_VALID[slot] = true;
            lastFailure = FAILURE_NONE;
        } else {
            INVERSE_VALID[slot] = false;
            lastFailure = FAILURE_JACOBIAN;
        }
    }

    private static final float SCALE_FLOOR = 0.2F;

    public static boolean isReady(InteractionHand hand) {
        int slot = slot(hand);
        return slot >= 0 && CAPTURED[slot] && System.nanoTime() - CAPTURED_AT[slot] <= STALE_NANOS;
    }

    public static boolean hasInverse(InteractionHand hand) {
        int slot = slot(hand);
        return slot >= 0 && isReady(hand) && INVERSE_VALID[slot];
    }

    public static float anchorX(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : ANCHOR_X[slot];
    }

    public static float anchorY(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : ANCHOR_Y[slot];
    }

    public static float inverse00(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : INVERSE_00[slot];
    }

    public static float inverse01(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : INVERSE_01[slot];
    }

    public static float inverse10(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : INVERSE_10[slot];
    }

    public static float inverse11(InteractionHand hand) {
        int slot = slot(hand);
        return slot < 0 ? 0.0F : INVERSE_11[slot];
    }

    public static boolean isNear(InteractionHand hand, float mouseX, float mouseY) {
        int slot = slot(hand);
        if (slot < 0 || !isReady(hand)) {
            return false;
        }
        float dx = mouseX - ANCHOR_X[slot];
        float dy = mouseY - ANCHOR_Y[slot];
        return dx * dx + dy * dy <= HIT_RADIUS_SQ[slot];
    }

    public static boolean hasAny() {
        return isReady(InteractionHand.MAIN_HAND) || isReady(InteractionHand.OFF_HAND);
    }

    public static float mouseGuiX(Minecraft minecraft) {
        int screenWidth = minecraft.getWindow().getScreenWidth();
        if (screenWidth <= 0) {
            return 0.0F;
        }
        return (float) (minecraft.mouseHandler.xpos()
                * (double) minecraft.getWindow().getGuiScaledWidth() / (double) screenWidth);
    }

    public static float mouseGuiY(Minecraft minecraft) {
        int screenHeight = minecraft.getWindow().getScreenHeight();
        if (screenHeight <= 0) {
            return 0.0F;
        }
        return (float) (minecraft.mouseHandler.ypos()
                * (double) minecraft.getWindow().getGuiScaledHeight() / (double) screenHeight);
    }

    public static void reset() {
        wanted = false;
        CAPTURED[MAIN] = false;
        CAPTURED[OFF] = false;
        INVERSE_VALID[MAIN] = false;
        INVERSE_VALID[OFF] = false;
        ANCHOR_X[MAIN] = 0.0F;
        ANCHOR_Y[MAIN] = 0.0F;
        ANCHOR_X[OFF] = 0.0F;
        ANCHOR_Y[OFF] = 0.0F;
        lastFailure = FAILURE_NONE;
    }

    public static void reportStale() {
        long now = System.currentTimeMillis();
        if (now - lastFailureLog < 5_000L) {
            return;
        }
        lastFailureLog = now;

        String reason = switch (lastFailure) {
            case FAILURE_SIZE -> "invalid GUI size";
            case FAILURE_MATRIX -> "projection matrix is unavailable";
            case FAILURE_PROJECTION -> "hand pivot is outside the projection";
            case FAILURE_JACOBIAN -> "hand projection is degenerate";
            default -> "renderArmWithItem did not reach the capture point";
        };
        Swerve.LOGGER.warn("ViewModel: hand pivot is unavailable ({})", reason);
    }

    private static void fail(int slot, int reason) {
        CAPTURED[slot] = false;
        INVERSE_VALID[slot] = false;
        lastFailure = reason;
    }

    private static int slot(InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            return MAIN;
        }
        return hand == InteractionHand.OFF_HAND ? OFF : -1;
    }

    private static boolean project(Matrix4fc matrix, float x, float y, float z, int width, int height) {
        float clipX = matrix.m00() * x + matrix.m10() * y + matrix.m20() * z + matrix.m30();
        float clipY = matrix.m01() * x + matrix.m11() * y + matrix.m21() * z + matrix.m31();
        float clipW = matrix.m03() * x + matrix.m13() * y + matrix.m23() * z + matrix.m33();
        if (!Float.isFinite(clipW) || clipW <= 1.0E-6F) {
            return false;
        }

        float normalizedX = clipX / clipW;
        float normalizedY = clipY / clipW;
        if (!Float.isFinite(normalizedX) || !Float.isFinite(normalizedY)
                || normalizedX < -4.0F || normalizedX > 4.0F
                || normalizedY < -4.0F || normalizedY > 4.0F) {
            return false;
        }

        PROJECTED[0] = (normalizedX * 0.5F + 0.5F) * width;
        PROJECTED[1] = (1.0F - (normalizedY * 0.5F + 0.5F)) * height;
        return true;
    }

    private static float clamp(float value, float min, float max) {
        return value < min ? min : Math.min(value, max);
    }
}
