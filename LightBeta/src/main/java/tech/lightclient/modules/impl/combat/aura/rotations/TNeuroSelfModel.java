package tech.lightclient.modules.impl.combat.aura.rotations;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import tech.lightclient.LightClient;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MLP-модель Test нейро-ротации: 20 → H → 2 (d_yaw, d_pitch за тик).
 * Обучается скриптом light_tneuro_train.py, сохраняется в light/tneuro/<name>.json.
 */
public final class TNeuroSelfModel {

    private static final Map<String, TNeuroSelfModel> loaded = new ConcurrentHashMap<>();
    private static volatile String activeName = "";
    private static volatile float  lastConfidence = 0f;

    // Поля заполняются через Gson при загрузке JSON
    private int      nIn;
    private int      nHide;
    private double[] xMean;
    private double[] xStd;
    private double[][] w1;   // [nHide][nIn]
    private double[] b1;
    private double[][] w2;   // [2][nHide]
    private double[] b2;

    // ── Публичный API ─────────────────────────────────────────────────────────

    public static boolean load(String rawName) {
        String name = sanitize(rawName);
        if (name.isEmpty()) return false;
        Path file = dir().resolve(name + ".json");
        if (!Files.exists(file)) return false;
        try {
            JsonObject root = new Gson().fromJson(Files.readString(file, StandardCharsets.UTF_8), JsonObject.class);
            TNeuroSelfModel m = new Gson().fromJson(root, TNeuroSelfModel.class);
            if (m.w1 == null || m.w2 == null || m.xMean == null || m.xStd == null
                    || m.nIn != TNeuroSelfRecorder.FEATURE_COUNT) {
                LightClient.LOGGER.warn("[tneuro] model '{}' wrong feature count (expected {}, got {})",
                        name, TNeuroSelfRecorder.FEATURE_COUNT, m.nIn);
                return false;
            }
            loaded.put(name, m);
            activeName = name;
            LightClient.LOGGER.info("[tneuro] model '{}' loaded (hide={})", name, m.nHide);
            return true;
        } catch (Exception e) {
            LightClient.LOGGER.error("[tneuro] failed to load model '{}'", name, e);
            return false;
        }
    }

    public static void setActive(String rawName) {
        String name = sanitize(rawName);
        if (loaded.containsKey(name)) activeName = name;
    }

    public static String getActiveName()   { return activeName; }
    public static boolean hasActive()      { return !activeName.isEmpty() && loaded.containsKey(activeName); }
    public static float getLastConfidence(){ return lastConfidence; }

    /** Предсказывает {dYaw, dPitch} или null если модели нет. */
    public static float[] predict(LivingEntity target) {
        if (!hasActive()) return null;
        float[] features = TNeuroSelfRecorder.buildFeatures(target);
        return loaded.get(activeName).forward(features);
    }

    // ── Инференс ──────────────────────────────────────────────────────────────

    private float[] forward(float[] raw) {
        double[] x = new double[nIn];
        for (int i = 0; i < nIn && i < raw.length; i++)
            x[i] = (raw[i] - xMean[i]) / (xStd[i] <= 1e-6 ? 1.0 : xStd[i]);

        double[] h = new double[nHide];
        for (int j = 0; j < nHide; j++) {
            double s = b1[j];
            for (int i = 0; i < nIn; i++) s += w1[j][i] * x[i];
            h[j] = Math.tanh(s);
        }

        float[] out = new float[2];
        for (int k = 0; k < 2; k++) {
            double s = b2[k];
            for (int j = 0; j < nHide; j++) s += w2[k][j] * h[j];
            out[k] = (float) s;
        }

        // Уверенность: насколько модель "решительна" (большой выход = уверена)
        float mag = (float) Math.sqrt(out[0] * out[0] + out[1] * out[1]);
        lastConfidence = Math.min(1f, mag / 10f);
        return out;
    }

    // ── Утилиты ───────────────────────────────────────────────────────────────

    private static Path dir() {
        return net.fabricmc.loader.api.FabricLoader.getInstance()
                .getGameDir().resolve("light").resolve("tneuro");
    }

    private static String sanitize(String name) {
        return name == null ? "" : name.replaceAll("[^A-Za-z0-9_-]", "").trim().toLowerCase();
    }
}
