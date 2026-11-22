package tech.lightclient.modules.impl.combat.aura.rotations;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import tech.lightclient.LightClient;
import tech.lightclient.modules.impl.combat.AuraModule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

/**
 * Записчик датасета для нейро-ротации Legit (lneuro).
 *
 * Каждому тику с активной целью соответствует одна строка датасета:
 *   d_yaw, d_pitch (метки - реальное движение прицела игрока за тик),
 *   затем 16 признаков контекста (дистанция, углы, скорости, состояние...).
 *
 * Строки попадают в файл ТОЛЬКО когда зафиксировано попадание по цели
 * (у цели вырос hurtTime - сервер подтвердил урон). Миссы не сохраняются.
 * Повторный .lneuro recode с тем же именем ДОПОЛНЯЕТ файл, а время записи
 * суммируется в <name>.meta.
 */
public final class LegitNeuroRecorder {
    public static final int FEATURE_COUNT = 16;
    private static final int BUFFER_TICKS = 4;
    // Окно, в течение которого рост hurtTime считается следствием своего удара.
    private static final long HIT_CONFIRM_WINDOW_MS = 500L;
    private static final String HEADER = "d_yaw,d_pitch,"
            + "dist,rel_yaw,rel_pitch,t_vel_x,t_vel_y,t_vel_z,"
            + "p_vel_x,p_vel_y,p_vel_z,on_ground,in_water,sprinting,"
            + "attack_cd,fall_dist,prev_dyaw,prev_dpitch";

    private static volatile boolean recording;
    private static volatile String datasetName = "";
    private static long sessionStartMs;
    private static long accumulatedMs;

    private static float prevPlayerYaw;
    private static float prevPlayerPitch;
    private static float prevDYaw;
    private static float prevDPitch;
    private static int hitCount;
    private static final java.util.HashMap<Integer, Integer> hurtCache = new java.util.HashMap<>();
    private static final ArrayDeque<float[]> pendingRows = new ArrayDeque<>();

    private LegitNeuroRecorder() {
    }

    public static boolean isRecording() {
        return recording;
    }

    public static String getName() {
        return datasetName;
    }

    /** Сколько всего мс записано в датасете (с прошлыми сессиями включительно). */
    public static long getTotalMs(String name) {
        Path meta = metaPath(sanitize(name));
        if (!Files.exists(meta)) return 0L;
        try {
            return Long.parseLong(Files.readString(meta, StandardCharsets.UTF_8).trim());
        } catch (Exception e) {
            return 0L;
        }
    }

    public static boolean start(String rawName) {
        String name = sanitize(rawName);
        if (name.isEmpty()) return false;
        if (recording && datasetName.equals(name)) return true;

        stop();
        try {
            Files.createDirectories(dir());
        } catch (IOException e) {
            LightClient.LOGGER.error("[lneuro] cannot create dataset dir", e);
            return false;
        }

        Path csv = csvPath(name);
        if (!Files.exists(csv)) {
            writeLine(csv, HEADER, true);
        }

        datasetName = name;
        accumulatedMs = getTotalMs(name);
        sessionStartMs = System.currentTimeMillis();
        prevPlayerYaw = safeYaw();
        prevPlayerPitch = safePitch();
        prevDYaw = 0f;
        prevDPitch = 0f;
        hitCount = 0;
        pendingRows.clear();
        hurtCache.clear();
        recording = true;
        return true;
    }

    /** Количество строк данных (без заголовка) в csv датасета. */
    public static long getRowCount(String rawName) {
        String name = sanitize(rawName);
        Path csv = csvPath(name);
        if (!Files.exists(csv)) return 0L;
        try {
            long count = 0L;
            var lines = Files.readAllLines(csv, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) { // пропускаем заголовок
                if (!lines.get(i).isBlank()) count++;
            }
            return count;
        } catch (IOException e) {
            return 0L;
        }
    }

