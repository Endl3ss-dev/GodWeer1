package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SkeletonEsp", category = Category.RENDER, description = "Рендерит скелет игроков линиями")
public class SkeletonEsp extends Module {

    public static SkeletonEsp getInstance() {
        return Instance.get(SkeletonEsp.class);
    }

    final ColorSetting color = new ColorSetting(this, "Цвет", ColorUtil.rgba(255, 255, 255, 255));
    final ColorSetting friendColor = new ColorSetting(this, "Цвет друзей", ColorUtil.rgba(0, 255, 0, 255));
    final SliderSetting size = new SliderSetting(this, "Толщина", 1.5f, 0.5f, 4f, 0.1f);
    final BooleanSetting penis = new BooleanSetting(this, "Хуй", true);
    final SliderSetting dlina = new SliderSetting(this, "Длина хуя в см", 5f, 2.5f, 40f, 2.5f).setVisible(penis::getValue);

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (mc.world == null || mc.player == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!player.isAlive()) continue;
            if (player == mc.player && mc.gameSettings.getPointOfView().firstPerson()) continue;
            renderSkeleton(event, player);
        }
    }

    private void renderSkeleton(Render3DLastEvent event, PlayerEntity player) {
        float pt = event.getPartialTicks();
        Vector3d pos = RenderUtil3D.interpolate(player, pt);

        float scale = player.isChild() ? 0.5f : 1.0f;
        float headY = 1.62f * scale;
        float torsoTopY = 1.2f * scale;
        float torsoBottomY = 0.65f * scale;
        float armOffset = 0.35f * scale;
        float armLen = 0.52f * scale;
        float legOffset = 0.15f * scale;
        float legLen = 0.62f * scale;

        if (player.isCrouching()) {
            headY = 1.34f * scale;
            torsoTopY = 0.98f * scale;
            torsoBottomY = 0.48f * scale;
        }

        float bodyYaw = MathHelper.interpolateAngle(pt, player.prevRenderYawOffset, player.renderYawOffset);
        float headYaw = MathHelper.interpolateAngle(pt, player.prevRotationYawHead, player.rotationYawHead) - bodyYaw;
        float headPitch = MathHelper.clamp(MathHelper.lerp(pt, player.prevRotationPitch, player.rotationPitch), -60.0f, 60.0f);
        float yawRad = (float) Math.toRadians(bodyYaw);

        float limbSwing = player.limbSwing - player.limbSwingAmount * (1.0f - pt);
        float limbSwingAmount = MathHelper.lerp(pt, player.prevLimbSwingAmount, player.limbSwingAmount);
        limbSwingAmount = Math.min(1.0f, limbSwingAmount);

        float rArmX = MathHelper.cos(limbSwing * 0.6662f + (float) Math.PI) * 2.0f * limbSwingAmount * 0.5f;
        float lArmX = MathHelper.cos(limbSwing * 0.6662f) * 2.0f * limbSwingAmount * 0.5f;
        float rLegX = MathHelper.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        float lLegX = MathHelper.cos(limbSwing * 0.6662f + (float) Math.PI) * 1.4f * limbSwingAmount;

        float swing = player.getSwingProgress(pt);
        if (swing > 0.0f) {
            lArmX += -MathHelper.sin(swing * (float) Math.PI) * 1.5f;
        }

        int lineColor = Excellent.inst().friendManager().isFriend(player.getGameProfile().getName()) ? friendColor.getValue() : color.getValue();
        float lineWidth = size.getValue();

        Joint pelvis = toWorld(pos, yawRad, 0, torsoBottomY, 0);
        Joint chest = toWorld(pos, yawRad, 0, torsoTopY, 0);
        Joint headBase = toWorld(pos, yawRad, 0, torsoTopY, 0);
        Joint headTop = toWorld(
                pos,
                yawRad,
                (float) Math.sin(Math.toRadians(-headYaw)) * 0.05f,
                headY,
                (float) Math.sin(Math.toRadians(headPitch)) * 0.05f
        );

        draw(event, pelvis, chest, lineColor, lineWidth);
        draw(event, headBase, headTop, lineColor, lineWidth);

        Joint lShoulder = toWorld(pos, yawRad, -armOffset, torsoTopY, 0);
        Joint rShoulder = toWorld(pos, yawRad, armOffset, torsoTopY, 0);
        draw(event, chest, lShoulder, lineColor, lineWidth);
        draw(event, chest, rShoulder, lineColor, lineWidth);

        Joint lHand = toWorld(pos, yawRad, -armOffset, torsoTopY - armLen * MathHelper.cos(lArmX), armLen * MathHelper.sin(lArmX));
        Joint rHand = toWorld(pos, yawRad, armOffset, torsoTopY - armLen * MathHelper.cos(rArmX), armLen * MathHelper.sin(rArmX));
        draw(event, lShoulder, lHand, lineColor, lineWidth);
        draw(event, rShoulder, rHand, lineColor, lineWidth);

        Joint lHip = toWorld(pos, yawRad, -legOffset, torsoBottomY, 0);
        Joint rHip = toWorld(pos, yawRad, legOffset, torsoBottomY, 0);
        Joint lFoot = toWorld(pos, yawRad, -legOffset, torsoBottomY - legLen * MathHelper.cos(lLegX), legLen * MathHelper.sin(lLegX));
        Joint rFoot = toWorld(pos, yawRad, legOffset, torsoBottomY - legLen * MathHelper.cos(rLegX), legLen * MathHelper.sin(rLegX));
        draw(event, pelvis, lHip, lineColor, lineWidth);
        draw(event, pelvis, rHip, lineColor, lineWidth);
        draw(event, lHip, lFoot, lineColor, lineWidth);
        draw(event, rHip, rFoot, lineColor, lineWidth);

        if (penis.getValue() && !player.isSwimming() && !player.isElytraFlying()) {
            float pLen = dlina.getValue() / 20.0f;
            Joint pEnd = toWorld(pos, yawRad, 0.0f, torsoBottomY - 0.03f, pLen);
            draw(event, pelvis, pEnd, lineColor, lineWidth);
        }
    }

    private void draw(Render3DLastEvent event, Joint a, Joint b, int color, float width) {
        RenderUtil3D.drawLine(event.getMatrix(), a.x, a.y, a.z, b.x, b.y, b.z, color, width, false, false);
    }

    private Joint toWorld(Vector3d base, float yawRad, float lx, float ly, float lz) {
        float wx = (float) (base.x + (lx * MathHelper.cos(yawRad) - lz * MathHelper.sin(yawRad)));
        float wy = (float) (base.y + ly);
        float wz = (float) (base.z + (lz * MathHelper.cos(yawRad) + lx * MathHelper.sin(yawRad)));
        return new Joint(wx, wy, wz);
    }

    private record Joint(float x, float y, float z) {
    }
}
