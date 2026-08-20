/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderTickCounter
 */
package minced.core;

import net.minecraft.client.render.RenderTickCounter;

public interface RenderTickAccess
extends RenderTickCounter {
    public float getSpeedMultiplier();

    public void setSpeedMultiplier(float var1);
}

