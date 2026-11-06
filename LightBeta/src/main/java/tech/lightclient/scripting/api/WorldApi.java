package tech.lightclient.scripting.api;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.LightClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/** Reading the world and the entities in it, mirroring the "Игровые данные" docs. */
public final class WorldApi {

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    private static ClientWorld w() {
        return mc().world;
    }

    public boolean loaded() {
        return w() != null;
    }

    // ---- entity wrapper --------------------------------------------------------------------

    /** Script-facing view of an entity. */
    public static final class ScriptEntity {
        private final Entity entity;

        public ScriptEntity(Entity entity) {
            this.entity = entity;
        }

        public Entity raw() {
            return entity;
        }

        public int id() {
            return entity.getId();
        }

        public String name() {
            return entity.getName().getString();
        }

        public String uuid() {
            return entity.getUuidAsString();
        }

        public String type() {
            return Registries.ENTITY_TYPE.getId(entity.getType()).toString();
        }

        public Vec3d position() {
            return entity.getEntityPos();
        }

        public Vec3d eyePosition() {
            return entity.getEyePos();
        }

        /** Smoothed position for the current frame; use this when drawing. */
        public Vec3d renderPosition() {
            float d = mc().getRenderTickCounter().getTickProgress(false);
            return new Vec3d(
                    entity.lastRenderX + (entity.getX() - entity.lastRenderX) * d,
                    entity.lastRenderY + (entity.getY() - entity.lastRenderY) * d,
                    entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ) * d
            );
        }

        public Vec3d velocity() {
            return entity.getVelocity();
        }

        public float yaw() { return entity.getYaw(); }

        public float pitch() { return entity.getPitch(); }

        public float width() { return entity.getWidth(); }

        public float height() { return entity.getHeight(); }

        public Box box() { return entity.getBoundingBox(); }

        public boolean alive() { return entity.isAlive(); }

        public boolean onGround() { return entity.isOnGround(); }

        public boolean sneaking() { return entity.isSneaking(); }

        public boolean sprinting() { return entity.isSprinting(); }

        public boolean inWater() { return entity.isTouchingWater(); }

        public boolean onFire() { return entity.isOnFire(); }

        public boolean isPlayer() { return entity instanceof PlayerEntity; }

        public boolean isSelf() { return entity == mc().player; }

        public boolean isLiving() { return entity instanceof LivingEntity; }

        /** True when the entity is on the client's friends list. */
        public boolean isAlly() {
            try {
                return isPlayer() && LightClient.getFriendStorage() != null
                        && LightClient.getFriendStorage().isFriend(name());
            } catch (Throwable t) {
                return false;
            }
        }

        public float health() {
            return entity instanceof LivingEntity l ? l.getHealth() : 0f;
        }

        public float maxHealth() {
            return entity instanceof LivingEntity l ? l.getMaxHealth() : 0f;
        }

        public double distanceTo(Vec3d point) {
            return entity.getEntityPos().distanceTo(point);
        }

        public double squaredDistanceTo(Vec3d point) {
            return entity.getEntityPos().squaredDistanceTo(point);
        }

