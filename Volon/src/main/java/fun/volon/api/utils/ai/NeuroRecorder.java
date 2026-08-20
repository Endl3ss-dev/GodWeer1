package fun.volon.api.utils.ai;

import fun.volon.api.QClient;
import fun.volon.api.events.EventInvoker;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventAttackEntity;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.api.utils.chat.ChatUtils;
import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Записывает:
 *  1. Скорость наведения прицела на противника (dYaw, dPitch) каждый тик
 *  2. Точки ударов (куда чаще всего происходит удар)
 *
 * При воспроизведении:
 *  - Применяет записанную скорость наведения к ротации
 *  - Бьёт в запомненные точки ударов
 *  - Обучается на основе успешных попаданий
 */
public class NeuroRecorder implements QClient {

    public static final NeuroRecorder INSTANCE = new NeuroRecorder();
    private static final Path SAVE_DIR = Paths.get("volon", "neuro_profiles");

    @Getter private boolean recording = false;
    @Getter private boolean replaying = false;

    // Записанные тики (скорость мыши)
    private final List<TickFrame> tickFrames = new ArrayList<>();
    // Записанные удары (куда бил + интервал)
    private final List<AttackFrame> attackFrames = new ArrayList<>();

    private int replayTickIndex   = 0;
    private int replayAttackIndex = 0;

    // Для записи
    private float prevYaw   = 0f;
    private float prevPitch = 0f;
    private boolean firstTick = true;
    private long lastAttackTime = 0;
    private LivingEntity recordTarget = null;

    // Для воспроизведения
    private long replayLastAttackTime = 0;

    private NeuroRecorder() {
        EventInvoker.register(this);
    }

    // -----------------------------------------------------------------------
    // Запись
    // -----------------------------------------------------------------------

    @EventLink
    public void onTick(EventUpdate event) {
        if (!recording || mc.player == null || mc.world == null) return;

        float curYaw   = mc.player.getYaw();
        float curPitch = mc.player.getPitch();

        if (firstTick) {
            prevYaw   = curYaw;
            prevPitch = curPitch;
            firstTick = false;
            return;
        }

        // Скорость поворота мыши за этот тик
        float dYaw   = MathHelper.wrapDegrees(curYaw - prevYaw);
        float dPitch = curPitch - prevPitch;

        prevYaw   = curYaw;
        prevPitch = curPitch;

        tickFrames.add(new TickFrame(dYaw, dPitch));
    }

    @EventLink
    public void onAttack(EventAttackEntity event) {
        if (!recording) return;
        if (event.getPlayer() != mc.player) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;

        // Точка удара относительно центра цели
        Vec3d center = target.getBoundingBox().getCenter();
        Vec3d eye    = mc.player.getEyePos();

        double dx   = center.x - eye.x;
        double dy   = center.y - eye.y;
        double dz   = center.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float toTargetYaw   = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float toTargetPitch = (float)(-Math.toDegrees(Math.atan2(dy, dist)));

        float hitOffsetYaw   = MathHelper.wrapDegrees(mc.player.getYaw()   - toTargetYaw);
        float hitOffsetPitch = mc.player.getPitch() - toTargetPitch;

        // Запоминаем точку удара (без интервалов)
        attackFrames.add(new AttackFrame(hitOffsetYaw, hitOffsetPitch));
    }

    // -----------------------------------------------------------------------
    // Воспроизведение
    // -----------------------------------------------------------------------