    public static void stop() {
        if (!recording) return;
        recording = false;
        accumulatedMs += System.currentTimeMillis() - sessionStartMs;
        writeLine(metaPath(datasetName), String.valueOf(accumulatedMs), false);
        pendingRows.clear();
        datasetName = "";
    }

    /**
     * Вызывается раз в тик из Aura. Не зависит от состояния Aura/цели:
     * контекст берётся от цели Aura, а если её нет - от ближайшего живого
     * существа рядом. Попадание подтверждается глобально по росту hurtTime
     * у любых существ в радиусе 8 блоков (миссы не сохраняются).
     */
    public static void tick(LivingEntity target) {
        if (!recording) return;
        if (mc.player == null || mc.world == null) return;

        long now = System.currentTimeMillis();

        // Засчитываем ТОЛЬКО собственный удар игрока и только если это не мисс.
        // PlayerHitTracker хранит id цели последнего удара локального игрока.
        boolean landedHit = false;
        int attackedId = PlayerHitTracker.getTargetId();
        boolean ownAttackPending = PlayerHitTracker.isPending(HIT_CONFIRM_WINDOW_MS);

        java.util.HashMap<Integer, Integer> newCache = new java.util.HashMap<>();
        for (Entity entity : mc.world.getOtherEntities(mc.player,
                mc.player.getBoundingBox().expand(8.0D))) {
            if (!(entity instanceof LivingEntity living)) continue;
            int hurtTime = living.hurtTime;
            Integer prev = hurtCache.get(entity.getId());
            // Подтверждение урона: hurtTime вырос именно у той цели, по которой
            // бил игрок (сервер подтвердил урон -> это не мисс).
            if (ownAttackPending && entity.getId() == attackedId
                    && prev != null && hurtTime > prev) {
                landedHit = true;
            }
            newCache.put(entity.getId(), hurtTime);
        }
        hurtCache.clear();
        hurtCache.putAll(newCache);

        // FakePlayer клиентский: сервер о нём не знает и урон не подтверждает,
        // поэтому засчитываем свой удар по нему как попадание по факту атаки.
        if (!landedHit && ownAttackPending && isFakePlayer(attackedId)) {
            landedHit = true;
        }

        if (landedHit) {
            PlayerHitTracker.consume();
        }

        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();
        float dYaw = MathHelper.wrapDegrees(yaw - prevPlayerYaw);
        float dPitch = pitch - prevPlayerPitch;
        prevPlayerYaw = yaw;
        prevPlayerPitch = pitch;

        // Контекст: цель Aura или ближайшее живое существо рядом
        LivingEntity context = (target != null && target.isAlive())
                ? target
                : findNearestLiving();
        if (context == null) {
            pendingRows.clear();
            prevDYaw = dYaw;
            prevDPitch = dPitch;
            return;
        }

        int hurtTime = context.hurtTime;

        if (landedHit) {
            hitCount++;
            if (!pendingRows.isEmpty()) {
                Path csv = csvPath(datasetName);
                for (float[] row : pendingRows) {
                    writeLine(csv, rowToCsv(row), false);
                }
                pendingRows.clear();
            }
            if (hitCount % 10 == 0 && mc.player != null) {
                long totalTime = accumulatedMs + (now - sessionStartMs);
                long seconds = totalTime / 1000L;
                mc.player.sendMessage(net.minecraft.text.Text.literal(
                        "\u00A77[lneuro] \u00A7fУдаров: \u00A7a" + hitCount
                                + " \u00A77| \u00A7fВремя записи: \u00A7e"
                                + String.format(java.util.Locale.ROOT, "%d:%02d", seconds / 60, seconds % 60)),
                        false);
            }
        }

        float[] features = buildFeatures(context);
        float[] row = new float[2 + FEATURE_COUNT];
        row[0] = dYaw;
        row[1] = dPitch;
        System.arraycopy(features, 0, row, 2, FEATURE_COUNT);

        pendingRows.addLast(row);
        while (pendingRows.size() > BUFFER_TICKS) {
            pendingRows.removeFirst();
        }

        prevDYaw = dYaw;
        prevDPitch = dPitch;

        // Автостоп по таймауту записи (защита от забытой записи)
        if (now - sessionStartMs > 30L * 60L * 1000L) {
            stop();
        }
    }

