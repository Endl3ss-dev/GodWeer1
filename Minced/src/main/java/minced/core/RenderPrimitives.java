/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.math.MatrixStack
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package minced.core;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Function;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class RenderPrimitives
{
    static String field7953;
    static int[][] field7954;
    static float[] I;
    static Vector3f[] field7955;
    static float field7956;
    static float field7957;
    static float field7958;
    static float field7959;
    static float field7960;
    static float field7961;
    static float field7962;
    static float field7963;
    static float field7964;
    static float field7965;
    static float field7966;

    private static void method1983() {
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private RenderPrimitives() {
        throw new UnsupportedOperationException(field7953);
    }

    public static void method1984(MatrixStack param0, BufferBuilder param1, float param2, float param3, float param4, float param5, int param6) {
        param0.push();
        param0.translate(param2, param3, param4);
        param0.scale(param5, param5, param5);
        Matrix4f v7 = param0.peek().getPositionMatrix();
        for (int v8 = 0; v8 < field7954.length; ++v8) {
            int[] v9 = field7954[v8];
            float v10 = I[v8];
            Vector3f v11 = field7955[v9[0]];
            Vector3f v12 = field7955[v9[1]];
            Vector3f v13 = field7955[v9[2]];
            int v14 = RenderPrimitives.method1985(param6, v10);
            param1.vertex(v7, v11.x, v11.y, v11.z).color(v14);
            param1.vertex(v7, v12.x, v12.y, v12.z).color(v14);
            param1.vertex(v7, v13.x, v13.y, v13.z).color(v14);
        }
        param0.pop();
    }

    private static int method1985(int param0, float param1) {
        int v2 = param0 >> 24 & 0xFF;
        int v3 = (int)((float)(param0 >> 16 & 0xFF) * param1);
        int v4 = (int)((float)(param0 >> 8 & 0xFF) * param1);
        int v5 = (int)((float)(param0 & 0xFF) * param1);
        v3 = Math.min(255, Math.max(0, v3));
        v4 = Math.min(255, Math.max(0, v4));
        v5 = Math.min(255, Math.max(0, v5));
        return v2 << 24 | v3 << 16 | v4 << 8 | v5;
    }

    public static BufferBuilder method1986() {
        RenderPrimitives.method1983();
        return Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
    }

    public static void method1987() {
        field7955 = new Vector3f[]{new Vector3f(0.0f, field7956, 0.0f), new Vector3f(0.0f, field7957, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(field7958, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, 0.0f, field7959)};
        field7954 = new int[][]{{0, 2, 4}, {0, 4, 3}, {0, 3, 5}, {0, 5, 2}, {1, 4, 2}, {1, 3, 4}, {1, 5, 3}, {1, 2, 5}};
        I = new float[]{1.0f, field7960, field7961, field7962, field7963, field7964, field7965, field7966};
    }

    static {
        field7953 = "This is a utility class and cannot be instantiated";
        field7956 = 1.5f;
        field7957 = -1.5f;
        field7958 = -1.0f;
        field7959 = -1.0f;
        field7960 = 0.8f;
        field7961 = 0.6f;
        field7962 = 0.9f;
        field7963 = 0.7f;
        field7964 = 0.5f;
        field7965 = 0.4f;
        field7966 = 0.6f;
        RenderPrimitives.method1987();
    }
}