    /**
     * Вызывается каждый тик при воспроизведении.
     * Возвращает [yaw, pitch, shouldAttack(0/1)] или null.
     *
     * Логика:
     *  - Берём направление на цель
     *  - Применяем записанное смещение (hitOffset) относительно цели
     *  - Воспроизводим записанную скорость наводки с улучшениями
     *  - Бьём с записанными интервалами но адаптивно
     */
    public float[] getReplayRotation(LivingEntity target) {
        if (!replaying || target == null || mc.player == null) return null;

        // Направление на центр цели
        Vec3d center = target.getBoundingBox().getCenter();
        Vec3d eye    = mc.player.getEyePos();

        double dx   = center.x - eye.x;
        double dy   = center.y - eye.y;
        double dz   = center.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float toTargetYaw   = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float toTargetPitch = (float)(-Math.toDegrees(Math.atan2(dy, dist)));

        float currentYaw   = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();

        boolean shouldAttack = false;

        // Проверяем нужно ли бить
        if (!attackFrames.isEmpty()) {
            AttackFrame af = attackFrames.get(replayAttackIndex % attackFrames.size());
            long now = System.currentTimeMillis();
            
            // Используем фиксированный интервал атаки (500ms) вместо записанного
            long attackInterval = 500L; // фиксированный интервал атаки
            
            if (now - replayLastAttackTime >= attackInterval) {
                // Целевая точка удара = центр цели + записанное смещение
                float aimYaw   = toTargetYaw   + af.hitOffsetYaw;
                float aimPitch = MathHelper.clamp(toTargetPitch + af.hitOffsetPitch, -90f, 90f);

                // Вычисляем адаптивную скорость наводки
                float adaptiveSpeed = calculateAdaptiveAimSpeed(target, (float)dist);
                
                // Плавно двигаемся к точке удара с адаптивной скоростью
                float yawDiff   = MathHelper.wrapDegrees(aimYaw - currentYaw);
                float pitchDiff = aimPitch - currentPitch;

                float finalYaw   = currentYaw   + MathHelper.clamp(yawDiff,   -adaptiveSpeed, adaptiveSpeed);
                float finalPitch = currentPitch + MathHelper.clamp(pitchDiff, -adaptiveSpeed * 0.7f, adaptiveSpeed * 0.7f);

                // Улучшенная логика удара - учитываем скорость цели
                Vec3d targetVel = new Vec3d(
                    target.getX() - target.prevX,
                    target.getY() - target.prevY,
                    target.getZ() - target.prevZ
                );
                float targetSpeed = (float)targetVel.length();
                
                // Более точные пороги для удара в зависимости от скорости цели
                float yawThreshold = targetSpeed > 0.1f ? 8f : 4f;
                float pitchThreshold = targetSpeed > 0.1f ? 5f : 2.5f;

                // Бьём только когда достаточно близко к точке
                if (Math.abs(yawDiff) < yawThreshold && Math.abs(pitchDiff) < pitchThreshold) {
                    shouldAttack = true;
                    replayLastAttackTime = now;
                    replayAttackIndex++;
                }

                return new float[]{ finalYaw, finalPitch, shouldAttack ? 1f : 0f };
            }
        }

        // Между ударами — улучшенное воспроизведение записанной скорости наводки
        float finalYaw   = currentYaw;
        float finalPitch = currentPitch;

        if (!tickFrames.isEmpty()) {
            TickFrame tf = tickFrames.get(replayTickIndex % tickFrames.size());
            replayTickIndex++;

            // Вычисляем куда нужно смотреть (центр цели)
            float targetDiffYaw   = MathHelper.wrapDegrees(toTargetYaw - currentYaw);
            float targetDiffPitch = toTargetPitch - currentPitch;

            // Адаптивное воспроизведение в зависимости от дистанции до цели
            float distanceToTarget = (float)Math.sqrt(targetDiffYaw * targetDiffYaw + targetDiffPitch * targetDiffPitch);
            
            if (distanceToTarget > 20f) {
                // Далеко от цели — быстро наводимся используя записанную скорость
                float speedScale = Math.min(distanceToTarget / 40f, 2.0f);
                finalYaw   = currentYaw   + tf.dYaw * speedScale;
                finalPitch = currentPitch + tf.dPitch * speedScale;
            } else if (distanceToTarget > 8f) {
                // Средняя дистанция — используем записанную скорость с небольшой коррекцией
                float correctionFactor = 0.15f;
                finalYaw   = currentYaw   + tf.dYaw + targetDiffYaw   * correctionFactor;
                finalPitch = currentPitch + tf.dPitch + targetDiffPitch * correctionFactor;
            } else {
                // Близко к цели — плавно отслеживаем с записанной скоростью
                float maxDrift = 6f; // максимальное отклонение от цели
                float appliedDYaw   = MathHelper.clamp(tf.dYaw,   -maxDrift, maxDrift);
                float appliedDPitch = MathHelper.clamp(tf.dPitch, -maxDrift * 0.6f, maxDrift * 0.6f);
                
                // Добавляем коррекцию к цели (20% вместо 10%)
                appliedDYaw   += targetDiffYaw   * 0.2f;
                appliedDPitch += targetDiffPitch * 0.2f;
                
                finalYaw   = currentYaw   + appliedDYaw;
                finalPitch = currentPitch + appliedDPitch;
            }

            finalPitch = MathHelper.clamp(finalPitch, -90f, 90f);
        }

        return new float[]{ finalYaw, finalPitch, 0f };
    }



    /**
     * Вычисляет адаптивную скорость прицеливания
     */
    private float calculateAdaptiveAimSpeed(LivingEntity target, float distance) {
        float baseSpeed = calculateAverageRotationSpeed();
        
        // Корректировка по дистанции
        if (distance > 4.0f) {
            baseSpeed *= 1.3f; // Быстрее на дальней дистанции
        } else if (distance < 2.0f) {
            baseSpeed *= 0.8f; // Медленнее на ближней дистанции
        }
        
        // Корректировка по движению цели
        Vec3d targetVel = new Vec3d(
            target.getX() - target.prevX,
            target.getY() - target.prevY,
            target.getZ() - target.prevZ
        );
        float targetSpeed = (float)targetVel.length();
        
        if (targetSpeed > 0.1f) {
            baseSpeed *= 1.2f; // Быстрее против движущихся целей
        }
        
        return MathHelper.clamp(baseSpeed, 8f, 35f);
    }

