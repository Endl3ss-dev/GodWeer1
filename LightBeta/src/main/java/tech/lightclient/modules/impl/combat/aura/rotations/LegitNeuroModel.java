package tech.lightclient.modules.impl.combat.aura.rotations;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.LivingEntity;
import tech.lightclient.LightClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Обученная модель нейро-ротации Legit: маленький MLP 16 -> H -> 2
 * (d_yaw, d_pitch за тик). Веса обучает python-скрипт light_lneuro_train.py
 * и сохраняет в light/lneuro/<name>.json.
 */
public final class LegitNeuroModel {
    private static final Map<String, LegitNeuroModel> loaded = new ConcurrentHashMap<>();
    private static volatile String activeName = "";

    private int nIn;
    private int nHide;
    private double[] xMean;
    private double[] xStd;
    private double[][] w1; // [nHide][nIn]
    private double[] b1;
    private double[][] w2; // [2][nHide]
    private double[] b2;

    public static boolean load(String rawName) {
        String name = sanitize(rawName);
        if (name.isEmpty()) return false;
        Path file = dir().resolve(name + ".json");
        if (!Files.exists(file)) return false;
        try {
            JsonObject root = new Gson().fromJson(Files.readString(file, StandardCharsets.UTF_8), JsonObject.class);
            LegitNeuroModel m = new Gson().fromJson(root, LegitNeuroModel.class);
            if (m.w1 == null || m.w2 == null || m.xMean == null || m.xStd == null
                    || m.nIn != LegitNeuroRecorder.FEATURE_COUNT) {
                return false;
            }
            loaded.put(name, m);
            activeName = name;
            LightClient.LOGGER.info("[lneuro] model '{}' loaded (hide={})", name, m.nHide);
            return true;
        } catch (Exception e) {
            LightClient.LOGGER.error("[lneuro] failed to load model '{}'", name, e);
            return false;
        }
    }

    public static void setActive(String rawName) {
        String name = sanitize(rawName);
        if (loaded.containsKey(name)) {
            activeName = name;
        }
    }

    public static String getActiveName() {
        return activeName;
    }

    public static boolean hasActive() {
        return !activeName.isEmpty() && loaded.containsKey(activeName);
    }

    /**
     * Предсказание доворота прицела за тик по контексту цели.
     * Возвращает {dYaw, dPitch} или null, если активной модели нет.
     */
    public static float[] predict(LivingEntity target) {
        if (!hasActive()) return null;
        LegitNeuroModel m = loaded.get(activeName);
        float[] features = LegitNeuroRecorder.buildFeatures(target);
        return m.forward(features);
    }

    private float[] forward(float[] raw) {
        double[] x = new double[nIn];
        for (int i = 0; i < nIn && i < raw.length; i++) {
            x[i] = (raw[i] - xMean[i]) / (xStd[i] <= 1e-6 ? 1.0 : xStd[i]);
        }

        double[] h = new double[nHide];
        for (int j = 0; j < nHide; j++) {
            double sum = b1[j];
            double[] row = w1[j];
            for (int i = 0; i < nIn; i++) {
                sum += row[i] * x[i];
            }
            h[j] = Math.tanh(sum);
        }

        float[] out = new float[2];
        for (int k = 0; k < 2; k++) {
            double sum = b2[k];
            for (int j = 0; j < nHide; j++) {
                sum += w2[k][j] * h[j];
            }
            out[k] = (float) sum;
        }
        return out;
    }

    private static Path dir() {
        return net.fabricmc.loader.api.FabricLoader.getInstance()
                .getGameDir().resolve("light").resolve("lneuro");
    }

    private static String sanitize(String name) {
        return name == null ? "" : name.replaceAll("[^A-Za-z0-9_-]", "").trim().toLowerCase();
    }
}
