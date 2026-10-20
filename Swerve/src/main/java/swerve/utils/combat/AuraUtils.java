package swerve.utils.combat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.minecraft.util.Mth;

/**
 * Геометрия боя для аур: строгие дистанции до хитбокса, углы от взгляда,
 * проверка стен и свободного пространства над головой.
 *
 * <p>Адаптировано из Private/src/java/org/privates/client/utils/rotation/AuraUtil.java
 * и RayTraceUtil.java: перебор точек хитбокса заменён аналитическим clamp'ом
 * (та же ближайшая точка AABB без сотен raytrace-вызовов), все расчёты —
 * без аллокаций в тике.
 */
public final class AuraUtils {

    /** Порог, ниже которого направление на цель считается неопределимым. */
    private static final double EPSILON = 1.0E-7D;

    private AuraUtils() {
    }

    /**
     * Строгая дистанция до цели: от глаз до ближайшей точки хитбокса
     * (getStrictDistance из Private). В отличие от distanceTo, не «прощает»
     * широкие хитбоксы и совпадает с тем, как сервер меряет дистанцию удара.
     */
    public static double strictDistance(Player player, Entity target) {
        AABB box = target.getBoundingBox();
        Vec3 eye = player.getEyePosition(1.0F);
        double dx = Mth.clamp(eye.x, box.minX, box.maxX) - eye.x;
        double dy = Mth.clamp(eye.y, box.minY, box.maxY) - eye.y;
        double dz = Mth.clamp(eye.z, box.minZ, box.maxZ) - eye.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Угол между взглядом игрока и направлением на ближайшую точку цели,
     * в градусах (calculateFOVFromCamera из Private). Точка хитбокса берётся
     * аналитически — ближайшая к глазам, поэтому угол не завышается на краях
     * широких хитбоксов.
     */
    public static double fovFromCamera(Player player, Entity target) {
        AABB box = target.getBoundingBox();
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 view = player.getViewVector(1.0F);
        double dx = Mth.clamp(eye.x, box.minX, box.maxX) - eye.x;
        double dy = Mth.clamp(eye.y, box.minY, box.maxY) - eye.y;
        double dz = Mth.clamp(eye.z, box.minZ, box.maxZ) - eye.z;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < EPSILON) {
            return 0.0D;
        }
        double dot = (view.x * dx + view.y * dy + view.z * dz) / length;
        return Math.toDegrees(Math.acos(Mth.clamp(dot, -1.0D, 1.0D)));
    }

    /**
     * Луч взгляда пересекает хитбокс цели в пределах дистанции
     * (RayTraceUtil.rayTraceEntity из Private). Slab-метод без аллокаций;
     * точка старта внутри хитбокса тоже считается попаданием.
     */
    public static boolean lookRayHitsBox(Player player, Entity target, double distance) {
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 view = player.getViewVector(1.0F);
        AABB box = target.getBoundingBox();
        return rayIntersectsBox(eye.x, eye.y, eye.z, view.x, view.y, view.z, distance, box);
    }

    /** Классический slab-тест пересечения луча с AABB, без ветвлений на аллокации. */
    private static boolean rayIntersectsBox(double px, double py, double pz,
                                            double dx, double dy, double dz,
                                            double distance, AABB box) {
        double tMin = 0.0D;
        double tMax = distance;
        // ось X
        if (dx == 0.0D) {
            if (px < box.minX || px > box.maxX) {
                return false;
            }
        } else {
            double t1 = (box.minX - px) / dx;
            double t2 = (box.maxX - px) / dx;
            if (t1 > t2) {
                double swap = t1;
                t1 = t2;
                t2 = swap;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
                return false;
            }
        }
        // ось Y
        if (dy == 0.0D) {
            if (py < box.minY || py > box.maxY) {
                return false;
            }
        } else {
            double t1 = (box.minY - py) / dy;
            double t2 = (box.maxY - py) / dy;
            if (t1 > t2) {
                double swap = t1;
                t1 = t2;
                t2 = swap;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
                return false;
            }
        }
        // ось Z
        if (dz == 0.0D) {
            if (pz < box.minZ || pz > box.maxZ) {
                return false;
            }
        } else {
            double t1 = (box.minZ - pz) / dz;
            double t2 = (box.maxZ - pz) / dz;
            if (t1 > t2) {
                double swap = t1;
                t1 = t2;
                t2 = swap;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
                return false;
            }
        }
        return true;
    }

