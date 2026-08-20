package org.privates.client.managers.module.impl.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;

@Category2
@ModuleInfo(name = "Wings", description = "Крылья на спине игрока", category = Category.RENDER)
public class Wings extends Module {

    private final BooleanSetting onSelf = new BooleanSetting(this, "На себя", true);
    private final BooleanSetting onPlayers = new BooleanSetting(this, "На игроков", false);
    private final BooleanSetting throughEntities = new BooleanSetting(this, "Сквозь энтити", true);
    private final SliderSetting size = new SliderSetting(this, "Размер", 1.0F, 0.1F, 3.0F, 0.1F);
    private final SliderSetting spread = new SliderSetting(this, "Размах", 17.0F, 1.0F, 100.0F, 1.0F);
    private final SliderSetting wingAngle = new SliderSetting(this, "Угол", 47.5F, -90.0F, 90.0F, 1.0F);
    private final BooleanSetting firstPerson = new BooleanSetting(this, "От первого лица", false);
    private final SliderSetting brightness = new SliderSetting(this, "Яркость", 165.0F, 40.0F, 255.0F, 1.0F);
    private final BooleanSetting flapEnabled = new BooleanSetting(this, "Взмах", true);
    private final SliderSetting flapSpeed = new SliderSetting(this, "Скорость взмаха", 2.0F, 0.5F, 8.0F, 0.5F).setVisible(flapEnabled::getValue);
    private final SliderSetting flapAmplitude = new SliderSetting(this, "Амплитуда взмаха", 26.0F, 5.0F, 90.0F, 1.0F).setVisible(flapEnabled::getValue);

    // Shader-like style
    private final SliderSetting layers = new SliderSetting(this, "Слои", 4.0F, 2.0F, 10.0F, 1.0F);
    private final SliderSetting layerShift = new SliderSetting(this, "Смещение слоёв", 0.08F, 0.02F, 0.2F, 0.01F);
    private final SliderSetting widthMul = new SliderSetting(this, "Ширина фигур", 1.7F, 0.8F, 3.0F, 0.1F);
    private final SliderSetting ghostAlpha = new SliderSetting(this, "Прозрачность фигур", 120.0F, 20.0F, 220.0F, 5.0F);

    private static final int RAYS = 7;

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (onSelf.getValue()) {
            boolean isFirst = mc.gameSettings.getPointOfView().firstPerson();
            if (!isFirst || firstPerson.getValue()) {
                renderWings(event, mc.player, true);
            }
        }

