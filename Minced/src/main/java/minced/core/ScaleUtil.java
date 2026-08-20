/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.ProjectionType
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.util.Window
 *  net.minecraft.util.math.Vec2f
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 */
package minced.core;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import minced.core.ScaleState;
import minced.core.ClientUtil;
import minced.core.WindowScaleUtil;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vec2f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;

public class ScaleUtil {
    static float field10064 = 1000.0f;
    static float field10065 = 3000.0f;
    static float field10066 = -2000.0f;

    public static ScaleState method2885(float param0) {
        if (param0 <= 0.0f || ClientUtil.I == null || ClientUtil.I.getWindow() == null) {
            return null;
        }
        Window v1 = ClientUtil.I.getWindow();
        double v2 = v1.getScaleFactor();
        Matrix4f v4 = new Matrix4f((Matrix4fc)RenderSystem.getProjectionMatrix());
        Matrix4fStack v5 = RenderSystem.getModelViewStack();
        Matrix4f v6 = new Matrix4f((Matrix4fc)v5);
        v1.setScaleFactor((double)param0);
        float v7 = (float)((double)v1.getWidth() / (double)param0);
        float v8 = (float)((double)v1.getHeight() / (double)param0);
        Matrix4f v9 = new Matrix4f().setOrtho(0.0f, v7, v8, 0.0f, field10064, field10065);
        RenderSystem.setProjectionMatrix((Matrix4f)v9, (ProjectionType)ProjectionType.ORTHOGRAPHIC);
        v5.identity();
        v5.translation(0.0f, 0.0f, field10066);
        return new ScaleState(v2, v4, v6);
    }

    public static void method2886(ScaleState param0) {
        if (param0 == null || ClientUtil.I == null || ClientUtil.I.getWindow() == null) {
            return;
        }
        Window v1 = ClientUtil.I.getWindow();
        Matrix4fStack v2 = RenderSystem.getModelViewStack();
        v1.setScaleFactor(param0.field7989);
        RenderSystem.setProjectionMatrix((Matrix4f)param0.field7990, (ProjectionType)RenderSystem.getProjectionType());
        v2.identity();
        v2.mul((Matrix4fc)param0.field7991);
    }

    public static Vec2f method2887(int param0, int param1) {
        return WindowScaleUtil.method2888(param0, param1);
    }
}



