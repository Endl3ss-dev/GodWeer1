/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  org.lwjgl.opengl.GL11
 */
package ru.decide.utils.render;

import java.util.ArrayDeque;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.lwjgl.opengl.GL11;
import ru.decide.utils.render.DrawBatcher;

@Environment(value=EnvType.CLIENT)
public class Scissor {
    private static final class_310 mc = class_310.method_1551();
    private static final Deque<int[]> scissorStack = new ArrayDeque<int[]>();

    public static void enable(float x, float y, float width, float height, float guiScale) {
        DrawBatcher.flushPending();
        int windowHeight = mc.method_22683().method_4507();
        int scissorX = (int)(x * guiScale);
        int scissorY = (int)((float)windowHeight - (y + height) * guiScale);
        int scissorWidth = (int)(width * guiScale);
        int scissorHeight = (int)(height * guiScale);
        if (!scissorStack.isEmpty()) {
            int[] parent = scissorStack.peek();
            int parentX = parent[0];
            int parentY = parent[1];
            int parentX2 = parentX + parent[2];
            int parentY2 = parentY + parent[3];
            int newX2 = scissorX + scissorWidth;
            int newY2 = scissorY + scissorHeight;
            scissorX = Math.max(scissorX, parentX);
            scissorY = Math.max(scissorY, parentY);
            newX2 = Math.min(newX2, parentX2);
            newY2 = Math.min(newY2, parentY2);
            scissorWidth = Math.max(0, newX2 - scissorX);
            scissorHeight = Math.max(0, newY2 - scissorY);
        }
        scissorStack.push(new int[]{scissorX, scissorY, scissorWidth, scissorHeight});
        GL11.glEnable((int)3089);
        GL11.glScissor((int)scissorX, (int)scissorY, (int)scissorWidth, (int)scissorHeight);
    }

    public static void enable(float x, float y, float width, float height) {
        int currentGuiScale = mc.method_22683().method_4476(((Integer)Scissor.mc.field_1690.method_42474().method_41753()).intValue(), mc.method_1573());
        Scissor.enable(x, y, width, height, currentGuiScale);
    }

    public static void disable() {
        DrawBatcher.flushPending();
        if (!scissorStack.isEmpty()) {
            scissorStack.pop();
        }
        if (scissorStack.isEmpty()) {
            GL11.glDisable((int)3089);
        } else {
            int[] parent = scissorStack.peek();
            GL11.glScissor((int)parent[0], (int)parent[1], (int)parent[2], (int)parent[3]);
        }
    }

    public static void reset() {
        DrawBatcher.flushPending();
        scissorStack.clear();
        GL11.glDisable((int)3089);
    }
}

