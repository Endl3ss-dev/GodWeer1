/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import minced.enums.fire;

public class RemovalState {
    public static int[] field12251;

    public static void method3510() {
        field12251 = new int[fire.values().length];
        RemovalState.field12251[fire.fire.ordinal()] = 1;
        RemovalState.field12251[fire.bossbar.ordinal()] = 2;
        RemovalState.field12251[fire.scoreboard.ordinal()] = 3;
        RemovalState.field12251[fire.title.ordinal()] = 4;
        RemovalState.field12251[fire.totem.ordinal()] = 5;
        RemovalState.field12251[fire.hurttime.ordinal()] = 6;
        RemovalState.field12251[fire.cameraclip.ordinal()] = 7;
        RemovalState.field12251[fire.fishingrod.ordinal()] = 8;
        RemovalState.field12251[fire.destroyparticles.ordinal()] = 9;
        RemovalState.field12251[fire.rain.ordinal()] = 10;
        RemovalState.field12251[fire.shadows.ordinal()] = 11;
        RemovalState.field12251[fire.glowing.ordinal()] = 12;
        RemovalState.field12251[fire.smoke.ordinal()] = 13;
        RemovalState.field12251[fire.vignette.ordinal()] = 14;
        RemovalState.field12251[fire.arrows.ordinal()] = 15;
        RemovalState.field12251[fire.players.ordinal()] = 16;
        RemovalState.field12251[fire.bad_effects.ordinal()] = 17;
        RemovalState.field12251[fire.underwater_blur.ordinal()] = 18;
        RemovalState.field12251[fire.container_background.ordinal()] = 19;
        RemovalState.field12251[fire.lava.ordinal()] = 20;
    }

    static {
        RemovalState.method3510();
    }
}

