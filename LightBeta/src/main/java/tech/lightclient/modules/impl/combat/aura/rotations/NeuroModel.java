package tech.lightclient.modules.impl.combat.aura.rotations;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Грузит ONNX-модели.
 * <p>
 * Приоритет:
 * 1. run/nexis_models/rotation_<key>.onnx
 * 2. /assets/nexis/models/rotation_<key>.onnx
 * <p>
 * Можно reload без рестарта клиента.
 */
public final class NeuroModel {
    public static final String[] KEYS = {"sharp", "smooth", "precise", "aggressive", "lightning", "stealth", "balanced"};

    private static OrtEnvironment env;
    private static final Map<String, OrtSession> SESSIONS = new LinkedHashMap<>();

    private static volatile boolean inited = false;

    private NeuroModel() {
    }

    public static synchronized void init() {
        if (inited) return;

        try {
            env = OrtEnvironment.getEnvironment();
        } catch (Throwable t) {
            t.printStackTrace();
            return;
        }

        SESSIONS.clear();

        for (String key : KEYS) {
            OrtSession session = loadSession(key);

            if (session != null) {
                SESSIONS.put(key, session);
                System.out.println("[Light Neuro] loaded model: " + key);
            } else {
                System.out.println("[Light Neuro] missing model: " + key);
            }
        }

        inited = true;
    }

    /**
     * Reload моделей без рестарта клиента.
     */
    public static synchronized void reload() {
        shutdown();
        init();
    }

    private static OrtSession loadSession(String key) {
        String fileName = "rotation_" + key + ".onnx";

        try {
            Path externalPath = FabricLoader.getInstance()
                    .getGameDir()
                    .resolve("light_models")
                    .resolve(fileName);

            if (Files.isRegularFile(externalPath)) {
                byte[] bytes = Files.readAllBytes(externalPath);
                return env.createSession(bytes, new OrtSession.SessionOptions());
            }
        } catch (Throwable t) {
            System.out.println("[Light Neuro] failed external model: " + fileName);
            t.printStackTrace();
        }

        String resourcePath = "/assets/light/models/" + fileName;

        try (InputStream in = NeuroModel.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                return null;
            }

            byte[] bytes = in.readAllBytes();
            return env.createSession(bytes, new OrtSession.SessionOptions());
        } catch (Throwable t) {
            System.out.println("[Light Neuro] failed resource model: " + fileName);
            t.printStackTrace();
            return null;
        }
    }

    public static boolean isReady(String key) {
        return SESSIONS.get(key) != null;
    }

    public static float[] predict(String key, float[][][] input) {
        OrtSession session = SESSIONS.get(key);

        if (session == null) {
            return null;
        }

        try {
            synchronized (session) {
                try (OnnxTensor tensor = OnnxTensor.createTensor(env, input);
                     OrtSession.Result res = session.run(Collections.singletonMap("input", tensor))) {

                    float[][] out = (float[][]) res.get(0).getValue();

                    return new float[]{
                            out[0][0],
                            out[0][1]
                    };
                }
            }
        } catch (Throwable t) {
            return null;
        }
    }

    public static synchronized void shutdown() {
        for (OrtSession s : SESSIONS.values()) {
            try {
                s.close();
            } catch (Exception ignored) {
            }
        }

        SESSIONS.clear();
        inited = false;
    }
}