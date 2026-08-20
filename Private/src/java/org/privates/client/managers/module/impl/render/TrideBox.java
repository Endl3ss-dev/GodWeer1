package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.AntiBot;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.draw.RenderUtil3D;

@Category2
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "3D-BOX", category = Category.RENDER, description = "3D BOX также Penis esp ну а в кратце улучшает подсветку игроков")
public class TrideBox extends Module {

    public static TrideBox getInstance() {
        return Instance.get(TrideBox.class);
    }

    final BooleanSetting box = new BooleanSetting(this, "Бокс", true);
    final BooleanSetting skeleton = new BooleanSetting(this, "Скелет", true);
    final BooleanSetting penis = new BooleanSetting(this, "Пенис", false);
    final SliderSetting width = new SliderSetting(this, "Толщина линий", 1.5F, 0.5F, 5.0F, 0.1F);
    final BooleanSetting filled = new BooleanSetting(this, "Заливка бокса", true).setVisible(box::getValue);
    final SliderSetting fillAlpha = new SliderSetting(this, "Прозрачность заливки", 0.2F, 0.1F, 1.0F, 0.05F).setVisible(() -> box.getValue() && filled.getValue());
    final SliderSetting penisLength = new SliderSetting(this, "Длина пениса", 0.8F, 0.3F, 2.0F, 0.1F).setVisible(penis::getValue);
    final SliderSetting penisThickness = new SliderSetting(this, "Толщина пениса", 0.05F, 0.02F, 0.15F, 0.01F).setVisible(penis::getValue);
    final SliderSetting ballSize = new SliderSetting(this, "Размер яиц", 0.08F, 0.04F, 0.2F, 0.01F).setVisible(penis::getValue);

    private final Tessellator tessellator = Tessellator.getInstance();
    private final BufferBuilder bufferBuilder = tessellator.getBuffer();

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (mc.world == null || mc.getRenderManager() == null || mc.getRenderManager().info == null) {
            return;
        }

        MatrixStack matrixStack = event.getMatrix();
        float partialTicks = event.getPartialTicks();
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();