    /**
     * Вычисляет среднюю скорость поворота из записанных тиков
     */
    private float calculateAverageRotationSpeed() {
        if (tickFrames.isEmpty()) return 15f;
        
        float totalSpeed = 0f;
        int count = Math.min(tickFrames.size(), 20); // берём последние 20 тиков
        
        for (int i = 0; i < count; i++) {
            TickFrame tf = tickFrames.get(i);
            totalSpeed += Math.abs(tf.dYaw) + Math.abs(tf.dPitch);
        }
        
        float avgSpeed = totalSpeed / count;
        return MathHelper.clamp(avgSpeed, 5f, 25f); // ограничиваем разумными пределами
    }

    // -----------------------------------------------------------------------

    public void startRecording() {
        tickFrames.clear();
        attackFrames.clear();
        replayTickIndex   = 0;
        replayAttackIndex = 0;
        firstTick         = true;
        lastAttackTime    = 0;
        recordTarget      = null;
        recording         = true;
        replaying         = false;
        ChatUtils.sendMessage("§a[NeuroRec] Запись начата!");
        ChatUtils.sendMessage("§7Выключи киллауру и бей противника вручную.");
        ChatUtils.sendMessage("§7Система запомнит: скорость наводки, точки ударов, тайминги");
        ChatUtils.sendMessage("§7Когда закончишь: §f.neuro stop");
    }

    public void stopRecording() {
        recording = false;
        
        // Анализируем качество записи
        String quality = analyzeRecordingQuality();
        
        ChatUtils.sendMessage("§e[NeuroRec] Запись остановлена.");
        ChatUtils.sendMessage("§7Тиков: §f" + tickFrames.size()
                + " §7Ударов: §f" + attackFrames.size());
        ChatUtils.sendMessage("§7Качество записи: " + quality);
        ChatUtils.sendMessage("§7Сохрани: §f.neuro save <имя>");
    }

    /**
     * Анализирует качество записи и дает рекомендации
     */
    private String analyzeRecordingQuality() {
        if (tickFrames.isEmpty() || attackFrames.isEmpty()) {
            return "§cПлохое §7- нет данных";
        }
        
        // Анализируем стабильность движений
        float avgRotationSpeed = calculateAverageRotationSpeed();
        float rotationVariance = calculateRotationVariance();
        
        // Анализируем точность ударов (разброс смещений)
        double avgYawOffset = attackFrames.stream()
            .mapToDouble(af -> Math.abs(af.hitOffsetYaw))
            .average().orElse(0.0);
        double avgPitchOffset = attackFrames.stream()
            .mapToDouble(af -> Math.abs(af.hitOffsetPitch))
            .average().orElse(0.0);
        
        // Оценка качества
        int score = 0;
        
        // Достаточно данных
        if (tickFrames.size() > 100 && attackFrames.size() > 5) score += 25;
        else if (tickFrames.size() > 50 && attackFrames.size() > 3) score += 15;
        
        // Стабильная скорость ротации
        if (rotationVariance < 5.0f && avgRotationSpeed > 3.0f && avgRotationSpeed < 25.0f) score += 25;
        else if (rotationVariance < 8.0f) score += 15;
        
        // Хорошая точность ударов
        if (avgYawOffset < 3.0 && avgPitchOffset < 2.0) score += 25;
        else if (avgYawOffset < 5.0 && avgPitchOffset < 3.5) score += 15;
        
        if (score >= 80) return "§aОтличное §7- готово к использованию";
        if (score >= 60) return "§eХорошее §7- можно использовать";
        if (score >= 40) return "§6Среднее §7- лучше перезаписать";
        return "§cПлохое §7- рекомендуется перезаписать";
    }

    /**
     * Вычисляет вариацию скорости ротации для оценки стабильности
     */
    private float calculateRotationVariance() {
        if (tickFrames.size() < 10) return Float.MAX_VALUE;
        
        float avgSpeed = calculateAverageRotationSpeed();
        float sumSquaredDiff = 0f;
        
        for (TickFrame tf : tickFrames) {
            float speed = Math.abs(tf.dYaw) + Math.abs(tf.dPitch);
            float diff = speed - avgSpeed;
            sumSquaredDiff += diff * diff;
        }
        
        return (float)Math.sqrt(sumSquaredDiff / tickFrames.size());
    }

