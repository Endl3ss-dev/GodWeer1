package fun.volon.api.storages.implement;

import fun.volon.api.utils.ai.SimpleNeuralNetwork;

import java.util.ArrayList;
import java.util.List;

public class NeuroLLMStorage {

    // Нейросеть: 16 входов, 32 нейрона, 4 выхода
    // [yawOffset, pitchOffset, attackDelay, rotationSpeed]
    private static final SimpleNeuralNetwork network = new SimpleNeuralNetwork(16, 32, 4);

    // ---- Настройки (чекбоксы из Aura) ----
    public static boolean learnOnKick      = true;
    public static boolean learnOnFlag      = true;
    public static boolean learnOnMiss      = true;
    public static boolean learnOnHit       = true;
    public static boolean adaptDelay       = true;
    public static boolean adaptRotSpeed    = true;
    public static boolean adaptYawOffset   = true;
    public static boolean adaptPitchOffset = true;
    public static boolean experienceReplay = true;
    public static boolean showStats        = true;

    // ---- Статистика ----
    private static int totalHits    = 0;
    private static int totalMisses  = 0;
    private static int totalFlags   = 0;
    private static int totalKicks   = 0;
    private static int sessionHits  = 0;

    // ---- Детект срезания урона ----
    private static int   totalDamageReduced  = 0;
    private static float lastExpectedDamage  = 0f;
    private static float lastActualDamage    = 0f;
    private static float damageReductionRate = 0f;

    // ---- Последнее состояние для обучения ----
    private static double[] lastInput    = null;
    private static double[] lastOutput   = null;

    // ---- Текущие адаптивные параметры ----
    private static double currentYawOffset   = 0.0;
    private static double currentPitchOffset = 0.0;
    private static double currentDelay       = 0.0;
    private static double currentRotSpeed    = 0.35;

    // ---- История (experience replay) ----
    private static final List<TrainingEntry> history = new ArrayList<>();
    private static final int MAX_HISTORY = 200;

    // ---- Штрафные паттерны ----
    private static final List<double[]> penaltyPatterns = new ArrayList<>();
    private static final int MAX_PENALTY = 50;

    // -----------------------------------------------------------------------
    // Геттеры
    // -----------------------------------------------------------------------

    public static SimpleNeuralNetwork getNetwork()       { return network; }
    public static int    getTotalHits()                  { return totalHits; }
    public static int    getTotalMisses()                { return totalMisses; }
    public static int    getTotalFlags()                 { return totalFlags; }
    public static int    getTotalKicks()                 { return totalKicks; }
    public static int    getSessionHits()                { return sessionHits; }
    public static int    getTotalDamageReduced()         { return totalDamageReduced; }
    public static float  getLastExpectedDamage()         { return lastExpectedDamage; }
    public static float  getLastActualDamage()           { return lastActualDamage; }
    public static float  getDamageReductionRate()        { return damageReductionRate; }
    public static double getCurrentYawOffset()           { return currentYawOffset; }
    public static double getCurrentPitchOffset()         { return currentPitchOffset; }
    public static double getCurrentDelay()               { return currentDelay; }
    public static double getCurrentRotSpeed()            { return currentRotSpeed; }

    // -----------------------------------------------------------------------
    // Предсказание
    // -----------------------------------------------------------------------

    /**
     * Обновляет lastInput без вызова predict.
     * Используется для постоянного отслеживания состояния.
     */
    public static void updateLastInput(double[] input) {
        if (input != null && input.length == 16) {
            lastInput = input.clone();
        }
    }

    public static double[] predict(
            double playerYaw,  double playerPitch,
            double targetYaw,  double targetPitch,
            double distance,
            double tVelX, double tVelY, double tVelZ,
            double pVelX, double pVelY, double pVelZ,
            double timeSinceHit,
            double flagRate, double kickRate,
            double accuracy, double pingNorm
    ) {
        double[] input = buildInput(playerYaw, playerPitch, targetYaw, targetPitch,
                distance, tVelX, tVelY, tVelZ, pVelX, pVelY, pVelZ,
                timeSinceHit, flagRate, kickRate, accuracy, pingNorm);

        lastInput  = input;
        lastOutput = network.forward(input);

        double yawOff  = adaptYawOffset   ? lastOutput[0] * 12.0 : 0.0;
        double pitchOff= adaptPitchOffset ? lastOutput[1] * 8.0  : 0.0;
        double delay   = adaptDelay       ? Math.max(0, lastOutput[2] * 250.0) : 0.0;
        double rotSpd  = adaptRotSpeed    ? 0.2 + Math.abs(lastOutput[3]) * 0.6 : 0.35;

        currentYawOffset   = yawOff;
        currentPitchOffset = pitchOff;
        currentDelay       = delay;
        currentRotSpeed    = rotSpd;

        return new double[]{ yawOff, pitchOff, delay, rotSpd };
    }