    /**
     * Прямая видимость точки: луч от глаз до точки не упирается в блок
     * (isPointVisible из Private).
     */
    public static boolean isPointVisible(Player player, Vec3 point) {
        Vec3 eye = player.getEyePosition(1.0F);
        BlockHitResult result = player.level().clip(new ClipContext(
                eye, point, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return result.getType() == HitResult.Type.MISS;
    }

    /** Scratch-буфер координат точек хитбокса: клиентский поток один, аллокаций в тике нет. */
    private static final double[] POINTS = new double[27];

    /**
     * Ближайшая видимая точка хитбокса цели (getVisibleTargetPoint из Private):
     * восемь опорных точек, из незакрытых блоками выбирается ближайшая к глазам.
     * Если все точки закрыты, возвращается точка груди — как в оригинале.
     */
    public static Vec3 visibleTargetPoint(Player player, LivingEntity target) {
        fillTargetPoints(target);
        double[] p = POINTS;
        Vec3 eye = player.getEyePosition(1.0F);
        double bestDistance = Double.MAX_VALUE;
        int bestIndex = -1;
        for (int i = 0; i < p.length; i += 3) {
            Vec3 point = new Vec3(p[i], p[i + 1], p[i + 2]);
            if (!isPointVisible(player, point)) {
                continue;
            }
            double dx = p[i] - eye.x;
            double dy = p[i + 1] - eye.y;
            double dz = p[i + 2] - eye.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestIndex = i;
            }
        }
        if (bestIndex >= 0) {
            return new Vec3(p[bestIndex], p[bestIndex + 1], p[bestIndex + 2]);
        }
        // Fallback оригинала: позиция цели + 0.85 высоты глаз (грудь).
        return new Vec3(p[24], p[25] + target.getEyeHeight() * 0.85D, p[26]);
    }

    /** Есть ли у хитбокса хотя бы одна точка, не закрытая блоками. */
    public static boolean hasVisiblePoint(Player player, LivingEntity target) {
        fillTargetPoints(target);
        double[] p = POINTS;
        for (int i = 0; i < p.length; i += 3) {
            if (isPointVisible(player, new Vec3(p[i], p[i + 1], p[i + 2]))) {
                return true;
            }
        }
        return false;
    }

    /** Заполнение scratch-буфера опорными точками хитбокса (клиентский поток один). */
    private static void fillTargetPoints(LivingEntity target) {
        AABB box = target.getBoundingBox();
        double[] p = POINTS;
        double cx = (box.minX + box.maxX) * 0.5D;
        double cy = (box.minY + box.maxY) * 0.5D;
        double cz = (box.minZ + box.maxZ) * 0.5D;
        double neckY = box.minY + target.getBbHeight() * 0.75D;
        double midY = box.minY + target.getBbHeight() * 0.5D;
        double headY = box.maxY - 0.1D;
        double shoulderY = box.maxY - 0.2D;
        double insetX = 0.1D;
        double insetZ = 0.1D;
        // верх-центр, центр, грудь, четыре верхних угла, середина
        p[0] = cx; p[1] = headY; p[2] = cz;
        p[3] = cx; p[4] = cy; p[5] = cz;
        p[6] = cx; p[7] = neckY; p[8] = cz;
        p[9] = box.minX + insetX; p[10] = shoulderY; p[11] = box.minZ + insetZ;
        p[12] = box.maxX - insetX; p[13] = shoulderY; p[14] = box.minZ + insetZ;
        p[15] = box.minX + insetX; p[16] = shoulderY; p[17] = box.maxZ - insetZ;
        p[18] = box.maxX - insetX; p[19] = shoulderY; p[20] = box.maxZ - insetZ;
        p[21] = cx; p[22] = midY; p[23] = cz;
        // Центр колоны для fallback: ноги по X/Z, высота груди пересчитывается на месте.
        p[24] = cx; p[25] = box.minY; p[26] = cz;
    }

    /**
     * Свободное пространство над головой (PlayerUtil.isBlockAboveHead из Private,
     * имя оригинала обманывает: true возвращается, когда блоков над головой НЕТ —
     * есть место для прыжка и крита). Проверяется колонна от глаз до +1.5/+2.5 блока.
     */
    public static boolean hasHeadroom(Player player) {
        double half = player.getBbWidth() * 0.5D;
        double top = player.getY() + (player.onGround() ? 2.5D : 1.5D);
        AABB region = new AABB(
                player.getX() - half, player.getEyeY(), player.getZ() - half,
                player.getX() + half, top, player.getZ() + half);
        return player.level().noCollision(player, region);
    }

    /**
     * Игрок в паутине (PlayerUtil.isPlayerInWeb из Private, упрощённо:
     * проверяются блоки в ногах и на уровне глаз вместо сканирования 5x6x5).
     */
    public static boolean isPlayerInWeb(Player player) {
        double x = player.getX();
        double z = player.getZ();
        return player.level().getBlockState(BlockPos.containing(x, player.getY(), z)).is(Blocks.COBWEB)
                || player.level().getBlockState(BlockPos.containing(x, player.getEyeY(), z)).is(Blocks.COBWEB);
    }
}
