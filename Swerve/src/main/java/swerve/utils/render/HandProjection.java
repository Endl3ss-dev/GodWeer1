package swerve.utils.render;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

// Экранная проекция рук от первого лица. Всё состояние живёт в render-потоке и переиспользуется:
// в горячих хуках нет аллокаций, только set/mul по заранее созданным матрицам.
public final class HandProjection {

    private static final Matrix4f MAIN_BASE = new Matrix4f();
    private static final Matrix4f OFF_BASE = new Matrix4f();
    private static final Matrix4f MAIN_POSE = new Matrix4f();
    private static final Matrix4f OFF_POSE = new Matrix4f();
    private static final Matrix4f TMP = new Matrix4f();
    private static final Vector4f VEC = new Vector4f();
    private static final FloatBuffer DEPTH = BufferUtils.createFloatBuffer(1);

    private static boolean mainBaseValid;
    private static boolean offBaseValid;
    private static boolean mainPivotValid;
    private static boolean offPivotValid;

    private static float mainPivotX;
    private static float mainPivotY;
    private static float offPivotX;
    private static float offPivotY;

    private static float mainLocalX;
    private static float mainLocalY;
    private static float offLocalX;
    private static float offLocalY;

    private static boolean probeValid;
    private static boolean probeFresh;
    private static float probeDepth = 1.0F;
    private static float probeNdcX;
    private static float probeNdcY;

    private HandProjection() {
    }

    public static void beginFrame() {
        mainBaseValid = false;
        offBaseValid = false;
        mainPivotValid = false;
        offPivotValid = false;
    }

    // Базовый фрейм руки — до сдвига ViewModel. В нём смещение и считается, поэтому
    // матрица не зависит от текущего offset и пригодна для обратной проекции курсора.
    public static void captureBase(InteractionHand hand, PoseStack poseStack) {
        Matrix4f pose = poseStack.last().pose();
        if (hand == InteractionHand.MAIN_HAND) {
            MAIN_POSE.set(pose);
            MAIN_BASE.set(RenderSystem.getProjectionMatrix()).mul(RenderSystem.getModelViewStack()).mul(pose);
            mainBaseValid = true;
        } else {
            OFF_POSE.set(pose);
            OFF_BASE.set(RenderSystem.getProjectionMatrix()).mul(RenderSystem.getModelViewStack()).mul(pose);
            offBaseValid = true;
        }
    }

    // Пивот = точка, вокруг которой масштабируется рука. Храним его и в экранных координатах
    // (выбор руки, якорь панели), и в базовом фрейме (компенсация масштаба при перетаскивании).
    public static void capturePivot(InteractionHand hand, PoseStack poseStack) {
        boolean main = hand == InteractionHand.MAIN_HAND;
        if (main ? !mainBaseValid : !offBaseValid) {
            return;
        }

        TMP.set(main ? MAIN_POSE : OFF_POSE).invert().mul(poseStack.last().pose());
        float localX = TMP.m30();
        float localY = TMP.m31();
        float localZ = TMP.m32();

        VEC.set(localX, localY, localZ, 1.0F);
        (main ? MAIN_BASE : OFF_BASE).transform(VEC);
        if (VEC.w <= 1.0E-6F) {
            return;
        }

        Window window = Minecraft.getInstance().getWindow();
        float guiScale = (float) window.getGuiScale();
        float guiWidth = window.getWidth() / guiScale;
        float guiHeight = window.getHeight() / guiScale;
        float screenX = (VEC.x / VEC.w * 0.5F + 0.5F) * guiWidth;
        float screenY = (0.5F - VEC.y / VEC.w * 0.5F) * guiHeight;

        if (main) {
            mainPivotX = screenX;
            mainPivotY = screenY;
            mainLocalX = localX;
            mainLocalY = localY;
            mainPivotValid = true;
        } else {
            offPivotX = screenX;
            offPivotY = screenY;
            offLocalX = localX;
            offLocalY = localY;
            offPivotValid = true;
        }
    }

    // Читаем 1 пиксель глубины под курсором сразу после отрисовки рук: перед ней ванилла
    // очищает depth, так что значение < 1.0 означает, что курсор лежит именно на руке/предмете.
    public static void probe() {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        int fbWidth = window.getWidth();
        int fbHeight = window.getHeight();
        int screenWidth = window.getScreenWidth();
        int screenHeight = window.getScreenHeight();
        probeFresh = true;

        if (fbWidth <= 0 || fbHeight <= 0 || screenWidth <= 0 || screenHeight <= 0) {
            probeValid = false;
            return;
        }

        int px = (int) (mc.mouseHandler.xpos() * fbWidth / screenWidth);
        int py = (int) (mc.mouseHandler.ypos() * fbHeight / screenHeight);
        if (px < 0 || py < 0 || px >= fbWidth || py >= fbHeight) {
            probeValid = false;
            return;
        }

        DEPTH.clear();
        GL11.glReadPixels(px, fbHeight - 1 - py, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, DEPTH);
        probeDepth = DEPTH.get(0);
        probeNdcX = (px + 0.5F) / fbWidth * 2.0F - 1.0F;
        probeNdcY = 1.0F - (py + 0.5F) / fbHeight * 2.0F;
        probeValid = true;
    }

