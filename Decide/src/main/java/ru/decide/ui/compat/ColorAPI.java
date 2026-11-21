/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.compat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface ColorAPI {
    public int toggleOn(float var1);

    public int gradientA(float var1);

    public int gradientB(float var1);

    public int accent(float var1);

    public int accentSoft(float var1);

    public int accentBright(float var1);

    public int accentFill(float var1);

    public int rgba(int var1, float var2);

    public int mix(int var1, int var2, float var3);

    public int[] currentPalette();

    public int rainbowFlow(float var1, float var2);

    public float rainbowBaseHue();

    public int gradientColor(float var1, float var2);

    public void beginFrame();
}

