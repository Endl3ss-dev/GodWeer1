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

public class AngleUtil {
    float meanX;
    float meanY;
    float meanZ;
    int count;
    static float DEFAULT_MEAN_X;
    static float DEFAULT_MEAN_Y;
    static float DEFAULT_MEAN_Z;

    public AngleUtil(float param1, float param2, float param3) {
        this.meanX = param1;
        this.meanY = param2;
        this.meanZ = param3;
    }

    public void addSample(float param1, float param2, float param3) {
        param1 = MathHelper.clamp((float)param1, (float)0.0f, (float)1.0f);
        param2 = MathHelper.clamp((float)param2, (float)0.0f, (float)1.0f);
        param3 = MathHelper.clamp((float)param3, (float)0.0f, (float)1.0f);
        ++this.count;
        float v4 = 1.0f / (float)this.count;
        this.meanX += (param1 - this.meanX) * v4;
        this.meanY += (param2 - this.meanY) * v4;
        this.meanZ += (param3 - this.meanZ) * v4;
    }

    public void resetIfInvalid(float param1, float param2, float param3) {
        if (this.count < 0) {
            this.count = 0;
        }
        if (!Float.isFinite(this.meanX)) {
            this.meanX = param1;
        }
        if (!Float.isFinite(this.meanY)) {
            this.meanY = param2;
        }
        if (!Float.isFinite(this.meanZ)) {
            this.meanZ = param3;
        }
        this.meanX = MathHelper.clamp((float)this.meanX, (float)0.0f, (float)1.0f);
        this.meanY = MathHelper.clamp((float)this.meanY, (float)0.0f, (float)1.0f);
        this.meanZ = MathHelper.clamp((float)this.meanZ, (float)0.0f, (float)1.0f);
    }

    public AngleUtil() {
        this(DEFAULT_MEAN_X, DEFAULT_MEAN_Y, DEFAULT_MEAN_Z);
    }

    static {
        DEFAULT_MEAN_X = 0.5f;
        DEFAULT_MEAN_Y = 0.72f;
        DEFAULT_MEAN_Z = 0.5f;
    }
}



