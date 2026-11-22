/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package ru.decide.module.impl.render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_638;
import net.minecraft.class_746;
import ru.decide.utils.render.ModelBoxCapture;

@Environment(value=EnvType.CLIENT)
public final class ModelCollapseSnapshots {
    private static final long CAPTURE_INTERVAL_MS = 100L;
    private static final long IDLE_INTERVAL_MS = 400L;
    private static final long SNAPSHOT_TTL_MS = 2500L;
    private static final long SNAPSHOT_USE_MS = 1200L;
    private static final long ATTACK_MEMORY_MS = 2000L;
    private static final int MAX_TRACKED = 8;
    private static final int MAX_CAPTURES_PER_FRAME = 2;
    private static final double SNAPSHOT_RANGE = 32.0;
    private static final float DANGER_HEALTH = 0.6f;
    private static final Map<Integer, Snapshot> snapshots = new HashMap<Integer, Snapshot>();
    private static final Map<Integer, Long> lastCapture = new HashMap<Integer, Long>();
    private static final Map<Integer, Long> attacked = new HashMap<Integer, Long>();

    private ModelCollapseSnapshots() {
    }

    public static void noteAttack(class_1309 entity) {
        attacked.put(entity.method_5628(), System.currentTimeMillis());
    }

    public static void clear() {
        snapshots.clear();
        lastCapture.clear();
        attacked.clear();
    }

    public static void tick(float partialTick, double maxDistance, Predicate<class_1309> filter) {
        class_310 client = class_310.method_1551();
        class_638 level = client.field_1687;
        if (level == null) {
            return;
        }
        class_746 player = client.field_1724;
        if (player == null) {
            return;
        }
        long now = System.currentTimeMillis();
        snapshots.values().removeIf(s -> now - s.takenMs() > 2500L);
        lastCapture.values().removeIf(at -> now - at > 2500L);
        attacked.values().removeIf(at -> now - at > 2000L);
        double range = Math.min(maxDistance, 32.0);
        double maxSq = range * range;
        int taken = 0;
        for (class_1297 entity : level.method_18112()) {
            Long last;
            boolean danger;
            if (taken >= 2) break;
            if (!(entity instanceof class_1309)) continue;
            class_1309 living = (class_1309)entity;
            if (entity == player || living.method_31481() || !living.method_5805() || entity.method_5858((class_1297)player) > maxSq || !filter.test(living) || !(danger = ModelCollapseSnapshots.inDanger(living, now)) && snapshots.size() >= 8 && !snapshots.containsKey(living.method_5628()) || (last = lastCapture.get(living.method_5628())) != null && now - last < (danger ? 100L : 400L)) continue;
            lastCapture.put(living.method_5628(), now);
            List<ModelBoxCapture.Box> boxes = ModelBoxCapture.capture(living, partialTick, false);
            if (boxes.isEmpty()) continue;
            class_243 motion = living.method_18798();
            snapshots.put(living.method_5628(), new Snapshot(boxes, class_3532.method_16436((double)partialTick, (double)entity.field_6038, (double)living.method_23317()), class_3532.method_16436((double)partialTick, (double)entity.field_5971, (double)living.method_23318()), class_3532.method_16436((double)partialTick, (double)entity.field_5989, (double)living.method_23321()), (float)motion.field_1352, (float)motion.field_1351, (float)motion.field_1350, living.method_17682(), now, danger));
            ++taken;
        }
    }

    public static void collectVanished(long maxAgeMs, BiPredicate<Integer, Snapshot> handler) {
        if (snapshots.isEmpty()) {
            return;
        }
        class_638 level = class_310.method_1551().field_1687;
        if (level == null) {
            return;
        }
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Integer, Snapshot>> it = snapshots.entrySet().iterator();
        while (it.hasNext()) {
            class_1309 living;
            int id;
            class_1297 entity;
            boolean gone;
            Map.Entry<Integer, Snapshot> entry = it.next();
            Snapshot snapshot = entry.getValue();
            if (now - snapshot.takenMs() > maxAgeMs || !snapshot.danger() || !(gone = (entity = level.method_8469(id = entry.getKey().intValue())) == null || entity.method_31481() || entity instanceof class_1309 && !(living = (class_1309)entity).method_5805()) || !handler.test(id, snapshot)) continue;
            it.remove();
            lastCapture.remove(id);
        }
    }

    public static Snapshot take(int entityId) {
        Snapshot snapshot = snapshots.remove(entityId);
        if (snapshot == null) {
            return null;
        }
        lastCapture.remove(entityId);
        if (System.currentTimeMillis() - snapshot.takenMs() > 1200L) {
            return null;
        }
        return snapshot;
    }

    private static boolean inDanger(class_1309 entity, long now) {
        if (entity.field_6235 > 0) {
            return true;
        }
        float max = entity.method_6063();
        if (max > 0.0f && entity.method_6032() <= max * 0.6f) {
            return true;
        }
        Long hit = attacked.get(entity.method_5628());
        return hit != null && now - hit <= 2000L;
    }

    @Environment(value=EnvType.CLIENT)
    public record Snapshot(List<ModelBoxCapture.Box> boxes, double x, double y, double z, float motionX, float motionY, float motionZ, float height, long takenMs, boolean danger) {
    }
}

