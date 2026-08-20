package org.privates.client.managers.module.impl.combat.auraelement.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.NeuroSelf;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.other.GCDUtils;

import java.util.List;

public class NeuroSelfRot extends AbstractRotationElement {

    private int playbackIndex;
    private long lastUpdateTime;
    private float timeAccumulatorMs;
    private float replayYaw;
    private float replayPitch;
    private boolean replayInited;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        return getRotation(target, parentModule);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        super.onEnable(parentModule);
        reset();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        super.onDisable(parentModule);
        reset();
    }

    public Vector2f getRotation(LivingEntity target, AuraNew parentModule) {
        if (mc.player == null) {
            return new Vector2f(0.0f, 0.0f);
        }

        Vector2f current = parentModule.getLerpRotation() != null
                ? parentModule.getLerpRotation()
                : new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);

        if (target == null) {
            return current;
        }

        if (!replayInited) {
            replayYaw = current.x;
            replayPitch = current.y;
            replayInited = true;
            lastUpdateTime = System.currentTimeMillis();
        }

        NeuroSelf neuroSelf = NeuroSelf.getInstance();
        NeuroSelf.NeuroRotationProfile profile = neuroSelf != null ? neuroSelf.getGeneratedProfile() : null;
        if (profile == null || !profile.isValid() || profile.getSamples().isEmpty()) {
            Vector2f fallback = calculateSmartRotation(target, parentModule);
            float yStep = MathHelper.clamp(MathHelper.wrapDegrees(fallback.x - current.x), -8.0f, 8.0f);
            float pStep = MathHelper.clamp(fallback.y - current.y, -5.0f, 5.0f);
            float yaw = GCDUtils.getSensitivity(current.x + yStep);
            float pitch = GCDUtils.getSensitivity(MathHelper.clamp(current.y + pStep, -89.9f, 89.9f));
            applyRotation(new Rotation(yaw, pitch), 120, 90, 0, 2);
            parentModule.setLerpRotation(new Vector2f(yaw, pitch));
            return new Vector2f(yaw, pitch);
        }

        List<NeuroSelf.NeuroSample> samples = profile.getSamples();
        long now = System.currentTimeMillis();
        long dt = Math.max(1L, now - lastUpdateTime);
        lastUpdateTime = now;
        timeAccumulatorMs += dt;

        int applied = 0;
        while (applied < 8) {
            NeuroSelf.NeuroSample sample = samples.get(playbackIndex);
            int sampleDt = Math.max(1, sample.getDeltaMs());
            if (timeAccumulatorMs < sampleDt) break;

            applyReplaySample(sample, target);
            timeAccumulatorMs -= sampleDt;
            playbackIndex = (playbackIndex + 1) % samples.size();
            applied++;
        }

        if (applied == 0) {
            // keep tiny convergence to avoid drift when frame pacing is unstable
            NeuroSelf.NeuroSample sample = samples.get(playbackIndex);
            Vector2f vec = getVectorRotation(target);
            float desiredYaw = vec.x - sample.getYawError();
            float desiredPitch = vec.y - sample.getPitchError();
            replayYaw += MathHelper.wrapDegrees(desiredYaw - replayYaw) * 0.04f;
            replayPitch += (desiredPitch - replayPitch) * 0.04f;
            replayPitch = MathHelper.clamp(replayPitch, -89.9f, 89.9f);
        }

        float finalYaw = GCDUtils.getSensitivity(replayYaw);
        float finalPitch = GCDUtils.getSensitivity(replayPitch);

        float maxYawStep = Math.max(2.0f, profile.getMaxYawStep());
        float maxPitchStep = Math.max(1.5f, profile.getMaxPitchStep());

        applyRotation(
                new Rotation(finalYaw, finalPitch),
                Math.max(90, (int) (maxYawStep * 14.0f)),
                Math.max(70, (int) (maxPitchStep * 14.0f)),
                0,
                2
        );

        Vector2f out = new Vector2f(finalYaw, finalPitch);
        parentModule.setLerpRotation(out);
        return out;
    }

    private void reset() {
        playbackIndex = 0;
        lastUpdateTime = 0L;
        timeAccumulatorMs = 0.0f;
        replayYaw = 0.0f;
        replayPitch = 0.0f;
        replayInited = false;
    }

    private Vector2f getVectorRotation(LivingEntity target) {
        Vector3d eye = mc.player.getEyePosition(1.0F);
        Vector3d aim = target.getPositionVec().add(0.0, target.getEyeHeight() * 0.85, 0.0);
        Vector3d dir = aim.subtract(eye);
        float yaw = (float) Math.toDegrees(Math.atan2(dir.z, dir.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z)));
        return new Vector2f(yaw, pitch);
    }

    private void applyReplaySample(NeuroSelf.NeuroSample sample, LivingEntity target) {
        Vector2f vector = getVectorRotation(target);

        // Exact recorded hand movement step
        replayYaw += sample.getDYaw();
        replayPitch += sample.getDPitch();

        // Keep recorded offset-to-vector (so replay stays "on vector" but with your personal motion)
        float desiredYaw = vector.x - sample.getYawError();
        float desiredPitch = vector.y - sample.getPitchError();

        replayYaw += MathHelper.wrapDegrees(desiredYaw - replayYaw) * 0.10f;
        replayPitch += (desiredPitch - replayPitch) * 0.10f;
        replayPitch = MathHelper.clamp(replayPitch, -89.9f, 89.9f);
    }
}
