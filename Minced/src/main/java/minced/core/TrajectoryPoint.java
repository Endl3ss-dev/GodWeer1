/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import net.minecraft.util.math.Vec3d;

public class TrajectoryPoint {
    public Vec3d Р;
    public Vec3d t2;
    public float cfr_renamed_16;
    public float cfr_renamed_17;
    public int cfr_renamed_221;
    public float cfr_renamed_222;
    public Vec3d k2;
    public float cfr_renamed_223;
    public boolean cfr_renamed_224;
    public Vec3d У;
    public float cfr_renamed_225;

    public TrajectoryPoint(Vec3d param1, Vec3d param2, float param3, float param4, int param5, float param6) {
        this.Р = param1;
        this.t2 = param2;
        this.cfr_renamed_16 = param3;
        this.cfr_renamed_17 = param4;
        this.cfr_renamed_221 = param5;
        this.cfr_renamed_222 = param6;
        this.k2 = null;
        this.cfr_renamed_223 = 0.0f;
        this.cfr_renamed_224 = false;
        this.У = null;
        this.cfr_renamed_225 = 0.0f;
    }
}