        /** Non-null only for living entities; useful for the health checks in the docs. */
        public ScriptEntity asLiving() {
            return entity instanceof LivingEntity ? this : null;
        }
    }

    // ---- block wrapper ---------------------------------------------------------------------

    /** Script-facing view of a block at a position. */
    public static final class ScriptBlock {
        private final BlockPos pos;
        private final BlockState state;

        public ScriptBlock(BlockPos pos, BlockState state) {
            this.pos = pos;
            this.state = state;
        }

        public String id() {
            return Registries.BLOCK.getId(state.getBlock()).toString();
        }

        public boolean isA(String blockId) {
            String want = blockId.contains(":") ? blockId : "minecraft:" + blockId;
            return id().equals(want);
        }

        public int x() { return pos.getX(); }

        public int y() { return pos.getY(); }

        public int z() { return pos.getZ(); }

        public Vec3d position() {
            return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
        }

        public boolean air() { return state.isAir(); }

        public boolean replaceable() {
            return state.isAir() || state.isReplaceable();
        }

        public boolean blocksMovement() {
            ClientWorld world = w();
            return world != null && !state.getCollisionShape(world, pos).isEmpty();
        }

        public boolean liquid() { return !state.getFluidState().isEmpty(); }

        /** Collision box in world coordinates, so a slab is genuinely half height. */
        public Box box() {
            ClientWorld world = w();
            if (world == null || state.isAir()) {
                return new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
            }
            var shape = state.getOutlineShape(world, pos);
            if (shape.isEmpty()) {
                return new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
            }
            return shape.getBoundingBox().offset(pos.getX(), pos.getY(), pos.getZ());
        }

        public float hardness() {
            ClientWorld world = w();
            return world == null ? 0f : state.getHardness(world, pos);
        }
    }

    // ---- blocks ----------------------------------------------------------------------------

    public ScriptBlock block(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        ClientWorld world = w();
        return new ScriptBlock(pos, world == null ? net.minecraft.block.Blocks.AIR.getDefaultState() : world.getBlockState(pos));
    }

    public ScriptBlock block(Vec3d position) {
        return block((int) Math.floor(position.x), (int) Math.floor(position.y), (int) Math.floor(position.z));
    }

    /** True when nothing solid and no entity occupies the box. */
    public boolean isFree(Box box) {
        ClientWorld world = w();
        if (world == null) return false;
        if (!world.isSpaceEmpty(box)) return false;
        for (Entity e : world.getEntities()) {
            if (e == mc().player) continue;
            if (e.isAlive() && e.getBoundingBox().intersects(box)) return false;
        }
        return true;
    }

    // ---- entities --------------------------------------------------------------------------

    public List<ScriptEntity> entities() {
        List<ScriptEntity> out = new ArrayList<>();
        ClientWorld world = w();
        if (world == null) return out;
        for (Entity e : world.getEntities()) out.add(new ScriptEntity(e));
        return out;
    }

    public List<ScriptEntity> players() {
        List<ScriptEntity> out = new ArrayList<>();
        ClientWorld world = w();
        if (world == null) return out;
        for (Entity e : world.getPlayers()) out.add(new ScriptEntity(e));
        return out;
    }

    public ScriptEntity entityById(int id) {
        ClientWorld world = w();
        if (world == null) return null;
        Entity e = world.getEntityById(id);
        return e == null ? null : new ScriptEntity(e);
    }

    public List<ScriptEntity> entitiesNear(Vec3d centre, double radius) {
        return entitiesNear(centre, radius, null);
    }

    public List<ScriptEntity> entitiesNear(Vec3d centre, double radius, Predicate<ScriptEntity> filter) {
        List<ScriptEntity> out = new ArrayList<>();
        ClientWorld world = w();
        if (world == null) return out;
        double rSq = radius * radius;
        for (Entity e : world.getEntities()) {
            if (e.getEntityPos().squaredDistanceTo(centre) > rSq) continue;
            ScriptEntity se = new ScriptEntity(e);
            if (filter != null && !filter.test(se)) continue;
            out.add(se);
        }
        out.sort(Comparator.comparingDouble(a -> a.squaredDistanceTo(centre)));
        return out;
    }

    public ScriptEntity nearestEntity(Vec3d centre, double radius, Predicate<ScriptEntity> filter) {
        List<ScriptEntity> near = entitiesNear(centre, radius, filter);
        return near.isEmpty() ? null : near.get(0);
    }

    // ---- world state -----------------------------------------------------------------------

    public long time() {
        ClientWorld world = w();
        return world == null ? 0L : world.getTimeOfDay();
    }

    public boolean isDay() {
        ClientWorld world = w();
        return world != null && world.isDay();
    }

    public boolean isRaining() {
        ClientWorld world = w();
        return world != null && world.isRaining();
    }

    public String dimension() {
        ClientWorld world = w();
        return world == null ? "" : world.getRegistryKey().getValue().toString();
    }

    // ---- sound -----------------------------------------------------------------------------

    /** Plays a sound for you only, e.g. "minecraft:entity.experience_orb.pickup". */
    public void playSound(String id, float volume, float pitch) {
        Identifier ident = Identifier.tryParse(id.contains(":") ? id : "minecraft:" + id);
        if (ident == null) return;
        mc().execute(() -> {
            ClientWorld world = w();
            if (world == null || mc().player == null) return;
            SoundEvent event = Registries.SOUND_EVENT.get(ident);
            if (event == null) return;
            world.playSoundClient(mc().player.getX(), mc().player.getY(), mc().player.getZ(),
                    event, SoundCategory.MASTER, volume, pitch, false);
        });
    }
}
