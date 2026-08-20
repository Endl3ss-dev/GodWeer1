package org.privates.client.utils.rotation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.utils.other.GCDUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

public class HumanRotationTrainer {
    private static final int INPUT_SIZE = 10;
    private static final int HIDDEN_1 = 24;
    private static final int HIDDEN_2 = 16;
    private static final int OUTPUT_SIZE = 2;
    private static final float MAX_YAW_STEP = 10.0f;
    private static final float MAX_PITCH_STEP = 7.0f;
    private static final float LEARNING_RATE = 0.0022f;

    private float baseYaw;
    private float basePitch;
    private float velocityYaw;
    private float velocityPitch;
    private float phase;
    private long lastUpdateMs;
    private boolean initialized;
    private final float[][] w1 = new float[INPUT_SIZE][HIDDEN_1];
    private final float[] b1 = new float[HIDDEN_1];
    private final float[][] w2 = new float[HIDDEN_1][HIDDEN_2];
    private final float[] b2 = new float[HIDDEN_2];
    private final float[][] w3 = new float[HIDDEN_2][OUTPUT_SIZE];
    private final float[] b3 = new float[OUTPUT_SIZE];
    private final float[] z1 = new float[HIDDEN_1];
    private final float[] a1 = new float[HIDDEN_1];
    private final float[] z2 = new float[HIDDEN_2];
    private final float[] a2 = new float[HIDDEN_2];
    private final float[] out = new float[OUTPUT_SIZE];
    private long lastSaveMs;
    private long trainSamples;
    private boolean modelLoaded;
    private boolean datasetBootstrapTried;
    private Path modelPath;

    public void reset() {
        initialized = false;
        velocityYaw = 0f;
        velocityPitch = 0f;
        phase = 0f;
        lastUpdateMs = 0L;
    }

    public void flushModel() {
        ensureModel();
        saveModel();
    }

