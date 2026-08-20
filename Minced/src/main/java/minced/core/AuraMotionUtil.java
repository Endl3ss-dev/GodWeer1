/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import minced.core.AuraColorState;
import minced.core.AuraModeHandler;
import minced.core.AuraItemUtil;
import minced.core.Rotation;
import minced.core.RecordClass231;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class AuraMotionUtil
implements AuraModeHandler {
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field10802;
    Map<String, AuraColorState> field10803 = new HashMap<>();
    Map<String, Vec3d> field10804 = new HashMap<>();
    Map<String, Long> field10805 = new HashMap<>();
    AuraItemUtil field10769;
    LivingEntity field10806;

    protected RecordClass231 method3065(Rotation param1, Vec3d param2, float param3, float param4) {
        Vec3d v5 = AuraMotionUtil.I.player.getEyePos();
        Vec3d v6 = param2.subtract(v5);
        float v7 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v6.z, v6.x)) - field10802));
        float v8 = (float)(-Math.toDegrees(Math.atan2(v6.y, Math.sqrt(v6.x * v6.x + v6.z * v6.z))));
        float v9 = MathHelper.wrapDegrees((float)(v7 - param1.method3140()));
        float v10 = MathHelper.wrapDegrees((float)(v8 - param1.method3136()));
        float v11 = Math.min(Math.abs(v9), param3);
        float v12 = Math.min(Math.abs(v10), param4);
        return new RecordClass231(v7, v8, v9, v10, v11, v12);
    }

    protected Vec3d method3066(LivingEntity param1, double param2) {
        return param1.getPos().add(0.0, (double)param1.getHeight() * param2, 0.0);
    }

    protected RecordClass231 method3067(Rotation param1, LivingEntity param2, double param3, String param5, long param6, double param8, double param10, double param12, float param14, float param15) {
        Vec3d v16 = this.method3066(param2, param3);
        v16 = this.method3069(v16, param5, param6, param8, param10, param12);
        return this.method3065(param1, v16, param14, param15);
    }

    protected AuraColorState method3068(String param12, int param2) {
        AuraColorState v3 = this.field10803.computeIfAbsent(param12, param1 -> new AuraColorState(param2));
        v3.method1994(param2);
        return v3;
    }

    protected Vec3d method3069(Vec3d param1, String param2, long param3, double param5, double param7, double param9) {
        return param1.add(this.method3073(param2, param3, param5, param7, param9));
    }

    public void method3070() {
        this.field10803.clear();
        this.field10804.clear();
        this.field10805.clear();
    }

    public void method3072(AuraItemUtil param1, LivingEntity param2) {
        this.field10769 = param1;
        this.field10806 = param2;
    }

    protected Vec3d method3073(String param1, long param2, double param4, double param6, double param8) {
        long v12;
        long v10 = System.currentTimeMillis();
        if (v10 - (v12 = this.field10805.getOrDefault(param1, 0L).longValue()) >= param2 || !this.field10804.containsKey(param1)) {
            double v14 = ThreadLocalRandom.current().nextDouble(-param4, param4);
            double v16 = ThreadLocalRandom.current().nextDouble(-param6, param6);
            double v18 = ThreadLocalRandom.current().nextDouble(-param8, param8);
            this.field10804.put(param1, new Vec3d(v14, v16, v18));
            this.field10805.put(param1, v10);
        }
        return (Vec3d)this.field10804.get(param1);
    }

    static {
        field10802 = 90.0;
    }
}



