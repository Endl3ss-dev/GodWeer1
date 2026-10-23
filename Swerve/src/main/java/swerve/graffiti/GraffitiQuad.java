package swerve.graffiti;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

/**
 * [+] Мировая поза граффити: центр квада и его базис в плоскости грани.
 *
 * <p>{@code right} — «вправо» для зрителя, {@code down} — «вниз» картинки,
 * {@code normal} — наружу от блока. Базис всегда ортонормированный, поэтому
 * запись годится и для отрисовки, и для покадровой интерполяции между гранями
 * (см. {@link #interpolate}).
 *
 * <p>Пара {@code pos + face} хранится рядом с позой: по ней призрак понимает,
 * что цель — та же грань, и не гоняет квад туда-обратно из-за пересчёта базиса.
 */
public record GraffitiQuad(BlockPos pos, Direction face, Vec3 center, Vec3 right, Vec3 down, Vec3 normal) {

    /**
     * Поза грани с учётом поворота рисунка.
     *
     * <p>Поворот вращает {@code right} вокруг нормали по формуле Родрига,
     * {@code down} пересчитывается из нового {@code right} — картинка остаётся
     * в плоскости грани при любом угле.
     */
    public static GraffitiQuad of(BlockPos pos, Direction face, float spin) {
        Vec3 normal = Vec3.atLowerCornerOf(face.getUnitVec3i());
        Vec3 right = baseRight(face);
        Vec3 down = right.cross(normal).normalize();

        float angle = GraffitiSpot.normalizeSpin(spin);
        if (angle != 0.0F) {
            double radians = Math.toRadians(angle);
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            right = right.scale(cos).add(normal.cross(right).scale(sin)).normalize();
            down = right.cross(normal).normalize();
        }

        return new GraffitiQuad(pos.immutable(), face, centerOf(pos, face), right, down, normal);
    }

    /** Центр грани блока. */
    public static Vec3 centerOf(BlockPos pos, Direction face) {
        return new Vec3(
                pos.getX() + 0.5D + face.getStepX() * 0.5D,
                pos.getY() + 0.5D + face.getStepY() * 0.5D,
                pos.getZ() + 0.5D + face.getStepZ() * 0.5D);
    }

    /**
     * «Вправо» для зрителя перед гранью.
     *
     * <p>Для боковых граней это {@code cross(forward, up)}, где {@code forward}
     * смотрит в блок. Для пола и потолка «верх» картинки смотрит на север,
     * а вправо — на восток: иначе рисунок на полу поворачивался бы вслед за камерой.
     */
    public static Vec3 baseRight(Direction face) {
        return switch (face) {
            case NORTH -> new Vec3(-1.0D, 0.0D, 0.0D);
            case SOUTH -> new Vec3(1.0D, 0.0D, 0.0D);
            case EAST -> new Vec3(0.0D, 0.0D, -1.0D);
            case WEST -> new Vec3(0.0D, 0.0D, 1.0D);
            case UP, DOWN -> new Vec3(1.0D, 0.0D, 0.0D);
        };
    }

    /**
     * Промежуточная поза между двумя гранями — для плавного перемещения призрака.
     *
     * <p>Центр летит по небольшой дуге (подъём на {@code sin(πt)}, пропорциональный
     * расстоянию): так переход «стена → пол» читается как перенос, а не как
     * проезд сквозь блок. Базис интерполируется покомпонентно и ортонормализуется
     * заново — картинка не растягивается и не выворачивается.
     */
    public static GraffitiQuad interpolate(GraffitiQuad from, GraffitiQuad to, float t) {
        float clamped = Math.max(0.0F, Math.min(1.0F, t));

        double distance = from.center.distanceTo(to.center);
        double lift = Math.sin(Math.PI * clamped) * distance * 0.35D;
        Vec3 normal = blendNormal(from.normal, to.normal);
        Vec3 center = from.center.lerp(to.center, clamped).add(normal.scale(lift));

        Vec3 right = from.right.lerp(to.right, clamped);
        // Ортогонализация Грама — Шмидта: после лерпа базис перестаёт быть ортонормированным.
        right = right.subtract(normal.scale(right.dot(normal)));
        if (right.lengthSqr() < 1.0E-6D) {
            right = baseRight(to.face);
            right = right.subtract(normal.scale(right.dot(normal)));
        }
        right = right.normalize();

        Vec3 down = right.cross(normal).normalize();
        return new GraffitiQuad(to.pos, to.face, center, right, down, normal);
    }

    /** Нормаль, повёрнутая от одной грани к другой (нулевая пара — берём первую). */
    private static Vec3 blendNormal(Vec3 from, Vec3 to) {
        Vec3 blended = from.lerp(to, 0.5D);
        return blended.lengthSqr() < 1.0E-6D ? from : blended.normalize();
    }

    /** Копия с масштабом квада относительно центра (для «проявления» при установке). */
    public GraffitiQuad scaled(float scale) {
        return new GraffitiQuad(this.pos, this.face, this.center,
                this.right.scale(scale), this.down.scale(scale), this.normal);
    }

    /** Та же грань того же блока? */
    public boolean sameFace(BlockPos otherPos, Direction otherFace) {
        return this.face == otherFace && this.pos.equals(otherPos);
    }
}
