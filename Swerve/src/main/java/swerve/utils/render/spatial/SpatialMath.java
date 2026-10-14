package swerve.utils.render.spatial;

import com.mojang.blaze3d.platform.Window;

import swerve.mixins.accessor.GameRendererAccessor;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

import org.joml.Vector3f;

/** Математика панели: интерполяции и пересечение луча курсора с плоским/изогнутым экраном. Без аллокаций. */
public final class SpatialMath {

    private static final float DEG_TO_RAD = (float) (Math.PI / 180.0);

    /** Направление луча камеры после cameraRay(). */
    public static float rayX;
    public static float rayY;
    public static float rayZ;

    /** Точка попадания в пикселях кадрового буфера после hitQuad()/hitCylinder(). */
    public static double hitX;
    public static double hitY;

    private SpatialMath() {
    }

    public static float rad(float degrees) {
        return degrees * DEG_TO_RAD;
    }

    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }

    public static double lerp(double start, double end, float t) {
        return start + (end - start) * t;
    }

    public static float rotLerp(float start, float end, float t) {
        float diff = ((end - start) % 360.0F + 540.0F) % 360.0F - 180.0F;
        return start + diff * t;
    }

    public static float easeInOutSine(float t) {
        return (float) (-(Math.cos(Math.PI * t) - 1.0) / 2.0);
    }

    // Луч из камеры через пиксель окна: базис камеры + tan(fov/2), как в ванильной перспективе.
    public static void cameraRay(double screenX, double screenY) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        int screenWidth = window.getScreenWidth();
        int screenHeight = window.getScreenHeight();
        double ndcX = screenX / (double) screenWidth * 2.0 - 1.0;
        double ndcY = 1.0 - screenY / (double) screenHeight * 2.0;

        Camera camera = mc.gameRenderer.getMainCamera();
        float yaw = rad(camera.getYRot());
        float pitch = rad(camera.getXRot());

        float fx = (float) (-Math.sin(yaw) * Math.cos(pitch));
        float fy = (float) -Math.sin(pitch);
        float fz = (float) (Math.cos(yaw) * Math.cos(pitch));
        float inv = 1.0F / (float) Math.sqrt(fx * fx + fy * fy + fz * fz);
        fx *= inv;
        fy *= inv;
        fz *= inv;

        float rx = -fz;
        float rz = fx;
        inv = 1.0F / (float) Math.sqrt(rx * rx + rz * rz);
        rx *= inv;
        rz *= inv;

        float ux = -rz * fy;
        float uy = rz * fx - rx * fz;
        float uz = rx * fy;
        inv = 1.0F / (float) Math.sqrt(ux * ux + uy * uy + uz * uz);
        ux *= inv;
        uy *= inv;
        uz *= inv;

        float fov = ((GameRendererAccessor) mc.gameRenderer).swerve$getFov(camera, camera.getPartialTickTime(), true);
        float tanHalfY = (float) Math.tan(Math.toRadians(fov / 2.0));
        float tanHalfX = tanHalfY * (float) screenWidth / (float) screenHeight;
        float xCoeff = (float) ndcX * tanHalfX;
        float yCoeff = (float) ndcY * tanHalfY;

        float dx = fx + rx * xCoeff + ux * yCoeff;
        float dy = fy + uy * yCoeff;
        float dz = fz + rz * xCoeff + uz * yCoeff;
        inv = 1.0F / (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        rayX = dx * inv;
        rayY = dy * inv;
        rayZ = dz * inv;
    }

    public static boolean hitQuad(double screenX, double screenY, Vector3f center, Vector3f right, Vector3f up,
                                  Vector3f normal, float halfWidth, float halfHeight) {
        if (halfWidth <= 0.0F || halfHeight <= 0.0F) {
            return false;
        }
        cameraRay(screenX, screenY);
        float dx = rayX;
        float dy = rayY;
        float dz = rayZ;

        float denom = dx * normal.x + dy * normal.y + dz * normal.z;
        if (Math.abs(denom) < 1.0e-6F) {
            return false;
        }
        float t = (center.x * normal.x + center.y * normal.y + center.z * normal.z) / denom;
        if (t <= 0.0F || Float.isNaN(t) || Float.isInfinite(t)) {
            return false;
        }
        float hx = dx * t - center.x;
        float hy = dy * t - center.y;
        float hz = dz * t - center.z;
        float localX = hx * right.x + hy * right.y + hz * right.z;
        float localY = hx * up.x + hy * up.y + hz * up.z;
        float u = (localX / halfWidth + 1.0F) / 2.0F;
        float v = (localY / halfHeight + 1.0F) / 2.0F;
        if (Float.isNaN(u) || Float.isNaN(v)) {
            return false;
        }
        storeHit(u, v);
        return true;
    }

    public static boolean hitCylinder(double screenX, double screenY, Vector3f axisPos, Vector3f axisDir, Vector3f refDir,
                                      Vector3f center, float radius, float arcRadians, float halfHeight) {
        cameraRay(screenX, screenY);
        float dx = rayX;
        float dy = rayY;
        float dz = rayZ;

        float ox = -axisPos.x;
        float oy = -axisPos.y;
        float oz = -axisPos.z;
        float dParallel = dx * axisDir.x + dy * axisDir.y + dz * axisDir.z;
        float oParallel = ox * axisDir.x + oy * axisDir.y + oz * axisDir.z;
        float dpx = dx - axisDir.x * dParallel;
        float dpy = dy - axisDir.y * dParallel;
        float dpz = dz - axisDir.z * dParallel;
        float opx = ox - axisDir.x * oParallel;
        float opy = oy - axisDir.y * oParallel;
        float opz = oz - axisDir.z * oParallel;

        float a = dpx * dpx + dpy * dpy + dpz * dpz;
        if (a < 1.0e-10F) {
            return false;
        }
        float b = 2.0F * (opx * dpx + opy * dpy + opz * dpz);
        float c = opx * opx + opy * opy + opz * opz - radius * radius;
        float discriminant = b * b - 4.0F * a * c;
        if (discriminant < 0.0F) {
            return false;
        }
        float sqrtDisc = (float) Math.sqrt(discriminant);
        float t = (-b - sqrtDisc) / (2.0F * a);
        if (t <= 0.0F) {
            t = (-b + sqrtDisc) / (2.0F * a);
        }
        if (t <= 0.0F || Float.isNaN(t) || Float.isInfinite(t)) {
            return false;
        }

        float hx = dx * t;
        float hy = dy * t;
        float hz = dz * t;
        float rx = hx - axisPos.x;
        float ry = hy - axisPos.y;
        float rz = hz - axisPos.z;
        float rParallel = rx * axisDir.x + ry * axisDir.y + rz * axisDir.z;
        rx -= axisDir.x * rParallel;
        ry -= axisDir.y * rParallel;
        rz -= axisDir.z * rParallel;
        float rLength = (float) Math.sqrt(rx * rx + ry * ry + rz * rz);
        if (rLength < 1.0e-6F) {
            return false;
        }
        rx /= rLength;
        ry /= rLength;
        rz /= rLength;

        float cosPhi = refDir.x * rx + refDir.y * ry + refDir.z * rz;
        float sinPhi = (axisDir.y * rz - axisDir.z * ry) * refDir.x
                + (axisDir.z * rx - axisDir.x * rz) * refDir.y
                + (axisDir.x * ry - axisDir.y * rx) * refDir.z;
        float phi = (float) Math.atan2(sinPhi, cosPhi);
        float u = 0.5F + phi / arcRadians;
        float vertical = (hx - center.x) * axisDir.x + (hy - center.y) * axisDir.y + (hz - center.z) * axisDir.z;
        float v = 0.5F + vertical / (2.0F * halfHeight);
        if (Float.isNaN(u) || Float.isNaN(v)) {
            return false;
        }
        storeHit(u, v);
        return true;
    }

    private static void storeHit(float u, float v) {
        Window window = Minecraft.getInstance().getWindow();
        hitX = Mth.clamp(u, 0.0F, 1.0F) * window.getWidth();
        hitY = (1.0 - Mth.clamp(v, 0.0F, 1.0F)) * window.getHeight();
    }
}
