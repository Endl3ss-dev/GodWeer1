package tech.lightclient.scripting.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Optional;
import java.util.function.Predicate;

/** "What is in front of me" queries, mirroring the "Лучи и прицел" docs. */
public final class RaycastApi {

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    /** Result of a ray: a block, an entity, or nothing. */
    public abstract static class Hit {
        private final Vec3d position;
        private final double distance;

        Hit(Vec3d position, double distance) {
            this.position = position;
            this.distance = distance;
        }

        public Vec3d position() { return position; }

        public double distance() { return distance; }

        public boolean isBlock() { return this instanceof OnBlock; }

        public boolean isEntity() { return this instanceof OnEntity; }

        public boolean isMiss() { return this instanceof None; }

        /** The ray hit a block. */
        public static final class OnBlock extends Hit {
            private final BlockPos pos;
            private final Direction side;

            OnBlock(Vec3d position, double distance, BlockPos pos, Direction side) {
                super(position, distance);
                this.pos = pos;
                this.side = side;
            }

            public int blockX() { return pos.getX(); }

            public int blockY() { return pos.getY(); }

            public int blockZ() { return pos.getZ(); }

            /** Face the ray came in through, e.g. "UP". */
            public String side() { return side == null ? "" : side.name(); }
        }

        /** The ray hit an entity. */
        public static final class OnEntity extends Hit {
            private final WorldApi.ScriptEntity entity;

            OnEntity(Vec3d position, double distance, WorldApi.ScriptEntity entity) {
                super(position, distance);
                this.entity = entity;
            }

            public WorldApi.ScriptEntity entity() { return entity; }
        }

        /** The ray hit nothing. */
        public static final class None extends Hit {
            None(Vec3d position, double distance) {
                super(position, distance);
            }
        }
    }

    // ---- crosshair -------------------------------------------------------------------------

    /** The entity you are looking at, or null. */
    public WorldApi.ScriptEntity entityAtCrosshair(double range) {
        return entityAtCrosshair(range, null);
    }

    public WorldApi.ScriptEntity entityAtCrosshair(double range, Predicate<WorldApi.ScriptEntity> filter) {
        Hit hit = crosshair(range, false, filter);
        return hit instanceof Hit.OnEntity e ? e.entity() : null;
    }

    public Hit crosshair(double range) {
        return crosshair(range, true, null);
    }

    public Hit crosshair(double range, boolean includeBlocks) {
        return crosshair(range, includeBlocks, null);
    }

    public Hit crosshair(double range, boolean includeBlocks, Predicate<WorldApi.ScriptEntity> filter) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.world == null) return new Hit.None(Vec3d.ZERO, 0);
        Vec3d from = mc.player.getEyePos();
        Vec3d direction = mc.player.getRotationVec(1f);
        return cast(from, from.add(direction.multiply(range)), includeBlocks, filter);
    }

    /** Casts along a supplied yaw/pitch rather than where you are actually looking. */
    public Hit from(ScriptRotations.Rotation rotation, double range, boolean includeBlocks) {
        return from(rotation, range, includeBlocks, null);
    }

    public Hit from(ScriptRotations.Rotation rotation, double range, boolean includeBlocks,
                    Predicate<WorldApi.ScriptEntity> filter) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.world == null || rotation == null) return new Hit.None(Vec3d.ZERO, 0);
        Vec3d from = mc.player.getEyePos();
        Vec3d dir = Vec3d.fromPolar(rotation.pitch(), rotation.yaw());
        return cast(from, from.add(dir.multiply(range)), includeBlocks, filter);
    }

    private Hit cast(Vec3d from, Vec3d to, boolean includeBlocks, Predicate<WorldApi.ScriptEntity> filter) {
        MinecraftClient mc = mc();
        if (mc.world == null || mc.player == null) return new Hit.None(from, 0);

        double blockDistance = Double.MAX_VALUE;
        BlockHitResult blockHit = null;
        if (includeBlocks) {
            blockHit = mc.world.raycast(new RaycastContext(from, to,
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
            if (blockHit != null && blockHit.getType() == HitResult.Type.BLOCK) {
                blockDistance = from.distanceTo(blockHit.getPos());
            }
        }

        // Only look for entities nearer than the block we already hit.
        WorldApi.ScriptEntity bestEntity = null;
        Vec3d bestPoint = null;
        double bestDistance = blockDistance;

        Box search = new Box(from, to).expand(1.0);
        for (Entity e : mc.world.getOtherEntities(mc.player, search, ent -> ent.isAlive() && ent.canHit())) {
            WorldApi.ScriptEntity wrapped = new WorldApi.ScriptEntity(e);
            if (filter != null && !filter.test(wrapped)) continue;
            Optional<Vec3d> clip = e.getBoundingBox().expand(e.getTargetingMargin()).raycast(from, to);
            if (clip.isEmpty()) continue;
            double d = from.distanceTo(clip.get());
            if (d < bestDistance) {
                bestDistance = d;
                bestEntity = wrapped;
                bestPoint = clip.get();
            }
        }

        if (bestEntity != null) {
            return new Hit.OnEntity(bestPoint, bestDistance, bestEntity);
        }
        if (blockHit != null && blockHit.getType() == HitResult.Type.BLOCK) {
            return new Hit.OnBlock(blockHit.getPos(), blockDistance, blockHit.getBlockPos(), blockHit.getSide());
        }
        return new Hit.None(to, from.distanceTo(to));
    }

    // ---- blocks only -----------------------------------------------------------------------

    /** shape: COLLIDER, OUTLINE or VISUAL. fluids: NONE, SOURCE_ONLY or ANY. */
    public Hit blocks(Vec3d from, Vec3d to, String shape, String fluids) {
        MinecraftClient mc = mc();
        if (mc.world == null || mc.player == null) return new Hit.None(from, 0);
        RaycastContext.ShapeType shapeType = switch (shape == null ? "" : shape.toUpperCase()) {
            case "OUTLINE" -> RaycastContext.ShapeType.OUTLINE;
            case "VISUAL" -> RaycastContext.ShapeType.VISUAL;
            default -> RaycastContext.ShapeType.COLLIDER;
        };
        RaycastContext.FluidHandling fluidType = switch (fluids == null ? "" : fluids.toUpperCase()) {
            case "SOURCE_ONLY" -> RaycastContext.FluidHandling.SOURCE_ONLY;
            case "ANY" -> RaycastContext.FluidHandling.ANY;
            default -> RaycastContext.FluidHandling.NONE;
        };
        BlockHitResult hit = mc.world.raycast(new RaycastContext(from, to, shapeType, fluidType, mc.player));
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return new Hit.None(to, from.distanceTo(to));
        }
        return new Hit.OnBlock(hit.getPos(), from.distanceTo(hit.getPos()), hit.getBlockPos(), hit.getSide());
    }

    // ---- visibility ------------------------------------------------------------------------

    /** True when no block sits between the two points. */
    public boolean canSee(Vec3d from, Vec3d to) {
        MinecraftClient mc = mc();
        if (mc.world == null || mc.player == null) return false;
        BlockHitResult hit = mc.world.raycast(new RaycastContext(from, to,
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
        return hit == null || hit.getType() == HitResult.Type.MISS;
    }

    /** Point on an entity's box the ray meets, or null on a miss. */
    public Vec3d hitOn(WorldApi.ScriptEntity entity, Vec3d from, Vec3d to) {
        if (entity == null) return null;
        return entity.raw().getBoundingBox().raycast(from, to).orElse(null);
    }
}
