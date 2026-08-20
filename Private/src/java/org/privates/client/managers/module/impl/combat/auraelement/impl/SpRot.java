package org.privates.client.managers.module.impl.combat.auraelement.impl;

import com.adl.nativeprotect.Native;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.other.GCDUtils;

import java.util.concurrent.ThreadLocalRandom;

public class SpRot extends AbstractRotationElement {

    private long patternChangeTime = 0;
    private float yawSpeed, pitchSpeed, smoothness, jitterIntensity;
    private float multipointOffsetX, multipointOffsetY;
    private float spiralPhase = 0f, jitterPhase = 0f;
    private Vector3d lastTargetPos = null;
    private long lastMovementTime = 0;
    private float lastYaw, lastPitch;
    private boolean isAfk = false;
    private long afkStartTime = 0;

    @Override
    @Native
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) return currentLerpRotation;

        long currentTime = System.currentTimeMillis();
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        if (currentTime - patternChangeTime > 3000) {
            randomizePattern();
            patternChangeTime = currentTime;
        }

        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d targetPos = target.getPositionVec();
        double dx = targetPos.x - playerPos.x;
        double dy = targetPos.y - playerPos.y;
        double dz = targetPos.z - playerPos.z;
        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        double distanceY = Math.abs(dy);

        if (distanceXZ <= 0.001 && distanceY <= 000001) return currentLerpRotation;

        checkAfkStatus(mc, currentTime);

        if (isAfk) {
            float afkYaw = currentLerpRotation.x + (float) (Math.sin(currentTime / 5000.0) * 0.3);
            float afkPitch = currentLerpRotation.y + (float) (Math.cos(currentTime / 4500.0) * 0.15);
            return new Vector2f(GCDUtils.getSensitivity(afkYaw), GCDUtils.getSensitivity(MathHelper.clamp(afkPitch, -89.9f, 89.9f)));
        }

        float targetSpeedFactor = getTargetSpeedFactor(target);
        float fallFactor = getFallFactor(mc);
        float combatIntensity = getCombatIntensity(mc, target);

        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d baseTarget = target.getPositionVec().add(multipointOffsetX, target.getHeight() * (0.5 + multipointOffsetY), 0);

        spiralPhase += 0.12f + rng.nextFloat(-0.02f, 0.02f);
        jitterPhase += 0.25f + rng.nextFloat(-0.05f, 0.05f);

        float jitterX = (float) Math.sin(jitterPhase) * jitterIntensity * 0.08f * targetSpeedFactor;
        float jitterY = (float) Math.cos(jitterPhase * 1.3) * jitterIntensity * 0.06f * targetSpeedFactor;
        float jitterZ = (float) Math.sin(jitterPhase * 0.7) * jitterIntensity * 0.08f * targetSpeedFactor;

        Vector3d finalTarget = baseTarget.add(jitterX, jitterY, jitterZ);
        Vector3d direction = finalTarget.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        float absPitchDiff = Math.abs(pitchDiff);
        if (absPitchDiff < 15) pitchDiff *= 0.3f;
        else if (absPitchDiff < 25) pitchDiff *= 0.6f;

        float dynamicYawSpeed = yawSpeed * (1 + targetSpeedFactor * 0.4f) * (1 + combatIntensity * 0.3f);
        float dynamicPitchSpeed = pitchSpeed * (1 + fallFactor * 0.2f) * (1 + combatIntensity * 0.2f);

        float yawStep = Math.min(Math.abs(yawDiff), dynamicYawSpeed) * Math.signum(yawDiff);
        float pitchStep = Math.min(Math.abs(pitchDiff), dynamicPitchSpeed) * Math.signum(pitchDiff);

        float newYaw = currentLerpRotation.x + yawStep * smoothness;
        float newPitch = currentLerpRotation.y + pitchStep * (smoothness * 0.7f);

        if (mc.player.isElytraFlying()) {
            newYaw = currentLerpRotation.x + yawStep * 0.4f;
            newPitch = currentLerpRotation.y + pitchStep * 0.3f;
        }

        if (target.hurtTime > 0) {
            float hurtJitter = rng.nextFloat(-1.2f, 1.2f);
            newYaw += hurtJitter * (1 - smoothness);
            newPitch += hurtJitter * 0.6f * (1 - smoothness);
        }

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -89.9f, 89.9f));

        Vector2f result = new Vector2f(newYaw, newPitch);
        applyRotation(new Rotation(newYaw, newPitch),
                180 + rng.nextInt(0, 40),
                90 + rng.nextInt(0, 20),
                0, 5);
        parentModule.setLerpRotation(result);

        lastTargetPos = targetPos;
        lastYaw = newYaw;
        lastPitch = newPitch;

        return result;
    }

    private void randomizePattern() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        yawSpeed = 15f + rng.nextFloat(-3f, 3f);
        pitchSpeed = 8f + rng.nextFloat(-2f, 2f);
        smoothness = 0.75f + rng.nextFloat(-0.1f, 0.1f);
        jitterIntensity = 0.6f + rng.nextFloat(-0.15f, 0.15f);
        multipointOffsetX = rng.nextFloat(-0.15f, 0.15f);
        multipointOffsetY = rng.nextFloat(-0.1f, 0.1f);
    }

    private float getTargetSpeedFactor(LivingEntity target) {
        if (lastTargetPos == null) return 1.0f;
        double dist = target.getPositionVec().distanceTo(lastTargetPos);
        return MathHelper.clamp((float) (dist * 20), 0.8f, 2.5f);
    }

    private float getFallFactor(Minecraft mc) {
        if (!mc.player.isOnGround() && mc.player.getMotion().y < -0.3) return 1.4f;
        if (mc.player.fallDistance > 3) return 1.6f;
        return 1.0f;
    }

    private float getCombatIntensity(Minecraft mc, LivingEntity target) {
        float distance = mc.player.getDistance(target);
        float health = mc.player.getHealth() / mc.player.getMaxHealth();
        float intensity = 1.0f;
        if (distance < 2.5f) intensity += 0.3f;
        if (health < 0.5f) intensity += 0.4f;
        if (target.hurtTime > 0) intensity += 0.2f;
        return Math.min(intensity, 2.0f);
    }

    private void checkAfkStatus(Minecraft mc, long currentTime) {
        boolean moving = Math.abs(mc.player.getMotion().x) > 0.001 || Math.abs(mc.player.getMotion().z) > 0.001;
        boolean rotating = Math.abs(lastYaw - mc.player.rotationYaw) > 0.1f || Math.abs(lastPitch - mc.player.rotationPitch) > 0.1f;

        if (moving || rotating) {
            lastMovementTime = currentTime;
            isAfk = false;
        } else if (currentTime - lastMovementTime > 8000) {
            if (!isAfk) afkStartTime = currentTime;
            isAfk = true;
        }
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        patternChangeTime = 0;
        lastTargetPos = null;
        lastMovementTime = System.currentTimeMillis();
        isAfk = false;
        spiralPhase = 0f;
        jitterPhase = 0f;
        randomizePattern();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        lastTargetPos = null;
        isAfk = false;
    }
}