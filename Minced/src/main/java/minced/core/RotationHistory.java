/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

public final class RotationHistory {
    static float field12527;
    static float field12528;
    static float field12529;
    static float field12530;
    static float field12531;

    public static float method3558(float param0, float param1, float param2) {
        return Math.max(param1, Math.min(param2, param0));
    }

    public static float method3559(float param0, float param1, float param2) {
        return param0 + param2 * (param1 - param0);
    }

    private RotationHistory() {
    }

    public static float method3560(float param0, float param1, float param2) {
        float v3 = (param1 - param0 + field12527) % field12528 - field12529;
        if (v3 < field12530) {
            v3 += field12531;
        }
        return param0 + v3 * param2;
    }

    static {
        field12527 = 180.0f;
        field12528 = 360.0f;
        field12529 = 180.0f;
        field12530 = -180.0f;
        field12531 = 360.0f;
    }
}

