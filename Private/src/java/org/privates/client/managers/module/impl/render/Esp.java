package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.events.orbit.EventPriority;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.events.render.RenderNameEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Font;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.gif.GifRender;

import java.util.*;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Esp", category = Category.RENDER, description = "ESP с различными режимами рендера")
public class Esp extends Module {
    public static Esp getInstance() {
        return Instance.get(Esp.class);
    }

    private final MultiBooleanSetting modes = new MultiBooleanSetting(this, "Режимы",
            BooleanSetting.of("Nametags", true),
            BooleanSetting.of("3D-Box", false)
    );

    private final MultiBooleanSetting checks = new MultiBooleanSetting(this, "Элементы",
            BooleanSetting.of("Игроки", true)
    );

    private final SliderSetting fontSize = new SliderSetting(this, "Размер шрифта", 7F, 6F, 12F, 0.1F).setVisible(() -> modes.getValue("Nametags"));
    private final BooleanSetting optimized = new BooleanSetting(this, "Оптимизировать", true).setVisible(() -> modes.getValue("Nametags"));
    private final BooleanSetting renderDonat = new BooleanSetting(this, "Отображать донат", true).setVisible(() -> modes.getValue("Nametags"));
    private final BooleanSetting showArmor = new BooleanSetting(this, "Отображать броню", true).setVisible(() -> modes.getValue("Nametags"));
    private final BooleanSetting showDurability = new BooleanSetting(this, "Прочность брони", true).setVisible(() -> modes.getValue("Nametags") && showArmor.getValue());
    private final BooleanSetting holyWorld = new BooleanSetting(this, "HolyWorld", false).setVisible(() -> modes.getValue("Nametags"));
    private final BooleanSetting renderItemIcon = new BooleanSetting(this, "Рендерить иконку", true).setVisible(() -> modes.getValue("Nametags"));
    private final BooleanSetting renderBigItem = new BooleanSetting(this, "Большой предмет", true).setVisible(() -> modes.getValue("Nametags"));
    private final BooleanSetting renderLowHPGif = new BooleanSetting(this, "Гиф при низком HP", true).setVisible(() -> modes.getValue("Nametags"));
    private final BooleanSetting nametagBlur = new BooleanSetting(this, "Блюр ректов", true).setVisible(() -> modes.getValue("Nametags"));
    private final SliderSetting nametagBlurStrength = new SliderSetting(this, "Сила блюра", 10F, 1F, 30F, 1F).setVisible(() -> modes.getValue("Nametags") && nametagBlur.getValue());
    private final SliderSetting nametagRectAlpha = new SliderSetting(this, "Прозрачность ректов", 200, 0, 255, 1).setVisible(() -> modes.getValue("Nametags"));

    private final ModeSetting boxMode = new ModeSetting(this, "Тип бокса", "Full", "Corner", "Line").setVisible(() -> modes.getValue("3D-Box"));
    private final BooleanSetting skeleton = new BooleanSetting(this, "Скелет", false).setVisible(() -> modes.getValue("3D-Box"));
    private final BooleanSetting penis = new BooleanSetting(this, "Пенис", false).setVisible(() -> modes.getValue("3D-Box"));
    private final ColorSetting boxColor = new ColorSetting(this, "Цвет бокса", 0xFFFFFFFF).setVisible(() -> modes.getValue("3D-Box"));
    private final SliderSetting boxThickness = new SliderSetting(this, "Толщина бокса", 1.0f, 0.1f, 5.0f, 0.1f).setVisible(() -> modes.getValue("3D-Box"));
    private final SliderSetting penisLength = new SliderSetting(this, "Длина пениса", 0.8F, 0.3F, 2.0F, 0.1F).setVisible(penis::getValue);
    private final SliderSetting penisThickness = new SliderSetting(this, "Толщина пениса", 0.05F, 0.02F, 0.15F, 0.01F).setVisible(penis::getValue);
    private final SliderSetting ballSize = new SliderSetting(this, "Размер яиц", 0.08F, 0.04F, 0.2F, 0.01F).setVisible(penis::getValue);

