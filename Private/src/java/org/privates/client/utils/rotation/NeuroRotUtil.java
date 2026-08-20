package org.privates.client.utils.rotation;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;

import java.util.concurrent.ThreadLocalRandom;

import static org.privates.client.api.interfaces.IMinecraft.mc;


public class NeuroRotUtil {

    private Vector2f currentRotation = new Vector2f(0, 0);
    private Vector2f lastRotation = new Vector2f(0, 0);
    private Vector2f lerpRotation = new Vector2f(0, 0);

    private String currentMode = "Polar";
    private long lastModeChangeTime = 0;
    private final long modeChangeInterval = 1000;
    private int modeIndex = 0;
    private final StealthRotationSystem stealthSystem;
    private final boolean useStealthMode = false;

    private long systemStartTime = System.currentTimeMillis();
    private long lastAttackTime = 0;
    private long lastRotationUpdate = 0;
    private final boolean shouldCorrectAim = true;
    private final float aimTolerance = 2.5f;

    private final boolean usePrediction = true;
    private final boolean useGCD = true;
    private final boolean useHitboxTargeting = true;

    public NeuroRotUtil() {
        this.stealthSystem = new StealthRotationSystem();
        resetSystem();
    }

    public void updateRotation(LivingEntity target) {
        if (target == null) return;

        stealthSystem.updateStealthRotation(target);
        currentRotation = new Vector2f(stealthSystem.getYaw(), stealthSystem.getPitch());

        long currentTime = System.currentTimeMillis();
        long randomInterval = 1000 + ThreadLocalRandom.current().nextLong(4000);

        if (currentTime - lastModeChangeTime > randomInterval) {
            float randomType = ThreadLocalRandom.current().nextFloat();
            if (randomType < 0.7f) {
                changeMode();
            } else if (randomType < 0.9f) {
                setRandomMode();
            } else {
                setUltraRandomMode();
            }
            lastModeChangeTime = currentTime;
        }

        lastRotationUpdate = currentTime;
        lastRotation = new Vector2f(currentRotation.x, currentRotation.y);
    }


    public String getRandomModeFromCategory(String category) {
        String[] allModes = getAllModes();
        String[] categoryModes = java.util.Arrays.stream(allModes)
                .filter(mode -> mode.toLowerCase().contains(category.toLowerCase()))
                .toArray(String[]::new);

        if (categoryModes.length > 0) {
            return categoryModes[ThreadLocalRandom.current().nextInt(categoryModes.length)];
        }
        return allModes[ThreadLocalRandom.current().nextInt(allModes.length)];
    }


    private void applyRotationMode(LivingEntity target) {
        switch (currentMode) {
            case "Polar":
                applyPolarRotation(target);
                break;
            case "PolarV2":
                applyPolarV2Rotation(target);
                break;
            case "PolarV3":
                applyPolarV3Rotation(target);
                break;
            case "Polar+":
                applyPolarPlusRotation(target);
                break;
            case "PolarPro":
                applyPolarProRotation(target);
                break;
            case "PolarUltimate":
                applyPolarUltimateRotation(target);
                break;

            case "Intave":
                applyIntaveRotation(target);
                break;
            case "IntaveV2":
                applyIntaveV2Rotation(target);
                break;
            case "IntaveV3":
                applyIntaveV3Rotation(target);
                break;
            case "Intave+":
                applyIntavePlusRotation(target);
                break;
            case "IntaveNew":
                applyIntaveNewRotation(target);
                break;
            case "IntaveNewV2":
                applyIntaveNewV2Rotation(target);
                break;
            case "IntavePro":
                applyIntaveProRotation(target);
                break;
            case "IntaveUltimate":
                applyIntaveUltimateRotation(target);
                break;

            case "Matrix":
                applyMatrixRotation(target);
                break;
            case "MatrixV2":
                applyMatrixV2Rotation(target);
                break;
            case "MatrixV3":
                applyMatrixV3Rotation(target);
                break;
            case "MatrixPro":
                applyMatrixProRotation(target);
                break;
            case "MatrixUltimate":
                applyMatrixUltimateRotation(target);
                break;

            case "Verus":
                applyVerusRotation(target);
                break;
            case "VerusV2":
                applyVerusV2Rotation(target);
                break;
            case "VerusV3":
                applyVerusV3Rotation(target);
                break;
            case "VerusPro":
                applyVerusProRotation(target);
                break;

            case "Vulcan":
                applyVulcanRotation(target);
                break;
            case "VulcanV2":
                applyVulcanV2Rotation(target);
                break;
            case "VulcanV3":
                applyVulcanV3Rotation(target);
                break;
            case "VulcanPro":
                applyVulcanProRotation(target);
                break;

            case "Watchdog":
                applyWatchdogRotation(target);
                break;
            case "WatchdogV2":
                applyWatchdogV2Rotation(target);
                break;
            case "WatchdogV3":
                applyWatchdogV3Rotation(target);
                break;
            case "WatchdogPro":
                applyWatchdogProRotation(target);
                break;

            case "GrimAC":
                applyGrimACRotation(target);
                break;
            case "GrimV2":
                applyGrimV2Rotation(target);
                break;
            case "GrimV3":
                applyGrimV3Rotation(target);
                break;
            case "GrimPro":
                applyGrimProRotation(target);
                break;

            case "Spartan":
                applySpartanRotation(target);
                break;
            case "SpartanV2":
                applySpartanV2Rotation(target);
                break;
            case "SpartanV3":
                applySpartanV3Rotation(target);
                break;
            case "SpartanPro":
                applySpartanProRotation(target);
                break;

            case "Karhu":
                applyKarhuRotation(target);
                break;
            case "KarhuV2":
                applyKarhuV2Rotation(target);
                break;
            case "KarhuV3":
                applyKarhuV3Rotation(target);
                break;
            case "KarhuPro":
                applyKarhuProRotation(target);
                break;

            case "HolyWorld":
                applyHolyWorldRotation(target);
                break;
            case "HolyWorldV2":
                applyHolyWorldV2Rotation(target);
                break;
            case "HolyWorldPro":
                applyHolyWorldProRotation(target);
                break;

            case "FunTime":
                applyFunTimeRotation(target);
                break;
            case "FunTimeV2":
                applyFunTimeV2Rotation(target);
                break;
            case "FunTimePro":
                applyFunTimeProRotation(target);
                break;

            case "SpookyTime":
                applySpookyTimeRotation(target);
                break;
            case "SpookyTimeV2":
                applySpookyTimeV2Rotation(target);
                break;
            case "SpookyTimePro":
                applySpookyTimeProRotation(target);
                break;

            case "CubeWorld":
                applyCubeWorldRotation(target);
                break;
            case "CubeWorldV2":
                applyCubeWorldV2Rotation(target);
                break;
            case "CubeWorldPro":
                applyCubeWorldProRotation(target);
                break;


            case "AdvancedAC":
                applyAdvancedACRotation(target);
                break;
            case "AdvancedV2":
                applyAdvancedV2Rotation(target);
                break;
            case "AdvancedV3":
                applyAdvancedV3Rotation(target);
                break;
            case "AdvancedPro":
                applyAdvancedProRotation(target);
                break;

            case "Vanilla+":
                applyVanillaPlusRotation(target);
                break;
            case "VanillaV2":
                applyVanillaV2Rotation(target);
                break;
            case "VanillaPro":
                applyVanillaProRotation(target);
                break;

            case "MegaSmooth":
                applyMegaSmoothRotation(target);
                break;
            case "MegaSmoothV2":
                applyMegaSmoothV2Rotation(target);
                break;
            case "MegaSmoothPro":
                applyMegaSmoothProRotation(target);
                break;
            case "UltraSmooth":
                applyUltraSmoothRotation(target);
                break;

            case "PolarIntave":
                applyPolarIntaveHybrid(target);
                break;
            case "PolarMatrix":
                applyPolarMatrixHybrid(target);
                break;
            case "IntaveMatrix":
                applyIntaveMatrixHybrid(target);
                break;
            case "VulcanGrim":
                applyVulcanGrimHybrid(target);
                break;
            case "WatchdogKarhu":
                applyWatchdogKarhuHybrid(target);
                break;
            case "SuperHybrid":
                applySuperHybridRotation(target);
                break;

            case "Quantum":
                applyQuantumRotation(target);
                break;
            case "QuantumV2":
                applyQuantumV2Rotation(target);
                break;
            case "Neural":
                applyNeuralRotation(target);
                break;
            case "NeuralV2":
                applyNeuralV2Rotation(target);
                break;
            case "Adaptive":
                applyAdaptiveRotation(target);
                break;
            case "AdaptiveV2":
                applyAdaptiveV2Rotation(target);
                break;
            case "Evolved":
                applyEvolvedRotation(target);
                break;
            case "EvolvedV2":
                applyEvolvedV2Rotation(target);
                break;

            case "Lightning":
                applyLightningRotation(target);
                break;
            case "LightningV2":
                applyLightningV2Rotation(target);
                break;
            case "Instant":
                applyInstantRotation(target);
                break;
            case "InstantV2":
                applyInstantV2Rotation(target);
                break;
            case "Flash":
                applyFlashRotation(target);
                break;
            case "FlashV2":
                applyFlashV2Rotation(target);
                break;

            case "Silk":
                applySilkRotation(target);
                break;
            case "SilkV2":
                applySilkV2Rotation(target);
                break;
            case "Butter":
                applyButterRotation(target);
                break;
            case "ButterV2":
                applyButterV2Rotation(target);
                break;
            case "Velvet":
                applyVelvetRotation(target);
                break;
            case "VelvetV2":
                applyVelvetV2Rotation(target);
                break;

            case "1TickAttack":
                apply1TickAttackRotation(target);
                break;
            case "2TickAttack":
                apply2TickAttackRotation(target);
                break;
            case "3TickAttack":
                apply3TickAttackRotation(target);
                break;
            case "4TickAttack":
                apply4TickAttackRotation(target);
                break;
            case "5TickAttack":
                apply5TickAttackRotation(target);
                break;

            case "Legit":
                applyLegitRotation(target);
                break;
            case "LegitV2":
                applyLegitV2Rotation(target);
                break;
            case "LegitPro":
                applyLegitProRotation(target);
                break;
            case "Human":
                applyHumanRotation(target);
                break;
            case "HumanV2":
                applyHumanV2Rotation(target);
                break;
            case "HumanPro":
                applyHumanProRotation(target);
                break;

            case "Rage":
                applyRageRotation(target);
                break;
            case "RageV2":
                applyRageV2Rotation(target);
                break;
            case "RagePro":
                applyRageProRotation(target);
                break;

            case "Stealth":
                applyStealthRotation(target);
                break;
            case "StealthV2":
                applyStealthV2Rotation(target);
                break;
            case "StealthPro":
                applyStealthProRotation(target);
                break;

            case "Mineplex":
                applyMineplexRotation(target);
                break;
            case "Hypixel":
                applyHypixelRotation(target);
                break;
            case "CubeCraft":
                applyCubeCraftRotation(target);
                break;
            case "Hive":
                applyHiveRotation(target);
                break;
            case "Minehut":
                applyMinehutRotation(target);
                break;
            case "Aternos":
                applyAternosRotation(target);
                break;

            default:
                applyPolarRotation(target);
                break;
        }
    }

