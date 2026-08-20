package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.draw.RenderUtil3D;

import java.util.concurrent.ThreadLocalRandom;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "RotHitBox", category = Category.COMBAT, description = "Visual hitbox + legit assist rotation")
public class RotHitBox extends Module {

    public static RotHitBox getInstance() {
        return Instance.get(RotHitBox.class);
    }

    // Hitbox settings
    final SliderSetting hitboxWidth = new SliderSetting(this, "Hitbox Width", 0.6f, 0.3f, 2.0f, 0.05f);
    final SliderSetting hitboxHeight = new SliderSetting(this, "Hitbox Height", 1.8f, 1.0f, 3.0f, 0.1f);
    final SliderSetting lineWidth = new SliderSetting(this, "Line Width", 2.0f, 0.5f, 5.0f, 0.1f);
    final BooleanSetting filled = new BooleanSetting(this, "Filled", true);

    // Colors
    final ColorSetting normalColor = new ColorSetting(this, "Normal Color", -1);
    final SliderSetting normalAlpha = new SliderSetting(this, "Normal Alpha", 0.30f, 0.0f, 1.0f, 0.05f);
    final ColorSetting hoverColor = new ColorSetting(this, "Hover Color", 0xFFFF5555);
    final SliderSetting hoverAlpha = new SliderSetting(this, "Hover Alpha", 0.50f, 0.0f, 1.0f, 0.05f);

    // Rotation settings
    final SliderSetting yawSpeed = new SliderSetting(this, "Yaw Speed", 80.0f, 20.0f, 220.0f, 5.0f);
    final SliderSetting pitchSpeed = new SliderSetting(this, "Pitch Speed", 60.0f, 20.0f, 220.0f, 5.0f);
    final SliderSetting smoothness = new SliderSetting(this, "Smoothness", 0.65f, 0.25f, 0.95f, 0.05f);
    final SliderSetting rotationRange = new SliderSetting(this, "Rotation Range", 6.0f, 3.0f, 15.0f, 0.5f);
    final BooleanSetting autoReturn = new BooleanSetting(this, "Auto Return", true);
    final SliderSetting returnYawSpeed = new SliderSetting(this, "Return Yaw", 70.0f, 20.0f, 220.0f, 5.0f)
            .setVisible(autoReturn::getValue);
    final SliderSetting returnPitchSpeed = new SliderSetting(this, "Return Pitch", 55.0f, 20.0f, 220.0f, 5.0f)
            .setVisible(autoReturn::getValue);
    final SliderSetting returnSmoothness = new SliderSetting(this, "Return Smoothness", 0.70f, 0.25f, 0.95f, 0.05f)
            .setVisible(autoReturn::getValue);

    // Legitness + trajectory emulation
    final BooleanSetting legitMode = new BooleanSetting(this, "Legit Mode", true);
    final SliderSetting jitter = new SliderSetting(this, "Legit Jitter", 0.35f, 0.0f, 2.5f, 0.05f)
            .setVisible(legitMode::getValue);
    final SliderSetting accelLimit = new SliderSetting(this, "Legit Accel", 9.0f, 1.0f, 30.0f, 0.5f)
            .setVisible(legitMode::getValue);
    final BooleanSetting emulateTrajectory = new BooleanSetting(this, "Trajectory Emulation", true)
            .setVisible(legitMode::getValue);
    final SliderSetting trajectoryMemory = new SliderSetting(this, "Trajectory Memory", 0.55f, 0.0f, 1.0f, 0.05f)
            .setVisible(() -> legitMode.getValue() && emulateTrajectory.getValue());

    // Hit animation
    final ColorSetting hitColor = new ColorSetting(this, "Hit Anim Color", 0xFFFFCC44);
    final SliderSetting hitBlend = new SliderSetting(this, "Hit Anim Blend", 0.55f, 0.0f, 1.0f, 0.05f);
    final SliderSetting hitAnimTime = new SliderSetting(this, "Hit Anim Time", 180f, 60f, 600f, 10f);

    final Tessellator tessellator = Tessellator.getInstance();
    final BufferBuilder bufferBuilder = tessellator.getBuffer();

    // State
    LivingEntity hoveredTarget;
    LivingEntity previousTarget;
    float currentYaw;
    float currentPitch;
    int lastTargetId = -1;

    // trajectory memory
    float lastYawStep;
    float lastPitchStep;
    float avgYawVel;
    float avgPitchVel;