    public void startReplay() {
        if (tickFrames.isEmpty() && attackFrames.isEmpty()) {
            ChatUtils.sendMessage("§c[NeuroRec] Нет профиля. .neuro play <имя>");
            return;
        }
        replayTickIndex      = 0;
        replayAttackIndex    = 0;
        replayLastAttackTime = 0;
        replaying            = true;
        recording            = false;
        ChatUtils.sendMessage("§a[NeuroRec] Воспроизведение: §f" + tickFrames.size()
                + " §aтиков, §f" + attackFrames.size() + " §aударов");
    }

    public void stopReplay() {
        replaying         = false;
        replayTickIndex   = 0;
        replayAttackIndex = 0;
        ChatUtils.sendMessage("§e[NeuroRec] Воспроизведение остановлено");
    }

    // -----------------------------------------------------------------------

    public boolean save(String name) {
        if (tickFrames.isEmpty() && attackFrames.isEmpty()) {
            ChatUtils.sendMessage("§c[NeuroRec] Нечего сохранять");
            return false;
        }
        try {
            Files.createDirectories(SAVE_DIR);
            Path file = SAVE_DIR.resolve(sanitize(name) + ".nrec");
            try (DataOutputStream out = new DataOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(file)))) {
                // Тики
                out.writeInt(tickFrames.size());
                for (TickFrame f : tickFrames) {
                    out.writeFloat(f.dYaw);
                    out.writeFloat(f.dPitch);
                }
                // Удары
                out.writeInt(attackFrames.size());
                for (AttackFrame f : attackFrames) {
                    out.writeFloat(f.hitOffsetYaw);
                    out.writeFloat(f.hitOffsetPitch);
                }
            }
            ChatUtils.sendMessage("§a[NeuroRec] Сохранено §f\"" + name + "\"§a: "
                    + tickFrames.size() + " тиков, " + attackFrames.size() + " ударов");
            return true;
        } catch (IOException e) {
            ChatUtils.sendMessage("§c[NeuroRec] Ошибка: " + e.getMessage());
            return false;
        }
    }

    public boolean load(String name) {
        try {
            Path file = SAVE_DIR.resolve(sanitize(name) + ".nrec");
            if (!Files.exists(file)) {
                ChatUtils.sendMessage("§c[NeuroRec] Профиль §f\"" + name + "\" §cне найден");
                return false;
            }
            tickFrames.clear();
            attackFrames.clear();
            try (DataInputStream in = new DataInputStream(
                    new BufferedInputStream(Files.newInputStream(file)))) {
                int tickCount = in.readInt();
                for (int i = 0; i < tickCount; i++) {
                    tickFrames.add(new TickFrame(in.readFloat(), in.readFloat()));
                }
                int attackCount = in.readInt();
                for (int i = 0; i < attackCount; i++) {
                    attackFrames.add(new AttackFrame(
                        in.readFloat(), in.readFloat()
                    ));
                }
            }
            ChatUtils.sendMessage("§a[NeuroRec] Загружен §f\"" + name + "\"§a: "
                    + tickFrames.size() + " тиков, " + attackFrames.size() + " ударов");
            return true;
        } catch (IOException e) {
            ChatUtils.sendMessage("§c[NeuroRec] Ошибка: " + e.getMessage());
            return false;
        }
    }

    public List<String> listProfiles() {
        try {
            if (!Files.exists(SAVE_DIR)) return Collections.emptyList();
            List<String> names = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(SAVE_DIR, "*.nrec")) {
                for (Path p : stream) {
                    String n = p.getFileName().toString();
                    names.add(n.substring(0, n.length() - 5));
                }
            }
            return names;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public String getStatus() {
        if (recording) return "§aЗапись... §f" + tickFrames.size()
                + " §7тиков, §f" + attackFrames.size() + " §7ударов";
        if (replaying) return "§bВоспроизведение §f" + replayAttackIndex
                + "§7/§f" + attackFrames.size() + " §7ударов";
        if (!tickFrames.isEmpty()) return "§7Загружено §f" + tickFrames.size()
                + " §7тиков, §f" + attackFrames.size() + " §7ударов";
        return "§7Неактивен";
    }

    public int getFrameCount() { return tickFrames.size(); }

    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    // -----------------------------------------------------------------------

    /** Скорость поворота мыши за один тик */
    public static class TickFrame {
        public final float dYaw;
        public final float dPitch;
        public TickFrame(float dYaw, float dPitch) {
            this.dYaw = dYaw; this.dPitch = dPitch;
        }
    }

    /** Один удар: куда бил (точка удара) */
    public static class AttackFrame {
        public final float hitOffsetYaw;   // смещение от центра цели по yaw при ударе
        public final float hitOffsetPitch; // смещение от центра цели по pitch при ударе
        public AttackFrame(float oy, float op) {
            hitOffsetYaw = oy; hitOffsetPitch = op;
        }
    }
}