    private boolean isAimingAtTarget(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] targetRotation = calculateRotation(targetPos);

        float yawDiff = Math.abs(getAngleDifference(targetRotation[0], currentRotation.x));
        float pitchDiff = Math.abs(getAngleDifference(targetRotation[1], currentRotation.y));

        return yawDiff <= aimTolerance && pitchDiff <= aimTolerance;
    }

    private void correctAiming(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] targetRotation = calculateRotation(targetPos);

        float correctionSpeed = 8.0f;
        float yawDelta = getAngleDifference(targetRotation[0], currentRotation.x);
        float pitchDelta = getAngleDifference(targetRotation[1], currentRotation.y);

        currentRotation.x += yawDelta / correctionSpeed;
        currentRotation.y += pitchDelta / correctionSpeed;

        normalizeRotation();
    }

    private void applyPolarIntaveHybrid(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        boolean usePolar = (time / 150) % 2 == 0;

        if (usePolar) {
            float jitter = (float) (Math.sin(time * 0.01) * 0.08f);
            rawRotations[0] += jitter;
            rawRotations[1] += jitter * 0.5f;
            interpolateToTarget(rawRotations[0], rawRotations[1], 6.5f);
        } else {
            float randomFactor = (float) (0.8 + Math.random() * 0.4);
            rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.12f * randomFactor;
            rawRotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.06f * randomFactor;
            interpolateToTarget(rawRotations[0], rawRotations[1], 4.2f);
        }

        applyRotationComponent(175, 88, 2, 4);
    }

    private void applyPolarMatrixHybrid(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float polarJitter = (float) (Math.sin(time * 0.01) * 0.08f);
        boolean matrixTick = (time % 83) == 0;

        rawRotations[0] += polarJitter + (matrixTick ? 0.15f : 0);
        rawRotations[1] += polarJitter * 0.5f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 7.0f);
        applyRotationComponent(185, 92, 2, 4);
    }

    private void resetModeSpecificSettings() {
        lastAttackTime = 0;
        lastRotationUpdate = System.currentTimeMillis();

        switch (currentMode) {
            case "FunTime":
            case "FunTimeV2":
            case "FunTimePro":
                break;
            case "CubeWorld":
            case "CubeWorldV2":
            case "CubeWorldPro":
                break;
            case "Intave":
            case "IntaveV2":
            case "IntaveV3":
                break;
            default:
                break;
        }
    }

    private void applyIntaveMatrixHybrid(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float randomFactor = (float) (0.8 + Math.random() * 0.4);
        boolean matrixMode = (time % 150) < 75;

        float modifier = matrixMode ? 0.15f : 0.08f;
        rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * modifier * randomFactor;
        rawRotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * modifier * randomFactor * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], matrixMode ? 5.5f : 4.2f);
        applyRotationComponent(170, 85, 2, 4);
    }

    private void applyVulcanGrimHybrid(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        boolean vulcanSpike = ThreadLocalRandom.current().nextFloat() < 0.15f;
        if (vulcanSpike) {
            float spike = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 8.0f;
            rawRotations[0] += spike;
            rawRotations[1] += spike * 0.5f;
        } else {
            float precision = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.05f;
            rawRotations[0] += precision;
            rawRotations[1] += precision * 0.5f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 4.0f);
        applyRotationComponent(150, 75, 3, 5);
    }

    private void applyHolyWorldRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        boolean holySnap = ThreadLocalRandom.current().nextFloat() < 0.15f;

        if (holySnap) {
            currentRotation.x = rawRotations[0];
            currentRotation.y = rawRotations[1];
        } else {
            interpolateToTarget(rawRotations[0], rawRotations[1], 3.8f);
        }

        long time = System.currentTimeMillis();
        float holyWave = (float) (Math.sin(time * 0.005) * 1.2f);
        currentRotation.x += holyWave * 0.3f;

        normalizeRotation();
        applyRotationComponent(150, 75, 3, 6);
    }

    private void applyHolyWorldV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        boolean divineMode = (time % 250) < 80;
        boolean holySnap = ThreadLocalRandom.current().nextFloat() < (divineMode ? 0.25f : 0.1f);

        if (holySnap) {
            float divineJitter = (ThreadLocalRandom.current().nextFloat() - 0.5f) * (divineMode ? 0.4f : 0.2f);
            currentRotation.x = rawRotations[0] + divineJitter;
            currentRotation.y = rawRotations[1] + divineJitter * 0.6f;
        } else {
            interpolateToTarget(rawRotations[0], rawRotations[1], divineMode ? 3.0f : 4.5f);
        }

        float holyWave = (float) (Math.sin(time * 0.006) * (divineMode ? 1.8f : 1.0f));
        currentRotation.x += holyWave * 0.25f;

        normalizeRotation();
        applyRotationComponent(155, 77, 3, 6);
    }

    private void applyHolyWorldProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double holyMath = Math.sin(time * 0.0045) * Math.cos(time * 0.0033);
        boolean celestialSnap = ThreadLocalRandom.current().nextFloat() < (0.15f + Math.abs(holyMath) * 0.2f);

        if (celestialSnap) {
            float celestialJitter = (float) holyMath * 0.6f;
            currentRotation.x = rawRotations[0] + celestialJitter;
            currentRotation.y = rawRotations[1] + celestialJitter * 0.7f;
        } else {
            interpolateToTarget(rawRotations[0], rawRotations[1], 4.2f);
        }

        float celestialWave = (float) (Math.sin(time * 0.0055) * 1.5f + Math.cos(time * 0.0041) * 0.8f);
        currentRotation.x += celestialWave * 0.2f;

        normalizeRotation();
        applyRotationComponent(160, 80, 2, 5);
    }

    private void applyWatchdogKarhuHybrid(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float watchdogComplex = (float) (Math.sin(time * 0.0023) * 0.15f);
        float karhuMicro = (float) (Math.sin(time * 0.0067) * 0.03f);

        rawRotations[0] += watchdogComplex + karhuMicro;
        rawRotations[1] += (watchdogComplex + karhuMicro) * 0.7f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 9.0f);
        applyRotationComponent(200, 100, 1, 3);
    }

    private void applySuperHybridRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        int hybridMode = (int) ((time / 1000) % 5);

        switch (hybridMode) {
            case 0:
                float polarJitter = (float) (Math.sin(time * 0.01) * 0.08f);
                rawRotations[0] += polarJitter;
                rawRotations[1] += polarJitter * 0.5f;
                interpolateToTarget(rawRotations[0], rawRotations[1], 6.5f);
                break;
            case 1:
                float randomFactor = (float) (0.8 + Math.random() * 0.4);
                rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.12f * randomFactor;
                rawRotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.06f * randomFactor;
                interpolateToTarget(rawRotations[0], rawRotations[1], 4.2f);
                break;
            case 2:
                if ((time % 83) == 0) {
                    rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.24f;
                }
                interpolateToTarget(rawRotations[0], rawRotations[1], 7.2f);
                break;
            case 3:
                if (ThreadLocalRandom.current().nextFloat() < 0.2f) {
                    float spike = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 12.0f;
                    rawRotations[0] += spike;
                    rawRotations[1] += spike * 0.5f;
                }
                interpolateToTarget(rawRotations[0], rawRotations[1], 3.2f);
                break;
            case 4:
                float microMovement = (float) (Math.sin(time * 0.0067) * 0.03f);
                rawRotations[0] += microMovement;
                rawRotations[1] += microMovement * 0.6f;
                interpolateToTarget(rawRotations[0], rawRotations[1], 9.5f);
                break;
        }

        applyRotationComponent(180, 90, 2, 4);
    }

    private Vector3d getBestTargetPosition(LivingEntity target) {
        if (!useHitboxTargeting) {
            return target.getPositionVec().add(0, target.getEyeHeight() * 0.85, 0);
        }

        Vector3d playerPos = mc.player.getEyePosition(1.0F);
        Vector3d bestPos = null;
        double bestDistance = Double.MAX_VALUE;

        for (float y = 0.2f; y <= 0.9f; y += 0.2f) {
            for (float x = -0.3f; x <= 0.3f; x += 0.2f) {
                for (float z = -0.3f; z <= 0.3f; z += 0.2f) {
                    Vector3d testPos = target.getPositionVec().add(x, target.getHeight() * y, z);
                    double distance = playerPos.distanceTo(testPos);

                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestPos = testPos;
                    }
                }
            }
        }

        if (usePrediction && target.getMotion().length() > 0.05) {
            Vector3d motion = target.getMotion();
            double predictionTicks = Math.min(bestDistance / 20.0, 5.0);
            bestPos = bestPos.add(motion.scale(predictionTicks));
        }

        return bestPos != null ? bestPos : target.getPositionVec().add(0, target.getEyeHeight() * 0.85, 0);
    }


    private void applyPolarRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        float smoothness = 6.5f + (float) (Math.random() * 1.5f);
        float randomFactor = 0.15f;

        float targetYaw = rawRotations[0] + (ThreadLocalRandom.current().nextFloat() - 0.5f) * randomFactor * 2.0f;
        float targetPitch = rawRotations[1] + (ThreadLocalRandom.current().nextFloat() - 0.5f) * randomFactor;

        interpolateToTarget(targetYaw, targetPitch, smoothness);
        applyRotationComponent(180, 90, 2, 4);
    }

    private void applyPolarV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        float smoothness = 7.0f + (float) (Math.sin(System.currentTimeMillis() * 0.003) * 1.0f);
        float jitter = (float) (Math.sin(System.currentTimeMillis() * 0.01) * 0.08f);

        float targetYaw = rawRotations[0] + jitter;
        float targetPitch = rawRotations[1] + jitter * 0.5f;

        interpolateToTarget(targetYaw, targetPitch, smoothness);
        applyRotationComponent(185, 92, 2, 4);
    }

    private void applyPolarV3Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float dynamicSmooth = 5.5f + (float) (Math.sin(time * 0.002) * 2.0f);
        float microJitter = (float) (Math.sin(time * 0.015) * 0.05f + Math.cos(time * 0.012) * 0.03f);

        float targetYaw = rawRotations[0] + microJitter;
        float targetPitch = rawRotations[1] + microJitter * 0.6f;

        interpolateToTarget(targetYaw, targetPitch, dynamicSmooth);
        applyRotationComponent(190, 95, 1, 3);
    }

    private void applyPolarPlusRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        float acceleration = Math.abs(getAngleDifference(rawRotations[0], currentRotation.x)) > 30 ? 1.3f : 1.0f;
        float jitterX = (float) (Math.sin(System.currentTimeMillis() * 0.01) * 0.08f);
        float jitterY = (float) (Math.cos(System.currentTimeMillis() * 0.008) * 0.04f);

        float targetYaw = rawRotations[0] + jitterX;
        float targetPitch = rawRotations[1] + jitterY;

        float smoothness = 6.5f / acceleration;
        interpolateToTarget(targetYaw, targetPitch, smoothness);

        if (useGCD) {
            applyGCDCorrection();
        }

        applyRotationComponent(200, 100, 1, 3);
    }

    private void applyPolarProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float complexWave = (float) (Math.sin(time * 0.0023) * 0.6 + Math.cos(time * 0.0031) * 0.4);
        float adaptiveSmooth = 7.2f + complexWave;

        float targetYaw = rawRotations[0] + complexWave * 0.1f;
        float targetPitch = rawRotations[1] + complexWave * 0.05f;

        interpolateToTarget(targetYaw, targetPitch, adaptiveSmooth);
        applyRotationComponent(195, 97, 1, 3);
    }

    private void applyPolarUltimateRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float wave1 = (float) (Math.sin(time * 0.0019) * 0.8);
        float wave2 = (float) (Math.cos(time * 0.0027) * 0.6);
        float wave3 = (float) (Math.sin(time * 0.0013) * 1.1);

        float ultimateModifier = (wave1 + wave2 * 0.7f + wave3 * 0.4f) * 0.15f;
        float adaptiveSpeed = 8.0f + Math.abs(ultimateModifier) * 2.0f;

        float targetYaw = rawRotations[0] + ultimateModifier;
        float targetPitch = rawRotations[1] + ultimateModifier * 0.6f;

        interpolateToTarget(targetYaw, targetPitch, adaptiveSpeed);
        applyRotationComponent(210, 105, 1, 2);
    }

    private void applyIntaveRotation(LivingEntity target) {
        if (System.currentTimeMillis() - lastRotationUpdate < 75) return;

        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        float randomFactor = (float) (0.8 + Math.random() * 0.4);
        float adaptiveSpeed = 4.2f * (1.0f + Math.abs(getAngleDifference(rawRotations[0], currentRotation.x)) / 180.0f * 0.5f);

        float targetYaw = rawRotations[0] + (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.12f * randomFactor;
        float targetPitch = rawRotations[1] + (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.06f * randomFactor;

        interpolateToTarget(targetYaw, targetPitch, adaptiveSpeed);
        applyRotationComponent(160, 80, 3, 5);
    }

    private void applyIntaveV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float timeBasedRandom = (float) (Math.sin(time * 0.004) * 0.1f);
        float adaptiveDelay = 60 + (int) (Math.random() * 30);

        if (time - lastRotationUpdate < adaptiveDelay) return;

        float targetYaw = rawRotations[0] + timeBasedRandom;
        float targetPitch = rawRotations[1] + timeBasedRandom * 0.5f;

        float speed = 4.5f + (float) (Math.random() * 1.0f);
        interpolateToTarget(targetYaw, targetPitch, speed);
        applyRotationComponent(165, 82, 3, 5);
    }

    private void applyIntaveV3Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float complexity = (float) (Math.sin(time * 0.003) * 1.5f + Math.cos(time * 0.002) * 0.8f);
        float microDelay = (time % 100 < 30) ? 0.5f : 1.0f;

        float targetYaw = rawRotations[0] + complexity * 0.08f;
        float targetPitch = rawRotations[1] + complexity * 0.04f;

        float speed = 4.8f * microDelay;
        interpolateToTarget(targetYaw, targetPitch, speed);
        applyRotationComponent(170, 85, 2, 4);
    }

    private void applyIntavePlusRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float complexity = (float) Math.sin(time * 0.003) * 1.5f;

        float targetYaw = rawRotations[0] + complexity * 0.1f;
        float targetPitch = rawRotations[1] + complexity * 0.05f;

        float speed = 4.2f * (0.8f + Math.abs(complexity) * 0.4f);
        interpolateToTarget(targetYaw, targetPitch, speed);
        applyRotationComponent(170, 85, 2, 4);
    }

    private void applyIntaveNewRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float silkWave = (float) (Math.sin(time * 0.002) * 0.8 + Math.cos(time * 0.0015) * 0.6);

        float targetYaw = rawRotations[0] + silkWave * 0.05f;
        float targetPitch = rawRotations[1] + silkWave * 0.03f;

        interpolateToTarget(targetYaw, targetPitch, 8.0f);
        applyRotationComponent(190, 95, 1, 3);
    }

    private void applyIntaveNewV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float silk1 = (float) (Math.sin(time * 0.0018) * 0.6);
        float silk2 = (float) (Math.cos(time * 0.0013) * 0.4);
        float silk3 = (float) (Math.sin(time * 0.0021) * 0.7);

        float ultraSilk = (silk1 + silk2 * 0.8f + silk3 * 0.6f) * 0.04f;

        float targetYaw = rawRotations[0] + ultraSilk;
        float targetPitch = rawRotations[1] + ultraSilk * 0.7f;

        interpolateToTarget(targetYaw, targetPitch, 9.0f);
        applyRotationComponent(195, 97, 1, 3);
    }

    private void applyIntaveProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double proMath = Math.sin(time * 0.0025) * Math.cos(time * 0.0019);

        float targetYaw = rawRotations[0] + (float) proMath * 0.12f;
        float targetPitch = rawRotations[1] + (float) proMath * 0.08f;

        interpolateToTarget(targetYaw, targetPitch, 5.5f);
        applyRotationComponent(175, 87, 2, 4);
    }

    private void applyIntaveUltimateRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double ultra1 = Math.sin(time * 0.0023) * Math.cos(time * 0.0017);
        double ultra2 = Math.tan(time * 0.0011) * 0.5;

        float ultimateModifier = (float) (ultra1 + ultra2) * 0.1f;

        float targetYaw = rawRotations[0] + ultimateModifier;
        float targetPitch = rawRotations[1] + ultimateModifier * 0.6f;

        interpolateToTarget(targetYaw, targetPitch, 6.0f);
        applyRotationComponent(200, 100, 1, 3);
    }

    private void applyMatrixRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        if ((time % 83) == 0) {
            float matrixJump = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.24f;
            rawRotations[0] += matrixJump;
            rawRotations[1] += matrixJump * 0.5f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 7.2f);
        applyRotationComponent(185, 92, 2, 4);
    }

    private void applyMatrixV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float matrixWave = (float) (Math.sin(time * 0.0025) * 0.12f);
        boolean matrixTick = (time % 67) < 10;

        float targetYaw = rawRotations[0] + matrixWave + (matrixTick ? 0.15f : 0);
        float targetPitch = rawRotations[1] + matrixWave * 0.6f;

        interpolateToTarget(targetYaw, targetPitch, 7.5f);
        applyRotationComponent(190, 95, 2, 4);
    }

    private void applyMatrixV3Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float matrix1 = (float) (Math.sin(time * 0.0027) * 0.1f);
        float matrix2 = (float) (Math.cos(time * 0.0021) * 0.08f);
        boolean complexTick = (time % 89) < 15;

        float targetYaw = rawRotations[0] + matrix1 + (complexTick ? matrix2 * 2.0f : 0);
        float targetPitch = rawRotations[1] + matrix1 * 0.7f;

        interpolateToTarget(targetYaw, targetPitch, 8.0f);
        applyRotationComponent(195, 97, 2, 4);
    }

    private void applyMatrixProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double matrixMath = Math.sin(time * 0.0029) * Math.cos(time * 0.0023);
        boolean proTick = (time % 97) < 20;

        float proModifier = (float) matrixMath * (proTick ? 0.25f : 0.12f);

        float targetYaw = rawRotations[0] + proModifier;
        float targetPitch = rawRotations[1] + proModifier * 0.6f;

        interpolateToTarget(targetYaw, targetPitch, 8.5f);
        applyRotationComponent(200, 100, 1, 3);
    }


    private void applyMatrixUltimateRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double ultra1 = Math.sin(time * 0.0031) * Math.cos(time * 0.0025);
        double ultra2 = Math.tan(time * 0.0017) * 0.4;
        double ultra3 = Math.sin(time * 0.0013) * Math.sin(time * 0.0029);

        float ultimateMatrix = (float) (ultra1 + ultra2 * 0.7 + ultra3 * 0.5) * 0.15f;
        boolean megaTick = (time % 101) < 25;

        float targetYaw = rawRotations[0] + ultimateMatrix * (megaTick ? 2.5f : 1.0f);
        float targetPitch = rawRotations[1] + ultimateMatrix * 0.8f;

        interpolateToTarget(targetYaw, targetPitch, 9.0f);
        applyRotationComponent(210, 105, 1, 2);
    }

    private void applyVerusRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        if (ThreadLocalRandom.current().nextFloat() < 0.25f) {
            float verusSpike = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 1.8f;
            rawRotations[0] += verusSpike;
            rawRotations[1] += verusSpike * 0.5f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.8f);
        applyRotationComponent(170, 85, 3, 5);
    }

    private void applyVerusV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        boolean verusMode = (time % 150) < 50;

        if (verusMode && ThreadLocalRandom.current().nextFloat() < 0.3f) {
            float spike = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2.2f;
            rawRotations[0] += spike;
            rawRotations[1] += spike * 0.4f;
        }

        float jitter = (float) (Math.sin(time * 0.012) * 0.1f);
        rawRotations[0] += jitter;
        rawRotations[1] += jitter * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.2f);
        applyRotationComponent(175, 87, 3, 5);
    }

    private void applyVerusV3Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float verusComplexity = (float) (Math.sin(time * 0.0034) * 0.15f + Math.cos(time * 0.0041) * 0.12f);

        if (ThreadLocalRandom.current().nextFloat() < 0.2f) {
            float adaptiveSpike = verusComplexity * 8.0f;
            rawRotations[0] += adaptiveSpike;
            rawRotations[1] += adaptiveSpike * 0.5f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.5f + Math.abs(verusComplexity) * 2.0f);
        applyRotationComponent(180, 90, 2, 4);
    }

    private void applyVerusProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float proWave1 = (float) (Math.sin(time * 0.0019) * 0.8);
        float proWave2 = (float) (Math.cos(time * 0.0027) * 0.6);
        float proComplexity = (proWave1 + proWave2) * 0.1f;

        boolean proSpike = ThreadLocalRandom.current().nextFloat() < (0.15f + Math.abs(proComplexity) * 0.5f);
        if (proSpike) {
            float spikeIntensity = 1.5f + Math.abs(proComplexity) * 3.0f;
            rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * spikeIntensity;
            rawRotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * spikeIntensity * 0.6f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 7.0f);
        applyRotationComponent(185, 92, 2, 4);
    }

    private void applyVulcanRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        if (ThreadLocalRandom.current().nextFloat() < 0.2f) {
            float vulcanSpike = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 12.0f;
            rawRotations[0] += vulcanSpike;
            rawRotations[1] += vulcanSpike * 0.5f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 3.2f);
        applyRotationComponent(120, 60, 5, 8);
    }

    private void applyVulcanV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        boolean vulcanRage = (time % 200) < 80;

        if (vulcanRage && ThreadLocalRandom.current().nextFloat() < 0.35f) {
            float rageSpike = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 15.0f;
            rawRotations[0] += rageSpike;
            rawRotations[1] += rageSpike * 0.4f;
        }

        float smoothness = vulcanRage ? 2.8f : 4.2f;
        interpolateToTarget(rawRotations[0], rawRotations[1], smoothness);
        applyRotationComponent(110, 55, 6, 9);
    }

    private void applyVulcanV3Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float vulcanCycle = (float) (Math.sin(time * 0.005) * 0.5 + 0.5);

        if (ThreadLocalRandom.current().nextFloat() < (0.15f + vulcanCycle * 0.25f)) {
            float cycleSpike = (ThreadLocalRandom.current().nextFloat() - 0.5f) * (8.0f + vulcanCycle * 8.0f);
            rawRotations[0] += cycleSpike;
            rawRotations[1] += cycleSpike * 0.6f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 3.5f + vulcanCycle * 2.0f);
        applyRotationComponent(125, 62, 4, 7);
    }

    private void applyVulcanProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float proIntensity = (float) (Math.sin(time * 0.003) * 0.6 + Math.cos(time * 0.002) * 0.4);
        float adaptiveSpike = Math.abs(proIntensity) * 20.0f + 5.0f;

        if (ThreadLocalRandom.current().nextFloat() < (0.2f + Math.abs(proIntensity) * 0.3f)) {
            rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * adaptiveSpike;
            rawRotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * adaptiveSpike * 0.5f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 3.8f);
        applyRotationComponent(130, 65, 4, 6);
    }

    private void applyWatchdogRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float complexity1 = (float) (Math.sin(time * 0.0023) * 0.15f);
        float complexity2 = (float) (Math.cos(time * 0.0031) * 0.105f);

        rawRotations[0] += complexity1 * 2.0f;
        rawRotations[1] += complexity2;

        interpolateToTarget(rawRotations[0], rawRotations[1], 8.5f);
        applyRotationComponent(195, 97, 1, 3);
    }

    private void applyWatchdogV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float wave1 = (float) (Math.sin(time * 0.0025) * 0.12f);
        float wave2 = (float) (Math.cos(time * 0.0033) * 0.08f);
        float wave3 = (float) (Math.sin(time * 0.0019) * 0.15f);

        float complexModifier = wave1 + wave2 * 0.8f + wave3 * 0.6f;

        rawRotations[0] += complexModifier * 1.5f;
        rawRotations[1] += complexModifier * 0.8f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 9.0f);
        applyRotationComponent(200, 100, 1, 3);
    }

    private void applyWatchdogV3Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float superWave1 = (float) (Math.sin(time * 0.0021) * 0.1);
        float superWave2 = (float) (Math.cos(time * 0.0029) * 0.07);
        float superWave3 = (float) (Math.sin(time * 0.0017) * 0.13);
        float superWave4 = (float) (Math.cos(time * 0.0037) * 0.05);

        float ultraComplex = (superWave1 + superWave2 * 0.9f + superWave3 * 0.7f + superWave4 * 1.1f) * 0.8f;

        rawRotations[0] += ultraComplex * 1.8f;
        rawRotations[1] += ultraComplex * 0.9f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 9.5f);
        applyRotationComponent(205, 102, 1, 2);
    }

    private void applyWatchdogProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double mathComplex = Math.sin(time * 0.0023) * Math.cos(time * 0.0031) * 0.12;
        double trigComplex = Math.tan(time * 0.001) * 0.03;

        float proModifier = (float) (mathComplex + trigComplex);

        rawRotations[0] += proModifier * 2.2f;
        rawRotations[1] += proModifier * 1.1f;

        float adaptiveSmooth = 8.8f + Math.abs(proModifier) * 3.0f;
        interpolateToTarget(rawRotations[0], rawRotations[1], adaptiveSmooth);
        applyRotationComponent(210, 105, 1, 2);
    }

    private void applyGrimACRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        float grimPrecision = 0.05f;
        rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * grimPrecision;
        rawRotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * grimPrecision * 0.5f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 4.8f);
        applyRotationComponent(140, 70, 4, 6);
    }

    private void applyGrimV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float grimMicro = (float) (Math.sin(time * 0.008) * 0.03f);

        rawRotations[0] += grimMicro;
        rawRotations[1] += grimMicro * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.2f);
        applyRotationComponent(145, 72, 4, 6);
    }

    private void applyGrimV3Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float grimUltraMicro = (float) (Math.sin(time * 0.0067) * 0.02f + Math.cos(time * 0.0043) * 0.015f);

        rawRotations[0] += grimUltraMicro;
        rawRotations[1] += grimUltraMicro * 0.7f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.5f);
        applyRotationComponent(150, 75, 3, 5);
    }

    private void applyGrimProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double grimMath = Math.sin(time * 0.0051) * Math.cos(time * 0.0037) * 0.025;

        rawRotations[0] += (float) grimMath;
        rawRotations[1] += (float) grimMath * 0.8f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.0f);
        applyRotationComponent(155, 77, 3, 5);
    }

    private void applySpartanRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        boolean spartanSnap = ThreadLocalRandom.current().nextFloat() < 0.1f;

        if (spartanSnap) {
            currentRotation.x = rawRotations[0];
            currentRotation.y = rawRotations[1];
        } else {
            interpolateToTarget(rawRotations[0], rawRotations[1], 6.8f);
        }

        normalizeRotation();
        applyRotationComponent(160, 80, 2, 5);
    }

    private void applySpartanV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        boolean spartanMode = (time % 300) < 100;
        boolean snapChance = ThreadLocalRandom.current().nextFloat() < (spartanMode ? 0.15f : 0.05f);

        if (snapChance) {
            float snapJitter = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.2f;
            currentRotation.x = rawRotations[0] + snapJitter;
            currentRotation.y = rawRotations[1] + snapJitter * 0.5f;
        } else {
            interpolateToTarget(rawRotations[0], rawRotations[1], spartanMode ? 5.5f : 7.2f);
        }

        normalizeRotation();
        applyRotationComponent(165, 82, 2, 5);
    }

    private void applySpartanV3Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float spartanCycle = (float) (Math.sin(time * 0.004) * 0.5 + 0.5);
        boolean adaptiveSnap = ThreadLocalRandom.current().nextFloat() < (0.08f + spartanCycle * 0.12f);

        if (adaptiveSnap) {
            float adaptiveJitter = spartanCycle * 0.3f;
            currentRotation.x = rawRotations[0] + (ThreadLocalRandom.current().nextFloat() - 0.5f) * adaptiveJitter;
            currentRotation.y = rawRotations[1] + (ThreadLocalRandom.current().nextFloat() - 0.5f) * adaptiveJitter * 0.6f;
        } else {
            interpolateToTarget(rawRotations[0], rawRotations[1], 6.0f + spartanCycle * 2.0f);
        }

        normalizeRotation();
        applyRotationComponent(170, 85, 2, 4);
    }

    private void applySpartanProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double spartanMath = Math.sin(time * 0.0035) * Math.cos(time * 0.0027);
        boolean proSnap = ThreadLocalRandom.current().nextFloat() < (0.1f + Math.abs(spartanMath) * 0.15f);

        if (proSnap) {
            float mathJitter = (float) spartanMath * 0.4f;
            currentRotation.x = rawRotations[0] + mathJitter;
            currentRotation.y = rawRotations[1] + mathJitter * 0.7f;
        } else {
            interpolateToTarget(rawRotations[0], rawRotations[1], 7.5f);
        }

        normalizeRotation();
        applyRotationComponent(175, 87, 2, 4);
    }

    private void applyKarhuRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float microX = (float) (Math.sin(time * 0.0067) * 0.03f);
        float microY = (float) (Math.cos(time * 0.0043) * 0.018f);

        rawRotations[0] += microX;
        rawRotations[1] += microY;

        interpolateToTarget(rawRotations[0], rawRotations[1], 9.5f);
        applyRotationComponent(210, 105, 1, 2);
    }

    private void applyKarhuV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float silk1 = (float) (Math.sin(time * 0.0071) * 0.025f);
        float silk2 = (float) (Math.cos(time * 0.0047) * 0.015f);
        float silk3 = (float) (Math.sin(time * 0.0063) * 0.022f);

        float silkModifier = silk1 + silk2 * 0.8f + silk3 * 0.6f;

        rawRotations[0] += silkModifier;
        rawRotations[1] += silkModifier * 0.7f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 10.2f);
        applyRotationComponent(215, 107, 1, 2);
    }

    private void applyKarhuV3Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float ultraSilk1 = (float) (Math.sin(time * 0.0079) * 0.02f);
        float ultraSilk2 = (float) (Math.cos(time * 0.0053) * 0.012f);
        float ultraSilk3 = (float) (Math.sin(time * 0.0069) * 0.018f);
        float ultraSilk4 = (float) (Math.cos(time * 0.0041) * 0.015f);

        float ultraModifier = (ultraSilk1 + ultraSilk2 * 0.9f + ultraSilk3 * 0.7f + ultraSilk4 * 0.8f) * 0.8f;

        rawRotations[0] += ultraModifier;
        rawRotations[1] += ultraModifier * 0.75f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 11.0f);
        applyRotationComponent(220, 110, 1, 2);
    }

    private void applyKarhuProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double karhuMath = Math.sin(time * 0.0083) * Math.cos(time * 0.0057) * 0.015;
        double karhuTrig = Math.tan(time * 0.0031) * 0.008;

        float proSilk = (float) (karhuMath + karhuTrig);

        rawRotations[0] += proSilk;
        rawRotations[1] += proSilk * 0.8f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 12.0f);
        applyRotationComponent(225, 112, 1, 1);
    }


    private void applyFunTimeRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float funWave = (float) (Math.sin(time * 0.004) * 2.2f);
        float funCos = (float) (Math.cos(time * 0.003) * 2.2f * 0.6f);

        rawRotations[0] += funWave * 0.1f;
        rawRotations[1] += funCos * 0.1f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.2f);
        applyRotationComponent(165, 82, 2, 5);
    }

    private void applyFunTimeV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float fun1 = (float) (Math.sin(time * 0.0045) * 1.8f);
        float fun2 = (float) (Math.cos(time * 0.0035) * 1.5f);
        float fun3 = (float) (Math.sin(time * 0.0052) * 2.0f);

        float funMix = (fun1 + fun2 * 0.8f + fun3 * 0.6f) * 0.08f;

        rawRotations[0] += funMix;
        rawRotations[1] += funMix * 0.7f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.8f);
        applyRotationComponent(170, 85, 2, 5);
    }

    private void applyFunTimeProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double funMath = Math.sin(time * 0.0041) * Math.cos(time * 0.0033) * 2.5;
        boolean funBurst = ThreadLocalRandom.current().nextFloat() < 0.1f;

        float funModifier = (float) funMath * 0.12f;
        if (funBurst) {
            funModifier *= 2.0f;
        }

        rawRotations[0] += funModifier;
        rawRotations[1] += funModifier * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.2f);
        applyRotationComponent(175, 87, 2, 4);
    }


    private void applySpookyTimeRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float spooky1 = (float) (Math.sin(time * 0.0013) * 1.8f);
        float spooky2 = (float) (Math.cos(time * 0.0021) * 1.8f * 0.7f);
        float spooky3 = (float) (Math.sin(time * 0.0008) * 1.8f * 1.3f);

        float spookyMix = (spooky1 + spooky3 * 0.5f) * 0.15f;
        rawRotations[0] += spookyMix;
        rawRotations[1] += spooky2 * 0.1f;

        if (ThreadLocalRandom.current().nextFloat() < 0.25f) {
            rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 8.0f;
            rawRotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 3.0f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.8f);
        applyRotationComponent(155, 78, 3, 5);
    }

    private void applySpookyTimeV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float spookyIntensity = (float) (Math.sin(time * 0.002) * 0.5 + 0.5);
        boolean phantomMode = (time % 300) < 100;

        float spookyModifier = spookyIntensity * (phantomMode ? 0.3f : 0.15f);
        rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * spookyModifier * 20.0f;
        rawRotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * spookyModifier * 10.0f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.5f);
        applyRotationComponent(160, 80, 3, 5);
    }

    private void applySpookyTimeProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double spookyMath = Math.sin(time * 0.0017) * Math.cos(time * 0.0023) * Math.tan(time * 0.0011);

        float ultimateSpooky = (float) spookyMath * 0.25f;
        boolean megaPhantom = ThreadLocalRandom.current().nextFloat() < (0.15f + Math.abs(ultimateSpooky));

        if (megaPhantom) {
            rawRotations[0] += ultimateSpooky * 30.0f;
            rawRotations[1] += ultimateSpooky * 15.0f;
        } else {
            rawRotations[0] += ultimateSpooky * 2.0f;
            rawRotations[1] += ultimateSpooky;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 7.0f);
        applyRotationComponent(165, 82, 2, 4);
    }


    private void applyCubeWorldRotation(LivingEntity target) {
        Vector3d targetPos;
        long time = System.currentTimeMillis();
        int corner = (int) ((time / 200) % 8);

        targetPos = getCubeCornerPosition(target, corner);
        float[] rawRotations = calculateRotation(targetPos);

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.5f);
        applyRotationComponent(180, 90, 2, 4);
    }

    private void applyCubeWorldV2Rotation(LivingEntity target) {
        Vector3d targetPos;
        long time = System.currentTimeMillis();

        int corner1 = (int) ((time / 150) % 8);
        int corner2 = (int) ((time / 200) % 8);
        boolean useCorner1 = (time % 100) < 50;

        targetPos = getCubeCornerPosition(target, useCorner1 ? corner1 : corner2);
        float[] rawRotations = calculateRotation(targetPos);

        float cubeRandom = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.1f;
        rawRotations[0] += cubeRandom;
        rawRotations[1] += cubeRandom * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.0f);
        applyRotationComponent(185, 92, 2, 4);
    }

    public String getDebugInfo() {
        long timeSinceLastChange = System.currentTimeMillis() - lastModeChangeTime;
        return String.format("§6[Debug] §eCurrent: §a%s §e| Index: §a%d/%d §e| Time since change: §a%dms §e| Interval: §a%dms",
                currentMode, modeIndex + 1, getAllModes().length, timeSinceLastChange, modeChangeInterval);
    }

    private void changeMode() {
        String[] modes = getAllModes();
        String oldMode = currentMode;

        do {
            modeIndex = ThreadLocalRandom.current().nextInt(modes.length);
            currentMode = modes[modeIndex];
        } while (currentMode.equals(oldMode) && modes.length > 1);

        resetModeSpecificSettings();
    }

    public void setRandomMode() {
        String[] modes = getAllModes();
        String oldMode = currentMode;

        do {
            long timeEntropy = System.currentTimeMillis() % 1000;
            int randomSeed = ThreadLocalRandom.current().nextInt(modes.length) + (int) timeEntropy;
            modeIndex = Math.abs(randomSeed) % modes.length;
            currentMode = modes[modeIndex];
        } while (currentMode.equals(oldMode) && modes.length > 1);

        resetModeSpecificSettings();
    }

    public void setUltraRandomMode() {
        String[] modes = getAllModes();

        long nanoTime = System.nanoTime();
        int mouseX = (int) (Math.random() * 1000);
        int playerRotation = (int) (mc.player != null ? mc.player.rotationYaw : 0);

        int entropy = (int) (nanoTime % 1000) + mouseX + playerRotation + ThreadLocalRandom.current().nextInt(1000);
        modeIndex = Math.abs(entropy) % modes.length;
        currentMode = modes[modeIndex];

        resetModeSpecificSettings();
    }


    private void applyCubeWorldProRotation(LivingEntity target) {
        Vector3d targetPos;
        long time = System.currentTimeMillis();

        double distance = mc.player.getDistance(target);
        int optimalCorner = (int) (distance % 8);
        int timeCorner = (int) ((time / 100) % 8);

        int finalCorner = distance > 5.0 ? optimalCorner : timeCorner;

        targetPos = getCubeCornerPosition(target, finalCorner);
        float[] rawRotations = calculateRotation(targetPos);

        float cubeProModifier = (float) (Math.sin(time * 0.003) * 0.08f);
        rawRotations[0] += cubeProModifier;
        rawRotations[1] += cubeProModifier * 0.7f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.8f);
        applyRotationComponent(190, 95, 1, 3);
    }


    private void applyAdvancedACRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float wave1 = (float) (Math.sin(time * 0.0019) * 0.8);
        float wave2 = (float) (Math.cos(time * 0.0027) * 0.6);
        float wave3 = (float) (Math.sin(time * 0.0013) * 1.1);

        float complexModifier = (wave1 + wave2 * 0.7f + wave3 * 0.4f) * 0.3f;

        rawRotations[0] += complexModifier;
        rawRotations[1] += complexModifier * 0.5f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.2f);
        applyRotationComponent(175, 87, 2, 4);
    }

    private void applyAdvancedV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double advanced1 = Math.sin(time * 0.0023) * Math.cos(time * 0.0031);
        double advanced2 = Math.tan(time * 0.0017) * 0.3;

        float advancedModifier = (float) (advanced1 + advanced2) * 0.2f;

        rawRotations[0] += advancedModifier;
        rawRotations[1] += advancedModifier * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.8f);
        applyRotationComponent(180, 90, 2, 4);
    }

    private void applyAdvancedV3Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double math1 = Math.sin(time * 0.0021) * Math.cos(time * 0.0029);
        double math2 = Math.tan(time * 0.0015) * Math.sin(time * 0.0033);
        double math3 = Math.cos(time * 0.0019) * 0.7;

        float ultraAdvanced = (float) (math1 + math2 * 0.8f + math3 * 0.6f) * 0.15f;

        rawRotations[0] += ultraAdvanced * 2.0f;
        rawRotations[1] += ultraAdvanced;

        interpolateToTarget(rawRotations[0], rawRotations[1], 7.2f);
        applyRotationComponent(185, 92, 2, 4);
    }

    private void applyAdvancedProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double pro1 = Math.sin(time * 0.0025) * Math.cos(time * 0.0033) * Math.tan(time * 0.0019);
        double pro2 = Math.log(Math.abs(Math.sin(time * 0.0027)) + 1) * 0.1;

        float proAdvanced = (float) (pro1 + pro2) * 0.18f;

        rawRotations[0] += proAdvanced * 1.8f;
        rawRotations[1] += proAdvanced * 0.9f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 7.8f);
        applyRotationComponent(190, 95, 1, 3);
    }

    private void applyVanillaPlusRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        float humanError = (float) (ThreadLocalRandom.current().nextGaussian() * 0.1);
        rawRotations[0] += humanError;
        rawRotations[1] += humanError * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 4.5f);
        applyRotationComponent(130, 65, 3, 6);
    }

    private void applyVanillaV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float vanillaWave = (float) (Math.sin(time * 0.001) * 0.05f);
        float humanFactor = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.15f;

        rawRotations[0] += vanillaWave + humanFactor;
        rawRotations[1] += (vanillaWave + humanFactor) * 0.7f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.0f);
        applyRotationComponent(135, 67, 3, 6);
    }

    private void applyVanillaProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        double distance = mc.player.getDistance(target);
        float skillFactor = 0.85f;
        float distanceError = distance > 8.0f ? 0.08f : 0.04f;

        float proVanilla = (ThreadLocalRandom.current().nextFloat() - 0.5f) * distanceError * (1.0f - skillFactor + 0.1f);

        rawRotations[0] += proVanilla;
        rawRotations[1] += proVanilla * 0.8f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.8f);
        applyRotationComponent(140, 70, 3, 5);
    }


    private void applyMegaSmoothRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float butterWave = (float) (Math.sin(time * 0.001) * 0.3 + Math.cos(time * 0.0007) * 0.2);

        rawRotations[0] += butterWave;
        rawRotations[1] += butterWave * 0.4f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 12.0f);
        applyRotationComponent(200, 100, 1, 2);
    }

    private void applyMegaSmoothV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float mega1 = (float) (Math.sin(time * 0.0008) * 0.25f);
        float mega2 = (float) (Math.cos(time * 0.0006) * 0.15f);
        float mega3 = (float) (Math.sin(time * 0.0009) * 0.3f);

        float megaCombo = (mega1 + mega2 * 0.8f + mega3 * 0.6f) * 0.8f;

        rawRotations[0] += megaCombo;
        rawRotations[1] += megaCombo * 0.5f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 14.0f);
        applyRotationComponent(210, 105, 1, 2);
    }

    private void applyMegaSmoothProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double megaMath = Math.sin(time * 0.0007) * Math.cos(time * 0.0005) * 0.4;

        rawRotations[0] += (float) megaMath;
        rawRotations[1] += (float) megaMath * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 16.0f);
        applyRotationComponent(220, 110, 1, 1);
    }

    private void applyUltraSmoothRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double ultra1 = Math.sin(time * 0.0005) * Math.cos(time * 0.0003);
        double ultra2 = Math.tan(time * 0.0002) * 0.1;

        float ultraModifier = (float) (ultra1 + ultra2) * 0.3f;

        rawRotations[0] += ultraModifier;
        rawRotations[1] += ultraModifier * 0.7f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 20.0f);
        applyRotationComponent(250, 125, 1, 1);
    }


    private void applyQuantumRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double quantum1 = Math.sin(time * 0.0031) * Math.cos(time * 0.0047);
        double quantum2 = Math.tan(time * 0.0023) * 0.5;
        double quantum3 = Math.sin(time * 0.0067) * Math.sin(time * 0.0041);

        float quantumModifier = (float) (quantum1 + quantum2 * 0.6 + quantum3 * 0.8) * 0.15f;

        rawRotations[0] += quantumModifier * 2.0f;
        rawRotations[1] += quantumModifier;

        interpolateToTarget(rawRotations[0], rawRotations[1], 8.5f + Math.abs(quantumModifier) * 3.0f);
        applyRotationComponent(190, 95, 1, 3);
    }

    private void applyQuantumV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double quantumField = Math.sin(time * 0.0029) * Math.cos(time * 0.0043) * Math.tan(time * 0.0019);
        boolean quantumFlip = (time % 173) < 50;

        float fieldModifier = (float) quantumField * (quantumFlip ? 0.25f : 0.15f);

        rawRotations[0] += fieldModifier * 3.0f;
        rawRotations[1] += fieldModifier * 1.5f;

        if (quantumFlip && ThreadLocalRandom.current().nextFloat() < 0.1f) {
            rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2.0f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 9.0f);
        applyRotationComponent(195, 97, 1, 3);
    }

    private void applyNeuralRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float input1 = (float) Math.sin(time * 0.0033);
        float input2 = (float) Math.cos(time * 0.0027);
        float input3 = (float) Math.tan(time * 0.0019);

        float neuron1 = input1 * 0.6f + input2 * 0.3f + input3 * 0.1f;
        float neuron2 = input1 * 0.2f + input2 * 0.7f + input3 * 0.1f;
        float output = neuron1 * 0.8f + neuron2 * 0.2f;

        rawRotations[0] += output * 0.2f;
        rawRotations[1] += output * 0.1f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 7.5f + Math.abs(output) * 2.0f);
        applyRotationComponent(185, 92, 2, 4);
    }

    private void applyNeuralV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float[] inputs = {
                (float) Math.sin(time * 0.0031),
                (float) Math.cos(time * 0.0029),
                (float) Math.tan(time * 0.0017),
                (float) Math.sin(time * 0.0041),
                (float) Math.cos(time * 0.0037)
        };

        float[] hidden = new float[3];
        hidden[0] = inputs[0] * 0.5f + inputs[1] * 0.3f + inputs[2] * 0.2f;
        hidden[1] = inputs[1] * 0.4f + inputs[2] * 0.3f + inputs[3] * 0.3f;
        hidden[2] = inputs[2] * 0.2f + inputs[3] * 0.4f + inputs[4] * 0.4f;

        float output1 = hidden[0] * 0.6f + hidden[1] * 0.2f + hidden[2] * 0.2f;
        float output2 = hidden[0] * 0.3f + hidden[1] * 0.4f + hidden[2] * 0.3f;

        rawRotations[0] += output1 * 0.25f;
        rawRotations[1] += output2 * 0.15f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 8.0f);
        applyRotationComponent(190, 95, 2, 4);
    }

    private void applyAdaptiveRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        Vector3d targetMotion = target.getMotion();
        double motionIntensity = targetMotion.length();

        float adaptiveSmooth = (float) (6.0f + motionIntensity * 4.0f);
        float adaptiveRandom = (float) (0.1f + motionIntensity * 0.15f);

        rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * adaptiveRandom;
        rawRotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * adaptiveRandom * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], adaptiveSmooth);
        applyRotationComponent(175, 87, 2, 4);
    }

    private void applyAdaptiveV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        double distance = mc.player.getDistance(target);
        Vector3d targetMotion = target.getMotion();
        double motionIntensity = targetMotion.length();

        float distanceModifier = distance > 5.0 ? 1.5f : 1.0f;
        float motionModifier = (float) (1.0f + motionIntensity * 2.0f);

        float adaptiveSmooth = 6.5f * distanceModifier / motionModifier;
        float adaptiveRandom = 0.12f * motionModifier;

        rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * adaptiveRandom;
        rawRotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * adaptiveRandom * 0.7f;

        interpolateToTarget(rawRotations[0], rawRotations[1], adaptiveSmooth);
        applyRotationComponent(180, 90, 2, 4);
    }

    private void applyEvolvedRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float generation = (time / 10000) % 100;
        float mutation = (float) (Math.sin(generation * 0.1) * 0.2f);

        rawRotations[0] += mutation;
        rawRotations[1] += mutation * 0.6f;

        float evolvedSmooth = 7.0f + Math.abs(mutation) * 3.0f;
        interpolateToTarget(rawRotations[0], rawRotations[1], evolvedSmooth);
        applyRotationComponent(185, 92, 2, 4);
    }

    private void applyEvolvedV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float gene1 = (float) (Math.sin((time / 8000) % 50 * 0.2) * 0.15f);
        float gene2 = (float) (Math.cos((time / 12000) % 75 * 0.15) * 0.12f);
        float gene3 = (float) (Math.tan((time / 15000) % 100 * 0.1) * 0.08f);

        float evolution = gene1 + gene2 * 0.8f + gene3 * 0.6f;

        rawRotations[0] += evolution * 1.5f;
        rawRotations[1] += evolution * 0.9f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 7.5f);
        applyRotationComponent(190, 95, 2, 4);
    }


    private void applyLightningRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        if (ThreadLocalRandom.current().nextFloat() < 0.7f) {
            interpolateToTarget(rawRotations[0], rawRotations[1], 1.5f);
        }

        applyRotationComponent(300, 150, 0, 2);
    }

    private void applyLightningV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        boolean lightningStrike = (time % 100) < 20;

        if (lightningStrike) {
            currentRotation.x = rawRotations[0];
            currentRotation.y = rawRotations[1];
        } else {
            interpolateToTarget(rawRotations[0], rawRotations[1], 2.0f);
        }

        normalizeRotation();
        applyRotationComponent(320, 160, 0, 2);
    }

    private void applyInstantRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        currentRotation.x = rawRotations[0];
        currentRotation.y = rawRotations[1];

        normalizeRotation();
        applyRotationComponent(360, 180, 0, 1);
    }

    private void applyInstantV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        float instantJitter = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.1f;

        currentRotation.x = rawRotations[0] + instantJitter;
        currentRotation.y = rawRotations[1] + instantJitter * 0.6f;

        normalizeRotation();
        applyRotationComponent(360, 180, 0, 1);
    }

    private void applyFlashRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        boolean flash = (time % 75) < 25;

        if (flash) {
            currentRotation.x = rawRotations[0];
            currentRotation.y = rawRotations[1];
        }

        normalizeRotation();
        applyRotationComponent(280, 140, 0, 3);
    }

    private void applyFlashV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float flashIntensity = (float) (Math.sin(time * 0.02) * 0.5 + 0.5);
        boolean flash = ThreadLocalRandom.current().nextFloat() < flashIntensity;

        if (flash) {
            float flashJitter = flashIntensity * 0.2f;
            currentRotation.x = rawRotations[0] + (ThreadLocalRandom.current().nextFloat() - 0.5f) * flashJitter;
            currentRotation.y = rawRotations[1] + (ThreadLocalRandom.current().nextFloat() - 0.5f) * flashJitter * 0.6f;
        }

        normalizeRotation();
        applyRotationComponent(290, 145, 0, 3);
    }


    private void applySilkRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float silkWave = (float) (Math.sin(time * 0.002) * 0.8 + Math.cos(time * 0.0015) * 0.6);

        rawRotations[0] += silkWave * 0.05f;
        rawRotations[1] += silkWave * 0.03f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 12.0f);
        applyRotationComponent(220, 110, 1, 2);
    }

    private void applySilkV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float silk1 = (float) (Math.sin(time * 0.0018) * 0.6);
        float silk2 = (float) (Math.cos(time * 0.0013) * 0.4);
        float silk3 = (float) (Math.sin(time * 0.0021) * 0.7);

        float ultraSilk = (silk1 + silk2 * 0.8f + silk3 * 0.6f) * 0.04f;

        rawRotations[0] += ultraSilk;
        rawRotations[1] += ultraSilk * 0.7f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 13.5f);
        applyRotationComponent(230, 115, 1, 2);
    }

    private void applyButterRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float butterWave = (float) (Math.sin(time * 0.001) * 0.3 + Math.cos(time * 0.0007) * 0.2);

        rawRotations[0] += butterWave * 0.6f;
        rawRotations[1] += butterWave * 0.4f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 15.0f);
        applyRotationComponent(240, 120, 1, 2);
    }

    private void applyButterV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float butter1 = (float) (Math.sin(time * 0.0009) * 0.25);
        float butter2 = (float) (Math.cos(time * 0.0006) * 0.15);
        float butter3 = (float) (Math.sin(time * 0.0011) * 0.3);

        float megaButter = (butter1 + butter2 * 0.7f + butter3 * 0.9f) * 0.3f;

        rawRotations[0] += megaButter;
        rawRotations[1] += megaButter * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 16.5f);
        applyRotationComponent(250, 125, 1, 1);
    }

    private void applyVelvetRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double velvetMath = Math.sin(time * 0.0008) * Math.cos(time * 0.0005);

        rawRotations[0] += (float) velvetMath * 0.4f;
        rawRotations[1] += (float) velvetMath * 0.25f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 18.0f);
        applyRotationComponent(260, 130, 1, 1);
    }

    private void applyVelvetV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        double velvet1 = Math.sin(time * 0.0007) * Math.cos(time * 0.0004);
        double velvet2 = Math.tan(time * 0.0003) * 0.1;

        float ultraVelvet = (float) (velvet1 + velvet2) * 0.3f;

        rawRotations[0] += ultraVelvet;
        rawRotations[1] += ultraVelvet * 0.7f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 20.0f);
        applyRotationComponent(270, 135, 1, 1);
    }


    private void apply1TickAttackRotation(LivingEntity target) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastAttackTime >= 50) {
            Vector3d targetPos = getBestTargetPosition(target);
            float[] rawRotations = calculateRotation(targetPos);

            currentRotation.x = rawRotations[0];
            currentRotation.y = rawRotations[1];

            lastAttackTime = currentTime;
        }

        normalizeRotation();
        applyRotationComponent(360, 180, 0, 1);
    }

    private void apply2TickAttackRotation(LivingEntity target) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastAttackTime >= 100) {
            Vector3d targetPos = getBestTargetPosition(target);
            float[] rawRotations = calculateRotation(targetPos);

            float yawDelta = getAngleDifference(rawRotations[0], currentRotation.x);
            float pitchDelta = getAngleDifference(rawRotations[1], currentRotation.y);

            currentRotation.x += yawDelta * 0.5f;
            currentRotation.y += pitchDelta * 0.5f;

            lastAttackTime = currentTime;
        }

        normalizeRotation();
        applyRotationComponent(300, 150, 0, 2);
    }

    private void apply3TickAttackRotation(LivingEntity target) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastAttackTime >= 150) {
            Vector3d targetPos = getBestTargetPosition(target);
            float[] rawRotations = calculateRotation(targetPos);

            interpolateToTarget(rawRotations[0], rawRotations[1], 3.0f);
            lastAttackTime = currentTime;
        }

        normalizeRotation();
        applyRotationComponent(250, 125, 1, 3);
    }

    private void apply4TickAttackRotation(LivingEntity target) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastAttackTime >= 200) {
            Vector3d targetPos = getBestTargetPosition(target);
            float[] rawRotations = calculateRotation(targetPos);

            interpolateToTarget(rawRotations[0], rawRotations[1], 4.0f);
            lastAttackTime = currentTime;
        }

        normalizeRotation();
        applyRotationComponent(200, 100, 2, 4);
    }

    private void apply5TickAttackRotation(LivingEntity target) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastAttackTime >= 250) {
            Vector3d targetPos = getBestTargetPosition(target);
            float[] rawRotations = calculateRotation(targetPos);

            interpolateToTarget(rawRotations[0], rawRotations[1], 5.0f);
            lastAttackTime = currentTime;
        }

        normalizeRotation();
        applyRotationComponent(180, 90, 2, 5);
    }


    private void applyLegitRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        float humanError = (float) (ThreadLocalRandom.current().nextGaussian() * 0.15);
        float mouseAccel = 1.0f + (float) (Math.random() * 0.3);

        rawRotations[0] += humanError;
        rawRotations[1] += humanError * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 4.5f * mouseAccel);
        applyRotationComponent(130, 65, 3, 6);
    }

    private void applyLegitV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float humanJitter = (float) (Math.sin(time * 0.008) * 0.1 + ThreadLocalRandom.current().nextGaussian() * 0.05);
        float mouseFlick = ThreadLocalRandom.current().nextFloat() < 0.05f ? (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2.0f : 0;

        rawRotations[0] += humanJitter + mouseFlick;
        rawRotations[1] += humanJitter * 0.7f + mouseFlick * 0.3f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.2f);
        applyRotationComponent(135, 67, 3, 6);
    }

    private void applyLegitProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        float proError = (float) (ThreadLocalRandom.current().nextGaussian() * 0.08);
        double distance = mc.player.getDistance(target);
        float difficultyModifier = distance > 10.0 ? 1.3f : 1.0f;

        rawRotations[0] += proError * difficultyModifier;
        rawRotations[1] += proError * 0.8f * difficultyModifier;

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.0f);
        applyRotationComponent(140, 70, 3, 5);
    }

    private void applyHumanRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        float humanError = (float) (ThreadLocalRandom.current().nextGaussian() * 0.2);
        float fatigue = (float) (Math.sin(System.currentTimeMillis() * 0.0001) * 0.1);

        rawRotations[0] += humanError + fatigue;
        rawRotations[1] += humanError * 0.7f + fatigue * 0.5f;

        float humanSpeed = 4.0f + (float) (Math.random() * 2.0f);
        interpolateToTarget(rawRotations[0], rawRotations[1], humanSpeed);
        applyRotationComponent(120, 60, 4, 7);
    }

    private void applyHumanV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float stress = (float) (Math.sin(time * 0.0001) * 0.15f);
        float focus = 1.0f - Math.abs(stress);
        float humanJitter = (ThreadLocalRandom.current().nextFloat() - 0.5f) * (0.3f - focus * 0.2f);

        rawRotations[0] += humanJitter + stress;
        rawRotations[1] += humanJitter * 0.6f + stress * 0.4f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 4.5f * focus + 2.0f);
        applyRotationComponent(125, 62, 4, 7);
    }

    private void applyHumanProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        double distance = mc.player.getDistance(target);
        float skill = 0.9f;
        float pressure = distance > 10.0f ? 0.1f : 0.05f;

        float proError = (ThreadLocalRandom.current().nextFloat() - 0.5f) * pressure;
        rawRotations[0] += proError;
        rawRotations[1] += proError * 0.8f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.5f + (1.0f - skill) * 2.0f);
        applyRotationComponent(135, 67, 3, 5);
    }

    private void applyRageRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        boolean rageMode = ThreadLocalRandom.current().nextFloat() < 0.3f;
        if (rageMode) {
            currentRotation.x = rawRotations[0];
            currentRotation.y = rawRotations[1];
        } else {
            interpolateToTarget(rawRotations[0], rawRotations[1], 2.0f);
        }

        normalizeRotation();
        applyRotationComponent(300, 150, 0, 2);
    }

    private void applyRageV2Rotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        boolean megaRage = (time % 200) < 80;

        if (megaRage) {
            float rageJitter = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 3.0f;
            currentRotation.x = rawRotations[0] + rageJitter;
            currentRotation.y = rawRotations[1] + rageJitter * 0.5f;
        } else {
            interpolateToTarget(rawRotations[0], rawRotations[1], 1.5f);
        }

        normalizeRotation();
        applyRotationComponent(320, 160, 0, 2);
    }

    private void applyRageProRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        double distance = mc.player.getDistance(target);
        boolean controlledRage = distance < 3.0f && ThreadLocalRandom.current().nextFloat() < 0.4f;

        if (controlledRage) {
            float proRageJitter = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2.0f;
            currentRotation.x = rawRotations[0] + proRageJitter;
            currentRotation.y = rawRotations[1] + proRageJitter * 0.6f;
        } else {
            interpolateToTarget(rawRotations[0], rawRotations[1], 2.5f);
        }

        normalizeRotation();
        applyRotationComponent(280, 140, 0, 3);
    }

    private void applyStealthRotation(LivingEntity target) {
        long time = System.currentTimeMillis();

        if (time - lastRotationUpdate > 100 + ThreadLocalRandom.current().nextInt(50)) {
            Vector3d targetPos = getBestTargetPosition(target);
            float[] rawRotations = calculateRotation(targetPos);

            interpolateToTarget(rawRotations[0], rawRotations[1], 8.0f);
            lastRotationUpdate = time;
        }

        applyRotationComponent(140, 70, 3, 6);
    }

    private void applyStealthV2Rotation(LivingEntity target) {
        long time = System.currentTimeMillis();

        int stealthDelay = 80 + ThreadLocalRandom.current().nextInt(80);
        boolean stealthActive = (time % 500) < 200;

        if (stealthActive && time - lastRotationUpdate > stealthDelay) {
            Vector3d targetPos = getBestTargetPosition(target);
            float[] rawRotations = calculateRotation(targetPos);

            float stealthJitter = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.05f;
            rawRotations[0] += stealthJitter;
            rawRotations[1] += stealthJitter * 0.7f;

            interpolateToTarget(rawRotations[0], rawRotations[1], 9.0f);
            lastRotationUpdate = time;
        }

        applyRotationComponent(145, 72, 3, 6);
    }

    private void applyStealthProRotation(LivingEntity target) {
        long time = System.currentTimeMillis();

        double distance = mc.player.getDistance(target);
        int adaptiveDelay = distance > 5.0f ? 120 : 80;
        adaptiveDelay += ThreadLocalRandom.current().nextInt(60);

        boolean proStealth = (time % 800) < 300;

        if (proStealth && time - lastRotationUpdate > adaptiveDelay) {
            Vector3d targetPos = getBestTargetPosition(target);
            float[] rawRotations = calculateRotation(targetPos);

            float proMicro = (float) (Math.sin(time * 0.001) * 0.03f);
            rawRotations[0] += proMicro;
            rawRotations[1] += proMicro * 0.8f;

            interpolateToTarget(rawRotations[0], rawRotations[1], 10.0f);
            lastRotationUpdate = time;
        }

        applyRotationComponent(150, 75, 3, 5);
    }


    private void applyMineplexRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        boolean mineplexMode = (time % 250) < 100;

        float mineplexModifier = mineplexMode ? 0.12f : 0.08f;
        rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * mineplexModifier;
        rawRotations[1] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * mineplexModifier * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.0f);
        applyRotationComponent(160, 80, 3, 5);
    }

    private void applyHypixelRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float hypixelWave = (float) (Math.sin(time * 0.0021) * 0.1f + Math.cos(time * 0.0017) * 0.07f);

        rawRotations[0] += hypixelWave;
        rawRotations[1] += hypixelWave * 0.7f;

        if ((time % 91) == 0) {
            rawRotations[0] += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.15f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 7.5f);
        applyRotationComponent(180, 90, 2, 4);
    }

    private void applyCubeCraftRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        boolean cubecraftSafe = ThreadLocalRandom.current().nextFloat() < 0.85f;

        if (cubecraftSafe) {
            float safeModifier = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.06f;
            rawRotations[0] += safeModifier;
            rawRotations[1] += safeModifier * 0.8f;
        }

        interpolateToTarget(rawRotations[0], rawRotations[1], 8.2f);
        applyRotationComponent(155, 77, 3, 5);
    }

    private void applyHiveRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        float hivePattern = (float) (Math.sin(time * 0.0031) * 0.08f);

        rawRotations[0] += hivePattern;
        rawRotations[1] += hivePattern * 0.6f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 6.8f);
        applyRotationComponent(165, 82, 3, 5);
    }

    private void applyMinehutRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        float minehutRandom = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.1f;
        rawRotations[0] += minehutRandom;
        rawRotations[1] += minehutRandom * 0.7f;

        interpolateToTarget(rawRotations[0], rawRotations[1], 5.5f);
        applyRotationComponent(150, 75, 3, 6);
    }

    private void applyAternosRotation(LivingEntity target) {
        Vector3d targetPos = getBestTargetPosition(target);
        float[] rawRotations = calculateRotation(targetPos);

        long time = System.currentTimeMillis();
        boolean aternosBoost = (time % 300) < 100;

        float aternosSpeed = aternosBoost ? 4.8f : 6.2f;
        interpolateToTarget(rawRotations[0], rawRotations[1], aternosSpeed);
        applyRotationComponent(140, 70, 4, 6);
    }


    private void interpolateToTarget(float targetYaw, float targetPitch, float speed) {
        float yawDelta = getAngleDifference(targetYaw, currentRotation.x);
        float pitchDelta = getAngleDifference(targetPitch, currentRotation.y);

        currentRotation.x += yawDelta / speed;
        currentRotation.y += pitchDelta / speed;

        normalizeRotation();
    }

    private void applyGCDCorrection() {
        if (!useGCD) return;

        float sensitivity = (float) (mc.gameSettings.mouseSensitivity * 0.6F + 0.2F);
        float gcd = sensitivity * sensitivity * sensitivity * 1.2F;

        currentRotation.x = Math.round(currentRotation.x / gcd) * gcd;
        currentRotation.y = Math.round(currentRotation.y / gcd) * gcd;
    }

    private Vector3d getCubeCornerPosition(LivingEntity target, int corner) {
        double minX = target.getBoundingBox().minX;
        double minY = target.getBoundingBox().minY;
        double minZ = target.getBoundingBox().minZ;
        double maxX = target.getBoundingBox().maxX;
        double maxY = target.getBoundingBox().maxY;
        double maxZ = target.getBoundingBox().maxZ;

        switch (corner % 8) {
            case 0:
                return new Vector3d(minX, minY, minZ);
            case 1:
                return new Vector3d(maxX, minY, minZ);
            case 2:
                return new Vector3d(minX, maxY, minZ);
            case 3:
                return new Vector3d(maxX, maxY, minZ);
            case 4:
                return new Vector3d(minX, minY, maxZ);
            case 5:
                return new Vector3d(maxX, minY, maxZ);
            case 6:
                return new Vector3d(minX, maxY, maxZ);
            case 7:
                return new Vector3d(maxX, maxY, maxZ);
            default:
                return target.getPositionVec().add(0, target.getEyeHeight(), 0);
        }
    }

    public void onPlayerDamage() {
        if (stealthSystem != null) {
            stealthSystem.onPlayerDamage();
        }
    }

    public StealthRotationSystem getStealthSystem() {
        return stealthSystem;
    }

    private float[] calculateRotation(Vector3d targetPos) {
        Vector3d playerPos = mc.player.getEyePosition(1.0F);
        Vector3d direction = targetPos.subtract(playerPos);

        double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, horizontalDistance));

        return new float[]{yaw, pitch};
    }

    private float getAngleDifference(float target, float current) {
        float difference = target - current;
        while (difference > 180.0F) difference -= 360.0F;
        while (difference < -180.0F) difference += 360.0F;
        return difference;
    }

    private void normalizeRotation() {
        currentRotation.x = wrapAngleTo180(currentRotation.x);
        currentRotation.y = MathHelper.clamp(currentRotation.y, -90.0F, 90.0F);
    }

    private float wrapAngleTo180(float angle) {
        angle = angle % 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

    private void applyRotationComponent(float maxYawSpeed, float maxPitchSpeed, int minYawSpeed, int maxYawSpeedLimit) {
        Rotation rotation = new Rotation(currentRotation.x, currentRotation.y);
        RotationComponent.update(rotation, maxYawSpeed, maxPitchSpeed, minYawSpeed, maxYawSpeedLimit);
    }


    public void applyToRotationComponent(LivingEntity target) {
        updateRotation(target);
    }

    public void applyToRotationComponent(LivingEntity target, String specificMode) {
        String originalMode = this.currentMode;
        this.currentMode = specificMode;
        updateRotation(target);
        this.currentMode = originalMode;
    }

    public String[] getAllModes() {
        return new String[]{
                "Polar", "PolarV2", "PolarV3", "Polar+", "PolarPro", "PolarUltimate",
                "Intave", "IntaveV2", "IntaveV3", "Intave+", "IntaveNew", "IntaveNewV2", "IntavePro", "IntaveUltimate",
                "Matrix", "MatrixV2", "MatrixV3", "MatrixPro", "MatrixUltimate",
                "Verus", "VerusV2", "VerusV3", "VerusPro",
                "Vulcan", "VulcanV2", "VulcanV3", "VulcanPro",
                "Watchdog", "WatchdogV2", "WatchdogV3", "WatchdogPro",
                "GrimAC", "GrimV2", "GrimV3", "GrimPro",
                "Spartan", "SpartanV2", "SpartanV3", "SpartanPro",
                "Karhu", "KarhuV2", "KarhuV3", "KarhuPro",
                "HolyWorld", "HolyWorldV2", "HolyWorldPro",
                "FunTime", "FunTimeV2", "FunTimePro",
                "SpookyTime", "SpookyTimeV2", "SpookyTimePro",
                "CubeWorld", "CubeWorldV2", "CubeWorldPro",
                "AdvancedAC", "AdvancedV2", "AdvancedV3", "AdvancedPro",
                "Vanilla+", "VanillaV2", "VanillaPro",
                "MegaSmooth", "MegaSmoothV2", "MegaSmoothPro", "UltraSmooth",
                "PolarIntave", "PolarMatrix", "IntaveMatrix", "VulcanGrim", "WatchdogKarhu", "SuperHybrid",
                "Quantum", "QuantumV2", "Neural", "NeuralV2", "Adaptive", "AdaptiveV2", "Evolved", "EvolvedV2",
                "Lightning", "LightningV2", "Instant", "InstantV2", "Flash", "FlashV2",
                "Silk", "SilkV2", "Butter", "ButterV2", "Velvet", "VelvetV2",
                "1TickAttack", "2TickAttack", "3TickAttack", "4TickAttack", "5TickAttack",
                "Legit", "LegitV2", "LegitPro", "Human", "HumanV2", "HumanPro",
                "Rage", "RageV2", "RagePro", "Stealth", "StealthV2", "StealthPro",
                "Mineplex", "Hypixel", "CubeCraft", "Hive", "Minehut", "Aternos"
        };
    }

    public void nextMode() {
        setRandomMode();
    }

    public void setMode(String mode) {
        this.currentMode = mode;
        resetModeSpecificSettings();
    }

    public String getCurrentMode() {
        return currentMode;
    }

    public Vector2f getCurrentRotation() {
        return new Vector2f(currentRotation.x, currentRotation.y);
    }

    public float getYaw() {
        return currentRotation.x;
    }

    public float getPitch() {
        return currentRotation.y;
    }

    public void resetSystem() {
        currentRotation = new Vector2f(mc.player != null ? mc.player.rotationYaw : 0,
                mc.player != null ? mc.player.rotationPitch : 0);
        lastRotation = new Vector2f(currentRotation.x, currentRotation.y);
        lerpRotation = new Vector2f(currentRotation.x, currentRotation.y);

        lastModeChangeTime = 0;
        lastAttackTime = 0;
        lastRotationUpdate = 0;
        systemStartTime = System.currentTimeMillis();

        setRandomMode();

    }

}

