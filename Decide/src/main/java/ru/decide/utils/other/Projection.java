/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_2338
 *  net.minecraft.class_2374
 *  net.minecraft.class_238
 *  net.minecraft.class_241
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  net.minecraft.class_4604
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector4d
 *  org.joml.Vector4f
 */
package ru.decide.utils.other;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_238;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4604;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.render.RenderUtil;

@Environment(value=EnvType.CLIENT)
public final class Projection
implements IMinecraft {
    private static final double NEAR_PLANE = 0.05;

    public static class_243 worldSpaceToScreenSpace(class_243 pos) {
        class_4184 camera = Projection.mc.method_1561().field_4686;
        if (camera == null) {
            return class_243.field_1353;
        }
        int displayHeight = mc.method_22683().method_4506();
        int[] viewport = new int[]{0, 0, mc.method_22683().method_4489(), displayHeight};
        Vector3f target = new Vector3f();
        double deltaX = pos.field_1352 - camera.method_71156().field_1352;
        double deltaY = pos.field_1351 - camera.method_71156().field_1351;
        double deltaZ = pos.field_1350 - camera.method_71156().field_1350;
        Vector4f transformedCoordinates = new Vector4f((float)deltaX, (float)deltaY, (float)deltaZ, 1.0f);
        transformedCoordinates.mul((Matrix4fc)RenderUtil.Render3D.lastWorldSpaceMatrix);
        Matrix4f matrixProj = new Matrix4f((Matrix4fc)RenderUtil.Render3D.lastProjMat);
        Matrix4f matrixModel = new Matrix4f((Matrix4fc)RenderUtil.Render3D.lastModMat);
        matrixProj.mul((Matrix4fc)matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);
        return new class_243((double)target.x / 2.0, (double)((float)displayHeight - target.y) / 2.0, (double)target.z);
    }

    private static Matrix4d toMatrix4d(Matrix4f mat) {
        Matrix4d result = new Matrix4d();
        result.m00((double)mat.m00()).m01((double)mat.m01()).m02((double)mat.m02()).m03((double)mat.m03());
        result.m10((double)mat.m10()).m11((double)mat.m11()).m12((double)mat.m12()).m13((double)mat.m13());
        result.m20((double)mat.m20()).m21((double)mat.m21()).m22((double)mat.m22()).m23((double)mat.m23());
        result.m30((double)mat.m30()).m31((double)mat.m31()).m32((double)mat.m32()).m33((double)mat.m33());
        return result;
    }

    private static double getViewZ(class_243 pos, class_243 cameraPos) {
        double deltaX = pos.field_1352 - cameraPos.field_1352;
        double deltaY = pos.field_1351 - cameraPos.field_1351;
        double deltaZ = pos.field_1350 - cameraPos.field_1350;
        Matrix4d worldSpace = Projection.toMatrix4d(RenderUtil.Render3D.lastWorldSpaceMatrix);
        Vector4d view = new Vector4d(deltaX, deltaY, deltaZ, 1.0);
        worldSpace.transform(view);
        return -view.z;
    }

    private static ClipResult worldSpaceToClipSpaceDouble(class_243 pos, class_243 cameraPos) {
        double deltaX = pos.field_1352 - cameraPos.field_1352;
        double deltaY = pos.field_1351 - cameraPos.field_1351;
        double deltaZ = pos.field_1350 - cameraPos.field_1350;
        Matrix4d worldSpace = Projection.toMatrix4d(RenderUtil.Render3D.lastWorldSpaceMatrix);
        Vector4d view = new Vector4d(deltaX, deltaY, deltaZ, 1.0);
        worldSpace.transform(view);
        Matrix4d proj = Projection.toMatrix4d(RenderUtil.Render3D.lastProjMat);
        Matrix4d model = Projection.toMatrix4d(RenderUtil.Render3D.lastModMat);
        Matrix4d combined = new Matrix4d((Matrix4dc)proj).mul((Matrix4dc)model);
        double clipX = combined.m00() * view.x + combined.m10() * view.y + combined.m20() * view.z + combined.m30() * view.w;
        double clipY = combined.m01() * view.x + combined.m11() * view.y + combined.m21() * view.z + combined.m31() * view.w;
        double clipZ = combined.m02() * view.x + combined.m12() * view.y + combined.m22() * view.z + combined.m32() * view.w;
        double clipW = combined.m03() * view.x + combined.m13() * view.y + combined.m23() * view.z + combined.m33() * view.w;
        return new ClipResult(clipX, clipY, clipZ, clipW, -view.z);
    }

    private static class_243 clipToScreenDouble(ClipResult clip, int[] viewport, int displayHeight, double scale) {
        double w = clip.w;
        if (Math.abs(w) < 1.0E-14) {
            return new class_243((double)viewport[2] / scale / 2.0, (double)displayHeight / scale / 2.0, 0.0);
        }
        double invW = 1.0 / w;
        double ndcX = clip.x * invW;
        double ndcY = clip.y * invW;
        double ndcZ = clip.z * invW;
        double screenX = (ndcX * 0.5 + 0.5) * (double)viewport[2] / scale;
        double screenY = ((double)displayHeight - (ndcY * 0.5 + 0.5) * (double)viewport[3]) / scale;
        double screenZ = ndcZ * 0.5 + 0.5;
        return new class_243(screenX, screenY, w > 0.0 ? screenZ : -1.0);
    }

    public static double getDistanceToGround() {
        if (Projection.mc.field_1724 == null || Projection.mc.field_1687 == null) {
            return 256.0;
        }
        for (double y = Projection.mc.field_1724.method_23318(); y > 0.0; y -= 0.1) {
            if (Projection.mc.field_1687.method_8320(Projection.mc.field_1724.method_24515().method_10087((int)(Projection.mc.field_1724.method_23318() - y + 1.0))).method_26215()) continue;
            return Projection.mc.field_1724.method_23318() - y;
        }
        return 256.0;
    }

    public static class_243 interpolate(class_1297 entity) {
        float tickDelta = mc.method_61966().method_60637(true);
        return entity.method_30950(tickDelta);
    }

    public static class_243 interpolate(class_1297 entity, float tickDelta) {
        return entity.method_30950(tickDelta);
    }

    public static Vector4d getVector4D(class_1297 ent) {
        return Projection.getVector4D(ent, mc.method_61966().method_60637(true));
    }

    public static Vector4d getVector4D(class_1297 ent, float tickDelta) {
        if (ent == null) {
            return null;
        }
        class_4184 camera = Projection.mc.method_1561().field_4686;
        if (camera == null) {
            return null;
        }
        class_243 cameraPos = camera.method_71156();
        class_243 interp = ent.method_30950(tickDelta);
        class_243 entityPos = ent.method_73189();
        class_238 box = ent.method_5829().method_997(interp.method_1020(entityPos));
        class_243 boxCenter = box.method_1005();
        double centerViewZ = Projection.getViewZ(boxCenter, cameraPos);
        if (centerViewZ < -5.0) {
            return null;
        }
        int displayHeight = mc.method_22683().method_4506();
        int[] viewport = new int[]{0, 0, mc.method_22683().method_4489(), displayHeight};
        double scale = 2.0;
        double screenW = (double)mc.method_22683().method_4489() / 2.0;
        double screenH = (double)mc.method_22683().method_4506() / 2.0;
        class_243[] corners = new class_243[]{new class_243(box.field_1323, box.field_1322, box.field_1321), new class_243(box.field_1323, box.field_1322, box.field_1324), new class_243(box.field_1320, box.field_1322, box.field_1321), new class_243(box.field_1320, box.field_1322, box.field_1324), new class_243(box.field_1323, box.field_1325, box.field_1321), new class_243(box.field_1323, box.field_1325, box.field_1324), new class_243(box.field_1320, box.field_1325, box.field_1321), new class_243(box.field_1320, box.field_1325, box.field_1324)};
        int[][] edges = new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}, {4, 5}, {4, 6}, {5, 7}, {6, 7}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
        ClipResult[] clipResults = new ClipResult[8];
        for (int i = 0; i < 8; ++i) {
            clipResults[i] = Projection.worldSpaceToClipSpaceDouble(corners[i], cameraPos);
            if (clipResults[i] != null) continue;
            return null;
        }
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -1.7976931348623157E308;
        double maxY = -1.7976931348623157E308;
        int visibleCount = 0;
        for (int i = 0; i < 8; ++i) {
            ClipResult clip = clipResults[i];
            if (!(clip.viewZ > 0.05)) continue;
            ++visibleCount;
            class_243 screen = Projection.clipToScreenDouble(clip, viewport, displayHeight, scale);
            double px = Projection.clampScreenX(screen.field_1352, screenW);
            double py = Projection.clampScreenY(screen.field_1351, screenH);
            minX = Math.min(minX, px);
            minY = Math.min(minY, py);
            maxX = Math.max(maxX, px);
            maxY = Math.max(maxY, py);
        }
        for (int[] edge : edges) {
            double denom;
            boolean v1;
            ClipResult c0 = clipResults[edge[0]];
            ClipResult c1 = clipResults[edge[1]];
            boolean v0 = c0.viewZ > 0.05;
            boolean bl = v1 = c1.viewZ > 0.05;
            if (v0 == v1 || Math.abs(denom = c1.viewZ - c0.viewZ) < 1.0E-14) continue;
            double t = (0.05 - c0.viewZ) / denom;
            t = Math.max(0.0, Math.min(1.0, t));
            ClipResult clipped = new ClipResult(c0.x + t * (c1.x - c0.x), c0.y + t * (c1.y - c0.y), c0.z + t * (c1.z - c0.z), c0.w + t * (c1.w - c0.w), 0.05);
            class_243 screen = Projection.clipToScreenDouble(clipped, viewport, displayHeight, scale);
            double px = Projection.clampScreenX(screen.field_1352, screenW);
            double py = Projection.clampScreenY(screen.field_1351, screenH);
            minX = Math.min(minX, px);
            minY = Math.min(minY, py);
            maxX = Math.max(maxX, px);
            maxY = Math.max(maxY, py);
        }
        if (visibleCount == 0 && minX == Double.MAX_VALUE) {
            return null;
        }
        if (maxX <= minX || maxY <= minY) {
            return null;
        }
        minX = Math.max(-screenW, minX);
        minY = Math.max(-screenH, minY);
        maxX = Math.min(screenW * 2.0, maxX);
        maxY = Math.min(screenH * 2.0, maxY);
        return new Vector4d(minX, minY, maxX, maxY);
    }

    private static double clampScreenX(double x, double screenW) {
        return Math.max(-screenW * 2.0, Math.min(screenW * 3.0, x));
    }

    private static double clampScreenY(double y, double screenH) {
        return Math.max(-screenH * 2.0, Math.min(screenH * 3.0, y));
    }

    private static boolean isPointInFrontDouble(class_243 point, class_243 cameraPos, class_4184 camera) {
        double lookZ;
        double lookY;
        double toPointX = point.field_1352 - cameraPos.field_1352;
        double toPointY = point.field_1351 - cameraPos.field_1351;
        double toPointZ = point.field_1350 - cameraPos.field_1350;
        double yaw = camera.method_19330();
        double pitch = camera.method_19329();
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        double cosPitch = Math.cos(pitchRad);
        double lookX = -Math.sin(yawRad) * cosPitch;
        double dot = lookX * toPointX + (lookY = -Math.sin(pitchRad)) * toPointY + (lookZ = Math.cos(yawRad) * cosPitch) * toPointZ;
        return dot > -10.0;
    }

    public static boolean canSee(class_243 vec3d) {
        class_4184 camera = Projection.mc.field_1773.method_19418();
        if (camera == null) {
            return false;
        }
        class_241 angle = Projection.fromVec3d(vec3d.method_1020(camera.method_71156()));
        return Math.abs(class_3532.method_15393((float)(angle.field_1343 - camera.method_19330()))) < 90.0f && Math.abs(class_3532.method_15393((float)(angle.field_1342 - camera.method_19329()))) < 60.0f || Projection.canSee(new class_238(class_2338.method_49638((class_2374)vec3d)));
    }

    public static class_241 fromVec3d(class_243 vector) {
        return new class_241((float)class_3532.method_15338((double)(Math.toDegrees(Math.atan2(vector.field_1350, vector.field_1352)) - 90.0)), (float)class_3532.method_15338((double)Math.toDegrees(-Math.atan2(vector.field_1351, Math.hypot(vector.field_1352, vector.field_1350)))));
    }

    public static boolean canSee(class_238 box) {
        if (box == null || Projection.mc.field_1773 == null) {
            return false;
        }
        class_4184 camera = Projection.mc.field_1773.method_19418();
        if (camera == null || !camera.method_19332()) {
            return false;
        }
        class_243 cameraPos = camera.method_71156();
        Matrix4f viewMatrix = new Matrix4f().rotation((Quaternionfc)camera.method_23767());
        Matrix4f projectionMatrix = Projection.mc.field_1773.method_22973(((Integer)Projection.mc.field_1690.method_41808().method_41753()).floatValue());
        class_4604 frustum = new class_4604(viewMatrix, projectionMatrix);
        frustum.method_23088(cameraPos.field_1352, cameraPos.field_1351, cameraPos.field_1350);
        return frustum.method_23093(box);
    }

    public static boolean cantSee(Vector4d vec) {
        if (vec == null) {
            return true;
        }
        double screenWidth = (double)mc.method_22683().method_4489() / 2.0;
        double screenHeight = (double)mc.method_22683().method_4506() / 2.0;
        if (Double.isNaN(vec.x) || Double.isNaN(vec.y) || Double.isNaN(vec.z) || Double.isNaN(vec.w)) {
            return true;
        }
        if (Double.isInfinite(vec.x) || Double.isInfinite(vec.y) || Double.isInfinite(vec.z) || Double.isInfinite(vec.w)) {
            return true;
        }
        if (vec.z < -screenWidth || vec.x > screenWidth * 2.0) {
            return true;
        }
        return vec.w < -screenHeight || vec.y > screenHeight * 2.0;
    }

    public static double centerX(Vector4d vec) {
        return vec.x + (vec.z - vec.x) / 2.0;
    }

    public static boolean isInFrontOfCamera(class_243 worldPos) {
        class_4184 camera = Projection.mc.field_1773.method_19418();
        if (camera == null || !camera.method_19332()) {
            return false;
        }
        return Projection.isPointInFrontDouble(worldPos, camera.method_71156(), camera);
    }

    @Generated
    private Projection() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @Environment(value=EnvType.CLIENT)
    private record ClipResult(double x, double y, double z, double w, double viewZ) {
    }
}

