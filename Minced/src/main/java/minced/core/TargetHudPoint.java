/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import minced.core.MathUtil;
import net.minecraft.util.math.Vec3d;

public class TargetHudPoint
{
    Vec3d field9147;
    public Vec3d field9148;
    static float field9149 = -45.0f;
    static float field9150 = 45.0f;
    static float field9151 = -45.0f;
    static float field9152 = 45.0f;
    static float field9153 = -45.0f;
    static float field9154 = 45.0f;
    Vec3d field9155;
    public long field9156;
    static long field9157 = 1250L;
    static long field9158 = 750L;
    public long field9159;
    public float field9160;
    static float field9161 = 10.0f;
    static float field9162 = 0.75f;

    public TargetHudPoint(Vec3d param1) {
        this.field9147 = param1;
        this.field9148 = param1;
        this.field9155 = param1.add((double)(-ThreadLocalRandom.current().nextFloat(field9149, field9150)), (double)(-ThreadLocalRandom.current().nextFloat(field9151, field9152)), (double)(-ThreadLocalRandom.current().nextFloat(field9153, field9154)));
        this.field9156 = System.currentTimeMillis();
        this.field9159 = field9157 + ThreadLocalRandom.current().nextLong(field9158);
    }

    public void method2647(float param1, float param2) {
        this.field9160 = MathUtil.method1870(this.field9160, 1.0f, field9161);
        this.field9147 = MathUtil.cfr_underscore(this.field9147, this.field9155, field9162);
        this.field9148 = new Vec3d((double)param1 + this.field9147.x, (double)param2 + this.field9147.y, this.field9147.z);
    }
}

