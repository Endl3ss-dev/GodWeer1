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

public class HWRot extends AbstractRotationElement {

    // Simple smooth rotation variables
    private float baseYawSpeed = 120.0f;
    private float basePitchSpeed = 90.0f;
    private float smoothness = 0.4f;

    // FPS adaptation
    private long lastUpdateTime = 0;
    private float fpsMultiplier = 1.0f;

    // Legit micro-adjustments
    private float microJitterYaw = 0;
    private float microJitterPitch = 0;
    private long lastJitterUpdate = 0;

    // Smooth interpolation
    private float currentYaw = 0;
    private float currentPitch = 0;

    @Override
    @Native
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null) {
            return currentLerpRotation;
        }

        long currentTime = System.currentTimeMillis();
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        // FPS adaptation - calculate time delta
        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
            currentYaw = currentLerpRotation.x;
            currentPitch = currentLerpRotation.y;
        }

        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f; // Convert to seconds
        lastUpdateTime = currentTime;

        // Adapt to FPS (clamp to reasonable values)
        fpsMultiplier = MathHelper.clamp(deltaTime * 60.0f, 0.5f, 2.0f);

        // Calculate target rotation
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d targetPos = target.getPositionVec().add(0, target.getEyeHeight() * 0.5, 0);
        Vector3d delta = targetPos.subtract(playerEyes);
        double dist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

        float targetYaw = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(delta.y, dist));
        targetPitch = MathHelper.clamp(targetPitch, -89.9f, 89.9f);

        // Add micro jitter for legit look (update every 50-100ms)
        if (currentTime - lastJitterUpdate > 50 + rng.nextInt(50)) {
            microJitterYaw = rng.nextFloat(-0.3f, 0.3f);
            microJitterPitch = rng.nextFloat(-0.2f, 0.2f);
            lastJitterUpdate = currentTime;
        }

        targetYaw += microJitterYaw;
        targetPitch += microJitterPitch;

        // Calculate rotation difference
        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDiff = targetPitch - currentPitch;

        // Calculate angle distance for speed adaptation
        float angleDistance = (float) Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);

        // Adaptive speed based on angle distance (slower for small angles, faster for large)
        float speedMultiplier = 1.0f;
        if (angleDistance < 5.0f) {
            speedMultiplier = 0.6f; // Slow down for precision
        } else if (angleDistance > 30.0f) {
            speedMultiplier = 1.3f; // Speed up for large angles
        }

        // Calculate step size with smoothness and FPS adaptation
        float yawSpeed = baseYawSpeed * speedMultiplier * fpsMultiplier;
        float pitchSpeed = basePitchSpeed * speedMultiplier * fpsMultiplier;

        // Apply smoothness (lerp factor)
        float lerpFactor = 1.0f - smoothness;
        float yawStep = yawDiff * lerpFactor;
        float pitchStep = pitchDiff * lerpFactor;

        // Clamp step by max speed per frame
        float maxYawStep = yawSpeed / 20.0f; // Divide by 20 for per-tick speed
        float maxPitchStep = pitchSpeed / 20.0f;

        yawStep = MathHelper.clamp(yawStep, -maxYawStep, maxYawStep);
        pitchStep = MathHelper.clamp(pitchStep, -maxPitchStep, maxPitchStep);

        // Add small random variation to steps for human-like movement
        yawStep *= rng.nextFloat(0.95f, 1.05f);
        pitchStep *= rng.nextFloat(0.95f, 1.05f);

        // Update current rotation
        currentYaw += yawStep;
        currentPitch += pitchStep;
        currentPitch = MathHelper.clamp(currentPitch, -89.9f, 89.9f);

        // Apply GCD sensitivity
        float finalYaw = GCDUtils.getSensitivity(currentYaw);
        float finalPitch = GCDUtils.getSensitivity(currentPitch);

        Vector2f newRotation = new Vector2f(finalYaw, finalPitch);

        // Apply rotation with moderate speeds
        applyRotation(new Rotation(finalYaw, finalPitch),
                (int) (baseYawSpeed * speedMultiplier),
                (int) (basePitchSpeed * speedMultiplier),
                0, 3);

        parentModule.setLerpRotation(newRotation);

        return newRotation;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        // Randomize base speeds slightly for variation
        baseYawSpeed = 110.0f + rng.nextFloat(-10.0f, 10.0f);
        basePitchSpeed = 80.0f + rng.nextFloat(-10.0f, 10.0f);
        smoothness = 0.35f + rng.nextFloat(-0.05f, 0.05f);

        // Reset state
        lastUpdateTime = 0;
        fpsMultiplier = 1.0f;
        microJitterYaw = 0;
        microJitterPitch = 0;
        lastJitterUpdate = 0;

        if (Minecraft.getInstance().player != null) {
            currentYaw = Minecraft.getInstance().player.rotationYaw;
            currentPitch = Minecraft.getInstance().player.rotationPitch;
        }
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        lastUpdateTime = 0;
    }
}