    private void ensureModel() {
        if (modelLoaded) {
            return;
        }
        modelPath = Paths.get("private", "neural", "rotation_mlp_v1.json");
        try {
            Path parent = modelPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.exists(modelPath)) {
                String json = new String(Files.readAllBytes(modelPath), StandardCharsets.UTF_8);
                JsonObject root = new JsonParser().parse(json).getAsJsonObject();
                trainSamples = root.has("trainSamples") ? root.get("trainSamples").getAsLong() : 0L;
                read2D(root.getAsJsonArray("w1"), w1);
                read1D(root.getAsJsonArray("b1"), b1);
                read2D(root.getAsJsonArray("w2"), w2);
                read1D(root.getAsJsonArray("b2"), b2);
                read2D(root.getAsJsonArray("w3"), w3);
                read1D(root.getAsJsonArray("b3"), b3);
            } else {
                initRandomWeights();
                saveModel();
            }
        } catch (Exception ignored) {
            initRandomWeights();
        }
        bootstrapFromDatasetIfPresent();
        modelLoaded = true;
    }

    private void bootstrapFromDatasetIfPresent() {
        if (datasetBootstrapTried) {
            return;
        }
        datasetBootstrapTried = true;
        Path[] candidates = new Path[]{
                Paths.get("workspace", "ai_rotation_v2", "rotation_samples.json"),
                Paths.get("workspace", "ai_rotation_v2", "rotation_samples.csv"),
                Paths.get("private", "neural", "rotation_samples.json"),
                Paths.get("private", "neural", "rotation_samples.csv"),
                Paths.get("private", "neural", "rotation_dataset.json"),
                Paths.get("private", "neural", "rotation_dataset.csv")
        };
        for (Path p : candidates) {
            try {
                if (!Files.exists(p) || Files.size(p) <= 2) {
                    continue;
                }
                String content = new String(Files.readAllBytes(p), StandardCharsets.UTF_8).trim();
                if (content.isEmpty()) {
                    continue;
                }
                if (p.toString().endsWith(".json")) {
                    pretrainFromJson(content);
                } else {
                    pretrainFromCsv(content);
                }
                saveModel();
                break;
            } catch (Exception ignored) {
            }
        }
    }

    private void pretrainFromJson(String content) {
        JsonElement root = new JsonParser().parse(content);
        JsonArray arr = root.isJsonArray() ? root.getAsJsonArray() : null;
        if (arr == null || arr.size() == 0) {
            return;
        }
        int passes = 2;
        for (int pass = 0; pass < passes; pass++) {
            for (JsonElement element : arr) {
                float[] input = new float[INPUT_SIZE];
                float[] target = new float[OUTPUT_SIZE];
                if (!extractSampleFromJson(element, input, target)) {
                    continue;
                }
                trainStep(input, target);
                trainSamples++;
            }
        }
    }

    private boolean extractSampleFromJson(JsonElement element, float[] input, float[] target) {
        if (element == null || element.isJsonNull()) {
            return false;
        }
        if (element.isJsonArray()) {
            JsonArray a = element.getAsJsonArray();
            if (a.size() < 12) {
                return false;
            }
            for (int i = 0; i < INPUT_SIZE; i++) {
                input[i] = MathHelper.clamp(a.get(i).getAsFloat(), -1.0f, 1.0f);
            }
            target[0] = MathHelper.clamp(a.get(10).getAsFloat(), -1.0f, 1.0f);
            target[1] = MathHelper.clamp(a.get(11).getAsFloat(), -1.0f, 1.0f);
            return true;
        }
        if (!element.isJsonObject()) {
            return false;
        }
        JsonObject o = element.getAsJsonObject();
        input[0] = pick(o, "yawDiff", "dyaw", "in_yaw_diff");
        input[1] = pick(o, "pitchDiff", "dpitch", "in_pitch_diff");
        input[2] = pick(o, "distance", "dist", "in_distance");
        input[3] = pick(o, "targetMotionX", "tmx", "in_target_motion_x");
        input[4] = pick(o, "targetMotionY", "tmy", "in_target_motion_y");
        input[5] = pick(o, "targetMotionZ", "tmz", "in_target_motion_z");
        input[6] = pick(o, "playerMotionX", "pmx", "in_player_motion_x");
        input[7] = pick(o, "playerMotionZ", "pmz", "in_player_motion_z");
        input[8] = pick(o, "phaseSin", "sin", "in_phase_sin");
        input[9] = pick(o, "phaseCos", "cos", "in_phase_cos");
        for (int i = 0; i < INPUT_SIZE; i++) {
            input[i] = MathHelper.clamp(input[i], -1.0f, 1.0f);
        }
        target[0] = MathHelper.clamp(pick(o, "labelYaw", "outYaw", "targetYaw"), -1.0f, 1.0f);
        target[1] = MathHelper.clamp(pick(o, "labelPitch", "outPitch", "targetPitch"), -1.0f, 1.0f);
        return true;
    }

    private float pick(JsonObject o, String... keys) {
        for (String k : keys) {
            if (o.has(k)) {
                try {
                    return o.get(k).getAsFloat();
                } catch (Exception ignored) {
                }
            }
        }
        return 0f;
    }

    private void pretrainFromCsv(String content) {
        String[] lines = content.split("\\r?\\n");
        int passes = 2;
        for (int pass = 0; pass < passes; pass++) {
            for (String line : lines) {
                if (line == null || line.isEmpty()) {
                    continue;
                }
                String[] parts = line.contains(";") ? line.split(";") : line.split(",");
                if (parts.length < 12) {
                    continue;
                }
                float[] input = new float[INPUT_SIZE];
                float[] target = new float[OUTPUT_SIZE];
                try {
                    for (int i = 0; i < INPUT_SIZE; i++) {
                        input[i] = MathHelper.clamp(Float.parseFloat(parts[i].trim()), -1.0f, 1.0f);
                    }
                    target[0] = MathHelper.clamp(Float.parseFloat(parts[10].trim()), -1.0f, 1.0f);
                    target[1] = MathHelper.clamp(Float.parseFloat(parts[11].trim()), -1.0f, 1.0f);
                } catch (Exception e) {
                    continue;
                }
                trainStep(input, target);
                trainSamples++;
            }
        }
    }

    private void initRandomWeights() {
        for (int i = 0; i < INPUT_SIZE; i++) {
            for (int j = 0; j < HIDDEN_1; j++) {
                w1[i][j] = randomWeight(INPUT_SIZE);
            }
        }
        for (int i = 0; i < HIDDEN_1; i++) {
            b1[i] = 0f;
            for (int j = 0; j < HIDDEN_2; j++) {
                w2[i][j] = randomWeight(HIDDEN_1);
            }
        }
        for (int i = 0; i < HIDDEN_2; i++) {
            b2[i] = 0f;
            for (int j = 0; j < OUTPUT_SIZE; j++) {
                w3[i][j] = randomWeight(HIDDEN_2);
            }
        }
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            b3[i] = 0f;
        }
    }

    private float randomWeight(int fanIn) {
        float scale = (float) Math.sqrt(2.0 / Math.max(1, fanIn));
        return (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2.0f * scale;
    }

    private void saveModel() {
        if (modelPath == null) {
            return;
        }
        try {
            JsonObject root = new JsonObject();
            root.addProperty("trainSamples", trainSamples);
            root.add("w1", write2D(w1));
            root.add("b1", write1D(b1));
            root.add("w2", write2D(w2));
            root.add("b2", write1D(b2));
            root.add("w3", write2D(w3));
            root.add("b3", write1D(b3));
            Files.write(modelPath, root.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    private JsonArray write1D(float[] src) {
        JsonArray arr = new JsonArray();
        for (float v : src) {
            arr.add(v);
        }
        return arr;
    }

    private JsonArray write2D(float[][] src) {
        JsonArray outer = new JsonArray();
        for (float[] row : src) {
            outer.add(write1D(row));
        }
        return outer;
    }

    private void read1D(JsonArray arr, float[] dst) {
        if (arr == null) {
            return;
        }
        int len = Math.min(arr.size(), dst.length);
        for (int i = 0; i < len; i++) {
            dst[i] = arr.get(i).getAsFloat();
        }
    }

    private void read2D(JsonArray arr, float[][] dst) {
        if (arr == null) {
            return;
        }
        int outer = Math.min(arr.size(), dst.length);
        for (int i = 0; i < outer; i++) {
            read1D(arr.get(i).getAsJsonArray(), dst[i]);
        }
    }

    private float tanh(float v) {
        return (float) Math.tanh(v);
    }

    private float dtanhFromActivated(float activated) {
        return 1.0f - activated * activated;
    }

    private void forward(float[] input) {
        for (int j = 0; j < HIDDEN_1; j++) {
            float sum = b1[j];
            for (int i = 0; i < INPUT_SIZE; i++) {
                sum += input[i] * w1[i][j];
            }
            z1[j] = sum;
            a1[j] = tanh(sum);
        }
        for (int j = 0; j < HIDDEN_2; j++) {
            float sum = b2[j];
            for (int i = 0; i < HIDDEN_1; i++) {
                sum += a1[i] * w2[i][j];
            }
            z2[j] = sum;
            a2[j] = tanh(sum);
        }
        for (int j = 0; j < OUTPUT_SIZE; j++) {
            float sum = b3[j];
            for (int i = 0; i < HIDDEN_2; i++) {
                sum += a2[i] * w3[i][j];
            }
            out[j] = tanh(sum);
        }
    }

    private void trainStep(float[] input, float[] target) {
        forward(input);
        float[] dOut = new float[OUTPUT_SIZE];
        for (int j = 0; j < OUTPUT_SIZE; j++) {
            float err = out[j] - target[j];
            dOut[j] = 2.0f * err * dtanhFromActivated(out[j]);
        }

        float[] dA2 = new float[HIDDEN_2];
        for (int i = 0; i < HIDDEN_2; i++) {
            float acc = 0f;
            for (int j = 0; j < OUTPUT_SIZE; j++) {
                acc += w3[i][j] * dOut[j];
            }
            dA2[i] = acc;
        }

        float[] dZ2 = new float[HIDDEN_2];
        for (int i = 0; i < HIDDEN_2; i++) {
            dZ2[i] = dA2[i] * dtanhFromActivated(a2[i]);
        }

        float[] dA1 = new float[HIDDEN_1];
        for (int i = 0; i < HIDDEN_1; i++) {
            float acc = 0f;
            for (int j = 0; j < HIDDEN_2; j++) {
                acc += w2[i][j] * dZ2[j];
            }
            dA1[i] = acc;
        }

        float[] dZ1 = new float[HIDDEN_1];
        for (int i = 0; i < HIDDEN_1; i++) {
            dZ1[i] = dA1[i] * dtanhFromActivated(a1[i]);
        }

        for (int i = 0; i < HIDDEN_2; i++) {
            for (int j = 0; j < OUTPUT_SIZE; j++) {
                float grad = a2[i] * dOut[j];
                w3[i][j] -= LEARNING_RATE * MathHelper.clamp(grad, -1.0f, 1.0f);
            }
        }
        for (int j = 0; j < OUTPUT_SIZE; j++) {
            b3[j] -= LEARNING_RATE * MathHelper.clamp(dOut[j], -1.0f, 1.0f);
        }

        for (int i = 0; i < HIDDEN_1; i++) {
            for (int j = 0; j < HIDDEN_2; j++) {
                float grad = a1[i] * dZ2[j];
                w2[i][j] -= LEARNING_RATE * MathHelper.clamp(grad, -1.0f, 1.0f);
            }
        }
        for (int j = 0; j < HIDDEN_2; j++) {
            b2[j] -= LEARNING_RATE * MathHelper.clamp(dZ2[j], -1.0f, 1.0f);
        }

        for (int i = 0; i < INPUT_SIZE; i++) {
            for (int j = 0; j < HIDDEN_1; j++) {
                float grad = input[i] * dZ1[j];
                w1[i][j] -= LEARNING_RATE * MathHelper.clamp(grad, -1.0f, 1.0f);
            }
        }
        for (int j = 0; j < HIDDEN_1; j++) {
            b1[j] -= LEARNING_RATE * MathHelper.clamp(dZ1[j], -1.0f, 1.0f);
        }
    }

    private float[] buildInput(Minecraft mc, LivingEntity target, float yawDiff, float pitchDiff) {
        Vector3d tm = target.getMotion();
        Vector3d pm = mc.player.getMotion();
        float distance = (float) mc.player.getDistance(target);
        long t = System.currentTimeMillis();
        return new float[]{
                MathHelper.clamp(yawDiff / 180.0f, -1.0f, 1.0f),
                MathHelper.clamp(pitchDiff / 90.0f, -1.0f, 1.0f),
                MathHelper.clamp(distance / 8.0f, 0.0f, 1.0f),
                MathHelper.clamp((float) tm.x, -1.5f, 1.5f) / 1.5f,
                MathHelper.clamp((float) tm.y, -1.5f, 1.5f) / 1.5f,
                MathHelper.clamp((float) tm.z, -1.5f, 1.5f) / 1.5f,
                MathHelper.clamp((float) pm.x, -1.5f, 1.5f) / 1.5f,
                MathHelper.clamp((float) pm.z, -1.5f, 1.5f) / 1.5f,
                (float) Math.sin(t * 0.0019),
                (float) Math.cos(t * 0.0019)
        };
    }

    public Vector2f update(Minecraft mc, LivingEntity target) {
        if (mc == null || mc.player == null || target == null) {
            return Vector2f.ZERO;
        }
        ensureModel();

        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        Vector3d targetPos = target.getPositionVec().add(0.0, target.getEyeHeight() * 0.92, 0.0);
        Vector3d delta = targetPos.subtract(eyePos);
        double horiz = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        float desiredYaw = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0f;
        float desiredPitch = (float) -Math.toDegrees(Math.atan2(delta.y, horiz));
        desiredPitch = MathHelper.clamp(desiredPitch, -89.0f, 89.0f);

        float yawDiff = MathHelper.wrapDegrees(desiredYaw - mc.player.rotationYaw);
        float pitchDiff = desiredPitch - mc.player.rotationPitch;
        float[] input = buildInput(mc, target, yawDiff, pitchDiff);

        forward(input);

        float teacherYaw = MathHelper.clamp(yawDiff * 0.36f, -MAX_YAW_STEP, MAX_YAW_STEP);
        float teacherPitch = MathHelper.clamp(pitchDiff * 0.36f, -MAX_PITCH_STEP, MAX_PITCH_STEP);
        float predYaw = out[0] * MAX_YAW_STEP;
        float predPitch = out[1] * MAX_PITCH_STEP;

        float blend = MathHelper.clamp(trainSamples / 1800.0f, 0.12f, 1.0f);
        float stepYaw = teacherYaw + (predYaw - teacherYaw) * blend;
        float stepPitch = teacherPitch + (predPitch - teacherPitch) * blend;

        float nextYaw = mc.player.rotationYaw + stepYaw;
        float nextPitch = MathHelper.clamp(mc.player.rotationPitch + stepPitch, -89.0f, 89.0f);

        float sensYaw = GCDUtils.getSensitivity(nextYaw);
        float sensPitch = GCDUtils.getSensitivity(nextPitch);

        float[] label = new float[]{
                MathHelper.clamp(teacherYaw / MAX_YAW_STEP, -1.0f, 1.0f),
                MathHelper.clamp(teacherPitch / MAX_PITCH_STEP, -1.0f, 1.0f)
        };
        trainStep(input, label);
        trainSamples++;

        long now = System.currentTimeMillis();
        if (now - lastSaveMs > 7000L) {
            lastSaveMs = now;
            saveModel();
        }

        return new Vector2f(sensYaw, sensPitch);
    }

    public Vector2f update(Minecraft mc) {
        if (mc == null || mc.player == null) {
            return Vector2f.ZERO;
        }

        long now = System.currentTimeMillis();
        if (!initialized) {
            initialized = true;
            baseYaw = mc.player.rotationYaw;
            basePitch = mc.player.rotationPitch;
            phase = ThreadLocalRandom.current().nextFloat() * (float) Math.PI * 2f;
            lastUpdateMs = now;
            return new Vector2f(baseYaw, basePitch);
        }

        float dt = MathHelper.clamp((now - lastUpdateMs) / 1000f, 0.001f, 0.05f);
        lastUpdateMs = now;
        phase += dt * 2.1f;

        float swayYaw = (float) Math.sin(phase * 0.83f) * 6.5f;
        float swayPitch = (float) Math.cos(phase * 0.71f) * 2.9f;

        float microYaw = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.7f;
        float microPitch = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.35f;

        float targetYaw = baseYaw + swayYaw + microYaw;
        float targetPitch = MathHelper.clamp(basePitch + swayPitch + microPitch, -40f, 40f);

        float diffYaw = MathHelper.wrapDegrees(targetYaw - mc.player.rotationYaw);
        float diffPitch = targetPitch - mc.player.rotationPitch;

        float accelYaw = 110f * dt;
        float accelPitch = 85f * dt;

        velocityYaw += diffYaw * accelYaw;
        velocityPitch += diffPitch * accelPitch;

        velocityYaw *= 0.82f;
        velocityPitch *= 0.80f;

        float maxYawStep = 9.0f;
        float maxPitchStep = 6.0f;
        float nextYaw = mc.player.rotationYaw + MathHelper.clamp(velocityYaw, -maxYawStep, maxYawStep);
        float nextPitch = mc.player.rotationPitch + MathHelper.clamp(velocityPitch, -maxPitchStep, maxPitchStep);

        nextPitch = MathHelper.clamp(nextPitch, -89f, 89f);

        float sensYaw = GCDUtils.getSensitivity(nextYaw);
        float sensPitch = GCDUtils.getSensitivity(nextPitch);

        mc.player.rotationYaw = sensYaw;
        mc.player.rotationPitch = sensPitch;

        return new Vector2f(sensYaw, sensPitch);
    }
}