        setupRender();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive() || AntiBot.getInstance().isBot(player)) {
                continue;
            }

            int color = Theme.getInstance().clientColor();
            float red = ((color >> 16) & 0xFF) / 255.0F;
            float green = ((color >> 8) & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;

            Vector3d playerPos = RenderUtil3D.interpolate(player, partialTicks).subtract(cameraPos);

            matrixStack.push();
            matrixStack.translate(playerPos.x, playerPos.y, playerPos.z);

            if (box.getValue()) {
                drawBox(matrixStack, player, red, green, blue);
            }
            if (skeleton.getValue()) {
                drawSkeleton(matrixStack, player, partialTicks, red, green, blue);
            }
            if (penis.getValue()) {
                drawPenis(matrixStack, player, partialTicks, red, green, blue);
            }

            matrixStack.pop();
        }

        releaseRender();
    }

    private void drawBox(MatrixStack matrixStack, PlayerEntity player, float r, float g, float b) {
        float halfWidth = player.getWidth() / 2.0f;
        float height = player.getHeight();

        if (filled.getValue()) {
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            RenderUtil3D.renderFilledBox(bufferBuilder, matrixStack, -halfWidth, 0, -halfWidth, halfWidth, height, halfWidth, r, g, b, fillAlpha.getValue());
            tessellator.draw();
        }

        GL11.glLineWidth(width.getValue());
        bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil3D.renderWireframeBox(bufferBuilder, matrixStack, -halfWidth, 0, -halfWidth, halfWidth, height, halfWidth, r, g, b, 1.0f);
        tessellator.draw();
    }

    private void drawSkeleton(MatrixStack matrixStack, PlayerEntity player, float partialTicks, float r, float g, float b) {
        float bodyYaw = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
        float h = player.getHeight();

        Vector3d head = new Vector3d(0, h * 0.95, 0);
        Vector3d neck = new Vector3d(0, h * 0.85, 0);
        Vector3d upperChest = new Vector3d(0, h * 0.75, 0);
        Vector3d midChest = new Vector3d(0, h * 0.65, 0);
        Vector3d lowerChest = new Vector3d(0, h * 0.55, 0);
        Vector3d pelvis = new Vector3d(0, h * 0.45, 0);

        Vector3d rightShoulder = rotate(new Vector3d(-0.25, h * 0.78, 0), -bodyYaw);
        Vector3d leftShoulder = rotate(new Vector3d(0.25, h * 0.78, 0), -bodyYaw);

        float rightArmSwing = player.prevLimbSwingAmount + (player.limbSwingAmount - player.prevLimbSwingAmount) * partialTicks;
        float leftArmSwing = -rightArmSwing;

        Vector3d rightElbow = rotate(new Vector3d(-0.35 + Math.sin(rightArmSwing * 2) * 0.1, h * 0.55 - Math.abs(Math.sin(rightArmSwing * 2)) * 0.05, Math.cos(rightArmSwing * 2) * 0.15), -bodyYaw);
        Vector3d leftElbow = rotate(new Vector3d(0.35 + Math.sin(leftArmSwing * 2) * 0.1, h * 0.55 - Math.abs(Math.sin(leftArmSwing * 2)) * 0.05, Math.cos(leftArmSwing * 2) * 0.15), -bodyYaw);

        Vector3d rightHand = rotate(new Vector3d(-0.4 + Math.sin(rightArmSwing * 2) * 0.15, h * 0.35 - Math.abs(Math.sin(rightArmSwing * 2)) * 0.1, Math.cos(rightArmSwing * 2) * 0.2), -bodyYaw);
        Vector3d leftHand = rotate(new Vector3d(0.4 + Math.sin(leftArmSwing * 2) * 0.15, h * 0.35 - Math.abs(Math.sin(leftArmSwing * 2)) * 0.1, Math.cos(leftArmSwing * 2) * 0.2), -bodyYaw);

        float legSwing = player.prevLimbSwingAmount + (player.limbSwingAmount - player.prevLimbSwingAmount) * partialTicks;

        Vector3d rightHip = rotate(new Vector3d(-0.15, h * 0.45, 0), -bodyYaw);
        Vector3d leftHip = rotate(new Vector3d(0.15, h * 0.45, 0), -bodyYaw);

        Vector3d rightKnee = rotate(new Vector3d(-0.15 + Math.sin(legSwing * 2 + Math.PI) * 0.05, h * 0.25, Math.cos(legSwing * 2 + Math.PI) * 0.15), -bodyYaw);
        Vector3d leftKnee = rotate(new Vector3d(0.15 + Math.sin(legSwing * 2) * 0.05, h * 0.25, Math.cos(legSwing * 2) * 0.15), -bodyYaw);

        Vector3d rightFoot = rotate(new Vector3d(-0.15 + Math.sin(legSwing * 2 + Math.PI) * 0.08, 0.05, Math.cos(legSwing * 2 + Math.PI) * 0.25), -bodyYaw);
        Vector3d leftFoot = rotate(new Vector3d(0.15 + Math.sin(legSwing * 2) * 0.08, 0.05, Math.cos(legSwing * 2) * 0.25), -bodyYaw);

        GL11.glLineWidth(width.getValue());
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        drawLine(bufferBuilder, matrixStack, head, neck, r, g, b);
        drawLine(bufferBuilder, matrixStack, neck, upperChest, r, g, b);
        drawLine(bufferBuilder, matrixStack, upperChest, midChest, r, g, b);
        drawLine(bufferBuilder, matrixStack, midChest, lowerChest, r, g, b);
        drawLine(bufferBuilder, matrixStack, lowerChest, pelvis, r, g, b);

        drawLine(bufferBuilder, matrixStack, upperChest, rightShoulder, r, g, b);
        drawLine(bufferBuilder, matrixStack, rightShoulder, rightElbow, r, g, b);
        drawLine(bufferBuilder, matrixStack, rightElbow, rightHand, r, g, b);
        drawLine(bufferBuilder, matrixStack, upperChest, leftShoulder, r, g, b);
        drawLine(bufferBuilder, matrixStack, leftShoulder, leftElbow, r, g, b);
        drawLine(bufferBuilder, matrixStack, leftElbow, leftHand, r, g, b);

        drawLine(bufferBuilder, matrixStack, pelvis, rightHip, r, g, b);
        drawLine(bufferBuilder, matrixStack, rightHip, rightKnee, r, g, b);
        drawLine(bufferBuilder, matrixStack, rightKnee, rightFoot, r, g, b);
        drawLine(bufferBuilder, matrixStack, pelvis, leftHip, r, g, b);
        drawLine(bufferBuilder, matrixStack, leftHip, leftKnee, r, g, b);
        drawLine(bufferBuilder, matrixStack, leftKnee, leftFoot, r, g, b);

        tessellator.draw();
    }

    private void drawPenis(MatrixStack matrixStack, PlayerEntity player, float partialTicks, float r, float g, float b) {
        double yaw = Math.toRadians(-player.rotationYaw);
        double penisLen = penisLength.getValue();
        double thickness = penisThickness.getValue();
        double balls = ballSize.getValue();
        float h = player.getHeight();

        Vector3d basePos = new Vector3d(0, h * 0.43, 0);
        Vector3d forward = new Vector3d(Math.sin(yaw), 0, Math.cos(yaw));

        Vector3d leftBall = basePos.add(Math.sin(yaw - Math.PI / 2) * balls * 1.2, -balls * 0.5, Math.cos(yaw - Math.PI / 2) * balls * 1.2);
        Vector3d rightBall = basePos.add(Math.sin(yaw + Math.PI / 2) * balls * 1.2, -balls * 0.5, Math.cos(yaw + Math.PI / 2) * balls * 1.2);

        Vector3d shaftStart = basePos.add(forward.scale(balls * 0.3));
        Vector3d shaftMid = shaftStart.add(forward.scale(penisLen * 0.5)).add(0, -penisLen * 0.05, 0);
        Vector3d shaftEnd = shaftStart.add(forward.scale(penisLen)).add(0, -penisLen * 0.08, 0);
        Vector3d tip = shaftEnd.add(forward.scale(balls * 0.6));

        GL11.glLineWidth(width.getValue());

        drawOptimizedSphere(matrixStack, leftBall, balls, 8, r * 0.9f, g * 0.7f, b * 0.7f);
        drawOptimizedSphere(matrixStack, rightBall, balls, 8, r * 0.9f, g * 0.7f, b * 0.7f);

        drawOptimizedCylinder(matrixStack, shaftStart, shaftMid, thickness, 12, r, g, b);
        drawOptimizedCylinder(matrixStack, shaftMid, shaftEnd, thickness * 0.95, 12, r, g, b);

        drawOptimizedSphere(matrixStack, tip, balls * 0.7, 10, r * 1.1f, g * 0.8f, b * 0.8f);

        Vector3d urethraStart = shaftEnd.add(forward.scale(balls * 0.3));
        Vector3d urethraEnd = tip;
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        drawLine(bufferBuilder, matrixStack, urethraStart, urethraEnd, r * 0.6f, g * 0.4f, b * 0.4f);
        tessellator.draw();
    }

    private void drawOptimizedSphere(MatrixStack matrixStack, Vector3d pos, double radius, int segments, float r, float g, float b) {
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        double angleStep = Math.PI / segments;

        for (int i = 0; i < segments; i++) {
            double alpha1 = i * angleStep;
            double alpha2 = (i + 1) * angleStep;

            for (int j = 0; j < segments * 2; j++) {
                double beta1 = j * angleStep;
                double beta2 = (j + 1) * angleStep;

                double x1 = pos.x + radius * Math.cos(beta1) * Math.sin(alpha1);
                double y1 = pos.y + radius * Math.sin(beta1) * Math.sin(alpha1);
                double z1 = pos.z + radius * Math.cos(alpha1);

                double x2 = pos.x + radius * Math.cos(beta2) * Math.sin(alpha1);
                double y2 = pos.y + radius * Math.sin(beta2) * Math.sin(alpha1);
                double z2 = pos.z + radius * Math.cos(alpha1);

                drawLine(bufferBuilder, matrixStack, new Vector3d(x1, y1, z1), new Vector3d(x2, y2, z2), r, g, b);

                double x3 = pos.x + radius * Math.cos(beta1) * Math.sin(alpha2);
                double y3 = pos.y + radius * Math.sin(beta1) * Math.sin(alpha2);
                double z3 = pos.z + radius * Math.cos(alpha2);

                drawLine(bufferBuilder, matrixStack, new Vector3d(x1, y1, z1), new Vector3d(x3, y3, z3), r, g, b);
            }
        }

        tessellator.draw();
    }

    private void drawOptimizedCylinder(MatrixStack matrixStack, Vector3d start, Vector3d end, double radius, int slices, float r, float g, float b) {
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        double angleStep = 2 * Math.PI / slices;

        for (int i = 0; i < slices; i++) {
            double a1 = i * angleStep;
            double a2 = (i + 1) * angleStep;

            double x1s = start.x + radius * Math.cos(a1);
            double z1s = start.z + radius * Math.sin(a1);
            double x2s = start.x + radius * Math.cos(a2);
            double z2s = start.z + radius * Math.sin(a2);

            double x1e = end.x + radius * Math.cos(a1);
            double z1e = end.z + radius * Math.sin(a1);
            double x2e = end.x + radius * Math.cos(a2);
            double z2e = end.z + radius * Math.sin(a2);

            drawLine(bufferBuilder, matrixStack, new Vector3d(x1s, start.y, z1s), new Vector3d(x1e, end.y, z1e), r, g, b);
            drawLine(bufferBuilder, matrixStack, new Vector3d(x1s, start.y, z1s), new Vector3d(x2s, start.y, z2s), r, g, b);
            drawLine(bufferBuilder, matrixStack, new Vector3d(x1e, end.y, z1e), new Vector3d(x2e, end.y, z2e), r, g, b);
        }

        tessellator.draw();
    }

    private Vector3d rotate(Vector3d vec, float yaw) {
        double rad = Math.toRadians(yaw);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double x = vec.x * cos - vec.z * sin;
        double z = vec.x * sin + vec.z * cos;
        return new Vector3d(x, vec.y, z);
    }

    private void drawLine(BufferBuilder buffer, MatrixStack stack, Vector3d from, Vector3d to, float r, float g, float b) {
        buffer.pos(stack.getLast().getMatrix(), (float) from.x, (float) from.y, (float) from.z).color(r, g, b, 1.0f).endVertex();
        buffer.pos(stack.getLast().getMatrix(), (float) to.x, (float) to.y, (float) to.z).color(r, g, b, 1.0f).endVertex();
    }

    private void setupRender() {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    private void releaseRender() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}