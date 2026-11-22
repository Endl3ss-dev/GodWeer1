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
public interface AccentAPI {
    public void msdfIcon(String var1, String var2, float var3, float var4, float var5, float var6);

    public void msdfIcon(String var1, String var2, float var3, float var4, float var5, float var6, float var7);

    public void fillHorizontal(float var1, float var2, float var3, float var4, float var5, float var6);

    public void fillVertical(float var1, float var2, float var3, float var4, float var5, float var6);

    public void fillHorizontalLoop(float var1, float var2, float var3, float var4, float var5, float var6);

    public void overlayTrackShade(float var1, float var2, float var3, float var4, float var5, float var6);

    public void overlayKnobFade(float var1, float var2, float var3, float var4, float var5, float var6);

    public void fillKnob(float var1, float var2, float var3, float var4, float var5, float var6);

    public float categoryListIndexT(String var1);

    public boolean usesDual();
}

