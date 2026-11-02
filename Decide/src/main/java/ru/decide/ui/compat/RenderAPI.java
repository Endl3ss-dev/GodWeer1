/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 */
package ru.decide.ui.compat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;

@Environment(value=EnvType.CLIENT)
public interface RenderAPI {
    public void rect(float var1, float var2, float var3, float var4, int var5);

    public void rect(float var1, float var2, float var3, float var4, float var5, int var6);

    public void rect(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9);

    public void gradientRect(float var1, float var2, float var3, float var4, int var5, int var6, int var7, int var8, float var9);

    public void gradientRect(float var1, float var2, float var3, float var4, int var5, int var6, int var7, int var8, float var9, float var10, float var11, float var12);

    public void outline(float var1, float var2, float var3, float var4, float var5, int var6);

    public void outline(float var1, float var2, float var3, float var4, float var5, int var6, float var7);

    public void outline(float var1, float var2, float var3, float var4, float var5, int var6, float var7, float var8, float var9, float var10);

    public void gradientOutline(float var1, float var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9, float var10);

    public void gradientOutline(float var1, float var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9, float var10, float var11, float var12, float var13);

    public void blur(float var1, float var2, float var3, float var4, float var5, float var6);

    public void blur(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9);

    public void texture(class_2960 var1, float var2, float var3, float var4, float var5, int var6);

    public void texture(class_2960 var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8);

    public void scissor(float var1, float var2, float var3, float var4);

    public void scissorEnd();

    public void beginOverlay();

    public void endOverlay();

    public void flush();
}

