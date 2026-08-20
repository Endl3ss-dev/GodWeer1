/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  org.lwjgl.opengl.GL11C
 *  org.lwjgl.system.MemoryStack
 */
package minced.core;

import java.awt.Rectangle;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import minced.core.ScissorState;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.MemoryStack;

public class ScissorUtil
implements Predicate {
    static List field9137;
    static List field9138;
    static Rectangle field9139;

    public static void method2620() {
        ScissorState v0;
        ScissorState clsClass472 = v0 = field9137.isEmpty() ? null : (ScissorState)field9137.remove(field9137.size() - 1);
        if (v0 == null) {
            GL11C.glDisable((int)3089);
        } else if (v0.field9294) {
            GL11C.glEnable((int)3089);
            int[] v1 = v0.field9295;
            if (v1 != null) {
                GL11C.glScissor((int)v1[0], (int)v1[1], (int)v1[2], (int)v1[3]);
            }
        } else {
            GL11C.glDisable((int)3089);
        }
        if (!field9138.isEmpty()) {
            field9138.remove(field9138.size() - 1);
            field9139 = field9138.isEmpty() ? null : (Rectangle)field9138.get(field9138.size() - 1);
        } else {
            field9139 = null;
        }
    }

    public static void method2621(double param0, double param2, double param4, double param6) {
        IntBuffer v11;
        MemoryStack v10;
        boolean v8 = GL11C.glIsEnabled((int)3089);
        int[] v9 = null;
        if (v8) {
            v10 = MemoryStack.stackPush();
            v11 = v10.mallocInt(4);
            GL11C.glGetIntegerv((int)3088, (IntBuffer)v11);
            v9 = new int[]{v11.get(0), v11.get(1), v11.get(2), v11.get(3)};
            if (v10 != null) {
                v10.close();
            }
        }
        field9137.add(new ScissorState(v8, v9));
        field9138.add(field9139);
        MinecraftClient v13 = MinecraftClient.getInstance();
        Object object = v13 != null ? v13.getWindow() : null;
        if (object == null) {
            return;
        }
        net.minecraft.client.util.Window v11b = (net.minecraft.client.util.Window)object;
        double v12 = v11b.getScaleFactor();
        int v14 = (int)(param0 * v12);
        int v15 = (int)(param2 * v12);
        int v16 = (int)(param4 * v12);
        int v17 = (int)(param6 * v12);
        int v18 = v11b.getFramebufferWidth();
        int v19 = v11b.getFramebufferHeight();
        int v20 = v19 - v15 - v17;
        Rectangle v21 = new Rectangle(v14, v20, Math.max(0, v16), Math.max(0, v17));
        Rectangle v22 = new Rectangle(0, 0, v18, v19);
        Rectangle v23 = field9139;
        if (v23 == null || v23.width <= 0 || v23.height <= 0) {
            v23 = v22;
        }
        Rectangle v24 = ScissorUtil.method2623(v21, v23);
        v24 = ScissorUtil.method2623(v24, v22);
        if (v24.width < 0) {
            v24.width = 0;
        }
        if (v24.height < 0) {
            v24.height = 0;
        }
        GL11C.glEnable((int)3089);
        GL11C.glScissor((int)v24.x, (int)v24.y, (int)v24.width, (int)v24.height);
        if (!field9138.isEmpty()) {
            field9138.set(field9138.size() - 1, new Rectangle(v24));
        }
        field9139 = new Rectangle(v24);
    }

    public static void method2622() {
        field9138 = new ArrayList();
        field9139 = null;
        field9137 = new ArrayList();
    }

    private static Rectangle method2623(Rectangle param0, Rectangle param1) {
        if (param0 == null || param1 == null) {
            return new Rectangle(0, 0, 0, 0);
        }
        return param0.intersection(param1);
    }

    @Override
    public boolean test(Object param1) {
        return false;
    }

    static {
        ScissorUtil.method2622();
    }
}



