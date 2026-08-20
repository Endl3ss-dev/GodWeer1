/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderLayer$OutlineMode
 *  net.minecraft.client.render.RenderPhase$Target
 *  net.minecraft.client.render.RenderPhase$TextureBase
 */
package minced.core;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

public interface RenderLayerParamsAccess
{
    public RenderLayer.OutlineMode minced$getOutlineMode();

    public RenderPhase.TextureBase minced$getTexture();

    public RenderPhase.Target minced$getTarget();
}

