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
 * Записчик датасета для Test нейро-ротации (tneuro).
 * Расширенный вектор: 20 признаков (16 базовых + 4 угловых).
 * Строки записываются только при подтверждённых попаданиях (не миссах).
 */
public final class TNeuroSelfRecorder {

    public static final int FEATURE_COUNT = 20;
    private static final int BUFFER_TICKS = 5;
    private static final long HIT_CONFIRM_WINDOW_MS = 500L;

    private static final String HEADER =
            "d_yaw,d_pitch," +
            "dist,rel_yaw,rel_pitch,t_vel_x,t_vel_y,t_vel_z," +
            "p_vel_x,p_vel_y,p_vel_z,on_ground,in_water,sprinting," +
            "attack_cd,fall_dist,prev_dyaw,prev_dpitch," +
            "sin_rel_yaw,cos_rel_yaw,aim_yaw_err,aim_pitch_err";

    private static volatile boolean recording;
    private static volatile String datasetName = "";
    private static long sessionStartMs;
    private static long accumulatedMs;

    private static float prevPlayerYaw;
    private static float prevPlayerPitch;
    private static float prevDYaw;
    private static float prevDPitch;
    private static float prevRotStateYaw   = 0f;
    private static float prevRotStatePitch = 0f;
    private static int hitCount;
    private static final java.util.HashMap<Integer, Integer> hurtCache = new java.util.HashMap<>();
    private static final ArrayDeque<float[]> pendingRows = new ArrayDeque<>();

    private TNeuroSelfRecorder() {}

    public static boolean isRecording() { return recording; }
    public static String getName()      { return datasetName; }

    public static long getTotalMs(String name) {
        Path meta = metaPath(sanitize(name));
        if (!Files.exists(meta)) return 0L;
        try {
            return Long.parseLong(Files.readString(meta, StandardCharsets.UTF_8).trim());
        } catch (Exception e) { return 0L; }
    }

    public static boolean start(String rawName) {
        String name = sanitize(rawName);
        if (name.isEmpty()) return false;
        if (recording && datasetName.equals(name)) return true;

        stop();
        try { Files.createDirectories(dir()); }
        catch (IOException e) { LightClient.LOGGER.error("[tneuro] cannot create dataset dir", e); return false; }

        Path csv = csvPath(name);
        if (!Files.exists(csv)) writeLine(csv, HEADER, true);

        datasetName    = name;
        accumulatedMs  = getTotalMs(name);
        sessionStartMs = System.currentTimeMillis();
        prevPlayerYaw  = safeYaw();
        prevPlayerPitch = safePitch();
        prevDYaw = prevDPitch = 0f;
        prevRotStateYaw   = safeYaw();
        prevRotStatePitch = safePitch();
        hitCount = 0;
        pendingRows.clear();
        hurtCache.clear();
        recording = true;
        return true;
    }

    public static long getRowCount(String rawName) {
        String name = sanitize(rawName);
        Path csv = csvPath(name);
        if (!Files.exists(csv)) return 0L;
        try {
            long count = 0L;
            var lines = Files.readAllLines(csv, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) if (!lines.get(i).isBlank()) count++;
            return count;
        } catch (IOException e) { return 0L; }
    }

    public static void stop() {
        if (!recording) return;
        recording = false;
        accumulatedMs += System.currentTimeMillis() - sessionStartMs;
        writeLine(metaPath(datasetName), String.valueOf(accumulatedMs), false);
        pendingRows.clear();
        datasetName = "";
    }

    public static void tick(LivingEntity target) {
        if (!recording || mc.player == null || mc.world == null) return;
        long now = System.currentTimeMillis();

        boolean landedHit = false;
        int attackedId = PlayerHitTracker.getTargetId();
        boolean ownAttackPending = PlayerHitTracker.isPending(HIT_CONFIRM_WINDOW_MS);

        var newCache = new java.util.HashMap<Integer, Integer>();
        for (Entity e : mc.world.getOtherEntities(mc.player, mc.player.getBoundingBox().expand(8.0D))) {
            if (!(e instanceof LivingEntity living)) continue;
            int ht = living.hurtTime;
            Integer prev = hurtCache.get(e.getId());
            if (ownAttackPending && e.getId() == attackedId && prev != null && ht > prev)
                landedHit = true;
            newCache.put(e.getId(), ht);
        }
        hurtCache.clear();
        hurtCache.putAll(newCache);

        if (!landedHit && ownAttackPending && isFakePlayer(attackedId)) landedHit = true;
        if (landedHit) PlayerHitTracker.consume();

        float yaw   = mc.player.getYaw();
        float pitch = mc.player.getPitch();
        // Движение мыши за тик — используется только как признак (prev_dyaw/dpitch)
        float dYaw   = MathHelper.wrapDegrees(yaw - prevPlayerYaw);
        float dPitch = pitch - prevPlayerPitch;
        prevPlayerYaw   = yaw;
        prevPlayerPitch = pitch;

        LivingEntity ctx = (target != null && target.isAlive()) ? target : findNearestLiving();
        if (ctx == null) { pendingRows.clear(); prevDYaw = dYaw; prevDPitch = dPitch; return; }

        if (landedHit) {
            hitCount++;
            if (!pendingRows.isEmpty()) {
                Path csv = csvPath(datasetName);
                for (float[] row : pendingRows) writeLine(csv, rowToCsv(row), false);
                pendingRows.clear();
            }
            if (hitCount % 10 == 0 && mc.player != null) {
                long secs = (accumulatedMs + (now - sessionStartMs)) / 1000L;
                mc.player.sendMessage(net.minecraft.text.Text.literal(
                    "\u00A77[tneuro] \u00A7fУдаров: \u00A7a" + hitCount +
                    " \u00A77| \u00A7fВремя: \u00A7e" +
                    String.format(java.util.Locale.ROOT, "%d:%02d", secs / 60, secs % 60)), false);
            }
        }

        float[] features = buildFeatures(ctx);
        float[] row = new float[2 + FEATURE_COUNT];

        // Метки: шаг ротации (stateYaw delta) если Test ротация активна,
        // иначе — движение реального прицела игрока.
        // Это учит модель копировать скорость наводки самой ротации.
        float rotStateYaw   = TNeuroRotation.getStateYaw();
        float rotStatePitch = TNeuroRotation.getStatePitch();
        if (!Float.isNaN(rotStateYaw)) {
            row[0] = MathHelper.wrapDegrees(rotStateYaw   - prevRotStateYaw);
            row[1] = rotStatePitch - prevRotStatePitch;
        } else {
            row[0] = dYaw;
            row[1] = dPitch;
        }
        prevRotStateYaw   = Float.isNaN(rotStateYaw)   ? yaw   : rotStateYaw;
        prevRotStatePitch = Float.isNaN(rotStatePitch) ? pitch : rotStatePitch;

        System.arraycopy(features, 0, row, 2, FEATURE_COUNT);
        pendingRows.addLast(row);
        while (pendingRows.size() > BUFFER_TICKS) pendingRows.removeFirst();

        prevDYaw   = dYaw;
        prevDPitch = dPitch;

        if (now - sessionStartMs > 30L * 60L * 1000L) stop();
    }

