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
public interface RenderHelperAPI {
    public void drawPanelBg(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9);

    public void drawPanelBg(float var1, float var2, float var3, float var4, float var5);

    public void drawDropBackground(float var1, float var2, float var3, float var4, float var5);

    public void drawClientRect(float var1, float var2, float var3, float var4, float var5, float var6, float var7);

    public void drawClientRectFixedRadius(float var1, float var2, float var3, float var4, float var5, float var6, float var7);

    public float effectiveCornerRadius(float var1, float var2, float var3);

    public float cornerEdgeInset(float var1, float var2);
}