        if (onPlayers.getValue()) {
            for (PlayerEntity p : mc.world.getPlayers()) {
                if (p == mc.player) continue;
                renderWings(event, p, false);
            }
        }
    }

    private void renderWings(Render3DPosedEvent event, PlayerEntity player, boolean isSelf) {
        float pt = event.getPartialTicks();
        float bodyYaw = MathHelper.interpolateAngle(
                pt,
                isSelf ? mc.player.prevRenderYawOffset : player.prevRenderYawOffset,
                isSelf ? mc.player.renderYawOffset : player.renderYawOffset
        );

        double worldX = MathHelper.lerp(pt, player.lastTickPosX, player.getPosX());
        double worldY = MathHelper.lerp(pt, player.lastTickPosY, player.getPosY()) + 1.3D;
        double worldZ = MathHelper.lerp(pt, player.lastTickPosZ, player.getPosZ());
        Vector3d cam = mc.getRenderManager().info.getProjectedView();

        MatrixStack ms = event.getMatrix();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 1);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        if (throughEntities.getValue()) RenderSystem.disableDepthTest();
        else RenderSystem.enableDepthTest();
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        ms.push();
        ms.translate(worldX - cam.x, worldY - cam.y, worldZ - cam.z);
        ms.rotate(Vector3f.YP.rotationDegrees(-bodyYaw));
        ms.translate(0.0D, 0.0D, -0.25D);

        float flapPhase = flapEnabled.getValue() ? (float) Math.sin(System.currentTimeMillis() * 0.001D * flapSpeed.getValue()) : 0.0F;
        drawWingLocal(ms, false, flapPhase);
        drawWingLocal(ms, true, flapPhase);
        ms.pop();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    private void drawWingLocal(MatrixStack ms, boolean right, float flapPhase) {
        float sideSign = right ? -1.0F : 1.0F;
        float spreadVal = spread.getValue();
        float sizeVal = size.getValue();
        int bright = (int) brightness.getValue().floatValue();
        float centerElev = wingAngle.getValue();
        float fanHalf = 52.5F;
        float fanStart = centerElev - fanHalf;
        float fanEnd = centerElev + fanHalf;
        float flapAmp = flapEnabled.getValue() ? flapAmplitude.getValue() : 0.0F;
        float sideAngleDeg = 180.0F + sideSign * spreadVal;

        for (int i = 0; i < RAYS; i++) {
            float t = i / (float) (RAYS - 1);
            float elevDeg = fanStart + t * (fanEnd - fanStart);
            float elevRad = (float) Math.toRadians(elevDeg);
            float lenMul = wingProfile(t);
            float rayLen = sizeVal * 1.8F * lenMul;
            float rayHalfW = sizeVal * 0.18F * lenMul;

            float flapOffset = flapPhase * flapAmp * t * sideSign;
            float flapYawRad = (float) Math.toRadians(sideAngleDeg - flapOffset);
            float fhx = (float) Math.sin(flapYawRad);
            float fhz = (float) Math.cos(flapYawRad);

            float tipX = fhx * rayLen * (float) Math.cos(elevRad);
            float tipY = (float) Math.sin(elevRad) * rayLen;
            float tipZ = fhz * rayLen * (float) Math.cos(elevRad);

            float fdx = tipX, fdy = tipY, fdz = tipZ;
            float flen = (float) Math.sqrt(fdx * fdx + fdy * fdy + fdz * fdz);
            if (flen < 0.001F) continue;
            fdx /= flen;
            fdy /= flen;
            fdz /= flen;

            float p1x = fdy * 0.0F - fdz;
            float p1y = fdz * 0.0F - fdx * 0.0F;
            float p1z = fdx - fdy * 0.0F;
            float pl1 = (float) Math.sqrt(p1x * p1x + p1y * p1y + p1z * p1z);
            if (pl1 < 0.001F) {
                p1x = -fhz;
                p1y = 0.0F;
                p1z = fhx;
                pl1 = 1.0F;
            }
            p1x /= pl1;
            p1y /= pl1;
            p1z /= pl1;

            float p2x = fdy * p1z - fdz * p1y;
            float p2y = fdz * p1x - fdx * p1z;
            float p2z = fdx * p1y - fdy * p1x;
            float pl2 = (float) Math.sqrt(p2x * p2x + p2y * p2y + p2z * p2z);
            if (pl2 < 0.001F) {
                p2x = 0.0F;
                p2y = 1.0F;
                p2z = 0.0F;
                pl2 = 1.0F;
            }
            p2x /= pl2;
            p2y /= pl2;
            p2z /= pl2;

            float baseHalfW = rayHalfW * widthMul.getValue() * 0.85F;
            float tipHalfW = rayHalfW * widthMul.getValue() * 0.45F;
            float rayProgress = i / (float) RAYS;

            drawGhostWing(ms, tipX, tipY, tipZ, p1x, p1y, p1z, baseHalfW, tipHalfW, bright, fdx, fdy, fdz, rayProgress);
            drawGhostWing(ms, tipX, tipY, tipZ, p2x, p2y, p2z, baseHalfW * 0.8F, tipHalfW * 0.8F, bright, fdx, fdy, fdz, rayProgress);
        }
    }

    private void drawGhostWing(MatrixStack ms,
                               float tipX, float tipY, float tipZ,
                               float perpX, float perpY, float perpZ,
                               float baseHalfW, float tipHalfW,
                               int peakBright,
                               float dirX, float dirY, float dirZ,
                               float rayProgress) {
        int rgb = Theme.getInstance().clientColor();
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        Matrix4f mat = ms.getLast().getMatrix();
        BufferBuilder buf = Tessellator.getInstance().getBuffer();

        int layerCount = layers.getValue().intValue();
        float shift = layerShift.getValue();
        int alphaBase = (int) ghostAlpha.getValue().floatValue();

        for (int l = 0; l < layerCount; l++) {
            float t = l / (float) Math.max(1, (layerCount - 1));
            float back = shift * l;
            float alphaMul = 1.0F - t * 0.62F;
            int fillA = Math.max(18, (int) (alphaBase * alphaMul * (0.9F + rayProgress * 0.35F)));
            int lineA = Math.min(255, Math.max(45, (int) (peakBright * alphaMul)));

            float bx = -dirX * back;
            float by = -dirY * back;
            float bz = -dirZ * back;
            float tx = tipX - dirX * back;
            float ty = tipY - dirY * back;
            float tz = tipZ - dirZ * back;

            float l0x = bx - perpX * baseHalfW;
            float l0y = by - perpY * baseHalfW;
            float l0z = bz - perpZ * baseHalfW;
            float r0x = bx + perpX * baseHalfW;
            float r0y = by + perpY * baseHalfW;
            float r0z = bz + perpZ * baseHalfW;
            float l1x = tx - perpX * tipHalfW;
            float l1y = ty - perpY * tipHalfW;
            float l1z = tz - perpZ * tipHalfW;
            float r1x = tx + perpX * tipHalfW;
            float r1y = ty + perpY * tipHalfW;
            float r1z = tz + perpZ * tipHalfW;

            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buf.pos(mat, l0x, l0y, l0z).color(r, g, b, fillA).endVertex();
            buf.pos(mat, r0x, r0y, r0z).color(r, g, b, fillA).endVertex();
            buf.pos(mat, r1x, r1y, r1z).color(r, g, b, Math.max(12, fillA - 45)).endVertex();
            buf.pos(mat, l1x, l1y, l1z).color(r, g, b, Math.max(12, fillA - 45)).endVertex();
            Tessellator.getInstance().draw();

            RenderSystem.lineWidth(1.0F + (1.0F - t) * 1.4F);
            buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
            buf.pos(mat, l0x, l0y, l0z).color(220, 240, 255, lineA).endVertex();
            buf.pos(mat, r0x, r0y, r0z).color(220, 240, 255, lineA).endVertex();
            buf.pos(mat, r1x, r1y, r1z).color(220, 240, 255, Math.max(28, lineA - 32)).endVertex();
            buf.pos(mat, l1x, l1y, l1z).color(220, 240, 255, Math.max(28, lineA - 32)).endVertex();
            Tessellator.getInstance().draw();
        }
    }

    private float wingProfile(float t) {
        float s = (float) Math.sin(t * Math.PI);
        return 0.35F + 0.65F * s;
    }
}