    private static LivingEntity findNearestLiving() {
        LivingEntity best = null;
        double bestDist = 6.0D;
        for (Entity entity : mc.world.getOtherEntities(mc.player,
                mc.player.getBoundingBox().expand(6.0D))) {
            if (!(entity instanceof LivingEntity living) || !living.isAlive()) continue;
            double dist = mc.player.distanceTo(living);
            if (dist < bestDist) {
                bestDist = dist;
                best = living;
            }
        }
        return best;
    }

    /** Проверяет, что сущность с данным id - это клиентский FakePlayer. */
    private static boolean isFakePlayer(int entityId) {
        var fake = tech.lightclient.LightClient.getFunctionManager().getFakePlayer();
        if (fake == null || !fake.isState() || mc.world == null) return false;
        Entity entity = mc.world.getEntityById(entityId);
        return entity != null && fake.isFakeEntity(entity);
    }

    /**
     * Признаки контекста для записи И для инференса - общий порядок колонок.
     */
    public static float[] buildFeatures(LivingEntity target) {
        float[] f = new float[FEATURE_COUNT];
        if (mc.player == null || target == null) return f;

        double dist = mc.player.getEyePos().distanceTo(target.getEyePos());

        double dx = target.getX() - mc.player.getX();
        double dz = target.getZ() - mc.player.getZ();
        float relYaw = MathHelper.wrapDegrees((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0)
                - mc.player.getYaw());
        double dy = (target.getEyePos().y - mc.player.getEyePos().y);
        double horiz = Math.max(0.5D, Math.sqrt(dx * dx + dz * dz));
        float relPitch = (float) (-Math.toDegrees(Math.atan2(dy, horiz)) - mc.player.getPitch());

        var tv = target.getVelocity();
        var pv = mc.player.getVelocity();

        f[0] = (float) dist;
        f[1] = relYaw;
        f[2] = relPitch;
        f[3] = (float) tv.x;
        f[4] = (float) tv.y;
        f[5] = (float) tv.z;
        f[6] = (float) pv.x;
        f[7] = (float) pv.y;
        f[8] = (float) pv.z;
        f[9] = mc.player.isOnGround() ? 1f : 0f;
        f[10] = mc.player.isTouchingWater() ? 1f : 0f;
        f[11] = mc.player.isSprinting() ? 1f : 0f;
        f[12] = AuraModule.getNormalizedAttackCooldown();
        f[13] = Math.min(10f, (float) mc.player.fallDistance);
        f[14] = prevDYaw;
        f[15] = prevDPitch;
        return f;
    }

    private static String rowToCsv(float[] row) {
        StringBuilder sb = new StringBuilder(row.length * 12);
        for (int i = 0; i < row.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(String.format(java.util.Locale.ROOT, "%.4f", row[i]));
        }
        return sb.toString();
    }

    private static void writeLine(Path file, String line, boolean header) {
        try {
            if (header && Files.exists(file)) return;
            Files.write(file,
                    (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            LightClient.LOGGER.error("[lneuro] write failed: {}", file, e);
        }
    }

    private static Path dir() {
        return net.fabricmc.loader.api.FabricLoader.getInstance()
                .getGameDir().resolve("light").resolve("lneuro");
    }

    private static Path csvPath(String name) {
        return dir().resolve(name + ".csv");
    }

    private static Path metaPath(String name) {
        return dir().resolve(name + ".meta");
    }

    private static String sanitize(String name) {
        return name == null ? "" : name.replaceAll("[^A-Za-z0-9_-]", "").trim().toLowerCase();
    }

    private static float safeYaw() {
        return mc.player != null ? mc.player.getYaw() : 0f;
    }

    private static float safePitch() {
        return mc.player != null ? mc.player.getPitch() : 0f;
    }
}
