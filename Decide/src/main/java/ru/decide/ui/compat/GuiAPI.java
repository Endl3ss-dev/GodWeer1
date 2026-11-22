/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 */
package ru.decide.ui.compat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;

@Environment(value=EnvType.CLIENT)
public interface GuiAPI {
    public void rect(float var1, float var2, float var3, float var4, int var5);

    public void rect(float var1, float var2, float var3, float var4, float var5, int var6);

    public void rect(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9);

    public void rect(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, int var10, int var11, int var12);

    public void rect(float var1, float var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9);

    public void outline(float var1, float var2, float var3, float var4, float var5, float var6, int var7);

    public void outline(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10);

    public void outline(float var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8, int var9, int var10);

    public void text(String var1, String var2, float var3, float var4, float var5, int var6);

    public void text(String var1, String var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9);

    public void text(String var1, String var2, float var3, float var4, float var5, int var6, float var7, float var8, float var9);

    public float textWidth(String var1, String var2, float var3);

    public void msdfText(String var1, String var2, float var3, float var4, float var5, int var6);

    public void msdfText(String var1, String var2, float var3, float var4, float var5, int var6, int var7, int var8, int var9);

    public void msdfText(String var1, String var2, float var3, float var4, float var5, int var6, float var7, float var8, float var9);

    public float msdfWidth(String var1, String var2, float var3);

    public float[] msdfBounds(String var1, String var2, float var3);

    public void msdfShimmer(String var1, String var2, float var3, float var4, float var5, int var6, float var7, float var8, float var9, float var10);

    public void image(String var1, float var2, float var3, float var4, float var5, float var6, int var7);

    public void image(String var1, float var2, float var3, float var4, float var5, int ... var6);

    public boolean imageReady(String var1);

    public void pushScissor(class_332 var1, float var2, float var3, float var4, float var5);

    public void popScissor(class_332 var1);

    public void blur(float var1, float var2, float var3, float var4, float var5);

    public void blur(float var1, float var2, float var3, float var4, float var5, float var6);

    public void line(float var1, float var2, float var3, float var4, float var5, int var6);

    public void circle(float var1, float var2, float var3, int var4);

    public void circleOutline(float var1, float var2, float var3, float var4, int var5);

    public void beginFrame(class_332 var1);

    public void flush();
}