    private final Font font = Fonts.SF_MEDIUM;
    private final int timeTick = 600;
    private final Map<String, GifRender> gifCache = new HashMap<>();
    private final Map<PlayerEntity, Float> healthAnimationMap = new HashMap<>();
    private final Tessellator tessellator = Tessellator.getInstance();
    private final BufferBuilder bufferBuilder = tessellator.getBuffer();

    @EventHandler
    public void onRenderName(RenderNameEvent event) {
        if (modes.getValue("Nametags") && event.getEntity() instanceof AbstractClientPlayerEntity) {
            event.cancel();
        }
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        if (!modes.getValue("Nametags") || PlayerUtil.nullCheck()) return;
        IPacket<?> packet = e.getPacket();
        if (packet instanceof SEntityStatusPacket entityStatus) {
            Entity entity = entityStatus.getEntity(mc.world);
            if (entity instanceof PlayerEntity player && (entityStatus.getOpCode() == 2 || entityStatus.getOpCode() == 33)) {
                if (!(player.prevFallDistance > 3 && player.fallDistance == 0)) {
                    for (Entity ent : mc.world.getAllEntities()) {
                        if (ent instanceof MobEntity mob && mob.getLastAttackedEntity() == player && mob.getLastAttackedEntityTime() < 1000) {
                            return;
                        }
                    }
                    player.addPotionEffect(new EffectInstance(Effects.UNLUCK, timeTick, -1));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRender3D(Render3DPosedEvent event) {
        if (modes.getValue("Nametags") && checks.getValue("Игроки")) {
            renderNametags(event);
        }
        if (modes.getValue("3D-Box")) {
            handle3DBox(event);
        }
    }

    private void renderNametags(Render3DPosedEvent event) {
        float fontHeight = fontSize.getValue();
        List<PlayerEntity> players = mc.world.getEntitiesWithinAABB(PlayerEntity.class,
                mc.renderViewEntity.getBoundingBox().grow(128), this::isValidPlayer);

        MatrixStack matrixStack = event.getMatrix();
        float partialTicks = event.getPartialTicks();
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        GL11.glDepthMask(false);
        RenderSystem.disableTexture();
        RenderSystem.enableTexture();

        for (PlayerEntity player : players) {
            Vector3d pos = RenderUtil3D.interpolate(player, partialTicks)
                    .subtract(cameraPos)
                    .add(0, player.getHeight() - 1.82f, 0);

            matrixStack.push();
            matrixStack.translate(pos.x, pos.y, pos.z);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-mc.getRenderManager().info.getYaw()));
            matrixStack.scale(-0.0175f, -0.0175f, 0.0175f);

            renderNametag(player, matrixStack, fontHeight);

            matrixStack.pop();
        }

        GL11.glDepthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private void handle3DBox(Render3DPosedEvent event) {
        if (mc.world == null || mc.getRenderManager() == null || mc.getRenderManager().info == null) return;

        MatrixStack matrixStack = event.getMatrix();
        float partialTicks = event.getPartialTicks();
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();

        setupRender();

        List<PlayerEntity> players = mc.world.getEntitiesWithinAABB(PlayerEntity.class, mc.renderViewEntity.getBoundingBox().grow(128), this::isValidPlayer);
        players.forEach(player -> {
            if (player == mc.player || !player.isAlive()) return;

            int color = Theme.getInstance().clientColor();

            Vector3d playerPos = RenderUtil3D.interpolate(player, partialTicks).subtract(cameraPos);

            matrixStack.push();
            matrixStack.translate(playerPos.x, playerPos.y, playerPos.z);

            AxisAlignedBB bb = player.getBoundingBox();

            if (boxMode.is("Full")) {
                drawFullBox(matrixStack, bb, boxColor.getValue(), boxThickness.getValue());
            } else if (boxMode.is("Corner")) {
                drawCornerBox(matrixStack, bb, boxColor.getValue(), boxThickness.getValue());
            } else if (boxMode.is("Line")) {
                drawLineBox(matrixStack, bb, boxColor.getValue(), boxThickness.getValue());
            }

            if (skeleton.getValue()) {
                float r = ((color >> 16) & 0xFF) / 255.0F;
                float g = ((color >> 8) & 0xFF) / 255.0F;
                float b = (color & 0xFF) / 255.0F;
                drawSkeleton(matrixStack, player, partialTicks, r, g, b);
            }
            if (penis.getValue()) {
                float r = ((color >> 16) & 0xFF) / 255.0F;
                float g = ((color >> 8) & 0xFF) / 255.0F;
                float b = (color & 0xFF) / 255.0F;
                drawPenis(matrixStack, player, partialTicks, r, g, b);
            }

            matrixStack.pop();
        });

        releaseRender();
    }

    private void drawFullBox(MatrixStack matrixStack, AxisAlignedBB bb, int color, float lineWidth) {
        float halfWidth = (float) (bb.maxX - bb.minX) / 2.0f;
        float height = (float) (bb.maxY - bb.minY);

        GL11.glLineWidth(lineWidth);
        bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        RenderUtil3D.renderWireframeBox(bufferBuilder, matrixStack, -halfWidth, 0, -halfWidth, halfWidth, height, halfWidth, r, g, b, 1.0f);
        tessellator.draw();
    }

    private void drawCornerBox(MatrixStack matrixStack, AxisAlignedBB bb, int color, float lineWidth) {
        float x = (float) (bb.minX + bb.maxX) / 2.0f;
        float y = (float) (bb.minY + bb.maxY) / 2.0f;
        float z = (float) (bb.minZ + bb.maxZ) / 2.0f;

        Vector3d center = new Vector3d(x, y, z);

        Vector3d[] corners = {
                new Vector3d(bb.minX, bb.minY, bb.minZ),
                new Vector3d(bb.maxX, bb.minY, bb.minZ),
                new Vector3d(bb.minX, bb.maxY, bb.minZ),
                new Vector3d(bb.maxX, bb.maxY, bb.minZ),
                new Vector3d(bb.minX, bb.minY, bb.maxZ),
                new Vector3d(bb.maxX, bb.minY, bb.maxZ),
                new Vector3d(bb.minX, bb.maxY, bb.maxZ),
                new Vector3d(bb.maxX, bb.maxY, bb.maxZ)
        };

        GL11.glLineWidth(lineWidth);
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        for (Vector3d corner : corners) {
            drawLine(bufferBuilder, matrixStack, corner, center, r, g, b);
        }

        tessellator.draw();
    }

    private void drawLineBox(MatrixStack matrixStack, AxisAlignedBB bb, int color, float lineWidth) {
        GL11.glLineWidth(lineWidth);
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        Vector3d min = new Vector3d(bb.minX, bb.minY, bb.minZ);
        Vector3d max = new Vector3d(bb.maxX, bb.maxY, bb.maxZ);

        drawLine(bufferBuilder, matrixStack, min, new Vector3d(max.x, min.y, min.z), r, g, b);
        drawLine(bufferBuilder, matrixStack, new Vector3d(max.x, min.y, min.z), max, r, g, b);
        drawLine(bufferBuilder, matrixStack, max, new Vector3d(min.x, max.y, max.z), r, g, b);
        drawLine(bufferBuilder, matrixStack, new Vector3d(min.x, max.y, max.z), min, r, g, b);

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

        GL11.glLineWidth(boxThickness.getValue());
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

        GL11.glLineWidth(boxThickness.getValue());

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

    private void renderNametag(Entity entity, MatrixStack matrix, float fontHeight) {
        if (!(entity instanceof AbstractClientPlayerEntity player)) return;

        float centerX = 0f;
        float playerHeight = player.getHeight() / 0.0175f;
        float currentY = -playerHeight - 30f;

        ItemStack offHandStack = player.getHeldItemOffhand();
        String talismanName = getTalismanName(offHandStack);
        if (talismanName != null) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            drawTalismanText(matrix, centerX, currentY, talismanName, fontHeight);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            currentY += fontHeight + 3f;
        }

        float headSize = 7f;
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        drawPlayerHead(matrix, player, centerX - headSize / 2f, currentY, headSize, headSize);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        currentY += headSize + 2f;

        currentY = renderArmorItems(matrix, player, centerX, currentY, fontHeight);

        drawNametagInfo(matrix, player, centerX, currentY - 8f, fontHeight);

        drawHealthBar(matrix, centerX, -107f, playerHeight, player);

        drawEffects(matrix, centerX + 35f, -playerHeight, player, fontHeight);
    }

    private float renderArmorItems(MatrixStack matrix, AbstractClientPlayerEntity player, float centerX, float currentY, float fontHeight) {
        if (!showArmor.getValue()) return currentY;

        List<ItemStack> armor = new ArrayList<>();
        for (ItemStack stack : player.inventory.armorInventory) {
            if (!stack.isEmpty()) armor.add(stack);
        }
        Collections.reverse(armor);
        if (armor.isEmpty()) return currentY;

        float size = 9.6f;
        float spacing = 2f;
        float padding = 1f;
        float totalWidth = armor.size() * (size + padding * 2) + (armor.size() - 1) * spacing;
        float y = currentY - 7;
        float x = centerX - totalWidth / 2f;

        for (ItemStack stack : armor) {
            float rectW = size + padding * 2;
            float rectH = size + padding * 2 + (showDurability.getValue() && stack.isDamageable() ? 1.2f : 0f);

            RenderUtil.CustomStyled.drawGui(matrix, x, y - 9, rectW, rectH, 2, nametagBlur.getValue(), nametagBlurStrength.getValue());

            renderItemInWorld(matrix, stack, x + padding, y - 9 + padding, size);

            if (showDurability.getValue() && stack.isDamageable()) {
                float percent = 1f - (float) stack.getDamage() / stack.getMaxDamage();
                float barY = y + padding + size + 0.5f;

                RenderUtil.Rounded.roundedRect(matrix, x + padding, barY - 9, size, 0.8f, ColorUtil.getColor(0, 0, 0, 180), Round.of(0.4f));
                RenderUtil.Rounded.roundedRect(matrix, x + padding, barY - 9, size * percent, 0.8f, getDurabilityColor(percent), Round.of(0.4f));
            }

            x += rectW + spacing;
        }

        return y + size + padding * 2 + (showDurability.getValue() ? 1.2f : 0f) + 3f;
    }

    private void renderItemInWorld(MatrixStack matrix, ItemStack stack, float x, float y, float size) {
        matrix.push();
        matrix.translate(x + size / 2f, y + size / 2f, 0);
        matrix.scale(size, -size, size);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableRescaleNormal();
        RenderHelper.enableStandardItemLighting();

        IRenderTypeBuffer.Impl buffer = mc.getRenderTypeBuffers().getBufferSource();

        mc.getItemRenderer().renderItem(
                stack,
                ItemCameraTransforms.TransformType.GUI,
                15728880,
                OverlayTexture.NO_OVERLAY,
                matrix,
                buffer
        );

        buffer.finish();

        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        matrix.pop();
    }

    private void drawHealthBar(MatrixStack matrix, float x, float y, float height, AbstractClientPlayerEntity player) {
        float barWidth = 2.5f;
        float barHeight = height;

        float targetHealth = player.getHealth() / player.getMaxHealth();
        Float animatedHealth = healthAnimationMap.get(player);
        if (animatedHealth == null) animatedHealth = targetHealth;

        animatedHealth += (targetHealth - animatedHealth) * 0.15f;
        healthAnimationMap.put(player, animatedHealth);

        float offsetX = -32;

        RenderUtil.CustomStyled.drawGui(matrix, x + offsetX, y, barWidth, barHeight, 1.5f, nametagBlur.getValue(), nametagBlurStrength.getValue());

        int startColor = ColorUtil.rgba(255, 50, 50, 255);
        int endColor = Theme.getInstance().clientColor();

        float filledHeight = barHeight * animatedHealth;

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        float x1 = x + offsetX + 0.3f;
        float x2 = x1 + barWidth - 0.6f;
        float y1 = y + barHeight - filledHeight;
        float y2 = y + barHeight;

        float r1 = ((startColor >> 16) & 0xFF) / 255.0f;
        float g1 = ((startColor >> 8) & 0xFF) / 255.0f;
        float b1 = (startColor & 0xFF) / 255.0f;
        float a1 = ((startColor >> 24) & 0xFF) / 255.0f;

        float r2 = ((endColor >> 16) & 0xFF) / 255.0f;
        float g2 = ((endColor >> 8) & 0xFF) / 255.0f;
        float b2 = (endColor & 0xFF) / 255.0f;
        float a2 = ((endColor >> 24) & 0xFF) / 255.0f;

        bufferBuilder.pos(matrix.getLast().getMatrix(), x1, y2, 0).color(r2, g2, b2, a2).endVertex();
        bufferBuilder.pos(matrix.getLast().getMatrix(), x2, y2, 0).color(r2, g2, b2, a2).endVertex();
        bufferBuilder.pos(matrix.getLast().getMatrix(), x2, y1, 0).color(r1, g1, b1, a1).endVertex();
        bufferBuilder.pos(matrix.getLast().getMatrix(), x1, y1, 0).color(r1, g1, b1, a1).endVertex();

        tessellator.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void drawEffects(MatrixStack matrix, float x, float y, AbstractClientPlayerEntity player, float fontHeight) {
        Collection<EffectInstance> effects = player.getActivePotionEffects();
        if (effects.isEmpty()) return;

        float currentY = y;
        for (EffectInstance effect : effects) {
            Effect potion = effect.getPotion();
            String name = potion.getDisplayName().getString();
            String level = effect.getAmplifier() > 0 ? " " + (effect.getAmplifier() + 1) : "";

            int duration = effect.getDuration();
            String durationText = duration < 20 ? "∞" : formatDuration(duration);

            String text = name + level;

            float textWidth = font.getWidth(text + " " + durationText, fontHeight - 1f);
            float rectWidth = textWidth + 6f;
            float rectHeight = fontHeight + 2f;

            boolean isBad = !potion.isBeneficial();
            int textColor = isBad ? 0xFFFF5555 : -1;
            int durationColor = 0xFFAAAAAA;

            RenderUtil.CustomStyled.drawGui(matrix, x, currentY, rectWidth, rectHeight, 2f,
                    nametagBlur.getValue(), nametagBlurStrength.getValue());

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            font.draw(matrix, text, x + 3f, currentY + 1f, textColor, fontHeight - 1f);

            float durationX = x + rectWidth - font.getWidth(durationText, fontHeight - 1f) - 3f;
            font.draw(matrix, durationText, durationX, currentY + 1f, durationColor, fontHeight - 1f);
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            currentY += rectHeight + 2f;
        }
    }

    private String formatDuration(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        seconds %= 60;
        return minutes > 0 ? String.format("%d:%02d", minutes, seconds) : String.valueOf(seconds);
    }

    private String getTalismanName(ItemStack stack) {
        if (stack.isEmpty()) return null;
        String displayName = stack.getDisplayName().getString().toLowerCase();
        if (displayName.contains("талисман") || displayName.contains("сфера")) {
            return displayName.replace("талисман", "").replace("сфера", "").trim();
        }
        return null;
    }

    private void drawPlayerHead(MatrixStack matrix, AbstractClientPlayerEntity player, float x, float y, float width, float height) {
        net.minecraft.util.ResourceLocation skin = player.getLocationSkin();

        matrix.push();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderUtil.Texture.roundedHead(skin, x, y, width, height, 2f, 1f, 0f);
        RenderSystem.disableBlend();
        matrix.pop();
    }

    private void drawNametagInfo(MatrixStack matrix, AbstractClientPlayerEntity player, float centerX, float y, float fontHeight) {
        String donateText = renderDonat.getValue() ? player.getDisplayName().getString() : "";
        String playerName = player.getName().getString();
        String health = String.format("%.1f", player.getHealth());

        float padding = 3;
        float dotSize = 2;
        float spacing = 2;

        float donateWidth = donateText.isEmpty() ? 0 : font.getWidth(donateText, fontHeight);
        float nameWidth = font.getWidth(playerName, fontHeight);
        float healthWidth = font.getWidth(health, fontHeight);

        float totalWidth = (donateText.isEmpty() ? 0 : donateWidth + dotSize + spacing * 2) +
                nameWidth + dotSize + spacing * 2 + healthWidth;

        float rectWidth = totalWidth + padding * 2;
        float rectHeight = fontHeight + padding * 2;

        RenderUtil.CustomStyled.drawGui(matrix, centerX - rectWidth / 2f, y - padding, rectWidth, rectHeight, 3, nametagBlur.getValue(), nametagBlurStrength.getValue());

        float currentX = centerX - totalWidth / 2f;

        if (!donateText.isEmpty()) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            font.draw(matrix, donateText, currentX, y, Theme.getInstance().clientColor(), fontHeight);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            currentX += donateWidth + spacing;
            drawDot(matrix, currentX, y + 1 + fontHeight / 2f - dotSize / 2f, dotSize, Theme.getInstance().clientColor());
            currentX += dotSize + spacing;
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        font.draw(matrix, playerName, currentX, y, -1, fontHeight);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        currentX += nameWidth + spacing;

        drawDot(matrix, currentX, y + 1 + fontHeight / 2f - dotSize / 2f, dotSize, Theme.getInstance().clientColor());
        currentX += dotSize + spacing;

        int healthColor = ColorUtil.interpolateColor(ColorUtil.RED, ColorUtil.GREEN, player.getHealth() / player.getMaxHealth());
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        font.draw(matrix, health, currentX, y, healthColor, fontHeight);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void drawTalismanText(MatrixStack matrix, float x, float y, String text, float fontHeight) {
        float textWidth = font.getWidth(text, fontHeight);
        int color = ColorUtil.RED;

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        font.draw(matrix, text, x - textWidth / 2f, y, color, fontHeight);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void drawDot(MatrixStack matrix, float x, float y, float size, int color) {
        RenderUtil.Rounded.roundedRect(matrix, x, y, size, size, color, Round.of(size / 2f));
    }

    private int getDurabilityColor(float percent) {
        if (percent > 0.75f) {
            return ColorUtil.getColor(50, 255, 100, 255);
        } else if (percent > 0.5f) {
            return ColorUtil.getColor(180, 255, 50, 255);
        } else if (percent > 0.25f) {
            return ColorUtil.getColor(255, 200, 50, 255);
        } else {
            return ColorUtil.getColor(255, 50, 50, 255);
        }
    }

    private boolean isValidPlayer(final PlayerEntity player) {
        if (!player.isAlive()) {
            return false;
        }
        if (mc.renderViewEntity != null && player == mc.renderViewEntity && mc.gameSettings.getPointOfView().firstPerson()) {
            return false;
        }
        return isInView(player);
    }

    public boolean isInView(Entity entity) {
        if (mc.getRenderViewEntity() == null || mc.worldRenderer.getClippinghelper() == null) {
            return false;
        }
        return mc.worldRenderer.getClippinghelper().isBoundingBoxInFrustum(entity.getBoundingBox()) || entity.ignoreFrustumCheck;
    }
}