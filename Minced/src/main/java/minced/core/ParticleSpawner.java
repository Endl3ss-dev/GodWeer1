/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.ClientState;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ParticleSpawner
implements CancellableEvent {
    long field9044;
    static double field9045 = 1.0E9;
    public ClientState field9046 = new ClientState();
    public long field9047;
    float field9048;
    static double field9049 = 60.0;
    Vec3d O;
    public Vec3d field9050;
    public float field9051;
    static double field9052 = -0.8;
    float field9053;
    static double field9054;
    static double field9055;
    static double field9056;
    static double field9057;
    static double field9058;
    double field9059;
    static double field9060;
    public float field9061 = 1.0f;
    public int field9062;
    public int field9063;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field9064;

    public void method2553() {
        double v26;
        double v24;
        long v1 = System.nanoTime();
        double v3 = (double)(v1 - this.field9044) / field9045;
        this.field9044 = v1;
        float v5 = Math.min(1.0f, (float)this.field9046.method3112() / (float)this.field9047);
        double v6 = Math.pow(1.0 - (double)v5, this.field9048);
        double v8 = v3 * field9049;
        double v10 = this.O.x;
        double v12 = this.O.y;
        double v14 = this.O.z;
        double v16 = this.field9050.x;
        double v22 = v16 + v10 * v6 * v8;
        double v18 = this.field9050.y;
        double v20 = this.field9050.z;
        if (ParticleSpawner.method2554(v22, v18, v20, this.field9051) == null) {
            v10 *= field9052;
            v22 = v16;
        }
        if (ParticleSpawner.method2554(v22, v24 = v18 + v12 * v6 * v8, v20, this.field9051) == null) {
            v12 = this.field9053 > 0.0f ? (v12 *= -(field9054 + (double)this.field9053)) : (v12 *= field9055);
            v24 = v18;
        }
        if (ParticleSpawner.method2554(v22, v24, v26 = v20 + v14 * v6 * v8, this.field9051) == null) {
            v14 *= field9056;
            v26 = v20;
        }
        this.field9050 = new Vec3d(v22, v24, v26);
        this.O = new Vec3d(v10 * field9057, v12 * field9058 - this.field9059, v14 * field9060);
        this.field9061 = 1.0f - v5;
    }

    public ParticleSpawner(double param1, double param3, double param5, Vec3d param7, int param8, float param9, long param10, float param12, double param13, float param15, int param16) {
        this.field9050 = new Vec3d(param1, param3, param5);
        this.O = param7;
        this.field9062 = param8;
        this.field9051 = param9;
        this.field9047 = param10;
        this.field9046.method3114();
        this.field9044 = System.nanoTime();
        this.field9048 = param12;
        this.field9059 = param13;
        this.field9053 = param15;
        this.field9063 = param16;
    }

    public static Vec3d method2554(double param0, double param2, double param4, float param6) {
        if (ParticleSpawner.I.world == null) {
            return new Vec3d(param0, param2, param4);
        }
        double v7 = (double)param6 * field9064;
        int v9 = MathHelper.floor((double)(param0 - v7));
        int v10 = MathHelper.floor((double)(param0 + v7));
        int v11 = MathHelper.floor((double)(param2 - v7));
        int v12 = MathHelper.floor((double)(param2 + v7));
        int v13 = MathHelper.floor((double)(param4 - v7));
        int v14 = MathHelper.floor((double)(param4 + v7));
        BlockPos.Mutable v15 = new BlockPos.Mutable();
        for (int v16 = v9; v16 <= v10; ++v16) {
            for (int v17 = v11; v17 <= v12; ++v17) {
                for (int v18 = v13; v18 <= v14; ++v18) {
                    BlockState v19 = ParticleSpawner.I.world.getBlockState((BlockPos)v15.set(v16, v17, v18));
                    if (v19.isAir()) continue;
                    return null;
                }
            }
        }
        return new Vec3d(param0, param2, param4);
    }

    static {
        field9054 = 1.5;
        field9055 = -1.5;
        field9056 = -0.8;
        field9057 = 0.9999;
        field9058 = 0.9999;
        field9060 = 0.9999;
        field9064 = 0.5;
    }
}



