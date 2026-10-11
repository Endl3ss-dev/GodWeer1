package swerve.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

// Экранная привязка рук от первого лица: где рука в пикселях окна и сколько пикселей
// занимает один мировой юнит в её пространстве. Нужно ViewModel для двойного клика по
// руке, перетаскивания и обводки — без этих данных клик «в руку» был бы гаданием.
// Матрицы и временные массивы переиспользуются: на руку три проекции, ноль аллокаций.
public final class HandViewport {

    private static final int MAIN = 0;
    private static final int OFF = 1;
    private static final int SLOTS = 2;

    private static final float BASIS_STEP = 0.25F;
    private static final long STALE_NANOS = 250_000_000L;

    private static final Matrix4f COMBINED = new Matrix4f();
    private static final float[] POINT = new float[2];
    private static final float[] BASIS_X = new float[2];
    private static final float[] BASIS_Y = new float[2];

    private static final boolean[] captured = new boolean[SLOTS];
    private static final long[] capturedAt = new long[SLOTS];
    private static final float[] anchorX = new float[SLOTS];
    private static final float[] anchorY = new float[SLOTS];
    private static final float[] unitX = new float[SLOTS];
    private static final float[] unitY = new float[SLOTS];

    private HandViewport() {
    }

    // Вызывается из renderArmWithItem после того, как смещения и масштаб руки уже в матрице.
    public static void capture(InteractionHand hand, PoseStack poseStack, float handX, float handY, float handZ) {
        int slot = slot(hand);
        if (slot < 0 || poseStack == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();
        if (width <= 0 || height <= 0) {
            captured[slot] = false;
            return;
        }

        PoseStack.Pose pose = poseStack.last();
        if (pose == null) {
            captured[slot] = false;
            return;
        }

        Matrix4fc projection = RenderSystem.getProjectionMatrix();
        Matrix4fc modelView = RenderSystem.getModelViewMatrix();
        if (projection == null || modelView == null) {
            captured[slot] = false;
            return;
        }

        COMBINED.set(projection).mul(modelView).mul(pose.pose());

        if (!project(COMBINED, handX, handY, handZ, POINT, width, height)) {
            captured[slot] = false;
            return;
        }

        float centerX = POINT[0];
        float centerY = POINT[1];

        if (!project(COMBINED, handX + BASIS_STEP, handY, handZ, BASIS_X, width, height)
                || !project(COMBINED, handX, handY + BASIS_STEP, handZ, BASIS_Y, width, height)) {
            captured[slot] = false;
            return;
        }

        float pixelsX = Math.abs(BASIS_X[0] - centerX) / BASIS_STEP;
        float pixelsY = Math.abs(BASIS_Y[1] - centerY) / BASIS_STEP;
        if (pixelsX < 1.0E-4F || pixelsY < 1.0E-4F) {
            captured[slot] = false;
            return;
        }

        anchorX[slot] = centerX;
        anchorY[slot] = centerY;
        unitX[slot] = pixelsX;
        unitY[slot] = pixelsY;
        captured[slot] = true;
        capturedAt[slot] = System.nanoTime();
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

    // Ближайшая к курсору рука в пределах radiusUnits мировых юнитов её собственного масштаба.
    public static InteractionHand nearest(double mouseX, double mouseY, float radiusUnits) {
        InteractionHand best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int slot = 0; slot < SLOTS; slot++) {
            InteractionHand hand = slot == MAIN ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (!isReady(hand)) {
                continue;
            }

            double radius = Math.max(16.0D, radiusUnits * Math.max(unitX[slot], unitY[slot]));
            double deltaX = mouseX - anchorX[slot];
            double deltaY = mouseY - anchorY[slot];
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (distance > radius || distance >= bestDistance) {
                continue;
            }

            bestDistance = distance;
            best = hand;
        }

        return best;
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

    // HUD рисуется сразу после рук: сбрасываем привязки в конце кадра, чтобы обводка
    // не оставалась висеть там, где руки в этом кадре уже не рисовались (F5, прицел, hideGui).
    public static void consume() {
        captured[MAIN] = false;
        captured[OFF] = false;
    }

    public static void reset() {
        consume();
        anchorX[MAIN] = 0.0F;
        anchorY[MAIN] = 0.0F;
        anchorX[OFF] = 0.0F;
        anchorY[OFF] = 0.0F;
        unitX[MAIN] = 0.0F;
        unitY[MAIN] = 0.0F;
        unitX[OFF] = 0.0F;
        unitY[OFF] = 0.0F;
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
