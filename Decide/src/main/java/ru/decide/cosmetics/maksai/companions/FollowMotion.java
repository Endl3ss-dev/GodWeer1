/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.maksai.companions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class FollowMotion {
    public double x;
    public double y;
    public double z;
    public double yaw;
    public double speed;
    public double previousX;
    public double previousY;
    public double previousZ;
    private boolean ready;

    public void reset() {
        this.ready = false;
    }

    public void tick(double ox, double oy, double oz, double ownerYaw, Ground world) {
        double nz;
        double nx;
        double ny;
        if (!Double.isFinite(ox + oy + oz + ownerYaw)) {
            throw new IllegalArgumentException("owner");
        }
        double angle = Math.toRadians(ownerYaw);
        double tx = ox + Math.cos(angle) * 2.5 - Math.sin(angle);
        double tz = oz + Math.sin(angle) * 2.5 + Math.cos(angle);
        if (!this.ready || Math.hypot(this.x - ox, this.z - oz) > 24.0 || Math.abs(this.y - oy) > 12.0) {
            double ground = world.floor(tx, oy, tz);
            if (!Double.isFinite(ground) || !world.clear(tx, ground, tz)) {
                this.speed = 0.0;
                return;
            }
            this.x = tx;
            this.y = ground;
            this.z = tz;
            this.ready = true;
        }
        this.previousX = this.x;
        this.previousY = this.y;
        this.previousZ = this.z;
        double dx = tx - this.x;
        double dz = tz - this.z;
        double len = Math.hypot(dx, dz);
        double step = Math.min(0.15, Math.max(0.0, len - 0.35));
        if (step > 0.0 && Double.isFinite(ny = world.floor(nx = this.x + dx / len * step, this.y, nz = this.z + dz / len * step)) && Math.abs(ny - this.y) <= 1.5 && world.clear(nx, ny, nz)) {
            this.x = nx;
            this.y = ny;
            this.z = nz;
            double target = Math.toDegrees(Math.atan2(-dx, dz));
            double d = (target - this.yaw) % 360.0;
            if (d > 180.0) {
                d -= 360.0;
            }
            if (d < -180.0) {
                d += 360.0;
            }
            this.yaw += Math.max(-15.0, Math.min(15.0, d));
        }
        this.speed = Math.hypot(this.x - this.previousX, this.z - this.previousZ) * 20.0;
    }

    public boolean ready() {
        return this.ready;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Ground {
        public double floor(double var1, double var3, double var5);

        public boolean clear(double var1, double var3, double var5);
    }
}

