package org.privates.client.managers.module.impl.combat;

import com.mojang.authlib.GameProfile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;

import java.util.*;


@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CreateRotation",
        category = Category.COMBAT,
        description = "Records your aim movements and creates AuraNew rotation profile.")
public class CreateRotation extends Module {

    @Getter
    public static class RotationProfile {
        private final float smoothness;
        private final float yawSpeed;
        private final float pitchSpeed;
        private final float yawAccel;
        private final int pauses;
        private final boolean isValid;
        private final String rotationType;
        private final int snapFocusTicks;
        private final float randomVariance;
        private final float jitterIntensity;
        private final int multiPointCount;
        private final List<String> bodyParts;
        private final float microCorrection;
        private final float aimSmoothness;

        public RotationProfile(float smoothness, float yawSpeed, float pitchSpeed, float yawAccel,
                               int pauses, String rotationType, int snapFocusTicks, float randomVariance,
                               float jitterIntensity, int multiPointCount, List<String> bodyParts,
                               float microCorrection, float aimSmoothness) {
            this.smoothness = smoothness;
            this.yawSpeed = yawSpeed;
            this.pitchSpeed = pitchSpeed;
            this.yawAccel = yawAccel;
            this.pauses = pauses;
            this.isValid = true;
            this.rotationType = rotationType;
            this.snapFocusTicks = snapFocusTicks;
            this.randomVariance = randomVariance;
            this.jitterIntensity = jitterIntensity;
            this.multiPointCount = multiPointCount;
            this.bodyParts = bodyParts;
            this.microCorrection = microCorrection;
            this.aimSmoothness = aimSmoothness;
        }

        public RotationProfile() {
            this.smoothness = 0.8f;
            this.yawSpeed = 100f;
            this.pitchSpeed = 80f;
            this.yawAccel = 5000f;
            this.pauses = 0;
            this.isValid = false;
            this.rotationType = "Unknown";
            this.snapFocusTicks = 0;
            this.randomVariance = 0.4f;
            this.jitterIntensity = 0f;
            this.multiPointCount = 0;
            this.bodyParts = new ArrayList<>();
            this.microCorrection = 0f;
            this.aimSmoothness = 0.8f;
        }

        public float[] applyRotation(float currentYaw, float currentPitch, float targetYaw, float targetPitch) {
            float deltaYaw = targetYaw - currentYaw;
            float deltaPitch = targetPitch - currentPitch;

            while (deltaYaw > 180) deltaYaw -= 360;
            while (deltaYaw < -180) deltaYaw += 360;

            float moveYaw = deltaYaw * smoothness;
            float movePitch = deltaPitch * smoothness;

            float maxYawMove = yawSpeed * (1 + (float) Math.random() * randomVariance);
            float maxPitchMove = pitchSpeed * (1 + (float) Math.random() * randomVariance);

            if (Math.abs(moveYaw) > maxYawMove) {
                moveYaw = Math.signum(moveYaw) * maxYawMove;
            }
            if (Math.abs(movePitch) > maxPitchMove) {
                movePitch = Math.signum(movePitch) * maxPitchMove;
            }

            if (jitterIntensity > 0) {
                moveYaw += (Math.random() - 0.5) * jitterIntensity * 2;
                movePitch += (Math.random() - 0.5) * jitterIntensity;
            }

            if (microCorrection > 0 && Math.abs(deltaYaw) < 5) {
                moveYaw += (Math.random() - 0.5) * microCorrection;
            }

            float newYaw = currentYaw + moveYaw;
            float newPitch = Math.max(-90, Math.min(90, currentPitch + movePitch));

            return new float[]{newYaw, newPitch};
        }
    }

    final BooleanSetting autoSpawn = new BooleanSetting(this, "Auto Spawn FakePlayer", true);
    final SliderSetting recordTime = new SliderSetting(this, "Record Time (sec)", 15.0F, 10.0F, 45.0F, 1.0F);
    final SliderSetting randomVariance = new SliderSetting(this, "Random Variance %", 40.0F, 0.0F, 40.0F, 1.0F);
    final SliderSetting fakePlayerDistance = new SliderSetting(this, "FakePlayer Distance", 3.0F, 2.0F, 6.0F, 0.5F);

    RemoteClientPlayerEntity fakePlayer;
    long recordStartTime;
    final Deque<RotationData> rotationHistory = new ArrayDeque<>();
    boolean isRecordComplete = false;
    private RotationProfile generatedProfile = new RotationProfile();
    private int activeTicks = 0;
    private int totalTicks = 0;
    private float prevYaw = 0;
    private float prevPitch = 0;

    public RotationProfile getGeneratedProfile() {
        return generatedProfile;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        reset();
        if (autoSpawn.getValue()) {
            spawnFakePlayer();
            ChatUtil.addText("§a[CreateRotation]§f FakePlayer spawned. Aim at it to record your movements.");
        }
    }

    public static CreateRotation getInstance() {
        return Instance.get(CreateRotation.class);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (!rotationHistory.isEmpty() && isRecordComplete) {
            generateRotationProfile();
        }
        if (fakePlayer != null) {
            removeFakePlayer();
        }
        reset();
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            reset();
            return;
        }
        if (isRecordComplete) return;

