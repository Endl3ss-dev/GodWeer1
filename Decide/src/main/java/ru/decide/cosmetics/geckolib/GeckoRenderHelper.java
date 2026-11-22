/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4587
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package ru.decide.cosmetics.geckolib;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import ru.decide.cosmetics.geo.GeoBone;
import ru.decide.cosmetics.geo.GeoCube;

@Environment(value=EnvType.CLIENT)
public class GeckoRenderHelper {
    private static final Vector3f POSITIVE_X = new Vector3f(1.0f, 0.0f, 0.0f);
    private static final Vector3f POSITIVE_Y = new Vector3f(0.0f, 1.0f, 0.0f);
    private static final Vector3f POSITIVE_Z = new Vector3f(0.0f, 0.0f, 1.0f);

    public static void translate(GeoBone bone, class_4587 matrices) {
        matrices.method_46416(-bone.getPositionX() / 16.0f, bone.getPositionY() / 16.0f, bone.getPositionZ() / 16.0f);
    }

    public static void moveToPivot(GeoBone bone, class_4587 matrices) {
        matrices.method_46416(bone.getPivotX() / 16.0f, bone.getPivotY() / 16.0f, bone.getPivotZ() / 16.0f);
    }

    public static void moveBackFromPivot(GeoBone bone, class_4587 matrices) {
        matrices.method_46416(-bone.getPivotX() / 16.0f, -bone.getPivotY() / 16.0f, -bone.getPivotZ() / 16.0f);
    }

    public static void rotate(GeoBone bone, class_4587 matrices) {
        if (bone.getRotationZ() != 0.0f) {
            matrices.method_22907((Quaternionfc)GeckoRenderHelper.getRadialQuaternion(POSITIVE_Z, bone.getRotationZ()));
        }
        if (bone.getRotationY() != 0.0f) {
            matrices.method_22907((Quaternionfc)GeckoRenderHelper.getRadialQuaternion(POSITIVE_Y, bone.getRotationY()));
        }
        if (bone.getRotationX() != 0.0f) {
            matrices.method_22907((Quaternionfc)GeckoRenderHelper.getRadialQuaternion(POSITIVE_X, bone.getRotationX()));
        }
    }

    public static void scale(GeoBone bone, class_4587 matrices) {
        matrices.method_22905(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    public static void moveToPivot(GeoCube cube, class_4587 matrices) {
        matrices.method_46416(cube.pivot.getX() / 16.0f, cube.pivot.getY() / 16.0f, cube.pivot.getZ() / 16.0f);
    }

    public static void moveBackFromPivot(GeoCube cube, class_4587 matrices) {
        matrices.method_46416(-cube.pivot.getX() / 16.0f, -cube.pivot.getY() / 16.0f, -cube.pivot.getZ() / 16.0f);
    }

    public static void rotate(GeoCube cube, class_4587 matrices) {
        if (cube.rotation.getZ() != 0.0f) {
            matrices.method_22907((Quaternionfc)GeckoRenderHelper.getRadialQuaternion(POSITIVE_Z, cube.rotation.getZ()));
        }
        if (cube.rotation.getY() != 0.0f) {
            matrices.method_22907((Quaternionfc)GeckoRenderHelper.getRadialQuaternion(POSITIVE_Y, cube.rotation.getY()));
        }
        if (cube.rotation.getX() != 0.0f) {
            matrices.method_22907((Quaternionfc)GeckoRenderHelper.getRadialQuaternion(POSITIVE_X, cube.rotation.getX()));
        }
    }

    public static Quaternionf getRadialQuaternion(Vector3f axis, float angle) {
        float sin = (float)Math.sin(angle / 2.0f);
        float x = axis.x() * sin;
        float y = axis.y() * sin;
        float z = axis.z() * sin;
        float cos = (float)Math.cos(angle / 2.0f);
        return new Quaternionf(x, y, z, cos);
    }
}

