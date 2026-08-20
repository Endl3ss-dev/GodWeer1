package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.render.color.ColorUtil;

import java.util.*;
import java.util.stream.Collectors;


@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AuraVorovstvo",
        category = Category.COMBAT,
        description = "Анализирует и копирует стиль ротаций противника.")
public class AuraVorovstvo extends Module {

    @Getter
    public static class RotationProfile {
        private final float smoothness;
        private final float yawSpeed;
        private final float pitchSpeed;
        private final float yawAccel;
        private final float yawJerk;
        private final int pauses;
        private final int multiPointCount;
        private final boolean isValid;
        private final String rotationType;
        private final int snapFocusTicks;
        private final float yawSpeedMin;
        private final float yawSpeedMax;
        private final float pitchSpeedMin;
        private final float pitchSpeedMax;
        private final float smoothnessVariance;
        private final float aimPointBlendSpeed;
        private final float microMovementIntensity;
        private final float transitionSmoothness;
        private final float momentumDecay;
        private final float predictionStrength;
        private final boolean hasPerlinNoise;
        private final float perlinInfluence;
        private final float verticalBias;
        private final float horizontalBias;

        public RotationProfile(float smoothness, float yawSpeed, float pitchSpeed, float yawAccel, float yawJerk,
                               int pauses, int multiPointCount, String rotationType, int snapFocusTicks,
                               float yawSpeedMin, float yawSpeedMax, float pitchSpeedMin, float pitchSpeedMax,
                               float smoothnessVariance, float aimPointBlendSpeed, float microMovementIntensity,
                               float transitionSmoothness, float momentumDecay, float predictionStrength,
                               boolean hasPerlinNoise, float perlinInfluence, float verticalBias, float horizontalBias) {
            this.smoothness = smoothness;
            this.yawSpeed = yawSpeed;
            this.pitchSpeed = pitchSpeed;
            this.yawAccel = yawAccel;
            this.yawJerk = yawJerk;
            this.pauses = pauses;
            this.multiPointCount = multiPointCount;
            this.isValid = true;
            this.rotationType = rotationType;
            this.snapFocusTicks = snapFocusTicks;
            this.yawSpeedMin = yawSpeedMin;
            this.yawSpeedMax = yawSpeedMax;
            this.pitchSpeedMin = pitchSpeedMin;
            this.pitchSpeedMax = pitchSpeedMax;
            this.smoothnessVariance = smoothnessVariance;
            this.aimPointBlendSpeed = aimPointBlendSpeed;
            this.microMovementIntensity = microMovementIntensity;
            this.transitionSmoothness = transitionSmoothness;
            this.momentumDecay = momentumDecay;
            this.predictionStrength = predictionStrength;
            this.hasPerlinNoise = hasPerlinNoise;
            this.perlinInfluence = perlinInfluence;
            this.verticalBias = verticalBias;
            this.horizontalBias = horizontalBias;
        }

        public RotationProfile() {
            this.smoothness = 0.15f;
            this.yawSpeed = 100f;
            this.pitchSpeed = 80f;
            this.yawAccel = 5000f;
            this.yawJerk = 0f;
            this.pauses = 0;
            this.multiPointCount = 0;
            this.isValid = false;
            this.rotationType = "Unknown";
            this.snapFocusTicks = 0;
            this.yawSpeedMin = 100f;
            this.yawSpeedMax = 100f;
            this.pitchSpeedMin = 80f;
            this.pitchSpeedMax = 80f;
            this.smoothnessVariance = 0f;
            this.aimPointBlendSpeed = 0f;
            this.microMovementIntensity = 0f;
            this.transitionSmoothness = 0f;
            this.momentumDecay = 0f;
            this.predictionStrength = 0f;
            this.hasPerlinNoise = false;
            this.perlinInfluence = 0f;
            this.verticalBias = 0f;
            this.horizontalBias = 0f;
        }
    }

    final SliderSetting analysisDuration = new SliderSetting(this, "Время анализа (сек)", 15.0F, 10.0F, 45.0F, 1.0F);
    final SliderSetting targetRange = new SliderSetting(this, "Дистанция цели", 8.0F, 3.0F, 15.0F, 0.5F);

    PlayerEntity target;
    long analysisStartTime;
    final Deque<RotationSnapshot> history = new ArrayDeque<>();
    boolean isAnalysisComplete = false;
    private RotationProfile lastProfile = new RotationProfile();
    private int rotatingTicks = 0;
    private int totalTicks = 0;
    private long lastRotationChange = 0;
    private float lastYaw = 0;
    private float lastPitch = 0;

