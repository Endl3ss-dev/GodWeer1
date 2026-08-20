/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterable
 *  net.minecraft.util.math.MathHelper
 */
package minced.core;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterable;
import java.util.Collection;
import net.minecraft.util.math.MathHelper;

public class InterpolationUtil {
    int count;
    float yawResponse = field8467;
    static float field8438;
    float pitchResponse = field8468;
    static float field8439;
    float yawJitter = field8469;
    static float field8440;
    float pitchJitter = field8470;
    static float field8441;
    float smoothness = field8471;
    static float field8442;
    float holdChance = field8472;
    static float field8443;
    static float field8444;
    static float field8445;
    static float field8446;
    static float field8447;
    static float field8448;
    static float field8449;
    static float field8450;
    static float field8451;
    static float field8452;
    static float field8453;
    static float field8454;
    static float field8455;
    static float field8456;
    static float field8457;
    static float field8458;
    static float field8459;
    static float field8460;
    static float field8461;
    static float field8462;
    static float field8463;
    static float field8464;
    static float field8465;
    static float field8466;
    static float field8467;
    static float field8468;
    static float field8469;
    static float field8470;
    static float field8471;
    static float field8472;

    public void method2293() {
        if (this.count < 0) {
            this.count = 0;
        }
        if (!Float.isFinite(this.yawResponse)) {
            this.yawResponse = field8438;
        }
        if (!Float.isFinite(this.pitchResponse)) {
            this.pitchResponse = field8439;
        }
        if (!Float.isFinite(this.yawJitter)) {
            this.yawJitter = field8440;
        }
        if (!Float.isFinite(this.pitchJitter)) {
            this.pitchJitter = field8441;
        }
        if (!Float.isFinite(this.smoothness)) {
            this.smoothness = field8442;
        }
        if (!Float.isFinite(this.holdChance)) {
            this.holdChance = field8443;
        }
        this.yawResponse = MathHelper.clamp((float)this.yawResponse, (float)field8444, (float)field8445);
        this.pitchResponse = MathHelper.clamp((float)this.pitchResponse, (float)field8446, (float)field8447);
        this.yawJitter = MathHelper.clamp((float)this.yawJitter, (float)field8448, (float)field8449);
        this.pitchJitter = MathHelper.clamp((float)this.pitchJitter, (float)field8450, (float)field8451);
        this.smoothness = MathHelper.clamp((float)this.smoothness, (float)field8452, (float)field8453);
        this.holdChance = MathHelper.clamp((float)this.holdChance, (float)0.0f, (float)field8454);
    }

    public void method2294(float param1, float param2, float param3, float param4, float param5, float param6, float param7) {
        ++this.count;
        float v8 = MathHelper.clamp((float)(Math.abs(param3) / Math.max(Math.abs(param1), param7)), (float)field8455, (float)field8456);
        float v9 = MathHelper.clamp((float)(Math.abs(param4) / Math.max(Math.abs(param2), param7)), (float)field8457, (float)field8458);
        float v10 = MathHelper.clamp((float)Math.abs(param3 - param5), (float)field8459, (float)field8460);
        float v11 = MathHelper.clamp((float)Math.abs(param4 - param6), (float)field8461, (float)field8462);
        float v12 = Math.abs(param3) + Math.abs(param4) + Math.abs(param5) + Math.abs(param6) + param7;
        float v13 = 1.0f - MathHelper.clamp((float)((Math.abs(param3 - param5) + Math.abs(param4 - param6)) / v12), (float)0.0f, (float)1.0f);
        float v14 = Math.abs(param1) < field8463 && Math.abs(param2) < field8464 && Math.abs(param3) <= param7 * field8465 && Math.abs(param4) <= param7 * field8466 ? 1.0f : 0.0f;
        float v15 = 1.0f / (float)this.count;
        this.yawResponse += (v8 - this.yawResponse) * v15;
        this.pitchResponse += (v9 - this.pitchResponse) * v15;
        this.yawJitter += (v10 - this.yawJitter) * v15;
        this.pitchJitter += (v11 - this.pitchJitter) * v15;
        this.smoothness += (v13 - this.smoothness) * v15;
        this.holdChance += (v14 - this.holdChance) * v15;
        this.method2293();
    }

    static {
        field8467 = 0.28f;
        field8468 = 0.22f;
        field8469 = 0.18f;
        field8470 = 0.1f;
        field8471 = 0.48f;
        field8472 = 0.08f;
        field8455 = 0.02f;
        field8456 = 1.25f;
        field8457 = 0.02f;
        field8458 = 1.1f;
        field8459 = 0.02f;
        field8460 = 3.5f;
        field8461 = 0.01f;
        field8462 = 2.4f;
        field8463 = 7.5f;
        field8464 = 5.5f;
        field8465 = 1.15f;
        field8466 = 1.15f;
        field8438 = 0.28f;
        field8439 = 0.22f;
        field8440 = 0.18f;
        field8441 = 0.1f;
        field8442 = 0.48f;
        field8443 = 0.08f;
        field8444 = 0.02f;
        field8445 = 1.25f;
        field8446 = 0.02f;
        field8447 = 1.1f;
        field8448 = 0.01f;
        field8449 = 4.0f;
        field8450 = 0.01f;
        field8451 = 2.7f;
        field8452 = 0.1f;
        field8453 = 0.92f;
        field8454 = 0.4f;
    }
}



