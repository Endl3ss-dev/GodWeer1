package never.lose.modules.impl.Render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import never.lose.NeverLose;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.SliderSetting;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ModuleInfo(name = "Trails", desc = "Красивый след после ходьбы игрока", category = ModuleCategory.RENDER)
public class Trails extends Module {

    public final SliderSetting length = new SliderSetting("Длина", 250, 150, 350, 5);

    private final List<Point> points = new ArrayList<>();

    @Override
    public void onDisable() {
        points.clear();
    }

    @Override
    public void onRender3D(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.gameRenderer == null || mc.options.getPerspective() == Perspective.FIRST_PERSON) {
            return;
        }

        long lifetime = (long) length.getValue();
        long now = System.currentTimeMillis();
        Iterator<Point> iterator = points.iterator();
        while (iterator.hasNext()) {
            if (now - iterator.next().time > lifetime) {
                iterator.remove();
            }
        }

        double x = player.prevX + (player.getX() - player.prevX) * tickDelta;
        double y = player.prevY + (player.getY() - player.prevY) * tickDelta;
        double z = player.prevZ + (player.getZ() - player.prevZ) * tickDelta;
        points.add(new Point(new Vec3d(x, y, z), now));
        if (points.size() < 2) {
            return;
        }

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cam = camera.getPos();
        int color = NeverLose.getInstance().getThemeManager().getCurrentTheme().colorClient;
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        int index = 0;
        int size = points.size();
        for (Point point : points) {
            float alpha = Math.min((float) index / (float) size, 1.0F) * 0.45F;
            int finalColor = ((int) (alpha * 255.0F) << 24) | (color & 0x00FFFFFF);
            Vec3d pos = point.pos.subtract(cam);
            buffer.vertex(matrix, (float) pos.x, (float) (pos.y + player.getHeight()), (float) pos.z).color(finalColor);
            buffer.vertex(matrix, (float) pos.x, (float) pos.y + 0.001F, (float) pos.z).color(finalColor);
            index++;
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        drawLine(matrices, camera, player, color, true);
        drawLine(matrices, camera, player, color, false);

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private void drawLine(MatrixStack matrices, Camera camera, ClientPlayerEntity player, int color, boolean withHeight) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Vec3d cam = camera.getPos();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        int index = 0;
        int size = points.size();
        for (Point point : points) {
            float alpha = Math.min((float) index / (float) size, 1.0F) * 0.7F;
            int finalColor = ((int) (alpha * 255.0F) << 24) | (color & 0x00FFFFFF);
            Vec3d pos = point.pos.subtract(cam);
            float y = withHeight ? (float) (pos.y + player.getHeight()) : (float) pos.y + 0.001F;
            buffer.vertex(matrix, (float) pos.x, y, (float) pos.z).color(finalColor);
            index++;
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }

    private static final class Point {
        private final Vec3d pos;
        private final long time;

        private Point(Vec3d pos, long time) {
            this.pos = pos;
            this.time = time;
        }
    }
}
