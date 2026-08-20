package org.privates.client.utils.render.draw;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.api.interfaces.IRender;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.utils.math.Interpolator;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_SMOOTH;

@SuppressWarnings("SameParameterValue")
@UtilityClass
public class RenderUtil3D implements IRender, IMinecraft {
    public Vector3d interpolate(Entity entity, float partialTicks) {
        double posX = Interpolator.lerp(entity.lastTickPosX, entity.getPosX(), partialTicks);
        double posY = Interpolator.lerp(entity.lastTickPosY, entity.getPosY(), partialTicks);
        double posZ = Interpolator.lerp(entity.lastTickPosZ, entity.getPosZ(), partialTicks);
        return new Vector3d(posX, posY, posZ);
    }

    /**
     * Проецирует мировые координаты в экранные (scaled GUI координаты).
     * Возвращает float[]{screenX, screenY} или null если точка за камерой.
     */
    public static float[] worldToScreen(double worldX, double worldY, double worldZ) {
        Minecraft mc = Minecraft.getInstance();

        // Вектор относительно камеры
        Vector3d cam = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
        float rx = (float) (worldX - cam.x);
        float ry = (float) (worldY - cam.y);
        float rz = (float) (worldZ - cam.z);

        // Читаем матрицы из OpenGL (column-major)
        FloatBuffer modelBuf = BufferUtils.createFloatBuffer(16);
        FloatBuffer projBuf = BufferUtils.createFloatBuffer(16);
        // Фикс 1: viewport читаем в IntBuffer, не FloatBuffer
        java.nio.IntBuffer viewportBuf = BufferUtils.createIntBuffer(16);

        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelBuf);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projBuf);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewportBuf);

        int vpX = viewportBuf.get(0);
        int vpY = viewportBuf.get(1);
        int vpW = viewportBuf.get(2);
        int vpH = viewportBuf.get(3);

        // Трансформируем вручную чтобы не зависеть от Matrix4f.set(float[])
        // modelView * vec
        float[] mv = readBuffer(modelBuf);
        float mvX = mv[0] * rx + mv[4] * ry + mv[8] * rz + mv[12];
        float mvY = mv[1] * rx + mv[5] * ry + mv[9] * rz + mv[13];
        float mvZ = mv[2] * rx + mv[6] * ry + mv[10] * rz + mv[14];
        float mvW = mv[3] * rx + mv[7] * ry + mv[11] * rz + mv[15];

        // projection * (mvX, mvY, mvZ, mvW)
        float[] pj = readBuffer(projBuf);
        float clipX = pj[0] * mvX + pj[4] * mvY + pj[8] * mvZ + pj[12] * mvW;
        float clipY = pj[1] * mvX + pj[5] * mvY + pj[9] * mvZ + pj[13] * mvW;
        float clipW = pj[3] * mvX + pj[7] * mvY + pj[11] * mvZ + pj[15] * mvW;

        // За камерой
        if (clipW <= 0.0f) return null;

        // NDC → экранные пиксели (физические)
        float ndcX = clipX / clipW;
        float ndcY = clipY / clipW;
        float physX = vpX + (ndcX + 1.0f) * vpW * 0.5f;
        float physY = vpY + (1.0f - ndcY) * vpH * 0.5f;

        // MainWindow.getScaleFactor() - это и есть GUI scale factor (1,2,3,4)
        float scale = (float) mc.getMainWindow().getScaleFactor();

        return new float[]{physX / scale, physY / scale};
    }

    /**
     * Читает 16 float из FloatBuffer (column-major OpenGL порядок).
     */
    private static float[] readBuffer(FloatBuffer buf) {
        buf.rewind();
        float[] arr = new float[16];
        for (int i = 0; i < 16; i++) arr[i] = buf.get(i);
        return arr;
    }

    public static void setup3dForBlockPos(Render3DPosedEvent event, Runnable render, boolean bloom, boolean disableDepth) {
        event.getMatrix().push();
        event.getMatrix().translate(-Minecraft.getInstance().getRenderManager().renderPosX(), -Minecraft.getInstance().getRenderManager().renderPosY(), -Minecraft.getInstance().getRenderManager().renderPosZ());
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, bloom ? GL11.GL_ONE : GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        if (disableDepth) GL11.glDisable(GL11.GL_DEPTH_TEST);
        else GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glShadeModel(GL_SMOOTH);
        render.run();
        GL11.glLineWidth(1.0f);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        if (disableDepth) GL11.glEnable(GL11.GL_DEPTH_TEST);
        if (bloom)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_BLEND);
        event.getMatrix().pop();
    }

    public static void drawCanisterBox(MatrixStack matrixIn, BufferBuilder buffer, Tessellator tessellator, AxisAlignedBB aabb, boolean outlineBox, boolean decussationBox, boolean fullBox, int outlineColor, int decussationColor, int fullColor) {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        if (outlineBox) {
            buffer.begin(2, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(outlineColor).endVertex();
            tessellator.draw();
        }
        if (decussationBox) {
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(decussationColor).endVertex();
            tessellator.draw();
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(decussationColor).endVertex();
            tessellator.draw();
        }
        if (outlineBox) {
            buffer.begin(2, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(outlineColor).endVertex();
            tessellator.draw();
        }
        if (decussationBox) {
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(decussationColor).endVertex();
            tessellator.draw();
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(decussationColor).endVertex();
            tessellator.draw();
        }
        if (outlineBox) {
            buffer.begin(2, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(outlineColor).endVertex();
            tessellator.draw();
        }
        if (decussationBox) {
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(decussationColor).endVertex();
            tessellator.draw();
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(decussationColor).endVertex();
            tessellator.draw();
        }
        if (outlineBox) {
            buffer.begin(2, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(outlineColor).endVertex();
            tessellator.draw();
        }
        if (decussationBox) {
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(decussationColor).endVertex();
            tessellator.draw();
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(decussationColor).endVertex();
            tessellator.draw();
        }
        if (outlineBox) {
            buffer.begin(2, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(outlineColor).endVertex();
            tessellator.draw();
        }
        if (decussationBox) {
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(decussationColor).endVertex();
            tessellator.draw();
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(decussationColor).endVertex();
            tessellator.draw();
        }
        if (outlineBox) {
            buffer.begin(2, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(outlineColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(outlineColor).endVertex();
            tessellator.draw();
        }
        if (decussationBox) {
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(decussationColor).endVertex();
            tessellator.draw();
            buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(decussationColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(decussationColor).endVertex();
            tessellator.draw();
        }
        if (fullBox) {
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            tessellator.draw();
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            tessellator.draw();
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            tessellator.draw();
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            tessellator.draw();
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            tessellator.draw();
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(fullColor).endVertex();
            buffer.pos(matrixIn.getLast().getMatrix(), (float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ).color(fullColor).endVertex();
            tessellator.draw();
        }
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(1.F);
    }

    public void renderWireframeBox(BufferBuilder bufferBuilder, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color) {
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;

        bufferBuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
    }

    public void renderFilledBox(BufferBuilder bufferBuilder, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color) {
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        float alpha = ((color >> 24) & 0xFF) / 255.0f;

        bufferBuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        bufferBuilder.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
    }

    public void renderWireframeBox(BufferBuilder buffer, MatrixStack stack, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a) {
        Matrix4f matrix = stack.getLast().getMatrix();
        buffer.pos(matrix, minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, maxY, maxZ).color(r, g, b, a).endVertex();
    }

    public void renderFilledBox(BufferBuilder buffer, MatrixStack stack, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a) {
        Matrix4f matrix = stack.getLast().getMatrix();
        buffer.pos(matrix, minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, maxY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, minY, minZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, minY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, maxY, maxZ).color(r, g, b, a).endVertex();
        buffer.pos(matrix, minX, maxY, minZ).color(r, g, b, a).endVertex();
    }

    public void drawFilledBoundingBox(MatrixStack matrix, AxisAlignedBB bb, int color, boolean depth, boolean cull) {
        if (!depth) RenderSystem.disableDepthTest();
        else RenderSystem.depthMask(false);
        if (!cull) RenderSystem.disableCull();
        drawQuadH(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ, color, true, true);
        drawQuadH(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, color, true, true);
        drawQuad(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ, color, true, true);
        drawQuad(matrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, color, true, true);
        drawQuad(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, color, true, true);
        drawQuad(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ, color, true, true);
        if (!cull) RenderSystem.enableCull();
        if (!depth) RenderSystem.enableDepthTest();
        else RenderSystem.depthMask(true);
    }

    public void drawBoundingBox(MatrixStack matrix, AxisAlignedBB bb, int color, float lineWidth, boolean depth, boolean cull) {
        if (!depth) RenderSystem.disableDepthTest();
        if (!cull) RenderSystem.disableCull();
        drawLineQuad(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ, color, lineWidth, true, true);
        drawLineQuad(matrix, (float) bb.minX, (float) bb.maxY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, color, lineWidth, true, true);
        drawLine(matrix, (float) bb.minX, (float) bb.minY, (float) bb.minZ, (float) bb.minX, (float) bb.maxY, (float) bb.minZ, color, lineWidth, true, true);
        drawLine(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.minZ, (float) bb.maxX, (float) bb.maxY, (float) bb.minZ, color, lineWidth, true, true);
        drawLine(matrix, (float) bb.maxX, (float) bb.minY, (float) bb.maxZ, (float) bb.maxX, (float) bb.maxY, (float) bb.maxZ, color, lineWidth, true, true);
        drawLine(matrix, (float) bb.minX, (float) bb.minY, (float) bb.maxZ, (float) bb.minX, (float) bb.maxY, (float) bb.maxZ, color, lineWidth, true, true);
        if (!cull) RenderSystem.enableCull();
        if (!depth) RenderSystem.enableDepthTest();
    }

    public AxisAlignedBB createBoxFromCorners(double x1, double y1, double z1, double x2, double y2, double z2) {
        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxX = Math.max(x1, x2);
        double maxY = Math.max(y1, y2);
        double maxZ = Math.max(z1, z2);
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public void drawCubeFromCorners(MatrixStack matrix, double x1, double y1, double z1, double x2, double y2, double z2,
                                    int fillColor, int outlineColor, float lineWidth, boolean depth, boolean cull, boolean drawFill) {
        AxisAlignedBB box = createBoxFromCorners(x1, y1, z1, x2, y2, z2);
        if (drawFill) {
            drawFilledBoundingBox(matrix, box, fillColor, depth, cull);
        }
        drawBoundingBox(matrix, box, outlineColor, lineWidth, depth, cull);
    }

    private void drawFace(Matrix4f matrix4f, float x1, float y1, float z1, float x2, float y2, float z2, float[] rgb) {
        BUFFER.begin(3, DefaultVertexFormats.POSITION);
        BUFFER.pos(matrix4f, x1, y1, z1).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        BUFFER.pos(matrix4f, x2, y1, z1).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        BUFFER.pos(matrix4f, x2, y1, z2).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        BUFFER.pos(matrix4f, x1, y1, z2).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        BUFFER.pos(matrix4f, x1, y1, z1).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        TESSELLATOR.draw();
    }

    private void drawEdge(Matrix4f matrix4f, float x1, float y1, float z1, float x2, float y2, float z2, float[] rgb) {
        BUFFER.begin(3, DefaultVertexFormats.POSITION);
        BUFFER.pos(matrix4f, x1, y1, z1).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        BUFFER.pos(matrix4f, x2, y2, z2).color(rgb[0], rgb[1], rgb[2], rgb[3]).endVertex();
        TESSELLATOR.draw();
    }

    public void drawQuad(MatrixStack matrix, float x1, float y1, float z1, float x2, float y2, float z2, int color, boolean depth, boolean cull) {
        float dx1 = (float) (x1 - cameraPos().x);
        float dy1 = (float) (y1 - cameraPos().y);
        float dz1 = (float) (z1 - cameraPos().z);
        float dx2 = (float) (x2 - cameraPos().x);
        float dy2 = (float) (y2 - cameraPos().y);
        float dz2 = (float) (z2 - cameraPos().z);
        matrix.push();
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        setupWorldRenderer();
        if (!depth) RenderSystem.disableDepthTest();
        if (!cull) RenderSystem.disableCull();
        BUFFER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        BUFFER.pos(matrix4f, dx1, dy1, dz1).color(color).endVertex();
        BUFFER.pos(matrix4f, dx1, dy2, dz1).color(color).endVertex();
        BUFFER.pos(matrix4f, dx2, dy2, dz2).color(color).endVertex();
        BUFFER.pos(matrix4f, dx2, dy1, dz2).color(color).endVertex();
        TESSELLATOR.draw();
        if (!cull) RenderSystem.enableCull();
        if (!depth) RenderSystem.enableDepthTest();
        cleanupWorldRenderer();
        matrix.pop();
    }

    public void drawQuadH(MatrixStack matrix, float x1, float y1, float z1, float x2, float y2, float z2, int color, boolean depth, boolean cull) {
        float dx1 = (float) (x1 - cameraPos().x);
        float dy1 = (float) (y1 - cameraPos().y);
        float dz1 = (float) (z1 - cameraPos().z);
        float dx2 = (float) (x2 - cameraPos().x);
        float dy2 = (float) (y2 - cameraPos().y);
        float dz2 = (float) (z2 - cameraPos().z);
        matrix.push();
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        setupWorldRenderer();
        if (!depth) RenderSystem.disableDepthTest();
        if (!cull) RenderSystem.disableCull();
        BUFFER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        BUFFER.pos(matrix4f, dx1, dy1, dz1).color(color).endVertex();
        BUFFER.pos(matrix4f, dx2, dy1, dz1).color(color).endVertex();
        BUFFER.pos(matrix4f, dx2, dy2, dz2).color(color).endVertex();
        BUFFER.pos(matrix4f, dx1, dy2, dz2).color(color).endVertex();
        TESSELLATOR.draw();
        if (!cull) RenderSystem.enableCull();
        if (!depth) RenderSystem.enableDepthTest();
        cleanupWorldRenderer();
        matrix.pop();
    }

    public void drawLineQuad(MatrixStack matrix, float x1, float y1, float z1, float x2, float y2, float z2, int color, float lineWidth, boolean depth, boolean cull) {
        drawLine(matrix, x1, y1, z1, x2, y2, z1, color, lineWidth, depth, cull);
        drawLine(matrix, x2, y1, z1, x2, y2, z2, color, lineWidth, depth, cull);
        drawLine(matrix, x2, y1, z2, x1, y2, z2, color, lineWidth, depth, cull);
        drawLine(matrix, x1, y1, z2, x1, y2, z1, color, lineWidth, depth, cull);
    }

    public void drawLine(MatrixStack matrix, float x1, float y1, float z1, float x2, float y2, float z2, int color, float lineWidth, boolean depth, boolean cull) {
        float dx1 = (float) (x1 - cameraPos().x);
        float dy1 = (float) (y1 - cameraPos().y);
        float dz1 = (float) (z1 - cameraPos().z);
        float dx2 = (float) (x2 - cameraPos().x);
        float dy2 = (float) (y2 - cameraPos().y);
        float dz2 = (float) (z2 - cameraPos().z);
        matrix.push();
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        setupWorldRenderer();
        enableBlendAndSmoothLines(lineWidth);
        if (!depth) RenderSystem.disableDepthTest();
        if (!cull) RenderSystem.disableCull();
        BUFFER.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        BUFFER.pos(matrix4f, dx1, dy1, dz1).color(color).endVertex();
        BUFFER.pos(matrix4f, dx2, dy2, dz2).color(color).endVertex();
        TESSELLATOR.draw();
        if (!cull) RenderSystem.enableCull();
        if (!depth) RenderSystem.enableDepthTest();
        disableBlendAndSmoothLines();
        cleanupWorldRenderer();
        matrix.pop();
    }

    public Vector3d cameraPos() {
        return mc.gameRenderer.getActiveRenderInfo().getProjectedView();
    }

    public void setupOrientationMatrix(MatrixStack matrix, double x, double y, double z) {
        matrix.translate(x - cameraPos().x, y - cameraPos().y, z - cameraPos().z);
    }

    public void rotateToCamera(MatrixStack matrix) {
        matrix.rotate(mc.getRenderManager().getCameraOrientation());
    }

    public void setupWorldRenderer() {
        RenderSystem.disableLighting();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableTexture();
        setupBlend();
    }

    public void cleanupWorldRenderer() {
        cleanupBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableAlphaTest();
    }

    private void enableBlendAndSmoothLines(float lineWidth) {
        setupBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(lineWidth);
    }

    private void disableBlendAndSmoothLines() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(1F);
        cleanupBlend();
    }

    private static void setupBlend() {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private static void cleanupBlend() {
        RenderSystem.disableBlend();
    }
}
