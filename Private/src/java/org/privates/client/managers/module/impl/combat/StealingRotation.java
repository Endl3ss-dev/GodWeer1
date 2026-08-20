package org.privates.client.managers.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.Excellent;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.GCDUtils;

import java.util.*;


public class StealingRotation extends AbstractRotationElement {

    private enum BodyPart {
        HEAD(0.9),
        CHEST(0.6),
        LEGS(0.3);

        private final double heightModifier;

        BodyPart(double heightModifier) {
            this.heightModifier = heightModifier;
        }

        public Vector3d getOffset(LivingEntity entity) {
            return new Vector3d(0, entity.getHeight() * this.heightModifier, 0);
        }
    }

    private BodyPart lastHitBodyPart = BodyPart.CHEST;
    private Vector3d currentAimPoint = null;
    private boolean hasValidProfile = false;

    private float stolenSmoothness = 0.15f;
    private float stolenYawSpeed = 100f;
    private float stolenPitchSpeed = 80f;
    private float stolenYawAccel = 5000f;
    private float stolenYawJerk = 0f;
    private boolean isMultiPointDetected = false;

    private float momentumYaw = 0f;
    private float momentumPitch = 0f;

    private final List<Vector3d> multiPointOffsets = new ArrayList<>();
    private int multiPointIndex = 0;
    private long lastMultiPointSwitchTime = 0;
    private final long multiPointSwitchDelay = 600L;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null || mc.world == null) return currentLerpRotation;

        if (!hasValidProfile) {
            return currentLerpRotation;
        }

        detectHitLocation(mc);

        Vector3d targetPoint;
        long currentTime = System.currentTimeMillis();

        if (isMultiPointDetected) {
            targetPoint = getNextMultiPoint(target, currentTime);
        } else {
            targetPoint = target.getPositionVec().add(lastHitBodyPart.getOffset(target));
        }

        if (currentAimPoint == null) {
            currentAimPoint = targetPoint;
        }

        float transitionSpeed = 0.1f + stolenSmoothness * 0.5f;
        currentAimPoint = currentAimPoint.lerp(targetPoint, transitionSpeed);

        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d direction = currentAimPoint.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float jitterAmount = stolenYawJerk > 5000 ? 0.3f : (stolenYawJerk > 2000 ? 0.15f : 0f);
        if (jitterAmount > 0) {
            targetYaw += (float) (Math.sin(currentTime * 0.02) * jitterAmount);
            targetPitch += (float) (Math.cos(currentTime * 0.02) * jitterAmount * 0.5f);
        }

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        float accelerationFactor = Math.min(stolenYawAccel / 5000f, 2.0f);
        momentumYaw = MathHelper.lerp(0.3f, momentumYaw, yawDiff * 0.1f * accelerationFactor);
        momentumPitch = MathHelper.lerp(0.3f, momentumPitch, pitchDiff * 0.08f * accelerationFactor);

        float smoothedYaw = currentLerpRotation.x + (yawDiff * stolenSmoothness) + momentumYaw;
        float smoothedPitch = currentLerpRotation.y + (pitchDiff * stolenSmoothness) + momentumPitch;

        smoothedYaw = GCDUtils.getSensitivity(smoothedYaw);
        smoothedPitch = GCDUtils.getSensitivity(MathHelper.clamp(smoothedPitch, -89.9f, 89.9f));

        Vector2f newLerpRotation = new Vector2f(smoothedYaw, smoothedPitch);
        applyRotation(new Rotation(smoothedYaw, smoothedPitch), stolenYawSpeed, stolenPitchSpeed, 0, 4);
        parentModule.setLerpRotation(newLerpRotation);

        return newLerpRotation;
    }


    private void detectHitLocation(Minecraft mc) {
        for (PlayerEntity enemy : mc.world.getPlayers()) {
            if (enemy == mc.player || enemy.isSpectator()) continue;

            Vector3d enemyEyePos = enemy.getEyePosition(1.0f);
            Vector3d lookVec = enemy.getLook(1.0f);
            Vector3d rayEnd = enemyEyePos.add(lookVec.scale(8.0));

            Optional<Vector3d> hitVec = mc.player.getBoundingBox().rayTrace(enemyEyePos, rayEnd);

            if (hitVec.isPresent()) {
                double hitY = hitVec.get().y - mc.player.getPosY();
                double playerHeight = mc.player.getHeight();

                if (hitY > playerHeight * 0.75) {
                    lastHitBodyPart = BodyPart.HEAD;
                } else if (hitY > playerHeight * 0.4) {
                    lastHitBodyPart = BodyPart.CHEST;
                } else {
                    lastHitBodyPart = BodyPart.LEGS;
                }
                return;
            }
        }
    }


    private Vector3d getNextMultiPoint(LivingEntity target, long currentTime) {
        if (multiPointOffsets.isEmpty()) {
            initializeMultiPointTargets(target);
        }

        if (currentTime - lastMultiPointSwitchTime > multiPointSwitchDelay) {
            multiPointIndex = (multiPointIndex + 1) % multiPointOffsets.size();
            lastMultiPointSwitchTime = currentTime;
        }

        Vector3d offset = multiPointOffsets.get(multiPointIndex);
        return target.getPositionVec().add(offset);
    }


    private void initializeMultiPointTargets(LivingEntity target) {
        multiPointOffsets.clear();
        Random random = new Random();

        multiPointOffsets.add(new Vector3d(0, target.getHeight() * 0.9, 0));
        multiPointOffsets.add(new Vector3d(0, target.getHeight() * 0.65, 0));
        multiPointOffsets.add(new Vector3d(0, target.getHeight() * 0.45, 0));
        multiPointOffsets.add(new Vector3d(0, target.getHeight() * 0.2, 0));

        for (int i = 0; i < 4; i++) {
            Vector3d baseOffset = multiPointOffsets.get(i);
            double rX = (random.nextDouble() - 0.5) * 0.3;
            double rY = (random.nextDouble() - 0.5) * 0.3;
            double rZ = (random.nextDouble() - 0.5) * 0.3;
            multiPointOffsets.add(baseOffset.add(rX, rY, rZ));
        }

        Collections.shuffle(multiPointOffsets);
        multiPointIndex = 0;
    }


    private void loadProfileFromAuraVorovstvo() {
        AuraVorovstvo vorovstvo = Excellent.inst().moduleManager().get(AuraVorovstvo.class);

        if (vorovstvo == null) {
            ChatUtil.addText("§c[StealingRotation]§f AuraVorovstvo модуль не найден!");
            hasValidProfile = false;
            return;
        }

        AuraVorovstvo.RotationProfile profile = vorovstvo.getLastProfile();

        if (!profile.isValid()) {
            ChatUtil.addText("§c[StealingRotation]§f Изначально вам нужно включить функцию AuraVorovstvo и своровать профиль ротации у другого игрока (только если у него включена AuraNew)");
            hasValidProfile = false;
            return;
        }

        stolenSmoothness = MathHelper.clamp(profile.getSmoothness(), 0.05f, 0.5f);
        stolenYawSpeed = MathHelper.clamp(profile.getYawSpeed(), 50f, 200f);
        stolenPitchSpeed = MathHelper.clamp(profile.getPitchSpeed(), 40f, 180f);
        stolenYawAccel = profile.getYawAccel();
        stolenYawJerk = profile.getYawJerk();
        isMultiPointDetected = profile.getMultiPointCount() > 1;

        hasValidProfile = true;

        ChatUtil.addText("§a[StealingRotation]§f Профиль успешно загружен:");
        ChatUtil.addText(String.format("§f> Плавность: §c%.3f", stolenSmoothness));
        ChatUtil.addText(String.format("§f> Скорость Yaw: §c%.2f", stolenYawSpeed));
        ChatUtil.addText(String.format("§f> Скорость Pitch: §c%.2f", stolenPitchSpeed));
        ChatUtil.addText(String.format("§f> Ускорение: §c%.0f", stolenYawAccel));
        ChatUtil.addText(String.format("§f> Мультипоинт: §c%s", isMultiPointDetected ? "Да" : "Нет"));
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        lastHitBodyPart = BodyPart.CHEST;
        currentAimPoint = null;
        momentumYaw = 0f;
        momentumPitch = 0f;
        multiPointOffsets.clear();
        multiPointIndex = 0;
        lastMultiPointSwitchTime = 0;

        loadProfileFromAuraVorovstvo();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        super.onDisable(parentModule);
        currentAimPoint = null;
        multiPointOffsets.clear();
        hasValidProfile = false;
    }
}