package tech.lightclient.client.utils.player;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public final class TpGrim {
    private static final double STEP_MIN = 7.0D;
    private static final double STEP_MAX = 9.5D;
    private static final int WORLD_MIN_Y = -64;
    private static final int WORLD_MAX_Y = 320;
    private static final double CLIMB_THRESHOLD = 2.0D;

    private final Map<String, Vec3d> positions = new HashMap<>();
    private final Consumer<String> info;
    private final Consumer<String> success;
    private final Consumer<String> error;
    private Vec3d destination;
    private boolean active;
    private int waitTicks;
    private int stepCount;

    public TpGrim(Consumer<String> info, Consumer<String> success, Consumer<String> error) {
        this.info = info;
        this.success = success;
        this.error = error;
    }

    public void start(double x, double y, double z) {
        destination = new Vec3d(x, y, z);
        active = true;
        waitTicks = 0;
        stepCount = 0;
        success.accept(String.format("ТП → %.0f %.0f %.0f", x, y, z));
    }

    public boolean startToPlayer(String name) {
        Vec3d position = positions.get(name.toLowerCase());
        if (position == null) {
            error.accept("Игрок не найден: " + name);
            if (!positions.isEmpty()) {
                info.accept("Доступные: " + String.join(", ", positions.keySet()));
            }
            return false;
        }

        success.accept(String.format("ТП к игроку %s (%.0f %.0f %.0f)", name, position.x, position.y, position.z));
        start(position.x, position.y, position.z);
        return true;
    }

    public void stop() {
        active = false;
        info.accept("ТП остановлен.");
    }

    public boolean isActive() {
        return active;
    }

    public void tick(MinecraftClient minecraft) {
        updatePositions(minecraft);
        if (!active || destination == null) {
            return;
        }

        ClientPlayerEntity player = minecraft.player;
        World world = minecraft.world;
        if (player == null || world == null) {
            return;
        }

        if (waitTicks > 0) {
            waitTicks--;
            sendCurrentPosition(player);
            player.setVelocity(0, 0, 0);
            return;
        }

        Vec3d current = player.getEntityPos();
        double distXZ = Math.hypot(destination.x - current.x, destination.z - current.z);
        if (distXZ < 1.5D) {
            active = false;
            player.setVelocity(0, 0, 0);
            player.fallDistance = 0.0F;
            success.accept(String.format("Прибыли! (%d шагов)", stepCount));
            return;
        }

        Vec3d direction = destination.subtract(current).normalize();
        double moveDist = Math.min(distXZ, ThreadLocalRandom.current().nextDouble(STEP_MIN, STEP_MAX));
        double sideOffset = ThreadLocalRandom.current().nextDouble(-0.4D, 0.4D);
        Vec3d side = new Vec3d(-direction.z, 0, direction.x).multiply(sideOffset);
        Vec3d nextXZ = current.add(direction.multiply(moveDist)).add(side);

        if (isHole(world, nextXZ.x, nextXZ.z, current.y)) {
            nextXZ = bypassHole(world, current, nextXZ);
            info.accept("⚠ Дыра обнаружена — обход");
        }

        double surfaceAtNext = getRealSurfaceY(world, nextXZ.x, nextXZ.z);
        boolean climbing = surfaceAtNext - current.y > CLIMB_THRESHOLD;
        double finalY = climbing
                ? findFullBuriedY(world, nextXZ.x, nextXZ.z)
                : findHalfBuriedY(world, nextXZ.x, nextXZ.z);

        performMove(player, new Vec3d(nextXZ.x, finalY, nextXZ.z));
        stepCount++;
        if (stepCount % 4 == 0) {
            waitTicks = ThreadLocalRandom.current().nextInt(12, 18);
            info.accept(String.format("%s | Y=%.1f | Пауза %d | Осталось %.0f",
                    climbing ? "▲" : "→", finalY, waitTicks, distXZ));
        } else {
            waitTicks = ThreadLocalRandom.current().nextInt(2, 4);
        }
    }

    private boolean isSolid(BlockState state) {
        return !state.isAir() && state.getFluidState().isEmpty() && state.blocksMovement();
    }

    private double getRealSurfaceY(World world, double x, double z) {
        int blockX = (int) Math.floor(x);
        int blockZ = (int) Math.floor(z);
        for (int y = WORLD_MAX_Y; y >= WORLD_MIN_Y; y--) {
            if (isSolid(world.getBlockState(new BlockPos(blockX, y, blockZ)))) {
                return y + 1.0D;
            }
        }
        return WORLD_MIN_Y + 1.0D;
    }

    private double findHalfBuriedY(World world, double x, double z) {
        return getRealSurfaceY(world, x, z) - 0.9D;
    }

    private double findFullBuriedY(World world, double x, double z) {
        int blockX = (int) Math.floor(x);
        int blockZ = (int) Math.floor(z);
        double surfaceY = getRealSurfaceY(world, x, z);
        int surfaceBlock = (int) Math.floor(surfaceY) - 1;
        for (int y = surfaceBlock; y >= Math.max(WORLD_MIN_Y, surfaceBlock - 5); y--) {
            BlockState lower = world.getBlockState(new BlockPos(blockX, y, blockZ));
            BlockState upper = world.getBlockState(new BlockPos(blockX, y + 1, blockZ));
            if (isSolid(lower) && isSolid(upper)) {
                return y + 0.1D;
            }
        }
        return surfaceY - 0.9D;
    }

    private boolean isHole(World world, double x, double z, double currentY) {
        return currentY - getRealSurfaceY(world, x, z) > 4.0D;
    }

    private Vec3d bypassHole(World world, Vec3d current, Vec3d nextXZ) {
        Vec3d direction = nextXZ.subtract(current).normalize();
        Vec3d perpendicular = new Vec3d(-direction.z, 0, direction.x);
        double[] offsets = {2.0D, -2.0D, 4.0D, -4.0D, 6.0D, -6.0D};
        for (double offset : offsets) {
            Vec3d candidate = nextXZ.add(perpendicular.multiply(offset));
            if (!isHole(world, candidate.x, candidate.z, current.y)) {
                return candidate;
            }
        }
        return current.add(destination.subtract(current).normalize().multiply(3.0D));
    }

    private void performMove(ClientPlayerEntity player, Vec3d target) {
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(
                target.x, target.y, target.z,
                player.getYaw(), player.getPitch(),
                true, false
        ));
        player.setPos(target.x, target.y, target.z);
        player.setVelocity(0, 0, 0);
        player.fallDistance = 0.0F;
    }

    private void sendCurrentPosition(ClientPlayerEntity player) {
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(
                player.getX(), player.getY(), player.getZ(),
                player.getYaw(), player.getPitch(),
                true, false
        ));
    }

    private void updatePositions(MinecraftClient minecraft) {
        if (minecraft.world == null) {
            return;
        }
        minecraft.world.getPlayers().forEach(player -> {
            if (player != minecraft.player) {
                positions.put(player.getName().getString().toLowerCase(), player.getEntityPos());
            }
        });
    }
}
