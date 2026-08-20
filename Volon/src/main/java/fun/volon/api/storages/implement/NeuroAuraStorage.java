package fun.volon.api.storages.implement;

import fun.volon.api.QClient;
import fun.volon.api.utils.rotate.Rotation;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Оригинальный NeuroAuraStorage — система записи и воспроизведения паттернов ротации (Data ротация).
 * Восстановлен по API из DataCommand.
 */
public class NeuroAuraStorage implements QClient {

    // ---- Состояние ----
    @Getter @Setter private boolean recording    = false;
    @Getter @Setter private boolean usingNeuro   = false;
    @Getter         private boolean showStats     = true;

    // ---- Паттерны ----
    private final List<RotationPattern> patterns = new ArrayList<>();
    private int patternIndex = 0;
    private int recordedThisSession = 0;

    // ---- Директория сохранений ----
    private static final Path SAVE_DIR = Paths.get("volon", "data_patterns");

    // ---- Состояние воспроизведения ----
    private float smoothYaw   = 0f;
    private float smoothPitch = 0f;
    private boolean initialized = false;

    // -----------------------------------------------------------------------

    public void startRecording() {
        patterns.clear();
        recordedThisSession = 0;
        recording = true;
        usingNeuro = false;
        initialized = false;
    }

    public void stopRecording() {
        recording = false;
    }

    public void clearPatterns() {
        patterns.clear();
        recordedThisSession = 0;
        patternIndex = 0;
        initialized = false;
    }

    public void resetState() {
        patternIndex = 0;
        initialized = false;
        smoothYaw = 0f;
        smoothPitch = 0f;
    }

    public int getPatternCount() {
        return patterns.size();
    }

    public int getRecordedThisSession() {
        return recordedThisSession;
    }

    public List<String> getPatternNames() {
        try {
            if (!Files.exists(SAVE_DIR)) return Collections.emptyList();
            List<String> names = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(SAVE_DIR, "*.dat")) {
                for (Path p : stream) {
                    String name = p.getFileName().toString();
                    names.add(name.substring(0, name.length() - 4));
                }
            }
            return names;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public boolean savePatterns(String name) {
        if (patterns.isEmpty()) return false;
        try {
            Files.createDirectories(SAVE_DIR);
            Path file = SAVE_DIR.resolve(name + ".dat");
            try (DataOutputStream out = new DataOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(file)))) {
                out.writeInt(patterns.size());
                for (RotationPattern p : patterns) {
                    out.writeFloat(p.yaw);
                    out.writeFloat(p.pitch);
                    out.writeLong(p.deltaMs);
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean loadPatterns(String name) {
        try {
            Path file = SAVE_DIR.resolve(name + ".dat");
            if (!Files.exists(file)) return false;
            patterns.clear();
            try (DataInputStream in = new DataInputStream(
                    new BufferedInputStream(Files.newInputStream(file)))) {
                int count = in.readInt();
                for (int i = 0; i < count; i++) {
                    float yaw   = in.readFloat();
                    float pitch = in.readFloat();
                    long  delta = in.readLong();
                    patterns.add(new RotationPattern(yaw, pitch, delta));
                }
            }
            patternIndex = 0;
            return !patterns.isEmpty();
        } catch (IOException e) {
            return false;
        }
    }

    public boolean deletePatterns(String name) {
        try {
            Path file = SAVE_DIR.resolve(name + ".dat");
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Записывает тик ротации во время записи.
     */
    public void recordTick(LivingEntity target, float yaw, float pitch) {
        if (!recording) return;
        long delta = patterns.isEmpty() ? 0 : 50L;
        patterns.add(new RotationPattern(yaw, pitch, delta));
        recordedThisSession++;
    }

    /**
     * Возвращает следующую ротацию из паттерна (Data ротация).
     */
    public Rotation getNeuroRotation(LivingEntity target, float currentYaw, float currentPitch, boolean focus) {
        if (patterns.isEmpty()) return null;

        if (!initialized) {
            smoothYaw   = currentYaw;
            smoothPitch = currentPitch;
            initialized = true;
        }

        RotationPattern p = patterns.get(patternIndex % patterns.size());
        patternIndex++;

        float speed = focus ? 0.35f : 0.18f;
        smoothYaw   += (p.yaw   - smoothYaw)   * speed;
        smoothPitch += (p.pitch - smoothPitch) * speed;

        return new Rotation(smoothYaw, smoothPitch);
    }

    public String getStatusString() {
        if (recording) {
            return "§aData§7: запись... §f" + patterns.size() + " §7паттернов";
        } else if (usingNeuro) {
            return "§bData§7: воспроизведение §f" + patterns.size() + " §7паттернов";
        } else {
            return "§7Data§7: неактивен";
        }
    }

    // -----------------------------------------------------------------------

    private static class RotationPattern {
        final float yaw, pitch;
        final long  deltaMs;
        RotationPattern(float yaw, float pitch, long deltaMs) {
            this.yaw = yaw; this.pitch = pitch; this.deltaMs = deltaMs;
        }
    }
}
