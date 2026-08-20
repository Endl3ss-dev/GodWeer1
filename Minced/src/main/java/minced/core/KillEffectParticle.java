/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import java.util.function.Predicate;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class KillEffectParticle
{
    long field9284;
    long field9285;
    public Vec3d field9286;
    static float field9287 = 0.5f;
    public float field9288;
    static float field9289 = 10.0f;
    public float field9290;

    public boolean method2688(long param1) {
        return param1 - this.field9284 > this.field9285;
    }

    public KillEffectParticle(Vec3d param1, float param2, long param3, long param5) {
        this.field9286 = param1;
        this.field9288 = Math.max(field9287, param2);
        this.field9284 = param3;
        this.field9285 = param5;
        this.field9290 = 0.21913123f * field9289;
    }

    public float method2689(long param1) {
        return MathHelper.clamp((float)((float)(param1 - this.field9284) / (float)this.field9285), (float)0.0f, (float)1.0f);
    }
}