        if (autoSpawn.getValue() && fakePlayer != null) {
            if (recordStartTime == 0) {
                recordStartTime = System.currentTimeMillis();
            }
            recordRotationData();
        }
    }

    @EventHandler
    public void onEvent(Render2DEvent event) {
        if (recordStartTime > 0 && !isRecordComplete) {
            long elapsed = System.currentTimeMillis() - recordStartTime;
            double totalDuration = recordTime.getValue() * 1000.0;
            double progress = Math.min(elapsed / totalDuration, 1.0);

            String text = String.format("Recording Aim: %.0f%%", progress * 100);
            MatrixStack matrix = event.getMatrix();
            FontRenderer font = mc.fontRenderer;
            float x = (mw.getScaledWidth() - font.getStringWidth(text)) / 2f;
            float y = mw.getScaledHeight() / 2f + 20f;
            font.drawStringWithShadow(matrix, text, x, y, ColorUtil.WHITE);
        }
    }

    private void spawnFakePlayer() {
        UUID uuid = UUID.nameUUIDFromBytes("AimTrainingTarget".getBytes());
        fakePlayer = new RemoteClientPlayerEntity(mc.world, new GameProfile(uuid, "Target"));

        double distance = fakePlayerDistance.getValue();
        double angle = Math.toRadians(mc.player.rotationYaw);
        double offsetX = -Math.sin(angle) * distance;
        double offsetZ = Math.cos(angle) * distance;

        fakePlayer.setPosition(
                mc.player.getPosX() + offsetX,
                mc.player.getPosY(),
                mc.player.getPosZ() + offsetZ
        );

        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        fakePlayer.renderYawOffset = mc.player.renderYawOffset;
        fakePlayer.rotationPitchHead = mc.player.rotationPitchHead;
        fakePlayer.container = mc.player.container;
        fakePlayer.inventory = mc.player.inventory;
        fakePlayer.setHealth(20.0f);
        mc.world.addEntity(1339, fakePlayer);
    }

    private void removeFakePlayer() {
        if (fakePlayer != null) {
            mc.world.removeEntityFromWorld(1339);
            fakePlayer = null;
        }
    }

    private void recordRotationData() {
        long elapsed = System.currentTimeMillis() - recordStartTime;
        if (elapsed > recordTime.getValue() * 1000) {
            ChatUtil.addText("§a[CreateRotation]§f Recording complete. Disable module to generate profile.");
            isRecordComplete = true;
            return;
        }

        totalTicks++;
        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float yawDelta = Math.abs(currentYaw - prevYaw);
        float pitchDelta = Math.abs(currentPitch - prevPitch);

        if (yawDelta > 0.5f || pitchDelta > 0.5f) {
            activeTicks++;
        }

        prevYaw = currentYaw;
        prevPitch = currentPitch;

        rotationHistory.add(new RotationData(
                System.currentTimeMillis(),
                currentYaw,
                currentPitch,
                yawDelta,
                pitchDelta
        ));
    }

    private void generateRotationProfile() {
        List<Float> yawSpeeds = new ArrayList<>();
        List<Float> pitchSpeeds = new ArrayList<>();
        List<Float> accelerations = new ArrayList<>();
        List<Float> smoothnessValues = new ArrayList<>();
        List<Float> jitters = new ArrayList<>();
        List<Float> microMoves = new ArrayList<>();
        List<Float> pitchAngles = new ArrayList<>();

        int pauseCount = 0;
        int snapCount = 0;
        int rapidChanges = 0;

        RotationData prev = null;
        RotationData prev2 = null;

        for (RotationData current : rotationHistory) {
            if (prev != null) {
                long dt = current.timestamp - prev.timestamp;
                if (dt > 0 && dt < 100) {
                    float yawSpeed = Math.abs((current.yaw - prev.yaw) / dt * 1000f);
                    float pitchSpeed = Math.abs((current.pitch - prev.pitch) / dt * 1000f);

                    if (yawSpeed > 1.0f) yawSpeeds.add(yawSpeed);
                    if (pitchSpeed > 1.0f) pitchSpeeds.add(pitchSpeed);

                    pitchAngles.add(current.pitch);

                    if (yawSpeed < 1.0f && pitchSpeed < 1.0f) {
                        pauseCount++;
                    }

                    if (yawSpeed > 200f) {
                        snapCount++;
                    }

                    if (yawSpeed > 5f && yawSpeed < 30f) {
                        jitters.add(yawSpeed);
                    }

                    if (yawSpeed > 1f && yawSpeed < 10f) {
                        microMoves.add(yawSpeed);
                    }

                    if (prev2 != null) {
                        long dt2 = prev.timestamp - prev2.timestamp;
                        if (dt2 > 0 && dt2 < 100) {
                            float prevYawSpeed = Math.abs((prev.yaw - prev2.yaw) / dt2 * 1000f);
                            float accel = Math.abs((yawSpeed - prevYawSpeed) / dt);
                            accelerations.add(accel);

                            if (prevYawSpeed > 1.0f && yawSpeed > 1.0f) {
                                float smooth = Math.min(prevYawSpeed, yawSpeed) / Math.max(prevYawSpeed, yawSpeed);
                                smoothnessValues.add(smooth);
                            }

                            if (Math.abs(yawSpeed - prevYawSpeed) > 20f) {
                                rapidChanges++;
                            }
                        }
                    }
                }
            }
            prev2 = prev;
            prev = current;
        }

        float avgYawSpeed = (float) yawSpeeds.stream().mapToDouble(f -> f).average().orElse(100);
        float avgPitchSpeed = (float) pitchSpeeds.stream().mapToDouble(f -> f).average().orElse(80);
        float avgSmoothness = (float) smoothnessValues.stream().mapToDouble(f -> f).average().orElse(0.8);
        float avgAccel = (float) accelerations.stream().mapToDouble(f -> f).average().orElse(5000);
        float avgJitter = (float) jitters.stream().mapToDouble(f -> f).average().orElse(0);
        float avgMicro = (float) microMoves.stream().mapToDouble(f -> f).average().orElse(0);

        float activeRatio = (float) activeTicks / totalTicks;
        String rotationType;
        int snapTicks;

        if (snapCount > 5 && activeRatio < 0.3f) {
            rotationType = "Snap";
            snapTicks = Math.min(3, (int) ((float) activeTicks / Math.max(snapCount, 1)));
        } else if (activeRatio > 0.7f) {
            rotationType = "Focus";
            snapTicks = 0;
        } else {
            rotationType = "Hybrid";
            snapTicks = 2;
        }

        Collections.sort(pitchAngles);
        List<String> targetParts = new ArrayList<>();
        if (!pitchAngles.isEmpty()) {
            float lastPitch = -999;
            for (float pitch : pitchAngles) {
                if (Math.abs(pitch - lastPitch) > 10.0f) {
                    lastPitch = pitch;
                    if (pitch < -20) targetParts.add("Head");
                    else if (pitch < 15) targetParts.add("Body");
                    else targetParts.add("Legs");
                }
            }
        }

        int multiPoint = (int) targetParts.stream().distinct().count();
        String multiPointInfo = multiPoint > 1
                ? "Yes (" + String.join(", ", targetParts.stream().distinct().toArray(String[]::new)) + ")"
                : "No";

        float jitterIntensity = rapidChanges > 10 ? avgJitter / 10f : 0f;
        float variance = randomVariance.getValue() / 100f;
        float microCorrection = avgMicro / 5f;
        float aimSmooth = avgSmoothness * 0.85f;

        ChatUtil.addText("§f[CreateRotation] §aRotation Profile Generated:");
        ChatUtil.addText(String.format("§f> Type: §c%s §f| Snap Ticks: §c%d", rotationType, snapTicks));
        ChatUtil.addText(String.format("§f> Yaw Speed: §c%.2f §f| Pitch Speed: §c%.2f", avgYawSpeed, avgPitchSpeed));
        ChatUtil.addText(String.format("§f> Smoothness: §c%.3f §f| Aim Smooth: §c%.3f", avgSmoothness, aimSmooth));
        ChatUtil.addText(String.format("§f> Acceleration: §c%.0f", avgAccel));
        ChatUtil.addText(String.format("§f> Jitter: §c%.2f §f| Micro: §c%.2f", jitterIntensity, microCorrection));
        ChatUtil.addText(String.format("§f> Variance: §c%.0f%% §f| Pauses: §c%d", variance * 100, pauseCount));
        ChatUtil.addText("§f> MultiPoint: §c" + multiPointInfo);
        ChatUtil.addText("§f§l> Profile ready for AuraNew integration!");

        generatedProfile = new RotationProfile(
                avgSmoothness,
                avgYawSpeed,
                avgPitchSpeed,
                avgAccel,
                pauseCount,
                rotationType,
                snapTicks,
                variance,
                jitterIntensity,
                multiPoint,
                targetParts.stream().distinct().toList(),
                microCorrection,
                aimSmooth
        );

        System.out.printf(
                "PROFILE | Type: %s | Snap: %d | Smooth: %.3f | YawSpd: %.2f | PitchSpd: %.2f | Accel: %.0f | Jitter: %.2f | Variance: %.2f | Multi: %d%n",
                rotationType, snapTicks, avgSmoothness, avgYawSpeed, avgPitchSpeed, avgAccel, jitterIntensity, variance, multiPoint
        );
    }

    private void reset() {
        recordStartTime = 0;
        rotationHistory.clear();
        isRecordComplete = false;
        activeTicks = 0;
        totalTicks = 0;
        prevYaw = 0;
        prevPitch = 0;
    }

    private static class RotationData {
        final long timestamp;
        final float yaw;
        final float pitch;
        final float yawDelta;
        final float pitchDelta;

        RotationData(long timestamp, float yaw, float pitch, float yawDelta, float pitchDelta) {
            this.timestamp = timestamp;
            this.yaw = yaw;
            this.pitch = pitch;
            this.yawDelta = yawDelta;
            this.pitchDelta = pitchDelta;
        }
    }
}