    // -----------------------------------------------------------------------
    // Обучение
    // -----------------------------------------------------------------------

    public static void onHit() {
        totalHits++;
        sessionHits++;
        if (learnOnHit && lastInput != null) {
            double[] t = cloneOutput();
            // Усиливаем успешный паттерн более агрессивно
            t[0] *= 1.08; // увеличиваем yaw offset
            t[1] *= 1.08; // увеличиваем pitch offset
            t[3] *= 1.03; // немного увеличиваем скорость ротации
            network.train(lastInput, t, 1.5); // увеличенная награда
            addHistory(lastInput, t, 1.5);
        }
        maybeAdaptLR();
        maybeReplay();
    }

    public static void onMiss() {
        totalMisses++;
        if (learnOnMiss && lastInput != null) {
            double[] t = cloneOutput();
            // Корректируем углы при промахе более точно
            double distance = lastInput[4] * 6.0; // восстанавливаем дистанцию
            if (distance > 3.0) {
                // На дальней дистанции увеличиваем смещения
                t[0] *= 1.15;
                t[1] *= 1.15;
            } else {
                // На ближней дистанции уменьшаем
                t[0] *= 0.85;
                t[1] *= 0.85;
            }
            t[3] *= 0.9; // замедляем ротацию
            network.train(lastInput, t, -0.8);
            addHistory(lastInput, t, -0.8);
        }
        maybeAdaptLR();
        maybeReplay();
    }

    public static void onFlag() {
        totalFlags++;
        if (learnOnFlag && lastInput != null) {
            double[] t = cloneOutput();
            // Радикально снижаем агрессивность при флаге
            t[2] = Math.min(1.0, t[2] + 0.35);  // сильно увеличиваем задержку
            t[3] = Math.max(-1.0, t[3] - 0.25); // сильно замедляем ротацию
            t[0] *= 0.3;  // радикально уменьшаем yaw offset
            t[1] *= 0.3;  // радикально уменьшаем pitch offset
            network.train(lastInput, t, -2.0);
            addHistory(lastInput, t, -2.0);
            addPenaltyPattern(lastInput);
            // Интенсивное переобучение для закрепления
            for (int i = 0; i < 5; i++) {
                network.train(lastInput, t, -1.8);
            }
        }
        maybeAdaptLR();
        maybeReplay();
    }

    public static void onKick(String reason) {
        totalKicks++;
        if (learnOnKick && lastInput != null) {
            double[] t = cloneOutput();
            // Экстремально консервативная стратегия при кике
            t[0] = 0.0;   // полностью убираем yaw offset
            t[1] = 0.0;   // полностью убираем pitch offset
            t[2] = 1.0;   // максимальная задержка
            t[3] = -0.8;  // очень медленная ротация
            network.train(lastInput, t, -4.0); // максимальный штраф
            addHistory(lastInput, t, -4.0);
            addPenaltyPattern(lastInput);
            // Экстремальное переобучение (15 раз)
            for (int i = 0; i < 15; i++) {
                network.train(lastInput, t, -3.5);
            }
        }
        maybeAdaptLR();
        // Массивный replay после кика
        for (int i = 0; i < 10; i++) {
            maybeReplay();
        }
    }

    public static void onDamageReduced(float expected, float actual) {
        totalDamageReduced++;
        lastExpectedDamage = expected;
        lastActualDamage   = actual;
        float reduction = expected > 0 ? 1f - (actual / expected) : 0f;
        damageReductionRate = damageReductionRate * 0.7f + reduction * 0.3f;

        if (lastInput != null) {
            double[] t = cloneOutput();
            // Адаптивная корректировка в зависимости от степени срезания
            float severity = reduction; // 0.0 = нет срезания, 1.0 = полное срезание
            t[2] = Math.min(1.0, t[2] + 0.2 + severity * 0.3);  // задержка зависит от серьезности
            t[0] *= (1.0 - severity * 0.5);  // yaw offset
            t[1] *= (1.0 - severity * 0.5);  // pitch offset
            t[3] *= (1.0 - severity * 0.3);  // скорость ротации
            
            double penalty = -1.0 - severity * 1.5; // штраф от -1.0 до -2.5
            network.train(lastInput, t, penalty);
            addHistory(lastInput, t, penalty);
            addPenaltyPattern(lastInput);
            
            // Дополнительное обучение пропорционально серьезности
            int extraTraining = (int)(severity * 4) + 1;
            for (int i = 0; i < extraTraining; i++) {
                network.train(lastInput, t, penalty * 0.8);
            }
        }
        maybeAdaptLR();
        maybeReplay();
    }

    // -----------------------------------------------------------------------
    // Паттерны
    // -----------------------------------------------------------------------

    public static boolean isNearPenaltyPattern(double[] input) {
        for (double[] penalty : penaltyPatterns) {
            if (euclidean(input, penalty) < 0.25) return true;
        }
        return false;
    }

