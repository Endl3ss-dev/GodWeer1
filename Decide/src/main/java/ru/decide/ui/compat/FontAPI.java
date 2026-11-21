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
public interface FontAPI {
    public void draw(String var1, String var2, float var3, float var4, float var5, int var6);

    public void drawCentered(String var1, String var2, float var3, float var4, float var5, int var6);

    public float width(String var1, String var2, float var3);

    public float height(String var1, float var2);

    public void msdf(String var1, String var2, float var3, float var4, float var5, int var6);

    public float msdfWidth(String var1, String var2, float var3);

    public void msdfFade(String var1, String var2, float var3, float var4, float var5, int var6, float var7, float var8, float var9, float var10, float var11);

    public void shimmer(String var1, String var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9);

    public void wave(String var1, String var2, float var3, float var4, float var5, int var6, float var7);

    public void drawFadingText(String var1, String var2, float var3, float var4, float var5, int var6, float var7);

    public void drawFadingTextReverse(String var1, String var2, float var3, float var4, float var5, int var6, float var7);

    public void drawWrappedText(String var1, String var2, float var3, float var4, float var5, int var6, float var7);

    public float getWrappedHeight(String var1, String var2, float var3, float var4);

    public void drawGRS(String var1, String var2, float var3, float var4, int var5, int var6, float var7);

    public void drawRightAlignFadeLeft(String var1, String var2, float var3, float var4, float var5, int var6, float var7);
}