    // hit pulse
    LivingEntity hitTarget;
    long hitStartTime;
    float hitPulse;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) return;

        if (hitPulse > 0f) {
            float duration = Math.max(1f, hitAnimTime.getValue());
            float t = (System.currentTimeMillis() - hitStartTime) / duration;
            hitPulse = MathHelper.clamp(1.0f - t, 0.0f, 1.0f);
            if (hitPulse <= 0f) {
                hitTarget = null;
            }
        }

        if (hoveredTarget == null && previousTarget != null && autoReturn.getValue()) {
            applyReturnRotation();
            if (Math.abs(MathHelper.wrapDegrees(Rotation.cameraYaw() - currentYaw)) < 0.8f
                    && Math.abs(Rotation.cameraPitch() - currentPitch) < 0.8f) {
                previousTarget = null;
            }
        }
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (event == null || !(event.getTarget() instanceof LivingEntity entity)) return;
        hitTarget = entity;
        hitStartTime = System.currentTimeMillis();
        hitPulse = 1.0f;
    }

    private void applyReturnRotation() {
        float yawDiff = MathHelper.wrapDegrees(Rotation.cameraYaw() - currentYaw);
        float pitchDiff = Rotation.cameraPitch() - currentPitch;

        float lerpFactor = 1.0f - returnSmoothness.getValue();
        float yawStep = yawDiff * lerpFactor;
        float pitchStep = pitchDiff * lerpFactor;

        float maxYawStep = returnYawSpeed.getValue() / 20.0f;
        float maxPitchStep = returnPitchSpeed.getValue() / 20.0f;
        yawStep = MathHelper.clamp(yawStep, -maxYawStep, maxYawStep);
        pitchStep = MathHelper.clamp(pitchStep, -maxPitchStep, maxPitchStep);

        currentYaw += yawStep;
        currentPitch = MathHelper.clamp(currentPitch + pitchStep, -89.9f, 89.9f);

        RotationComponent.update(
                new Rotation(GCDUtils.getSensitivity(currentYaw), GCDUtils.getSensitivity(currentPitch)),
                returnYawSpeed.getValue(), returnPitchSpeed.getValue(),
                returnYawSpeed.getValue(), returnPitchSpeed.getValue(),
                0, 3, false
        );
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (mc.world == null || mc.player == null || mc.getRenderManager() == null) return;

        MatrixStack matrixStack = event.getMatrix();
        float partialTicks = event.getPartialTicks();
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();

        setupRender();

        LivingEntity newHoveredTarget = null;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive() || AntiBot.getInstance().isBot(player)) continue;

            Vector3d playerPos = RenderUtil3D.interpolate(player, partialTicks).subtract(cameraPos);
            float hw = hitboxWidth.getValue() / 2.0f;
            float hh = hitboxHeight.getValue();

            AxisAlignedBB customBox = new AxisAlignedBB(
                    playerPos.x - hw, playerPos.y, playerPos.z - hw,
                    playerPos.x + hw, playerPos.y + hh, playerPos.z + hw
            );

            boolean isHovered = isLookingAtBox(customBox, cameraPos);
            if (isHovered && mc.player.getDistance(player) <= rotationRange.getValue()) {
                newHoveredTarget = player;
            }

            int baseColor = isHovered ? hoverColor.getValue() : normalColor.getValue();
            float alpha = isHovered ? hoverAlpha.getValue() : normalAlpha.getValue();
            float pulse = (hitTarget != null && hitTarget == player) ? hitPulse * hitBlend.getValue() : 0f;
            int color = blendColor(baseColor, hitColor.getValue(), pulse);

            float red = ((color >> 16) & 0xFF) / 255.0F;
            float green = ((color >> 8) & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;

            matrixStack.push();
            matrixStack.translate(playerPos.x, playerPos.y, playerPos.z);

            if (filled.getValue()) {
                bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                RenderUtil3D.renderFilledBox(bufferBuilder, matrixStack, -hw, 0, -hw, hw, hh, hw, red, green, blue, alpha);
                tessellator.draw();
            }

            GL11.glLineWidth(lineWidth.getValue());
            bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            RenderUtil3D.renderWireframeBox(bufferBuilder, matrixStack, -hw, 0, -hw, hw, hh, hw, red, green, blue, 1.0f);
            tessellator.draw();

            matrixStack.pop();
        }

        releaseRender();

        if (newHoveredTarget != null) {
            if (hoveredTarget == null || newHoveredTarget.getEntityId() != lastTargetId) {
                // critical fix: reset start point on target switch to avoid snap to previous place
                currentYaw = Rotation.cameraYaw();
                currentPitch = Rotation.cameraPitch();
                lastYawStep = 0f;
                lastPitchStep = 0f;
                avgYawVel *= 0.35f;
                avgPitchVel *= 0.35f;
            }
            hoveredTarget = newHoveredTarget;
            previousTarget = newHoveredTarget;
            lastTargetId = newHoveredTarget.getEntityId();
            updateRotation();
        } else {
            hoveredTarget = null;
            lastTargetId = -1;
            lastYawStep = 0f;
            lastPitchStep = 0f;
        }
    }

    private boolean isLookingAtBox(AxisAlignedBB box, Vector3d cameraPos) {
        float yaw = Rotation.cameraYaw();
        float pitch = Rotation.cameraPitch();

        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        float cosYaw = MathHelper.cos(-yawRad - (float) Math.PI);
        float sinYaw = MathHelper.sin(-yawRad - (float) Math.PI);
        float cosPitch = -MathHelper.cos(-pitchRad);
        float sinPitch = MathHelper.sin(-pitchRad);

        Vector3d lookVec = new Vector3d(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
        Vector3d eyePos = mc.player.getEyePosition(1.0f).subtract(cameraPos);
        Vector3d endVec = eyePos.add(lookVec.scale(rotationRange.getValue()));
        return box.rayTrace(eyePos, endVec).isPresent();
    }

    private void updateRotation() {
        if (hoveredTarget == null || mc.player == null) return;
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        Vector3d targetPos = hoveredTarget.getPositionVec().add(0, hoveredTarget.getEyeHeight() * 0.5, 0);
        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        Vector3d delta = targetPos.subtract(eyePos);
        double dist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

        float targetYaw = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0f;
        float targetPitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(delta.y, dist)), -89.9f, 89.9f);

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDiff = targetPitch - currentPitch;

        float lerpFactor = 1.0f - smoothness.getValue();
        float yawStep = yawDiff * lerpFactor;
        float pitchStep = pitchDiff * lerpFactor;

        float yawAngle = Math.abs(yawDiff);
        float pitchAngle = Math.abs(pitchDiff);
        float yawSpeedMultiplier = yawAngle < 10 ? 0.6f : (yawAngle < 30 ? 0.8f : 1.0f);
        float pitchSpeedMultiplier = pitchAngle < 5 ? 0.6f : (pitchAngle < 15 ? 0.8f : 1.0f);

        float maxYawStep = (yawSpeed.getValue() / 20.0f) * yawSpeedMultiplier;
        float maxPitchStep = (pitchSpeed.getValue() / 20.0f) * pitchSpeedMultiplier;
        yawStep = MathHelper.clamp(yawStep, -maxYawStep, maxYawStep);
        pitchStep = MathHelper.clamp(pitchStep, -maxPitchStep, maxPitchStep);

        if (legitMode.getValue()) {
            if (emulateTrajectory.getValue()) {
                float mem = trajectoryMemory.getValue();
                if (Math.abs(avgYawVel) > 0.001f) {
                    yawStep = MathHelper.lerp(mem, yawStep, Math.copySign(Math.abs(avgYawVel), yawStep));
                }
                if (Math.abs(avgPitchVel) > 0.001f) {
                    pitchStep = MathHelper.lerp(mem, pitchStep, Math.copySign(Math.abs(avgPitchVel), pitchStep));
                }
            }

            float acc = accelLimit.getValue() / 20.0f;
            yawStep = lastYawStep + MathHelper.clamp(yawStep - lastYawStep, -acc, acc);
            pitchStep = lastPitchStep + MathHelper.clamp(pitchStep - lastPitchStep, -acc, acc);

            float jitterScale = jitter.getValue() / 20.0f;
            yawStep += rng.nextFloat(-jitterScale, jitterScale);
            pitchStep += rng.nextFloat(-jitterScale * 0.8f, jitterScale * 0.8f);
        } else {
            yawStep *= rng.nextFloat(0.98f, 1.02f);
            pitchStep *= rng.nextFloat(0.98f, 1.02f);
        }

        currentYaw += yawStep;
        currentPitch = MathHelper.clamp(currentPitch + pitchStep, -89.9f, 89.9f);

        lastYawStep = yawStep;
        lastPitchStep = pitchStep;
        avgYawVel = MathHelper.lerp(0.18f, avgYawVel, yawStep);
        avgPitchVel = MathHelper.lerp(0.18f, avgPitchVel, pitchStep);

        RotationComponent.update(
                new Rotation(GCDUtils.getSensitivity(currentYaw), GCDUtils.getSensitivity(currentPitch)),
                yawSpeed.getValue(), pitchSpeed.getValue(),
                yawSpeed.getValue(), pitchSpeed.getValue(),
                0, 3, false
        );
    }

    private int blendColor(int a, int b, float t) {
        t = MathHelper.clamp(t, 0f, 1f);
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int r = (int) (ar + (br - ar) * t);
        int g = (int) (ag + (bg - ag) * t);
        int bl = (int) (ab + (bb - ab) * t);
        return (r << 16) | (g << 8) | bl;
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

    @Override
    protected void onEnable() {
        super.onEnable();
        hoveredTarget = null;
        previousTarget = null;
        lastTargetId = -1;
        lastYawStep = 0f;
        lastPitchStep = 0f;
        avgYawVel = 0f;
        avgPitchVel = 0f;
        hitTarget = null;
        hitPulse = 0f;
        if (mc.player != null) {
            currentYaw = Rotation.cameraYaw();
            currentPitch = Rotation.cameraPitch();
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        hoveredTarget = null;
        previousTarget = null;
        lastTargetId = -1;
        lastYawStep = 0f;
        lastPitchStep = 0f;
        avgYawVel = 0f;
        avgPitchVel = 0f;
        hitTarget = null;
        hitPulse = 0f;
        RotationComponent.getInstance().smoothResetRotation();
    }
}