    private static LivingEntity findNearestLiving() {
        LivingEntity best = null; double bestDist = 6.0D;
        for (Entity e : mc.world.getOtherEntities(mc.player, mc.player.getBoundingBox().expand(6.0D))) {
            if (!(e instanceof LivingEntity l) || !l.isAlive()) continue;
            double d = mc.player.distanceTo(l);
            if (d < bestDist) { bestDist = d; best = l; }
        }
        return best;
    }

    private static boolean isFakePlayer(int id) {
        var fp = tech.lightclient.LightClient.getFunctionManager().getFakePlayer();
        if (fp == null || !fp.isState() || mc.world == null) return false;
        Entity e = mc.world.getEntityById(id);
        return e != null && fp.isFakeEntity(e);
    }

    /**
     * 20 признаков: 0-15 совместимы с NeuroSelfRecorder, 16-19 — расширение.
     * Порядок должен совпадать с тем, что ожидает модель при инференсе.
     */
    public static float[] buildFeatures(LivingEntity target) {
        float[] f = new float[FEATURE_COUNT];
        if (mc.player == null || target == null) return f;

        double dx = target.getX() - mc.player.getX();
        double dz = target.getZ() - mc.player.getZ();
        double dy = target.getEyePos().y - mc.player.getEyePos().y;
        double horiz = Math.max(0.5D, Math.sqrt(dx * dx + dz * dz));
        double dist  = mc.player.getEyePos().distanceTo(target.getEyePos());

        float relYaw   = MathHelper.wrapDegrees(
                (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0) - mc.player.getYaw());
        float relPitch = (float)(-Math.toDegrees(Math.atan2(dy, horiz)) - mc.player.getPitch());

        var tv = target.getVelocity();
        var pv = mc.player.getVelocity();

        f[0]  = (float) dist;
        f[1]  = relYaw;
        f[2]  = relPitch;
        f[3]  = (float) tv.x;
        f[4]  = (float) tv.y;
        f[5]  = (float) tv.z;
        f[6]  = (float) pv.x;
        f[7]  = (float) pv.y;
        f[8]  = (float) pv.z;
        f[9]  = mc.player.isOnGround()      ? 1f : 0f;
        f[10] = mc.player.isTouchingWater() ? 1f : 0f;
        f[11] = mc.player.isSprinting()     ? 1f : 0f;
        f[12] = AuraModule.getNormalizedAttackCooldown();
        f[13] = Math.min(10f, (float) mc.player.fallDistance);
        f[14] = prevDYaw;
        f[15] = prevDPitch;

        // Расширенные признаки: sin/cos relYaw убирают цикличность угла
        float relYawRad = (float) Math.toRadians(relYaw);
        f[16] = (float) Math.sin(relYawRad);
        f[17] = (float) Math.cos(relYawRad);

        // Ошибка прицела (насколько камера смотрит мимо цели)
        float targetYaw   = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float targetPitch = (float)(-Math.toDegrees(Math.atan2(dy, horiz)));
        f[18] = MathHelper.wrapDegrees(targetYaw   - mc.player.getYaw());
        f[19] = targetPitch - mc.player.getPitch();

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
            Files.write(file, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) { LightClient.LOGGER.error("[tneuro] write failed: {}", file, e); }
    }

    private static Path dir()              { return net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir().resolve("light").resolve("tneuro"); }
    private static Path csvPath(String n)  { return dir().resolve(n + ".csv"); }
    private static Path metaPath(String n) { return dir().resolve(n + ".meta"); }
    private static String sanitize(String n) { return n == null ? "" : n.replaceAll("[^A-Za-z0-9_-]", "").trim().toLowerCase(); }
    private static float safeYaw()   { return mc.player != null ? mc.player.getYaw()   : 0f; }
    private static float safePitch() { return mc.player != null ? mc.player.getPitch() : 0f; }
}