    public static void endUiFrame() {
        if (!probeFresh) {
            probeValid = false;
        }
        probeFresh = false;
    }

    public static boolean isHandUnderCursor() {
        return probeValid && probeDepth < 1.0F;
    }

    public static boolean copyBaseInverse(InteractionHand hand, Matrix4f destination) {
        boolean main = hand == InteractionHand.MAIN_HAND;
        if (main ? !mainBaseValid : !offBaseValid) {
            return false;
        }
        destination.set(main ? MAIN_BASE : OFF_BASE).invert();
        return true;
    }

    public static boolean unprojectProbe(Matrix4f inverse, float[] out) {
        if (!isHandUnderCursor()) {
            return false;
        }
        VEC.set(probeNdcX, probeNdcY, probeDepth * 2.0F - 1.0F, 1.0F);
        inverse.transform(VEC);
        if (Math.abs(VEC.w) < 1.0E-7F) {
            return false;
        }
        out[0] = VEC.x / VEC.w;
        out[1] = VEC.y / VEC.w;
        out[2] = VEC.z / VEC.w;
        return true;
    }

    // Луч курсора пересекается с плоскостью z = planeZ в локальном фрейме руки:
    // точное перемещение без линеаризации (якобиана), как бы далеко ни увели руку.
    public static boolean rayPlane(Matrix4f inverse, float ndcX, float ndcY, float planeZ, float[] out) {
        VEC.set(ndcX, ndcY, -1.0F, 1.0F);
        inverse.transform(VEC);
        if (Math.abs(VEC.w) < 1.0E-7F) {
            return false;
        }
        float nearX = VEC.x / VEC.w;
        float nearY = VEC.y / VEC.w;
        float nearZ = VEC.z / VEC.w;

        VEC.set(ndcX, ndcY, 1.0F, 1.0F);
        inverse.transform(VEC);
        if (Math.abs(VEC.w) < 1.0E-7F) {
            return false;
        }
        float farX = VEC.x / VEC.w;
        float farY = VEC.y / VEC.w;
        float farZ = VEC.z / VEC.w;

        float dz = farZ - nearZ;
        if (Math.abs(dz) < 1.0E-6F) {
            return false;
        }
        float t = (planeZ - nearZ) / dz;
        out[0] = nearX + (farX - nearX) * t;
        out[1] = nearY + (farY - nearY) * t;
        out[2] = planeZ;
        return true;
    }

    public static float probeNdcX() {
        return probeNdcX;
    }

    public static float probeNdcY() {
        return probeNdcY;
    }

    public static float cursorNdcX() {
        Minecraft mc = Minecraft.getInstance();
        int width = Math.max(1, mc.getWindow().getScreenWidth());
        return (float) (mc.mouseHandler.xpos() / width * 2.0 - 1.0);
    }

    public static float cursorNdcY() {
        Minecraft mc = Minecraft.getInstance();
        int height = Math.max(1, mc.getWindow().getScreenHeight());
        return (float) (1.0 - mc.mouseHandler.ypos() / height * 2.0);
    }

    public static boolean pivotLocal(InteractionHand hand, float[] out) {
        if (hand == InteractionHand.MAIN_HAND) {
            if (!mainPivotValid) {
                return false;
            }
            out[0] = mainLocalX;
            out[1] = mainLocalY;
            return true;
        }
        if (!offPivotValid) {
            return false;
        }
        out[0] = offLocalX;
        out[1] = offLocalY;
        return true;
    }

    public static boolean isPivotValid(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? mainPivotValid : offPivotValid;
    }

    public static float pivotX(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? mainPivotX : offPivotX;
    }

    public static float pivotY(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? mainPivotY : offPivotY;
    }

    public static InteractionHand pickHand(float guiX, float guiY) {
        if (mainPivotValid && offPivotValid) {
            return dist2(guiX, guiY, mainPivotX, mainPivotY) <= dist2(guiX, guiY, offPivotX, offPivotY)
                    ? InteractionHand.MAIN_HAND
                    : InteractionHand.OFF_HAND;
        }
        if (mainPivotValid) {
            return InteractionHand.MAIN_HAND;
        }
        if (offPivotValid) {
            return InteractionHand.OFF_HAND;
        }

        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        boolean rightHalf = guiX >= window.getWidth() / (float) window.getGuiScale() * 0.5F;
        LocalPlayer player = mc.player;
        HumanoidArm mainArm = player != null ? player.getMainArm() : mc.options.mainHand().get();
        return rightHalf == (mainArm == HumanoidArm.RIGHT) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    private static float dist2(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return dx * dx + dy * dy;
    }
}
