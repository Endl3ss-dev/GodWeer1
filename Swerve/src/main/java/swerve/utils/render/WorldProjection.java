package swerve.utils.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;

// Мир -> экран для HUD-слоя. Матрицы кадра снимаются один раз за кадр в мировом рендере
// (WorldRenderEvents.AFTER_ENTITIES), а в HUD остаётся только умножение вектора — поэтому
// элемент можно привязать к точке мира и он будет следовать за целью без лишних аллокаций.
//
// Про сдвиг камеры: позиционная матрица в контексте — это матрица, переданная рендереру мира.
// В одних сборках она уже содержит сдвиг на -cameraPos (тогда координаты подаются абсолютные),
// в других — только поворот камеры (тогда координаты подаются камера-относительные).
// Направление определяет одна проверка длины вектора переноса матрицы, поэтому обе схемы
// работают без угадывания: у чистой матрицы поворота перенос нулевой.
public final class WorldProjection {

    private static final Matrix4f VIEW_PROJECTION = new Matrix4f();
    private static final Matrix4f PROJECTION = new Matrix4f();
    private static final float TRANSLATION_EPSILON = 0.5f;
    private static final float MIN_CLIP_W = 1.0E-6f;
    private static final float OFF_SCREEN_LIMIT = 4.0f;

    private static float cameraX;
    private static float cameraY;
    private static float cameraZ;
    private static boolean cameraRelative;
    private static boolean ready;
    private static boolean registered;

    private WorldProjection() {
    }

    public static void init() {
        if (registered) {
            return;
        }
        registered = true;
        WorldRenderEvents.AFTER_ENTITIES.register(WorldProjection::capture);
    }

    private static void capture(WorldRenderContext context) {
        Matrix4f position = context.positionMatrix();
        Matrix4f projection = context.projectionMatrix();
        if (position == null || projection == null) {
            ready = false;
            return;
        }

        Vec3 camera = context.camera().getPosition();
        cameraX = (float) camera.x;
        cameraY = (float) camera.y;
        cameraZ = (float) camera.z;

        float translation = Math.abs(position.m30()) + Math.abs(position.m31()) + Math.abs(position.m32());
        cameraRelative = translation < TRANSLATION_EPSILON;

        VIEW_PROJECTION.set(projection).mul(position);
        // [+] Чистая перспектива кадра — нужна SpatialGui: панель строится в пространстве камеры
        // и должна проецироваться тем же fov/near/far, что и мир.
        PROJECTION.set(projection);
        ready = true;
    }

    public static boolean isReady() {
        return ready;
    }

    // Копия перспективной матрицы кадра в dest. false — кадр ещё не снят (меню, загрузка).
    public static boolean copyProjection(Matrix4f dest) {
        if (!ready || dest == null) {
            return false;
        }
        dest.set(PROJECTION);
        return true;
    }

    // out[0]/out[1] — координаты точки в GUI-пикселях. false — точка позади камеры,
    // далеко за пределами экрана или матрицы кадра ещё не сняты.
    public static boolean project(double x, double y, double z, float[] out) {
        if (!ready || out == null || out.length < 2) {
            return false;
        }

        float pointX = cameraRelative ? (float) (x - cameraX) : (float) x;
        float pointY = cameraRelative ? (float) (y - cameraY) : (float) y;
        float pointZ = cameraRelative ? (float) (z - cameraZ) : (float) z;

        float clipX = VIEW_PROJECTION.m00() * pointX + VIEW_PROJECTION.m10() * pointY
                + VIEW_PROJECTION.m20() * pointZ + VIEW_PROJECTION.m30();
        float clipY = VIEW_PROJECTION.m01() * pointX + VIEW_PROJECTION.m11() * pointY
                + VIEW_PROJECTION.m21() * pointZ + VIEW_PROJECTION.m31();
        float clipW = VIEW_PROJECTION.m03() * pointX + VIEW_PROJECTION.m13() * pointY
                + VIEW_PROJECTION.m23() * pointZ + VIEW_PROJECTION.m33();
        if (clipW <= MIN_CLIP_W) {
            return false;
        }

        float normalizedX = clipX / clipW;
        float normalizedY = clipY / clipW;
        if (normalizedX < -OFF_SCREEN_LIMIT || normalizedX > OFF_SCREEN_LIMIT
                || normalizedY < -OFF_SCREEN_LIMIT || normalizedY > OFF_SCREEN_LIMIT) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        out[0] = (normalizedX * 0.5f + 0.5f) * minecraft.getWindow().getGuiScaledWidth();
        out[1] = (1.0f - (normalizedY * 0.5f + 0.5f)) * minecraft.getWindow().getGuiScaledHeight();
        return true;
    }
}
