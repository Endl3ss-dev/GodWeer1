/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.enums.Block;

public class PushState {
    public static int[] field11659;

    public static void method3280() {
        field11659 = new int[Block.values().length];
        PushState.field11659[Block.Block.ordinal()] = 1;
        PushState.field11659[Block.Water.ordinal()] = 2;
        PushState.field11659[Block.Player.ordinal()] = 3;
        PushState.field11659[Block.FishingRod.ordinal()] = 4;
    }

    static {
        PushState.method3280();
    }
}

