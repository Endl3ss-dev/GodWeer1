/*
 * This file is part of Arcanic Visuals (https://github.com/arcanic-su/arcanic)
 *
 * Copyright (c) 2026 Arcanic
 *
 * Arcanic Visuals is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Arcanic Visuals is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Arcanic Visuals. If not, see <https://www.gnu.org/licenses/>.
 */
package swerve.cosmetics.cape;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;

/** One cloth simulation per player wearing a cape, stepped every tick from how the player moves. */
public class ClothSimulations {
    private static final float GRAVITY = 25.0F;
    private static final float HEIGHT_MULTIPLIER = 6.0F;
    private static final float STRAVE_MULTIPLIER = 2.0F;
    private static final int INITIAL_SIMULATION_STEPS = 8;
    private static final float DEFAULT_GRAVITY_Y = -1.0F;
    private static final float ZERO_FLOAT = 0.0F;
    private static final double FALL_HACK_MULTIPLIER = 10.0;
    private static final double SNEAKING_OFFSET_X = 3.0;
    private static final double SNEAKING_OFFSET_Y = 1.0;
    private static final float UNDERWATER_GRAVITY_DIVISOR = 10.0F;
    private static final float UNDERWATER_HEIGHT_MULTIPLIER = 2.0F;
    private static final float SWIM_PITCH_OFFSET = 90.0F;
    private static final double CLAMP_MIN = 0.0;
    private static final double CLAMP_MAX = 1.0;
    private static ClothSimulations instance;
    private final Map<UUID, ClothLink> entries = new ConcurrentHashMap<>();

    public static ClothSimulations getInstance() {
        if (instance == null) instance = new ClothSimulations();
        return instance;
    }

    public ClothSimulation simulationFor(AbstractClientPlayer player) {
        ClothLink link = entries.get(player.getUUID());
        return link == null ? null : link.simulation;
    }

    public void clear() {
        entries.clear();
    }

    /** Игрок вышел — его симуляция больше не нужна. */
    public void forget(UUID id) {
        entries.remove(id);
    }

    public void tickAll(ClientLevel level) {
        List<AbstractClientPlayer> players = level.players();
        for (int i = 0; i < players.size(); i++) {
            AbstractClientPlayer player = players.get(i);
            if (Capes.resolve(player) == null) continue;
            update(player);
        }
    }

    private void update(AbstractClientPlayer player) {
        ClothLink link = entries.computeIfAbsent(player.getUUID(), id -> new ClothLink());
        ClothSimulation sim = link.simulation;
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        if (!sim.init() && link.hasPrev) {
            double prevX = link.prevX;
            double prevY = link.prevY;
            double prevZ = link.prevZ;
            // [!] В 1.21.4 у игрока нет состояния аватара с координатами плаща,
            //     поэтому лаг плаща считается по позиции игрока.
            double cloakX = x;
            double cloakZ = z;
            float bodyYaw = player.getYRot();
            double forwardX = Math.sin(Math.toRadians(bodyYaw));
            double forwardZ = -Math.cos(Math.toRadians(bodyYaw));
            boolean underwater = player.isUnderWater();
            float heightMultiplier = underwater ? HEIGHT_MULTIPLIER * UNDERWATER_HEIGHT_MULTIPLIER : HEIGHT_MULTIPLIER;
            double falling = clamp((prevY - y) * FALL_HACK_MULTIPLIER, CLAMP_MIN, CLAMP_MAX);
            sim.setGravity(underwater ? GRAVITY / UNDERWATER_GRAVITY_DIVISOR : GRAVITY);
            link.gravityDirection.set(ZERO_FLOAT, DEFAULT_GRAVITY_Y, ZERO_FLOAT);

            // sideways movement relative to where the player faces
            double yaw = Math.toRadians(-player.getYRot());
            double dx = x - prevX;
            double dz = z - prevZ;
            double strafe = Math.cos(yaw) * dx - Math.sin(yaw) * dz;

            boolean crouching = player.isCrouching();
            boolean startedSneaking = crouching && !sim.isSneaking();
            double lift = (cloakX - x) * forwardX
                    + (cloakZ - z) * forwardZ
                    + falling
                    + (startedSneaking ? SNEAKING_OFFSET_X : 0);
            double rise = (y - prevY) * heightMultiplier + (startedSneaking ? SNEAKING_OFFSET_Y : 0);
            double sway = -strafe * STRAVE_MULTIPLIER;
            sim.setSneaking(crouching);
            link.movementChange.set((float) lift, (float) rise, (float) sway);
            if (player.isSwimming()) {
                float pitch = player.getXRot() + SWIM_PITCH_OFFSET;
                link.gravityDirection.rotateDegrees(pitch);
                link.movementChange.rotateDegrees(pitch);
            }

            sim.setGravityDirection(link.gravityDirection);
            sim.applyMovement(link.movementChange);
            sim.simulate();
            link.prevX = x;
            link.prevY = y;
            link.prevZ = z;
        } else {
            // first tick for this player: let the cape settle under gravity before it is drawn
            link.prevX = x;
            link.prevY = y;
            link.prevZ = z;
            link.hasPrev = true;
            link.gravityDirection.set(ZERO_FLOAT, DEFAULT_GRAVITY_Y, ZERO_FLOAT);
            for (int i = 0; i < INITIAL_SIMULATION_STEPS; i++) {
                sim.setGravity(GRAVITY);
                sim.setGravityDirection(link.gravityDirection);
                sim.simulate();
            }
        }
    }

    private static double clamp(double value, double min, double max) {
        return value < min ? min : Math.min(value, max);
    }
}