    // -----------------------------------------------------------------------
    // Сброс
    // -----------------------------------------------------------------------

    public static void resetSession() {
        sessionHits = 0;
    }

    public static void resetAll() {
        totalHits = totalMisses = totalFlags = totalKicks = sessionHits = 0;
        totalDamageReduced = 0;
        damageReductionRate = 0f;
        history.clear();
        penaltyPatterns.clear();
    }

    // -----------------------------------------------------------------------
    // Статистика
    // -----------------------------------------------------------------------

    public static double getAccuracy() {
        int total = totalHits + totalMisses;
        return total == 0 ? 0.0 : totalHits * 100.0 / total;
    }

    public static double getFlagRate() {
        int total = totalHits + totalMisses;
        return total == 0 ? 0.0 : totalFlags * 100.0 / total;
    }

    public static String getStatsLine() {
        return String.format(
            "§aNeuro§7: acc §f%.1f%%§7 flag §c%.1f%%§7 kick §4%d§7 lr §e%.4f",
            getAccuracy(), getFlagRate(), totalKicks, network.getLearningRate()
        );
    }

    public static String getDamageStats() {
        return String.format(
            "§e[NeuroLLM] §7Срезание: §c%.0f%%§7 ожид §f%.1f§7 реал §f%.1f§7 раз §c%d",
            damageReductionRate * 100, lastExpectedDamage, lastActualDamage, totalDamageReduced
        );
    }

    // -----------------------------------------------------------------------
    // Приватные утилиты
    // -----------------------------------------------------------------------

    private static double[] buildInput(
            double playerYaw, double playerPitch,
            double targetYaw, double targetPitch,
            double distance,
            double tVelX, double tVelY, double tVelZ,
            double pVelX, double pVelY, double pVelZ,
            double timeSinceHit,
            double flagRate, double kickRate,
            double accuracy, double pingNorm
    ) {
        double[] in = new double[16];
        in[0]  = norm180(playerYaw)  / 180.0;
        in[1]  = playerPitch         / 90.0;
        in[2]  = norm180(targetYaw)  / 180.0;
        in[3]  = targetPitch         / 90.0;
        in[4]  = Math.min(distance / 6.0, 1.0);
        in[5]  = Math.tanh(tVelX * 5);
        in[6]  = Math.tanh(tVelY * 5);
        in[7]  = Math.tanh(tVelZ * 5);
        in[8]  = Math.tanh(pVelX * 5);
        in[9]  = Math.tanh(pVelY * 5);
        in[10] = Math.tanh(pVelZ * 5);
        in[11] = Math.min(timeSinceHit / 1000.0, 1.0);
        in[12] = Math.min(flagRate / 100.0, 1.0);
        in[13] = Math.min(kickRate / 10.0,  1.0);
        in[14] = Math.min(accuracy / 100.0, 1.0);
        in[15] = Math.min(pingNorm, 1.0);
        return in;
    }

    private static double[] cloneOutput() {
        if (lastOutput == null) return new double[4];
        double[] c = new double[lastOutput.length];
        System.arraycopy(lastOutput, 0, c, 0, lastOutput.length);
        return c;
    }

    private static void addHistory(double[] input, double[] target, double reward) {
        history.add(new TrainingEntry(input.clone(), target.clone(), reward));
        if (history.size() > MAX_HISTORY) history.remove(0);
    }

    private static void addPenaltyPattern(double[] input) {
        penaltyPatterns.add(input.clone());
        if (penaltyPatterns.size() > MAX_PENALTY) penaltyPatterns.remove(0);
    }

    private static void maybeReplay() {
        if (!experienceReplay || history.size() < 20) return;
        if (Math.random() > 0.25) return;
        TrainingEntry e = history.get((int)(Math.random() * history.size()));
        network.train(e.input, e.target, e.reward * 0.4);
    }

    private static void maybeAdaptLR() {
        int total = totalHits + totalMisses;
        if (total == 0 || total % 30 != 0) return;
        double acc = getAccuracy();
        double fr  = getFlagRate();
        if (totalKicks > 0 || fr > 20) {
            network.setLearningRate(0.025);
        } else if (acc > 85 && fr < 5) {
            network.setLearningRate(0.004);
        } else if (acc < 50) {
            network.setLearningRate(0.018);
        } else {
            network.setLearningRate(0.01);
        }
    }

    private static double norm180(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    private static double euclidean(double[] a, double[] b) {
        int len = Math.min(a.length, b.length);
        double sum = 0;
        for (int i = 0; i < len; i++) sum += (a[i] - b[i]) * (a[i] - b[i]);
        return Math.sqrt(sum);
    }

    private static class TrainingEntry {
        final double[] input, target;
        final double reward;
        TrainingEntry(double[] i, double[] t, double r) { input = i; target = t; reward = r; }
    }
}
