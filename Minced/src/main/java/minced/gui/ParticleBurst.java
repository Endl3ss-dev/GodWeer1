/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.impl.client.screen.ScreenExtensions
 *  net.fabricmc.fabric.mixin.screen.ScreenAccessor
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Drawable
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ParentElement
 *  net.minecraft.client.gui.navigation.Navigable
 */
package minced.gui;

import java.awt.Color;
import minced.core.RenderUtil;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.fabricmc.fabric.mixin.screen.ScreenAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.Navigable;

public class ParticleBurst {
    long spawnTime;
    static float lifetime;
    static float baseAlpha;
    float size;
    static float sizeFade;
    static float alphaScale;
    double x;
    double y;
    static double halfTurn;
    static double circleMultiplier;
    static double minimumSpeed;
    static double speedRange;
    double velocityX;
    double velocityY;
    static double sizeRange;
    static double horizontalDrag;
    static double verticalDrag;
    static long lifetimeMillis;

    public void render(DrawContext param1) {
        long v2 = System.currentTimeMillis();
        float v4 = (float)(v2 - this.spawnTime) / lifetime;
        if (v4 >= 1.0f) {
            return;
        }
        float v5 = baseAlpha * (1.0f - v4);
        float v6 = this.size * (1.0f - v4 * sizeFade);
        Color v7 = new Color(255, 255, 255, (int)(v5 * alphaScale));
        RenderUtil.method397((float)this.x - v6 / 2.0f, (float)this.y - v6 / 2.0f, v6, v6, v6 / 2.0f, v7.getRGB());
    }

    public ParticleBurst(double param1, double param3) {
        this.x = param1;
        this.y = param3;
        this.spawnTime = System.currentTimeMillis();
        double v5 = Math.random() * halfTurn * circleMultiplier;
        double v7 = minimumSpeed + Math.random() * speedRange;
        this.velocityX = Math.cos(v5) * v7;
        this.velocityY = Math.sin(v5) * v7;
        this.size = 2.0f + (float)(Math.random() * sizeRange);
    }

    public void tick() {
        this.x += this.velocityX;
        this.y += this.velocityY;
        this.velocityX *= horizontalDrag;
        this.velocityY *= verticalDrag;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - this.spawnTime >= lifetimeMillis;
    }

    static {
        halfTurn = Math.PI;
        circleMultiplier = 2.0;
        minimumSpeed = 0.5;
        speedRange = 0.5;
        sizeRange = 2.0;
        horizontalDrag = 0.98;
        verticalDrag = 0.98;
        lifetime = 1000.0f;
        baseAlpha = 0.15f;
        sizeFade = 0.3f;
        alphaScale = 255.0f;
        lifetimeMillis = 1000L;
    }
}