    public RotationProfile getLastProfile() {
        return lastProfile;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (!history.isEmpty() && isAnalysisComplete) {
            generateAndOutputReport();
        }
        reset();
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            reset();
            return;
        }
        if (isAnalysisComplete) return;

        if (target == null || !target.isAlive() || mc.player.getDistance(target) > targetRange.getValue()) {
            findTarget();
        }

        if (target != null) {
            if (analysisStartTime == 0) {
                analysisStartTime = System.currentTimeMillis();
            }
            collectData();
        }
    }

    @EventHandler
    public void onEvent(Render2DEvent event) {
        if (target != null && analysisStartTime > 0 && !isAnalysisComplete) {
            long elapsedTime = System.currentTimeMillis() - analysisStartTime;
            double totalDuration = analysisDuration.getValue() * 1000.0;
            double progress = Math.min(elapsedTime / totalDuration, 1.0);
            String text = String.format("Анализ %s: %.0f%%", target.getName().getString(), progress * 100);
            MatrixStack matrix = event.getMatrix();
            FontRenderer font = mc.fontRenderer;
            float x = (mw.getScaledWidth() - font.getStringWidth(text)) / 2f;
            float y = mw.getScaledHeight() / 2f + 20f;
            font.drawStringWithShadow(matrix, text, x, y, ColorUtil.WHITE);
        }
    }

    private void findTarget() {
        reset();
        mc.world.getPlayers().stream()
                .filter(p -> p != mc.player && p.isAlive() && mc.player.getDistance(p) <= targetRange.getValue())
                .min(java.util.Comparator.comparingDouble(p -> mc.player.getDistanceSq(p)))
                .ifPresent(p -> target = p);
    }

    private void collectData() {
        long elapsedTime = System.currentTimeMillis() - analysisStartTime;
        if (elapsedTime > analysisDuration.getValue() * 1000) {
            ChatUtil.addText("§a[AuraVorovstvo]§f Анализ завершен. Выключите модуль, чтобы получить отчет.");
            isAnalysisComplete = true;
            return;
        }

        totalTicks++;
        long currentTime = System.currentTimeMillis();
        float yawDiff = Math.abs(target.rotationYaw - lastYaw);
        float pitchDiff = Math.abs(target.rotationPitch - lastPitch);

        if (yawDiff > 0.5f || pitchDiff > 0.5f) {
            rotatingTicks++;
            lastRotationChange = currentTime;
        }

        lastYaw = target.rotationYaw;
        lastPitch = target.rotationPitch;

        history.add(new RotationSnapshot(currentTime, target.rotationYaw, target.rotationPitch));
    }

    private void generateAndOutputReport() {
        List<Float> yawSpeeds = new ArrayList<>();
        List<Float> pitchSpeeds = new ArrayList<>();
        List<Float> yawAccels = new ArrayList<>();
        List<Float> yawJerks = new ArrayList<>();
        List<Float> pitchValues = new ArrayList<>();
        List<Float> smoothnessValues = new ArrayList<>();
        List<Float> transitionSpeeds = new ArrayList<>();
        List<Float> microMovements = new ArrayList<>();
        int pauses = 0;
        int snapEvents = 0;
        int focusedTicks = 0;

        RotationSnapshot prev = null;
        RotationSnapshot prev2 = null;
        RotationSnapshot prev3 = null;

        float minYawSpeed = Float.MAX_VALUE;
        float maxYawSpeed = Float.MIN_VALUE;
        float minPitchSpeed = Float.MAX_VALUE;
        float maxPitchSpeed = Float.MIN_VALUE;

        for (RotationSnapshot current : history) {
            if (prev != null) {
                long dt = current.timestamp - prev.timestamp;
                if (dt > 0 && dt < 100) {
                    float yawSpeed = Math.abs((current.yaw - prev.yaw) / dt * 1000f);
                    float pitchSpeed = Math.abs((current.pitch - prev.pitch) / dt * 1000f);

                    if (yawSpeed > 1.0f) {
                        yawSpeeds.add(yawSpeed);
                        minYawSpeed = Math.min(minYawSpeed, yawSpeed);
                        maxYawSpeed = Math.max(maxYawSpeed, yawSpeed);
                    }
                    if (pitchSpeed > 1.0f) {
                        pitchSpeeds.add(pitchSpeed);
                        minPitchSpeed = Math.min(minPitchSpeed, pitchSpeed);
                        maxPitchSpeed = Math.max(maxPitchSpeed, pitchSpeed);
                    }

                    pitchValues.add(current.pitch);

                    if (yawSpeed < 1.0f && pitchSpeed < 1.0f) {
                        pauses++;
                    }

                    if (yawSpeed > 200f) {
                        snapEvents++;
                    } else if (yawSpeed > 5f) {
                        focusedTicks++;
                    }

                    if (yawSpeed < 15f && yawSpeed > 1f) {
                        microMovements.add(yawSpeed);
                    }

                    if (prev2 != null) {
                        long dt2 = prev.timestamp - prev2.timestamp;
                        if (dt2 > 0 && dt2 < 100) {
                            float prevYawSpeed = Math.abs((prev.yaw - prev2.yaw) / dt2 * 1000f);
                            float yawAccel = Math.abs((yawSpeed - prevYawSpeed) / dt);
                            yawAccels.add(yawAccel);

                            if (Math.abs(prevYawSpeed) > 1.0f && yawSpeed > 1.0f) {
                                float smoothness = Math.min(prevYawSpeed, yawSpeed) / Math.max(prevYawSpeed, yawSpeed);
                                smoothnessValues.add(smoothness);
                            }

                            if (Math.abs(yawSpeed - prevYawSpeed) > 5f) {
                                transitionSpeeds.add(Math.abs(yawSpeed - prevYawSpeed));
                            }

                            if (prev3 != null) {
                                long dt3 = prev2.timestamp - prev3.timestamp;
                                if (dt3 > 0 && dt3 < 100) {
                                    float prev2YawSpeed = Math.abs((prev2.yaw - prev3.yaw) / dt3 * 1000f);
                                    float prevYawAccel = Math.abs((prevYawSpeed - prev2YawSpeed) / dt2);
                                    float yawJerk = Math.abs((yawAccel - prevYawAccel) / dt);
                                    yawJerks.add(yawJerk);
                                }
                            }
                        }
                    }
                }
            }
            prev3 = prev2;
            prev2 = prev;
            prev = current;
        }

        double avgYawSpeed = yawSpeeds.stream().mapToDouble(f -> f).average().orElse(0);
        double avgPitchSpeed = pitchSpeeds.stream().mapToDouble(f -> f).average().orElse(0);
        double avgSmoothness = smoothnessValues.stream().mapToDouble(f -> f).average().orElse(1);
        double avgYawAccel = yawAccels.stream().mapToDouble(f -> f).average().orElse(0);
        double avgYawJerk = yawJerks.stream().mapToDouble(f -> f).average().orElse(0);
        double stdDevYaw = calculateStdDev(yawSpeeds, avgYawSpeed);
        double avgTransitionSpeed = transitionSpeeds.stream().mapToDouble(f -> f).average().orElse(0);
        double avgMicroMovement = microMovements.stream().mapToDouble(f -> f).average().orElse(0);

        float smoothnessVariance = smoothnessValues.isEmpty() ? 0 :
                (float) Math.sqrt(smoothnessValues.stream().mapToDouble(s -> Math.pow(s - avgSmoothness, 2)).average().orElse(0));

        String rotationType = "Focus";
        int snapFocusTicks = 0;
        float rotationRatio = (float) rotatingTicks / totalTicks;

        if (snapEvents > 5 && rotationRatio < 0.3f) {
            rotationType = "Snap";
            snapFocusTicks = Math.min(3, (int) ((float) rotatingTicks / snapEvents));
        } else if (rotationRatio > 0.7f) {
            rotationType = "Focus";
            snapFocusTicks = 0;
        } else {
            rotationType = "Hybrid";
            snapFocusTicks = 2;
        }

        Collections.sort(pitchValues);
        List<String> bodyParts = new ArrayList<>();
        if (!pitchValues.isEmpty()) {
            float lastPitchVal = -999;
            for (float pitch : pitchValues) {
                if (Math.abs(pitch - lastPitchVal) > 8.0f) {
                    lastPitchVal = pitch;
                    if (pitch < -30) bodyParts.add("Голова");
                    else if (pitch < 10) bodyParts.add("Тело");
                    else bodyParts.add("Ноги");
                }
            }
        }

        int multiPointCount = (int) bodyParts.stream().distinct().count();
        String multiPointResult = multiPointCount > 1
                ? "Обнаружен (" + String.join(", ", bodyParts.stream().distinct().collect(Collectors.toList())) + ")"
                : "Не обнаружен";

        float aimPointBlendSpeed = (float) avgTransitionSpeed / 100f;
        float momentumDecay = smoothnessValues.isEmpty() ? 0.5f : (float) (1.0 - avgSmoothness) * 0.5f;
        boolean hasPerlinNoise = avgMicroMovement > 2f && avgMicroMovement < 8f;
        float perlinInfluence = hasPerlinNoise ? (float) avgMicroMovement / 10f : 0f;

        float verticalBias = 0f;
        if (!pitchValues.isEmpty()) {
            float avgPitch = (float) pitchValues.stream().mapToDouble(f -> f).average().orElse(0);
            if (avgPitch < -20) verticalBias = 0.85f;
            else if (avgPitch < 5) verticalBias = 0.6f;
            else verticalBias = 0.3f;
        }

        ChatUtil.addText("§f[AuraVorovstvo] Профиль ротации §c" + target.getName().getString() + "§f:");
        ChatUtil.addText(String.format("§f> Тип: §c%s §f(Тики наведения: §c%d§f)", rotationType, snapFocusTicks));
        ChatUtil.addText(String.format("§f> Скорость Yaw: §c%.2f §f(Мин: §c%.2f§f, Макс: §c%.2f§f)", avgYawSpeed, minYawSpeed, maxYawSpeed));
        ChatUtil.addText(String.format("§f> Скорость Pitch: §c%.2f §f(Мин: §c%.2f§f, Макс: §c%.2f§f)", avgPitchSpeed, minPitchSpeed, maxPitchSpeed));
        ChatUtil.addText(String.format("§f> Плавность: §c%.3f §f(Разброс: §c%.3f§f)", avgSmoothness, smoothnessVariance));
        ChatUtil.addText(String.format("§f> Ускорение: §c%.0f §f| Рывок: §c%.0f", avgYawAccel, avgYawJerk));
        ChatUtil.addText(String.format("§f> Скорость перехода точек: §c%.2f", aimPointBlendSpeed));
        ChatUtil.addText(String.format("§f> Плавность перехода: §c%.3f", avgSmoothness * aimPointBlendSpeed));
        ChatUtil.addText(String.format("§f> Затухание инерции: §c%.3f", momentumDecay));
        ChatUtil.addText(String.format("§f> Микродвижения: §c%.2f", avgMicroMovement));
        ChatUtil.addText(String.format("§f> Perlin Noise: §c%s §f(Влияние: §c%.2f§f)", hasPerlinNoise ? "Да" : "Нет", perlinInfluence));
        ChatUtil.addText(String.format("§f> Вертикальное смещение: §c%.2f", verticalBias));
        ChatUtil.addText(String.format("§f> Паузы: §c%d §f| Стабильность: §c%.2f", pauses, stdDevYaw));
        ChatUtil.addText("§f> Мультипоинт: §c" + multiPointResult);

        lastProfile = new RotationProfile(
                (float) avgSmoothness,
                (float) avgYawSpeed,
                (float) avgPitchSpeed,
                (float) avgYawAccel,
                (float) avgYawJerk,
                pauses,
                multiPointCount,
                rotationType,
                snapFocusTicks,
                minYawSpeed,
                maxYawSpeed,
                minPitchSpeed,
                maxPitchSpeed,
                smoothnessVariance,
                aimPointBlendSpeed,
                (float) avgMicroMovement,
                (float) (avgSmoothness * aimPointBlendSpeed),
                momentumDecay,
                0.5f,
                hasPerlinNoise,
                perlinInfluence,
                verticalBias,
                0f
        );

        System.out.printf(
                "Type: %s***SnapTicks: %d***Smoothness: %.3f***YawSpeed: %.2f-%.2f***PitchSpeed: %.2f-%.2f***Accel: %.0f***BlendSpeed: %.2f***MicroMove: %.2f***Momentum: %.3f***Perlin: %s-%.2f***VertBias: %.2f%n",
                rotationType, snapFocusTicks, avgSmoothness, minYawSpeed, maxYawSpeed, minPitchSpeed, maxPitchSpeed,
                avgYawAccel, aimPointBlendSpeed, avgMicroMovement, momentumDecay, hasPerlinNoise, perlinInfluence, verticalBias
        );
    }

    private double calculateStdDev(List<Float> numbers, double mean) {
        if (numbers.isEmpty()) return 0;
        double standardDeviation = 0.0;
        for (float num : numbers) standardDeviation += Math.pow(num - mean, 2);
        return Math.sqrt(standardDeviation / numbers.size());
    }

    private void reset() {
        target = null;
        analysisStartTime = 0;
        history.clear();
        isAnalysisComplete = false;
        rotatingTicks = 0;
        totalTicks = 0;
        lastRotationChange = 0;
        lastYaw = 0;
        lastPitch = 0;
    }

    private record RotationSnapshot(long timestamp, float yaw, float pitch) {
    }
}